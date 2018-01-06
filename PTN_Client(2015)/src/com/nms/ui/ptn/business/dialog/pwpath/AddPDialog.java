﻿package com.nms.ui.ptn.business.dialog.pwpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import twaver.TWaverUtil;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.pw.MsPwInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.pw.PwNniInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EQosDirection;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.LabelInfoService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.pw.PwNniInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.ViewDataTable;
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
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dialog.tunnel.action.AutoRouteAction;
import com.nms.ui.ptn.business.pw.PwBusinessPanel;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 * 
 * @author __USER__
 */
public class AddPDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8078399713900616996L;
	private PwInfo pwInfo = null;
	private PwBusinessPanel pwPathPanel;
	private QosConfigController controller;
	private List<OamInfo> oamList = new ArrayList<OamInfo>();
	private List<QosInfo> qosList = new ArrayList<QosInfo>();
	private DefaultTableModel tableModel = null;
	private List<Integer> pwLabelList = new ArrayList<Integer>();
	private  List<Integer> pwLabelList_temp = new ArrayList<Integer>();
	private  List<Integer> pwLabelList_old = new ArrayList<Integer>();
	private int aSiteId = 0;
	private int zSiteId = 0;
	private Map<String, String> siteId_portIdMap = new HashMap<String, String>();
	private boolean hasCheck = false;
	private final String TUNNELTABLE = "tunnelofthepw"; // pw列表的名称
	private TunnelTopology tunnelTopology = null;
	private int batchNum = 0;
//	private boolean tunnelChanged = false;//判断tunnel下拉列表事件是否触发
	public Map<Integer, String> getMipMap() {
		return mipMap;
	}

	public void setMipMap(Map<Integer, String> mipMap) {
		this.mipMap = mipMap;
	}

	private List<Tunnel> tunnels = null;
	private List<Tunnel> useTunnels = new ArrayList<Tunnel>();//tunnel路径
	private List<SiteInst> siteInsts = new ArrayList<SiteInst>();//必经网元
	private JLabel vlan;//外层vlan配置
	private JButton vlanButton;
	private Map<Integer, String> mipMap = new HashMap<Integer, String>();
	private Map<String, Tunnel> route_TunnelMap = new LinkedHashMap<String, Tunnel>();
	
	public AddPDialog(){
		try {
			autoTable = new JTable();
			this.initLabelTable();
			this.tunnelTable = new ViewDataTable<Tunnel>(this.TUNNELTABLE);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	public AddPDialog(PwBusinessPanel jPanel1, boolean modal, PwInfo pwinfo) {
		try {
			
			this.setModal(modal);
			this.initComponents();
			this.addListener();
			this.setLayout();
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_PW));
			this.pwInfo = pwinfo;
			this.pwPathPanel = jPanel1;
			this.comBoxPwtypeData(pwType);
			this.tunnelTopology = new TunnelTopology(this);
			this.jSplitPane1.setRightComponent(tunnelTopology);
			if (this.pwInfo != null) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_PW));
				int sum = 0;

				if (pwinfo.getQosList().size() > 0) {
					for (int i = 0; i < pwinfo.getQosList().size(); i++) {
						sum += pwinfo.getQosList().get(i).getCir();
					}
				} 
				this.qostext.setText("totalCir=" + sum);

				if (pwinfo.getOamList().size() > 0) {
					this.oamtext.setText("megid=" + pwinfo.getOamList().get(0).getOamMep().getMegIcc() + pwinfo.getOamList().get(0).getOamMep().getMegUmc());
				} else {
					this.oamtext.setText("megid=0");
				}
				ptnSpinnerNumber.setEnabled(false);
				this.nametext.setText(this.pwInfo.getPwName());
				this.aPortComboBox.setEnabled(false);
				this.zPortComboBox.setEnabled(false);
				this.pwType.setEnabled(false);
				this.qosConfigButton.setEnabled(false);
				// this.oamConfigButton.setEnabled(false);
				this.comboBoxSelect(pwType, this.pwInfo.getType().getValue() + "");
				this.comBoxPortConfigSelect(aPortComboBox, zPortComboBox, pwInfo);
				this.isActive.setSelected(this.pwInfo.getPwStatus() == EActiveStatus.ACTIVITY.getValue() ? true : false);
				this.btnReset.setText(ResourceUtil.srcStr(StringKeysBtn.BTN_RESETLABEL));
				this.comboBoxSelect(payloadCombo, this.pwInfo.getPayload() + "");
				super.getComboBoxDataUtil().comboBoxSelectByValue(businessTypeComboBox, this.pwInfo.getBusinessType());
				super.getComboBoxDataUtil().comboBoxSelectByValue(modelJComboBox, this.pwInfo.getQosModel()+"");
				this.businessTypeComboBox.setEnabled(false);
				this.payloadCombo.setEnabled(false);
				this.initLabelValues();
				this.matching_Step2(pwLabelList);
//				tunnelTopology.removePopMenu();
				this.tunnelTopology.getTunnelTopo().getNetWork().setPopupMenuGenerator(null);
				this.tunnelTopology.setPath(useTunnels);
				this.aSiteId = pwInfo.getASiteId();
				this.zSiteId = pwInfo.getZSiteId();
				this.autoBtn.setEnabled(false);
				this.initAutoValue();
				this.MSRouteJcbBox.setEnabled(false);
				this.tunnelJcbBox.setEnabled(false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	public AddPDialog(Tunnel tunnel, int num)
	{
		batchNum = num;
	}
	public void createDefaultPW()
	{
		
	}

	/**
	 * 修改pw时，赋值标签表格
	 */
	private void initLabelValues(){
		TunnelService_MB tunnelService = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			List<Integer> integers = new ArrayList<Integer>();
			useTunnels.clear();
			Map<Integer,Tunnel> map = new LinkedHashMap<Integer, Tunnel>();
			//1代表多段pw,0代表普通pw
			int i = 0;
			if("1".equals(pwInfo.getBusinessType())){
				for(MsPwInfo msPwInfo : pwInfo.getMsPwInfos()){
					if(i== 0){
						pwLabelList.add(msPwInfo.getFrontInlabel());
						pwLabelList_old.add(msPwInfo.getFrontInlabel());
						pwLabelList.add(msPwInfo.getBackOutlabel());
						pwLabelList_old.add(msPwInfo.getBackOutlabel());
					}
					pwLabelList.add(msPwInfo.getBackInlabel());
					pwLabelList_old.add(msPwInfo.getBackInlabel());
					pwLabelList.add(msPwInfo.getFrontOutlabel());
					pwLabelList_old.add(msPwInfo.getFrontOutlabel());
					Tunnel tunnel = new Tunnel();
					tunnel.setTunnelId(msPwInfo.getFrontTunnelId());
					tunnel = tunnelService.select(tunnel).get(0);
					map.put(tunnel.getTunnelId(), tunnel);
					integers.add(msPwInfo.getFrontTunnelId());
					Tunnel tunnel2 = new Tunnel();
					tunnel2.setTunnelId(msPwInfo.getBackTunnelId());
					tunnel2 = tunnelService.select(tunnel2).get(0);
					map.put(tunnel2.getTunnelId(), tunnel2);
					integers.add(msPwInfo.getBackTunnelId());
					i++;	
				}
				for(Integer integer : map.keySet()){
					useTunnels.add(map.get(integer));
				}
			}else{
				Tunnel tunnel = new Tunnel();
				tunnel.setTunnelId(this.pwInfo.getTunnelId());
				tunnel = tunnelService.select(tunnel).get(0);
				useTunnels.add(tunnel);
				pwLabelList.add(this.pwInfo.getOutlabelValue());
				pwLabelList.add(this.pwInfo.getInlabelValue());
				pwLabelList_old.add(this.pwInfo.getOutlabelValue());
				pwLabelList_old.add(this.pwInfo.getInlabelValue());
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelService);
		}
	}
	
	/**
	 * 修改pw时，赋值路由表格
	 */
	@SuppressWarnings("static-access")
	private void initAutoValue(){
		DefaultTableModel model = (DefaultTableModel) autoTable.getModel();
		Object[] object;
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			String AsiteName = siteService.select(pwInfo.getASiteId()).getCellId();
			String ZsiteName = siteService.select(pwInfo.getZSiteId()).getCellId();
			
			if("0".equals(pwInfo.getBusinessType())){
				object = new Object[]{ 1, AsiteName + "->" + ZsiteName};
				
			}else{
				String str = "";
				for(MsPwInfo msPwInfo:pwInfo.getMsPwInfos()){
					str += siteService.select(msPwInfo.getSiteId()).getCellId()+"->";
				}
				object = new Object[]{ 1, AsiteName + "->" +str+ZsiteName};
			}
			model.addRow(object);
			autoTable.setModel(model);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 添加事件监听
	 */
	private void addListener() {
		qosConfigButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qosConfigButtonActionPerformed(evt);
				int sum = 0;

				if (qosList.size() > 0) {
					for (int i = 0; i < qosList.size(); i++) {
						sum += qosList.get(i).getCir();
					}
				}
				
				qostext.setText("totalCir=" + sum);
			}
		});

		oamConfigButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					oamConfigButtonActionPerformed(evt);

					if (oamList.size() != 0) {
						oamtext.setText("megid=" + oamList.get(0).getOamMep().getMegIcc() + oamList.get(0).getOamMep().getMegUmc());
					} else {
						oamtext.setText("megid=0");
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});

		btnManager.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					mappingConfig();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}

			}
		});
		vlanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					vlanConfig();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});
		Confirm.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 保存之前先做标签验证
				BuildMultiPwInfor();
			}

			@Override
			public boolean checking() {
				return verify();
			}
		});

		this.btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				pwLabelList.clear();
				tableModel.getDataVector().clear();
				tableModel.fireTableDataChanged();
				if(useTunnels != null && useTunnels.size()>0){
					for (int i = 0; i < useTunnels.size(); i++) {
						List<Integer> labelValues = matchingLabel(useTunnels.get(i));
						int aValue = labelValues.get(i*2);
						int zValue = labelValues.get(i*2+1);
						Object[] obj = new Object[] {i+1, aValue, zValue};
						tableModel.addRow(obj);
					}
				}else{
					DialogBoxUtil.errorDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_WORKROUTER));
					return ;
				}
				labelTable.setModel(tableModel);
			}
		});

		this.btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (pwLabelList.size() != 0) {
						if (labelTable.getSelectedColumn() > 0 && labelTable.isEditing()) {
							verifyLabelIsNumber();
						} else {
							boolean flag = saveLabel();
							if (flag) {
								DialogBoxUtil.succeedDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
							}
						}
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});


		this.labelTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				if (labelTable.getSelectedColumn() > 0) {
					int selectR = labelTable.getSelectedRow();
					int selectC = labelTable.getSelectedColumn();
					try {
						int oldValue = Integer.parseInt(labelTable.getValueAt(selectR, selectC).toString());
						if (labelTable.isEditing()) {
							labelTable.getCellEditor().stopCellEditing();
						}
						boolean flag = commitTable(labelTable);
						if (!flag) {
							DialogBoxUtil.succeedDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
							labelTable.setValueAt(oldValue, selectR, selectC);
							labelTable.updateUI();
						}
						pwLabelList.clear();
						for (int i = 0; i < labelTable.getRowCount(); i++) {
							pwLabelList.add(Integer.parseInt(labelTable.getValueAt(i, 1).toString()));
							pwLabelList.add(Integer.parseInt(labelTable.getValueAt(i, 2).toString()));
						}
						
					} catch (Exception e) {
						try {
							if (labelTable.isEditing()) {
								labelTable.getCellEditor().stopCellEditing();
							}
							DialogBoxUtil.succeedDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
							matching_Step2(pwLabelList);
						} catch (Exception e1) {
							ExceptionManage.dispose(e1, this.getClass());
						}
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});
		
		this.autoNamingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoNamingActionPerformed();
			}
		});
		
//		this.pwType.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				typeActionPerformed();
//			}
//		});
		
		this.pwType.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					typeActionPerformed();
				}
			}
		});

		autoBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean falg = false;
				((DefaultTableModel) autoTable.getModel()).getDataVector().clear();
				((DefaultTableModel) autoTable.getModel()).fireTableDataChanged();
				if(tunnelTopology.getSiteA() == null){
					DialogBoxUtil.errorDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_SELECT_A));
				}else if(tunnelTopology.getSiteZ() == null){
					DialogBoxUtil.errorDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_SELECT_Z));
				}else if(tunnelTopology.getSiteA() != null && tunnelTopology.getSiteZ() != null){
					//多段PW
					if ("1".equals(((Code) ((ControlKeyValue) businessTypeComboBox.getSelectedItem()).getObject()).getCodeValue())) {
						falg = autoAction();
					}else{
						//普通PW
						falg = autoActionPerformed();
					}
					if (!falg) {
						DialogBoxUtil.errorDialog(AddPDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
					}
				}
			}
		});
		
		this.MSRouteJcbBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					mSRouteActionPerformed();
				}
			}
		});
		
		this.tunnelJcbBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					tunnelActionPerformed();
				}
			}
		});
		
//		this.tunnelJcbBox.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				tunnelChanged = true;
//			}
//		});
		
		this.businessTypeComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				busiTypeActionPerformed();
			}
		});
	}

	/**
	 * 当业务类型相互切换时，要重置路由和标签以及QoS和OAM
	 */
	private void busiTypeActionPerformed() {
		tunnelTopology = new TunnelTopology(this);
		jSplitPane1.setRightComponent(tunnelTopology);
		// 清除tunnel面板
		this.tunnelTable.clear();
		// 清除标签面板
		this.clearLabelTable();
		//清除路由和标签信息
		((DefaultTableModel)this.autoTable.getModel()).getDataVector().clear();
		this.useTunnels.clear();
		this.siteInsts.clear();
		this.pwLabelList_temp.clear();
		this.pwLabelList.clear();
		this.route_TunnelMap.clear();
		this.MSRouteJcbBox.removeAllItems();
		this.tunnelJcbBox.removeAllItems();
		this.qosList = null;
		this.qostext.setText(null);
		this.oamList = null;
		this.oamtext.setText(null);
		this.lblTunnelLabel.setText("");
	}

	/**
	 * 普通pw的自动路由
	 * @return
	 */
	private boolean autoActionPerformed() {
		DefaultTableModel model = (DefaultTableModel) autoTable.getModel();
		this.useTunnels.clear();
		this.route_TunnelMap.clear();
		this.MSRouteJcbBox.removeAllItems();
		this.tunnelJcbBox.removeAllItems();
		this.tunnelTopology.setLinkColor(Color.GREEN);
		model.getDataVector().clear();
		model.fireTableDataChanged();
		if(tunnelTopology.tunnelMust.size()>1){
			return false;
		}
		//获取拓扑所有tunnel
		if (tunnels == null) {
			TunnelService_MB tunnelService = null;
			try {
				tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				tunnels = new ArrayList<Tunnel>();
				tunnels = tunnelService.selectAll();
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(tunnelService);
			}
		}
		
		for (Tunnel tunnel : tunnels) {
			if ((tunnel.getASiteId() == tunnelTopology.getSiteA().getSite_Inst_Id() && tunnel.getZSiteId() == tunnelTopology.getSiteZ().getSite_Inst_Id())
					|| (tunnel.getZSiteId() == tunnelTopology.getSiteA().getSite_Inst_Id() && tunnel.getASiteId() == tunnelTopology.getSiteZ().getSite_Inst_Id())) {
				if(tunnelTopology.tunnelMust.size()==1){
					if(tunnel.getTunnelId() == tunnelTopology.tunnelMust.get(0).getTunnelId()){
						useTunnels.add(tunnel);
						break;
					}
				}else{
					tunnelTopology.tunnelMust.add(tunnel);
					useTunnels.add(tunnel);
					break;
				}
			}
		}
		if (useTunnels.size() > 0) {
			String routeName = tunnelTopology.getSiteA().getCellId() + "->" + tunnelTopology.getSiteZ().getCellId();
			Object[] object = { 1,  routeName};
			model.addRow(object);
			autoTable.setModel(model);
			tunnelTopology.setPath(useTunnels);
			//路由选择完毕，需要初始化分段路由和选择Tunnel两个下拉列表
			Tunnel tunnel = useTunnels.get(0);
			this.route_TunnelMap.put(routeName, tunnel);
			this.initMSRouteJCbBox();
			return true;
		}
		
		return false;
	}
	
	/**
	 * 当在右边拓扑选择路径时，更新左边的下拉列表
	 */
	public void updateMSRoute(String route, Tunnel tunnelBefore, Tunnel selectedTunnel){
		this.MSRouteJcbBox.setSelectedItem(route);
		this.initTunnelJcbBox(this.route_TunnelMap.get(route));
		this.updatePathMust(tunnelBefore, selectedTunnel);
		this.clearLabelTable();
	}
	
	/**
	 * 更改路径后，需要把左边界面和右边拓扑的必经路径更新
	 */
	private void updatePathMust(Tunnel tunnelBefore, Tunnel selectedTunnel) {
		int index = 0;
		for (int i = 0; i < this.useTunnels.size(); i++) {
			if(this.useTunnels.get(i).getTunnelId() == tunnelBefore.getTunnelId()){
				index = i;
				break;
			}
		}
		this.useTunnels.remove(index);
		this.useTunnels.add(index, selectedTunnel);
		int result = this.tunnelTopology.chcekTunnel(tunnelBefore);
		if(result >= 0){
			this.tunnelTopology.tunnelMust.remove(result);
			this.tunnelTopology.tunnelMust.add(result, selectedTunnel);
		}
	}

	/**
	 * 清空标签列表
	 */
	private void clearLabelTable(){
		this.tableModel = (DefaultTableModel) labelTable.getModel();
		this.tableModel.getDataVector().clear();
		this.tableModel.fireTableDataChanged();
	}

	/**
	 * 初始化分段路由下拉列表
	 */
	private void initMSRouteJCbBox() {
		int i = 1;
		this.MSRouteJcbBox.removeAllItems();
		for (String route : this.route_TunnelMap.keySet()) {
			this.MSRouteJcbBox.addItem(route);
			if(i == 1){
				this.MSRouteJcbBox.setSelectedItem(route);
				this.initTunnelJcbBox(this.route_TunnelMap.get(route));
			}
			i++;
		}
	}
	
	/**
	 * 初始化选择Tunnel下拉列表
	 * id 绑定分段路由和对应的Tunnel列表
	 * tunnel 表示当前所选中的tunnel
	 */
	private void initTunnelJcbBox(Tunnel tunnel) {
		TunnelService_MB tunnelServiceMB = null;
		try {
			this.tunnelJcbBox.removeAllItems();
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			List<Tunnel> tunnelList = tunnelServiceMB.selectByASiteIdAndZSiteId(tunnel.getaSiteId(), tunnel.getzSiteId());
			if(tunnelList != null && !tunnelList.isEmpty()){
				for (Tunnel t : tunnelList) {
					ControlKeyValue keyValue = new ControlKeyValue(t.getTunnelId()+"", t.getTunnelName(), t);
					this.tunnelJcbBox.addItem(keyValue);
					if(t.getTunnelId() == tunnel.getTunnelId()){
						this.tunnelJcbBox.setSelectedItem(keyValue);
						this.lblTunnelLabel.setText(this.getTunnelLabel(tunnel));
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}
	
	/**
	 * 显示tunnel的标签信息
	 */
	private String getTunnelLabel(Tunnel tunnel){
		int aSiteId = tunnel.getaSiteId();
		int zSiteId = tunnel.getzSiteId();
		List<Lsp> lspList = tunnel.getLspParticularList();
		String aLabel = tunnel.getShowSiteAname()+"-"+ResourceUtil.srcStr(StringKeysObj.INLABEL_OUTLABEL)+"_";
		String zLabel = tunnel.getShowSiteZname()+"-"+ResourceUtil.srcStr(StringKeysObj.INLABEL_OUTLABEL)+"_";
		for (Lsp lsp : lspList) {
			String in_outLabel = lsp.getBackLabelValue()+"\\"+lsp.getFrontLabelValue();
			String out_inLabel = lsp.getFrontLabelValue()+"\\"+lsp.getBackLabelValue();
			if(lsp.getASiteId() == aSiteId){
				aLabel += in_outLabel;
			}else if(lsp.getZSiteId() == aSiteId){
				aLabel += out_inLabel;
			}
			if(lsp.getASiteId() == zSiteId){
				zLabel += in_outLabel;
			}else if(lsp.getZSiteId() == zSiteId){
				zLabel += out_inLabel;
			}
		}
		return aLabel+" "+zLabel;
	}
	
	/**
	 * 分段路由改变之后，tunnel下拉列表也要改变
	 */
	private void mSRouteActionPerformed() {
		if(!this.route_TunnelMap.isEmpty()){
			String route = (String) this.MSRouteJcbBox.getSelectedItem();
			this.initTunnelJcbBox(this.route_TunnelMap.get(route));
		}
	}
	
	/**
	 * tunnel改变之后，右边的拓扑也要改变，标签也要清空
	 */
	private void tunnelActionPerformed() {
		if(!this.route_TunnelMap.isEmpty()){
			String route = (String) this.MSRouteJcbBox.getSelectedItem();
			Tunnel tunnelBefore = this.route_TunnelMap.get(route);
			ControlKeyValue keyValue = (ControlKeyValue) this.tunnelJcbBox.getSelectedItem();
			Tunnel selectedTunnel = (Tunnel) keyValue.getObject();
			this.route_TunnelMap.put(route, selectedTunnel);
			this.lblTunnelLabel.setText(this.getTunnelLabel(selectedTunnel));
			this.updatePathMust(tunnelBefore, selectedTunnel);
			this.tunnelTopology.setLinkColor(tunnelBefore, Color.GREEN);
			this.tunnelTopology.setLinkColor(selectedTunnel, Color.BLUE);
			this.clearLabelTable();
			this.pwLabelList_temp.clear();
			this.pwLabelList.clear();
//			tunnelChanged = false;
		}
	}

	/**
	 * 多段pw的自动路由
	 * @return
	 */
	private boolean autoAction(){
		DefaultTableModel model = (DefaultTableModel) autoTable.getModel();
		AutoRouteAction autoRouteAction = new AutoRouteAction();
		TunnelService_MB tunnelServiceMB = null;
		SiteService_MB siteService = null;
		StringBuffer siteName = null;
		OamInfoService_MB oamInfoService = null;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			useTunnels.clear();
			siteInsts.clear();
			this.route_TunnelMap.clear();
			this.MSRouteJcbBox.removeAllItems();
			this.tunnelJcbBox.removeAllItems();
			this.tunnelTopology.setLinkColor(Color.GREEN);
			model.getDataVector().clear();
			model.fireTableDataChanged();
			//获取拓扑所有tunnel
			if (tunnels == null) {
			    oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				tunnels = new ArrayList<Tunnel>();
				tunnels = tunnelServiceMB.selectAll();
			}
			
			//增加所有网元节点信息
			for(Tunnel tunnel : tunnels){
				autoRouteAction.addRoute(tunnel.getASiteId(), tunnel.getZSiteId(), 1);
			}
			//所有必经网元信息
			List<Integer> mustSites = new ArrayList<Integer>();
			for(SiteInst siteInst: tunnelTopology.siteMust){
				mustSites.add(siteInst.getSite_Inst_Id());
			}
			//所有必经tunnel的网元信息
			List<Integer> tunnelSites = new ArrayList<Integer>();
			for(Tunnel tunnel : tunnelTopology.tunnelMust){
				if(!tunnelSites.contains(tunnel.getASiteId())){
					tunnelSites.add(tunnel.getASiteId());
				}
				if(!tunnelSites.contains(tunnel.getZSiteId())){
					tunnelSites.add(tunnel.getZSiteId());
				}
			}
			
			//最短路径包含的网元信息
			List<String> siteList = autoRouteAction.show(tunnelTopology.getSiteA().getSite_Inst_Id(), mustSites, tunnelSites, null, tunnelTopology.getSiteZ().getSite_Inst_Id());
			if(siteList != null && siteList.size()>0){//获取包含必经网元，必经tunnel的最短路径
				siteName = new StringBuffer();
				String[] strs = siteList.get(0).split("-");
				if(strs.length>2){//最短路径必须包含三个以上网元
					for (int i = 0; i < strs.length-1; i++) {
						boolean contain = false;
						List<Tunnel> tunnelList = tunnelServiceMB.selectByASiteIdAndZSiteId(Integer.parseInt(strs[i]), Integer.parseInt(strs[i+1]));
						labelA:
						for(Tunnel tunnel : tunnelList){//判断最短路径中两网元之间的tunnel包含必经tunnel
							for(Tunnel tunnelMust:tunnelTopology.tunnelMust){
								if(tunnel.getTunnelId() == tunnelMust.getTunnelId()){
									useTunnels.add(tunnel);
									contain = true;
									break labelA;
								}
							}
						}
						if(!contain){//不包含,添加集合第一条数据
							tunnelTopology.tunnelMust.add(tunnelList.get(0));
							useTunnels.add(tunnelList.get(0));
						}
					}
					for (int i = 1; i < strs.length-1; i++) {
						SiteInst siteInst = siteService.select(Integer.parseInt(strs[i]));
						siteInsts.add(siteInst);
						siteName.append(siteInst.getCellId()+"->");
					}
					String routeName = tunnelTopology.getSiteA().getCellId() + "->" +siteName.toString()+ tunnelTopology.getSiteZ().getCellId();
					Object[] object = {1, routeName};
					model.addRow(object);
					autoTable.setModel(model);
					tunnelTopology.setPath(useTunnels);
					String[] routeArr = routeName.split("->");
					for (int i = 0; i < routeArr.length-1; i++) {
						this.route_TunnelMap.put(routeArr[i]+"->"+routeArr[i+1], useTunnels.get(i));
					}
					this.initMSRouteJCbBox();
					return true;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(tunnelServiceMB);
		}
		
		return false;
	}
	
	private void comBoxPwtypeData(JComboBox pwType2) {
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) pwType2.getModel();
		for (EPwType type : EPwType.values()) {
			if (type != EPwType.NONE)
				defaultComboBoxModel.addElement(new ControlKeyValue(type.getValue() + "", type.toString(), type));
		}
		pwType2.setModel(defaultComboBoxModel);
	}

	private void comBoxPortConfigSelect(JComboBox aBox, JComboBox zBox, PwInfo pw) throws Exception {
		for (int i = 0; i < aBox.getItemCount(); i++) {
			if (((ControlKeyValue) aBox.getItemAt(i)).getId().equals(pw.getaPortConfigId() + "")) {
				aBox.setSelectedIndex(i);
				break;
			}
		}

		for (int i = 0; i < zBox.getItemCount(); i++) {
			if (((ControlKeyValue) zBox.getItemAt(i)).getId().equals(pw.getzPortConfigId() + "")) {
				zBox.setSelectedIndex(i);
				return;
			}
		}

	}

	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	// GEN-BEGIN:initComponents
	
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() throws Exception {
		Dimension dimension = new Dimension(1200, 700);
		this.setSize(dimension);
		this.setMinimumSize(dimension);

		this.lblMessage = new JLabel();
		jSplitPane1 = new JSplitPane();
		jPanel2 = new JPanel();
		jLabel1 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		pwTypeName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		jLabel3 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		qosConfigButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		oamConfigButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		isActive = new JCheckBox();
		isActive.setSelected(true);
		aPortComboBox = new JComboBox();
		aPortComboBox.setPreferredSize(new Dimension(155, 20));
		zPortComboBox = new JComboBox();
		zPortComboBox.setPreferredSize(new Dimension(155, 20));
		pwType = new JComboBox();
		pwType.setPreferredSize(new Dimension(155, 20));
		Confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		qoslable = new JLabel("QoS");
		oamlable = new JLabel("OAM");
		qostext = new PtnTextField();
		qostext.setEnabled(false);
		oamtext = new PtnTextField();
		oamtext.setEnabled(false);
		lblMapping = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MAPPING_MANAGE));
		btnManager = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));

		lblLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LABELINFO));
		btnReset = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_MATCHING));
		btnSave = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVELABEL));
		autoNamingButton = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));

		nametext = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.Confirm, this);
		this.payloadTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PAYLOAD));
		this.payloadCombo = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(this.payloadCombo, "PAYLOAD");
		super.getComboBoxDataUtil().comboBoxSelect(this.payloadCombo, UiUtil.getCodeByValue("PAYLOAD", "2").getId() + "");

		this.tunnelTable = new ViewDataTable<Tunnel>(this.TUNNELTABLE);
		this.tunnelTable.getTableHeader().setResizingAllowed(true);
		this.tunnelTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		lblAutoroute = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_AUTOrOUTER));
		autoTable = new JTable();
		this.autoPane = new JScrollPane();
		this.autoPane.setViewportView(this.autoTable);
		autoBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_WORK_ROUTER));
		this.initLabelTable();
		businessType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SERVICENAME_TYPE));
		businessTypeComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(this.businessTypeComboBox, "BUSINESSTYPE");
		tableModel = (DefaultTableModel) labelTable.getModel();
		vlan = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
		vlanButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		modelJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MODAL));
		modelJComboBox = new JComboBox();
		super.getComboBoxDataUtil().comboBoxData(this.modelJComboBox, "MODEL");
		
		// 创建个数控件 dxh
		this.lblNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		this.ptnSpinnerNumber = new PtnSpinner(1, 1, 1000, 1);
		this.lblMSRoute = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SINGLE_ROUTE));
		this.MSRouteJcbBox = new JComboBox();
		this.lblTunnel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_TUNNEL));
		this.tunnelJcbBox = new JComboBox();
		this.lblTunnelLabel = new JLabel();
	}

	/**
	 * ETH-->PDH 普通类型/多段类型 = 不重置/重置(并且多段选项灰掉)
	 * PDH-->ETH 普通类型 = 不重置
	 */
	private void typeActionPerformed() {
		try {
			this.qosList = null;
			this.businessTypeComboBox.setEnabled(true);
			ControlKeyValue codePwType = (ControlKeyValue) this.pwType.getSelectedItem();
			EPwType ePwType = (EPwType) codePwType.getObject();
			if (ePwType.getValue() == EPwType.ETH.getValue()) {
				payloadCombo.setEnabled(false);
			} else {
				payloadCombo.setEnabled(true);
				if("1".equals(((Code) ((ControlKeyValue) this.businessTypeComboBox.getSelectedItem())
						.getObject()).getCodeValue())){
					int index = this.businessTypeComboBox.getSelectedIndex();
					if(index == 0){
						this.businessTypeComboBox.setSelectedIndex(1);
					}else{
						this.businessTypeComboBox.setSelectedIndex(0);
					}
				}
				this.businessTypeComboBox.setEnabled(false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 自动命名
	 * 
	 * @param e
	 */
	private void autoNamingActionPerformed() {
		PwInfo pwInfo;
		ControlKeyValue pwTypeValue;
		try {
			pwInfo = new PwInfo();
			pwTypeValue = (ControlKeyValue) this.pwType.getSelectedItem();
			pwInfo.setIsSingle(0);
			if (0 == aSiteId || 0 == zSiteId) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
				return;
			}
			pwInfo.setASiteId(aSiteId);
			pwInfo.setZSiteId(zSiteId);
			pwInfo.setType((EPwType) pwTypeValue.getObject());
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			PortInst pA = new PortInst();
			pA.setSiteId(aSiteId);
			PortInst pZ = new PortInst();
			pZ.setSiteId(zSiteId);
			String autoNaming = (String) autoNamingUtil.autoNaming(pwInfo, pA, pZ);
			nametext.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}

	}	

	private boolean verify() {
		boolean flag = false;
		boolean flagReturn = false;
		String beforeName = null;
		OamInfoService_MB oamInfoService = null;
		try {
			if(this.pwInfo != null && this.pwInfo.getPwId() == 0){
				oamInfoService = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
				for(Tunnel tunnel :useTunnels){
					// 验证改tunnel上是否有lck告警
					OamMepInfo oamMepInfo = new OamMepInfo();
					oamMepInfo.setObjId(tunnel.getTunnelId());
					oamMepInfo.setObjType("TUNNEL_TEST");
					oamMepInfo.setLck(true);
					if((oamInfoService.selectByOamMepInfo(oamMepInfo).size()>0)){
						DialogBoxUtil.errorDialog(this, tunnel.getTunnelName()+ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_LCK));
						return false;
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoService);
		}

//		if (addPDialog.pwLabelList.size() == 0) {
//			DialogBoxUtil.errorDialog(addPDialog, ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_FILL));
//			return false;
//		}
		// 判断如果没有配置标签，则提示用户
		try {
			// 验证名称是否存在
			if (this.pwInfo != null && this.pwInfo.getPwId() != 0) {
				beforeName = this.pwInfo.getPwName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.PW.getValue(), this.nametext.getText().trim(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}

			// 创建的时候对OAM、QoS作校验
			if (this.pwInfo == null || this.pwInfo.getPwId() == 0) {
				if (this.getQosList() == null || this.getQosList().size() == 0) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
					return false;
				}
			}
			if (labelTable.getSelectedColumn() > 0 && labelTable.isEditing()) {
				// 验证输入标签是否合法
				if(!verifyLabelIsNumber()){
					return false;
				}
			} else if(pwLabelList.size()>0){
				flag = saveLabel();
			}
			if(!flag){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_FILL));
				return false;
			}
			flagReturn = true;
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} 
		return flagReturn;
	}

	private boolean verifyLabelIsNumber() {
		int selectR = labelTable.getSelectedRow();
		int selectC = labelTable.getSelectedColumn();
		int oldValue = Integer.parseInt(labelTable.getValueAt(selectR, selectC).toString());
		if (labelTable.isEditing()) {
			labelTable.getCellEditor().stopCellEditing();
		}
		try {
			int value = Integer.parseInt(labelTable.getValueAt(selectR, selectC).toString());
			if (value < 16 || value > 1048575) {
				labelOutOfBound(selectR, selectC, oldValue);
				return false;
			}
		} catch (Exception e) {
			labelOutOfBound(selectR, selectC, oldValue);
			return false;
		}
		return true;
	}
	
	private void labelOutOfBound(int selectR, int selectC, int oldValue){
		DialogBoxUtil.succeedDialog(this,ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
		labelTable.setValueAt(oldValue, selectR, selectC);
	}

	/**
	 * 分配标签
	 */
	private List<Integer> matchingLabel(Tunnel tunnel) {
		String label = null;
		try {
			label = matching_Step1(tunnel);
			int frontLabel = Integer.parseInt(label.split(",")[0]);
			if(!pwLabelList_temp.contains(frontLabel)){
				pwLabelList_temp.add(frontLabel);
			}
			pwLabelList.add(frontLabel);
			int backLabel = Integer.parseInt(label.split(",")[1]);
			if(!pwLabelList_temp.contains(backLabel)){
				pwLabelList_temp.add(backLabel);
			}
			pwLabelList.add(backLabel);	
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return pwLabelList;
	}

	/**
	 * 分配标签第一步 根据A，Z端网元id分配标签
	 * 
	 * @param aSiteId
	 * @param zSiteId
	 * @return
	 * @throws Exception
	 */
	private String matching_Step1(Tunnel tunnel) throws Exception {
		String label = null;
		LabelInfoService_MB labelService = null;
		LspInfoService_MB lspService = null;
		boolean flag = true;
		try {
		    labelService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
		    siteId_portIdMap.clear();
			siteId_portIdMap.put(tunnel.getASiteId() + "-" + tunnel.getZSiteId(), tunnel.getAPortId() + "-" + tunnel.getZPortId());
			/*目前设备的芯片不支持同一端口的lsp的入标签和该端口上的pw的入标签一样,所以要用while里面的代码******************************/
			while(flag){
				label = labelService.matchingUsableLabel(tunnel.getASiteId(), tunnel.getZSiteId(), siteId_portIdMap, pwLabelList_temp, "PW");
				//分配完标签后,再做验证,如果通过,就跳出循环
				if(this.verifyInLabel(label, tunnel, lspService)){
					flag = false;
				}else{
					int frontLabel = Integer.parseInt(label.split(",")[0]);
					if(!pwLabelList_temp.contains(frontLabel)){
						pwLabelList_temp.add(frontLabel);
					}
					int backLabel = Integer.parseInt(label.split(",")[1]);
					if(!pwLabelList_temp.contains(backLabel)){
						pwLabelList_temp.add(backLabel);
					}
				}
			}
			/*如果以后芯片支持同一端口的lsp的入标签和该端口上的pw的入标签一样,就用下面的代码,把上面的代码关掉**********************************/
//			label = labelService.matchingUsableLabel(tunnel.getASiteId(), tunnel.getZSiteId(), siteId_portIdMap, pwLabelList_temp, "PW");
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(labelService);
			UiUtil.closeService_MB(lspService);
		}
		return label;
	}

	/**
	 * 验证pw的入标签,如果同一端口的lsp的入标签和该端口上的pw的入标签一样,返回false,如果不一样,返回true
	 * @param label 例如"16,17"
	 * @param tunnel 根据端口号和siteId,去查找lsp中的端口所有的入标签,进行比较
	 * @param lspService 
	 */
	private boolean verifyInLabel(String label, Tunnel tunnel, LspInfoService_MB lspService) {
		int frontLabel = Integer.parseInt(label.split(",")[0]);//z网元的入标签
		int backLabel = Integer.parseInt(label.split(",")[1]);//a网元的入标签
		try {
			int direction = 1;//1 正向 2 反向
			int asiteId = 0;
			if (pwLabelList_old.size() > 0) {
				asiteId = aSiteId;
			}else{
				asiteId = tunnelTopology.getSiteA().getSite_Inst_Id();
			}
			//说明是反向
			if(asiteId == useTunnels.get(0).getzSiteId()){
				direction = 2;
			}
			boolean aFlag = false;
			boolean zFlag = false;
			if(direction == 1){
				//先比较A网元的入标签
				aFlag = lspService.verifyInLabel(tunnel.getaSiteId(), tunnel.getaPortId(), backLabel);
				//再比较Z网元的入标签
				zFlag = lspService.verifyInLabel(tunnel.getzSiteId(), tunnel.getzPortId(), frontLabel);
			}else{
				//先比较A网元的入标签
				aFlag = lspService.verifyInLabel(tunnel.getzSiteId(), tunnel.getzPortId(), backLabel);
				//再比较Z网元的入标签
				zFlag = lspService.verifyInLabel(tunnel.getaSiteId(), tunnel.getaPortId(), frontLabel);
			}
			if(aFlag && zFlag){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return true;
	}

	/**
	 * 分配标签第二步 界面显示标签值
	 * 
	 * @param labelValues
	 * @param index
	 * @throws Exception
	 */
	private void matching_Step2(List<Integer> labelValues) throws Exception {
		tableModel = (DefaultTableModel) labelTable.getModel();
		// 更新数据
		for (int i = 0; i < labelValues.size()/2; i++) {
			Object[] obj = null;
			int aValue = labelValues.get(2*i+0);
			int zValue = labelValues.get(2*i+1);
			obj = new Object[] { 1, aValue, zValue };
			tableModel.addRow(obj);
		}
		
		labelTable.setModel(tableModel);
	}

	/**
	 * 保存标签,验证标签是否可用，不可用则提示用户
	 * 
	 * @throws Exception
	 */
	private boolean saveLabel() throws Exception {
		LabelInfoService_MB labelService = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
		LspInfoService_MB lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
		List<Integer> labelList = new ArrayList<Integer>();
		try {
			pwLabelList.clear();
			for (int i = 0; i < labelTable.getRowCount(); i++) {
				pwLabelList.add(Integer.parseInt(labelTable.getValueAt(i, 1).toString()));
				pwLabelList.add(Integer.parseInt(labelTable.getValueAt(i, 2).toString()));
			}
			if (pwLabelList_old.size() > 0) {
				// 修改pw标签时在此验证
				List<Integer> integers = new ArrayList<Integer>();
				integers.add(pwInfo.getASiteId());
				for(MsPwInfo msPwInfo: pwInfo.getMsPwInfos()){
					integers.add(msPwInfo.getSiteId());
				}
				integers.add(pwInfo.getZSiteId());
				for (int i = 0; i < pwLabelList.size()/2; i++) {
					int aLabel = pwLabelList.get(2*i+0);
					int aLabel_old = pwLabelList_old.get(2*i+0);
					int zLabel = pwLabelList.get(2*i+1);
					int zLabel_old = pwLabelList_old.get(2*i+1);
					boolean flag = false;
					if (aLabel != aLabel_old) {
						flag = labelService.isUsedLabel(aLabel, integers.get(i+1),"PW");
						// 如果flag为true，则表示标签可用，否则不可用
						if (!flag) {
							labelList.add(aLabel);
						}
					}
					
					if (zLabel != zLabel_old) {
						flag = labelService.isUsedLabel(zLabel, integers.get(i),"PW");
						// 如果flag为true，则表示标签可用，否则不可用
						if (!flag) {
							labelList.add(zLabel);
						}
					}
				}
			} else {
				boolean flag = false;
				// 新建pw时在此验证
				List<SiteInst> insts = new ArrayList<SiteInst>();
				insts.add(tunnelTopology.getSiteA());
				insts.addAll(siteInsts);
				insts.add(tunnelTopology.getSiteZ());
				if (pwLabelList.size() > 0) {
					for (int i = 0; i < useTunnels.size(); i++) {
						flag = labelService.isUsedLabel(pwLabelList.get(2*i+0), insts.get(i+1).getSite_Inst_Id(),"PW");
						// 如果flag为true，则表示标签可用，否则不可用
						if (!flag) {
							labelList.add(pwLabelList.get(2*i+0));
						}
						
						flag = labelService.isUsedLabel(pwLabelList.get(2*i+1), insts.get(i).getSite_Inst_Id(),"PW");
						// 如果flag为true，则表示标签可用，否则不可用
						if (!flag) {
							labelList.add(pwLabelList.get(2*i+1));
						}
					}
				}
			}
			
			/*目前设备的芯片不支持同一端口的lsp的入标签和该端口上的pw的入标签一样,所以要加上下面的代码*******************************/
			if (pwLabelList.size() > 0) {
				int direction = 1;//1 正向 2 反向
				boolean flag = false;
				int asiteId = 0;
				if (pwLabelList_old.size() > 0) {
					asiteId = aSiteId;
				}else{
					asiteId = tunnelTopology.getSiteA().getSite_Inst_Id();
				}
				//说明是反向
				if(asiteId == useTunnels.get(0).getzSiteId()){
					direction = 2;
				}
				for (int i = 0; i < useTunnels.size(); i++) {
					Tunnel tunnel = useTunnels.get(i);
					if(direction == 1){
						flag = lspService.verifyInLabel(tunnel.getaSiteId(), tunnel.getaPortId(), 
								pwLabelList.get(2*i+1));
					}else{
						flag = lspService.verifyInLabel(tunnel.getzSiteId(), tunnel.getzPortId(), 
								pwLabelList.get(2*i+1));
					}
					// 如果flag为true，则表示标签可用，否则不可用
					if (!flag && !labelList.contains(pwLabelList.get(2*i+1))) {
						labelList.add(pwLabelList.get(2*i+1));
					}
					
					if(direction == 1){
						flag = lspService.verifyInLabel(tunnel.getzSiteId(), tunnel.getzPortId(), 
								pwLabelList.get(2*i+0));
					}else{
						flag = lspService.verifyInLabel(tunnel.getaSiteId(), tunnel.getaPortId(), 
								pwLabelList.get(2*i+0));
					}
					// 如果flag为true，则表示标签可用，否则不可用
					if (!flag && !labelList.contains(pwLabelList.get(2*i+0))) {
						labelList.add(pwLabelList.get(2*i+0));
					}
				}
			}
			/*如果以后芯片支持同一端口的lsp的入标签和该端口上的pw的入标签一样,就把上面的代码关掉**************************/
			
			if (labelList.size() == 0) {
				// 标签可用，就可下发入库
				return true;
			} else {
				// 标签不可用
				String notice = "";
				int siteId_label = 0;
				for (int i = 0; i < labelList.size(); i++) {
					siteId_label = labelList.get(i);
					notice += siteId_label + " ";
				}
				notice += ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_OCCUPY);
				DialogBoxUtil.errorDialog(this, notice);
				return false;
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(labelService);
			UiUtil.closeService_MB(lspService);
		}
	}

	/**
	 * 判断是否分配标签
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isMatchingLabel() throws Exception {
		boolean flag = true;
		if (pwLabelList.size() == 0) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 使表格数据瞬间变化
	 * 
	 * @param table
	 * @throws Exception
	 */
	private boolean commitTable(JTable table) throws Exception {
		boolean flag = true;
		int label = 0;
		// 标签范围：16-1048575
		try {
			int selectR = labelTable.getSelectedRow();
			int selectC = labelTable.getSelectedColumn();
			label = Integer.parseInt(labelTable.getValueAt(selectR, selectC).toString());
			if (label < 16 || label > 1048575) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
			return flag;
		}
		return flag;
	}

	/**
	 * 初始化表格
	 */
	private void initLabelTable() {
		this.labelTable = new JTable();
		labelTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), ResourceUtil.srcStr(StringKeysObj.STRING_A_LABEL), ResourceUtil.srcStr(StringKeysObj.STRING_Z_LABEL) }) {

			private static final long serialVersionUID = 4920383802986023368L;
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class, java.lang.Object.class };
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0)
					return false;
				return true;
			}
		});

		labelTable.getTableHeader().setResizingAllowed(true);
		labelTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = labelTable.getColumnModel().getColumn(0);
		c.setPreferredWidth(30);
		c.setMaxWidth(30);
		c.setMinWidth(30);

		this.pane = new JScrollPane();
		this.pane.setViewportView(this.labelTable);

		autoTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), ResourceUtil.srcStr(StringKeysObj.STRING_ROUTER) }) {

			private static final long serialVersionUID = 714435537424144075L;
			Class[] types = new Class[] { java.lang.Object.class, java.lang.Object.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0)
					return false;
				return true;
			}

		});
	}

	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jPanel2.setPreferredSize(new Dimension(250, 700));
		this.jSplitPane1.setLeftComponent(this.jPanel2);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 5, 150, 50 };
		layout.columnWeights = new double[] { 0.0, 0, 0 };
		layout.rowHeights = new int[] { 25, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.jPanel2.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		int i = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel2.add(this.lblMessage);

		/**
		 * 第一行,名称LABLE,文本框3列 有2列合并
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel1, c);
		this.jPanel2.add(this.jLabel1);
		c.gridx = 1;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.nametext, c);
		this.jPanel2.add(this.nametext);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.autoNamingButton, c);
		this.jPanel2.add(this.autoNamingButton);
		/**
		 * 第二行,名称LABLE,文本框3列 有2列合并
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.pwTypeName, c);
		this.jPanel2.add(this.pwTypeName);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.pwType, c);
		this.jPanel2.add(this.pwType);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.businessType, c);
		this.jPanel2.add(this.businessType);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.businessTypeComboBox, c);
		this.jPanel2.add(this.businessTypeComboBox);

		/**
		 * 第三行
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.payloadTxt, c);
		this.jPanel2.add(this.payloadTxt);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.payloadCombo, c);
		this.jPanel2.add(this.payloadCombo);

		/**
		 * 第4行,隧道LABLE,下拉列表 3列 有2列合并
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.lblAutoroute, c);
		this.jPanel2.add(this.lblAutoroute);

		// tunnel列表
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.autoPane, c);
		this.jPanel2.add(this.autoPane);

		c.gridx = 2;
		c.gridy = i++;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		layout.addLayoutComponent(this.autoBtn, c);
		this.jPanel2.add(this.autoBtn);

		/**
		 * 第5行,A端端口配置,下拉列表 有2列合并
		 */
		// c.gridx = 0;
		// c.gridy = 4;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(5, 10, 5, 5);
		// // layout.setConstraints(this.jLabel7, c);
		// // this.jPanel2.add(this.jLabel7);
		// c.gridx = 1;
		// c.gridy = 4;
		// c.gridheight = 1;
		// c.gridwidth = 2;
		// c.insets = new Insets(5, 5, 5, 5);
		// // layout.addLayoutComponent(this.aPortComboBox, c);
		// // this.jPanel2.add(this.aPortComboBox);
		// /**
		// * 第五行,Z端端口配置,下拉列表 有2列合并
		// */
		// c.gridx = 0;
		// c.gridy = 5;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(5, 10, 5, 5);
		// // layout.setConstraints(this.jLabel8, c);
		// // this.jPanel2.add(this.jLabel8);
		// c.gridx = 1;
		// c.gridy = 5;
		// c.gridheight = 1;
		// c.gridwidth = 2;
		// c.insets = new Insets(5, 5, 5, 5);
		// layout.addLayoutComponent(this.zPortComboBox, c);
		// this.jPanel2.add(this.zPortComboBox);
		// /**
		// * 第六行,入标签,文本框, 有2列合并
		// */
		// c.gridx = 0;
		// c.gridy = 6;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(5, 10, 5, 5);
		// layout.setConstraints(this.jLabel4, c);
		// this.jPanel2.add(this.jLabel4);
		// c.gridx = 1;
		// c.gridy = 6;
		// c.gridheight = 1;
		// c.gridwidth = 2;
		// c.insets = new Insets(5, 5, 5, 5);
		// layout.addLayoutComponent(this.inlabel, c);
		// this.jPanel2.add(this.inlabel);
		// /**
		// * 第七行 ,出标签,文本框,有2列合并
		// */
		// c.gridx = 0;
		// c.gridy = 7;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(5, 10, 5, 5);
		// layout.setConstraints(this.jLabel5, c);
		// this.jPanel2.add(this.jLabel5);
		// c.gridx = 1;
		// c.gridy = 7;
		// c.gridheight = 1;
		// c.gridwidth = 2;
		// c.insets = new Insets(5, 5, 5, 5);
		// layout.addLayoutComponent(this.outlabel, c);
		// this.jPanel2.add(this.outlabel);
		/**
		 * 插入两行：分段路由下拉列表，选择Tunnel下拉列表
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.lblMSRoute, c);
		this.jPanel2.add(this.lblMSRoute);

		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.MSRouteJcbBox, c);
		this.jPanel2.add(this.MSRouteJcbBox);

		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.addLayoutComponent(this.lblTunnel, c);
		this.jPanel2.add(this.lblTunnel);

		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.tunnelJcbBox, c);
		this.jPanel2.add(this.tunnelJcbBox);
		//显示tunnel标签信息
		c.gridx = 1;
		c.gridy = i++;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.lblTunnelLabel, c);
		this.jPanel2.add(this.lblTunnelLabel);

		
		/**
		 * 插入两行，标签表格，重置标签按钮，保存标签按钮
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.lblLabel, c);
		this.jPanel2.add(this.lblLabel);

		c.gridx = 1;
		c.gridy = i;
		c.gridheight = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.pane, c);
		this.jPanel2.add(this.pane);

		c.gridx = 2;
		c.gridy = i++;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		layout.addLayoutComponent(this.btnReset, c);
		this.jPanel2.add(this.btnReset);

		c.gridx = 2;
		c.gridy = i++;
		layout.addLayoutComponent(this.btnSave, c);
		this.jPanel2.add(this.btnSave);

		/**
		 * 第7行,QosLable,文本框,按钮
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.qoslable, c);
		this.jPanel2.add(this.qoslable);
		c.gridx = 1;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.qostext, c);
		this.jPanel2.add(this.qostext);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.qosConfigButton, c);
		this.jPanel2.add(this.qosConfigButton);

		/**
		 * 第8行,OamLable,文本框,按钮
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.oamlable, c);
		this.jPanel2.add(this.oamlable);
		c.gridx = 1;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.oamtext, c);
		this.jPanel2.add(this.oamtext);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.oamConfigButton, c);
		this.jPanel2.add(this.oamConfigButton);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.modelJLabel, c);
		this.jPanel2.add(this.modelJLabel);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.modelJComboBox, c);
		this.jPanel2.add(this.modelJComboBox);
		

		/**
		 * 第9行 , 是否激活,单选按钮,有2列合并
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel3, c);
		this.jPanel2.add(this.jLabel3);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.isActive, c);
		this.jPanel2.add(this.isActive);
		
		/**
		 * 插入一行，批量创建的数量
		 */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lblNumber, c);
		this.jPanel2.add(this.lblNumber);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.ptnSpinnerNumber, c);
		this.jPanel2.add(this.ptnSpinnerNumber);

		/**
		 * 中间插入一行，添加映射表配置,第一列 “映射表管理”标签 第二列 “配置”按钮
		 */
		c.gridx = 0;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.lblMapping, c);
		this.jPanel2.add(this.lblMapping);
		c.gridx = 1;
		c.gridy = i++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 130);
		layout.addLayoutComponent(this.btnManager, c);
		this.jPanel2.add(this.btnManager);

//		c.gridx = 0;
//		c.gridy = i;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 5);
//		layout.setConstraints(this.vlan, c);
//		this.jPanel2.add(this.vlan);
//		c.gridx = 1;
//		c.gridy = i++;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 130);
//		layout.addLayoutComponent(this.vlanButton, c);
//		this.jPanel2.add(this.vlanButton);
		
		/** 第十二行 确定按钮 中间空出一行 */
		c.gridx = 2;
		c.gridy = i;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.Confirm, c);
		this.jPanel2.add(this.Confirm);
	}

	private void oamConfigButtonActionPerformed(java.awt.event.ActionEvent evt) throws Exception {
		oamConfig();
	}

	/*
	 * 配置oam
	 */
	private void oamConfig() throws Exception {
		if (!checkPortHasConfig()) {
			return;
		}
		if(pwInfo.getASiteId() == 0){
			pwInfo.setASiteId(tunnelTopology.getSiteA().getSite_Inst_Id());
		}
		if(pwInfo.getZSiteId() == 0){
			pwInfo.setZSiteId(tunnelTopology.getSiteZ().getSite_Inst_Id());
		}
		new OamInfoDialog(pwInfo, EServiceType.PW.toString(), 0, true,this);
	}

	private void qosConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			qosConfig();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/*
	 * 配置qos
	 */
	private void qosConfig() throws Exception {
		if (!checkPortHasConfig()) {
			return;
		}
		this.pwInfo.setPayload(Integer.parseInt(((ControlKeyValue) this.payloadCombo.getSelectedItem()).getId()));
		setChoosePwType((EPwType) ((ControlKeyValue) this.pwType.getSelectedItem()).getObject());
		controller = new QosConfigController();
		controller.openQosConfig(controller, "PW", pwInfo, (EPwType) ((ControlKeyValue) this.pwType.getSelectedItem()).getObject(),AddPDialog.this);
	}

	/**
	 * 配置映射表
	 * 
	 * @return
	 * @throws Exception
	 */
	private void mappingConfig() throws Exception {
		if (!checkPortHasConfig()) {
			return;
		}
		new MappingConfigDialog(pwInfo);
	}

	private void vlanConfig() throws Exception{
		if (!checkPortHasConfig()) {
			return;
		}
		//new VlanConfigDialog(pwInfo);
	}
	
	private boolean checkPortHasConfig() throws Exception {
		Tunnel tunnel = new Tunnel();
		List<Tunnel> tunnels = null;
		TunnelService_MB tunnelService = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			if (null != tunnelTable.getAllElement() && tunnelTable.getAllElement().size() == 1) {
				tunnel.setTunnelId(tunnelTable.getAllElement().get(0).getTunnelId());
				tunnels = tunnelService.select(tunnel);
			}
			if (nametext.getText().trim().length() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				return false;
			}
			if (pwInfo == null) {
				pwInfo = new PwInfo();
				pwInfo.setPwName(nametext.getText());
			}
			return true;
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(tunnelService);
		}
	}

	protected void PwSelectItemStateChanged(ItemEvent evt) {
		PwNniInfoService_MB pwNniService = null;
		PwNniInfo pwNniInfo = null;
		List<PwNniInfo> aPwNniList = null;
		List<PwNniInfo> zPwNniList = null;
		Tunnel tunnel = null;
		ControlKeyValue controlKeyValue = null;
		DefaultComboBoxModel aportBoxModel = null;
		DefaultComboBoxModel zportBoxModel = null;

		try {
			aPortComboBox.removeAllItems();
			zPortComboBox.removeAllItems();
			aportBoxModel = new DefaultComboBoxModel();
			zportBoxModel = new DefaultComboBoxModel();

			controlKeyValue = (ControlKeyValue) evt.getItem();
			tunnel = (Tunnel) controlKeyValue.getObject();
			// TunnelTopoPanel.getTunnelTopoPanel().boxData(tunnel.getTunnelId());

			pwNniService = (PwNniInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwNniBuffer);
			aportBoxModel.addElement(new ControlKeyValue("0", "", null));
			zportBoxModel.addElement(new ControlKeyValue("0", "", null));
			if (pwInfo != null && pwInfo.getTunnelId() == tunnel.getTunnelId()) {
				if (pwInfo.getaPortConfigId() > 0) {
					pwNniInfo = new PwNniInfo();
					pwNniInfo.setId(pwInfo.getaPortConfigId());
					pwNniInfo = pwNniService.select(pwNniInfo).get(0);
					aportBoxModel.addElement(new ControlKeyValue(pwNniInfo.getId() + "", pwNniInfo.getName(), pwNniInfo));
				}

				if (pwInfo.getzPortConfigId() > 0) {
					pwNniInfo = new PwNniInfo();
					pwNniInfo.setId(pwInfo.getzPortConfigId());
					pwNniInfo = pwNniService.select(pwNniInfo).get(0);
					zportBoxModel.addElement(new ControlKeyValue(pwNniInfo.getId() + "", pwNniInfo.getName(), pwNniInfo));
				}

			} else {
				pwNniInfo = new PwNniInfo();
				pwNniInfo.setSiteId(tunnel.getASiteId());
				// pwNniInfo.setPortId(tunnel.getAPortId());
				aPwNniList = pwNniService.select(pwNniInfo);
				for (PwNniInfo obj : aPwNniList) {
					if (obj.getPwId() == 0)
						aportBoxModel.addElement(new ControlKeyValue(obj.getId() + "", obj.getName(), obj));
				}

				pwNniInfo = new PwNniInfo();
				pwNniInfo.setSiteId(tunnel.getZSiteId());
				// pwNniInfo.setPortId(tunnel.getZPortId());
				zPwNniList = pwNniService.select(pwNniInfo);
				for (PwNniInfo obj : zPwNniList) {
					if (obj.getPwId() == 0)
						zportBoxModel.addElement(new ControlKeyValue(obj.getId() + "", obj.getName(), obj));
				}

			}

			aPortComboBox.setModel(aportBoxModel);
			zPortComboBox.setModel(zportBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwNniService);
			pwNniInfo = null;
			aPwNniList = null;
			zPwNniList = null;
			tunnel = null;
			controlKeyValue = null;
			aportBoxModel = null;
			zportBoxModel = null;
		}
	}
	
	private void BuildPwInfo()
	{
		if (pwInfo == null) {
			pwInfo = new PwInfo();
		}
		SiteService_MB siteService=null;
		if (pwInfo.getPwId() == 0) {
			try {
				siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				this.pwInfo.setASiteId(aSiteId);
				this.pwInfo.setZSiteId(zSiteId);
				this.pwInfo.setQosList(this.getQosList());
				this.pwInfo.setAoppositeId(siteService.getSiteID(aSiteId));
				this.pwInfo.setZoppositeId(siteService.getSiteID(zSiteId));
				this.pwInfo.setShowaSiteName(siteService.getSiteName(aSiteId));
				this.pwInfo.setShowzSiteName(siteService.getSiteName(zSiteId));
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(siteService);
			}
		}
	}
	
	private void BuildPwInfo(PwInfo pwInfo,SiteService_MB siteService)
	{
		if (pwInfo == null) {
			pwInfo = new PwInfo();
		}
		if (pwInfo.getPwId() == 0) {
			try {
				pwInfo.setASiteId(aSiteId);
				pwInfo.setZSiteId(zSiteId);
				pwInfo.setQosList(this.getQosList());
				pwInfo.setAoppositeId(siteService.getSiteID(zSiteId));
				pwInfo.setZoppositeId(siteService.getSiteID(aSiteId));
				pwInfo.setShowaSiteName(siteService.getSiteName(aSiteId));
				pwInfo.setShowzSiteName(siteService.getSiteName(zSiteId));
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} 
		}
	}
	
	private void setPwinfo(PwInfo pwInfo, ControlKeyValue pwTypeValue, Code payload,Code businessType,Code codeModel) throws IOException, ClassNotFoundException
	{
		if (((EPwType) pwTypeValue.getObject()).getValue() != EPwType.ETH.getValue()) {
			pwInfo.setPayload(payload.getId());
		}
		pwInfo.setQosModel(Integer.parseInt(codeModel.getCodeValue()));
		if(pwInfo.getPwId() == 0){
			pwInfo.setOamList(this.copy(this.getOamList()));
		}else{
			if(this.getOamList() != null && this.getOamList().size() > 0){
				pwInfo.setOamList(this.getOamList());
			}
		}
		pwInfo.setType((EPwType) pwTypeValue.getObject());
		pwInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
		pwInfo.setCreateUser(ConstantUtil.user.getUser_Name());
		pwInfo.setPwStatus(isActive.isSelected() == true ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
		pwInfo.setBusinessType(businessType.getCodeValue());
		pwInfo.setaSourceMac("00-00-00-33-44-55");
		pwInfo.setAtargetMac("00-00-00-AA-BB-CC");
		pwInfo.setZtargetMac("00-00-00-33-44-55");
		pwInfo.setzSourceMac("00-00-00-AA-BB-CC");
		if(pwInfo.getaOutVlanValue() == 0){
			pwInfo.setaOutVlanValue(2);
		}
		if(pwInfo.getzOutVlanValue() == 0){
			pwInfo.setzOutVlanValue(2);
		}
	}
	
	/**
	 * 对象深度复制
	 */
	@SuppressWarnings("unchecked")
	private List<OamInfo> copy(List<OamInfo> oamList) throws IOException, ClassNotFoundException{
	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   ObjectOutputStream oos = new ObjectOutputStream(bos);
	   oos.writeObject(oamList);
	   ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
	   return (List<OamInfo>) ois.readObject();
	}
	
	private void  SetMultPwinfor(int times, MsPwInfo msPwInfo, List<MsPwInfo> msPwInfos,SiteService_MB siteService) throws Exception
	{
		int size = 0;
		try
		{
			if(siteInsts.size()>0){
				size = siteInsts.size();
			}else{
				size = pwInfo.getMsPwInfos().size();
				for(MsPwInfo ms: pwInfo.getMsPwInfos()){
					SiteInst siteInst = siteService.select(ms.getSiteId());
					siteInsts.add(siteInst);
				}
			}
			if(times == 1)
			{
				for (int i = 0; i < size; i++) 
				{
					if(pwInfo.getMsPwInfos()!= null && pwInfo.getMsPwInfos().size()>0){
						msPwInfo = pwInfo.getMsPwInfos().get(i);
					}else{
						msPwInfo = new MsPwInfo();
					}
					msPwInfo.setSiteId(siteInsts.get(i).getSite_Inst_Id());
					msPwInfo.setSiteName(siteInsts.get(i).getCellId());
					if(mipMap.get(siteInsts.get(i).getSite_Inst_Id()) != null){
						msPwInfo.setMipId(Integer.parseInt(mipMap.get(siteInsts.get(i).getSite_Inst_Id()).split(",")[0]));
					}
					msPwInfo.setFrontInlabel(pwLabelList.get(2*i));
					msPwInfo.setFrontOutlabel(pwLabelList.get(2*i+3));
					msPwInfo.setBackInlabel(pwLabelList.get(2*i+2));
					msPwInfo.setBackOutlabel(pwLabelList.get(2*i+1));
					msPwInfo.setFrontTunnelId(useTunnels.get(i).getTunnelId());
					msPwInfo.setBackTunnelId(useTunnels.get(i+1).getTunnelId());
					msPwInfo.setFrontTunnelName(useTunnels.get(i).getTunnelName());
					msPwInfo.setBackTunnelName(useTunnels.get(i+1).getTunnelName());
					msPwInfos.add(msPwInfo);
				}
			}
			else if(times > 1)
			{
				for (int i = 0; i < size; i++) {
					if(pwInfo.getMsPwInfos()!= null && pwInfo.getMsPwInfos().size()>0){
						msPwInfo = pwInfo.getMsPwInfos().get(i);
					}else{
						msPwInfo = new MsPwInfo();
					}
					msPwInfo.setSiteId(siteInsts.get(i).getSite_Inst_Id());
					msPwInfo.setSiteName(siteInsts.get(i).getCellId());
					if(mipMap.get(siteInsts.get(i).getSite_Inst_Id()) != null){
						msPwInfo.setMipId(Integer.parseInt(mipMap.get(siteInsts.get(i).getSite_Inst_Id()).split(",")[0]));
					}
					msPwInfo.setFrontInlabel(0);
					msPwInfo.setFrontOutlabel(0);
					msPwInfo.setBackInlabel(0);
					msPwInfo.setBackOutlabel(0);
					msPwInfo.setFrontTunnelId(useTunnels.get(i).getTunnelId());
					msPwInfo.setBackTunnelId(useTunnels.get(i+1).getTunnelId());
					msPwInfo.setFrontTunnelName(useTunnels.get(i).getTunnelName());
					msPwInfo.setBackTunnelName(useTunnels.get(i+1).getTunnelName());
					msPwInfos.add(msPwInfo);
				}
			}
		}
		catch(Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	//如果是批量创建多条pw，在此处理
	private void BuildMultiPwInfor() 
	{
		PwInfoService_MB pwService = null;
		SiteService_MB siteService = null;
		ControlKeyValue pwTypeValue = null;
		Code payload = null;
		Code codeModel = null;
		ControlKeyValue model = null;
		int num = Integer.parseInt(this.ptnSpinnerNumber.getValue().toString());
		Code businessType =(Code) ((ControlKeyValue) this.businessTypeComboBox.getSelectedItem()).getObject();
		DispatchUtil pwOperationImpl = null;
		String str = "";
		try {
			payload = (Code) ((ControlKeyValue) this.payloadCombo.getSelectedItem()).getObject();
			model = (ControlKeyValue) this.modelJComboBox.getSelectedItem();
			codeModel = (Code) model.getObject();
			pwTypeValue = (ControlKeyValue) this.pwType.getSelectedItem();
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			pwOperationImpl = new DispatchUtil(RmiKeys.RMI_PW);
			if(num == 1){
				BuildOnePwInfor(pwTypeValue, businessType,payload,codeModel);
			}else{
				List<PwInfo> pwInfos = new ArrayList<PwInfo>();
				if(num > 1 && !"1".equals(businessType.getCodeValue())){
					for(int i = 1; i <= num; i++)
					{
						PwInfo pwInfo = new PwInfo();
						BuildPwInfo(pwInfo,siteService);
						if(i == 1)
						{
							pwInfo.setInlabelValue(pwLabelList.get(1));
							pwInfo.setOutlabelValue(pwLabelList.get(0));
						}
						else if(i > 1)
						{
							pwInfo.setInlabelValue(0);
							pwInfo.setOutlabelValue(0);
						}
						
						pwInfo.setTunnelId(useTunnels.get(0).getTunnelId());
						pwInfo.setTunnelName(useTunnels.get(0).getTunnelName());
						
						setPwinfo(pwInfo,pwTypeValue,payload, businessType,codeModel);
						pwInfo.setPwName(nametext.getText()+"_Copy" + i);
						
						pwInfos.add(pwInfo);
					}
				}
				else if(num > 1 && "1".equals(businessType.getCodeValue()))
				{
					MsPwInfo msPwInfo = null;
					for(int k = 1; k <= num; k++)
					{
						List<MsPwInfo> msPwInfos = new ArrayList<MsPwInfo>();
						PwInfo pwInfo = new PwInfo();
						BuildPwInfo(pwInfo,siteService);
						pwInfo.setTunnelId(0);
						if(k == 1)
						{
							pwInfo.setInlabelValue(pwLabelList.get(0));
							pwInfo.setOutlabelValue(pwLabelList.get(1));
							pwInfo.setBackInlabel(pwLabelList.get(pwLabelList.size()-2));
							pwInfo.setBackOutlabel(pwLabelList.get(pwLabelList.size()-1));
						}
						else if(k > 1)
						{
							pwInfo.setInlabelValue(0);
							pwInfo.setOutlabelValue(0);
							pwInfo.setBackInlabel(0);
							pwInfo.setBackOutlabel(0);
						}
						
						SetMultPwinfor(k, msPwInfo, msPwInfos,siteService);
						
						pwInfo.setPwName(nametext.getText()+"_Copy" + k);
						setPwinfo(pwInfo,pwTypeValue,payload, businessType,codeModel);
						pwInfo.setMsPwInfos(msPwInfos);
						pwInfos.add(pwInfo);
					}
				}
				// 验证qos是否足够 --创建pw 验证选择的tunnel的带宽
				if (!pwInfos.isEmpty() && checkQosIsEnough(useTunnels.get(0), pwInfos.get(0), num)) {
					//如果是pdh类型的pw,则提示"请先增加端口和隧道的QoS配额"
					if(((EPwType) pwTypeValue.getObject()).getValue() == EPwType.PDH.getValue()) {
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOSISNOTENOUGH_E1));
					}else{
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_QOS_ALARM));
					}
					return;
				}
				str = pwOperationImpl.excuteInsert(pwInfos);
				// 添加日志记录
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				for (PwInfo pwInfo : pwInfos) {
					if(str.contains(ResultString.CONFIG_SUCCESS)){
						PwInfo pwTemp = new PwInfo();
						pwTemp.setPwName(pwInfo.getPwName());
						pwInfo = pwService.selectBypwid_notjoin(pwTemp);
					}
					this.insertOpeLog(EOperationLogType.PWINSERT.getValue(), str, null, pwInfo);
				}
				DialogBoxUtil.succeedDialog(this, str);
			}
			this.dispose();
			TWaverUtil.clearImageIconCache();
			if (null != this.pwPathPanel) {
				pwPathPanel.getController().refresh();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally {
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(siteService);
		}
		
	}

	//处理创建一条pw
	private void BuildOnePwInfor(ControlKeyValue pwTypeValue, Code businessType,Code payload, Code codeModel) {
		List<MsPwInfo> msPwInfos = null;
		MsPwInfo msPwInfo = null;
		SiteService_MB siteService=null;
		try {
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			msPwInfos = new ArrayList<MsPwInfo>();
			BuildPwInfo();
			if("1".equals(businessType.getCodeValue())){
				this.pwInfo.setTunnelId(0);
				this.pwInfo.setInlabelValue(pwLabelList.get(0));
				this.pwInfo.setOutlabelValue(pwLabelList.get(1));
				this.pwInfo.setBackInlabel(pwLabelList.get(pwLabelList.size()-2));
				this.pwInfo.setBackOutlabel(pwLabelList.get(pwLabelList.size()-1));
				SetMultPwinfor(1, msPwInfo, msPwInfos,siteService);
			}else{
				
				this.pwInfo.setTunnelId(useTunnels.get(0).getTunnelId());
				this.pwInfo.setTunnelName(useTunnels.get(0).getTunnelName());
				this.pwInfo.setInlabelValue(pwLabelList.get(1));
				this.pwInfo.setOutlabelValue(pwLabelList.get(0));
			}
			// 验证qos是否足够 --创建pw 验证选择的tunnel的带宽
			if (checkQosIsEnough(useTunnels.get(0), this.pwInfo, 1)) {
				//如果是pdh类型的pw,则提示"请先增加端口和隧道的QoS配额"
				if(((EPwType) pwTypeValue.getObject()).getValue() == EPwType.PDH.getValue()) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOSISNOTENOUGH_E1));
				}else{
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_QOS_ALARM));
				}
				return;
			}
			pwInfo.setPwName(nametext.getText());
			setPwinfo(pwInfo,pwTypeValue,payload, businessType,codeModel);
			pwInfo.setMsPwInfos(msPwInfos);
			DispatchUtil pwOperationImpl = new DispatchUtil(RmiKeys.RMI_PW);
			String str = "";
			List<PwInfo> pws = new ArrayList<PwInfo>();
			if (pwInfo.getPwId() == 0) {
				pws.add(pwInfo);
				str = pwOperationImpl.excuteInsert(pws);
				// 添加日志记录
				this.insertOpeLog(EOperationLogType.PWINSERT.getValue(), str, null, pwInfo);
			} else {
				PwInfo pwForLog = this.getpwInfoForLog();
				str = pwOperationImpl.excuteUpdate(pwInfo);
				// 添加日志记录
				this.insertOpeLog(EOperationLogType.PWUPDATE.getValue(), str, pwForLog, pwInfo);
			}
			DialogBoxUtil.succeedDialog(this, str);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}
	
	protected void insertOpeLog(int operationType, String result, PwInfo oldPw, PwInfo newPw){
		this.getOamSiteName(newPw);
		AddOperateLog.insertOperLog(Confirm, operationType, result, oldPw, newPw, newPw.getASiteId(), newPw.getPwName(), "pwInfo");
		AddOperateLog.insertOperLog(Confirm, operationType, result, oldPw, newPw, newPw.getZSiteId(), newPw.getPwName(), "pwInfo");
		if(newPw.getMsPwInfos() != null && newPw.getMsPwInfos().size() > 0){
			for (MsPwInfo msPw : newPw.getMsPwInfos()) {
				AddOperateLog.insertOperLog(Confirm, operationType, result, oldPw, newPw, msPw.getSiteId(), newPw.getPwName(), "pwInfo");
			}
		}
	}

	private PwInfo getpwInfoForLog() {
		SiteService_MB siteService = null;
		TunnelService_MB tunnelService = null;
		PwInfoService_MB pwService = null;
		PwInfo pw = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo condition = new PwInfo();
			condition.setPwId(this.pwInfo.getPwId());
			pw = pwService.selectBypwid_notjoin(condition);
			pw.setShowaSiteName(siteService.getSiteName(pw.getASiteId()));
			pw.setShowzSiteName(siteService.getSiteName(pw.getZSiteId()));
			pw.setTunnelName(tunnelService.getTunnelName(pw.getTunnelId()));
			List<MsPwInfo> msPwInfoList = pw.getMsPwInfos();
			if(msPwInfoList != null && msPwInfoList.size() > 0){
				for (MsPwInfo msPwInfo : msPwInfoList) {
					msPwInfo.setSiteName(siteService.getSiteName(msPwInfo.getSiteId()));
					msPwInfo.setFrontTunnelName(tunnelService.getTunnelName(msPwInfo.getFrontTunnelId()));
					msPwInfo.setBackTunnelName(tunnelService.getTunnelName(msPwInfo.getBackTunnelId()));
				}
			}
			this.getOamSiteName(pw);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(tunnelService);
		}
		return pw;
	}
	
	private void getOamSiteName(PwInfo pw){
		List<OamInfo> oamList = pw.getOamList();
		if(oamList != null && oamList.size() > 0){
			for (OamInfo oamInfo : oamList) {
				if(oamInfo.getOamMep().getSiteId() == pw.getASiteId()){
					oamInfo.getOamMep().setSiteName(pw.getShowaSiteName());
				}else if(oamInfo.getOamMep().getSiteId() == pw.getZSiteId()){
					oamInfo.getOamMep().setSiteName(pw.getShowzSiteName());
				}
			}
		}
	}

	// 验证qos是否足够 --创建pw 验证选择的tunnel的带宽
	private boolean checkQosIsEnough(Tunnel tunl, PwInfo pwInfo, int num) {
		List<QosInfo> tunnelQosInfoList = null;
		List<QosInfo> pwUsedQosInfoList = null;
		List<QosInfo> pwQosInfoList = null;
		QosInfoService_MB qosService = null;
		PwInfoService_MB pwService = null;
		Map<Integer, Integer> preMap = null;
		Map<Integer, Integer> nextMap = null;
		List<Integer> tunnelIds = null;
		List<PwInfo> pwList = null;
		try {
			tunnelIds = new ArrayList<Integer>();
			preMap = new HashMap<Integer, Integer>();
			nextMap = new HashMap<Integer, Integer>();
			pwList = new ArrayList<PwInfo>();
			qosService = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			tunnelQosInfoList = new ArrayList<QosInfo>();
			pwQosInfoList = new ArrayList<QosInfo>();
			tunnelQosInfoList = qosService.getQosByObj(EServiceType.TUNNEL.toString(), tunl.getTunnelId());
			for (QosInfo q : tunnelQosInfoList) {
				if (Integer.parseInt(q.getDirection()) == EQosDirection.FORWARD.getValue()) {
					preMap.put(q.getCos(), q.getCir());
				}
				if (Integer.parseInt(q.getDirection()) == EQosDirection.BACKWARD.getValue()) {
					nextMap.put(q.getCos(), q.getCir());
				}
			}
			// 找出多条在同一条隧道上的pw
			tunnelIds.add(tunl.getTunnelId());
			pwList = pwService.selectPwInfoByTunnelId(tunnelIds);
			// 减去已经被在同一条隧道上pw使用过的qos
			int used = 0;
			int use = 0;
			for (PwInfo pw : pwList) {
				pwUsedQosInfoList = new ArrayList<QosInfo>();
				pwUsedQosInfoList = qosService.getQosByObj(EServiceType.PW.toString(), pw.getPwId());
				for (QosInfo q : pwUsedQosInfoList) {
					if (Integer.parseInt(q.getDirection()) == EQosDirection.FORWARD.getValue()) {
						if(preMap.get(q.getCos()) != null){
							use = preMap.get(q.getCos());
							used = q.getCir();
							preMap.put(q.getCos(), use - used);
						}
					}
					if (Integer.parseInt(q.getDirection()) == EQosDirection.BACKWARD.getValue()) {
						if(nextMap.get(q.getCos()) != null){
							use = nextMap.get(q.getCos());
							used = q.getCir();
							nextMap.put(q.getCos(), use - used);
						}
					}
				}
			}
			pwQosInfoList = this.getQosList();
			if(pwQosInfoList != null){
				for (QosInfo q : pwQosInfoList) {
					if (Integer.parseInt(q.getDirection()) == EQosDirection.FORWARD.getValue()) {
						try {
							if (preMap.get(q.getCos()) != null) {
								if (q.getCir()*num > preMap.get(q.getCos())) {
									return true;
								}
							}else{
								return true;
							}
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						}
					}
					if (Integer.parseInt(q.getDirection()) == EQosDirection.BACKWARD.getValue()) {
						try {
							if (nextMap.get(q.getCos()) != null) {
								if (q.getCir()*num > nextMap.get(q.getCos())) {
									return true;
								}
							}else{
								return true;
							}
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(qosService);
			UiUtil.closeService_MB(pwService);
		}
		return false;
	}

//	protected void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {
//		ControlKeyValue controlKeyValue = (ControlKeyValue) evt.getItem();
//		TNetwork network = null;
//
//		try {
//			TunnelTopoPanel.getTunnelTopoPanel().boxData(Integer.parseInt(controlKeyValue.getId()));
//			network = TunnelTopoPanel.getTunnelTopoPanel().getNetWork();
//			network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		} finally {
//			controlKeyValue = null;
//			network = null;
//		}
//	}

	/**
	 * 武汉
	 * 
	 * @param 验证是否能在该tunnel上创建pw
	 * @return
	 */
//	public boolean tunnelUsed(Tunnel tunnel) {
//		CurAlarmService_MB curAlarmService = null;
//		List<Integer> siteList = null;
//		CurrentAlarmInfo currentAlarmInfo = null;
//		List<CurrentAlarmInfo> alarmInfos = null;
//		TunnelService_MB tunnelServiceMB = null;
//		try {
//			curAlarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
//			tunnelServiceMB=(TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
//			siteList = tunnelServiceMB.getSiteIds(tunnel, false);
//			if (siteList != null && siteList.size() > 0) {// 选择的tunnel包含武汉的设备
//				currentAlarmInfo = new CurrentAlarmInfo();
//				currentAlarmInfo.setObjectId(tunnel.getTunnelId());
//				currentAlarmInfo.setObjectType(EObjectType.TUNNEL);
//				currentAlarmInfo.setAlarmCode(183);
//				currentAlarmInfo.setAlarmLevel(2);
//				currentAlarmInfo.setSiteId(tunnel.getASiteId());
//				alarmInfos = curAlarmService.select(currentAlarmInfo);
//				if (alarmInfos != null && alarmInfos.size() > 0) {// 该tunnel存在TMP_LCK告警，则该tunnel不能再添加pw
//					return false;
//				}
//				currentAlarmInfo.setObjectId(tunnel.getTunnelId());
//				currentAlarmInfo.setSiteId(tunnel.getZSiteId());
//				alarmInfos = curAlarmService.select(currentAlarmInfo);
//				if (alarmInfos != null && alarmInfos.size() > 0) {// 该tunnel存在TMP_LCK告警，则该tunnel不能再添加pw
//					return false;
//				}
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		} finally {
//			UiUtil.closeService_MB(tunnelServiceMB);
//			UiUtil.closeService_MB(curAlarmService);
//			siteList = null;
//			currentAlarmInfo = null;
//			alarmInfos = null;
//		}
//		return true;
//	}

	public List<OamInfo> getOamList() {
		return oamList;
	}

	public void setOamList(List<OamInfo> oamList) {
		this.oamList = oamList;
	}

	public List<QosInfo> getQosList() {
		return qosList;
	}

	public void setQosList(List<QosInfo> qosList) {
		this.qosList = qosList;
	}

	public EPwType getChoosePwType() {
		return choosePwType;
	}

	public void setChoosePwType(EPwType choosePwType) {
		this.choosePwType = choosePwType;
	}

	public PtnButton getConfirm() {
		return Confirm;
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private PtnButton Confirm;
	private JButton btnManager;
	private JComboBox aPortComboBox;
	private JCheckBox isActive;
	private JLabel jLabel1;
	private JLabel jLabel3;
	private JLabel lblMapping;
	private JPanel jPanel2;
	private JSplitPane jSplitPane1;
	private JTextField nametext;
	private JButton oamConfigButton;
	private JButton qosConfigButton;
	private JComboBox zPortComboBox;
	private JLabel qoslable;
	private JLabel oamlable;
	private JTextField qostext;
	private JTextField oamtext;
	private JLabel pwTypeName;
	private JComboBox pwType;
	public EPwType choosePwType;
	public JLabel lblMessage;

	// 新增标签分配表格和按钮
	private JLabel lblLabel;
	private JTable labelTable;
	private JButton btnReset;
	private JButton btnSave;
	private JScrollPane pane;
	private JButton autoNamingButton;
	private JLabel payloadTxt;
	private JComboBox payloadCombo;
	private ViewDataTable<Tunnel> tunnelTable; // 选择的pwtable
	private JLabel lblAutoroute;
	private JTable autoTable;
	private JScrollPane autoPane;
	private JButton autoBtn;
	private JLabel businessType;// 业务类型
	private JComboBox businessTypeComboBox;
	private JLabel modelJLabel;//模式
	private JComboBox modelJComboBox;
	// 创建个数控件 dxh
	private JLabel lblNumber;
	private PtnSpinner ptnSpinnerNumber;
	private JLabel lblMSRoute;//分段路由
	private JComboBox MSRouteJcbBox;
	private JLabel lblTunnel;//选择Tunnel
	private JComboBox tunnelJcbBox;
	private JLabel lblTunnelLabel;//显示tunnel标签信息
	public boolean isHasCheck() {
		return hasCheck;
	}

	public void setHasCheck(boolean hasCheck) {
		this.hasCheck = hasCheck;
	}

	public PwInfo getPwInfo() {
		return pwInfo;
	}

	public void setPwInfo(PwInfo pwInfo) {
		this.pwInfo = pwInfo;
	}

	/**
	 * 给aSiteId赋值 ，并且刷新tunnel数据
	 * 
	 * @param aSiteId
	 */
	public List<Tunnel> aSiteIdChange(int aSiteId) {
		List<Tunnel> tunnelList;
		List<Tunnel> tunnelTable = null;
		TunnelService_MB tunnelServiceMB = null;
		try {
			this.aSiteId = aSiteId;
			tunnelTable = new ArrayList<Tunnel>();
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnelList = (List<Tunnel>) tunnelServiceMB.selectByASiteIdAndZSiteId(aSiteId, zSiteId);
			if (tunnelList.size() > 0) {
				tunnelTable.add(tunnelList.get(0));
			}
			this.tunnelTable.clear();
			// 清除标签面板
			tableModel = (DefaultTableModel) labelTable.getModel();
			tableModel.getDataVector().clear();
			tableModel.fireTableDataChanged();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
		}
		return tunnelTable;
	}

	/**
	 * 给aSiteId赋值 ，并且刷新tunnel数据
	 * 
	 * @param aSiteId
	 */
	public List<Tunnel> zSiteIdChange(int zSiteId) {
		List<Tunnel> tunnelList;
		List<Tunnel> tunnelTable = null;
		TunnelService_MB tunnelServiceMB = null;
		try {
			this.zSiteId = zSiteId;
			tunnelTable = new ArrayList<Tunnel>();
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnelList = (List<Tunnel>) tunnelServiceMB.selectByASiteIdAndZSiteId(aSiteId, zSiteId);
			if (tunnelList.size() > 0) {
				tunnelTable.add(tunnelList.get(0));
			}
			this.tunnelTable.clear();
			// 清除标签面板
			tableModel = (DefaultTableModel) labelTable.getModel();
			tableModel.getDataVector().clear();
			tableModel.fireTableDataChanged();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
		}
		return tunnelTable;
	}

	public JComboBox getPwType() {
		return pwType;
	}

	public void setPwType(JComboBox pwType) {
		this.pwType = pwType;
	}

	public int getaSiteId() {
		return aSiteId;
	}

	public void setaSiteId(int aSiteId) {
		this.aSiteId = aSiteId;
	}

	public int getzSiteId() {
		return zSiteId;
	}

	public void setzSiteId(int zSiteId) {
		this.zSiteId = zSiteId;
	}

	public ViewDataTable<Tunnel> getTunnelTable() {
		return tunnelTable;
	}

	public void setTunnelTable(ViewDataTable<Tunnel> tunnelTable) {
		this.tunnelTable = tunnelTable;
	}

	public List<SiteInst> getSiteInsts() {
		return siteInsts;
	}

	public void setSiteInsts(List<SiteInst> siteInsts) {
		this.siteInsts = siteInsts;
	}

	public Map<String, Tunnel> getRoute_TunnelMap() {
		return route_TunnelMap;
	}

	public List<Tunnel> getUseTunnels() {
		return useTunnels;
	}

	public void setPortInst_A(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setAText(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setZText(String string) {
		// TODO Auto-generated method stub
		
	}
}