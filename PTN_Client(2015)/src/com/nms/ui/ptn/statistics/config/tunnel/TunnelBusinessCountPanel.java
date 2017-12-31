package com.nms.ui.ptn.statistics.config.tunnel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.ui.frame.ContentView;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.business.pw.PwNetworkTablePanel;
import com.nms.ui.ptn.business.pw.PwQosQueuePanel;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.business.tunnel.LspNetworkTablePanel;
import com.nms.ui.ptn.business.tunnel.OamMainInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.Schematize;

/**
 */
public class TunnelBusinessCountPanel extends ContentView<Tunnel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8573672821821114967L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PwQosQueuePanel qosPanel;
	private LspNetworkTablePanel lspNetworkTablePanel;
	private JScrollPane oamScrollPane;
	private ViewDataTable<OamInfo> oamTable;
	private TopoPanel topoPanel;
//	private static TunnelBusinessPanel tunnelBusinessPanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private OamMainInfoPanel oamMainInfoPanel;//OAM关键信息
	private PwNetworkTablePanel pwNetworkTablePanel;
	public TunnelBusinessCountPanel() {
		super("tunnelBusinessTable",RootFactory.COUNTMODU);
		init();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_TUNNEL_COUNT)));
		initComponent();
		setLayout();
		setActionListention();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					//清除详细面板数据
					oamTable.clear();
					qosPanel.clear();
					oamMainInfoPanel.clear();
					topoPanel.clear();
					lspNetworkTablePanel.clear();
					schematize_panel.clear();
					pwNetworkTablePanel.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
	}

	private void initComponent() {
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		qosPanel = new PwQosQueuePanel();
		lspNetworkTablePanel=new LspNetworkTablePanel();
		oamTable = new ViewDataTable<OamInfo>("pwBusinessOAMTable");
		oamTable.getTableHeader().setResizingAllowed(true);
		oamTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		oamTable.setTableHeaderPopupMenuFactory(null);
		oamTable.setTableBodyPopupMenuFactory(null);
		oamScrollPane = new JScrollPane();
		oamScrollPane.setViewportView(oamTable);
		oamScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		oamScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		topoPanel = new TopoPanel();
		schematize_panel=new Schematize();
		oamMainInfoPanel = new OamMainInfoPanel();
		pwNetworkTablePanel = new PwNetworkTablePanel();
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), topoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_ROUTE_INFO), lspNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_INFO), oamScrollPane);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_MAIN_INFO), oamMainInfoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_PW_INFORMATION), this.pwNetworkTablePanel);
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

	@Override
	public void setController() {
		controller = new TunnelBusinessCountController(this);
	}

	public PwQosQueuePanel getQosPanel() {
		return qosPanel;
	}

	public ViewDataTable<OamInfo> getOamTable() {
		return oamTable;
	}

	public TopoPanel getTopoPanel() {
		return topoPanel;
	}

	public void setTopoPanel(TopoPanel topoPanel) {
		this.topoPanel = topoPanel;
	}
	
	public LspNetworkTablePanel getLspNetworkTablePanel() {
		return lspNetworkTablePanel;
	}

	public void setLspNetworkTablePanel(LspNetworkTablePanel lspNetworkTablePanel) {
		this.lspNetworkTablePanel = lspNetworkTablePanel;
	}
	
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public OamMainInfoPanel getOamMainInfoPanel() {
		return oamMainInfoPanel;
	}

	public PwNetworkTablePanel getPwNetworkTablePanel() {
		return pwNetworkTablePanel;
	}
}
