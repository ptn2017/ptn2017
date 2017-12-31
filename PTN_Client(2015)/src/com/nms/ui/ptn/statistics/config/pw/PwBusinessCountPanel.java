package com.nms.ui.ptn.statistics.config.pw;

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
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.business.pw.BusinessNetworkTablePanel;
import com.nms.ui.ptn.business.pw.PwQosQueuePanel;
import com.nms.ui.ptn.business.pw.PwXcTablePanel;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.business.tunnel.LspNetworkTablePanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.Schematize;
/**
 */
public class PwBusinessCountPanel extends ContentView<PwInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5464726456543388260L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private PwQosQueuePanel qosPanel;
	private JScrollPane oamScrollPane;
	private ViewDataTable<OamInfo> oamTable;
	private TopoPanel topoPanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private PwXcTablePanel pwXcTablePanel; 	
	private LspNetworkTablePanel lspNetworkTablePanel;//lsp信息
	private BusinessNetworkTablePanel businessNetworkTablePanel;
	public PwBusinessCountPanel() {
		super("pwBusinessTable",RootFactory.COUNTMODU);
		init();
		//pwBusinessPanel = this;
	}

	/*public static PwBusinessPanel getPwBusinessPanel() {
		return pwBusinessPanel;
	}*/

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_PW_COUNT)));
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
					// 清除详细面板数据
					oamTable.clear();
					qosPanel.clear();
					topoPanel.clear();
					lspNetworkTablePanel.clear();
					schematize_panel.clear();
					businessNetworkTablePanel.clear();
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
		lspNetworkTablePanel=new LspNetworkTablePanel();
		businessNetworkTablePanel = new BusinessNetworkTablePanel(); 
		qosPanel = new PwQosQueuePanel();
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
		pwXcTablePanel = new PwXcTablePanel();
	}

	public void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_TOPO_SHOW), topoPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_ROUTE_INFO), lspNetworkTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_QOS_INFO), qosPanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_OAM_INFO), oamScrollPane);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_PW_XC), pwXcTablePanel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_SCHEMATIZE), this.schematize_panel);
		tabbedPane.add(ResourceUtil.srcStr(StringKeysPanel.PANEL_BUSINESS_INFORMATION), this.businessNetworkTablePanel);
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
		controller = new PwBusinessCountController(this);
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
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public PwXcTablePanel getPwXcTablePanel() {
		return pwXcTablePanel;
	}

	public void setPwXcTablePanel(PwXcTablePanel pwXcTablePanel) {
		this.pwXcTablePanel = pwXcTablePanel;
	}

	public LspNetworkTablePanel getLspNetworkTablePanel() {
		return lspNetworkTablePanel;
	}
	
	public BusinessNetworkTablePanel getBusinessNetworkTablePanel() {
		return businessNetworkTablePanel;
	}
}
