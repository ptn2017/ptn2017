﻿package com.nms.ui.ptn.business.dialog.tunnel;

import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import twaver.Element;
import twaver.Link;
import twaver.TDataBox;
import twaver.TWaverConst;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.LabelInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.ptn.business.dialog.eline.AddElineAllDialog;
import com.nms.ui.ptn.business.dialog.pwpath.VlanConfigDialog;
import com.nms.ui.ptn.business.dialog.tunnel.action.TunnelAction;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 
 * @author __USER__
 */
public class AddTunnelPathDialog extends PtnDialog {

	private static final long serialVersionUID = -7662332319752178232L;
	//private static AddTunnelPathDialog addLspPathsDialog;
	private PortInst portInst_a = null;
	private PortInst portInst_z = null;
	private Tunnel tunnel;
	private boolean create = true;
	private boolean hasCheck = false;
	private List<OamInfo> oamList = new ArrayList<OamInfo>();
	private List<OamInfo> oamList_protect = new ArrayList<OamInfo>(); // 保护OAM
	private List<QosInfo> qosList = new ArrayList<QosInfo>();
	private Map<Integer, String> mipList = new HashMap<Integer, String>();
	private Map<Integer, String> mipList_pro = new HashMap<Integer, String>();
	private final TunnelAction tunnelAction = new TunnelAction();
	private TunnelBusinessPanel tunneBusinessPanel;
	private DefaultTableModel tableModel = null;
	private DefaultTableModel autoRouteModel = null;
	private final int NOINDEX = -1;
	private List<Component[]> addComponentList;
	private EquipmentTopology equipmentTopology=null;
	private JLabel inBandwidthControl;
	private JCheckBox inBandwidthControlCheckBox;
	private JLabel outBandwidthControl;
	private JCheckBox outBandwidthControlCheckBox;
	private Segment aWorkSegment;
	private Segment zWorkSegment;
	private Segment aProSegment;
	private Segment zProSegment;
	
	/**
	 * 快速配置业务时。需要用到此构造函数
	 * 
	 * @param AddElineAllDialog
	 */
	public AddTunnelPathDialog() {
		try {
			this.setModal(true);
			equipmentTopology=new EquipmentTopology(this);	
			initComponents();
			setLayout();
			this.comboBoxData();
			this.addListener(equipmentTopology);			
			jSplitPane1.setRightComponent(equipmentTopology);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	public AddTunnelPathDialog(TunnelBusinessPanel jpanel, boolean modal,Tunnel tunnel) {
		try {
			this.tunnel = tunnel;
			this.setModal(modal);
			this.equipmentTopology = new EquipmentTopology(this);	
			this.initComponents();
			this.setLayout();
			this.comboBoxData();
			this.addListener(equipmentTopology );
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_TUNNEL));
			jSplitPane1.setRightComponent(equipmentTopology);
			tunneBusinessPanel = jpanel;
			equipmentTopology.setSiteA(null);
			equipmentTopology.setSiteZ (null);
			this.resetBtn.setActionCommand("work");
			if (tunnel != null) 
			{
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_TUNNEL));
				this.AutoBtn.setEnabled(false);
				this.AutoProBtn.setEnabled(false);				
				this.resetBtn.setEnabled(false);
				cmbAport.setEnabled(false);
				cmbProAport.setEnabled(false);
				cmbZport.setEnabled(false);
				cmbProZport.setEnabled(false);
				cmbANe.setEnabled(false);
				cmbZNe.setEnabled(false);
				ptnSpinnerNumber.setEnabled(false);
				this.jcbPW.setEnabled(false);
				int sum = 0;
				if (tunnel.getQosList().size() != 0) 
				{
					for (int i = 0; i < tunnel.getQosList().size(); i++) 
					{
						sum += tunnel.getQosList().get(i).getCir();
					}
				} else 
				{
					txtQos.setText("totalCir=" + 0);
				}

				txtQos.setText("totalCir=" + sum);
				String megid = null;
				if (this.tunnel.getOamList().size() != 0) {
					for (OamInfo oamInfo : this.tunnel.getOamList()) {
						if (null != oamInfo.getOamMep()) {
							if (0 != oamInfo.getOamMep().getMegId()) {
								megid = oamInfo.getOamMep().getMegId() + "";
							} else {
								megid = oamInfo.getOamMep().getMegIcc()
										+ oamInfo.getOamMep().getMegUmc();
							}
						}
						if(null==oamInfo.getOamMep())
							break;
						if(null!=oamInfo.getOamMep().getMegIcc()&&!"".equals(oamInfo.getOamMep().getMegIcc())){
							txtOam.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
						}else{
							txtOam.setText("megid=" + oamInfo.getOamMep().getMegId());
						}
					}
				} else {
					txtOam.setText("megid=0");
				}

				Integer[][] siteId_label = null;
				Integer[][] siteId_label_temp = null;
				if (tunnel.getLspParticularList().size() > 0) {
					for (Lsp lsp : tunnel.getLspParticularList()) {
						siteId_label = new Integer[2][2];
						siteId_label_temp = new Integer[2][2];
						siteId_label[0][0] = lsp.getASiteId();
						siteId_label[0][1] = lsp.getFrontLabelValue();
						siteId_label[1][0] = lsp.getZSiteId();
						siteId_label[1][1] = lsp.getBackLabelValue();
						labelWorkList.add(siteId_label);

						siteId_label_temp[0][0] = lsp.getASiteId();
						siteId_label_temp[0][1] = lsp.getFrontLabelValue();
						siteId_label_temp[1][0] = lsp.getZSiteId();
						siteId_label_temp[1][1] = lsp.getBackLabelValue();
						labelWorkList_temp.add(siteId_label_temp);

						siteId_PortId_WorkMap.put(
								lsp.getASiteId() + "-" + lsp.getZSiteId(),
								lsp.getAPortId() + "-" + lsp.getZPortId());
					}
				}
				inBandwidthControlCheckBox.setSelected(tunnel.getInBandwidthControl()==1?true:false);
				outBandwidthControlCheckBox.setSelected(tunnel.getOutBandwidthControl()==1?true:false);
				if (tunnel.getProtectTunnelId() > 0) {
					for (Lsp lsp : tunnel.getProtectTunnel()
							.getLspParticularList()) {
						siteId_label = new Integer[2][2];
						siteId_label_temp = new Integer[2][2];
						siteId_label[0][0] = lsp.getASiteId();
						siteId_label[0][1] = lsp.getFrontLabelValue();
						siteId_label[1][0] = lsp.getZSiteId();
						siteId_label[1][1] = lsp.getBackLabelValue();
						labelProtList.add(siteId_label);

						siteId_label_temp[0][0] = lsp.getASiteId();
						siteId_label_temp[0][1] = lsp.getFrontLabelValue();
						siteId_label_temp[1][0] = lsp.getZSiteId();
						siteId_label_temp[1][1] = lsp.getBackLabelValue();
						labelProtList_temp.add(siteId_label_temp);

						siteId_PortId_ProtMap.put(
								lsp.getASiteId() + "-" + lsp.getZSiteId(),
								lsp.getAPortId() + "-" + lsp.getZPortId());
					}
				}

				this.cmbType.setEnabled(false);
				this.create = false;
				this.tunnelAction.initLeftPanelData(tunnel, this);
				this.equipmentTopology.removePopMenu();
				this.pathCheckButton.setEnabled(false);
				this.qosConfigButton.setEnabled(false);
				this.jCheckBox2.setEnabled(false);
				if (UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType()))
						.getCodeName().toString().trim().equals("1:1")) {
					txtWaitTime.setEnabled(true);
					txtDelayTime.setEnabled(true);
					sourceMacText_backup.setEnabled(true);
					endMacText_backup.setEnabled(true);
					vlanButton_backup.setEnabled(true);
				} else {
					txtWaitTime.setEnabled(false);
					txtDelayTime.setEnabled(false);
					sourceMacText_backup.setEnabled(false);
					endMacText_backup.setEnabled(false);
					vlanButton_backup.setEnabled(false);
				}
				chkAps.setEnabled(false);

				jCheckBoxSNCP.setEnabled(false);
				jComboBoxSNCP.setEnabled(false);
				if (tunnel.getSncpIds() != null
						&& tunnel.getSncpIds().length() > 0) {
					jCheckBoxSNCP.setSelected(true);
					setJComboBoxSNCP(tunnel);
				}
			}
			//addLspPathsDialog = this;
			if(null!=this.tunnel){
				this.oamList = this.tunnel.getOamList();
				if (null != this.tunnel.getProtectTunnel()) {
					this.oamList_protect = this.tunnel.getProtectTunnel()
							.getOamList();
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	public void setJComboBoxSNCP(Tunnel tunnel) {
		Tunnel sncpTunnel = new Tunnel();
		TunnelService_MB tunnelServiceMB = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			String[] strs = tunnel.getSncpIds().split("/");
			if (strs.length == 4) {
				sncpTunnel.setASiteId(Integer.parseInt(strs[0]));
				sncpTunnel.setAprotectId(Integer.parseInt(strs[1]));
				sncpTunnel.setZSiteId(Integer.parseInt(strs[2]));
				sncpTunnel.setZprotectId(Integer.parseInt(strs[3]));
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				defaultComboBoxModel = new DefaultComboBoxModel();
				sncpTunnel = tunnelServiceMB.select_nojoin(sncpTunnel).get(0);
				defaultComboBoxModel.addElement(new ControlKeyValue(sncpTunnel.getTunnelId() + "", sncpTunnel.getTunnelName() + "/"
						+ sncpTunnel.getASiteId() + "--"
						+ sncpTunnel.getZSiteId(), sncpTunnel));
				jComboBoxSNCP.setModel(defaultComboBoxModel);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}

	public void setcmbSite(JComboBox cmb)
	{
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			List<SiteInst> sites = getAllSites();
			String tip = ResourceUtil.srcStr(StringKeysTip.TIP_UNCHOUSEN);
			defaultComboBoxModel.addElement(new ControlKeyValue("0", tip, null));
			for(SiteInst site: sites){
				defaultComboBoxModel.addElement(new ControlKeyValue(site.getSite_Inst_Id()+"",site.getCellId(),site));
			}
			
			cmb.setModel(defaultComboBoxModel);
		}
		catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	public void setcmbPort(int siteId,JComboBox cmbport)
	{
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			String tip = ResourceUtil.srcStr(StringKeysTip.TIP_UNCHOUSEN);
			defaultComboBoxModel.addElement(new ControlKeyValue("0", tip, null));
			if(siteId != 0)
			{
				List<PortInst> ports = getAllPorts(siteId);
				for(PortInst port: ports){
					defaultComboBoxModel.addElement(new ControlKeyValue(port.getPortId()+"",port.getPortName(),port));
				}
			}
			cmbport.setModel(defaultComboBoxModel);
		}
		catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private List<PortInst> getAllPorts(int siteId)
	{
		PortService_MB portServiceMB = null;
		List<PortInst> ports = new ArrayList<PortInst>();
		try
		{
			portServiceMB = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
		    if(siteId > 0)
			{
				ports=portServiceMB.selectNniPortname(siteId);
			}
		}
		catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portServiceMB);
		}
		
		return ports;
	}
	
	private List<Integer> removeRepeatedID(List<Integer> Ids) {
		List<Integer> siteIds = Ids;
		for (int i = 0; i < siteIds.size() - 1; i++) {
			for (int j = siteIds.size() - 1; j > i; j--) {
				if (siteIds.get(j) == siteIds.get(i)) {
					siteIds.remove(j);
				}
			}
		}
		return siteIds;
	}
	private List<SiteInst> getAllSites()
	{
		SegmentService_MB segmentServiceMB = null;
		SiteService_MB siteServiceMB = null;
		List<SiteInst> sites = null;
		try {
			segmentServiceMB = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			sites = new ArrayList<SiteInst>();
			List<Segment> segmentList =	segmentServiceMB.selectNoOam();
			List<Integer> siteIds = new ArrayList<Integer>();
			for(Segment segment:segmentList)
			{
				if(!siteIds.contains(segment.getASITEID())){
					siteIds.add(segment.getASITEID());
				}
				if(!siteIds.contains(segment.getZSITEID())){
					siteIds.add(segment.getZSITEID());
				}
				
			}
			sites = siteServiceMB.selectByids(siteIds);			
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(segmentServiceMB);
			UiUtil.closeService_MB(siteServiceMB);
		}
		
		return sites;
	}

	/**
	 * 添加监听
	 */
	private void addListener(final EquipmentTopology equipmentTopology) {
		this.cmbType.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					try {
						Code code = (Code) ((ControlKeyValue) evt.getItem())
								.getObject();
						if ("1".equals(code.getCodeValue())) {
							equipmentTopology.clearProtect(AddTunnelPathDialog.this);
							txtWaitTime.getTxt().setText("");
							txtWaitTime.setEnabled(false);
							txtDelayTime.getTxt().setText("");
							txtDelayTime.setEnabled(false);
							chkAps.setEnabled(false);
							protectBack.setEnabled(false);
							AutoBtn.setEnabled(true);
							AutoProBtn.setEnabled(false);
							resetBtn.setEnabled(true);
							jCheckBoxSNCP.setEnabled(true);
							jComboBoxSNCP.setEnabled(true);
							sourceMacText_backup.setEnabled(false);
							endMacText_backup.setEnabled(false);
							vlanButton_backup.setEnabled(false);
							cmbANe.setEnabled(true);
							cmbZNe.setEnabled(true);
							cmbAport.setEnabled(true);
							cmbZport.setEnabled(true);
							cmbProAport.setEnabled(false);
							cmbProZport.setEnabled(false);
							cmbRotateWay.setEnabled(false);
							cmbRotateLocation.setEnabled(false);
							cmbRotateMode.setEnabled(false);
							spinnerTnpLayer.setEnabled(false);
							spinnerRotateThreshold.setEnabled(false);
						}else if ("2".equals(code.getCodeValue())){
							txtWaitTime.getTxt().setEditable(true);
							txtWaitTime.setEnabled(true);
							txtWaitTime.getTxt().setText("5");
							txtDelayTime.getTxt().setEditable(true);
							txtDelayTime.setEnabled(true);
							txtDelayTime.getTxt().setText("0");
							chkAps.setEnabled(true);
							protectBack.setEnabled(true);
							AutoBtn.setEnabled(true);
							AutoProBtn.setEnabled(true);
							resetBtn.setEnabled(true);
							
							jComboBoxSNCP.setEnabled(false);
							sourceMacText_backup.setEnabled(true);
							endMacText_backup.setEnabled(true);
							vlanButton_backup.setEnabled(true);
							cmbProAport.setEnabled(true);
							cmbProZport.setEnabled(true);
							cmbRotateWay.setEnabled(true);
							cmbRotateLocation.setEnabled(true);
							cmbRotateMode.setEnabled(true);
							spinnerTnpLayer.setEnabled(true);
							spinnerRotateThreshold.setEnabled(true);
						}else
						{
							txtWaitTime.getTxt().setEditable(true);
							txtWaitTime.setEnabled(true);
							txtWaitTime.getTxt().setText("5");
							txtDelayTime.getTxt().setEditable(true);
							txtDelayTime.setEnabled(true);
							txtDelayTime.getTxt().setText("0");
							chkAps.setEnabled(true);
							protectBack.setEnabled(true);
							AutoBtn.setEnabled(true);
							AutoProBtn.setEnabled(true);
							resetBtn.setEnabled(true);
							
							jComboBoxSNCP.setEnabled(true);
							sourceMacText_backup.setEnabled(true);
							endMacText_backup.setEnabled(true);
							vlanButton_backup.setEnabled(true);
							cmbProAport.setEnabled(true);
							cmbProZport.setEnabled(true);
							cmbRotateWay.setEnabled(true);
							cmbRotateLocation.setEnabled(true);
							cmbRotateMode.setEnabled(true);
							spinnerTnpLayer.setEnabled(true);
							spinnerRotateThreshold.setEnabled(true);
						}
						
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});

		this.autoTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent evt) {

				if (evt.getClickCount() == 1) {// 单击事件
					try {
						labelUI(autoTable.getSelectedRow());

					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});

		this.labResetBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (labelWorkList.size() != 0 || labelProtList.size() != 0) {
						if (labelTable.getSelectedColumn() > 1&& labelTable.isEditing()) {
							// 验证输入标签是否合法
							verifyLabelIsNumber();
						} else {
							resetLabel();
						}
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});

		this.labSaveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (labelWorkList.size() != 0 || labelProtList.size() != 0) {
						if (labelTable.getSelectedColumn() > 1&& labelTable.isEditing()) {
							verifyLabelIsNumber();
						} else {
							boolean flag = saveLabel();
							if (flag) {
								DialogBoxUtil.succeedDialog(AddTunnelPathDialog.this,ResourceUtil.srcStr(StringKeysTip.TIP_SAVE_SUCCEED));
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
				if (labelTable.getSelectedColumn() > 1) {
					int selectR = labelTable.getSelectedRow();
					int selectC = labelTable.getSelectedColumn();
					try {
						int oldValue = Integer.parseInt(labelTable.getValueAt(selectR, selectC).toString());
						if (labelTable.isEditing()) {
							labelTable.getCellEditor().stopCellEditing();
						}
						boolean flag = commitTable(labelTable);
						if (!flag) {
							DialogBoxUtil.succeedDialog(AddTunnelPathDialog.this,ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
							labelTable.setValueAt(oldValue, selectR, selectC);
							labelTable.updateUI();
						}
					} catch (Exception e) {
						try {
							if (labelTable.isEditing()) {
								labelTable.getCellEditor().stopCellEditing();
							}
							DialogBoxUtil.succeedDialog(AddTunnelPathDialog.this,ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
							labelUI(autoTable.getSelectedRow());
						} catch (Exception e1) {
							ExceptionManage.dispose(e1, this.getClass());
						}
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});
		
		autoNamingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoNamingActionPerformed();
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
		
		vlanButton_backup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					vlanBackUpConfig();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});
		
		cmbANe.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					Object object=null;
					try {
						object=((ControlKeyValue)cmbANe.getSelectedItem()).getObject();
						if(object instanceof SiteInst){
							SiteInst site = (SiteInst)object;
							setcmbPort(site.getSite_Inst_Id(), cmbAport);
							setcmbPort(site.getSite_Inst_Id(), cmbProAport);
							getEquipmentTopology().setSiteA(site);
						}
						//快速新建eline用到
						setSiteInst4Eline(1);
					} catch (Exception e1) {
						ExceptionManage.dispose(e1,this.getClass());
					}
				}
			}
		});
		
		cmbZNe.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					Object object=null;
					try {
						object=((ControlKeyValue)cmbZNe.getSelectedItem()).getObject();
						if(object instanceof SiteInst){
							SiteInst site = (SiteInst)object;
							setcmbPort(site.getSite_Inst_Id(), cmbZport);
							setcmbPort(site.getSite_Inst_Id(), cmbProZport);
							getEquipmentTopology().setSiteZ(site);
						}
						//快速新建eline用到
						setSiteInst4Eline(2);
					} catch (Exception e1) {
						ExceptionManage.dispose(e1,this.getClass());
					}
				}
			}
		});
		cmbAport.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == 1) 
				{
					setAWorkSegment(null);
					ControlKeyValue obj = (ControlKeyValue)cmbAport.getSelectedItem();
					//通过端口找光纤的对端网元，并设置为必经网元
					List<Segment> list = getMustSegment(Integer.parseInt(obj.getId()));
					 if(list.size()>0)
					 {
						 setAWorkSegment(list.get(0));
						 setWorkPort(true);
						 setPortexist(true);
					 }
					 else
					 {
						 //端口不存在路由判断
						 setPortexist(false);
						 return;
					 }
				}
			}
		});
		
		cmbZport.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) 
				{
					setZWorkSegment(null);
					ControlKeyValue obj = (ControlKeyValue)cmbZport.getSelectedItem();
					//通过端口找光纤的对端网元，并设置为必经网元
					List<Segment> list = getMustSegment(Integer.parseInt(obj.getId()));
					if(list.size()>0)
					 {
						 setZWorkSegment(list.get(0));
						 setWorkPort(true);
						 setPortexist(true);
					 }
					 else
					 {
						 //端口不存在路由判断
						 setPortexist(false);
						 return;
					 }
				}
			}
		});
		
		cmbProAport.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) 
			{
				if (e.getStateChange() == 1) 
				{
					setAProSegment(null);
					ControlKeyValue obj = (ControlKeyValue)cmbProAport.getSelectedItem();
					List<Segment> list = getMustSegment(Integer.parseInt(obj.getId()));
					if(list.size()>0)
					 {
						 setAProSegment(list.get(0));
						 setProtectPort(true);
						 setProtectportexist(true);
					 }
					 else
					 {
						 //端口不存在路由判断
						 setProtectportexist(false);
						 return;
					 }
				}
			}
		});
		
		cmbProZport.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) 
			{
				if (e.getStateChange() == 1) 
				{
					setZProSegment(null);
					ControlKeyValue obj = (ControlKeyValue)cmbProZport.getSelectedItem();
					List<Segment> list = getMustSegment(Integer.parseInt(obj.getId()));
					if(list.size()>0)
					 {
						 setZProSegment(list.get(0));
						 setProtectPort(true);
						 setProtectportexist(true);
					 }
					 else
					 {
						 //端口不存在路由判断
						 setProtectportexist(false);
						 return;
					 }
				}
			}
		});
	}
	
	protected void setSiteInst4Eline(int type) {
		if (this instanceof AddElineAllDialog) {
			AddElineAllDialog addElineAllDialog = (AddElineAllDialog) this;
			if(type == 1){
				addElineAllDialog.setSiteInst_a(this.equipmentTopology.getSiteA());
			}else{
				addElineAllDialog.setSiteInst_z(this.equipmentTopology.getSiteZ());
			}
		}
	}

	private List<Segment> getMustSegment(int portId)
	{
		List<Segment> list = null;
		SegmentService_MB segmentServiceMB = null;
		try {
			segmentServiceMB = (SegmentService_MB)ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			list = segmentServiceMB.selectBySegmentPortId(portId);
		} catch (Exception e1) {
			
			ExceptionManage.dispose(e1,this.getClass());
		}
		finally
		{
			UiUtil.closeService_MB(segmentServiceMB);
		}
		
		return list;
	}

	/**
	 * 主用外层vlan配置
	 */
	private void vlanConfig() throws Exception{
		new VlanConfigDialog(this.getTunnel(), ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
	}
	
	/**
	 * 备用外层vlan配置
	 */
	private void vlanBackUpConfig() throws Exception{
		if(this.getTunnel().getProtectTunnel() == null){
			this.getTunnel().setProtectTunnel(new Tunnel());
		}
		new VlanConfigDialog(this.getTunnel().getProtectTunnel(), ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_BACKUP));
	}
	
	/**
	 * 自动命名
	 * 
	 * @param e
	 */
	private void autoNamingActionPerformed() {
		try {
			Tunnel tunnel = new Tunnel();
			tunnel.setIsSingle(0);
			//提示A端Z端不能为空
			if (null == equipmentTopology.getSiteA()|| null == equipmentTopology.getSiteZ()) {
				DialogBoxUtil.errorDialog(this,ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
				return;
			}
//			portInst_a = portService.selectPortybyid(this.equipmentTopology.getAPortId());
//			portInst_z = portService.selectPortybyid(this.equipmentTopology.getZPortId());
			portInst_a = (PortInst)((ControlKeyValue)this.cmbAport.getSelectedItem()).getObject();
			portInst_z = (PortInst)((ControlKeyValue)this.cmbZport.getSelectedItem()).getObject();
			tunnel.setASiteId(equipmentTopology.getSiteA().getSite_Inst_Id());
			tunnel.setZSiteId(equipmentTopology.getSiteZ().getSite_Inst_Id());
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			String autoNaming = (String) autoNamingUtil.autoNaming(tunnel,portInst_a, portInst_z);
			jTextField1.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}

	}

	protected boolean verifyLabelIsNumber() {
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
	
	protected void labelOutOfBound(int selectR, int selectC, int oldValue){
		DialogBoxUtil.succeedDialog(this,ResourceUtil.srcStr(StringKeysTip.TIP_LIMIT_16_1040383));
		labelTable.setValueAt(oldValue, selectR, selectC);
	}

	protected void labelUI(int index) throws Exception {
		if (index == 0) {
			matching_Step4(labelWorkList, index);
			// 此处可将工作路由设置为蓝色...
		} else {
			matching_Step4(labelProtList, index);
		}

	}

	/**
	 * 根据路由信息分配标签
	 * 
	 * @throws Exception
	 */
	private void matchingLabel(int index) throws Exception {
		List<Integer> siteIds = null;
		try {
			// 先找出所选中路由经过的网元id
			siteIds = this.matching_Step1(index);
			// 判断是否有必经网元，根据判断结果分配标签
			if (siteIds.size() == 0) {
				int aSiteId = equipmentTopology.getSiteA().getSite_Inst_Id();
				int zSiteId = equipmentTopology.getSiteZ().getSite_Inst_Id();
				this.matching_step2(aSiteId, zSiteId, index);
			} else if (siteIds.size() > 0) {
				// 匹配中间网元标签
				int aSiteId = 0;
				int zSiteId = 0;
				// 标签的行数
				for (int i = 0; i < siteIds.size() + 1; i++) {
					if (i == 0) {
						aSiteId = equipmentTopology.getSiteA().getSite_Inst_Id();
						zSiteId = siteIds.get(i);
					} else {
						if (i == siteIds.size()) {
							aSiteId = siteIds.get(i - 1);
							zSiteId =equipmentTopology.getSiteZ().getSite_Inst_Id();
						} else {
							aSiteId = siteIds.get(i - 1);
							zSiteId = siteIds.get(i);
						}
					}

					this.matching_step2(aSiteId, zSiteId, index);
				}
			}

			if (index == 0) {
				matching_Step4(labelWorkList, index);
			} else {
				matching_Step4(labelProtList, index);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			siteIds = null;
		}

	}

	/**
	 * A网元的出标签等于Z网元的入标签 Z网元的出标签等于A网元的入标签
	 * 
	 * @param aSiteId
	 * @param zSiteId
	 * @throws Exception
	 */
	private void matchingForSNCP(int aSiteId, int zSiteId) throws Exception {
		Integer[][] labelValue = null;
		int value = 0;
		int aLabelArea = 0;
		int zLabelArea = 0;
		for (int i = 0; i < labelWorkList.size(); i++) {
			labelValue = labelWorkList.get(i);
			int siteId = labelValue[0][0];
			if (siteId == aSiteId) {
				int aSiteId_temp = labelValue[1][0];
				value = Integer.parseInt(matching_Step3(aSiteId_temp, zSiteId,
						0).split(",")[0]);
				labelWorkList.get(i)[0][1] = value;
				aLabelArea = i;
			}
			siteId = labelValue[1][0];
			if (siteId == zSiteId) {
				int zSiteId_temp = labelValue[0][0];
				value = Integer.parseInt(matching_Step3(aSiteId, zSiteId_temp,
						0).split(",")[1]);
				labelWorkList.get(i)[1][1] = value;
				zLabelArea = i;
			}
		}

		labelWorkList.get(zLabelArea)[0][1] = labelWorkList.get(aLabelArea)[0][1];
		labelWorkList.get(aLabelArea)[1][1] = labelWorkList.get(zLabelArea)[1][1];
		// 刷新界面数据
		labelUI(0);
	}

	/**
	 * 分配标签第一步 通过段查找必经网元id,然后确定aSiteId,zSiteId
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Integer> matching_Step1(int index) throws Exception {
		List<Integer> siteIds = new ArrayList<Integer>();
		List<Segment> segments = null;
		String key = null;
		String value = null;
		String key_last = null;
		String value_last = null;
		try {
			if (index == 0) {
				segments = workSg;
			} else {
				segments = getProSg();
			}

			if (segments.size() == 1) {
				int aSiteId_temp = segments.get(0).getASITEID();
				int aPortId = segments.get(0).getAPORTID();
				int zPortId = segments.get(0).getZPORTID();
				int aSiteId = equipmentTopology.getSiteA().getSite_Inst_Id();
				int zSiteId = equipmentTopology.getSiteZ().getSite_Inst_Id();
				key = aSiteId + "-" + zSiteId;
				if (aSiteId_temp == aSiteId) {
					value = aPortId + "-" + zPortId;
				} else {
					value = zPortId + "-" + aPortId;
				}
				if (index == 0) {
					// 工作路径
					siteId_PortId_WorkMap.put(key, value);
				} else {
					// 保护路径
					siteId_PortId_ProtMap.put(key, value);
				}
			} else {
				for (int i = 0; i < segments.size() - 1; i++) {
					int aSiteId = segments.get(i).getASITEID();
					int aPortId = segments.get(i).getAPORTID();
					int zSiteId = segments.get(i).getZSITEID();
					int zPortId = segments.get(i).getZPORTID();
					int aSiteId_next = segments.get(i + 1).getASITEID();
					int zSiteId_next = segments.get(i + 1).getZSITEID();

					if (aSiteId == aSiteId_next || aSiteId == zSiteId_next) {
						// 4<--3-->2 || 4<--3<--2
						key = zSiteId + "-" + aSiteId;
						value = zPortId + "-" + aPortId;
						siteIds.add(aSiteId);
					}

					if (zSiteId == aSiteId_next || zSiteId == zSiteId_next) {
						// 4-->3-->2 || 4-->3<--2
						key = aSiteId + "-" + zSiteId;
						value = aPortId + "-" + zPortId;
						siteIds.add(zSiteId);
					}

					if (index == 0) {
						// 工作路径
						siteId_PortId_WorkMap.put(key, value);
					} else {
						// 保护路径
						siteId_PortId_ProtMap.put(key, value);
					}

					if (i == segments.size() - 2) {
						if (aSiteId == aSiteId_next || zSiteId == aSiteId_next) {
							// 4<--3-->2 || 4-->3-->2
							key_last = aSiteId_next + "-" + zSiteId_next;
							value_last = segments.get(i + 1).getAPORTID() + "-"
									+ segments.get(i + 1).getZPORTID();
						}
						if (aSiteId == zSiteId_next || zSiteId == zSiteId_next) {
							// 4<--3<--2 || 4-->3<--2
							key_last = zSiteId_next + "-" + aSiteId_next;
							value_last = segments.get(i + 1).getZPORTID() + "-"
									+ segments.get(i + 1).getAPORTID();
						}
						if (index == 0) {
							// 工作路径
							siteId_PortId_WorkMap.put(key_last, value_last);
						} else {
							// 保护路径
							siteId_PortId_ProtMap.put(key_last, value_last);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			segments = null;
		}
		return siteIds;
	}

	/**
	 * 分配标签第二步
	 * 
	 * @param aSiteId
	 * @param zSiteId
	 * @param index
	 * @throws Exception
	 */
	private void matching_step2(int aSiteId, int zSiteId, int index)throws Exception {
		Integer[][] labelValue = new Integer[2][2];
		String label = null;
		label = this.matching_Step3(aSiteId, zSiteId, index);
		int value = Integer.parseInt(label.split(",")[0]);
		/* 匹配前向标签 */
		labelValue[0][0] = aSiteId;
		labelValue[0][1] = value;
		/* 匹配后向标签 */
		value = Integer.parseInt(label.split(",")[1]);
		labelValue[1][0] = zSiteId;
		labelValue[1][1] = value;
		if (index == 0) {
			labelWorkList.add(labelValue);
		} else {
			labelProtList.add(labelValue);
		}

	}

	/**
	 * 分配标签第三步 根据A，Z端网元id分配标签
	 * 
	 * @param aSiteId
	 * @param zSiteId
	 * @return
	 * @throws Exception
	 */
	private String matching_Step3(int aSiteId, int zSiteId, int index)
			throws Exception {
		String label = "";
		LabelInfoService_MB labelServiceMB = null;
		try {
			labelServiceMB = (LabelInfoService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.LABELINFO);
			// 自动匹配标签
			List<Integer> unEnabledLabel = new ArrayList<Integer>();
			String aValue = labelMap.get(aSiteId);
			String zValue = labelMap.get(zSiteId);
			if (aValue == null) {
				aValue = "/";
			}
			if (zValue == null) {
				zValue = "/";
			}
			String value = aValue + zValue;
			if (!value.equals("//")) {
				List<Integer> allLabelList = labelServiceMB.quertyAllLabel(aSiteId, "TUNNEL");
				String[] values = value.split("/");
				for (int i = 0; i < values.length; i++) {
					if (!values[i].equals("")) {
						// 下面这行代码是为了防止用户手动输入标签造成查库时没有可有标签(查库规则：大于当前的标签)
						if (allLabelList.contains(Integer.parseInt(values[i]))) {
							if (!unEnabledLabel.contains(Integer
									.parseInt(values[i]))) {
								unEnabledLabel.add(Integer.parseInt(values[i]));
							}
						}
					}
				}
			}
			label = labelServiceMB.matchingUsableLabel(aSiteId, zSiteId,index == 0 ? siteId_PortId_WorkMap : siteId_PortId_ProtMap,unEnabledLabel, "TUNNEL");
			int front_label = Integer.parseInt(label.split(",")[0]);
			int back_label = Integer.parseInt(label.split(",")[1]);
//			labelMap.put(aSiteId, aValue + front_label + "/");
			labelMap.put(zSiteId, zValue + front_label + "/"+back_label+"/");
			labelMap.put(aSiteId, aValue + front_label + "/"+back_label+"/");
//			labelMap.put(zSiteId, zValue + back_label + "/");
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(labelServiceMB);
		}
		return label;
	}

	/**
	 * 分配标签第五步 界面显示标签值
	 * 
	 * @param labelValues
	 * @param index
	 * @throws Exception
	 */
	private void matching_Step4(List<Integer[][]> labelValues, int index)
			throws Exception {
		tableModel = (DefaultTableModel) labelTable.getModel();
		// 清除原数据
		tableModel.getDataVector().clear();
		tableModel.fireTableDataChanged();
		// 更新数据
		Object[] obj = null;
		int aValue = 0;
		int zValue = 0;
		for (int i = 0; i < labelValues.size(); i++) {
			aValue = labelValues.get(i)[0][1];
			zValue = labelValues.get(i)[1][1];
			obj = new Object[] { index, i + 1, aValue, zValue };
			tableModel.addRow(obj);
		}
		labelTable.setModel(tableModel);
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
			label = Integer.parseInt(labelTable.getValueAt(selectR, selectC)
					.toString());
			if (label < 16 || label > 1048575) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			return false;
		}
		return flag;
	}

	/**
	 * 重置标签
	 * 
	 * @throws Exception
	 */
	private void resetLabel() throws Exception {
		tableModel = (DefaultTableModel) labelTable.getModel();
		List<Integer[][]> labelValues = new ArrayList<Integer[][]>();
		int index = -1;
		try {
			index = Integer.parseInt(tableModel.getValueAt(0, 0).toString());
		} catch (Exception e) {
			return;
		}
		int aValue = 0;
		int aSiteId = 0;
		int zValue = 0;
		int zSiteId = 0;
		Integer[][] values = null;
		String label = null;

		labelMap.clear();
		for (Integer[][] labels : labelWorkList) {
			String value = labelMap.get(labels[0][0]);
			if (value == null) {
				value = "/";
			}
			labelMap.put(labels[0][0], value + labels[0][1] + "/"
					+ labels[1][1] + "/");
			value = labelMap.get(labels[1][0]);
			if (value == null) {
				value = "/";
			}
			labelMap.put(labels[1][0], value + labels[0][1] + "/"
					+ labels[1][1] + "/");
		}
		for (Integer[][] labels : labelProtList) {
			String value = labelMap.get(labels[0][0]);
			if (value == null) {
				value = "/";
			}
			labelMap.put(labels[0][0], value + labels[0][1] + "/"
					+ labels[1][1] + "/");
			value = labelMap.get(labels[1][0]);
			if (value == null) {
				value = "/";
			}
			labelMap.put(labels[1][0], value + labels[0][1] + "/"
					+ labels[1][1] + "/");
		}

		if (index == 0) {
			// 说明是工作路由的标签
			List<Integer[][]> labelWorkList_ache = new ArrayList<Integer[][]>();
			for (Integer[][] labels : labelWorkList) {
				aSiteId = labels[0][0];
				zSiteId = labels[1][0];
				label = this.matching_Step3(aSiteId, zSiteId, index);
				aValue = Integer.parseInt(label.split(",")[0]);
				zValue = Integer.parseInt(label.split(",")[1]);
				values = new Integer[2][2];
				values[0][0] = aSiteId;
				values[0][1] = aValue;
				values[1][0] = zSiteId;
				values[1][1] = zValue;
				labelValues.add(values);
				labelWorkList_ache.add(values);
			}
			labelWorkList.clear();
			for (Integer[][] integers : labelWorkList_ache) {
				labelWorkList.add(integers);
			}

			// //如果类型是SNCP,保证工作路径的A，Z端的入标签相同
//			 matchingForSNCP();
		} else {
			// index为1，说明是保护路由的标签
			List<Integer[][]> labelProtList_ache = new ArrayList<Integer[][]>();
			for (Integer[][] labels : labelProtList) {
				aSiteId = labels[0][0];
				zSiteId = labels[1][0];
				label = this.matching_Step3(aSiteId, zSiteId, index);
				aValue = Integer.parseInt(label.split(",")[0]);
				zValue = Integer.parseInt(label.split(",")[1]);
				values = new Integer[2][2];
				values[0][0] = aSiteId;
				values[0][1] = aValue;
				values[1][0] = zSiteId;
				values[1][1] = zValue;
				labelValues.add(values);
				labelProtList_ache.add(values);
			}
			labelProtList.clear();
			for (Integer[][] integers : labelProtList_ache) {
				labelProtList.add(integers);
			}
		}
		matching_Step4(labelValues, index);
	}

	/**
	 * 保存标签,验证标签是否可用，不可用则提示用户
	 * 
	 * @throws Exception
	 */
	private boolean saveLabel() throws Exception {
		List<Integer> siteIdList = new ArrayList<Integer>();
		List<Integer> labelList = new ArrayList<Integer>();
		tableModel = (DefaultTableModel) labelTable.getModel();
		int index = -1;
		try {
			index = Integer.parseInt(tableModel.getValueAt(0, 0).toString());
		} catch (Exception e) {
			return true;
		}

		for (int i = 0; i < labelWorkList.size(); i++) {
			int aSiteId = labelWorkList.get(i)[0][0];
			int zSiteId = labelWorkList.get(i)[1][0];
			if (!siteIdList.contains(aSiteId)) {
				siteIdList.add(aSiteId);
			}
			if (!siteIdList.contains(zSiteId)) {
				siteIdList.add(zSiteId);
			}
		}

		for (int i = 0; i < labelProtList.size(); i++) {
			int aSiteId = labelProtList.get(i)[0][0];
			int zSiteId = labelProtList.get(i)[1][0];
			if (!siteIdList.contains(aSiteId)) {
				siteIdList.add(aSiteId);
			}
			if (!siteIdList.contains(zSiteId)) {
				siteIdList.add(zSiteId);
			}
		}

		if (index == 0) {
			for (int i = 0; i < labelWorkList.size(); i++) {
				labelWorkList.get(i)[0][1] = Integer.parseInt(tableModel
						.getValueAt(i, 2).toString());
				labelWorkList.get(i)[1][1] = Integer.parseInt(tableModel
						.getValueAt(i, 3).toString());
			}
		} else {
			for (int i = 0; i < labelProtList.size(); i++) {
				labelProtList.get(i)[0][1] = Integer.parseInt(tableModel
						.getValueAt(i, 2).toString());
				labelProtList.get(i)[1][1] = Integer.parseInt(tableModel
						.getValueAt(i, 3).toString());
			}
		}
		labelMap.clear();
		verifyLabelIsRepeated(labelWorkList);
		verifyLabelIsRepeated(labelProtList);

		// 先验证界面上的值是否有重复值
		List<Integer> unUsableLabel = new ArrayList<Integer>();
		for (int i = 0; i < siteIdList.size(); i++) {
			List<Integer> valueList = new ArrayList<Integer>();
			String value = labelMap.get(siteIdList.get(i));
			value = value.substring(0, value.length() - 1);
			String[] values = value.split("/");
			for (int j = 0; j < values.length; j++) {
				if (!"".equals(values[j])) {
					valueList.add(Integer.parseInt(values[j]));
				}
			}
			if (valueList.size() == 1) {
				// 如果是一个标签，无需验证
			}
			if (valueList.size() == 2) {
				// 如果是两个标签，不能重复
				int first = valueList.get(0);
				int second = valueList.get(1);
				if (first == second) {
					unUsableLabel.add(first);
				}
			} else if (valueList.size() == 4) {
				// 如果是四个标签，说明是中间网元，且有保护路径,四个标签不能一样
				int label1 = valueList.get(0);
				int label2 = valueList.get(1);
				int label3 = valueList.get(2);
				int label4 = valueList.get(3);
				if (label1 == label2 || label1 == label3 || label1 == label4) {
					unUsableLabel.add(label1);
				}
				if (label2 == label3 || label2 == label4) {
					unUsableLabel.add(label2);
				}
				if (label3 == label4) {
					unUsableLabel.add(label3);
				}
			}
		}

		// 如果有重复值，无需下一步验证，直接提示用户标签不可用
		if (unUsableLabel.size() > 0) {
			String notice = "";
			for (int i = 0; i < unUsableLabel.size(); i++) {
				notice += unUsableLabel.get(i) + " ";
			}
			notice += ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_REPEAT);
			DialogBoxUtil.errorDialog(this, notice);
			return false;
		} else {
			// 如果没有重复值，再查库验证该标签是否可用
			if (labelWorkList_temp.size() > 0) {
				// 修改tunnel时，在此验证工作路径标签
				verifyLabelIsUsedByUpdate(labelWorkList, labelWorkList_temp,
						labelList, 0);
			} else {
				// 新建tunnel时，在此验证工作路径标签
				verifyLabelIsUsedByCreate(labelWorkList, labelList, 0);
			}

			if (labelProtList_temp.size() > 0) {
				// 修改tunnel时，在此验证保护路径标签
				verifyLabelIsUsedByUpdate(labelProtList, labelProtList_temp,
						labelList, 1);
			} else {
				// 新建tunnel时，在此验证保护路径标签
				verifyLabelIsUsedByCreate(labelProtList, labelList, 1);
			}

			if (labelList.size() == 0) {
				// 没有重复值，标签可用，就可下发入库
				return true;
			} else {
				// 没有重复值，但标签不可用
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
		}
	}

	/**
	 * 新建tunnel时验证标签是否可用
	 * 
	 * @param labelUIList
	 * @param labelList
	 * @param labelInfoDao
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private List<Integer> verifyLabelIsUsedByCreate(List<Integer[][]> labelUIList, List<Integer> labelList, int index)throws Exception {
		LabelInfoService_MB labelServiceMB = null;
		try {
			labelServiceMB = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			for (int i = 0; i < labelUIList.size(); i++) {
				int aSiteId = labelUIList.get(i)[0][0];
				int zSiteId = labelUIList.get(i)[1][0];
				int labelValue = labelUIList.get(i)[1][1];
				int aPortId = 0;
				int zPortId = 0;
				if (index == 0) {
					aPortId = Integer.parseInt(siteId_PortId_WorkMap.get(
							aSiteId + "-" + zSiteId).split("-")[0]);
					zPortId = Integer.parseInt(siteId_PortId_WorkMap.get(
							aSiteId + "-" + zSiteId).split("-")[1]);
				} else {
					aPortId = Integer.parseInt(siteId_PortId_ProtMap.get(
							aSiteId + "-" + zSiteId).split("-")[0]);
					zPortId = Integer.parseInt(siteId_PortId_ProtMap.get(
							aSiteId + "-" + zSiteId).split("-")[1]);
				}
				boolean flag = false;
				if (index == 0) {
					flag = labelServiceMB.isUsedLabel(labelValue, aSiteId,"TUNNEL");
					if (flag) {
						flag = labelServiceMB.checkingOutLabel(labelValue, zPortId,
								zSiteId, "TUNNEL");
					}
				} else {
					flag = labelServiceMB.isUsedLabel(labelValue, aSiteId,"TUNNEL");
					if (flag) {
						flag = labelServiceMB.checkingOutLabel(labelValue, zPortId,
								zSiteId, "TUNNEL");
					}
				}
				
				// 如果flag为true，则表示标签可用，否则不可用
				if (!flag) {
					labelList.add(labelValue);
				}
				
				labelValue = labelUIList.get(i)[0][1];
				if (index == 0) {
					flag = labelServiceMB.isUsedLabel(labelValue, zSiteId,"TUNNEL");
					if (flag) {
						flag = labelServiceMB.checkingOutLabel(labelValue, aPortId,
								aSiteId, "TUNNEL");
					}
				} else {
					flag = labelServiceMB.isUsedLabel(labelValue, zSiteId,"TUNNEL");
					if (flag) {
						flag = labelServiceMB.checkingOutLabel(labelValue, aPortId,
								aSiteId, "TUNNEL");
					}
				}
				if (!flag) {
					labelList.add(labelValue);
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(labelServiceMB);
		}
		return labelList;
	}

	/**
	 * 修改tunnel时查询数据库验证标签是否可用
	 * 
	 * @param labelWorkListTemp
	 * @param labelWorkList2
	 * @param conn
	 * @param labelInfoDao
	 * @param labelList
	 * @return
	 * @throws Exception
	 */
	private List<Integer> verifyLabelIsUsedByUpdate(List<Integer[][]> labelUIList, List<Integer[][]> labelListTemp,List<Integer> labelList, int index) throws Exception {
		
		LabelInfoService_MB labelServiceMB = null;
		try {
			labelServiceMB = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			for (int i = 0; i < labelUIList.size(); i++) {
				int aSiteId = labelUIList.get(i)[0][0];
				int zSiteId = labelUIList.get(i)[1][0];
				int aPortId = 0;
				int zPortId = 0;
				if (index == 0) {
					aPortId = Integer.parseInt(siteId_PortId_WorkMap.get(
							aSiteId + "-" + zSiteId).split("-")[0]);
					zPortId = Integer.parseInt(siteId_PortId_WorkMap.get(
							aSiteId + "-" + zSiteId).split("-")[1]);
				} else {
					aPortId = Integer.parseInt(siteId_PortId_ProtMap.get(
							aSiteId + "-" + zSiteId).split("-")[0]);
					zPortId = Integer.parseInt(siteId_PortId_ProtMap.get(
							aSiteId + "-" + zSiteId).split("-")[1]);
				}
				int labelValue = labelUIList.get(i)[1][1];
				int labelValue_temp = labelListTemp.get(i)[1][1];
				boolean flag = false;
				// 如果修改tunnel时，修改标签就查库验证,如果没有修改标签就直接入库
				if (labelValue != labelValue_temp) {
					if (index == 0) {
						flag = labelServiceMB.isUsedLabel(labelValue, aSiteId,"TUNNEL");
						if (flag) {
							flag = labelServiceMB.checkingOutLabel(labelValue,
									zPortId, zSiteId, "TUNNEL");
						}
					} else {
						flag = labelServiceMB.isUsedLabel(labelValue, aSiteId,"TUNNEL");
						if (flag) {
							flag = labelServiceMB.checkingOutLabel(labelValue,
									zPortId, zSiteId, "TUNNEL");
						}
					}
					if (!flag) {
						// 如果flag为true，则表示标签可用，否则不可用
						labelList.add(labelValue);
					}
				}
				
				labelValue = labelUIList.get(i)[0][1];
				labelValue_temp = labelListTemp.get(i)[0][1];
				
				if (labelValue != labelValue_temp) {
					if (index == 0) {
						flag = labelServiceMB.isUsedLabel(labelValue, zSiteId,"TUNNEL");
						if (flag) {
							flag = labelServiceMB.checkingOutLabel(labelValue,
									aPortId, aSiteId, "TUNNEL");
						}
					} else {
						flag = labelServiceMB.isUsedLabel(labelValue, zSiteId,"TUNNEL");
						if (flag) {
							flag = labelServiceMB.checkingOutLabel(labelValue,
									aPortId, aSiteId, "TUNNEL");
						}
					}
					
					if (!flag) {
						labelList.add(labelValue);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(labelServiceMB);
		}
		return labelList;
	}

	/**
	 * 标签重复验证
	 */
	private void verifyLabelIsRepeated(List<Integer[][]> labelList) {
		for (Integer[][] labels : labelList) {
			String aValue = labelMap.get(labels[0][0]);
			String zValue = labelMap.get(labels[1][0]);
			if (aValue == null) {
				aValue = "/";
			}
			if (zValue == null) {
				zValue = "/";
			}
			labelMap.put(labels[0][0], aValue + labels[1][1] + "/");
			labelMap.put(labels[1][0], zValue + labels[0][1] + "/");
		}
	}

	/** 
	 * 向表中插入一行
	 */
	@SuppressWarnings("unchecked")
	private void insertItem(String type, String sbs) throws Exception {
		autoRouteModel = (DefaultTableModel) autoTable.getModel();
		Vector vector = autoRouteModel.getDataVector();
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (type.equals("protect")) {
			for (int i = 0; i < vector.size(); i++) {
				if (vector.elementAt(i).toString().indexOf("protect") != -1) {
					list.add(i);
				}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			autoRouteModel.removeRow(list.get(i));
		}
		Object[] obj = null;
		obj = new Object[] { type, sbs };
		autoRouteModel.insertRow(1, obj);
		autoTable.setModel(autoRouteModel);
	}

	private void addItems(String sbs, String type) throws Exception {
		autoRouteModel = (DefaultTableModel) autoTable.getModel();
		// 清除原数据
		autoRouteModel.getDataVector().clear();
		autoRouteModel.fireTableDataChanged();
		// 更新数据
		Object[] obj = null;
		// String is = null;
		// is = sbs.get(index).toString();
		obj = new Object[] { type, sbs };
		autoRouteModel.addRow(obj);

		autoTable.setModel(autoRouteModel);
	}

	/**
	 * 绑定下拉列表数据
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private void comboBoxData() throws Exception {
		super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PROTECTTYPE");

	}

//	public static AddTunnelPathDialog getDialog() {
//		if (addLspPathsDialog == null) {
//			addLspPathsDialog = new AddTunnelPathDialog(
//					new TunnelBusinessPanel(), true, null);
//		}
//		return addLspPathsDialog;
//	}

//	public static void setAddLspPathsDialog(AddTunnelPathDialog addLspPathsDialog) {
//		AddTunnelPathDialog.addLspPathsDialog = addLspPathsDialog;
//	}

	public Tunnel getTunnel() {
		if (this.tunnel == null) {
			this.tunnel = new Tunnel();
		}
		return this.tunnel;
	}

	public void setProtectTunnelName(String tunnelName) {
		this.jTextField4.setText(tunnelName);
	}


	public JTable getAutoTable() {
		return autoTable;
	}

	public javax.swing.JCheckBox getjCheckBox1() {
		return jCheckBox1;
	}

	public void setjCheckBox1(javax.swing.JCheckBox jCheckBox1) {
		this.jCheckBox1 = jCheckBox1;
	}

	public javax.swing.JCheckBox getjCheckBox2() {
		return jCheckBox2;
	}

	public javax.swing.JTextArea getjTextArea1() {
		return jTextArea1;
	}

	public void setjTextArea1(javax.swing.JTextArea jTextArea1) {
		this.jTextArea1 = jTextArea1;
	}

	public void setjCheckBox2(javax.swing.JCheckBox jCheckBox2) {
		this.jCheckBox2 = jCheckBox2;
	}


	public javax.swing.JTextField getjTextField4() {
		return jTextField4;
	}

	public void setjTextField4(javax.swing.JTextField jTextField4) {
		this.jTextField4 = jTextField4;
	}


	public void setPortInst_A(PortInst portInst) {
		this.portInst_a = portInst;
	}

	public void setPortInst_Z(PortInst portInst) {
		this.portInst_z = portInst;
	}

	public PortInst getPortInst_a() {
		return portInst_a;
	}

	public PortInst getPortInst_z() {
		return portInst_z;
	}

	public javax.swing.JTextField getjTextField1() {
		return jTextField1;
	}

	public void setjTextField1(javax.swing.JTextField jTextField1) {
		this.jTextField1 = jTextField1;
	}


	public void setTunnel(Tunnel tunnel) {
		this.tunnel = tunnel;
	}


	public List<OamInfo> getOamList() {
		return oamList;
	}

	public void setOamList(List<OamInfo> oamList) {
		this.oamList = oamList;
	}

	public Map<Integer, String> getMipList() {
		return mipList;
	}

	public void setMipList(Map<Integer, String> mipList) {
		this.mipList = mipList;
	}

	public List<QosInfo> getQosList() {
		return qosList;
	}

	public void setQosList(List<QosInfo> qosList) {
		this.qosList = qosList;
	}

	/* 判断一个段是顺着的还是反着 */
	public boolean isSegmentS(Segment sg, String name) {
		String aName = sg.getShowSiteAname().trim();
		if (name.equals(aName)) {
			return true;
		} else {
			return false;
		}
	}

	/* 判断第二个段是顺着的还是反着 */
	public boolean isSegmentS(Segment sgNext, Segment sgBefore) {
		String aNext = sgNext.getShowSiteAname().trim();
		String aName = sgBefore.getShowSiteAname().trim();
		String zName = sgBefore.getShowSiteZname().trim();
		if (aNext.equals(aName) || aNext.equals(zName)) {
			return true;
		} else {
			return false;
		}
	}

	/* 添加下拉框的选项 */
	public String addAutoRoute(List<Segment> Segments) {
		StringBuffer sb = new StringBuffer();
		String realSiteAName = equipmentTopology.getSiteA().getCellId().trim();
		String realSiteZName = equipmentTopology.getSiteZ().getCellId().trim();
		sb.append(realSiteAName);
		if (Segments != null) {
			if (Segments.size() == 2) {
				Segment segmentA = Segments.get(0);
				String nameA = segmentA.getShowSiteAname().trim();
				if (nameA.equals(realSiteAName)) {
					sb.append("->").append(segmentA.getShowSiteZname());
				} else {
					sb.append("->").append(segmentA.getShowSiteAname());
				}
			} else if (Segments.size() > 2) {
				String temp = "";
				for (int i = 0; i < Segments.size() - 1; i++) {
					if (i == 0) {
						if (isSegmentS(Segments.get(i), realSiteAName)) {
							temp += "->"
									+ Segments.get(i).getShowSiteZname().trim();
						} else {
							temp += "->"
									+ Segments.get(i).getShowSiteAname().trim();
						}
					} else if (i > 0) {
						if (isSegmentS(Segments.get(i), Segments.get(i - 1))) {
							temp += "->"
									+ Segments.get(i).getShowSiteZname().trim();
						} else {
							temp += "->"
									+ Segments.get(i).getShowSiteAname().trim();
						}
					}
				}

				sb.append(temp);
			}

			sb.append("->").append(realSiteZName);
			return sb.toString();
		}

		return null;
	}

	private int getindex(List<Integer> indexList1, List<Integer> indexList2) {
		int index = 0;
		if (indexList1.size() == 1) {
			if (indexList2.size() == 1) {
				if (indexList1.get(0) == indexList2.get(0)) {
					index = indexList1.get(0);
				}
			} else if (indexList2.size() > 1) {
				int temp = indexList1.get(0);
				if (indexList2.contains(temp)) {
					index = temp;
				}
			} else if (indexList2.size() < 1) {
				index = indexList1.get(0);
			}

		} else if (indexList1.size() > 1) {
			if (indexList2.size() == 1) {
				int temp = indexList2.get(0);
				if (indexList1.contains(temp)) {
					index = temp;
				}
			} else if (indexList2.size() < 1) {
				index = indexList1.get(0);
			} else if (indexList2.size() > 1) {
				for (int i : indexList1) {
					if (indexList2.contains(i)) {
						return i;
					}
				}
			}
		} else if (indexList1.size() < 1) {
			if (indexList2.size() >= 1) {
				index = indexList2.get(0);
			}
		}

		return index;
	}

	private int findShortestWay(List<List<Segment>> llist) {
		int index = 0;
		int min = 0;
		if (llist != null && llist.size() > 0) {
			min = llist.get(0).size();
		}

		for (int i = 1; i < llist.size(); i++) {
			if (llist.get(i) != null && llist.get(i).size() < min) {
				min = llist.get(i).size();
				index = i;
			}
		}

		return index;
	}

	private int findSecondWay(List<List<Segment>> llist, int shortest) {
		int index = 0;
		int min = 0;
		if (llist.size() <= 1) {
			return 0;
		}
		if (shortest == 0) {
			min = llist.get(1).size();
		} else {
			min = llist.get(1).size();
		}

		for (int i = 0; i < llist.size(); i++) {
			if (i == shortest) {
				continue;
			}
			if (llist.get(i) != null && llist.get(i).size() <= min) {
				min = llist.get(i).size();
				index = i;
			}
		}

		return index;
	}

	/*
	 * 获取必经段的所有index集合
	 */
	private List<Integer> getMustSgindex(List<List<Segment>> list,
			List<Segment> sgMust) {
		List<Integer> returnValue = new ArrayList<Integer>();

		List<String> strList = new ArrayList<String>();
		if (sgMust.size() == 0) {
			return returnValue;
		}

		if (list != null && list.size() == 1) {
			if (list.get(0) != null) {
				returnValue.add(0);
				return returnValue;
			}
		}

		// 将路由转换成segment的ID的集合，以便于遍历
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null) {
				List<Segment> sgs = list.get(i);
				StringBuffer sb = new StringBuffer();
				for (Segment sg : sgs) {
					sb.append(sg.getId()).append("-");
				}

				strList.add(sb.toString());
			}
		}

		for (int i = 0; i < strList.size(); i++) {
			String temp = strList.get(i);
			int iCount = 0;
			for (Segment sg : sgMust) {
				if (temp.indexOf(sg.getId() + "") != -1) {
					iCount++;
					if (iCount == sgMust.size()) {
						returnValue.add(i);
					}
				}
			}
		}

		return returnValue;
	}

	public  List<Integer> getContainsMustIndex(List<List<Segment>> list) {
		List<Integer> returnValue = new ArrayList<Integer>();
		List<String> mustList = new ArrayList<String>();
		for (SiteInst site : equipmentTopology.getSiteMust()) {
			mustList.add(site.getSite_Inst_Id() + "");
		}
		List<String> strList = new ArrayList<String>();

		// 将路由转换成segment的网元ID的集合，以便于遍历
		for (int i = 0; i < list.size(); i++) {
			List<Integer> idList = new ArrayList<Integer>();
			if (list.get(i) != null) {
				List<Segment> sgs = list.get(i);
				for (Segment sg : sgs) {
					idList.add(sg.getASITEID());
					idList.add(sg.getZSITEID());
				}
			}

			// 去掉重复的网元ID
			List<Integer> alone = new ArrayList<Integer>();
			for (int k = 0; k < idList.size(); k++) {
				if (!alone.contains(idList.get(k))) {
					alone.add(idList.get(k));
				}
			}

			// 将格式转换成1-2的格式
			StringBuffer sb = new StringBuffer();
			for (int k = 0; k < alone.size(); k++) {
				sb.append(alone.get(k).toString()).append("-");
			}

			strList.add(sb.toString());
		}

		for (int i = 0; i < strList.size(); i++) {
			String temp = strList.get(i);
			int iCount = 0;
			for (String id : mustList) {
				if (temp.indexOf(id) != -1) {
					iCount++;
					if (iCount == mustList.size()) {
						returnValue.add(i);
					}
				}
			}
		}

		return returnValue;
	}

	public String getSiteNameBySiteId(int siteId) throws Exception {
		SiteService_MB siteServiceMB = null;
		String name = "";
		try {
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory
			.newService_MB(Services.SITE);
			name = siteServiceMB.select(siteId).getCellId();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}
		return name;
	}

	public String getRouteNameFromLSP(List<Lsp> LspList, Tunnel tunnelRoute) {
		StringBuffer str = new StringBuffer();
		int aSiteId = tunnelRoute.getaSiteId();
		int zSiteId = tunnelRoute.getzSiteId();
		for(int i = 0;i < LspList.size();i++){
			for (Lsp lsp : LspList) {
				if(lsp.getASiteId() == aSiteId){
					try {
						str.append(getSiteNameBySiteId(lsp.getASiteId()));
						str.append("->");
						aSiteId = lsp.getZSiteId();
						break;
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		}
		try {
			str.append(getSiteNameBySiteId(zSiteId));
		} catch (Exception e1) {
			ExceptionManage.dispose(e1, this.getClass());
		}
		return str.toString();
	}

	public DefaultTableModel getDataModel(Tunnel tunnel) {
		LspInfoService_MB lspParticularServiceMB = null;
		List<Lsp> worklsp = new ArrayList<Lsp>();
		List<Lsp> protectlsp = new ArrayList<Lsp>();
		int protectId = tunnel.getProtectTunnelId();
		try {
			lspParticularServiceMB = (LspInfoService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.LSPINFO);

			worklsp.addAll(lspParticularServiceMB.selectBytunnelId(tunnel
					.getTunnelId()));
			if (protectId != 0) {
				protectlsp.addAll(lspParticularServiceMB
						.selectBytunnelId(protectId));
			}
		} catch (Exception e1) {
			ExceptionManage.dispose(e1, this.getClass());
		} finally {
			UiUtil.closeService_MB(lspParticularServiceMB);
		}
		autoRouteModel = (DefaultTableModel) autoTable.getModel();
		// 清除原数据
		autoRouteModel.getDataVector().clear();
		autoRouteModel.fireTableDataChanged();
		String autoStr = getRouteNameFromLSP(worklsp, tunnel);
		String proStr = "";
		if (protectId != 0) {
			proStr = getRouteNameFromLSP(protectlsp, tunnel.getProtectTunnel());
		}

		Object[] obj0 = null;
		Object[] obj1 = null;
		if (!autoStr.equals("")) {
			obj0 = new Object[] { "work", autoStr };
			autoRouteModel.addRow(obj0);

			if (!proStr.equals("") && protectId != 0) {
				obj1 = new Object[] { "protect", proStr };
				autoRouteModel.addRow(obj1);
			}
		}

		return autoRouteModel;
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() throws Exception {
		Dimension dimension = null;
 		if(ResourceUtil.language.equals("zh_CN")){
 			 dimension = new Dimension(1300, 800);
		}else{
			 dimension = new Dimension(1300, 800);
		}
		this.setSize(dimension);
		this.setMinimumSize(dimension);

		// 每次新建的时候将必经清空
		equipmentTopology.getSiteMust().clear();	
		equipmentTopology.setMust(false);
		equipmentTopology.setSgMust(false);
		equipmentTopology.getSgMust().clear();
		equipmentTopology.setIsproSgMust(false);
		equipmentTopology.getSgproMust().clear();
		jLblAport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WORK_ASIDE_PORT));
		jLblZport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WORK_ZSIDE_PORT));
		jLblProAport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROASIDE_NAMW));
		jLblProZport = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROZSIDE_NAMW));

		this.lblMessage = new JLabel();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel3 = new javax.swing.JPanel();
		lblName = new javax.swing.JLabel(
				ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		jLabel4 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ASIDE_NAMW));
		cmbANe = new JComboBox();
		cmbZNe = new JComboBox();
		setcmbSite(cmbANe);
		setcmbSite(cmbZNe);
		cmbAport = new JComboBox();
		cmbZport = new JComboBox();
		cmbProAport = new JComboBox();
		cmbProZport = new JComboBox();
		setcmbPort(0,cmbAport);
		setcmbPort(0,cmbZport);
		setcmbPort(0,cmbProAport);
		setcmbPort(0,cmbProZport);
		cmbProAport.setEnabled(false);
		cmbProZport.setEnabled(false);
		jLabel5 = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ZSIDE_NAMW));
		jTextArea1 = new javax.swing.JTextArea();
		jCheckBox1 = new javax.swing.JCheckBox(
				ResourceUtil.srcStr(StringKeysLbl.LBL_IS_ACTIVATED));
		jcbPW = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_PW));
		btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		jCheckBox2 = new javax.swing.JCheckBox(
				ResourceUtil.srcStr(StringKeysLbl.LBL_IS_CREATE_OPPOSITE));
		jCheckBox2.setSelected(true);
		// jButton3 = new
		// javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		jTextField4 = new PtnTextField();
		jTextField4.setEditable(false);
		pathCheckButton = new javax.swing.JButton(
				ResourceUtil.srcStr(StringKeysBtn.BTN_PATH_INSPECT));
//		qosConfigButton = new javax.swing.JButton(
//				ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
//		oamConfigButton = new javax.swing.JButton(
//				ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		qosConfigButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysObj.STRING_QOS_CONFIG));
		oamConfigButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_OAM_CONFIGOAM));
		/* add auto route by dxh */
		this.lblAutoroute = new JLabel(
				ResourceUtil.srcStr(StringKeysLbl.LBL_SELECT_AUTOrOUTER));
		this.autoTable = new JTable();
		this.autoPane = new JScrollPane();
		this.initAutoRouteTable();
		this.AutoBtn = new JButton(
				ResourceUtil.srcStr(StringKeysBtn.BTN_WORK_ROUTER));
		this.AutoProBtn = new JButton(
				ResourceUtil.srcStr(StringKeysBtn.BTN_PROT_ROUTER));
		// add reset route by guoqc
		this.resetBtn = new JButton(
				ResourceUtil.srcStr(StringKeysBtn.BTN_RESET_ROUTER));
		this.lblLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LABELINFO));
		// 添加重置标签
		this.labResetBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_RESETLABEL));
		this.labSaveBtn = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVELABEL));
		this.initLabelTable();
		UiUtil.jTableColumsHide(labelTable, 1);
//		lblqos = new JLabel("QoS");
//		lbloam = new JLabel("OAM");
		txtQos = new PtnTextField();
		txtQos.setEditable(false);
		txtOam = new PtnTextField();
		txtOam.setEditable(false);
		jTextField1 = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH,
				this.lblMessage, this.btnSave, this);
		this.lblwaitTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
		this.txtDelayTime = new PtnSpinner(2500, 0, 100, ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
//		this.txtWaitTime = new PtnSpinner(10, 0, 1,
//				ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		this.txtWaitTime = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
		this.chkAps = new JCheckBox(
				ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		this.autoNamingButton = new JButton(
				ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		this.protectBack = new JCheckBox(
				ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
		jCheckBoxSNCP = new JCheckBox("SNCP");
		jComboBoxSNCP = new JComboBox();
		this.cmbType = new JComboBox();
		this.txtWaitTime.getTxt().setText("");
		this.txtWaitTime.setEnabled(false);

		txtDelayTime.getTxt().setText("");
		txtDelayTime.setEnabled(false);
		this.chkAps.setEnabled(false);
		this.AutoProBtn.setEnabled(false);
		this.protectBack.setEnabled(false);
		this.lblRotateWay = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATEWAY));
		this.cmbRotateWay = new JComboBox();
		List<String> itemList = new ArrayList<String>();
		itemList.add("SD");
		itemList.add("SF");
		this.setCmbItem(this.cmbRotateWay, itemList);
		this.lblRotateLocation = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATELOCATION));
		this.cmbRotateLocation = new JComboBox();
		itemList.clear();
		itemList.add("A_NE");
		itemList.add("Z_NE");
		this.setCmbItem(this.cmbRotateLocation, itemList);
		this.lblRotateMode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATEMODE));
		this.cmbRotateMode = new JComboBox();
		itemList.clear();
		itemList.add("MANUAL");
		itemList.add("AUTO");
		this.setCmbItem(this.cmbRotateMode, itemList);
		this.lblTnpLayer = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TNPLAYER));
		this.spinnerTnpLayer = new PtnSpinner(100, 1, 1, ResourceUtil.srcStr(StringKeysLbl.LBL_TNPLAYER));
		this.spinnerTnpLayer.getTxt().setText("1");
		this.lblRotateThreshold = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATETHRESHOLD));
		this.spinnerRotateThreshold = new PtnSpinner(100, 1, 1, ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATETHRESHOLD));
		this.spinnerRotateThreshold.getTxt().setText("1");
		this.cmbRotateWay.setEnabled(false);
		this.cmbRotateLocation.setEnabled(false);
		this.cmbRotateMode.setEnabled(false);
		this.spinnerTnpLayer.setEnabled(false);
		this.spinnerRotateThreshold.setEnabled(false);
		
		//新增外层vlan
		this.vlan = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
		this.vlanButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		this.vlan_backup = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_BACKUP));
		this.vlanButton_backup = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		this.vlanButton_backup.setEnabled(false);
		//新增mac
		this.sourceMacLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC)); 
		this.sourceMacText = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, lblMessage, btnSave, this);
		this.sourceMacText.setText("00-00-00-00-00-01");
		this.endMacLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PURPOSE_MAC));
		this.endMacText = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, lblMessage, btnSave, this);
		this.endMacText.setText("00-00-00-00-00-01");
		//新增备用mac
		this.sourceMacLabel_backup = new JLabel(ResourceUtil.srcStr(StringKeysLbl.lBL_SOURCE_MAC_BACKUP)); 
		this.sourceMacText_backup = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, lblMessage, btnSave, this);
		this.sourceMacText_backup.setText("00-00-00-00-00-01");
		this.sourceMacText_backup.setEnabled(false);
		this.endMacLabel_backup = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PURPOSE_MAC_BACKUP));
		this.endMacText_backup = new PtnTextField(true, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, lblMessage, btnSave, this);
		this.endMacText_backup.setText("00-00-00-00-00-01");
		this.endMacText_backup.setEnabled(false);
		inBandwidthControl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INBANDWIDTH));
		inBandwidthControlCheckBox = new JCheckBox();
		outBandwidthControl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUTBANDWIDTH));
		outBandwidthControlCheckBox = new JCheckBox();
		pathCheckButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					tunnelAction.pathCheckButtonActionPerformed(evt,
							AddTunnelPathDialog.this);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});

		// 计算保护路由
		AutoProBtn.addActionListener(new ActionListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				clearProtectRoute();
				if (equipmentTopology.getSiteA() != null&& equipmentTopology.getSiteZ() != null) {
					// 将上一次保护路由的颜色情况，重置为绿色
					if (getProSg().size() > 0) {
						try {
							drawTopoByPassLinks(getProSg(), Color.GREEN);
						} catch (Exception e2) {
							ExceptionManage.dispose(e2, this.getClass());
						}
					}

					if ((cmbType.getSelectedIndex() == 1 || cmbType.getSelectedIndex() == 2)) {
						if(!isProtectportexist())
						{
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
							return;
						}
						if(isProtectPort())
						{
							boolean flag = getworksgList("protect",equipmentTopology);
							if(!flag)
							{
								DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
								return;
							}
						}
						List<List<Segment>> proList = tunnelAction.autoRoute("protect",AddTunnelPathDialog.this);
						if (proList == null) {
							return;
						}
						if(proList.size()==0)
						{
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
							return;
						}
						List<Integer> mustsg = getMustSgindex(proList,equipmentTopology.getSgproMust());
						List<Integer> mustsite = getContainsMustIndex(proList);
						int index = getindex(mustsg, mustsite);
						if (mustsg.size() < 1 && mustsite.size() < 1) {
							index = proList.size() - 1;
						}

						setProSg(proList.get(index));
					}

					try {
						drawTopoByPassLinks(getProSg(), Color.YELLOW);
					} catch (Exception e2) {
						ExceptionManage.dispose(e2, this.getClass());
					}
					String itemStr = addAutoRoute(getProSg());
					try {
						insertItem("protect", itemStr);
					} catch (Exception e1) {
						ExceptionManage.dispose(e1, this.getClass());
					}
					// 计算完路由就自动分配标签
					try {
						// 先判断是否已分配标签，如果已经分配，则直接显示标签
						if (labelProtList.size() == 0) {
							matchingLabel(1);
						} else {
							labelUI(1);
						}
					} catch (Exception e1) {
						ExceptionManage.dispose(e1, this.getClass());
					}
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_AZ_CONFIG_BEFORE));
				}
			}
		});

		/* 新增自动路由入口函数 add by dxh */
		AutoBtn.addActionListener(new ActionListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				if (equipmentTopology.getSiteA() != null && equipmentTopology.getSiteZ() != null) {
					clearData();
					if(!isPortexist())
					{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
						return;
					}
					// 如果有必经网元，找出其index
					if (equipmentTopology.isMust()) {
						// 处理即有必经网元，又有必经段
						if (equipmentTopology.isSgMust()) {
							setWorksgMust(true);
						} else {
							setWorksgMust(false) ;
						}

						setWorkMust (true) ;
					} else {
						// 处理没有必经网元，只有必经段
						if (equipmentTopology.isSgMust()) {
							setWorksgMust(true);
						} else {
							setWorksgMust(false) ;
						}

						setWorkMust (false) ;
					}
					if(isWorkPort())
					{
						boolean flag = getworksgList("work", equipmentTopology);
						if(!flag)
						{
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
							return;
						}
					}
					list = tunnelAction.autoRoute("work",AddTunnelPathDialog.this);
					
					if (list == null) {
						return;
					}
					if(list.size()==0)
					{
						DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_NO_ROUTE));
						return;
					}
					// 找出list中最短的路径
					shortestIndex = findShortestWay(list);
					secondIndex = findSecondWay(list, shortestIndex);

					ExceptionManage.infor("currengt index is:" + shortestIndex,this.getClass());
					// 如果有必经网元，找出其index
					if (equipmentTopology.isMust()) {
						// 处理即有必经网元，又有必经段
						if (equipmentTopology.isSgMust()) {
							List<Integer> indexList1 = getMustSgindex(list,equipmentTopology.getSgMust());
							List<Integer> indexList2 = getContainsMustIndex(list);
							int index = getindex(indexList1, indexList2);
							if (index == NOINDEX) {
								return;
							}

							setWorkSg (list.get(index));
						} else {
							int index = 0;
							List<Integer> indexList = getContainsMustIndex(list);
							if (indexList != null && indexList.size() == 1) {
								index = indexList.get(0);
							} else {
								index = 0;
							}
							setWorkSg (list.get(index));
						}
					} else {
						// 如果有必经段，找出其index
						if (equipmentTopology.isSgMust()) {
							int index = 0;
							List<Integer> indexList = getMustSgindex(list,
									equipmentTopology.getSgMust());
							if (indexList != null && indexList.size() >= 1) {
								index = indexList.get(0);
							} else {
								index = 0;
							}
							setWorkSg (list.get(index));
						} else {
							setWorkSg (list.get(shortestIndex));
						}
					}

					try {
						drawTopoByPassLinks(workSg, Color.BLUE);
					} catch (Exception e2) {
						ExceptionManage.dispose(e2, this.getClass());
					}
					String itemStr = addAutoRoute(workSg);
					try {
						addItems(itemStr, "work");
					} catch (Exception e1) {
						ExceptionManage.dispose(e1, this.getClass());
					}

					// 计算完路由就自动分配标签
					try {
						// 先判断是否已分配标签，如果已经分配，则直接显示标签
						if (labelWorkList.size() == 0) {
							matchingLabel(0);
						} else {
							labelUI(0);
						}
					} catch (Exception e1) {
						ExceptionManage.dispose(e1, this.getClass());
					}
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_AZ_CONFIG_BEFORE));
				}
			}

		});

		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String action = resetBtn.getActionCommand();
				// 重置路由，全部清空
				if (action.equals("work")) {
					cmbANe.setSelectedIndex(0);
					cmbZNe.setSelectedIndex(0);
					cmbAport.setSelectedIndex(0);
					cmbZport.setSelectedIndex(0);
					cmbProAport.setSelectedIndex(0);
					cmbProZport.setSelectedIndex(0);
					equipmentTopology.setSiteA(null);
					equipmentTopology.setSiteZ(null);
					
				}

//				EquipmentTopology.isMust = false;
//				EquipmentTopology.SiteMust.clear();
//				EquipmentTopology.isSgMust = false;
//				EquipmentTopology.sgMust.clear();
//				EquipmentTopology.isproSgMust = false;
//				EquipmentTopology.sgproMust.clear();
				equipmentTopology.setMust(false);
				equipmentTopology.getSiteMust().clear();
				equipmentTopology.setSgMust(false);
				equipmentTopology.getSgMust().clear();
				equipmentTopology.setIsproSgMust(false);
				equipmentTopology.getSgproMust().clear();

				equipmentTopology.setLinkColor(Color.GREEN);
				// 清除数据
				clearData();

				// 清除AZ提示
				if (equipmentTopology.getElementA() != null) {
					equipmentTopology.removeBusinessObjct(
							equipmentTopology.getElementA(), "");
					equipmentTopology.setElementA(null) ;
				}
				if (equipmentTopology.getElementZ() != null) {
					equipmentTopology.removeBusinessObjct(
							equipmentTopology.getElementZ(), "");
					equipmentTopology.setElementZ(null) ;
				}
				if (equipmentTopology.getElementM() != null) {
					for (Element element : equipmentTopology.getElementM()) {
						equipmentTopology.removeBusinessObjct(element, "");
					}

					equipmentTopology.getElementM().clear();
				}

			}
		});

		btnSave.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// 保存之前先做路由和标签验证
//				boolean flag = verifyRouter();
//				if (flag) {
					tunnelAction.createTunnel(evt, AddTunnelPathDialog.this);
//				}			
			}

			@Override
			public boolean checking() {
				try {
//					return tunnelAction.checking(AddTunnelPathDialog.this);
					if(tunnelAction.checking(AddTunnelPathDialog.this) && verifyRouter()){
						return true;
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, getClass());
				}
				return false;
				
			}
		});
		qosConfigButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tunnelAction.qosConfigButtonActionPerformed(evt,
						AddTunnelPathDialog.this);
				int sum = 0;

				if (qosList.size() != 0) {
					for (int i = 0; i < qosList.size(); i++) {
						sum += qosList.get(i).getCir();
					}

					txtQos.setText("totalCir=" + sum);
				} else {
					txtQos.setText("totalCir=" + 0);
				}
			}
		});

		oamConfigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Code code_type = (Code) ((ControlKeyValue) getCmbType()
						.getSelectedItem()).getObject();
				if (tunnel == null)
				{
					if(oamConfigCheck(code_type))
					{
						return;
					}
				}else {
					tunnel.setTunnelType(code_type.getId() + "");
					tunnelAction.oamConfigButtonActionPerformed(evt,AddTunnelPathDialog.this);
//					tunnel.setOamList(oamList);
					// tunnel.setOamList_(oamList_protect);
					if (oamList.size() != 0) {
						for (OamInfo oamInfo : oamList) {
							if (null != oamInfo.getOamMep()) {
								if(null!=oamInfo.getOamMep().getMegIcc()&&!"".equals(oamInfo.getOamMep().getMegIcc())){
									txtOam.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
								}else{
									txtOam.setText("megid=" + oamInfo.getOamMep().getMegId());
								}
							}
						}
					} else {
						txtOam.setText("megid=0");
					}
				}

			}
		});
		// 创建个数控件 kk
		this.lblNumber = new JLabel(
				ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		this.ptnSpinnerNumber = new PtnSpinner(1, 1, 1000, 1);

		jCheckBoxSNCP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (jCheckBoxSNCP.isSelected()) {
					if (workSg == null || !(workSg.size() > 0)) {
						jCheckBoxSNCP.setSelected(false);
						DialogBoxUtil.errorDialog(
								AddTunnelPathDialog.this,
								ResourceUtil
										.srcStr(StringKeysTip.TIP_PLEASE_WORKROUTER));
					} else {
						DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) jComboBoxSNCP
								.getModel();
						if (!(defaultComboBoxModel.getSize() > 1)) {
							addSNCPtunnel();
						}
					}

				}
			}
		});
		jComboBoxSNCP.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					ControlKeyValue controlKeyValue = null;
					Tunnel tunnel = null;
					try {
						controlKeyValue = (ControlKeyValue) evt.getItem();
						tunnel = (Tunnel) controlKeyValue.getObject();
						if (tunnel.getTunnelId() > 0) {
							List<Integer> integers = getSiteIds();
							boolean b = false;
							for (Integer integer : integers) {
								if (integer == tunnel.getASiteId()) {
									b = true;
									break;
								} else if (integer == tunnel.getZSiteId()) {
									b = false;
									break;
								}
							}
							if (b) {
								matchingForSNCP(tunnel.getASiteId(),
										tunnel.getZSiteId());
							} else {
								matchingForSNCP(tunnel.getZSiteId(),
										tunnel.getASiteId());
							}

						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});
		if(ResourceUtil.language.equals("zh_CN")){
			this.jSplitPane1.setDividerLocation(470);
		}else{
			this.jSplitPane1.setDividerLocation(750);
		}
	}
	
	private void setCmbItem(JComboBox cmb, List<String> itemList) {
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			for(String value: itemList){
				defaultComboBoxModel.addElement(value);
			}
			cmb.setModel(defaultComboBoxModel);
		}catch(Exception e){
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 通过已选中的端口获取工作和保护的必经路径
	 * @param type
	 * @param equip
	 */
	private boolean getworksgList(String type, EquipmentTopology equip)
	{
		List<Segment> segList = new ArrayList<Segment>();
		//必须两端都有必经段，如果只有一端的段，返回失败
		int icount = 0;
		if(type.equals("work"))
		{
			if(getAWorkSegment() != null)
			{
				segList.add(getAWorkSegment());
				icount++;
			}
			if(getZWorkSegment() != null)
			{
				segList.add(getZWorkSegment());
				icount++;
			}
			equip.setSgMust(true);
			equip.setSgMust(segList);
		}
		else if(type.equals("protect"))
		{
			if(getAProSegment() != null)
			{
				segList.add(getAProSegment());
				icount++;
			}
			if(getZProSegment() != null)
			{
				segList.add(getZProSegment());
				icount++;
			}	
			
			equip.setIsproSgMust(true);
			equip.setSgproMust(segList);
		}
		
		if(icount < 2)
		{
			return false;
		}
		
		if(segList.size() == 2)
		{
			ControlKeyValue objA = (ControlKeyValue)cmbANe.getSelectedItem();
			ControlKeyValue objZ = (ControlKeyValue)cmbZNe.getSelectedItem();
			int siteA = Integer.parseInt(objA.getId());
			int siteZ = Integer.parseInt(objZ.getId());
			if(segList.get(0).getId() != segList.get(1).getId())
			{		
				if((segList.get(0).getASITEID()==siteA || segList.get(0).getZSITEID() == siteA) && (segList.get(0).getASITEID()==siteZ || segList.get(0).getZSITEID() == siteZ))
				{
					
					if((segList.get(1).getASITEID()==siteA || segList.get(1).getZSITEID() == siteA) && (segList.get(1).getASITEID()==siteZ || segList.get(1).getZSITEID() == siteZ))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}

	/**
	 * Oam配置验证
	 */
	protected boolean oamConfigCheck(Code code_type) {
		boolean flag = false;
		
		if(("2".equals(code_type.getCodeValue()) || "3".equals(code_type.getCodeValue())) && 0 == getProSg().size()){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_PROTROUTER));
			flag = true;
			return flag ;
		}
		if(null==tunnel){
			if(0 == workSg.size()){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_WORKROUTER));
				flag = true;
				return flag ;
			}
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
			flag = true;
			return flag;
		}
		return flag;
	}

	/**
	 * 当tunnel类型为SNCP时，添加符合条件的SNCP保护 pc
	 */
	public void addSNCPtunnel() {
		TunnelService_MB tunnelServiceMB = null;
		List<Tunnel> allTunnels = null;
		List<Integer> tunnelSiteIdS = null;
		List<Integer> integers = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory
					.newService_MB(Services.Tunnel);
			if (equipmentTopology.getSiteA() != null
					&& equipmentTopology.getSiteZ() != null) {
				allTunnels = tunnelServiceMB.selectSNCPtunnel(
						equipmentTopology.getSiteA().getSite_Inst_Id(),
						equipmentTopology.getSiteZ().getSite_Inst_Id());
			}
			defaultComboBoxModel = new DefaultComboBoxModel();
			tunnelSiteIdS = new ArrayList<Integer>();
			tunnelSiteIdS = getSiteIds();// 按顺序获得当前tunnel路径包含的siteID集合
			defaultComboBoxModel.removeAllElements();
			defaultComboBoxModel.addElement(new ControlKeyValue(null + "", "",
					new Tunnel()));
			if (allTunnels != null && allTunnels.size() > 0) {
				for (Tunnel tunnel : allTunnels) {// 获得符合条件的SNCP保护
					integers = tunnelServiceMB.getSiteIds(tunnel, false);
					if (checkSNCPtunnel(tunnelSiteIdS, integers)) {
						defaultComboBoxModel.addElement(new ControlKeyValue(
								tunnel.getTunnelId() + "", tunnel
										.getTunnelName()
										+ "/"
										+ tunnel.getASiteId()
										+ "--"
										+ tunnel.getZSiteId(), tunnel));
					}
				}
			}
			jComboBoxSNCP.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}

	/**
	 * 按顺序获得当前tunnel路径包含的siteID集合
	 * 
	 * @return
	 */
	public List<Integer> getSiteIds() {
		List<Integer> integers = new ArrayList<Integer>();
		for (int i = 0; i < workSg.size(); i++) {
			Segment segment = workSg.get(i);
			if (i == 0) {
				if (segment.getASITEID() == equipmentTopology.getSiteA()
						.getSite_Inst_Id()) {
					integers.add(segment.getASITEID());
					integers.add(segment.getZSITEID());
				} else {
					integers.add(segment.getZSITEID());
					integers.add(segment.getASITEID());
				}
			} else {
				if (segment.getASITEID() == integers.get(i)) {
					integers.add(segment.getZSITEID());
				} else if (segment.getZSITEID() == integers.get(i)) {
					integers.add(segment.getASITEID());
				}
			}
		}
		return integers;
	}

	/**
	 * 检测sncp是否符合条件 pc
	 * 
	 * @param tunnelSiteIdS
	 * @param integers
	 * @return
	 */
	public boolean checkSNCPtunnel(List<Integer> tunnelSiteIdS,
			List<Integer> integers) {
		boolean b = true;
		boolean contain = false;
		if ((tunnelSiteIdS.size() - integers.size()) < 2) {
			return false;
		}
		for (Integer integer : integers) {
			for (Integer integer2 : tunnelSiteIdS) {
				if (integer2 == integer) {
					contain = true;
					break;
				}
			}
			if (!contain) {
				return false;
			}
		}

		return b;
	}

	protected boolean verifyRouter() {
		// 判断如果没有配置标签，则提示用户
		boolean flag = false;
		try {
			Code code_type = (Code) ((ControlKeyValue) this.getCmbType().getSelectedItem()).getObject();
			
			if ("2".equals(code_type.getCodeValue()) || "3".equals(code_type.getCodeValue())) {
				if (this.getLabelWorkList().size() == 0) {
					// 提示：请计算工作路由
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_WORKROUTER));
					// isSuccessed = 0;
					return false;
				}
				if (this.getLabelProtList().size() == 0) {
					// 提示：请计算保护路由
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_PROTROUTER));
					// isSuccessed = 0;
					return false;
				}
			} else {
				if (this.getLabelWorkList().size() == 0) {
					// 提示：请计算工作路由
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PLEASE_WORKROUTER));
					return false;
				}
			}
			
			if (labelTable.getSelectedColumn() > 1 && labelTable.isEditing()) {
				// 验证输入标签是否合法
				if(!verifyLabelIsNumber()){
					return false;
				}
			} else {
//				flag = saveLabel();
				if(!saveLabel()){
					return false;
				}
			}
			// 选择sncp类型时，是否包含sncp保护
			if (jCheckBoxSNCP.isSelected()) {
				Tunnel tunnel = (Tunnel) ((ControlKeyValue) jComboBoxSNCP.getSelectedItem()).getObject();
				if (!(tunnel.getTunnelId() > 0)) {
					DialogBoxUtil.errorDialog(this,ResourceUtil.srcStr(StringKeysTip.TIP_NO_SNCP));
					return false;
				}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return flag;
	}

	private void clearProtectRoute()
	{
		List<Segment> pro = getProSg();
		if (pro != null && pro.size() > 0) {
			try {
				drawTopoByPassLinks(pro, Color.GREEN);
			} catch (Exception e2) {
				ExceptionManage.dispose(e2, this.getClass());
			}
			getProSg().clear();
		}
		siteId_PortId_ProtMap.clear();
		labelProtList.clear();
	}
	private void clearData() {
		if (list != null && list.size() > 0) {
			list.clear();
		}
		if (workSg != null && workSg.size() > 0) {
			// 此时如果已经有路由，将其颜色变绿
			try {
				drawTopoByPassLinks(workSg, Color.GREEN);
			} catch (Exception e2) {
				ExceptionManage.dispose(e2, this.getClass());
			}
			workSg.clear();
		}
		// 清除原数据
		tableModel = (DefaultTableModel) labelTable.getModel();
		tableModel.getDataVector().clear();
		tableModel.fireTableDataChanged();
		labelWorkList.clear();

		labelMap.clear();
		siteId_PortId_WorkMap.clear();

		autoRouteModel = (DefaultTableModel) autoTable.getModel();
		autoRouteModel.getDataVector().clear();
		autoRouteModel.fireTableDataChanged();
	}

	private boolean linkSimilarWithSegment(Link link, Segment seg2) {
		if (link.getUserObject() instanceof Segment) {
			Segment seg1 = (Segment) link.getUserObject();
			if ((seg1.getAPORTID() == seg2.getAPORTID()
					&& seg1.getASITEID() == seg2.getASITEID()
					&& seg1.getZPORTID() == seg2.getZPORTID() && seg1
					.getZSITEID() == seg2.getZSITEID())
					|| (seg1.getAPORTID() == seg2.getZPORTID()
							&& seg1.getASITEID() == seg2.getZSITEID()
							&& seg1.getZPORTID() == seg2.getAPORTID() && seg1
							.getZSITEID() == seg2.getASITEID()))
				return true;
		} else if (link.getUserObject() instanceof Lsp) {
			Lsp lspp = (Lsp) link.getUserObject();
			if ((lspp.getAPortId() == seg2.getAPORTID()
					&& lspp.getASiteId() == seg2.getASITEID()
					&& lspp.getZPortId() == seg2.getZPORTID() && lspp
					.getZSiteId() == seg2.getZSITEID())
					|| (lspp.getAPortId() == seg2.getZPORTID()
							&& lspp.getASiteId() == seg2.getZSITEID()
							&& lspp.getZPortId() == seg2.getAPORTID() && lspp
							.getZSiteId() == seg2.getASITEID()))
				return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void drawTopoByPassLinks(List<Segment> segmentList, Color color)throws Exception {
		TDataBox box = equipmentTopology.getSegmentTopo().getBox();
		List<Element> elementList = box.getAllElements();
		TopologyUtil topologyUtil=new TopologyUtil();
		for (Segment obj : segmentList) {
			for (int i = elementList.size() - 1; i >= 0; i--) {
				Element element = elementList.get(i);
				if (element instanceof Link
						&& linkSimilarWithSegment((Link) element, obj)) {
					((Link) element).setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
					((Link) element).putLinkColor(color);
					Segment segment = (Segment) element.getUserObject();
					topologyUtil.setLinkWidth(segment, ((Link) element));
					((Link) element).putLinkFlowingWidth(3);
					break;
				}
			}
		}
	}

	@SuppressWarnings("serial")
	private void initAutoRouteTable() {
		this.autoTable = new JTable();
		autoTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM),
						ResourceUtil.srcStr(StringKeysObj.STRING_ROUTER) }) {
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] { java.lang.Object.class,
					java.lang.Object.class };

			@Override
			@SuppressWarnings({ "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0 || columnIndex == 1)
					return false;
				return true;
			}
		});

		autoTable.getTableHeader().setResizingAllowed(true);
		autoTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = autoTable.getColumnModel().getColumn(0);
		c.setPreferredWidth(55);
		c.setMaxWidth(60);
		c.setMinWidth(30);

		this.autoPane = new JScrollPane();
		this.autoPane.setViewportView(this.autoTable);

	}

	@SuppressWarnings("serial")
	private void initLabelTable() {
		this.labelTable = new JTable();
		labelTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "",
						ResourceUtil.srcStr(StringKeysObj.ORDER_NUM),
						ResourceUtil.srcStr(StringKeysObj.STRING_A_LABEL),
						ResourceUtil.srcStr(StringKeysObj.STRING_Z_LABEL) }) {
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] { java.lang.Object.class,
					java.lang.Object.class, java.lang.Object.class,
					java.lang.Object.class };

			@Override
			@SuppressWarnings({ "unchecked"})
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0 || columnIndex == 1)
					return false;
				return true;
			}
		});

		labelTable.getTableHeader().setResizingAllowed(true);
		labelTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = labelTable.getColumnModel().getColumn(1);
		c.setPreferredWidth(30);
		c.setMaxWidth(30);
		c.setMinWidth(30);

		this.labelScrollPane = new JScrollPane();
		this.labelScrollPane.setViewportView(this.labelTable);

	}

	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jSplitPane1.setLeftComponent(this.jPanel3);
		GridBagLayout layout = new GridBagLayout();
		if(!ResourceUtil.language.equals("zh_CN")){
			layout.columnWidths = new int[] { 250, 120, 80, 120 };
			layout.columnWeights = new double[] { 0.0, 0, 0, 0 };
		}else{
			layout.columnWidths = new int[] { 30, 120, 80, 120 };	
			layout.columnWeights = new double[] { 0, 0, 0, 0 };
		}
		layout.rowHeights = new int[] { 25, 30, 30, 30, 30, 30, 30, 30, 30, 30,30, 30, 30, 30, 30, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, 0, 0, 0 };
		this.jPanel3.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		// 第0行 错误消息*/
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel3.add(this.lblMessage);

		// 第一行 名称 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblName, c);
		this.jPanel3.add(lblName);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(jTextField1, c);
		this.jPanel3.add(jTextField1);
		c.gridx = 3;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 55);
		layout.addLayoutComponent(autoNamingButton, c);
		this.jPanel3.add(autoNamingButton);

		// 第二行 类型 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.lblType, c);
		this.jPanel3.add(this.lblType);
		c.gridx = 1;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.cmbType, c);
		this.jPanel3.add(this.cmbType);

		//  A端NE 
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.jLabel4, c);
		this.jPanel3.add(this.jLabel4);
		c.gridx = 1;
		layout.addLayoutComponent(this.cmbANe, c);
		this.jPanel3.add(this.cmbANe);

		// Z端NE
		c.gridx = 2;
		c.gridy = 3;
		layout.setConstraints(this.jLabel5, c);
		this.jPanel3.add(this.jLabel5);
		c.gridx = 3;
		layout.addLayoutComponent(this.cmbZNe, c);
		this.jPanel3.add(this.cmbZNe);

		//  A端端口 
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.jLblAport, c);
		this.jPanel3.add(this.jLblAport);
		c.gridx = 1;
		layout.addLayoutComponent(this.cmbAport, c);
		this.jPanel3.add(this.cmbAport);

		// Z端端口
		c.gridx = 2;
		c.gridy = 4;
		layout.setConstraints(this.jLblZport, c);
		this.jPanel3.add(this.jLblZport);
		c.gridx = 3;
		layout.addLayoutComponent(this.cmbZport, c);
		this.jPanel3.add(this.cmbZport);
		
		//  A端端口 
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.jLblProAport, c);
		this.jPanel3.add(this.jLblProAport);
		c.gridx = 1;
		layout.addLayoutComponent(this.cmbProAport, c);
		this.jPanel3.add(this.cmbProAport);

		// Z端端口
		c.gridx = 2;
		c.gridy = 5;
		layout.setConstraints(this.jLblProZport, c);
		this.jPanel3.add(this.jLblProZport);
		c.gridx = 3;
		layout.addLayoutComponent(this.cmbProZport, c);
		this.jPanel3.add(this.cmbProZport);

		// 第5行
		int row = 6; // 行号
		// 验证是否需要添加控件
		addComponentList = this.addComponent();
		if (null != addComponentList) {
			// 遍历集合。 把所有的控件添加到界面中
			for (Component[] components : addComponentList) {
				// 添加控件 数组0是label 数组1是combox 数组2是button
				c.gridx = 0;
				c.gridy = row;
				c.gridwidth = 1;
				layout.setConstraints(components[0], c);
				this.jPanel3.add(components[0]);
				c.gridx = 1;
				c.gridwidth = 2;
				layout.addLayoutComponent(components[1], c);
				this.jPanel3.add(components[1]);
				c.gridx = 3;
				c.gridwidth = 1;
				layout.addLayoutComponent(components[2], c);
				this.jPanel3.add(components[2]);
				row++;
			}
		}

		/* 新增自动路由 */
		c.gridx = 0;
		c.gridy = row++;
		c.gridwidth = 1;
		layout.setConstraints(this.lblAutoroute, c);
		this.jPanel3.add(this.lblAutoroute);
		c.gridx = 1;
		c.gridheight = 2;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.autoPane, c);
		this.jPanel3.add(this.autoPane);
		row++;

		// 按钮
		c.gridx = 1;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 15);
		layout.addLayoutComponent(this.AutoBtn, c);
		this.jPanel3.add(this.AutoBtn);
		
		c.gridx = 2;
		c.insets = new Insets(7, 5, 7, 5);
		layout.addLayoutComponent(this.AutoProBtn, c);
		this.jPanel3.add(this.AutoProBtn);
		
		c.gridx = 3;
		c.insets = new Insets(7, 5, 7, 55);
		layout.addLayoutComponent(this.resetBtn, c);
		this.jPanel3.add(this.resetBtn);
		
		c.insets = new Insets(7, 5, 7, 5);
		// 新增两行,第二列占两行放标签table，第一行第三列放重置标签按钮，第二行第三列放保存标签按钮
		// by guoqc
		if (null == addComponentList) {
			c.gridx = 0;
			c.gridy = row++;
			c.gridheight = 1;
			c.gridwidth = 1;
			layout.addLayoutComponent(this.lblLabel, c);
			this.jPanel3.add(this.lblLabel);
			
			c.gridx = 1;
			c.gridheight = 2;
			c.gridwidth = 2;
			layout.addLayoutComponent(this.labelScrollPane, c);
			this.jPanel3.add(this.labelScrollPane);

			c.gridx = 3;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(7, 5, 7, 55);
			layout.addLayoutComponent(this.labResetBtn, c);
			this.jPanel3.add(this.labResetBtn);
			c.gridx = 3;
			c.gridy = row++;
			c.insets = new Insets(7, 5, 7, 55);
			layout.addLayoutComponent(this.labSaveBtn, c);
			this.jPanel3.add(this.labSaveBtn);
		}
		// qos
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 35);
//		layout.setConstraints(this.lblqos, c);
//		this.jPanel3.add(this.lblqos);
		layout.addLayoutComponent(this.qosConfigButton, c);
		this.jPanel3.add(this.qosConfigButton);
		c.gridx = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.addLayoutComponent(this.txtQos, c);
		this.jPanel3.add(this.txtQos);
//		c.gridx = 3;
//		layout.addLayoutComponent(this.qosConfigButton, c);
//		this.jPanel3.add(this.qosConfigButton);

		// OAM
		c.gridx = 2;
		c.insets = new Insets(7, 5, 7, 25);
		layout.addLayoutComponent(this.oamConfigButton, c);
		this.jPanel3.add(this.oamConfigButton);
//		layout.setConstraints(this.lbloam, c);
//		this.jPanel3.add(this.lbloam);
		c.gridx = 3;
//		c.gridwidth = 2;
		c.insets = new Insets(7, 5, 7, 5);
		layout.addLayoutComponent(this.txtOam, c);
		this.jPanel3.add(this.txtOam);
//		c.gridx = 3;
//		c.gridwidth = 1;
//		layout.addLayoutComponent(this.oamConfigButton, c);
//		this.jPanel3.add(this.oamConfigButton);

		// 等待恢复时间 */
		c.gridx = 0;
		c.gridy = row;
		layout.setConstraints(this.lblwaitTime, c);
		this.jPanel3.add(this.lblwaitTime);
		c.gridx = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.txtWaitTime, c);
		this.jPanel3.add(this.txtWaitTime);

		// 拖延时间
		c.gridx = 2;
		c.gridy = row++;
		c.gridwidth = 1;
		layout.setConstraints(this.lbldelayTime, c);
		this.jPanel3.add(this.lbldelayTime);
		c.gridx = 3;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.addLayoutComponent(this.txtDelayTime, c);
		this.jPanel3.add(this.txtDelayTime);

		// aps使能 、是否返回*/
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.chkAps, c);
		this.jPanel3.add(this.chkAps);
		c.gridx = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.protectBack, c);
		this.jPanel3.add(this.protectBack);
		
		// 倒换准则
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblRotateWay, c);
		this.jPanel3.add(this.lblRotateWay);
		c.gridx = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.cmbRotateWay, c);
		this.jPanel3.add(this.cmbRotateWay);
		
		// 倒换位置，倒换模式
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblRotateLocation, c);
		this.jPanel3.add(this.lblRotateLocation);
		c.gridx = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.cmbRotateLocation, c);
		this.jPanel3.add(this.cmbRotateLocation);
		
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblRotateMode, c);
		this.jPanel3.add(this.lblRotateMode);
		c.gridx = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.cmbRotateMode, c);
		this.jPanel3.add(this.cmbRotateMode);
		
		// TNP层速率，自动倒换阈值
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblTnpLayer, c);
		this.jPanel3.add(this.lblTnpLayer);
		c.gridx = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.spinnerTnpLayer, c);
		this.jPanel3.add(this.spinnerTnpLayer);
		
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblRotateThreshold, c);
		this.jPanel3.add(this.lblRotateThreshold);
		c.gridx = 3;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(this.spinnerRotateThreshold, c);
		this.jPanel3.add(this.spinnerRotateThreshold);

		// SNCP
		c.gridx = 0;
		c.gridy = row;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.jCheckBoxSNCP, c);
		this.jPanel3.add(this.jCheckBoxSNCP);
		c.gridx = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.addLayoutComponent(this.jComboBoxSNCP, c);
		this.jPanel3.add(this.jComboBoxSNCP);

		c.gridx = 2;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(this.lblNumber, c);
		this.jPanel3.add(this.lblNumber);
		c.gridx = 3;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.ptnSpinnerNumber, c);
		this.jPanel3.add(this.ptnSpinnerNumber);
		
		//源mac
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.sourceMacLabel, c);
		this.jPanel3.add(this.sourceMacLabel);
		c.gridx = 1;
		c.insets = new Insets(7, 5, 7, 0);
		layout.addLayoutComponent(this.sourceMacText, c);
		this.jPanel3.add(this.sourceMacText);
		
		//备用源mac
		c.gridx = 2;
		c.gridheight = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.sourceMacLabel_backup, c);
		this.jPanel3.add(this.sourceMacLabel_backup);
		c.gridx = 3;
		c.insets = new Insets(7, 0, 7, 5);
		layout.addLayoutComponent(this.sourceMacText_backup, c);
		this.jPanel3.add(this.sourceMacText_backup);
		
		//目的mac
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.endMacLabel, c);
		this.jPanel3.add(this.endMacLabel);
		c.gridx = 1;
		c.insets = new Insets(7, 5, 7, 0);
		layout.addLayoutComponent(this.endMacText, c);
		this.jPanel3.add(this.endMacText);
		
		
		
		//备用目的mac
		c.gridx = 2;
		c.gridheight = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.endMacLabel_backup, c);
		this.jPanel3.add(this.endMacLabel_backup);
		c.gridx = 3;
		c.insets = new Insets(7, 0, 7, 5);
		layout.addLayoutComponent(this.endMacText_backup, c);
		this.jPanel3.add(this.endMacText_backup);
		
		//主用外层vlan
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.vlan, c);
		this.jPanel3.add(this.vlan);
		c.gridx = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 50);
		layout.addLayoutComponent(this.vlanButton, c);
		this.jPanel3.add(this.vlanButton);
		//备用外层vlan
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.vlan_backup, c);
		this.jPanel3.add(this.vlan_backup);
		c.gridx = 3;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 50);
		layout.addLayoutComponent(this.vlanButton_backup, c);
		this.jPanel3.add(this.vlanButton_backup);
		
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.inBandwidthControl, c);
		this.jPanel3.add(this.inBandwidthControl);
		c.gridx = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 50);
		layout.addLayoutComponent(this.inBandwidthControlCheckBox, c);
		this.jPanel3.add(this.inBandwidthControlCheckBox);
		
		c.gridx = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 5);
		layout.setConstraints(this.outBandwidthControl, c);
		this.jPanel3.add(this.outBandwidthControl);
		c.gridx = 3;
		c.gridwidth = 1;
		c.insets = new Insets(7, 5, 7, 50);
		layout.addLayoutComponent(this.outBandwidthControlCheckBox, c);
		this.jPanel3.add(this.outBandwidthControlCheckBox);

		// 是否激活
		c.gridx = 0;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.jCheckBox1, c);
		this.jPanel3.add(this.jCheckBox1);
		
		// 是否创建PW
		c.gridx = 1;
		c.gridy = row-1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		layout.setConstraints(this.jcbPW, c);
		this.jPanel3.add(this.jcbPW);

		// 保存按钮
		c.gridx = 2;
		c.gridy = row++;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.btnSave, c);
		this.jPanel3.add(this.btnSave);
	}

	/**
	 * 添加控件。 如果需要添加控件。可重写此方法
	 * 
	 * @return
	 */
	public List<Component[]> addComponent() {
		return null;
	}

	public boolean isHasCheck() {
		return hasCheck;
	}
//	public static AddTunnelPathDialog getDialog() {
//		if (addLspPathsDialog == null) {
//			addLspPathsDialog = new AddTunnelPathDialog(
//					new TunnelBusinessPanel(), true, null);
//		}
//		return addLspPathsDialog;
//	}

	public void setHasCheck(boolean hasCheck) {
		this.hasCheck = hasCheck;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public   JComboBox getCmbType() {
		return cmbType;
	}

	public JButton getAutoBtn() {
		return AutoBtn;
	}

	public JButton getAutoProBtn() {
		return AutoProBtn;
	}
	public JCheckBox getjCheckBoxSNCP() {
		return jCheckBoxSNCP;
	}

	public void setjCheckBoxSNCP(JCheckBox jCheckBoxSNCP) {
		this.jCheckBoxSNCP = jCheckBoxSNCP;
	}

	public JComboBox getjComboBoxSNCP() {
		return jComboBoxSNCP;
	}

	public void setjComboBoxSNCP(JComboBox jComboBoxSNCP) {
		this.jComboBoxSNCP = jComboBoxSNCP;
	}
	public JButton getResetBtn() {
		return resetBtn;
	}

	public JCheckBox getChkAps() {
		return chkAps;
	}

	public JTextField getTxtOam() {
		return txtOam;
	}

	public List<Integer[][]> getLabelWorkList() {
		return labelWorkList;
	}

	public List<Integer[][]> getLabelProtList() {
		return labelProtList;
	}

	
	public boolean isWorkMust() {
		return workMust;
	}

	public void setWorkMust(boolean workMust) {
		this.workMust = workMust;
	}

	public boolean isWorksgMust() {
		return worksgMust;
	}

	public void setWorksgMust(boolean worksgMust) {
		this.worksgMust = worksgMust;
	}

	public List<Segment> getWorkSg() {
		return workSg;
	}

	public void setWorkSg(List<Segment> workSg) {
		this.workSg = workSg;
	}

	public TunnelBusinessPanel getTunneBusinessPanel() {
		return tunneBusinessPanel;
	}

	public void setTunneBusinessPanel(TunnelBusinessPanel tunneBusinessPanel) {
		this.tunneBusinessPanel = tunneBusinessPanel;
	}

	public Map<Integer, String> getMipList_pro() {
		return mipList_pro;
	}

	public void setMipList_pro(Map<Integer, String> mipList_pro) {
		this.mipList_pro = mipList_pro;
	}

	public List<OamInfo> getOamList_protect() {
		return oamList_protect;
	}

	public void setOamList_protect(List<OamInfo> oamList_protect) {
		this.oamList_protect = oamList_protect;
	}

	// public PtnTextField getTxtWaitTime() {
	// return txtWaitTime;
	// }
	//
	// public void setTxtWaitTime(PtnTextField txtWaitTime) {
	// this.txtWaitTime = txtWaitTime;
	// }

	public PtnSpinner getTxtDelayTime() {
		return txtDelayTime;
	}

	public PtnSpinner getTxtWaitTime() {
		return txtWaitTime;
	}

	public void setTxtWaitTime(PtnSpinner txtWaitTime) {
		this.txtWaitTime = txtWaitTime;
	}

	public void setTxtDelayTime(PtnSpinner txtDelayTime) {
		this.txtDelayTime = txtDelayTime;
	}

	public TunnelAction getTunnelAction() {
		return tunnelAction;
	}

	/**
	 * 获取保存按钮
	 * 
	 * @return
	 */
	public PtnButton getBtnSave() {
		return btnSave;
	}

	public JCheckBox getProtectBack() {
		return protectBack;
	}

	public void setProtectBack(JCheckBox protectBack) {
		this.protectBack = protectBack;
	}
	
	public JComboBox getCmbRotateWay() {
		return cmbRotateWay;
	}

	public JComboBox getCmbRotateLocation() {
		return cmbRotateLocation;
	}

	public JComboBox getCmbRotateMode() {
		return cmbRotateMode;
	}

	public PtnSpinner getSpinnerTnpLayer() {
		return spinnerTnpLayer;
	}

	public PtnSpinner getSpinnerRotateThreshold() {
		return spinnerRotateThreshold;
	}

	public PtnSpinner getPtnSpinnerNumber() {
		return ptnSpinnerNumber;
	}

	public List<Component[]> getAddComponentList() {
		return addComponentList;
	}

	public javax.swing.JPanel getjPanel3() {
		return jPanel3;
	}

	public JSplitPane getjSplitPane1() {
		return jSplitPane1;
	}

	public JScrollPane getLabelScrollPane() {
		return labelScrollPane;
	}

	public void setjSplitPane1(javax.swing.JSplitPane jSplitPane1) {
		this.jSplitPane1 = jSplitPane1;
	}

	public void setLabelScrollPane(JScrollPane labelScrollPane) {
		this.labelScrollPane = labelScrollPane;
	}

	public List<Segment> getProSg() {
		return proSg;
	}

	public void setProSg(List<Segment> proSg) {
		this.proSg = proSg;
	}

	public EquipmentTopology getEquipmentTopology() {
		return equipmentTopology;
	}

	public void setEquipmentTopology(EquipmentTopology equipmentTopology) {
		this.equipmentTopology = equipmentTopology;
	}
	
	public JTextField getSourceMacText() {
		return sourceMacText;
	}

	public void setSourceMacText(JTextField sourceMacText) {
		this.sourceMacText = sourceMacText;
	}

	public JTextField getEndMacText() {
		return endMacText;
	}

	public void setEndMacText(JTextField endMacText) {
		this.endMacText = endMacText;
	}

	public JTextField getSourceMacText_backup() {
		return sourceMacText_backup;
	}

	public void setSourceMacText_backup(JTextField sourceMacTextBackup) {
		sourceMacText_backup = sourceMacTextBackup;
	}

	public JTextField getEndMacText_backup() {
		return endMacText_backup;
	}

	public void setEndMacText_backup(JTextField endMacTextBackup) {
		endMacText_backup = endMacTextBackup;
	}
	
	public boolean isPortexist() {
		return portexist;
	}

	public void setPortexist(boolean portexist) {
		this.portexist = portexist;
	}
	
	public void setASite(String text) {
		for(int i=1; i< cmbANe.getItemCount(); i++)
		{
			ControlKeyValue obj = (ControlKeyValue)cmbANe.getItemAt(i);
			if(obj.getName().equals(text))
			{
				cmbANe.setSelectedIndex(i);
				break;
			}
		}
	}
	public void setZSite(String text) {
		for(int i=1; i< cmbZNe.getItemCount(); i++)
		{
			ControlKeyValue obj = (ControlKeyValue)cmbZNe.getItemAt(i);
			if(obj.getName().equals(text))
			{
				cmbZNe.setSelectedIndex(i);
				break;
			}
		}
	}
	

	public JLabel getInBandwidthControl() {
		return inBandwidthControl;
	}

	public void setInBandwidthControl(JLabel inBandwidthControl) {
		this.inBandwidthControl = inBandwidthControl;
	}

	public JCheckBox getInBandwidthControlCheckBox() {
		return inBandwidthControlCheckBox;
	}

	public void setInBandwidthControlCheckBox(JCheckBox inBandwidthControlCheckBox) {
		this.inBandwidthControlCheckBox = inBandwidthControlCheckBox;
	}

	public JLabel getOutBandwidthControl() {
		return outBandwidthControl;
	}

	public void setOutBandwidthControl(JLabel outBandwidthControl) {
		this.outBandwidthControl = outBandwidthControl;
	}

	public JCheckBox getOutBandwidthControlCheckBox() {
		return outBandwidthControlCheckBox;
	}

	public void setOutBandwidthControlCheckBox(JCheckBox outBandwidthControlCheckBox) {
		this.outBandwidthControlCheckBox = outBandwidthControlCheckBox;
	}
	public Segment getAWorkSegment() {
		return aWorkSegment;
	}

	public void setAWorkSegment(Segment workSegment) {
		aWorkSegment = workSegment;
	}

	public Segment getZWorkSegment() {
		return zWorkSegment;
	}

	public void setZWorkSegment(Segment workSegment) {
		zWorkSegment = workSegment;
	}

	public Segment getAProSegment() {
		return aProSegment;
	}

	public void setAProSegment(Segment proSegment) {
		aProSegment = proSegment;
	}

	public Segment getZProSegment() {
		return zProSegment;
	}

	public void setZProSegment(Segment proSegment) {
		zProSegment = proSegment;
	}

	public JComboBox getCmbANe() {
		return cmbANe;
	}

	public JComboBox getCmbZNe() {
		return cmbZNe;
	}
	
	public boolean isWorkPort() {
		return workPort;
	}

	public void setWorkPort(boolean workPort) {
		this.workPort = workPort;
	}

	public boolean isProtectPort() {
		return protectPort;
	}

	public void setProtectPort(boolean protectPort) {
		this.protectPort = protectPort;
	}
	
	public boolean isProtectportexist() {
		return protectportexist;
	}

	public void setProtectportexist(boolean protectportexist) {
		this.protectportexist = protectportexist;
	}

	public JCheckBox getJcbPW() {
		return jcbPW;
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private PtnButton btnSave;
	// private javax.swing.JButton jButton3;
	// public javax.swing.JCheckBox jCheckBox1;
	public  javax.swing.JCheckBox jCheckBox1;
	private JCheckBox jcbPW;
	private javax.swing.JCheckBox jCheckBox2;
	private javax.swing.JLabel lblName;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private JLabel jLblAport;
	private JLabel jLblZport;
	private JComboBox cmbAport;
	private JComboBox cmbZport;
	private JLabel jLblProAport;
	private JLabel jLblProZport;
	private JComboBox cmbProAport;
	private JComboBox cmbProZport;
	private javax.swing.JPanel jPanel3;
	private JSplitPane jSplitPane1;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JTextField jTextField1;
	private JComboBox cmbANe;
	private JComboBox cmbZNe;
	private javax.swing.JTextField jTextField4;
	private javax.swing.JButton pathCheckButton;
	private javax.swing.JButton qosConfigButton;
	private javax.swing.JButton oamConfigButton;
//	private JLabel lblqos;
//	private JLabel lbloam;
	private JTextField txtQos;
	private JTextField txtOam;
	private JLabel lblMessage;
	private JLabel lblType;
	private  JComboBox cmbType;
	private JLabel lblwaitTime;
	private JLabel lbldelayTime;
	private PtnSpinner txtWaitTime;
	// private PtnTextField txtWaitTime;
	// private PtnTextField txtDelayTime;
	private PtnSpinner txtDelayTime;
	private JCheckBox chkAps;
	private JCheckBox protectBack;
	/* 新增自动路由_dxh */
	private JLabel lblAutoroute;
	public JTable autoTable;
	private JScrollPane autoPane;
	private JButton AutoBtn;
	private JButton AutoProBtn;
	private JButton resetBtn;
	/* 新增修改标签table btn */
	private JLabel lblLabel;
	private JButton labResetBtn;
	private JButton labSaveBtn;
	private JTable labelTable;
	private JScrollPane labelScrollPane;
	private javax.swing.JButton autoNamingButton;
	// 创建个数控件 kk
	private JLabel lblNumber;
	private PtnSpinner ptnSpinnerNumber;

	public List<List<Segment>> list = new ArrayList<List<Segment>>();
	public int shortestIndex = 0;
	public int secondIndex = 0;
	private boolean workMust = false; // 如果有必经网元，置为true
	private boolean worksgMust = false;
	private boolean workPort = false; // 如果有端口选中，置为true
	private boolean protectPort = false;
	private boolean portexist = true;
	private boolean protectportexist = true;
	private List<Segment> proSg = new ArrayList<Segment>();
	private List<Segment> workSg = new ArrayList<Segment>();
	private List<Integer[][]> labelWorkList = new ArrayList<Integer[][]>();
	private  List<Integer[][]> labelProtList = new ArrayList<Integer[][]>();
	private  Map<Integer, String> labelMap = new HashMap<Integer, String>();
	private  List<Integer[][]> labelWorkList_temp = new ArrayList<Integer[][]>();
	private  List<Integer[][]> labelProtList_temp = new ArrayList<Integer[][]>();
	private  Map<String, String> siteId_PortId_WorkMap = new HashMap<String, String>();
	private  Map<String, String> siteId_PortId_ProtMap = new HashMap<String, String>();

	// 新增SNCP保护控件 pc
	private JCheckBox jCheckBoxSNCP;
	private JComboBox jComboBoxSNCP;
	
	//新增主用外层vlan
	private JLabel vlan;
	private JButton vlanButton;
	//备用外层vlan
	private JLabel vlan_backup;
	private JButton vlanButton_backup; 
	
	//新增mac地址
	private JLabel sourceMacLabel;
	private JTextField sourceMacText;
	private JLabel endMacLabel;
	private JTextField endMacText;
	//新增备用mac地址
	private JLabel sourceMacLabel_backup;
	private JTextField sourceMacText_backup;
	private JLabel endMacLabel_backup;
	private JTextField endMacText_backup;
	
	private JLabel lblRotateWay;
	private JComboBox cmbRotateWay;// 倒换准则 SD/SF
	private JLabel lblRotateLocation;
	private JComboBox cmbRotateLocation;// 倒换位置 A端网元/Z端网元
	private JLabel lblRotateMode;
	private JComboBox cmbRotateMode;// 倒换模式 人工倒换/自动倒换
	private JLabel lblTnpLayer;
	private PtnSpinner spinnerTnpLayer;// TNP层速率 1-100
	private JLabel lblRotateThreshold;
	private PtnSpinner spinnerRotateThreshold;// 自动倒换阈值(%) 1-100
}
