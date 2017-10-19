package com.nms.ui.ptn.ne.vlanMacStudy.controller;


import java.util.ArrayList;
import java.util.List;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.SecondMacStudyService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.db.bean.ptn.SsMacStudy;
import com.nms.db.bean.ptn.VlanMacStudyInfo;
import com.nms.ui.ptn.ne.ssMacStudy.view.AddSsMacStudyDialog;
import com.nms.ui.ptn.ne.ssMacStudy.view.StaticSecondMacPanel;
import com.nms.ui.ptn.ne.vlanMacStudy.view.AddVlanMacStudyDialog;
import com.nms.ui.ptn.ne.vlanMacStudy.view.VlanMacMacPanel;

public class VlanMcStudyController extends AbstractController {
	private VlanMacMacPanel view;
	
	public VlanMcStudyController(VlanMacMacPanel staticSecondMacPanel) {
		this.view = staticSecondMacPanel;
	}
	
	/**
	 * 新建
	 */
	@Override
	public void openCreateDialog(){
		
		VlanMacStudyInfo ssMacStudy = new VlanMacStudyInfo();
		if (this.view.getSelect() != null) {
			this.view.getSelect().setName("");
		}	
		new AddVlanMacStudyDialog(ssMacStudy, this);
	}

	/**
	 * 修改
	 */
	@Override
	public void openUpdateDialog(){
		
		if (this.view.getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			VlanMacStudyInfo mac = this.view.getAllSelect().get(0);
			new AddVlanMacStudyDialog(mac, this);
		}
		
	}

	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, ConstantUtil.siteId,ResourceUtil.srcStr(StringKeysTab.TAB_STATIC_SECOND_MAC),"");		
	}
	
	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		
		try {
			initInfoData();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 查询详细信息
	 * 
	 * @throws Exception
	 */
	private void initInfoData() throws Exception {

		VlanMacStudyInfo ssMacStudy = null;
		try {
			ssMacStudy = this.view.getSelect();						       
			this.view.getssMacStudyInfo().initData(ssMacStudy);
			this.view.getssMacStudyInfo().updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			ssMacStudy = null;
		}
	}

	
	/**
	 * 刷新
	 */
	@Override
	public void refresh() {
		
	}

	public void refresh(String value) {
		this.searchAndRefreshData(value);
	}
	
	private void searchAndRefreshData(String value) {
		
		List<VlanMacStudyInfo> allList = new ArrayList<VlanMacStudyInfo>();
		SecondMacStudyService_MB secondMacStudyService = null;
		SsMacStudy ssMacStudyInfo =null;
		try {
			ssMacStudyInfo = new SsMacStudy();
			ssMacStudyInfo.setSiteId(ConstantUtil.siteId);
			secondMacStudyService = (SecondMacStudyService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SECONDMACSTUDY);			
//			allList = secondMacStudyService.selectBySecondMacStudyInfo(ConstantUtil.siteId);
			this.view.clear();	
			this.view.getssMacStudyInfo().clear();
			this.view.initData(allList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(secondMacStudyService);
		}
	}
	
	
	@Override
	public void synchro(){
		try {
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_MACSTUDY);
			String result = dispatch.synchro(ConstantUtil.siteId);
			DialogBoxUtil.succeedDialog(this.view, result);
			this.insertOpeLog(EOperationLogType.SYSSECONDMAC.getValue(), result, null, null);		
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
}

