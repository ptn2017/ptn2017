package com.nms.ui.ptn.systemManage.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.AutoDatabaseBackAllThread;
import com.nms.rmi.ui.AutoDatabaseTimeDeleteThread;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;
import com.nms.ui.ptn.systemManage.controller.UnLoadingController;



public class UnLoadDeleteDialog extends PtnDialog {
	
	private static final long serialVersionUID = -1740678601593949447L;
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
	private JLabel startTime;//开始时间
	private JTextField startTimeText;
    private JLabel  timeJLabel;//
    private JComboBox timeJCom;
    private JLabel cellType;//激活
    private JCheckBox cellbox;
    private Map<String,Runnable> threadMap ;
    private UnLoadingController unloadController=null;
        
	public UnLoadDeleteDialog(UnLoadingController unloadController) {
		try {
		super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_UPDATE_TRANSFER));
		this.unloadController=unloadController;
			init();
			addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
    


	private void init() {
	
		try {
			UnLoading unload=this.unloadController.getViews().getSelect();
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_DELETE_LOG));  
			weight = 300;		
			startTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_START_TIME));
			startTimeText=new JTextField();	
			if(unload.getDeleteStartTime()==null){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				startTimeText.setText(df.format(new Date()));// new Date()为获取当前系统时间
			}else{
				startTimeText.setText(unload.getDeleteStartTime());
			}
			timeJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_AUTO_BACKDATA));
			timeJCom= new JComboBox();
			timeJCom.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_ONETIME_AUTO_BACKDATA)); //1
			timeJCom.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_ONE_WEEK_TIME_AUTO_BACKDATA));//7
			timeJCom.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_FIFTHTEEN_TIME_AUTO_BACKDATA));//15
			timeJCom.addItem(ResourceUtil.srcStr(StringKeysLbl.LBL_THIRDY_TIME_AUTO_BACKDATA));//30
//			timeJCom.addItem("1min"); //1
//			timeJCom.addItem("7min");//7
//			timeJCom.addItem("15min");//15
//			timeJCom.addItem("30min");//30
			if(unload.getDeleteTime()!=0){
				if(unload.getDeleteTime()==1){
					timeJCom.setSelectedItem("1min");
				}else if(unload.getDeleteTime()==7){
					timeJCom.setSelectedItem("7min");
				}else if(unload.getDeleteTime()==15){
					timeJCom.setSelectedItem("15min");
				}else if(unload.getDeleteTime()==30){
					timeJCom.setSelectedItem("30min");
				}
			}
			cellType = new JLabel(ResourceUtil.srcStr(StringKeysMenu.MENU_ACTIVATION));//
			boolean flag=false;
			if(unload.getDeleteCellyType()==0){
				flag=false;
			}else{
				flag=true;
			}		
			cellbox=new javax.swing.JCheckBox();
			cellbox.setSelected(flag);
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
		
			setCompentLayout();		
			this.add(startTime);
			this.add(startTimeText);
			this.add(timeJLabel);
			this.add(timeJCom);		
			this.add(cellType);
			this.add(cellbox);				
			this.add(confirm);
			this.add(cancel);						
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
					
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

		
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
    //取消事件
	private void cancel() {
		this.dispose();
	}

	

	
	private void confirmSave() {
		ReadUnloadXML readUnloadXML=null;
		List<UnLoading> list=null;
		UnLoading unLoad=null;	
		String	 regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";	
		try {
			threadMap = Ptnf.getPtnf().getThreadMap();
			unLoad=this.unloadController.getViews().getSelect();
		    if(!startTimeText.getText().trim().matches(regex)){
			   DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_ERROR));
			   return ;
		    }	
			String nowTime=DateUtil.strDate(DateUtil.getDate(DateUtil.FULLTIME), DateUtil.FULLTIME);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
	        Date dt1 = df.parse(startTimeText.getText().trim());
	        Date dt2 = df.parse(nowTime);
	       // if(unLoad.getDeleteStartTime()==null && dt1.getTime() < dt2.getTime()) {
	       //        DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_LOG_STARTTIME));
			//	   return;
	      //  }	
		    unLoad.setDeleteStartTime(startTimeText.getText().trim());
		    String timeSelect=timeJCom.getSelectedItem().toString();
		    if(timeSelect.equals(ResourceUtil.srcStr(StringKeysLbl.LBL_ONETIME_AUTO_BACKDATA))){
		    	unLoad.setDeleteTime(1);
		    }else if(timeSelect.equals(ResourceUtil.srcStr(StringKeysLbl.LBL_ONE_WEEK_TIME_AUTO_BACKDATA))){
		    	unLoad.setDeleteTime(7);
		    }else if(timeSelect.equals(ResourceUtil.srcStr(StringKeysLbl.LBL_FIFTHTEEN_TIME_AUTO_BACKDATA))){
		    	unLoad.setDeleteTime(15);
		    }else if(timeSelect.equals(ResourceUtil.srcStr(StringKeysLbl.LBL_THIRDY_TIME_AUTO_BACKDATA))){
		    	unLoad.setDeleteTime(30);
		    }
		    if(cellbox.isSelected()){
		    	    unLoad.setDeleteCellyType(1);
				    AutoDatabaseBackAllThread autoDatabaseBackThread ;//线程
				    AutoDatabaseTimeDeleteThread task ; //定时任务	
					//通过一个线程来启用一个定时器来固定时间来执行任务						
		             long startTime = df.parse(unLoad.getDeleteStartTime()).getTime();       
					 task = new AutoDatabaseTimeDeleteThread(startTime, unLoad);
				   	 long cycleTime=unLoad.getDeleteTime()*24*60*60*1000;
//				   	long cycleTime=unLoad.getDeleteTime()*60*1000;
					 autoDatabaseBackThread = new AutoDatabaseBackAllThread(task, startTime, cycleTime);
					 String threadNames = "task_Delete_Time";
					 Thread threads = new Thread(autoDatabaseBackThread,threadNames);				
					 if(threadMap != null && threadMap.size() >0){
					 if(threadMap.get(threads.getName()) != null){
					     ((AutoDatabaseBackAllThread)threadMap.get(threads.getName())).stop();
				    	 threadMap.remove(threads.getName());
					 }
					 } 
					threads.start();
					threadMap.put(threadNames, autoDatabaseBackThread);
			}else{
				String threadNames = "task_Delete_Time";	
				if(threadMap != null && threadMap.size() >0){
					if(threadMap.get(threadNames) != null){
					 ((AutoDatabaseBackAllThread)threadMap.get(threadNames)).stop();
				    	 threadMap.remove(threadNames);
					 }
				} 										
			}
			   readUnloadXML=new ReadUnloadXML();
			   list=ReadUnloadXML.selectUnloadXML();
			   readUnloadXML.updateUnloadXML(unLoad);			  
			   File fileMider = new File(unLoad.getFileWay());
			   if(!fileMider.exists()){
					 fileMider.mkdirs();
			   }
			this.insertOpeLog(EOperationLogType.DELETELOG.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			 this.dispose();
			 this.unloadController.refresh();		
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			
		}
		
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_TELNETMANAGE_T),"");		
	}


	private void setCompentLayout() {
		try {
			gridBagLayout.columnWidths = new int[] {60,80,40};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] {20,20,20};
			gridBagLayout.rowWeights = new double[] { 0, 0,0,0,0,0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(startTime, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(startTimeText, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(timeJLabel, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(timeJCom, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(cellType, gridBagConstraints);
  
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(cellbox, gridBagConstraints);
			
		
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 25, 5, 5);
			gridBagConstraints.gridx =1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
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
