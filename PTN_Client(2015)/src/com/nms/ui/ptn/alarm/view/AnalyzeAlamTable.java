package com.nms.ui.ptn.alarm.view;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import twaver.TDataBox;
import twaver.table.TAlarmTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.alarm.AlarmTools;
import com.nms.ui.ptn.alarm.controller.AlarmController;

public class AnalyzeAlamTable extends PtnDialog {
	private static final long serialVersionUID = -4046119221737730984L;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private TDataBox box = new TDataBox();
	private TAlarmTable alarmTable;
//	private JButton refreshButton;
	private AlarmController controller;
	private JPanel buttonPanel;
	int pageSize[] = null;
	private CurrentAlarmInfo alarm;
	
	public AnalyzeAlamTable(CurrentAlarmInfo currentAlarmInfo) {
		this.alarm = currentAlarmInfo;
		init();
	}

	/*
	 * 初始化界面和数据
	 */
	public void init() {
		initComponents();
		setLayout();
		addListention();
		initData();
	}

	private void addListention() {
//		this.refreshButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				controller.refresh();
//			}
//		});

		this.alarmTable.addAlarmClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

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
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_CURRALARM)));
//		refreshButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REFRESH));
		AlarmTools alarmTools=new AlarmTools();
		alarmTable = new TAlarmTable(box, alarmTools.createColumns());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height = 220;
		screenSize.width = 120;
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
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 180, 80 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0, 0.4, 0 };
		buttonLayout.rowHeights = new int[] { 40 };
		buttonLayout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		buttonPanel.setLayout(buttonLayout);
//		// 操作菜单按钮布局
//		c.gridx = 0;
//		c.gridy = 0;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 5);
//		c.fill = GridBagConstraints.HORIZONTAL;
//		buttonLayout.setConstraints(refreshButton, c);
//		buttonPanel.add(refreshButton);
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

//	public JButton getRefreshButton() {
//		return refreshButton;
//	}
//
//	public void setRefreshButton(JButton refreshButton) {
//		this.refreshButton = refreshButton;
//	}

	public AlarmController getController() {
		return controller;
	}


	/*
	 * 初始化数据
	 */
	private void initData() { 
		// 如果是link_los
		if(this.alarm.getAlarmCode() == 11){
			List<CurrentAlarmInfo> showList = new ArrayList<CurrentAlarmInfo>();
			showList.add(this.alarm);
			this.alarm.setAnalyze("根源告警");
			CurAlarmService_MB service = null;
			LspInfoService_MB lspService = null;
			PwInfoService_MB pwService = null;
			PortService_MB portService = null;
			try {
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				service = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CURRENTALAMSERVICE);
				lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				PortInst con = new PortInst();
				con.setSiteId(this.alarm.getSiteId());
				con.setNumber(this.alarm.getObjectId());
				PortInst port = portService.select(con).get(0);
				List<Lsp> lspList = lspService.selectByPort(port.getPortId());
				List<Integer> tBusinessIdList = new ArrayList<Integer>();
				List<Integer> pwBusinessIdList = new ArrayList<Integer>();
				if(lspList != null){
					List<Integer> tIdList = new ArrayList<Integer>();
					for(Lsp lsp : lspList){
						if(lsp.getASiteId() == this.alarm.getSiteId()){
							tIdList.add(lsp.getTunnelId());
							tBusinessIdList.add(lsp.getAtunnelbusinessid());
						}else if(lsp.getZSiteId() == this.alarm.getSiteId()){
							tIdList.add(lsp.getTunnelId());
							tBusinessIdList.add(lsp.getZtunnelbusinessid());
						}
					}
					List<PwInfo> pwList = pwService.select(this.alarm.getSiteId());
					if(pwList != null){
						for(PwInfo pw : pwList){
							if(tIdList.contains(pw.getTunnelId())){
								if(pw.getASiteId() == this.alarm.getSiteId()){
									pwBusinessIdList.add(pw.getApwServiceId());
								}else if(pw.getZSiteId() == this.alarm.getSiteId()){
									pwBusinessIdList.add(pw.getZpwServiceId());
								}
							}
						}
					}
				}
				List<CurrentAlarmInfo> allList = service.select();
				for(CurrentAlarmInfo info : allList){
					info.setAnalyze("衍生告警");
					int alarmCode = info.getAlarmCode();
					if(info.getSiteId() == this.alarm.getSiteId()){
						// TMS_LOC,TMP_LOC,TMC_LOC
						if(alarmCode == 10){
							if(info.getObjectId() == this.alarm.getObjectId()){
								showList.add(info);
							}
						}else if(alarmCode == 7){
							if(tBusinessIdList.contains(info.getObjectId())){
								showList.add(info);
							}
						}else if(alarmCode == 6){
							if(pwBusinessIdList.contains(info.getObjectId())){
								showList.add(info);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				UiUtil.closeService_MB(service);
				UiUtil.closeService_MB(lspService);
				UiUtil.closeService_MB(pwService);
				UiUtil.closeService_MB(portService);
			}
			if(showList.size() > 1){
				for (CurrentAlarmInfo info : showList) {
					info.putClientProperty();
					box.getAlarmModel().addAlarm(info);
				}
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
