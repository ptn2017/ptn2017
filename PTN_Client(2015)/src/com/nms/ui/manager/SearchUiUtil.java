package com.nms.ui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;

public class SearchUiUtil {

	/**
	 * 搜索Tunnel数据
	 * @param siteInsts
	 */
	public void searchTunnel(List<SiteInst> siteInsts){
		TunnelService_MB tunnelServiceMB = null;
		Map<Integer,Tunnel> aTunnelMap = new HashMap<Integer,Tunnel>();//a端
		Map<Integer,Tunnel> zTunnelMap = new HashMap<Integer,Tunnel>();//z端
		Map<Integer,Tunnel> xcTunnelMap = new HashMap<Integer,Tunnel>();//xc
		Map<Integer,Tunnel> xcTunnelMap2 = new HashMap<Integer,Tunnel>();//过滤XC
		List<List<Tunnel>> lists = new ArrayList<List<Tunnel>>();
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			for(SiteInst siteInst : siteInsts){
				List<Tunnel> tunnels = tunnelServiceMB.searchWh(siteInst.getSite_Inst_Id());
				for(Tunnel tunnel : tunnels){
					if(tunnel.getaSiteId()>0){
						aTunnelMap.put(tunnel.getTunnelId(), tunnel);
					}else if(tunnel.getzSiteId()>0){
						zTunnelMap.put(tunnel.getTunnelId(), tunnel);
					}else{
						xcTunnelMap.put(tunnel.getTunnelId(), tunnel);
						xcTunnelMap2.put(tunnel.getTunnelId(), tunnel);
					}
				}
			}
			Iterator<Integer> iterator = aTunnelMap.keySet().iterator();
			while(iterator.hasNext()){
				Integer tunnelID = iterator.next();
				Tunnel startTunnel = aTunnelMap.get(tunnelID);//tunnel链的起始节点
				Lsp startlsp = startTunnel.getLspParticularList().get(0);
				List<Tunnel> tunnelLink = new ArrayList<Tunnel>();//tunnel链
				tunnelLink.add(startTunnel);
				
				//先匹配XC
				Iterator<Integer> iteratorXC = xcTunnelMap.keySet().iterator();
				while(iteratorXC.hasNext()){
					Integer integer = iteratorXC.next();
					Tunnel xcTunnel = xcTunnelMap.get(integer);
					Lsp lsp1 = xcTunnel.getLspParticularList().get(0);
					Lsp lsp2 = xcTunnel.getLspParticularList().get(1);
					if(startlsp.getASiteId() != lsp1.getZSiteId()){
						if(startlsp.getAoppositeId().equals(lsp1.getAoppositeId())
								&&	startlsp.getFrontLabelValue()== lsp1.getFrontLabelValue() && startlsp.getBackLabelValue() == lsp1.getBackLabelValue()){
							tunnelLink.add(xcTunnel);
							iteratorXC.remove();
							xcTunnel_lsp(lsp2, xcTunnelMap2,tunnelLink);	
						}
					}
				}	
				
				//匹配Z
				Iterator<Integer> iteratorzEnd = zTunnelMap.keySet().iterator();
				while(iteratorzEnd.hasNext()){
					Integer integer2 = iteratorzEnd.next();
					Tunnel zTunnel = zTunnelMap.get(integer2);
					Lsp zLsp = zTunnel.getLspParticularList().get(0);
					Tunnel endTunnel = tunnelLink.get(tunnelLink.size()-1);
					Lsp endLsp = null;
					if(tunnelLink.size()>1){//包含XC
						endLsp = endTunnel.getLspParticularList().get(1);
						if(endLsp.getASiteId() != zTunnel.getzSiteId()){//去掉同一网元
							if(endLsp.getAoppositeId().equals(zLsp.getAoppositeId())
								&&	endLsp.getFrontLabelValue()== zLsp.getFrontLabelValue() && endLsp.getBackLabelValue() == zLsp.getBackLabelValue()){
								tunnelLink.add(zTunnel);
								iteratorzEnd.remove();
								lists.add(tunnelLink);
								break;
							}
						}
					}else{//不包含XC
						endLsp = endTunnel.getLspParticularList().get(0);
						if(endLsp.getASiteId() != zTunnel.getzSiteId()){//去掉同一网元
							if(endLsp.getZoppositeId().equals(zLsp.getZoppositeId())
								&&	endLsp.getFrontLabelValue()== zLsp.getFrontLabelValue() && endLsp.getBackLabelValue() == zLsp.getBackLabelValue()){
								tunnelLink.add(zTunnel);
								iteratorzEnd.remove();
								lists.add(tunnelLink);
								break;
							}
						}
					}
					
				}
			}
			for(List<Tunnel> tunnels : lists){
				tunnelServiceMB.doSearchWh(tunnels);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(tunnelServiceMB);
		}
		
	}
	
	/**
	 * 匹配XC数据
	 * @param lsp
	 * @param xcTunnelMap
	 * @param tunnelLink
	 */
	private void xcTunnel_lsp(Lsp lsp,Map<Integer,Tunnel> xcTunnelMap,List<Tunnel> tunnelLink){

		Iterator<Integer> iteratorXC = xcTunnelMap.keySet().iterator();
		while(iteratorXC.hasNext()){
			Integer integer = iteratorXC.next();
			Tunnel xcTunnel = xcTunnelMap.get(integer);
			Lsp lsp1 = xcTunnel.getLspParticularList().get(0);
			Lsp lsp2 = xcTunnel.getLspParticularList().get(1);
			if(lsp.getASiteId() != lsp1.getZSiteId()){
				if(lsp.getAoppositeId().equals(lsp1.getAoppositeId())
						&&	lsp.getFrontLabelValue()== lsp1.getFrontLabelValue() && lsp.getBackLabelValue() == lsp1.getBackLabelValue()){
					iteratorXC.remove();
					tunnelLink.add(xcTunnel);
					xcTunnel_lsp(lsp2, xcTunnelMap,tunnelLink);	
				}
			}
		
		}
	}
	
	/**
	 * pw搜索
	 * @param siteInsts
	 */
	public void searchPw(List<SiteInst> siteInsts){
		List<PwInfo> pwInfos = null;
		Map<Integer,PwInfo> aPwMap = new HashMap<Integer,PwInfo>();//a端
		Map<Integer,PwInfo> zPwMap = new HashMap<Integer,PwInfo>();//z端
		PwInfoService_MB pwInfoService = null;
		List<List<PwInfo>> listLink = new ArrayList<List<PwInfo>>();
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for(SiteInst siteInst : siteInsts){
				pwInfos = pwInfoService.selectNodeBySiteid(siteInst.getSite_Inst_Id());
				for(PwInfo pwInfo: pwInfos){
					if(pwInfo.getASiteId()>0 && pwInfo.getIsSingle() ==1){
						aPwMap.put(pwInfo.getPwId(), pwInfo);
					}else if(pwInfo.getZSiteId()>0&& pwInfo.getIsSingle() ==1){
						zPwMap.put(pwInfo.getPwId(), pwInfo);
					}
				}
			}
			
			//a端
			Iterator<Integer> iterator = aPwMap.keySet().iterator();
			while(iterator.hasNext()){
				List<PwInfo> pwList = new ArrayList<PwInfo>();
				Integer pwId = iterator.next();
				PwInfo aPwInfo = aPwMap.get(pwId);
				pwList.add(aPwInfo);
				//匹配z端
				Iterator<Integer> zIterator = zPwMap.keySet().iterator();
				while(zIterator.hasNext()){
					Integer zPwId = zIterator.next();
					PwInfo zPwInfo = zPwMap.get(zPwId);
					if(aPwInfo.getTunnelId() == zPwInfo.getTunnelId() 
						&& zPwInfo.getInlabelValue() == aPwInfo.getInlabelValue()
						&& zPwInfo.getOutlabelValue() == aPwInfo.getOutlabelValue()
						&& aPwInfo.getASiteId() != zPwInfo.getZSiteId()){
						pwList.add(zPwInfo);
						listLink.add(pwList);
						zIterator.remove();
						break;
					}
				}
				iterator.remove();
			}
			
			for(List<PwInfo> pwList : listLink){
				pwInfoService.doSearch(pwList, null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(pwInfoService);
		}
	}
	
	/**
	 * eline搜索
	 * @param siteInsts
	 */
	public void searchEline(List<SiteInst> siteInsts){
		ElineInfoService_MB elineService = null;
		Map<Integer,ElineInfo> aElines = new HashMap<Integer, ElineInfo>();
		Map<Integer,ElineInfo> zElines = new HashMap<Integer, ElineInfo>();
		List<List<ElineInfo>> linkEline = new ArrayList<List<ElineInfo>>();
		List<ElineInfo> elines = null;
		try {
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			for(SiteInst siteInst : siteInsts){
				elines = elineService.selectNodeBySite(siteInst.getSite_Inst_Id());
				for(ElineInfo elineInfo : elines){
					if(elineInfo.getaSiteId()>0 && elineInfo.getIsSingle() ==1){
						aElines.put(elineInfo.getId(), elineInfo);
					}else if(elineInfo.getzSiteId()>0 && elineInfo.getIsSingle() ==1){
						zElines.put(elineInfo.getId(), elineInfo);
					}
				}
			}
			Iterator<Integer> aElineIds = aElines.keySet().iterator();
			while(aElineIds.hasNext()){
				Integer aID = aElineIds.next();
				ElineInfo aElineInfo = aElines.get(aID);
				List<ElineInfo> elineInfos = new ArrayList<ElineInfo>();
				elineInfos.add(aElineInfo);
				Iterator<Integer> zElineIds = zElines.keySet().iterator();
				while(zElineIds.hasNext()){
					Integer integer = zElineIds.next();
					ElineInfo zElineInfo = zElines.get(integer);
					if(aElineInfo.getaSiteId() != zElineInfo.getzSiteId() && aElineInfo.getPwId() == zElineInfo.getPwId()){
						elineInfos.add(zElineInfo);
						linkEline.add(elineInfos);
						zElineIds.remove();
					}
				}
				aElineIds.remove();
			}
			for(List<ElineInfo> elineInfos : linkEline){
				elineService.doSearch(elineInfos);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(elineService);
		}
	}
	
	
	public void searchCES(List<SiteInst> siteInsts){
		CesInfoService_MB cesInfoService = null;
		Map<Integer,CesInfo> aCes = new HashMap<Integer, CesInfo>();
		Map<Integer,CesInfo> zCes = new HashMap<Integer, CesInfo>();
		List<List<CesInfo>> linkCes = new ArrayList<List<CesInfo>>();
		List<CesInfo> cesInfos = null;
		try {
			cesInfoService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			for(SiteInst siteInst : siteInsts){
				cesInfos = cesInfoService.selectNodeBySite(siteInst.getSite_Inst_Id());
				for(CesInfo cesInfo : cesInfos){
					if(cesInfo.getaSiteId()>0 && cesInfo.getIsSingle() ==1){
						aCes.put(cesInfo.getId(), cesInfo);
					}else if(cesInfo.getzSiteId()>0 && cesInfo.getIsSingle() ==1){
						zCes.put(cesInfo.getId(), cesInfo);
					}
				}
			}
			Iterator<Integer> aCesIds = aCes.keySet().iterator();
			while(aCesIds.hasNext()){
				Integer aID = aCesIds.next();
				CesInfo acesInfo = aCes.get(aID);
				List<CesInfo> cesInfos2 = new ArrayList<CesInfo>();
				cesInfos2.add(acesInfo);
				Iterator<Integer> zCesIds = zCes.keySet().iterator();
				while(zCesIds.hasNext()){
					Integer integer = zCesIds.next();
					CesInfo zCesInfo = zCes.get(integer);
					if(acesInfo.getaSiteId() != zCesInfo.getzSiteId() && acesInfo.getPwId() == zCesInfo.getPwId()){
						cesInfos2.add(zCesInfo);
						linkCes.add(cesInfos2);
						zCesIds.remove();
					}
				}
				aCesIds.remove();
			}
			for(List<CesInfo> infos : linkCes){
				cesInfoService.doSearch(infos);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(cesInfoService);
		}
	}
}
