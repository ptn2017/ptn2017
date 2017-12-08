package com.nms.ui.ptn.patchConfig;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnFileChooser;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.wait.WaitDialog;
import com.nms.ui.manager.xmlbean.LoginConfig;

public class PatchManagerDialog extends PtnDialog{
	private static final long serialVersionUID = 1L;
	private JLabel lblSelect = null; // 选择文件，目录 标签
	private JTextField txtSelect = null; // 选择文件，目录 文本框
	private PtnButton btnSelect = null; // 选择文件，目录 按钮
	private JButton btnSave = null;//确定按钮
	private JButton btnCancel = null;//取消按钮
	
	/**
	 * 创建一个新的实例
	 */
	public PatchManagerDialog(String title) {
		this.setTitle(title);
		this.initComponent();
		this.setLayout();
		this.addListener();
		UiUtil.showWindow(this, 500, 350);
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.lblSelect = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CATA_ROUTE));
		this.txtSelect = new JTextField();
		this.btnSelect = new PtnButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA), true, 1, this);
		this.txtSelect.setEditable(false);
		this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
		this.btnCancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
	}
	
	/**
	 * 设置布局
	 */
	public void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 100, 250, 100 };
		componentLayout.columnWeights = new double[] { 0.1, 0.1, 0.1 };
		componentLayout.rowHeights = new int[] { 50, 200, 50, 20 };
		componentLayout.rowWeights = new double[] { 0, 0, 0, 0.1 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();

		// 选择目录标签
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.lblSelect, c);
		this.add(this.lblSelect);
		// 选择目录文本框
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		componentLayout.setConstraints(this.txtSelect, c);
		this.add(this.txtSelect);
		// 选择目录按钮
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		componentLayout.setConstraints(this.btnSelect, c);
		this.add(this.btnSelect);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 15, 0, -150);
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		
		//取消按钮
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.insets = new Insets(0, 15, 0, 0);
		componentLayout.setConstraints(this.btnCancel, c);
		this.add(this.btnCancel);
	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		this.btnSave.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveAction();
				} catch (Exception e1) {
					ExceptionManage.dispose(e1,this.getClass());
				}
			}
			
			@Override
			public boolean checking() {
	            return true;
			}
		});		
		
		// 选择目录按钮事件
		this.btnSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new PtnFileChooser(PtnFileChooser.TYPE_FILE, txtSelect, null);
			}

		});
		
		// 取消按钮事件
		this.btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private void saveAction() {
		if(this.getTitle().equals(ResourceUtil.srcStr(StringKeysMenu.MENU_LOAD_PATCH))){
			//加载补丁
			this.updateVersion("V2.1.6");
		}else{
			//卸载补丁
			this.updateVersion("V2.1.5");
		}
	}
	
	private void updateVersion(String version) {
		if(this.txtSelect.getText() == null || this.txtSelect.getText().equals("")){
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_FILE));
			return;
		}
		LoginConfig loginConfig = new LoginConfig();
	    loginConfig.setVersion(version);
		LoginUtil loginUtil = new LoginUtil();
		loginUtil.writeLoginConfig(loginConfig);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_RESET_CLIENT));
		System.exit(0);	
	}
}
