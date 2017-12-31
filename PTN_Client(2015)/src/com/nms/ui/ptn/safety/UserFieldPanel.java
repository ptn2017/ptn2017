package com.nms.ui.ptn.safety;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import twaver.Dummy;
import twaver.Element;
import twaver.Node;
import twaver.SubNetwork;
import twaver.TDataBox;
import twaver.tree.TTree;

import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.NetService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.topology.util.CreateElementUtil;

/**
 * 域 显示（树）
 * 
 * @author sy
 * 
 */
public class UserFieldPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TDataBox box = null;// 数据容器
	private TTree tree = null;// 树 对象
	private JScrollPane scrollPane = null; // 带滚动条的panel
	private CreateElementUtil createElementUtil = null;

	public UserFieldPanel() {
		this.initComponent();
		this.setLayout();
		this.initData();
	}
	public void initComponent() {
		this.box = new TDataBox(ResourceUtil.srcStr(StringKeysLbl.LBL_FIELD_LIST));
		this.tree = new TTree(this.box);
		this.tree.setDataBoxIconURL(null);
		this.scrollPane = new JScrollPane(this.tree);
		this.createElementUtil = new CreateElementUtil();
	}
	
	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 0 };
		layout.columnWeights = new double[] { 0.1 };
		layout.rowHeights = new int[] {250 };
		layout.rowWeights = new double[] { 0.1 };
		this.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		layout.addLayoutComponent(this.scrollPane, c);
		this.add(scrollPane);
	}
	
	/**
	 * 初始化界面
	 *            域集合
	 * @throws Exception
	 */
	public void initData()  {
		NetService_MB userNetworkService = null;
		List<NetWork> userFieldList = null;
		try {
			userNetworkService = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			userFieldList = userNetworkService.select();
			
			if (null != userFieldList && userFieldList.size() > 0) {
				// 遍历域集合
				this.createTopo(userFieldList);
				this.tree.expandAll();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(userNetworkService);
		}
	}
	
	/**
	 * 获取所有选中的菜单
	 * 
	 * @return 选中的菜单集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<NetWork> getSelectUserField() throws Exception {
		List<NetWork> roleManageList = null;
		List<Element> elementList = null;
		try {
			roleManageList = new ArrayList<NetWork>();
			elementList = this.box.getAllElements();
			// 遍历box上面所有元素
			for (Element element : elementList) {
				// 如果元素为Node
				if (element instanceof Node) {
					// 如果是选中的，就放入结果集中。
					if (element.isSelected() && element.getUserObject() instanceof NetWork) {
						NetWork netWork = (NetWork) element.getUserObject();
						roleManageList.add(netWork);
						List<Element> nodeList = element.getChildren();
						if(nodeList != null){
							List<Field> fieldList = new ArrayList<Field>();
							for (Element e : nodeList) {
								// 如果元素为Node
								if (e instanceof Node && e.getUserObject() instanceof Field) {
									if(e.isSelected()){
										fieldList.add((Field)e.getUserObject());
									}
								}
							}
							if(fieldList.size() > 0){
								netWork.setFieldList(fieldList);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			elementList = null;
		}
		return roleManageList;
	}
	
	/**
	 * 选中数据
	 * 
	 * @param userFieldList
	 *            要选中的域集合
	 * @throws Exception
	 */
	public void checkData(List<NetWork> userFieldList, List<Integer> fieldList) throws Exception {
		try {
			if (userFieldList.size() > 0) {
				for (NetWork roleManage : userFieldList) {
					this.checkNode(roleManage, fieldList);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据权限对象选中树节点
	 * 
	 * @param userField
	 *            权限对象
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void checkNode(NetWork userField, List<Integer> fieldList) throws Exception {
		List<Element> elementList = null;
		NetWork userFieldNode = null;
		try {
			elementList = this.box.getAllElements();
			// 遍历box中所有元素
			for (Element element : elementList) {
				// 如果元素为node
				if (element instanceof Node) {
					// 如果此元素的userObject属性不为null 并且为Field对象
					if (null != element.getUserObject() && element.getUserObject() instanceof NetWork) {
						userFieldNode = (NetWork) element.getUserObject();
						// 如果此node的Field对象和入参的主键相同。 把此node设为选中 并且跳出循环
						if (userFieldNode.getNetWorkId() == userField.getNetWorkId()) {
							element.setSelected(true);
						}
						List<Element> eList = element.getChildren();
						if(eList != null){
							for(Element e : eList){
								if(null != e.getUserObject() && e.getUserObject() instanceof Field){
									for(Integer fId : fieldList){
										Field field = (Field) e.getUserObject();
										if(fId == field.getId()){
											e.setSelected(true);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			elementList = null;
			userFieldNode = null;
		}
	}
	/**
	 * 设置树的 初始 显示  
	 * @param isAll
	 */
	public void initTree(UserInst userInst)throws Exception{
		if(null==userInst){
			throw new Exception("userInst is null");
		}
		//新建时
		if(userInst.getUser_Id()==0){
			//树  可以选择的
			this.tree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);
		}
		//修改
		else {
//			if(userInst.getIsAll()==0){
				treeEnable(true);
//			}
		}
		
	}
	/**
	 * 设置树的可用性。当前台点击是否查看所有域时，此树的可用性为false
	 * 
	 * @param flag
	 *            是否可用
	 */
	@SuppressWarnings("unchecked")
	public void treeEnable(boolean flag) {
		//为所有域时
		boolean isSelect=false;
		List<Element> elementList = null;
		if (flag) {
			this.tree.setTTreeSelectionMode(TTree.CHECK_DESCENDANT_ANCESTOR_SELECTION);	
		} else {
			this.tree.setTTreeSelectionMode(TTree.DEFAULT_SELECTION);
			isSelect=true;
		}
		try {
			elementList = this.box.getAllElements();
			// 遍历界面上所有元素
			for (Element element : elementList) {
				if (element instanceof Node) {
					element.setSelected(isSelect);
				}
			}
		}catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	public TDataBox getBox() {
		return box;
	}
	public void setBox(TDataBox box) {
		this.box = box;
	}

	public TTree getTree() {
		return tree;
	}
	public void setTree(TTree tree) {
		this.tree = tree;
	}

	private void createTopo(List<NetWork> netWorks) {
		try {
			this.box.clear();
			for (int i = 0; i < netWorks.size(); i++) {
				this.createGroupContent(netWorks.get(i));	
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void createGroupContent(NetWork netWork) {
		SubNetwork subNetwork = null;
		FieldService_MB fieldService = null;
		List<Field> groups = null;
		try {
			// 创建域对象
			subNetwork = this.createElementUtil.createSubNetwork(netWork);
			this.box.addElement(subNetwork);
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			groups = fieldService.queryByNetWorkid(netWork.getNetWorkId());
			for (int i = 0; i < groups.size(); i++) {
				Dummy dummy = new Dummy();
				dummy.setUserObject(groups.get(i));
				dummy.setName(groups.get(i).getFieldName());
				dummy.setParent(subNetwork);
				this.box.addElement(dummy);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(fieldService);
		}
	}
}
