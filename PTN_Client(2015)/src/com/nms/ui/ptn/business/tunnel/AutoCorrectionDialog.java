package com.nms.ui.ptn.business.tunnel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;

public class AutoCorrectionDialog extends PtnDialog{
	private static final long serialVersionUID = -9125315668377808981L;
	private JLabel autoCorrectionLbl;
	private JComboBox autoComboBox;
	private JLabel dataBaseLbl;
	private JComboBox dataBaseComboBox;
	private JButton confirmButton;
	private JButton cancelButton;
	
	public AutoCorrectionDialog(){
		init();
	}

	private void init() {
		try{
			this.initComponents();
			this.setLayout();
			this.addActionListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}


	private void addActionListener() {
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirm();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
	}

	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 50, 150, 50 };
		componentLayout.columnWeights = new double[] { 0, 0, 0,0};
		componentLayout.rowHeights = new int[] {40,40,40,40,40,40};
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.4};
		this.setLayout(componentLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(40, 5, 15, 5);
		componentLayout.setConstraints(this.autoCorrectionLbl, c);
		this.add(this.autoCorrectionLbl);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.autoComboBox, c);
		this.add(this.autoComboBox);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 15, 5);
		componentLayout.setConstraints(this.dataBaseLbl, c);
		this.add(this.dataBaseLbl);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.dataBaseComboBox, c);
		this.add(this.dataBaseComboBox);
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 20, 10);
		componentLayout.setConstraints(this.confirmButton, c);
		this.add(this.confirmButton);
		c.gridx = 2;
		c.insets = new Insets(5, 5, 20, 10);
		componentLayout.setConstraints(this.cancelButton, c);
		this.add(this.cancelButton);
		
		this.setLayout(componentLayout);
	}

	private void initComponents() {
	    autoCorrectionLbl = new JLabel(ResourceUtil.srcStr(StringKeysMenu.MENU_AUTO_CORRECTION));
		autoComboBox = new JComboBox();
		autoComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_CORRECTION_USER));
		autoComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_CORRECTION_AUTO));
		dataBaseLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CORRECTION_RULE));
		dataBaseComboBox = new JComboBox();
		dataBaseComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_DATA_BASE_NMS));
		dataBaseComboBox.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_DATA_BASE_NE));
		confirmButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		cancelButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
	}
	
	private void confirm() {
		try {
			if(autoComboBox.getSelectedIndex() == 1){
				if(dataBaseComboBox.getSelectedIndex() == 0){
					// 以网管数据为准
					ConstantUtil.autoCorrection = 1;
				}else{
					// 以设备数据为准
					ConstantUtil.autoCorrection = 2;
				}
			}else{
				ConstantUtil.autoCorrection = 0;
			}
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, AutoCorrectionDialog.class);
		}
	}
	
	private void cancel() {
		this.dispose();
	}
}
