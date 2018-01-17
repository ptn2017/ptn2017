package com.nms.ui.ptn.performance.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import twaver.DataBoxSelectionEvent;
import twaver.DataBoxSelectionListener;
import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.VisibleFilter;
import twaver.list.TList;

import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.system.code.Code;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.NeTreePanel;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.model.HisPerformanceFilter;

public class HisPerformanceFilterDialog extends PtnDialog {

	private static final long serialVersionUID = 3225684618653439284L;
	private PtnButton confirm;
	private JButton cancel;
	private JButton clear;
	private JLabel lblTitle;
	private JLabel lblTaskObj;
	private JScrollPane typePane;
	private JLabel lblPerforType;
	private TDataBox typeBox;
	private TList tlType;
	private JLabel lblCycle;
	private JRadioButton rb15min;
	private JRadioButton rb24hour;
	private JRadioButton rb50m;
	private JRadioButton rb10min;
	private ButtonGroup group;
	private String filterInfo;
	private JLabel lblObjectType;
	private JComboBox cbObjectType;
	private JLabel filterZero;
	private JCheckBox filterZeroBox;
	private JLabel endTime;
	private JComboBox selectEndTime;
	private ControlKeyValue newTime;
	private String startTime;
	private String readEndTime;
	// 性能类别
	private JCheckBox cbType;
	private JScrollPane treePane;
	private TList tlist;
	private TDataBox treeBox;
	private JLabel lblDesc;
	private JCheckBox cbPerType;

	private Map<String, List<Capability>> portMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> tmsMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> tmpMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> ethMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> llidMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> mplsMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> pdhMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> phyMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> ponMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> pwtdmMap = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> stm1Map = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> tvc12Map = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> Map1731 = new HashMap<String, List<Capability>>();
	private Map<String, List<Capability>> allMap = new HashMap<String, List<Capability>>();
	private List<Capability> portList = new ArrayList<Capability>();
	private List<Capability> tmsList = new ArrayList<Capability>();
	private List<Capability> tmpList = new ArrayList<Capability>();
	private List<Capability> ethList = new ArrayList<Capability>();
	private List<Capability> llidList = new ArrayList<Capability>();
	private List<Capability> mplsList = new ArrayList<Capability>();
	private List<Capability> pdhList = new ArrayList<Capability>();
	private List<Capability> phyList = new ArrayList<Capability>();
	private List<Capability> ponList = new ArrayList<Capability>();
	private List<Capability> pwtdList = new ArrayList<Capability>();
	private List<Capability> stm1List = new ArrayList<Capability>();
	private List<Capability> vc12List = new ArrayList<Capability>();
	private List<Capability> list1731 = new ArrayList<Capability>();
	private JPanel buttonPanel;
	private NeTreePanel neTreePanel = null; // 网元树panel

	public HisPerformanceFilterDialog() {
		this.setModal(true);
		init();
	}

	private void init() {
		try {
			initComponents();
			setLayout();
			initData();
			addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initData() {
		selectEndTime.setSelectedIndex(0);
		initType();
	}

	/**
	 * 验证输入数据的正确性
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public boolean validateParams() throws Exception {
		if (!this.neTreePanel.verifySelect()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_OBJ));
			return false;
		}
		Iterator it = typeBox.getSelectionModel().selection();
		if (!it.hasNext()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_PERFORMANCE_TYPE));
			return false;
		}
		it = treeBox.getSelectionModel().selection();
		if (!it.hasNext()) {
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PERFORMANCE_TYPE));
			return false;
		}
		if (!rb15min.isSelected() && !rb24hour.isSelected() && !rb10min.isSelected() && !rb50m.isSelected()) {
			JOptionPane.showMessageDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_MONITORING_PERIOD));
			return false;
		}
		return true;
	}

	// 获取当前对话框的过滤对象
	@SuppressWarnings("rawtypes")
	public HisPerformanceFilter get() throws Exception {
		HisPerformanceFilter filter = new HisPerformanceFilter();
		// 如果界面选择的是网元类型 把tree上选中的网元加载到过滤条件中
		if (cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.NET_BASE))) {
			// 根据网元查询当前性能值
			filter.setSiteInsts(this.neTreePanel.getPrimaryKeyList("site"));
			filter.setObjectType(EObjectType.SITEINST);
			filter.setIsCardOrSite(1);
		} else if (cbObjectType.getSelectedItem().equals(ResourceUtil.srcStr(StringKeysObj.BOARD))) {
			filter.setSlotInsts(this.neTreePanel.getPrimaryKeyList("slot"));
			filter.setObjectType(EObjectType.SLOTINST);
			filter.setIsCardOrSite(2);
		}
		// 添加性能类型条件
		Iterator it = typeBox.getSelectionModel().selection();
		if (it.hasNext()) {
			StringBuilder strTypeBuilder = new StringBuilder();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.getUserObject() instanceof Capability) {
					Capability capability = (Capability) node.getUserObject();
					filter.getCapabilityIdList().add(Integer.valueOf(capability.getId()));
					strTypeBuilder.append(capability.getCapabilitytype()).append(",");
				}
			}
			String str = strTypeBuilder.toString();
			str = str.substring(0, str.length() - 1);
			filter.setTypeStr(str);
		}

		// 添加性能类别条件
		it = treeBox.getSelectionModel().selection();
		if (it.hasNext()) {
			StringBuilder strTypeBuilder = new StringBuilder();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node.getUserObject() instanceof Capability) {
					Capability capability = (Capability) node.getUserObject();
					filter.getCapabilityNameList().add(capability.getCapabilitydesc());
					strTypeBuilder.append(capability.getCapabilitydesc()).append(",");
				}
			}
			String str = strTypeBuilder.toString();
			str = str.substring(0, str.length() - 1);
		}

		if (rb24hour.isSelected()) {
			filter.setMonitorCycle(EMonitorCycle.HOUR24);
		} else if (rb15min.isSelected()) {
			filter.setMonitorCycle(EMonitorCycle.MIN15);
		} else if(rb50m.isSelected()){
			filter.setMonitorCycle(EMonitorCycle.M50);
		} else if(rb10min.isSelected()){
			filter.setMonitorCycle(EMonitorCycle.MIN10);
		} else {
			filter.setMonitorCycle(null);
		}
		if (filterZeroBox.isSelected()) {
			filter.setFiterZero(0);
		} else {
			filter.setFiterZero(1);
		}
		ControlKeyValue modekey_broad = (ControlKeyValue) this.selectEndTime.getSelectedItem();
		Code code = (Code) (modekey_broad.getObject());
		if (code == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				filter.setStartTime(sdf.parse(startTime).getTime());
				filter.setEndTime(sdf.parse(readEndTime).getTime());
			} catch (ParseException e) {
				ExceptionManage.dispose(e, this.getClass());
			}
		} else {
			// 任何时间
			if (code.getCodeValue().equals("0")) {
				filter.setStartTime(0);
				filter.setEndTime(new Date().getTime());
			}
			// 近1个小时
			else if (code.getCodeValue().equals("1")) {
				long nowTime = new Date().getTime();
				filter.setStartTime(nowTime - 60 * 60 * 1000);
				filter.setEndTime(nowTime);
			}
			// 近12个小时
			else if (code.getCodeValue().equals("2")) {
				long nowTime = new Date().getTime();
				filter.setStartTime(nowTime - 12 * 60 * 60 * 1000);
				filter.setEndTime(nowTime);
			}
			// 近24个小时
			else if (code.getCodeValue().equals("3")) {
				long nowTime = new Date().getTime();
				filter.setStartTime(nowTime - 24 * 60 * 60 * 1000);
				filter.setEndTime(nowTime);
			}
			// 近7个天
			else if (code.getCodeValue().equals("4")) {
				long nowTime = new Date().getTime();
				filter.setStartTime(nowTime - 7 * 24 * 60 * 60 * 1000);
				filter.setEndTime(nowTime);
			}
			// 近30天
			else if (code.getCodeValue().equals("5")) {
				long nowTime = new Date().getTime();
				filter.setStartTime(nowTime - 30L * 24 * 60 * 60 * 1000);
				filter.setEndTime(nowTime);
			}
			// //自定义
			// if (code.getCodeValue().equals("6")) {
			// filter.setStartTime(Long.valueOf(startTime));
			// filter.setEndTime(Long.valueOf(readEndTime));
			// }
		}
		// filter.setFiterTime(Integer.parseInt(modekey_broad.getId()));

		return filter;
	}

	/*
	 * 初始化性能类型
	 */
	private void initType() {
		CapabilityService_MB service = null;
		List<Capability> perforTypeList = null;
		List<Capability> perforList = null;
		try {
			service = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			perforList = new ArrayList<Capability>();
			perforTypeList = service.select();
			perforList.addAll(perforTypeList);
			perforTypeList = removeRepeatedType(perforTypeList, 1);
			perforList = removeRepeatedType(perforList, 2);
			// 增加性能类别
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
					if (type.getCapabilitytype().equalsIgnoreCase("PORT")) {
						portList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("TMP/TMC")) {
						tmpList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("TMS")) {
						tmsList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("ETH")) {
						ethList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("LLID")) {
						llidList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("MPLS")) {
						mplsList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("PDH")) {
						pdhList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("PHY")) {
						phyList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("PON")) {
						ponList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("PWTDM")) {
						pwtdList.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("STM1")) {
						stm1List.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("VC12")) {
						vc12List.add(type);
					} else if (type.getCapabilitytype().equalsIgnoreCase("1731")) {
						list1731.add(type);
					}
				}
			}
			allMap.put("ALL", perforList);
			portMap.put("PORT", portList);
			tmsMap.put("TMS", tmsList);
			tmpMap.put("TMP/TMC", tmpList);
			ethMap.put("ETH", ethList);
			llidMap.put("LLID", llidList);
			mplsMap.put("MPLS", mplsList);
			pdhMap.put("PDH", pdhList);
			phyMap.put("PHY", phyList);
			ponMap.put("PON", ponList);
			pwtdmMap.put("PWTDM", pwtdList);
			stm1Map.put("STM1", stm1List);
			tvc12Map.put("VC12", vc12List);
			Map1731.put("1731", list1731);
			// 增加性能类型
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
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	private void addListener() {

		selectEndTime.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue todTransmissionTimeTypekey_broad = (ControlKeyValue) selectEndTime.getSelectedItem();
					Code code = (Code) (todTransmissionTimeTypekey_broad.getObject());
					if (code != null && code.getCodeValue().equals("6")) {
						TimeWindow timewndow = new TimeWindow();
						UiUtil.showWindow(timewndow, 300, 200);
						startTime = timewndow.getStartTimeText().getText();
						readEndTime = timewndow.getEndTimeText().getText();
						String addItems = ResourceUtil.srcStr(StringKeysTip.START) + startTime + ResourceUtil.srcStr(StringKeysTip.END) + readEndTime;
						if (newTime != null) {
							selectEndTime.removeItem(newTime);
						}
						newTime = new ControlKeyValue("7", addItems);
						selectEndTime.addItem(newTime);
						selectEndTime.setSelectedItem(newTime);
					}
				}
			}
		});

		// 性能类别全选复选框
		cbPerType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (cbPerType.isSelected()) {
					treeBox.selectAll();
				} else {
					treeBox.getSelectionModel().clearSelection();
//					cbType.setSelected(false);
				}
			}
		});

		typeBox.getSelectionModel().addDataBoxSelectionListener(new DataBoxSelectionListener() {
			@Override
			public void selectionChanged(DataBoxSelectionEvent e) {
				// TODO Auto-generated method stub
				Iterator it = e.getBoxSelectionModel().selection();
				treeBox.clear();
				boolean fals = it.hasNext();
				// 增加性能类别
				if (fals) {
					while (it.hasNext()) {
						Node node = (Node) it.next();
						if (node.getDisplayName().equalsIgnoreCase("PORT")) {
							List<Capability> perforList = portMap.get("PORT");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("TMP/TMC")) {
							List<Capability> perforList = tmpMap.get("TMP/TMC");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("TMS")) {
							List<Capability> perforList = tmsMap.get("TMS");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("ETH")) {
							List<Capability> perforList = ethMap.get("ETH");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("LLID")) {
							List<Capability> perforList = llidMap.get("LLID");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("MPLS")) {
							List<Capability> perforList = mplsMap.get("MPLS");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("PDH")) {
							List<Capability> perforList = pdhMap.get("PDH");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("PHY")) {
							List<Capability> perforList = phyMap.get("PHY");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("PON")) {
							List<Capability> perforList = ponMap.get("PON");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("PWTDM")) {
							List<Capability> perforList = pwtdmMap.get("PWTDM");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("STM1")) {
							List<Capability> perforList = stm1Map.get("STM1");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("VC12")) {
							List<Capability> perforList = tvc12Map.get("VC12");
							initTreeData(perforList);
						} else if (node.getDisplayName().equalsIgnoreCase("1731")) {
							List<Capability> perforList = Map1731.get("1731");
							initTreeData(perforList);
						}
					}
				} else {
					// 增加性能类别
					List<Capability> perforList = allMap.get("ALL");
					if (perforList != null) {
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
				}
			}
		});

		// 性能类型全选复选框
		cbType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (cbType.isSelected()) {
					typeBox.selectAll();
					cbPerType.setSelected(false);
				} else {
					typeBox.getSelectionModel().clearSelection();
					cbPerType.setSelected(false);
				}
			}
		});

		// 设置性能类型是否可见
		tlType.addVisibleFilter(new VisibleFilter() {
			public boolean isVisible(Element element) {
				return true;
			}
		});

		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				HisPerformanceFilterDialog.this.dispose();
			}
		});

		// 清除按钮
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				HisPerformanceFilterDialog.this.clear();
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
	}

	// 清除面板信息
	private void clear() {
		this.neTreePanel.clear();
		typeBox.getSelectionModel().clearSelection();
		cbType.setSelected(false);
		filterZeroBox.setSelected(false);
		selectEndTime.setSelectedIndex(0);
		group.clearSelection();
	}

	private void initComponents() throws Exception {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		lblTaskObj = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_OBJ));
		lblObjectType = new JLabel(ResourceUtil.srcStr(StringKeysObj.OBJ_TYPE));
		cbObjectType = new JComboBox();
		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.NET_BASE));
//		cbObjectType.addItem(ResourceUtil.srcStr(StringKeysObj.BOARD));
		this.neTreePanel=new NeTreePanel(false, 2, null,false);
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
		lblCycle = new JLabel(ResourceUtil.srcStr(StringKeysObj.MONITORING_PERIOD));
		group = new ButtonGroup();
		rb15min = new JRadioButton("15" + ResourceUtil.srcStr(StringKeysObj.MINUTES));
		rb24hour = new JRadioButton("24" + ResourceUtil.srcStr(StringKeysObj.HOURS));
		rb50m = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_50_M));
		rb10min = new JRadioButton(ResourceUtil.srcStr(StringKeysObj.OBJ_10_MINUTES));
		group.add(rb15min);
		group.add(rb24hour);
		group.add(rb50m);
		group.add(rb10min);
		confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), true);
		cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		filterZero = new JLabel(ResourceUtil.srcStr(StringKeysBtn.PERFORMFITERZERO));
		filterZeroBox = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.PERFORMFITERZEROBOX));
		endTime = new JLabel(ResourceUtil.srcStr(StringKeysOperaType.BTN_OVER_TIME));
		selectEndTime = new JComboBox();
		buttonPanel = new javax.swing.JPanel();
		super.getComboBoxDataUtil().comboBoxData(this.selectEndTime, "endTime");
		lblDesc = new JLabel(ResourceUtil.srcStr(StringKeysObj.CAPABILITYDESC));

		treeBox = new TDataBox();
		treePane = new JScrollPane();
		tlist = new TList(treeBox);
		tlist.setTListSelectionMode(TList.CHECK_SELECTION);
		tlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		treePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		treePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treePane.setViewportView(tlist);
		cbPerType = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLSELECT));

	}

	private void setButtonLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40 };
		layout.columnWeights = new double[] { 0.2, 0.2 };
		layout.rowHeights = new int[] { 20 };
		layout.rowWeights = new double[] { 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.setConstraints(this.confirm, c);
		this.buttonPanel.add(confirm);
		c.gridy = 1;
		layout.setConstraints(this.cancel, c);
		this.buttonPanel.add(cancel);

	}

	private void setLayout() {
		this.setButtonLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 80, 40, 40, 230 };
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0.3 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
		layout.rowWeights = new double[] { 0, 0, 4, 0, 4, 0, 0, 4, 0, 0, 0, 0, 0, 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblObjectType, c);
		this.add(lblObjectType);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbObjectType, c);
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
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(this.neTreePanel, c);
		this.add(this.neTreePanel);

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
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(typePane, c);
		this.add(typePane);

		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbType, c);
		this.add(cbType);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(lblDesc, c);
		this.add(lblDesc);

		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(treePane, c);
		this.add(treePane);

		c.gridx = 1;
		c.gridy = 9;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 10);
		layout.addLayoutComponent(cbPerType, c);
		this.add(cbPerType);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(lblCycle, c);
		this.add(lblCycle);
		c.gridx = 1;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb15min, c);
		this.add(rb15min);
		c.gridx = 2;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb24hour, c);
		this.add(rb24hour);
		c.gridx = 3;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb50m, c);
		this.add(rb50m);
		c.gridx = 4;
		c.gridy = 10;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(rb10min, c);
		this.add(rb10min);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(filterZero, c);
		this.add(filterZero);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 11;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(filterZeroBox, c);
		this.add(filterZeroBox);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(endTime, c);
		this.add(endTime);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 12;
		c.gridheight = 1;
		c.gridwidth = 4;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(selectEndTime, c);
		this.add(selectEndTime);

		c.gridx = 0;
		c.gridy = 13;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 5, 10, 10);
		layout.addLayoutComponent(clear, c);
		this.add(clear);
		c.gridx = 4;
		c.gridy = 13;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(buttonPanel, c);
		this.add(buttonPanel);

	}

	public String getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(String filterInfo) {
		this.filterInfo = filterInfo;
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	/**
	 * 初始化性能类别
	 * 
	 * @param perforList
	 */
	private void initTreeData(List<Capability> perforList) {
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

	private List<Capability> removeRepeatedType(List<Capability> perforTypeList, int label) {
		List<Capability> NorepeatedCapability = perforTypeList;
		// 增加性能类型
		if (label == 1) {
			for (int i = 0; i < NorepeatedCapability.size() - 1; i++) {
				for (int j = NorepeatedCapability.size() - 1; j > i; j--) {
					if (NorepeatedCapability.get(j).getCapabilitytype().equals(NorepeatedCapability.get(i).getCapabilitytype())) {
						NorepeatedCapability.remove(j);
					}
				}
			}
		} else {
			// 增加性能类别
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
}
