package com.nms.ui.ptn.safety.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.ptn.safety.controller.OperationLogPanelController;



/**
 * 日志导出设置
 */

public class LogExportDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel fileTypeLbl;// 保存文件类型
	private JComboBox fileTypeCmb;
	private JButton confirm = null;
	private JButton cancel = null;
	private OperationLogPanelController controller;
	
	public LogExportDialog() {
		init();
		UiUtil.showWindow(this, 400, 400);
	}

	public LogExportDialog(OperationLogPanelController operationLogPanelController) {
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.controller = operationLogPanelController;
		init();
		UiUtil.showWindow(this, 400, 400);
	}

	private void init() {
		try {
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_SAVE_FILE_TYPE));
			this.fileTypeLbl = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SAVE_FILE_TYPE));
			this.fileTypeCmb = new JComboBox();
			this.confirm = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			this.cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.initBorderLayout();
			this.initData();
			this.addButtonListener(); 
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void initData() {
		this.fileTypeCmb.addItem("Excel");
		this.fileTypeCmb.addItem("txt");
	}

	private void addButtonListener() {
		try {
			this.confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					btnSaveConfirm();
				}
			});
			
			this.cancel.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
				   cancel();
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 取消事件
	 * 
	 */
	private void cancel() {
		this.dispose();
	}

	private void btnSaveConfirm() {
		try {
			this.controller.setFileType((String) this.fileTypeCmb.getSelectedItem());
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void initBorderLayout() {
		GridBagLayout gridBagLayouts = null;
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagLayouts = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();

			this.setLayout(gridBagLayouts);
			
			gridBagLayouts.columnWidths = new int[] { 50, 200, 50 };
			gridBagLayouts.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayouts.rowHeights = new int[] { 40, 40, 40, 40};
			gridBagLayouts.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.2 };
			
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.insets = new Insets(20, 5, 15, 5);
			gridBagLayouts.setConstraints(this.fileTypeLbl, gridBagConstraints);
			this.add(fileTypeLbl);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagLayouts.setConstraints(this.fileTypeCmb, gridBagConstraints);
			this.add(this.fileTypeCmb);
			
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.insets = new Insets(5, 5, 0, 10);
			gridBagLayouts.setConstraints(confirm, gridBagConstraints);
			this.add(this.confirm);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.insets = new Insets(5, 5, 0, 10);
			gridBagLayouts.setConstraints(this.cancel, gridBagConstraints);
			this.add(this.cancel);

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
}
