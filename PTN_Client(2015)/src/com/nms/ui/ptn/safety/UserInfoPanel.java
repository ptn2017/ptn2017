/*
 * Safety.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.safety;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.nms.db.bean.system.user.UserInst;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.controller.UserPanelController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 用户信息面板
 * @author __USER__
 */
public class UserInfoPanel extends ContentView<UserInst> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6986009368787506679L;

	public UserInfoPanel() {
		super("userTable",RootFactory.SATYMODU);
		init();
	}

	private void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_USER_LIST)));
		this.initComponents();
		setLayout();
		try {
			getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}

	private void initComponents() {
		
		tabbedPane = new JTabbedPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		int high = Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue() / 2;
		splitPane.setDividerLocation(high - 65);
		splitPane.setTopComponent(this.getContentPanel());
		splitPane.setBottomComponent(tabbedPane);
		userLockPanel=new UserLockPanel(this);
		
	}

	public void setTabbedPaneLayout() {
		tabbedPane.addTab(ResourceUtil.srcStr(StringKeysTab.TAB_USER_INFO), userLockPanel);
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
		controller = new UserPanelController(this);
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getSynchroButton());
		return needRemoveButtons;
	}
	
	/**
	 * 添加 锁定，解锁按钮
	 */
	public List<JButton> setAddButtons() {
		final JPanel contentPanel = new JPanel();

		clearButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.CLEAR_UPDATE),false,RootFactory.SATY_MANAGE);
		 lockButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.LOCK_UPDATE),false,RootFactory.SATY_MANAGE);
		clearButton.addActionListener(new MyActionListener() {
			@Override
			/*
			 * * 解锁事件
			 */
			public void actionPerformed(ActionEvent arg0) {
				UserPanelController userpanelcontroller = (UserPanelController) controller;
				if (getTable().getAllSelect().size() == 0) {
					DialogBoxUtil.errorDialog(contentPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_MORE));	
					return;
				} else {
					try {
						if(UiUtil.isNotAdmin()){
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));												
						}else{
						int result = DialogBoxUtil.confirmDialog(contentPanel, ResourceUtil.srcStr(StringKeysTip.TIP_IS_CLEAR));
						if (result == 0) {
							userpanelcontroller.clearUser();
						}
					 }
					} catch (Exception e) {

						ExceptionManage.dispose(e,this.getClass());
					}
				}
			}
		
			@Override
			public boolean checking() {
				
				return true;
			}
		});
		lockButton.addActionListener(new MyActionListener() {
			@Override
			/*
			 * * 锁定事件
			 */
			public void actionPerformed(ActionEvent arg0) {
				UserPanelController userpanelcontroller = (UserPanelController) controller;
				if (getTable().getAllSelect().size() == 0) {
					DialogBoxUtil.errorDialog(contentPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_MORE));
					return;
				} else {
					try {
						if(UiUtil.isNotAdmin()){
							DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
						}else{
							// 确实锁定吗
							int result = DialogBoxUtil.confirmDialog(contentPanel, ResourceUtil.srcStr(StringKeysTip.TIP_IS_LOCK));
							if (result == 0) {
								userpanelcontroller.lockUser();
							}
					    }
					} catch (Exception e) {
						ExceptionManage.dispose(e,this.getClass());
					}
				}
			}
			@Override
			public boolean checking() {
				return true;
			}
		});
		List<JButton> list = new ArrayList<JButton>();
		list.add(clearButton);
		list.add(lockButton);
		return list;
	}
	
	public PtnButton getClearButton() {
		return clearButton;
	}

	public void setClearButton(PtnButton clearButton) {
		this.clearButton = clearButton;
	}

	public PtnButton getLockButton() {
		return lockButton;
	}

	public void setLockButton(PtnButton lockButton) {
		this.lockButton = lockButton;
	}

	private PtnButton clearButton;
	private PtnButton lockButton;
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;
	private UserLockPanel userLockPanel;
}