package com.nms.ui.ptn.business.pw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;

public class PwProtectDialog extends PtnDialog {
	private static final long serialVersionUID = -1740678601593949447L;
	private JLabel lblwaitTime;
	private JLabel lbldelayTime;
	private PtnSpinner txtWaitTime;
	private PtnSpinner txtDelayTime;
	private JCheckBox chkAps;
	private JCheckBox protectBack;
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
	private PwInfo pw = null;
	private AbstractController controller;
    
	public PwProtectDialog(PwInfo pwInfo, AbstractController con) {
		this.controller = con;
		this.pw = pwInfo;
		init();
		initData();
		addListener();
		UiUtil.showWindow(this, this.getWeight(), 250);
	}

	private void init() {
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle("PW倒换参数配置");  
			weight = 300;
			this.lblwaitTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_WAIT_TIME));
			this.lbldelayTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
			this.txtDelayTime = new PtnSpinner(2500, 0, 100, ResourceUtil.srcStr(StringKeysLbl.LBL_DELAY_TIME2));
			this.txtWaitTime = new PtnSpinner(PtnSpinner.TYPE_WAITTIME);
			this.chkAps = new JCheckBox(
					ResourceUtil.srcStr(StringKeysLbl.LBL_APS_ENABLE));
			this.protectBack = new JCheckBox(
					ResourceUtil.srcStr(StringKeysLbl.LBL_BACK));
			this.txtWaitTime.getTxt().setText("5");
			this.txtDelayTime.getTxt().setText("0");

			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
			setCompentLayout();
			this.add(lblwaitTime);
			this.add(lbldelayTime);
			this.add(txtDelayTime);
			this.add(txtWaitTime);
			this.add(chkAps);
			this.add(protectBack);
			this.add(confirm);
			this.add(cancel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void addListener() {
		try {
			confirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					confirmSave();
				}
			});

			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void confirmSave() {
		PwInfoService_MB service = null;
		try{
			String delayTime = this.txtDelayTime.getTxtData();
			String waitTime = this.txtWaitTime.getTxtData();
			String aps = this.chkAps.isSelected() ? "1":"0";
			String back = this.protectBack.isSelected() ? "1":"0";
			StringBuffer sb = new StringBuffer();
			sb.append(delayTime).append("@").append(waitTime).append("@").append(aps).append("@").append(back);
			this.pw.setDirection("主用"+"@"+sb.toString());
			service = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			service.update(this.pw);
			this.controller.refresh();
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(service);
		}
	}

	private void initData() {
		String cotent = this.pw.getDirection();
		if(cotent != null && cotent.contains("@")){
			String[] arr = cotent.split("@");
			try {
				this.txtDelayTime.setTxtData1(arr[1]);
				this.txtWaitTime.setTxtData1(arr[2]);
				if(Integer.parseInt(arr[3]) == 1){
					this.chkAps.setSelected(true);
				}else{
					this.chkAps.setSelected(false);
				}
				if(Integer.parseInt(arr[4]) == 1){
					this.protectBack.setSelected(true);
				}else{
					this.protectBack.setSelected(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCompentLayout() {
		try {
			gridBagLayout.columnWidths = new int[] {70,10};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] {20,20,20,20,20,30};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(this.lbldelayTime, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(this.txtDelayTime, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(this.lblwaitTime, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(this.txtWaitTime, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(this.chkAps, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(this.protectBack, gridBagConstraints);
			
			
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.insets = new Insets(20, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);

			gridBagConstraints.insets = new Insets(20, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(cancel, gridBagConstraints);
			this.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
