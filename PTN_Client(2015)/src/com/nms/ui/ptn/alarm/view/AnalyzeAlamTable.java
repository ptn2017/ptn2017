package com.nms.ui.ptn.alarm.view;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import twaver.TDataBox;
import twaver.table.TAlarmTable;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.LspInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.ptn.alarm.AlarmTools;

public class AnalyzeAlamTable extends PtnDialog {
	private static final long serialVersionUID = -4046119221737730984L;
	private JTabbedPane tabPanel;
	private List<CurrentAlarmInfo> allList;
	Map<Integer, ArrayList<CurrentAlarmInfo>> portIdAlarmMap = new HashMap<Integer, ArrayList<CurrentAlarmInfo>>();
	
	public AnalyzeAlamTable(List<CurrentAlarmInfo> list) {
		this.allList = list;
		init();
	}

	public void init() {
		initData();
		initComponents();
		setLayout();
	}

	public void initComponents() {
		this.setTitle("告警定位");
		tabPanel = new JTabbedPane();
		int i = 1;
		for(int portId : portIdAlarmMap.keySet()){
			if(portIdAlarmMap.get(portId).size() > 1){
				AnalyzeAlarmPanel panel = new AnalyzeAlarmPanel(portIdAlarmMap.get(portId));
				tabPanel.add("告警定位"+(i++), panel);
			}
		}
	}

	public void setLayout() {
		GridBagLayout contentLayout = new GridBagLayout();
//		contentLayout.columnWidths = new int[] { 800};
//		contentLayout.columnWeights = new double[] { 1};
//		contentLayout.rowHeights = new int[] { 500 };
//		contentLayout.rowWeights = new double[] { 1};
		this.setLayout(contentLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(tabPanel, c);
		this.add(tabPanel);
	}

	private void initData() { 
		// 根据端口将告警归类
		LspInfoService_MB lspService = null;
		PwInfoService_MB pwService = null;
		PortService_MB portService = null;
		TunnelService_MB tunnelService = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			lspService = (LspInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LSPINFO);
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for(CurrentAlarmInfo alarm : this.allList){
				String wName = alarm.getWarningLevel().getWarningname();
				if("LRSOFFLINE".equals(wName) || "SFP_NOLIGH".equals(wName) || "LINK_LOS".equals(wName)
					|| "TMS_LOC".equals(wName) || "TMS_UNP".equals(wName) || "TMS_MMG".equals(wName) || "TMS_UNM".equals(wName)){
					PortInst con = new PortInst();
					con.setSiteId(alarm.getSiteId());
					con.setNumber(alarm.getObjectId());
					PortInst port = portService.select(con).get(0);
					if(portIdAlarmMap.containsKey(port.getPortId())){
						portIdAlarmMap.get(port.getPortId()).add(alarm);
					}else{
						ArrayList<CurrentAlarmInfo> alarmList = new ArrayList<CurrentAlarmInfo>();
						alarmList.add(alarm);
						portIdAlarmMap.put(port.getPortId(), alarmList);
					}
				}else if("TMP_LOC".equals(wName) || "TMP_UNP".equals(wName) || "TMP_MMG".equals(wName) || "TMP_UNM".equals(wName)){
					List<Lsp> lspList = lspService.selectBySiteId(alarm.getSiteId());
					int portId = 0;
					if(lspList != null){
						for(Lsp lsp : lspList){
							if(lsp.getASiteId() == alarm.getSiteId() && lsp.getAtunnelbusinessid() == alarm.getObjectId()){
								portId = lsp.getAPortId();
								break;
							}else if(lsp.getZSiteId() == alarm.getSiteId() && lsp.getZtunnelbusinessid() == alarm.getObjectId()){
								portId = lsp.getZPortId();
								break;
							}
						}
					}
					if(portId > 0){
						if(portIdAlarmMap.containsKey(portId)){
							portIdAlarmMap.get(portId).add(alarm);
						}else{
							ArrayList<CurrentAlarmInfo> alarmList = new ArrayList<CurrentAlarmInfo>();
							alarmList.add(alarm);
							portIdAlarmMap.put(portId, alarmList);
						}
					}
				}else if("TMC_LOC".equals(wName) || "TMC_UNP".equals(wName) || "TMC_MMG".equals(wName) || "TMC_UNM".equals(wName)){
					List<PwInfo> pwList = pwService.select(alarm.getSiteId());
					if(pwList != null){
						int portId = 0;
						int tunnelId = 0;
						for(PwInfo pw : pwList){
							if(pw.getASiteId() == alarm.getSiteId()){
								if(pw.getApwServiceId() == alarm.getObjectId()){
									tunnelId = pw.getTunnelId();
									break;
								}
							}else if(pw.getZSiteId() == alarm.getSiteId()){
								if(pw.getZpwServiceId() == alarm.getObjectId()){
									tunnelId = pw.getTunnelId();
									break;
								}
							}
						}
						Tunnel t = tunnelService.selectByID(tunnelId);
						if(t.getASiteId() == alarm.getSiteId()){
							portId = t.getaPortId();
						}else if(t.getZSiteId() == alarm.getSiteId()){
							portId = t.getzPortId();
						}
						if(portId > 0){
							if(portIdAlarmMap.containsKey(portId)){
								portIdAlarmMap.get(portId).add(alarm);
							}else{
								ArrayList<CurrentAlarmInfo> alarmList = new ArrayList<CurrentAlarmInfo>();
								alarmList.add(alarm);
								portIdAlarmMap.put(portId, alarmList);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			UiUtil.closeService_MB(lspService);
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(tunnelService);
		}
		
		for(int portId : portIdAlarmMap.keySet()){
			List<CurrentAlarmInfo> alarmList = portIdAlarmMap.get(portId);
			if(alarmList.size() > 0){
				boolean hasSFPAlarm = false;
				for(CurrentAlarmInfo alarm : alarmList){
					String wName = alarm.getWarningLevel().getWarningname();
					if("LRSOFFLINE".equals(wName) || "SFP_NOLIGH".equals(wName)){
						alarm.setAnalyze("根源告警");
						hasSFPAlarm = true;
					}else if("TMS_UNP".equals(wName) || "TMS_MMG".equals(wName) || "TMS_UNM".equals(wName) ||
							"TMP_UNP".equals(wName) || "TMP_MMG".equals(wName) || "TMP_UNM".equals(wName) ||
							"TMC_UNP".equals(wName) || "TMC_MMG".equals(wName) || "TMC_UNM".equals(wName)){
						alarm.setAnalyze("根源告警");
					}else if("TMS_LOC".equals(wName) || "TMP_LOC".equals(wName) || "TMC_LOC".equals(wName)){
						alarm.setAnalyze("衍生告警");
					}
				}
				for(CurrentAlarmInfo alarm : alarmList){
					String wName = alarm.getWarningLevel().getWarningname();
					if("LINK_LOS".equals(wName)){
						if(hasSFPAlarm){
							alarm.setAnalyze("衍生告警");
						}else{
							alarm.setAnalyze("根源告警");
						}
					}
				}
			}
		}
	}
	
	private class AnalyzeAlarmPanel extends JPanel{
		private static final long serialVersionUID = -4064914925681630532L;
		private JScrollPane contentPanel;// 根源告警
		private JScrollPane contentPanel1;// 衍生告警
		private TDataBox box = new TDataBox();
		private TAlarmTable alarmTable;
		private TDataBox box1 = new TDataBox();
		private TAlarmTable alarmTable1;
		private List<CurrentAlarmInfo> alarmList;
		
		public AnalyzeAlarmPanel(List<CurrentAlarmInfo> list) {
			this.alarmList = list;
			init();
		}

		public void init() {
			initComponents();
			initAlarm();
		}

		private void initAlarm() {
			if(alarmList.size() > 1){
				for (CurrentAlarmInfo info : alarmList) {
					info.putClientProperty();
					if(info.getAnalyze().contains("根源告警")){
						box.getAlarmModel().addAlarm(info);
					}else{
						box1.getAlarmModel().addAlarm(info);
					}
				}
			}
		}

		public void initComponents() {
			contentPanel = new JScrollPane();
			contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			contentPanel.setBorder(BorderFactory.createTitledBorder("根源告警"));
			contentPanel1 = new JScrollPane();
			contentPanel1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			contentPanel1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			contentPanel1.setBorder(BorderFactory.createTitledBorder("衍生告警"));
			AlarmTools alarmTools = new AlarmTools();
			alarmTable = new TAlarmTable(box, alarmTools.createColumns());
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenSize.height = 520;
			screenSize.width = 120;
			alarmTable.setPreferredScrollableViewportSize(screenSize);
			alarmTable.getTableHeader().setResizingAllowed(true);
			alarmTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			alarmTable.setDragEnabled(false);
			alarmTable.setTableHeaderPopupMenuFactory(null);
			alarmTable.setTableBodyPopupMenuFactory(null);
			alarmTable1 = new TAlarmTable(box1, alarmTools.createColumns());
			Dimension screenSize1 = Toolkit.getDefaultToolkit().getScreenSize();
			screenSize1.height = 520;
			screenSize1.width = 120;
			alarmTable1.setPreferredScrollableViewportSize(screenSize1);
			alarmTable1.getTableHeader().setResizingAllowed(true);
			alarmTable1.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			alarmTable1.setDragEnabled(false);
			alarmTable1.setTableHeaderPopupMenuFactory(null);
			alarmTable1.setTableBodyPopupMenuFactory(null);
			contentPanel.setViewportView(alarmTable);
			contentPanel1.setViewportView(alarmTable1);
			// 布局
			GridBagLayout contentLayout = new GridBagLayout();
			this.setLayout(contentLayout);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.weightx = 1.0;
			c.weighty = 0.4;
			c.insets = new Insets(0, 0, 2, 0);
			c.fill = GridBagConstraints.BOTH;
			contentLayout.setConstraints(contentPanel, c);
			this.add(contentPanel);
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.weightx = 1.0;
			c.weighty = 0.4;
			c.insets = new Insets(0, 0, 2, 0);
			contentLayout.setConstraints(contentPanel1, c);
			this.add(contentPanel1);
		}
	}
}
