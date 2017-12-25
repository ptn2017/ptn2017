﻿package com.nms.ui.ptn.performance.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.Card;
import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.list.TList;
import twaver.tree.TTree;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.perform.PerformanceTaskInfo;
import com.nms.db.bean.system.Field;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.db.enums.ERunStates;
import com.nms.db.enums.EServiceType;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;

/**
 * 创建长期性能任务面板
 * 
 * @author Administrator
 * 
 */
public class CreatePerforTaskDialog extends PtnDialog {
	
	private static final long serialVersionUID = -6367793259685627493L;
	private PtnButton confirm;
	private JButton cancel;
	private JButton clear;
	private JLabel lblTitle;
	private JLabel lblTaskObj;
	private JScrollPane typePane;
	private JLabel lblPerforType;
	private TDataBox typeBox;
	private TList tlType;
	private JCheckBox cbType;
	private JLabel lblCycle;
	private JLabel lblName;
	private JLabel lblRunStates;
	private JTextField tfName;
	private JRadioButton rb15min;
	private JRadioButton rb24hour;
	private JRadioButton rb50m;
	private JRadioButton rb10min;
	private ButtonGroup rbGroup;
	//网元，板卡，复选框
	private JComboBox cbObjectType;
	private JLabel lblObejctType;
	private JRadioButton rbRun;
	private JRadioButton rbHang;
	private JRadioButton rbStop;
	private ButtonGroup runGroup;
	private JCheckBox cbAutoName;
	private PerformanceTaskPanel view;
	private PerformanceTaskInfo info;
	private JPanel buttonPanel;
	private NeTreePanel neTreePanel = null; // 网元树panel
    private JCheckBox startTime;//开始时间
    private JCheckBox endTime;//结束时间
	private JTextField startTimeField;
	private JTextField endTimeField;
	private JLabel trapLabel;
	private JRadioButton rbTrap;
	public CreatePerforTaskDialog(PerformanceTaskPanel view) {
		this.setModal(true);
		this.view = view;
		init();
	}

	public void init() {
		initComponents();
		setLayout();
		initData();
		addListener();
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		initType();
		boolean isCheck = true;//修改长期性能任务时，界面的性能类型是否选中,默认性能类型全部选中： 即 全选  --选中
		if (view.getSelect() != null) {
			 info = view.getSelect();	
			 cbObjectType.setEnabled(false);
			 List<Element> nodesType = this.neTreePanel.getAllElement();
			 neTreePanel.setEnabled(false);
			 neTreePanel.getTree().setTTreeSelectionMode(TTree.DEFAULT_SELECTION);
			 for (Element node : nodesType) {
				  if(info.getObjectType().getValue()==1){
					cbObjectType.setSelectedIndex(0);
					if(node instanceof Node && node.getUserObject() instanceof SiteInst&& info.getObjectId()==((SiteInst)node.getUserObject()).getSite_Inst_Id()){
						node.setSelected(true);
					}
					}else {
						cbObjectType.setSelectedIndex(1);
						if(node instanceof Card && node.getUserObject() instanceof SlotInst && info.getObjectId()==((SlotInst)node.getUserObject()).getId()){
							node.setSelected(true);
						}	
					}
				}
		    
			tfName.setText(info.getTaskName());
			List<Node> nodes = typeBox.getAllElements();
			for (Node node : nodes) {
				if(info.getPerforType()!=null){
					if (info.getPerforType().contains(node.getDisplayName())) {
						node.setSelected(true);
					} else {
						node.setSelected(false);
						isCheck=false;//只要有性能类型没有选中，全选在修改界面就不选中
					}
				}
			}
			//判断： 全选         是否选中/修改界面
			if(isCheck){
				this.cbType.setSelected(true);
			}
			if (info.getMonitorCycle() == EMonitorCycle.MIN15) {
				rb15min.setSelected(true);
			} else if(info.getMonitorCycle() == EMonitorCycle.HOUR24){
				rb24hour.setSelected(true);
			}else if(info.getMonitorCycle() == EMonitorCycle.M50){
				rb50m.setSelected(true);
			}else if(info.getMonitorCycle() == EMonitorCycle.MIN10){
				rb10min.setSelected(true);
			}
//			rb15min.setEnabled(false);
//			rb24hour.setEnabled(false);
			if (info.getRunstates() != null) {
				if (info.getRunstates() == ERunStates.RUN) {
					rbRun.setSelected(true);
				} else if (info.getRunstates() == ERunStates.HANG) {
					rbHang.setSelected(true);
				} else if (info.getRunstates() == ERunStates.STOP) {
					rbStop.setSelected(true);
				}
			}
			CreatePerforTaskDialog.this.neTreePanel.setEnabled(true);
			lblTitle.setText(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_LONG_PERFORMANCE_TASK));
		
			if(info.getCreateTime()!= null && !info.getCreateTime().equals("")){
				startTime.setSelected(true);
				startTimeField.setEditable(true);
				startTime.setEnabled(false);
				startTimeField.setEditable(false);
				startTimeField.setText(info.getCreateTime()); 
			}
			if(info.getEndTime() != null && !info.getEndTime().equals("")){
				endTime.setSelected(true);
//				endTime.setEnabled(false);
//				endTimeField.setEditable(false);/
				endTimeField.setText(info.getEndTime());
			}
			if(info.getIsReported() == 0){
				rbTrap.setSelected(false);
			}else{
				rbTrap.setSelected(true);
			}
		} else {
			rb15min.setSelected(true);
			rbRun.setSelected(true);
		}
	}
	/**
	 * 事件处理
	 */
	private void addListener() {
		cbAutoName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(CreatePerforTaskDialog.this.neTreePanel.isEnabled()){
					try{
						if (((JCheckBox) e.getSource()).isSelected()) {
							if (CreatePerforTaskDialog.this.neTreePanel.getSelectSiteInst().size()==0) {
								JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
								cbAutoName.setSelected(false);
							} else {
								StringBuilder str = new StringBuilder();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								if(view.getSelect() != null){
									String threadName = view.getSelect().getThreadName();
									String[] names = threadName.split("/");
									str.append(names[0]).append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
									tfName.setText(str.toString());
								}else{
								if( CreatePerforTaskDialog.this.neTreePanel.getSelectField().size()>0){
									if(CreatePerforTaskDialog.this.neTreePanel.getSelectSiteInst().size()>0){
										SiteInst siteInst=CreatePerforTaskDialog.this.neTreePanel.getSelectSiteInst().get(0);
										Field field=CreatePerforTaskDialog.this.neTreePanel.getSelectField().get(0);
										if(CreatePerforTaskDialog.this.neTreePanel.getSelectCardInst().size()>0){
											CardInst card =  CreatePerforTaskDialog.this.neTreePanel.getSelectCardInst().get(0);
											str.append(field.getFieldName()).append("_").append(siteInst.getCellId()).append("_").append(card.getCardName()).append("/")
													.append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
											tfName.setText(str.toString());
										}else{
											str.append(field.getFieldName()).append("_").append(siteInst.getCellId()).append("/").append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
											tfName.setText(str.toString());
										}
									}
								}else{
									tfName.setText("");
								}
							}
							}
						} else {
							tfName.setText("");
						}
						}catch (Exception e1) {
							ExceptionManage.dispose(e1, this.getClass());
						}
					}else{
						if(cbAutoName.isSelected()){
							StringBuilder str = new StringBuilder();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							str.append(info.getTaskName().split("/")[0]).append("/").append(sdf.format(new Date()).toString()).append("/").append(new Date().getTime());
							tfName.setText(str.toString());
						}else{
							tfName.setText("");
						}
					
					}
			
					
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CreatePerforTaskDialog.this.dispose();
			}
		});

		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CreatePerforTaskDialog.this.clear();
			}
		});

		// 性能类型全选复选框
		cbType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (cbType.isSelected()) {
					typeBox.selectAll();
				} else {
					typeBox.getSelectionModel().clearSelection();
				}
			}
		});
		// 下拉列表的选项改变事件
		this.cbObjectType.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					if (itemEvent.getItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))) {
						neTreePanel.setLevel(2);
					} else {
						neTreePanel.setLevel(3);
					}
				}
			}
		});
		
		startTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(startTime.isSelected()){
					startTimeField.setEditable(true);
					startTimeField.setText(DateUtil.getDateMin(DateUtil.FULLTIME));
				}else{
					startTimeField.setText("");
					startTimeField.setEditable(false);
				}
			}
		});
		endTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//结束时间都开始时间前进1一天的时间
				if(endTime.isSelected()){
					endTimeField.setEditable(true);
					String regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
					if(startTimeField.isEditable()){
						if(startTimeField.getText().trim().matches(regex)){
							endTimeField.setText(DateUtil.getDate(new Date(DateUtil.updateTimeToLong(startTimeField.getText().trim(), DateUtil.FULLTIME)+1000*60*60*24),DateUtil.FULLTIME));	
						}
					}else{
						endTimeField.setText(DateUtil.getDate(new Date(System.currentTimeMillis()+1000*60*60*24),DateUtil.FULLTIME));
					}
				}else{
					endTimeField.setText("");
					endTimeField.setEditable(false);
				}
			}
		});
		
		
	}

	// 清除面板信息
	private void clear() {
		this.neTreePanel.clear();
		typeBox.getSelectionModel().clearSelection();
		cbType.setSelected(false);
		tfName.setText("");
		cbAutoName.setSelected(false);
		rb15min.setSelected(false);
		rb24hour.setSelected(false);
		runGroup.clearSelection();
	}

	/*
	 * 将当前界面的数据封装到PerforTaskTemp对象中
	 */
	@SuppressWarnings( { "rawtypes" })
	public List<PerformanceTaskInfo> get() {
		List<PerformanceTaskInfo> taskList = new ArrayList<PerformanceTaskInfo>();
		List<Element> elements = this.neTreePanel.getElement();
		//修改 不能修改网元
		if(view.getSelect() != null){
			 info = view.getSelect();	
			 getPerformanceTask(info);
			 taskList.add(info);
		}else{
			for (Element element : elements) {
				PerformanceTaskInfo task = new PerformanceTaskInfo();
				if(element instanceof Node && (element.getUserObject() instanceof SiteInst || element.getUserObject() instanceof SlotInst )){
					if (element instanceof Node && element.getUserObject() instanceof SiteInst) {
						// 根据网元查找
						SiteInst siteInst = ((SiteInst) element.getUserObject());
						task.setSiteInst(siteInst);
						task.setObjectId(siteInst.getSite_Inst_Id());
						task.setObjectType(EObjectType.SITEINST);
					} else if (element instanceof Card && element.getUserObject() instanceof SlotInst) {
						// 网元+板卡
						SlotInst slot = (SlotInst) element.getUserObject();
						task.setSiteInst((SiteInst) element.getParent().getUserObject());
						task.setObjectId(slot.getId());
						task.setSlotCard(slot.getSlotType());
						task.setObjectType(EObjectType.SLOTINST);
					}
					getPerformanceTask(task);
					taskList.add(task);
				}
			}
		}
		return taskList;
	}

	private void getPerformanceTask(PerformanceTaskInfo task){
		SimpleDateFormat format = null;
		SimpleDateFormat formatOther = null;
		try {
			formatOther = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			task.setTaskName(tfName.getText());
			task.setCreater(ConstantUtil.user.getUser_Name());
			
			//开始时间和结束时间
			if(startTimeField.getText()!= null && !startTimeField.getText().trim().equals("")){
				long l = formatOther.parse(startTimeField.getText().trim()).getTime();
				long l1 = formatOther.parse(format.format(new Date())).getTime();
				//如果开始是按
				if(view.getSelect() != null)
				{
					task.setCreateTime(startTimeField.getText().trim());
				}else
				{
					if(l == l1)
					{
						task.setCreateTime(DateUtil.getDateMin(DateUtil.FULLTIME,startTimeField.getText().trim()));
					}else
					{
					    task.setCreateTime(startTimeField.getText().trim());
				    }	
				}
			}
			else{
				task.setCreateTime(DateUtil.getDateMin(DateUtil.FULLTIME));
			}
			if(endTimeField.getText()!= null && !endTimeField.getText().trim().equals("")){
				task.setEndTime(endTimeField.getText().trim());
			}else 
			{
				task.setEndTime("");
			}
			Integer cycle = 1;
			if(rb24hour.isSelected()){
				cycle = 2;
			}else if(rb50m.isSelected()){
				cycle = 3;
			}else if(rb10min.isSelected()){
				cycle = 4;
			}
			task.setMonitorCycle(EMonitorCycle.forms(cycle));
			task.setTaskLabel(cycle);
			
			if (rbRun.isSelected()) {
				task.setRunstates(ERunStates.RUN);
			} else if (rbHang.isSelected()) {
				task.setRunstates(ERunStates.HANG);
			} else if (rbStop.isSelected()) {
				task.setRunstates(ERunStates.STOP);
				task.setEndTime(DateUtil.getDateMin(DateUtil.FULLTIME));
			} else {
				task.setRunstates(ERunStates.RUN);
			}
			Iterator iter = typeBox.getSelectionModel().selection();
			if (iter.hasNext()) {
				StringBuilder builder = new StringBuilder();
				while (iter.hasNext()) {
					Node node = (Node) iter.next();
					builder.append(node.getDisplayName()).append(",");
				}
				task.setPerforType(builder.substring(0, builder.length() - 1).toString());
			}
			if(rbTrap.isSelected()){
				task.setIsReported(1);
			}else{
				task.setIsReported(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void updateTo(PerformanceTaskInfo info) {
		if (info != null) {
			info.setTaskName(tfName.getText());
			info.setMonitorCycle(EMonitorCycle.forms(rb15min.isSelected() ? 1 : 2));
			if(rb15min.isSelected()&&rb24hour.isSelected()){
				info.setTaskLabel(2);
			}else{
				info.setTaskLabel(1);
			}
			if (rbRun.isSelected()) {
				info.setRunstates(ERunStates.RUN);
			} else if (rbHang.isSelected()) {
				info.setRunstates(ERunStates.HANG);
			} else if (rbStop.isSelected()) {
				info.setRunstates(ERunStates.STOP);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				info.setEndTime(sdf.format(new Date()));
			} else {
				info.setRunstates(ERunStates.RUN);
			}
			
			List<Element> elements = this.neTreePanel.getElement();
			for (Element element : elements) {
				if (element instanceof Node && element.getUserObject() instanceof SiteInst) {
					// 根据网元查找
					SiteInst siteInst = ((SiteInst) element.getUserObject());
					info.setSiteInst(siteInst);
					info.setObjectId(siteInst.getSite_Inst_Id());
					info.setObjectType(EObjectType.SITEINST);
				} else if (element instanceof Card && element.getUserObject() instanceof CardInst) {
					// 网元+板卡
					CardInst cardInst = (CardInst) element.getUserObject();
					info.setSiteInst((SiteInst) element.getParent().getUserObject());
					info.setObjectId(cardInst.getSlotId());
					info.setObjectType(EObjectType.SLOTINST);
				}
			}
			Iterator iter = typeBox.getSelectionModel().selection();
			if (iter.hasNext()) {
				StringBuilder builder = new StringBuilder();
				while (iter.hasNext()) {
					Node node = (Node) iter.next();
					builder.append(node.getDisplayName()).append(",");
				}
				info.setPerforType(builder.substring(0, builder.length() - 1).toString());
			}else{
				info.setPerforType("");
			}
		}
	}

	/**
	 * 验证数据正确性
	 */
	public boolean validateParams() {
		
		boolean  flag = false;
		try {
			//判断是否选择监控网元
			if (!this.neTreePanel.verifySelect()) {
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
				return false;
			}
			//任务名称
			if (tfName.getText() == null || "".equals(tfName.getText())) {
				JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_INPUT_TASK_NAME));
				return false;
			}
			//性能类型
//			Iterator it = typeBox.getSelectionModel().selection();
//			if (!it.hasNext()) {
//				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_PERFORMANCE_TYPE));
//				return false;
//			}		
//			if (!rb15min.isSelected() && !rb24hour.isSelected()) {
//				JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_PERIOD));
//				return false;
//			}
			
			String regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
			
			if(startTimeField.isEditable()){
				String startTime = startTimeField.getText().trim();
				if(!startTime.matches(regex)){
					JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.DATEREGEXFALSE));
					startTimeField.setText(DateUtil.getDateMin(DateUtil.FULLTIME));
					return false;
				}else{
					if(view.getSelect() == null)
					{
						if(DateUtil.updateTimeToLong(startTime,DateUtil.FULLTIME)<System.currentTimeMillis()){
							JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.STARTTIMEERROR));
							return false;
						}
					}
				}
			}
			if(endTimeField.isEditable()){
				String endTime = endTimeField.getText().trim();
				if(!endTime.matches(regex)){  
					JOptionPane.showMessageDialog(CreatePerforTaskDialog.this, ResourceUtil.srcStr(StringKeysTip.DATEREGEXFALSE));
					endTimeField.setText(DateUtil.getDate(new Date(System.currentTimeMillis()+1000*60*60*24),DateUtil.FULLTIME));
					return false;
				}
			}
			
				if(endTimeField.isEditable()&&startTimeField.isEditable()){
					String startTime = startTimeField.getText().trim();
					String endTime = endTimeField.getText().trim();
					if(DateUtil.updateTimeToLong(startTime, DateUtil.FULLTIME) > DateUtil.updateTimeToLong(endTime, DateUtil.FULLTIME)){
						DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.STARTTIMEANDENDTIME));
						return false;
					}
				}
			//验证所选任务名称是否一样
			
			String beforeName = "";
			if (this.view.getSelect() != null) {
				beforeName = this.view.getSelect().getTaskName();
			}

			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyName(EServiceType.PERFORMANCETASK.getValue(), this.tfName.getText().trim(), beforeName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}
			
			flag = true;
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	/**
	 * 用于判断是否选择了监控网元
	 * @return true:选择了监控对象 ；false:没选择监控对象
	 */
	private boolean isSelectObject(){
		
		boolean falg = false;
		List<Element> elements = this.neTreePanel.getElement();
		try {
			for (Element element : elements) {
				if ((element instanceof Node && element.getUserObject() instanceof SiteInst)||(element instanceof Card && element.getUserObject() instanceof CardInst)) {
					falg = true;
					break;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			elements = null;
		}
		return falg;
	}

	private void initComponents() {
		
		if(view.getSelect() != null){
			this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_UPDATE_LONG_PERFORMANCE_TASK));
		}else{
		this.setTitle(ResourceUtil.srcStr(StringKeysLbl.LBL_CREAT_LONG_PERFORMANCE_TASK));
		}
		lblTitle = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CREAT_LONG_PERFORMANCE_TASK));
		/**
		 * 修改 界面，添加 网元，板卡复选框
		 */
		cbObjectType = new JComboBox();
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
//		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.BOARD));
		//对象类型
		lblObejctType=new JLabel(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE));
		lblTaskObj = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_OBJ));
		lblName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_TASK_NAME));
		tfName = new JTextField();
		cbAutoName = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		lblPerforType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROPERTY_TYPE));
		typeBox = new TDataBox();
		tlType = new TList(typeBox);
		tlType.setTListSelectionMode(TList.CHECK_SELECTION);
		tlType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cbType = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLSELECT));
		typePane = new JScrollPane();
		typePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		typePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		typePane.setViewportView(tlType);
		lblCycle = new JLabel(ResourceUtil.srcStr(StringKeysObj.RUN_PERIOD));
		rb15min = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_15_MINUTES));
		rb24hour = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_24_HOURS));
		rb50m = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_50_M));
		rb10min = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_10_MINUTES));
		rbGroup = new ButtonGroup();
		rbGroup.add(rb15min);
		rbGroup.add(rb24hour);
		rbGroup.add(rb50m);
		rbGroup.add(rb10min);
		
		startTime = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_START_TIME));
		endTime = new JCheckBox(ResourceUtil.srcStr(StringKeysOperaType.BTN_OVER_TIME));
		startTimeField = new JTextField(); 
		endTimeField = new JTextField();
		startTimeField.setText("");
		startTimeField.setEditable(false);
		endTimeField.setText("");
		endTimeField.setEditable(false);
		
		lblRunStates = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_RUN_STATUS));
		runGroup = new ButtonGroup();
		rbRun = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_RUN));
		rbHang = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_HANG_UP));
		rbStop = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_STOP));
		runGroup.add(rbRun);
		runGroup.add(rbHang);
		runGroup.add(rbStop);
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		this.neTreePanel=new NeTreePanel(false, 2, null,false);
		buttonPanel=new javax.swing.JPanel();
		trapLabel = new JLabel("是否自动上报");
		rbTrap = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_YES));
	}

	/*
	 * 初始化性能类型
	 */
	private void initType() {
		CapabilityService_MB service = null;
		List<Capability> perforTypeList = null;
		try {
			service = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			perforTypeList = service.select();
			// 过滤相同的指标
			perforTypeList = removeRepeatedType(perforTypeList);
			for (Capability type : perforTypeList) {
				if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
					Node node = new Node();
					node.setName(type.getCapabilitytype());
					node.setDisplayName(type.getCapabilitytype());
					node.setUserObject(type);
					typeBox.addElement(node);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	public PtnButton getConfirm() {
		return confirm;
	}
	/**
	 * 按钮布局
	 */
	private void setButtonLayout(){
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {50,50 };
		layout.columnWeights = new double[] { 0.2, 0.2};
		layout.rowHeights = new int[] { 20};
		layout.rowWeights = new double[] { 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 0);
		layout.setConstraints(this.confirm, c);
		this.buttonPanel.add(confirm);
		c.gridy=1;
		layout.setConstraints(this.cancel, c);
		this.buttonPanel.add(cancel);
		
	}
	/**
	 * 布局
	 */
	private void setLayout() {
		setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 10, 50, 50, 30, 120};
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0 };
		layout.rowHeights = new int[] { 20,20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
		layout.rowWeights = new double[] { 0,0, 0.1, 0, 0, 0, 0, 0, 0, 0, 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 0);
		layout.setConstraints(lblTitle, c);
		//this.add(lblTitle);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(this.lblObejctType, c);
		this.add(lblObejctType);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(this.cbObjectType, c);
		this.add(cbObjectType);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(lblTaskObj, c);
		this.add(lblTaskObj);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 2;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(this.neTreePanel, c);
		this.add(neTreePanel);
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblName, c);
		this.add(lblName);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(tfName, c);
		this.add(tfName);
		c.gridx = 4;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.CENTER;
		layout.addLayoutComponent(cbAutoName, c);
		this.add(cbAutoName);
//		c.fill = GridBagConstraints.BOTH;
//		c.gridx = 0;
//		c.gridy = 5;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 10);
//		layout.addLayoutComponent(lblPerforType, c);
//		this.add(lblPerforType);
//		c.gridx = 1;
//		c.gridy = 5;
//		c.gridheight = 2;
//		c.gridwidth = 4;
//		c.insets = new Insets(5, 5, 5, 10);
//		layout.addLayoutComponent(typePane, c);
//		this.add(typePane);
//		c.gridx = 1;
//		c.gridy = 7;
//		c.gridheight = 1;
//		c.gridwidth = 1;
//		c.insets = new Insets(5, 5, 5, 10);
//		layout.addLayoutComponent(cbType, c);
//		this.add(cbType);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblCycle, c);
		this.add(lblCycle);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(rb15min, c);
		this.add(rb15min);
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb24hour, c);
		this.add(rb24hour);
		c.gridx = 3;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb50m, c);
		this.add(rb50m);
		c.gridx = 4;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb10min, c);
		this.add(rb10min);
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(startTime, c);
		this.add(startTime);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(startTimeField, c);
		this.add(startTimeField);
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(endTime, c);
		this.add(endTime);
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(endTimeField, c);
		this.add(endTimeField);
		
		
		
		c.gridx = 0;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblRunStates, c);
		this.add(lblRunStates);
		c.gridx = 1;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(rbRun, c);
		this.add(rbRun);
		c.gridx = 2;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(rbHang, c);
		this.add(rbHang);
		c.gridx = 3;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rbStop, c);
		this.add(rbStop);
		
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(trapLabel, c);
		this.add(trapLabel);
		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 0);
		layout.addLayoutComponent(rbTrap, c);
		this.add(rbTrap);
		
		if(view.getSelect() == null){
			c.gridx = 0;
			c.gridy = 10;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(10, 5, 20, 5);
			layout.addLayoutComponent(clear, c);
			this.add(clear);
		}
		
		c.gridx = 4;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(buttonPanel, c);
		this.add(buttonPanel);
	}

	/**
	 * fanction:过滤相同的指标
	 * 
	 * @param perforTypeList
	 * @return
	 */
	private List<Capability> removeRepeatedType(List<Capability> perforTypeList) {
		List<Capability> NorepeatedCapability = perforTypeList;
		for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
			for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
				if (NorepeatedCapability.get(j).getCapabilitytype().equals(NorepeatedCapability.get(i).getCapabilitytype())) {
					NorepeatedCapability.remove(j);
				}
			}
		}
		return NorepeatedCapability;
	}
}
