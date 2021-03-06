﻿package com.nms.ui.ptn.statistics.config.ces;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.business.ces.CesPortNetworkTablePanel;
import com.nms.ui.ptn.business.dialog.cespath.AddCesDialog;
import com.nms.ui.ptn.business.pw.PwNetworkTablePanel;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.client.view.ClientInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.Schematize;

/**
 */
public class CesBusinessCountPanel extends ContentView<CesInfo> {
	private static final long serialVersionUID = -5153949231603674993L;

	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private TopoPanel topoPanel;
	private CesPortNetworkTablePanel cesPortNetworkTablePanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private ClientInfoPanel clientInfoPanel;
	private AddCesDialog dialog;
	private PwNetworkTablePanel pwNetworkTablePanel;
	
	public CesBusinessCountPanel() {
		super("cesBusinessTable",RootFactory.COUNTMODU);
		init();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_CES_COUNT)));
		initComponent();
		setLayout();
		setActionListention();
	}

	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					//清除详细面板数据
					topoPanel.clear();
					cesPortNetworkTablePanel.clear();
					schematize_panel.clear();
					clientInfoPanel.clear();
					pwNetworkTablePanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getFiterZero());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getExportButton());
		return needRemoveButtons;
	}
	
	private void initComponent() {
		cesPortNetworkTablePanel=new CesPortNetworkTablePanel();
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		topoPanel = new TopoPanel();
		schematize_panel=new Schematize();
		clientInfoPanel = new ClientInfoPanel();
		pwNetworkTablePanel = new PwNetworkTablePanel();
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), topoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PWSTATUS), pwNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_PORT_INFO), cesPortNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_CLIENTINFO), this.clientInfoPanel);
	}

	public void setLayout() {
		setTabbedPaneLayout();
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
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
	
	@Override
	public void setController() {
		controller = new CesBusinessCountController(this);
	}

	public TopoPanel getTopoPanel() {
		return topoPanel;
	}

	public void setTopoPanel(TopoPanel topoPanel) {
		this.topoPanel = topoPanel;
	}

	public CesPortNetworkTablePanel getCesPortNetworkTablePanel() {
		return cesPortNetworkTablePanel;
	}
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public ClientInfoPanel getClientInfoPanel() {
		return clientInfoPanel;
	}public AddCesDialog getDialog() {
		return dialog;
	}

	public void setDialog(AddCesDialog dialog) {
		this.dialog = dialog;
	}

	public PwNetworkTablePanel getPwNetworkTablePanel() {
		return pwNetworkTablePanel;
	}
}
