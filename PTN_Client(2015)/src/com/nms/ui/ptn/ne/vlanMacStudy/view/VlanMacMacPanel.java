package com.nms.ui.ptn.ne.vlanMacStudy.view;

import java.awt.Dimension;
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

import com.nms.db.bean.ptn.SsMacStudy;
import com.nms.db.bean.ptn.VlanMacStudyInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.ne.vlanMacStudy.controller.VlanMcStudyController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class VlanMacMacPanel extends ContentView<VlanMacStudyInfo> {

	private static final long serialVersionUID = -5638118410017167790L;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private VlanMacStudyInfoPanel ssMacStudyInfo;
	
	public VlanMacMacPanel() {
		super("vplsmacAddress", RootFactory.CORE_MANAGE);		
		try {
			this.init();
			super.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder("VALN MAC"));
		initComponent();
		setLayout();
		this.setActionListention();
	}
	/**
	 * 添加监听
	 */
	private void setActionListention() {
		getTable().addElementClickedActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelect() == null) {
					ssMacStudyInfo.clear();
					return;
				} else {
					getController().initDetailInfo();
				}
			}
		});
	}
	

	private void initComponent(){
		
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		ssMacStudyInfo=new VlanMacStudyInfoPanel();
	}

	private void setLayout() {
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
	
	private void setTabbedPaneLayout() {
		tabbedPane.add(ResourceUtil.srcStr(StringKeysTab.TAB_BASIC_INFO), this.ssMacStudyInfo);
	}
	

	
	@Override
	public void setController() {
		this.controller = new VlanMcStudyController(this);
	}
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}
	
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
//		needRemoveButtons.add(getSynchroButton());
		return needRemoveButtons;
	}
	
	public VlanMacStudyInfoPanel getssMacStudyInfo() {
		return ssMacStudyInfo;
	}

}
