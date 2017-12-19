﻿package com.nms.ui.ptn.statistics.performance;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.chart.LineChart;

import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.db.enums.EMonitorCycle;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * 性能 统计  （图表）
 * @author sy
 *
 */
public class PerformanceInfoPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton filterButton;// 设置过滤
	private JButton clearFilter;// 清除过滤
	private PtnButton export;
	private JPanel buttonPanel;
	private JScrollPane chartPerformancePnael;//图表  pnael
	private TDataBox box;
	private LineChart lineChart;// 图表 （线行） 
	/**
	 * 时间  选择 （开始，结束）
	 */
	private String startTime;
	private String readEndTime;
	//查询周期
	private EMonitorCycle monitorCycle ;
	//性能類型（過濾）code 值
	private int code;
	private Object object;// 监控对象

	public EMonitorCycle getMonitorCycle() {
		return monitorCycle;
	}
	
	public void setMonitorCycle(EMonitorCycle monitorCycle) {
		this.monitorCycle = monitorCycle;
	}
	/**
	 * 网元树   选中的  端口,性能类型集合
	 */
	private List<Element> portElement;
	
	public PerformanceInfoPanel(){
		init();
	}
	
	// 初始化
	public void init(){
		this.initComponents();
		//setChart();
		this.setLayout();
		this.addListener();
	}
	
	// 实例化
	public void initComponents(){
		this.buttonPanel=new javax.swing.JPanel();
		this.chartPerformancePnael=new javax.swing.JScrollPane();
		this.box=new TDataBox();
		this.lineChart=new LineChart(box);
		lineChart.setYAxisVisible(true);
		//显示（Y）列值
		lineChart.setYScaleTextVisible(true);
		lineChart.setXAxisVisible(true);
		lineChart.setXScaleTextVisible(true);
		lineChart.setYAxisText(ResourceUtil.srcStr(StringKeysObj.OBJ_PERFORMANCE_CODE));//性能值
		lineChart.setXScaleTextFont(new Font("",8, 7));
		lineChart.setFont(new Font("",100,100));
		lineChart.setXScaleTextOrientation(TWaverConst.LABEL_ORIENTATION_RIGHT);
		//Consts.CHART_XSCALE_TEXT_ORIENTATION_VERTICAL;
		//每一个点上是否需要显示标记
		lineChart.setInflexionVisible(true);
		this.chartPerformancePnael.add(this.lineChart);
		chartPerformancePnael.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		chartPerformancePnael.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		chartPerformancePnael.setViewportView(this.lineChart);
		filterButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER));
		clearFilter= new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_FILTER_CLEAR));
		export = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT),false,RootFactory.COUNTMODU);
	}
	
	public LineChart getLineChart() {
		return lineChart;
	}
	
	public void setLineChart(LineChart lineChart) {
		this.lineChart = lineChart;
	}
	/**
	 * 传人  统计图  （数据）
	 * 		通过  设置 其他 条件
	 * 			取出   参数，穿入
	 * 
	 * 需要  ：
	 * 		端口名（List），性能值(List)
	 * @param ssPerform
	 * @throws Exception 
	 */
	public void bingData(List<HisPerformanceInfo> pList) throws Exception{
		/**
		 * 设定  ，  节点（图表）
		 * 		的 随机 颜色
		 */
		Color[] colorNode=new Color[35];
		int k = 0;
		colorNode[k++]=Color.BLACK;
		colorNode[k++]=Color.BLUE;
		colorNode[k++]=Color.CYAN;
		colorNode[k++]=Color.DARK_GRAY;
		colorNode[k++]=Color.GRAY;
		colorNode[k++]=Color.GREEN;
		colorNode[k++]=Color.LIGHT_GRAY;
		colorNode[k++]=Color.MAGENTA;
		colorNode[k++]=Color.ORANGE;
		colorNode[k++]=Color.PINK;
		colorNode[k++]=Color.RED;
		colorNode[k++]=Color.WHITE;
		colorNode[k++]=Color.YELLOW;
		colorNode[k++]=new Color(-1664897);
		colorNode[k++]=new Color(-1764897);
		colorNode[k++]=new Color(-1864897);
		colorNode[k++]=new Color(-1964897);
		colorNode[k++]=new Color(-1464897);
		colorNode[k++]=new Color(-1364897);
		colorNode[k++]=new Color(-1264897);
		colorNode[k++]=new Color(-1164897);
		colorNode[k++]=new Color(-1064897);
		colorNode[k++]=new Color(-964897);
		colorNode[k++]=new Color(-864897);
		colorNode[k++]=new Color(-764897);
		colorNode[k++]=new Color(-664897);
		colorNode[k++]=new Color(-564897);
		colorNode[k++]=new Color(-464897);
		colorNode[k++]=new Color(-364897);
		colorNode[k++]=new Color(-264897);
		colorNode[k++]=new Color(-2064897);
		colorNode[k++]=new Color(-2164897);
		colorNode[k++]=new Color(-2264897);
		colorNode[k++]=new Color(-2364897);
		colorNode[k++]=new Color(-2464897);
		
//		HisPerformanceInfo hisInfo=null;
//		HisPerformanceService_Mb hisPerformanceService=null;
		Capability capability = null;
		try {
			int index = 0;
			for(Element element : portElement){
				Node n = (Node) element;
				if (n.getUserObject() instanceof Capability) {
					capability = (Capability) n.getUserObject();
					for(HisPerformanceInfo p : pList){
						Capability c = p.getCapability();
						//性能类型 不为空，则 创建图表的节点
						if(c != null){
							if(c.getId() == capability.getId()){
								//创建 节点
								Element node = new Node();
								String name = p.getSiteName()+"/"+p.getObjectName()+"-"+capability.getCapabilityname();
								//	Font f=new Font(name,100,5);
								node.setName(name);
								//设置  显示 样式
								node.putChartInflexionStyle(TWaverConst.INFLEXION_STYLE_TRIANGLE);
								//随机 取出  Color数组中1-9  
								node.putChartColor(colorNode[index]);
								index++;
								box.addElement(node);
								/**
								 * X轴 时间
								 */
								lineChart.addXScaleText(" "+p.getPerformanceTime());
								//Y  轴  值
								node.addChartValue(p.getPerformanceValue());
							}
						}
					}
				}
			}
			
//			for(HisPerformanceInfo p : pList){
//				capability = p.getCapability();
//				//性能类型 不为空，则 创建图表的节点
//				if(capability != null){
//					//创建 节点
//					Element node = new Node();
//					String name = p.getSiteName()+"/"+p.getObjectName()+"-"+capability.getCapabilityname();
//					//	Font f=new Font(name,100,5);
//					node.setName(name);
//					//设置  显示 样式
//					node.putChartInflexionStyle(TWaverConst.INFLEXION_STYLE_TRIANGLE);
//					//随机 取出  Color数组中1-9  
//					node.putChartColor(colorNode[index]);
//					index++;
//					box.addElement(node);
//					/**
//					 * X轴 时间
//					 */
//					lineChart.addXScaleText(" "+p.getPerformanceTime());
//					//Y  轴  值
//					node.addChartValue(p.getPerformanceValue());
//				}
//			}
//			int index = 0;
////			hisPerformanceService = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
//			for(int j=0;j<portElement.size();j++){
//				Element element=portElement.get(j);
//				if(element instanceof Node){
//					//将  节点 转为  性能类型
//					if(element.getUserObject()!=null){						
//						//取出选中的   性能类型
//						if(element.getUserObject() instanceof Capability){
//							capability=(Capability)element.getUserObject();
//							PortInst portInst=null;
//							SiteInst siteInst=null;
//							if(element.getParent()!=null&&element.getParent().getUserObject() instanceof PortInst){
//								if(element.getParent().getParent().getParent()!=null&&element.getParent().getParent().getParent().getParent().getUserObject() instanceof SiteInst){
//									siteInst=(SiteInst)element.getParent().getParent().getParent().getParent().getUserObject();
//								}
//								portInst=(PortInst)element.getParent().getUserObject();
//							}
//							// 此节点的   父节点端口存在 ： 并且 网元（父节点的父的父）存在
//							if(siteInst!=null&&portInst!=null){
//								//创建  历史性能对象
//								hisInfo=new HisPerformanceInfo();
//								hisInfo.setSiteId(siteInst.getSite_Inst_Id());
//								hisInfo.setObjectName(portInst.getPortName());
//							}
//							//性能类型 不为空，则 创建图表的节点
//							if(capability!=null){
//								//创建 节点
//								Element  node=new Node();
//								/**
//								 * 图表  中 节点 的 名称
//								 */
//								String name=siteInst.getCellId()+"/"+portInst.getPortName()+"-"+capability.getCapabilityname();
//								//	Font f=new Font(name,100,5);
//								node.setName(name);
//								//设置  显示 样式
//								node.putChartInflexionStyle(TWaverConst.INFLEXION_STYLE_TRIANGLE);
//								//随机 取出  Color数组中1-9  
//								node.putChartColor(colorNode[index]);
//								index++;
//								box.addElement(node);
//								/**
//								 * 选择  自定义时间
//								 */
//								if(code==6){
//									if(this.getStartTime()!=null&&this.getReadEndTime()!=null){
//										//   开始与结束  非数据库数据，界面 显示做参数（hisInfo）传入 DAO
//										hisInfo.setStartTime(this.getStartTime());
//										hisInfo.setPerformanceEndTime(this.getReadEndTime());
//									}
//								}
//								hisInfo.setMonitorCycle(this.monitorCycle);
////								hisPerformanceService=(HisPerformanceService) ConstantUtil.serviceFactory.newService(Services.HisPerformance);
//								List<HisPerformanceInfo> performanceList=hisPerformanceService.selectPerformanceValue(hisInfo, code, capability);
//								/**
//								 * 遍历 传人的参数（性能 List）
//								 */
//								if(performanceList!=null){
//									if(performanceList.size()>0){
//										//遍历   性能统计
//										for(int i=0;i<performanceList.size();i++){
//											PerformanceInfo perform=(PerformanceInfo)performanceList.get(i);
//											/**
//											 * 设置 某个  端口的  性能值
//											 */
//											if(perform.getObjectName().equals(portInst.getPortName())){
//												/**
//												 * X轴 时间
//												 */
//												lineChart.addXScaleText(" "+perform.getPerformanceTime());
//												//Y  轴  值
//												node.addChartValue(perform.getPerformanceValue());
//											}
//											else{
//												node.addChartValue(0);
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
		} catch (Exception e) {
			throw e;
		}finally{
		}
	}
	
	/**
	 * 按钮事件监听
	 */
	public void addListener(){
		// 设置过滤条件
		filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new PerformanceFileDialog(PerformanceInfoPanel.this);
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});

		// 清除过滤条件
		clearFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					clear();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		});
	}
	
	/**
	 * 按钮布局
	 */
	public void setButtonLayout(){
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[] {10,10,10};
		layout.columnWeights =new double[]{0,0,0};
		layout.rowHeights=new int[]{10,};
		layout.rowWeights=new double[]{0};
		this.buttonPanel.setLayout(layout);
		
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=0;
		c.gridheight=1;
		c.gridwidth=1;
		c.insets=new Insets(5,5,5,10);
		layout.setConstraints(this.export, c);
		
		c.gridx=1;
		layout.setConstraints(this.filterButton, c);
		buttonPanel.add(this.filterButton);
		
		c.gridx=2;
		layout.setConstraints(this.clearFilter, c);
		buttonPanel.add(this.clearFilter);
	}
	
	/**
	 * 设置布局
	 */
	public void setLayout(){
		this.setButtonLayout();
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[] {40,40,40};
		layout.columnWeights =new double[]{0,0.2,0};
		layout.rowHeights=new int[]{10,20,10,80};
		layout.rowWeights=new double[]{0,0,0,0.2};
		this.setLayout(layout);
		
		GridBagConstraints c=new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=1;
		c.gridheight=1;
		c.gridwidth=1;
		layout.setConstraints(this.buttonPanel, c);
		this.add(this.buttonPanel);
		
		c.gridy=3;
		c.gridheight=1;
		c.gridwidth=3;
		layout.setConstraints(this.chartPerformancePnael, c);
		this.add(this.chartPerformancePnael);
		
	}
	
	/**
	 * 清空
	 */
	public void clear(){
		this.portElement=null;
		this.box.clear();
	}
	
	public TDataBox getBox() {
		return box;
	}
	
	public void setBox(TDataBox box) {
		this.box = box;
	}

	public List<Element> getPortElement() {
		return portElement;
	}
	
	public void setPortElement(List<Element> portElement) {
		this.portElement = portElement;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getReadEndTime() {
		return readEndTime;
	}

	public void setReadEndTime(String readEndTime) {
		this.readEndTime = readEndTime;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
