package com.nms.ui.ptn.safety.dialog;

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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.nms.db.bean.system.LogManager;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.AutoDatabaseBackAllThread;
import com.nms.rmi.ui.AutoDatabaseTimeBackThread;
import com.nms.rmi.ui.AutoDatabaseVolumeBackThread;
import com.nms.rmi.ui.util.ServerConstant;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnFileChooser;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.controller.LogManagerController;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;



/**
 * 修改按钮 打开的界面
 * @author sy
 *
 */

public class LogManagerDialog extends PtnDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private    LogManagerPanel loadPanel=new LogManagerPanel(this);
	private JLabel fileType;
	private JTextField fileFiled;
	private JButton fileButton;	
	private JLabel fileVType;
	private JTextField fileVFiled;
	private JButton fileVButton;		
	private PtnButton confirm;
	private JButton cancel;
	private JLabel lblMessage;//提示信息
	private JLabel volume;//容量
	private JTextField volumeFiled;
	private JLabel volumeJ;//容量
	private LogManagerController unloadController=null;
	private JLabel startTime;//开始时间
	private JTextField startTimeText;
	private JLabel timeInterval;//时间间隔
	private PtnSpinner timeJSpinner;
	private Map<String,Runnable> threadMap ;
	private JLabel cellType;//激活
	private JCheckBox cellbox;
	private JLabel vcellType;//激活
	private JCheckBox vcellbox;	
	public LogManagerDialog(LogManagerController unloadController) {
		try {
		super.setTitle(ResourceUtil.srcStr(StringKeysTab.TAB_OPERATION_MANAGERS));
		this.unloadController=unloadController;
			init();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	private void init() throws Exception {
		initComponents();
		setLayout();	
		addListener();
	}
	private void addListener() {		
		//文件选择按钮
		fileButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fileButtonActionListener(1);
				
			}});
		fileVButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fileButtonActionListener(2);
				
			}});		
		
		//保存，（显示转储信息）按钮
		confirm.addActionListener(new MyActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				confirmActionListener();
				
			}

			@Override
			public boolean checking() {
				return true;
			}});
		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LogManagerDialog.this.dispose();
			}
		});	
		

	}	
	/**
	 * 文件转储路径选择
	 */
	private void fileButtonActionListener(int a){

		try {
			if(a==1){
		    	this.fileFiled.setText("");
				FileNameExtensionFilter filter=new FileNameExtensionFilter("sql文件","sql");
				JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, fileFiled, filter);
			}
			if(a==2){
		    	this.fileVFiled.setText("");
				FileNameExtensionFilter filter=new FileNameExtensionFilter("sql文件","sql");
				JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, fileVFiled, filter);
			}			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}	
	private void initComponents() throws Exception {
		LogManager unload=this.unloadController.getView().getSelect();
		if(unload.getLogType()==3 && unload.getFileWay().equals("")){
			unload.setFileWay(ServerConstant.AUTODATABACKOPERATION_LOGTIMEFILE);
		}else if(unload.getLogType()==3 && unload.getFileVWay().equals("")){
			unload.setFileVWay(ServerConstant.AUTODATABACKOPERATION_LOGVOLUMNEFILE);
		}else if(unload.getLogType()==5 && unload.getFileWay().equals("")) {
			unload.setFileWay(ServerConstant.AUTODATABACKLOGIN_LOGTIMEFILE);
		}else if(unload.getLogType()==5 && unload.getFileVWay().equals("")){
			unload.setFileVWay(ServerConstant.AUTODATABACKLOGIN_LOGVOLUMNEFILE);
		}else if(unload.getLogType()==6 && unload.getFileWay().equals("")) {
			unload.setFileWay(ServerConstant.AUTODATABACKSYSTEM_LOGTIMEFILE);
		}else if(unload.getLogType()==6 && unload.getFileVWay().equals("")){
			unload.setFileVWay(ServerConstant.AUTODATABACKSYSTEM_LOGVOLUMNEFILE);
		}else if(unload.getLogType()==7 && unload.getFileWay().equals("")) {
			unload.setFileWay(ServerConstant.AUTODATABACKEVENT_LOGTIMEFILE);
		}else if(unload.getLogType()==7 && unload.getFileVWay().equals("")){
			unload.setFileVWay(ServerConstant.AUTODATABACKEVENT_LOGVOLUMNEFILE);
		}
		fileType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
		fileFiled=new javax.swing.JTextField(unload.getFileWay());
		fileFiled.setEditable(false);//只读
		fileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));
		fileVType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
		fileVFiled=new javax.swing.JTextField(unload.getFileVWay());
		fileVFiled.setEditable(false);//只读
		fileVButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));
		lblMessage=new JLabel();		
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),false);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		cellType = new JLabel(ResourceUtil.srcStr(StringKeysMenu.MENU_TIME_ACTIVATION));//
		vcellType = new JLabel(ResourceUtil.srcStr(StringKeysMenu.MENU_VOLUME_ACTIVATION));//
		startTime = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_START_TIME));//开始时间
		startTimeText = new JTextField();
		volume = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOG_MAXVOLUMN));//开始时间
		volumeFiled =new JTextField();
		volumeFiled.setText("0");
		volumeJ= new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LOG_MAXVOLUMN_M));//开始时间
		timeInterval = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TIMEINTERVAL));//时间间隔
		timeJSpinner = new PtnSpinner(unload.getTimeLimit(),1,Integer.MAX_VALUE,1);
		SimpleDateFormat fat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		if(unload.getStartTime()!= null && !unload.getStartTime().equals("")){
			startTimeText.setText(unload.getStartTime());
		}else{
			startTimeText.setText(fat.format(new Date()));
		}
		if(unload.getVolumeLimit()!=0){
			volumeFiled.setText(unload.getVolumeLimit()+"");
		}
		
		boolean flag=false;
		if(unload.getCellType()==0){
			flag=true;
		}
	    cellbox=new javax.swing.JCheckBox();
		cellbox.setSelected(flag);
		boolean flags=false;
		if(unload.getVcellType()==0){
			flags=true;
		}
	    vcellbox=new javax.swing.JCheckBox();
		vcellbox.setSelected(flags);		 
	}
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 85,80,50,50};
		layout.columnWeights = new double[] { 0,0.1, 0.1,0};
		layout.rowHeights = new int[] { 20,25,35,35,35,35,10};
		layout.rowWeights = new double[] { 0, 0,0,0,0,0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();	
		
		//第一行
		//c.fill = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 5);
		/**1  上边的距离
		 * 2   左边的距离
		 * 3   下边的距离
		 */
		layout.addLayoutComponent(this.lblMessage, c);
		this.add(lblMessage);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.volume, c);
		this.add(volume);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy =1;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.volumeFiled, c);
		this.add(volumeFiled);
		
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.volumeJ, c);
		this.add(volumeJ);

		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileVType, c);
		this.add(fileVType);
		
		c.gridx =1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth =2;
		layout.addLayoutComponent(this.fileVFiled, c);
		this.add(fileVFiled);
		
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileVButton, c);
		this.add(fileVButton);		
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.vcellType, c);
		this.add(vcellType);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy =3;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.vcellbox, c);
		this.add(vcellbox);
		
			
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy =4;
		c.gridheight = 1;
		layout.addLayoutComponent(this.startTime, c);
		this.add(startTime);
			
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy =4;
		c.gridheight = 1;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.startTimeText, c);
		this.add(startTimeText);
			
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy =5;
		c.gridheight = 1;
		c.gridwidth = 2;
		layout.addLayoutComponent(this.timeInterval, c);
		this.add(timeInterval);
			
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy =5;
		c.gridheight = 1;
		c.gridwidth = 3;
		layout.addLayoutComponent(this.timeJSpinner, c);
		this.add(timeJSpinner);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileType, c);
		this.add(fileType);
		
		c.gridx =1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth =2;
		layout.addLayoutComponent(this.fileFiled, c);
		this.add(fileFiled);
		
		c.gridx = 3;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.fileButton, c);
		this.add(fileButton);		
		
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.cellType, c);
		this.add(cellType);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy =7;
		c.gridheight = 1;
		c.gridwidth = 1;
		layout.addLayoutComponent(this.cellbox, c);
		this.add(cellbox);		

		//第7行
		c.fill = GridBagConstraints.EAST;
		c.anchor = GridBagConstraints.EAST;
		c.gridx =2;
		c.gridy = 8;
		c.gridheight = 2;
		c.gridwidth = 1;		
		layout.addLayoutComponent(confirm, c);
		this.add(confirm);
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx =3;
		c.gridy = 8;
		c.gridheight = 2;
		c.gridwidth = 1;
		layout.addLayoutComponent(cancel, c);
		this.add(cancel);
				

	}
	
	

	public PtnButton getConfirm() {
		return confirm;
	}
	
	/**
	 * ，保存事件
	 * 日志管理列表信息
	 */
	private void confirmActionListener(){
		
		List<LogManager> list=null;
		LogManager unload=null;
		LogManager unLoad=null;	
		ReadUnloadXML readUnloadXML=null;
		long time=0;
		String	 regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
		try {
			threadMap = Ptnf.getPtnf().getThreadMap();
			
			
			if(null==volumeFiled.getText()||null==startTime.getText()||volumeFiled.getText().trim().length() == 0||startTimeText.getText().trim().length() == 0){
				DialogBoxUtil.confirmDialog(this.unloadController.getView(), ResourceUtil.srcStr(StringKeysBtn.BTN_UNDATA));
			}else{
				  unLoad=this.unloadController.getView().getSelect();
				  unload =new LogManager();
				  unload.setLogType(unLoad.getLogType());
				  
				  if (!CheckingUtil.checking(volumeFiled.getText().trim(), CheckingUtil.NUMBER_REGULAR) ) {
					  DialogBoxUtil.succeedDialog(null,  ResourceUtil.srcStr(StringKeysLbl.LBL_LOG_MAXVOLUMN_NUMBER));
					  return;
				  }
				  if (this.getFileFiled().getText().trim().length() == 0) {
					  DialogBoxUtil.succeedDialog(null,  ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKBACKUP_ROUTE));
					  return;
				  }
				  unload.setFileWay(this.getFileFiled().getText());
				  if (this.getFileVFiled().getText().trim().length() == 0) {
					  DialogBoxUtil.succeedDialog(null,  ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKBACKUP_ROUTE));
					  return;
				  }	
				  unload.setFileVWay(this.getFileVFiled().getText());
				  unload.setVolumeLimit(Integer.parseInt(volumeFiled.getText().trim()));
				  if(!startTimeText.getText().trim().matches(regex)){
					   DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysLbl.LBL_TIME_ERROR));
					   return ;
				  }
				  if(this.cellbox.isSelected()){					
						unload.setCellType(0);
				  }else{
						unload.setCellType(1);
				  }
				  if(this.vcellbox.isSelected()){					
						unload.setVcellType(0);
				  }else{
						unload.setVcellType(1);
				  }	
				  String nowTime=DateUtil.strDate(DateUtil.getDate(DateUtil.FULLTIME), DateUtil.FULLTIME);
				  DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		          Date dt1 = df.parse(startTimeText.getText().trim());
		          Date dt2 = df.parse(nowTime);
		       //   if (dt1.getTime() < dt2.getTime()) {
		       //       DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_LOG_STARTTIME));
				//	   return;
		       //   }
				  unload.setStartTime(startTimeText.getText().trim());
				  unload.setTimeLimit(Integer.parseInt(this.timeJSpinner.getTxtData()));				  
				  if(this.cellbox.isSelected()){					  				  
					  AutoDatabaseBackAllThread autoDatabaseBackThread ;//线程
					  AutoDatabaseTimeBackThread task ; //定时任务	
						//通过一个线程来启用一个定时器来固定时间来执行任务						
		             long startTime = df.parse(unload.getStartTime()).getTime();       
					 task = new AutoDatabaseTimeBackThread(startTime, unload);
				   	 long cycleTime=unload.getTimeLimit()*60*60*1000;
					 autoDatabaseBackThread = new AutoDatabaseBackAllThread(task, startTime, cycleTime);
					 String threadNames = "task_Log_Time";
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
					String threadNames = "task_Log_Time";	
					if(threadMap != null && threadMap.size() >0){
						if(threadMap.get(threadNames) != null){
						 ((AutoDatabaseBackAllThread)threadMap.get(threadNames)).stop();
					    	 threadMap.remove(threadNames);
						 }
					} 										
				}
			   if(this.vcellbox.isSelected()){  
				   AutoDatabaseVolumeBackThread autoThread = new AutoDatabaseVolumeBackThread(time,unload);
				   String threadName = "task_LogVolume_"+unLoad.getLogType();
				   Thread thread = new Thread(autoThread,threadName);
				   if(threadMap != null && threadMap.size() >0){
					   if(threadMap.get(thread.getName()) != null){
						   ((AutoDatabaseVolumeBackThread)threadMap.get(thread.getName())).stop();
						   threadMap.remove(thread.getName());
					   }
				   }
				   thread.start();
				   threadMap.put(thread.getName(), autoThread);
			   }else{
					String threadNames = "task_LogVolume_";	
					if(threadMap != null && threadMap.size() >0){
						if(threadMap.get(threadNames) != null){
						 ((AutoDatabaseVolumeBackThread)threadMap.get(threadNames)).stop();
					    	 threadMap.remove(threadNames);
						 }
					}
			   }
				  
			    readUnloadXML=new ReadUnloadXML();
			    list=ReadUnloadXML.selectLog();
			    LogManager old=new LogManager();
			    for(int i=0;i<list.size();i++){
				   if(list.get(i).getLogType() == unLoad.getLogType()){
					   old=list.get(i);	
					   break;
				   }
				   				  
			   }
			   readUnloadXML.updateUnloadXML(unload);
			   
			   File fileMider = new File(unload.getFileWay());
			   if(!fileMider.exists()){
					 fileMider.mkdirs();
				 }
			   fileMider = new File(unload.getFileVWay());
			   if(!fileMider.exists()){
					 fileMider.mkdirs();
				 }
				//添加日志记录
			   this.confirm.setOperateKey(EOperationLogType.UNLOADUPDATE.getValue());
			   confirm.setResult(1);
			   this.insertOpeLog(EOperationLogType.UPDATELOGMANAGER.getValue(), ResultString.CONFIG_SUCCESS, old, unload);
			   this.unloadController.refresh();				
			}
				
		} catch (Exception e) {
			
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			unLoad=null;
			unload=null;
		}
		LogManagerDialog.this.dispose();		
			
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_UNLOADING),"LogManager");		
	}
	public JTextField getFileFiled() {
		return fileFiled;
	}
	public void setFileFiled(JTextField fileFiled) {
		this.fileFiled = fileFiled;
	}
	public JTextField getFileVFiled() {
		return fileVFiled;
	}
	public void setFileVFiled(JTextField fileVFiled) {
		this.fileVFiled = fileVFiled;
	}	
}
