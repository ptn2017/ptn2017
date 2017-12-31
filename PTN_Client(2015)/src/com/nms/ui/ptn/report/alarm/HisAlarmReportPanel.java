﻿package com.nms.ui.ptn.report.alarm;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import twaver.Alarm;
import twaver.Element;
import twaver.TDataBox;
import twaver.table.TAlarmTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.alarm.HisAlarmInfo;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.AlarmTools;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 */
public class HisAlarmReportPanel extends JPanel {
	private static final long serialVersionUID = -4046119221737730984L;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private final TDataBox box = new TDataBox();
	private TAlarmTable alarmTable;
	private JButton filterButton;// 设置过滤
	private JButton clearFilterButton;// 清除过滤
	private JLabel filterInfo;// 过滤条件
	private JButton refreshButton;
	private JButton exportButton;
	private HisAlarmReportController controller;
	private JPanel buttonPanel;
	int pageSize[] = null;
	private PtnButton prevPageBtn;
	private PtnButton nextPageBtn;
	private JLabel currPageLabel;
	private JLabel divideLabel;
	private JLabel totalPageLabel;

	public HisAlarmReportPanel() {
		init();
	}

	/*
	 * 初始化界面和数据
	 */
	public void init() {
		initComponents();
		setLayout();
		addListention();
		controller = new HisAlarmReportController(this);
		controller.refresh();
	}

	private void addListention() {
		this.refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.refresh();
			}
		});

		this.filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.openFilterDialog();
			}
		});

		this.clearFilterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.clearFilter();
			}
		});

		this.alarmTable.addAlarmClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Alarm alarm = (Alarm) e.getSource();
				Element element = HisAlarmReportPanel.this.box.getElementByID(alarm.getElementID());
				HisAlarmReportPanel.this.box.getSelectionModel().setSelection(element);
			}
		});
		exportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					export();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
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

	public void export()throws Exception{
		CSVUtil csvUtil=null;
		String[] s={};
		String path=null;
		OperationLog operationLog=null;
		OperationLogService_MB operationService=null;
		List<HisAlarmInfo> hisAlarmInfos=null;
		UiUtil uiUtil = new UiUtil();
		int comfirmResult = 0;
		String csvFilePath = "";
		try{
			hisAlarmInfos=new ArrayList<HisAlarmInfo>();
			hisAlarmInfos=controller.getHisAlarmInfos();
			csvUtil=new CSVUtil();
	        operationLog=new OperationLog();
			operationLog.setOperationType(EOperationLogType.HISALARMEXPORT.getValue());
			path = csvUtil.showChooserWindow("save","选择文件",s);
			
			if(path != null && !"".equals(path)){
				csvFilePath = path + ".csv";
				if(uiUtil.isExistAlikeFileName(csvFilePath)){
					comfirmResult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
					if(comfirmResult == 1){
						return ;
					}
				}
				operationLog.setStartTime(DateUtil.getDate(DateUtil.FULLTIME));	
				csvUtil.WriteHisCsv(csvFilePath,hisAlarmInfos,null);
				operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
				operationLog.setOperationResult(1);
				operationLog.setUser_id(ConstantUtil.user.getUser_Id());
				operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);				
				operationService.insertOperationLog(operationLog);
			}
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(operationService);
		}
	}
	/*
	 * 设置右键菜单
	 */
	public void setTablePopupMenuFactory(TTablePopupMenuFactory factory) {
		alarmTable.setTableBodyPopupMenuFactory(factory);
	}

	public void initComponents() {
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_ALARM_REPORT)));
		filterInfo = new JLabel(ResourceUtil.srcStr(StringKeysBtn.BTN_FILETER_CHOOSE));
		exportButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT), RootFactory.ALARM_SELECT);
		refreshButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REFRESH));
		filterButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		clearFilterButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
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
		alarmTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		alarmTable.setDragEnabled(false);
		contentScrollPane.setViewportView(alarmTable);
		alarmTable.setTableHeaderPopupMenuFactory(null);
		alarmTable.setTableBodyPopupMenuFactory(null);
		pageSize = new int[] { 0, 10, 30, 50 };
		buttonPanel = new JPanel();
	}

	/*
	 * 工具按钮布局
	 */
	public void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 40,140, 15,10,5,10,15 };
		buttonLayout.columnWeights = new double[] { 0,0, 0,0, 0.4, 0 };
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
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(exportButton, c);
		buttonPanel.add(exportButton);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(refreshButton, c);
		buttonPanel.add(refreshButton);
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		buttonLayout.setConstraints(filterButton, c);
		buttonPanel.add(filterButton);
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		buttonLayout.setConstraints(clearFilterButton, c);
		buttonPanel.add(clearFilterButton);
		
		c.gridx = 5;
		buttonLayout.setConstraints(prevPageBtn, c);
		buttonPanel.add(prevPageBtn);
		
		c.gridx = 6;
		buttonLayout.setConstraints(currPageLabel, c);
		buttonPanel.add(currPageLabel);
		
		c.gridx = 7;
		buttonLayout.setConstraints(divideLabel, c);
		buttonPanel.add(divideLabel);
		
		c.gridx = 8;
		buttonLayout.setConstraints(totalPageLabel, c);
		buttonPanel.add(totalPageLabel);
		
		c.gridx = 9;
		buttonLayout.setConstraints(nextPageBtn, c);
		buttonPanel.add(nextPageBtn);
	}

	public void setLayout() {
		setButtonLayout();
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
		panelLayout.setConstraints(contentPanel, c);
		this.add(contentPanel);
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

	public HisAlarmReportController getController() {
		return controller;
	}

	public JLabel getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(JLabel filterInfo) {
		this.filterInfo = filterInfo;
	}

	public JButton getPrevPageBtn() {
		return prevPageBtn;
	}

	public void setPrevPageBtn(PtnButton prevPageBtn) {
		this.prevPageBtn = prevPageBtn;
	}

	public JButton getNextPageBtn() {
		return nextPageBtn;
	}

	public void setNextPageBtn(PtnButton nextPageBtn) {
		this.nextPageBtn = nextPageBtn;
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

	/*
	 * 显示过滤条件
	 */
	public void setFilterInfos(String str) {
		str = "过滤条件=" + str;
		if (str.length() > 105) {
			str = str.substring(0, 105);
			str = str + "...";
		}
		this.getFilterInfo().setText(str);

	}

	/*
	 * 清除过滤条件
	 */
	public void clear() {
		filterInfo.setText(ResourceUtil.srcStr(StringKeysBtn.BTN_FILETER_CHOOSE));
		this.alarmTable.getDataBox().clear();
		box.getAlarmModel().clear();
	}

	/*
	 * 初始化数据
	 */
	public void initData(List<HisAlarmInfo> infos) {
		if (infos != null && infos.size() > 0) {
			for (HisAlarmInfo info : infos) {
				info.putClientProperty();
				box.getAlarmModel().addAlarm(info);
			}
		}

	}

	public TAlarmTable getAlarmTable() {
		return alarmTable;
	}

	public TDataBox getBox() {
		return box;
	}
}
