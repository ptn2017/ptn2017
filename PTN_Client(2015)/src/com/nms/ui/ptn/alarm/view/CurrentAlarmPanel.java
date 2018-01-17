﻿package com.nms.ui.ptn.alarm.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import twaver.TDataBox;
import twaver.table.TAlarmTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.alarm.HisAlarmInfo;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.alarm.HisAlarmService_MB;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.ptn.alarm.AlarmTools;
import com.nms.ui.ptn.alarm.controller.CurrentAlarmController;
import com.nms.ui.ptn.alarm.service.AlarmAnalyse;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 当前告警面板
 * 
 * @author lp
 * 
 */
public class CurrentAlarmPanel extends JPanel {

	private static final long serialVersionUID = -7556197239484214641L;

	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private TDataBox box = new TDataBox();
	private TAlarmTable alarmTable; // 告警table表
	private JButton filterButton; // 设置过滤
	private JButton clearFilterButton; // 清除过滤
	private JLabel filterInfo; // 过滤条件
	private JButton exportButton;// 导出按钮
	private JButton refreshButton; // 刷新按钮
	private JTabbedPane tabbedPane;
	private JPanel userInfoPanel;
	private JSplitPane splitPane;
	public  CurrentAlarmController controller;
	private JPanel buttonPanel;
	private JLabel warningNameLabel;
	private JTextField warningNameText;
	private JLabel warningDescLabel;
	private JTextArea warnningDescText;
	private JLabel effectLabel;// 影响;
	private JTextArea effectText;
	private JLabel solutionLabel;// 解决方案
	private JTextArea solutionText;
	private JLabel mayreasonLabel;// 告警可能原因
	private JTextArea mayreasonText;
	private JLabel reserveLabel;// 备注
	private JTextArea reserveText;
	private JLabel relatedPathLabel;// 关联路径
	private JTextArea relatedPathText;

	private JSplitPane analysisAlarmSplitPane;// 告警分析
	private JScrollPane mainBusinessScrollPane;
	private JScrollPane relatedBusinessScrollPane;
	private JPanel mainBusinessPanel;// 主要业务
	private JPanel relatedBusinessPanel;// 关联影响业务
	private JLabel mainBusinessLabel;
	private JLabel relatedBusinessLabel;
	@SuppressWarnings("rawtypes")
	private ViewDataTable mainBusinessTable;
	@SuppressWarnings("rawtypes")
	private ViewDataTable relatedBusinessTable;
	private PtnButton btnClear;
	private JButton btnAffirm;
	private PtnButton synchroButton;//同步按钮
	int pageSize[] = null;
	private PtnButton prevPageBtn;
	private PtnButton nextPageBtn;
	private JLabel currPageLabel;
	private JLabel divideLabel;
	private JLabel totalPageLabel;

	public CurrentAlarmPanel() {
		init();
	}

	public void init() {
		initComponents();
		setLayout();
		addListention();
		controller = new CurrentAlarmController(this);
	}

	private void addListention() {
		
		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				controller.refresh();
			}
		});

		filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				controller.openFilterDialog();

			}
		});

		exportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					export();
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});

		clearFilterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				controller.clearFilter();
				controller.refresh();
			}
		});
         
		synchroButton.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.synchro();
			}
			
			@Override
			public boolean checking() {
				return true;
			}
		});
		
		
		alarmTable.addAlarmClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				alarmAnalyseShow();
			}
		});
		
		JPopupMenu analyzeMenu = new JPopupMenu();
		JMenuItem analyzeItem = new JMenuItem("告警解析");
		analyzeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (alarmTable.getAllSelectedAlarms().size() > 0) {
					try {
						UiUtil.showWindow(new AnalyzeAlamTable(alarmTable.getAllSelectedAlarms()), 950, 520);
					} catch(Exception e1){
						e1.printStackTrace();
					}
				}
			}
		});
		analyzeMenu.add(analyzeItem);
		alarmTable.setInheritsPopupMenu(true);
		alarmTable.setComponentPopupMenu(analyzeMenu);
		
		btnAffirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (alarmTable.getAllSelectedAlarms().size() > 0) {
					try {
						// 新增告警确认添加备注 add by dxh
						CurrentAlarmInfo currentAlarmInfo = (CurrentAlarmInfo) alarmTable.getAllSelectedAlarms().get(0);
						AlarmBack alarmremark = new AlarmBack(currentAlarmInfo.getId());
						UiUtil.showWindow(alarmremark, 300, 180);
						affirmAlarm(alarmremark.getRemarks());
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				} else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
					return;
				}
			}

		});

		btnClear.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (alarmTable.getAllSelectedAlarms().size() > 0) {
					try {
						clearAlarm();
						controller.refresh();
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}else {
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_MORE));
					return;
				}
			}

			@Override
			public boolean checking() {
				return true;
			}
		});

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int index = tabbedPane.getSelectedIndex();
				switch (index) {
				// 用户信息面板
				case 0:
					// initUserInfoPanel();
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				default:
					break;
				}
			}
		});
		
		this.prevPageBtn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.prevPage();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
		
		this.nextPageBtn.addActionListener(new MyActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.nextPage();
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
	}

	public void alarmAnalyseShow() {
		// 新增告警分析 add dxh
		if (alarmTable.getAllSelectedAlarms() != null && alarmTable.getAllSelectedAlarms().size() > 0) {
			CurrentAlarmInfo currentAlarmInfo = (CurrentAlarmInfo) alarmTable.getAllSelectedAlarms().get(0);
			AlarmAnalyse aa = new AlarmAnalyse(currentAlarmInfo, mainBusinessTable, relatedBusinessTable);
			if (alarmTable.getSelectedRowCount() > 0) {
				
				aa.show();

				// 新增用户信息
				warningNameText.setText(currentAlarmInfo.getWarningLevel().getWarningname());// 告警名称
				warnningDescText.setText(currentAlarmInfo.getAlarmDesc());// 描述
				effectText.setText(currentAlarmInfo.getWarningLevel().getWaringeffect());// 影响
				reserveText.setText(currentAlarmInfo.getAlarmComments());// 备注
				solutionText.setText(currentAlarmInfo.getWarningLevel().getWarningadvice());// 解决方案
				mayreasonText.setText(currentAlarmInfo.getWarningLevel().getWarningmayreason());
			} else {
				aa.clearData();
			}
		}
	}

	/**
	 * 确认告警
	 * 
	 * @throws Exception
	 */
	public void affirmAlarm(String remarks) throws Exception {
		List<CurrentAlarmInfo> currentAlarmInfoList = this.getCurrentAlarmInfoList();
		DispatchUtil alarmDispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
		CurAlarmService_MB service = null;
		HisAlarmService_MB hisservice = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			service = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			hisservice = (HisAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.HisAlarm);
			if (null != currentAlarmInfoList && currentAlarmInfoList.size() > 0) {
				for (CurrentAlarmInfo currentAlarmInfo : currentAlarmInfoList) {
					currentAlarmInfo.setAckUser(ConstantUtil.user.getUser_Name());
					currentAlarmInfo.setAckTime(new Date());
					currentAlarmInfo.setCommentS(remarks);
					currentAlarmInfo.setAlarmTime(sdf.format(currentAlarmInfo.getRaisedTime()));
					if(currentAlarmInfo.getClearedTime() != null)
					{
						service.delete(currentAlarmInfo.getId());
						alarmDispatch.clearAlarm(currentAlarmInfo);
						hisservice.saveOrUpdate(getHisAlarm(currentAlarmInfo));
					}
					else
					{
						service.saveOrUpdate(currentAlarmInfo);
						AddOperateLog.insertOperLog(btnClear, EOperationLogType.ALARMCONFIRM.getValue(), ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null, null,
								currentAlarmInfoList.get(0).getSiteId(), currentAlarmInfo.getObjectName()+"/"+currentAlarmInfo.getAlarmDesc(), "");
					}
				}
			}
			// 清除告警后应该刷新下top,让告警的数量显示正确
			TopologyUtil topologyUtil = new TopologyUtil();
			topologyUtil.updateSiteInstAlarm(service);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(hisservice);
		}
	}

	/**
	 * 已确认的清除告警放到历史告警中，未确认的清除告警还是放在当前告警里面
	 * @throws Exception
	 */
	public void clearAlarm() throws Exception {
		DispatchUtil alarmDispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
		List<CurrentAlarmInfo> currentAlarmInfoList = this.getCurrentAlarmInfoList();
		CurAlarmService_MB curservice = null;
		HisAlarmService_MB hisservice = null;
		String lockTip = ResourceUtil.srcStr(StringKeysTip.TIP_ALARMLOCK);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			curservice = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			hisservice = (HisAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.HisAlarm);
			if (null != currentAlarmInfoList && currentAlarmInfoList.size() > 0) {
				for (CurrentAlarmInfo currentAlarmInfo : currentAlarmInfoList) {
					currentAlarmInfo.setAlarmTime(sdf.format(currentAlarmInfo.getRaisedTime()));
					if(currentAlarmInfo.getAckTime()!=null || currentAlarmInfo.getAckUser() != null)
					{
						curservice.delete(currentAlarmInfo.getId());
						alarmDispatch.clearAlarm(currentAlarmInfo);
						currentAlarmInfo.setIsCleared(ResourceUtil.srcStr(StringKeysTip.TIP_CLEARED));
						hisservice.saveOrUpdate(getHisAlarm(currentAlarmInfo));
					}
					else
					{
						currentAlarmInfo.setClearedTime(new Date());
						currentAlarmInfo.setCommentS(lockTip);
						currentAlarmInfo.setIsCleared(ResourceUtil.srcStr(StringKeysTip.TIP_CLEARED));
						curservice.saveOrUpdate(currentAlarmInfo);
					}
					AddOperateLog.insertOperLog(btnClear, EOperationLogType.ALARMCLEAN.getValue(), ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS), null, null, 
							currentAlarmInfoList.get(0).getSiteId(), currentAlarmInfo.getObjectName()+"/"+currentAlarmInfo.getAlarmDesc(), "");
				}
			}
			// 清除告警后应该刷新下top,让告警的数量显示正确
			TopologyUtil topologyUtil = new TopologyUtil();
			topologyUtil.updateSiteInstAlarm(curservice);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(curservice);
			UiUtil.closeService_MB(hisservice);
		}
	}

	/**
	 * 获得选中告警
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<CurrentAlarmInfo> getCurrentAlarmInfoList() {
		List<CurrentAlarmInfo> currentAlarmInfoList = new ArrayList<CurrentAlarmInfo>();

		for (int i = 0; i < this.alarmTable.getSelectedRowCount(); i++) {
			Vector v = this.alarmTable.getRowDataByRowIndex(this.alarmTable.getSelectedRows()[i]);
			for (Object obj : v) {
				if (null != obj) {
					if (obj instanceof CurrentAlarmInfo) {
						CurrentAlarmInfo currentAlarmInfo = (CurrentAlarmInfo) obj;
						currentAlarmInfoList.add(currentAlarmInfo);
						break;
					}
				}
			}
		}
		return currentAlarmInfoList;
	}

	/**
	 * 获得历史告警对象
	 * 
	 * @param curAlarmInfo
	 * @return
	 */
	public HisAlarmInfo getHisAlarm(CurrentAlarmInfo curAlarmInfo) {
		HisAlarmInfo hisAlarmInfo = new HisAlarmInfo();
		hisAlarmInfo.setSiteId(curAlarmInfo.getSiteId());
		hisAlarmInfo.setSlotId(curAlarmInfo.getSlotId());
		hisAlarmInfo.setObjectId(curAlarmInfo.getObjectId());
		hisAlarmInfo.setObjectType(curAlarmInfo.getObjectType());
		hisAlarmInfo.setAlarmCode(curAlarmInfo.getAlarmCode());
		hisAlarmInfo.setAlarmLevel(curAlarmInfo.getAlarmLevel());
		hisAlarmInfo.setObjectName(curAlarmInfo.getObjectName());
		hisAlarmInfo.setAckUser(curAlarmInfo.getAckUser());
		hisAlarmInfo.setAckTime(curAlarmInfo.getAckTime());
		hisAlarmInfo.setRaisedTime(curAlarmInfo.getRaisedTime());
		hisAlarmInfo.setIsCleared(curAlarmInfo.getIsCleared());
		hisAlarmInfo.setClearedTime(new Date());
		hisAlarmInfo.setAlarmId(curAlarmInfo.getId());
		hisAlarmInfo.setCommonts(curAlarmInfo.getAlarmComments());

		return hisAlarmInfo;
	}

	public void setTablePopupMenuFactory(TTablePopupMenuFactory factory) {
		alarmTable.setTableBodyPopupMenuFactory(factory);
	}

	public void export() throws Exception {
		CSVUtil csvUtil = null;
		String[] s = {};
		String path = null;
		OperationLogService_MB operationService = null;
		OperationLog operationLog = null;
		List<CurrentAlarmInfo> infos = null;
		UiUtil uiUtil = new UiUtil();
		int reult = 0;
		try {
			infos = new ArrayList<CurrentAlarmInfo>();
			infos = this.getController().getCurrInfos();
			csvUtil = new CSVUtil();
			operationLog = new OperationLog();
			operationLog.setOperationType(EOperationLogType.CURRENTALARMEXPORT.getValue());
			path = csvUtil.showChooserWindow("save", "选择文件", s);
			
			if(path != null && !"".equals(path)){
				String csvFilePath = path + ".csv";
				if(uiUtil.isExistAlikeFileName(csvFilePath)){
					reult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
					if(reult == 1){
						return ;
					}
				}
				operationLog.setStartTime(DateUtil.getDate(DateUtil.FULLTIME));
				csvUtil.WriteCsv(csvFilePath, infos);
				operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
				operationLog.setOperationResult(1);
				operationLog.setUser_id(ConstantUtil.user.getUser_Id());
				operationService = (OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
				operationService.insertOperationLog(operationLog);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(operationService);
		}
	}

	@SuppressWarnings("rawtypes")
	public void initComponents() {
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_CURRALARM)));
		filterInfo = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_FILETER_CHOOSE));
		refreshButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REFRESH));
		exportButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT), RootFactory.ALARM_SELECT);
		filterButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		clearFilterButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		synchroButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO),true);
		this.prevPageBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_PREV_PAGE), 
				false, RootFactory.COREMODU, null);
		this.nextPageBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_NEXT_PAGE),
				false, RootFactory.COREMODU, null);
		this.currPageLabel = new JLabel("1");
		this.divideLabel = new JLabel("/");
		this.totalPageLabel = new JLabel("1");
		AlarmTools alarmTools=new AlarmTools();
		alarmTable = new TAlarmTable(box, alarmTools.createDefaultColumns());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height = 220;
		alarmTable.setPreferredScrollableViewportSize(screenSize);
		alarmTable.getTableHeader().setResizingAllowed(true);
		alarmTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		alarmTable.setDragEnabled(true);
		alarmTable.setColumnResizable(true);
		alarmTable.setRowResizable(false);
		contentScrollPane.setViewportView(alarmTable);
		// 用户信息面板
		userInfoPanel = new JPanel();
		warningNameLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_ALARM));
		warningNameText = new JTextField(50);
		warningNameText.setEditable(false);
		warningDescLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_DESC));
		warnningDescText = new JTextArea(5, 50);
		warnningDescText.setEditable(false);
		warnningDescText.setLineWrap(true);
		warnningDescText.setWrapStyleWord(true);
		warnningDescText.setBorder(BorderFactory.createEtchedBorder());
		effectLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_IMPACT));
		effectText = new JTextArea(5, 50);
		effectText.setEditable(false);
		effectText.setLineWrap(true);
		effectText.setWrapStyleWord(true);
		effectText.setBorder(BorderFactory.createEtchedBorder());
		solutionLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_SOLUTION));
		solutionText = new JTextArea(5, 50);
		solutionText.setEditable(false);
		solutionText.setLineWrap(true);
		solutionText.setWrapStyleWord(true);
		solutionText.setBorder(BorderFactory.createEtchedBorder());
		reserveLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_REMARK));
		reserveText = new JTextArea(5, 50);
		reserveText.setEditable(false);
		reserveText.setLineWrap(true);
		reserveText.setWrapStyleWord(true);
		reserveText.setBorder(BorderFactory.createEtchedBorder());
		mayreasonLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_MAY_REASON));
		mayreasonText = new JTextArea(10, 50);
		mayreasonText.setEditable(false);
		mayreasonText.setLineWrap(true);
		mayreasonText.setWrapStyleWord(true);
		mayreasonText.setBorder(BorderFactory.createEtchedBorder());
		// 告警分析面板
		// 主要业务
		mainBusinessScrollPane = new JScrollPane();
		mainBusinessPanel = new JPanel();
		mainBusinessLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_MAINLY_AFFECT_BUSINESS));

		// 关联影响业务
		relatedBusinessScrollPane = new JScrollPane();
		relatedBusinessPanel = new JPanel();
		relatedBusinessLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_ASSOCIATED_AFFECT_BUSINESS));
		// table
		mainBusinessTable = new ViewDataTable("analysisAlarmTable");
		mainBusinessTable.getTableHeader().setResizingAllowed(true);
		mainBusinessTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		mainBusinessTable.setTableHeaderPopupMenuFactory(null);
		mainBusinessTable.setTableBodyPopupMenuFactory(null);

		Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize2.height = 220;
		screenSize2.width = 800;
		mainBusinessTable.setPreferredScrollableViewportSize(screenSize2);

		mainBusinessScrollPane.setViewportView(mainBusinessTable);
		mainBusinessScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		relatedBusinessTable = new ViewDataTable("analysisAlarmTable");
		relatedBusinessTable.getTableHeader().setResizingAllowed(true);
		relatedBusinessTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		relatedBusinessTable.setTableHeaderPopupMenuFactory(null);
		relatedBusinessTable.setTableBodyPopupMenuFactory(null);

		relatedBusinessTable.setPreferredScrollableViewportSize(screenSize2);

		relatedBusinessScrollPane.setViewportView(relatedBusinessTable);
		relatedBusinessScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		analysisAlarmSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		analysisAlarmSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		analysisAlarmSplitPane.setTopComponent(mainBusinessPanel);
		analysisAlarmSplitPane.setBottomComponent(relatedBusinessPanel);
		analysisAlarmSplitPane.setOneTouchExpandable(true);
		double high2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		// ---------------------------------

		analysisAlarmSplitPane.setDividerLocation(Double.valueOf(high2).intValue() / 10);

		tabbedPane = new JTabbedPane();

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(contentPanel);
		splitPane.setBottomComponent(tabbedPane);
		alarmTable.setTableHeaderPopupMenuFactory(null);
		alarmTable.setTableBodyPopupMenuFactory(null);
		pageSize = new int[] { 0, 10, 30, 50 };
		// navigator = new TPageNavigator(alarmTable.getTableModel(), pageSize);
		buttonPanel = new JPanel();

		this.btnClear = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CLEAR_ALARM), RootFactory.ALARM_MANAGE);
		this.btnAffirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_AFFIRM_ALARM), RootFactory.ALARM_MANAGE);
	}

	public void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 40, 40, 40, 40,140, 15,10,5,10,15 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0.4,0 };
		buttonLayout.rowHeights = new int[] { 40 };
		buttonLayout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		buttonPanel.setLayout(buttonLayout);
		// 操作菜单按钮布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(exportButton, c);
		buttonPanel.add(exportButton);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(refreshButton, c);
		buttonPanel.add(refreshButton);
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(filterButton, c);
		buttonPanel.add(filterButton);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(clearFilterButton, c);
		buttonPanel.add(clearFilterButton);
		c.gridx = 4;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(this.btnAffirm, c);
		buttonPanel.add(this.btnAffirm);
		c.gridx = 5;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(this.btnClear, c);
		buttonPanel.add(this.btnClear);

		c.gridx = 6;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 5);
		buttonLayout.setConstraints(this.synchroButton, c);
		buttonPanel.add(this.synchroButton);
		
		c.gridx = 8;
		c.fill = GridBagConstraints.NONE;
		buttonLayout.setConstraints(this.prevPageBtn, c);
		buttonPanel.add(this.prevPageBtn);
		
		c.gridx = 9;
		buttonLayout.setConstraints(this.currPageLabel, c);
		buttonPanel.add(this.currPageLabel);
		
		c.gridx = 10;
		buttonLayout.setConstraints(this.divideLabel, c);
		buttonPanel.add(this.divideLabel);
		
		c.gridx = 11;
		buttonLayout.setConstraints(this.totalPageLabel, c);
		buttonPanel.add(this.totalPageLabel);
		
		c.gridx = 12;
		buttonLayout.setConstraints(this.nextPageBtn, c);
		buttonPanel.add(this.nextPageBtn);

		// c.gridx = 3;
		// c.gridy = 0;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(0, 5, 0, 5);
		// buttonLayout.setConstraints(filterInfo, c);
		// buttonPanel.add(filterInfo);
		// c.gridx = 4;
		// c.gridy = 0;
		// c.gridheight = 1;
		// c.gridwidth = 1;
		// c.insets = new Insets(0, 5, 0, 5);
		// buttonLayout.setConstraints(navigator, c);
		// buttonPanel.add(navigator);
	}

	public void setTabbedPaneLayout() {

		setUserInfoLayout();
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_USER_INFO), userInfoPanel);
		tabbedPane.setSelectedIndex(0);

		setAnalysisAlarmLayout();
		tabbedPane.add(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_ALARM_ANALYSIS), analysisAlarmSplitPane);

	}

	public void setUserInfoLayout() {
		GridBagLayout userInfoLayout = new GridBagLayout();
		userInfoLayout.columnWidths = new int[] { 40, 80, 40, 80, 40, 80 };
		userInfoLayout.columnWeights = new double[] { 0, 0.2, 0, 0.2, 0, 0.2 };
		userInfoLayout.rowHeights = new int[] { 40, 50, 50 };
		userInfoLayout.rowWeights = new double[] { 0, 0.1, 0.1 };
		userInfoPanel.setLayout(userInfoLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		userInfoLayout.setConstraints(warningNameLabel, c);
		userInfoPanel.add(warningNameLabel);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		userInfoLayout.setConstraints(warningNameText, c);
		userInfoPanel.add(warningNameText);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		userInfoLayout.setConstraints(warningDescLabel, c);
		userInfoPanel.add(warningDescLabel);

		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		userInfoLayout.setConstraints(warnningDescText, c);
		userInfoPanel.add(warnningDescText);

		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		userInfoLayout.setConstraints(effectLabel, c);
		userInfoPanel.add(effectLabel);

		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		userInfoLayout.setConstraints(effectText, c);
		userInfoPanel.add(effectText);

		c.gridx = 4;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		userInfoLayout.setConstraints(mayreasonLabel, c);
		userInfoPanel.add(mayreasonLabel);

		c.gridx = 5;
		c.gridy = 1;
		c.gridheight = 2;
		c.gridwidth = 1;
		userInfoLayout.setConstraints(mayreasonText, c);
		userInfoPanel.add(mayreasonText);

		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		userInfoLayout.setConstraints(solutionLabel, c);
		userInfoPanel.add(solutionLabel);

		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		userInfoLayout.setConstraints(solutionText, c);
		userInfoPanel.add(solutionText);

		c.gridx = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		userInfoLayout.setConstraints(reserveLabel, c);
		userInfoPanel.add(reserveLabel);

		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		userInfoLayout.setConstraints(reserveText, c);
		userInfoPanel.add(reserveText);

	}

	public void setAnalysisAlarmLayout() {
		GridBagLayout mainBusinessLayout = new GridBagLayout();
		mainBusinessPanel.setLayout(mainBusinessLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 0);
		mainBusinessLayout.setConstraints(mainBusinessLabel, c);
		mainBusinessPanel.add(mainBusinessLabel);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 10;
		c.weightx = 0.2;
		c.weighty = 0.4;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 2);
		mainBusinessLayout.setConstraints(mainBusinessScrollPane, c);
		mainBusinessPanel.add(mainBusinessScrollPane);

		GridBagLayout relatedBusinessLayout = new GridBagLayout();
		relatedBusinessPanel.setLayout(relatedBusinessLayout);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 0);
		relatedBusinessLayout.setConstraints(relatedBusinessLabel, c);
		relatedBusinessPanel.add(relatedBusinessLabel);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.2;
		c.weighty = 0.4;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 2);
		relatedBusinessLayout.setConstraints(relatedBusinessScrollPane, c);
		relatedBusinessPanel.add(relatedBusinessScrollPane);

	}

	public void setLayout() {
		setButtonLayout();
		setTabbedPaneLayout();
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(buttonPanel, c);
		contentPanel.add(buttonPanel);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.4;
		c.insets = new Insets(0, 0, 2, 0);
		contentLayout.setConstraints(contentScrollPane, c);
		contentPanel.add(contentScrollPane);
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(splitPane, c);
		this.add(splitPane);
	}

	public void add() {
	}

	public JButton getFilterButton() {
		return filterButton;
	}

	public void setFilterButton(JButton filterButton) {
		this.filterButton = filterButton;
	}

	public JButton getClearFilterButton() {
		return clearFilterButton;
	}

	public void setClearFilterButton(JButton clearFilterButton) {
		this.clearFilterButton = clearFilterButton;
	}

	public JButton getRefreshButton() {
		return refreshButton;
	}

	public void setRefreshButton(JButton refreshButton) {
		this.refreshButton = refreshButton;
	}

	public CurrentAlarmController getController() {
		return controller;
	}

	public void setController(CurrentAlarmController controller) {
		this.controller = controller;
	}

	public JLabel getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(JLabel filterInfo) {
		this.filterInfo = filterInfo;
	}

	public void setFilterInfos(String str) {
		str = "过滤条件=" + str;
		if (str.length() > 105) {
			str = str.substring(0, 105);
			str = str + "...";
		}
		this.getFilterInfo().setText(str);
	}

	public void clear() {
		filterInfo.setText("过滤条件=请选择过滤条件！");
		this.alarmTable.getDataBox().clear();
		box.getAlarmModel().clear();
	}

	public void initData(List<CurrentAlarmInfo> infos) {
		if (infos != null && infos.size() > 0) {
			for (CurrentAlarmInfo info : infos) {
				info.putClientProperty();
				box.getAlarmModel().addAlarm(info);
			}
		}

	}

	public TDataBox getBox() {
		return box;
	}

	public void setBox(TDataBox box) {
		this.box = box;
	}

	public PtnButton getPrevPageBtn() {
		return prevPageBtn;
	}

	public void setPrevPageBtn(PtnButton prevPageBtn) {
		this.prevPageBtn = prevPageBtn;
	}

	public PtnButton getNextPageBtn() {
		return nextPageBtn;
	}

	public JLabel getCurrPageLabel() {
		return currPageLabel;
	}

	public void setCurrPageLabel(JLabel currPageLabel) {
		this.currPageLabel = currPageLabel;
	}

	public JLabel getTotalPageLabel() {
		return totalPageLabel;
	}

	public void setTotalPageLabel(JLabel totalPageLabel) {
		this.totalPageLabel = totalPageLabel;
	}

	public void setNextPageBtn(PtnButton nextPageBtn) {
		this.nextPageBtn = nextPageBtn;
	}
}
