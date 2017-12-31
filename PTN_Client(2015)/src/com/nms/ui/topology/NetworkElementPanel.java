﻿/*
 * This source code is part of TWaver 4.1
 *
 * Serva Software PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Copyright 2002 - 2011 Serva Software. All rights reserved.
 */

package com.nms.ui.topology;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import twaver.Dummy;
import twaver.Element;
import twaver.Group;
import twaver.Link;
import twaver.Node;
import twaver.PopupMenuGenerator;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.TSubNetwork;
import twaver.TUIManager;
import twaver.TView;
import twaver.TWaverConst;
import twaver.VisibleFilter;
import twaver.network.InteractionEvent;
import twaver.network.InteractionListener;
import twaver.network.NetworkToolBarFactory;
import twaver.network.TNetwork;
import twaver.network.background.Background;
import twaver.network.background.ColorBackground;
import twaver.network.background.ImageBackground;
import twaver.tree.ElementNode;
import twaver.tree.TTree;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.protect.LoopProtectInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Lsp;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.bean.system.code.Code;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.protect.WrappingProtectService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.util.Services;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnPanel;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.manager.util.TopologyUtil;
import com.nms.ui.ptn.basicinfo.NetWorkInfoPanel;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.action.LockTopoAction;
import com.nms.ui.topology.action.RefreshAction;
import com.nms.ui.topology.action.SavePlaceAction;
import com.nms.ui.topology.util.CreateAllTopo;
import com.nms.ui.topology.util.CreateElementUtil;

/**
 * 主拓扑
 * 
 * @author k.k
 * 
 */
public class NetworkElementPanel extends PtnPanel {

	private static final long serialVersionUID = -8992587141692586095L;
	private final Color COLOR_G = new Color(153, 204, 255); // 主拓扑背景颜色
	private static NetworkElementPanel networkelementpanel;

	private JSplitPane mainUI = new JSplitPane(); // 主拓扑分隔控件。左侧为网元树，右侧为拓扑界面
	private TDataBox box = new TDataBox(ResourceUtil.srcStr(StringKeysObj.PTN_MANAGE_SYSTEM)); // twaver数据对象
	private TNetwork network = new TNetwork(box); // 拓扑对象
	private TTree tree = new TTree(box); // tree对象
	private JComboBox cmbTopoType = new JComboBox(); // 拓扑视图类型下拉列表。包括 段拓扑、tunnel拓扑、pw拓扑、业务拓扑等
	private Background background = new ColorBackground(COLOR_G); // 拓扑背景对象
	/**
	 * 带有checkbox的tree的选择监听。 选择一个网元，拓扑呈现一个网元
	 */
	private TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath[] treePaths = null;
			TDataBox dataBox = null;
			ElementNode elementNode = null;
			SiteInst siteinst = null;
			CreateElementUtil createElementUtil=null;
			try {
				createElementUtil=new CreateElementUtil();
				treePaths = tree.getSelectionPaths();
				if (treePaths != null) {
					dataBox = new TDataBox();
					for (TreePath treePath : treePaths) {
							elementNode = (ElementNode) treePath.getLastPathComponent();
						if(elementNode.getElement().getClass().getSimpleName().equals("Node")){
							siteinst = (SiteInst) elementNode.getElement().getUserObject();
							dataBox.addElement(createElementUtil.createNode(siteinst, null));
						}
					}
					dataBox.setBackground(background);
					network.setDataBox(dataBox);
					refreshLinked(dataBox, getShowTopoType());

				} else {
					network.setDataBox(new TDataBox());
				}
			} catch (Exception e1) {
				ExceptionManage.dispose(e1, this.getClass());
			} finally {
				treePaths = null;
				dataBox = null;
				elementNode = null;
				siteinst = null;
				createElementUtil=null;
			}
		}
	};

	public NetworkElementPanel() {
		// 左侧面板，tree菜单
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		leftPanel.setPreferredSize(new Dimension(200, 1000));
		// 右侧面板，拓扑图
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(network, BorderLayout.CENTER);

		// 向主界面添加左右面板
		mainUI.setContinuousLayout(true);
		mainUI.add(rightPanel, JSplitPane.RIGHT);
		mainUI.add(leftPanel, JSplitPane.LEFT);

		// 初始化控件
		tree.setEnsureVisibleOnSelected(true);
		network.setEnsureVisibleOnSelected(true);
		box.setBackground(background);
		
		// 设置布局，并且把主控件加载进去
		this.setLayout(new BorderLayout());
		this.add(mainUI, BorderLayout.CENTER);

		try {
			// 默认咱开一级tree
			tree.expand(1);
			this.toolbarButton();
			showTopo(false);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

		// 过滤点tree上的link元素
		tree.addVisibleFilter(new VisibleFilter() {
			@Override
			public boolean isVisible(Element element) {
				if (element instanceof Link)
					return false;
				else
					return true;
			}
		});

		// 设置拓扑上的右键菜单
		network.setPopupMenuGenerator(new PopupMenuGenerator() {
			public JPopupMenu generate(final TView tview, MouseEvent mouseEvent) {
				JPopupMenu menu = null;
				try {
					menu = showMenu(tview);
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				return menu;
			}
		});

		// 设置拓扑上的双击事件
		network.addInteractionListener(new InteractionListener() {
			public void interactionPerformed(InteractionEvent event) {
				try {
					MouseEvent mouseEvent = event.getMouseEvent();
					if (mouseEvent.getClickCount() == 2) {
						netWorkDuobleClick();
					} else if (mouseEvent.getClickCount() == 1) {
						netWorkSingClick();
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		});
		
		//添加树节点的单击事件
		tree.addTreeNodeClickedActionListener(new ActionListener() { 
		@Override
		public void actionPerformed(ActionEvent e) {
			ElementNode elementNode = null;
			try {
				if(e.getSource() instanceof ElementNode){
					elementNode = (ElementNode) e.getSource();
					// 如果是网元的话。加载网元属性配置界面
					if (elementNode.getElement().getUserObject() instanceof SiteInst) {
						Field filed = (Field)elementNode.getElement().getParent().getUserObject();
						if(filed.getNetWorkId() == 0)
						{
							ConstantUtil.fieldId = netWordIdByParentId(filed.getParentId());
						}else
						{

							ConstantUtil.fieldId = filed.getNetWorkId();	
						}
					}else if(elementNode.getElement().getUserObject() instanceof NetWork){
						ConstantUtil.fieldId = ((NetWork)elementNode.getElement().getUserObject()).getNetWorkId();
					}else if(elementNode.getElement().getUserObject() instanceof Field){
						
						Field filed = (Field)elementNode.getElement().getUserObject();
						if(filed.getNetWorkId() == 0)
						{
							ConstantUtil.fieldId = netWordIdByParentId(filed.getParentId());
						}else
						{

							ConstantUtil.fieldId = filed.getNetWorkId();	
						}
					}
				}
			} catch (Exception e2) {
				ExceptionManage.dispose(e2, NetworkElementPanel.class);
			} finally {
				elementNode = null;
			}
		}

	});
		
		// 添加树节点的双击事件
		tree.addElementNodeDoubleClickedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ElementNode elementNode = null;
				SiteInst siteInst = null;
				try {
					if(e.getSource() instanceof ElementNode){
						elementNode = (ElementNode) e.getSource();
						// 如果是网元的话。加载网元属性配置界面
						if (elementNode.getElement().getUserObject() instanceof SiteInst) {
							siteInst = (SiteInst) elementNode.getElement().getUserObject();
							ConstantUtil.siteId = siteInst.getSite_Inst_Id();
							Ptnf.getPtnf().mainTabPanel(ConstantUtil.jTabbedPane, siteInst.getCellId() + "/" + ResourceUtil.srcStr(StringKeysTab.TAB_NETWORK), new NetWorkInfoPanel());
						}
					}
				} catch (Exception e2) {
					ExceptionManage.dispose(e2, NetworkElementPanel.class);
				} finally {
					elementNode = null;
					siteInst = null;
				}
			}
		});
	}
	
	private int netWordIdByParentId(int fieldId)
	{
		FieldService_MB fieldService = null;
		List<Field> fieldList = null;
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = fieldService.selectByFieldId(fieldId);
			if(fieldList != null && !fieldList.isEmpty())
			{
				return fieldList.get(0).getNetWorkId();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(fieldService);
			fieldList = null;
		}
		return 0;
	}
	/**
	 * 
	 * 主拓扑双击事件
	 */
	private void netWorkDuobleClick()
	{
		Element element = network.getLastSelectedElement();
		// 双击的如果为域。记录域主键
		if (element instanceof SubNetwork) {
			NetWork netWork = (NetWork) element.getUserObject();
			if(netWork.getBackgroundPath() != null){
				SubNetwork sbNetwork= (SubNetwork) element;
				sbNetwork.setBackground(new ImageBackground(netWork.getBackgroundPath()));
			}
			ConstantUtil.fieldId = netWork.getNetWorkId();
		} else if (element instanceof Group) {

		} else if (element instanceof Node) {
			// 双击的如果是网元，打开网元配置界面，并且记录网元主键
			if (null != element.getUserObject()) {
				try {
					SiteInst siteInst = (SiteInst) element.getUserObject();
					ConstantUtil.siteId = siteInst.getSite_Inst_Id();
					Ptnf.getPtnf().mainTabPanel(ConstantUtil.jTabbedPane, siteInst.getCellId() + "/" + ResourceUtil.srcStr(StringKeysTab.TAB_NETWORK), new NetWorkInfoPanel());
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
			}
		}
	}
	
	/**
	 * 
	 * 主拓扑单击事件
	 */
	private void netWorkSingClick()
	{
		Element element = network.getLastSelectedElement();
		// 双击的如果为域。记录域主键
		if(element instanceof Link)
		{
			if(element!=null&&element instanceof Link){
				if(element.getUserObject() instanceof Segment &&element.getBusinessObject()== null ){
					TUIManager.registerAttachment("SegmenttopoTitle", ShowAttarmentTable.class,1, (int) element.getX(), (int) element.getY());
					element.putClientProperty("listKeyModel", (Segment)element.getUserObject());
					element.addAttachment("SegmenttopoTitle");
				}
			}
		}
	}
	
	public static NetworkElementPanel getNetworkElementPanel() {
		if (networkelementpanel == null) {
			networkelementpanel = new NetworkElementPanel();
		}
		return networkelementpanel;
	}

	public TDataBox getBox() {
		return box;
	}

	/**
	 * 获取拓扑中选中的元素
	 * 
	 * @return 选中的元素
	 */
	public Element getSelectElement() {
		return this.network.getDataBox().getLastSelectedElement();
	}

	/**
	 * 设置拓扑的快捷按钮
	 * 
	 * @throws Exception
	 */
	private void toolbarButton() throws Exception {
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			// 默认按钮
			String[] ids = new String[] { "Selection", "Magnifier", "Pan", "ZoomIn", "ZoomOut", TWaverConst.TOOLBAR_ZOOMTOOVERVIEW, "ZoomToRectangle", "ZoomReset", "OverView", "FullScreen" };

			JToolBar toolBar = NetworkToolBarFactory.getToolBar(ids, network);

			// 添加保存位置按钮
			topologyUtil.createTopoButton("/com/nms/ui/images/topo/topoPlace.png", new SavePlaceAction(network), toolBar, ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE_PLACE));
			// 添加刷新拓扑按钮
			topologyUtil.createTopoButton("/com/nms/ui/images/topo/topoRefresh.png", new RefreshAction(), toolBar, ResourceUtil.srcStr(StringKeysBtn.BTN_REFRESH));
			// 添加锁定拓扑按钮
			topologyUtil.createTopoButton("/com/nms/ui/images/topo/lock.png", new LockTopoAction(), toolBar, ResourceUtil.srcStr(StringKeysBtn.LOCK_UPDATE));
			// 添加拓扑类型按钮下拉菜单
			toolBar.add(this.getTopoType());
			network.setToolbar(toolBar);
			RoleRoot roleRoot = new RoleRoot();
			roleRoot.setItemEnbale(toolBar, RootFactory.TOPOLOGY_MANAGE);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	public TNetwork getNetwork() {
		return network;
	}
	public TTree getTree() {
		return tree;
	}

	/**
	 * 界面上的拓扑类型下拉表绑定数据
	 * 
	 * @return
	 * @throws Exception
	 */
	private JComboBox getTopoType() throws Exception {
		Dimension d = null;
		try {
			d = new Dimension(230, 21);
			this.cmbTopoType.setMaximumSize(d);
			this.cmbTopoType.setMaximumSize(d);
			super.getComboBoxDataUtil().comboBoxData(cmbTopoType, "TOPOTYPE");
			this.cmbTopoType.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					topoTypeItemStateChanged(evt);
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			d = null;
		}
		return this.cmbTopoType;
	}

	/**
	 * 拓扑类型下拉列表值改变事件
	 * 
	 * @param evt
	 */
	private void topoTypeItemStateChanged(java.awt.event.ItemEvent evt) {
		if (evt.getStateChange() == 1) {
			try {
				showTopo(true);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}
		}
	}

	/**
	 * 根据拓扑类型的选择显示不同拓扑
	 * 
	 * @param flag
	 *            是否刷新告警
	 * @throws Exception
	 */
	public void showTopo(boolean flag) throws Exception {
		String topoType = null;
		TSubNetwork subNetwork = null;
		CreateAllTopo createAllTopo=null;
		TopologyUtil topologyUtil = null;
		CurAlarmService_MB curAlarmService = null;
		try {
			createAllTopo=new CreateAllTopo(this.box,2);
			// 拓扑视图当前显示的域
			subNetwork = this.network.getCurrentSubNetwork();
			topoType = this.getShowTopoType();
			this.box.clear();
			if (topoType.equals("SEGMENT")) {
				createAllTopo.createTopo(true,false);
				showSegmentTopo(topoType);
				// 刷新后，选中刷新前选中的域
				this.selectSubNetwork(subNetwork);
			} else {
				createAllTopo.createTopo(false,false);
				showCheckBoxTree();
			}
			if (flag) {
				curAlarmService = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
				topologyUtil = new TopologyUtil();
				topologyUtil.updateSiteInstAlarm(curAlarmService);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally
		{
			 topoType = null;
			 subNetwork = null;
			 createAllTopo=null;
			 topologyUtil = null;
			 UiUtil.closeService_MB(curAlarmService);
			 
		}
	}

	/**
	 * 刷新拓扑时，自动选中上次选中的拓扑
	 * 
	 * @Exception 异常对象
	 */
	private void selectSubNetwork(TSubNetwork subNetwork) {

		if (null != subNetwork) {
			NetWork netWork = (NetWork) subNetwork.getUserObject();

			for (int i = 0; i < this.box.getAllElements().size(); i++) {
				Element element = (Element) this.box.getAllElements().get(i);
				if (element instanceof SubNetwork) {
					SubNetwork subNetwork_select = (SubNetwork) element;
					NetWork field_select = (NetWork) subNetwork_select.getUserObject();
					if (netWork.getNetWorkId() == field_select.getNetWorkId()) {
						this.network.setCurrentSubNetwork(subNetwork_select);
						if(field_select.getBackgroundPath() != null){
							subNetwork_select.setBackground(new ImageBackground(field_select.getBackgroundPath()));
						}
						break;
					}
				}
			}

		}

	}

	/**
	 * 显示带有checkbox的tree拓扑
	 * 
	 * @throws Exception
	 */
	private void showCheckBoxTree() throws Exception {
		try {
			// 给network赋一个新的数据对象。 因为要清空network 不清空tree
			network.setDataBox(new TDataBox());
			tree.expand(2);
			tree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);
			if (tree.getTreeSelectionListeners().length == 1) {
				tree.addTreeSelectionListener(treeSelectionListener);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 显示段类型拓扑
	 * 
	 * @param type
	 * @throws Exception
	 */
	private void showSegmentTopo(String type) throws Exception {
		try {
			// 设置tree的模式为默认，并且移除tree的事件
			tree.expand(1);
			tree.setTTreeSelectionMode(TTree.DEFAULT_SELECTION);
			tree.removeTreeSelectionListener(treeSelectionListener);
			this.network.setDataBox(box);
			this.refreshLinked(box, type);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}


	/**
	 * 创建网元之间的link
	 * 
	 * @param box
	 *            数据源
	 * @param type
	 *            不同type 不同link
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void refreshLinked(TDataBox box, String type) throws Exception {
		List<Element> elements = null;
		HashMap<Integer, Node> nodeMap = null;
		Node node = null;
		try {
			elements = box.getAllElements();
			// 把所有的网元节点放入map中。 在之后的验证此link的两端节点是否在拓扑图中存在时用
			nodeMap = new HashMap<Integer, Node>();
			for (int j = 0; j < elements.size(); j++) {
				if (elements.get(j) instanceof Node && elements.get(j).getUserObject() instanceof SiteInst) {
					node = (Node) elements.get(j);
					nodeMap.put((Integer) ((SiteInst) node.getUserObject()).getSite_Inst_Id(), node);
				}
			}
			if ("SEGMENT".equals(type)) {
				this.createSegmentLink(box, nodeMap);
			} else if ("TUNNEL".equals(type)) {
				this.createTunnelLink(box, nodeMap);
			} else if ("PW".equals(type)) {
				this.createPwLink(box, nodeMap);
			} else if ("ELINE".equals(type)) {
				this.createElineLink(box, nodeMap);
			} else if ("CES".equals(type)) {
				this.createCesLink(box, nodeMap);
			} else if ("ETREE".equals(type)) {
				this.createEtreeLink(box, nodeMap);
			} else if ("ELAN".equals(type)) {
				this.createELanLink(box, nodeMap);
			}else if ("LOOP".equals(type)) {
				this.createLoopLink(box, nodeMap);
			}else if ("DUAL".equals(type)) {
				this.createDualLink(box, nodeMap);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elements = null;
			nodeMap = null;
			node = null;
		}
	}

	/**
	 * 创建段的link
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createSegmentLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {
		SegmentService_MB service = null;
		List<Segment> segmentList = null;
		try {
			service = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
			segmentList = service.selectNoOam();
			if (segmentList != null && segmentList.size() > 0) {
				for (Segment segment : segmentList) {
					Node aNode = nodeMap.get(segment.getASITEID());
					Node zNode = nodeMap.get(segment.getZSITEID());
					if (aNode != null && zNode != null) {
						box.addElement(this.createLink(aNode, zNode, "SEGMENT", segment));
					}
				}
			}
			//根据连接状态修改颜色
			drawLinkColorByStatus(segmentList, box);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			segmentList = null;
		}
	}

	private boolean linkSimilarWithSegment(Link link, Segment seg2) 
	{
		if (link.getUserObject() instanceof Segment)
		{
			Segment seg1 = (Segment) link.getUserObject();
			if ((seg1.getAPORTID() == seg2.getAPORTID() && seg1.getASITEID() == seg2.getASITEID()
				 && seg1.getZPORTID() == seg2.getZPORTID() && seg1.getZSITEID() == seg2.getZSITEID())
				|| (seg1.getAPORTID() == seg2.getZPORTID() 	&& seg1.getASITEID() == seg2.getZSITEID()
				&& seg1.getZPORTID() == seg2.getAPORTID() && seg1.getZSITEID() == seg2.getASITEID()))
				return true;
			} 
		    else if (link.getUserObject() instanceof Lsp) 
			{
				Lsp lspp = (Lsp) link.getUserObject();
				if ((lspp.getAPortId() == seg2.getAPORTID()	&& lspp.getASiteId() == seg2.getASITEID()
					&& lspp.getZPortId() == seg2.getZPORTID() && lspp.getZSiteId() == seg2.getZSITEID())
					|| (lspp.getAPortId() == seg2.getZPORTID()&& lspp.getASiteId() == seg2.getZSITEID()
					&& lspp.getZPortId() == seg2.getAPORTID() && lspp.getZSiteId() == seg2.getASITEID()))
					return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void drawLinkColorByStatus(List<Segment> segmentList, TDataBox box)throws Exception {
		SiteService_MB service = null;
		try {
			List<Element> elementList = box.getAllElements();
			service = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			TopologyUtil topologyUtil=new TopologyUtil();
			for (Segment obj : segmentList)
			{
				int aSiteID = obj.getASITEID();
				int zSiteID = obj.getZSITEID();
				//如果AZ两端都是连接状态即为绿色，否则就是灰色
				if(service.queryNeStatus(aSiteID) == 1 && service.queryNeStatus(zSiteID) == 1)
				{
					for (int i = elementList.size() - 1; i >= 0; i--) {
						Element element = elementList.get(i);
						if (element instanceof Link && linkSimilarWithSegment((Link)element, obj))
						{
							((Link) element).setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
							((Link) element).putLinkColor(Color.green);
							Segment segment = (Segment) element.getUserObject();
							topologyUtil.setLinkWidth(segment, ((Link) element));
							((Link) element).putLinkFlowingWidth(3);
							break;
						}
					}
				}
				else
				{
					for (int i = elementList.size() - 1; i >= 0; i--) 
					{
						Element element = elementList.get(i);
						if (element instanceof Link && linkSimilarWithSegment((Link)element, obj)) 
						{
							((Link) element).setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
							((Link) element).putLinkColor(Color.gray);
							Segment segment = (Segment) element.getUserObject();
							topologyUtil.setLinkWidth(segment, ((Link) element));
							((Link) element).putLinkFlowingWidth(3);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(service);
		}
	}
	
	/**
	 * 创建tunnel的link
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createTunnelLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnelList = null;
		List<Integer> siteList = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnelList = tunnelService.select();
			if (tunnelList != null && tunnelList.size() > 0) {

				for (Tunnel tunnel : tunnelList) {

					siteList = this.convertSite(tunnel);

					if (this.verificationLink(siteList, nodeMap)) {
						for (Lsp lspParticular : tunnel.getLspParticularList()) {
							Node aNode = nodeMap.get(lspParticular.getASiteId());
							Node zNode = nodeMap.get(lspParticular.getZSiteId());
							if (aNode != null && zNode != null) {
								box.addElement(this.createLink(aNode, zNode, "TUNNEL", tunnel));
							}
						}
					}
					// 如果是保护的tunnel。把保护tunnel创建到拓扑中
					if ("2".equals(UiUtil.getCodeById(Integer.parseInt(tunnel.getTunnelType())).getCodeValue())) {
						siteList = this.convertSite(tunnel.getProtectTunnel());
						if (this.verificationLink(siteList, nodeMap)) {
							for (Lsp lspParticular : tunnel.getProtectTunnel().getLspParticularList()) {
								Node aNode = nodeMap.get(lspParticular.getASiteId());
								Node zNode = nodeMap.get(lspParticular.getZSiteId());
								if (aNode != null && zNode != null) {
									box.addElement(this.createLink(aNode, zNode, "PROTECT", tunnel.getProtectTunnel()));
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(tunnelService);
			tunnelList = null;
			siteList = null;
		}
	}

	/**
	 * 创建pw的link
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createPwLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		PwInfoService_MB pwInfoService = null;
		List<PwInfo> pwInfoList = null;
		List<Integer> siteList = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwInfoList = pwInfoService.select();

			for (PwInfo pwInfo : pwInfoList) {
				siteList = this.convertSite(pwInfo.getPwId());

				if (this.verificationLink(siteList, nodeMap)) {
					Node aNode = nodeMap.get(pwInfo.getASiteId());
					Node zNode = nodeMap.get(pwInfo.getZSiteId());
					if (aNode != null && zNode != null) {
						box.addElement(this.createLink(aNode, zNode, "PW", pwInfo));
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			pwInfoList = null;
			siteList = null;
		}

	}

	/**
	 * 创建eline的link拓扑图
	 * 
	 * @throws Exception
	 */
	private void createElineLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		List<ElineInfo> elineInfoList = null;
		ElineInfoService_MB elineService = null;
		List<Integer> siteList = null;
		try {
			elineService = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);
			elineInfoList = elineService.select();

			for (ElineInfo elineInfo : elineInfoList) {
				siteList = this.convertSite(elineInfo.getPwId());

				if (this.verificationLink(siteList, nodeMap)) {
					Node aNode = nodeMap.get(elineInfo.getaSiteId());
					Node zNode = nodeMap.get(elineInfo.getzSiteId());
					if (aNode != null && zNode != null) {
						box.addElement(this.createLink(aNode, zNode, "ELINE", elineInfo));
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(elineService);
			elineInfoList = null;
			siteList = null;
		}
	}

	/**
	 * 创建ces的link拓扑图
	 * 
	 * @throws Exception
	 */
	private void createCesLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {
		List<CesInfo> cesInfoList = null;
		CesInfoService_MB cesInfoService = null;
		List<Integer> siteList = null;
		try {
			cesInfoService = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			cesInfoList = cesInfoService.select();

			for (CesInfo cesInfo : cesInfoList) {
				siteList = this.convertSite(cesInfo.getPwId());

				if (this.verificationLink(siteList, nodeMap)) {
					Node aNode = nodeMap.get(cesInfo.getaSiteId());
					Node zNode = nodeMap.get(cesInfo.getzSiteId());
					if (aNode != null && zNode != null) {
						box.addElement(this.createLink(aNode, zNode, "CES", cesInfo));
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			cesInfoList = null;
			UiUtil.closeService_MB(cesInfoService);
			siteList = null;
		}
	}

	/**
	 * 创建etree的link拓扑
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createEtreeLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		Map<Integer, List<EtreeInfo>> etreeInfoMap = null;
		EtreeInfoService_MB etreeService = null;
		Set<Integer> serviceIds = null; // serviceinfo表中的serviceId字段 一个id代表一条etree
		List<EtreeInfo> etreeInfoList = null;
		List<Integer> pwIdList = null;
		try {
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			etreeInfoMap = etreeService.select();
			serviceIds = etreeInfoMap.keySet();

			for (Integer serviceid : serviceIds) {
				etreeInfoList = etreeInfoMap.get(serviceid);
				pwIdList = new ArrayList<Integer>();
				for (EtreeInfo etreeInfo : etreeInfoList) {
					pwIdList.add(etreeInfo.getPwId());
				}
				this.createElanOrEtree(pwIdList, box, nodeMap, etreeInfoList, "ETREE");
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfoMap = null;
			UiUtil.closeService_MB(etreeService);
			serviceIds = null;
			etreeInfoList = null;
			pwIdList = null;
		}
	}

	/**
	 * 创建etree的link拓扑
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createELanLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		Map<Integer, List<ElanInfo>> elanInfoMap = null;
		ElanInfoService_MB elanInfoService = null;
		Set<Integer> serviceIds = null; // serviceinfo表中的serviceId字段 一个id代表一条elan
		List<ElanInfo> elanInfoList = null;
		List<Integer> pwIdList = null;
		try {
			elanInfoService = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);
			elanInfoMap = elanInfoService.select();
			serviceIds = elanInfoMap.keySet();

			for (Integer serviceid : serviceIds) {
				elanInfoList = elanInfoMap.get(serviceid);
				pwIdList = new ArrayList<Integer>();
				for (ElanInfo elanInfo : elanInfoList) {
					pwIdList.add(elanInfo.getPwId());
				}
				this.createElanOrEtree(pwIdList, box, nodeMap, elanInfoList, "ELAN");
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elanInfoMap = null;
			UiUtil.closeService_MB(elanInfoService);
			serviceIds = null; // serviceinfo表中的serviceId字段 一个id代表一条etree
			elanInfoList = null;
			pwIdList = null;
		}
	}

	
	/**
	 * 创建loop的link拓扑
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createLoopLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {
		List<LoopProtectInfo> loopProtectInfoList = null;
		WrappingProtectService_MB protectService = null;
		LoopProtectInfo info = null;
		try {						
			protectService = (WrappingProtectService_MB) ConstantUtil.serviceFactory.newService_MB(Services.WRAPPINGPROTECT);
			info = new LoopProtectInfo();
			loopProtectInfoList = protectService.select(info);
			for(LoopProtectInfo loopProtectInfo : loopProtectInfoList){	
				Node aNode = nodeMap.get(loopProtectInfo.getWestSegment().getASITEID());
				Node zNode = nodeMap.get(loopProtectInfo.getWestSegment().getZSITEID());
				if (aNode != null && zNode != null) {
					box.addElement(this.createLink(aNode, zNode, "LOOP", loopProtectInfo));
				}
			}			

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(protectService);
			info =null;
			loopProtectInfoList = null;
		}													
	}
	
			
	
	
	/**
	 * 创建dual的link拓扑
	 * 
	 * @param box
	 * @param nodeMap
	 * @throws Exception
	 */
	private void createDualLink(TDataBox box, HashMap<Integer, Node> nodeMap) throws Exception {

		Map<Integer, List<DualInfo>> dualInfoMap = null;
		DualInfoService_MB dualInfoService = null;
		Set<Integer> serviceIds = null; // serviceinfo表中的serviceId字段 一个id代表一条elan
		List<DualInfo> dualInfoList = null;
		List<Integer> pwIdList = null;
		try {
			dualInfoService = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);
			dualInfoMap = dualInfoService.select(0,0);
			serviceIds = dualInfoMap.keySet();

			for (Integer serviceid : serviceIds) {
				dualInfoList = dualInfoMap.get(serviceid);
				pwIdList = new ArrayList<Integer>();
				for (DualInfo dualInfo : dualInfoList) {
					pwIdList.add(dualInfo.getPwId());
				}
				this.createElanOrEtree(pwIdList, box, nodeMap, dualInfoList, "DUAL");
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			dualInfoMap = null;
			UiUtil.closeService_MB(dualInfoService);
			serviceIds = null; // serviceinfo表中的serviceId字段 一个id代表一条etree
			dualInfoList = null;
			pwIdList = null;
		}
	}
	/**
	 * 是否创建elan或者etree的拓扑。
	 * 
	 * @param pwIdList
	 *            elan或者etree所用到的pwid
	 * @param box
	 *            tdatabox
	 * @param nodeMap
	 * @param object
	 * @param type
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void createElanOrEtree(List<Integer> pwIdList, TDataBox box, HashMap<Integer, Node> nodeMap, Object object, String type) throws Exception {
		Object[] objects = null;
		List<PwInfo> pwinfoList = null;
		List<Integer> siteIdList = null;
		try {
			objects = this.convertSite(pwIdList);
			pwinfoList = (List<PwInfo>) objects[0];
			siteIdList = (List<Integer>) objects[1];

			// 验证成功后，遍历此elan用到的所有tunnel 并且创建tunnel的link
			if (this.verificationLink(siteIdList, nodeMap)) {
				for (PwInfo pwinfo : pwinfoList) {
					Node aNode = nodeMap.get(pwinfo.getASiteId());
					Node zNode = nodeMap.get(pwinfo.getZSiteId());
					if (aNode != null && zNode != null) {
						box.addElement(this.createLink(aNode, zNode, type, object));
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			objects = null;
			siteIdList = null;
		}
	}

	/**
	 * 创建LINK
	 * 
	 * @param aNode
	 * @param zNode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Link createLink(Node aNode, Node zNode, String objType, Object object) {
		TopologyUtil topologyUtil=new TopologyUtil();
		
		Link link = new Link();
		link.putLabelYOffset(-10);
		link.putLinkLabelRotatable(true);
		link.putLabelPosition(TWaverConst.POSITION_CENTER);
		link.putLabelColor(Color.WHITE);
		link.putLabelFont(new Font("Forte", Font.PLAIN, 18));
		link.setFrom(aNode);
		link.setTo(zNode);
		link.putLinkBundleExpand(false);

		if ("ETREE".equals(objType)) {
			List<EtreeInfo> etreeInfoList = (List<EtreeInfo>) object;
			link.putClientProperty(TWaverConst.PROPERTYNAME_LINK_STYLE, TWaverConst.LINK_STYLE_DASH_DOT);
			link.putLinkColor(Color.YELLOW);
			link.putLinkOutlineColor(Color.YELLOW);
			link.putLinkWidth(3);
			link.setToolTipText(etreeInfoList.get(0).getName());
			link.setUserObject(etreeInfoList);
		} else if ("ELAN".equals(objType)) {
			List<ElanInfo> elanInfoList = (List<ElanInfo>) object;
			link.putClientProperty(TWaverConst.PROPERTYNAME_LINK_STYLE, TWaverConst.LINK_STYLE_DASH_DOT);
			link.putLinkColor(Color.ORANGE);
			link.putLinkOutlineColor(Color.ORANGE);
			link.putLinkWidth(3);
			link.setToolTipText(elanInfoList.get(0).getName());
			link.setUserObject(elanInfoList);
		} else if ("CES".equals(objType)) {
			CesInfo cesInfo = (CesInfo) object;
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkColor(Color.GREEN);
			link.putLinkOutlineColor(Color.GREEN);
			link.putLinkWidth(3);
			link.setToolTipText(cesInfo.getName());
			link.setUserObject(cesInfo);
		} else if ("ELINE".equals(objType)) {
			ElineInfo elineInfo = (ElineInfo) object;
			link.putClientProperty(TWaverConst.PROPERTYNAME_LINK_STYLE, TWaverConst.LINK_STYLE_DASH_DOT);
			link.putLinkColor(Color.GREEN);
			link.putLinkOutlineColor(Color.GREEN);
			link.putLinkWidth(3);
			link.setToolTipText(elineInfo.getName());
			link.setUserObject(elineInfo);
		} else if ("PW".equals(objType)) {
			PwInfo pwInfo = (PwInfo) object;
			link.putLinkFlowing(true);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkFlowingColor(Color.GREEN);
			link.putLinkColor(Color.YELLOW);
			link.putLinkFlowingWidth(3);
			link.putLinkWidth(3);
			link.setToolTipText(pwInfo.getPwName());
			link.setUserObject(pwInfo);
		} else if ("TUNNEL".equals(objType)) {
			Tunnel tunnel = (Tunnel) object;
			link.putLinkFlowing(true);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkFlowingColor(Color.black);
			link.putLinkColor(Color.GREEN);
			link.putLinkFlowingWidth(5);
			link.putLinkWidth(5);
			link.setToolTipText(tunnel.getTunnelName());
			link.setUserObject(tunnel);
		} else if ("SEGMENT".equals(objType)) { // 段
			Segment segment = (Segment) object;
			link.setToolTipText(segment.getNAME());
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkColor(Color.green);
			link.putLinkOutlineColor(Color.BLACK);
			// link.putLinkWidth(3);
			try {
				topologyUtil.setLinkWidth(segment, link);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			}
			link.setUserObject(segment);
		} else if ("PROTECT".equals(objType)) { // 保护
			Tunnel tunnel = (Tunnel) object;
			link.putLinkFlowing(true);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkFlowingColor(Color.black);
			link.putLinkColor(Color.YELLOW);
			link.putLinkFlowingWidth(5);
			link.putLinkWidth(5);
			link.setToolTipText(tunnel.getTunnelName());
			link.setUserObject(tunnel);
		}else if ("LOOP".equals(objType)) { // 环网				
		    LoopProtectInfo loopProtectInfo = (LoopProtectInfo) object;
			link.putLinkFlowing(true);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkFlowingColor(Color.black);
			link.putLinkColor(Color.YELLOW);
			link.putLinkFlowingWidth(5);
			link.putLinkWidth(5);
			link.setToolTipText(loopProtectInfo.getName());
			link.setUserObject(loopProtectInfo);
		}else if ("DUAL".equals(objType)) {
			List<DualInfo> dualInfoList = (List<DualInfo>) object;
			link.putLinkFlowing(true);
			link.setLinkType(TWaverConst.LINE_TYPE_DEFAULT);
			link.putLinkFlowingColor(Color.black);
			link.putLinkColor(Color.YELLOW);
			link.putLinkFlowingWidth(5);
			link.putLinkWidth(5);
			link.setToolTipText(dualInfoList.get(0).getName());
			link.setUserObject(dualInfoList);
		}
		return link;
	}

	/**
	 * 验证siteid集合中的网元是否在拓扑上都存在。
	 * 
	 * @param siteIdList
	 * @param nodeMap
	 * @return 存在返回true 失败返回false
	 */
	private boolean verificationLink(List<Integer> siteIdList, HashMap<Integer, Node> nodeMap) {

		boolean flag = true;
		for (Integer siteid : siteIdList) {
			if (nodeMap.get(siteid) == null) {
				flag = false;
				break;
			}
		}
		return flag;

	}

	/**
	 * 获取此tunnel中所有siteid
	 */
	private List<Integer> convertSite(Tunnel tunnel) throws Exception {
		List<Integer> list = null;
		try {
			list = new ArrayList<Integer>();

			for (Lsp lspParticular : tunnel.getLspParticularList()) {
				if (!list.contains(lspParticular.getASiteId())) {
					list.add(lspParticular.getASiteId());
				}
				if (!list.contains(lspParticular.getZSiteId())) {
					list.add(lspParticular.getZSiteId());
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return list;
	}

	/**
	 * 根据pwID获取此pw下的所有siteId
	 * 
	 * @param pwid
	 * @return siteId
	 * @throws Exception
	 */
	private List<Integer> convertSite(int pwid) throws Exception {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwinfo = null;
		List<Integer> siteIdList = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwinfo = pwInfoService.selectByPwId(pwid);

			siteIdList = new ArrayList<Integer>();
			siteIdList.add(pwinfo.getASiteId());
			siteIdList.add(pwinfo.getZSiteId());
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			pwinfo = null;
		}
		return siteIdList;
	}

	/**
	 * 根据pwID获取此pw下的所有siteId
	 * 
	 * @param pwid
	 * @return object[0]为tunnel集合 object[1]为siteid集合
	 */
	private Object[] convertSite(List<Integer> pwids) throws Exception {
		List<Integer> siteId_result = null;
		List<Integer> siteIdList = null;
		Object[] objects_result = null;
		List<PwInfo> pwinfoList = null;
		PwInfoService_MB pwInfoService = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			pwinfoList = new ArrayList<PwInfo>();
			siteId_result = new ArrayList<Integer>();
			for (Integer pwid : pwids) {				
				pwinfoList.add(pwInfoService.selectByPwId(pwid));

				siteIdList = this.convertSite(pwid);

				for (Integer siteid : siteIdList) {
					if (!siteId_result.contains(siteid)) {
						siteId_result.add(siteid);
					}
				}
			}
			objects_result = new Object[2];
			objects_result[0] = pwinfoList;
			objects_result[1] = siteId_result;

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			siteId_result = null;
			siteIdList = null;
		}
		return objects_result;
	}

	/**
	 * 获取拓扑视图类型下拉列表选中的类型
	 * 
	 * @return
	 */
	public String getShowTopoType() {
		ControlKeyValue controlKeyValue = (ControlKeyValue) cmbTopoType.getSelectedItem();
		Code code = (Code) controlKeyValue.getObject();

		return code.getCodeValue();
	}

	/**
	 * 拓扑上显示邮件菜单
	 * 
	 * @param tview
	 * @return
	 * @throws Exception
	 */
	private JPopupMenu showMenu(final TView tview) throws Exception {
		JPopupMenu menu = new JPopupMenu();
		JMenu menuItem = new javax.swing.JMenu(ResourceUtil.srcStr(StringKeysMenu.MENU_SELECT_ROUTEBUSINESS)); //
		String topoType = getShowTopoType();
		if (tview.getDataBox().getSelectionModel().isEmpty()) {
			if (network.getCurrentSubNetwork() == null && topoType.equals("SEGMENT")) {
				// 创建域菜单
				menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_CREATE_FIELD));
			
				// 网元定位菜单
				menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SELECTSITE));
			} else {
				if (topoType.equals("SEGMENT")) {
					// 创建组
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_CREATE_GROUP));
					// 创建子网
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_CREATE_SUBNET));
					// 创建网元
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_CREATE_SITE));
					// 网元定位菜单
					menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SELECTSITE));
					//自动发现拓扑
					menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_TOPO_FOUND));
				}
				// 自动布局菜单
				menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_AUTOLAYOUT));
				//查询所有域网元的信息
				menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_FIELDALLINFO));//StringKeysMenu.MENU_FIELDALLINFO
				// 设置拓扑背景
				menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_TOPO_BACKGROUD));
				// 还原拓扑背景
				menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_RESET_TOPO_BACKGROUD));
			}
		} else {
			final Element element = tview.getDataBox().getLastSelectedElement();
			if ((element instanceof SubNetwork) || (element instanceof Node) || (element instanceof Link)) {
				if (element instanceof SubNetwork) {
					// 修改域菜单
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_UPDATE_FIELD));
					// 删除域菜单
					menu.add(TopoMenu.createMenu(StringKeysBtn.BTN_DELETE));

				}else if (element instanceof Dummy) {
					// 修改组菜单
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_UPDATE_GROUP));
					// 删除组菜单
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_DELETE_GROUP));
				} else if (element instanceof Group) {
					// 修改子网菜单
					menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_UPDATE_SUBNET));
					// 删除子网菜单
					menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_DELETESUBNET));
				} else if (element instanceof Node) {
					if (null != element.getUserObject()) {
						final SiteInst siteInst = (SiteInst) element.getUserObject();
						RoleRoot roleRoot=new RoleRoot();
						// 网元配置信息菜单
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_CONFIGURATION));
						// 复制网元菜单
//						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_COPYSITE));

						// 移动网元菜单
						menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_MOVE_SITE_FIELD));

						menu.add(menuItem);
						// 查询本站路由业务
						menuItem.add(menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SELECT_HOMEROUTEBUSINESS)));

						// 查询经过路由业务
						menuItem.add(menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SELECT_XC_ROUTEBUSINESS)));

						/*
						 * 添加 权限验证
						 */
						roleRoot.setItemEnbale(TopoMenu.createMenu(StringKeysTitle.TIT_MOVE_SITE_FIELD), RootFactory.DEPLOY_SELECT);
						// 删除网元菜单
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_DELETENE));

						// 如果已经登陆的网元。显示修改网元菜单 否则显示连接网元菜单
//						if (siteInst.getLoginstatus() == 1) {
//							menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_UPDATE_SITE));
//						} else {
//							menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_LOGINSITE));
//						}
						menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_UPDATE_SITE));
						if(siteInst.getLoginstatus() == 0){
							menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_LOGINSITE));
						}
						menu.add(TopoMenu.createMenu(StringKeysMenu.TAB_CURRPERFOR_T1));
						menu.add(TopoMenu.createMenu(StringKeysMenu.TAB_HISPERFOR_T1));						
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_CURRENTALARM));
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_HISALARM));
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SYNCHRO));
						//ping功能
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_PING));
						//网元的详细信息
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_SITEINFO));
						//telnet功能
						menu.add(TopoMenu.createMenu(StringKeysMenu.MENU_TELNET));
					}
				}else if (element instanceof Link) {
					Object userObj = element.getUserObject();
					if (null != userObj){ 
						if((userObj instanceof Segment)) {
							//查询光功率
							menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_QUERY_SFPPOW));
						}else if(userObj instanceof Tunnel){
							// 查询隧道承载的伪线
							menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_PW_ON_TUNNEL));
						}else if(userObj instanceof PwInfo){
							// 查询伪线承载的业务
							menu.add(TopoMenu.createMenu(StringKeysTitle.TIT_SERVICE_ON_PW));
						}
						
					}
				}
			}
		}
		return menu;
	}

}
