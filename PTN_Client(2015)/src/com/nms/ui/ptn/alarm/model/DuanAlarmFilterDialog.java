package com.nms.ui.ptn.alarm.model;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnCalendarField;
import com.nms.ui.manager.control.PtnDateDocument;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.ptn.alarm.controller.DuanAlarmController;
/**
 * 查询操作日志 过滤对话框
 * @author sy
 *
 */
public class DuanAlarmFilterDialog extends PtnDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PtnButton confirm;  //确认按钮
	private JButton cancel;   //取消按钮
	private JButton clear;    //清除按钮
	private JLabel operationTypeLabel; //操作类型
	private JComboBox operationResultBox;
	private JPanel buttonConfirCanel;
	private JPanel claerJpanel;
	private JPanel buttonPanel;
	private DuanAlarmController duanAlarmController;
	/**
	 * 实例化对象
	 */
	public DuanAlarmFilterDialog(DuanAlarmController duanAlarmController) {
		this.duanAlarmController = duanAlarmController;
		this.setModal(true);
		init();
	}
	//初始化
	public void init() {
		initComponents();
		setLayout();
		addListener();
	}
	
	public PtnButton getConfirm() {
		return confirm;
	}
	//时间处理
	private void addListener() {
		
		//取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DuanAlarmFilterDialog.this.dispose();
			}
		});
		
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirm();
			}
		});
	}
	
	private void confirm(){
		Integer type = operationResultBox.getSelectedIndex()+1;
		duanAlarmController.filter(type);
		this.dispose();
	}
	private void initComponents() {
		buttonConfirCanel=new JPanel();
		claerJpanel=new JPanel();
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		this.operationTypeLabel=new JLabel(ResourceUtil.srcStr(StringKeysOperaType.BTN_OPERATIONTYPE));
		this.operationResultBox=new JComboBox();
		operationResultBox.addItem("tunnel");
		operationResultBox.addItem("pw");
		operationResultBox.addItem("eth and tdm");
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		buttonPanel=new JPanel();
	}
	//按钮布局
	private void setButtonLayout() {
		GridBagLayout layout = new GridBagLayout();
	layout.columnWidths = new int[] {  245,70,100 };
		layout.columnWeights = new double[] { 0.2, 0, 0};
		layout.rowHeights = new int[] { 10};
		layout.rowWeights = new double[] {0.1};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		//第一行
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(confirm, c);
		this.buttonPanel.add(confirm);
		c.gridx =2;
		layout.addLayoutComponent(this.cancel, c);
		this.buttonPanel.add(cancel);
	}
	//页面布局
	private void setLayout() {
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 60, 170, 15, 170 };
		layout.columnWeights = new double[] { 0, 0.1, 0, 0.1 };
		layout.rowHeights = new int[] { 10, 20, 20, 20, 20, 20, 20 ,20};
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		// 第一行
//		c.fill = GridBagConstraints.BOTH;
//		c.gridx = 0;
//		c.gridy = 1;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 10);
//		layout.addLayoutComponent(userType, c);
//		this.add(userType);
//		c.gridx =1;
//		c.gridwidth = 2;
//		layout.addLayoutComponent(userField, c);
//		this.add(userField);
//		
//		c.gridx=3;
//		c.gridwidth = 1;
//		layout.addLayoutComponent(this.chbLikeSelect, c);
//		this.add(chbLikeSelect);
//		
//		// 第二行
//		c.fill = GridBagConstraints.BOTH;
//		c.gridx = 0;
//		c.gridy = 2;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 10);
//		layout.addLayoutComponent(siteField, c);
//		this.add(siteField);
//		c.gridx =1;
//		c.gridwidth = 2;
//		layout.addLayoutComponent(this.siteCombox, c);
//		this.add(siteCombox);
				
		
		//第三行
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(this.operationTypeLabel, c);
		this.add(operationTypeLabel);
		
		c.gridx = 1;
		c.gridy =1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(this.operationResultBox, c);
		this.add(operationResultBox);
		
		//7
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(buttonPanel , c);
		this.add(buttonPanel );
	}


	public JButton getCancel() {
		return cancel;
	}

	public JButton getClear() {
		return clear;
	}

	public JLabel getOperationTypeLabel() {
		return operationTypeLabel;
	}
	public JComboBox getOperationResultBox() {
		return operationResultBox;
	}


	public JPanel getButtonConfirCanel() {
		return buttonConfirCanel;
	}

	public JPanel getClaerJpanel() {
		return claerJpanel;
	}

}
