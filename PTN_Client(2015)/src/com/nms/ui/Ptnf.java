﻿package com.nms.ui;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import twaver.TUIManager;
import twaver.TWaverConst;
import twaver.TWaverUtil;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.bean.system.user.UserInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.system.PerformanceRamService_MB;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.CodeConfigItem;
import com.nms.model.util.Services;
import com.nms.rmi.ui.AutoDatabaseBackThradUtil;
import com.nms.rmi.ui.AutoDatabaseBackThread;
import com.nms.rmi.ui.util.ServerConstant;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.datamanager.databackup.DataBackupJDialog;
import com.nms.ui.datamanager.datarecover.DataRecoverJDialog;
import com.nms.ui.lockscreen.HandLockDialog;
import com.nms.ui.lockscreen.controller.HandLockThreadFactory;
import com.nms.ui.lockscreen.controller.HandLockTimerTask;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TopoAttachment;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.manager.xmlbean.LoginConfig;
import com.nms.ui.ptn.alarm.tca.TCAAlarmPanel;
import com.nms.ui.ptn.alarm.view.AlarmBusinessBlockDialog;
import com.nms.ui.ptn.alarm.view.AlarmReversalPanel;
import com.nms.ui.ptn.alarm.view.AlarmVoiceDialog;
import com.nms.ui.ptn.alarm.view.CircleButton;
import com.nms.ui.ptn.alarm.view.CurrentAlarmPanel;
import com.nms.ui.ptn.alarm.view.HisAlarmPanel;
import com.nms.ui.ptn.alarm.view.TopoAlamTable;
import com.nms.ui.ptn.basicinfo.NetWorkInfoPanel;
import com.nms.ui.ptn.basicinfo.SegmentPanel;
import com.nms.ui.ptn.basicinfo.SetNameRulePanel;
import com.nms.ui.ptn.basicinfo.dialog.group.view.GroupManagerPanel;
import com.nms.ui.ptn.basicinfo.dialog.site.SelectSiteDialog1;
import com.nms.ui.ptn.basicinfo.dialog.site.SiteSearchDialog;
import com.nms.ui.ptn.basicinfo.dialog.subnet.view.SubnetManagerPanel;
import com.nms.ui.ptn.business.ces.CesBusinessPanel;
import com.nms.ui.ptn.business.dual.DualBusinessPanel;
import com.nms.ui.ptn.business.elan.ElanBusinessPanel;
import com.nms.ui.ptn.business.eline.ElineBusinessPanel;
import com.nms.ui.ptn.business.etree.EtreeBusinessPanel;
import com.nms.ui.ptn.business.loopProtect.LoopProtectPanel;
import com.nms.ui.ptn.business.pw.PwBusinessPanel;
import com.nms.ui.ptn.business.serviceRepaired.NNIPortMovedDialog;
import com.nms.ui.ptn.business.serviceRepaired.UNIPortMovedDialog;
import com.nms.ui.ptn.business.tunnel.AutoCorrectionDialog;
import com.nms.ui.ptn.business.tunnel.TunnelBusinessPanel;
import com.nms.ui.ptn.client.view.ClientManagerPanel;
import com.nms.ui.ptn.configinfo.AlarmDescPanel;
import com.nms.ui.ptn.event.oamEvent.view.OamEventPanel;
import com.nms.ui.ptn.help.AboutHelp;
import com.nms.ui.ptn.help.VersionInfoDialog;
import com.nms.ui.ptn.patchConfig.PatchManagerDialog;
import com.nms.ui.ptn.performance.model.PerformanceRAMInfo;
import com.nms.ui.ptn.performance.view.CurrPerformCountPanel;
import com.nms.ui.ptn.performance.view.CurrentPerformancePanel;
import com.nms.ui.ptn.performance.view.HisPerformancePanel;
import com.nms.ui.ptn.performance.view.PathPerformCountPanel;
import com.nms.ui.ptn.performance.view.PerformanceDescPanel;
import com.nms.ui.ptn.performance.view.PerformanceStoragePanel;
import com.nms.ui.ptn.performance.view.PerformanceTaskPanel;
import com.nms.ui.ptn.safety.LogManagerPanel;
import com.nms.ui.ptn.safety.LoginLogPanel;
import com.nms.ui.ptn.safety.OperationLogPanel;
import com.nms.ui.ptn.safety.SystemLogManagerPanel;
import com.nms.ui.ptn.safety.UserInfoPanel;
import com.nms.ui.ptn.safety.UserOnLinePanel;
import com.nms.ui.ptn.safety.roleManage.RoleManagePanel;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.statistics.alarm.AlarmInfoPanel;
import com.nms.ui.ptn.statistics.business.ProfessPanel;
import com.nms.ui.ptn.statistics.card.CardInfoPanel;
import com.nms.ui.ptn.statistics.lable.LableInfoPanel;
import com.nms.ui.ptn.statistics.layerRate.LayerRateCountPanel;
import com.nms.ui.ptn.statistics.nepathstatics.NePathNumStatisticsPanel;
import com.nms.ui.ptn.statistics.path.PathStatisticsWidthPanel;
import com.nms.ui.ptn.statistics.pathstatics.PathNumStatisticsPanel;
import com.nms.ui.ptn.statistics.performance.PerformanceInfoPanel;
import com.nms.ui.ptn.statistics.prot.LspPortPanel;
import com.nms.ui.ptn.statistics.prot.PortInfoPanel;
import com.nms.ui.ptn.statistics.sement.SegmentStatisticsPanel;
import com.nms.ui.ptn.statistics.sement.SegmentStatisticsWidthPanel;
import com.nms.ui.ptn.statistics.site.SiteCountStatisticsPanel;
import com.nms.ui.ptn.statistics.site.SpecificSiteStatisticsPanel;
import com.nms.ui.ptn.statistics.slot.SlotStatisticsPanel;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;
import com.nms.ui.ptn.systemManage.SystemConfigView;
import com.nms.ui.ptn.systemManage.monitor.SystemMontorConfigPanel;
import com.nms.ui.ptn.systemManage.view.UnLoadingDeletePanel;
import com.nms.ui.ptn.systemManage.view.UnLoadingPanel;
import com.nms.ui.ptn.systemconfig.CodePanel;
import com.nms.ui.ptn.systemconfig.NeConfigView;
import com.nms.ui.ptn.systemconfig.QosTemplatePanel;
import com.nms.ui.ptn.systemconfig.UdaGroupPanel;
import com.nms.ui.ptn.systemconfig.UploadDownloadConfigurePanel;
import com.nms.ui.ptn.systemconfig.Template.view.ExpMappingPanel;
import com.nms.ui.ptn.systemconfig.Template.view.PriorityToVlanpriPanel;
import com.nms.ui.ptn.systemconfig.Template.view.VlanpriToColorPanel;
import com.nms.ui.ptn.systemconfig.dialog.bsUpdate.view.BatchSoftwareUpgradePanel;
import com.nms.ui.ptn.systemconfig.dialog.siteInitialise.SiteInitialiseConfig;
import com.nms.ui.ptn.telnetManage.TelnetDialog;
import com.nms.ui.ptn.userDesgin.UserDesginJDialog;
import com.nms.ui.thread.AlarmBtnColorThread;
import com.nms.ui.thread.AlarmSoundThread;
import com.nms.ui.thread.RefreshTopoAlarmThread;
import com.nms.ui.thread.RefreshTopoThread;
import com.nms.ui.thread.UserLogoutThread;
import com.nms.ui.thread.WaitWindowThread;
import com.nms.ui.topology.NetworkElementPanel;
import com.nms.util.OpenHelpManual;

/**
 * 
 * @author __USER__
 */

public class Ptnf extends javax.swing.JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Ptnf ptnf;
	public boolean handLockScreen = true;
	public long startTime = 0;
	public HandLockDialog handLockDialog = null;
	public SelectSiteDialog1 selectSiteDialog = null;
	// 保存设备上推的告警，保存到内存中 currentAlarmMap中map的key为网元的数据库id
	private Map<Integer, Map<String, CurrentAlarmInfo>> currentAlarmMap = new HashMap<Integer, Map<String, CurrentAlarmInfo>>();

	private AlarmBtnColorThread alarmColorThread = null;//告警灯闪烁线程
	
	private UserLogoutThread userLogoutThread = null; //检测用户注销线程
	
	public UserLogoutThread getUserLogoutThread() {
		return userLogoutThread;
	}


	private boolean alarmSoundSwitch = true;//告警声音线程开关

	private boolean isToop = false;
	/** Creates new form Ptnf */
	static {
		TUIManager.registerAttachment("topoTitle", TopoAttachment.class);
	}

	private Ptnf() {

		try {
			ptnf = this;

			// 设置twaver的语言
			TWaverUtil.setLocale(ResourceUtil.language.equals("zh_CN") ? TWaverConst.ZH_CN : TWaverConst.EN_US);

			UiUtil.setLogo(this);
			
			this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PTN_CLIENT));
			// setCapability();
			// 初始化控件
			initComponents();
			this.initToolBarButton();
			toolBarLayout();
			this.addPanelChangeListener();

		
			// 加载拓扑tab
			ConstantUtil.jTabbedPane = this.jTabbedPane1;
			this.mainTabPanel(jTabbedPane1, ResourceUtil.srcStr(StringKeysTab.TAB_TOPO), NetworkElementPanel.getNetworkElementPanel());
			// 软件版本号和登陆用户显示
			LoginUtil loginUtil=new LoginUtil();
			LoginConfig loginConfig = loginUtil.readLoginConfig();
			this.jLabel1.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_VERSIONS) + loginConfig.getVersion());
//			this.jLabel1.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_VERSIONS) + ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL2_VERSIONS));
			logLabel = ResourceUtil.srcStr(StringKeysLbl.LBL_LOGINUSER) + ConstantUtil.user.getUser_Name();
			this.loginLabel.setText(logLabel);
            this.neCountLable.setText(necount);
			this.setVisible(true);

			// 定时来自动的锁屏
			setHandLockSreccen();    
			// 告警声音线程
			startTime = new Date().getTime();
			selectSiteDialog = new SelectSiteDialog1(true);
			// 用来监听网管系统的所有的键盘事件
			getToolkit().addAWTEventListener(new AWTEventListener() {
				@Override
				public void eventDispatched(AWTEvent event) {
					handLockScreen = false;
					startTime = new Date().getTime();
					 if ( ((KeyEvent) event).isControlDown()&&((KeyEvent) event).getKeyCode()==KeyEvent.VK_F) {
						   jMenuItemSelectNeActionPerformed();
				        }					
			     }				
			}, AWTEvent.KEY_EVENT_MASK);
			 
			// 用来监听网管系统的所有的鼠标事件
			getToolkit().addAWTEventListener(new AWTEventListener() {
				@Override
				public void eventDispatched(AWTEvent event) {
					handLockScreen = false;
					startTime = new Date().getTime();
				}
			}, AWTEvent.MOUSE_EVENT_MASK);
			
			// 检测用户是否被注销线程
			userLogoutThread=new UserLogoutThread();
			new Thread(this.userLogoutThread).start(); 
			
			// 初始化等待框
			WaitWindowThread waitWindowThread = new WaitWindowThread();
			ConstantUtil.waitWindowThread = waitWindowThread;

			// 定时刷新网元连接情况
			new Thread(new RefreshTopoThread()).start();
			//设置自动转储告警和性能
			setBackAlarmAndPerformance();
			
			alarmColorThread = new AlarmBtnColorThread(this.btnToolAlarmUrgency,this.btnToolAlarmMajor, this.btnToolAlarmMinor, this.btnToolAlarmPrompt);
			new Thread(this.alarmColorThread).start(); 
				
			//用户是否设置了性能文件的监控
//			createMonitorPerformanceRam();
			// 定时刷新拓扑告警
			new Thread(new RefreshTopoAlarmThread(this)).start();
			
			handLockDialog = new HandLockDialog();	
			
			//加载数据库中的性能对照表
			this.initCapability();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 加载数据库中的性能对照表
	 */
	private void initCapability() {
		CapabilityService_MB capabilityService = null;
		try {
			if(ConstantUtil.capabilityMap == null){
				ConstantUtil.capabilityMap = new HashMap<String, Capability>();
				capabilityService = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
				List<Capability> capabilityList = capabilityService.select();
				if(capabilityList != null){
					for (Capability capability : capabilityList) {
						ConstantUtil.capabilityMap.put(capability.getManufacturer() + "/" + capability.getCapabilitycode(), capability);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(capabilityService);
		}
	}

	/**
	 * 监控性能文件
	 */
	private void createMonitorPerformanceRam() {
		//开启自动的默认备份数据 不影响其他的启动线程任务 2个小时查询一次
		long cycleTime = 2*60*60*1000;
		AutoDatabaseBackThradUtil autoDatabaseBackThradUtil = new AutoDatabaseBackThradUtil();
		PerformanceRamService_MB performanceRamService = null;
		PerformanceRAMInfo performanceRAMInfo = null;
		try {
			performanceRamService = (PerformanceRamService_MB)ConstantUtil.serviceFactory.newService_MB(Services.PERFORMANCERAM);
			performanceRAMInfo = performanceRamService.select(ConstantUtil.user.getUser_Name());
			if(performanceRAMInfo !=null && (performanceRAMInfo.getTimeSelect().equals("true") || performanceRAMInfo.getRamPerformanceSelect().equals("true"))){
				autoDatabaseBackThradUtil.createThread(cycleTime);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			 UiUtil.closeService_MB(performanceRamService);
			 autoDatabaseBackThradUtil = null;
		}
	}

	/**
	 * 如果当前面板是主拓扑,就刷新一下主拓扑
	 */
	private void addPanelChangeListener() {
		this.jTabbedPane1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Component comp = jTabbedPane1.getSelectedComponent();
				isToop = false;
				if(comp instanceof NetworkElementPanel){
					try {
						isToop = true;
						((NetworkElementPanel)comp).showTopo(true);
						showCurrSiteStatus(false);
					} catch (Exception e1) {
						ExceptionManage.dispose(e1,this.getClass());
					}
				}else if(comp instanceof NetWorkInfoPanel){
					try {
						showCurrSiteStatus(true);
					} catch (Exception e2) {
						ExceptionManage.dispose(e2, this.getClass());
					}
				}else if(comp instanceof SegmentPanel){
					try {
						((SegmentPanel)comp).getController().refresh();
					} catch (Exception e2) {
						ExceptionManage.dispose(e2, this.getClass());
					}
				}else{
					showCurrSiteStatus(false);
				}
			}
		});
	}
    /**
     * 设置性能和告警的自动转储
     */
	private void setBackAlarmAndPerformance(){
		
		List<UnLoading> unloadList = null;
		UnLoading unLoading = null;
		try {
			unloadList = ReadUnloadXML.selectUnloadXML();
			if(unloadList != null && unloadList.size() >0){
				for(int i = 0; i< unloadList.size() ; i++){
					unLoading = unloadList.get(i);
					//自动备份告警
					if(unLoading.getUnloadType() == 1){
						//配置文件配置了自动转储
						if(unLoading.getIsAuto() ==1 ){
							//转储时间
							unLoading.setTimeInterval(unLoading.getTimeInterval());
							if("".equals(unLoading.getFileWay())){
								unLoading.setFileWay(ServerConstant.AUTODATABACKALARM_FILE);
							}
							if("".equals(unLoading.getAutoStartTime())){
								unLoading.setAutoStartTime(DateUtil.getDate(DateUtil.FULLTIME));
							}
							autoBackAlarmAndPerformance(unLoading);
						}
					}
					//自动备份性能
					else if(unLoading.getUnloadType() == 2){
						//配置文件配置了自动转储
						if(unLoading.getIsAuto() ==1 ){
							unLoading.setTimeInterval(unLoading.getTimeInterval());
							if("".equals(unLoading.getAutoStartTime())){
							    unLoading.setAutoStartTime(DateUtil.getDate(DateUtil.FULLTIME));
							}
							if("".equals(unLoading.getFileWay())){
							    unLoading.setFileWay(ServerConstant.AUTODATABACKPM_FILE);
							}
							autoBackAlarmAndPerformance(unLoading);
						}
					}
					//自动备份操作日志
					else if(unLoading.getUnloadType() == 3){
						if(unLoading.getIsAuto() ==1 ){
							unLoading.setTimeInterval(unLoading.getTimeInterval());
							if("".equals(unLoading.getAutoStartTime())){
							    unLoading.setAutoStartTime(DateUtil.getDate(DateUtil.FULLTIME));
							}
							if("".equals(unLoading.getFileWay())){
							    unLoading.setFileWay(ServerConstant.AUTODATABACKOPERATION_FILE);
							}
							autoBackAlarmAndPerformance(unLoading);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			unloadList = null;
		}
	}
	
	/**
	 * 当服务器启动后根据键盘和鼠标事件来判断是否锁屏
	 * 
	 * @throws Exception
	 */
	private void setHandLockSreccen() throws Exception {
		HandLockThreadFactory lockSreccen = null;
		try {
			lockSreccen = HandLockThreadFactory.getInstance();
			HandLockTimerTask task = new HandLockTimerTask();
			lockSreccen.createLockThread(task, 60 * 1000);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			lockSreccen = null;
		}
	}

	public static Ptnf getPtnf() {

		if (null == ptnf) {
			synchronized (Ptnf.class) {
				if (ptnf == null) {
					ptnf = new Ptnf();
				}
			}
		}
		return ptnf;
	}
	
	/**
	 * 自动转储历史告警和历史性能
	 * @param unload
	 */
	private void autoBackAlarmAndPerformance(UnLoading unload){
		
		try {
			AutoDatabaseBackThread autoThread = new AutoDatabaseBackThread(System.currentTimeMillis(),unload);
			String threadName = "task_UnLoading_"+ unload.getUnloadType();
			Thread thread = new Thread(autoThread,threadName);
			if(threadMap != null && threadMap.size() >0){
				if(threadMap.get(thread.getName()) != null){
					((AutoDatabaseBackThread)threadMap.get(thread.getName())).stop();
					threadMap.remove(thread.getName());
				}
			}
			thread.start();
			threadMap.put(thread.getName(), autoThread);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 刷新告警灯
	 */
	public void refreshAlarmNum(CurAlarmService_MB curAlarmService) {
		int urgencyNum = 0;
		int majorNum = 0;
		int minorNum = 0;
		int promptNum = 0;
		try {
			urgencyNum = curAlarmService.queryCurrAlarmBylevel(5);
			majorNum = curAlarmService.queryCurrAlarmBylevel(4);
			minorNum = curAlarmService.queryCurrAlarmBylevel(3);
			promptNum = curAlarmService.queryCurrAlarmBylevel(2);

			// 给工具栏的告警灯后的值赋值
			this.lblToolAlarmUrgencyNum.setText(urgencyNum + "");
			this.lblToolAlarmMajorNum.setText(majorNum + "");
			this.lblToolAlarmMinorNum.setText(minorNum + "");
			this.lblToolAlarmPromptNum.setText(promptNum + "");
			this.alarmColorThread.setUrgencycolor(urgencyNum);
			this.alarmColorThread.setMajorcolor(majorNum);
			this.alarmColorThread.setMinorcolor(minorNum);
			this.alarmColorThread.setPromptcolor(promptNum);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}
	}

	private void toolBarLayout() {
		this.jToolBar1.setLayout(new BorderLayout());
		JPanel lefTool = new JPanel(new FlowLayout());
		JPanel rightTool = new JPanel(new FlowLayout());
		JPanel jToolBar2 = new JPanel();
		jToolBar1.add(lefTool, BorderLayout.WEST);
		jToolBar2.add(rightTool, BorderLayout.WEST);
		jToolBar2.setPreferredSize(new Dimension(350, 0));
		jToolBar1.add(jToolBar2, BorderLayout.EAST);
		lefTool.add(this.btnToolsExit);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsAlarm);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsSegment);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsTunnel);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsPw);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsEline);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsEtree);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsElan);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsCes);
		lefTool.add(new JLabel());
		lefTool.add(new JLabel());
		/** 第一行 menu */
		lefTool.add(this.btnToolsWrapping);
		
		rightTool.add(this.btnToolAlarmUrgency);
		rightTool.add(this.lblToolAlarmUrgencyNum);
		rightTool.add(this.btnToolAlarmMajor);
		rightTool.add(this.lblToolAlarmMajorNum);
		rightTool.add(this.btnToolAlarmMinor);
		rightTool.add(this.lblToolAlarmMinorNum);
		rightTool.add(this.btnToolAlarmPrompt);
		rightTool.add(this.lblToolAlarmPromptNum);
	}

	/**
	 * 初始化工具栏的按钮
	 */
	private void initToolBarButton() {
		final RoleRoot roleRoot=new RoleRoot();
		// 退出系统按钮
		btnToolsExit = this.createToolBtn("32_exit.png", ResourceUtil.srcStr(StringKeysTitle.TIT_EXIT_SYSTEM));
		btnToolsExit.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				exitSystem();
			}
		});

		// 当前告警按钮
		btnToolsAlarm = this.createToolBtn("32_list.png", ResourceUtil.srcStr(StringKeysTab.TAB_CURRALARM));
		btnToolsAlarm.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsAlarm, RootFactory.ALARMMODU)) {
					currAlarmActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});

		// 段列表按钮
		btnToolsSegment = this.createToolBtn("32_section.png", ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENT));
		btnToolsSegment.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsSegment, RootFactory.TOPOLOGYMODU)) {
					jMenuItem9ActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});

		// TUNNEL列表按钮
		btnToolsTunnel = this.createToolBtn("32_tunnel.png", ResourceUtil.srcStr(StringKeysTab.TAB_TUNNEL));
		btnToolsTunnel.setMaximumSize(new Dimension(36, 26));
		btnToolsTunnel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsTunnel, RootFactory.COREMODU)) {
					jMenuItem7ActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});

		// PW列表按钮
		btnToolsPw = this.createToolBtn("32_PW.png", ResourceUtil.srcStr(StringKeysTab.TAB_PWINFO));
		btnToolsPw.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsPw, RootFactory.COREMODU)) {
					jMenuItem8ActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});
		// Eline列表按钮
		btnToolsEline = this.createToolBtn("32_eline.png", ResourceUtil.srcStr(StringKeysTab.TAB_ELINEINFO));
		btnToolsEline.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsEline, RootFactory.COREMODU)) {
					elineMenuItemActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// etree列表按钮
		btnToolsEtree = this.createToolBtn("32_etree.png", ResourceUtil.srcStr(StringKeysTab.TAB_ETREEINFO));
		btnToolsEtree.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsEtree, RootFactory.COREMODU)) {
					etreeMenuItemActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// elan列表按钮
		btnToolsElan = this.createToolBtn("32_elan.png", ResourceUtil.srcStr(StringKeysTab.TAB_ELANINFO));
		btnToolsElan.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsElan, RootFactory.COREMODU)) {
					elanMenuItemActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// ces列表按钮
		btnToolsCes = this.createToolBtn("32_CES.png", ResourceUtil.srcStr(StringKeysTab.TAB_CESINFO));
		btnToolsCes.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsCes, RootFactory.COREMODU)) {
					cesMenuItemActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});
		// 环网保护列表按钮
		btnToolsWrapping = this.createToolBtn("32_huan.png", ResourceUtil.srcStr(StringKeysTab.TAB_WRAPPING));
		btnToolsWrapping.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolsWrapping, RootFactory.COREMODU)) {
					wrappingMenuItemActionPerformed();
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// 紧急告警按钮
		btnToolAlarmUrgency = new CircleButton();
		btnToolAlarmUrgency.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolAlarmUrgency, RootFactory.ALARMMODU)) {
					showAlarmList(5);
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// 主要告警按钮
		btnToolAlarmMajor = new CircleButton();
		btnToolAlarmMajor.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolAlarmMajor, RootFactory.ALARMMODU)) {
					showAlarmList(4);
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}

			}
		});

		// 次要告警按钮
		btnToolAlarmMinor = new CircleButton();
		btnToolAlarmMinor.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolAlarmMinor, RootFactory.ALARMMODU)) {
					showAlarmList(3);
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});

		// 提示告警按钮
		btnToolAlarmPrompt = new CircleButton();
		btnToolAlarmPrompt.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 添加 权限验证
				if (roleRoot.setItemEnbale(btnToolAlarmPrompt, RootFactory.ALARMMODU)) {
					showAlarmList(2);
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return;
				}
			}
		});
	}

	
	/**
	 * 设置图片按钮
	 * 
	 * @param imageName
	 *            图标名称
	 * @param title
	 *            提示语
	 * @return
	 */
	private JButton createToolBtn(String imageName, String title) {
		JButton btn = new JButton();

		btn.setToolTipText(title);
		btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/nms/ui/images/tools/" + imageName)));
		btn.setFocusable(false);
		btn.setContentAreaFilled(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setOpaque(false);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setBorder(null);
		return btn;
	}

	private void initComponents() {
		jToolBar1 = new javax.swing.JPanel();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel3 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jMenuBar2 = new javax.swing.JMenuBar();
		menuSystem = new javax.swing.JMenu();
		jMenu13 = new javax.swing.JMenu();
		jMenuItem9 = new javax.swing.JMenuItem();
		jMenuSetName= new javax.swing.JMenuItem();
		jMenu14 = new javax.swing.JMenu();
		jMenuItem3 = new javax.swing.JMenuItem();
		jMenuItem4 = new javax.swing.JMenuItem();
		qosTemplateMenuItem = new javax.swing.JMenuItem();
		nameRuleMenuItem = new JMenuItem();
		NeConfigMenuItem = new javax.swing.JMenuItem();
		SubnetConfigMenuItem = new javax.swing.JMenuItem();
		groupConfigMenuItem = new JMenuItem();
		jMenu15 = new javax.swing.JMenu();
		jMenuItem7 = new javax.swing.JMenuItem();
		jMenuItem8 = new javax.swing.JMenuItem();
		CESMenuItem = new javax.swing.JMenuItem();
		elineMenuItem = new javax.swing.JMenuItem();
		elanMenuItem = new javax.swing.JMenuItem();
		etreeMenuItem = new javax.swing.JMenuItem();
		jMenu16 = new javax.swing.JMenu();
		jMenu1 = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		currAlarmMenuItem = new JMenuItem();
		hisAlarmMenuItem = new JMenuItem();
		jMenu17 = new javax.swing.JMenu();
		jMenuItem2 = new javax.swing.JMenuItem();
		currPerMenuItem = new JMenuItem();
		hisPerMenuItem = new JMenuItem();
		perTaskMenuItem = new JMenuItem();
		currPerformCountMenuItem = new JMenuItem();
		siteAlarmShieldItem = new JMenuItem();
		jMenu18 = new javax.swing.JMenu();
		jMenu19 = new javax.swing.JMenu();
		jMenuItem5 = new javax.swing.JMenuItem();
		jMenuLogin= new javax.swing.JMenu();// 登陆管理
		jMenuLoginLog = new JMenuItem();// 日志
		jMenuImport=new JMenuItem();
		jMenuOperation = new JMenuItem();
		jMenuOperationLog = new javax.swing.JMenuItem();// 操作日志查询
		jMenuRoleManage = new javax.swing.JMenuItem();// 角色管理
		jMenuUserOnLine = new javax.swing.JMenuItem();// 在线
		jMenu20 = new javax.swing.JMenu();
		jMenu22 = new javax.swing.JMenu();
		jMenu23 = new javax.swing.JMenu();   //数据管理
		dataBackupsItem = new javax.swing.JMenuItem(); //数据备份
		dataRecoverItem = new javax.swing.JMenuItem(); //数据恢复
		jMenuItem10 = new javax.swing.JMenuItem();
		jMenuItem11 = new javax.swing.JMenuItem();
		jMenuItem12 = new javax.swing.JMenuItem();
		jMenuItem13 = new javax.swing.JMenuItem();
		// menuItemClearLock = new javax.swing.JMenuItem();
		uploadDownloadConfigure = new JMenuItem();
		siteInitialise = new JMenuItem();
		jMenuItemCard = new javax.swing.JMenuItem();
		jMenuItemProfess=new javax.swing.JMenuItem();
		slotMenuItem = new JMenuItem();
		jMenuItemAlarm = new javax.swing.JMenuItem();
		jMenuItemPerfor = new javax.swing.JMenuItem();
		jMenuItemLable = new javax.swing.JMenuItem();
		jMenuItemPort = new javax.swing.JMenuItem();
		jMenuItemPath = new javax.swing.JMenuItem();//端到端路径数量
		jMenuItemNePath = new javax.swing.JMenuItem();//单网元路径数量
		siteCountMenuItem = new JMenuItem();//网元统计
		userDesign = new javax.swing.JMenuItem();
		help = new javax.swing.JMenuItem();// 帮助
		about = new javax.swing.JMenuItem();// 关于
		versionInfo = new javax.swing.JMenuItem();// 版本信息
		clientManagerMenuItem = new javax.swing.JMenuItem();		
		this.menuItemLogout = new JMenuItem(); // 退出系统
		this.unloadItem = new JMenuItem();// 转储管理
		this.menuItemCloseAlarm = new JMenuItem(); // 关闭告警声音
		this.handLock = new JMenuItem();// 手动锁屏
		this.searchSiteMenuItem = new JMenuItem();
		this.alarmVoiceMenuItem = new JMenuItem();// 告警声音设置
		this.alarmReversal = new JMenuItem();//告警反转
		this.loginLabel = new JLabel();
		this.neCountLable = new JLabel();
		this.oamEventMenuItem = new JMenuItem();// oam事件
		this.tcaAlarmMenuIten = new JMenuItem(); // tca告警
		modelManagerMenuItem = new javax.swing.JMenu(); // 模板管理
		cosToExpMenuItem = new JMenuItem(); // COS到EXP映射
		vlanpriToColorMenuItem = new JMenuItem(); // VLANPRI到颜色映射
		priorityToVlanpriMenuItem = new JMenuItem(); // 优先业务到VLANPRI映射
		this.DualInfoItem = new JMenuItem();// 双归保护管理
		cirCount = new JMenuItem();//链路CIR统计
		performanceMenuItem = new JMenuItem();
		this.lblToolAlarmUrgencyNum = new JLabel();
		this.lblToolAlarmUrgencyNum.setText("0");
		this.lblToolAlarmMajorNum = new JLabel();
		this.lblToolAlarmMajorNum.setText("0");
		this.lblToolAlarmMinorNum = new JLabel();
		this.lblToolAlarmMinorNum.setText("0");
		this.lblToolAlarmPromptNum = new JLabel();
		this.lblToolAlarmPromptNum.setText("0");
		RoleRoot roleRoot=new RoleRoot();
		ethService = new JMenu();//以太网业务分类
		this.serviceRepairedMenu = new JMenu();//业务修复
		this.NNIPortMovedMenuItem = new JMenuItem();//NNI端口迁移
		this.UNIPortMovedMenuItem = new JMenuItem();//UNI端口迁移
		siteCount = new JMenu();//网元统计
		alarmCount = new JMenu();//告警统计
		performanceCount = new JMenu();//性能统计
		ethServiceCount = new JMenu();//业务统计
		this.jMenuItemPathPerfor = new JMenuItem();//端到端性能统计
		this.layerRateMenuItem = new JMenuItem();//层速率统计
		systemMonitorBaseCount = new JMenuItem();//数据库运行记录
		this.currSiteLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.CURR_SITE_STATUS));
		this.currSiteBtn = new JLabel();
		this.showCurrSiteStatus(false);//不显示当前网元状态
		batchSoftwateUpdate = new JMenuItem();//批量升级网元
		this.selectNe = new JMenuItem();// 搜索网元
		this.telnetManage = new JMenuItem();//telnet设置
		this.loadPatchItem = new JMenuItem();//加载补丁
		this.unLoadPatchItem = new JMenuItem();//卸载补丁
		this.autoCorrectionItem = new JMenuItem();
		this.unloadDeleteItem = new JMenuItem();// 转储管理
		this.systemConfigItem = new JMenuItem();// 系统配置管理
		this.systemLogItem = new JMenuItem();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(191, 213, 235));
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				exitSystem();			
			}
		});

		// 底部布局（版本号+登录用户）
		// jTabbedPane1.setBackground(new java.awt.Color(191, 213,235));
		jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel3.setLayout(new BorderLayout());
		JPanel rootLabel = new JPanel(new FlowLayout());
		rootLabel.add(jLabel1);
		rootLabel.add(new JLabel());
		rootLabel.add(loginLabel);
		rootLabel.add(neCountLable);
		rootLabel.add(this.currSiteLabel);
		rootLabel.add(this.currSiteBtn);
		JPanel rootLabel1 = new JPanel();
		rootLabel1.setLayout(new BorderLayout());
		rootLabel1.add(rootLabel, BorderLayout.WEST);
		jPanel3.add(rootLabel1, BorderLayout.WEST);

		jMenuBar2.setBackground(new java.awt.Color(191, 213, 235));

		// 系统管理
		menuSystem.setBackground(new java.awt.Color(191, 213, 235));
		menuSystem.setMnemonic(KeyEvent.VK_S);
		menuSystem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_SYSTEM_MANAGE));
		
		// 关闭告警声音
		this.menuItemCloseAlarm.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_ALARMSOUND_OPEN));
		this.menuItemCloseAlarm.setMnemonic(KeyEvent.VK_M);
		this.menuItemCloseAlarm.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String state = menuItemCloseAlarm.getText();
				if(state.equals(ResourceUtil.srcStr(StringKeysMenu.MENU_ALARMSOUND_OPEN))){
					//如果是点击开启声音按钮,判断:有告警则开启线程,没有告警不开启
					CurAlarmService_MB curAlarmService = null;
					int urgencyNum = 0;
					int majorNum = 0;
					int minorNum = 0;
					int promptNum = 0;
					try {
						curAlarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
						urgencyNum = curAlarmService.queryCurByAlarmlevel(5);
						majorNum = curAlarmService.queryCurByAlarmlevel(4);
						minorNum = curAlarmService.queryCurByAlarmlevel(3);
						promptNum = curAlarmService.queryCurByAlarmlevel(2);
					}catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					} finally {
						UiUtil.closeService_MB(curAlarmService);
					}
					if(urgencyNum > 0 || majorNum > 0 || minorNum > 0 || promptNum > 0){
						alarmSoundSwitch = true;
						AlarmSoundThread alarmSoundThread = new AlarmSoundThread();
						new Thread(alarmSoundThread).start();
					}
					menuItemCloseAlarm.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_ALARMSOUND_CLOSE));
					this.insertOpeLog(EOperationLogType.OPENALARMSOUND.getValue(), ResultString.CONFIG_SUCCESS, null, null);		
				}else{
					//如果是点击关闭声音按钮,则关闭线程
					alarmSoundSwitch = false;
					menuItemCloseAlarm.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_ALARMSOUND_OPEN));
					menuItemCloseAlarm.setMnemonic(KeyEvent.VK_K);
					this.insertOpeLog(EOperationLogType.CLOSEALARMSOUND.getValue(), ResultString.CONFIG_SUCCESS, null, null);
				}
			}
			
			private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
				AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTip.TIP_ALARM_SOUND),"");		
			}
		});
		this.menuSystem.add(this.menuItemCloseAlarm);
		
		
		//加载补丁
		this.loadPatchItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_LOAD_PATCH));
		this.loadPatchItem.setMnemonic(KeyEvent.VK_Y);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.loadPatchItem, RootFactory.SYSTEMMODU);
		this.loadPatchItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new PatchManagerDialog(ResourceUtil.srcStr(StringKeysMenu.MENU_LOAD_PATCH));
			}
		});
		this.menuSystem.add(this.loadPatchItem);	
		//卸载补丁
		this.unLoadPatchItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOAD_PATCH));
		this.unLoadPatchItem.setMnemonic(KeyEvent.VK_Y);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.unLoadPatchItem, RootFactory.SYSTEMMODU);
		this.unLoadPatchItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new PatchManagerDialog(ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOAD_PATCH));
			}
		});
		this.menuSystem.add(this.unLoadPatchItem);

		// 转储管理
		this.unloadItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOADING_T));
		this.unloadItem.setMnemonic(KeyEvent.VK_T);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.unloadItem, RootFactory.SYSTEMMODU);
		this.unloadItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				unloadActionPerformed();
			}
		});
		this.menuSystem.add(this.unloadItem);
		
		this.unloadDeleteItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_DELETE_LOG));
		this.unloadDeleteItem.setMnemonic(KeyEvent.VK_D);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.unloadDeleteItem, RootFactory.SYSTEMMODU);
		this.unloadDeleteItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				unloadDeleteActionPerformed();
			}
		});
		this.menuSystem.add(this.unloadDeleteItem);	
		//数据管理
		//数据备份
		this.dataBackupsItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_BACKUP_DATA));
		this.dataBackupsItem.setMnemonic(KeyEvent.VK_T);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.dataBackupsItem, RootFactory.SYSTEMMODU);
		this.dataBackupsItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemDataBackupActionPerformed();
			}
		});
		//数据恢复
		this.dataRecoverItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_RECOVER_DATA));
		this.dataRecoverItem.setMnemonic(KeyEvent.VK_T);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.dataRecoverItem, RootFactory.SYSTEMMODU);
		this.dataRecoverItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemDataRecoverActionPerformed();
			}
		});
		this.jMenu23.setText(ResourceUtil.srcStr(StringKeysTab.TAB_DATA_MANAGE));
		this.jMenu23.setMnemonic(KeyEvent.VK_T);
		this.jMenu23.add(this.dataBackupsItem);
		this.jMenu23.add(this.dataRecoverItem);
		this.menuSystem.add(this.jMenu23);
		// 菜单条：用户个性化设置
		this.userDesign.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_USERDESIGN_T));
		this.userDesign.setMnemonic(KeyEvent.VK_C);
		this.userDesign.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemUserDesginActionPerformed();
			}
		});
		this.menuSystem.add(this.userDesign);

        // 菜单条：telnet设置
		this.telnetManage.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_TELNETMANAGE_T));
		this.telnetManage.setMnemonic(KeyEvent.VK_N);
		this.telnetManage.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemTelnetActionPerformed();
			}
		});
		this.menuSystem.add(this.telnetManage);
		
		// 菜单条： 屏幕锁定
		this.handLock.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_HONDLOCK_T));
		this.handLock.setMnemonic(KeyEvent.VK_L);
		this.handLock.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK));
		this.handLock.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemHankLockActionPerformed();
			}
		});
		this.menuSystem.add(this.handLock);

		// 退出系统
		this.menuItemLogout.setText(ResourceUtil.srcStr(StringKeysTitle.TIT_EXIT_SYSTEM));
		this.menuItemLogout.setMnemonic(KeyEvent.VK_X);
		this.menuItemLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK));
		this.menuItemLogout.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitSystem();
			}
		});
		this.menuSystem.add(this.menuItemLogout);

		// 系统管理暂时没有菜单项
		jMenuBar2.add(menuSystem);
		/*
		 * 拓扑管理 菜单
		 */
		jMenu13.setBackground(new java.awt.Color(191, 213, 235));
		jMenu13.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_TOPO_MANAGE));
		jMenu13.setMnemonic(KeyEvent.VK_T);
		// 菜单条：段管理
		jMenuItem9.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SEGMENT_T));
		/*  
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem9, RootFactory.TOPOLOGYMODU);
		jMenuItem9.setMnemonic(KeyEvent.VK_E);
		jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem9ActionPerformed();
			}
		});
		//菜单条：命名规则
		jMenuSetName.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SETNAME));
		
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuSetName, RootFactory.TOPOLOGYMODU);
		
		jMenuSetName.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuSetNameActionPerformed();
			}
		});
		
		// 菜单条：子网管理
		SubnetConfigMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TIT_SUBNET_MANAGER_T));
		SubnetConfigMenuItem.setMnemonic(KeyEvent.VK_N);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.SubnetConfigMenuItem, RootFactory.TOPOLOGYMODU);
		SubnetConfigMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SubnetMenuItemMenuItemActionPerformed(evt);
			}
		});
		this.jMenu13.add(SubnetConfigMenuItem);
		jMenu13.add(jMenuSetName);
		jMenu13.add(jMenuItem9);
		
		
		
		// 菜单条：组管理
		groupConfigMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TIT_GROUP_MANAGER_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.groupConfigMenuItem, RootFactory.TOPOLOGYMODU);
		groupConfigMenuItem.setMnemonic(KeyEvent.VK_Z);
		groupConfigMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				GroupMenuItemMenuItemActionPerformed(evt);
			}
		});
		this.jMenu13.add(groupConfigMenuItem);
		jMenu13.add(jMenuItem9);
		
		jMenuBar2.add(jMenu13);
		// 菜单：配置管理
		jMenu14.setBackground(new java.awt.Color(191, 213, 235));
		jMenu14.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_CONFIG_MANAGE));
		jMenu14.setMnemonic(KeyEvent.VK_C);
		jMenu14.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenu14ActionPerformed(evt);
			}
		});
		// 菜单条：UDA 配置
		jMenuItem3.setText(ResourceUtil.srcStr(StringKeysTab.TAB_UDA));
		jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem3ActionPerformed(evt);
			}
		});
		// 菜单条： CODE配置
		jMenuItem4.setText(ResourceUtil.srcStr(StringKeysTab.TAB_CODE));
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem4ActionPerformed(evt);
			}
		});

		if (CodeConfigItem.getInstance().getCodeconfig() == 1) {
			jMenu14.add(jMenuItem3);
			jMenu14.add(jMenuItem4);
		}
		// 菜单条： QOS 模块管理
		qosTemplateMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_QOSTEMPLATE));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.qosTemplateMenuItem, RootFactory.DEPLOYMODU);
		this.qosTemplateMenuItem.setMnemonic(KeyEvent.VK_Q);
		qosTemplateMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				qosTemplateMenuItemActionPerformed(evt);
			}
		});
		jMenu14.add(qosTemplateMenuItem);
	
		// 菜单条： COS到EXP映射
		cosToExpMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_COSTOEXPTEMPLATE));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.cosToExpMenuItem, RootFactory.DEPLOYMODU);
		this.cosToExpMenuItem.setMnemonic(KeyEvent.VK_E);
		cosToExpMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cosToExpTemplateMenuItemActionPerformed(evt);
			}
		});

		// 菜单条： VLANPRI到颜色映射
		vlanpriToColorMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_VLANPRITOCOLORTEMPLATE));

		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.vlanpriToColorMenuItem, RootFactory.DEPLOYMODU);
		this.vlanpriToColorMenuItem.setMnemonic(KeyEvent.VK_V);
		vlanpriToColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				vlanpriToColorTemplateMenuItemActionPerformed(evt);
			}
		});

		// 菜单条：优先业务到VLANPRI映射
		priorityToVlanpriMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PRIORITYTOVLANPRITEMPLATE_T));

		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.priorityToVlanpriMenuItem, RootFactory.DEPLOYMODU);
		this.priorityToVlanpriMenuItem.setMnemonic(KeyEvent.VK_O);
		priorityToVlanpriMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				priorityToVlanpriTemplateMenuItemActionPerformed(evt);
			}
		});

		// QoS模板管理
		this.modelManagerMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_TEMPLATEMANAGER));
		this.modelManagerMenuItem.setMnemonic(KeyEvent.VK_T);
		modelManagerMenuItem.add(qosTemplateMenuItem);		
		if (CodeConfigItem.getInstance().getWuhan() == 0) {
			modelManagerMenuItem.add(cosToExpMenuItem);// COS到EXP映射
			modelManagerMenuItem.add(vlanpriToColorMenuItem);// VLANPRI到颜色映射
			modelManagerMenuItem.add(priorityToVlanpriMenuItem);// 优先业务到VLANPRI映射
		}
		jMenu14.add(modelManagerMenuItem);
		// 菜单条： 网元管理
		NeConfigMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_DOMAINCONFIG_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.NeConfigMenuItem, RootFactory.DEPLOYMODU);
		this.NeConfigMenuItem.setMnemonic(KeyEvent.VK_M);
		NeConfigMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				NeConfigMenuItemMenuItemActionPerformed(evt);
			}
		});
		jMenu14.add(NeConfigMenuItem);

		// 菜单条：客户信息管理
		clientManagerMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_CLIENTMANAGER_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.clientManagerMenuItem, RootFactory.DEPLOYMODU);
		this.clientManagerMenuItem.setMnemonic(KeyEvent.VK_C);
		clientManagerMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clientManagerMenuItemActionPerformed(evt);
			}
		});
		jMenu14.add(clientManagerMenuItem);
		
		
//		// 菜单条：搜索网元
//		searchSiteMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_SITESEARCH));
//		/*
//		 * 添加 权限验证
//		 */
//		roleRoot.setItemEnbale(searchSiteMenuItem, RootFactory.DEPLOYMODU);
//		searchSiteMenuItem.setMnemonic(KeyEvent.VK_S);
//		searchSiteMenuItem.addActionListener(new java.awt.event.ActionListener() {
//			@Override
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				searchSiteMenuItemActionPerformed(evt);
//			}
//		});
//		// 搜索网元菜单武汉、晨晓的都显示
//		jMenu14.add(searchSiteMenuItem);
		
		// 菜单条：上载/下载网元配置管理
		uploadDownloadConfigure.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_UPLOAD_DOWNLOAD_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(uploadDownloadConfigure, RootFactory.DEPLOYMODU);
		this.uploadDownloadConfigure.setMnemonic(KeyEvent.VK_D);
		uploadDownloadConfigure.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuItemUploadDownloadConfigureActionPerformed(evt);
			}
		});

		// 搜索网元菜单武汉、晨晓的都显示
		if (CodeConfigItem.getInstance().getWuhan() == 0) {
			jMenu14.add(searchSiteMenuItem);
		}
		jMenu14.add(uploadDownloadConfigure);

				
		// 所有网元的初始化
		siteInitialise.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_REMOTE_IP));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(siteInitialise, RootFactory.DEPLOYMODU);
		this.siteInitialise.setMnemonic(KeyEvent.VK_R);
		siteInitialise.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuItemsiteInitialiseActionPerformed(evt);
			}
		});
		jMenu14.add(siteInitialise);

		
		//网元批量升级
		// 菜单条：网元批量升级管理
		batchSoftwateUpdate.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_BATCHSOFTWARE_UPDATE_G));		
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(batchSoftwateUpdate, RootFactory.DEPLOYMODU);
		this.batchSoftwateUpdate.setMnemonic(KeyEvent.VK_G);
		batchSoftwateUpdate.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuItemBatchSoftwateUpdateActionPerformed(evt);
			}
		});
		jMenu14.add(batchSoftwateUpdate);
		
		jMenuBar2.add(jMenu14);
		// 菜单： 业务管理
		jMenu15.setBackground(new java.awt.Color(191, 213, 235));
		jMenu15.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_SERVICE_MANAGE));
		jMenu15.setMnemonic(KeyEvent.VK_O);
		// 菜单条： TUNNEL 管理
		jMenuItem7.setText(ResourceUtil.srcStr(StringKeysTab.TAB_TUNNEL));
		this.jMenuItem7.setMnemonic(KeyEvent.VK_T);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem7, RootFactory.COREMODU);
		jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem7ActionPerformed();
			}
		});
		jMenu15.add(jMenuItem7);
		// 菜单条： PW 管理
		jMenuItem8.setText(ResourceUtil.srcStr(StringKeysTab.TAB_PWINFO));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem8, RootFactory.COREMODU);
		this.jMenuItem8.setMnemonic(KeyEvent.VK_P);
		jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem8ActionPerformed();
			}
		});
		jMenu15.add(jMenuItem8);
		// 菜单条： CES 管理
		CESMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_CESINFO));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.CESMenuItem, RootFactory.COREMODU);
		this.CESMenuItem.setMnemonic(KeyEvent.VK_C);
		CESMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cesMenuItemActionPerformed();
			}
		});
		jMenu15.add(CESMenuItem);
		
		
		ethService.setText(ResourceUtil.srcStr(StringKeysTab.ETHSERVICE_JMENU));
		this.ethService.setMnemonic(KeyEvent.VK_T);
		// 菜单条： ELINE 管理
		elineMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_ELINEINFO));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.elineMenuItem, RootFactory.COREMODU);
		this.elineMenuItem.setMnemonic(KeyEvent.VK_E);
		elineMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				elineMenuItemActionPerformed();
			}
		});
		ethService.add(elineMenuItem);
		// 菜单条： ELAN 管理
		elanMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_ELANINFO));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.elanMenuItem, RootFactory.COREMODU);
		this.elanMenuItem.setMnemonic(KeyEvent.VK_L);
		elanMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				elanMenuItemActionPerformed();
			}
		});
		ethService.add(elanMenuItem);
		// 菜单条：ETREE 管理
		etreeMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ETREEINFO_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.etreeMenuItem, RootFactory.COREMODU);
		this.etreeMenuItem.setMnemonic(KeyEvent.VK_W);
		etreeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				etreeMenuItemActionPerformed();
			}
		});
		ethService.add(etreeMenuItem);
		jMenu15.add(ethService);
		
		this.serviceRepairedMenu.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_SERVICE_REPAIRED));
		this.NNIPortMovedMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_NNI_PORT_MOVED));
		roleRoot.setItemEnbale(this.NNIPortMovedMenuItem, RootFactory.COREMODU);
		this.NNIPortMovedMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NNIPortMovedMenuItemActionPerformed();
			}
		});
		this.serviceRepairedMenu.add(this.NNIPortMovedMenuItem);
		this.UNIPortMovedMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_UNI_PORT_MOVED));
		roleRoot.setItemEnbale(this.UNIPortMovedMenuItem, RootFactory.COREMODU);
		this.UNIPortMovedMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UNIPortMovedMenuItemActionPerformed();
			}
		});
		this.serviceRepairedMenu.add(this.UNIPortMovedMenuItem);
		this.jMenu15.add(this.serviceRepairedMenu);
		
		DualInfoItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_DUALINFO));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.DualInfoItem, RootFactory.COREMODU);
		this.DualInfoItem.setMnemonic(KeyEvent.VK_K);
		DualInfoItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DualQinQItemActionPerformed();
			}
		});

		jMenu15.add(DualInfoItem);
		
		/**
		 * 自动校正配置
		 */
		this.autoCorrectionItem.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_AUTO_CORRECTION));
		roleRoot.setItemEnbale(this.autoCorrectionItem, RootFactory.COREMODU);
		this.autoCorrectionItem.setMnemonic(KeyEvent.VK_K);
		this.autoCorrectionItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				autoCorrectionMenuItemActionPerformed(evt);
			}
		});
		jMenu15.add(this.autoCorrectionItem);

		jMenuBar2.add(jMenu15);
		/**
		 * 故障管理
		 */
		jMenu16.setBackground(new java.awt.Color(191, 213, 235));
		jMenu16.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_STOPPAGE_MANAGE));
		jMenu16.setMnemonic(KeyEvent.VK_F);
		// 菜单条：告警维护
		jMenu1.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_ALARM_MAINTAIN));
		jMenu1.setMnemonic(KeyEvent.VK_M);
		// 子菜单条： 告警描述
		jMenuItem1.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ALARM_T));
		jMenuItem1.setMnemonic(KeyEvent.VK_A);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem1, RootFactory.ALARMMODU);
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});
		// 子菜单条： 告警声音设置
		alarmVoiceMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ALARM_VOICE_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.alarmVoiceMenuItem, RootFactory.ALARMMODU);
		this.alarmVoiceMenuItem.setMnemonic(KeyEvent.VK_S);
		alarmVoiceMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alarmVoiceMenuItemActionPerformed(evt);
			}
		});
		
		//子菜单 告警反转
		alarmReversal.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ALARM_REVERSAL));
		roleRoot.setItemEnbale(this.alarmReversal, RootFactory.ALARMMODU);
		this.alarmReversal.setMnemonic(KeyEvent.VK_V);
		alarmReversal.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				alarmReversalMenuItemActionPerformed(evt);
			}
		});
		
		// 菜单条： 当前告警
		currAlarmMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_CURRALARM_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.currAlarmMenuItem, RootFactory.ALARMMODU);
		this.currAlarmMenuItem.setMnemonic(KeyEvent.VK_C);
		currAlarmMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				currAlarmActionPerformed();
			}
		});
		
		jMenu1.add(jMenuItem1);
		jMenu1.add(alarmVoiceMenuItem);
		jMenu1.add(alarmReversal);
		
		jMenu16.add(jMenu1);
		jMenu16.add(currAlarmMenuItem);
		// 菜单条： 历史告警
		hisAlarmMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_HISALARM_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.hisAlarmMenuItem, RootFactory.ALARMMODU);
		hisAlarmMenuItem.setMnemonic(KeyEvent.VK_H);
		hisAlarmMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				hisAlarmActionPerformed();
			}
		});
		jMenu16.add(hisAlarmMenuItem);
		//菜单条： 告警屏蔽
		siteAlarmShieldItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ALARM_SHIELD));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.siteAlarmShieldItem, RootFactory.ALARMMODU);
		siteAlarmShieldItem.setMnemonic(KeyEvent.VK_H);
		siteAlarmShieldItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				siteAlarmShieldActionPerformed();
			}
		});
		jMenu16.add(siteAlarmShieldItem);
		//菜单条： OAM事件
		oamEventMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_EVENT));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.oamEventMenuItem, RootFactory.ALARMMODU);
		this.oamEventMenuItem.setMnemonic(KeyEvent.VK_O);
		oamEventMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				oamEventActionPerformed();
			}
		});
		jMenu16.add(oamEventMenuItem);
		
		// TCA告警菜单只有晨晓的设备才有，武汉的设备暂时需要关闭此菜单以免误解
		if (CodeConfigItem.getInstance().getWuhan() == 0) {
			this.tcaAlarmMenuIten.setText(ResourceUtil.srcStr(StringKeysTab.TAB_TCA_ALARM));
			/*
			 * 添加 权限验证
			 */
			roleRoot.setItemEnbale(this.tcaAlarmMenuIten, RootFactory.ALARMMODU);
			this.tcaAlarmMenuIten.setMnemonic(KeyEvent.VK_T);
			this.tcaAlarmMenuIten.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					tcaAlarmActionPerformed();
				}
			});
			jMenu16.add(this.tcaAlarmMenuIten);
		}
		
		// 添加数据库运行状态记录  TAB_SYSTEM_MONITOR
		this.systemMonitorBaseCount.setText(ResourceUtil.srcStr(StringKeysTab.TAB_SYSTEM_MONITOR));
//		/*
//		 * 添加 权限验证
//		 */
		roleRoot.setItemEnbale(this.systemMonitorBaseCount, RootFactory.ALARMMODU);
		this.systemMonitorBaseCount.setMnemonic(KeyEvent.VK_R);
		this.systemMonitorBaseCount.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				systemMonitorPerformed();
			}
		});
		jMenu16.add(this.systemMonitorBaseCount);
		jMenuBar2.add(jMenu16);
		/**
		 * 菜单： 性能管理
		 */
		jMenu17.setBackground(new java.awt.Color(191, 213, 235));
		jMenu17.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_PROPERTY_MANAGE));
		jMenu17.setMnemonic(KeyEvent.VK_P);
		// 菜单条：性能描述
		jMenuItem2.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PROPERTY_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem2, RootFactory.PROFORMANCEMODU);
	    this.jMenuItem2.setMnemonic(KeyEvent.VK_M);
		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});
		
		/**
		 * 性能数据存储设置
		 */ 
//		performanceMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.PERFORMANCEDATAMENGAE_JMENU));
//		roleRoot.setItemEnbale(this.performanceMenuItem, RootFactory.PROFORMANCEMODU);
//	    this.performanceMenuItem.setMnemonic(KeyEvent.VK_S);
//	    performanceMenuItem.addActionListener(new java.awt.event.ActionListener() {
//			@Override
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				performanceSetActionPerformed(evt);
//			}
//		});
		
		// 菜单条： 当前性能
		currPerMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_CURRPERFOR_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.currPerMenuItem, RootFactory.PROFORMANCEMODU);
		this.currPerMenuItem.setMnemonic(KeyEvent.VK_P);
		currPerMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currPerMenuItemActionPerformed();
			}
		});
		// 菜单条： 历史性能
		hisPerMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_HISPERFOR_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.hisPerMenuItem, RootFactory.PROFORMANCEMODU);
		this.hisPerMenuItem.setMnemonic(KeyEvent.VK_H);
		hisPerMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				hisPerMenuItemActionPerformed();
			}
		});
		// 菜单条： 长期性能任务
		perTaskMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PERTASK_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.perTaskMenuItem, RootFactory.PROFORMANCEMODU);
		this.perTaskMenuItem.setMnemonic(KeyEvent.VK_T);
		perTaskMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				perTaskMenuItemActionPerformed();
			}
		});
		// 菜单条：实时性能统计
		currPerformCountMenuItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_CURRENT_PERFORMANCE_STATISTICS));
//		/*
//		 * 添加 权限验证
//		 */
		roleRoot.setItemEnbale(this.currPerformCountMenuItem, RootFactory.PROFORMANCEMODU);
	    this.currPerformCountMenuItem.setMnemonic(KeyEvent.VK_M);
	    currPerformCountMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				currPerformCountMenuItemActionPerformed();
			}
		});
	    
	    //历史性能统计
	    jMenuItemPerfor.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PERFORINFO_T));
		jMenuItemPerfor.setMnemonic(KeyEvent.VK_H);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemPerfor, RootFactory.COUNTMODU);
		jMenuItemPerfor.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemPerforActionPerformed();
			}
		});
		//端到端性能统计
		jMenuItemPathPerfor.setText(ResourceUtil.srcStr(StringKeysMenu.PATHPERFORMCOUNT));
		jMenuItemPathPerfor.setMnemonic(KeyEvent.VK_H);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemPathPerfor, RootFactory.COUNTMODU);
		jMenuItemPathPerfor.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pathPerforActionPerformed();
			}
		});
		jMenu17.add(jMenuItem2);
//		jMenu17.add(performanceMenuItem);
		jMenu17.add(currPerMenuItem);
		jMenu17.add(hisPerMenuItem);
		jMenu17.add(perTaskMenuItem);
		jMenu17.add(currPerformCountMenuItem);
		jMenu17.add(jMenuItemPerfor);
		jMenu17.add(jMenuItemPathPerfor);
		jMenuBar2.add(jMenu17);
		/*
		 * 菜单： 维护管理
		 */
		jMenu18.setBackground(new java.awt.Color(191, 213, 235));
		jMenu18.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_MAINTAIN_MANAGE));
		// 维护管理暂时没有菜单项
		// jMenuBar2.add(jMenu18);
		/*
		 * 菜单： 安全管理
		 */
		jMenu19.setBackground(new java.awt.Color(191, 213, 235));
		jMenu19.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_SAFETY_MANAGE));
        this.jMenu19.setMnemonic(KeyEvent.VK_A);
		jMenuItem5.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_USER_T));
//		roleRoot.setItemEnbale(this.jMenuItem5, RootFactory.SATYMODU);
		/*
		 * 添加 权限验证
		 * 用户管理
		 */
		if(ConstantUtil.user.getUser_Name().equals("admin")){
			roleRoot.setItemEnbale(this.jMenuItem5, RootFactory.SATYMODU);
		}else{
			roleRoot.setItemEnbale(this.jMenuItem5, RootFactory.SATY_SELECTOTHRER);
		}
		
		this.jMenuItem5.setMnemonic(KeyEvent.VK_U);
		jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem5ActionPerformed(evt);
			}
		});
		// menuItemClearLock.setText(ResourceUtil.srcStr(StringKeysTab.CLEAR_SITELOCK));
		// menuItemClearLock.addActionListener(new java.awt.event.ActionListener() {
		// public void actionPerformed(java.awt.event.ActionEvent evt) {
		// menuItemClearLockActionPerformed(evt);
		// }
		// });

		jMenu19.add(jMenuItem5);
		// jMenu19.add(menuItemClearLock);
		//登陆管理
		
	
		jMenuLogin.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_LOGIN));
		jMenuLogin.setMnemonic(KeyEvent.VK_D);
		
		
		// 菜单条日志查看
		jMenuLoginLog.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_LOG_T));
		/*
		 * 添加 权限验证
		 */
//		roleRoot.setItemEnbale(this.jMenuLoginLog, RootFactory.SATYMODU);
		if(ConstantUtil.user.getUser_Name().equals("admin")){
			roleRoot.setItemEnbale(this.jMenuLoginLog, RootFactory.SATYMODU);
		}else{
			roleRoot.setItemEnbale(this.jMenuLoginLog, RootFactory.SATY_SELECTOTHRER);
		}
		
		this.jMenuLoginLog.setMnemonic(KeyEvent.VK_L);
		jMenuLoginLog.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuLoginLogActionPerformed(evt);
			}
		});
		/*
		 * 添加 权限验证
		 */
//		roleRoot.setItemEnbale(this.jMenuImport, RootFactory.SATYMODU);
		if(ConstantUtil.user.getUser_Name().equals("admin")){
			roleRoot.setItemEnbale(this.jMenuUserOnLine, RootFactory.SATYMODU);
		}else{
			roleRoot.setItemEnbale(this.jMenuUserOnLine, RootFactory.SATY_SELECTOTHRER);
		}
		
		jMenuLogin.add(jMenuLoginLog);
//		jMenuLogin.add(jMenuImport);
		// 在线查看
		jMenuUserOnLine.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_USERONLINE_T));
		/*
		 * 添加 权限验证
		 */
//		roleRoot.setItemEnbale(this.jMenuUserOnLine, RootFactory.SATYMODU);
//		if(ConstantUtil.user.getUser_Name().equals("admin")){
//			roleRoot.setItemEnbale(this.jMenuUserOnLine, RootFactory.SATYMODU);
//		}else{
//			roleRoot.setItemEnbale(this.jMenuUserOnLine, RootFactory.SATY_SELECTOTHRER);
//		}
		
		this.jMenuUserOnLine.setMnemonic(KeyEvent.VK_O);
		jMenuUserOnLine.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuUserOnLineActionPerformed(evt);
			}
		});
		// 操作日志查看
		jMenuOperationLog.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_USEROPERATIONLOG_T));
		
		/*
		 * 添加 权限验证
		 */
		if(ConstantUtil.user.getUser_Name().equals("admin")){
			roleRoot.setItemEnbale(this.jMenuOperationLog, RootFactory.SATYMODU);
		}else{
			roleRoot.setItemEnbale(this.jMenuOperationLog, RootFactory.SATY_SELECTOTHRER);
		}
//		roleRoot.setItemEnbale(this.jMenuOperationLog, RootFactory.SATYMODU);
		this.jMenuOperationLog.setMnemonic(KeyEvent.VK_N);
		jMenuOperationLog.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuOperationLogActionPerformed(evt);
			}
		});

		// 角色管理
		jMenuRoleManage.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ROLEMANAGE_T));
		/*
		 * 添加 权限验证
		 */
//		roleRoot.setItemEnbale(this.jMenuRoleManage, RootFactory.SATYMODU);
		if(ConstantUtil.user.getUser_Name().equals("admin")){
			roleRoot.setItemEnbale(this.jMenuRoleManage, RootFactory.SATYMODU);
		}else{
			roleRoot.setItemEnbale(this.jMenuRoleManage, RootFactory.SATY_SELECTOTHRER);
		}
		this.jMenuRoleManage.setMnemonic(KeyEvent.VK_R);
		jMenuRoleManage.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuRoleManageActionPerformed(evt);
			}
		});
		
		jMenuOperation.setText(ResourceUtil.srcStr(StringKeysTab.TAB_OPERATION_MANAGERS));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuOperation, RootFactory.SATYMODU);		
		this.jMenuOperation.setMnemonic(KeyEvent.VK_P);
		jMenuOperation.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuOperation(evt);
			}
		});	
		
		systemConfigItem.setText(ResourceUtil.srcStr(StringKeysTab.TAB_SYSTEM_CONFIG));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.systemConfigItem, RootFactory.SATYMODU);		
		this.systemConfigItem.setMnemonic(KeyEvent.VK_P);
		this.systemConfigItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				systemConfigActionPerformed();
			}
		});	
		systemLogItem.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_LOG_SYSTEM));
		systemLogItem.setMnemonic(KeyEvent.VK_Y);
		roleRoot.setItemEnbale(this.systemLogItem, RootFactory.SYSTEMMODU);
		this.systemLogItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				systemLogActionPerformed();
			}
		});
		
		jMenu19.add(jMenuRoleManage);
		jMenu19.add(jMenuLogin);
		jMenu19.add(systemLogItem);
		jMenu19.add(jMenuUserOnLine);
		jMenu19.addSeparator();
		jMenu19.add(jMenuOperationLog);
		jMenu19.add(jMenuOperation);
		jMenu19.add(systemConfigItem);
		jMenuBar2.add(jMenu19);
//		jMenuBar2.add(systemLogItem);
		// 统计 s

		jMenu20.setBackground(new java.awt.Color(191, 213, 235));
		jMenu20.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_STATISTICS_MANAGE));
		jMenu20.setMnemonic(KeyEvent.VK_R);
		jMenuBar2.add(jMenu20);
		siteCount.add(jMenuItem10);  
		siteCount.setText(ResourceUtil.srcStr(StringKeysTab.NEMANGTH_JMENU));
		siteCount.setMnemonic(KeyEvent.VK_N);
		alarmCount.setText(ResourceUtil.srcStr(StringKeysTab.ALARMCOUNT));//告警统计
		alarmCount.setMnemonic(KeyEvent.VK_A);
		performanceCount.setText(ResourceUtil.srcStr(StringKeysTab.PERFORMANCECOUNT_JMENU));//性能统计
		performanceCount.setMnemonic(KeyEvent.VK_P);
		ethServiceCount.setText(ResourceUtil.srcStr(StringKeysTab.ETHMANGER_JMENU));//业务统计
		ethServiceCount.setMnemonic(KeyEvent.VK_E);
		
		// 菜单条： 网元配置信息统计
		jMenuItem10.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SITEINFO_T));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem10, RootFactory.COUNTMODU);
		jMenuItem10.setMnemonic(KeyEvent.VK_N);
		jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem10ActionPerformed();
			}
		});
		siteCount.add(jMenuItem11);
		// 菜单条：网元物理连接信息统计
		jMenuItem11.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SEGMENTNFO_T));
		jMenuItem11.setMnemonic(KeyEvent.VK_S);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem11, RootFactory.COUNTMODU);
		jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem11ActionPerformed();
			}
		});
		siteCount.add(slotMenuItem);
		// 槽位信息统计
		slotMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SLOTTNFO_T));
		slotMenuItem.setMnemonic(KeyEvent.VK_C);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.slotMenuItem, RootFactory.COUNTMODU);
		slotMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				slotMenuItemActionPerformed();
			}
		});
		
		// 网元板卡数量统计数
		siteCountMenuItem.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_NECARDCOUNT));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.siteCountMenuItem, RootFactory.COUNTMODU);
		siteCountMenuItem.setMnemonic(KeyEvent.VK_B);
		siteCountMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				neCountMenuItemActionPerformed();
			}
		});
		siteCount.add(siteCountMenuItem);
		
		// 网元物理连接带宽统计
		siteCount.add(jMenuItem12);
		jMenuItem12.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_SEGMENTNFOWIDTH_T));
		jMenuItem12.setMnemonic(KeyEvent.VK_T);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem12, RootFactory.COUNTMODU);
		jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem12ActionPerformed();
			}
		});
		// 服务路径带宽统计
		ethServiceCount.add(jMenuItem13);
		jMenuItem13.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PATHTNFOWIDTH_T));
		jMenuItem13.setMnemonic(KeyEvent.VK_F);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItem13, RootFactory.COUNTMODU);
		jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem13ActionPerformed();
			}
		});
		
		//统计QOS cir 值
		ethServiceCount.add(cirCount);
		cirCount.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_LINKCOUNT));
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.cirCount, RootFactory.COUNTMODU);
		cirCount.setMnemonic(KeyEvent.VK_L);
		cirCount.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cirCountActionPerformed();
			}
		});
		
//		// 单板信息统计
		siteCount.add(jMenuItemCard);
		jMenuItemCard.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_CARDINFO_T));
		jMenuItemCard.setMnemonic(KeyEvent.VK_D);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemCard, RootFactory.COUNTMODU);
		jMenuItemCard.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemCardActionPerformed();
			}
		});
		
		jMenu20.add(siteCount);
		// 业务信息统计
		ethServiceCount.add(jMenuItemProfess);
		jMenuItemProfess.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PROFESSINFO_T));
		jMenuItemProfess.setMnemonic(KeyEvent.VK_Q);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemProfess, RootFactory.COUNTMODU);
		jMenuItemProfess.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemProfessActionPerformed();
			}
		});
		// 告警
		alarmCount.add(jMenuItemAlarm);
		jMenuItemAlarm.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ALARMINFO_T));
		jMenuItemAlarm.setMnemonic(KeyEvent.VK_U);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemAlarm, RootFactory.COUNTMODU);
		jMenuItemAlarm.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemAlarmActionPerformed();
			}
		});
		jMenu20.add(alarmCount);
		// 性能统计
//		performanceCount.add(this.jMenuItemPerfor);
//		jMenuItemPerfor.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PERFORINFO_T));
//		jMenuItemPerfor.setMnemonic(KeyEvent.VK_H);
		/*
		 * 添加 权限验证
		 */
//		roleRoot.setItemEnbale(this.jMenuItemPerfor, RootFactory.COUNTMODU);
//		jMenuItemPerfor.addActionListener(new java.awt.event.ActionListener() {
//			@Override
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				jMenuItemPerforActionPerformed();
//			}
//		}); 
//		jMenu20.add(performanceCount);
		// 标签
		ethServiceCount.add(jMenuItemLable);
		jMenuItemLable.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_LABLE_INFO_T));
		this.jMenuItemLable.setMnemonic(KeyEvent.VK_L);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemLable, RootFactory.COUNTMODU);
		jMenuItemLable.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemLableActionPerformed();
			}
		});
		jMenu20.add(ethServiceCount);
		// 端口
		ethServiceCount.add(jMenuItemPort);
		jMenuItemPort.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PORTINFO_T));
		jMenuItemPort.setMnemonic(KeyEvent.VK_P);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemPort, RootFactory.COUNTMODU);
		jMenuItemPort.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemPortActionPerformed();
			}
		});
		//端到端路径数量统计
		ethServiceCount.add(jMenuItemPath);
		jMenuItemPath.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_PATHNUM_T));
		jMenuItemPath.setMnemonic(KeyEvent.VK_J);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemPath, RootFactory.COUNTMODU);
		jMenuItemPath.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemPathNumActionPerformed();
			}
		});
		
		//单网元路径数量统计  后面的 TAB_NEPATHNUM_J 是快捷键
		ethServiceCount.add(jMenuItemNePath);
		jMenuItemNePath.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_NEPATHNUM_T));
		jMenuItemNePath.setMnemonic(KeyEvent.VK_E);
		/*
		 * 添加 权限验证
		 */
		roleRoot.setItemEnbale(this.jMenuItemNePath, RootFactory.COUNTMODU);
		jMenuItemNePath.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemNePathNumActionPerformed();
			}
		});
		
		// 统计 e
		jMenu22.setBackground(new java.awt.Color(191, 213, 235));
		jMenu22.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_HELP));
		jMenu22.setMnemonic(KeyEvent.VK_H);
		
		//
//		jMenu20.add(this.layerRateMenuItem);
//		this.layerRateMenuItem.setText("层速率统计");
//		this.layerRateMenuItem.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				layerRateActionPerformed();
//			}
//		});
		
		CodeConfigItem	codeConfigItem = CodeConfigItem.getInstance();
//		if(codeConfigItem.getValueByKey("IconImageShowOrHide").equals("1")){
			// 去掉java图标
			jMenu22.add(help);// 帮助
//		}
		help.setText(ResourceUtil.srcStr(StringKeysMenu.MENU_HELP));
		help.setMnemonic(KeyEvent.VK_H);
		help.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemHelpLableActionPerformed();
			}
		});

		jMenu22.add(about);// 关于
		about.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_ABOUT_T));
		about.setMnemonic(KeyEvent.VK_A);
		about.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemAbouLableActionPerformed();
			}
		});
		// 帮助暂时没有菜单项
		jMenuBar2.add(jMenu22);

		setJMenuBar(jMenuBar2);
		
		jMenu22.add(versionInfo);// 版本信息
		versionInfo.setText(ResourceUtil.srcStr(StringKeysMenu.TAB_VERSIONINFO));
		versionInfo.setMnemonic(KeyEvent.VK_A);
		versionInfo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemVersionActionPerformed();
			}
		});
		
//		this.selectNe.setMnemonicKeyEvent.VK_F);
//		this.selectNe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
//		this.selectNe.addActionListener(new java.awt.event.ActionListener() {
//			@Override
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				jMenuItemSelectNeActionPerformed();
//			}
//		});
//		
		
		getContentPane().add(jToolBar1, BorderLayout.NORTH);
		getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
		getContentPane().add(jPanel3, BorderLayout.SOUTH);
		jTabbedPane1.getAccessibleContext().setAccessibleName("\u5de5\u4f5c\u53f0");
		this.setPreferredSize(new Dimension(900, 751));
		pack();
		
	}
	
	/**
	 * 网管版本信息
	 */
	private void jMenuItemVersionActionPerformed(){
		try {
			new VersionInfoDialog();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * NNI端口迁移
	 */
	private void NNIPortMovedMenuItemActionPerformed() {
		try {
			new NNIPortMovedDialog();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * UNI端口迁移
	 */
	private void UNIPortMovedMenuItemActionPerformed() {
		try {
			new UNIPortMovedDialog();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 层速率统计
	 */
	private void layerRateActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysMenu.MENU_LAYERRATECOUNT), new LayerRateCountPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 端到端性能统计
	 */
	private void pathPerforActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysMenu.PATHPERFORMCOUNT), new PathPerformCountPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * EXP映射
	 * 
	 * @param evt
	 */
	private void cosToExpTemplateMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_COSTOEXPTEMPLATE), new ExpMappingPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * VLANPRI到颜色映射
	 * 
	 * @param evt
	 */
	private void vlanpriToColorTemplateMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_VLANPRITOCOLORTEMPLATE), new VlanpriToColorPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 优先业务到VLANPRI映射
	 * 
	 * @param evt
	 */
	private void priorityToVlanpriTemplateMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PRIORITYTOVLANPRITEMPLATE), new PriorityToVlanpriPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * dual
	 */
	private void DualQinQItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_DUALINFO), new DualBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 告警声音设置
	 * 
	 * @param evt
	 */
	protected void alarmVoiceMenuItemActionPerformed(ActionEvent evt) {
		AlarmVoiceDialog alarmVoiceDialog = null;
		try {
			alarmVoiceDialog = new AlarmVoiceDialog();
			UiUtil.showWindow(alarmVoiceDialog, 720, 350);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 告警设置
	 * 
	 * @param evt
	 */
	protected void alarmReversalMenuItemActionPerformed(ActionEvent evt) {
		AlarmReversalPanel alarmReversalPanel = null;
		try {
			alarmReversalPanel = new AlarmReversalPanel();
			UiUtil.showWindow(alarmReversalPanel, 370, 270);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 自动校正配置
	 * @param evt
	 */
	protected void autoCorrectionMenuItemActionPerformed(ActionEvent evt) {
		AutoCorrectionDialog autoCorrectionDialog = null;
		try {
			autoCorrectionDialog = new AutoCorrectionDialog();
			UiUtil.showWindow(autoCorrectionDialog, 370, 270);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 转储管理
	 */
	private void unloadActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.MENU_UNLOADING), new UnLoadingPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void unloadDeleteActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_UNLOADING_DELETE), new UnLoadingDeletePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// 登录日志查询
	protected void jMenuLoginLogActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_LOG), new LoginLogPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	// 查询操作日志
	protected void jMenuOperationLogActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_USEROPERATIONLOG), new OperationLogPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	// 角色管理
	protected void jMenuRoleManageActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ROLEMANAGE), new RoleManagePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	// 在线用户查看
	protected void jMenuUserOnLineActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_USERONLINE), new UserOnLinePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	protected void menuItemUploadDownloadConfigureActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_UPLOAD_DOWNLOAD), new UploadDownloadConfigurePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	/*网元批量升级*/
	protected void menuItemBatchSoftwateUpdateActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_BATCHSOFTWAREUPDATE), new BatchSoftwareUpgradePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	/* 网元初始化 */
	protected void menuItemsiteInitialiseActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SITEINITIALISE), new SiteInitialiseConfig());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	protected void NeConfigMenuItemMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_DOMAINCONFIG), new NeConfigView());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	private void qosTemplateMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_QOSTEMPLATE), new QosTemplatePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	
	// GEN-END:initComponents

	private void jMenuItem9ActionPerformed() {
		try { 
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENT), new SegmentPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
//拓扑命名规则
	private void jMenuSetNameActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_NAME_RULE), new SetNameRulePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	private void GroupMenuItemMenuItemActionPerformed(ActionEvent evt) {
		try {
			new GroupManagerPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	private void SubnetMenuItemMenuItemActionPerformed(ActionEvent evt) {
		try {
			new SubnetManagerPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 根据告警级别 弹出告警列表
	 * 
	 * @param alarmLenve
	 *            告警级别
	 */
	private void showAlarmList(int alarmLenve) {
		try {
			UiUtil.showWindow(new TopoAlamTable(alarmLenve), 950, 520);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void elineMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ELINEINFO), new ElineBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem8ActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PWINFO), new PwBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void cesMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CESINFO), new CesBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void elanMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ELANINFO), new ElanBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void etreeMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ETREEINFO), new EtreeBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void wrappingMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_WRAPPING), new LoopProtectPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem7ActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_TUNNEL), new TunnelBusinessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_USER), new UserInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CODE), new CodePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_UDA), new UdaGroupPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenu14ActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void performanceSetActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			PerformanceStoragePanel performanceSet = new PerformanceStoragePanel();
			UiUtil.showWindow(performanceSet, 400, 250);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PROPERTY), new PerformanceDescPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 系统监护
	 */
	private void systemMonitorPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SYSTEMMOINTOR),  new SystemMontorConfigPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void currPerMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CURRPERFOR), new CurrentPerformancePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void hisPerMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_HISPERFOR), new HisPerformancePanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void perTaskMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PERTASK), new PerformanceTaskPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 实时性能统计
	 */
	private void currPerformCountMenuItemActionPerformed(){
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CURRENT_PERFORMANCE_STATISTICS), new CurrPerformCountPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ALARM), new AlarmDescPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void currAlarmActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CURRALARM),new CurrentAlarmPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void hisAlarmActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_HISALARM), new HisAlarmPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void siteAlarmShieldActionPerformed() {
		AlarmBusinessBlockDialog alarmDialog = null;
		try {
//			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ALARMSHIELD), new AlarmBusinessShield());
			alarmDialog = new AlarmBusinessBlockDialog();
			UiUtil.showWindow(alarmDialog, 550, 500);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void oamEventActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_OAM_EVENT), new OamEventPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * tca告警菜单点击事件
	 */
	private void tcaAlarmActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_TCA_ALARM), new TCAAlarmPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void jMenuItem10ActionPerformed() {
		try {
//			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SITEINFO), new SiteStatisticsPanel(0));
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SITEINFO), new SpecificSiteStatisticsPanel(0));
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	private void jMenuItem11ActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENTNFO), new SegmentStatisticsPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	private void slotMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SLOTTNFO), new SlotStatisticsPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	//网元总统计数目
	private void neCountMenuItemActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane,ResourceUtil.srcStr(StringKeysMenu.TAB_NECARDCOUNT_T), new SiteCountStatisticsPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	// jMenuItem12ActionPerformed
	private void jMenuItem12ActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SEGMENTNFOWIDTH), new SegmentStatisticsWidthPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// jMenuItem13ActionPerformed
	private void jMenuItem13ActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PATHTNFOWIDTH), new PathStatisticsWidthPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	// jMenuItem13ActionPerformed
	private void cirCountActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysMenu.TAB_LINKCOUNT_T), new LspPortPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// jMenuItemCardActionPerformed
	private void jMenuItemCardActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CARDINFO),new CardInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	/**
	 * 业务信息统计
	 */
	private void jMenuItemProfessActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTitle.TIT_PROFESS_COUNT), new ProfessPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// jMenuItemAlarmActionPerformed
	private void jMenuItemAlarmActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_ALARMINFO), new AlarmInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// 性能统计
	private void jMenuItemPerforActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PERFORINFO), new PerformanceInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// jMenuItemLableActionPerformed
	private void jMenuItemLableActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_LABLEINFO), new LableInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// 端口信息统计
	private void jMenuItemPortActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PORTINFO), new PortInfoPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	// 端到端路径数量统计
	private void jMenuItemPathNumActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_PATHNUM), new PathNumStatisticsPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	// 单网元路径数量统计
	private void jMenuItemNePathNumActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_NEPATHNUM), new NePathNumStatisticsPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	private void jMenuOperation(java.awt.event.ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_OPERATION_MANAGERS), new LogManagerPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	private void clientManagerMenuItemActionPerformed(ActionEvent evt) {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_CLIENTMANAGER), new ClientManagerPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	private void systemLogActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysLbl.LBL_LOG_SYSTEM), new SystemLogManagerPanel());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void systemConfigActionPerformed() {
		try {
			this.mainTabPanel(ConstantUtil.jTabbedPane, ResourceUtil.srcStr(StringKeysTab.TAB_SYSTEM_CONFIG), new SystemConfigView());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	// 网元搜索
	private void searchSiteMenuItemActionPerformed(ActionEvent evt) {
		try {
			new SiteSearchDialog();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	// 帮助的单击事件
	private void jMenuItemHelpLableActionPerformed() {
		OpenHelpManual openHelpManual = null;
		try {
			openHelpManual = new OpenHelpManual();
			openHelpManual.openHelp(this.getjTabbedPane1().getSelectedComponent());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			openHelpManual = null;
		}
	}
	
	// 关于的单击事件
	private void jMenuItemAbouLableActionPerformed() {
		AboutHelp aboutHelp = null;
		try {
			aboutHelp = new AboutHelp();
			UiUtil.showWindow(aboutHelp, aboutHelp.getWeight(), 300);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
		}
	}

	// 用户个性化设置
	private void jMenuItemUserDesginActionPerformed() {
		UserDesginJDialog userDesginJDialog = null;
		try {
			userDesginJDialog = new UserDesginJDialog();
			UiUtil.showWindow(userDesginJDialog, userDesginJDialog.getWeight(), 250);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	//数据备份
	private void jMenuItemDataBackupActionPerformed() {
		DataBackupJDialog dataBackupJDialog = null;
		try {
			dataBackupJDialog = new DataBackupJDialog();
			UiUtil.showWindow(dataBackupJDialog, dataBackupJDialog.getWeight(), 350);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	//数据恢复
	private void jMenuItemDataRecoverActionPerformed() {
		DataRecoverJDialog dataRecoverJDialog = null;
		try {
			dataRecoverJDialog = new DataRecoverJDialog();
			UiUtil.showWindow(dataRecoverJDialog, dataRecoverJDialog.getWeight(), 350);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	// telnet设置
		private void jMenuItemTelnetActionPerformed() {
			TelnetDialog telnetDialog = null;
			try {
				telnetDialog = new TelnetDialog();
				UiUtil.showWindow(telnetDialog, telnetDialog.getWeight(), 210);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}
		}
		
	// 用户锁屏的单击事件
	private void jMenuItemHankLockActionPerformed() {
		try {
			handLockDialog.getPassWord().setText("");
			UiUtil.showWindow(handLockDialog, handLockDialog.getWeight(), 300);		
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} 
	}
	
	
	// 搜索网元事件
		private void jMenuItemSelectNeActionPerformed() {
			try {
				selectSiteDialog.getJtextField1().setText("");	
				selectSiteDialog.getJtextField2().setText("");
				selectSiteDialog.getJtextField3().setText("");
				selectSiteDialog.getJtextField4().setText("");
				selectSiteDialog.setSiteIdexist(false);
				UiUtil.showWindow(selectSiteDialog, selectSiteDialog.getWidth(), 200);
				this.insertOpeLogslelect(EOperationLogType.SELECTSITE.getValue(), ResultString.CONFIG_SUCCESS, null, null);			
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} 
		}
		
		


		private void insertOpeLogslelect(int operationType, String result, Object oldMac, Object newMac){
			AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TIT_SELECT_SITE),"");		
		}
		
	private void exitSystem() {

		int result = DialogBoxUtil.confirmDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_AFFIRM_EXIT));
		if (result == 0) {
			LoginLogServiece_Mb loginLogServiece = null;
			UserInstServiece_Mb userInstServiece = null;
			try {
				loginLogServiece = (LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			    userInstServiece = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
				LoginLog loginlog = new LoginLog();
				UserInst userinst = new UserInst();
				userinst.setUser_Name(ConstantUtil.user.getUser_Name());
				List<UserInst> userInst = userInstServiece.select(userinst);
				UserInst user = userInst.get(0);
				loginlog.setUser_id(user.getUser_Id());
//				loginlog.setLogoutState(2);
				loginLogServiece.updateExitLoginLog(loginlog, 2);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(loginLogServiece);
				UiUtil.closeService_MB(userInstServiece);
			}
			this.insertOpeLog(EOperationLogType.EXITSYSTEM.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			System.exit(0);
		} else {
			return;
		}

	}
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TIT_EXIT_SYSTEM),"");		
	}
	/**
	 * 添加主窗口tabbedpanel的panel
	 * 
	 * @param jTabbedPane
	 *            主窗口的tabbedpanel
	 * @param title
	 *            窗口标题
	 * @param jPanel
	 *            tab的panel
	 * @throws Exception
	 */
	public void mainTabPanel(final JTabbedPane jTabbedPane, String title, final JPanel jPanel) throws Exception {

		JPanel jPanelTitle = null;
		JLabel jLabel = null;
		JButton jButton = null;
		try {
			int result = -1;
			lablea:
			for (int i = 0; i < jTabbedPane.getTabCount(); i++) {

				JPanel jPanel2 = (JPanel) jTabbedPane.getTabComponentAt(i);
				for (int j = 0; j < jPanel2.getComponentCount(); j++) {
					if (jPanel2.getComponents()[j].getClass().getSimpleName().equals("JLabel")) {
						jLabel = (JLabel) jPanel2.getComponents()[j];
						jLabel.setSize(new Dimension(50, 50));
						if (jPanel instanceof NetWorkInfoPanel) {
							if (jLabel.getText().contains("/网元配置")) {
								result = i;
								jLabel.setText(title);
								break lablea;
							}
						} else {						
							if (title.equals(jLabel.getText())) {								
								result = i;
								break lablea;
							}
							if(title.equals(ResourceUtil.srcStr(StringKeysTab.TAB_CURRPERFOR))){
								result = -1;
								break lablea;
							}
						}
					}
				}
			}
			jPanelTitle = new JPanel();
			jPanelTitle.setOpaque(false);

			jLabel = new JLabel(title);
			//布局  分中英文
			if(ResourceUtil.language.equals("zh_CN")){
				jPanelTitle.setPreferredSize(new Dimension(title.length()*12 + 30, 20));
			}else{
				if(title.length() > 15 && title.length()<30){
					jPanelTitle.setPreferredSize(new Dimension(15 * 12 + 30, 20));
				}else if(title.length() > 30){
					jPanelTitle.setPreferredSize(new Dimension(20* 12 + 30, 20));
				}else{
					jPanelTitle.setPreferredSize(new Dimension(title.length()*12 + 30, 20));
				}
			}
			jPanelTitle.add(jLabel, BorderLayout.WEST);

			if (jTabbedPane.getTabCount() > 0 && !title.equals(ResourceUtil.srcStr(StringKeysTab.TAB_TOPO))) {
				jButton = new JButton();
				jButton.setOpaque(false);
				jButton.setContentAreaFilled(false);
				jButton.setIcon(new javax.swing.ImageIcon(new UiUtil().getClass().getResource("/com/nms/ui/images/cross.png")));
				jButton.setPreferredSize(new Dimension(18, 18));

				jButton.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mouseClicked(java.awt.event.MouseEvent evt) {
						try {
							jTabbedPane.remove(jPanel);
							TWaverUtil.clearImageIconCache();
							if(jPanel instanceof NetWorkInfoPanel){
								showCurrSiteStatus(false);
							}
						} catch (Exception e) {
							ExceptionManage.dispose(e, UiUtil.class);
						}finally{
							jPanel.removeAll();
						}
					}
				});

				jPanelTitle.add(jButton, BorderLayout.EAST);
			}

			jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			if (result >= 0) {
				jTabbedPane.setTabComponentAt(result, jPanelTitle);
				jTabbedPane.setComponentAt(result, jPanel);
				jTabbedPane.setSelectedIndex(result);
			} else {
				jTabbedPane.addTab(title, jPanel);
				jTabbedPane.setTabComponentAt(jTabbedPane.getTabCount() - 1, jPanelTitle);
				jTabbedPane.setSelectedIndex(jTabbedPane.getTabCount() - 1);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			jPanelTitle = null;
			jLabel = null;
			jButton = null;
		}
	}
	
	/**
	 * flag true/false = 显示当前网元状态/不显示当前网元状态
	 */
	public void showCurrSiteStatus(boolean flag){
		this.currSiteLabel.setVisible(flag);
		this.currSiteBtn.setVisible(flag);
	}
	
	/**
	 * 是否在线
	 * @param isOnLine true/false 在线/不在线
	 * 在线/不在线 = 绿色/灰色
	 */
	public void isOnLine(boolean isOnLine){
		if(isOnLine){
			this.currSiteBtn.setIcon(new ImageIcon(new UiUtil().getClass().getResource(
					"/com/nms/ui/images/tools/onLine.png")));
		}else{
			this.currSiteBtn.setIcon(new ImageIcon(new UiUtil().getClass().getResource(
					"/com/nms/ui/images/tools/offLine.png")));
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Ptnf main = new Ptnf();
				main.setExtendedState(JFrame.MAXIMIZED_BOTH);
				main.setVisible(true);
			}
		});
				
	}
   
	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem elineMenuItem;
	private javax.swing.JMenuItem CESMenuItem;
	private javax.swing.JMenuItem elanMenuItem;
	private javax.swing.JMenuItem etreeMenuItem;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu13;
	private javax.swing.JMenu jMenu14;
	private javax.swing.JMenu jMenu15;
	private javax.swing.JMenu jMenu16;
	private javax.swing.JMenu jMenu17;
	private javax.swing.JMenu jMenu18;
	private javax.swing.JMenu jMenu19;
	private javax.swing.JMenu jMenu20;
	private javax.swing.JMenu jMenu22;
	private javax.swing.JMenu jMenu23;  //数据管理
	private javax.swing.JMenu menuSystem;
	private javax.swing.JMenuBar jMenuBar2;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JMenuItem jMenuItem2;
	private javax.swing.JMenuItem jMenuItem3;
	private javax.swing.JMenuItem jMenuItem4;
	private javax.swing.JMenuItem jMenuItem5;
	private javax.swing.JMenuItem jMenuItem7;
	private javax.swing.JMenuItem jMenuItem8;
	private javax.swing.JMenuItem jMenuItem9;
	private javax.swing.JMenuItem jMenuSetName;// 命名规则
	private JMenuItem currPerMenuItem;
	private JMenuItem hisPerMenuItem;
	private JMenuItem perTaskMenuItem;
	private JMenuItem jMenuItemPathPerfor;//端到端性能统计
	private JMenuItem jMenuItemPerfor;//历时性能统计
	private JMenuItem currPerformCountMenuItem;//实时性能统计
	private JMenuItem currAlarmMenuItem;
	private JMenuItem hisAlarmMenuItem;
	private JMenuItem siteAlarmShieldItem;   //告警屏蔽
	private JMenuItem slotMenuItem;
	private JMenuItem siteCountMenuItem;
	private JMenuItem SubnetConfigMenuItem;
	private JMenuItem groupConfigMenuItem;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JTabbedPane jTabbedPane1;
	// private javax.swing.JToolBar jToolBar1;
	private javax.swing.JPanel jToolBar1;
	private javax.swing.JMenuItem qosTemplateMenuItem;
	private javax.swing.JMenuItem NeConfigMenuItem;
	private JButton btnToolsExit; // 退出系统工具栏按钮
	private JButton btnToolsAlarm; // 查询当前告警工具栏按钮
	private JButton btnToolsSegment; // 查询段工具栏按钮
	private JButton btnToolsTunnel; // 查询TUNNEL工具栏按钮
	private JButton btnToolsPw; // 查询PW工具栏按钮
	private JButton btnToolsEline; // 查询ELINE工具栏按钮
	private JButton btnToolsEtree; // 查询ETREE工具栏按钮
	private JButton btnToolsElan; // 查询ELAN工具栏按钮
	private JButton btnToolsCes; // 查询CES工具栏按钮
	private JButton btnToolsWrapping; // 查询环网保护工具栏按钮
	private CircleButton btnToolAlarmUrgency; // 查询紧急告警工具栏按钮
	private JLabel lblToolAlarmUrgencyNum; // 紧急告警数量
	private CircleButton btnToolAlarmMajor; // 查询重要告警工具栏按钮
	private JLabel lblToolAlarmMajorNum; // 重要告警数量
	private CircleButton btnToolAlarmMinor; // 查询次要告警工具栏按钮
	private JLabel lblToolAlarmMinorNum; // 次要告警数量
	private CircleButton btnToolAlarmPrompt; // 查询提示告警工具栏按钮
	private JLabel lblToolAlarmPromptNum; // 提示告警数量
	private javax.swing.JMenuItem jMenuItem10;
	private javax.swing.JMenuItem jMenuItem11;
	private javax.swing.JMenuItem jMenuItem12; // 网元物理连接带宽统计
	private javax.swing.JMenuItem jMenuItem13; // 服务路径带宽统计
	private javax.swing.JMenu jMenuLogin;//登陆管理
	private JMenuItem jMenuLoginLog;//查询登陆日志
	private JMenuItem jMenuImport;//接入设置
   
	private javax.swing.JMenuItem jMenuUserOnLine;// 在线用户查看
	// private javax.swing.JMenuItem menuItemClearLock; // 网元强制解锁
	private javax.swing.JMenuItem jMenuItemCard;// 单板信息统计
	private javax.swing.JMenuItem jMenuItemProfess;//业务统计
	private javax.swing.JMenuItem jMenuItemAlarm; // 告警统计列表
	private javax.swing.JMenuItem jMenuItemLable; // 标签统计列表
	private javax.swing.JMenuItem jMenuItemPort; // 端口统计列表
	private javax.swing.JMenuItem jMenuItemPath; // 端到端路径数量统计列表
	private javax.swing.JMenuItem jMenuItemNePath;//单网元路径数量统计列表
	private javax.swing.JMenuItem cirCount;// 配置信息统计链路QOS cir值的统计
	private javax.swing.JMenuItem help;// 帮助
	private javax.swing.JMenuItem about;// 关于
	
	private JMenuItem uploadDownloadConfigure;// 上载/下载配置文件
	private JMenuItem siteInitialise;// 网元的初始化
	private JMenuItem clientManagerMenuItem;
	private JMenuItem menuItemLogout; // 退出系统菜单
	private javax.swing.JMenuItem unloadItem;// 转储管理
	//数据管理
	private javax.swing.JMenuItem dataBackupsItem;
	private javax.swing.JMenuItem dataRecoverItem;
	private JMenuItem menuItemCloseAlarm; // 关闭告警声音
	private javax.swing.JMenuItem handLock;// 手动锁屏
	private javax.swing.JMenuItem userDesign;// 用户个性化设置
	private JMenuItem searchSiteMenuItem;
	private JMenuItem alarmVoiceMenuItem;// 告警声音设置
	private JMenuItem alarmReversal;//告警反转
	private JLabel loginLabel;// 登入用户名
	private JLabel neCountLable;//网元统计
	private JMenuItem jMenuOperationLog;// 操作日志查询
	private JMenuItem jMenuRoleManage;// 角色管理
	
	private JMenuItem oamEventMenuItem;// OAM事件
	private JMenuItem tcaAlarmMenuIten; // tca告警
	private javax.swing.JMenu modelManagerMenuItem;// 模板管理
	private JMenuItem cosToExpMenuItem;// COS到EXP映射
	private JMenuItem vlanpriToColorMenuItem;// VLANPRI到颜色映射
	private JMenuItem priorityToVlanpriMenuItem;// 优先业务到VLANPRI映射
	private javax.swing.JMenuItem DualInfoItem;// 双归保护管理
	private String logLabel = "";
	private String necount = "";
	private Map<String,Runnable> threadMap = new HashMap<String, Runnable>();
	private JMenu ethService;//以太网业务分类
	private javax.swing.JMenu siteCount;//网元统计
	private javax.swing.JMenu alarmCount;//告警统计
	private javax.swing.JMenu performanceCount;//性能统计
	private javax.swing.JMenu ethServiceCount;//业务统计
	private JMenuItem layerRateMenuItem;//层速率统计
	// End of variables declaration//GEN-END:variables
	private JMenuItem nameRuleMenuItem;//命名规则
	private JMenuItem performanceMenuItem;
	private javax.swing.JMenuItem systemMonitorBaseCount;//系统监护
	private JMenuItem batchSoftwateUpdate;// 批量升级

	//添加当前网元在线状态等指示
	private JLabel currSiteLabel;//当前网元状态
	private JLabel currSiteBtn;//当前网元状态指示灯
	//业务修复
	private JMenu serviceRepairedMenu;//业务修复
	private JMenuItem NNIPortMovedMenuItem;//NNI端口迁移
	private JMenuItem UNIPortMovedMenuItem;//UNI端口迁移
	private JMenuItem selectNe;// 定位网元
	private JMenuItem telnetManage;// telnet设置
	private JMenuItem versionInfo;//版本信息
	private JMenuItem loadPatchItem;//加载补丁
	private JMenuItem unLoadPatchItem;//卸载补丁
	private JMenuItem autoCorrectionItem;// 自动校正配置
	private JMenuItem jMenuOperation;//日志管理
	private JMenuItem unloadDeleteItem;// 备份删除管理
	private JMenuItem systemConfigItem;// 系统配置
	private JMenuItem systemLogItem;// 系统配置
	
	public Map<Integer, Map<String, CurrentAlarmInfo>> getCurrentAlarmMap() {
		return currentAlarmMap;
	}

	public void setCurrentAlarmMap(Map<Integer, Map<String, CurrentAlarmInfo>> currentAlarmMap) {
		this.currentAlarmMap = currentAlarmMap;
	}

	public Map<String, Runnable> getThreadMap() {
		return threadMap;
	}

	public void setThreadMap(Map<String, Runnable> threadMap) {
		this.threadMap = threadMap;
	}
	public boolean isHandLockScreen() {
		return handLockScreen;
	}
	public void setHandLockScreen(boolean handLockScreen) {
		this.handLockScreen = handLockScreen;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public HandLockDialog getHandLockDialog() {
		return handLockDialog;
	}
	public void setHandLockDialog(HandLockDialog handLockDialog) {
		this.handLockDialog = handLockDialog;
	}
	public JLabel getLoginLabel() {
		return loginLabel;
	}
	public void setLoginLabel(JLabel loginLabel) {
		this.loginLabel = loginLabel;
	}
	public String getLogLabel() {
		return logLabel;
	}
	public void setLogLabel(String logLabel) {
		this.logLabel = logLabel;
	}
	public javax.swing.JTabbedPane getjTabbedPane1() {
		return jTabbedPane1;
	}
	public void setjTabbedPane1(javax.swing.JTabbedPane jTabbedPane1) {
		this.jTabbedPane1 = jTabbedPane1;
	}

	public String getNecount() {
		return necount;
	}

	public void setNecount(String necount) {
		this.necount = necount;
	}

	public JLabel getNeCountLable() {
		return neCountLable;
	}

	public void setNeCountLable(JLabel neCountLable) {
		this.neCountLable = neCountLable;
	}

	public boolean isToop() {
		return isToop;
	}

	public void setToop(boolean isToop) {
		this.isToop = isToop;
	}
	
	public JLabel getLblToolAlarmUrgencyNum() {
		return lblToolAlarmUrgencyNum;
	}

	public JLabel getLblToolAlarmMajorNum() {
		return lblToolAlarmMajorNum;
	}

	public JLabel getLblToolAlarmMinorNum() {
		return lblToolAlarmMinorNum;
	}

	public JLabel getLblToolAlarmPromptNum() {
		return lblToolAlarmPromptNum;
	}
	
	public boolean isAlarmSoundSwitch() {
		return alarmSoundSwitch;
	}
	
	 
}
