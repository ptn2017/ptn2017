﻿package com.nms.ui.ptn.business.eline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.ServiceInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
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
import com.nms.ui.ptn.business.dialog.eline.AddElineDialog;
import com.nms.ui.ptn.ne.camporeData.CamporeBusinessDataDialog;
import com.nms.ui.ptn.systemconfig.dialog.qos.ComparableSort;

/**
 * @author lepan
 */
public class ElineBusinessController extends AbstractController {

	private final ElineBusinessPanel view;
	private ElineInfo elineInfo = null;
	private int total;
	private int now = 0;
	private List<ElineInfo> infos = null;

	public ElineBusinessController(ElineBusinessPanel view) {
		this.view = view;
	}

	@Override
	public void delete() throws Exception {
		List<ElineInfo> infos = null;
		boolean onlineFlag = false;
		List<Integer> allSiteIds = null;
		List<Integer> siteIds = null;
		try {

			infos = this.view.getAllSelect();
			// 判断该eline上是否存在在线网元托管的
			SiteUtil siteUtil = new SiteUtil();
			allSiteIds = new ArrayList<Integer>();
			siteIds = new ArrayList<Integer>();
			for (ElineInfo eline : infos) {
				allSiteIds.add(eline.getaSiteId());
				allSiteIds.add(eline.getzSiteId());
			}
			for (int i = 0; i < allSiteIds.size(); i++) {
				if (1 == siteUtil.SiteTypeOnlineUtil(allSiteIds.get(i))) {
					siteIds.add(allSiteIds.get(i));
				}
			}
			if (siteIds != null && siteIds.size() != 0) {
				onlineFlag = true;
			}
			if (onlineFlag) {
				WhImplUtil wu = new WhImplUtil();
				String str = wu.getNeNames(siteIds);
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE) + "" + str + ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
				return;
			}

			DispatchUtil elineDispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
			String resultStr = elineDispatch.excuteDelete(infos);
			// 添加日志记录
			for (ElineInfo eline : infos) {
				AddOperateLog.insertOperLog(null, EOperationLogType.ELINEDELETE.getValue(), resultStr, null, null, eline.getaSiteId(), eline.getName(), null);
				AddOperateLog.insertOperLog(null, EOperationLogType.ELINEDELETE.getValue(), resultStr, null, null, eline.getzSiteId(), eline.getName(), null);
			}
			DialogBoxUtil.succeedDialog(this.view, resultStr);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			allSiteIds = null;
			siteIds = null;
		}
	}

	@Override
	public void openCreateDialog() throws Exception {
		try {

			AddElineDialog addelinedialog = new AddElineDialog(this.view, true, null);
			addelinedialog.setLocation(UiUtil.getWindowWidth(addelinedialog.getWidth()), UiUtil.getWindowHeight(addelinedialog.getHeight()));
			addelinedialog.setVisible(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@Override
	public void openUpdateDialog() throws Exception {
		ElineInfo info = null;
		try {
			info = this.view.getSelect();
			if (info == null) {
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			} else {
				AddElineDialog addelinedialog = new AddElineDialog(this.view, true, info);
				addelinedialog.setLocation(UiUtil.getWindowWidth(addelinedialog.getWidth()), UiUtil.getWindowHeight(addelinedialog.getHeight()));
				addelinedialog.setVisible(true);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh() throws Exception {
		ElineInfoService_MB elineInfoServiceMB = null;
		ListingFilter filter = null;
		List<ElineInfo> needs = new ArrayList<ElineInfo>();
		try {
			filter = new ListingFilter();
			elineInfoServiceMB = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);

			if (null == this.elineInfo) {
				this.elineInfo = new ElineInfo();
			}

			infos = (List<ElineInfo>) filter.filterList(elineInfoServiceMB.selectByCondition(this.elineInfo));
			
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
			if (this.view.getOamTable() != null) {
				this.view.getOamTable().clear();
			}
			if (view.getTopoPanel() != null) {
				view.getTopoPanel().clear();
			}
			if (view.getPortNetworkTablePanel() != null) {
				view.getPortNetworkTablePanel().clear();
			}
			if (view.getClientInfoPanel() != null) {
				view.getClientInfoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			if (view.getPwNetworkTablePanel() != null) {
				this.view.getPwNetworkTablePanel().clear();
			}
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			filter = null;
			UiUtil.closeService_MB(elineInfoServiceMB);
		}
	}

	@Override
	public void search() throws Exception {
		try {
			new SearchSegmentDialog(this.view);
//			Thread.sleep(22000);
//			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
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
			initOamPanel();
			initAcPanel();
			this.initSchematizePanel();
			initClientInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwServiceMB = null;
		try {
			ElineInfo elineInfo = this.view.getSelect();
			pwServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo pw = pwServiceMB.selectByPwId(elineInfo.getPwId());
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			pwList.add(pw);
			this.view.getPwNetworkTablePanel().initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwServiceMB);
		}
	}

	/**
	 * 列表点击事件（客户信息）
	 */
	private void initClientInfo() {
		ElineInfo elineInfo = null;
		ClientService_MB clientServiceMB = null;
		List<Client> clientList = null;
		try {
			clientServiceMB = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			elineInfo = view.getSelect();

			if (0 != elineInfo.getClientId()) {
				clientList = clientServiceMB.select(elineInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
				this.view.updateUI();
			} else {
				this.view.getClientInfoPanel().clear();
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
			UiUtil.closeService_MB(clientServiceMB);
			clientList = null;
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
		ElineInfo elineInfo = null;
		try {
			elineInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(elineInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
		}
	}

	@SuppressWarnings("unchecked")
	private void initOamPanel() throws Exception {
		OamInfoService_MB oamInfoServiceMB = null;
		ElineInfo elineInfo = null;
		List<OamInfo> oamList = null;
		try {
			elineInfo = this.view.getSelect();
			oamList = new ArrayList<OamInfo>();
			oamInfoServiceMB = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			OamInfo oam = new OamInfo();
			OamMepInfo oamMep = new OamMepInfo();
			oamMep.setServiceId(elineInfo.getId());
			oamMep.setObjType(EServiceType.ELINE.toString());
			oam.setOamMep(oamMep);
			oamList = oamInfoServiceMB.queryByServiceId(oam);
			ComparableSort sort = new ComparableSort();
			oamList = (List<OamInfo>) sort.compare(oamList);
			this.view.getOamTable().clear();
			this.view.getOamTable().initData(oamList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoServiceMB);
			elineInfo = null;
			oamList = null;
		}
	}

	private void initTopoPanel() {
		ElineInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			info = null;
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
		ElineInfo elineInfo = null;
		List<Integer> acIdList = null;
		try {
			acIdList = new ArrayList<Integer>();
			elineInfo = view.getSelect();

			acIdList.add(elineInfo.getaAcId());
			acIdList.add(elineInfo.getzAcId());

			view.getPortNetworkTablePanel().clear();
			view.getPortNetworkTablePanel().initData(acIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
			acIdList = null;
		}
	}

	/**
	 * 激活处理事件
	 */
	public void doActive() {
		List<ElineInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		try {
			infos = this.view.getAllSelect();
			int failCount = 0;
			if (infos != null && infos.size() > 0) {
				dispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
				for (ElineInfo info : infos) {
					info.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
					info.setActivatingTime(DateUtil.getDate(DateUtil.FULLTIME));
					result = dispatch.excuteUpdate(info);
					if (result == null || !result.contains(ResultString.CONFIG_SUCCESS)) {
						failCount++;
					}
					// 添加日志记录*************************/
					AddOperateLog.insertOperLog(null, EOperationLogType.ELINEACIVE.getValue(), result, null, null, -1, info.getName(), null);
					// ************************************/
				}
				result = ResourceUtil.srcStr(StringKeysTip.TIP_BATCH_CREATE_RESULT);
				result = result.replace("{C}", (infos.size() - failCount) + "");
				result = result.replace("{S}", failCount + "");
			}
			String str = this.getOfflineSiteIdNames(infos);
			if (!str.equals("")) {
				result += "," + str + ResultString.NOT_ONLINE_SUCCESS;
			}
			DialogBoxUtil.succeedDialog(this.view, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			result = null;
		}
	}

	private String getOfflineSiteIdNames(List<ElineInfo> elineList) throws Exception {
		List<Integer> siteIds = null;
		String str = "";
		try {
			siteIds = new ArrayList<Integer>();
			for (ElineInfo eline : elineList) {
				siteIds.add(eline.getaSiteId());
				siteIds.add(eline.getzSiteId());
			}
			str = new WhImplUtil().getNeNames(siteIds);
		} catch (Exception e) {
			throw e;
		}
		return str;
	}

	/**
	 * 去激活处理事件
	 */
	public void doUnActive() {
		List<ElineInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		try {
			infos = this.view.getAllSelect();
			int failCount = 0;
			if (infos != null && infos.size() > 0) {
				dispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
				for (ElineInfo info : infos) {
					info.setActiveStatus(EActiveStatus.UNACTIVITY.getValue());
					info.setActivatingTime(null);
					result = dispatch.excuteUpdate(info);
					if (result == null || !result.contains(ResultString.CONFIG_SUCCESS)) {
						failCount++;
					}
					// 添加日志记录*************************/
					AddOperateLog.insertOperLog(null, EOperationLogType.ELINEDOACIVE.getValue(), result, null, null, -1, info.getName(), null);
					// ************************************/
				}
				result = ResourceUtil.srcStr(StringKeysTip.TIP_BATCH_CREATE_RESULT);
				result = result.replace("{C}", (infos.size() - failCount) + "");
				result = result.replace("{S}", failCount + "");
			}
			String str = this.getOfflineSiteIdNames(infos);
			if (!str.equals("")) {
				result += "," + str + ResultString.NOT_ONLINE_SUCCESS;
			}
			DialogBoxUtil.succeedDialog(this.view, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			result = null;
		}
	}

	public void searchElineBusiness() {
		// ElineInfo elineInfo = null;
		// List<ELineObject> elineObject = null;
		// ActionObject actionObject = new ActionObject();
		// actionObject.setElineObjectList(elineObject);
		// try {
		// elineInfo.setPwId(elineObject.get(0).getVpwsId());
		// elineInfo.setaSiteId(elineObject.get(0).getPwIdNNI());
		//
		// } catch (Exception e) {
		// ExceptionManage.dispose(e, this.getClass());
		// } finally {
		// elineInfo = null;
		// elineObject = null;
		//
		// }
	}

	public void searchOam() throws Exception {
		// ElineInfo elineInfo = null;
		// List<OamInfo> oamList = null;
		// OamInfo oam = null;
		// OamMepInfo oamMep = null;
		// List<ELineObject> elineObject = null;
		// ActionObject actionObject = new ActionObject();
		// actionObject.setElineObjectList(elineObject);
		// try {
		//
		// oam.setOamMep(oamMep);
		// elineInfo.setOamList(oamList);
		//
		// } catch (Exception e) {
		// ExceptionManage.dispose(e, this.getClass());
		// } finally {
		// elineInfo = null;
		// oamList = null;
		// oam = null;
		// oamMep = null;
		// elineObject = null;
		//
		// }
	}

	@Override
	public void openFilterDialog() throws Exception {
		new EthServiceFilterDialog(this.elineInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.elineInfo = null;
		this.refresh();
	}

	/**
	 * 复制业务
	 */
	public void copy() {
		List<ElineInfo> elineList = this.view.getAllSelect();
		if (elineList != null && elineList.size() == 1) {

		} else {
			DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		}
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
		List<ElineInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();
		if (this.view.getOamTable() != null) {
			this.view.getOamTable().clear();
		}
		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getPortNetworkTablePanel() != null) {
			view.getPortNetworkTablePanel().clear();
		}
		if (view.getClientInfoPanel() != null) {
			view.getClientInfoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		if (view.getPwNetworkTablePanel() != null) {
			this.view.getPwNetworkTablePanel().clear();
		}
		this.view.initData(needs);
		this.view.updateUI();
	}
	
	public void consistence(){
		ElineInfoService_MB elineService = null;
		SiteService_MB siteService = null;
		Map<Integer, List<ElineInfo>> elineEMSMap = null;
		Map<Integer, List<ElineInfo>> elineNEMap = null;
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
				elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				elineEMSMap = new HashMap<Integer, List<ElineInfo>>();
				elineNEMap = new HashMap<Integer, List<ElineInfo>>();
				DispatchUtil elineDispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
				for(int siteId : siteIdOnLineList){
					List<ElineInfo> eMSSingle = elineService.selectElineBySite(siteId);
					List<ServiceInfo> nESingle = (List<ServiceInfo>) elineDispatch.consistence(siteId);
					if(eMSSingle != null && !eMSSingle.isEmpty()){
						elineEMSMap.put(siteId, eMSSingle);
					}
					if(nESingle != null && !nESingle.isEmpty()){
						elineNEMap.put(siteId, this.filterElineList(nESingle));
					}
				}
				CamporeBusinessDataDialog camporeDataDialog = new CamporeBusinessDataDialog("ELINE", elineEMSMap, elineNEMap, this);
				if(eline != null){
					AddElineDialog addelinedialog = new AddElineDialog(this.view, true, eline);
					addelinedialog.setLocation(UiUtil.getWindowWidth(addelinedialog.getWidth()), UiUtil.getWindowHeight(addelinedialog.getHeight()));
					addelinedialog.setVisible(true);
				}
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(elineService);
		}
	}
	
	/**
	 * 过滤出eline业务
	 */
	private List<ElineInfo> filterElineList(List<ServiceInfo> neList) {
		List<ElineInfo> elineList = new ArrayList<ElineInfo>();
		for (ServiceInfo elineInfo : neList) {
			if(elineInfo.getServiceType() == 1){
				elineList.add((ElineInfo)elineInfo);
			}
		}
		return elineList;
	}
	
	private ElineInfo eline;

	public void setEline(ElineInfo eline) {
		this.eline = eline;
	}
	
}
