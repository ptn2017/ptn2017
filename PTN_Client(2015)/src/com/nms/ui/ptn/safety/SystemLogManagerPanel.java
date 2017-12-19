package com.nms.ui.ptn.safety;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.SystemLog;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.safety.controller.LogManagerController;
import com.nms.ui.ptn.safety.controller.OperationLogPanelController;
import com.nms.ui.ptn.safety.controller.SystemLogController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 转储管理
 * 主界面
 * 面板
 * @author sy
 *
 */
public class SystemLogManagerPanel extends ContentView<SystemLog> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;


	public SystemLogManagerPanel() {
		super("systemLogTable",RootFactory.SYSTEM_MANAGE);
		init();		
	}
	public void init(){	
		try{			
			this.setViewLayout();
			setLayout();
			this.getController().refresh();
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}			
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
		panelLayout.setConstraints(getContentPanel(), c);
		this.add(getContentPanel());
	}
	@Override
	public void setController() {
		controller = new SystemLogController(this);	
	}
	private void setViewLayout(){		
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_OPERATION_MANAGERS)));
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
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getFiterZero());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(this.getInportButton());
		return needRemoveButtons;
	}
	
	@Override
	public List<JButton> setAddButtons() {
		//添加移除 按钮
		final PtnButton removeButton=new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE_DATA),false,RootFactory.SATY_MANAGE);
		removeButton.addActionListener(new MyActionListener(){

			@Override
			public boolean checking() {				
				return true;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				//y移除事件，弹出时间选择框
				((OperationLogPanelController) controller).removeAction();
			}					
		});
		List<JButton> list=new ArrayList<JButton>();
		list.add(removeButton);
		return list;
	}

}