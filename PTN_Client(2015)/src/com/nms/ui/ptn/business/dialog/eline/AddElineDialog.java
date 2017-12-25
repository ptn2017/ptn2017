/**
 * AddPWDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.business.dialog.eline;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import twaver.Element;
import twaver.Link;
import twaver.TUIManager;
import twaver.TWaverUtil;
import twaver.network.TNetwork;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.qos.QosInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.qos.QosInfoService_MB;
import com.nms.model.util.CodeConfigItem;
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
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TopoAttachment;
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
import com.nms.ui.ptn.business.dialog.tunnel.TunnelTopoPanel;
import com.nms.ui.ptn.business.eline.ElineBusinessPanel;
import com.nms.ui.ptn.oam.view.dialog.OamInfoDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 新建Eline 对话�? * @author __USER__
 */
public class AddElineDialog extends PtnDialog {

	private static final long serialVersionUID = 3473241789496237525L;
	private ElineInfo eline = null;;
	private ElineBusinessPanel elineBsPanel;
	private PortInst portInst_a = null;
	private PortInst portInst_z = null;
	private List<OamInfo> oamList = new ArrayList<OamInfo>();
	private TNetwork network = null;
	private TunnelTopoPanel tunnelTopoPanel;
	private List<PwInfo> batchPwList = new ArrayList<PwInfo>();//批量创建eline时用�?
	/** Creates new form AddPWDialog */
	public AddElineDialog(ElineBusinessPanel jPanel1, boolean modal, ElineInfo elineinfo) {
		try {
			this.setModal(modal);
			this.initComponents();
			this.setLayout();
			
			tunnelTopoPanel=new TunnelTopoPanel();
			
			this.jSplitPane1.setRightComponent(tunnelTopoPanel);
			super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_ELINE));
			network= tunnelTopoPanel.getNetWork();
			
			this.eline = elineinfo;
			this.elineBsPanel = jPanel1;
			this.initPwAndAcComboxData();
			this.clientComboxData();
			this.addListener();
			
			if (this.eline != null) {
				this.ptnSpinnerNumber.setEnabled(false);
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_ELINE));
				if (elineinfo.getOamList().size() != 0) {
					for (OamInfo oamInfo : elineinfo.getOamList()) {
						this.oamtext.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
					}
				} else {
					this.oamtext.setText("megid=0");
				}
				this.jTextField1.setText(this.eline.getName());
				this.pwSelect.setEnabled(false);
				this.ActivejCheckBox1.setSelected(this.eline.getActiveStatus() == EActiveStatus.ACTIVITY.getValue() ? true : false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 初始化组�?	 * 
	 * @throws Exception
	 */
	private void initComponents() throws Exception {
		Dimension dimension = new Dimension(1200, 700);
		this.setSize(dimension);
		this.setMinimumSize(dimension);
		this.lblMessage = new JLabel();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel3 = new javax.swing.JPanel();
		nameL = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		zPortL = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_MARGIN_AC));
		aPortL = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_A_MARGIN_AC));
		activeStatusL = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_ACTIVATED));
		ActivejCheckBox1 = new javax.swing.JCheckBox();
		ActivejCheckBox1.setSelected(true);
		pwNameL = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_NAME));
		pwSelect = new javax.swing.JComboBox();
		pwSelect.setPreferredSize(new Dimension(155, 20));
		aACjComboBox = new javax.swing.JComboBox();
		aACjComboBox.setPreferredSize(new Dimension(155, 20));
		zACjComboBox = new javax.swing.JComboBox();
		zACjComboBox.setPreferredSize(new Dimension(155, 20));
		saveButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		autoNamingButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		oamConfigButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIG));
		oamlable = new JLabel("OAM");
		oamtext = new PtnTextField();
		oamtext.setEnabled(false);
		client = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_NAME));
		clientComboBox = new javax.swing.JComboBox();
		jTextField1 = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.saveButton, this);
		this.lblNumber = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREATE_NUM));
		this.ptnSpinnerNumber = new PtnSpinner(1, 1, 100, 1);
	}

	/**
	 * 界面布局
	 */
	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jSplitPane1.setLeftComponent(this.jPanel3);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 150, 50 };
		layout.columnWeights = new double[] { 0.3, 0, 0 };
		layout.rowHeights = new int[] { 25, 30, 30, 30, 30, 30, 30, 15, 30, 30 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.jPanel3.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel3.add(this.lblMessage);

		/**
		 * 第一�?名称LABLE,文本�?�?�?列合�?		 */

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.nameL, c);
		this.jPanel3.add(this.nameL);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.jTextField1, c);
		this.jPanel3.add(this.jTextField1);
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.autoNamingButton, c);
		this.jPanel3.add(this.autoNamingButton);

		/**
		 * 第二�?PW名称,下拉列表 3�?�?列合�?		 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.pwNameL, c);
		this.jPanel3.add(this.pwNameL);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.pwSelect, c);
		this.jPanel3.add(this.pwSelect);
		/**
		 * 第三�?A端AC,下拉列表 �?列合�?		 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.aPortL, c);
		this.jPanel3.add(this.aPortL);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.aACjComboBox, c);
		this.jPanel3.add(this.aACjComboBox);
		/**
		 * 第四�?Z端AC,下拉列表 �?列合�?		 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.zPortL, c);
		this.jPanel3.add(this.zPortL);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.zACjComboBox, c);
		this.jPanel3.add(this.zACjComboBox);

		/**
		 * 第五�?选中客户
		 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.client, c);
		this.jPanel3.add(this.client);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.clientComboBox, c);
		this.jPanel3.add(this.clientComboBox);

		/**
		 * 第六�?OamLable,文本�?按钮
		 */
		if(CodeConfigItem.getInstance().getWuhan() == 0){
			//晨晓的需要OAM,武汉的不需�?			
			c.gridx = 0;
			c.gridy = 6;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 10, 5, 5);
			layout.setConstraints(this.oamlable, c);
			this.jPanel3.add(this.oamlable);
			c.gridx = 1;
			c.gridy = 6;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 5, 5, 5);
			layout.addLayoutComponent(this.oamtext, c);
			this.jPanel3.add(this.oamtext);
			c.gridx = 2;
			c.gridy = 6;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 5, 5, 5);
			layout.addLayoutComponent(this.oamConfigButton, c);
			this.jPanel3.add(this.oamConfigButton);
			
			/**
			 * 第七�?, 是否激�?单选按�?�?列合�?			 */
			c.gridx = 0;
			c.gridy = 7;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 10, 5, 5);
			layout.setConstraints(this.activeStatusL, c);
			this.jPanel3.add(this.activeStatusL);
			c.gridx = 1;
			c.gridy = 7;
			c.gridheight = 1;
			c.gridwidth = 2;
			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.CENTER;
			layout.addLayoutComponent(this.ActivejCheckBox1, c);
			this.jPanel3.add(this.ActivejCheckBox1);
		}else{
			// �?行，批量创建的数�?			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 6;
			c.gridwidth = 1;
			c.insets = new Insets(5, 10, 5, 5);
			layout.setConstraints(this.lblNumber, c);
			this.jPanel3.add(this.lblNumber);
			c.gridx = 1;
			c.gridwidth = 2;
			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.CENTER;
			layout.addLayoutComponent(this.ptnSpinnerNumber, c);
			this.jPanel3.add(this.ptnSpinnerNumber);
			
			/**
			 * �?�?, 是否激�?单选按�?�?列合�?			 */
			c.gridx = 0;
			c.gridy = 7;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 10, 5, 5);
			layout.setConstraints(this.activeStatusL, c);
			this.jPanel3.add(this.activeStatusL);
			c.gridx = 1;
			c.gridheight = 1;
			c.gridwidth = 2;
			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.CENTER;
			layout.addLayoutComponent(this.ActivejCheckBox1, c);
			this.jPanel3.add(this.ActivejCheckBox1);
		}

		/** 第九�?确定按钮 中间空出一�?*/
		c.gridx = 2;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.saveButton, c);
		this.jPanel3.add(this.saveButton);
	}

	/**
	 * 初始化pw和ac下拉列表
	 */
	@SuppressWarnings("unchecked")
	private void initPwAndAcComboxData() {
		int pwId = 0;
		int aAcId = 0;
		int zAcId = 0;
		if (eline != null) {
			pwId = this.eline.getPwId();
			aAcId = this.eline.getaAcId();
			zAcId = this.eline.getzAcId();
		}
		PwInfoService_MB pwservice = null;
		AcPortInfoService_MB acInfoServiceMB = null;
		List<PwInfo> pwList = null;
		PwInfo pwInfo = null;
		AcPortInfo acInfo = null;
		List<AcPortInfo> acList = null;
		DefaultComboBoxModel pwModel = null;
		DefaultComboBoxModel aAcModel = null;
		DefaultComboBoxModel zAcModel = null;
		ControlKeyValue controlKeyValue = null;
		List<PwInfo> infosNoUser = null;
		ListingFilter filter=null;
		PwInfo info = null;
		try {
			pwModel = (DefaultComboBoxModel) pwSelect.getModel();
			aAcModel = (DefaultComboBoxModel) aACjComboBox.getModel();
			zAcModel = (DefaultComboBoxModel) zACjComboBox.getModel();
			pwservice = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			acInfoServiceMB = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			info = new PwInfo();
			info.setType(EPwType.ETH);
			filter=new ListingFilter();
			pwList = (List<PwInfo>) filter.filterList( pwservice.selectbyType(info));
			infosNoUser = new ArrayList<PwInfo>();
			for (PwInfo pw : pwList) {
				
				if (pw.getRelatedServiceId() == 0) {
					// 显示pw,但保存tunnel对象,为后面显示拓扑图
					pwModel.addElement(new ControlKeyValue(pw.getPwId() + "", pw.getPwName(), pw));
				}
				// 修改eline时添加pw
				if (eline != null) {
					if (pwId == pw.getPwId()) {
						pwModel.addElement(new ControlKeyValue(pw.getPwId() + "", pw.getPwName(), pw));
						pwModel.setSelectedItem(new ControlKeyValue(pw.getPwId() + "", pw.getPwName(), pw));
					}
				}
				if(pw.getRelatedServiceId() == 0){//过滤出未被使用的pw
					infosNoUser.add(pw);
				}
			}
			pwSelect.setModel(pwModel);
			//			
			// 拓扑显示
			if (eline != null) {
				controlKeyValue = (ControlKeyValue) pwSelect.getSelectedItem();
			} else {
				controlKeyValue = (ControlKeyValue) pwSelect.getItemAt(0);
			}
			if(controlKeyValue != null){
				pwInfo = (PwInfo) controlKeyValue.getObject();
			}
			
//			if (pwInfo != null) {
				tunnelTopoPanel.boxData(pwInfo);
//			}

			// 绑定A、Z端的ac
				acInfo = new AcPortInfo();
				// 修改eline时获取siteId
				if (eline != null && pwInfo != null) {
					acInfo.setSiteId(pwInfo.getASiteId());
				} else if(infosNoUser != null && infosNoUser.size() > 0) {
					// 新建eline时获取siteId
					if (infosNoUser.get(0) != null) {
						acInfo.setSiteId(infosNoUser.get(0).getASiteId());
					}
				}
				acInfo.setAcStatus(EActiveStatus.ACTIVITY.getValue());
				acList = acInfoServiceMB.queryByAcPortInfo(acInfo);
				for (AcPortInfo ac : acList) {
					if (ac.getIsUser() == 0) {
						aAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
					// 修改eline时添加ac
					if (eline != null && ac.getId() == aAcId) {
						aAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
						aAcModel.setSelectedItem(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
				}
				aACjComboBox.setModel(aAcModel);

				acInfo = new AcPortInfo();
				// 修改eline时获取siteId
				if (eline != null && pwInfo != null) {
					acInfo.setSiteId(pwInfo.getZSiteId());
					
				} else if(infosNoUser != null && infosNoUser.size() > 0){
					// 新建eline时获取siteId
					if (infosNoUser.get(0) != null) {
						acInfo.setSiteId(infosNoUser.get(0).getZSiteId());
					}
				}
				acInfo.setAcStatus(EActiveStatus.ACTIVITY.getValue());
				acList = acInfoServiceMB.queryByAcPortInfo(acInfo);
				for (AcPortInfo ac : acList) {
					if (ac.getIsUser() == 0) {
						zAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
					// 修改eline时添加ac
					if (eline != null && ac.getId() == zAcId) {
						zAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
						zAcModel.setSelectedItem(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
				}
				zACjComboBox.setModel(zAcModel);
				
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(pwservice);
			UiUtil.closeService_MB(acInfoServiceMB);
		}
	}

	/**
	 * 初始化客户信息下拉列�?	 */
	private void clientComboxData() {

		ClientService_MB service = null;
		List<Client> clientList = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) clientComboBox.getModel();
		try {
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			clientList = service.refresh();
			defaultComboBoxModel.addElement(new ControlKeyValue("0", "", null));
			for (Client clientDef : clientList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(clientDef.getId() + "", clientDef.getName(), clientDef));
			}
			clientComboBox.setModel(defaultComboBoxModel);
			if(null != this.eline){
				super.getComboBoxDataUtil().comboBoxSelect(clientComboBox, this.eline.getClientId()+"");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			clientList = null;
		}
	}

	/**
	 * 添加事件监听
	 */
	private void addListener() {
		pwSelect.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				if (evt.getStateChange() == 1) {
					pwSelectItemStateChanged(evt);
				}
			}
		});

		autoNamingButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonActionPerformed(evt);
			}
		});

		saveButton.addActionListener(new MyActionListener() {

			@Override
			public boolean checking() {
				return checkValue();
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				jButton1ActionPerformed(e);
				
			}
		});

		oamConfigButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				oamConfigButtonActionPerformed(evt);
				if (oamList.size() != 0) {
					for (OamInfo oamInfo : oamList) {
						if(null!=oamInfo.getOamMep().getMegIcc()&&!"".equals(oamInfo.getOamMep().getMegIcc())){
							oamtext.setText("megid=" + oamInfo.getOamMep().getMegIcc() + oamInfo.getOamMep().getMegUmc());
						}else{
							oamtext.setText("megid=" + oamInfo.getOamMep().getMegId());
						}
					}
				} else {
					oamtext.setText("megid=0");
				}
			}
		});
		network.addElementClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Element element = (Element) e.getSource();
				if(element!=null&&element instanceof Link){
					if(element.getUserObject()!=null&&element.getBusinessObject()==null){
						TUIManager.registerAttachment("SegmenttopoTitle", TopoAttachment.class,1, (int) element.getX(), (int) element.getY());
						PwInfo pwinfo =  (PwInfo)element.getUserObject();
						element.setBusinessObject(pwinfo.getPwName());
						element.addAttachment("SegmenttopoTitle");
					}else{
						element.removeAttachment("SegmenttopoTitle");
						element.setBusinessObject(null);
					}    
				}
			}
		});
	}

	/**
	 * oam按钮监听事件
	 * 
	 * @param evt
	 */
	private void oamConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {
		oamConfig();
	}

	/**
	 * oam配置
	 */
	private void oamConfig() {
//		OamAction oamAction = new OamAction();
		ElineInfo elineInfo = new ElineInfo();
		try {
			if(null==aACjComboBox.getSelectedItem()||null==zACjComboBox.getSelectedItem()){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
				return;
			}
			
			
			if(null==this.pwSelect.getSelectedItem()||"".equals(pwSelect.getSelectedItem())){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PWANDACNOMATE_OTHER));
				return;
			}
			
			if(null==this.eline){
				elineInfo.setName(this.jTextField1.getText());
				elineInfo.setaSiteId(((AcPortInfo)((ControlKeyValue) aACjComboBox.getSelectedItem()).getObject()).getSiteId());
				elineInfo.setzSiteId(((AcPortInfo)((ControlKeyValue) zACjComboBox.getSelectedItem()).getObject()).getSiteId());
				elineInfo.setOamList(this.oamList);
			//	oamAction.openOamConfig(EServiceType.ELINE.toString(), elineInfo);
				new OamInfoDialog(elineInfo, "ELINE",0 , true,this);
			}else{
				new OamInfoDialog(eline, "ELINE",0 , true,this);
			//	oamAction.openOamConfig(EServiceType.ELINE.toString(), this.eline);
			}
			//OamController oamController = new OamController();
			//oamController.openOamConfig("ELINE", eline,0);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally {
	//		oamAction = null;
		}
		/*if (!checkPortHasConfig()) {
			return;
		}
		OamConfigController controller = new OamConfigController();
		controller.openOamConfig(controller, "ELINE", eline);*/
	}

	/**
	 * 检查是否填写完�?	 * 
	 * @return
	 */
	private boolean checkPortHasConfig() {
		if (jTextField1.getText().trim().length() == 0 || (ControlKeyValue) pwSelect.getSelectedItem() == null || (ControlKeyValue) aACjComboBox.getSelectedItem() == null || (ControlKeyValue) zACjComboBox.getSelectedItem() == null) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
			return false;
		}
		if (eline == null) {
			eline = new ElineInfo();
			eline.setName(jTextField1.getText());
			eline.setaSiteId(((AcPortInfo) ((ControlKeyValue) aACjComboBox.getSelectedItem()).getObject()).getSiteId());
			eline.setAportId(((AcPortInfo) ((ControlKeyValue) aACjComboBox.getSelectedItem()).getObject()).getPortId());
			eline.setzSiteId(((AcPortInfo) ((ControlKeyValue) zACjComboBox.getSelectedItem()).getObject()).getSiteId());
			eline.setZportId(((AcPortInfo) ((ControlKeyValue) zACjComboBox.getSelectedItem()).getObject()).getPortId());
		}
		return true;
	}

	/**
	 * pw选择事件
	 * 
	 * @param evt
	 */
	private void pwSelectItemStateChanged(java.awt.event.ItemEvent evt) {
		AcPortInfoService_MB acInfoServiceMB = null;
		ControlKeyValue controlKeyValue = null;
		// Tunnel tunnel = null;
		PwInfo pwInfo = null;
		List<AcPortInfo> acList = null;
		DefaultComboBoxModel aAcModel = null;
		DefaultComboBoxModel zAcModel = null;
		AcPortInfo acPortInfo = null;
		try {
			zACjComboBox.removeAllItems();
			aACjComboBox.removeAllItems();
			aAcModel = (DefaultComboBoxModel) aACjComboBox.getModel();
			zAcModel = (DefaultComboBoxModel) zACjComboBox.getModel();
			controlKeyValue = (ControlKeyValue) evt.getItem();

			acInfoServiceMB = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);

			// 拓扑显示
			pwInfo = (PwInfo) controlKeyValue.getObject();
			if (pwInfo != null) {
				tunnelTopoPanel.boxData(pwInfo);

				// 绑定A、Z端的ac
				acPortInfo = new AcPortInfo();
				acPortInfo.setSiteId(pwInfo.getASiteId());
				acPortInfo.setAcStatus(EActiveStatus.ACTIVITY.getValue());
				acList = acInfoServiceMB.queryByAcPortInfo(acPortInfo);
				for (AcPortInfo ac : acList) {
					if (ac.getIsUser() == 0) {
						aAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
					// 修改eline时将ac添加到列表中
					if (eline != null && eline.getPwId() == Integer.parseInt(controlKeyValue.getId()) && eline.getaAcId() == ac.getId()) {
						aAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
						aAcModel.setSelectedItem(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
				}
				aACjComboBox.setModel(aAcModel);
				acPortInfo = new AcPortInfo();
				acPortInfo.setSiteId(pwInfo.getZSiteId());
				acPortInfo.setAcStatus(EActiveStatus.ACTIVITY.getValue());
				acList = acInfoServiceMB.queryByAcPortInfo(acPortInfo);
				for (AcPortInfo ac : acList) {
					if (ac.getIsUser() == 0) {
						zAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
					// 修改eline时将ac添加到列表中
					if (eline != null && eline.getPwId() == Integer.parseInt(controlKeyValue.getId()) && eline.getzAcId() == ac.getId()) {
						zAcModel.addElement(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
						zAcModel.setSelectedItem(new ControlKeyValue(ac.getId() + "", ac.getName(), ac));
					}
				}
				zACjComboBox.setModel(zAcModel);
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoServiceMB);
		}
	}

	/**
	 * 自动命名
	 * 
	 * @param evt
	 */
	private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {
		ControlKeyValue selecttunnel;
		try {
			ElineInfo elineInfo = new ElineInfo();
			// pwInfo = new PwInfo();
			if (null != pwSelect.getSelectedItem()) {
				selecttunnel = (ControlKeyValue) pwSelect.getSelectedItem();
				PwInfo pwInfo = (PwInfo) selecttunnel.getObject();
				pwInfo.setIsSingle(0);
				elineInfo.setaSiteId(pwInfo.getASiteId());
				elineInfo.setzSiteId(pwInfo.getZSiteId());
				AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
				String autoNaming = (String) autoNamingUtil.autoNaming(elineInfo, null, null);
				jTextField1.setText(autoNaming);
			} else {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PORTNULL));
				return;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 保存按钮事件
	 * 
	 * @param evt
	 */
	private void jButton1ActionPerformed(ActionEvent evt) {
		PwInfo pw = null;
		ControlKeyValue aAc = null;
		ControlKeyValue zAc = null;
		ControlKeyValue client = null;
		AcPortInfoService_MB acPortInfoServiceMB = null;
		ElineInfoService_MB elineService = null;
		try {
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			acPortInfoServiceMB = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			int num = Integer.parseInt(this.ptnSpinnerNumber.getTxtData());
			client = (ControlKeyValue) clientComboBox.getSelectedItem();
			String result = "";
			if(num == 1){
				pw = (PwInfo) ((ControlKeyValue) pwSelect.getSelectedItem()).getObject();
				aAc = (ControlKeyValue) aACjComboBox.getSelectedItem();
				zAc = (ControlKeyValue) zACjComboBox.getSelectedItem();
				result = this.saveELineInfo(this.eline, pw, aAc, zAc, client, 0, acPortInfoServiceMB, elineService);
			}else{
				int successNum = 0;
//				DefaultComboBoxModel pwModel = (DefaultComboBoxModel)this.pwSelect.getModel();
				DefaultComboBoxModel aAcModel = (DefaultComboBoxModel)this.aACjComboBox.getModel();
				DefaultComboBoxModel zAcModel = (DefaultComboBoxModel)this.zACjComboBox.getModel();
				for (int i = 0; i < num; i++) {
//					pw = (ControlKeyValue) pwModel.getElementAt(i);
					pw = this.batchPwList.get(i);
					aAc = (ControlKeyValue) aAcModel.getElementAt(i);
					zAc = (ControlKeyValue) zAcModel.getElementAt(i);
					ElineInfo elineInfo = new ElineInfo();
					result = this.saveELineInfo(elineInfo, pw, aAc, zAc, client, i, acPortInfoServiceMB, elineService);
					if(result.contains(ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS))){
						successNum++;
					}
				}
				result = ResourceUtil.srcStr(StringKeysTip.TIP_BATCH_CREATE_RESULT);
				result = result.replace("{C}", successNum + "");
				result = result.replace("{S}", (num - successNum) + "");
			}
			DialogBoxUtil.succeedDialog(this, result);
			this.dispose();
			TWaverUtil.clearImageIconCache();
			if (null != this.elineBsPanel) {
				elineBsPanel.getController().refresh();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(elineService);
			UiUtil.closeService_MB(acPortInfoServiceMB);
		}
	}

	private String saveELineInfo(ElineInfo elineInfo, PwInfo pw, ControlKeyValue aAc, ControlKeyValue zAc, 
			ControlKeyValue client, int i, AcPortInfoService_MB acPortInfoServiceMB, ElineInfoService_MB elineService) {
		List<AcPortInfo> aAcportList = null;
		List<AcPortInfo> zAcportList = null;
		String str = "";
		ClientService_MB clientService = null;
		try {
			elineInfo.setName(jTextField1.getText());
			if(i > 0){
				elineInfo.setName(jTextField1.getText()+"_copy"+i);
			}
			elineInfo.setPwId(pw.getPwId());
			if(elineInfo.getId() > 0){
				//如果修改a段ac 给BeforeAAc赋�?				
				if(0!=elineInfo.getaAcId()&&elineInfo.getaAcId()!=((AcPortInfo) aAc.getObject()).getId()){
					aAcportList = new ArrayList<AcPortInfo>();
					aAcportList.add(acPortInfoServiceMB.selectById(elineInfo.getaAcId()));
					elineInfo.setBeforeAAcList(aAcportList);
					elineInfo.setAction(1);
				}
				//如果修改z段ac 给BeforeZAc赋�?				
				if(0!=elineInfo.getzAcId()&&elineInfo.getzAcId()!=((AcPortInfo) zAc.getObject()).getId()){
					zAcportList = new ArrayList<AcPortInfo>();
					zAcportList.add(acPortInfoServiceMB.selectById(elineInfo.getzAcId()));
					elineInfo.setBeforeZAcList(zAcportList);
					elineInfo.setAction(1);
				}
			}
			if(pw.getASiteId() == ((AcPortInfo) aAc.getObject()).getSiteId()){
				elineInfo.setaAcId(((AcPortInfo) aAc.getObject()).getId());
				elineInfo.setAportId(((AcPortInfo) aAc.getObject()).getPortId());
				elineInfo.setaSiteId(((AcPortInfo) aAc.getObject()).getSiteId());
				elineInfo.setzAcId(((AcPortInfo) zAc.getObject()).getId());
				elineInfo.setZportId(((AcPortInfo) zAc.getObject()).getPortId());
				elineInfo.setzSiteId(((AcPortInfo) zAc.getObject()).getSiteId());
				elineInfo.setaAcName(((AcPortInfo) aAc.getObject()).getName());
				elineInfo.setzAcName(((AcPortInfo) zAc.getObject()).getName());
			}else{
				elineInfo.setaAcId(((AcPortInfo) zAc.getObject()).getId());
				elineInfo.setAportId(((AcPortInfo) zAc.getObject()).getPortId());
				elineInfo.setaSiteId(((AcPortInfo) zAc.getObject()).getSiteId());
				elineInfo.setzAcId(((AcPortInfo) aAc.getObject()).getId());
				elineInfo.setZportId(((AcPortInfo) aAc.getObject()).getPortId());
				elineInfo.setzSiteId(((AcPortInfo) aAc.getObject()).getSiteId());
				elineInfo.setaAcName(((AcPortInfo) zAc.getObject()).getName());
				elineInfo.setzAcName(((AcPortInfo) aAc.getObject()).getName());
			}
			elineInfo.setPwName(pw.getPwName());
			elineInfo.setActiveStatus(ActivejCheckBox1.isSelected() ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
			elineInfo.setServiceType(EServiceType.ELINE.getValue());
			elineInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
			elineInfo.setCreateUser(ConstantUtil.user.getUser_Name());
			if(ActivejCheckBox1.isSelected()){
				elineInfo.setActivatingTime(elineInfo.getCreateTime());
			}else{
				elineInfo.setActivatingTime(null);
			}
			if (!"".equals(client.getId())) 
				elineInfo.setClientId(Integer.parseInt(client.getId()));
			if(((Client)client.getObject()) != null){
				elineInfo.setClientName(((Client)client.getObject()).getName());
			}
			DispatchUtil elineImpl = new DispatchUtil(RmiKeys.RMI_ELINE);
			if (elineInfo.getId() == 0) {
				elineInfo.setOamList(getOamList());
				str = elineImpl.excuteInsert(elineInfo);
				//添加日志记录
				AddOperateLog.insertOperLog(saveButton, EOperationLogType.ELINEINSERT.getValue(), str, 
						null, elineInfo, elineInfo.getaSiteId(), elineInfo.getName(), "eline");
				AddOperateLog.insertOperLog(saveButton, EOperationLogType.ELINEINSERT.getValue(), str, 
						null, elineInfo, elineInfo.getzSiteId(), elineInfo.getName(), "eline");
			} else {
				//添加日志记录
				ElineInfo elineBefore = new ElineInfo();
				elineBefore.setId(elineInfo.getId());
				elineBefore = elineService.selectByCondition(elineBefore).get(0);
				elineBefore.setaAcName(acPortInfoServiceMB.selectById(elineBefore.getaAcId()).getName());
				elineBefore.setzAcName(acPortInfoServiceMB.selectById(elineBefore.getzAcId()).getName());
				elineBefore.setPwName(pw.getPwName());
				clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
				if(elineBefore.getClientId() > 0){
					List<Client> clientList = clientService.select(elineBefore.getClientId());
					if(clientList != null && clientList.size() > 0){
						elineBefore.setClientName(clientList.get(0).getName());
					}
				}
				str = elineImpl.excuteUpdate(elineInfo);
				elineInfo.setBeforeAAcList(null);
				elineInfo.setBeforeZAcList(null);
				AddOperateLog.insertOperLog(saveButton, EOperationLogType.ELINEUPDATE.getValue(), str, 
						elineBefore, elineInfo, elineInfo.getaSiteId(), elineInfo.getName(), "eline");
				AddOperateLog.insertOperLog(saveButton, EOperationLogType.ELINEUPDATE.getValue(), str, 
						elineBefore, elineInfo, elineInfo.getzSiteId(), elineInfo.getName(), "eline");
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(clientService);
		}
		return str;
	}

	private boolean checkValue_nt(){
		boolean flag = false;
		ControlKeyValue pw = null;
		ControlKeyValue aAc = null;
		ControlKeyValue zAc = null;
		pw = (ControlKeyValue) pwSelect.getSelectedItem();
		aAc = (ControlKeyValue) aACjComboBox.getSelectedItem();
		zAc = (ControlKeyValue) zACjComboBox.getSelectedItem();
		AcPortInfo aAcPortInfo = (AcPortInfo) aAc.getObject();
		AcPortInfo zAcPortInfo = (AcPortInfo) zAc.getObject();
		PwInfo pwInfo = (PwInfo)pw.getObject();
		if(pwInfo.getQosList() != null &&  pwInfo.getQosList().size()>0){
			QosInfo qosInfo = pwInfo.getQosList().get(0);
			if(aAcPortInfo.getBufferList().get(0).getCir()>qosInfo.getCir()){
				return flag;
			}
			if(zAcPortInfo.getBufferList().get(0).getCir()>qosInfo.getCir()){
				return flag;
			}
			
		}else{
			return flag;
		}
		
		return true;
	}
	/**
	 * 下发之前验证数据的正确�?true:全部正确�?false：取值验证失�?	
	 *  * @return
	 */
	private boolean checkValue() {
		ControlKeyValue pw = null;
		ControlKeyValue aAc = null;
		ControlKeyValue zAc = null;
		String beforeName = null;
//		AcInfoService acInfoService = null;
		QosInfoService_MB qosInfoServiceMB = null;
		boolean flag = false;
		try {
			pw = (ControlKeyValue) pwSelect.getSelectedItem();
			aAc = (ControlKeyValue) aACjComboBox.getSelectedItem();
			zAc = (ControlKeyValue) zACjComboBox.getSelectedItem();
			
			if (null == pw) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PW));
				return false; 
			}

			if (aAc == null || zAc == null) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_MUSTNETWORK_BEFORE));
				return false;
			}

			if (eline == null) {
				eline = new ElineInfo();
			}

			// 验证名称是否存在
			if (this.eline.getId() != 0) {
				beforeName = this.eline.getName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.ELINE.getValue(), this.jTextField1.getText().trim(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}
			
			//验证pw和ac的qos是否匹配�?			
			if(!checkValue_nt()){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PW_AC_QOS_NOT_MATCHING));
				UiUtil.insertOperationLog(EOperationLogType.TUNNELCREATE9.getValue());//447
				return false;
			}
			//批量创建时，验证ac数量和pw数量是否匹配
			int num = Integer.parseInt(this.ptnSpinnerNumber.getTxtData());
			if(num > 1){
				DefaultComboBoxModel aAcModel = (DefaultComboBoxModel)this.aACjComboBox.getModel();
				int aAcCount = aAcModel.getSize();
				if(aAcCount < num){
					//提示A端AC数量不匹�?					
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_A_AC_IS_NOT_MATCHING));
					return false;
				}
				DefaultComboBoxModel zAcModel = (DefaultComboBoxModel)this.zACjComboBox.getModel();
				int zAcCount = zAcModel.getSize();
				if(zAcCount < num){
					//提示Z端AC数量不匹�?					
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_Z_AC_IS_NOT_MATCHING));
					return false;
				}
				DefaultComboBoxModel pwModel = (DefaultComboBoxModel)this.pwSelect.getModel();
				int pwCount = pwModel.getSize();
				int count = 0;
				int aSiteId = ((PwInfo)pw.getObject()).getASiteId();
				int zSiteId = ((PwInfo)pw.getObject()).getZSiteId();
				for (int i = 0; i < pwCount; i++) {
					ControlKeyValue ckv = (ControlKeyValue) pwModel.getElementAt(i);
					PwInfo pwInfo = (PwInfo) ckv.getObject();
					if((pwInfo.getASiteId() == aSiteId && pwInfo.getZSiteId() == zSiteId) ||
							(pwInfo.getASiteId() == zSiteId && pwInfo.getZSiteId() == aSiteId)){
						this.batchPwList.add(pwInfo);
						count++;
					}
				}
				if(count < num){
					//提示PW数量不匹�?					
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PW_IS_NOT_MATCHING));
					return false;
				}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(qosInfoServiceMB);
//			UiUtil.closeService(acInfoService);
		}
		return flag;
	}
	
	
	public PortInst getPortInst_a() {
		return portInst_a;
	}

	public void setPortInst_a(PortInst portInst_a) {
		this.portInst_a = portInst_a;
	}

	public PortInst getPortInst_z() {
		return portInst_z;
	}

	public void setPortInst_z(PortInst portInst_z) {
		this.portInst_z = portInst_z;
	}

	public List<OamInfo> getOamList() {
		return oamList;
	}

	public void setOamList(List<OamInfo> oamList) {
		this.oamList = oamList;
	}

	private JCheckBox ActivejCheckBox1;
	private JLabel aPortL;
	private JLabel activeStatusL;
	private PtnButton saveButton;
	private JButton autoNamingButton;// 自动命名
	private JPanel jPanel3;
	private JSplitPane jSplitPane1;
	private JTextField jTextField1;
	private JComboBox aACjComboBox;
	private JComboBox zACjComboBox;
	private JLabel nameL;
	private JLabel pwNameL;
	private JComboBox pwSelect;
	private JLabel zPortL;
	private JButton oamConfigButton;
	private JLabel oamlable;
	private JTextField oamtext;
	private JLabel lblMessage;
	private JLabel client;
	private JComboBox clientComboBox;
	private JLabel lblNumber;//创建数量
	private PtnSpinner ptnSpinnerNumber;
}
