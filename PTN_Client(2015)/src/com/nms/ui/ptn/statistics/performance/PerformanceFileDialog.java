package com.nms.ui.ptn.statistics.performance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.list.TList;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.perform.HisPerformanceService_Mb;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.model.CurrentPerformanceFilter;
import com.nms.ui.ptn.performance.model.HisPerformanceFilter;
import com.nms.ui.ptn.performance.view.TimeWindow;

/**
 * 历史性能统计 设置过滤
 * @author Administrator
 * 
 */
public class PerformanceFileDialog extends PtnDialog {

	private static final long serialVersionUID = 8186959383566893786L;
	private JLabel lblSite;//网元
	private JComboBox cmbSite;
	private JLabel lblPerformType;//监控对象类型
	private JComboBox cmbPerformType;
	private JLabel  lblMonitorObj;//监控对象
	private JComboBox cmbMonitorObj;
	private JLabel lblPerforType;// 性能类型
	private TDataBox treeBox;
	private TList tlist;
	private JScrollPane treePane;
	private JLabel lblCycle;//采样周期
	private JComboBox cmbCycle;
    private JPanel buttonPanel;
    private PtnButton confirm;//确定
	private JButton cancel;
	private JButton clear;
	private PerformanceInfoPanel view;
	private CurrentPerformanceFilter filter;
	private JLabel endTime;
	private JComboBox selectEndTime;
	private ControlKeyValue newTime;
	private JLabel lblShowType;
	private JComboBox cmbShowType;
	private Map<String,List<Capability>> portMap=new HashMap<String,List<Capability>>();
    private Map<String,List<Capability>> tmsMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> tmpMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> tmcMap=new HashMap<String,List<Capability>>();
	private Map<String,List<Capability>> ethMap=new HashMap<String,List<Capability>>();
	private List<Capability> portList=new ArrayList<Capability>();
	private List<Capability> tmsList=new ArrayList<Capability>();
	private List<Capability> tmpList=new ArrayList<Capability>();
	private List<Capability> tmcList=new ArrayList<Capability>();
	private List<Capability> ethList=new ArrayList<Capability>();
	public PerformanceFileDialog(PerformanceInfoPanel view) {
		this.setModal(true);
		this.view = view;
		this.initComponents();
		this.setLayout();
		this.initData();
		this.addListener();
		UiUtil.showWindow(this, 400, 400);
	}
	
	private void initType() {
		CapabilityService_MB service = null;
		List<Capability> perforTypeList = null;
		List<Capability> perforList = null;
		try {
			service = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			perforTypeList = service.select();
			perforList=new ArrayList<Capability>();
			perforList.addAll(perforTypeList);
//			perforTypeList = removeRepeatedType(perforTypeList,1);
//			perforList=removeRepeatedType(perforList,2);
			//增加性能类别
			for (Capability type : perforList) {
				if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
					Node nodeType = new Node();
					if(ResourceUtil.language.equals("zh_CN")){
						nodeType.setName(type.getCapabilitydesc());
						nodeType.setDisplayName(type.getCapabilitydesc());
					}else{
						nodeType.setName(type.getCapabilitydesc_en());
						nodeType.setDisplayName(type.getCapabilitydesc_en());
					}
					nodeType.setUserObject(type);
					treeBox.addElement(nodeType);
					if(type.getCapabilitytype().equalsIgnoreCase("PORT")){
						portList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("TMP/TMC")){
						if(type.getCapabilityname().contains("TMP")){
							tmpList.add(type);
						}else{
							tmcList.add(type);
						}
					}else if(type.getCapabilitytype().equalsIgnoreCase("TMS")){
						tmsList.add(type);
					}else if(type.getCapabilitytype().equalsIgnoreCase("ETH")){
						ethList.add(type);
					}
				}
			}
			  portMap.put("PORT", portList);
			  tmsMap.put("TMS", tmsList);
			  tmpMap.put("TMP", tmpList);
			  tmcMap.put("TMC", tmcList);
			  ethMap.put("ETH", ethList);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	private List<Capability> removeRepeatedType(List<Capability> perforTypeList,int label) {
		List<Capability> NorepeatedCapability = perforTypeList;
		//增加性能类型
		if(label==1){
			for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
				for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
					if (NorepeatedCapability.get(j).getCapabilitytype().equals(NorepeatedCapability.get(i).getCapabilitytype())) {
						NorepeatedCapability.remove(j);
					}
				}
			}
		}else{
			//增加性能类别
			for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
				for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
					if (NorepeatedCapability.get(j).getCapabilitydesc().equals(NorepeatedCapability.get(i).getCapabilitydesc())) {
						NorepeatedCapability.remove(j);
					}
				}
			}
		}
		return NorepeatedCapability;
	}
	
	private void initComponents() {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		this.lblSite = new JLabel("网元");
		this.cmbSite = new JComboBox();
		this.lblPerformType = new JLabel("监控对象类型");
		this.cmbPerformType = new JComboBox();
		this.lblMonitorObj = new JLabel("监控对象");
		this.cmbMonitorObj = new JComboBox();
		this.lblPerforType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PROPERTY_TYPE));
		treeBox=new TDataBox();
		treePane=new JScrollPane();
		tlist=new TList(treeBox);
		tlist.setTListSelectionMode(TList.CHECK_SELECTION);
		tlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		treePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		treePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treePane.setViewportView(tlist);
		this.lblCycle = new JLabel("采样周期");
		this.cmbCycle = new JComboBox();
		endTime = new JLabel(ResourceUtil.srcStr(StringKeysOperaType.BTN_OVER_TIME));
		selectEndTime = new JComboBox();
		try {
			super.getComboBoxDataUtil().comboBoxData(this.selectEndTime, "hisPerformCount");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.lblShowType = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DATA_SHOW));
		this.cmbShowType = new JComboBox();
		this.clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		this.confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		this.cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.buttonPanel=new JPanel();
		this.buttonPanel.add(confirm);
		this.buttonPanel.add(cancel);
	}
	
	private void setLayout() {
		this.setCompentLayoutButton(buttonPanel, confirm, cancel);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 40, 40, 130 };
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0.3 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblSite, c);
		this.add(lblSite);
		
		c.gridx = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cmbSite, c);
		this.add(cmbSite);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblPerformType, c);
		this.add(lblPerformType);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cmbPerformType, c);
		this.add(cmbPerformType);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblMonitorObj, c);
		this.add(lblMonitorObj);
		
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cmbMonitorObj, c);
		this.add(cmbMonitorObj);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblPerforType, c);
		this.add(lblPerforType);
		
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 5;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(treePane, c);
		this.add(treePane);
		
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblCycle, c);
		this.add(lblCycle);
		
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cmbCycle, c);
		this.add(cmbCycle);
		
		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(endTime, c);
		this.add(endTime);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(selectEndTime, c);
		this.add(selectEndTime);
		
		c.gridx = 0;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblShowType, c);
		this.add(lblShowType);
		c.gridx = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.addLayoutComponent(cmbShowType, c);
		this.add(cmbShowType);
		
		c.gridx = 0;
		c.gridy = 14;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(clear, c);
		this.add(clear);
		
		c.gridx = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(buttonPanel, c);
		this.add(buttonPanel);
	}

	/**
	 *  按钮所在按钮布局
	 */
	private void setCompentLayoutButton(JPanel jpenl,JButton button1,JButton button2) {
		GridBagConstraints gridBagConstraints=null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths=new int[]{15,15};
			gridBagLayout.columnWeights=new double[]{0,0};
			gridBagLayout.rowHeights=new int[]{21};
			gridBagLayout.rowWeights=new double[]{0};
			
			gridBagConstraints.insets=new Insets(5,5,5,5);
			gridBagConstraints= new GridBagConstraints();
			gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(button1, gridBagConstraints);
			
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(button2, gridBagConstraints);
			
			jpenl.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void addListener() {
		selectEndTime.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue ck = (ControlKeyValue) selectEndTime.getSelectedItem();
					Code code = (Code) (ck.getObject());
					if (code != null && code.getCodeValue().equals("6")) {
						TimeWindow timewndow = new TimeWindow();
						UiUtil.showWindow(timewndow, 300, 200);
						if(!(timewndow.getStartTimeText().getText()==null||timewndow.getStartTimeText().getText().equals("")||
								timewndow.getEndTimeText().getText()==null||timewndow.getEndTimeText().getText().equals(""))){
							view.setStartTime(timewndow.getStartTimeText().getText());
							view.setReadEndTime(timewndow.getEndTimeText().getText());
							String addItems = ResourceUtil.srcStr(StringKeysTip.START) + view.getStartTime()+ ResourceUtil.srcStr(StringKeysTip.END)+ view.getReadEndTime();
							if (newTime != null) {
								selectEndTime.removeItem(newTime);
							}
							newTime = new ControlKeyValue("7",addItems);
							selectEndTime.addItem(newTime);
							selectEndTime.setSelectedItem(newTime);
						}
					}
				}
			}
		});
		
		//监控对象类型
		cmbPerformType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performTypeChange();
			}
		});
		
		//保存按钮
		confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSaveAction();
			}
		});
		
		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		//清除按钮
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
				
			}
		});
	}

	/**
	 * 初始化网元,监控对象类型和采样周期
	 */
	private void initData() {
		//网元
		this.initSiteList();
		//监控对象类型包括: 端口/TUNNEL/PW/以太网业务
		this.cmbPerformType.addItem(new ControlKeyValue("0", ""));
		this.cmbPerformType.addItem(new ControlKeyValue("1", "端口"));
//		this.cmbPerformType.addItem(new ControlKeyValue("2", "段"));
		this.cmbPerformType.addItem(new ControlKeyValue("3", "TUNNEL"));
		this.cmbPerformType.addItem(new ControlKeyValue("4", "PW"));
//		this.cmbPerformType.addItem(new ControlKeyValue("5", "以太网业务"));
		//采样周期: 15min/24hour
		this.cmbCycle.addItem(new ControlKeyValue("1", "15min"));
		this.cmbCycle.addItem(new ControlKeyValue("2", "24hour"));
		this.cmbShowType.addItem(new ControlKeyValue("1", "图形"));
		this.cmbShowType.addItem(new ControlKeyValue("2", "表格"));
		this.initType();
	}
	
	/**
	 * 初始化网元信息
	 */
	private void initSiteList() {
		SiteService_MB siteServiceMB = null;
		try {
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			List<SiteInst> siteList = siteServiceMB.select();
			for (SiteInst site : siteList) {
				ControlKeyValue con = new ControlKeyValue(site.getSite_Inst_Id()+"", site.getCellId(), site);
				this.cmbSite.addItem(con);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(siteServiceMB);
		}
	}

	/**
	 * 监控对象类型改变时加载对应的监控对象
	 */
	private void performTypeChange() {
		ControlKeyValue selectedItem = (ControlKeyValue) this.cmbPerformType.getSelectedItem();
		int id = Integer.parseInt(selectedItem.getId());
		int siteId = Integer.parseInt(((ControlKeyValue)this.cmbSite.getSelectedItem()).getId());
		this.cmbMonitorObj.removeAllItems();
		this.treeBox.clear();
		if(id == 1){
			//端口
			PortService_MB portService = null;
			try {
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				PortInst portinst = new PortInst();
				portinst.setSiteId(siteId);
				List<PortInst> portList = portService.select(portinst);
				List<PortInst> portInstList = new ArrayList<PortInst>();
				for (PortInst port : portList) {
					if(port.getPortType().equals("NNI") || port.getPortType().equals("UNI")
							|| port.getPortType().equals("NONE")){
						portInstList.add(port);
					}
				}
				this.initCmbMonitorObj(portInstList, 1);
				this.initPerformanceType(1);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(portService);
			}
//		}else if(id == 2){
//			//段
//			SegmentService_MB segmentService = null;
//			try {
//				segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
//				List<Segment> segList = segmentService.queryBySiteId(siteId);
//				this.initCmbMonitorObj(segList, 2);
//			    this.initPerformanceType(2);
//			} catch (Exception e) {
//				ExceptionManage.dispose(e, this.getClass());
//			} finally {
//				UiUtil.closeService_MB(segmentService);
//			}
		}else if(id == 3){
			//TUNNEL
			TunnelService_MB tunnelServiceMB = null;
			try {
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Tunnel> tunnelList = tunnelServiceMB.queryTunnelBySiteId(siteId);
				this.initCmbMonitorObj(tunnelList, 3);
				this.initPerformanceType(3);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(tunnelServiceMB);
			}
		}else if(id == 4){
			//PW
			PwInfoService_MB pwService = null;
			try {
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				List<PwInfo> pwList = pwService.queryBySiteId(siteId);	
				this.initCmbMonitorObj(pwList, 4);
				this.initPerformanceType(4);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(pwService);
			}
		}else if(id == 5){
			//以太网业务
			ElineInfoService_MB elineServiceMB = null;
			EtreeInfoService_MB etreeServiceMB = null;
			ElanInfoService_MB elanService = null;
			try {
				this.initPerformanceType(5);
				elineServiceMB = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
				List<ElineInfo> elineList = elineServiceMB.selectBySite_network(siteId);
				elineList.addAll(elineList.size(), elineServiceMB.selectBySite_node(siteId));
				this.initCmbMonitorObj(elineList, 5);
				etreeServiceMB = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				Map<Integer, List<EtreeInfo>> etreeMap_netWork = etreeServiceMB.selectBySite_network(siteId);
				Map<Integer, List<EtreeInfo>> etreeMap_node = etreeServiceMB.selectBySite_node(siteId);
				this.initCmbMonitorObj(etreeMap_netWork, 6);
				this.initCmbMonitorObj(etreeMap_node, 6);
				elanService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
				Map<Integer, List<ElanInfo>> elanMap_netWork = elanService.selectBySite_network(siteId);
				Map<Integer, List<ElanInfo>> elanMap_node = elanService.selectBySite_node(siteId);
				this.initCmbMonitorObj(elanMap_netWork, 7);
				this.initCmbMonitorObj(elanMap_node, 7);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(elineServiceMB);
				UiUtil.closeService_MB(etreeServiceMB);
				UiUtil.closeService_MB(elanService);
			}
		}
	}
	
	/**
	 * 初始化性能类型
	 * @param type
	 */
	private void initPerformanceType(int type) {
		if(type == 1){
			List<Capability> perforList = portMap.get("PORT");
			initTreeData(perforList);
		}else if(type == 2){
			
		}else if(type == 3){
			List<Capability> perforList = tmpMap.get("TMP");
			initTreeData(perforList);
		}else if(type == 4){
			List<Capability> perforList = tmcMap.get("TMC");
			initTreeData(perforList);
		}else if(type == 5){
			List<Capability> perforList = ethMap.get("ETH");
			initTreeData(perforList);
		}
	}
	
	/**
	 * 初始化性能类别
	 * @param perforList
	 */
	private void initTreeData(List<Capability> perforList){
		for (Capability type : perforList) {
			if (type.getCapabilitytype() != null && !"".equals(type.getCapabilitytype())) {
				Node nodeType = new Node();
				if(ResourceUtil.language.equals("zh_CN")){
					nodeType.setName(type.getCapabilitydesc());
					nodeType.setDisplayName(type.getCapabilitydesc());
				}else{
					nodeType.setName(type.getCapabilitydesc_en());
					nodeType.setDisplayName(type.getCapabilitydesc_en());
				}
				nodeType.setUserObject(type);
				treeBox.addElement(nodeType);
			}
		}
	}

	/**
	 * 初始化监控对象
	 */
	@SuppressWarnings("unchecked")
	private void initCmbMonitorObj(Object obj, int type) {
		if(type == 1){
			//端口
			List<PortInst> portList = (List<PortInst>) obj;
			for (PortInst port : portList) {
				ControlKeyValue con = new ControlKeyValue(port.getPortId()+"", port.getPortName(), port);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type ==2){
			//段
			List<Segment> segList = (List<Segment>) obj;
			for (Segment seg : segList) {
				ControlKeyValue con = new ControlKeyValue(seg.getId()+"", seg.getNAME(), seg);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 3){
			//Tunnel
			List<Tunnel> tunnelList = (List<Tunnel>) obj;
			for (Tunnel tunnel : tunnelList) {
				ControlKeyValue con = new ControlKeyValue(tunnel.getTunnelId()+"", tunnel.getTunnelName(), tunnel);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 4){
			//Pw 
			List<PwInfo> pwList = (List<PwInfo>) obj;
			for (PwInfo pw : pwList) {
				ControlKeyValue con = new ControlKeyValue(pw.getPwId()+"", pw.getPwName(), pw);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 5){
			//eline
			List<ElineInfo> elineList = (List<ElineInfo>) obj;
			for (ElineInfo eline : elineList) {
				ControlKeyValue con = new ControlKeyValue(EServiceType.ELINE.toString(), eline.getName(), eline);
				this.cmbMonitorObj.addItem(con);
			}
		}else if(type == 6){
			//etree
			Map<Integer, List<EtreeInfo>> etreeList = (Map<Integer, List<EtreeInfo>>) obj;
			for(Integer serviceId : etreeList.keySet()){
				List<EtreeInfo> eList = etreeList.get(serviceId);
				if(eList.size() > 0){
					ControlKeyValue con = new ControlKeyValue(EServiceType.ETREE.toString(), eList.get(0).getName(), eList);
					this.cmbMonitorObj.addItem(con);
				}
			}
		}else if(type == 7){
			//elan
			Map<Integer, List<ElanInfo>> elanList = (Map<Integer, List<ElanInfo>>) obj;
			for(Integer serviceId : elanList.keySet()){
				List<ElanInfo> eList = elanList.get(serviceId);
				if(eList.size() > 0){
					ControlKeyValue con = new ControlKeyValue(EServiceType.ELAN.toString(), eList.get(0).getName(), eList);
					this.cmbMonitorObj.addItem(con);
				}
			}
		}
	}
	
	/**
	 * 清除界面数据
	 */
	private void clear(){
		this.cmbPerformType.setSelectedIndex(0);
		this.cmbMonitorObj.removeAllItems();
		this.cmbCycle.setSelectedIndex(0);
		this.selectEndTime.setSelectedIndex(0);
		this.cmbShowType.setSelectedIndex(0);
	}

	private void btnSaveAction(){
		HisPerformanceService_Mb service = null;
		try {
			//数据完整性校验
			boolean flag = this.checkIsFull();
			if(flag){
				String value="";
				if(selectEndTime.getSelectedItem().equals(newTime)){
					value=7+"";
				}else{
					ControlKeyValue controlKeyValue=(ControlKeyValue)selectEndTime.getSelectedItem();
					 value=((Code)controlKeyValue.getObject()).getCodeValue();
					if("0".equals(value)){
						DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_INPUT_OVERTIME));
						return;
					}
				}
				view.setCode(Integer.parseInt(value));
				HisPerformanceFilter filter = this.getFilter();
				filter.setStartTime(DateUtil.updateTimeToLong(view.getStartTime(), DateUtil.FULLTIME));
				filter.setEndTime(DateUtil.updateTimeToLong(view.getReadEndTime(), DateUtil.FULLTIME));
				view.clear();
				int count=0;
				Iterator it = treeBox.getSelectionModel().selection();
				List<Element> element = new ArrayList<Element>();
				List<Capability> capList = new ArrayList<Capability>();
				if (it.hasNext()) {
					while (it.hasNext()) {
						Node node = (Node) it.next();
						if (node.getUserObject() instanceof Capability) {
							element.add(node);
							capList.add((Capability) node.getUserObject());
							count++;
						}
					}
				}
				if(count == 0){
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PERFORMANCEPORT_MAX));
					count=0;
					return;
				}
				if(count>35){
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PERFORMANCE_MAX));
					count=0;
					return;
				}
				count=0;
				view.setPortElement( element);
				
				if(element == null || element.size() == 0){
					JOptionPane.showMessageDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_PERFORMANCE_TYPE));
					return;
				}
				service = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
				List<HisPerformanceInfo> pList = new ArrayList<HisPerformanceInfo>();
				for(Capability cap : capList){
					HisPerformanceInfo condition = new HisPerformanceInfo();
					condition.setStartTime(view.getStartTime());
					condition.setPerformanceEndTime(view.getReadEndTime());
					condition.setSiteId(filter.getSiteInsts().get(0));
					condition.setMonitor(filter.getMonitorCycle().getValue());
					List<HisPerformanceInfo> list = this.filterPerformance(service.selectPerformanceValue(condition, view.getCode(), cap));
					if(list != null){
						pList.addAll(list);
					}
				}
//				List<HisPerformanceInfo> list = this.filterPerformance(service.selectByPage(1, 0, filter, filter.getSiteInsts(), 30000));
				if(this.cmbShowType.getSelectedIndex() == 1){
					// 表格
					DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
					this.dispose();
					new PerformanceShowDialog(pList);
				}else{
					// 图形
					view.bingData(pList);
					this.dispose();
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	public List<HisPerformanceInfo> filterPerformance(List<HisPerformanceInfo> list) {
		List<HisPerformanceInfo> performList = new ArrayList<HisPerformanceInfo>();
		ControlKeyValue ck = (ControlKeyValue) this.cmbMonitorObj.getSelectedItem();
		Object obj = ck.getObject();
		this.view.setObject(obj);
		for(HisPerformanceInfo info : list)
		if(obj instanceof PortInst){
			PortInst port = (PortInst) obj;
			if(info.getObjectType() == EObjectType.PORT && info.getSiteId() == port.getSiteId() && info.getObjectId() == port.getNumber()){
				performList.add(info);
			}
		}else if(obj instanceof Tunnel){
			Tunnel tunnel = (Tunnel) obj;
			if(info.getObjectType() == EObjectType.TUNNEL){
				for(Lsp lsp : tunnel.getLspParticularList()){
					if((lsp.getASiteId() == info.getSiteId() && lsp.getAtunnelbusinessid() == info.getObjectId()) ||
							lsp.getZSiteId() == info.getSiteId() && lsp.getZtunnelbusinessid() == info.getObjectId()){
						performList.add(info);
						break;
					}
				}
			}
		}else if(obj instanceof PwInfo){
			PwInfo pw = (PwInfo) obj;
			if((pw.getASiteId() == info.getSiteId() && pw.getApwServiceId() == info.getObjectId()) ||
					(pw.getZSiteId() == info.getSiteId() && pw.getZpwServiceId() == info.getObjectId())){
				performList.add(info);
			}
		}
		return performList;
	}
	
	/**
	 * 查看界面数据是否选择完整,完整返回true,不完整返回false
	 */
	private boolean checkIsFull() {
		if(this.cmbSite.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, "请先新建网元");
			return false;
		}
		if(this.cmbPerformType.getSelectedIndex() == 0){
			DialogBoxUtil.errorDialog(this, "请选择监控对象类型");
			return false;
		}
		if(this.cmbMonitorObj.getSelectedItem() == null){
			DialogBoxUtil.errorDialog(this, "请先新建监控对象");
			return false;
		}
		if(this.selectEndTime.getSelectedIndex() == 0){
			DialogBoxUtil.errorDialog(this, "请选择结束时间");
			return false;
		}
		return true;
	}


	/**
	 *  获取当前选择的过滤对象
	 */
	private HisPerformanceFilter getFilter() throws Exception {
		HisPerformanceFilter filter = new HisPerformanceFilter();
		//根据网元查询
		List<Integer> siteIdList = new ArrayList<Integer>();
		siteIdList.add(Integer.parseInt(((ControlKeyValue)this.cmbSite.getSelectedItem()).getId()));
		filter.setSiteInsts(siteIdList);
		filter.setObjectType(EObjectType.SITEINST);
		// 添加性能类型条件
		ControlKeyValue conType = (ControlKeyValue) this.cmbPerformType.getSelectedItem();
		int index = Integer.parseInt(conType.getId());
		// 添加性能类型条件
		Iterator it = treeBox.getSelectionModel().selection();
		if (it.hasNext()) {
			StringBuilder strBuilder = new StringBuilder();
			StringBuilder strTypeBuilder = new StringBuilder();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.getUserObject() instanceof Capability) {
					Capability capability = (Capability) node.getUserObject();
					filter.getCapabilityIdList().add(Integer.valueOf(capability.getId()));
					filter.getCapabilityNameList().add(capability.getCapabilitydesc());
					strBuilder.append(capability.getCapabilitytype()).append(",");
					strTypeBuilder.append(capability.getCapabilitydesc()).append(",");
				}
			}
			String str = strBuilder.toString();
			str = str.substring(0, str.length() - 1);
			filter.setTypeStr(str);
			str = strTypeBuilder.toString();
			str = str.substring(0, str.length() - 1);
		}
		
//		StringBuilder strTypeBuilder = new StringBuilder();
		if(index == 1 || index == 2){
			//端口 || 段
			String portType = "NNI";
			if(index == 1){
				PortInst port = (PortInst) ((ControlKeyValue) this.cmbMonitorObj.getSelectedItem()).getObject();
				portType = port.getPortType();
			}
			if(portType.equals("NNI") || portType.equals("UNI")){
//				strTypeBuilder.append("PORT");//性能类型
			}
		}else if(index == 3 || index == 4){
			//tunnel || pw
//			strTypeBuilder.append("TMP/TMC");//性能类型
		}
		//监控周期
		if (this.cmbCycle.getSelectedIndex() == 1) {
			filter.setMonitorCycle(EMonitorCycle.HOUR24);
		} else {
			filter.setMonitorCycle(EMonitorCycle.MIN15);
		}
		// 获取时间段
		String value="";
		if(selectEndTime.getSelectedItem().equals(newTime)){
			value=7+"";
		}else{
			ControlKeyValue controlKeyValue=(ControlKeyValue)selectEndTime.getSelectedItem();
			value=((Code)controlKeyValue.getObject()).getCodeValue();
		}
		view.setCode(Integer.parseInt(value));
		return filter;
	}
	
}