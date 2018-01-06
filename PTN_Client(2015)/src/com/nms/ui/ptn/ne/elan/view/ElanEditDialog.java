﻿package com.nms.ui.ptn.ne.elan.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.eth.VplsInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;

public class ElanEditDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8028375726038793791L;
	private ElanInfo elanInfo;
	private ElanPanel elanPanel;
	private AcPortInfo eLanAc;
	private List<ElanInfo> elanInfoListForUpdate;
	public ElanEditDialog(ElanInfo elanInfo, ElanPanel elanPanel) {
		try {
			this.elanInfo = elanInfo;
			this.elanPanel = elanPanel;
			this.initComponent();
			this.setLayout();
			this.listPwData();
			this.listAcData();
			this.addListener();
			this.initData();
			if(ResourceUtil.language.equals("zh_CN")){
				UiUtil.showWindow(this, 510, 500);
			}else{
				UiUtil.showWindow(this, 550, 500);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 初始化表单数据
	 * @throws Exception 
	 */
	private void initData() throws Exception {
		try {
			if (null == this.elanInfo) {
				this.elanInfo = new ElanInfo();
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_ELAN));
			}else{
				this.ptnSpinnerNumber.setEnabled(false);
				this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_ELAN));
				this.txtname.setText(this.elanInfo.getName());
//				this.btnRight.setEnabled(false);
//				this.btnLeft.setEnabled(false);
				this.chkactivate.setSelected(this.elanInfo.getActiveStatus()==1?true:false);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ElanEditDialog.this.dispose();
			}
		});

		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveInfo();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
		// 自动命名事件
		jButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonActionPerformed(evt);
			}
		});

		this.btnRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnAction(listPw,ListSelectPw);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		this.btnLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnAction(ListSelectPw,listPw);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		this.btnAcRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnAction(listAC,listSelectAC);
				} catch (Exception e) {
					e.printStackTrace();
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
		
		this.btnAcLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					btnAction(listSelectAC,listAC);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
	}

	/**
	 * 向右选择pw按钮事件
	 * sourceList 源
	 * purposeList 目的
	 * @throws Exception
	 */
	private void btnAction(JList sourceList,JList purposeList) throws Exception {
		int index = 0;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListModel_select = null;
		try {
			index = sourceList.getSelectedIndex();
			if (index >= 0) {
				defaultListModel_select = (DefaultListModel) purposeList.getModel();
				defaultListModel = (DefaultListModel) sourceList.getModel();
				defaultListModel_select.addElement(defaultListModel.getElementAt(index));
				defaultListModel.removeElementAt(index);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			defaultListModel = null;
			defaultListModel_select = null;
		}
	}

	/**
	 * 绑定pw的list数据
	 * 
	 * @throws Exception
	 */
	private void listPwData() throws Exception {
		PwInfoService_MB pwInfoService = null;
		List<PwInfo> pwInfoList = null;
		DefaultListModel defaultListModel = null;
		ElanInfoService_MB elanInfoService = null;
		DefaultListModel defaultListModelSel = null;
		PwInfo pwInfoSel;
		try {
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfoList = pwInfoService.getAvailable(ConstantUtil.siteId,EPwType.ETH);

			defaultListModel = new DefaultListModel();
			if (null != pwInfoList && pwInfoList.size() > 0) {
				for (PwInfo pwInfo : pwInfoList) {
					defaultListModel.addElement(new ControlKeyValue(pwInfo.getPwId() + "", pwInfo.getPwName(), pwInfo));
				}
			}
			this.listPw.setModel(defaultListModel);
			//选中PW
			if(null!=this.elanInfo){
				elanInfoListForUpdate =  elanInfoService.selectByServiceId(this.elanInfo.getServiceId());
				defaultListModelSel = new DefaultListModel();
				pwInfoList = new ArrayList<PwInfo>();
				for (ElanInfo elanInfo : elanInfoListForUpdate) {
					pwInfoSel = new PwInfo();
					pwInfoSel.setPwId(elanInfo.getPwId());
					pwInfoSel = pwInfoService.selectBypwid_notjoin(pwInfoSel);
					pwInfoList.add(pwInfoSel);
				}
				if (null != pwInfoList && pwInfoList.size() > 0) {
					for (PwInfo pwInfo : pwInfoList) {
						defaultListModelSel.addElement(new ControlKeyValue(pwInfo.getPwId() + "", pwInfo.getPwName(), pwInfo));
					}
					this.ListSelectPw.setModel(defaultListModelSel);
				}
			}else{
				this.ListSelectPw.setModel(new DefaultListModel());
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(pwInfoService);
			pwInfoList = null;
			defaultListModel = null;
		}
	}
	
	
  
	private void listAcData() 
	{
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acportInfoList = null;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListModelSel = null;
		List<ElanInfo> elanInfoList = null;
		ElanInfoService_MB elanInfoService = null;
		List<Integer> acIdList = null;
		Set<Integer> acSet = null;
		UiUtil uiUtil = null;
		try 
		{
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acportInfoList = acInfoService.selectBySiteId(ConstantUtil.siteId);
			defaultListModel = new DefaultListModel();
			if (acportInfoList != null && acportInfoList.size() > 0) {
				for (AcPortInfo acPortInfo : acportInfoList) {
					if (acPortInfo.getIsUser() == 0) {
						defaultListModel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
					}
				}
			}
			this.listAC.setModel(defaultListModel);
			//选中的AC
			if(null!=this.elanInfo){
				uiUtil = new UiUtil();
				elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				elanInfoList =  elanInfoService.selectByServiceId(this.elanInfo.getServiceId());
				defaultListModelSel = new DefaultListModel();
				acSet = new HashSet<Integer>();
				acIdList = new ArrayList<Integer>();
				for (ElanInfo elanInfo : elanInfoList) {
					acSet.addAll(uiUtil.getAcIdSets(elanInfo.getAmostAcId()));
					acSet.addAll(uiUtil.getAcIdSets(elanInfo.getZmostAcId()));
//					if(elanInfo.getaAcId() >0 )
//					{
//						acIdList.add(elanInfo.getaAcId());
//					}else
//					{
//						acIdList.add(elanInfo.getzAcId());
//					}
				}
				acIdList = new ArrayList<Integer>(acSet);
				acportInfoList = acInfoService.select(acIdList);
				if (acportInfoList != null && acportInfoList.size() > 0) {
					for (AcPortInfo acPortInfo : acportInfoList) {
						defaultListModelSel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
					}
				}				
				this.listSelectAC.setModel(defaultListModelSel);
			}else{
				this.listSelectAC.setModel(new DefaultListModel());
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(elanInfoService);
			acportInfoList = null;
			defaultListModel = null;
			acportInfoList = null;
			defaultListModelSel = null;
			elanInfoList = null;
			acIdList = null;
		}
	}

	// 自动命名事件
	private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			elanInfo.setIsSingle(1);
			elanInfo.setaSiteId(ConstantUtil.siteId);
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			String autoNaming = (String) autoNamingUtil.autoNaming(elanInfo, null, null);
			txtname.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 保存方法
	 * 
	 * @throws Exception
	 */
	private void saveInfo() throws Exception {

		List<ElanInfo> elanInfoList = null;
		List<Integer> acIdList = null;
		List<AcPortInfo> useAcPortList = null;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListModelAc = null;
		PwInfo pwinfo = null;
		DispatchUtil elanDispatch = null;
		String resultStr = null;
		String beforeName = null;
		AcPortInfoService_MB acInfoService = null;
		ElanInfoService_MB elanInfoService = null;
		ElanInfo elanInfoAction = null;
		try {

			if (!this.isFull()) {
				return;
			}
			if (this.elanInfo.getId() != 0) {
				beforeName = this.elanInfo.getName();
			}
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyNameBySingle(EServiceType.ELAN.getValue(), this.txtname.getText().trim(), beforeName, ConstantUtil.siteId)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			if (!this.isSelectPw()) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PW_ERROR));
				return;
			}
			
			defaultListModel = (DefaultListModel) this.ListSelectPw.getModel();
			if(defaultListModel.getSize()>64)
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_EXCEEDPWELANNUMBER));
				return;
			}
			defaultListModelAc  = (DefaultListModel) this.listSelectAC.getModel();
			if(defaultListModelAc.getSize()>10)
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_EXCEEDACELANNUMBER));
				return;
			}
			acIdList = new ArrayList<Integer>();
			useAcPortList = new ArrayList<AcPortInfo>();
			getAllAcId(acIdList,useAcPortList);
			List<Integer> pwIdList = new ArrayList<Integer>();
			
			// 批量创建
			int num = Integer.parseInt(this.ptnSpinnerNumber.getTxt().getText());
			List<List<ElanInfo>> elanBatchList = new ArrayList<List<ElanInfo>>();
			if(num > 1){
				if((defaultListModel.getSize() < 2*num) || (defaultListModelAc.getSize() != num)){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTCOUNT_NOT_EQUAL));
					return;
				}
				for(int i = 0; i < num; i++){
					elanInfoList = new ArrayList<ElanInfo>();
					String batchName = this.txtname.getText().trim();
					if(i > 0){
						batchName += "copy_"+i;
					}
					for(int j = 0; j < 2; j++){
						pwinfo = (PwInfo) ((ControlKeyValue) defaultListModel.getElementAt(2*i+j)).getObject();
						elanInfoAction = new ElanInfo();
						//匹配pw和 原elan数据
						elanInfoAction.setName(batchName);
						elanInfoAction.setServiceType(EServiceType.ELAN.getValue());
						elanInfoAction.setActiveStatus(this.chkactivate.isSelected() ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
						elanInfoAction.setIsSingle(1);
						elanInfoAction.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
						if(chkactivate.isSelected()){
							elanInfoAction.setActivatingTime(elanInfoAction.getCreateTime());
						}else{
							elanInfoAction.setActivatingTime(null);
						}
						elanInfoAction.setCreateUser(ConstantUtil.user.getUser_Name());
						elanInfoAction.setPwId(pwinfo.getPwId());
						pwIdList.add(pwinfo.getPwId());
						elanInfoAction.setaSiteId(ConstantUtil.siteId);
						AcPortInfo ac =  (AcPortInfo) ((ControlKeyValue) defaultListModelAc.getElementAt(i)).getObject();
						elanInfoAction.setAmostAcId(ac.getId()+"");
						elanInfoList.add(elanInfoAction);
					}
					elanBatchList.add(elanInfoList);
				}
			}else{
				elanInfoList = new ArrayList<ElanInfo>();
			for (int i = 0; i < defaultListModel.getSize(); i++) {
				pwinfo = (PwInfo) ((ControlKeyValue) defaultListModel.getElementAt(i)).getObject();
				elanInfoAction = new ElanInfo();
					//匹配pw和 原elan数据
				elanInfoAction.setName(this.txtname.getText().trim());
				elanInfoAction.setServiceType(EServiceType.ELAN.getValue());
				elanInfoAction.setActiveStatus(this.chkactivate.isSelected() ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
				elanInfoAction.setIsSingle(1);
				elanInfoAction.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
					if(chkactivate.isSelected()){
						elanInfoAction.setActivatingTime(elanInfoAction.getCreateTime());
					}else{
						elanInfoAction.setActivatingTime(null);
					}
				elanInfoAction.setCreateUser(ConstantUtil.user.getUser_Name());
				elanInfoAction.setPwId(pwinfo.getPwId());
				pwIdList.add(pwinfo.getPwId());
				elanInfoAction.setaSiteId(ConstantUtil.siteId);
				elanInfoAction.setAmostAcId(acIdList.toString().subSequence(1, acIdList.toString().length() -1).toString());
				elanInfoList.add(elanInfoAction);
			}
				elanBatchList.add(elanInfoList);
			}
			
			
			
			elanDispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			if(this.elanInfo.getServiceId()>0){
				integrateElanList(elanInfoList);
				VplsInfo vplsBefore = this.getVplsBefore(null, 0, null);
				resultStr = elanDispatch.excuteUpdate(elanInfoListForUpdate);
				//添加日志记录
				VplsInfo vplsInfo = this.getVplsBefore(null, 0, pwIdList);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.ELANUPDATE.getValue(), resultStr, vplsBefore, vplsInfo, ConstantUtil.siteId, vplsInfo.getVplsName(), "elan");
			}else{
				for(List<ElanInfo> list : elanBatchList){
					resultStr = elanDispatch.excuteInsert(list);
				//添加日志记录
					VplsInfo vplsInfo = this.getVplsBefore(list, 1, null);
				AddOperateLog.insertOperLog(btnSave, EOperationLogType.ELANINSERT.getValue(), resultStr, null, vplsInfo, ConstantUtil.siteId, vplsInfo.getVplsName(), "elan");
			}
			}
			
			DialogBoxUtil.succeedDialog(this, resultStr);
			this.elanPanel.getController().refresh();
			this.dispose();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(elanInfoService);
			UiUtil.closeService_MB(acInfoService);
		}
	}
	
	private VplsInfo getVplsBefore(List<ElanInfo> elanInfoList, int type, List<Integer> pwIdList) {
		ElanInfoService_MB service = null;
		SiteService_MB siteService = null;
		PwInfoService_MB pwService = null;
		VplsInfo vplsInfo = new VplsInfo();
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			if(type == 0){
				service = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				if(pwIdList == null){
					List<ElanInfo> elanList_db =  service.selectByServiceId(this.elanInfo.getServiceId());
					pwIdList = new ArrayList<Integer>();
					for (ElanInfo elanInfo : elanList_db) {
						pwIdList.add(elanInfo.getPwId());
					}
					elanInfoList = service.selectElanbypwid(pwIdList);
				}else{
					elanInfoList = service.selectElanbypwid(pwIdList);
				}
			}
			List<ElanInfo> elanList_log = new ArrayList<ElanInfo>();
			ElanInfo elanLog = new ElanInfo();
			elanLog.setNodeName(siteService.getSiteName(ConstantUtil.siteId));
			for (ElanInfo elan : elanInfoList) {
				if(elan.getaSiteId() == ConstantUtil.siteId){
					elanLog.setAcNameList(this.getAcNameList(elan.getAmostAcId()));
					break;
				}else if(elan.getzSiteId() == ConstantUtil.siteId){
					elanLog.setAcNameList(this.getAcNameList(elan.getZmostAcId()));
					break;
				}
			}
			List<CommonBean> pwNameList = new ArrayList<CommonBean>();
			for (ElanInfo elan : elanInfoList) {
				if(elan.getaSiteId() == ConstantUtil.siteId || elan.getzSiteId() == ConstantUtil.siteId){
					CommonBean cb = new CommonBean();
					PwInfo pwCondition = new PwInfo();
					pwCondition.setPwId(elan.getPwId());
					cb.setPwName4vpls(pwService.selectBypwid_notjoin(pwCondition).getPwName());
					pwNameList.add(cb);
				}
			}
			elanLog.setaSiteId(ConstantUtil.siteId);
			elanLog.setPwNameList(pwNameList);
			elanList_log.add(elanLog);
			vplsInfo.setVplsName(elanInfoList.get(0).getName());
			vplsInfo.setActiveStatus(elanInfoList.get(0).getActiveStatus());
			vplsInfo.setElanInfoList(elanList_log);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(siteService);
		}
		return vplsInfo;
	}
	
	/**
	 * 根据acId数组获取ac名称
	 * @param amostAcId
	 * @return
	 */
	private List<CommonBean> getAcNameList(String amostAcId) {
		AcPortInfoService_MB acService = null;
		List<CommonBean> acNameList = null;
		try {
			if(amostAcId != null){
				acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				List<Integer> acIdList = new ArrayList<Integer>();
				if(amostAcId.length() > 1){
					for (String id : amostAcId.split(",")) {
						acIdList.add(Integer.parseInt(id.trim()));
					}
				}else{
					acIdList.add(Integer.parseInt(amostAcId));
				}
				acNameList = new ArrayList<CommonBean>();
				List<AcPortInfo> acList = acService.select(acIdList);
				for (AcPortInfo acInfo : acList) {
					CommonBean acName = new CommonBean();
					acName.setAcName(acInfo.getName());
					acNameList.add(acName);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(acService);
		}
		return acNameList;
	}

	private void integrateElanList(List<ElanInfo> elanInfoList) {
		
		ElanInfo elanInfo_update = null;
		if(null==this.elanInfoListForUpdate)
		{
			return ;
		}
		//先把所有修改的Elan数据改成删除状态,之后匹配数据,会把状态该成其他状态
		for(ElanInfo elanInst : elanInfoListForUpdate)
		{
			elanInst.setAction(3);
			elanInst.setSiteId(ConstantUtil.siteId);
		}
		
		//收集所有新数据
		for(ElanInfo elanInst : elanInfoList)
		{
			elanInfo_update = this.findElan(elanInst);
			if(null != elanInfo_update)
			{
				this.integrateElan(elanInfo_update,elanInst);
			}else
			{
				//如果为空 说明为新增加的PW
				elanInst.setCreateTime(elanInfoListForUpdate.get(0).getCreateTime());
				if(chkactivate.isSelected()){
					elanInst.setActivatingTime(this.elanInfoListForUpdate.get(0).getActivatingTime());
				}else{
					elanInst.setActivatingTime(null);
				}
				elanInst.setCreateUser(elanInfoListForUpdate.get(0).getCreateUser());
				elanInst.setServiceId(elanInfoListForUpdate.get(0).getServiceId());
				elanInst.setAxcId(elanInfoListForUpdate.get(0).getAxcId());
				elanInst.setAction(2);
				elanInfoListForUpdate.add(elanInst);
			}
		}
	}
	
	private void integrateElan(ElanInfo elanInfoUpdate, ElanInfo elanInst) {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwinfo = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			if(elanInfoUpdate.getPwId() != elanInst.getPwId())
			{
				pwinfo = new PwInfo();
				pwinfo.setPwId(elanInfoUpdate.getPwId());
				elanInfoUpdate.setBeforePw(pwInfoService.queryByPwId(pwinfo));
				elanInfoUpdate.setPwId(elanInst.getPwId());
				elanInfoUpdate.setAction(1);
			}
			// 如果修改了根端口 取之前的根端口对象，并且更新etreeInfo_update中的aAcId、BeforeRootAc、action=1字段
			if(null != elanInfoUpdate.getAmostAcId()&& !isSame(elanInfoUpdate.getAmostAcId(),elanInst.getAmostAcId()))
			{
				setBerforeAAcList(elanInfoUpdate,elanInst.getAmostAcId(),elanInfoUpdate.getAmostAcId());
				elanInfoUpdate.setAmostAcId(elanInst.getAmostAcId());
				elanInfoUpdate.setAction(1);
			}
			
			elanInfoUpdate.setName(elanInst.getName());
			elanInfoUpdate.setActiveStatus(elanInst.getActiveStatus());
			elanInfoUpdate.setClientId(elanInst.getClientId());
			
			// 如果action还等于3 说明上面三个条件没有成立，此时给此属性赋0=没有改变pw
			if(elanInfoUpdate.getAction() == 3)
			{
				elanInfoUpdate.setAction(0);
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(pwInfoService);
		}
	}
	
	 private boolean isSame(String updateAcString,String newAcString)
	  {
		  boolean flag = false;
		  UiUtil uiutil = null;
		  Set<Integer> updateAcSet = null;
		  Set<Integer> newAcSet = null;
		  try 
		  {
	        uiutil = new UiUtil();
	        updateAcSet = uiutil.getAcIdSets(updateAcString);
	        newAcSet = uiutil.getAcIdSets(newAcString);
	        if(updateAcSet.size() == newAcSet.size())
	        {
	        	newAcSet.removeAll(updateAcSet);
	 	        if(newAcSet.size() == 0)
	 	        {
	 	        	flag = true;
	 	        }
	        }
		  } catch (Exception e) 
		  {
			ExceptionManage.dispose(e, getClass());
		 }finally
		 {
			  uiutil = null;
			  updateAcSet = null;
			  newAcSet = null;
		 }
		return flag;
	  }
	
	private ElanInfo findElan(ElanInfo elanInst) {
		for(ElanInfo elanInfo : elanInfoListForUpdate)
		{
			if(elanInfo.getPwId() == elanInst.getPwId())
			{
				return elanInfo;
			}
		}
		return null;
	}

	/**
	 * 给修改以前的AC赋值
	 * @param elanInfoAction
	 * @param mostAcId
	 * @param acIdList
	 */                           
	private void setBerforeAAcList(ElanInfo elanInfoAction, String mostAcId,String oldMostACId) 
	{
		String[] acIds = oldMostACId.split(",");
		String[] acIdsUdate = mostAcId.split(",");
		Set<Integer> acSet = null;
		List<Integer> acList = null;
		AcPortInfoService_MB acInfoService = null;
		try {
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acSet = new HashSet<Integer>();
			acList = new ArrayList<Integer>();
			for(String acId : acIds)
			{
				if(!isExist(acId.trim(),acIdsUdate))
				{
					acSet.add(Integer.parseInt(acId.trim()));
				}
			}
			acList = new ArrayList<Integer>(acSet);
			if(!acList.isEmpty())
			{
				elanInfoAction.setBeforeAAcList(acInfoService.select(acList));
			}else if((acIdsUdate.length > acIds.length)&&acList.isEmpty())
			{
			   elanInfoAction.setBeforeAAcList(new ArrayList<AcPortInfo>());
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(acInfoService);
			acSet = null;
			acList = null;
			acIds = null;
		}
	}

	
	/**
	 * 对比之前使用的找出之前替换掉的AC
	 * @param acId
	 * @param useAcPortList
	 * @return
	 */
	private boolean isExist(String acId,String[]  useAcPortList)
	{
		boolean fglag = false;
		try 
		{
			for(String acIds : useAcPortList)
			{
				if(acIds.trim().equals(acId))
				{
					fglag = true;
					break;
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		return fglag;
	}
	
	private void getAllAcId(List<Integer> acIdList,List<AcPortInfo> useAcPortList) throws Exception 
	{
		DefaultListModel defaultListModel = null;
		try 
		{
			defaultListModel = (DefaultListModel) this.listSelectAC.getModel();
			for (int i = 0; i < defaultListModel.getSize(); i++) 
			{
				acIdList.add(Integer.parseInt(((ControlKeyValue) defaultListModel.getElementAt(i)).getId()));
				useAcPortList.add((AcPortInfo)((ControlKeyValue) defaultListModel.getElementAt(i)).getObject());
			}
		} catch (Exception e) 
		{
			throw e;
		}finally
		{
			defaultListModel = null;
		}
	}

	/**
	 * 验证是否填写完整
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isFull() throws Exception {

		boolean flag = true;
		DefaultListModel defaultListModel = null;
		DefaultListModel defaultListAcModel = null;
		try {
			defaultListModel = (DefaultListModel) this.ListSelectPw.getModel();
			defaultListAcModel = (DefaultListModel) this.listSelectAC.getModel();
			if (defaultListModel.getSize() == 0) 
			{
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PW));
			flag = false;
			}else if(defaultListAcModel.size() == 0)
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_AC));
				flag = false;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			defaultListModel = null;
			defaultListAcModel = null;
		}
		return flag;
	}

	/**
	 * 验证选中的pw条数是否正确
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isSelectPw() throws Exception {

		DefaultListModel defaultListModel = null;
		boolean flag = true;
		try {
			defaultListModel = (DefaultListModel) this.ListSelectPw.getModel();
			if (defaultListModel.size() < 2) {
				flag = false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			defaultListModel = null;
		}
		return flag;
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 150, 50, 100, 50 };
		componentLayout.columnWeights = new double[] { 0.0, 0.0, 0.0,0.0,0.0 };
		componentLayout.rowHeights = new int[] { 25, 40, 40, 40, 40, 40, 40, 40, 40, 15 ,15};
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0.0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(1, 5, 5, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.add(this.lblMessage);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 20, 5);
		componentLayout.setConstraints(this.lblname, c);
		this.add(this.lblname);
		c.gridx = 1;
		c.insets = new Insets(5, 5, 20, 5);
		componentLayout.setConstraints(this.txtname, c);
		this.add(this.txtname);
		c.gridx = 2;
		c.insets = new Insets(5, 5, 20, 5);
		componentLayout.setConstraints(this.jButton, c);
		this.add(this.jButton);
		c.gridx = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 45, 15, 5);
		componentLayout.setConstraints(this.lblactivate, c);
		this.add(this.lblactivate);
		c.gridx = 4;
		// c.gridwidth = 2;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 15, 15, 5);
		componentLayout.setConstraints(this.chkactivate, c);
		this.add(this.chkactivate);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 4;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblAC, c);
		this.add(this.lblAC);
		c.gridx = 1;
		componentLayout.setConstraints(this.slpAC, c);
		this.add(this.slpAC);
		c.gridx = 3;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.slpSelectAC, c);
		this.add(this.slpSelectAC);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnAcRight, c);
		this.add(this.btnAcRight);
		c.gridy = 4;
		componentLayout.setConstraints(this.btnAcLeft, c);
		this.add(this.btnAcLeft);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 4;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblPw, c);
		this.add(this.lblPw);
		c.gridx = 1;
		componentLayout.setConstraints(this.slpPw, c);
		this.add(this.slpPw);
		c.gridx = 3;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.slpSelectPw, c);
		this.add(this.slpSelectPw);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnRight, c);
		this.add(this.btnRight);
		c.gridy = 9;
		componentLayout.setConstraints(this.btnLeft, c);
		this.add(this.btnLeft);

		// 批量创建
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblNumber, c);
		this.add(this.lblNumber);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.ptnSpinnerNumber, c);
		this.add(this.ptnSpinnerNumber);


		c.anchor = GridBagConstraints.EAST;
		c.gridx = 3;
		c.gridy = 11;
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		c.gridx = 4;
		componentLayout.setConstraints(this.btnCanel, c);
		this.add(this.btnCanel);

	}

	/**
	 * 初始化控件
	 * 
	 * @throws Exception
	 */
	private void initComponent() throws Exception {
		this.lblMessage = new JLabel();
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
		this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.lblname = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		this.txtname = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
		this.lblactivate = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.chkactivate = new JCheckBox();
		jButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
//		this.lblac = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_AC));
//		this.cmbac = new JComboBox();
//		this.lblport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_PORT));
//		this.cmbport = new JComboBox();
		
		/*********添加多AC端口 2015-3-9 张坤***********/
		lblAC = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_AC));
		listAC = new JList();
		slpAC = new JScrollPane();
		slpAC.setViewportView(listAC); 
		listSelectAC = new JList();
		slpSelectAC = new JScrollPane();
		slpSelectAC.setViewportView(listSelectAC);	
		btnAcLeft = new JButton("<<");
		btnAcRight = new JButton(">>");
		
		this.lblPw = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_PW));
		this.listPw = new JList();
		this.slpPw = new JScrollPane();
		this.slpPw.setViewportView(listPw);
		this.ListSelectPw = new JList();
		this.slpSelectPw = new JScrollPane();
		this.slpSelectPw.setViewportView(ListSelectPw);
		this.btnLeft = new JButton("<<");
		this.btnRight = new JButton(">>");
		this.lblNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		this.ptnSpinnerNumber = new PtnSpinner(1, 1, 64, 1);
	}
	
	/**
	 * 修改时添加自身AC
	 */
//	private void addAcForUpdate() {
//		if(null == this.eLanAc)
//			return;
//		if(this.eLanAc.getPortId()>0)
//			if(((ControlKeyValue)this.cmbport.getSelectedItem()).getId().equals(this.eLanAc.getPortId()+""))
//				((DefaultComboBoxModel)cmbac.getModel()).addElement(new ControlKeyValue(this.eLanAc.getId() + "", this.eLanAc.getName(), this.eLanAc));
//		
//		if(this.eLanAc.getLagId()>0)
//			if(((ControlKeyValue)this.cmbport.getSelectedItem()).getId().equals(this.eLanAc.getLagId()+""))
//				((DefaultComboBoxModel)cmbac.getModel()).addElement(new ControlKeyValue(this.eLanAc.getId() + "", this.eLanAc.getName(), this.eLanAc));
//	}
	
	private JLabel lblname;
	private JTextField txtname;
	private JButton jButton;
//	private JLabel lblport;
//	private JComboBox cmbport;
//	private JLabel lblac;
//	private JComboBox cmbac;
	/***********多AC**************/
	private JLabel lblAC;
	private JScrollPane slpAC;
	private JList listAC;
	private JScrollPane slpSelectAC;
	private JList listSelectAC;
	private JButton btnAcLeft;
	private JButton btnAcRight;
	
	private JLabel lblactivate;
	private JCheckBox chkactivate;
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel lblPw;
	private JScrollPane slpPw;
	private JList listPw;
	private JScrollPane slpSelectPw;
	private JList ListSelectPw;
	private JButton btnLeft;
	private JButton btnRight;
	private JLabel lblMessage;
	private JLabel lblNumber;
	private PtnSpinner ptnSpinnerNumber;
}
