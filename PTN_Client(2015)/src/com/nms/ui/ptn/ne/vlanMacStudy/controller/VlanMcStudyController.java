package com.nms.ui.ptn.ne.vlanMacStudy.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nms.db.bean.ptn.SsMacStudy;
import com.nms.db.bean.ptn.VlanMacStudyInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.ptn.SecondMacStudyService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
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
import com.nms.ui.ptn.ne.vlanMacStudy.view.AddVlanMacStudyDialog;
import com.nms.ui.ptn.ne.vlanMacStudy.view.VlanMacMacPanel;

public class VlanMcStudyController extends AbstractController {
	private VlanMacMacPanel view;
	private int total;
	private int now = 1;
	private List<VlanMacStudyInfo> vlanMacStudyInfos;
	
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

	public void refresh(String value,String param) {
		this.searchAndRefreshData(value,param);
	}
	
	private void searchAndRefreshData(String value,String param) {
		
		List<VlanMacStudyInfo> needs = new ArrayList<VlanMacStudyInfo>();
		SsMacStudy ssMacStudyInfo =null;
		String[] params = param.split(",");
		try {
//			value = "01 43 00 00 00 0A 0B 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";
			String[] values = value.split(" ");
			vlanMacStudyInfos = new ArrayList<VlanMacStudyInfo>();
			for (int i = 0; i < (values.length-10)/6; i++) {
				VlanMacStudyInfo vlanMacStudyInfo = new VlanMacStudyInfo();
				String mac = "";
				for (int j = 0; j < 6; j++) {
					if(j!=5){
						mac +=values[9+i*6+j]+"-";
					}else{
						mac +=values[9+i*6+j];
					}
					
				}
				vlanMacStudyInfo.setLanid(Integer.parseInt(params[0]));
				vlanMacStudyInfo.setElanid(Integer.parseInt(params[1]));
				vlanMacStudyInfo.setName(params[2]);
				vlanMacStudyInfo.setMacAddress(mac);
				vlanMacStudyInfos.add(vlanMacStudyInfo);
			}
			ssMacStudyInfo = new SsMacStudy();
			ssMacStudyInfo.setSiteId(ConstantUtil.siteId);
			
			if(vlanMacStudyInfos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now =1;
				if (vlanMacStudyInfos.size() % ConstantUtil.flipNumber == 0) {
					total = vlanMacStudyInfos.size() / ConstantUtil.flipNumber;
				} else {
					total = vlanMacStudyInfos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getGoToJButton().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getGoToJButton().setEnabled(true);
				}
				if (vlanMacStudyInfos.size() - (now - 1) * ConstantUtil.flipNumber > ConstantUtil.flipNumber) {
					needs = vlanMacStudyInfos.subList((now - 1) * ConstantUtil.flipNumber, ConstantUtil.flipNumber);
				} else {
					needs = vlanMacStudyInfos.subList((now - 1) * ConstantUtil.flipNumber, vlanMacStudyInfos.size() - (now - 1) * ConstantUtil.flipNumber);
				}
			}
			
			this.view.clear();	
			this.view.getssMacStudyInfo().clear();
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
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
	
	@Override
    public void prevPage()throws Exception{
    	now = now-1;
    	if(now == 1){
    		view.getPrevPageBtn().setEnabled(false);
    	}
    	view.getNextPageBtn().setEnabled(true);
    	
    	flipRefresh();
    }

	private void flipRefresh() {
		view.getCurrPageLabel().setText(now+"");
    	try {
    		List<VlanMacStudyInfo> needTunnels = null;
    		if(now*ConstantUtil.flipNumber>vlanMacStudyInfos.size()){
    			needTunnels = vlanMacStudyInfos.subList((now-1)*ConstantUtil.flipNumber, vlanMacStudyInfos.size());
    		}else{
    			needTunnels = vlanMacStudyInfos.subList((now-1)*ConstantUtil.flipNumber, now*ConstantUtil.flipNumber);
    		}
    		this.view.clear();
			this.view.initData(needTunnels);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
		}
	}
}

