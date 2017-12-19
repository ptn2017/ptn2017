﻿package com.nms.ui.ptn.systemManage.monitor.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.nms.db.bean.ptn.DbInfoTask;
import com.nms.db.bean.system.DataBaseInfo;
import com.nms.model.system.DataBaseService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.xmlbean.LoginConfig;
import com.nms.ui.ptn.systemManage.monitor.MointorCPUThread;
import com.nms.ui.ptn.systemManage.monitor.MonitorDbThread;
/**
 * <p>文件名称:DataBaseRightPanel.java</p>
 * <p>文件描述:监控数据库内存和客户机的内存情况</p>
 * <p>版权所有: 版权所有(C)2013-2015</p>
 * <p>公    司: 北京建博信通软件技术有限公司</p>
 * <p>内容摘要:</p>
 * <p>其他说明: </p>
 * <p>完成日期: 2015年2月12日</p>
 * <p>修改记录1:</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>修改记录2：</p>
 * @version 1.0
 * @author zhangkun
 */
public class DataBaseRightPanel extends JPanel{
	
	private JPanel dataBaseMonitor;
	private JPanel dataBaseJpanel;
	private JPanel dataBaseInfoJpJPanel;
	private JPanel dataBaseInfo = new JPanel();
	private JButton findButoon = null;
	private JButton moinButton = null;
	private JButton startButoon = null;
	private JButton cpuStartButoon = null;
	/**********用来标记是数据库还是服务器****1:数据库；2:表示服务器***********/
    private int lable;	
	
	/**   
	*   
	* @since Ver 1.1   
	*/   
	private static final long serialVersionUID = -7673414709008910387L;
	
	public DataBaseRightPanel(int label) 
	{
		try 
		{
			this.lable = label;
			init();
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 无参构造函数
	 */
	public DataBaseRightPanel() 
	{
	}
	
	
	private void init() throws Exception {
		initComponents();
		setLayout();
		addListener();
	}
	//初始化
   private void initComponents() {
	   
	   try {
		   dataBaseMonitor = new JPanel();
		   dataBaseJpanel = new JPanel();
		   dataBaseInfoJpJPanel = new JPanel();
		   if(lable ==1 ){
			   //数据库资源监控
			   dataBaseMonitor.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_DB)));
			   //数据库资源查询
			   dataBaseJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.DATAFIND)));
			   //数据库服务器基本信息
			   dataBaseInfo.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.DATASERVICE)));
		   }else{
			  //服务器性能监控
			   dataBaseMonitor.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SERVICEPER)));
			   //服务器性能查看
			   dataBaseJpanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SERVICEFIND)));
			   //服务器基本信息
			   dataBaseInfo.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SERVICEBASEINFO)));
			   
		   }
		   initMoinLayoutJpanel(dataBaseMonitor);
		   initBorderLayoutJpanel(dataBaseJpanel);
		   initBorderLayout(dataBaseInfo);
		   
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}

	//数据库资源查询
	private void addListener() {
		
		findButoon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//查看数据库资源
			  new DataInfoAllPanel(lable);
			}
		});
		
		//数据库/CPU监控配置
		moinButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				DataBaseService_MB dataBaseService = null;
				try 
				{
					dataBaseService = (DataBaseService_MB)ConstantUtil.serviceFactory.newService_MB(Services.DATABASEINFO);
					if(lable == 1)
					{
						DbInfoTask dbInfoTask = dataBaseService.selectMoinTableInfo("ptn","1");
						new MointorDbTask(dbInfoTask);
					}else
					{
						DbInfoTask dbInfoTask = dataBaseService.selectMoinTableInfo("ptn","2");
						disdatchDbInfoTask(dbInfoTask);
						new MointorCPUJpanel(dbInfoTask);
					}
					
				} catch (Exception e2) 
				{
					ExceptionManage.dispose(e2, getClass());
				}finally
				{
					UiUtil.closeService_MB(dataBaseService);
				}
			}
		});
		//配置数据库
		if(startButoon != null)
		{
			startButoon.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
						if(startButoon.getText().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START)))
						{
							Thread monitorThread = new Thread(new MonitorDbThread("db"));
							monitorThread.start();
							ConstantUtil.threadMap.put("db", monitorThread);
							startButoon.setText(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP));
							ConstantUtil.dbThread = 1;
						}else if(startButoon.getText().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP)))
						{
							
							startButoon.setText(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START));
							stopThread("db");
							ConstantUtil.dbThread = 0;
						}
					}
			});
		}
		
		//CPU/内存/监控开始和结束
		if(cpuStartButoon != null)
		{
			cpuStartButoon.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
						if(cpuStartButoon.getText().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START)))
						{
							Thread mointorCPUThread = new Thread(new MointorCPUThread(1,"cpu"));
							mointorCPUThread.start();
							ConstantUtil.threadMap.put("cpu", mointorCPUThread);
							Thread mointorDISCThread = new Thread(new MointorCPUThread(2,"memory"));
							mointorDISCThread.start();
							ConstantUtil.threadMap.put("memory", mointorDISCThread);
							cpuStartButoon.setText(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP));
							ConstantUtil.cpuThread = 1;
						}else if(cpuStartButoon.getText().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP)))
						{
							
							cpuStartButoon.setText(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START));
							stopThread("cpu");
							stopThread("memory");
							ConstantUtil.cpuThread = 0;
						}
					}
			});	
		}
	}
	public void setLayout(JPanel main ,JPanel dataBaseJpanel){
		
		GridBagLayout	gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{50,80};
		gridBagLayout.rowWeights = new double[]{50,80};
		gridBagLayout.columnWidths = new int[]{100};
		gridBagLayout.columnWeights = new double[]{0,0.1};
		main.setLayout(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(40, 40, 0, 20);
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBagLayout.setConstraints(dataBaseInfoJpJPanel, c);
		main.add(dataBaseInfoJpJPanel);

		c.insets = new Insets(20, 20, 0, 20);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBagLayout.setConstraints(dataBaseJpanel, c);
		main.add(dataBaseJpanel);
	}

	
	public void setLayout(){
       
		GridBagLayout	gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{50,50,80};
		gridBagLayout.rowWeights = new double[]{100};
		gridBagLayout.columnWidths = new int[]{50,50,50};
		gridBagLayout.columnWeights = new double[]{1};
		this.setLayout(gridBagLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		gridBagLayout.setConstraints(dataBaseMonitor, c);
		this.add(dataBaseMonitor);
		
		c.fill = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		gridBagLayout.setConstraints(dataBaseJpanel, c);
		this.add(dataBaseJpanel);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBagLayout.setConstraints(dataBaseInfo, c);
		this.add(dataBaseInfo);
		
	}
	private void initBorderLayout(JPanel dataBaseInfoJpJPanel) {
		
		JLabel jlabel1 = null;
		JLabel jlabel2 = null;
		JLabel jlabel3 = null;
		JLabel jlabel4 = null;
		JLabel jlabel5 = null;
		JLabel jlabel6 = null;
		GridBagLayout gridBagLayouts = null;
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagLayouts=new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			
			String[] lables = setJlbelValues();
			jlabel1 = new JLabel(lables[0]);
    		jlabel2 = new JLabel(lables[1]);
    		jlabel3 = new JLabel(lables[2]);
    		jlabel4 = new JLabel(lables[3]);
    		jlabel5 = new JLabel(lables[4]);
    		jlabel6 = new JLabel(lables[5]);
			
			dataBaseInfoJpJPanel.setLayout(gridBagLayouts);
			
			gridBagLayouts.columnWidths = new int[] {10,20,50,50,50,50,50};
			gridBagLayouts.columnWeights = new double[] { 1,0, 0, 1 };
			gridBagLayouts.rowHeights = new int[] { 20, 20, 20, 20, 20, 20};
			gridBagLayouts.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gridBagConstraints.fill = GridBagConstraints.BOTH;
		    gridBagConstraints.insets = new Insets(20, 30, 0, 0);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayouts.setConstraints(jlabel1, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayouts.setConstraints(jlabel2, gridBagConstraints);

			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayouts.setConstraints(jlabel3, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayouts.setConstraints(jlabel4, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagLayouts.setConstraints(jlabel5, gridBagConstraints);

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagLayouts.setConstraints(jlabel6, gridBagConstraints);


			dataBaseInfoJpJPanel.add(jlabel1);
			dataBaseInfoJpJPanel.add(jlabel2);
			dataBaseInfoJpJPanel.add(jlabel3);
			dataBaseInfoJpJPanel.add(jlabel4);
			dataBaseInfoJpJPanel.add(jlabel5);
			dataBaseInfoJpJPanel.add(jlabel6);
			dataBaseInfoJpJPanel.setLayout(gridBagLayouts);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
		}
	}
	
    private void initBorderLayoutJpanel(JPanel dataBaseInfoJpJPanel) {
		
		GridBagLayout gridBagLayouts = null;
		GridBagConstraints gridBagConstraints = null;
		String textArea1 = null;
		String textArea2 = null;
		int textLable = 1;
		try {
			gridBagLayouts=new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			dataBaseInfoJpJPanel.setLayout(gridBagLayouts);
			if(lable == 1 )
			{
				textArea1 = ResourceUtil.srcStr(StringKeysTip.DESCSTRING);
				textLable = 1;
			}else
			{               
				textArea1 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SERVICETEXT1);
				textArea2 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SERVICETEXT2);
				textLable = 2;
			}
			
			JTextArea  textArea = setTestAreaValue(textArea1,textArea2,textLable);
			textArea.setEditable(false);
			findButoon = new JButton(ResourceUtil.srcStr(StringKeysTip.FINDBUTTON));
			
			gridBagLayouts.columnWidths = new int[] {50,80};
			gridBagLayouts.columnWeights = new double[] { 0, 1, 0, 1 };
			gridBagLayouts.rowHeights = new int[] { 30,30,30,30};
			gridBagLayouts.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			
			gridBagConstraints.insets = new Insets(20, 30, 0, 20);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayouts.setConstraints(textArea, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(20, 20, 0, 20);
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayouts.setConstraints(findButoon, gridBagConstraints);
			dataBaseInfoJpJPanel.add(textArea);
			dataBaseInfoJpJPanel.add(findButoon);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
		    textArea1 = null;
			textArea2 = null;
		}
	}

	private void initMoinLayoutJpanel(JPanel dataBaseInfoJpJPanel) {
		
		GridBagLayout gridBagLayouts = null;
		GridBagConstraints gridBagConstraints = null;
		String textArea1 = null;
		String textArea2 = null;
		int textLable = 1;
		try {
			gridBagLayouts=new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			dataBaseInfoJpJPanel.setLayout(gridBagLayouts);
			if(lable ==1 ){
				 textArea1 = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_DBTEXT1);
				 textArea2 =  ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_DBTEXT2);
				 textLable = 2;
				 if(ConstantUtil.dbThread == 1){
					 startButoon =  new JButton(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP));
				 }else
				 {
					 startButoon = new JButton(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START));
				 }
			}else
			{
				 textArea1 =  ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_CPUTEXT);
				 textLable = 1;
				 if(ConstantUtil.cpuThread == 1){
					 cpuStartButoon =  new JButton(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_STOP));
				 }else
				 {
					 cpuStartButoon = new JButton(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_START));
				 }
			}
			JTextArea  textArea = setTestAreaValue(textArea1,textArea2,textLable);
			textArea.setEditable(false);
			moinButton = new JButton(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_SET));
			
			gridBagLayouts.columnWidths = new int[] {50,80};
			gridBagLayouts.columnWeights = new double[] { 0, 1, 0, 1 };
			gridBagLayouts.rowHeights = new int[] { 30,30,30,30};
			gridBagLayouts.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			
			gridBagConstraints.insets = new Insets(20, 30, 0, 20);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayouts.setConstraints(textArea, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(20, 20, 0, 20);
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			if(lable ==1)
			{
				gridBagLayouts.setConstraints(startButoon, gridBagConstraints);	
				dataBaseInfoJpJPanel.add(startButoon);
			}
			else
			{
				gridBagLayouts.setConstraints(cpuStartButoon, gridBagConstraints);	
				dataBaseInfoJpJPanel.add(cpuStartButoon);
			}
			gridBagConstraints.insets = new Insets(20, 20, 0, 20);
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayouts.setConstraints(moinButton, gridBagConstraints);
			
			dataBaseInfoJpJPanel.add(textArea);
			dataBaseInfoJpJPanel.add(moinButton);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			textArea1 = null;
			textArea2 = null;
		}
    }
    /**
     * 
     * @return String[] 标签值
     */
    private String[] setJlbelValues()
    {
    	DataBaseService_MB dataBaseService  =  null;
		DataBaseInfo dataBaseInfo = null;
		DispatchUtil serviceDispatch = null;
		String[] lables = new String[6];
    	try 
    	{
    		LoginUtil loginUtil=new LoginUtil();
     		LoginConfig loginConfig = loginUtil.readLoginConfig();
    		if(lable == 1){
    			dataBaseService = (DataBaseService_MB)ConstantUtil.serviceFactory.newService_MB(Services.DATABASEINFO);
    			dataBaseInfo = dataBaseService.slectDataInfo();
    			Properties props = new Properties();
    			InputStream propsIs = DataBaseRightPanel.class.getClassLoader().getResourceAsStream("config/config.properties");
    			props.load(propsIs);
    			String ipAndPortNumber = props.getProperty("jdbc.url").replace("{?}", ConstantUtil.serviceIp);
    			
    			lables[0] = ResourceUtil.srcStr(StringKeysTip.EQNAMES)+ipAndPortNumber.substring(ipAndPortNumber.indexOf("//")+2, ipAndPortNumber.lastIndexOf("/"));
    			lables[1] = ResourceUtil.srcStr(StringKeysTip.EQTYPE);
    			lables[2] = ResourceUtil.srcStr(StringKeysTip.DBTYPE)+dataBaseInfo.getProductName();
    			lables[3] = ResourceUtil.srcStr(StringKeysTip.DBPORT)+ipAndPortNumber.substring(ipAndPortNumber.lastIndexOf("/")-4,ipAndPortNumber.lastIndexOf("/"));
    			lables[4] = ResourceUtil.srcStr(StringKeysTip.DBVER)+dataBaseInfo.getProductName()+":"+dataBaseInfo.getProductVersion();
    			lables[5] = ResourceUtil.srcStr(StringKeysTip.HOSTIP)+"  "+ConstantUtil.serviceIp;
    		}else{
    			 serviceDispatch = new DispatchUtil(RmiKeys.RMI_SERVICE);
    			 String serviceInfo = serviceDispatch.synchro(lable);
    			 lables[0] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_EQUIETNAME)+"("+ConstantUtil.serviceIp+")";
     			 lables[1] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_EQUIETTYPE);
     			 lables[2] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_NEVERSIONS)+ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL3_PTN)+loginConfig.getVersion();
//     			 lables[2] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_NEVERSIONS)+ResourceUtil.srcStr(StringKeysLbl.LBL_JLABTL3_PTN);
     			 lables[3] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_HOSTIP)+ConstantUtil.serviceIp;
     			 lables[4] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_HOSTNAME)+serviceInfo.split(";")[0];
     			 lables[5] = ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_OS)+serviceInfo.split(";")[1];
    		}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
		 UiUtil.closeService_MB(dataBaseService);	
		 serviceDispatch = null;
		 dataBaseInfo = null;
		}
		return lables;
    }
    
    
    /**
     * 为TextArea赋值
     * @return JTextArea
     */
    private JTextArea setTestAreaValue(String textAreaValue1,String textAreaValue2,int textLabel) {
    	JTextArea textArea = new JTextArea();
    	if(textLabel == 1 )
    	{
    		textArea.setText(textAreaValue1);
    	}else
    	{
    		textArea.setText(textAreaValue1+"\r\n");
    		textArea.append(textAreaValue2);
    	}
		return textArea;
	}
    
   /**
    *匹对数据
    * @param dbInfoTask
    * @param object
    */
	public void disdatchDbInfoTask(DbInfoTask dbInfoTask) {
		Map<Integer, Object> serviceValue = new HashMap<Integer, Object>();
		DecimalFormat df = new DecimalFormat("######0.00");
		List<Long> memoryList = null;
		DispatchUtil serviceDispatch = null;
		List<DataBaseInfo> daTableList = null;
		try {
			serviceDispatch = new DispatchUtil(RmiKeys.RMI_SERVICE); 
			Object object =  serviceDispatch.consistence(1);
			serviceValue = (Map<Integer, Object>) object;
			if(dbInfoTask != null && dbInfoTask.getPtnDb_instPath_id() >0 ){
				if(serviceValue.size() >0 && dbInfoTask.getDaTableList() != null && dbInfoTask.getDaTableList().size()>0){
					for(DataBaseInfo dataBaseInfo : dbInfoTask.getDaTableList()){
						if(dataBaseInfo.getName().equals("CPU"))
						{
							//cpu
							dataBaseInfo.setCountSize(Double.parseDouble(serviceValue.get(1).toString()));
							dataBaseInfo.setMointorLevel(2);
						}else if(dataBaseInfo.getName().equals(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_MEMORY)))
						{
							//内存值
							 dataBaseInfo.setMointorLevel(4);
							 memoryList = (List<Long>) serviceValue.get(2);
							 dataBaseInfo.setCountSize(Double.parseDouble(df.format(((double)memoryList.get(1)/(double)memoryList.get(0))*100)));
						}else 
						{
							  File[] roots = (File[]) serviceValue.get(3);  
							  if(roots != null && roots.length >0)
							  {
								  for (File file : roots) 
						            {  
									  if(file.getPath().equals(dataBaseInfo.getName()) && file.getTotalSpace()>0)
									  {
										  long userSpace = file.getTotalSpace() - file.getFreeSpace();
										  dataBaseInfo.setCountSize(Double.parseDouble(df.format(((double)userSpace/(double)file.getTotalSpace())*100)));
										  dataBaseInfo.setCountMemory(file.getTotalSpace()/1024);  
										  dataBaseInfo.setUseMemory(userSpace/1024);
										  dataBaseInfo.setMointorLevel(3);
										  break;
									 }
						        }  
							}
						}
					}
				}
			}else
			{
				dbInfoTask.setMointorTotal(true);
				dbInfoTask.setMointorTypeDb(true);
				daTableList = new ArrayList<DataBaseInfo>();
				DataBaseInfo dataCPUInfo = new DataBaseInfo();
				dataCPUInfo.setName("CPU");
				dataCPUInfo.setMointorLevel(2);
				dataCPUInfo.setCountSize((Double)serviceValue.get(1));
				daTableList.add(dataCPUInfo);
				
				DataBaseInfo dataMemoryInfo = new DataBaseInfo();
				dataMemoryInfo.setName(ResourceUtil.srcStr(StringKeysTip.MOINTOR_LABEL_MEMORY));
				dataMemoryInfo.setMointorLevel(4);
				memoryList = (List<Long>) serviceValue.get(2);
				dataMemoryInfo.setCountSize(Double.parseDouble(df.format(((double)memoryList.get(1)/(double)memoryList.get(0))*100)));
				daTableList.add(dataMemoryInfo);
				
				  File[] roots = (File[]) serviceValue.get(3);  
				  if(roots != null && roots.length >0)
				  {
					  for (File file : roots) 
			            {  
						  if(file.getTotalSpace()>0)
						  {
							  long userSpace = file.getTotalSpace() - file.getFreeSpace();
						      DataBaseInfo dataInfo = new DataBaseInfo();
							  dataInfo.setName(file.getPath());
							  dataInfo.setCountSize(Double.parseDouble(df.format(((double)userSpace/(double)file.getTotalSpace())*100)));
							  dataInfo.setCountMemory(file.getTotalSpace()/1024);  
							  dataInfo.setUseMemory(userSpace/1024);
							  dataInfo.setMointorLevel(3);
							  daTableList.add(dataInfo);  
						  }
			        }  
				}
				  dbInfoTask.setDaTableList(daTableList);
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			serviceValue = null;
			df = null;
			memoryList = null;
			serviceDispatch = null;
		}
	}
	
	/**
	 * 取消摸个线程的任务并且杀死线程
	 * @param thread
	 */
	private void stopThread(String name){
		Thread thread = ConstantUtil.threadMap.get(name);
	 try {
		if(thread != null && !thread.isInterrupted())
		{
			thread.interrupt();
			ConstantUtil.threadMap.remove(name);
		}
		}finally{
			thread = null;
		}
	}
}
