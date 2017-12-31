/*
 * dialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.safety.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.bean.system.roleManage.RoleInfo;
import com.nms.db.bean.system.user.UserField;
import com.nms.db.bean.system.user.UserInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.roleManage.RoleInfoService_MB;
import com.nms.model.system.user.UserFieldService_MB;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnPasswordField;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.UserFieldPanel;
import com.nms.ui.ptn.safety.UserInfoPanel;
import com.nms.ui.topology.NetworkElementPanel;
/**
 * 新建用户对话框
 * @author __USER__
 */
public class AddUserDialog extends PtnDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserInst userInst;
	private UserInfoPanel panel;
	private JPanel btnPanel;
	private JLabel roleLabel;
	private JComboBox roleBox;
	private UserFieldPanel userFieldPanel;// 域列表
	private JScrollPane allField;
	private JScrollPane  scrolUserInface;
	// 用户详细信息
	private JLabel lblUserInterface;
	private JTextArea txtAreaUserInterface;
	private JLabel lblPswExpires;
	private JTextField txtPswExpires;
	private JLabel lblRemind;
	private JTextField txtRemind;
	private JLabel startipLabel; // 起始IP名称label
	private JLabel endipLabel; // 终止IPlabel
	private JTextField startipField; // 起始IP文本框
	private JTextField endipField; // 终止IP文本框
	private JLabel levelLbl;// 级别
	private JLabel levelTxt;
	
	
	public AddUserDialog(UserInst userInst, UserInfoPanel panel) {
		this.setModal(true);
		try {
			this.userInst = userInst;
			this.panel = panel;			
			addListener();	
			initComponents();
			setLayout();
			this.bindingData();
			if (userInst.getUser_Id() == 0) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_USER));
			} else {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_USER));
			}
			this.showWindow();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
	}
	/**
	 * 修改时 绑定数据
	 * @throws Exception 
	 */
	private void bindingData() throws Exception{
		if(null != this.userInst && this.userInst.getUser_Id()>0){
			usernametext.setText(this.userInst.getUser_Name());
			userpasswordtext.setText(this.userInst.getUser_Pass());
			beforeuserpass = this.userInst.getUser_Pass();
			startipField.setText(this.userInst.getStartIp());
			endipField.setText(this.userInst.getEndIp());
			userpasswordtextage.setText(this.userInst.getUser_Pass());
			this.txtAreaUserInterface.setText(this.userInst.getUser_Inface());
			this.comboBoxSelectByValue(roleBox, this.userInst.getUser_Group());
			this.txtPswExpires.setText(this.userInst.getPswExpires());
			this.txtRemind.setText(String.valueOf(this.userInst.getBeforeRemind()));
			if(this.userInst.getIsAll()==1){
				this.jCheckBox.setSelected(true);
				this.userFieldPanel.getBox().selectAll();
				this.userFieldPanel.getTree().disable();
			}else{
				List<Integer> fieldList = new ArrayList<Integer>();
				UserFieldService_MB service = null;
				try {
					service = (UserFieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERFIELD);
					List<UserField> userFieldList = service.query(this.userInst.getUser_Id());
					if(userFieldList != null){
						for(UserField uField : userFieldList){
							fieldList.add(uField.getSubId());
						}
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				} finally {
					UiUtil.closeService_MB(service);
				}
			    this.userFieldPanel.checkData(this.userInst.getFieldList(), fieldList);
			}
		}
	}
	
	private void comboBoxSelectByValue(JComboBox jComboBox, String selectValue){
		ControlKeyValue controlKeyValue = null;
		String value = null;
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			controlKeyValue = (ControlKeyValue) jComboBox.getItemAt(i);
			value = ((RoleInfo) controlKeyValue.getObject()).getRoleName();
			if (null != value && value.equals(selectValue)) {// 判断value不为空，过滤条件中默认的所有对应的value为空
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}
	/**
	 * 实例化
	 * @throws Exception
	 */
	private void initComponents() throws Exception {
		RoleInfoService_MB infoService = null;
		try {
			infoService = (RoleInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ROLEINFOSERVICE);
			this.lblMessage = new JLabel();
			this.btnPanel=new JPanel();
			userFieldPanel=new UserFieldPanel();
			userFieldPanel.initTree(userInst);
			this.lblUserInterface=new JLabel(ResourceUtil.srcStr(StringKeysPanel.PANEL_DETAILED_INFORMATION));
			this.txtAreaUserInterface=new JTextArea();
			this.txtAreaUserInterface.setLineWrap(true);//自动换行
			this.scrolUserInface=new JScrollPane (this.txtAreaUserInterface);
			allField = new JScrollPane(this.userFieldPanel);
			scrolUserInface.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			username = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_USERNAME));
			usernametext = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.btnSave, this);
			userpassword = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PASSWORD));
//			userpasswordtext = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
			userpasswordage = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PASSWORD_AFFIRM));
//			userpasswordtextage = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
			//起始，终止IP
			this.startipLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_START_IP));
			this.endipLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_END_IP));
			startipField = new JTextField();
			endipField = new JTextField();
			isAll = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_LOOK_ALLFIELD));		
			roleLabel=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROLE));
			roleBox=new JComboBox();
			//密码过期期限和过期前多少天提醒
			lblPswExpires = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PSW_EXPIRES));
			txtPswExpires = new JTextField();
			txtPswExpires.setText("60");
			lblRemind = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PSW_REMIND));
			txtRemind = new JTextField();
			txtRemind.setText("5");
			this.levelLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL));
			String uName = ConstantUtil.user.getUser_Name();// 当前用户名
			String currName = this.userInst.getUser_Name();// 当前被修改的用户名
			if(currName != null && !"".equals(currName) && ("admin".equals(currName) || "admin1".equals(currName) || "admin2".equals(currName))){
				if("admin".equals(currName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL1));
				}else if("admin1".equals(currName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL2));
				}else if("admin2".equals(currName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL3));
				}
			}else{
				if("admin".equals(uName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL1));
				}else if("admin1".equals(uName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL2));
				}else if("admin2".equals(uName)){
					this.levelTxt = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEVEL3));
				}
			}
			List<RoleInfo> roleInfoList = infoService.select(new RoleInfo());
			if(roleInfoList != null && roleInfoList.size()>0){
				uName = this.userInst.getUser_Name();
				for(int i=0;i< roleInfoList.size();i++){
					RoleInfo info = roleInfoList.get(i);
					String roleName = info.getRoleName();
					if("admin".equals(uName) || "admin1".equals(uName) || "admin2".equals(uName)){
						if(ResourceUtil.language.equals("zh_CN")){
							roleBox.addItem(new ControlKeyValue(info.getId() + "", info.getRoleName(), info));
						}else{
							roleBox.addItem(new ControlKeyValue(info.getId() + "", info.getRoleEnName(), info));
						}
					}else{
						if(!ResourceUtil.srcStr(StringKeysTip.TIP_PROVINCIAL_ADMIN).equals(roleName) && 
								!ResourceUtil.srcStr(StringKeysTip.TIP_MUNICIPAL_ADMIN).equals(roleName)&&
								!ResourceUtil.srcStr(StringKeysTip.TIP_COUNTY_ADMIN).equals(roleName)){
							if(ResourceUtil.language.equals("zh_CN")){
								roleBox.addItem(new ControlKeyValue(info.getId() + "", info.getRoleName(), info));
							}else{
								roleBox.addItem(new ControlKeyValue(info.getId() + "", info.getRoleEnName(), info));
							}
						}
					}
				}
			}
			
			//缺省用户只允许修改密码
			if ("admin".equals(userInst.getUser_Name()) || "admin1".equals(userInst.getUser_Name()) || "admin2".equals(userInst.getUser_Name())){
				usernametext.setEditable(false);
				startipField.setEditable(false);
				endipField.setEditable(false);
				txtPswExpires.setEditable(false);
				txtRemind.setEditable(false);
				roleBox.setEnabled(false);
				txtAreaUserInterface.setEditable(false);
				jCheckBox.setEnabled(false);
				//设置密码的最小长度为5
				userpasswordtext = new PtnPasswordField(true,this.lblMessage,this.btnSave,this,5);
				userpasswordtextage = new PtnPasswordField(true,this.lblMessage,this.btnSave,this,5);
			}else{
				uName = ConstantUtil.user.getUser_Name();// 当前用户名
				if("admin".equals(uName)){
					jCheckBox.setEnabled(true);
				}else{
					jCheckBox.setEnabled(false);
				}
				userpasswordtext = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
				userpasswordtextage = new PtnPasswordField(true,this.lblMessage,this.btnSave,this);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,AddUserDialog.class);
		}finally{
			UiUtil.closeService_MB(infoService);
		}
	}
	/**
	 * 设置按钮布局
	 */
	private void setBtnLayout(){
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths = new int[] { 100, 50, 50 };
		layout.columnWeights=new double[] {0.1,0,0};
		layout.rowHeights=new int[] {40};
		layout.rowWeights =new double[]{0};
		this.btnPanel.setLayout(layout);
		
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=1;
		c.gridy=0;
		c.insets=new Insets(5,5,5,5);
		layout.addLayoutComponent(this.btnSave, c);
		this.btnPanel.add(this.btnSave);
		c.gridx=2;
		layout.addLayoutComponent(this.btnCanel, c);
		this.btnPanel.add(this.btnCanel);
	}
	private void setLayout() {
		this.setBtnLayout();
		Dimension dimension = new Dimension(400, 630);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);

		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 150 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 25,35,35,35,35,35, 35 ,35, 35,35,35,80,80,35};
		layout.rowWeights = new double[] { 0, 0,0, 0,0, 0, 0, 0, 0, 0, 0,0,0.3,0,0.3};
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		/** 第一行 用户名 */
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		add(this.lblMessage);

		/** 第一行 用户名 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 5, 5);
		layout.setConstraints(username, c);
		add(username);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(usernametext, c);
		add(usernametext);

		/** 第二行 用户密码 */
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(userpassword, c);
		add(userpassword);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(userpasswordtext, c);
		add(userpasswordtext);

		/** 第三行 确认密码 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(userpasswordage, c);
		add(userpasswordage);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(userpasswordtextage, c);
		add(userpasswordtextage);
		
		/** 第四行 起始IP */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(startipLabel, c);
		add(startipLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(startipField, c);
		add(startipField);
		
		/** 第五行 终止IP */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(endipLabel, c);
		add(endipLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(endipField, c);
		add(endipField);
		
		/** 第四行 密码过期长度 */
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblPswExpires, c);
		add(lblPswExpires);
		
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtPswExpires, c);
		add(txtPswExpires);
		
		/** 第三行 过期多少天前提醒 */
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.setConstraints(lblRemind, c);
		add(lblRemind);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(txtRemind, c);
		add(txtRemind);
		
		/**
		 * 插入   分配角色
		 * 
		 */
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		layout.setConstraints(roleLabel, c);
		add(roleLabel);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.roleBox, c);
		add(roleBox);
		
		/**
		 * 插入权限级别
		 */
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		layout.setConstraints(levelLbl, c);
		add(levelLbl);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.levelTxt, c);
		add(levelTxt);
		
		/**
		 * 添加  用户 详细信息
		 */
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		layout.setConstraints(this.lblUserInterface, c);
		add(lblUserInterface);
		c.gridx = 1;
		layout.addLayoutComponent(this.scrolUserInface, c);
		add(scrolUserInface);
			
		/** 第5行 是否显示所有域*/
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		layout.setConstraints(isAll, c);
		add(isAll);
		c.gridx = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(jCheckBox, c);
		add(jCheckBox);

		/** 第五行 树 */
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 3;
		layout.setConstraints(allField, c);
		add(allField);

		/** 第七行 确定按钮空出一行 */
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 2;
		c.gridheight=1;
		layout.setConstraints(this.btnPanel, c);
		this.add(this.btnPanel);
	}

	private void showWindow() {
		this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
		this.setVisible(true);
	}
	//事件处理
	private void addListener() {
		jCheckBox = new JCheckBox();
		btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AddUserDialog.this.dispose();
			}
		});

		btnSave.addActionListener(new MyActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddUserDialog.this.savauser();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});

		jCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				userFieldPanel.treeEnable(!jCheckBox.isSelected());
				if(jCheckBox.isSelected()){
				userFieldPanel.getBox().selectAll();
				userFieldPanel.getTree().disable();
				}else{
				userFieldPanel.getTree().enable(true);
				userFieldPanel.getBox().getSelectionModel().clearSelection();
				
				}
			}
		});
	}
	// 保存信息用户信息
	private void savauser() {
		int isSuccess = 0;
		int userid = 0;
		UserInstServiece_Mb userInstServiece = null;
		List<UserInst> userInstList=null;
		List<NetWork> userFieldList=null;
		try {
			userFieldList = this.userFieldPanel.getSelectUserField();
			if (this.userpasswordtext.getText().trim().length() == 0 || this.userpasswordtextage.getText().trim().length() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PASSWORDERROR));
				return;
			}
			if (!this.userpasswordtext.getText().trim().equals(this.userpasswordtextage.getText().trim())) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PASSWORDERROR));
				return;
			}
			
			userInstServiece = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			
			//新建  时
			if(this.userInst.getUser_Id()==0){
				 userInstList=userInstServiece.selectuserid(new UserInst());
				if(null!=userInstList&&userInstList.size()>0){
					for(int i=0;i<userInstList.size();i++){
						UserInst userinst = userInstList.get(i);
						if(userinst.getUser_Name().equals(this.usernametext.getText())){
							DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_EXIST));
							return;
						}
					}
				}
			}else{//修改
				UserInst userSelect = new UserInst();
				userSelect.setUser_Id(this.userInst.getUser_Id());
				userInstList=userInstServiece.selectuserid(userSelect);
				for(int i=0;i < userInstList.size();i++){
					UserInst userinst = userInstList.get(i);
					if(userinst.getUser_Name().equals(this.usernametext.getText())){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_EXIST));
						return;
					}
				}
			}
			this.userInst.setUser_Name(usernametext.getText());
			this.userInst.setUser_Pass(userpasswordtext.getText());
			this.userInst.setStartIp(startipField.getText());
			this.userInst.setEndIp(endipField.getText());
			this.userInst.setPswExpires(txtPswExpires.getText());
			this.userInst.setBeforeRemind(Integer.parseInt(txtRemind.getText()));
			this.userInst.setManagerId(ConstantUtil.user.getUser_Id());
			/*
			 *  如果当前用户是admin1(市级管理员),被改用户是其他，则只能选择一个域
			 *  如果当前用户是admin2(县级管理员),被改用户是其他，则只能选择一个域下的一个组
			 */
			String currName = ConstantUtil.user.getUser_Name();
			String uName = this.userInst.getUser_Name();
			if("admin1".equals(currName) && !"admin1".equals(uName)){
				if(userFieldList.size() > 1){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_ONLY_ONE_NETWORK));
					return;
				}
			}
			if("admin2".equals(currName) && !"admin2".equals(uName)){
				if(userFieldList.size() > 1){
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_ONLY_ONE_FIELD));
					return;
				}
				if(userFieldList.size() == 1){
					List<Field> fieldList = userFieldList.get(0).getFieldList();
					if(fieldList != null && fieldList.size() > 1){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_ONLY_ONE_FIELD));
						return;
					}
				}
			}
			
			RoleInfo roleInfo = (RoleInfo)((ControlKeyValue)this.roleBox.getSelectedItem()).getObject();
			
			this.userInst.setUser_Group(roleInfo.getRoleName());
			this.userInst.setUser_GroupEn(roleInfo.getRoleEnName());
//			if(ResourceUtil.language.equals("zh_CN")){
//			}else{
//				this.userInst.setUser_GroupEn((String)this.roleBox.getSelectedItem());
//			}
			this.userInst.setFieldList(userFieldList);
			this.userInst.setUser_Inface(this.txtAreaUserInterface.getText());
			if(this.userInst.getUser_Inface().length()>(255/4)){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USERINTERFACE_OUTOFLENGH));
				return;
			}
			if (jCheckBox.isSelected() == true) {
				isSuccess = 1;
				this.userInst.setIsAll(isSuccess);
				
				if(this.userInst.getUser_Id() == 0){
				  userid = userInstServiece.saveOrUpdate(userInst);
				  this.insertOpeLog(EOperationLogType.USERINSERT.getValue(), ResultString.CONFIG_SUCCESS, null, null);
				}else{
				  userid = userInstServiece.saveOrUpdate(userInst);	
				  this.insertOpeLog(EOperationLogType.USERUPDATE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
				}
				
			} else {
				isSuccess = 0;
				this.userInst.setIsAll(isSuccess);
				
				if(this.userInst.getUser_Id() == 0){
					userid = userInstServiece.saveOrUpdate(userInst);
					this.insertOpeLog(EOperationLogType.USERINSERT.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
				}else{
					userid = userInstServiece.saveOrUpdate(userInst);	
					this.insertOpeLog(EOperationLogType.USERUPDATE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
				}
				
			}
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.panel.getController().refresh();
			
			//如果修改的是登陆用户，需要刷新拓扑 重新加载域
			if (this.userInst.getUser_Name().equals(ConstantUtil.user.getUser_Name())) {
				NetworkElementPanel.getNetworkElementPanel().showTopo(true);
			}
//			if (this.userInst.getUser_Name().equals("admin") && !(userpasswordtext.getText().equalsIgnoreCase(beforeuserpass))) {
//				System.exit(0);
//			}

			this.dispose();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(userInstServiece);
		}
	}

	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(btnSave, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTab.TAB_USER_INFO),"");		
	}
	
	private PtnButton btnSave;
	private JButton btnCanel;
	private JLabel username;
	private JTextField usernametext;
	private JLabel userpassword;
	private JPasswordField userpasswordtext;
	private String beforeuserpass;
	private JLabel userpasswordage;
	private JPasswordField userpasswordtextage;
	private JLabel isAll;
	private JCheckBox jCheckBox;
	private JLabel lblMessage;
}