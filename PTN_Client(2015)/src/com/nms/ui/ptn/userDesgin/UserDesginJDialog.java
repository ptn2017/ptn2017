package com.nms.ui.ptn.userDesgin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.system.UserDesginInfo;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.UserDesginService_Mb;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.ptn.clock.view.cx.time.TextFiledKeyListener;
import com.nms.ui.ptn.clock.view.cx.time.TextfieldFocusListener;

public class UserDesginJDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1740678601593949447L;
	private JCheckBox timeJcheckBox;// 时间单选按钮
	private JTextField time;// 时间填写框
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
    private JLabel  lblMessage;
    private UserDesginInfo userDesginfo = null;
//    private JCheckBox closeJcheckBox;// 时间单选按钮
//	private JTextField closeTField;// 时间填写框
    
	public UserDesginJDialog() {
		init();
		initDate();
		addListener();
	}


	private void init() {
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_USERDESIGN));  
			lblMessage = new JLabel();
			weight = 300;
			timeJcheckBox = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_LOCKSCRESS));
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
			time = new  JTextField();
			time.setEditable(false);
//			this.closeJcheckBox = new JCheckBox("网管自动注销时间(min)");
//			closeTField = new JTextField();
//			closeTField.setEditable(false);
			setCompentLayout();
			userDesginfo = new UserDesginInfo();
			this.add(lblMessage);
			this.add(timeJcheckBox);
			this.add(time);
			this.add(confirm);
			this.add(cancel);
//			this.add(closeJcheckBox);
//			this.add(closeTField);
			addKeyListenerForTextfield();
			addFocusListenerForTextfield();/*textfield聚焦事件监听，当离开此textfield判断值是否在指定范围内*/
			//this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void addListener() {
		// TODO Auto-generated method stub
		try {
			confirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					confirmSave();
				}
			});

			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					cancel();
				}
			});

			timeJcheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (timeJcheckBox.isSelected()) {
						time.setEditable(true);
					}else{
//						time.setText("1");
						time.setEditable(false);
					}
				}
			});

//			closeJcheckBox.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					if (closeJcheckBox.isSelected()) {
//						closeTField.setEditable(true);
//					}else{
////						time.setText("1");
//						closeTField.setEditable(false);
//					}
//				}
//			});
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
    //取消事件
	private void cancel() {
		this.dispose();
	}
	/**
	 * <p>
	 * textfield添加监听，只允许输入数字
	 * </p>
	 * @throws Exception
	 */
	private void addKeyListenerForTextfield()throws Exception{
		
		TextFiledKeyListener textFIledKeyListener=null;
		TextFiledKeyListener textFIledKeyListener1=null;
		try{
			/* 为1：只接受数字 **/
			textFIledKeyListener = new TextFiledKeyListener(1);
			this.time.addKeyListener(textFIledKeyListener);
			textFIledKeyListener1 = new TextFiledKeyListener(1);
//			this.closeTField.addKeyListener(textFIledKeyListener1);
		}catch(Exception e){
			throw e;
		}
	}
	/**
	 * <p>
	 * 判断输入数值是否在指定区间内
	 * </p>
	 * @throws Exception
	 */
	private void addFocusListenerForTextfield()throws Exception{
		
		TextfieldFocusListener textfieldFocusListener=null;
		TextfieldFocusListener textfieldFocusListener1=null;
		String whichTextTield=null;
		try{
			whichTextTield=ResourceUtil.srcStr(StringKeysLbl.LBL_LOCKSCRESS);
			textfieldFocusListener = new TextfieldFocusListener(whichTextTield,20,this.time);
			this.time.addFocusListener(textfieldFocusListener);
//			textfieldFocusListener1 = new TextfieldFocusListener(whichTextTield,20,this.closeTField);
//			this.closeTField.addFocusListener(textfieldFocusListener1);
			
		}catch(Exception e){
			
			throw e;
		}
	}
	
	
	private void confirmSave() {
		 UserDesginService_Mb serevices = null;
		try {
			serevices = (UserDesginService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.USERDESGINSERIVE);
			if(timeJcheckBox.isSelected()){
				userDesginfo.setIsSelect(1);
			}else{
				userDesginfo.setIsSelect(0);
			}
			userDesginfo.setMinute(time.getText().trim());
//			if(closeJcheckBox.isSelected()){
//				userDesginfo.setCloseSelect(1);
//			}else{
//				userDesginfo.setCloseSelect(0);
//			}
//			userDesginfo.setCloseTime(closeTField.getText().trim());
			userDesginfo.setUserName(ConstantUtil.user.getUser_Name());
			serevices.save(userDesginfo);
			this.insertOpeLog(EOperationLogType.SELFMANAGE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(serevices);
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_USERDESIGN),"");		
	}
/**
 *初始化界面
 */
	private void initDate() {
		UserDesginService_Mb serevices = null;
		try {
			serevices = (UserDesginService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.USERDESGINSERIVE);
			userDesginfo = serevices.select(ConstantUtil.user.getUser_Name());
			if(userDesginfo.getIsSelect()==1){
				timeJcheckBox.setSelected(true);
				time.setEditable(true);
			}
			time.setText(userDesginfo.getMinute());
//			if(userDesginfo.getCloseSelect() == 1){
//				closeJcheckBox.setSelected(true);
//				closeTField.setEditable(true);
//			}
//			closeTField.setText(userDesginfo.getCloseTime());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(serevices);
		}
	}

	private void setCompentLayout() {
		try {
			gridBagLayout.columnWidths = new int[] {70,10};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] {20,20,20,30};
			gridBagLayout.rowWeights = new double[] { 0, 0, 0, 0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(5, 5, 5, 20);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(lblMessage, gridBagConstraints);
			
//			gridBagConstraints.fill = GridBagConstraints.NONE;
//			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints.gridx = 0;
//			gridBagConstraints.gridy = 1;
//			gridBagLayout.setConstraints(closeJcheckBox, gridBagConstraints);
//			
//			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints.gridx = 1;
//			gridBagConstraints.gridy = 1;
//			gridBagLayout.setConstraints(closeTField, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(timeJcheckBox, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(time, gridBagConstraints);
			
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
