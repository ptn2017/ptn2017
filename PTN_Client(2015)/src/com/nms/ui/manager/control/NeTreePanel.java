package com.nms.ui.manager.control;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import twaver.Element;
import twaver.Node;
import twaver.TDataBox;
import twaver.tree.ElementNode;
import twaver.tree.TTree;
import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.system.Field;
import com.nms.model.equipment.slot.SlotService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.topology.util.CreateAllTopo;

/**
 * 网元树panel 一个通用的类，所有要加载网元树的 都可直接调用此方法
 * 
 * @author kk
 * 
 */
public class NeTreePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TDataBox tDataBox = null; // 数据对象
	private TTree tree = null; // tree对象
	private int minWidth = 200; // 最小宽度 默认为200
	private boolean isShowSearch = false; // 是否显示搜索控件
	private JLabel lblSearch = null; // 快速查询label
	private JTextField txtSearch = null; // 快速查询文本框
	private JPanel panelSearch = null; // 快速查询面板
	private CreateAllTopo createAllTopo = null;
	private ViewDataTable viewDataTable = null; // 点击tree要加载的table
	private int level = 2; // 加载等级
	
	private SiteInst siteInst=null;//网元为跟节点时，上层（网元下拉表）传人
	private boolean flag=false;//网元树头部显示信息，false默认显示域列表，true,显示网元列表
	private List<String> typeList;//要显示的 端口类型的集合（eth，pdh,sdh,lag）null为全部都显示
	private int isNNI;// ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	private boolean isCapability;//默认false 不加载性能类型，true： 加载性能 类型
	private List<SiteInst> siteInstList = null;//只加载固定网元

	/**
	 * 创建一个新的实例
	 * 
	 * @param isShowSearch
	 *            是否显示搜索控件
	 * @param level
	 *            加载级别 1=域级别 2=网元级别 3=板卡级别 4=端口级别
	 */
	public NeTreePanel(boolean isShowSearch, int level, ViewDataTable viewDataTable,boolean isCapability) {
		try {
			this.isCapability =isCapability;
			this.isShowSearch = isShowSearch;
			this.viewDataTable = viewDataTable;
			this.level = level;
			this.initComponent();
			this.setLayout();
			this.initWindows();
			this.createAllTopo = new CreateAllTopo(this.tDataBox, this.level);
			this.initData();
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	/**
	 * 创建一个新的实例
	 * @param level
	 * 		加载等级    级别 2=网元级别  3=板卡级别  4=类型+端口级别  5= 性能类型级别
	 * @param siteInst
	 * 		跟节点 网元对象
	 * @param typeList
	 * 		要加载的端口类型集合（eth,pdh,sdh,lag） null=全部都加载
	 * @param isNNI
	 * 		 ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	 * @param isLag
	 */
	public NeTreePanel(int level ,SiteInst siteInst,List<String> typeList,int isNNI ){
		
		try {
			this.isNNI=isNNI;
			this.siteInst=siteInst;
			this.typeList=typeList;
			this.flag=true;
			this.level=level;
			this.initComponent();
			this.setLayout();
			this.initWindows();
			this.createAllTopo = new CreateAllTopo(this.tDataBox, this.level);
			this.initData();
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 创建一个新的实例
	 * 
	 * @param isShowSearch
	 *            是否显示搜索控件
	 * @param level
	 *            加载级别 1=域级别 2=网元级别 3=板卡级别 4=端口级别
	 */
	public NeTreePanel(boolean isShowSearch, int level, List<String> typeList, int isNNI) {
		try {
			this.isShowSearch = isShowSearch;
			this.level = level;
			this.typeList = typeList;
			this.isNNI = isNNI;
			this.flag=true;
			this.initComponent();
			this.setLayout();
			this.initWindows();
			this.createAllTopo = new CreateAllTopo(this.tDataBox, this.level);
			this.initData();
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}
	
	/**
	 * 创建一个新的实例
	 * @param level
	 * 		加载等级    级别 2=网元级别  3=板卡级别  4=类型+端口级别  5= 性能类型级别
	 * @param siteInst
	 * 		跟节点 网元对象
	 * @param typeList
	 * 		要加载的端口类型集合（eth,pdh,sdh,lag） null=全部都加载
	 * @param isNNI
	 * 		 ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	 * @param isLag
	 */
	public NeTreePanel(int level, List<SiteInst> siteInstList, List<String> typeList, int isNNI ){
		
		try {
			this.isNNI=isNNI;
			this.siteInstList=siteInstList;
			this.typeList=typeList;
			this.flag=true;
			this.level=level;
			this.initComponent();
			this.setLayout();
			this.initWindows();
			this.createAllTopo = new CreateAllTopo(this.tDataBox, this.level);
			this.initData();
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	/**
	 * 创建一个新的实例
	 * @param level
	 * 		加载等级    级别 2=网元级别  3=板卡级别  4=类型+端口级别  5= 性能类型级别
	 * @param siteInst
	 * 		跟节点 网元对象
	 * @param typeList
	 * 		要加载的端口类型集合（eth,pdh,sdh,lag） null=全部都加载
	 * @param isNNI
	 * 		 ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	 * @param flag
	 * 		显示域列表/显示网元列表 false/true
	 */
	public NeTreePanel(int level, List<SiteInst> siteInstList, List<String> typeList, int isNNI, boolean flag){
		
		try {
			this.isNNI=isNNI;
			this.siteInstList=siteInstList;
			this.typeList=typeList;
			this.flag=flag;
			this.level=level;
			this.initComponent();
			this.setLayout();
			this.initWindows();
			this.createAllTopo = new CreateAllTopo(this.tDataBox, this.level);
			this.initData();
			this.addListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	public NeTreePanel(){
		
	}

	/**
	 * 初始化对象
	 */
	private void initComponent() {
		if(flag){
			this.tDataBox = new TDataBox(ResourceUtil.srcStr(StringKeysLbl.LBL_NE_LIST));
		}else{
			this.tDataBox = new TDataBox(ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD_LIST));
		}
		this.tree = new TTree(tDataBox);
		this.tree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);
		
		if (this.isShowSearch) {
			this.lblSearch = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QUICK_FIND));
			this.txtSearch = new JTextField();
			this.panelSearch = new JPanel();
		}
	}

	/**
	 * 布局
	 */
	private void setLayout() {
		super.setLayout(new BorderLayout());
		if (this.isShowSearch) {
			this.setPanelSearchLayout();
			this.add(this.panelSearch, BorderLayout.NORTH);
		}
		this.add(new JScrollPane(this.tree), BorderLayout.CENTER);
	}

	/**
	 * 设置快速查找面板布局
	 */
	private void setPanelSearchLayout() {
		int labelWidth = (int) (this.minWidth * 0.3);
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { labelWidth, this.minWidth - labelWidth };
		componentLayout.columnWeights = new double[] { 0, 0.1 };
		componentLayout.rowHeights = new int[] { 40 };
		componentLayout.rowWeights = new double[] { 0 };
		this.panelSearch.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		// 添加域tree
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		componentLayout.setConstraints(this.lblSearch, c);
		this.panelSearch.add(this.lblSearch);

		c.gridx = 1;
		componentLayout.setConstraints(this.txtSearch, c);
		this.panelSearch.add(this.txtSearch);
	}

	/**
	 * 设置panel的最小宽度也是默认宽度
	 * 
	 * @param minWidth
	 *            宽度
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		this.initWindows();
	}

	/**
	 * 初始化窗口
	 */
	private void initWindows() {
		this.setMinimumSize(new Dimension(this.minWidth, 0));
	}

	/**
	 * 加载数据
	 * 
	 * @param level
	 *            加载等级。 1=域级别 2=网元级别 3=板卡级别 4=端口级别5=tunnel/6=pw
	 * @throws Exception
	 */
	public void initData() throws Exception {
		try {
			if(flag){
				if(this.getSiteInst()!=null){
					this.createAllTopo.createNode(this.getSiteInst(),true,this.typeList,isNNI,isCapability,true);
				}else{
					if(this.getSiteInstList() != null){
						this.createAllTopo.createNode(false, this.getSiteInstList(), this.typeList,
								this.isNNI, true);
					}else{
						this.createAllTopo.createNode(true, null, this.typeList, isNNI, true);
					}
				}
			}else{
				this.createAllTopo.createTopo(true,true);
			}
			
			if (level > 2) {
				this.tree.addTreeWillExpandListener(this.willExpandListener);
			} else {
				this.tree.removeTreeWillExpandListener(this.willExpandListener);
			}
			this.tree.expand(2);
		} catch (Exception e) {
			throw e;
		} finally {
		}
	}

	/**
	 * 树的 展开节点事件，（加以控制） TreeExpansionListener expoansionListener
	 */
	private TreeWillExpandListener willExpandListener = new TreeWillExpandListener() {

		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
			Element element = null;
			try {
				element = getElement(event);				
				if (element != null) {
					if (element instanceof Node) {
						// 取出网元 节点，添加 板卡 （查 DB）
						if (element.getUserObject() instanceof SiteInst) {
							createAllTopo.createCard((Node) element, false,typeList,isNNI,isCapability,true);
						}else if(element.getUserObject() instanceof CardInst){//取出板卡节点，添加端口
							//createAllTopo.createPort((Node) element, false,true);
						}
					}
				}
			} catch (Exception e) {
				ExceptionManage.dispose(e, getClass());
			} finally {
				element = null;
			}
		}

		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

		}
	};

	/**
	 * 获取tree上当前要展开的节点
	 * 
	 * @param e
	 * @return
	 */
	private Element getElement(TreeExpansionEvent e) {
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

	/**
	 * 添加监听
	 */
	private void addListener() {
		if (null != this.viewDataTable) {
			if (this.tree.getTreeSelectionListeners().length == 1) {
				this.tree.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						List<SiteInst> siteInstList = null;
						try {
							siteInstList = getSelectSiteInst();
							viewDataTable.initData(siteInstList);
						} catch (Exception e1) {
							ExceptionManage.dispose(e1, this.getClass());
						}
					}
				});
			}
		}

		if (this.isShowSearch) {
			this.txtSearch.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent e) {
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					txtSearchValueChange();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					txtSearchValueChange();
				}
			});
		}
	}

	/**
	 * 获取所有选中的网元
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	public List<SiteInst> getSelectSiteInst() throws Exception {
		return this.getSelect(new SiteInst());
	}

	/**
	 * 获取所有选中的板卡
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	public List<CardInst> getSelectCardInst() throws Exception {
		return this.getSelect(new CardInst());
	}
	/**
	 *获取所有选中的端口
	 * @author sy
	 * @throws Exception
	 */
	public List<PortInst> getSelectPortInst()throws Exception{
		return this.getSelect(new PortInst());
	}
	/**
	 *获取所有选中的	域
	 * @author sy
	 * @throws Exception
	 */
	public List<Field> getSelectField()throws Exception{
		return this.getSelect(new Field());
	}
	/**
	 * 获取所有选中的端口
	 * @return
	 * @throws Exception
	 */
	public List<Capability> getSelectCapability()throws Exception{
		return this.getSelect(new Capability());
	}
	
	/**
	 * 获取所有选中的槽位
	 * 
	 * @author kk
	 * @Exception 异常对象
	 */
	public List<SlotInst> getSelectSlotInst() throws Exception {
		List<CardInst> cardInstList = null;
		List<SlotInst> slotInstList = null;
		SlotService_MB slotService = null;
		SlotInst slotinst = null;
		List<SlotInst> slotInstList_select = null;
		try {
			slotService = (SlotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SLOT);
			slotInstList = new ArrayList<SlotInst>();
			cardInstList = this.getSelectCardInst();
			for (CardInst cardInst : cardInstList) {
				// 根据主键查询slot对象
				slotinst = new SlotInst();
				slotinst.setId(cardInst.getSlotId());
				slotInstList_select = slotService.select(slotinst);
				if (null != slotInstList_select && slotInstList_select.size() == 1) {
					slotInstList.add(slotInstList_select.get(0));
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			cardInstList = null;
			UiUtil.closeService_MB(slotService);
			slotinst = null;
			slotInstList_select = null;
		}
		return slotInstList;
	}
	/**
	 * 获取所有选中的对象
	 * 
	 * @param object
	 *            要验证的对象。如传入siteInst对象， 返回的就是list<siteInst>
	 * @author kk
	 * @Exception 异常对象
	 */
	@SuppressWarnings("unchecked")
	private List getSelect(Object object) throws Exception {
//		TreePath[] treePaths = null;
//		ElementNode elementNode = null;
		List objectList = new ArrayList();
		try {
			//原来是根据下拉层次取的值 导致有些界面如果选择之后 重新收缩的话导致数据部准确
//			treePaths = tree.getSelectionPaths();
//			if (treePaths != null) {
//				for (TreePath treePath : treePaths) {
//					elementNode = (ElementNode) treePath.getLastPathComponent();
//					if (null != elementNode.getElement().getUserObject()) {
//						if (elementNode.getElement().getUserObject().getClass().toString().equals(object.getClass().toString())) {
//							objectList.add(elementNode.getElement().getUserObject());
//						}
//					}
//				}
//			}
			//2014-11-12 张坤修改			
			List<Element> allSelectElement = tree.getDataBox().getSelectionModel().getAllSelectedElement();	
			if (allSelectElement != null) {
				for (Element element : allSelectElement) {
//					elementNode = (ElementNode) treePath.getLastPathComponent();
					if (null != element.getUserObject()) {
						if (element.getUserObject().getClass().toString().equals(object.getClass().toString())) {
							objectList.add(element.getUserObject());
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
//			treePaths = null;
//			elementNode = null;
		}
		return objectList;
	}
//	private List getSelect(Object object) throws Exception {
//		TreePath[] treePaths = null;
//		ElementNode elementNode = null;
//		List objectList = new ArrayList();
//		try {
//			treePaths = tree.getSelectionPaths();
//			if (treePaths != null) {
//				for (TreePath treePath : treePaths) {
//					elementNode = (ElementNode) treePath.getLastPathComponent();
//					if (null != elementNode.getElement().getUserObject()) {
//						if (elementNode.getElement().getUserObject().getClass().toString().equals(object.getClass().toString())) {
//							objectList.add(elementNode.getElement().getUserObject());
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			treePaths = null;
//			elementNode = null;
//		}
//		return objectList;
//	}

	/**
	 * 设置树等等级
	 * 
	 * @param level
	 *            加载等级 1=域级别 2=网元级别 3=板卡级别 4=端口级别5=tunnel6=pw
	 */
	public void setLevel(int level) {
		try {
			this.level = level;
			this.createAllTopo.setLevel(this.level);
			this.initData();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	
	public void setLevel(int level, List<String> typeList, int isNNI) {
		try {
			
			this.isNNI=isNNI;
			this.typeList=typeList;
			this.level=level;
			this.createAllTopo.setLevel(this.level);
			this.initData();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	

	
	/**
	 * 验证是否有选中的。
	 * 
	 * @return true 有选中 false 没有
	 * @throws Exception
	 */
	public boolean verifySelect() throws Exception {
		boolean result = false;
		Object object = null;
		try {
			// 根据树展现不同的等级 设置不同的参数
			if (this.level == 2) {
				object = new SiteInst();
			} else if (this.level == 3) {
				object = new CardInst();
			} else if (this.level == 4) {
				object = new PortInst();
			}

			// 通过参数调用选择方法。如果返回的结果集大于0 说明有选中
			if (null != object) {
				if (this.getSelect(object).size() > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			object = null;
		}
		return result;
	}

	/**
	 * 根据不同的类型，获取tree上选择的类型主键 如site就是siteinst的主键
	 * 
	 * @param type
	 *            site、slot、card、port 其他返回异常
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getPrimaryKeyList(String type) throws Exception {

		if (null == type || "".equals(type)) {
			throw new Exception("type is null");
		}

		List<Integer> integerList = new ArrayList<Integer>();
		try {
			if ("site".equals(type)) {
				for (SiteInst siteInst : this.getSelectSiteInst()) {
					integerList.add(siteInst.getSite_Inst_Id());
				}
			} else if ("slot".equals(type)) {
				for (SlotInst slotInst : this.getSelectSlotInst()) {
					integerList.add(slotInst.getId());
				}
			} else if ("card".equals(type)) {
				for (CardInst cardInst : this.getSelectCardInst()) {
					integerList.add(cardInst.getId());
				}
			} else if ("port".equals(type)) {
				for(PortInst portInst: this.getSelectPortInst()){
					integerList.add(portInst.getPortId());
				}
			} 
			else {
				throw new Exception("type 参数错误");
			}

		} catch (Exception e) {
			throw e;
		}
		return integerList;
	}

	/**
	 * 搜索文本框值改变事件
	 * 
	 * @throws Exception
	 */
	private void txtSearchValueChange() {
		String searchTxt = null;
		Iterator iterator = null;
		Element element = null;
		String elementName = null;
		try {
			searchTxt = this.txtSearch.getText().trim();
			iterator = this.tDataBox.getAllElements().iterator();
			while (iterator.hasNext()) {
				element = (Element) iterator.next();
				if (searchTxt.equals("")) {
					element.setSelected(false);
				} else {

					elementName = element.getName();
					if (elementName.contains(searchTxt) && !element.isSelected()) {
						element.setSelected(true);
					} else if (!elementName.contains(searchTxt) && element.isSelected()) {
						element.setSelected(false);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally{
			searchTxt = null;
			iterator = null;
			element = null;
			elementName = null;
		}

	}

	/**
	 * 获取所有选中的集合
	 * @return
	 */
	public List<Element> getElement(){
		return this.tDataBox.getSelectionModel().getAllSelectedElement();
	}
	
	/**
	 * 修改时，刷新网元树（包括已经选中的）
	 * @return
	 */
	public List<Element> getAllElement(){
		return this.tDataBox.getAllElements();
	} 
//	/**
//	 * 选中的性能类型  最大个数  max-1
//	 * @param max
//	 *    不能超过 max
//	 * @return
//	 */
//	public boolean getMax(int max){
//		boolean flag=false;
//		List<Capability> capabilityList=null;
//		try{
//			capabilityList=this.getSelectCapability();
//			if(capabilityList!=null&&capabilityList.size()<=max){
//				flag=true;
//			}
//		}catch (Exception e) {
//			ExceptionManage.dispose(e, this.getClass());
//		} finally{
//		
//		}
//		return flag;
//	}
	/**
	 * 清空所有选中的复选框
	 */
	public void clear(){
		this.tDataBox.getSelectionModel().clearSelection();
	}
	public void clearBox(){
		this.tDataBox.clear();
	}
	public SiteInst getSiteInst() {
		return siteInst;
	}
	public void setSiteInst(SiteInst siteInst) {
		this.siteInst = siteInst;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getIsNNI() {
		return isNNI;
	}
	public void setIsNNI(int isNNI) {
		this.isNNI = isNNI;
	}
	public List<SiteInst> getSiteInstList() {
		return siteInstList;
	}
	public void setSiteInstList(List<SiteInst> siteInstList) {
		this.siteInstList = siteInstList;
	}
	public TDataBox gettDataBox() {
		return tDataBox;
	}
	public void settDataBox(TDataBox tDataBox) {
		this.tDataBox = tDataBox;
	}
	public TTree getTree() {
		return tree;
	}
	public void setTree(TTree tree) {
		this.tree = tree;
	}
	
}
