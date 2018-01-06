/*
 * This source code is part of TWaver 4.1
 *
 * Serva Software PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Copyright 2002 - 2011 Serva Software. All rights reserved.
 */

package com.nms.ui.ptn.ne.camporeData;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.table.TTreeTable;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.CccInfo;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.MsPwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.service.impl.util.SynchroUtil;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.ces.CesBusinessController;
import com.nms.ui.ptn.business.elan.ElanBusinessController;
import com.nms.ui.ptn.business.eline.ElineBusinessController;
import com.nms.ui.ptn.business.etree.EtreeBusinessController;
import com.nms.ui.ptn.business.pw.PwBusinessController;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessController;

public class CamporeBusinessDataDialog extends PtnDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2730476939671147241L;
	
	private TDataBox box = new TDataBox();
	private TTreeTable table = new CamporeTable(box);
	private PtnButton btn;
	private PtnButton autoBtn;
	private JButton cancelButton;
	private JPanel buttonPanel;
	private JRadioButton emsDataJRadioButton;
	private JRadioButton neDataJRadioButton;
	private ButtonGroup buttonGroup;
	private String title;
	private Object EMSobject;
	private Object nEObject;
	private CamporeBusinessDataDialog camporeDataDialog;
	private JCheckBox jCheckBox;
	private CamporeBusinessInfoLoader loader;
	private JButton exportButton;
	private int type;
	private AbstractController controller;
	private Object object;
	public CamporeBusinessDataDialog(String title,Object EMSobject,Object nEObject, AbstractController controller){
		camporeDataDialog = this;
		this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_CAMPORE_DATA));
		this.title = title;
		this.EMSobject = EMSobject;
		this.nEObject = nEObject;
		initComponent();
		setLayout();
		addListener();
		this.controller = controller;
		if(ConstantUtil.autoCorrection == 0){
			// 人工校正
			UiUtil.showWindow(this, 700, 600);
		}else{
			// 自动校正
			if(ConstantUtil.autoCorrection == 1){
				// 以网管数据为准
				this.emsDataJRadioButton.setSelected(true);
			}else{
				// 以设备数据为准
				this.neDataJRadioButton.setSelected(true);
			}
			this.autoBtn.doClick();
		}
	}
	
	private void initComponent(){
		table.getTree().setLazyLoader(loader);
		table.getTree().setRootVisible(false);
		table.setElementClass(Node.class);
		if(ResourceUtil.language.equals("zh_CN")){
			table.registerElementClassXML(Node.class, "/com/nms/ui/ptn/ne/camporeData/CamporeElement.xml");
		}else{
			table.registerElementClassXML(Node.class, "/com/nms/ui/ptn/ne/camporeData/CamporeElement_en.xml");
		}
		table.setRowHeight(18);
		table.setShowGrid(false);
		table.setEditable(false);
		table.setTreeColumnDisplayName(title);
		TableColumn tableCellEditor = table.getColumnModel().getColumn(1);
		tableCellEditor.setCellEditor(new DefaultCellEditor(new JTextField()));
		btn = new PtnButton("人工校正",true);
		autoBtn = new PtnButton("自动校正",true);
		Dimension dimension = new Dimension();
		dimension.setSize(50, 50);
		btn.setSize(dimension);
		Dimension dimension1 = new Dimension();
		dimension1.setSize(50, 50);
		autoBtn.setSize(dimension1);
		cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		emsDataJRadioButton = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SAVE_EMS));
		neDataJRadioButton = new JRadioButton(ResourceUtil.srcStr(StringKeysLbl.LBL_SAVE_NE));
		neDataJRadioButton.setSelected(true);
		buttonGroup = new ButtonGroup();
		buttonGroup.add(emsDataJRadioButton);
		buttonGroup.add(neDataJRadioButton);
		buttonPanel = new JPanel();
		exportButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT));
		jCheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ONLE_DIFFERENT));
		loader = new CamporeBusinessInfoLoader(box,EMSobject, nEObject, this);
	}
	
	private void setLayout(){
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(jCheckBox);
		buttonPanel.add(emsDataJRadioButton);
		buttonPanel.add(neDataJRadioButton);
		buttonPanel.add(btn);
		buttonPanel.add(autoBtn);
		buttonPanel.add(cancelButton);
	}
	
	private void addListener(){
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				camporeDataDialog.close();
			}
		});
		jCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				box.clear();
				if(jCheckBox.isSelected()){
					loader.camporeData(true, EMSobject, nEObject);
				}else{
					loader.camporeData(false, EMSobject, nEObject);
				}
				
			}
		});
		
		btn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				btnActionPerformed();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		autoBtn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				butonActionPerformed();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		//导出数据
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportData();
			}
		});
	}

	/**
	 * 人工校正
	 */
	protected void btnActionPerformed() {
		Object obj = ((Node)this.loader.getSelect()).getBusinessObject();
		if(obj != null){
			if(obj instanceof Tunnel){
				((TunnelBusinessController)controller).setTunnel((Tunnel)obj);
			}else if(obj instanceof PwInfo){
				((PwBusinessController)controller).setPw((PwInfo)obj);;
			}else if(obj instanceof ElineInfo){
				((ElineBusinessController)controller).setEline((ElineInfo)obj);;
			}else if(obj instanceof EtreeInfo){
				((EtreeBusinessController)controller).setEtree((EtreeInfo)obj);;
			}else if(obj instanceof ElanInfo){
				((ElanBusinessController)controller).setElan((ElanInfo)obj);;
			}else if(obj instanceof CesInfo){
				((CesBusinessController)controller).setCes((CesInfo)obj);;
			}
			this.dispose();
		}
	}
	
	private void close(){
		if(controller instanceof TunnelBusinessController){
			((TunnelBusinessController)controller).setTunnel(null);
		}else if(controller instanceof PwBusinessController){
			((PwBusinessController)controller).setPw(null);
		}else if(controller instanceof ElineBusinessController){
			((ElineBusinessController)controller).setEline(null);
		}else if(controller instanceof EtreeBusinessController){
			((EtreeBusinessController)controller).setEtree(null);
		}else if(controller instanceof ElanBusinessController){
			((ElanBusinessController)controller).setElan(null);
		}else if(controller instanceof CesBusinessController){
			((CesBusinessController)controller).setCes(null);
		}
		this.dispose();
	}

	/**
	 * 保存数据
	 */
	@SuppressWarnings("unchecked")
	private void butonActionPerformed(){
		if(controller instanceof TunnelBusinessController){
			((TunnelBusinessController)controller).setTunnel(null);
		}else if(controller instanceof PwBusinessController){
			((PwBusinessController)controller).setPw(null);
		}else if(controller instanceof ElineBusinessController){
			((ElineBusinessController)controller).setEline(null);
		}else if(controller instanceof EtreeBusinessController){
			((EtreeBusinessController)controller).setEtree(null);
		}else if(controller instanceof ElanBusinessController){
			((ElanBusinessController)controller).setElan(null);
		}else if(controller instanceof CesBusinessController){
			((CesBusinessController)controller).setCes(null);
		}
		
		String result = ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS);
		if(emsDataJRadioButton.isSelected()){//网管数据为准
			if(EMSobject != null){
				Map<Integer, List> eMSMap = (Map<Integer, List>) EMSobject;
				for(Integer	siteId : eMSMap.keySet()){
					List list = eMSMap.get(siteId);
					if(list != null && list.size() > 0){
						//端口操作
						if(type == EServiceType.ETH.getValue()){
							List<PortInst> portInsts = (List<PortInst>) list;
//							for(PortInst portInst : portInsts){
								try {
									DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_PORT);
									result = dispatchUtil.excuteUpdate(portInsts.get(0));
									for (PortInst portInst : portInsts) {
										AddOperateLog.insertOperLog(null, EOperationLogType.EMSPORT.getValue(), result, 
												null, null, siteId, portInst.getPortName(), null);
									}
								} catch (Exception e) {
									ExceptionManage.dispose(e, this.getClass());
								}	
//							}
						}else if(type == EServiceType.CES.getValue()){
							List<CesInfo> cesList = (List<CesInfo>) list;
//							for (CesInfo cesInfo : cesList) {
								try {
									DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_CES);
									result = dispatchUtil.excuteUpdate(cesList.get(0));
									for (CesInfo cesInfo : cesList) {
										AddOperateLog.insertOperLog(null, EOperationLogType.EMSCES.getValue(), result, 
												null, null, siteId, cesInfo.getName(), null);
									}
								} catch (Exception e) {
									ExceptionManage.dispose(e, this.getClass());
								}
//							}
						}else if(type == EServiceType.ELINE.getValue()){
							List<ElineInfo> elineList = (List<ElineInfo>) list;
//							for (ElineInfo elineInfo : elineList) {
								try {
									DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_ELINE);
									result = dispatchUtil.excuteUpdate(elineList.get(0));
									for (ElineInfo elineInfo : elineList) {
										AddOperateLog.insertOperLog(null, EOperationLogType.EMSELINE.getValue(), result, 
												null, null, siteId, elineInfo.getName(), null);
									}
								} catch (Exception e) {
									ExceptionManage.dispose(e, this.getClass());
								}
//							}
						}else if(type == EServiceType.ETREE.getValue()){
							List<EtreeInfo> etreeList = (List<EtreeInfo>) list;
							try {
								DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_ETREE);
								result = dispatchUtil.excuteUpdate(etreeList);
								for (EtreeInfo etreeInfo : etreeList) {
									AddOperateLog.insertOperLog(null, EOperationLogType.EMSETREE.getValue(), result, 
											null, null, siteId, etreeInfo.getName(), null);
								}
							} catch (Exception e) {
								ExceptionManage.dispose(e, this.getClass());
							}
						}else if(type == EServiceType.ELAN.getValue()){
							List<ElanInfo> elanList = (List<ElanInfo>) list;
							try {
								DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_ELAN);
								result = dispatchUtil.excuteUpdate(elanList);
								for (ElanInfo elanInfo : elanList) {
									AddOperateLog.insertOperLog(null, EOperationLogType.EMSELAN.getValue(), result, 
											null, null, siteId, elanInfo.getName(), null);
								}
							} catch (Exception e) {
								ExceptionManage.dispose(e, this.getClass());
							}
						}else if(type == EServiceType.TUNNEL.getValue()){//tunnel操作
							List<Tunnel> tunnels = (List<Tunnel>) list;
							try {
								DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_TUNNEL);
								dispatchUtil.excuteUpdate(tunnels.get(0));
								for (Tunnel tunnel : tunnels) {
									AddOperateLog.insertOperLog(null, EOperationLogType.EMSTUNNEL.getValue(), result, 
											null, null, siteId, tunnel.getTunnelName(), null);
								}
							} catch (Exception e) {
								ExceptionManage.dispose(e, this.getClass());
							}
						}else if(type == EServiceType.CCC.getValue()){
							List<CccInfo> cccList = (List<CccInfo>) list;
								try {
									DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_CCC);
									result = dispatchUtil.excuteUpdate(cccList.get(0));
									for (CccInfo cccInfo : cccList) {
										AddOperateLog.insertOperLog(null, EOperationLogType.EMSCCC.getValue(), result, 
												null, null, siteId, cccInfo.getName(), null);
									}
								} catch (Exception e) {
									ExceptionManage.dispose(e, this.getClass());
								}
							}else if(type == EServiceType.PW.getValue()){
							try {
								List<Object> pwList = (List<Object>) list;
								DispatchUtil  pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
								if(pwList != null && pwList.size()>0){
									for(Object object : pwList){
										if(object instanceof PwInfo){
											pwDispatch.excuteUpdate((PwInfo)object);
											break;
										}
									}
								}
								for (Object object : pwList) {
									AddOperateLog.insertOperLog(null, EOperationLogType.EMSPW.getValue(), ResultString.CONFIG_SUCCESS, 
											null, null, siteId, ((PwInfo)object).getPwName(), null);
								}
							} catch (Exception e) {
								ExceptionManage.dispose(e, this.getClass());
							}
						}
					}else{
						result = ResourceUtil.srcStr(StringKeysTip.TIP_EMSISNULL);
					}
				}
			}
		}else if(neDataJRadioButton.isSelected()){//设备数据为准
			if(nEObject != null){
				Map<Integer, List> nEMap = (Map<Integer, List>) nEObject;
				for(Integer	siteId : nEMap.keySet()){
					List list = nEMap.get(siteId);
					if(list != null && list.size() > 0){
						//端口操作
						if(type == EServiceType.ETH.getValue()){
							List<PortInst> portInsts = (List<PortInst>) list;
							portNEAction(portInsts, siteId);
						}else if(type == EServiceType.CES.getValue()){
							List<CesInfo> cesList = (List<CesInfo>) list;
							cesNEAction(cesList, siteId);
						}else if(type == EServiceType.ELINE.getValue()){
							List<ElineInfo> elineList = (List<ElineInfo>) list;
							elineNEAction(elineList, siteId);
						}else if(type == EServiceType.ETREE.getValue()){
							List<EtreeInfo> etreeList = (List<EtreeInfo>) list;
							etreeNEAction(etreeList, siteId);
						}else if(type == EServiceType.ELAN.getValue()){
							List<ElanInfo> elanList = (List<ElanInfo>) list;
							elanNEAction(elanList, siteId);
						}else if(type == EServiceType.TUNNEL.getValue()){//tunnel操作
							SynchroUtil synchroUtil = new SynchroUtil();
							List<Tunnel> tunnels = (List<Tunnel>) list;
							for (Tunnel tunnel : tunnels) {
								try {
									synchroUtil.tunnelSynchro(tunnel,"", siteId);
								} catch (Exception e) {
									ExceptionManage.dispose(e,this.getClass());
								}
							}
						}else if(type == EServiceType.PW.getValue()){
							List<Object> pwList = (List<Object>) list;
							elinePWAction(pwList, siteId);
						}else if(type == EServiceType.CCC.getValue()){
							List<CccInfo> cccList = (List<CccInfo>) list;
							cccNEAction(cccList, siteId);
						}
					}else{
						result = ResourceUtil.srcStr(StringKeysTip.TIP_NEISNULL);
					}
				}
			}
		}
		DialogBoxUtil.succeedDialog(this, result);
		if(result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
			try {
				this.controller.refresh();
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}
			this.dispose();
		}
	}
	
	//以设备的数据为准 将服务器上的数据修改
	private void elinePWAction(List<Object> pwList, int siteId) {
		try {
			if(pwList != null && pwList.size() >0){
				SynchroUtil synchroUtil = new SynchroUtil();
				for (Object pwInfo : pwList) {
					try {
						if(pwInfo instanceof PwInfo){
							synchroUtil.pwInfoSynchro((PwInfo)pwInfo, siteId);
						}
//						else if(pwInfo instanceof MsPwInfo){
//							this.synchroMspw_db((MsPwInfo)pwInfo,ConstantUtil.siteId);
//						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}
	/**
	 * 多段PW的同步
	 * @param msPwObjects
	 * @param siteId
	 */
	private void synchroMspw_db(MsPwInfo msPwInfo, int siteId) {
		
		List<MsPwInfo> msPwInfos = new ArrayList<MsPwInfo>();
		TunnelService_MB tunnelService = null;
		MsPwInfoService_MB msPwInfoService = null;

		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			msPwInfoService = (MsPwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.MSPWSERVICE);
			Tunnel frontTunnel = tunnelService.selectBySiteIdAndServiceId(siteId, msPwInfo.getFrontTunnelId());
			msPwInfo.setFrontTunnelId(frontTunnel.getTunnelId());
			Tunnel backTunnel = tunnelService.selectBySiteIdAndServiceId(siteId, msPwInfo.getBackTunnelId());
			msPwInfo.setBackTunnelId(backTunnel.getTunnelId());
			msPwInfo.setSiteId(siteId);
			msPwInfo.setFrontInlabel(msPwInfo.getFrontInlabel());
			msPwInfo.setFrontOutlabel(msPwInfo.getFrontOutlabel());
			msPwInfo.setBackInlabel(msPwInfo.getBackInlabel());
			msPwInfo.setBackOutlabel(msPwInfo.getBackOutlabel());
			msPwInfo.setMipId(msPwInfo.getMipId());
			msPwInfos = msPwInfoService.select(msPwInfo);
			
			if (msPwInfos.size() == 0) {
				msPwInfoService.insert(msPwInfo);
			}else{
				msPwInfoService.update(msPwInfo);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelService);
			UiUtil.closeService_MB(msPwInfoService);
		}
	}
	/**
	 * 与同步类似操作
	 * @param protInfoList
	 */
	private void portNEAction(List<PortInst> protInfoList, int siteId){
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portService.initializtionSite(siteId);
			SynchroUtil synchroUtil = new SynchroUtil();
			for (PortInst portInst : protInfoList) {
				try {
					synchroUtil.updatePort(portInst);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
				AddOperateLog.insertOperLog(null, EOperationLogType.PORTEMS.getValue(),ResultString.CONFIG_SUCCESS,
						null, null, siteId, portInst.getPortName(), null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void cesNEAction(List<CesInfo> cesList, int siteId) {
		ElineInfoService_MB elineService = null;
		try {
			SynchroUtil synchroUtil = new SynchroUtil();
			//把所有ces业务修改为未激活
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			elineService.updateActiveStatusByType(siteId, EActiveStatus.UNACTIVITY.getValue(), EServiceType.CES.getValue());
			for (CesInfo cesInfo : cesList) {
				try {
					synchroUtil.CesSynchro(cesInfo, siteId);
					synchroUtil.pwNniBufferInfoSynchro(cesInfo.getPwNniList().get(0), siteId, false);
					AddOperateLog.insertOperLog(null, EOperationLogType.CESEMS.getValue(),ResultString.CONFIG_SUCCESS, 
							null, null, siteId, cesInfo.getName(), null);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(elineService);
		}
	}
	
	private void elineNEAction(List<ElineInfo> elineList, int siteId) {
		ElineInfoService_MB elineService = null;
		try {
			SynchroUtil synchroUtil = new SynchroUtil();
			//把所有eline业务修改为未激活
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			elineService.updateActiveStatusByType(siteId, EActiveStatus.UNACTIVITY.getValue(), EServiceType.ELINE.getValue());
			for (ElineInfo eline : elineList) {
				try {
					synchroUtil.acPortInfoSynchro(eline.getAcPortList().get(0), siteId);
					AcPortInfo ac = eline.getAcPortList().get(0);
					if(eline.getaSiteId() == siteId){
						eline.setaAcId(ac.getId());
					}else{
						eline.setzAcId(ac.getId());
					}
					synchroUtil.elineSynchro(eline, siteId);
					synchroUtil.acPortInfoSynchro(eline.getAcPortList().get(0), siteId);
					synchroUtil.pwNniBufferInfoSynchro(eline.getPwNniList().get(0), siteId, false);
					AddOperateLog.insertOperLog(null, EOperationLogType.ELINEEMS.getValue(),ResultString.CONFIG_SUCCESS, 
							null, null, siteId, eline.getName(), null);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(elineService);
		}
	}
	
	private void etreeNEAction(List<EtreeInfo> etreeList, int siteId) {
		try {
			DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			etreeDispatch.synchro(siteId);
			for (EtreeInfo etreeInfo : etreeList) {
				AddOperateLog.insertOperLog(null, EOperationLogType.ETREEEMS.getValue(),ResultString.CONFIG_SUCCESS, 
						null, null, siteId, etreeInfo.getName(), null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
//		try {
//			SynchroUtil synchroUtil = new SynchroUtil();
//			for (EtreeInfo etree : etreeList) {
//				try {
//					for (AcPortInfo acPortInfo : etree.getAcPortList()) {
//						synchroUtil.acPortInfoSynchro(acPortInfo, ConstantUtil.siteId);
//					}
//					AcPortInfo ac = etree.getAcPortList().get(0);
//					if(etree.getaSiteId() == ConstantUtil.siteId){
//						etree.setaAcId(ac.getId());
//					}else{
//						etree.setzAcId(ac.getId());
//					}
//					for (PwNniInfo pwNniInfo : etree.getPwNniList()) {
//						synchroUtil.pwNniBufferInfoSynchro(pwNniInfo, ConstantUtil.siteId);
//					}
//				} catch (Exception e) {
//					ExceptionManage.dispose(e, this.getClass());
//				}
//			}
//			synchroUtil.etreeSynchro(etreeList, ConstantUtil.siteId);
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		}
	}

	private void elanNEAction(List<ElanInfo> elanList, int siteId) {
		try {
			DispatchUtil elanDispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			elanDispatch.synchro(siteId);
			for (ElanInfo elanInfo : elanList) {
				AddOperateLog.insertOperLog(null, EOperationLogType.ELANEMS.getValue(),ResultString.CONFIG_SUCCESS, 
						null, null, siteId, elanInfo.getName(), null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
//		try {
//			SynchroUtil synchroUtil = new SynchroUtil();
//			for (ElanInfo elan : elanList) {
//				try {
//					for (AcPortInfo acPortInfo : elan.getAcPortList()) {
//						synchroUtil.acPortInfoSynchro(acPortInfo, ConstantUtil.siteId);
//					}
//					AcPortInfo ac = elan.getAcPortList().get(0);
//					if(elan.getaSiteId() == ConstantUtil.siteId){
//						elan.setaAcId(ac.getId());
//					}else{
//						elan.setzAcId(ac.getId());
//					}
//					for (PwNniInfo pwNniInfo : elan.getPwNniList()) {
//						synchroUtil.pwNniBufferInfoSynchro(pwNniInfo, ConstantUtil.siteId);
//					}
//				} catch (Exception e) {
//					ExceptionManage.dispose(e, this.getClass());
//				}
//			}
//			synchroUtil.elanSynchro(elanList, ConstantUtil.siteId);
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		}
	}

	private void cccNEAction(List<CccInfo> cccList, int siteId) {
		try {
			DispatchUtil cccDispatch = new DispatchUtil(RmiKeys.RMI_CCC);
			cccDispatch.synchro(siteId);
			for (CccInfo cccInfo : cccList) {
				AddOperateLog.insertOperLog(null, EOperationLogType.CCCEMS.getValue(),ResultString.CONFIG_SUCCESS, 
						null, null, siteId, cccInfo.getName(), null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
  //产生一致性报告:导出
	private void exportData() {
		List<Element> elemetensList = loader.getBox().getAllElements();
		ExportExcel exportExcel = null;
		List<String> headNames = null;
		List<String[]> listData = null;
		String[] data = null;
		String[] dataHead = null;
		Element element = null;
		try {
			headNames = new ArrayList<String>();
			listData = new ArrayList<String[]>();
			headNames.add(ResourceUtil.srcStr(StringKeysObj.ORDER_NUM));
			headNames.add(title);
			headNames.add(ResourceUtil.srcStr(StringKeysObj.EMSNAME));
			headNames.add(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
			exportExcel = new ExportExcel();
			
			if(elemetensList != null && elemetensList.size()>0){
				for(int i = elemetensList.size() -1 ; i>-1;i--){
					element = elemetensList.get(i);
					if(element != null && element.getParent() != null){
						dataHead = new String[1];
						dataHead[0] = element.getParent().getName();
						data = new String[3];
						data[0] = element.getName();
						if(((CamporeData)element.getBusinessObject()) != null ){
							data[1] = ((CamporeData)element.getBusinessObject()).getEMS();
							data[2] = ((CamporeData)element.getBusinessObject()).getNE();
						}
						if(!isExit(listData,dataHead)){
							listData.add(dataHead);
						}
						listData.add(data);
					}
				}
				exportExcel.exportExcel(listData,headNames);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			exportExcel = null;
			headNames = null;
			listData = null;
			data = null;
			dataHead = null;
			element = null;
		}
		
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 判断名称是否已经存在
	 * @param listString
	 * @param datas
	 * @return
	 */
	private boolean isExit(List<String[]> listString , String[] datas){
		boolean flga = false;
		try {
			if(listString != null && listString.size()>1){
				for(String[] data : listString){
					if(data[0].equals(datas[0])){
						return true;
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flga;
	}
	
}