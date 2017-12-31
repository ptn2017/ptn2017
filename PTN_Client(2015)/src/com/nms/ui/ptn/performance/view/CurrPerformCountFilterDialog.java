package com.nms.ui.ptn.performance.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.perform.CurrPerformCountInfo;
import com.nms.db.bean.perform.CurrentPerforInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.path.SegmentService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.model.CurrentPerformanceFilter;

public class CurrPerformCountFilterDialog extends PtnDialog {
	private static final long serialVersionUID = 8186959383566893786L;
	private JLabel lblSite;//网元
	private JComboBox cmbSite;
	private JLabel lblPerformType;//监控对象类型
	private JComboBox cmbPerformType;
	private JLabel  lblMonitorObj;//监控对象
	private JComboBox cmbMonitorObj;
	private JLabel lblCycle;//采样周期
	private JComboBox cmbCycle;
    private JPanel buttonPanel;
    private PtnButton confirm;//确定
	private JButton cancel;
	private JButton clear;
	private CurrPerformCountPanel view;
	private CurrentPerformanceFilter filter;
	private CurrPerformCountThread thread = null;
	public CurrPerformCountFilterDialog(CurrPerformCountPanel view) {
		this.setModal(true);
		this.view = view;
		this.initComponents();
		this.setLayout();
		this.initData();
		this.addListener();
		UiUtil.showWindow(this, 400, 300);
	}
	
	private void initComponents() {
		this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		this.lblSite = new JLabel("网元");
		this.cmbSite = new JComboBox();
		this.lblPerformType = new JLabel("监控对象类型");
		this.cmbPerformType = new JComboBox();
		this.lblMonitorObj = new JLabel("监控对象");
		this.cmbMonitorObj = new JComboBox();
		this.lblCycle = new JLabel("采样周期: ");
		this.cmbCycle = new JComboBox();
		this.clear = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		this.confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),false);
		this.cancel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
		this.buttonPanel=new JPanel();
		this.buttonPanel.add(confirm);
		this.buttonPanel.add(cancel);
	}
	
	private void setLayout() {
		this.setCompentLayoutButton(buttonPanel,confirm,cancel);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40, 40, 40, 40,130 };
		layout.columnWeights = new double[] { 0, 0, 0, 0, 0.3 };
		layout.rowHeights = new int[] { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
		layout.rowWeights = new double[] { 0, 0, 0, 0.3, 0.2,0, 0, 0, 0.2,0,0,0,0};
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
		c.fill = GridBagConstraints.BOTH;
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
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
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
		c.gridy = 8;
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
		//监控对象类型包括: 端口/段/TUNNEL/PW/以太网业务
		this.cmbPerformType.addItem(new ControlKeyValue("0", ""));
		this.cmbPerformType.addItem(new ControlKeyValue("1", "端口"));
		this.cmbPerformType.addItem(new ControlKeyValue("2", "段"));
		this.cmbPerformType.addItem(new ControlKeyValue("3", "TUNNEL"));
		this.cmbPerformType.addItem(new ControlKeyValue("4", "PW"));
		this.cmbPerformType.addItem(new ControlKeyValue("5", "以太网业务"));
		//采样周期: 20/40/60(s)
		this.cmbCycle.addItem(new ControlKeyValue("1", "50s"));
		this.cmbCycle.addItem(new ControlKeyValue("2", "15m"));
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
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(portService);
			}
		}else if(id == 2){
			//段
			SegmentService_MB segmentService = null;
			try {
				segmentService = (SegmentService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SEGMENT);
				List<Segment> segList = segmentService.queryBySiteId(siteId);
				this.initCmbMonitorObj(segList, 2);
			} catch (Exception e) {
				ExceptionManage.dispose(e, this.getClass());
			} finally {
				UiUtil.closeService_MB(segmentService);
			}
		}else if(id == 3){
			//TUNNEL
			TunnelService_MB tunnelServiceMB = null;
			try {
				tunnelServiceMB = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
				List<Tunnel> tunnelList = tunnelServiceMB.queryTunnelBySiteId(siteId);
				this.initCmbMonitorObj(tunnelList, 3);
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
	}

	private void btnSaveAction(){
		try {
			//数据完整性校验
			boolean flag = this.checkIsFull();
			if(flag){
				//保存之前先关闭之前开启的线程,并刷新界面
				if(this.view.getThread() != null){
					this.view.getThread().stopThread();
				}
				//开启实时性能统计线程
				this.filter = this.getFilter();
				if (filter == null) {
					DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILTER));
					return;
				}
				thread = new CurrPerformCountThread(this, this.view);
				new Thread(thread).start();
				//注册监听
				this.view.stopMinotoring(this.thread);
				DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
				this.dispose();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CurrPerformCountInfo> getCurrPerformCount() {
		List<CurrentPerforInfo> currPerformList = new ArrayList<CurrentPerforInfo>();
		List<CurrentPerforInfo> currPerformList_after = new ArrayList<CurrentPerforInfo>();
		List<CurrPerformCountInfo> countList = new ArrayList<CurrPerformCountInfo>();
		long startTime = System.currentTimeMillis();
		List<CurrentPerforInfo> infoList = this.queryPerforByFilter(this.filter);
		this.setFilterCurrentPerformance(infoList, currPerformList, this.filter);
		long endTime = 0;
		if(this.cmbCycle.getSelectedIndex() == 0){
			endTime = startTime + 50*1000;
		}else if(this.cmbCycle.getSelectedIndex() == 1){
			endTime = startTime + 15*60*1000;
		}
		while(true){
			if(System.currentTimeMillis() > endTime){
				break;
			}
		}
		infoList = this.queryPerforByFilter(this.filter);
		this.setFilterCurrentPerformance(infoList, currPerformList_after, this.filter);
		ControlKeyValue conType = (ControlKeyValue) this.cmbPerformType.getSelectedItem();
		ControlKeyValue conObj = (ControlKeyValue) this.cmbMonitorObj.getSelectedItem();
		int index = Integer.parseInt(conType.getId());
		if(index == 1 || index == 2 || index == 3 || index == 4){
			this.getBandWidthUtil(currPerformList, currPerformList_after, null, countList, index, conObj);
		}else if(index == 5){
			//业务
			PwInfoService_MB pwService = null;
			String serviceType = conObj.getId();
			try {
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				if(serviceType.equals(EServiceType.ELINE.toString())){
					ElineInfo eline = (ElineInfo) conObj.getObject();
					try {
						PwInfo pw = new PwInfo();
						pw.setPwId(eline.getPwId());
						pw = pwService.selectBypwid_notjoin(pw);
						this.getBandWidthUtil(currPerformList, currPerformList_after,pw, countList, index, conObj);
					} catch (Exception e) {
						ExceptionManage.dispose(e, this.getClass());
					}
				}else if(serviceType.equals(EServiceType.ETREE.toString())){
					List<EtreeInfo> etreeList = (List<EtreeInfo>) conObj.getObject();
					for (EtreeInfo etree : etreeList) {
						try {
							PwInfo pw = new PwInfo();
							pw.setPwId(etree.getPwId());
							pw = pwService.selectBypwid_notjoin(pw);
							this.getBandWidthUtil(currPerformList, currPerformList_after, pw, countList, index, conObj);
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						}
					}
				}else if(serviceType.equals(EServiceType.ELAN.toString())){
					List<ElanInfo> elanList = (List<ElanInfo>) conObj.getObject();
					for (ElanInfo elan : elanList) {
						try {
							PwInfo pw = new PwInfo();
							pw.setPwId(elan.getPwId());
							pw = pwService.selectBypwid_notjoin(pw);
							this.getBandWidthUtil(currPerformList, currPerformList_after,pw, countList, index, conObj);
						} catch (Exception e) {
							ExceptionManage.dispose(e, this.getClass());
						}
					}
				}
			} catch (Exception e1) {
				ExceptionManage.dispose(e1,this.getClass());
			} finally {
				UiUtil.closeService_MB(pwService);
			}
		}
		return countList;
	}
	
	private void getBandWidthUtil(List<CurrentPerforInfo> currPerformList,
			List<CurrentPerforInfo> currPerformList_after, PwInfo pw, 
			List<CurrPerformCountInfo> countList, int index, ControlKeyValue conObj) {
		CurrPerformCountInfo count = new CurrPerformCountInfo();
//		float rx_Bytes = 0;
//		float tx_Bytes = 0;
//		float rx_Bytes_now = 0;
//		float tx_Bytes_now = 0;
//		for(CurrentPerforInfo c : currPerformList){
			this.getBytes(currPerformList, index, conObj, pw, 1, count);
//		}
//		for(CurrentPerforInfo c : currPerformList_after){
			this.getBytes(currPerformList_after, index, conObj, pw, 2, count);
//		}
		if(pw == null){
			count.setObjectName(conObj.getName());
		}else{
			count.setObjectName(conObj.getName()+"/"+pw.getPwName());
		}
		count.setSiteName(((ControlKeyValue)this.cmbSite.getSelectedItem()).getName());
		count.setTime(this.getcurrentTime());
//		count.setReceiveByte(rx_Bytes_now+"");
//		count.setSendByte(tx_Bytes_now+"");
		count.setInBandWidthUtil(this.caculateRate(Float.parseFloat(count.getReceiveByte_before()),
				Float.parseFloat(count.getReceiveByte())));
		count.setOutBandWidthUtil(this.caculateRate(Float.parseFloat(count.getSendByte_before()), 
				Float.parseFloat(count.getSendByte())));
		countList.add(count);		
	}

	private void getBytes(List<CurrentPerforInfo> currPerformList, int index, ControlKeyValue conObj,
					PwInfo pwInfo, int type, CurrPerformCountInfo count) {
		int siteId = Integer.parseInt(((ControlKeyValue) this.cmbSite.getSelectedItem()).getId());
		if(index == 1 || index == 2){
			//端口 || 段
			String portName = "";
			if(index == 1){
				PortInst port = (PortInst) conObj.getObject();
				portName = port.getPortName();
			}else{
				Segment seg = (Segment) conObj.getObject();
				String siteName = ((ControlKeyValue)this.cmbSite.getSelectedItem()).getName();
				if(seg.getShowSiteAname().equals(siteName)){
					portName = seg.getShowPortAname();
				}else if(seg.getShowSiteZname().equals(siteName)){
					portName = seg.getShowPortZname();
				}
			}
			for(CurrentPerforInfo c : currPerformList){
				if(portName.equalsIgnoreCase(c.getObjectName())){
					if(c.getCapability().getCapabilitycode() == 18){
						//接收总字节数
						if(type == 1){
							count.setReceiveByte_before(c.getPerformanceValue()+"");
						}else{
							count.setReceiveByte(c.getPerformanceValue()+"");
						}
					}else if(c.getCapability().getCapabilitycode() == 19){
						//发送总字节数
						if(type == 1){
							count.setSendByte_before(c.getPerformanceValue()+"");
						}else{
							count.setSendByte(c.getPerformanceValue()+"");
						}
					}
				}
			}
		}else if(index == 3){
			//tunnel
			Tunnel tunnel = (Tunnel) conObj.getObject();
			String tunnelName = tunnel.getTunnelName();
			//单网元只查xc类型 || 网络侧只查中间网元
			if((tunnel.getaSiteId() == 0 && tunnel.getzSiteId() == 0)
					|| (tunnel.getaSiteId() != siteId && tunnel.getzSiteId() != siteId)){
				for(CurrentPerforInfo c : currPerformList){
					if(c.getObjectName().contains(tunnelName)){
						if(c.getPerformanceCode() == 107){
							//接收总字节数
							if(type == 1){
								count.setReceiveByte_before(c.getPerformanceValue()+"");
							}else{
								count.setReceiveByte(c.getPerformanceValue()+"");
							}
						}else if(c.getPerformanceCode() == 108){
							//发送总字节数
							if(type == 1){
								count.setSendByte_before(c.getPerformanceValue()+"");
							}else{
								count.setSendByte(c.getPerformanceValue()+"");
							}
						}
					}
				}
			}
		}else if(index == 4 || index == 5){
			PwInfo pw = null;
			if(pwInfo == null){
				//pw
				pw = (PwInfo) conObj.getObject();				
			}else{
				//以太网业务
				pw = pwInfo;
			}
			String pwName = pw.getPwName();
			if(pw.getASiteId() == siteId){
				for(CurrentPerforInfo c : currPerformList){
					if(c.getObjectName().contains(pwName)){
						if(c.getPerformanceCode() == 111){
							//接收总字节数
							if(type == 1){
								count.setReceiveByte_before(c.getPerformanceValue()+"");
							}else{
								count.setReceiveByte(c.getPerformanceValue()+"");
							}
						}else if(c.getPerformanceCode() == 112){
							//发送总字节数
							if(type == 1){
								count.setSendByte_before(c.getPerformanceValue()+"");
							}else{
								count.setSendByte(c.getPerformanceValue()+"");
							}
						}
					}
				}
			}
		}
	}

	private String getcurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 计算剩余率
	 * 
	 * @param usedByte
	 * @param totalByte
	 * @return
	 */
	private String caculateRate(float usedByte, float totalByte) {
		double rate = 0.0d;
		String rate_temp = "";
		if (totalByte > 0) {
			Format format = new DecimalFormat("0.00");
			if(totalByte >= usedByte){
				rate_temp = format.format((double) (totalByte - usedByte) / totalByte);
			}else{
				rate_temp = format.format((double) (usedByte - totalByte) / totalByte);
			}
			rate = (Double.parseDouble(rate_temp)) * 100;
		} else {
			rate = 0.0d;
		}
		if(totalByte >= usedByte){
			return (totalByte - usedByte) + "/" + totalByte + " (" + rate + "%)";
		}else{
			return (usedByte - totalByte) + "/" + totalByte + " (" + rate + "%)";
		}
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
		return true;
	}

	/**
	 *  获取当前选择的过滤对象
	 */
	public CurrentPerformanceFilter getFilter() throws Exception {
		CurrentPerformanceFilter filter = new CurrentPerformanceFilter();
		//根据网元查询
		List<Integer> siteIdList = new ArrayList<Integer>();
		siteIdList.add(Integer.parseInt(((ControlKeyValue)this.cmbSite.getSelectedItem()).getId()));
		filter.setSiteInsts(siteIdList);
		filter.setObjectType(EObjectType.SITEINST);
		// 添加性能类型条件
		ControlKeyValue conType = (ControlKeyValue) this.cmbPerformType.getSelectedItem();
		int index = Integer.parseInt(conType.getId());
		List<Integer> capIdList = new ArrayList<Integer>();//性能类型
		List<String> descList = new ArrayList<String>();//性能类别
		StringBuilder strTypeBuilder = new StringBuilder();
		if(index == 1 || index == 2){
			//端口 || 段
			String portType = "NNI";
			if(index == 1){
				PortInst port = (PortInst) ((ControlKeyValue) this.cmbMonitorObj.getSelectedItem()).getObject();
				portType = port.getPortType();
			}
			if(portType.equals("NNI") || portType.equals("UNI")){
				capIdList.add(3);
				capIdList.add(4);
				descList.add("接收总字节数");
				descList.add("发送总字节数");
				strTypeBuilder.append("PORT");//性能类型
			}
		}else if(index == 3){
			//tunnel
			capIdList.add(220);
			capIdList.add(221);
			descList.add("TMP接收字节数");
			descList.add("TMP发送字节数");
			strTypeBuilder.append("TMP/TMC");//性能类型
		}else if(index == 4 || index == 5){
			//pw || 以太网业务
			capIdList.add(224);
			capIdList.add(225);
			descList.add("TMC接收字节数");
			descList.add("TMC发送字节数");
			strTypeBuilder.append("TMP/TMC");//性能类型
		}
		filter.getCapabilityIdList().addAll(capIdList);//主键id
		filter.setTypeStr(strTypeBuilder.toString());
		//添加性能类别条件
		filter.getCapabilityNameList().addAll(descList);
		//监控周期
		filter.setMonitorCycle(EMonitorCycle.MIN15);
		filter.setPerformanceType(0);
		filter.setPerformanceCount(0);
		filter.setPerformanceBeginCount(0);
		filter.setPerformanceMonitorTime(porseTime(0));
		//零值过滤
		filter.setFilterZero(0);
		return filter;
	}
	
	/**
	 * 根据性能条件查询性能值
	 */
	private List<CurrentPerforInfo> queryPerforByFilter(CurrentPerformanceFilter filter) {
		List<CurrentPerforInfo> queryPerforList = new ArrayList<CurrentPerforInfo>();
		try {
			DispatchUtil dispatch = new DispatchUtil(RmiKeys.RMI_PERFORMANCE);
			queryPerforList = dispatch.executeQueryCurrPerforByFilter(filter);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return queryPerforList;
	}

	private void setFilterCurrentPerformance(List<CurrentPerforInfo> infos, List<CurrentPerforInfo> currPerformFilterList, CurrentPerformanceFilter filter) {
		String[] filrertypes = filter.getTypeStr().trim().split(",");

		for (CurrentPerforInfo currentPerformanceInfo : infos) {
			if (currentPerformanceInfo.getCapability() != null) {
				for (int i = 0; i < filrertypes.length; i++) {
					if (filrertypes[i].equals(currentPerformanceInfo.getCapability().getCapabilitytype())) {
						for (String strType : filter.getCapabilityNameList()) {
							if (filter.getFilterZero() > 0) {
								if (currentPerformanceInfo.getCapability().getCapabilitydesc().equalsIgnoreCase(strType)
										&& currentPerformanceInfo.getPerformanceValue() != 0
										&& currentPerformanceInfo.getObjectName() != null) {
									currPerformFilterList.add(currentPerformanceInfo);
								}
							} else {
								if (currentPerformanceInfo.getCapability().getCapabilitydesc().equalsIgnoreCase(strType)
										&& currentPerformanceInfo.getObjectName() != null
										) {
									currPerformFilterList.add(currentPerformanceInfo);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 *处理时间 
	 * @param preseTime
	 * @return
	 */
	private long testTime(int size) {
		long filterTime = 0;
		try {
			long preseTime = new Date().getTime() - size*15*60*1000;
			SimpleDateFormat fat = new SimpleDateFormat("HH:mm");
			SimpleDateFormat fat2 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fat3 =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(preseTime);
			String preseTimeYear = fat2.format(date);
			String preseTimeString = fat.format(date);
			String[] str = preseTimeString.split(":");
			int i = Integer.valueOf(str[1]) / 15;
			preseTimeYear = preseTimeYear + " " + str[0] + ":";
			if (i == 0) {
				preseTimeString = preseTimeYear  + "00";
			}
			else if (i == 1) {
				preseTimeString = preseTimeYear + "15";
			}
			else if (i == 2) {
				preseTimeString = preseTimeYear + "30";
			}
			else if (i == 3) {
				preseTimeString = preseTimeYear + "45";
			}
			
			filterTime=fat3.parse(preseTimeString).getTime();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return filterTime;
	}
	
	/**
	 *处理时间 
	 * @param preseTime
	 * @return
	 */
	private String porseTime(int size) {
		SimpleDateFormat fat2 = null;
		String preseTimeString = null;
		try {
			if(size >= 100){
				fat2 = new SimpleDateFormat("yyyy-MM");
				SimpleDateFormat fat = new SimpleDateFormat("d ");
				long preseTime = new Date().getTime() - size*24*60*60*1000;
				Date date = new Date(preseTime);
				String preseTimeYear = fat2.format(date);
				if(size == 100){
					preseTimeString = preseTimeYear ;
				}if(size == 101){
					date = new Date(preseTime);
					preseTimeString = fat.format(date);
					preseTimeString = preseTimeYear+"-"+"";
				}
			}else{
				fat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long parseTime = testTime(size);
				preseTimeString = fat2.format(new Date(parseTime));
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return preseTimeString;
	}

	public CurrPerformCountThread getThread() {
		return thread;
	}
}