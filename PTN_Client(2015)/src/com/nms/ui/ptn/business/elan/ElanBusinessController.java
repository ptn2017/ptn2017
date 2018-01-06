﻿package com.nms.ui.ptn.business.elan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.StaticUnicastInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.SingleSpreadService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.filter.impl.EthServiceFilterDialog;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.basicinfo.dialog.segment.SearchSegmentDialog;
import com.nms.ui.ptn.business.dialog.elanpath.AddElanDialog;
import com.nms.ui.ptn.ne.camporeData.CamporeBusinessDataDialog;

/**
 * @author lepan
 */
public class ElanBusinessController extends AbstractController {
	private final ElanBusinessPanel view;
	private Map<Integer, List<ElanInfo>> elanMap = null;
	private ElanInfo elanInfo=null;
	private int total;
	private int now = 0;
	private List<ElanInfo> infos = new ArrayList<ElanInfo>();
	public ElanBusinessController(ElanBusinessPanel view) {
		this.view = view;
		try {
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@Override
	public void delete() throws Exception {
		List<ElanInfo> infos = null;
		boolean onlineFlag = false;
		List<Integer> allSiteIds =null;
		List<Integer> siteIds =null;
		List<StaticUnicastInfo> staticUniInfo = null;
		StaticUnicastInfo staticUni =null;
		StaticUnicastInfo staticUni1 =null;
		SingleSpreadService_MB uniService = null;
		List<StaticUnicastInfo> staticUniList = null;
		try {
			infos = this.getSelectElan();
			
			
			
			//判断该elan上是否存在在线网元托管的
			SiteUtil siteUtil = new SiteUtil();
			WhImplUtil whImplUtil = new WhImplUtil();
			allSiteIds = new ArrayList<Integer>();
			siteIds = new ArrayList<Integer>();
			allSiteIds=whImplUtil.getSiteIdList(infos);
			for(int i=0;i<allSiteIds.size();i++){
				if(1==siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))){
				   siteIds.add(allSiteIds.get(i));			    		
				}
		     }
			if(siteIds !=null && siteIds.size()!=0){
			   onlineFlag = true;
			}
			if(onlineFlag){
				String str=whImplUtil.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				return ;
			}
			
			//判断是否被静态单播所使用
			uniService = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
			staticUniInfo = new ArrayList<StaticUnicastInfo>();
			for(int i=0;i<infos.size();i++){
				staticUni = new StaticUnicastInfo();
				staticUni1 = new StaticUnicastInfo();
				if(infos.get(i).getaSiteId()>0){
				   staticUni.setSiteId(infos.get(i).getaSiteId());
				   staticUni.setVplsVs(infos.get(i).getAxcId());
				}
				if(infos.get(i).getzSiteId()>0){
				  staticUni1.setSiteId(infos.get(i).getzSiteId());
				  staticUni1.setVplsVs(infos.get(i).getZxcId());
				}
				staticUniInfo.add(staticUni);
				staticUniInfo.add(staticUni1);
			}
			for(int i=0;i<staticUniInfo.size();i++){
			    for(int j=staticUniInfo.size()-1;j>i;j--){
			    	if(staticUniInfo.get(j).getSiteId() == staticUniInfo.get(i).getSiteId() && 
			    		staticUniInfo.get(j).getVplsVs() == staticUniInfo.get(i).getVplsVs()){
			    		staticUniInfo.remove(j);
			    	}
			    }				    	
			}	
			int count=0;
			for(int i=0;i<staticUniInfo.size();i++){
				 staticUniList = uniService.selectByStaticUniInfo(staticUniInfo.get(i));
			     if(staticUniList.size()>0 && staticUniList !=null){				   
				    count++;
			     }
			}
			if(count!=0){
			   DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_NOT));
			   return;
			}
		
	
			
			DispatchUtil elanDispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			String result = elanDispatch.excuteDelete(infos);
//			String result = elanDispatch.excuteDelete(elanInfoList_delete);
			DialogBoxUtil.succeedDialog(this.view, result);
			// 添加日志记录
			for(ElanInfo elan : infos){
				AddOperateLog.insertOperLog(null, EOperationLogType.ELANDELETE.getValue(), result,
						null, null, -1, elan.getName(), null);
			}
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			allSiteIds =null;
			siteIds =null;
			UiUtil.closeService_MB(uniService);
			staticUni=null;
			staticUniList=null;
			staticUni1=null;
			staticUniInfo=null;
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void openCreateDialog() throws Exception {
		try {
			AddElanDialog addpwdialog = new AddElanDialog(this.view, true, 0, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void openUpdateDialog() throws Exception {
		ElanInfo info = null;
		List<ElanInfo> elanInfoList = null;
		List<StaticUnicastInfo> staticUniInfo = null;
		StaticUnicastInfo staticUni =null;
		StaticUnicastInfo staticUni1 =null;
		SingleSpreadService_MB uniService = null;
		List<StaticUnicastInfo> staticUniList = null;
		try {
			info = this.view.getSelect();
			if (info == null) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			} else {
				elanInfoList = this.getSelectElan();
				uniService = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
				staticUniInfo = new ArrayList<StaticUnicastInfo>();
				for(int i=0;i<elanInfoList.size();i++){
					staticUni = new StaticUnicastInfo();
					staticUni1 = new StaticUnicastInfo();
					staticUni.setSiteId(elanInfoList.get(i).getaSiteId());
					staticUni.setVplsVs(elanInfoList.get(i).getAxcId());
					staticUni1.setSiteId(elanInfoList.get(i).getzSiteId());
					staticUni1.setVplsVs(elanInfoList.get(i).getZxcId());
					staticUniInfo.add(staticUni);
					staticUniInfo.add(staticUni1);
				}
				for(int i=0;i<staticUniInfo.size();i++){
				    for(int j=staticUniInfo.size()-1;j>i;j--){
				    	if(staticUniInfo.get(j).getSiteId() == staticUniInfo.get(i).getSiteId() && 
				    		staticUniInfo.get(j).getVplsVs() == staticUniInfo.get(i).getVplsVs()){
				    		staticUniInfo.remove(j);
				    	}
				    }				    	
				}	
				int count=0;
				for(int i=0;i<staticUniInfo.size();i++){
					 staticUniList = uniService.selectByStaticUniInfo(staticUniInfo.get(i));
				     if(staticUniList.size()>0 && staticUniList !=null){				   
					    count++;
				     }
				}
				if(count!=0){
				   DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_NOT));
				   return;
				}
				
				AddElanDialog addpwdialog = new AddElanDialog(this.view, true, 0, info);

			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			elanInfoList = null;
			staticUniList = null;
			staticUniInfo = null;
			staticUni = null;
			staticUni1= null;
			staticUniList = null;
			UiUtil.closeService_MB(uniService);
		}
	}

	@Override
	public void refresh() throws Exception {
		List<ElanInfo> needs = new ArrayList<ElanInfo>();
		ElanInfoService_MB service = null;
		ListingFilter listingFilter = null;
		try {
			listingFilter = new ListingFilter();
			service = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			
			if(null==this.elanInfo){
				this.elanInfo=new ElanInfo();
			}
			
			elanMap = service.filterSelect(elanInfo);
			infos = new ArrayList<ElanInfo>();
			for (Map.Entry<Integer, List<ElanInfo>> entrySet : elanMap.entrySet()) {
				if (listingFilter.filterByList(entrySet.getValue())) {
					infos.add(entrySet.getValue().get(0));
				}
			}
			
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now =1;
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
			if (view.getPortNetworkTablePanel() != null) {
				view.getPortNetworkTablePanel().clear();
			}
			if (view.getPwNetworkTablePanel() != null) {
				view.getPwNetworkTablePanel().clear();
			}
			if (view.getClientInfoPanel() != null) {
				view.getClientInfoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			listingFilter = null;
		}
	}

	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		try {
			initTopoPanel();
			this.initPwNetworkTablePanel();
			this.initAcPanel();
			this.initSchematizePanel();
			initClientInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwService = null;
		try {
			List<ElanInfo> elanList = this.getElanInfoList(this.view.getSelect().getServiceId());
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			for (ElanInfo elan : elanList) {
				PwInfo pw = pwService.selectByPwId(elan.getPwId());
				pwList.add(pw);
			}
			this.view.getPwNetworkTablePanel().initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
		}
	}
	
	private List<ElanInfo> getElanInfoList(int elanServiceId) throws Exception {
		ElanInfoService_MB elanInfoservice = null;
		List<ElanInfo> elanInfoList = null;
		try {
			elanInfoservice = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			elanInfoList = elanInfoservice.select(elanServiceId);
		}finally{
			UiUtil.closeService_MB(elanInfoservice);
		}
		return elanInfoList;
	}

	/**
	 * 初始化图形化界面数据
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void initSchematizePanel() {
		ElanInfo elanInfo = null;
		try {
			elanInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(elanInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initTopoPanel() throws Exception {
		ElanInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 绑定AC列表数据
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void initAcPanel() {
		ElanInfo elanInfo = null;
		List<Integer> acIdList = null;
		ElanInfoService_MB elanInfoService = null;
		List<ElanInfo> elanInfoList = null;
		UiUtil uiUtil = null;
		Set<Integer> acIdSet = null;
		try {
			elanInfo = view.getSelect();
			uiUtil = new UiUtil();
			acIdSet = new HashSet<Integer>();
			
			// 查询出此tree的所有信息
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			elanInfoList = elanInfoService.select(elanInfo);

			for (int i = 0; i < elanInfoList.size(); i++) {
				acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getAmostAcId()));
				acIdSet.addAll(uiUtil.getAcIdSets(elanInfoList.get(i).getZmostAcId()));
			}
			acIdList = new ArrayList<Integer>(acIdSet);
			view.getPortNetworkTablePanel().clear();
			view.getPortNetworkTablePanel().initData(acIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elanInfo = null;
			acIdList = null;
			UiUtil.closeService_MB(elanInfoService);
			elanInfoList = null;
			acIdSet = null;
			uiUtil = null;
		}
	}

	/**
	 * 激活处理事件
	 */
	public void doActive() {
		List<ElanInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		List<ElanInfo> elanInfos = null;
		ElanInfoService_MB elanInfoService = null;
		List<ElanInfo> elanInfo2 = null;
		try {
			infos = this.view.getAllSelect();
			if (infos != null && infos.size() > 0) {
				elanInfos = new ArrayList<ElanInfo>();
				elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				elanInfo2 = new ArrayList<ElanInfo>();
				for (ElanInfo info : infos) {
					elanInfos = elanInfoService.selectByServiceId(info.getServiceId());
					for (ElanInfo elanInfo : elanInfos) {
						elanInfo.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
						elanInfo.setActivatingTime(DateUtil.getDate(DateUtil.FULLTIME));
						elanInfo2.add(elanInfo);
					}
				}
			}
			dispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			result = dispatch.excuteUpdate(elanInfo2);
			DialogBoxUtil.succeedDialog(this.view, result);
			//添加日志记录*************************/
			if (infos != null && infos.size() > 0) {
				for (ElanInfo info : infos) {
					AddOperateLog.insertOperLog(null, EOperationLogType.ELANACTIVE.getValue(), result, null, null, -1, info.getName(), null);
				}
			}
			//************************************/
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			elanInfos = null;
			UiUtil.closeService_MB(elanInfoService);
			elanInfo2 = null;
			result=null;
		}
	}
	
	/**
	 * 去激活处理事件
	 */
	public void doUnActive() {
		List<ElanInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		List<ElanInfo> elanInfos = null;
		ElanInfoService_MB elanInfoService = null;
		List<ElanInfo> elanInfo2 = null;
		try {
			infos = this.view.getAllSelect();
			if (infos != null && infos.size() > 0) {
				elanInfos = new ArrayList<ElanInfo>();
				elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				elanInfo2 = new ArrayList<ElanInfo>();
				for (ElanInfo info : infos) {
					elanInfos = elanInfoService.selectByServiceId(info.getServiceId());
					for (ElanInfo elanInfo : elanInfos) {
						elanInfo.setActiveStatus(EActiveStatus.UNACTIVITY.getValue());
						elanInfo.setActivatingTime(null);
						elanInfo2.add(elanInfo);
					}
				}
			}
			dispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			result = dispatch.excuteUpdate(elanInfo2);
			DialogBoxUtil.succeedDialog(this.view, result);
			//添加日志记录*************************/
			if (infos != null && infos.size() > 0) {
				for (ElanInfo info : infos) {
					AddOperateLog.insertOperLog(null, EOperationLogType.ELANDOACTIVE.getValue(), result, null, null, -1, info.getName(), null);
				}
			}
			//************************************/
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			elanInfos = null;
			UiUtil.closeService_MB(elanInfoService);
			elanInfo2 = null;
		}
	}

	/**
	 * 列表点击事件（客户信息）
	 */
	private void initClientInfo() {
		ElanInfo ElanInfo = null;
		ClientService_MB clientService = null;
		List<Client> clientList = null;
		try {
			clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			ElanInfo = view.getSelect();
			if (0 != ElanInfo.getClientId()) {
				clientList = clientService.select(ElanInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
			} else {
				this.view.getClientInfoPanel().clear();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(clientService);
		}

	}

	@Override
	public void search() throws Exception {
		try {
			new SearchSegmentDialog(this.view);
//			Thread.sleep(21000);
//			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}
	}
	
	@Override
	public void openFilterDialog() throws Exception {
		new EthServiceFilterDialog(this.elanInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.elanInfo=null;
		this.refresh();
	}
	/**
	 * 根据选中的elan返回此elan中所有elan记录
	 * 
	 * @return 全量的elan数据
	 * @throws Exception
	 */
	private List<ElanInfo> getSelectElan() throws Exception {
		List<ElanInfo> elanInfoList = null;
		try {
			// 根据选中的一条elan数据的组ID 在map中找到此elan包含的所有数据。
			elanInfoList = new ArrayList<ElanInfo>();
			for (ElanInfo elaninfo : this.view.getAllSelect()) {
				elanInfoList.addAll(this.elanMap.get(elaninfo.getServiceId()));
			}
		} catch (Exception e) {
			throw e;
		}
		return elanInfoList;
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
		List<ElanInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();
		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getPortNetworkTablePanel() != null) {
			view.getPortNetworkTablePanel().clear();
		}
		if (view.getPwNetworkTablePanel() != null) {
			view.getPwNetworkTablePanel().clear();
		}
		if (view.getClientInfoPanel() != null) {
			view.getClientInfoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		this.view.initData(needs);
		this.view.updateUI();
	}
	
	public void consistence(){
		ElanInfoService_MB elanService = null;
		SiteService_MB siteService = null;
		Map<Integer, List<ElanInfo>> elanEMSMap = null;
		Map<Integer, List<ElanInfo>> elanNEMap = null;
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
				elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				elanEMSMap = new HashMap<Integer, List<ElanInfo>>();
				elanNEMap = new HashMap<Integer, List<ElanInfo>>();
				DispatchUtil elanDispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
				for(int siteId : siteIdOnLineList){
					List<ElanInfo> nESingle = (List<ElanInfo>)elanDispatch.consistence(siteId);
					Map<Integer, List<ElanInfo>> emsMap = elanService.selectBySiteId(siteId);
					List<ElanInfo> eMSSingle = this.getEmsList(emsMap, siteId); 
					if(eMSSingle != null && !eMSSingle.isEmpty()){
						elanEMSMap.put(siteId, eMSSingle);
						
					}
					if(nESingle != null && !nESingle.isEmpty()){
						elanNEMap.put(siteId, nESingle);
					}
				}
				CamporeBusinessDataDialog camporeDataDialog = new CamporeBusinessDataDialog("ELAN", elanEMSMap, elanNEMap, this);
				if(elan != null){
					AddElanDialog addpwdialog = new AddElanDialog(this.view, true, 0, elan); 
				}
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(elanService);
		}
	}
	
	private ElanInfo elan;

	public void setElan(ElanInfo elan) {
		this.elan = elan;
	}

	private List<ElanInfo> getEmsList(Map<Integer, List<ElanInfo>> emsMap, int siteId) {
		AcPortInfoService_MB acService = null;
		PwInfoService_MB pwService = null;
		List<ElanInfo> elanList = new ArrayList<ElanInfo>();
		try {
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (int serviceId : emsMap.keySet()) {
				List<ElanInfo> elanInfoList = emsMap.get(serviceId);
				ElanInfo elan = elanInfoList.get(0);
				elan.getAcPortList().addAll(this.getAcInfo(siteId, elanInfoList.get(0), acService));
				for (ElanInfo elanInfo : elanInfoList) {
					elan.getPwNniList().add(this.getPwNniInfo(siteId, elanInfo, pwService));
				}
				elanList.add(elan);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acService);
			UiUtil.closeService_MB(pwService);
		}
		return elanList;
	}

	private List<AcPortInfo> getAcInfo(int siteId, ElanInfo elanInfo, AcPortInfoService_MB acService) throws Exception {
		int id = 0;
		UiUtil uiutil = null;
		Set<Integer> acIds = null;
		List<Integer> acIdList = null;
		try {
			acIds = new HashSet<Integer>();
			uiutil = new UiUtil();
			if(elanInfo.getaSiteId() == siteId){
//				id = elanInfo.getaAcId();
				acIds.addAll(uiutil.getAcIdSets(elanInfo.getAmostAcId()));
			}else{
//				id = elanInfo.getzAcId();
				acIds.addAll(uiutil.getAcIdSets(elanInfo.getZmostAcId()));
			}
			if(acIds.size() > 0)
			{
				acIdList = new ArrayList<Integer>(acIds);
				return  acService.select(acIdList);
			}
		} catch (Exception e)
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			 uiutil = null;
			 acIds = null;
			 acIdList = null;
		}
		return null;
	}

	private PwNniInfo getPwNniInfo(int siteId, ElanInfo elanInfo, PwInfoService_MB pwService) throws Exception {
		PwInfo pw = new PwInfo();
		pw.setPwId(elanInfo.getPwId());
		pw = pwService.selectBypwid_notjoin(pw);
		if(pw != null){
			if(pw.getASiteId() == siteId){
				return pw.getaPwNniInfo();
			}else if(pw.getZSiteId() == siteId){
				return pw.getzPwNniInfo();
			}
		}
		return null;
	}
}
