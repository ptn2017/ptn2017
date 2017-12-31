package com.nms.ui.ptn.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.report.SSProfess;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.report.StaticsticsService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTitle;


/**
 */
public class ProfessReportController extends AbstractController{
	private ProfessReportPanel view;
	private List<Integer> portIdList = new ArrayList<Integer>();
	private List<Integer> siteIdList = new ArrayList<Integer>();
	
	public ProfessReportController(ProfessReportPanel professPanel){
		this.setView(professPanel);
	}

	@Override
	public void refresh() throws Exception {
		List<SSProfess> ssList = null;
		StaticsticsService_MB ssService=null;
		try{
			ssService = (StaticsticsService_MB) ConstantUtil.serviceFactory.newService_MB(Services.STATISTICS);
			ssList = ssService.SSBusiness();
			this.view.clear();
			this.view.initData(ssList);
			this.view.updateUI();
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		}finally {
			UiUtil.closeService_MB(ssService);
		}
		
	}
			
	//导出统计数据保存到excel
	@Override
	public void export() throws Exception {
		
		List<SSProfess> infos = null;
		String result;
		ExportExcel export=null;
		// 得到页面信息
		try {
			infos =  this.view.getTable().getAllElement();
			export=new ExportExcel();
			//调用listString静态类，将得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(infos,"SSProfessPanel");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "SSProfessPanel");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.PROFESSEXPORT.getValue(),ResultString.CONFIG_SUCCESS, null, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infos = null;
			result=null;
			export=null;
		}
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TIT_PROFESS_COUNT),"");		
	}
	
	public ProfessReportPanel getView() {
		return view;
	}

	public void setView(ProfessReportPanel view) {
		this.view = view;
	}
	/**
	 * pw类型根据网元过滤
	 */
	public void filterByPw(List<Integer> siteIdList, List<Integer> portIdList) {
		this.portIdList = portIdList;
		this.siteIdList = siteIdList;
		this.filterSSProfess("PW");
	}

	/**
	 * tunnel类型根据网元+端口过滤
	 */
	public void filterByTunnel(List<Integer> siteIdList, List<Integer> portIdList) {
		this.portIdList = portIdList;
		this.siteIdList = siteIdList;
		this.filterSSProfess("TUNNEL");
	}
	
	/**
	 * CES类型根据网元过滤
	 */
	public void filterByCes(List<Integer> siteIdList, List<Integer> portIdList) {
		this.portIdList = portIdList;
		this.siteIdList = siteIdList;
		this.filterSSProfess("CES");
	}
	
	/**
	 * 以太网类型根据网元过滤
	 */
	public void filterByEth(List<Integer> siteIdList, List<Integer> portIdList) {
		this.portIdList = portIdList;
		this.siteIdList = siteIdList;
		this.filterSSProfess("ETH");
	}
	@SuppressWarnings("unchecked")
	private void filterSSProfess(String type) {
		List<SSProfess> ssList = null;	
		StaticsticsService_MB ssService = null;	
		PortService_MB portService = null;
		SiteService_MB siteService = null;
		List<PortInst> portList = new ArrayList<PortInst>();
		Map<Integer, String> siteNameMap = new HashMap<Integer, String>();
		try {
			//根据网元或者网元和端口查询
		
			if("TUNNEL".equals(type)){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				for (Integer siteId : this.siteIdList) {
					portList.addAll(portService.selectNniPortname(siteId));
					siteNameMap.put(siteId, siteService.getSiteName(siteId));
				}
				if(this.portIdList.isEmpty()){
					for (PortInst port : portList) {
						this.portIdList.add(port.getPortId());
					}
				}
				ssService = (StaticsticsService_MB) ConstantUtil.serviceFactory.newService_MB(Services.STATISTICS);
				ssList=ssService.SSBusinessByPortId(type,this.portIdList);

			}else if("PW".equals(type)){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				for (Integer siteId : this.siteIdList) {
					portList.addAll(portService.selectNniPortname(siteId));
					siteNameMap.put(siteId, siteService.getSiteName(siteId));
				}
				if(this.portIdList.isEmpty()){
					for (PortInst port : portList) {
						this.portIdList.add(port.getPortId());
					}
				}
				ssService = (StaticsticsService_MB) ConstantUtil.serviceFactory.newService_MB(Services.STATISTICS);
				ssList=ssService.SSBusinessByPortId(type,this.portIdList);
											
			}else if("CES".equals(type)){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);				
				for (Integer siteId : this.siteIdList) {
					portList.addAll(portService.selectE1Portname(siteId,type));
					siteNameMap.put(siteId, siteService.getSiteName(siteId));
				}
				if(this.portIdList.isEmpty()){
					for (PortInst port : portList) {
						this.portIdList.add(port.getPortId());
					}
				}
				ssService = (StaticsticsService_MB) ConstantUtil.serviceFactory.newService_MB(Services.STATISTICS);
				ssList=ssService.SSBusinessByPortId(type,this.portIdList);
				
			}else if("ETH".equals(type)){
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);				
				for (Integer siteId : this.siteIdList) {
					portList.addAll(portService.selectE1Portname(siteId,type));
					siteNameMap.put(siteId, siteService.getSiteName(siteId));
				}
				if(this.portIdList.isEmpty()){
					for (PortInst port : portList) {
						this.portIdList.add(port.getPortId());
					}
				}
				ssService = (StaticsticsService_MB) ConstantUtil.serviceFactory.newService_MB(Services.STATISTICS);
				ssList=ssService.SSBusinessByPortId(type,this.portIdList);
				
			}
			ListingFilter filter = new ListingFilter();
			List<SSProfess> infos = (List<SSProfess>) filter.filterList(ssList);
			this.view.clear();
			this.view.initData(infos);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(ssService);			
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(siteService);
		}
	}

	public List<Integer> getPortIdList() {
		return portIdList;
	}
	
	public List<Integer> getSiteIdList() {
		return siteIdList;
	}
	
}
