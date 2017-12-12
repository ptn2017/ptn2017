package com.nms.ui.ptn.alarm.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreePath;

import twaver.Element;
import twaver.tree.ElementNode;

import com.nms.db.bean.alarm.CurrentAlarmBlock;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EObjectType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
/**
 * 告警屏蔽(按中国移动标准开发)
 * 
 * @author Administrator
 * 
 */
public class AlarmBusinessBlockDialog extends PtnDialog {
	private static final long serialVersionUID = 4565808435435469929L;
	private PtnButton confirm;  //确认按钮
	private JButton cancel;   //取消按钮
	private JButton clear;    //清除按钮
	private JLabel lblTaskObj; //告警对象
	private JLabel lblObjectType; 	
	private JComboBox cbObjectType; //告警类型
	private JLabel lblObjectSeverity; 
	private JCheckBox chbUrgency;
	private JCheckBox chbMajor;
	private JCheckBox chbMinor;
	private JCheckBox chbPrompt;
	private String filterInfo;     //保存过滤条件string信息
	private JPanel buttonConfirCanel;
	private JPanel claerJpanel;
	private NeTreePanel neTreePanel = null; // 网元树panel
	
	private JCheckBox lblPerforType;//告警类型
	private JComboBox alarmPerforType;
	private JCheckBox startTimeLabel;//发生时间
	private JTextField alarmHappenText;
	private JLabel startTimeEndLabel;//发生时间止
	private JTextField alarmHappenEndText;//发生时间
	private JLabel lblAlarmSrcType;//监控对象类型
	private JComboBox cmbAlarmSrcType;
	private JLabel  lblMonitorObj;//监控对象
	private JComboBox cmbMonitorObj;
	
	private CurrentAlarmBlock block = null;
	
	public AlarmBusinessBlockDialog() {
		this.setModal(true);
		init();
	}	
	
	public void init() {
		initComponents();
		setLayout();
		initData();
		addListener();
	}
	
	private void initData() {
		//监控对象类型包括: 端口/段/TUNNEL/PW/VPWS业务/VPLS业务
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("0", "所有"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("1", "端口"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("2", "段"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("3", "TUNNEL"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("4", "PW"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("5", "VPWS业务"));
		this.cmbAlarmSrcType.addItem(new ControlKeyValue("6", "VPLS业务"));
	}
	
	public PtnButton getConfirm() {
		return confirm;
	}
	
	public Element getElement(TreeExpansionEvent e){
		TreePath path = e.getPath();
		  if (path != null) {
		    Object comp = path.getLastPathComponent();
		    if (comp instanceof ElementNode) {
		      ElementNode node = ((ElementNode) comp);
		      return node.getElement();
		    }
		  }
		  return null;
	}
	private void addListener() {
		// 保存按钮
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInfo();
			}
		});
		
		//监控对象类型
		cmbAlarmSrcType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performTypeChange();
			}
		});
		
		//取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AlarmBusinessBlockDialog.this.dispose();
			}
		});
		//清除按钮
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AlarmBusinessBlockDialog.this.clear();
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
		
		createCheckBoxActionLisener(startTimeLabel,alarmHappenText,alarmHappenEndText,1);
		
		
		
		lblPerforType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(lblPerforType.isSelected()){
					alarmPerforType.setEnabled(true);
				}else{
					alarmPerforType.setEnabled(false);
				}
			}
		});
		
	}
	
	private void saveInfo() {
		DispatchUtil dispatch = null;
		try {
			if(validateParams()){
				CurrentAlarmBlock block = this.get();
				dispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
				String result = dispatch.alarmBlocking(block);
				DialogBoxUtil.succeedDialog(this, result);
				this.dispose();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	//创建监听事件
	private void createCheckBoxActionLisener(final JCheckBox jCheckBoxObject,final JTextField jTextField,final JTextField jTextField1,final int label) {
		
		try {
			jCheckBoxObject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(jCheckBoxObject.isSelected()){
						if(label ==1){
							jTextField.setText(DateUtil.updateTimeToString(System.currentTimeMillis()-24*60*60*1000,DateUtil.FULLTIME));
							jTextField1.setText(DateUtil.getDate(DateUtil.FULLTIME));
							jTextField1.setEditable(true);
						}
						jTextField.setEditable(true);
					}else{
						if(label ==1){
							 setText(jTextField1);
						}
						 setText(jTextField);
					}
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
	}

	private void setText(JTextField clearTimeText){
		clearTimeText.setEditable(false);
		clearTimeText.setText("");
	}
	
	/**
	 * 监控对象类型改变时加载对应的监控对象
	 */
	private void performTypeChange() {
		ControlKeyValue selectedItem = (ControlKeyValue) this.cmbAlarmSrcType.getSelectedItem();
		int id = Integer.parseInt(selectedItem.getId());
		this.cmbMonitorObj.removeAllItems();
		if(id == 1){
			// 端口
			PortService_MB portService = null;
			SiteService_MB siteService = null;
			try {
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				List<SiteInst> siteList = siteService.select();
				Map<Integer, SiteInst> siteMap = new HashMap<Integer, SiteInst>();
				for(SiteInst site : siteList){
					siteMap.put(site.getSite_Inst_Id(), site);
				}
				PortInst port = new PortInst();
				List<PortInst> portList = portService.select(port);
				List<PortInst> portInstList = new ArrayList<PortInst>();
				if(portList != null){
					for (PortInst p : portList) {
						String type = p.getPortType();
						if("NONE".equals(type) || "NNI".equals(type) || "UNI".equals(type) || "e1".equals(type)){
							portInstList.add(p);
						}
					}
				}
				this.initCmbMonitorObj(portInstList, 7, siteMap);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(portService);
				UiUtil.closeService_MB(siteService);
			}
		}else if(id == 2){
			// 段
			SegmentService_MB segmentService = null;
			try {
				segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
				List<Segment> segmentList = segmentService.select();
				this.initCmbMonitorObj(segmentList, 8, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(segmentService);
			}
		}else if(id == 3){
			//TUNNEL
			TunnelService_MB tunnelService = null;
			try {
				tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Tunnel> tunnelList = tunnelService.select();
				this.initCmbMonitorObj(tunnelList, 1, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(tunnelService);
			}
		}else if(id == 4){
			//PW
			PwInfoService_MB pwService = null;
			try {
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				List<PwInfo> pwList = pwService.select();	
				this.initCmbMonitorObj(pwList, 2, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(pwService);
			}
		}else if(id == 5){
			//vpws业务
			ElineInfoService_MB elineService = null;
			CesInfoService_MB cesService = null;
			try {
				elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				List<ElineInfo> elineList = elineService.select();
				this.initCmbMonitorObj(elineList, 3, null);
				cesService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
				List<CesInfo> cesList = cesService.select();
				this.initCmbMonitorObj(cesList, 6, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(elineService);
				UiUtil.closeService_MB(cesService);
			}
		}else if(id == 6){
			//vpls业务
			EtreeInfoService_MB etreeService = null;
			ElanInfoService_MB elanService = null;
			try {
				etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				Map<Integer, List<EtreeInfo>> etreeMap_netWork = etreeService.select();
				this.initCmbMonitorObj(etreeMap_netWork, 4, null);
				elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				Map<Integer, List<ElanInfo>> elanMap_netWork = elanService.select();
				this.initCmbMonitorObj(elanMap_netWork, 5, null);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(etreeService);
				UiUtil.closeService_MB(elanService);
			}
		}
	}
	
	/**
	 * 初始化监控对象
	 */
	@SuppressWarnings("unchecked")
	private void initCmbMonitorObj(Object obj, int type, Map<Integer, SiteInst> siteMap) {
		if(type == 1){
			//Tunnel
			List<Tunnel> tunnelList = (List<Tunnel>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new Tunnel());
			this.cmbMonitorObj.addItem(all);
			for (Tunnel tunnel : tunnelList) {
				ControlKeyValue con = new ControlKeyValue(tunnel.getTunnelId()+"", tunnel.getTunnelName(), tunnel);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 2){
			//Pw 
			List<PwInfo> pwList = (List<PwInfo>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new PwInfo());
			this.cmbMonitorObj.addItem(all);
			for (PwInfo pw : pwList) {
				ControlKeyValue con = new ControlKeyValue(pw.getPwId()+"", pw.getPwName(), pw);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 3){
			//eline
			List<ElineInfo> elineList = (List<ElineInfo>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new ElineInfo());
			this.cmbMonitorObj.addItem(all);
			for (ElineInfo eline : elineList) {
				ControlKeyValue con = new ControlKeyValue(eline.getId()+"", eline.getName(), eline);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 4){
			//etree
			Map<Integer, List<EtreeInfo>> etreeList = (Map<Integer, List<EtreeInfo>>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new ArrayList<EtreeInfo>());
			this.cmbMonitorObj.addItem(all);
			for(Integer serviceId : etreeList.keySet()){
				List<EtreeInfo> eList = etreeList.get(serviceId);
				if(eList.size() > 0){
					ControlKeyValue con = new ControlKeyValue(serviceId+"", eList.get(0).getName(), eList);
					this.cmbMonitorObj.addItem(con);
				}
			}
		}else if(type == 5){
			//elan
			Map<Integer, List<ElanInfo>> elanList = (Map<Integer, List<ElanInfo>>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new ArrayList<ElanInfo>());
			this.cmbMonitorObj.addItem(all);
			for(Integer serviceId : elanList.keySet()){
				List<ElanInfo> eList = elanList.get(serviceId);
				if(eList.size() > 0){
					ControlKeyValue con = new ControlKeyValue(serviceId+"", eList.get(0).getName(), eList);
					this.cmbMonitorObj.addItem(con);
				}
			}
		}else if(type == 6){
			//ces
			List<CesInfo> cesList = (List<CesInfo>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new CesInfo());
			this.cmbMonitorObj.addItem(all);
			for (CesInfo ces : cesList) {
				ControlKeyValue con = new ControlKeyValue(ces.getId()+"", ces.getName(), ces);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 7){
			// 端口
			List<PortInst> portList = (List<PortInst>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new PortInst());
			this.cmbMonitorObj.addItem(all);
			for (PortInst port : portList) {
				ControlKeyValue con = new ControlKeyValue(port.getPortId()+"", siteMap.get(port.getSiteId()).getCellId()+"/"+port.getPortName(), port);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 8){
			// 段
			List<Segment> segmentList = (List<Segment>) obj;
			ControlKeyValue all = new ControlKeyValue("0", "所有", new Segment());
			this.cmbMonitorObj.addItem(all);
			for (Segment segment : segmentList) {
				ControlKeyValue con = new ControlKeyValue(segment.getId()+"", segment.getNAME(), segment);
				this.cmbMonitorObj.addItem(con);
			}
		}
	}
	
	// 清除面板信息
	private void clear() {
		//对象类型选择网元
		this.cbObjectType.setSelectedIndex(0);
		//告警对象清空
		this.neTreePanel.clear();
		this.neTreePanel.setLevel(2);
		// 告警源清空
		this.cmbAlarmSrcType.setSelectedIndex(0);
		this.cmbMonitorObj.removeAllItems();
		//告警级别选择紧急
		this.chbUrgency.setSelected(true);
		this.chbMajor.setSelected(false);
		this.chbMinor.setSelected(false);
		this.chbPrompt.setSelected(false);
		//告警类型不勾选，选择类型为通讯告警
		this.alarmPerforType.setSelectedIndex(0);
		this.lblPerforType.setSelected(false);
		this.alarmPerforType.setEnabled(false);
		//发生时间不勾选
		this.startTimeLabel.setSelected(false);
		this.setText(alarmHappenText);
		this.setText(alarmHappenEndText);
		this.block = null;
	}
	
	/**
	 * 验证输入数据的正确
	 * 性
	 * @return
	 */
	private boolean validateParams(){
		boolean flag = false;
		try{
			if (!this.neTreePanel.verifySelect()) {
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_ALARMOBJ));
				return false;
			}
			if(!chbUrgency.isSelected()&&!chbMajor.isSelected()&&!chbMinor.isSelected()&&!chbPrompt.isSelected()){
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_ALARMOB));
				return false;
			}
			if(!validateParamsTime(startTimeLabel,alarmHappenText,alarmHappenEndText,ResourceUtil.srcStr(StringKeysObj.HAPPENED_TIME))){
				return false;
			}
			flag = true;
		}catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return flag;
	}

	private void initComponents() {
		buttonConfirCanel=new JPanel();
		claerJpanel=new JPanel();
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		lblTaskObj = new JLabel(ResourceUtil.srcStr(StringKeysObj.ALARM_OBJECT));
		lblObjectType = new JLabel(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE));	
		cbObjectType = new JComboBox();
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
//			cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.BOARD));
		this.neTreePanel = new NeTreePanel(false, 2, null,false);
		 
		lblObjectSeverity = new JLabel(ResourceUtil.srcStr(StringKeysObj.ALARM_LEVEL));
		chbUrgency = new JCheckBox(ResourceUtil.srcStr(StringKeysObj.URGENCY));
		chbMajor = new JCheckBox(ResourceUtil.srcStr(StringKeysObj.MAJOR));
		chbMinor = new JCheckBox(ResourceUtil.srcStr(StringKeysObj.MINOR));
		chbPrompt = new JCheckBox(ResourceUtil.srcStr(StringKeysTitle.TIT_PROMPT));
		chbUrgency.setSelected(true);
		/*增加告警过滤的筛选*/
		lblPerforType = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ALAM_TYPE));
		alarmPerforType = new JComboBox();
		alarmPerforType.setEnabled(false);
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_COMMUNICATION_ALARM));
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_SERVICE_QUALITY_ALARM));
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_EQUIPMENT_ALARM));
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_DO_ERROR_ALARM));
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_ENVIRONMENT_ALARM));
		alarmPerforType.addItem(ResourceUtil.srcStr(StringKeysObj.STRING_EQUIPPOWER_ALARM));
		this.lblAlarmSrcType = new JLabel(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE));
		this.cmbAlarmSrcType = new JComboBox();
		this.lblMonitorObj = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_OBJ));
		this.cmbMonitorObj = new JComboBox();
		
		alarmHappenText = new JTextField();
		alarmHappenText.setEditable(false);
		startTimeLabel = new JCheckBox(ResourceUtil.srcStr(StringKeysObj.HAPPENED_TIME)+" "+ResourceUtil.srcStr(StringKeysObj.LBL_UP));
		
	    startTimeEndLabel = new JLabel(ResourceUtil.srcStr(StringKeysObj.LBL_DOWN));//发生时间止
	    alarmHappenEndText = new JTextField();//发生时间
	    alarmHappenEndText.setEditable(false);
		
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		addJbutton();
		addclear();
		
	  }

	private void addclear() {
		try {
			GridBagLayout layout = new GridBagLayout();
			layout.columnWidths = new int[] {10,10};
			layout.columnWeights = new double[] { 0, 0, 0, 0, 0};
			layout.rowHeights = new int[] { 10, 10};
			layout.rowWeights = new double[] {0, 0, 0, 0,0};
			claerJpanel.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 5, 5, 5);
			layout.addLayoutComponent(clear, c);
			claerJpanel.add(clear);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void addJbutton() {
		try {
			GridBagLayout layout = new GridBagLayout();
			layout.columnWidths = new int[] {10,10};
			layout.columnWeights = new double[] { 0, 0, 0, 0, 0};
			layout.rowHeights = new int[] { 10, 10};
			layout.rowWeights = new double[] {0, 0, 0, 0,0};
			buttonConfirCanel.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.insets = new Insets(5, 5, 5, 5);
			layout.addLayoutComponent(confirm, c);
			buttonConfirCanel.add(confirm);
			
			c.gridx = 1;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(5, 5, 5, 5);
			layout.addLayoutComponent(cancel, c);
			buttonConfirCanel.add(cancel);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}	
	}

	private void setLayout() {		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {20,30,40,40,40,40};
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0.3};
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20,20,20};
		layout.rowWeights = new double[] { 0, 0.2, 0, 0, 0, 0, 0, 0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblObjectType, c);
		this.add(lblObjectType);
		
		c.gridx = 1;
		c.gridy =0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(cbObjectType, c);
		this.add(cbObjectType);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(lblTaskObj, c);
		this.add(lblTaskObj);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 2;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.neTreePanel, c);
		this.add(this.neTreePanel);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblAlarmSrcType, c);
		this.add(lblAlarmSrcType);
		
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(cmbAlarmSrcType, c);
		this.add(cmbAlarmSrcType);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(lblMonitorObj, c);
		this.add(lblMonitorObj);
		
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(cmbMonitorObj, c);
		this.add(cmbMonitorObj);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblObjectSeverity, c);
		this.add(lblObjectSeverity);
		
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(chbUrgency, c);
		this.add(chbUrgency);
		
		c.gridx = 2;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(chbMajor, c);
		this.add(chbMajor);
		
		c.gridx = 3;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(chbMinor, c);
		this.add(chbMinor);
		
		c.gridx = 4;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(chbPrompt, c);
		this.add(chbPrompt);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblPerforType, c);
		this.add(lblPerforType);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(alarmPerforType, c);
		this.add(alarmPerforType);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(startTimeLabel, c);
		this.add(startTimeLabel);
		
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(alarmHappenText, c);
		this.add(alarmHappenText);
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 10);
		layout.addLayoutComponent(startTimeEndLabel, c);
		this.add(startTimeEndLabel);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 4;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(alarmHappenEndText, c);
		this.add(alarmHappenEndText);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 13;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(claerJpanel, c);
		this.add(claerJpanel);
		
		c.gridx = 4;
		c.gridy = 14;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(buttonConfirCanel, c);
		this.add(buttonConfirCanel);
	}

	private CurrentAlarmBlock get() {
		this.block = new CurrentAlarmBlock();
		StringBuilder strBuilder = new StringBuilder();
		List<Integer> levelList = new ArrayList<Integer>();
		//添加监控对象条件
		try{
			ControlKeyValue conType = (ControlKeyValue) this.cmbAlarmSrcType.getSelectedItem();
			ControlKeyValue conObj = (ControlKeyValue) this.cmbMonitorObj.getSelectedItem();
			int id = Integer.parseInt(conType.getId());
			block.setAlarmSrc(0);
			if(id == 0){
				// 所有
				block.setAlarmBusiness(null);
			}else{
				// 具体某一种类型
				block.setAlarmSrc(id);
				if(Integer.parseInt(conObj.getId()) == 0){
					// 所有配置
					block.setAlarmBusiness(null);
				}else{
					// 具体某一条配置
					block.setAlarmBusiness(conObj.getObject());
				}
			}
			
			strBuilder.append(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE)).append("：").append(cbObjectType.getSelectedItem()).append(";");
			if(cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))){
				// 根据网元查询当前性能值
				block.setSiteList(this.neTreePanel.getSelectSiteInst());
				block.setObjectType(EObjectType.SITEINST);
			}else if(cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.BOARD))){
				strBuilder.append(ResourceUtil.srcStr(StringKeysObj.ALARM_OBJECT)).append("：");
				block.setSlotList(this.neTreePanel.getSelectSlotInst());
				block.setObjectType(EObjectType.SLOTINST);
			}
			
			strBuilder.append(ResourceUtil.srcStr(StringKeysObj.ALARM_LEVEL)).append("：");
			if(chbUrgency.isSelected())
			{
				strBuilder.append(ResourceUtil.srcStr(StringKeysObj.URGENCY)+"，");
				levelList.add(5);
			}
			if(chbMajor.isSelected())
			{
				strBuilder.append(ResourceUtil.srcStr(StringKeysObj.MAJOR)+"，");
				levelList.add(4);
			}
			if(chbMinor.isSelected())
			{
				strBuilder.append(ResourceUtil.srcStr(StringKeysObj.MINOR)+"，");
				levelList.add(3);
			}
			if(chbPrompt.isSelected())
			{
				strBuilder.append(ResourceUtil.srcStr(StringKeysTitle.TIT_PROMPT)+",");
				levelList.add(2);
			}
			strBuilder.append(ResourceUtil.srcStr(StringKeysLbl.LBL_ALAM_TYPE)).append("：");
			 //添加告警状态
			 strBuilder.append(ResourceUtil.srcStr(StringKeysObj.ALARM_LEVELSTATE)).append("：");
			 
			strBuilder.replace(strBuilder.length()-1, strBuilder.length(), ";");
			//设置告警级别的数组
			block.setAlarmLevel(levelList);
			//告警类型
            if(lblPerforType.isSelected()){
            	block.setWarningType(alarmPerforType.getSelectedIndex()+1);
			}else{
				block.setWarningType(0);
			}
			//发生时间
			if(startTimeLabel.isSelected()){
				block.setHappenTime(alarmHappenText.getText().trim());
				block.setHappenEndTime(alarmHappenEndText.getText().trim());
			}
			this.filterInfo = strBuilder.toString();	
		}catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			
		}		
		return block;
	}

	public String getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(String filterInfo) {
		this.filterInfo = filterInfo;
	}	
	
	/**
	 * 验证时间的各个时间和用户的正确性
	 * 
	 * @param jcheckBox
	 * @param jText
	 * @return
	 */
	private boolean validateParamsTime(JCheckBox jcheckBox,JTextField jText,JTextField jTextEnd,String jcheckName){
		boolean flag = false;
		try {
			if(jcheckBox.isSelected()){
				//验证时间格式
				if(!CheckingUtil.checking(jText.getText().trim(), CheckingUtil.TIME_REGULAR) ){
					DialogBoxUtil.succeedDialog(this,  jcheckBox.getText()+":"+ResourceUtil.srcStr(StringKeysTip.DATEREGEXFALSE));
					return false;
				}
				if(!CheckingUtil.checking(jTextEnd.getText().trim(), CheckingUtil.TIME_REGULAR) ){
					DialogBoxUtil.succeedDialog(this,  jcheckName+ResourceUtil.srcStr(StringKeysObj.LBL_DOWN)+":"+ResourceUtil.srcStr(StringKeysTip.DATEREGEXFALSE));
					return false;
				}
				if(DateUtil.updateTimeToLong(jText.getText().trim(), DateUtil.FULLTIME) > DateUtil.updateTimeToLong(jTextEnd.getText().trim(),DateUtil.FULLTIME)){
					DialogBoxUtil.succeedDialog(this,  jcheckName+":"+ ResourceUtil.srcStr(StringKeysTip.STARTTIMEANDENDTIME));
					return false;
				}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return flag;
	}
}

