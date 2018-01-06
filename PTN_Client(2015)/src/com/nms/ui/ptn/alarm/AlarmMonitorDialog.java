package com.nms.ui.ptn.alarm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.alarm.WarningLevel;
import com.nms.db.enums.EObjectType;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;

public class AlarmMonitorDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1740678601593949447L;
	private JLabel label;// CPU温度阈值设置
	private JTextField tempValue;// 时间填写框
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
    
	public AlarmMonitorDialog() {
		init();
		initData();
		addListener();
	}


	private void init() {
		try {
			this.setTitle("硬件告警阈值设置");
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle("硬件告警阈值设置");  
			label = new JLabel("CPU温度阈值设置(c)");
			weight = 300;
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
			tempValue = new  JTextField();
			setCompentLayout();
			this.add(label);
			this.add(tempValue);
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
					cancel();
				}
			});

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void cancel() {
		this.dispose();
	}
	
	private void confirmSave() {
		ConstantUtil.cpuTemp = Integer.parseInt(this.tempValue.getText().trim());
		if(ConstantUtil.cpuTemp >= 75){
			addAlarm();
		}
		this.dispose();
	}
	
	private void addAlarm(){
		alarmPerformance(1076, "CPU温度过高告警", "CPU_TEMP", 233, false);
	}
	
	private void alarmPerformance(int codeValue,String alarmDesc,String warningName,int id, boolean isDelete){
		CurAlarmService_MB alarmServiceMB = null;
		try {
			alarmServiceMB = (CurAlarmService_MB)ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			CurrentAlarmInfo losAlarm = null;
			losAlarm = new CurrentAlarmInfo();
			losAlarm.setAlarmCode(codeValue);
			losAlarm.setAlarmDesc(alarmDesc);
			losAlarm.setAlarmLevel(2);
			losAlarm.setObjectName("EMS服务器_"+warningName);
			losAlarm.setObjectType(EObjectType.EMSCLIENT);
			losAlarm.setWarningLevel_temp(2);
			WarningLevel level = new WarningLevel();
			level.setId(id);
			level.setManufacturer(1);
			level.setWarningcode(codeValue);
			level.setWarningdescribe(alarmDesc);
			level.setWarninglevel(2);
			level.setWarninglevel_temp(2);
			level.setWarningname(warningName);
			level.setWarningnote(alarmDesc);
			losAlarm.setWarningLevel(level);
			losAlarm.setRaisedTime(new Date());
			List<CurrentAlarmInfo> existList = alarmServiceMB.select(losAlarm);
			System.out.println(existList);
			if(!isDelete){
				if(existList == null || existList.size() == 0){
					// 只产生一次，如果有，就不在放入数据库
					alarmServiceMB.saveOrUpdate(losAlarm);
				}
			}else{
				if(existList != null && existList.size() > 0){
					alarmServiceMB.delete(existList.get(0).getId());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(alarmServiceMB);
		}
	}
	
	private void initData() {
		this.tempValue.setText(ConstantUtil.cpuTemp+"");
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
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(label, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(tempValue, gridBagConstraints);
			
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
