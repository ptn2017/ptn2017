package com.nms.ui.ptn.systemManage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.enums.EOperationLogType;
import com.nms.rmi.ui.util.ServerConstant;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnFileChooser;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysTip;



public class DataAnalysis extends PtnDialog {
	
	private static final long serialVersionUID = -1740678601593949447L;
	private PtnButton confirm;
	private JButton cancel;
	private JPanel buttonJPanel;
	private int weight;
	private GridBagConstraints gridBagConstraints = null;
	private GridBagLayout gridBagLayout = null;
    private JLabel  alarmJLabel;//告警导出方式
    private JComboBox alarmJCom;
    private JLabel  alarmFile;
	private JTextField alarmfileFiled;
	private JButton alarmfileButton;	  
    private JLabel  performanceJLabel;//性能导出方式
    private JComboBox performanceJCom;
    private JLabel  performanceFile;
	private JTextField performancefileFiled;
	private JButton performancefileButton; 
	private JLabel  operationJLabel;//操作日志导出方式
    private JComboBox operationJCom;
    private JLabel  operationFile;
	private JTextField operationfileFiled;
	private JButton operationfileButton;    
    private JLabel  loginJLabel;//登录日孩子导出方式
    private JComboBox loginJCom;
    private JLabel  loginFile;
	private JTextField loginfileFiled;
	private JButton loginfileButton;  
    private List<UnLoading> list = null;
    
        
    
    
	public DataAnalysis() {
		init();
		addListener();
	}


	private void init() {
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			this.setTitle(ResourceUtil.srcStr(StringKeysMenu.MENU_DATA_ANALYSIS));  
			weight = 600;
			list = ReadUnloadXML.selectUnloadXML();			
			alarmJLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_ANALYSIS_ALARM));
			alarmJCom=new JComboBox();
			alarmJCom.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_CVS_FILE));
			performanceJLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_ANALYSIS_PERFORMANCE));
			performanceJCom=new JComboBox();
			performanceJCom.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_CVS_FILE));
			operationJLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_ANALYSIS_OPERATION));
			operationJCom=new JComboBox();
			operationJCom.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));			
			loginJLabel = new JLabel(ResourceUtil.srcStr(StringKeysTip.TIP_ANALYSIS_LOGIN));
			loginJCom=new JComboBox();
			loginJCom.addItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));			
			alarmFile = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
			performanceFile = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
			operationFile = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
			loginFile = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_FILE_ROUTE));
			for(int i=0;i<list.size();i++){
				UnLoading unloads=list.get(i);
				if(unloads.getUnloadType()==1){
					if(unloads.getExportWay()==null){
						alarmfileFiled=new javax.swing.JTextField(ServerConstant.AUTODATAEXPORT_ALARM);
					}else{
						alarmfileFiled=new javax.swing.JTextField(unloads.getExportWay());
					}
					if(unloads.getFileModel()!=0){
						alarmJCom.setSelectedItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));
					}
					continue;
				}
				if(unloads.getUnloadType()==2){
					if(unloads.getExportWay()==null){
						performancefileFiled=new javax.swing.JTextField(ServerConstant.AUTODATAEXPORT_PM);
					}else{
						performancefileFiled=new javax.swing.JTextField(unloads.getExportWay());
					}
					if(unloads.getFileModel()!=0){
						alarmJCom.setSelectedItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));
					}
					continue;
				}
				if(unloads.getUnloadType()==3){
					if(unloads.getExportWay()==null){
						operationfileFiled=new javax.swing.JTextField(ServerConstant.AUTODATAEXPORT_OPERATION);
					}else{
						operationfileFiled=new javax.swing.JTextField(unloads.getExportWay());
					}
					if(unloads.getFileModel()!=0){
						alarmJCom.setSelectedItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));
					}
					continue;
				}
				if(unloads.getUnloadType()==4){
					if(unloads.getExportWay()==null){
						loginfileFiled=new javax.swing.JTextField(ServerConstant.AUTODATAEXPORT_LOGIN);
					}else{
						loginfileFiled=new javax.swing.JTextField(unloads.getExportWay());
					}
					if(unloads.getFileModel()!=0){
						alarmJCom.setSelectedItem(ResourceUtil.srcStr(StringKeysMenu.MENU_EXCEL_FILE));
					}
					continue;
				}				
			}			
			alarmfileFiled.setEditable(false);//只读
			alarmfileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));			
			performancefileFiled.setEditable(false);//只读
			performancefileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));
			operationfileFiled.setEditable(false);//只读
			operationfileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));			
			loginfileFiled.setEditable(false);//只读
			loginfileButton=new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_CHECKCATA));
			
			confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM));
			cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			buttonJPanel = new JPanel();
			buttonJPanel.add(confirm);
			buttonJPanel.add(cancel);
		
			setCompentLayout();		
			this.add(alarmJLabel);
			this.add(alarmJCom);
			this.add(alarmFile);
			this.add(alarmfileFiled);
			this.add(alarmfileButton);
			
			this.add(performanceJLabel);
			this.add(performanceJCom);
			this.add(performanceFile);
			this.add(performancefileFiled);			
			this.add(performancefileButton);
			
			this.add(operationJLabel);
			this.add(operationJCom);
			this.add(operationFile);
			this.add(operationfileFiled);
			this.add(operationfileButton);
			
			this.add(loginJLabel);
			this.add(loginJCom);
			this.add(loginFile);
			this.add(loginfileFiled);
			this.add(loginfileButton);	
					
			this.add(confirm);
			this.add(cancel);						
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void addListener() {
		try {
			alarmfileButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					fileButtonActionListener(1);
					
				}});
			performancefileButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					fileButtonActionListener(1);
					
				}});
			operationfileButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					fileButtonActionListener(1);
					
				}});
			loginfileButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					fileButtonActionListener(1);
					
				}});
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
    //取消事件
	private void cancel() {
		this.dispose();
	}

	
	private void fileButtonActionListener(int a){

		try {
			if(a==1){
				JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, this.alarmfileFiled, null);
			}
			if(a==2){		    	
		    	JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, this.performancefileFiled, null);
			}	
			if(a==3){
		    	JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, this.operationfileFiled, null);
			}
			if(a==4){
		    	JFileChooser chooser=new PtnFileChooser(PtnFileChooser.TYPE_FOLDER, this.loginfileFiled, null);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}	
	
	private void confirmSave() {
		ReadUnloadXML read=null;
		try {
			read=new ReadUnloadXML();
			for(int i=0;i<list.size();i++){
				UnLoading u=list.get(i);
				if(u.getUnloadType()==1){
					u.setFileModel(1);
					u.setExportWay(this.alarmfileFiled.getText());
					read.updateUnloadXML(u);
					continue;
				}
				if(u.getUnloadType()==2){
					u.setFileModel(1);
					u.setExportWay(this.performancefileFiled.getText());
					read.updateUnloadXML(u);
					continue;
				}
				if(u.getUnloadType()==3){
					u.setFileModel(1);
					u.setExportWay(this.operationfileFiled.getText());
					read.updateUnloadXML(u);
					continue;
				}		
				if(u.getUnloadType()==4){
					u.setFileModel(1);
					u.setExportWay(this.loginfileFiled.getText());
					read.updateUnloadXML(u);
					continue;
				}				
			}
					
			this.insertOpeLog(EOperationLogType.DATAANALYSISI.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			
			this.dispose();
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			read=null;
		}
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysMenu.MENU_TELNETMANAGE_T),"");		
	}


	private void setCompentLayout() {
		try {
			gridBagLayout.columnWidths = new int[] {60,40,40,60,160,70,70};
			gridBagLayout.columnWeights = new double[] { 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] {20,20,20,20,20,20};
			gridBagLayout.rowWeights = new double[] { 0, 0,0,0,0,0 };
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(alarmJLabel, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(alarmJCom, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(alarmFile, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(alarmfileFiled, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(alarmfileButton, gridBagConstraints);
            //第二行
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(performanceJLabel, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(performanceJCom, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(performanceFile, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(performancefileFiled, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(performancefileButton, gridBagConstraints);
			//第三行 
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(operationJLabel, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(operationJCom, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(operationFile, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(operationfileFiled, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(operationfileButton, gridBagConstraints);		
			//第四行
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(loginJLabel, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(loginJCom, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(loginFile, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 4;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 2;
			gridBagLayout.setConstraints(loginfileFiled, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 6;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(loginfileButton, gridBagConstraints);				
	        //第五行				
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 5;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridwidth = 1;
			gridBagLayout.setConstraints(confirm, gridBagConstraints);

			gridBagConstraints.insets = new Insets(5, 25, 5, 5);
			gridBagConstraints.gridx =6;
			gridBagConstraints.gridy = 5;
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
