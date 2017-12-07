package com.nms.ui.ptn.business.etree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.StaticUnicastInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.SingleSpreadService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
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
import com.nms.ui.ptn.business.dialog.etreepath.AddEtreeDialog;
import com.nms.ui.ptn.ne.camporeData.CamporeBusinessDataDialog;

/**
 * 网络侧etree列表按钮事件控制类
 * 
 * @author lepan
 */
public class EtreeBusinessController extends AbstractController {

	/**
	 * etree列表界面对象，用来刷新界面用
	 */
	private EtreeBusinessPanel view;
	private Map<Integer, List<EtreeInfo>> etreeMap = null;
	private EtreeInfo etreeInfo=null;
	private int total;
	private int now = 0;
	private List<EtreeInfo> infos = null;
	public EtreeBusinessController(EtreeBusinessPanel view) {
		this.view = view;
	}

	@Override
	public void delete() throws Exception {
		List<EtreeInfo> infos = null;
		DispatchUtil etreeDispatch = null;
		boolean onlineFlag = false;
		List<Integer> allSiteIds = null;
		List<Integer> siteIds = null;
		List<StaticUnicastInfo> staticUniInfo = null;
		StaticUnicastInfo staticUni =null;
		StaticUnicastInfo staticUni1 =null;
		SingleSpreadService_MB uniService = null;
		List<StaticUnicastInfo> staticUniList = null;
		try {
			infos = this.getSelectEtree();
			//判断该etree上是否存在在线网元托管的
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
			}else{
				uniService = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
				staticUniInfo = new ArrayList<StaticUnicastInfo>();
				for(int i=0;i<infos.size();i++){
					staticUni = new StaticUnicastInfo();
					staticUni1 = new StaticUnicastInfo();
					if(infos.get(i).getaSiteId()>0){
					   staticUni.setSiteId(infos.get(i).getaSiteId());
					   staticUni.setVplsVs(infos.get(i).getaXcId());
					}
					if(infos.get(i).getzSiteId()>0){
					  staticUni1.setSiteId(infos.get(i).getzSiteId());
					  staticUni1.setVplsVs(infos.get(i).getzXcId());
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
				     if(staticUniList !=null && staticUniList.size()>0){				   
					    count++;
				     }
				}
				if(count!=0){
				   DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_DELETE_NOT));
				   return;
				}
			}
			
			etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			String result = etreeDispatch.excuteDelete(infos);
			// 添加日志记录
			for(EtreeInfo etree : infos){
				AddOperateLog.insertOperLog(null, EOperationLogType.ETREEDELETE.getValue(), result,
						null, null, -1, etree.getName(), null);
			}
			DialogBoxUtil.succeedDialog(this.view, result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			etreeDispatch = null;
			allSiteIds =null;
			siteIds =null;
			UiUtil.closeService_MB(uniService);
			staticUni=null;
			staticUniList=null;
			staticUni1=null;
			staticUniInfo=null;
		}
	}

	/**
	 * 根据选中的etree返回此etree中所有etree记录
	 * 
	 * @return 全量的etree数据
	 * @throws Exception
	 */
	private List<EtreeInfo> getSelectEtree() throws Exception {
		List<EtreeInfo> etreeInfoList = null;
		try {
			// 根据选中的一条etree数据的组ID 在map中找到此etree包含的所有数据。
			etreeInfoList = new ArrayList<EtreeInfo>();
			for (EtreeInfo etreeinfo : this.view.getAllSelect()) {
				etreeInfoList.addAll(this.etreeMap.get(etreeinfo.getServiceId()));
			}
		} catch (Exception e) {
			throw e;
		}
		return etreeInfoList;
	}

	@Override
	public void openCreateDialog() throws Exception {
		@SuppressWarnings("unused")
		AddEtreeDialog addEtreeBusiness = null;
		try {
			addEtreeBusiness = new AddEtreeDialog(this.view, true, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			addEtreeBusiness = null;
		}
	}

	@Override
	public void openUpdateDialog() throws Exception {
		@SuppressWarnings("unused")
		AddEtreeDialog addEtreeBusiness = null;
		List<EtreeInfo> etreeInfoList = null;
		List<StaticUnicastInfo> staticUniInfo = null;
		StaticUnicastInfo staticUni =null;
		StaticUnicastInfo staticUni1 =null;
		SingleSpreadService_MB uniService = null;
		List<StaticUnicastInfo> staticUniList = null;
		try {
			etreeInfoList = this.getSelectEtree();
			uniService = (SingleSpreadService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SINGELSPREAD);
			staticUniInfo = new ArrayList<StaticUnicastInfo>();
			for(int i=0;i<etreeInfoList.size();i++){
				staticUni = new StaticUnicastInfo();
				staticUni1 = new StaticUnicastInfo();
				staticUni.setSiteId(etreeInfoList.get(i).getaSiteId());
				staticUni.setVplsVs(etreeInfoList.get(i).getaXcId());
				staticUni1.setSiteId(etreeInfoList.get(i).getzSiteId());
				staticUni1.setVplsVs(etreeInfoList.get(i).getzXcId());
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
			
			addEtreeBusiness = new AddEtreeDialog(this.view, true, etreeInfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			addEtreeBusiness = null;
			etreeInfoList = null;
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
		EtreeInfoService_MB service = null;
		ListingFilter listingFilter = null;
		List<EtreeInfo> needs = new ArrayList<EtreeInfo>();
		try {
			listingFilter = new ListingFilter();
			service = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			
			if(null == this.etreeInfo){
				this.etreeInfo=new EtreeInfo();
			}
			
			etreeMap = service.filterSelect(this.etreeInfo);
			
			infos = new ArrayList<EtreeInfo>();
			if (null != etreeMap && etreeMap.size() > 0) {
				for (Map.Entry<Integer, List<EtreeInfo>> entrySet : etreeMap.entrySet()) {
					if (listingFilter.filterByList(entrySet.getValue())) {
						infos.add(entrySet.getValue().get(0));
					}
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
			List<EtreeInfo> etreeInfoList = this.getSelectEtree();
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			for (EtreeInfo etree : etreeInfoList) {
				PwInfo pw = pwService.selectByPwId(etree.getPwId());
				pwList.add(pw);
			}
			this.view.getPwNetworkTablePanel().initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
		}
	}

	/**
	 * 列表点击事件（客户信息）
	 */
	private void initClientInfo() {
		EtreeInfo etreeInfo = null;
		ClientService_MB clientService = null;
		List<Client> clientList = null;
		try {
			clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			etreeInfo = view.getSelect();
			if (0 != etreeInfo.getClientId()) {
				clientList = clientService.select(etreeInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
			} else {
				this.view.getClientInfoPanel().clear();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
			UiUtil.closeService_MB(clientService);
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
		EtreeInfo etreeInfo = null;
		try {
			etreeInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(etreeInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
		}
	}

	private void initTopoPanel() throws Exception {
		EtreeInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			throw e;
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
		EtreeInfo etreeInfo = null;
		List<Integer> acIdList = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfoList = null;
		UiUtil uiUtil = null;
		Set<Integer> acIdSet = null;
		try {
			etreeInfo = view.getSelect();
			uiUtil = new UiUtil();
			acIdSet = new HashSet<Integer>();
			// 查询出此tree的所有信息
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			etreeInfoList = etreeService.select(etreeInfo);

			for (int i = 0; i < etreeInfoList.size(); i++) {
				acIdSet.addAll(uiUtil.getAcIdSets(etreeInfoList.get(i).getAmostAcId()));
				acIdSet.addAll(uiUtil.getAcIdSets(etreeInfoList.get(i).getZmostAcId()));
			}
			acIdList = new ArrayList<Integer>(acIdSet);
			view.getPortNetworkTablePanel().clear();
			view.getPortNetworkTablePanel().initData(acIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
			acIdList = null;
			UiUtil.closeService_MB(etreeService);
			etreeInfoList = null;
			uiUtil = null;
			acIdSet = null;
		}
	}

	/**
	 * 激活处理事件
	 */
	public void doActive() {
		List<EtreeInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		List<EtreeInfo> etreeInfos = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfos2 = null;
		try {
			infos = this.view.getAllSelect();
			if (infos != null && infos.size() > 0) {
				etreeInfos = new ArrayList<EtreeInfo>();
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				etreeInfos2 = new ArrayList<EtreeInfo>();
				for (EtreeInfo info : infos) {
					etreeInfos = etreeService.selectByServiceId(info.getServiceId());
					for (EtreeInfo etreeInfo : etreeInfos) {
						etreeInfo.setActiveStatus(EActiveStatus.ACTIVITY.getValue());
						etreeInfo.setActivatingTime(DateUtil.getDate(DateUtil.FULLTIME));
						etreeInfos2.add(etreeInfo);
					}
				}
			}
			dispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			result = dispatch.excuteUpdate(etreeInfos2);
			DialogBoxUtil.succeedDialog(this.view, result);
			//添加日志记录*************************/
			if (infos != null && infos.size() > 0) {
				for (EtreeInfo info : infos) {
					AddOperateLog.insertOperLog(null, EOperationLogType.ETREEACIVE.getValue(), result, null, null, -1, info.getName(), null);
				}
			}
			//************************************/
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			etreeInfos = null;
			UiUtil.closeService_MB(etreeService);
			etreeInfos2 = null;
		}
	}
	
	/**
	 * 去激活处理事件
	 */
	public void doUnActive() {
		List<EtreeInfo> infos = null;
		String result = null;
		DispatchUtil dispatch = null;
		List<EtreeInfo> etreeInfos = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfos2 = null;
		try {
			infos = this.view.getAllSelect();
			if (infos != null && infos.size() > 0) {
				etreeInfos = new ArrayList<EtreeInfo>();
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				etreeInfos2 = new ArrayList<EtreeInfo>();
				for (EtreeInfo info : infos) {
					etreeInfos = etreeService.selectByServiceId(info.getServiceId());
					for (EtreeInfo etreeInfo : etreeInfos) {
						etreeInfo.setActiveStatus(EActiveStatus.UNACTIVITY.getValue());
						etreeInfo.setActivatingTime(null);
						etreeInfos2.add(etreeInfo);
					}
				}
			}
			dispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			result = dispatch.excuteUpdate(etreeInfos2);
			DialogBoxUtil.succeedDialog(this.view, result);
			//添加日志记录*************************/
			if (infos != null && infos.size() > 0) {
				for (EtreeInfo info : infos) {
					AddOperateLog.insertOperLog(null, EOperationLogType.ETREENOACTIVE.getValue(), result, null, null, -1, info.getName(), null);
				}
			}
			//************************************/
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			infos = null;
			dispatch = null;
			etreeInfos = null;
			UiUtil.closeService_MB(etreeService);
			etreeInfos2 = null;
		}
	}

	@Override
	public void search() throws Exception {
//		SearchSegmentDialog searchSegmentDialog = null;
//		try {
//			searchSegmentDialog = new SearchSegmentDialog(this.view);
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		} finally {
//			searchSegmentDialog = null;
//		}
		Thread.sleep(23000);
		DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
	}
	
	@Override
	public void openFilterDialog() throws Exception {
		new EthServiceFilterDialog(this.etreeInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.etreeInfo=null;
		this.refresh();
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
		List<EtreeInfo> needs = null;
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
		EtreeInfoService_MB etreeService = null;
		SiteService_MB siteService = null;
		Map<Integer, List<EtreeInfo>> etreeEMSMap = null;
		Map<Integer, List<EtreeInfo>> etreeNEMap = null;
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
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				etreeEMSMap = new HashMap<Integer, List<EtreeInfo>>();
				etreeNEMap = new HashMap<Integer, List<EtreeInfo>>();
				DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
				for(int siteId : siteIdOnLineList){
					List<EtreeInfo> nESingle = (List<EtreeInfo>) etreeDispatch.consistence(siteId);
					Map<String, List<EtreeInfo>> emsMap = etreeService.selectNodeBySite(siteId);
					List<EtreeInfo> eMSSingle = this.getEmsList(emsMap, siteId); 
					if(eMSSingle != null && !eMSSingle.isEmpty()){
						etreeEMSMap.put(siteId, eMSSingle);
					}
					if(nESingle != null && !nESingle.isEmpty()){
						etreeNEMap.put(siteId, nESingle);
					}
				}
				CamporeBusinessDataDialog camporeDataDialog = new CamporeBusinessDataDialog("ETREE", etreeEMSMap, etreeNEMap, this);
				UiUtil.showWindow(camporeDataDialog, 700, 600);
			}else{
				DialogBoxUtil.errorDialog(this.view, ResultString.QUERY_FAILED);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(etreeService);
		}
	}

	private List<EtreeInfo> getEmsList(Map<String, List<EtreeInfo>> emsMap, int siteId) {
		AcPortInfoService_MB acService = null;
		PwInfoService_MB pwService = null;
		List<EtreeInfo> etreeList = new ArrayList<EtreeInfo>();
		try {
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (String id_Type : emsMap.keySet()) {
				List<EtreeInfo> etreeInfoList = emsMap.get(id_Type);
				EtreeInfo etree = etreeInfoList.get(0);
				etree.getAcPortList().addAll(this.getAcInfo(siteId, etreeInfoList.get(0), acService));
				for (EtreeInfo etreeInfo : etreeInfoList) {
					etree.getPwNniList().add(this.getPwNniInfo(siteId, etreeInfo, pwService));
				}
				etreeList.add(etree);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acService);
			UiUtil.closeService_MB(pwService);
		}
		return etreeList;
	}

	private List<AcPortInfo> getAcInfo(int siteId, EtreeInfo etreeInfo, AcPortInfoService_MB acService) throws Exception {
		UiUtil uiutil = null;
		Set<Integer> acIds = null;
		List<Integer> acIdList = null;
		try {
			acIds = new HashSet<Integer>();
			uiutil = new UiUtil();
			if(etreeInfo.getRootSite() == siteId){
				acIds.addAll(uiutil.getAcIdSets(etreeInfo.getAmostAcId()));
			}else{
				acIds.addAll(uiutil.getAcIdSets(etreeInfo.getZmostAcId()));
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

	private PwNniInfo getPwNniInfo(int siteId, EtreeInfo etreeInfo, PwInfoService_MB pwService) throws Exception {
		PwInfo pw = new PwInfo();
		pw.setPwId(etreeInfo.getPwId());
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
