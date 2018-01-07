package com.nms.ui.ptn.alarm.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.nms.db.bean.alarm.DuanAlarmInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysPanel;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.controller.DuanAlarmController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

import twaver.table.TAlarmTable;
import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;


/**
 * 当前OAM 事件
 * 
 * @author
 * 
 */
public class DuanAlarmPanel extends ContentView<DuanAlarmInfo> {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane; // 选项卡控件
	private JSplitPane splitPane; // 上下分割控件
	private DuanAlarmPanel Panel = null; // OamEvent信息panel
	private TAlarmTable alarmTable;

	/**
	 * 创建一个新实例
	 */
	public DuanAlarmPanel() {	
		super("duanAlarm",RootFactory.CORE_MANAGE);
		try {
			this.initComponent();
			this.setLayout();
			this.addlistenter();
			//刷新列表
			this.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	

	private void addlistenter() {
		// TODO Auto-generated method stub
		mipositioning.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<DuanAlarmInfo> list = getAllSelect();
				String result = "无结果";
				StringBuffer stringBuffer = new StringBuffer();
				for (DuanAlarmInfo duanAlarmInfo : list) {
					stringBuffer.append(duanAlarmInfo.getWarningName()+",");
				}
				String str = stringBuffer.toString();
				
				if(str.contains("LINK_LOS")){
					if(str.contains("LRSOFFLINE")){
						result = "LRSOFFLINE";
					}else if(str.contains("SFP_NOLIGH")){
						result = "SFP_NOLIGH";
					}else if(str.contains("SFP_FAULT")){
						result = "SFP_FAULT";
					}
				}else if(str.contains("ETH_LOC")){
					if(str.contains("ETH_MMG")){
						result = "ETH_MMG";
					}else if(str.contains("ETH_UNM")){
						result = "ETH_UNM";
					}else if(str.contains("ETH_UNP")){
						result = "ETH_UNP";
					}
				}else if(str.contains("TMS_LOC")){
					if(str.contains("TMS_MMG")){
						result = "TMS_MMG";
					}else if(str.contains("TMS_UNM")){
						result = "TMS_UNM";
					}else if(str.contains("TMS_UNP")){
						result = "TMS_UNP";
					}
				}else if(str.contains("TMP_LOC")){
					if(str.contains("TMP_MMG")){
						result = "TMP_MMG";
					}else if(str.contains("TMP_UNM")){
						result = "TMP_UNM";
					}else if(str.contains("TMP_UNP")){
						result = "TMP_UNP";
					}
				}else if(str.contains("TMC_LOC")){
					if(str.contains("TMC_MMG")){
						result = "TMC_MMG";
					}else if(str.contains("TMC_UNM")){
						result = "TMC_UNM";
					}else if(str.contains("TMC_UNP")){
						result = "TMC_UNP";
					}
				}
				DialogBoxUtil.succeedDialog(null, result);
			}
		});
	}


	/**
	 * 初始化控件
	 */
	private void initComponent() {
		super.getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysPanel.PANEL_OAM_EVENT)));
//		this.tabbedPane = new JTabbedPane();
		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		this.splitPane.setDividerLocation(high);
		this.splitPane.setTopComponent(super.getContentPanel());
	//	this.splitPane.setBottomComponent(tabbedPane);

	//	this.Panel = new SiteOamEventPanel();
//		this.tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_BASIC_INFO), this.Panel);
	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
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
		panelLayout.setConstraints(this.splitPane, c);
		this.add(this.splitPane);
	}

	/**
	 * 移除按钮
	 */
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getExportButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getInportButton());
		needRemoveButtons.add(getFiterZero());
		needRemoveButtons.add(getClearFilterButton());
		return needRemoveButtons;
	}

	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable ttable, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(mipositioning);
					checkRoot(mipositioning, RootFactory.CORE_MANAGE);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
					return menu;
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}
	
	/**
	 * 添加按钮
	 */
	@Override
	public List<JButton> setAddButtons() {

		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		return needRemoveButtons;

	}

	/**
	 * 设置倒换按钮
	 * 
	 * @return
	 */
	private JButton getRotateButton() {
		JButton jButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ROTATE),RootFactory.CORE_MANAGE);

		// 倒换按钮事件
		jButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
//				mspProtectRotate();
			}
		});

		return jButton;
	}
	/**
	 * 倒换
	 */
//	private void mspProtectRotate() {
//		//倒换只能选择一条数据
//		if (this.getAllSelect().size() != 1) {
//			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_ONE));
//			return;
//		}
//		MspProtect mspProtect = this.getSelect();
////		new MspProtectRotateDialog(mspProtect,this);
//	}
	/**
	 * 设置大小
	 */
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}

	/**
	 * 给此界面添加控制类
	 */
	@Override
	public void setController() {
		super.controller = new DuanAlarmController(this);
	}
	
	private JMenuItem mipositioning;
}
