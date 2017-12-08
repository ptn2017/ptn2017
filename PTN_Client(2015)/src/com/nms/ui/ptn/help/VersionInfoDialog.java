package com.nms.ui.ptn.help;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.xmlbean.LoginConfig;

public class VersionInfoDialog extends PtnDialog {
	private static final long serialVersionUID = 3411638262163068758L;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private JPanel buttonPanel;
	private JTable versionTable;

	public VersionInfoDialog() {
		init();
	}

	/**
	 * 初始化界面和数据
	 */
	public void init() {
		this.initComponents();
		this.setLayout();
		this.initTable();
		UiUtil.showWindow(this, 600, 300);
	}

	public void initComponents() {
		this.versionTable = new JTable();
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysMenu.TAB_VERSIONINFO)));
		contentScrollPane.setViewportView(versionTable);
		buttonPanel = new JPanel();
	}

	public void setLayout() {
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		contentLayout.columnWidths = new int[] {120 };	
		contentLayout.columnWeights = new double[] { 0, 0, 0, 0 };
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

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("serial")
	private void initTable() {
		versionTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] {ResourceUtil.srcStr(StringKeysLbl.LBL_NAME),ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_DESC),
				ResourceUtil.srcStr(StringKeysLbl.SOFTWARE_EDITION),ResourceUtil.srcStr(StringKeysLbl.COMPILE_TIME)}) {
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] { java.lang.Object.class,
					java.lang.Object.class, java.lang.Object.class,
					java.lang.Object.class };

			@Override
			@SuppressWarnings({ "unchecked"})
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		});

		versionTable.getTableHeader().setResizingAllowed(true);
		versionTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		TableColumn c = versionTable.getColumnModel().getColumn(1);
		c.setPreferredWidth(180);
		c = versionTable.getColumnModel().getColumn(2);
		c.setPreferredWidth(55);
		c.setMaxWidth(60);
		c.setMinWidth(30);
		c = versionTable.getColumnModel().getColumn(3);
		c.setPreferredWidth(80);
		c.setMaxWidth(90);
		c.setMinWidth(60);
		
		LoginUtil loginUtil=new LoginUtil();
		LoginConfig loginConfig = loginUtil.readLoginConfig();
		DefaultTableModel tableModel = (DefaultTableModel) versionTable.getModel();
		String v = "V2.1.6";//服务器版本:V2.1.6
		Object[] obj = new Object[] {"ServerVersion", ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL3_PTN).substring(0,
				(ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL3_PTN).length()-1)), v, "2017-10-15"};
		tableModel.addRow(obj);
		v = loginConfig.getVersion();//客户端版本:V2.1.6
		obj = new Object[] {"ClientVersion", ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL2_PTN).substring(0,
				(ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL2_PTN).length()-1)), v, "2017-10-11"};
		tableModel.addRow(obj);
		obj = new Object[] {"QueryServer", ResourceUtil.srcStr(StringKeysObj.OBJ_QUERYSERVER), "v0.2.1", "2017-01-25"};
		tableModel.addRow(obj);
		obj = new Object[] {"CfgServer", ResourceUtil.srcStr(StringKeysObj.OBJ_CFGSERVER), "v0.1.1", "2017-02-11"};
		tableModel.addRow(obj);
		obj = new Object[] {"NeCfgService", ResourceUtil.srcStr(StringKeysObj.OBJ_NECFGSERVICE), "v0.3.1", "2017-02-22"};
		tableModel.addRow(obj);
		obj = new Object[] {"MSMPServer", ResourceUtil.srcStr(StringKeysObj.OBJ_MSMPSERVER), "v0.3.1", "2017-04-27"};
		tableModel.addRow(obj);
		obj = new Object[] {"DtServer", ResourceUtil.srcStr(StringKeysObj.OBJ_DTSERVER), "v0.2.1", "2017-01-21"};
		tableModel.addRow(obj);
		obj = new Object[] {"DispServer", ResourceUtil.srcStr(StringKeysObj.OBJ_DISPSERVER), "v0.2.1", "2017-01-03"};
		tableModel.addRow(obj);
		versionTable.setModel(tableModel);
	}

}
