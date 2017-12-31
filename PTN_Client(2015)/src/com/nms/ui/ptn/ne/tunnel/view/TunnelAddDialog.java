﻿package com.nms.ui.ptn.ne.tunnel.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EQosDirection;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.LabelInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.CustomException;
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
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dialog.pwpath.VlanConfigDialog;
import com.nms.ui.ptn.systemconfig.dialog.qos.controller.QosConfigController;

/**
 * 单网元tunnel添加页面
 * 
 * @author kk
 * 
 */
public class TunnelAddDialog extends PtnDialog {

	private static final long serialVersionUID = -760047891872234568L;
	//private static TunnelAddDialog tunnelAddDialog; // 添加tunnel对话框
	private Tunnel tunnelInfo; // tunnel对象
	private TunnelPanel tunnelPanel; // tunnel列表对象
	private int inLabel = 0; // 入标签
	private int outLabel = 0; // 出标签
	private int inLabel_after = 0; // 保护端入标签
	private int outLabel_after = 0; // 保护端出标签

	private List<QosInfo> qosList = new ArrayList<QosInfo>(); // qos集合

	public TunnelPanel getTunnelPanel() {
		return tunnelPanel;
	}

	public void setTunnelPanel(TunnelPanel tunnelPanel) {
		this.tunnelPanel = tunnelPanel;
	}

	public TunnelAddDialog(Tunnel tunnel, TunnelPanel tunnelPanel) {

		this.tunnelPanel = tunnelPanel;
		try {
			if (tunnel != null) {
				this.tunnelInfo = tunnel;
			} else {
				this.tunnelInfo = new Tunnel();
			}
			this.initComponent();
			this.setLayout();
			this.comboxData();
			this.addListener();
			this.different();
			if (tunnel != null) {
				setValue();
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_TUNNEL));
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_TUNNEL));
			}

			UiUtil.showWindow(this, 650, 450);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 处理武汉晨晓不相同部分
	 * 
	 * @throws Exception
	 */
	private void different() throws Exception {
		SiteService_MB siteServiceMB = null;
		try {
			siteServiceMB=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if (siteServiceMB.getManufacturer(ConstantUtil.siteId) == EManufacturer.CHENXIAO.getValue()) {
				this.removeSncp();
				this.protectBackLable.setVisible(false);
				this.protectBack.setVisible(false);
				this.setTTLVisible(false);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}

	}

	/**
	 * 如果是晨晓界面，移除SNCP选项
	 * 
	 * @throws Exception
	 */
	private void removeSncp() throws Exception {
		Code code = null;
		int index = 0;
		try {

			for (int i = 0; i < this.cmbType.getItemCount(); i++) {
				code = (Code) ((ControlKeyValue) this.cmbType.getItemAt(i)).getObject();
				if ("3".equals(code.getCodeValue())) {
					index = i;
				}
			}
			this.cmbType.removeItemAt(index);
		} catch (Exception e) {
			throw e;
		} finally {
			code = null;
		}
	}

	/**
	 * 下拉列表绑定数据
	 * 
	 * @throws Exception
	 */
	private void comboxData() throws Exception {
		this.intalPortComboBox(portComboBox);
		this.intalPortComboBox(portComboBox_after);
		super.getComboBoxDataUtil().comboBoxData(this.cmbRole, "TUNNELROLE");
		super.getComboBoxDataUtil().comboBoxData(this.cmbType, "PROTECTTYPE");
	}

	// /**
	// * 绑定保护tunnel数据
	// *
	// * @throws Exception
	// */
	// private void protectData() throws Exception {
	//
	// TunnelService tunnelService = null;
	// List<Tunnel> tunnelList = null;
	// DefaultComboBoxModel comboBoxModel = null;
	// String type = null;
	// try {
	// comboBoxModel = new DefaultComboBoxModel();
	// type = this.getSelectRole();
	// if (!"xc".equals(type)) {
	// tunnelService = (TunnelService) ConstantUtil.serviceFactory.newService(Services.Tunnel);
	// tunnelList = tunnelService.selectNodesBySiteId_protect(ConstantUtil.siteId, type);
	//
	// comboBoxModel.addElement(null);
	// for (Tunnel tunnel : tunnelList) {
	// comboBoxModel.addElement(new ControlKeyValue(tunnel.getTunnelId() + "", tunnel.getTunnelName(), tunnel));
	// }
	// }
	//
	// this.cmbprotect.setModel(comboBoxModel);
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// tunnelService = null;
	// tunnelList = null;
	// type = null;
	// }
	// }

	private void setValue() throws NumberFormatException, Exception {
		QosInfoService_MB qosInfoServiceMB = null;
		try {
			this.ptnSpinnerNumber.setEnabled(false);
			this.tunnelNameField.setText(tunnelInfo.getTunnelName());
			activeCheckBox.setSelected(tunnelInfo.getTunnelStatus() == 1 ? true : false);
			String roleValue = "";
			if (this.tunnelInfo.getASiteId() == ConstantUtil.siteId) {
				roleValue = "ingress";
				this.txtOpposite.setText(this.tunnelInfo.getLspParticularList().get(0).getAoppositeId());
				// UiUtil.comboBoxSelect(this.remoteSiteComboBox, this.tunnelInfo.getZSiteId() + "");
				super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox, this.tunnelInfo.getAPortId() + "");
				this.txtInLable.setText(this.tunnelInfo.getLspParticularList().get(0).getBackLabelValue() + "");
				this.txtOutLable.setText(this.tunnelInfo.getLspParticularList().get(0).getFrontLabelValue() + "");
				this.txtInTtl.setText(this.tunnelInfo.getLspParticularList().get(0).getFrontTtl() + "");
				this.txtOutTtl.setText(this.tunnelInfo.getLspParticularList().get(0).getBackTtl() + "");
				inLabel = this.tunnelInfo.getLspParticularList().get(0).getBackLabelValue();
				outLabel = this.tunnelInfo.getLspParticularList().get(0).getFrontLabelValue();
				sourceMacField.setText(this.tunnelInfo.getSourceMac());
				targetMacField.setText(this.tunnelInfo.getEndMac());
				if ("2".equals(UiUtil.getCodeById(Integer.parseInt(this.tunnelInfo.getTunnelType())).getCodeValue())) {
					this.txtOpposite_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getAoppositeId());
					super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox_after, this.tunnelInfo.getProtectTunnel().getAPortId() + "");
					this.txtInLable_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackLabelValue() + "");
					this.txtOutLable_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontLabelValue() + "");
					this.txtInTtl_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontTtl() + "");
					this.txtOutTtl_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackTtl() + "");
					inLabel_after = this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackLabelValue();
					outLabel_after = this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontLabelValue();
					sourceMacField_after.setText(this.tunnelInfo.getProtectTunnel().getSourceMac());
					targetMacField_after.setText(this.tunnelInfo.getProtectTunnel().getEndMac());
				}
				
			} else if (this.tunnelInfo.getZSiteId() == ConstantUtil.siteId) {
				roleValue = "egress";
				this.txtOpposite.setText(this.tunnelInfo.getLspParticularList().get(0).getZoppositeId());
				// UiUtil.comboBoxSelect(this.remoteSiteComboBox, this.tunnelInfo.getASiteId() + "");
				super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox, this.tunnelInfo.getZPortId() + "");
				this.txtInLable.setText(this.tunnelInfo.getLspParticularList().get(0).getFrontLabelValue() + "");
				this.txtOutLable.setText(this.tunnelInfo.getLspParticularList().get(0).getBackLabelValue() + "");
				this.txtInTtl.setText(this.tunnelInfo.getLspParticularList().get(0).getBackTtl() + "");
				this.txtOutTtl.setText(this.tunnelInfo.getLspParticularList().get(0).getFrontTtl() + "");
				inLabel = this.tunnelInfo.getLspParticularList().get(0).getFrontLabelValue();
				outLabel = this.tunnelInfo.getLspParticularList().get(0).getBackLabelValue();
				sourceMacField.setText(this.tunnelInfo.getSourceMac());
				targetMacField.setText(this.tunnelInfo.getEndMac());
				if ("2".equals(UiUtil.getCodeById(Integer.parseInt(this.tunnelInfo.getTunnelType())).getCodeValue())) {
					this.txtOpposite_after.setText(this.tunnelInfo.getLspParticularList().get(0).getZoppositeId());
					// UiUtil.comboBoxSelect(this.remoteSiteComboBox_after, this.tunnelInfo.getProtectTunnel().getASiteId() + "");
					super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox_after, this.tunnelInfo.getProtectTunnel().getZPortId() + "");
					this.txtInLable_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontLabelValue() + "");
					this.txtOutLable_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackLabelValue() + "");
					this.txtInTtl_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackTtl() + "");
					this.txtOutTtl_after.setText(this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontTtl() + "");
					inLabel_after = this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getFrontLabelValue();
					outLabel_after = this.tunnelInfo.getProtectTunnel().getLspParticularList().get(0).getBackLabelValue();
					sourceMacField_after.setText(this.tunnelInfo.getProtectTunnel().getSourceMac());
					targetMacField_after.setText(this.tunnelInfo.getProtectTunnel().getEndMac());
				}
			} else {
				roleValue = "xc";
				Lsp lspBefore = this.tunnelInfo.getLspParticularList().get(0);
				Lsp lspAftre = this.tunnelInfo.getLspParticularList().get(1);
				
				this.txtOpposite.setText(lspBefore.getZoppositeId());
				super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox, lspBefore.getZPortId() + "");
				this.txtInLable.setText(lspBefore.getFrontLabelValue() + "");
				this.txtOutLable.setText(lspBefore.getBackLabelValue() + "");
				inLabel = lspBefore.getFrontLabelValue();
				outLabel = lspBefore.getBackLabelValue();
				sourceMacField.setText(lspBefore.getSourceMac());
				targetMacField.setText(lspBefore.getTargetMac());
				this.txtOpposite_after.setText(lspAftre.getAoppositeId());
				super.getComboBoxDataUtil().comboBoxSelect(this.portComboBox_after, lspAftre.getAPortId() + "");
				this.txtInLable_after.setText(lspAftre.getBackLabelValue() + "");
				this.txtOutLable_after.setText(lspAftre.getFrontLabelValue() + "");
				inLabel_after = lspAftre.getBackLabelValue();
				outLabel_after = lspAftre.getFrontLabelValue();
				sourceMacField_after.setText(lspAftre.getSourceMac());
				targetMacField_after.setText(lspAftre.getTargetMac());
				
			}
			super.getComboBoxDataUtil().comboBoxSelect(this.cmbType, this.tunnelInfo.getTunnelType());
			super.getComboBoxDataUtil().comboBoxSelectByValue(this.cmbRole, roleValue);
			this.cmbRole.setEnabled(false);
			
			qosInfoServiceMB = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
			this.txtQos.setText(qosInfoServiceMB.qosCirSum(this.tunnelInfo.getQosList()));
			this.qosButton.setEnabled(false);
			this.txtOpposite.setEditable(false);
			this.txtOpposite_after.setEditable(false);
			// this.portComboBox.setEnabled(false);
			// this.portComboBox_after.setEnabled(false);
			this.txtInLable.setEnabled(true);
			this.txtInLable_after.setEnabled(true);
			this.txtOutLable.setEnabled(true);
			this.txtOutLable_after.setEnabled(true);
			this.qosList = this.tunnelInfo.getQosList();
			this.txtWaitTime.getTxt().setText(this.tunnelInfo.getWaittime() + "");
			this.txtDelayTime.getTxt().setText(this.tunnelInfo.getDelaytime() + "");
			this.chkAps.setSelected(this.tunnelInfo.getApsenable() == 0 ? false : true);
			this.protectBack.setSelected(this.tunnelInfo.getProtectBack() == 0 ? true : false);
			this.cmbType.setEnabled(false);
			this.inBandwidthControlCheckBox.setSelected(this.tunnelInfo.getInBandwidthControl() ==0 ?false:true);
			this.outBandwidthControlCheckBox.setSelected(this.tunnelInfo.getOutBandwidthControl() ==0 ?false:true);
			
			this.cmbRotateWay.setSelectedItem(this.tunnelInfo.getRotateWay());
			this.cmbRotateMode.setSelectedItem(this.tunnelInfo.getRotateMode());
			this.spinnerRotateThreshold.getTxt().setText(this.tunnelInfo.getRotateThreshold()+"");
			// this.chkAps.setEnabled(false);
			if (UiUtil.getCodeById(Integer.parseInt(tunnelInfo.getTunnelType())).getCodeName().toString().trim().equals("1:1")) {
				txtWaitTime.setEnabled(true);
				txtDelayTime.setEnabled(true);
				protectBack.setEnabled(true);
				cmbRotateWay.setEnabled(true);
				cmbRotateMode.setEnabled(true);
				spinnerRotateThreshold.setEnabled(true);
			} else {
				txtWaitTime.setEnabled(false);
				txtDelayTime.setEnabled(false);
				protectBack.setEnabled(false);
				cmbRotateWay.setEnabled(false);
				cmbRotateMode.setEnabled(false);
				spinnerRotateThreshold.setEnabled(false);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(qosInfoServiceMB);
		}
	}

	private void initComponent() throws Exception {
		this.lblMessage = new JLabel();
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE), true);
		tunnelNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		tunnelNameField = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.confirm, this);
		// remoteSiteComboBox = new JComboBox();
		portComboBox = new JComboBox();
		activeCheckBox = new JCheckBox();
		this.lblNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		this.ptnSpinnerNumber = new PtnSpinner(1, 1, 1000, 1);
		qosButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		this.confirm.setVisible(false);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		lblStatus = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.lblPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_PORT));
		this.lblRole = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE));
		this.cmbRole = new JComboBox();
		this.lblSite = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OPPOSITE_SITE));
		this.lblOutLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUTLABEL));
		this.txtOutLable = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.lblOutTtl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_TTL));
		this.txtOutTtl = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage, this.confirm, this);
		this.lblOutTtl_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_TTL));
		this.txtOutTtl_after = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage, this.confirm, this);

		this.txtOutLable.setCheckingMaxValue(true);
		this.txtOutLable.setCheckingMinValue(true);
		this.txtOutLable.setMaxValue(ConstantUtil.LABEL_MAXVALUE2);
		this.txtOutLable.setMinValue(ConstantUtil.LABEL_MINVALUE);
		this.txtOutTtl.setCheckingMaxValue(true);
		this.txtOutTtl.setCheckingMinValue(true);
		this.txtOutTtl.setMaxValue(255);
		this.txtOutTtl.setMinValue(0);
		this.txtOutTtl.setText("255");
		this.txtOutTtl_after.setCheckingMaxValue(true);
		this.txtOutTtl_after.setCheckingMinValue(true);
		this.txtOutTtl_after.setMaxValue(255);
		this.txtOutTtl_after.setMinValue(0);
		this.txtOutTtl_after.setText("255");

		this.lblInLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INLABEL));
		this.txtInLable = new PtnTextField(true, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.lblInTtl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IN_TTL));
		this.txtInTtl = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage, this.confirm, this);
		this.lblInTtl_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IN_TTL));
		this.txtInTtl_after = new PtnTextField(true, PtnTextField.TYPE_INT, 255, this.lblMessage, this.confirm, this);
		this.txtInLable.setCheckingMaxValue(true);
		this.txtInLable.setCheckingMinValue(true);
		this.txtInLable.setMaxValue(ConstantUtil.LABEL_MAXVALUE);
		this.txtInLable.setMinValue(ConstantUtil.LABEL_MINVALUE);
		this.txtInTtl.setCheckingMaxValue(true);
		this.txtInTtl.setCheckingMinValue(true);
		this.txtInTtl.setMaxValue(255);
		this.txtInTtl.setMinValue(0);
		this.txtInTtl.setText("255");
		this.txtInTtl_after.setCheckingMaxValue(true);
		this.txtInTtl_after.setCheckingMinValue(true);
		this.txtInTtl_after.setMaxValue(255);
		this.txtInTtl_after.setMinValue(0);
		this.txtInTtl_after.setText("255");

		this.lblQos = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QOS));
		this.txtQos = new PtnTextField();
		this.txtQos.setEnabled(false);
		this.panelTunnel = new JPanel();
		this.panelLsp = new JPanel();
		this.panelLsp.setVisible(false);
		this.panelButton = new JPanel();
		this.panelButton.setPreferredSize(new Dimension(600, 40));
		this.btnBack = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_BACK));
		this.btnBack.setVisible(false);
		this.btnNext = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_NEXT));
		this.panelContent = new JPanel();
		this.panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_TUNNEL_FIRST)));
		this.panelXCbefore = new JPanel();
		this.panelXCafter = new JPanel();
		this.lblPort_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOAD_PORT));
		// this.remoteSiteComboBox_after = new JComboBox();
		this.portComboBox_after = new JComboBox();
		this.lblSite_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OPPOSITE_SITE));
		this.lblOutLable_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUTLABEL));
		this.lblInLable_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INLABEL));
		this.txtOutLable_after = new PtnTextField(false, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.txtOutLable_after.setCheckingMaxValue(true);
		this.txtOutLable_after.setCheckingMinValue(true);
		this.txtOutLable_after.setMaxValue(ConstantUtil.LABEL_MAXVALUE2);
		this.txtOutLable_after.setMinValue(ConstantUtil.LABEL_MINVALUE);
		this.txtInLable_after = new PtnTextField(false, PtnTextField.TYPE_INT, PtnTextField.INT_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.txtInLable_after.setCheckingMaxValue(true);
		this.txtInLable_after.setCheckingMinValue(true);
		this.txtInLable_after.setMaxValue(ConstantUtil.LABEL_MAXVALUE);
		this.txtInLable_after.setMinValue(ConstantUtil.LABEL_MINVALUE);
		this.panelXCafter.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_RIGHT_LSP)));
		// this.lblprotect = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT));
		// this.cmbprotect = new JComboBox();
		this.lblwaitTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
		// this.txtWaitTime=new PtnSpinner(12,5,1);
		this.lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
		// this.txtDelayTime=new PtnSpinner(10000,0,100);

		this.txtDelayTime = new PtnSpinner(2500, 0, 100, ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
		this.txtWaitTime = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
		this.chkAps = new JCheckBox();
		this.lblType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TYPE));
		this.cmbType = new JComboBox();

		this.txtWaitTime.getTxt().setText("");
		this.txtWaitTime.setEnabled(false);
		this.txtDelayTime.getTxt().setText("");
		this.txtDelayTime.setEnabled(false);
		this.chkAps.setEnabled(false);
		this.lblAps = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
		this.txtOpposite = new PtnTextField(false, PtnTextField.TYPE_IP, PtnTextField.IP_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.txtOpposite_after = new PtnTextField(false, PtnTextField.TYPE_IP, PtnTextField.IP_MAXLENGTH, this.lblMessage, this.confirm, this);
		this.txtOpposite.setText("0.0.0.0");
		this.txtOpposite_after.setText("0.0.0.0");
		this.autoNamingButton = new JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		protectBackLable = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
		protectBack = new JCheckBox();
		protectBack.setEnabled(false);

		sourceMac = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC));
		sourceMacField = new PtnTextField(false, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, this.lblMessage, this.confirm, this);
		sourceMacField.setText("00-00-00-00-00-01");
		targetMac = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PURPOSE_MAC));
		targetMacField = new PtnTextField(false, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, this.lblMessage, this.confirm, this);
		targetMacField.setText("00-00-00-00-00-01");
		sourceMac_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SOURCE_MAC));
		sourceMacField_after = new PtnTextField(false, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, this.lblMessage, this.confirm, this);
		sourceMacField_after.setText("00-00-00-00-00-01");
		targetMac_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PURPOSE_MAC));
		targetMacField_after = new PtnTextField(false, PtnTextField.TYPE_MAC, PtnTextField.MAC_MAXLENGTH, this.lblMessage, this.confirm, this);
		targetMacField_after.setText("00-00-00-00-00-01");
		
		inBandwidthControl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_INBANDWIDTH));
		inBandwidthControlCheckBox = new JCheckBox();
		outBandwidthControl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUTBANDWIDTH));
		outBandwidthControlCheckBox = new JCheckBox();
		//新增主用外层vlan
		vlanLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
		vlanButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		//新增备用外层vlan
		vlanLabel_after = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_BACKUP));
		vlanButton_after = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		this.vlanButton_after.setEnabled(false);
		
		this.lblRotateWay = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATEWAY));
		this.cmbRotateWay = new JComboBox();
		List<String> itemList = new ArrayList<String>();
		itemList.add("SD");
		itemList.add("SF");
		this.setCmbItem(this.cmbRotateWay, itemList);
		
		this.lblRotateMode = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATEMODE));
		this.cmbRotateMode = new JComboBox();
		itemList.clear();
		itemList.add("MANUAL");
		itemList.add("AUTO");
		this.setCmbItem(this.cmbRotateMode, itemList);
		
		this.lblRotateThreshold = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATETHRESHOLD));
		this.spinnerRotateThreshold = new PtnSpinner(100, 1, 1, ResourceUtil.srcStr(StringKeysLbl.LBL_ROTATETHRESHOLD));
		this.spinnerRotateThreshold.getTxt().setText("1");
		this.cmbRotateWay.setEnabled(false);
		this.cmbRotateMode.setEnabled(false);
		this.spinnerRotateThreshold.setEnabled(false);
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

	private void setLayout() {

		this.setTunnelLayout();
		this.setLspLayout();
		this.setButtonLayout();

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {};
		componentLayout.columnWeights = new double[] {};
		componentLayout.rowHeights = new int[] {};
		componentLayout.rowWeights = new double[] {};
		this.panelContent.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.panelTunnel, c);
		this.panelContent.add(this.panelTunnel);
		componentLayout.setConstraints(this.panelLsp, c);
		this.panelContent.add(this.panelLsp);

		this.add(this.panelContent, BorderLayout.CENTER);
		this.add(this.panelButton, BorderLayout.SOUTH);

	}

	private void setTunnelLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 120, 100, 100, 80 };
		componentLayout.columnWeights = new double[] { 0, 0.1, 0.1, 0 };
		componentLayout.rowHeights = new int[] { 15, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.panelTunnel.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// tunnel名称
		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(tunnelNameLabel, c);
		this.panelTunnel.add(tunnelNameLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(tunnelNameField, c);
		this.panelTunnel.add(tunnelNameField);
		c.gridx = 3;
		componentLayout.setConstraints(this.autoNamingButton, c);
		this.panelTunnel.add(this.autoNamingButton);

		// tunnel类型
		c.gridx = 0;
		c.gridy = 2;
		componentLayout.setConstraints(this.lblType, c);
		this.panelTunnel.add(this.lblType);
		c.gridx = 1;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.cmbType, c);
		this.panelTunnel.add(this.cmbType);

		// tunnel角色
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblRole, c);
		this.panelTunnel.add(this.lblRole);
		c.gridx = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.cmbRole, c);
		this.panelTunnel.add(this.cmbRole);

		// qos
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblQos, c);
		this.panelTunnel.add(this.lblQos);
		c.gridx = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.txtQos, c);
		this.panelTunnel.add(this.txtQos);
		c.gridx = 3;
		componentLayout.setConstraints(this.qosButton, c);
		this.panelTunnel.add(this.qosButton);

		/** 第八�?等待恢复时间 */
		c.gridx = 0;
		c.gridy = 4;
		componentLayout.setConstraints(this.lblwaitTime, c);
		this.panelTunnel.add(this.lblwaitTime);
		c.gridx = 1;
		c.gridwidth = 1;
		componentLayout.addLayoutComponent(this.txtWaitTime, c);
		this.panelTunnel.add(this.txtWaitTime);
		/** 第九�?拖延时间 */
		c.gridx = 2;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lbldelayTime, c);
		this.panelTunnel.add(this.lbldelayTime);
		c.gridx = 3;
		c.gridwidth = 1;
		componentLayout.addLayoutComponent(this.txtDelayTime, c);
		this.panelTunnel.add(this.txtDelayTime);

		/** 第十�?aps使能 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.lblAps, c);
		this.panelTunnel.add(this.lblAps);
		c.gridx = 1;
		componentLayout.setConstraints(this.chkAps, c);
		this.panelTunnel.add(this.chkAps);

		// 是否返回
		c.gridx = 2;
		componentLayout.setConstraints(this.protectBackLable, c);
		this.panelTunnel.add(this.protectBackLable);
		c.gridx = 3;
		componentLayout.setConstraints(this.protectBack, c);
		this.panelTunnel.add(this.protectBack);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.inBandwidthControl, c);
		this.panelTunnel.add(this.inBandwidthControl);
		c.gridx = 1;
		componentLayout.setConstraints(this.inBandwidthControlCheckBox, c);
		this.panelTunnel.add(this.inBandwidthControlCheckBox);

		c.gridx = 2;
		componentLayout.setConstraints(this.outBandwidthControl, c);
		this.panelTunnel.add(this.outBandwidthControl);
		c.gridx = 3;
		componentLayout.setConstraints(this.outBandwidthControlCheckBox, c);
		this.panelTunnel.add(this.outBandwidthControlCheckBox);
		
		//新增主用vlan配置
		c.gridx = 0;
		c.gridy = 7;
		componentLayout.setConstraints(this.vlanLabel, c);
		this.panelTunnel.add(this.vlanLabel);
		c.gridx = 1;
		componentLayout.setConstraints(this.vlanButton, c);
		this.panelTunnel.add(this.vlanButton);
		
		//新增备用vlan配置
		c.gridx = 2;
		componentLayout.setConstraints(this.vlanLabel_after, c);
		this.panelTunnel.add(this.vlanLabel_after);
		c.gridx = 3;
		componentLayout.setConstraints(this.vlanButton_after, c);
		this.panelTunnel.add(this.vlanButton_after);
		
		// 新增保护模式，倒换准则，倒换阈值
		c.gridx = 0;
		c.gridy = 8;
		componentLayout.setConstraints(this.lblRotateWay, c);
		this.panelTunnel.add(this.lblRotateWay);
		c.gridx = 1;
		componentLayout.setConstraints(this.cmbRotateWay, c);
		this.panelTunnel.add(this.cmbRotateWay);
		
		c.gridx = 2;
		componentLayout.setConstraints(this.lblRotateMode, c);
		this.panelTunnel.add(this.lblRotateMode);
		c.gridx = 3;
		componentLayout.setConstraints(this.cmbRotateMode, c);
		this.panelTunnel.add(this.cmbRotateMode);
		
		c.gridx = 0;
		c.gridy = 9;
		componentLayout.setConstraints(lblRotateThreshold, c);
		this.panelTunnel.add(lblRotateThreshold);
		c.gridx = 1;
		componentLayout.setConstraints(this.spinnerRotateThreshold, c);
		this.panelTunnel.add(this.spinnerRotateThreshold);
		
		// 是否激活
		c.gridx = 2;
		componentLayout.setConstraints(lblStatus, c);
		this.panelTunnel.add(lblStatus);
		c.gridx = 3;
		componentLayout.setConstraints(this.activeCheckBox, c);
		this.panelTunnel.add(this.activeCheckBox);
		
		// 创建数量
		c.gridx = 0;
		c.gridy = 10;
		componentLayout.setConstraints(lblNumber, c);
		this.panelTunnel.add(lblNumber);
		c.gridx = 1;
		componentLayout.setConstraints(this.ptnSpinnerNumber, c);
		this.panelTunnel.add(this.ptnSpinnerNumber);
	}

	private void setButtonLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 410, 60, 60 };
		componentLayout.columnWeights = new double[] { 0.2, 0, 0 };
		componentLayout.rowHeights = new int[] { 30 };
		componentLayout.rowWeights = new double[] { 0.1 };
		this.panelButton.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 6, 6, 5);
		componentLayout.setConstraints(this.lblMessage, c);
		this.panelButton.add(this.lblMessage);

		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		componentLayout.setConstraints(this.btnBack, c);
		this.panelButton.add(this.btnBack);

		c.gridx = 2;
		componentLayout.setConstraints(this.btnNext, c);
		this.panelButton.add(this.btnNext);

		c.gridx = 2;
		componentLayout.setConstraints(this.confirm, c);
		this.panelButton.add(this.confirm);

		c.gridx = 3;
		componentLayout.setConstraints(this.cancel, c);
		this.panelButton.add(this.cancel);
	}

	private void setLspLayout() {

		this.setBeforeLayout();
		this.setAfterLayout();

		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 280, 280 };
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] {};
		componentLayout.rowWeights = new double[] {};
		this.panelLsp.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 5);
		componentLayout.setConstraints(this.panelXCbefore, c);
		this.panelLsp.add(this.panelXCbefore);

		c.gridx = 1;
		c.insets = new Insets(0, 5, 0, 0);
		componentLayout.setConstraints(this.panelXCafter, c);
		this.panelLsp.add(this.panelXCafter);
	}

	private void setBeforeLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200 };
		componentLayout.columnWeights = new double[] { 0, 0.5 };
		componentLayout.rowHeights = new int[] { 15, 40, 40, 40, 40, 40 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.2 };
		this.panelXCbefore.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// 对端网元
		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.lblSite, c);
		this.panelXCbefore.add(this.lblSite);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOpposite, c);
		this.panelXCbefore.add(this.txtOpposite);

		// 承载端口
		c.gridx = 0;
		c.gridy = 2;
		componentLayout.setConstraints(this.lblPort, c);
		this.panelXCbefore.add(this.lblPort);
		c.gridx = 1;
		componentLayout.setConstraints(this.portComboBox, c);
		this.panelXCbefore.add(this.portComboBox);

		// 入标签
		c.gridx = 0;
		c.gridy = 3;
		componentLayout.setConstraints(this.lblInLable, c);
		this.panelXCbefore.add(this.lblInLable);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtInLable, c);
		this.panelXCbefore.add(this.txtInLable);

		// 出标签
		c.gridx = 0;
		c.gridy = 4;
		componentLayout.setConstraints(this.lblOutLable, c);
		this.panelXCbefore.add(this.lblOutLable);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOutLable, c);
		this.panelXCbefore.add(this.txtOutLable);

		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.sourceMac, c);
		this.panelXCbefore.add(this.sourceMac);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.sourceMacField, c);
		this.panelXCbefore.add(this.sourceMacField);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.targetMac, c);
		this.panelXCbefore.add(this.targetMac);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.targetMacField, c);
		this.panelXCbefore.add(this.targetMacField);
		
		// 入TTL
		c.gridx = 0;
		c.gridy = 7;
		componentLayout.setConstraints(this.lblInTtl, c);
		this.panelXCbefore.add(this.lblInTtl);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtInTtl, c);
		this.panelXCbefore.add(this.txtInTtl);

		// 出TTL
		c.gridx = 0;
		c.gridy = 8;
		componentLayout.setConstraints(this.lblOutTtl, c);
		this.panelXCbefore.add(this.lblOutTtl);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOutTtl, c);
		this.panelXCbefore.add(this.txtOutTtl);
	}

	private void setAfterLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 200 };
		componentLayout.columnWeights = new double[] { 0, 0.5 };
		componentLayout.rowHeights = new int[] { 15, 40, 40, 40, 40, 40 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.2 };
		this.panelXCafter.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 1;
		componentLayout.setConstraints(this.lblSite_after, c);
		this.panelXCafter.add(this.lblSite_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOpposite_after, c);
		this.panelXCafter.add(this.txtOpposite_after);

		c.gridx = 0;
		c.gridy = 2;
		componentLayout.setConstraints(this.lblPort_after, c);
		this.panelXCafter.add(this.lblPort_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.portComboBox_after, c);
		this.panelXCafter.add(this.portComboBox_after);

		c.gridx = 0;
		c.gridy = 3;
		componentLayout.setConstraints(this.lblInLable_after, c);
		this.panelXCafter.add(this.lblInLable_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtInLable_after, c);
		this.panelXCafter.add(this.txtInLable_after);

		c.gridx = 0;
		c.gridy = 4;
		componentLayout.setConstraints(this.lblOutLable_after, c);
		this.panelXCafter.add(this.lblOutLable_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOutLable_after, c);
		this.panelXCafter.add(this.txtOutLable_after);

		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.sourceMac_after, c);
		this.panelXCafter.add(this.sourceMac_after);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.sourceMacField_after, c);
		this.panelXCafter.add(this.sourceMacField_after);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.targetMac_after, c);
		this.panelXCafter.add(this.targetMac_after);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.targetMacField_after, c);
		this.panelXCafter.add(this.targetMacField_after);
		
		c.gridx = 0;
		c.gridy = 7;
		componentLayout.setConstraints(this.lblInTtl_after, c);
		this.panelXCafter.add(this.lblInTtl_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtInTtl_after, c);
		this.panelXCafter.add(this.txtInTtl_after);

		c.gridx = 0;
		c.gridy = 8;
		componentLayout.setConstraints(this.lblOutTtl_after, c);
		this.panelXCafter.add(this.lblOutTtl_after);
		c.gridx = 1;
		componentLayout.setConstraints(this.txtOutTtl_after, c);
		this.panelXCafter.add(this.txtOutTtl_after);
	}

	private void addListener() {
		qosButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				QosInfoService_MB qosInfoServiceMB = null;
				try {
					qosInfoServiceMB = (QosInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.QosInfo);
					QosConfigController qoscontroller = new QosConfigController();
					qoscontroller.setNetwork(false);
					qoscontroller.openQosConfig(qoscontroller, "TUNNEL", tunnelInfo, null,TunnelAddDialog.this);
					txtQos.setText(qosInfoServiceMB.qosCirSum(getQosList()));
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}finally{
					UiUtil.closeService_MB(qosInfoServiceMB);
				}
			}
		});
		confirm.addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					confrimAction();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}

			@Override
			public boolean checking() {

				return true;
			}

		});
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		this.btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SiteService_MB siteServiceMB = null;
				try {
					siteServiceMB=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					// if(!QosIsFull()){
					// return ;
					// }
					if (getSelectRole().equals("xc")) {
						panelXCbefore.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_LEFT_LSP)));
						panelXCafter.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_RIGHT_LSP)));
						panelXCafter.setVisible(true);
						if (siteServiceMB.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()) {
							setTTLVisible(false);
						}
					} else {
						if (siteServiceMB.getManufacturer(ConstantUtil.siteId) == EManufacturer.WUHAN.getValue()) {
							setTTLVisible(true);
						}
						if ("2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
							panelXCbefore.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_JOB_LSP)));
							panelXCafter.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_PROTECT_LSP)));
							panelXCafter.setVisible(true);
						} else {
							panelXCbefore.setBorder(null);
							panelXCafter.setVisible(false);
						}
					}
					panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_TUNNEL_SECOND)));
					panelTunnel.setVisible(false);
					panelLsp.setVisible(true);
					btnBack.setVisible(true);
					btnNext.setVisible(false);
					confirm.setVisible(true);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				} finally {
					UiUtil.closeService_MB(siteServiceMB);
				}

			}
		});

		this.btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelContent.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_TUNNEL_FIRST)));
				panelTunnel.setVisible(true);
				panelLsp.setVisible(false);
				btnBack.setVisible(false);
				btnNext.setVisible(true);
				confirm.setVisible(false);

			}
		});
		this.cmbRole.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					try {
						if ("xc".equals(getSelectRole())) {
							txtInLable_after.setMustFill(true);
							txtOutLable_after.setMustFill(true);
							sourceMacField_after.setEnabled(true);
							targetMacField_after.setEnabled(true);
							// cmbprotect.setEnabled(false);
						} else {
							if ("2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
								// txtInLable_after.setText("");
								// txtOutLable_after.setText("");
								txtInLable_after.setMustFill(true);
								txtOutLable_after.setMustFill(true);
								sourceMacField_after.setEnabled(true);
								targetMacField_after.setEnabled(true);
							} else {
								// txtInLable_after.setText("");
								// txtOutLable_after.setText("");
								txtInLable_after.setMustFill(false);
								txtOutLable_after.setMustFill(false);
								sourceMacField_after.setEnabled(false);
								targetMacField_after.setEnabled(false);
							}

							// cmbprotect.setEnabled(true);
							// protectData();
						}
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}
			}
		});

		this.cmbType.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					try {
						Code code = (Code) ((ControlKeyValue) evt.getItem()).getObject();
						if ("1".equals(code.getCodeValue())) {
							cmbRotateWay.setEnabled(false);
							cmbRotateMode.setEnabled(false);
							spinnerRotateThreshold.setEnabled(false);
							
							txtWaitTime.getTxt().setText("");
							txtWaitTime.setEnabled(false);

							txtDelayTime.getTxt().setText("");
							txtDelayTime.setEnabled(false);
							chkAps.setEnabled(false);
							// DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) cmbRole.getModel();
							cmbRole.removeAllItems();
							getComboBoxDataUtil().comboBoxData(cmbRole, "TUNNELROLE");
							txtInLable_after.setText("");
							txtOutLable_after.setText("");
							txtInLable_after.setMustFill(false);
							txtOutLable_after.setMustFill(false);
							chkAps.setEnabled(false);
							protectBack.setEnabled(false);
							vlanButton_after.setEnabled(false);
						} else {
							cmbRotateWay.setEnabled(true);
							cmbRotateMode.setEnabled(true);
							spinnerRotateThreshold.setEnabled(true);
							
							txtWaitTime.getTxt().setText("5");
							txtWaitTime.setEnabled(true);

							txtDelayTime.getTxt().setText("0");
							txtDelayTime.setEnabled(true);
							// txtDelayTime.getTxt().setEditable(true);
							chkAps.setEnabled(true);
							protectBack.setEnabled(true);
							for (int i = 0; i < cmbRole.getItemCount(); i++) {
								if ("XC".equals(((ControlKeyValue) cmbRole.getItemAt(i)).getName())) {
									cmbRole.removeItemAt(i);
								}
							}
							cmbRole.setSelectedIndex(0);
							txtInLable_after.setMustFill(true);
							txtOutLable_after.setMustFill(true);
							vlanButton_after.setEnabled(true);
						}
					} catch (Exception e) {
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
		
		this.vlanButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					vlanConfig();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});
		
		this.vlanButton_after.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					vlanConfig_after();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});
	}

	/**
	 * 主用外层vlan配置
	 */
	private void vlanConfig() {
		new VlanConfigDialog(this.tunnelInfo, ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN));
	}
	
	/**
	 * 备用外层vlan配置
	 */
	private void vlanConfig_after() {
		if(this.tunnelInfo.getProtectTunnel() == null){
			this.tunnelInfo.setProtectTunnel(new Tunnel());
		}
		new VlanConfigDialog(this.tunnelInfo.getProtectTunnel(), ResourceUtil.srcStr(StringKeysLbl.LBL_OUT_VLAN_BACKUP));
	}

	/**
	 * 控制TTL属性是否显示
	 * 
	 * @param flag
	 *            true显示 false 不显示
	 * @throws Exception
	 */
	private void setTTLVisible(boolean flag) throws Exception {
		this.lblOutTtl.setVisible(flag);
		this.txtOutTtl.setVisible(flag);
		this.lblOutTtl_after.setVisible(flag);
		this.txtOutTtl_after.setVisible(flag);
		this.lblInTtl.setVisible(flag);
		this.txtInTtl.setVisible(flag);
		this.lblInTtl_after.setVisible(flag);
		this.txtInTtl_after.setVisible(flag);
	}

	/**
	 * @return boolean 判断Qos是否配置
	 */
	private boolean QosIsFull() {
		boolean flag = true;
		try {
			if (this.qosList == null || this.qosList.size() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
				flag = false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return flag;
	}

	/**
	 * 自动命名
	 */
	private void autoNamingActionPerformed() {
		Tunnel tunnel;
		String autoNaming;
		try {
			tunnel = new Tunnel();
			tunnel.setIsSingle(1);
			tunnel.setASiteId(ConstantUtil.siteId);
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			autoNaming = (String) autoNamingUtil.autoNaming(tunnel, null, null);
			tunnelNameField.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 获取角色下拉列表选中的�?
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getSelectRole() throws Exception {
		ControlKeyValue controlKeyValue = null;
		Code code = null;
		try {
			controlKeyValue = (ControlKeyValue) TunnelAddDialog.this.cmbRole.getSelectedItem();
			code = (Code) controlKeyValue.getObject();
			return code.getCodeValue();
		} catch (Exception e) {
			throw e;
		} finally {
			controlKeyValue = null;
			code = null;
		}
	}

	/**
	 * 绑定对端网元下拉列表
	 * 
	 * @param jComboBox
	 * @throws Exception
	 */
	private void intalRemoteSiteCombox(JComboBox jComboBox) throws Exception {
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
		SiteService_MB siteServiceMB = null;
		List<SiteInst> siteInstList = null;
		try {
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = siteServiceMB.select();
			for (SiteInst siteInst : siteInstList) {
				if (siteInst.getSite_Inst_Id() != ConstantUtil.siteId) {
					defaultComboBoxModel.addElement(new ControlKeyValue(siteInst.getSite_Inst_Id() + "", siteInst.getCellDescribe(), siteInst));
				}
			}
			jComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}

	}

	private void intalPortComboBox(JComboBox jComboBox) throws Exception {
		PortService_MB portServiceMB = null;
		List<PortInst> portInstList = null;
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
		try {
			portServiceMB = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst portInst = new PortInst();
			portInst.setSiteId(ConstantUtil.siteId);
			portInst.setPortType("NNI");

			portInstList = portServiceMB.select(portInst);
			for (PortInst inst : portInstList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(inst.getPortId() + "", inst.getPortName(), inst));
			}
			jComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portServiceMB);
			portInstList = null;
		}

	}

	/**
	 * 保存tunnel
	 * 
	 * @throws Exception
	 */
	private void confrimAction() throws Exception {
		String message = "";
		String beforeName = "";
		TunnelService_MB tunnelServiceMB  = null;
		try {
			if (!this.isFull()) {
				return;
			}
			if (this.tunnelInfo.getTunnelId() != 0) {
				beforeName = this.tunnelInfo.getTunnelName();
			}
			VerifyNameUtil verifyNameUtil = new VerifyNameUtil();
			if (verifyNameUtil.verifyNameBySingle(EServiceType.TUNNEL.getValue(), this.tunnelNameField.getText().trim(), beforeName, ConstantUtil.siteId)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			if (!this.isSiteEqual()) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_BEFORE_AFTER_SITE));
				return;
			}
			String tunnelrole = this.getSelectRole();
			// //新增tunnel是否被占用
			// if(tunnelInfo.getTunnelId()>0)
			// {
			// if(checktunnelUsed(tunnelInfo.getTunnelId()))
			// {
			// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_TUNNEL_USED));
			// return;
			// }
			// }

			// 验证保护端口和工作端口是否相同
			// if (!this.isProtectPort()) {
			// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PROTECT_PORT));
			// return;
			// }

			 String usableLabel = this.isLabelUsable();
			 if (usableLabel.length() > 0) {
				 usableLabel = usableLabel.substring(0, usableLabel.length() - 1);
			 }
			 if (usableLabel.length() > 0) {
				 DialogBoxUtil.errorDialog(this, usableLabel + " " +ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_OCCUPY));
				 return;
			 }

			this.tunnelInfo.setTunnelName(this.tunnelNameField.getText().trim());
			this.tunnelInfo.setTunnelStatus(this.activeCheckBox.isSelected() == true ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
			this.tunnelInfo.setIsReverse(1);
			this.tunnelInfo.setPosition(1);
			if (this.txtWaitTime.getTxtData().trim().length() > 0) {
				this.tunnelInfo.setWaittime(Integer.parseInt(this.txtWaitTime.getTxtData()));
			}
			if (this.txtDelayTime.getTxtData().trim().length() > 0) {
				this.tunnelInfo.setDelaytime(Integer.parseInt(this.txtDelayTime.getTxtData()));
			}
			this.tunnelInfo.setRotateWay(this.cmbRotateWay.getSelectedItem().toString());
			this.tunnelInfo.setRotateMode(this.cmbRotateMode.getSelectedItem().toString());
			this.tunnelInfo.setRotateThreshold(Integer.parseInt(this.spinnerRotateThreshold.getTxtData()));
			
			this.tunnelInfo.setApsenable(this.chkAps.isSelected() == true ? 1 : 0);
			this.tunnelInfo.setTunnelType(((ControlKeyValue) cmbType.getSelectedItem()).getId());
			this.tunnelInfo.setProtectBack(this.protectBack.isSelected() ? 0 : 1);
			this.tunnelInfo.setInBandwidthControl(inBandwidthControlCheckBox.isSelected()?1:0);
			this.tunnelInfo.setOutBandwidthControl(outBandwidthControlCheckBox.isSelected()?1:0);
			this.tunnelInfo.setSourceMac(this.sourceMacField.getText().trim());
			this.tunnelInfo.setEndMac(this.targetMacField.getText().trim());
			if(this.tunnelInfo.getaOutVlanValue() == 0){
				this.tunnelInfo.setaOutVlanValue(2);
			}
			if(this.tunnelInfo.getzOutVlanValue() == 0){
				this.tunnelInfo.setzOutVlanValue(2);
			}
			// if (this.tunnelInfo.getTunnelId() == 0) {
			this.tunnelInfo.setIsSingle(1);
			this.tunnelInfo.setCreateUser(ConstantUtil.user.getUser_Name());
			this.tunnelInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			this.tunnelInfo.setQosList(this.getQosList());

			if ("xc".equals(tunnelrole)) {
				this.convertXCTunnel(this.tunnelInfo);
			} else if ("egress".equals(tunnelrole)) {
				this.convertZTunnel(this.tunnelInfo, true);
			} else {
				this.convertATunnel(this.tunnelInfo, true);
			}

			if ("2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				this.getProtectTunnel(this.tunnelInfo, tunnelrole);
//				this.tunnelInfo.getProtectTunnel().setVlanEnable(this.tunnelInfo.getVlanEnable());
//				this.tunnelInfo.getProtectTunnel().setOutVlanValue(this.tunnelInfo.getOutVlanValue());
//				this.tunnelInfo.getProtectTunnel().setTp_id(this.tunnelInfo.getTp_id());
			}
			
			DispatchUtil tunnelOperationService = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			if (this.tunnelInfo.getTunnelId() > 0) {
				Tunnel tunnelBefore = this.getTunnelBefore(this.tunnelInfo.getTunnelId());
				tunnelBefore.setRole(tunnelrole);
				message = tunnelOperationService.excuteUpdate(this.tunnelInfo);
				this.tunnelInfo.setRole(tunnelrole);
				if(this.tunnelInfo.getProtectTunnelId() > 0){
					tunnelBefore.getProtectTunnel().setTunnelType(tunnelBefore.getTunnelType());
					this.tunnelInfo.getProtectTunnel().setTunnelType(this.tunnelInfo.getTunnelType());
					this.tunnelInfo.getProtectTunnel().setTunnelStatus(this.tunnelInfo.getTunnelStatus());
					this.insertLog(this.confirm, EOperationLogType.TUNNELUPDATE.getValue(), message, tunnelBefore.getProtectTunnel(), this.tunnelInfo.getProtectTunnel());
				}
				tunnelBefore.setProtectTunnel(null);
				this.tunnelInfo.setProtectTunnel(null);
				this.insertLog(this.confirm, EOperationLogType.TUNNELUPDATE.getValue(), message, tunnelBefore, this.tunnelInfo);
			} else {
				// 新建时  验证qos的带宽是否充足
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Lsp> lspList = tunnelServiceMB.getAllLsp(tunnelInfo);
				int maxCreateQos = tunnelServiceMB.getMinQosNum(lspList, tunnelInfo.getQosList(),null);
				if(maxCreateQos == 0){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORT_QOS_ALARM));
					return;
				}
				List<Tunnel> tunnelList = new ArrayList<Tunnel>();
				int num = Integer.parseInt(this.ptnSpinnerNumber.getTxt().getText());
				tunnelList.add(this.tunnelInfo);
				if(num > 1){
					this.createTunnelOnCopy(tunnelList, num-1, tunnelrole);
				}
				message = tunnelOperationService.excuteInsert(tunnelList);
				this.tunnelInfo.setRole(tunnelrole);
				if(this.tunnelInfo.getProtectTunnel() != null){
					this.tunnelInfo.getProtectTunnel().setTunnelType(this.tunnelInfo.getTunnelType());
					this.tunnelInfo.getProtectTunnel().setRole(tunnelrole);
					this.insertLog(this.confirm, EOperationLogType.TUNNELINSERT.getValue(), message, null, this.tunnelInfo.getProtectTunnel());
				}
				this.tunnelInfo.setProtectTunnel(null);
				this.insertLog(this.confirm, EOperationLogType.TUNNELINSERT.getValue(), message, null, this.tunnelInfo);
			}
			DialogBoxUtil.succeedDialog(this, message);
			// 添加日志记录
			int operationResult = 0;
			if (message.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))) {
				operationResult = 1;
			} else {
				operationResult = 2;
			}
			confirm.setResult(operationResult);

			this.tunnelPanel.getController().refresh();
			this.dispose();

		} catch (CustomException e) {
			ExceptionManage.dispose(e, this.getClass());
		} catch (NumberFormatException e) {
			ExceptionManage.dispose(e, this.getClass());
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(tunnelServiceMB);
		}
	}
	
	/**
	 * 批量创建tunnel
	 * @param tunnelrole 
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private void createTunnelOnCopy(List<Tunnel> tunnelList, int num, String tunnelrole) throws NumberFormatException, Exception {
		List<QosInfo> qosList = new ArrayList<QosInfo>();
		qosList.add(this.createQos(EQosDirection.FORWARD.getValue() + ""));
		qosList.add(this.createQos(EQosDirection.BACKWARD.getValue() + ""));
		Tunnel tunnel = null;
		for(int i = 0; i < num; i++){
			tunnel = new Tunnel();
			tunnel.setTunnelName(this.tunnelInfo.getTunnelName()+"_copy"+(i+1));
			tunnel.setTunnelStatus(this.tunnelInfo.getTunnelStatus());
			tunnel.setIsReverse(1);
			tunnel.setPosition(1);
			tunnel.setWaittime(this.tunnelInfo.getWaittime());
			tunnel.setDelaytime(this.tunnelInfo.getDelaytime());
			tunnel.setApsenable(this.tunnelInfo.getApsenable());
			tunnel.setTunnelType(this.tunnelInfo.getTunnelType());
			tunnel.setProtectBack(this.tunnelInfo.getProtectBack());
			tunnel.setInBandwidthControl(this.tunnelInfo.getInBandwidthControl());
			tunnel.setOutBandwidthControl(this.tunnelInfo.getOutBandwidthControl());
			tunnel.setSourceMac(this.tunnelInfo.getSourceMac());
			tunnel.setEndMac(this.tunnelInfo.getEndMac());
			tunnel.setaOutVlanValue(this.tunnelInfo.getaOutVlanValue());
			tunnel.setzOutVlanValue(this.tunnelInfo.getzOutVlanValue());
			tunnel.setIsSingle(1);
			tunnel.setCreateUser(this.tunnelInfo.getCreateUser());
			tunnel.setCreateTime(this.tunnelInfo.getCreateTime());
			tunnel.setQosList(qosList);
			if ("xc".equals(tunnelrole)) {
				this.convertXCTunnel(tunnel);
			} else if ("egress".equals(tunnelrole)) {
				this.convertZTunnel(tunnel, true);
			} else {
				this.convertATunnel(tunnel, true);
			}

			if ("2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				this.getProtectTunnel(tunnel, tunnelrole);
				for(Lsp lsp : tunnel.getProtectTunnel().getLspParticularList()){
					lsp.setBackLabelValue(0);
					lsp.setFrontLabelValue(0);
				}
			}
			for(Lsp lsp : tunnel.getLspParticularList()){
				lsp.setBackLabelValue(0);
				lsp.setFrontLabelValue(0);
			}
			tunnelList.add(tunnel);
		}
	}
	
	private void printAttrValue(Object obj){
		
	}

	private QosInfo createQos(String direction) {
		QosInfo info = new QosInfo();
		info.setQosType(this.getQosList().get(0).getQosType());
		info.setCos(this.getQosList().get(0).getCos());
		info.setDirection(direction);
		info.setCir(0);
		info.setCbs(1);
		info.setEir(0);
		info.setEbs(1);
		info.setPir(0);
		return info;
	}

	private Tunnel getTunnelBefore(int tunnelId) {
		TunnelService_MB service = null;
		try {
			service = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			return service.selectId(tunnelId);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		return null;
	}
	
	private void insertLog(PtnButton ptnButton, int operationType, String message, Tunnel tunnelBefore, Tunnel tunnel){
		List<Integer> siteIdList = new ArrayList<Integer>();
		SiteService_MB siteService = null;
		PortService_MB portService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			if(tunnelBefore != null){
				tunnelBefore.setShowSiteAname(siteService.getSiteName(tunnelBefore.getaSiteId()));
				tunnelBefore.setShowSiteZname(siteService.getSiteName(tunnelBefore.getzSiteId()));
				for(Lsp lsp : tunnelBefore.getLspParticularList()){
					lsp.setaSiteName(siteService.getSiteName(lsp.getASiteId()));
					lsp.setzSiteName(siteService.getSiteName(lsp.getZSiteId()));
					lsp.setaPortName(portService.getPortname(lsp.getAPortId()));
					lsp.setzPortName(portService.getPortname(lsp.getZPortId()));
				}
				this.getOamSiteName(tunnelBefore, siteService);
			}
			tunnel.setShowSiteAname(siteService.getSiteName(tunnel.getaSiteId()));
			tunnel.setShowSiteZname(siteService.getSiteName(tunnel.getzSiteId()));
			this.getOamSiteName(tunnel, siteService);
			for(Lsp lsp : tunnel.getLspParticularList()){
				lsp.setaSiteName(siteService.getSiteName(lsp.getASiteId()));
				lsp.setzSiteName(siteService.getSiteName(lsp.getZSiteId()));
				lsp.setaPortName(portService.getPortname(lsp.getAPortId()));
				lsp.setzPortName(portService.getPortname(lsp.getZPortId()));
			}
			for(Lsp lsp : tunnel.getLspParticularList()){
				if(!siteIdList.contains(lsp.getASiteId()) && lsp.getASiteId() > 0){
					siteIdList.add(lsp.getASiteId());
					AddOperateLog.insertOperLog(ptnButton, operationType, message, tunnelBefore, tunnel, lsp.getASiteId(), tunnel.getTunnelName(), "Tunnel");
				}
				if(!siteIdList.contains(lsp.getZSiteId()) && lsp.getZSiteId() > 0){
					siteIdList.add(lsp.getZSiteId());
					AddOperateLog.insertOperLog(ptnButton, operationType, message, tunnelBefore, tunnel, lsp.getZSiteId(), tunnel.getTunnelName(), "Tunnel");
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(portService);
		}
	}
	
	private void getOamSiteName(Tunnel tunnel, SiteService_MB siteService){
		List<OamInfo> oamList = tunnel.getOamList();
		if(oamList != null && oamList.size() > 0){
			for (OamInfo oamInfo : oamList) {
				if(oamInfo.getOamMep() != null){
					if(oamInfo.getOamMep().getSiteId() == tunnel.getASiteId()){
						oamInfo.getOamMep().setSiteName(tunnel.getShowSiteAname());
					}else if(oamInfo.getOamMep().getSiteId() == tunnel.getZSiteId()){
						oamInfo.getOamMep().setSiteName(tunnel.getShowSiteZname());
					}
				}
				if(oamInfo.getOamMip() != null){
					oamInfo.getOamMip().setSiteName(siteService.getSiteName(oamInfo.getOamMip().getSiteId()));
				}
			}
		}
	}

	/**
	 * 获取保护tunnel对象
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * @throws Exception 
	 * @throws NumberFormatException 
	 * 
	 * @Exception 异常对象
	 */
	private void getProtectTunnel(Tunnel tunnelInfo, String tunnelrole) throws NumberFormatException, Exception {
		Tunnel protectTunnel = null;
		if (tunnelInfo.getProtectTunnelId() == 0) {
			if(tunnelInfo.getProtectTunnel() == null){
				tunnelInfo.setProtectTunnel(new Tunnel());
			}
			protectTunnel = tunnelInfo.getProtectTunnel();
			protectTunnel.setTunnelStatus(tunnelInfo.getTunnelStatus());
			protectTunnel.setTunnelName(tunnelInfo.getTunnelName()+"_protect");
			protectTunnel.setZSiteId(tunnelInfo.getZSiteId());
			protectTunnel.setASiteId(tunnelInfo.getASiteId());
			protectTunnel.setQosList(tunnelInfo.getQosList());
			protectTunnel.setTunnelType("0");
			protectTunnel.setIsSingle(1);
			protectTunnel.setPosition(0);
			protectTunnel.setSourceMac(this.sourceMacField_after.getText().trim());
			protectTunnel.setEndMac(this.targetMacField_after.getText().trim());
			protectTunnel.setRotateWay(this.cmbRotateWay.getSelectedItem().toString());
			protectTunnel.setRotateMode(this.cmbRotateMode.getSelectedItem().toString());
			protectTunnel.setRotateThreshold(Integer.parseInt(this.spinnerRotateThreshold.getTxtData()));
			if(protectTunnel.getaOutVlanValue() == 0){
				protectTunnel.setaOutVlanValue(2);
			}
			if(protectTunnel.getzOutVlanValue() == 0){
				protectTunnel.setzOutVlanValue(2);
			}
		} else {
			protectTunnel = tunnelInfo.getProtectTunnel();
		}
		if ("egress".equals(tunnelrole)) {
			this.convertZTunnel(protectTunnel, false);
		} else {
			this.convertATunnel(protectTunnel, false);
		}
		tunnelInfo.setProtectTunnel(protectTunnel);

		if (tunnelInfo.getProtectTunnelId() > 0) {

			if ("egress".equals(tunnelrole)) {
				this.setPortLsp(tunnelInfo.getProtectTunnel(), false, "Z");
			} else {
				this.setPortLsp(tunnelInfo.getProtectTunnel(), false, "A");
			}
		}
	}


	// /**
	// * 转换tunnel保护对象
	// * @throws Exception
	// */
	// private void convertProtect() throws Exception{
	// String type=null;
	// Tunnel tunnel_stand=null;
	// ProtectionInfo protectionInfo=null;
	// int mainSlot=0;
	// int standSlot=0;
	// int mainPort=0;
	// int standPort=0;
	// List<ProtectionInfo> protectionInfoList=null;
	// try {
	// if(null!=this.cmbprotect.getSelectedItem()){
	// tunnel_stand=(Tunnel) ((ControlKeyValue)this.cmbprotect.getSelectedItem()).getObject();
	//
	// protectionInfo=new ProtectionInfo();
	// protectionInfo.setObjProtectType("LSP");
	// protectionInfo.setSiteId(ConstantUtil.siteId);
	// protectionInfo.setProtectionType(2);
	// protectionInfo.setStandbyTunnelID(tunnel_stand.getTunnelId());
	//
	// type=this.getSelectRole();
	// if("ingress".equals(type)){
	// mainSlot=this.tunnelInfo.getAPortId();
	// standSlot=tunnel_stand.getAPortId();
	// mainPort=this.tunnelInfo.getAPortId();
	// standPort=tunnel_stand.getAPortId();
	// }else{
	// mainSlot=this.tunnelInfo.getZPortId();
	// standSlot=tunnel_stand.getZPortId();
	// mainPort=this.tunnelInfo.getZPortId();
	// standPort=tunnel_stand.getZPortId();
	// }
	// protectionInfo.setMainSlot(mainSlot);
	// protectionInfo.setStandbySlot(standSlot);
	// protectionInfo.setMainPort(mainPort);
	// protectionInfo.setStandbyPort(standPort);
	// protectionInfoList=new ArrayList<ProtectionInfo>();
	// protectionInfoList.add(protectionInfo);
	// this.tunnelInfo.setProList(protectionInfoList);
	// }
	// } catch (Exception e) {
	// throw e;
	// }finally{
	//
	// }
	// }

	/**
	 * 验证标签是否可用
	 * 
	 * @return
	 * @throws Exception
	 */
	private String isLabelUsable() throws Exception {
		String result = null;
		LabelInfoService_MB labelInfoServiceMB = null;
		List<Integer> labelValues = null;
		try {

			labelInfoServiceMB = (LabelInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LABELINFO);
			labelValues = this.getLabelValues();
			result = labelInfoServiceMB.select(labelValues, ConstantUtil.siteId,"TUNNEL");
			// 出标签单独验证
			result += this.checkOutLabelUsable();

		} catch (CustomException e) {
			throw e;
		} catch (NumberFormatException e1) {
			throw e1;
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(labelInfoServiceMB);
		}
		return result;
	}

	/**
	 * 获取标签集合
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Integer> getLabelValues() throws Exception {
		List<Integer> labelvalues = null;
		try {
			labelvalues = new ArrayList<Integer>();
			// 如过修改tunnel时，未修改此标签就不用验证，即可入库
			if (Integer.parseInt(this.txtInLable.getText().trim()) != inLabel) {
				this.setLabelValues(labelvalues, Integer.parseInt(this.txtInLable.getText().trim()));
			}
			// 如过修改tunnel时，未修改此标签就不用验证，即可入库
			// if(Integer.parseInt(this.txtOutLable.getText().trim()) != outLabel){
			// this.setLabelValues(labelvalues, Integer.parseInt(this.txtOutLable.getText().trim()));
			// }

			if ("xc".equals(this.getSelectRole()) || "2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				// 如过修改tunnel时，未修改此标签就不用验证，即可入库
				if (Integer.parseInt(this.txtInLable_after.getText().trim()) != inLabel_after) {
					this.setLabelValues(labelvalues, Integer.parseInt(this.txtInLable_after.getText().trim()));
				}
				// 如过修改tunnel时，未修改此标签就不用验证，即可入库
				// if(Integer.parseInt(this.txtOutLable_after.getText().trim()) != outLabel_after){
				// this.setLabelValues(labelvalues, Integer.parseInt(this.txtOutLable_after.getText().trim()));
				// }
			}

		} catch (CustomException e) {
			throw e;
		} catch (NumberFormatException e) {
			throw new NumberFormatException(ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_NUMBER));
		} catch (Exception e) {
			throw e;
		}
		return labelvalues;
	}

	private void setLabelValues(List<Integer> labelvalues, int labelValue) throws Exception {

		if (!labelvalues.contains(labelValue)) {
			labelvalues.add(labelValue);
		} else {
			DialogBoxUtil.errorDialog(this, labelValue + " " + ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_REPEAT));
			throw new CustomException(labelValue + " " + ResourceUtil.srcStr(StringKeysTip.TIP_LABEL_REPEAT));
		}

	}

	/**
	 * 验证出标签是否可用 同一个端口的出标签不能一样
	 * 
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private String checkOutLabelUsable() throws NumberFormatException, Exception {
		int label = 0;
		int label_after = 0;
		ControlKeyValue kv = null;
		ControlKeyValue kv_after = null;
		int portId = 0;
		String result = "";
		LspInfoService_MB lspServiceMB = null;
		try {
			label = Integer.parseInt(this.txtOutLable.getText().trim());
			kv = (ControlKeyValue) this.portComboBox.getSelectedItem();
			
			if ("xc".equals(this.getSelectRole()) || "2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				label_after = Integer.parseInt(this.txtOutLable_after.getText().trim());
				kv_after = (ControlKeyValue) this.portComboBox_after.getSelectedItem();
			}
			
			if ("2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue()) && kv.getName().equals(kv_after.getName())) {
				if (label == label_after) {
					// 标签重复,同一个端口的出标签不能相同
					result = label + ",";
				}
			}
			lspServiceMB = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			// 如过修改tunnel时，未修改此标签就不用验证，即可入库
			if (Integer.parseInt(this.txtOutLable.getText().trim()) != outLabel) {
				portId = Integer.parseInt(kv.getId());
				if (!lspServiceMB.checkOutLabelUsable(portId, label)) {
					result = label + ",";
				}
			}
			
			if ("xc".equals(this.getSelectRole()) || "2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				// 如过修改tunnel时，未修改此标签就不用验证，即可入库
				if (Integer.parseInt(this.txtOutLable_after.getText().trim()) != outLabel_after) {
					portId = Integer.parseInt(kv.getId());
					if (!lspServiceMB.checkOutLabelUsable(portId, label_after)) {
						result += label_after + ",";
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(lspServiceMB);
		}
		return result;
	}

	/**
	 * 验证前后项的对端网元和承载接口是否相�?
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isSiteEqual() throws Exception {
		boolean flag = true;
		ControlKeyValue port_before = null;
		ControlKeyValue port_after = null;
		try {
			if ("xc".equals(this.getSelectRole())) {
				// site_before = (ControlKeyValue) this.remoteSiteComboBox.getSelectedItem();
				// site_after = (ControlKeyValue) this.remoteSiteComboBox_after.getSelectedItem();
				port_before = (ControlKeyValue) this.portComboBox.getSelectedItem();
				port_after = (ControlKeyValue) this.portComboBox_after.getSelectedItem();

				// if (site_before.getId().equals(site_after.getId()) || port_before.getId().equals(port_after.getId())) {
				// flag = false;
				// }
				if (port_before.getId().equals(port_after.getId())) {
					flag = false;
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			port_before = null;
			port_after = null;
		}
		return flag;

	}

	/**
	 * 验证是否完整
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isFull() throws Exception {

		boolean flag = true;
		try {
			if (!QosIsFull()) {
				return false;
			}
			// if (this.qosList == null || this.qosList.size() == 0) {
			// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_QOS_FILL));
			// flag = false;
			// } else
			if ("xc".equals(this.getSelectRole()) || "2".equals(((Code) ((ControlKeyValue) cmbType.getSelectedItem()).getObject()).getCodeValue())) {
				// if (null == remoteSiteComboBox.getSelectedItem() || null == remoteSiteComboBox_after.getSelectedItem()) {
				// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_SITE));
				// flag = false;
				// } else
				if (null == portComboBox.getSelectedItem() || null == portComboBox_after.getSelectedItem()) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
					flag = false;
				}
			} else {
				// if (null == remoteSiteComboBox.getSelectedItem()) {
				// DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_SITE));
				// flag = false;
				// } else
				if (null == portComboBox.getSelectedItem()) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
					flag = false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	private void setPortLsp(Tunnel tunnel, boolean isJob, String type) {
		ControlKeyValue port = null;
		if (isJob) {
			port = (ControlKeyValue) this.portComboBox.getSelectedItem();
		} else {
			port = (ControlKeyValue) this.portComboBox_after.getSelectedItem();
		}

		if (type.equals("A")) {
			tunnel.getLspParticularList().get(0).setAPortId(Integer.parseInt(port.getId()));
		} else if (type.equals("Z")) {
			tunnel.getLspParticularList().get(0).setZPortId(Integer.parseInt(port.getId()));
		}

	}

	/**
	 * 转换Atunnel
	 */
	private void convertATunnel(Tunnel tunnel, boolean isJob) {
		ControlKeyValue port = null;
		String opposite = null;
		int inlabel = 0;
		int outlabel = 0;
		int inTtl = 0;
		int outTtl = 0;
		String sourceMac = "";
		String targetMac = "";
		Lsp lsp = null;
		if (isJob) {
			port = (ControlKeyValue) this.portComboBox.getSelectedItem();
			// site = (ControlKeyValue) this.remoteSiteComboBox.getSelectedItem();
			opposite = this.txtOpposite.getText().trim();
			inlabel = Integer.parseInt(this.txtInLable.getText().trim());
			outlabel = Integer.parseInt(this.txtOutLable.getText().trim());
			inTtl = Integer.parseInt(this.txtInTtl.getText().trim());
			outTtl = Integer.parseInt(this.txtOutTtl.getText().trim());
			sourceMac = sourceMacField.getText();
			targetMac = targetMacField.getText();
		} else {
			port = (ControlKeyValue) this.portComboBox_after.getSelectedItem();
			// site = (ControlKeyValue) this.remoteSiteComboBox_after.getSelectedItem();
			opposite = this.txtOpposite_after.getText().trim();
			inlabel = Integer.parseInt(this.txtInLable_after.getText().trim());
			outlabel = Integer.parseInt(this.txtOutLable_after.getText().trim());
			inTtl = Integer.parseInt(this.txtInTtl_after.getText().trim());
			outTtl = Integer.parseInt(this.txtOutTtl_after.getText().trim());
			sourceMac = sourceMacField_after.getText();
			targetMac = targetMacField_after.getText();
		}

		if (tunnel.getTunnelId() == 0) {
			tunnel.setASiteId(ConstantUtil.siteId);
			// tunnel.setZSiteId(Integer.parseInt(site.getId()));
		}
		tunnel.setAPortId(Integer.parseInt(port.getId()));

		List<Lsp> lspList = new ArrayList<Lsp>();
		if (tunnel.getTunnelId() == 0) {
			lsp = new Lsp();
		} else {
			lsp = tunnel.getLspParticularList().get(0);
		}

		if (tunnel.getTunnelId() == 0) {
			lsp.setASiteId(ConstantUtil.siteId);
			// lsp.setZSiteId(Integer.parseInt(site.getId()));
			if (1 == tunnel.getPosition()) {
				lsp.setPosition(1);
			} else {
				lsp.setPosition(0);
			}
			if (opposite.length() > 0) {
				lsp.setZoppositeId(opposite);
			} else {
				lsp.setZoppositeId("0.0.0.0");
			}
			lsp.setAoppositeId("0.0.0.0");
		}
		lsp.setAPortId(Integer.parseInt(port.getId()));

		lsp.setFrontLabelValue(outlabel);
		lsp.setBackLabelValue(inlabel);
		lsp.setFrontTtl(inTtl);
		lsp.setBackTtl(outTtl);
		lsp.setSourceMac(sourceMac);
		lsp.setTargetMac(targetMac);
		lspList.add(lsp);

		tunnel.setLspParticularList(lspList);
	}

	/**
	 * 转换ztunnel
	 */
	private void convertZTunnel(Tunnel tunnel, boolean isJob) {
		ControlKeyValue port = null;
		String opposite = null;
		int inlabel = 0;
		int outlabel = 0;
		int inTtl = 0;
		int outTtl = 0;
		String sourceMac = "";
		String targetMac = "";

		Lsp lsp = null;
		if (isJob) {
			port = (ControlKeyValue) this.portComboBox.getSelectedItem();
			// site = (ControlKeyValue) this.remoteSiteComboBox.getSelectedItem();
			opposite = this.txtOpposite.getText().trim();
			inlabel = Integer.parseInt(this.txtInLable.getText().trim());
			outlabel = Integer.parseInt(this.txtOutLable.getText().trim());
			inTtl = Integer.parseInt(this.txtInTtl.getText().trim());
			outTtl = Integer.parseInt(this.txtOutTtl.getText().trim());
			sourceMac = sourceMacField.getText();
			targetMac = targetMacField.getText();
		} else {
			port = (ControlKeyValue) this.portComboBox_after.getSelectedItem();
			opposite = this.txtOpposite_after.getText().trim();
			// site = (ControlKeyValue) this.remoteSiteComboBox_after.getSelectedItem();
			inlabel = Integer.parseInt(this.txtInLable_after.getText().trim());
			outlabel = Integer.parseInt(this.txtOutLable_after.getText().trim());
			inTtl = Integer.parseInt(this.txtInTtl_after.getText().trim());
			outTtl = Integer.parseInt(this.txtOutTtl_after.getText().trim());
			sourceMac = sourceMacField_after.getText();
			targetMac = targetMacField_after.getText();
		}

		if (tunnel.getTunnelId() == 0) {
			tunnel.setZSiteId(ConstantUtil.siteId);
		}
		// tunnel.setASiteId(Integer.parseInt(site.getId()));
		tunnel.setZPortId(Integer.parseInt(port.getId()));
		List<Lsp> lspList = new ArrayList<Lsp>();
		if (tunnel.getTunnelId() == 0) {
			lsp = new Lsp();
		} else {
			lsp = tunnel.getLspParticularList().get(0);
		}
		if (tunnel.getTunnelId() == 0) {
			lsp.setZSiteId(ConstantUtil.siteId);
			// lsp.setASiteId(Integer.parseInt(site.getId()));
			if (opposite.length() > 0) {
				lsp.setAoppositeId(opposite);
			} else {
				lsp.setAoppositeId("0.0.0.0");
			}
			if (1 == tunnel.getPosition()) {
				lsp.setPosition(1);
			} else {
				lsp.setPosition(0);
			}
			lsp.setZoppositeId("0.0.0.0");
		}

		lsp.setZPortId(Integer.parseInt(port.getId()));

		lsp.setFrontLabelValue(inlabel);
		lsp.setBackLabelValue(outlabel);
		lsp.setFrontTtl(outTtl);
		lsp.setBackTtl(inTtl);
		lsp.setSourceMac(sourceMac);
		lsp.setTargetMac(targetMac);
		lspList.add(lsp);

		tunnel.setLspParticularList(lspList);
	}

	/**
	 * 转换xc
	 */
	private void convertXCTunnel(Tunnel tunnelInfo) {
		ControlKeyValue port_before = (ControlKeyValue) this.portComboBox.getSelectedItem();
		ControlKeyValue port_after = (ControlKeyValue) this.portComboBox_after.getSelectedItem();
		Lsp lsp_before = new Lsp();
		Lsp lsp_after = new Lsp();
		List<Lsp> lspList = new ArrayList<Lsp>();
		if (tunnelInfo.getTunnelId() == 0) {
			lsp_before = new Lsp();
			lsp_after = new Lsp();
		} else {
			if (tunnelInfo.getLspParticularList().get(0).getZSiteId() == ConstantUtil.siteId) {
				lsp_before = tunnelInfo.getLspParticularList().get(0);
				lsp_after = tunnelInfo.getLspParticularList().get(1);
			}
		}
		if (tunnelInfo.getTunnelId() == 0) {
			lsp_before.setZSiteId(ConstantUtil.siteId);
			// lsp_before.setASiteId(Integer.parseInt(site_before.getId()));
			lsp_before.setZPortId(Integer.parseInt(port_before.getId()));
		}
		lsp_before.setFrontLabelValue(Integer.parseInt(this.txtInLable.getText().trim()));
		lsp_before.setBackLabelValue(Integer.parseInt(this.txtOutLable.getText().trim()));
		lsp_before.setSourceMac(sourceMacField.getText());
		lsp_before.setTargetMac(targetMacField.getText());
		if (tunnelInfo.getTunnelId() == 0) {
			if (this.txtOpposite.getText().trim().length() > 0) {
				lsp_before.setZoppositeId(this.txtOpposite.getText());
			} else {
				lsp_before.setZoppositeId("0.0.0.0");
			}
			lsp_before.setAoppositeId("0.0.0.0");
		}

		lspList.add(lsp_before);

		// lsp_after.setZSiteId(Integer.parseInt(site_after.getId()));
		if (tunnelInfo.getTunnelId() == 0) {
			if (this.txtOpposite_after.getText().trim().length() > 0) {
				lsp_after.setAoppositeId(this.txtOpposite_after.getText());
			} else {
				lsp_after.setAoppositeId("0.0.0.0");
			}
			lsp_after.setZoppositeId("0.0.0.0");
			lsp_after.setASiteId(ConstantUtil.siteId);
			lsp_after.setAPortId(Integer.parseInt(port_after.getId()));
		}

		lsp_after.setFrontLabelValue(Integer.parseInt(this.txtOutLable_after.getText().trim()));
		lsp_after.setBackLabelValue(Integer.parseInt(this.txtInLable_after.getText().trim()));
		lsp_after.setSourceMac(sourceMacField_after.getText());
		lsp_after.setTargetMac(targetMacField_after.getText());
		lspList.add(lsp_after);

		tunnelInfo.setSourceMac(sourceMacField.getText().trim());
		tunnelInfo.setEndMac(targetMacField.getText().trim());
		tunnelInfo.setLspParticularList(lspList);
	}

	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	public void setConfirm(PtnButton confirm) {
		this.confirm = confirm;
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	// public JComboBox getRemoteSiteComboBox() {
	// return remoteSiteComboBox;
	// }
	//
	// public void setRemoteSiteComboBox(JComboBox remoteSiteComboBox) {
	// this.remoteSiteComboBox = remoteSiteComboBox;
	// }

	public JComboBox getPortComboBox() {
		return portComboBox;
	}

	public void setPortComboBox(JComboBox portComboBox) {
		this.portComboBox = portComboBox;
	}

	public Tunnel getTunnelInfo() {
		return tunnelInfo;
	}

	public void setTunnelInfo(Tunnel tunnelInfo) {
		this.tunnelInfo = tunnelInfo;
	}

	public List<QosInfo> getQosList() {
		return qosList;
	}

	public void setQosList(List<QosInfo> qosList) {
		this.qosList = qosList;
	}

//	public static TunnelAddDialog getDialog() {
//		if (tunnelAddDialog == null) {
//			tunnelAddDialog = new TunnelAddDialog(null, null);
//		}
//		return tunnelAddDialog;
//	}

	private JLabel tunnelNameLabel;
	private JLabel lblStatus;
	private JTextField tunnelNameField;
	// private JComboBox remoteSiteComboBox;
	private JComboBox portComboBox;
	private JCheckBox activeCheckBox;
	private JButton qosButton;
	private PtnButton confirm;
	private JButton cancel;
	private JLabel lblPort;
	private JLabel lblRole;
	private JComboBox cmbRole;
	private JLabel lblSite;
	private JLabel lblOutLable;
	private JLabel lblInLable;
	private PtnTextField txtOutLable;
	private PtnTextField txtInLable;
	private JLabel lblQos;
	private JTextField txtQos;
	private JPanel panelTunnel;
	private JPanel panelLsp;
	private JPanel panelButton;
	private JButton btnNext;
	private JButton btnBack;
	private JPanel panelContent;
	private JPanel panelXCbefore;
	private JPanel panelXCafter;
	private JLabel lblPort_after;
	// private JComboBox remoteSiteComboBox_after;
	private JComboBox portComboBox_after;
	private JLabel lblSite_after;
	private JLabel lblOutLable_after;
	private JLabel lblInLable_after;
	private PtnTextField txtOutLable_after;
	private PtnTextField txtInLable_after;
	// private JLabel lblprotect;
	// private JComboBox cmbprotect;
	private JLabel lblMessage;
	private JLabel lblType;
	private JComboBox cmbType;// tunnel的类型:普通,还是 1:1
	private JLabel lblwaitTime;
	private JLabel lbldelayTime;
	private PtnSpinner txtWaitTime;
	private PtnSpinner txtDelayTime;
	private JCheckBox chkAps;
	private JLabel lblAps;
	private PtnTextField txtOpposite;
	private PtnTextField txtOpposite_after;
	private JButton autoNamingButton;
	private JCheckBox protectBack;
	private JLabel protectBackLable;
	private JLabel sourceMac;
	private PtnTextField sourceMacField;
	private JLabel targetMac;
	private PtnTextField targetMacField;
	private JLabel sourceMac_after;
	private PtnTextField sourceMacField_after;
	private JLabel targetMac_after;
	private PtnTextField targetMacField_after;
	private JLabel lblOutTtl;
	private JLabel lblInTtl;
	private PtnTextField txtOutTtl;
	private PtnTextField txtInTtl;
	private JLabel lblOutTtl_after;
	private JLabel lblInTtl_after;
	private PtnTextField txtOutTtl_after;
	private PtnTextField txtInTtl_after;
	private JLabel inBandwidthControl;
	private JCheckBox inBandwidthControlCheckBox;
	private JLabel outBandwidthControl;
	private JCheckBox outBandwidthControlCheckBox;
	//新增主用外层vlan
	private JLabel vlanLabel;
	private JButton vlanButton;
	//新增备用外层vlan
	private JLabel vlanLabel_after;
	private JButton vlanButton_after;
	private JLabel lblNumber;
	private PtnSpinner ptnSpinnerNumber;
	private JLabel lblRotateWay;
	private JComboBox cmbRotateWay;// 倒换准则 SD/SF
	private JLabel lblRotateMode;
	private JComboBox cmbRotateMode;// 倒换模式 人工倒换/自动倒换
	private JLabel lblRotateThreshold;
	private PtnSpinner spinnerRotateThreshold;// 自动倒换阈值(%) 1-100
}
