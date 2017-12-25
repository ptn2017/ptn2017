﻿package com.nms.ui.ptn.oam.Node.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.oam.OamEthernetInfo;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamLinkInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.oam.OamEthreNetService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.SiteUtil;
import com.nms.service.impl.util.WhImplUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.oam.Node.view.OamNodePanel;
import com.nms.ui.ptn.oam.Node.view.dialog.ETHLinkOamNodeDialog;
import com.nms.ui.ptn.oam.Node.view.dialog.EthernetOamNodeDialog;
import com.nms.ui.ptn.oam.Node.view.dialog.OamInfoSingleDialog;
import com.nms.ui.ptn.oam.Node.view.dialog.WhETHLinkOamNodeDialog;

public class OamNodeController extends AbstractController {
	private OamNodePanel view;
	private List<OamInfo> oamInfoList = null;

	public OamNodeController(OamNodePanel view) {
		this.view = view;
		addListener();
	}

	@Override
	public void refresh() throws Exception {
		this.view.clear();
		this.view.initData(this.searchAndrefreshdata());
		this.view.updateUI();
	}

	@Override
	public void openCreateDialog() throws Exception {
		try {
			openCreateDialogAction();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		}
	}

	// 删除
	@Override
	public void delete() throws Exception {
		DispatchUtil dispath = null;
		String message = "";
		int operateKey = 0;
		try {
			oamInfoList = this.getView().getAllSelect();
			if(oamInfoList.get(0).getOamEthernetInfo() != null){
				dispath = new DispatchUtil(RmiKeys.RMI_ETHOAMCONFIG);
				operateKey =  EOperationLogType.ETHDELOAM.getValue();
			}else if(oamInfoList.get(0).getOamLinkInfo() != null){
				dispath = new DispatchUtil(RmiKeys.RMI_ETHLINKOAMCONFIG);
				operateKey =  EOperationLogType.ETHLINKDELETEOAM.getValue();
			}else{
				dispath = new DispatchUtil(RmiKeys.RMI_OAM);
				if(oamInfoList.get(0).getOamMep() != null && oamInfoList.get(0).getOamMep().getObjType().equals(EServiceType.SECTION.toString()))
				{
					operateKey =  EOperationLogType.SEGMENTDELOAM.getValue();
				}
				else if(oamInfoList.get(0).getOamMep() != null && oamInfoList.get(0).getOamMep().getObjType().equals(EServiceType.PW.toString()))
				{
					operateKey =  EOperationLogType.PWDELOAM.getValue();
				}
				else if(oamInfoList.get(0).getOamMep() != null && oamInfoList.get(0).getOamMep().getObjType().equals(EServiceType.TUNNEL.toString()))
				{
					operateKey =  EOperationLogType.TUNNELDELOAM.getValue();
				}
			}
			message=dispath.excuteDelete(oamInfoList);
			DialogBoxUtil.succeedDialog(this.getView(), message);
			refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
	}

	
	@Override
	public boolean deleteChecking() {
		boolean flag = false;
		SiteService_MB siteService = null;
		List<Integer> siteIds = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
				if(this.getView().getAllSelect().size() < 1){
					DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
					return false;
				}
				//判断是否为在线托管网�?				
				SiteUtil siteUtil = new SiteUtil();
				if(1==siteUtil.SiteTypeOnlineUtil(ConstantUtil.siteId)){
					WhImplUtil wu = new WhImplUtil();
					siteIds = new ArrayList<Integer>();
					siteIds.add(ConstantUtil.siteId);
					String str=wu.getNeNames(siteIds);
					DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_NOT_DELETEONLINE)+""+str+ResourceUtil.srcStr(StringKeysTip.TIP_ONLINENOT_DELETEONLINE));
					return false;  		    		
				}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			siteIds=null;
		}
		return flag;
	}
	
	// 修改
	@Override
	public void openUpdateDialog() throws Exception {
		if (this.getView().getAllSelect().size() == 0) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
		} else {
			OamInfo oamInfo = this.getView().getAllSelect().get(0);
			SiteService_MB siteService = null;
			try {
				siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == 0) {
					DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_TYPE));
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.SECTION.getValue()) {
					new OamInfoSingleDialog(oamInfo, EServiceType.SECTION.toString(), true);					
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.TUNNEL.getValue()) {
					new OamInfoSingleDialog(oamInfo, EServiceType.TUNNEL.toString(), true);
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.PW.getValue()) {
					new OamInfoSingleDialog(oamInfo, EServiceType.PW.toString(), true);
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.LINKOAM.getValue()) {
					if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
						WhETHLinkOamNodeDialog dialog = new WhETHLinkOamNodeDialog(oamInfo);
						dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()) - 200, UiUtil.getWindowHeight(dialog.getHeight()) / 2 - 8);
						dialog.setSize(new Dimension(680, 500));
						dialog.setVisible(true);
					}else{
						ETHLinkOamNodeDialog dialog = new ETHLinkOamNodeDialog(oamInfo);
						dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()) - 200, UiUtil.getWindowHeight(dialog.getHeight()) / 2 - 8);
						dialog.setSize(new Dimension(680, 490));
						dialog.setVisible(true);
					}
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ELINE.getValue()) {
					new OamInfoSingleDialog(oamInfo, EServiceType.ELINE.toString(), true);
					refresh();
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()) {
					EthernetOamNodeDialog dialog = new EthernetOamNodeDialog(oamInfo,this);
					dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth())-450, UiUtil.getWindowHeight(dialog.getHeight()) / 4);
					if(ResourceUtil.language.equals("zh_CN")){
					dialog.setSize(new Dimension(780, 630));
				    }else{
					dialog.setSize(new Dimension(900, 630));
				    }
					dialog.setVisible(true);
					refresh();
				}
				
			} catch (Exception e) {
				ExceptionManage.dispose(e,this.getClass());
			} finally {
				UiUtil.closeService_MB(siteService);
			}

		}
	}
	/**
	 * 刷新和搜�?	 * @return
	 * @throws Exception
	 */
	private List<OamInfo> searchAndrefreshdata() throws Exception {
		List<OamEthernetInfo> oamEthernetInfoList = null;
		OamInfoService_MB oamInfoService = null;
		OamEthreNetService_MB  oamEthreNetService=null;
		OamInfo oamInfo = null;
		OamMepInfo oamMepInfo = null;
		OamLinkInfo oamLinkInfo = null;
		OamEthernetInfo oamEthernetInfo=null;
		try {
			oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			oamEthreNetService= (OamEthreNetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OAMETHERNET);
			
			oamInfo = new OamInfo();
			oamMepInfo = new OamMepInfo();
			oamLinkInfo = new OamLinkInfo();
			oamEthernetInfo=new OamEthernetInfo();
			oamInfo.setOamMep(oamMepInfo);
			oamInfo.setOamLinkInfo(oamLinkInfo);
			oamEthernetInfoList=new ArrayList<OamEthernetInfo>();
			oamInfoList=new ArrayList<OamInfo>();
			
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.LINKOAM.getValue()) {
				oamInfo.getOamLinkInfo().setSiteId(ConstantUtil.siteId);
				oamLinkInfo.setObjType(EServiceType.LINKOAM.toString());
				oamInfoList = oamInfoService.queryLinkOAMBySiteId(oamInfo);
			} else {
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == 0) {
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.SECTION.getValue()) {
					oamMepInfo.setObjType(EServiceType.SECTION.toString());
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.TUNNEL.getValue()) {
					oamMepInfo.setObjType(EServiceType.TUNNEL.toString());
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.PW.getValue()) {
					oamMepInfo.setObjType(EServiceType.PW.toString());
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ELINE.getValue()) {
					oamMepInfo.setObjType(EServiceType.ELINE.toString());
				}
				if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()) {
					oamMepInfo.setObjType(EServiceType.ETHERNET.toString());
				}
				
				if(Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()){
					oamEthernetInfo.setSiteId(ConstantUtil.siteId);
					oamEthernetInfoList=oamEthreNetService.queryByNeIDSide(oamEthernetInfo);
					if(oamEthernetInfoList!=null&&oamEthernetInfoList.size()>0){
						for (int i = 0; i < oamEthernetInfoList.size(); i++) {
							OamInfo	oamInfos=new OamInfo();
							oamInfos.setOamEthernetInfo(oamEthernetInfoList.get(i));
							oamInfoList.add(oamInfos);
						}
					}
				}else{
					oamInfo.getOamMep().setSiteId(ConstantUtil.siteId);
					oamInfo.setOamMep(oamMepInfo);
					oamInfoList = oamInfoService.queryByServiceForNode(oamInfo);
				}
				
			}
			return oamInfoList;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(oamEthreNetService);
			oamInfo = null;
			oamMepInfo = null;
		}
	}

	/**
	 * 新建Action
	 * @throws Exception
	 */
	public void openCreateDialogAction() throws Exception {
		SiteService_MB siteService = null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == 0) {
				DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_TYPE));
				refresh();
			}
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.SECTION.getValue()) {
				new OamInfoSingleDialog(null, EServiceType.SECTION.toString(), true);
				refresh();
			}
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.TUNNEL.getValue()) {
				new OamInfoSingleDialog(null, EServiceType.TUNNEL.toString(), true);
				refresh();
			}
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.PW.getValue()) {
				new OamInfoSingleDialog(null, EServiceType.PW.toString(), true);
				refresh();
			}
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.LINKOAM.getValue()) {
				if(siteService.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()){
					
					if (oamInfoList!=null&&oamInfoList.size()>=6) {
						DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysLbl.LBL_NEOUT));
					}else{
						WhETHLinkOamNodeDialog dialog = new WhETHLinkOamNodeDialog(null);
						dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()) - 200, UiUtil.getWindowHeight(dialog.getHeight()) / 2 - 8);
						dialog.setSize(new Dimension(680, 500));
						dialog.setVisible(true);
					}
				}else{
					ETHLinkOamNodeDialog dialog = new ETHLinkOamNodeDialog(null);
					dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()) - 200, UiUtil.getWindowHeight(dialog.getHeight()) / 2 - 8);
					dialog.setSize(new Dimension(680, 490));
					dialog.setVisible(true);
				}
				
				refresh();
			}
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ELINE.getValue()) {
				new OamInfoSingleDialog(null, EServiceType.ELINE.toString(), true);
				refresh();
			}
			
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()) {
				if (oamInfoList!=null&&oamInfoList.size()>=6) {
					DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysLbl.LBL_NEOUT));
				} else {
					EthernetOamNodeDialog dialog = new EthernetOamNodeDialog(null,this);
					dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth())-450, UiUtil.getWindowHeight(dialog.getHeight()) / 4);
					
					if(ResourceUtil.language.equals("zh_CN")){
						dialog.setSize(new Dimension(780, 630));
					}else{
						dialog.setSize(new Dimension(900, 630));
					}
					dialog.setResizable(false);
					dialog.setVisible(true);
					refresh();
				}	
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
		}

	}

	/**
	 * 监听事件
	 */
	private void addListener() {
		this.view.getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (view.getSelect() == null) {
					return;
				} else {
					initDetailInfo();
				}
			}
		});

	};

	/**
	 *  oam链路 初始化数�?	 */
	@Override
	public void initDetailInfo() {
		OamInfo oamInfo = null;
		try {
			oamInfo = view.getSelect();
			initBasicInfo(oamInfo);
			view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			oamInfo = null;
		}
	}
	
	/**
	 * oam链路 初始化数�?	 * @param oamInfo
	 */
	private void initBasicInfo(OamInfo oamInfo) {
		if (oamInfo.getOamMep() != null) {
			this.view.getMelField().setText(oamInfo.getOamMep().getMel() + "");
			this.view.getLckCheckBox().setSelected(oamInfo.getOamMep().isLck());
			this.view.getLoopCheckBox().setSelected(oamInfo.getOamMep().isRingEnable());
			this.view.getTstCheckBox().setSelected(oamInfo.getOamMep().isTstEnable());
		}
	}

	public OamNodePanel getView() {
		return view;
	}

	public void setView(OamNodePanel view) {
		this.view = view;
	}
	
	/**
	 * 同步
	 */
	@Override
	public void synchro() {
		DispatchUtil oamDispatch = null;
		int operateKey = 0; //操作日志
		try {
			if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == 0) {
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_TYPE));
				return;
			}
			if(Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.LINKOAM.getValue()){
				oamDispatch = new DispatchUtil(RmiKeys.RMI_ETHLINKOAMCONFIG);
				operateKey =  EOperationLogType.ETHSYSOAM.getValue();
			}
			if(Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.SECTION.getValue()){
				oamDispatch = new DispatchUtil(RmiKeys.RMI_TMSOAMDISPATCH);
				operateKey = EOperationLogType.SEGMENTSYSOAM.getValue();
			}
			if(Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()){
				oamDispatch = new DispatchUtil(RmiKeys.RMI_ETHOAMCONFIG);
				operateKey = EOperationLogType.ETHLINKSYSOAM.getValue();
			}
		    String result = oamDispatch.synchro(ConstantUtil.siteId);
			this.refresh();
			DialogBoxUtil.succeedDialog(this.view, result);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			oamDispatch = null;
		}
	}
	
	@Override
	public void query() throws Exception {
		if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == 0) {
			DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_TYPE));
			refresh();
			return;
		}
		if (this.getView().getAllSelect().size() != 1) {
			DialogBoxUtil.errorDialog(this.getView(), ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
			return;
		}
		OamInfo oam = this.getView().getAllSelect().get(0);
		if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.SECTION.getValue()) {
			DialogBoxUtil.succeedDialog(this.getView(), this.queryLink(EServiceType.SECTION, oam));
			refresh();
		}
		if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.TUNNEL.getValue()) {
			DialogBoxUtil.succeedDialog(this.getView(), this.queryLink(EServiceType.TUNNEL, oam));
			refresh();
		}
		if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.PW.getValue()) {
			DialogBoxUtil.succeedDialog(this.getView(), this.queryLink(EServiceType.PW, oam));
			refresh();
		}
		if (Integer.parseInt(((ControlKeyValue) (view.getOamTypeComboBox().getSelectedItem())).getId()) == EServiceType.ETHERNET.getValue()) {
			DialogBoxUtil.succeedDialog(this.getView(), this.queryLink(EServiceType.ETHERNET, oam));
			refresh();
		}
	}
	
	/**
	 * 查询连通性检测结�?	 */
	/**
	 * @param type
	 * @param oam
	 * @return
	 */
	private String queryLink(EServiceType type, OamInfo oam){
		String result = "LINK_UP";
		CurAlarmService_MB alarmService = null;
		PortService_MB portService = null;
		TunnelService_MB tunnelService = null;
		PwInfoService_MB pwService = null;
		ElineInfoService_MB elineService = null;
		try {
			alarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			List<Integer> siteIdList = new ArrayList<Integer>();
			siteIdList.add(ConstantUtil.siteId);
			List<CurrentAlarmInfo> alarmList = alarmService.queryCurBySites(siteIdList);
			if(alarmList != null && !alarmList.isEmpty()){
				List<Integer> codeList = new ArrayList<Integer>();
				if(EServiceType.SECTION == type){
					codeList.add(118);codeList.add(10);codeList.add(119);codeList.add(117);codeList.add(120);codeList.add(116);
					portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
					for(CurrentAlarmInfo alarm : alarmList){
						if(alarm.getAlarmLevel() == 4 || alarm.getAlarmLevel() == 5){
							if(codeList.contains(alarm.getAlarmCode())){
								int portNum = alarm.getObjectId();
								PortInst port = new PortInst();
								port.setNumber(portNum);
								port.setSiteId(ConstantUtil.siteId);
								List<PortInst> portList = portService.select(port);
								if(portList != null && !portList.isEmpty()){
									if(portList.get(0).getPortId() == oam.getOamMep().getObjId()){
										result = "LINK_DOWN";
										break;
									}
								}
							}
						}
					}
				}else if(EServiceType.TUNNEL == type){
					tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
					codeList.add(108);codeList.add(7);codeList.add(110);codeList.add(106);codeList.add(112);codeList.add(104);
					for(CurrentAlarmInfo alarm : alarmList){
						if(alarm.getAlarmLevel() == 4 || alarm.getAlarmLevel() == 5){
							if(codeList.contains(alarm.getAlarmCode())){
								Tunnel tunnel = tunnelService.selectBySiteIdAndServiceId(ConstantUtil.siteId, alarm.getObjectId());
								if(oam.getOamMep().getServiceId() == tunnel.getTunnelId()){
									result = "LINK_DOWN";
									break;
								}
							}
						}
					}
				}else if(EServiceType.PW == type){
					pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
					PwInfo pwCon = new PwInfo();
					pwCon.setPwId(oam.getOamMep().getServiceId());
					PwInfo pw = pwService.selectBypwid_notjoin(pwCon);
					codeList.add(109);codeList.add(6);codeList.add(111);codeList.add(107);codeList.add(113);codeList.add(105);
					for(CurrentAlarmInfo alarm : alarmList){
						if(alarm.getAlarmLevel() == 4 || alarm.getAlarmLevel() == 5){
							if(codeList.contains(alarm.getAlarmCode())){
								if((pw.getASiteId() == ConstantUtil.siteId && pw.getApwServiceId() == alarm.getObjectId()) || 
										(pw.getZSiteId() == ConstantUtil.siteId && pw.getZpwServiceId() == alarm.getObjectId())){
									result = "LINK_DOWN";
									break;
								}
							}
						}
					}
				}else{
					codeList.add(131);codeList.add(129);codeList.add(8);codeList.add(100);codeList.add(103);codeList.add(199);
					codeList.add(21);codeList.add(132);codeList.add(101);codeList.add(102);
					elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
					for(CurrentAlarmInfo alarm : alarmList){
						if(alarm.getAlarmLevel() == 3 || alarm.getAlarmLevel() == 4 || alarm.getAlarmLevel() == 5){
							if(codeList.contains(alarm.getAlarmCode())){
								int serviceId = alarm.getObjectId();
								int count = elineService.queryEthBySiteAndServiceId(ConstantUtil.siteId, serviceId);
								if(count > 0){
									result = "LINK_DOWN";
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(alarmService);
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(elineService);
		}
		return result;
	}
}
