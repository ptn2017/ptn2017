﻿package com.nms.ui.ptn.business.pw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.PwProtectStatus;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.report.SSProfess;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.BfdInfoService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.MsPwInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.base.DispatchBase;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.segment.SearchSegmentDialog;
import com.nms.ui.ptn.business.dialog.pwpath.AddPDialog;
import com.nms.ui.ptn.business.dialog.pwpath.AddPwFilterDialog;
import com.nms.ui.ptn.ne.camporeData.CamporeBusinessDataDialog;
import com.nms.ui.ptn.systemconfig.dialog.qos.ComparableSort;

/**
 * @author lepan
 */
public class PwBusinessController extends AbstractController {
	private final PwBusinessPanel view;
	private PwInfo pwFilterCondition= null;//pw的过滤条件
	private int total;
	private int now = 0;
	private List<PwInfo> infos = null;
	
	public PwBusinessController(PwBusinessPanel view) {
		this.view = view;
	}

	@Override
	public void delete() {
		List<PwInfo> pwInfoList = null;
		try {
			pwInfoList = this.view.getAllSelect();
			DispatchUtil pwOperationImpl = new DispatchUtil(RmiKeys.RMI_PW);
			String message = pwOperationImpl.excuteDelete(pwInfoList);
			//操作日志记录
			for(PwInfo pw : pwInfoList){
				AddOperateLog.insertOperLog(null, EOperationLogType.PWDELETE.getValue(), message,
						null, null, -1, pw.getPwName(), null);
			}
			DialogBoxUtil.succeedDialog(this.view, message);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 删除前的验证
	 * 
	 * @return true 验证成功， false 验证失败
	 * @throws Exception
	 */
	@Override
	public boolean deleteChecking() throws Exception {
		List<PwInfo> pwInfoList = null;
		boolean flag = false;
		boolean onlineFlag = false;
		List<Integer> allSiteIds = null;
		List<Integer> siteIds = null;
		try {
			pwInfoList = this.view.getAllSelect();
			//判断该pw上是否存在在线网元托管的
			SiteUtil siteUtil = new SiteUtil();
			allSiteIds = new ArrayList<Integer>();
			siteIds = new ArrayList<Integer>();
			allSiteIds=getNotOnlineSiteIdNames(pwInfoList);
            for(int i=0;i<allSiteIds.size();i++){
			    if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
			    	siteIds.add(allSiteIds.get(i));			    		
			    }
			}
            if(siteIds !=null && siteIds.size()!=0){
				onlineFlag = true;
			}
				
			if(onlineFlag){
				WhImplUtil wu = new WhImplUtil();
				String str=wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				return false;
			}
			
			//删除pw之前先验证该pw是否有按需oam，没有才可删除，否则提示不能删除
			for (PwInfo pw : pwInfoList) {
				//如果为true，说明该条pw有按需oam，不能删除
				if(checkIsOam(pw)){
					flag = true;
					break;
				}
			}
			if(flag){
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CLEAN_OAM));
				return false;
			}
			
			//删除pw之前先验证该pw是否有BFD，没有才可删除，否则提示不能删除
			for (PwInfo pw : pwInfoList) {
				//如果为true，说明该条pw有BFD，不能删除
				if(checkIsBfd(pw)){
					flag = true;
					break;
				}
			}
			if(flag){
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CLEAN_BFD));
				return false;
			}
			
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
		allSiteIds = null;
		siteIds = null;
		pwInfoList= null;
		}
		return flag;
	}
	
	private boolean checkIsBfd(PwInfo pw) {
		boolean flag=false;
		BfdInfoService_MB bfdService = null;
		List<Integer> pwIds=null;
		List<Integer> pwIds2=null;
		try {
			pwIds=new ArrayList<Integer>();
			pwIds2=new ArrayList<Integer>();
			bfdService = (BfdInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.BFDMANAGEMENT);
			pwIds=bfdService.queryPwIds(pw.getASiteId(), 2);
			pwIds2=bfdService.queryPwIds(pw.getZSiteId(), 2);
            //a
			if(pwIds.contains(pw.getApwServiceId())||pwIds2.contains(pw.getZpwServiceId())){
				flag=true;				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		finally
		{
			pwIds2=null;
			pwIds=null;
			UiUtil.closeService_MB(bfdService);
		}
		return flag;
	}
	private boolean checkIsOam(PwInfo pw) {
		OamMepInfo mep = null;
		OamInfoService_MB service = null;
		boolean flag = false;
		try {
			mep = new OamMepInfo();
			mep.setObjId(pw.getPwId());
			mep.setObjType("PW_TEST");
			service = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			flag = service.queryByObjIdAndType(mep);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return flag;
	}

	@Override
	public void openCreateDialog() throws Exception {
		try {
			AddPDialog addpwdialog = new AddPDialog(this.view, true, null);
			addpwdialog.setLocation(UiUtil.getWindowWidth(addpwdialog.getWidth()), UiUtil.getWindowHeight(addpwdialog.getHeight()));
			addpwdialog.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		}
	}

	@Override
	public void openUpdateDialog() throws Exception {
		PwInfoService_MB pwInfoService = null;
		try {
			PwInfo pwinfo = this.view.getSelect();
			if (pwinfo == null) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			} else {
				//更新pw被使用的业务id
				pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				pwinfo = pwInfoService.selectByPwId(pwinfo.getPwId());
				AddPDialog addpwdialog = new AddPDialog(this.view, true, pwinfo);
				addpwdialog.setLocation(UiUtil.getWindowWidth(addpwdialog.getWidth()), UiUtil.getWindowHeight(addpwdialog.getHeight()));
				addpwdialog.setVisible(true);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(pwInfoService);
		}
	}

	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		PwInfoService_MB pwInfoServiceMB = null;
		try {
			pwInfoServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwInfos = new ArrayList<PwInfo>();
			pwInfos.add(view.getSelect());
			pwInfoServiceMB.getOAMandQoSforPw(pwInfos);
			initQosInfos();
			initOamInfos();
			initTopoPanel();
			initMspwInfo();
			this.initLspPanel();
			this.initSchematizePanel();
			this.initBusinessPanel();
			this.initPwProtectPanel(pwInfoServiceMB);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(pwInfoServiceMB);
		}
	}
	
	private void initPwProtectPanel(PwInfoService_MB service) throws Exception {
		PwInfo mainPw = view.getSelect();
		List<PwInfo> pwList = service.select();
		PwInfo standPw = null;
		for(PwInfo pw : pwList){
			if(pw.getDirection() != null && pw.getDirection().contains("备用")){
				standPw = pw;
				break;
			}
		}
		PwProtectStatus pwPro = new PwProtectStatus();
		pwPro.setMainPwName(mainPw.getPwName());
		pwPro.setStandPwName(standPw.getPwName());
		String[] arr = mainPw.getDirection().split("@");
		pwPro.setDelaytime(Integer.parseInt(arr[1]));
		pwPro.setWaittime(Integer.parseInt(arr[2]));
		pwPro.setSiteId(mainPw.getASiteId());
		pwPro.setPwId(mainPw.getPwId());
		this.view.getPwProtectPanel().clear();
		List<PwProtectStatus> pwProList = new ArrayList<PwProtectStatus>();
		pwProList.add(pwPro);
		this.view.getPwProtectPanel().initData(pwProList);
	}

	private void initBusinessPanel() {
		ElineInfoService_MB elineInfoServiceMB = null;
		EtreeInfoService_MB etreeInfoServiceMB = null;
		ElanInfoService_MB elanInfoServiceMB = null;
		CesInfoService_MB cesInfoServiceMB = null;
		DualInfoService_MB dualInfoServiceMB = null;
		List<Integer> pwList = new ArrayList<Integer>();
		SSProfess ss=null;
		List<SSProfess> ssList = null;
		try {
			PwInfo pwInfo = this.view.getSelect();
			pwList.add(pwInfo.getPwId());
			elineInfoServiceMB = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);	
			etreeInfoServiceMB = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);			
			elanInfoServiceMB = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);	
			cesInfoServiceMB = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);			
			dualInfoServiceMB = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);			
			List<ElineInfo> elineList = elineInfoServiceMB.selectElineByPwId(pwList);
			if(elineList!=null && elineList.size()!=0){
				ss=new SSProfess();
				ss.setName(elineList.get(0).getName());
				ss.setServiceType(elineList.get(0).getServiceType());
				ss.setCreateTime(elineList.get(0).getCreateTime());
				ss.setActiveStatus(elineList.get(0).getActiveStatus());
				ss.setClientName(elineList.get(0).getClientName());
				ssList = new ArrayList<SSProfess>();				
				ssList.add(ss);
				this.view.getBusinessNetworkTablePanel().clear();
				this.view.getBusinessNetworkTablePanel().initData(ssList);
			}else{
				List<EtreeInfo> etreeList = etreeInfoServiceMB.selectEtreeByPwId(pwList);
				if(etreeList!=null && etreeList.size()!=0){
					ss=new SSProfess();
					ss.setName(etreeList.get(0).getName());
					ss.setServiceType(etreeList.get(0).getServiceType());
					ss.setCreateTime(etreeList.get(0).getCreateTime());
					ss.setActiveStatus(etreeList.get(0).getActiveStatus());
					ss.setClientName(etreeList.get(0).getClientName());
					ssList = new ArrayList<SSProfess>();
					ssList.add(ss);
					this.view.getBusinessNetworkTablePanel().clear();
					this.view.getBusinessNetworkTablePanel().initData(ssList);
				}else{
					List<ElanInfo> elanList = elanInfoServiceMB.selectElanbypwid(pwList);
					if(elanList!=null && elanList.size()!=0){
						ss=new SSProfess();
						ss.setName(elanList.get(0).getName());
						ss.setServiceType(elanList.get(0).getServiceType());
						ss.setCreateTime(elanList.get(0).getCreateTime());
						ss.setActiveStatus(elanList.get(0).getActiveStatus());
						ss.setClientName(elanList.get(0).getClientName());
						ssList = new ArrayList<SSProfess>();
						ssList.add(ss);
						this.view.getBusinessNetworkTablePanel().clear();
						this.view.getBusinessNetworkTablePanel().initData(ssList);					
					}else{
						List<CesInfo> cesList = cesInfoServiceMB.selectCesByPwId(pwList);
						if(cesList!=null && cesList.size()!=0){
							ss=new SSProfess();
							ss.setName(cesList.get(0).getName());
							ss.setServiceType(cesList.get(0).getServiceType());
							ss.setCreateTime(cesList.get(0).getCreateTime());
							ss.setActiveStatus(cesList.get(0).getActiveStatus());
							ss.setClientName(cesList.get(0).getClientName());
							ssList = new ArrayList<SSProfess>();
							ssList.add(ss);
							this.view.getBusinessNetworkTablePanel().clear();
							this.view.getBusinessNetworkTablePanel().initData(ssList);	
						}else{
							List<DualInfo> dualList = dualInfoServiceMB.queryByPwId(pwInfo.getPwId());
							if(dualList!=null && dualList.size()!=0){
								ss=new SSProfess();
								ss.setName(dualList.get(0).getName());
								ss.setServiceType(dualList.get(0).getServiceType());
								ss.setCreateTime(dualList.get(0).getCreateTime());
								ss.setActiveStatus(dualList.get(0).getActiveStatus());
								ss.setClientName(dualList.get(0).getClientName());
								ssList = new ArrayList<SSProfess>();
								ssList.add(ss);
								this.view.getBusinessNetworkTablePanel().clear();
								this.view.getBusinessNetworkTablePanel().initData(ssList);
							}else{
								ssList = new ArrayList<SSProfess>();
								this.view.getBusinessNetworkTablePanel().clear();
								this.view.getBusinessNetworkTablePanel().initData(ssList);								
							}
							
						}
												
					}
				}
			}
			
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(elineInfoServiceMB);
			UiUtil.closeService_MB(etreeInfoServiceMB);
			UiUtil.closeService_MB(elanInfoServiceMB);
			UiUtil.closeService_MB(cesInfoServiceMB);
			UiUtil.closeService_MB(dualInfoServiceMB);
		}
	}

	/**
	 * 初始化图形化界面数据
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void initSchematizePanel() {
		PwInfo pwinfo = null;
		try {
			pwinfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(pwinfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void initTopoPanel() {
		PwInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	private void initOamInfos() throws Exception {
		OamInfoService_MB oamInfoservice = null;
		PwInfo pwInfo = null;
		List<OamInfo> oamList = null;
		try {
			pwInfo = this.view.getSelect();
			oamList = pwInfo.getOamList();
			ComparableSort sort = new ComparableSort();
			oamList = (List<OamInfo>) sort.compare(oamList);
			this.view.getOamTable().clear();
			this.view.getOamTable().initData(oamList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoservice);
			pwInfo = null;
			oamList = null;
		}
	}

	@SuppressWarnings("unchecked")
	private void initQosInfos() throws Exception {
		QosInfoService_MB qosInfoservice = null;
		List<QosInfo> qosList = null;
		PwInfo info = null;
		try {
			info = this.view.getSelect();
			qosList = info.getQosList();
			ComparableSort sort = new ComparableSort();
			qosList = (List<QosInfo>) sort.compare(qosList);
			this.view.getQosPanel().clear();
			this.view.getQosPanel().initData(qosList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(qosInfoservice);
		}
	}

	private void initMspwInfo(){
		this.view.getPwXcTablePanel().getTable().clear();
		this.view.getPwXcTablePanel().getTable().initData(view.getSelect().getMsPwInfos());
		this.view.getPwXcTablePanel().getTable().updateUI();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void refresh() throws Exception {
		PwInfoService_MB service = null;
		ListingFilter filter=null;
		List<PwInfo> needs = new ArrayList<PwInfo>();
		try {
			filter=new ListingFilter();
			if ( null == pwFilterCondition ) {
				pwFilterCondition = new PwInfo();
			}
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			infos = (List<PwInfo>) filter.filterList(service.selectFilter(pwFilterCondition));
			//单独查询多段PW
			infos.addAll(this.pwFilter(service, pwFilterCondition));
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now = 1;
				if (infos.size() % ConstantUtil.flipNumber == 0) {
					total = infos.size() / ConstantUtil.flipNumber;
				} else {
					total = infos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getGoToJButton().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getGoToJButton().setEnabled(true);
				}
				if (infos.size() - (now - 1) * ConstantUtil.flipNumber > ConstantUtil.flipNumber) {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, ConstantUtil.flipNumber);
				} else {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size() - (now - 1) * ConstantUtil.flipNumber);
				}
			}
			view.getCurrPageLabel().setText(now+"");
			view.getTotalPageLabel().setText(total + "");
			view.getPrevPageBtn().setEnabled(false);
			
			this.view.clear();
			if (view.getTopoPanel() != null) {
				view.getTopoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			this.view.initData(needs);
			this.view.getLspNetworkTablePanel().clear();
			this.view.getQosPanel().clear();
			this.view.getOamTable().clear();
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	private List<PwInfo> pwFilter(PwInfoService_MB service, PwInfo pwFilterCondition) {
		List<PwInfo> allList = null;
		List<PwInfo> msPwinfo = null;
		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnelList = null;
		List<Integer> tunnelIds = null;
		try {
			allList = new ArrayList<PwInfo>();
			msPwinfo = this.findMsPwInfo(service);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			//先通过端口号查tunnel，再通过tunnel查pw，然后过滤pw
			tunnelList = tunnelService.findTunnelByPortId(pwFilterCondition.getPortId());
			tunnelIds = new ArrayList<Integer>();
			if(null !=tunnelList && !tunnelList.isEmpty())
			{
				for(Tunnel tunnel : tunnelList)
				{
					tunnelIds.add(tunnel.getTunnelId());
				}
			}
			
			if(msPwinfo != null && msPwinfo.size() >0)
			{
				for(PwInfo msPwInst : msPwinfo )
				{
					if(isFilter(pwFilterCondition, msPwInst, tunnelIds)){
						allList.add(msPwInst);
					}
				}
			}
		} 
		catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		finally
		{
			UiUtil.closeService_MB(tunnelService);
		}
		return allList ;
	}
	
	private boolean isFilter(PwInfo condition, PwInfo mspw, List<Integer> tunnelIds){
		boolean flag = true;
		try 
		{
			//mspw 中包含的多段信息只有一条
			if(condition.getPortId() > 0 )
			{
				
				if(null != mspw.getMsPwInfos() &&(tunnelIds.contains(mspw.getMsPwInfos().get(0).getFrontTunnelId()) || tunnelIds.contains(mspw.getMsPwInfos().get(0).getBackTunnelId())))
				{
					flag = true;
				}else
				{
					return false;
				}
			}
			if( null != condition.getPwName() && !"".equals(condition.getPwName()))
			{
				if(mspw.getPwName().contains(condition.getPwName()))
				{
					flag = true;
				}else{
					return false;
				}
			}
			if(condition.getTunnelId() >0)
			{
				for(MsPwInfo msPwInst : mspw.getMsPwInfos())
				{
					if(msPwInst.getFrontTunnelId() == condition.getTunnelId() || msPwInst.getBackTunnelId() == condition.getTunnelId()){
						flag = true;
						break;
					}
					flag = false;
				}
				if(!flag)
				{
					return false;
				}
			}
			if(condition.getPwStatus() >0)
			{
				if(condition.getPwStatus() == mspw.getPwStatus()){
					flag = true;
				}
				else
				{
					return false;
				}
			}
			if(condition.getType() != null && !condition.getType().name().equals("NONE"))
			{
				if(condition.getType() != null && condition.getType() == mspw.getType())
				{
					flag = true;
				}
				else
				{
					return false;
				}
			}
			if(condition.getCreateUser() != null && !condition.getCreateUser().equals(""))
			{
				if(condition.getCreateUser().equals(mspw.getCreateUser()))
				{
					flag = true;
				}
				else
				{
					return false;
				}
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	private List<PwInfo> findMsPwInfo(PwInfoService_MB pwinfoService)
	{
		List<PwInfo> allList = null;
		MsPwInfoService_MB msPwInfoService = null;
		PwInfo pwInfo = null;
		List<MsPwInfo> msPwInfos = null;
		try 
		{
			allList = new ArrayList<PwInfo>();
			msPwInfoService = (MsPwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MSPWSERVICE);
			//先查中间网元
			msPwInfos = msPwInfoService.selectBySiteId(this.pwFilterCondition.getASiteId());
			if(msPwInfos != null && msPwInfos.size() > 0)
			{
				List<Integer> pwIdList = new ArrayList<Integer>();
				for(MsPwInfo msPwInfo : msPwInfos){
					if(!pwIdList.contains(msPwInfo.getPwId())){
						pwIdList.add(msPwInfo.getPwId());
					}
				}
				for(Integer pwId : pwIdList){
					pwInfo = pwinfoService.selectMsPwByPwId(pwId,0);
					if(pwInfo != null){
						MsPwInfo pw = new MsPwInfo();
						pw.setPwId(pwId);
						pwInfo.setMsPwInfos(msPwInfoService.select(pw));
						allList.add(pwInfo);
					}
				}
			}
			//再查A/Z端网元
			if(this.pwFilterCondition.getASiteId()>0){
				List<PwInfo> msPwList = pwinfoService.selectBySiteId_network(this.pwFilterCondition.getASiteId());
				if(msPwList != null && !msPwList.isEmpty()){
					for (PwInfo pw : msPwList) {
						if(pw.getTunnelId() == 0){
							MsPwInfo msPw = new MsPwInfo();
							msPw.setPwId(pw.getPwId());
							pw.setMsPwInfos(msPwInfoService.select(msPw));
							allList.add(pw);
						}
					}
				}
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(msPwInfoService);
		}
		return allList;
	}
	

	/**
	 * 执行搜索按钮动作
	 * **/
	@Override
	public void search() {
		try {
			new SearchSegmentDialog(this.view);
//			Thread.sleep(21000);
//			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} 
	}
	
	/**
	 * 设置pw的过滤查询
	 */
	@Override
	public void openFilterDialog() throws Exception {
		if (null == this.pwFilterCondition) {
			this.pwFilterCondition = new PwInfo();
		}
		new AddPwFilterDialog(1,this.pwFilterCondition,this.view);
	}
	
	/**
	 * 清除过滤
	 */
	@Override
	public void clearFilter() throws Exception {
		this.pwFilterCondition = null;
		this.refresh();
	}

	public void setFilterCondition(PwInfo filterCondition) {
		this.pwFilterCondition = filterCondition;
	}
	
	public PwInfo getFilterCondition() {
		return pwFilterCondition;
	}
	
	private void initLspPanel() throws Exception {
		List<Lsp> lspList = new ArrayList<Lsp>();
		PwInfo pw = this.view.getSelect();
		List<Tunnel> tunnelList = this.getTunnelByPwId(pw.getPwId());
		for (int i = tunnelList.size()-1; i >= 0; i--) {
			Tunnel tunnel = tunnelList.get(i);
			this.convertLspData(tunnel, "work");
			lspList.addAll(tunnel.getLspParticularList());
		}
		this.view.getLspNetworkTablePanel().initData(lspList);
		this.view.updateUI();
	}

	private List<Tunnel> getTunnelByPwId(int pwId) throws Exception {
		PwInfoService_MB pwInfoService = null;
		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnelList = new ArrayList<Tunnel>();
		try {
			Tunnel tunnel = new Tunnel();
			// 通过pwID查询pw对象
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo pwinfo = new PwInfo();
			pwinfo.setPwId(pwId);
			pwinfo = pwInfoService.selectBypwid_notjoin(pwinfo);
			// 通过pw中的tunnelID查询tunnel对象
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			if(pwinfo.getTunnelId() != 0){
				tunnel.setTunnelId(pwinfo.getTunnelId());
				tunnelList = tunnelService.select_nojoin(tunnel);
			}else{
				List<Integer> tunnelIdList = new ArrayList<Integer>();
				for(MsPwInfo msPwInfo : pwinfo.getMsPwInfos()){
					if(!tunnelIdList.contains(msPwInfo.getBackTunnelId())){
						tunnelIdList.add(msPwInfo.getBackTunnelId());
						tunnel.setTunnelId(msPwInfo.getBackTunnelId());
						List<Tunnel> listBack = tunnelService.select_nojoin(tunnel);
						tunnelList.addAll(listBack);
					}
					if(!tunnelIdList.contains(msPwInfo.getFrontTunnelId())){
						tunnelIdList.add(msPwInfo.getFrontTunnelId());
						tunnel.setTunnelId(msPwInfo.getFrontTunnelId());
						List<Tunnel> listFront = tunnelService.select_nojoin(tunnel);
						tunnelList.addAll(listFront);
					}
				}
			}
		} finally {
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwInfoService);
		}
		return tunnelList;	
	}

	/**
	 * 转换lsp对象 给lsp所需要的列赋值
	 * 
	 * @param tunnel
	 * @throws Exception
	 */
	private void convertLspData(Tunnel tunnel, String type) throws Exception {
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			for (Lsp lsp : tunnel.getLspParticularList()) {
				String asiteName = siteService.getSiteName(lsp.getASiteId());
				String zsiteName = siteService.getSiteName(lsp.getZSiteId());
				String aportName = portService.getPortname(lsp.getAPortId());
				String zportName = portService.getPortname(lsp.getZPortId());
				lsp.putClientProperty("id", lsp.getId());
				lsp.putClientProperty("lsptype", type);
				lsp.putClientProperty("lspname", tunnel.getTunnelName());
				lsp.putClientProperty("asiteName", asiteName);
				lsp.putClientProperty("zsiteName", zsiteName);
				lsp.putClientProperty("aportName", aportName);
				lsp.putClientProperty("zportName", zportName);
				lsp.putClientProperty("inlabel", lsp.getFrontLabelValue());
				lsp.putClientProperty("outlabel", lsp.getBackLabelValue());
				lsp.putClientProperty("position", tunnel.getPosition() == 1 ? true : false);
			}
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
	}
	
	/**
	 * 取出该PW上的所有网元siteid
	 * @param pwInfoActivate
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getNotOnlineSiteIdNames(List<PwInfo> pwInfoActivate) throws Exception {
		List<Integer> siteIds = new ArrayList<Integer>();
		DispatchBase dispatchbase = new DispatchBase();
		try {
			for (PwInfo pw : pwInfoActivate) {
				if (!siteIds.contains(pw.getASiteId()) && pw.getASiteId() > 0 && dispatchbase.getManufacturer(pw.getASiteId()) == EManufacturer.WUHAN.getValue()) {
					siteIds.add(pw.getASiteId());
				}
				if (!siteIds.contains(pw.getZSiteId()) && pw.getZSiteId() > 0 && EManufacturer.WUHAN.getValue() == dispatchbase.getManufacturer(pw.getZSiteId()))// 单点对象的Z端不用下发
				{
					siteIds.add(pw.getZSiteId());
				}
				if (pw.getMsPwInfos() != null && pw.getMsPwInfos().size() > 0) {
					for (MsPwInfo msPwInfo : pw.getMsPwInfos()) {
						if (!siteIds.contains(msPwInfo.getSiteId())) {
							siteIds.add(msPwInfo.getSiteId());
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return siteIds;
	}
	
	@Override
	public void prevPage() throws Exception {
		now = now - 1;
		if (now == 1) {
			view.getPrevPageBtn().setEnabled(false);
		}
		view.getNextPageBtn().setEnabled(true);

		flipRefresh();
	}

	@Override
	public void goToAction() throws Exception {

		if (CheckingUtil.checking(view.getGoToTextField().getText(), CheckingUtil.NUM1_9)) {// 判断填写是否为数字
			Integer goi = Integer.parseInt(view.getGoToTextField().getText());
			if(goi>= total){
				goi = total;
				view.getNextPageBtn().setEnabled(false);
			}
			if(goi == 1){
				view.getPrevPageBtn().setEnabled(false);
			}
			if(goi > 1){
				view.getPrevPageBtn().setEnabled(true);
			}
			if(goi<total){
				view.getNextPageBtn().setEnabled(true);
			}
			now = goi;
			flipRefresh();
		}else{
			DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.MESSAGE_NUMBER));
		}
		
	
	}

	@Override
	public void nextPage() throws Exception {
		now = now + 1;
		if (now == total) {
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}

	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<PwInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();
		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		this.view.initData(needs);
		this.view.getLspNetworkTablePanel().clear();
		this.view.getQosPanel().clear();
		this.view.getOamTable().clear();
		this.view.updateUI();
	}
	
	/**
	 * 一致性检测
	 */
	@Override
	public void consistence() throws Exception{
		DispatchUtil  pwDispatch = null;
		PwInfoService_MB pwinfoService = null;
		Map<Integer, List<PwInfo>> pwEMSMap = null;
		Map<Integer, List<PwInfo>> pwNEMap = null;
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			List<Integer> siteIdOnLineList = new ArrayList<Integer>();
			List<SiteInst> siteInstList = siteService.select();
			if(siteInstList != null){
				for(SiteInst site : siteInstList){
					if(site.getLoginstatus() == 1){
						siteIdOnLineList.add(site.getSite_Inst_Id());
					}
				}
			}
			if(!siteIdOnLineList.isEmpty()){
				pwinfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				pwEMSMap = new HashMap<Integer, List<PwInfo>>();
				pwNEMap = new HashMap<Integer, List<PwInfo>>();
				pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
				for(int siteId : siteIdOnLineList){
					List<PwInfo> pwEMSSingle = pwinfoService.selectNodeBySiteid(siteId);
					List<PwInfo> pwNESingle = (List<PwInfo>) pwDispatch.consistence(siteId);
					if(pwEMSSingle != null && !pwEMSSingle.isEmpty()){
						pwEMSMap.put(siteId, pwEMSSingle);
					}
					if(pwNESingle != null && !pwNESingle.isEmpty()){
						pwNEMap.put(siteId, pwNESingle);
					}
				}
				CamporeBusinessDataDialog camporeDataDialog = new CamporeBusinessDataDialog("PW", pwEMSMap, pwNEMap, this);
				if(pw != null){
					pw = pwinfoService.selectByPwId(pw.getPwId());
					AddPDialog addpwdialog = new AddPDialog(this.view, true, pw);
					addpwdialog.setLocation(UiUtil.getWindowWidth(addpwdialog.getWidth()), UiUtil.getWindowHeight(addpwdialog.getHeight()));
					addpwdialog.setVisible(true);
				}
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(pwinfoService);
		}
	}
	
	private PwInfo pw;

	public void setPw(PwInfo pw) {
		this.pw = pw;
	}
	
}
