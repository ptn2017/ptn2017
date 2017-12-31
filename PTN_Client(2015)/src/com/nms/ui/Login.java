﻿/*
 * Login.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.bean.system.loginlog.UserLock;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.system.loginlog.UserLockServiece_MB;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.ServiceFactory;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.dispatch.rmi.bean.ServiceBean;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.manager.wait.WaitDialogs;
import com.nms.ui.manager.xmlbean.LoginConfig;
import com.nms.ui.ptn.safety.dialog.ModifyPassword;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;
import com.nms.util.Mybatis_DBManager;

/**
 * 
 * @author __USER__
 */
public class Login extends javax.swing.JFrame {

	private static final long serialVersionUID = -749848028657588415L;
	private int count = 0;//用来统计无效帐号的次数 三到五次登录就将产生告警

	/** Creates new form Login */
	public Login() {
		
		try {
			UiUtil.setLogo(this);
			
			this.setTitle("Login");
			initComponents();
			this.addActionListener();
			DefaultComboBoxModel boxModel = (DefaultComboBoxModel) jComboBox1.getModel();
			boxModel.addElement(new ControlKeyValue("zh_CN", "中文"));
			boxModel.addElement(new ControlKeyValue("en_US", "English"));

			this.readLoginInfo();
			if (!txtUsername.getText().equals("")) {
				this.jPasswordField1.requestFocus();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		
		
		
	}

	private void addActionListener() {
		this.jPasswordField1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent evt) {
				if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
					jButton1ActionPerformed();
				}

			}

		});
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel3 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		txtUsername = new javax.swing.JTextField();
		jPasswordField1 = new javax.swing.JPasswordField();
		jComboBox1 = new javax.swing.JComboBox();
		jLabel4 = new javax.swing.JLabel();
		lblService = new javax.swing.JLabel();
		txtServiceIp = new javax.swing.JTextField();

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 100, Short.MAX_VALUE));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(0, 255, 204));
		setResizable(false);

		jPanel1.setBackground(new java.awt.Color(153, 204, 255));
		jPanel1.setForeground(new java.awt.Color(255, 255, 255));

		jLabel1.setFont(new java.awt.Font("微软雅黑", 1, 18));
		jLabel1.setForeground(new java.awt.Color(0, 51, 107));
		jLabel1.setText("\u7528\u6237\u540d\uff1a");

		jLabel2.setFont(new java.awt.Font("微软雅黑", 1, 18));
		jLabel2.setForeground(new java.awt.Color(0, 51, 107));
		jLabel2.setText("\u5bc6   \u7801\uff1a");

		jButton1.setFont(new java.awt.Font("宋体", 0, 14));
		jButton1.setText("\u767b \u5f55");
		jButton1.setPreferredSize(new java.awt.Dimension(100, 30));
		jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {

				jButton1ActionPerformed();

			}
		});

		jButton2.setFont(new java.awt.Font("宋体", 0, 14));
		jButton2.setText("\u53d6 \u6d88");
		jButton2.setPreferredSize(new java.awt.Dimension(100, 30));
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);

			}
		});
		Icon icon = UiUtil.getIcon();
		jLabel3.setIcon(icon); // NOI18N
		txtUsername.setFont(new java.awt.Font("宋体", 0, 14));
		// jTextField1.setText("admin");
		txtUsername.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		txtUsername.setPreferredSize(new java.awt.Dimension(200, 24));

		jPasswordField1.setFont(new java.awt.Font("宋体", 0, 14));
		// jPasswordField1.setText("admin");
		jPasswordField1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		jPasswordField1.setPreferredSize(new java.awt.Dimension(200, 24));

		jComboBox1.addItemListener(new java.awt.event.ItemListener() {

			public void itemStateChanged(java.awt.event.ItemEvent evt) {

				jComboBox1ItemStateChanged(evt);
			}
		});

		jLabel4.setFont(new java.awt.Font("微软雅黑", 1, 18));
		jLabel4.setForeground(new java.awt.Color(0, 51, 107));
		jLabel4.setText("\u9009\u62e9\u8bed\u8a00\uff1a");

		lblService.setFont(new java.awt.Font("微软雅黑", 1, 18));
		lblService.setForeground(new java.awt.Color(0, 51, 107));
		lblService.setText("\u670d\u52a1\u5668\uff1a");

		txtServiceIp.setFont(new java.awt.Font("宋体", 0, 14));
		txtServiceIp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		txtServiceIp.setPreferredSize(new java.awt.Dimension(200, 24));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(32, Short.MAX_VALUE).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(lblService).addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel4)).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE).addComponent(txtServiceIp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(47, 47, 47).addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(22, 22, 22))).addGap(63, 63, 63)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel3).addGap(34, 34, 34).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(15, 15, 15).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel2).addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblService).addComponent(txtServiceIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(33, 33, 33)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
	}// </editor-fold>

	// GEN-END:initComponents

	private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {
		
		if (evt.getStateChange() == 1) {
			ControlKeyValue controlKeyValue = (ControlKeyValue) jComboBox1.getSelectedItem();
			ResourceUtil.language = controlKeyValue.getId();
			if ("English".equals(controlKeyValue.getName())) {
				jLabel1.setText("User");
				jLabel2.setText("Password");
				jLabel4.setText("Language");
				jButton1.setText("Login");
				jButton2.setText("Cancel");
				this.lblService.setText("Service IP");
				Icon icon = UiUtil.getIcon();
				jLabel3.setIcon(icon); // NOI18N
			} else if ("中文".equals(controlKeyValue.getName())) {
				jLabel1.setText("用户名");
				jLabel2.setText("密   码");
				jLabel4.setText("选择语言");
				jButton1.setText("登 录");
				jButton2.setText("取 消");
				this.lblService.setText("服务器地址");
				Icon icon =  UiUtil.getIcon();
				jLabel3.setIcon(icon); // NOI18N
			}
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Login frame = new Login();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension frameSize = frame.getSize();

				if (frameSize.height > screenSize.height)
					frameSize.height = screenSize.height;
				if (frameSize.width > screenSize.width)
					frameSize.width = screenSize.width;

				frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
				frame.setVisible(true);
			}
		});
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	@SuppressWarnings("deprecation")
	private void jButton1ActionPerformed() {
		
		String username = null; // 用户名
		String userpwd = null; // 用户密码
		UserInst userInst = null; // 用户对象
		ServiceFactory serviceFactory = null;
		UserInstServiece_Mb userInstServiece = null;
		UserLockServiece_MB userlockServiece = null;
		LoginLogServiece_Mb loginLogServiece = null;
		List<UserInst> userInfoList = null; // 用户集合
		LoginLog loginlog = null; // 用户登陆日志对象
		UserLock userlock = null; // 用户琐对象
		ControlKeyValue controlKeyValue = (ControlKeyValue) jComboBox1.getSelectedItem();
		boolean loginflag = false; // 用户是否已经登陆
		boolean lockflag = false; // 用户是否已经被锁
		boolean flag = false; // 是否登陆成功
		boolean ipFlag = false;//IP 是否在设置的范围之内
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			// 设置系统语言
			ResourceUtil.language = controlKeyValue.getId();

			username = txtUsername.getText();
			userpwd = jPasswordField1.getText();

			// 验证用户名和密码是否填写
			if (username.length() == 0 || userpwd.length() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_FULL));
				return;
			}
			

			// 验证rmi是否可以通讯
			ConstantUtil.serviceIp = this.txtServiceIp.getText();
			if (!this.verificationRmi()) {
				return;
			}
			Mybatis_DBManager.init(ConstantUtil.serviceIp);
			

			// 初始化服务层
			serviceFactory = new ServiceFactory();
			ConstantUtil.serviceFactory = serviceFactory;

			// 初始化用户锁、登陆日志、用户服务层
			userlockServiece = (UserLockServiece_MB) serviceFactory.newService_MB(Services.USERLOCKSERVIECE);
			loginLogServiece = (LoginLogServiece_Mb) serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			userInstServiece = (UserInstServiece_Mb) serviceFactory.newService_MB(Services.UserInst);
			sameVersion();

			// 根据用户名查询用户
			userInst = new UserInst();
			userInst.setUser_Name(username);
			userInfoList = userInstServiece.select(userInst);

			// 如果没有查询到说明账户名不存在
			if (null == userInfoList || userInfoList.size() == 0) {
				
//				if(count == 3){
//					 DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_SYSTEMLOCK)); 
//					 return ;
//				}else{
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NOT_USER));
//					alarmTools.produceAlarmToLogin(userInst,2,1);
//					count ++;
					return;
//				}
			}
			userInst = (UserInst) userInfoList.get(0);

			// 查询是否为登陆用户
			loginlog = new LoginLog();
			loginlog.setUser_id(userInst.getUser_Id());
			loginflag = loginLogServiece.findLoginLog(loginlog);
			
			// 查询用户是否被锁上
			userlock = new UserLock();
			userlock.setUser_id(userInst.getUser_Id());
			lockflag = userlockServiece.findUserLock(userlock);
			ipFlag = isIpIn(userInst);
			// 如果没有被锁定，就验证是否已经登陆了
			if (!lockflag) {
				// 用户没有登陆
				if (!loginflag) {
					//检查IP是否在接入设置的范围之内
					if(ipFlag)
					{
						//是否密码过期
						if(!isDeadtime(userInst))
						{
							//如果密码没过期，则判断是否已经到了过期前多少天提示，如果到期则提示，如果没到期，则不提示
							if(isRemindtime(userInst))
							{
								//如果已经到提醒时间
								ModifyPassword modify = new ModifyPassword(userInst,"before");
								if(modify.isSucess())
								{
									ConstantUtil.user = modify.getUserInst();
									flag = true;
								}
								else
								{
									return;
								}
							}
							else
							{
								// 如果密码相等。登陆成功
								if (userInst.getUser_Pass().equals(userpwd)) {
									ConstantUtil.user = userInst;
									flag = true;
								}
							}
						}
						else
						{
							//如果密码过期，则要求重新修改密码
							ModifyPassword modify =new ModifyPassword(userInst,"after");
							if(modify.isSucess())
							{
								ConstantUtil.user = modify.getUserInst();
								flag = true;
							}
							else
							{
								return;
							}
						}
					}
					else
					{
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIT_IP_IN));
						return;
					}
						
				} else {
					// 用户已经登陆 并且密码相等
					if (userInst.getUser_Pass().equals(userpwd)) {

						// 弹出是否强制登陆，如果点击确认，就把此用户在线数据踢掉，重新登陆
						int result = DialogBoxUtil.confirmDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_LONGING));
						if (result == 0) {
//							loginlog.setLogoutState(1);
							loginLogServiece.updateExitLoginLog(loginlog, 1);
							ConstantUtil.user = userInst;
							flag = true;
						}else {
							return;
						}
					}
				}
			}
			// 如果登陆成功
			if (flag) {
				RoleRoot roleRoot=new RoleRoot();
				//将此用户  ，角色   权限存到内存中
				ConstantUtil.roleRelevanceList=roleRoot.rootPrivy();
				loginlog.setState(1);
				loginlog.setLoginState(2);
				LoginLog log = loginLogServiece.insertSuccessLoginLog(loginlog);
				ConstantUtil.loginTime = log.getLoginTime();
				this.setLoginInfo();
				//加载设备类型到内存中。
				equimentDataUtil.loadEquipmentType();
				Ptnf main = Ptnf.getPtnf();
				main.setExtendedState(Frame.MAXIMIZED_BOTH);
				count = 0;//将统计用户错误的标志 归为零
				this.dispose();
			} else {
				// 账户名或者密码不对时
				if (loginflag && lockflag) {
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_LOG_LOCK));
				} else if (lockflag) {// 用户已被锁定
					UserLock lock = userlockServiece.selectLockType(userlock);
					if (lock.getLockType() == 1) {// 管理员锁定
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_ADMINLOCK));
					} else {// 系统自动锁定
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_SYSTEMLOCK));
					}
				} else {
					// 密码错误，更新锁时间
					//插入一条日志记录
					//产生告警
					if(UiUtil.isNotAdmin()){
						loginlog.setState(0);
						loginlog.setLoginState(1);
						loginLogServiece.insertSuccessLoginLog(loginlog);
				    }
					int label = userlockServiece.updateLockTime(userlock, userInst);
					//当用用户密码输入小于4次时都将会产生密码错误告警;当大于4次时且用户名不是admin时将产生用户被锁告警
					if(label < 4){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_PASSWORK_ERROR));
//						alarmTools.produceAlarmToLogin(userInst,1,1);
					 }else if(label >3 && UiUtil.isNotAdmin()){
						 DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_SYSTEMLOCK)); 
//						 alarmTools.produceAlarmToLogin(userInst,3,1);
					  }else{
						  DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_USER_PASSWORK_ERROR));
					  }
					
				}
			}

		} catch (CommunicationsException e) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_ERROR));
		} catch (SQLException e) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_ERROR));
		} catch (Exception e) {
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_ERROR));
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(userInstServiece);
			UiUtil.closeService_MB(userlockServiece);
			UiUtil.closeService_MB(loginLogServiece);
		}
	}

	/**
	 * 检测密码是否过期
	 * @param user
	 * @return
	 */
	private boolean isDeadtime(UserInst user)
	{
		boolean flag = false;
		String currName = user.getUser_Name();
		if("admin".equals(currName) || "admin1".equals(currName) || "admin2".equals(currName))
		{
			return flag;
		}
		
		Calendar c = Calendar.getInstance();
		long currentTime = c.getTimeInMillis();
		long deadTime = Long.parseLong(user.getDeadTime());
		if(currentTime > deadTime)
		{
			flag = true;
		}
		
		return flag;
	}
	
	private boolean isIpIn(UserInst user)
	{
		boolean flag = false;
		String currName = user.getUser_Name();
		if("admin".equals(currName) || "admin1".equals(currName) || "admin2".equals(currName))
		{
			return true;
		}
		String serIp = txtServiceIp.getText().trim();
		if(serIp.equals("127.0.0.1"))
		{
			InetAddress addr;
			try
			{
				addr = InetAddress.getLocalHost();
				serIp=addr.getHostAddress().toString();//获得本机IP
			}
			catch (UnknownHostException e)
			{
				ExceptionManage.dispose(e,this.getClass());
			}
			
		}
		String startIp = user.getStartIp();
		String endIp = user.getEndIp();
		//如果起始和结束IP未设置，则默认为最小和最大，允许登陆
		if(startIp == null || startIp.equals(""))
		{
			startIp="0.0.0.0";
		}
		if(endIp == null || endIp.equals(""))
		{
			endIp = "255.255.255.255";
		}
		
		flag = validateIP(startIp,endIp,serIp);
		
		return flag;
	}
	
	private boolean validateIP(String startIP,String endIP, String realIP)
    {
        // 分离出ip中的四个数字位
        String[] startIPArray = startIP.split("\\.");
        String[] endIPArray = endIP.split("\\.");
        String[] realIPArray = realIP.split("\\.");

        // 取得各个数字
        long[] startIPNum = new long[4];
        long[] endIPNum = new long[4];
        long[] realIPNum = new long[4];
        for (int i = 0; i < 4; i++)
        {
            startIPNum[i] = Long.parseLong(startIPArray[i].trim());
            endIPNum[i] = Long.parseLong(endIPArray[i].trim());
            realIPNum[i] = Long.parseLong(realIPArray[i].trim());
        }

        // 各个数字乘以相应的数量级
        long startIPNumTotal = startIPNum[0] * 256 * 256 * 256 + startIPNum[1] * 256 * 256 + startIPNum[2] * 256 + startIPNum[3];
        long endIPNumTotal = endIPNum[0] * 256 * 256 * 256 + endIPNum[1] * 256 * 256 + endIPNum[2] * 256 + endIPNum[3];
        long realIPNumTotal = realIPNum[0] * 256 * 256 * 256 + realIPNum[1] * 256 * 256 + realIPNum[2] * 256 + realIPNum[3];
        if (realIPNumTotal >= startIPNumTotal && realIPNumTotal <= endIPNumTotal)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
	
	private boolean sameVersion(){
		boolean flag = false;
		DispatchUtil otherDispatch;
		try {
			otherDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			String serverVersion = otherDispatch.getVersion();
			LoginUtil loginUtil=new LoginUtil();
			LoginConfig loginConfig = loginUtil.readLoginConfig();
			if(!(loginConfig.getVersion()).equals(serverVersion)){
				int result = DialogBoxUtil.confirmDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_UPDATE_CLIENT));
				if(result == JOptionPane.NO_OPTION){
					return false;
				}else{
					this.updateVersion(serverVersion);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		
		
		return flag;
	}
	
	private void updateVersion(String version) throws IOException{
	    LoginConfig loginConfig=new LoginConfig();
	    loginConfig.setVersion(version);
		LoginUtil loginUtil=new LoginUtil();
		loginUtil.writeLoginConfig(loginConfig);			
		WaitDialogs wait=new WaitDialogs();
		wait.showDialog(this);
		DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_RESET_CLIENT));
		System.exit(0);
}
	
	private boolean isRemindtime(UserInst user)
	{
		boolean flag = false;
		String currName = user.getUser_Name();
		if("admin".equals(currName) || "admin1".equals(currName) || "admin2".equals(currName))
		{
			return flag;
		}
		Calendar c = Calendar.getInstance();
		int days = user.getBeforeRemind();
		long currentTime = c.getTimeInMillis();
		long deadTime = Long.parseLong(user.getDeadTime());
		deadTime = deadTime - days*24*60*60*1000;
		if(currentTime > deadTime)
		{
			flag = true;
		}
		
		return flag;
	}
	
	/**
	 * 连接rmi 返回最大连接数等，验证是否可以登陆网管
	 * 
	 * @return true 通过验证 false 没通过
	 * @throws Exception 
	 * @throws Exception
	 */
	private boolean verificationRmi() throws Exception {
		DispatchUtil dispatchUtil = null;
		ServiceBean serviceBean = null;
		boolean flag = false;
		try {
			dispatchUtil = new DispatchUtil(RmiKeys.RMI_INIT);
			serviceBean = dispatchUtil.init();

			if (null != serviceBean) {
				// 1为成功
				if (serviceBean.getConnectionResult() == 1) {
					ConstantUtil.serviceBean = serviceBean;
					flag = true;
				} else if (serviceBean.getConnectionResult() == 2) {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_MAXDATA));
				} else if (serviceBean.getConnectionResult() == 3) {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_LAST));
				}
			}

		} catch (java.rmi.ConnectException e) {
			ExceptionManage.dispose(e,this.getClass());
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONNECTION_ERROR));
		} finally {
			dispatchUtil = null;
			serviceBean = null;
		}
		return flag;
	}

	/**
	 * 保存登陆信息
	 */
	private void setLoginInfo(){
		LoginConfig loginConfig=new LoginConfig();
		loginConfig.setServiceIp(this.txtServiceIp.getText());
		loginConfig.setUsername(this.txtUsername.getText());
		LoginUtil loginUtil=new LoginUtil();
		loginUtil.writeLoginConfig(loginConfig);
	}
	
	/**
	 * 读取登陆信息并且赋到文本框中
	 */
	private void readLoginInfo(){
		LoginUtil loginUtil=new LoginUtil();
		LoginConfig loginConfig = loginUtil.readLoginConfig();
		this.txtServiceIp.setText(loginConfig.getServiceIp());
		this.txtUsername.setText(loginConfig.getUsername());
	}
	
	// ImageIcon icon = new
	// ImageIcon("/com/nms/ui/images/banner_01.png");
	// Image image = null;
	// public void paint(Graphics g) {
	// super.paint(g);
	// image = icon.getImage();
	// g.drawImage(image, 0, 0, null);
	// }

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPasswordField jPasswordField1;
	public javax.swing.JTextField txtUsername;
	private javax.swing.JLabel lblService;
	public javax.swing.JTextField txtServiceIp;
	// End of variables declaration//GEN-END:variables

}