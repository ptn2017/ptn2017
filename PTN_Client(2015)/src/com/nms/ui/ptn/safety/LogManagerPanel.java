package com.nms.ui.ptn.safety;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import com.nms.db.bean.system.LogManager;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.safety.controller.LogManagerController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 转储管理
 * 主界面
 * 面板
 * @author sy
 *
 */
public class LogManagerPanel extends ContentView<LogManager> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;


	public LogManagerPanel() {
		super("logManagerTable",RootFactory.SATYMODU);
		init();		
	}
	public void init(){	
		try{			
		this.initComponents();
			this.setViewLayout();
			
			setLayout();
			this.getController().refresh();
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}			
	}
	private void initComponents() {
		this.getInportButton().setText(ResourceUtil.srcStr(StringKeysOperaType.BTN_UNLOAD_INPORT));
		this.getExportButton().setText(ResourceUtil.srcStr(StringKeysOperaType.BTN_UNLOAD_EXPORT));
		super.checkRoot(this.getExportButton(), RootFactory.SATYMODU);
		super.checkRoot(this.getUpdateButton(), RootFactory.SATYMODU);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(this.getContentPanel());
	
	}
	public void setLayout() {
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
		controller = new LogManagerController(this);	
	}
	private void setViewLayout(){		
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_OPERATION_MANAGERS)));
//		this.add(getContentPanel());
	}	
	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}


	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}		
	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(this.getFilterButton());
		needRemoveButtons.add(this.getClearFilterButton());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(this.getFiterZero());
		needRemoveButtons.add(getSearchButton());
		return needRemoveButtons;
	}

	

}