package com.nms.ui.ptn.statistics.config.etree;

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

import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.business.eline.PortNetworkTablePanel;
import com.nms.ui.ptn.business.pw.PwNetworkTablePanel;
import com.nms.ui.ptn.business.topo.TopoPanel;
import com.nms.ui.ptn.client.view.ClientInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.Schematize;

/**
 */
public class EtreeBusinessCountPanel extends ContentView<EtreeInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2815895318506766443L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private TopoPanel topoPanel;
	private PortNetworkTablePanel portNetworkTablePanel;
	private Schematize schematize_panel = null;	//图形化显示界面
	private ClientInfoPanel clientInfoPanel;
	private PwNetworkTablePanel pwNetworkTablePanel;
	public EtreeBusinessCountPanel() {
		super("etreeBusinessTable",RootFactory.COUNTMODU);
		try {
		init();
			super.getController().refresh();	//刷新列表
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_ETREE_COUNT)));
		initComponent();
		setLayout();
		setActionListention();
	}
	/**
	 * 添加事件监听
	 */
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					// 清除详细面板数据
					topoPanel.clear();
					portNetworkTablePanel.clear();
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
		portNetworkTablePanel=new PortNetworkTablePanel();
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
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_AC_INFO), portNetworkTablePanel);
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
		controller = new EtreeBusinessCountController(this);
	}

	public TopoPanel getTopoPanel() {
		return topoPanel;
	}

	public void setTopoPanel(TopoPanel topoPanel) {
		this.topoPanel = topoPanel;
	}
	public PortNetworkTablePanel getPortNetworkTablePanel() {
		return portNetworkTablePanel;
	}
	public Schematize getSchematize_panel() {
		return schematize_panel;
	}

	public ClientInfoPanel getClientInfoPanel() {
		return clientInfoPanel;
	}

	public PwNetworkTablePanel getPwNetworkTablePanel() {
		return pwNetworkTablePanel;
	}
}
