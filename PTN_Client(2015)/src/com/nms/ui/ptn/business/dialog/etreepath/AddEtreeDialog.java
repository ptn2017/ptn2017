/**
 * AddPWDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package com.nms.ui.ptn.business.dialog.etreepath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.PopupMenuGenerator;
import twaver.TUIManager;
import twaver.TView;
import twaver.TWaverUtil;
import twaver.network.TNetwork;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.CommonBean;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.eth.VplsInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ViewDataTable;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.AutoNamingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.TopoAttachment;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysOperaType;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.dialog.tunnel.TunnelTopoPanel;
import com.nms.ui.ptn.business.etree.EtreeBusinessPanel;
import com.nms.ui.ptn.ne.ac.view.AcListDialog;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

/**
 * 新建Etree对对话框
 * 
 * @author __USER__
 */
public class AddEtreeDialog extends PtnDialog {

	/**
	 * 
	 * @since Ver 1.1
	 */

	private static final long serialVersionUID = -4639610687746114209L;
	int rootId = 0;
	private EtreeBusinessPanel etreeBusPanel;
	List<Node> selBranchNodeList = new ArrayList<Node>();//只要选择了叶子节点的所有数据（包括（选择了端口和没选择端口））
	ControlKeyValue rootAcAndSiteInst = null;
	private ViewDataTable<AcPortInfo> branchAcTable; // 选择的叶子table 叶子节点的所有数据（包括（选择了端口）�?	
	private ViewDataTable<PwInfo> pwInfoTable; // 选择的pwtable
	private final String ACTABLENAME = "selectAcList"; // AC列表的名�?	
	private final String PWTABLENAME = "selectPwList"; // pw列表的名�?	
	private JScrollPane jscrollPane_ac; // ac滚动条面�?	
	private JScrollPane jscrollPane_pw; // pw滚动条面�?	
	private List<EtreeInfo> etreeInfoList_update = null; // 要修改的etree集合。新建时此对象为null
	private int rootAcId = 0; // 根ACID 修改界面加载时，给此属性赋值。修改时通过此属性验证是否对根AC做了改变
	private TunnelTopoPanel tunnelTopoPanel=null;
	private String nameBefore = null;//日志记录需�?
	/**
	 * 构造方�?	 * 
	 * @param etreeBusinessPanel
	 *            panel对象，用来刷新列表用
	 * @param modal
	 *            是否为模式窗�?	 * @param etreeInfoList
	 *            要修改的etree数据，新建时传入null
	 */
	public AddEtreeDialog(EtreeBusinessPanel etreeBusinessPanel, boolean modal, List<EtreeInfo> etreeInfoList) {

		try {
			if (null == etreeInfoList) {
				super.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_CREATE_ETREE));
			} else {
				super.setTitle(ResourceUtil.srcStr(StringKeysOperaType.BTN_ETREE_UPDATE));
				this.etreeInfoList_update = etreeInfoList;
				this.rootAcId = etreeInfoList.get(0).getaAcId();
				this.nameBefore = etreeInfoList.get(0).getName();
			}
			this.setModal(modal);
			this.etreeBusPanel = etreeBusinessPanel;
			tunnelTopoPanel=new TunnelTopoPanel();
			this.initComponents();
			this.setLayout();
			this.setBtnListeners();
			this.clientComboxData();
			this.showPwTopo();
			this.initData();
			this.setLocation(UiUtil.getWindowWidth(this.getWidth()), UiUtil.getWindowHeight(this.getHeight()));
			this.setVisible(true);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 修改时，初始化数�?	 * 
	 * @throws Exception
	 */
	private void initData() throws Exception {
		EtreeInfo etreeInfo = null;
		SiteService_MB siteService = null;
		SiteInst siteInst_root = null; // 根网元对�?		
		AcPortInfo acPortInfo_root = null; // 根AC对象
		AcPortInfoService_MB acInfoService = null;
		UiUtil uiutil = null;
		try {
			if (null != this.etreeInfoList_update && this.etreeInfoList_update.size() > 0) {
				// 取第一条数�?				
				uiutil = new UiUtil();
				etreeInfo = this.etreeInfoList_update.get(0);

				// 根据根AC主键查询根端口对�?				
				acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				acPortInfo_root = acInfoService.selectById(uiutil.getAcIdSets(etreeInfo.getAmostAcId()).iterator().next());

				// 查询根网元对�?				
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				siteInst_root = siteService.select(etreeInfo.getRootSite());

				this.rootAcAndSiteInst = new ControlKeyValue(acPortInfo_root.getId() + "", "", siteInst_root);
				// 并且给name、根端口、客户信息赋�?				
				this.rootTextField.setText(acPortInfo_root.getName());
				this.etreeNameTextField.setText(etreeInfo.getName());
				this.activeCheckBox.setSelected(etreeInfo.getActiveStatus() == EActiveStatus.ACTIVITY.getValue() ? true : false);
				// 如果选择了客户信息，界面显示选中的客户信�?				
				if (etreeInfo.getClientId() > 0) {
					super.getComboBoxDataUtil().comboBoxSelect(this.clientComboBox, etreeInfo.getClientId() + "");
				}

				// 绑定叶子端口列表�?				
				this.initAcListData(acInfoService);
				// 绑定pw列表�?				
				this.initPwListData();
				// 绑定拓扑数据
				this.initTopoData();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(siteService);
			uiutil= null;
		}
	}

	/**
	 * 修改时初始化拓扑数据，link为蓝色，网元有标�?	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initTopoData() throws Exception {
		List<Element> elementList = null;
		int pwid = 0;
		Link link = null;
		try {
			// 获取拓扑中全部element
			elementList = tunnelTopoPanel.getNetWork().getDataBox().getAllElements();

			for (Element element : elementList) {
				// 如果元素为link，验证pw是否存在，如果存在则把此pw变色，并且标注此link的from、to的node
				if (element instanceof Link) {
					link = (Link) element;
					pwid = ((PwInfo) link.getUserObject()).getPwId();
					if (this.isPwInEtree(pwid)) {
						link.putLinkColor(Color.BLUE);
						this.setNodeBusiness(link.getFrom());
						this.setNodeBusiness(link.getTo());
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			elementList = null;
			link = null;
		}
	}

	/**
	 * 设置node的business属�?即显示的提示语，root或branch 修改etree时，加载etree的的拓扑，在拓扑标注根与叶子时用�?	 * 
	 * @param node
	 *            要设置的node
	 * @throws Exception
	 */
	private void setNodeBusiness(Node node) throws Exception {
		int siteId = 0;
		int rootSiteId = 0;
		try {
			// 获取根的网元ID
			rootSiteId = this.etreeInfoList_update.get(0).getRootSite();
			// 如果不存在business属�?才添�?			
			if (null == node.getBusinessObject() || "".equals(node.getBusinessObject())) {
				siteId = ((SiteInst) node.getUserObject()).getSite_Inst_Id();
				if (siteId == rootSiteId) {
					node.setBusinessObject("root");
				} else {
					node.setBusinessObject("branch");
					// 把叶子节点添加到集合中�?					
					this.getSelBranchNodeList().add(node);
				}
				node.addAttachment("topoTitle");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 初始化AC叶子table数据
	 * 
	 * @throws Exception
	 */
	private void initAcListData(AcPortInfoService_MB acInfoService) throws Exception {
		List<AcPortInfo> acPortInfoList = null;
		UiUtil uiutil = null;
		List<Integer> acIds = null;
		Set<Integer> acIdsSet = null;
		try {
			acPortInfoList = new ArrayList<AcPortInfo>();
			uiutil = new UiUtil();
			acIdsSet = new HashSet<Integer>();
			for (EtreeInfo etreeInfo : this.etreeInfoList_update) {
				acIdsSet.addAll(uiutil.getAcIdSets(etreeInfo.getAmostAcId()));
				acIdsSet.addAll(uiutil.getAcIdSets(etreeInfo.getZmostAcId()));
			}
			acIds = new ArrayList<Integer>(acIdsSet);
			if(null != acIds && acIds.size() >0)
			{
				acPortInfoList = acInfoService.select(acIds);
				this.branchAcTable.initData(acPortInfoList);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			acPortInfoList = null;
			uiutil = null;
			acIds = null;
			acIdsSet = null;
		}
	}

	/**
	 * 初始化PW的table数据
	 * 
	 * @throws Exception
	 */
	private void initPwListData() throws Exception {
		PwInfoService_MB pwInfoService = null;
		List<Integer> pwIds = new ArrayList<Integer>();
		List<PwInfo> pwInfoList = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			for (EtreeInfo etreeInfo : this.etreeInfoList_update) {
				pwIds.add(etreeInfo.getPwId());
			}
			pwInfoList = pwInfoService.selectServiceIdsByPwIds(pwIds);

			this.pwInfoTable.initData(pwInfoList);

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
		}
	}

	/**
	 * 添加按钮事件
	 */
	private void setBtnListeners() {
		// 自动命名事件
		this.jButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonActionPerformed();
			}
		});

		// 保存按钮事件
		this.oKButton.addActionListener(new MyActionListener() {

			@Override
			public boolean checking() {
				// 因为保存按钮显示等待条属性为false，所以此方法不执行，返回默认�?				
				return checkValue();
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				oKButtonActionPerformed();

			}
		});
		
		 tunnelTopoPanel.getNetWork().addElementClickedActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Element element = (Element) e.getSource();
					if(element!=null&&element instanceof Link){
						if(element.getUserObject()!=null&&element.getBusinessObject()==null){
							TUIManager.registerAttachment("SegmenttopoTitle", TopoAttachment.class,1, (int) element.getX(), (int) element.getY());
							PwInfo pwinfo =  (PwInfo)element.getUserObject();
							element.setBusinessObject(pwinfo.getPwName());
							element.addAttachment("SegmenttopoTitle");
						}else{
							element.removeAttachment("SegmenttopoTitle");
							element.setBusinessObject(null);
						}    
					}
				}
			});
	}

	/**
	 * 加载拓扑图时 获取pw集合
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PwInfo> getPwList() throws Exception {
		List<PwInfo> pwinfoList = null;
		PwInfo pwInfo = null;
		PwInfoService_MB pwService = null;
		ListingFilter filter = null;
		List<PwInfo> pwinfoList_result = null;
		try {
			pwinfoList_result = new ArrayList<PwInfo>();
			// 如果etreeinfolist为null，说明是新建操作，拓扑加载所有pw
			if (null == this.etreeInfoList_update) {
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				pwInfo = new PwInfo();
				pwInfo.setType(EPwType.ETH);
				// list过滤器，过滤掉没有权限的数据
				filter = new ListingFilter();
				pwinfoList = (List<PwInfo>) filter.filterList(pwService.selectbyType(pwInfo)); // 查询所有pw
				for (PwInfo info : pwinfoList) {
					// 如果是pw被使用，从集合中移除
					if (info.getRelatedServiceId() == 0) {
						pwinfoList_result.add(info);
					}
				}
			} else {
				// 修改操作，加载此根下的所有pw�?				this.getRootPw(pwinfoList_result);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			pwInfo = null;
			UiUtil.closeService_MB(pwService);
			filter = null;
			pwinfoList = null;
		}
		return pwinfoList_result;

	}

	/**
	 * 获取根下的所有pw
	 * 
	 * @param pwinfoList_result
	 *            pw结果集，把等到的结果放入此对象中
	 * @return
	 * @throws Exception
	 */
	private void getRootPw(List<PwInfo> pwinfoList_result) throws Exception {
		List<PwInfo> pwInfoList = null;
		int siteId = 0;
		PwInfoService_MB pwService = null;
		try {
			// 根据网元ID查询pw集合
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			siteId = this.etreeInfoList_update.get(0).getRootSite();
			pwInfoList = pwService.select(siteId);

			for (PwInfo pwInfo : pwInfoList) {
				// 如果pw没被使用，或者pw在要修改的etree中存在，就显示到拓扑�?				
				// 已经被使用，并且在etree中存在�?是要在拓扑中显示�?				
				if (pwInfo.getRelatedServiceId() == 0 || this.isPwInEtree(pwInfo.getPwId())) {
					if(pwInfo.getType() != null && pwInfo.getType() == EPwType.ETH){
						pwinfoList_result.add(pwInfo);
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			pwInfoList = null;
			UiUtil.closeService_MB(pwService);
		}
	}

	/**
	 * 验证pw是否在etree中存�?	 * 
	 * @return true 存在 false 不存�?	 * @throws Exception
	 */
	private boolean isPwInEtree(int pwid) throws Exception {
		boolean flag = false;
		try {

			for (EtreeInfo etreeInfo : this.etreeInfoList_update) {
				if (etreeInfo.getPwId() == pwid) {
					flag = true;
					break;
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 将有pw的隧道显示在拓扑图中
	 * 
	 * @throws Exception
	 */
	public void showPwTopo() throws Exception {
		List<PwInfo> pwInfoList = null;
		TNetwork network = null;
		try {
			// 获取pw集合并且在拓扑中画出所有pw路径及网�?			
			pwInfoList = this.getPwList();
			tunnelTopoPanel.boxDataByPws(pwInfoList);
			// 设置拓扑的自动布局
			network = tunnelTopoPanel.getNetWork();
//			network.doLayout(TWaverConst.LAYOUT_CIRCULAR);
			// 加载etree的右键菜�?			
			this.setMenu();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			pwInfoList = null;
			network = null;
		}
	}

	/**
	 * 加载etree的菜�?	 */
	private void setMenu() {
		TNetwork network = tunnelTopoPanel.getNetWork();

		// 设置拓扑的右键菜�?		
		network.setPopupMenuGenerator(new PopupMenuGenerator() {
			@Override
			public JPopupMenu generate(TView tview, MouseEvent mouseEvent) {
				JPopupMenu menu = new JPopupMenu();
				AddEtreeMenu addEtreeMenu = new AddEtreeMenu(AddEtreeDialog.this, tview);
				Link link = null;
				try {
					if (!tview.getDataBox().getSelectionModel().isEmpty()) {
						final Element element = tview.getDataBox().getLastSelectedElement();
						if (element instanceof Node) {
							// 如果此node没有设置根或者叶子，加载菜单�?设置根节点、设置叶子节�?							
							if (element.getBusinessObject() == null || "".equals(element.getBusinessObject().toString())) {
								// 如果不是修改，才显示设置根菜单，因为修改时，只能修改叶子，不能修改根
								if (null == etreeInfoList_update) {
									menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_ROOT, element,etreeInfoList_update));
								}
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_LEAF, element,etreeInfoList_update));
								TWaverUtil.clearImageIconCache();
							} else { // 否则加载取消设置、选择端口菜单
								// 如果不是修改，才显示取消设置菜单，因为修改时，只能修改叶子，不能修改�?								
								if (null == etreeInfoList_update) {
									menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_CANEL_CONFIG, element,etreeInfoList_update));
								} else {
									if (element.getBusinessObject() != null && !element.getBusinessObject().equals("root")) {
										menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_CANEL_CONFIG, element,etreeInfoList_update));
									}
								}
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_PORT, element,etreeInfoList_update));
								TWaverUtil.clearImageIconCache();
							}

						} else if (element instanceof Link) {
							// 如果是link 并且颜色是绿�?说明没有被选中，加�?设置路径菜单
							link = (Link) element;
							if (link.getLinkColor() == Color.GREEN) {
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_SELECT_PATH, element,etreeInfoList_update));
							} else {
								// 否则加载取消设置菜单
								menu.add(addEtreeMenu.createMenu(StringKeysMenu.MENU_CANEL_CONFIG, element,etreeInfoList_update));
							}
						}

					}
				} catch (Exception e) {
					ExceptionManage.dispose(e, this.getClass());
				}
				return menu;
			}

		});
	}

	/**
	 * 移除根节�?	 */
	protected void clearFields() {
		rootTextField.setText("");
		rootAcAndSiteInst = null;
		selBranchNodeList.removeAll(selBranchNodeList);

	}

	/**
	 * 设置AC端口
	 * 
	 * @param acListDialog
	 *            ac界面对象
	 * @param siteElement
	 *            网元拓扑元素对象
	 * @throws Exception
	 */
	protected void setport(AcListDialog acListDialog, Element siteElement) throws Exception {
		List<AcPortInfo>  acPortInfoList= null;
		List<AcPortInfo> acPortList = null;
		SiteInst siteInst = null;
		try {
			acPortInfoList = acListDialog.getAcPortInfoList();
			siteInst = (SiteInst) siteElement.getUserObject();
			
			if(acPortInfoList != null && !acPortInfoList.isEmpty())
			{
				if(acPortInfoList.size() > 10)
				{
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_EXCEEDACNUMBER));
					return;
				}
				
				if (((Node) siteElement).getBusinessObject().equals("root")) {
					rootAcAndSiteInst = new ControlKeyValue(acPortInfoList.get(0).getId() + "", "", siteInst);
					rootTextField.setText(acPortInfoList.get(0).getName());
				}
					
				// 如果有同网元的端口，清除掉。添加新�?				
				acPortList = new ArrayList<AcPortInfo>();
				for (AcPortInfo acPortInfo_table : this.branchAcTable.getAllElement()) {
					if(!isInsertTabel(acPortInfo_table.getSiteId(),acPortInfoList))
					{
						acPortList.add(acPortInfo_table);
					}
				}
				acPortList.addAll(acPortInfoList);
				this.branchAcTable.clear();
				this.branchAcTable.initData(acPortList);
			}
//			else
//			{
//				rootAcAndSiteInst = new ControlKeyValue(0 + "", "", siteInst);
//			}
			
		} catch (Exception e) {
			throw e;
		} finally {
		    acPortInfoList= null;
			acPortList = null;
		}
	}	
			
	private boolean isInsertTabel(int siteId,List<AcPortInfo> acPortList)
	{
		boolean flag = false;
		try 
		{
			for(AcPortInfo acPortInst :acPortList)
			{
				if (siteId == acPortInst.getSiteId()) {
					flag = true;
					break;
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}

	/**
	 * 自动命名按钮事件
	 */
	private void jButtonActionPerformed() {
		EtreeInfo etreeinfo = null;
		String autoNaming = null;
		try {
			etreeinfo = new EtreeInfo();
			etreeinfo.setIsSingle(0);
			etreeinfo.setaSiteId(ConstantUtil.siteId);
			AutoNamingUtil autoNamingUtil=new AutoNamingUtil();
			autoNaming = (String) autoNamingUtil.autoNaming(etreeinfo, null, null);
			this.etreeNameTextField.setText(autoNaming);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeinfo = null;
			autoNaming = null;
		}
	}
	/**
	 * 保存按钮事件
	 */
	private void oKButtonActionPerformed() {
		EtreeInfo etreeInfo = null;
		ControlKeyValue client = null;
		List<EtreeInfo> etreeInfoList = null;
		DispatchUtil etreeDisPatch = null;
		List<PwInfo> pwInfoList = null;
		List<AcPortInfo> branchAcList = null;
		String message = null; // 下发后返回的消息
		int action = 0; // 动作，加入日志时，动作是新建还是修改
		try {
			// 获取客户信息
			client = (ControlKeyValue) clientComboBox.getSelectedItem();
			pwInfoList = this.pwInfoTable.getAllElement();
			branchAcList = this.branchAcTable.getAllElement();

			SiteInst rootSiteInst = (SiteInst) this.rootAcAndSiteInst.getObject();
			etreeInfoList = new ArrayList<EtreeInfo>();
			// 遍历所有pw，有多少pw就有多少etree数据
			for (PwInfo pwInfo : pwInfoList) {
				etreeInfo = new EtreeInfo();
				etreeInfo.setPwId(pwInfo.getPwId());
				etreeInfo.setPwName(pwInfo.getPwName());
				etreeInfo.setActiveStatus(activeCheckBox.isSelected() ? EActiveStatus.ACTIVITY.getValue() : EActiveStatus.UNACTIVITY.getValue());
				etreeInfo.setAportId(pwInfo.getaPortConfigId());
				etreeInfo.setRootSite(rootSiteInst.getSite_Inst_Id());
				etreeInfo.setaSiteId(rootSiteInst.getSite_Inst_Id());
				// 如果根网元ID等于pw的A网元ID，说明A端是根则Z端就是叶
				if (rootSiteInst.getSite_Inst_Id() == pwInfo.getASiteId()) {
					etreeInfo.setBranchSite(pwInfo.getZSiteId());
					etreeInfo.setzSiteId(pwInfo.getZSiteId());
				} else if (rootSiteInst.getSite_Inst_Id() == pwInfo.getZSiteId()) {
					etreeInfo.setBranchSite(pwInfo.getASiteId());
					etreeInfo.setzSiteId(pwInfo.getASiteId());
				}
				// 遍历所有叶子AC集合
				mostAcString(branchAcList,etreeInfo);
				etreeInfo.setZportId(pwInfo.getzPortConfigId());
				etreeInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				if(activeCheckBox.isSelected()){
					etreeInfo.setActivatingTime(etreeInfo.getCreateTime());
				}else{
					etreeInfo.setActivatingTime(null);
				}
				etreeInfo.setCreateUser(ConstantUtil.user.getUser_Name());
				etreeInfo.setServiceType(EServiceType.ETREE.getValue());
				etreeInfo.setName(etreeNameTextField.getText());
				if (!"".equals(client.getId())) {
					etreeInfo.setClientId(Integer.parseInt(client.getId()));
				}
				etreeInfoList.add(etreeInfo);
			}
			// 如果是修改操作。把对象做转换，下发修改命令
			etreeDisPatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			if (null != this.etreeInfoList_update) {
				this.integrateEtreeList(etreeInfoList);
//				this.checkUpdateRoot();
				VplsInfo vplsBefore = this.getVplsBefore(null, client, 0);
				List<Integer> siteIdList_before = this.getSiteIdList_before(vplsBefore);
				message = etreeDisPatch.excuteUpdate(this.etreeInfoList_update);
				//日志记录
				nameBefore = etreeNameTextField.getText();
				VplsInfo vplsInfo = this.getVplsBefore(null, client, 0);
				List<Integer> siteIdList = this.getSiteIdList_before(vplsInfo);
				if(siteIdList_before.size() > siteIdList.size()){
					this.sortEtreeList(vplsBefore.getEtreeInfoList(), siteIdList);
					for (Integer siteId : siteIdList_before) {
						AddOperateLog.insertOperLog(oKButton, EOperationLogType.ETREEUPDATE.getValue(), message, vplsBefore, vplsInfo, siteId, vplsInfo.getVplsName(), "etree");
					}
				}else{
					this.sortEtreeList(vplsInfo.getEtreeInfoList(), siteIdList_before);
					for (Integer siteId : siteIdList) {
						AddOperateLog.insertOperLog(oKButton, EOperationLogType.ETREEUPDATE.getValue(), message, vplsBefore, vplsInfo, siteId, vplsInfo.getVplsName(), "etree");
					}
				}
			} else {
				message = etreeDisPatch.excuteInsert(etreeInfoList);
				//日志记录
				VplsInfo vplsInfo = this.getVplsBefore(etreeInfoList, client, 1);
				List<Integer> siteIdList = this.getSiteIdList_before(vplsInfo);
				for (Integer siteId : siteIdList) {
					AddOperateLog.insertOperLog(oKButton, EOperationLogType.ETREEINSERT.getValue(), message, null, vplsInfo, siteId, vplsInfo.getVplsName(), "etree");
				}
			}
			DialogBoxUtil.succeedDialog(this, message);
			TWaverUtil.clearImageIconCache();
			this.dispose();
			TWaverUtil.clearImageIconCache();
			this.etreeBusPanel.getController().refresh();

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 需要将List<EtreeInfo>排序，便于日志记录时比对
	 * 如：修改前是1,2,3三个网元，修改后�?,3两个网元，需要将之前的集合调整为1,3,2的顺�?	 * @param etreeInfoList需要调整顺序的集合
	 * @param siteIdList调整的顺�?	 */
	private void sortEtreeList(List<EtreeInfo> etreeInfoList, List<Integer> siteIdList) {
		List<EtreeInfo> etreeList = new ArrayList<EtreeInfo>();
		for (int siteId : siteIdList) {
			for (EtreeInfo etreeInfo : etreeInfoList) {
				if(etreeInfo.getBranchSite() == 0){
					etreeList.add(etreeInfo);
					break;
				}else{
					if(etreeInfo.getBranchSite() == siteId){
						etreeList.add(etreeInfo);
						break;
					}
				}
			}
		}
		for (EtreeInfo etreeInfo : etreeInfoList) {
			if(!siteIdList.contains(etreeInfo.getBranchSite()) && etreeInfo.getBranchSite() > 0){
				etreeList.add(etreeInfo);
			}
		}
		etreeInfoList.clear();
		etreeInfoList.addAll(etreeList);
	}

	private List<Integer> getSiteIdList_before(VplsInfo vplsBefore) {
		List<Integer> siteIdList = new ArrayList<Integer>();
		for (EtreeInfo etreeInfo : vplsBefore.getEtreeInfoList()) {
			if(etreeInfo.getBranchSite() == 0){
				siteIdList.add(etreeInfo.getRootSite());
			}else{
				if(!siteIdList.contains(etreeInfo.getBranchSite())){
					siteIdList.add(etreeInfo.getBranchSite());
				}
			}
		}
		return siteIdList;
	}

	/**
	 * 获取修改前的业务数据，便于日志记�?	 * @param type 0/1 需要查�?不需要查�?	 * @return
	 */
	private VplsInfo getVplsBefore(List<EtreeInfo> etreeInfoList, ControlKeyValue client, int type) {
		EtreeInfoService_MB service = null;
		SiteService_MB siteService = null;
		ClientService_MB clientService = null;
		PwInfoService_MB pwService = null;
		VplsInfo vplsInfo = new VplsInfo();
		try {
			EtreeInfo root = new EtreeInfo();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			if(type == 0){
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
				service = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
				etreeInfoList = service.selectAll(EServiceType.ETREE.getValue(), this.nameBefore);
				clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
				if(etreeInfoList.get(0).getClientId() > 0){
					vplsInfo.setClientName(clientService.select(etreeInfoList.get(0).getClientId()).get(0).getName());
				}
			}else{
				if(((Client)client.getObject()) != null){
					vplsInfo.setClientName(((Client)client.getObject()).getName());
				}
			}
			root.setRootName(siteService.getSiteName(etreeInfoList.get(0).getRootSite()));
			root.setBranchName(null);
			root.setRootSite(etreeInfoList.get(0).getRootSite());
			root.setAcNameList(this.getAcNameList(etreeInfoList.get(0).getAmostAcId()));
			for (EtreeInfo leaf : etreeInfoList) {
				leaf.setAcNameList(this.getAcNameList(leaf.getZmostAcId()));
				leaf.setRootName(null);
				leaf.setBranchName(siteService.getSiteName(leaf.getBranchSite()));
				if(type == 0){
					leaf.setPwName(pwService.selectByPwId(leaf.getPwId()).getPwName());
				}
			}
			etreeInfoList.add(root);
			vplsInfo.setVplsName(etreeInfoList.get(0).getName());
			vplsInfo.setActiveStatus(etreeInfoList.get(0).getActiveStatus());
			vplsInfo.setEtreeInfoList(etreeInfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
			UiUtil.closeService_MB(service);
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(clientService);
		}
		return vplsInfo;
	}

	/**
	 * 根据acId数组获取ac名称
	 * @param amostAcId
	 * @return
	 */
	private List<CommonBean> getAcNameList(String amostAcId) {
		AcPortInfoService_MB acService = null;
		List<CommonBean> acNameList = null;
		try {
			if(amostAcId != null){
				acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				List<Integer> acIdList = new ArrayList<Integer>();
				if(amostAcId.length() > 1){
					for (String id : amostAcId.split(",")) {
						acIdList.add(Integer.parseInt(id.trim()));
					}
				}else{
					acIdList.add(Integer.parseInt(amostAcId));
				}
				acNameList = new ArrayList<CommonBean>();
				List<AcPortInfo> acList = acService.select(acIdList);
				for (AcPortInfo acInfo : acList) {
					CommonBean acName = new CommonBean();
					acName.setAcName(acInfo.getName());
					acNameList.add(acName);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(acService);
		}
		return acNameList;
	}

	private void mostAcString(List<AcPortInfo> acPortInfoList,EtreeInfo etreeInfo)
	{
		List<Integer> acAIds = new ArrayList<Integer>();
		List<Integer> aczIds = new ArrayList<Integer>();
		try {
			
			for (AcPortInfo acPortInfo : acPortInfoList) {
				if(etreeInfo.getRootSite() == acPortInfo.getSiteId())
				{
					acAIds.add(acPortInfo.getId());
				}else if(etreeInfo.getBranchSite() == acPortInfo.getSiteId())
				{
					aczIds.add(acPortInfo.getId());
				}
			}
		   if(acAIds.size()>0)
		   {
			   etreeInfo.setAmostAcId(acAIds.toString().subSequence(1, acAIds.toString().length() -1).toString());
		   } 
		   if(aczIds.size()>0)
		   {
			   etreeInfo.setZmostAcId(aczIds.toString().subSequence(1, aczIds.toString().length() -1).toString());
		   }
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			 acAIds = null;
			 aczIds = null;
		}
	}
	
	
	/**
	 * 验证值的正确�?	 * @return
	 */

	private boolean checkValue() {
		boolean flag = false;
		ControlKeyValue client = null;
		List<PwInfo> pwInfoList = null;
		List<AcPortInfo> branchAcList = null;//所以叶子端口列表中的数�?		
		String beofreName = null; // etree的修改之前的名称�?		
		try {
			// 获取客户信息
			client = (ControlKeyValue) clientComboBox.getSelectedItem();
			pwInfoList = this.pwInfoTable.getAllElement();
			branchAcList = this.branchAcTable.getAllElement();

			// 如果etree存在，说明是修改操作。记录etree名称�?			
			if (null != this.etreeInfoList_update && this.etreeInfoList_update.size() > 0) {
				beofreName = this.etreeInfoList_update.get(0).getName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			// 验证名字是否重复
			if (verifyNameUtil.verifyName(EServiceType.ETREE.getValue(), etreeNameTextField.getText(), beofreName)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return false;
			}
			// 验证名字是否选择了根节点
			if (this.rootAcAndSiteInst == null) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_ROOT_PORT));
				return false;
			}
//			// 验证名字是否选择了根节点但是没选择端口
			if (null !=this.rootAcAndSiteInst && rootAcAndSiteInst.getId().equals("0")) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_ROOT_PORTERROR));
				return false;
			}
			// 验证是否选择了叶子节�?			
			if (this.getSelBranchNodeList().size() == 0) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_BRANCH));
				return false;
			}

			// 验证是否选择了叶子ac端口
//			if (branchAcList.size() != this.getSelBranchNodeList().size()) {
//				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_BRANCH_PORT));
//				return false;
//			}
           // 验证AC数量
			if((this.getSelBranchNodeList().size() == 0 || branchAcList.size()== 0)||verifyAc())
			{
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_BRANCH_PORT));
				return false;	
			}

			// 验证叶子ac端口和pw的个数是否相�?			
			if (pwInfoList.size() != this.getSelBranchNodeList().size()) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_PW_ERROR));
				return false;
			}

//			int rootAcId = Integer.valueOf(this.rootAcAndSiteInst.getId());
			SiteInst rootSiteInst = (SiteInst) this.rootAcAndSiteInst.getObject();

			// 验证界面上所有pw是否都指向同一个根
			if (!this.checkingPwRoot(pwInfoList, rootSiteInst)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_RONGPATH));
				return false;
			}

			// 验证pw路径与叶子ac是否匹配
			if (!this.pwAndAcMate(pwInfoList, branchAcList, rootSiteInst.getSite_Inst_Id())) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_PWANDACNOMATE));
				return false;
			}
			
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	
	   /***********验证AC的正确�?*****************/
	private boolean  verifyAc() 
	{
		boolean flga = false;
		SiteInst siteInst = null;
		try {
			for(Node siteNode : this.getSelBranchNodeList())
			{
				 siteInst = (SiteInst) siteNode.getUserObject();
				 if(!verifyExitAc(siteInst.getSite_Inst_Id()))
				 {
					 flga = true;
					 break;
				 }
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, this.getClass());
		}finally
		{
			siteInst = null;
		}
		return flga;
	}

	private boolean verifyExitAc(int siteId) 
	{
		boolean flag = false;
		try {
			for(AcPortInfo acPortInfo :this.branchAcTable.getAllElement())
			{
				if(siteId == acPortInfo.getSiteId())
				{
					flag = true;
					break;
				}
			}
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
	  return flag;
    }
	
	
	/**
	 * 验证etree中是否修改了根端口�?问题描述：如果叶子节点没有保留，全部删除后�?	 *  添加新叶子，此时。etree集合中的action都是2或�?，没有action=1 就没办法修改根的端口 此方法解决以上问题，
	 *  验证条件如下�?.如果集合中的action没有1或�?的情况下�?找出action=2的第一条记录�?	 *               2.验证此条记录的根端口是否与修改之前的根端口相等�?	 *               3.如果不相等，把之前的端口对象查询出来，给此etree记录的BeforeRootAc赋值�?	 * @throws Exception
	 */
	private void checkUpdateRoot() throws Exception {
		AcPortInfoService_MB acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
		try {
			int etreeIndex = -1; // etree新增对象的索引�?如果集合中有action�?�?的情况�?此索引为-1 不操作。否则操作etree新增对象
			for (int i = 0; i < this.etreeInfoList_update.size(); i++) {
				if (this.etreeInfoList_update.get(i).getAction() == 0 || this.etreeInfoList_update.get(i).getAction() == 1) {
					etreeIndex = -1;
					break;
				} else {
					if (this.etreeInfoList_update.get(i).getAction() == 2) {
						etreeIndex = i;
					}
				}
			}
			
			if (etreeIndex >= 0) {
				if (this.etreeInfoList_update.get(etreeIndex).getaAcId() != this.rootAcId) {
					//带写
//					this.etreeInfoList_update.get(etreeIndex).setBeforeRootAc(acInfoService.selectById(this.rootAcId));
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(acInfoService);
		}
	}

	/**
	 * 修改时，把界面新收集的etree和修改之前的etree进行整合
	 * 
	 * @param etreeInfoList_new
	 *            界面新收集的etree集合
	 * @throws Exception
	 */
	private void integrateEtreeList(List<EtreeInfo> etreeInfoList_new) throws Exception {

		EtreeInfo etreeInfo_update = null; // 匹配后的etree对象

		// 先把所有修改的etree数据改成删除状态，之后如果匹配到数据，会把状态改成其他状�?		
		for (EtreeInfo etreeInfo : this.etreeInfoList_update) {
			etreeInfo.setAction(3);
		}

		// 便利所有新收集的数据，
		for (EtreeInfo etreeInfo_new : etreeInfoList_new) {
			etreeInfo_update = this.findEtree(etreeInfo_new);
			// 如果找到的etree不为null 说明有匹配项
			if (null != etreeInfo_update) {
				this.integrateEtree(etreeInfo_update, etreeInfo_new);
			} else {
				// 如果为null 说明没有匹配项目，是新增操作�?				// 把创建时间和创建人修改成以前的数�?				
				etreeInfo_new.setCreateTime(this.etreeInfoList_update.get(0).getCreateTime());
				if(activeCheckBox.isSelected()){
					etreeInfo_new.setActivatingTime(this.etreeInfoList_update.get(0).getActivatingTime());
				}else{
					etreeInfo_new.setActivatingTime(null);
				}
				etreeInfo_new.setCreateUser(this.etreeInfoList_update.get(0).getCreateUser());
				etreeInfo_new.setServiceId(this.etreeInfoList_update.get(0).getServiceId());
				etreeInfo_new.setaXcId(this.etreeInfoList_update.get(0).getaXcId());
				etreeInfo_new.setAction(2); // 把此etree记录标识为新增数�?
				// 把此数据添加到要修改的etree集合中�?				
				this.etreeInfoList_update.add(etreeInfo_new);
			}
		}
	}
	
	/**
	 * 通过新的etree数据中的rootsite和branchsite去要修改的etree集合中找�?	 * 
	 * @param etreeInfo_new
	 *            新的etree数据
	 * @return 如果找到了，把找到的etree返回�?如果没找到，返回null
	 */
	private EtreeInfo findEtree(EtreeInfo etreeInfo_new) {

		for (EtreeInfo etreeInfo : this.etreeInfoList_update) {
			// 如果根网元和叶网元都相等，说明不是新增和删除操作�?			
			if (etreeInfo.getRootSite() == etreeInfo_new.getRootSite() && etreeInfo.getBranchSite() == etreeInfo_new.getBranchSite()) {
				return etreeInfo;
			}
		}
		return null;
	}

	/**
	 * 整合两个etree对象�?把新的etree值传入要修改的etree对象�?	 * 
	 * @param etreeInfo_update
	 *            要修改的etree对象
	 * @param etreeInfo_new
	 *            界面新收集的etree对象
	 * @throws Exception
	 */
	private void integrateEtree(EtreeInfo etreeInfo_update, EtreeInfo etreeInfo_new) throws Exception {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwinfo = null;
		AcPortInfoService_MB acInfoService = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			// 如果修改了pwId 取之前的pw对象，并且更新etreeInfo_update中的pw、beforepw、action=1字段
			if (etreeInfo_update.getPwId() != etreeInfo_new.getPwId()) {
				etreeInfo_update.setBeforePw(pwInfoService.selectByPwId(etreeInfo_update.getPwId()));
				etreeInfo_update.setPwId(etreeInfo_new.getPwId());
				etreeInfo_update.setAction(1);
			}
			// 如果修改了根端口 取之前的根端口对象，并且更新etreeInfo_update中的aAcId、BeforeRootAc、action=1字段
			if(null != etreeInfo_update.getAmostAcId()&& !isSame(etreeInfo_update.getAmostAcId(),etreeInfo_new.getAmostAcId()))
			{
				setBerforeAAcList(etreeInfo_update,etreeInfo_new.getAmostAcId(),1,etreeInfo_update.getAmostAcId());
				etreeInfo_update.setAmostAcId(etreeInfo_new.getAmostAcId());
				etreeInfo_update.setAction(1);
			}
			// 如果修改了叶子端�?取之前的叶子端口对象，并且更新etreeInfo_update中的zAcId、BeforeBranchAc、action=1字段
			if (null != etreeInfo_update.getZmostAcId()&&!isSame(etreeInfo_update.getZmostAcId(),etreeInfo_new.getZmostAcId())) 
			{
				setBerforeAAcList(etreeInfo_update,etreeInfo_new.getZmostAcId(),2,etreeInfo_update.getZmostAcId());
				etreeInfo_update.setZmostAcId(etreeInfo_new.getZmostAcId());
				etreeInfo_update.setAction(1);
			}
			// 赋其他修改参�?			
			etreeInfo_update.setName(etreeInfo_new.getName());
			etreeInfo_update.setActiveStatus(etreeInfo_new.getActiveStatus());
			etreeInfo_update.setClientId(etreeInfo_new.getClientId());
			// 如果action还等�? 说明上面三个条件没有成立，此时给此属性赋0=没有改变pw、根ac、叶ac
			if (etreeInfo_update.getAction() == 3) {
				etreeInfo_update.setAction(0);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(acInfoService);
			UiUtil.closeService_MB(pwInfoService);
		}
	}
	
	/**
	 * 给修改以前的AC赋�?	 * @param elanInfoAction
	 * @param mostAcId
	 * @param acIdList
	 * @param  label ==1:添加root节点 2添加支节�?	 */
	private void setBerforeAAcList(EtreeInfo etreeInfo_update,String mostAcId,int label,String oldMostAcId) 
	{
		String[] acIds = oldMostAcId.split(",");
		String[] acIdsUdate = mostAcId.split(",");
		Set<Integer> acSet = null;
		List<Integer> acList = null;
		AcPortInfoService_MB acInfoService = null;
		try {
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			acSet = new HashSet<Integer>();
			acList = new ArrayList<Integer>();
			for(String acId : acIds)
			{
				if(!isExist(acId.trim(),acIdsUdate))
				{
					acSet.add(Integer.parseInt(acId.trim()));
				}
			}
			acList = new ArrayList<Integer>(acSet);
			if(acList.size() >0 && label ==1 )
			{
				etreeInfo_update.setBeforeAAcList(acInfoService.select(acList));
			}else if(acList.size() >0 && label ==2 )
			{
				etreeInfo_update.setBeforeZAcList(acInfoService.select(acList));
			}else if((acIdsUdate.length > acIds.length)&&acList.isEmpty())
			{
				if(label ==1)
				{
					etreeInfo_update.setBeforeAAcList(new ArrayList<AcPortInfo>());
				}else
				{
					etreeInfo_update.setBeforeZAcList(new ArrayList<AcPortInfo>());
				}
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(acInfoService);
		}
	}

	private boolean isExist(String trim, String[] acIdsUdate) {
		boolean flag = false;
		try 
		{
			for(String acId : acIdsUdate)
			{
				if(acId.trim().equals(trim))
				{
					flag = true;
					break;
				}
			}
			
		} catch (Exception e) 
		{
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
  private boolean isSame(String updateAcString,String newAcString)
  {
	  boolean flag = false;
	  UiUtil uiutil = null;
	  Set<Integer> updateAcSet = null;
	  Set<Integer> newAcSet = null;
	  try 
	  {
        uiutil = new UiUtil();
        updateAcSet = uiutil.getAcIdSets(updateAcString);
        newAcSet = uiutil.getAcIdSets(newAcString);
        if(updateAcSet.size() == newAcSet.size())
        {
        	updateAcSet.removeAll(newAcSet);
            if(updateAcSet.size() == 0)
            {
            	flag = true;
            }	
        }
	  } catch (Exception e) 
	  {
		ExceptionManage.dispose(e, getClass());
	 }finally
	 {
		  uiutil = null;
		  updateAcSet = null;
		  newAcSet = null;
	 }
	return flag;
  }
	/**
	 * 验证所有pw是否都指向一个根网元
	 * 
	 * @param pwinfoList
	 *            所有选中的pw
	 * @param rootSiteInst
	 *            根网�?	 * @return true=�?false=�?	 * @throws Exception
	 */
	private boolean checkingPwRoot(List<PwInfo> pwinfoList, SiteInst rootSiteInst) throws Exception {

		boolean flag = true;
		try {

			for (PwInfo pwInfo : pwinfoList) {
				// 如果pw的A、Z两端网元都不是根我网元，返回false
				if (pwInfo.getASiteId() != rootSiteInst.getSite_Inst_Id() && pwInfo.getZSiteId() != rootSiteInst.getSite_Inst_Id()) {
					flag = false;
					break;
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 验证pw和叶子网元AC是否匹配
	 * 
	 * @param pwInfoList
	 *            所有pw路径
	 * @param acPortInfoList
	 *            叶子网元的ac集合
	 * @param rootSiteId
	 *            根网元id
	 * @return 通过验证true 没通过false
	 * @throws Exception
	 */
	private boolean pwAndAcMate(List<PwInfo> pwInfoList, List<AcPortInfo> acPortInfoList, int rootSiteId) throws Exception {

		boolean flag = true;
		List<Integer> siteIdList = null;
		try {
			// 把ac的所有网元放入集合中，做比较�?			
			siteIdList = new ArrayList<Integer>();
			for (AcPortInfo acPortInfo : acPortInfoList) {
				siteIdList.add(acPortInfo.getSiteId());
			}

			// 遍历pw,分别比较AZ端。如果有一端不在ac中�?说明验证不通过。返回false
			for (PwInfo pwInfo : pwInfoList) {

				if (pwInfo.getASiteId() != rootSiteId) {
					if (!siteIdList.contains(pwInfo.getASiteId())) {
						flag = false;
						break;
					}
				}

				if (pwInfo.getZSiteId() != rootSiteId) {
					if (!siteIdList.contains(pwInfo.getZSiteId())) {
						flag = false;
						break;
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			siteIdList = null;
		}
		return flag;
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() throws Exception {
		Dimension dimension = new Dimension(1200, 700);
		this.setSize(dimension);
		this.setMinimumSize(dimension);
		this.lblMessage = new JLabel();
		oKButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true,RootFactory.COREMODU,this);
		jButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysLbl.LBL_AUTO_NAME));
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel3 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
//		branchLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LEAF_PORT));
		branchLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_LIST));
		rootLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ROOT_PORT));
		rootTextField = new PtnTextField();
		rootTextField.setEditable(false);
		jLabel5 = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		activeCheckBox = new javax.swing.JCheckBox();
		pwlist = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_LIST));
		client = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CLIENT_NAME));
		clientComboBox = new javax.swing.JComboBox();
		etreeNameTextField = new PtnTextField(true, PtnTextField.STRING_MAXLENGTH, this.lblMessage, this.oKButton, this);

		// pw和ac叶子端口的tabel  
		this.branchAcTable = new ViewDataTable<AcPortInfo>(this.ACTABLENAME);//AC列表
		this.pwInfoTable = new ViewDataTable<PwInfo>(this.PWTABLENAME);//PW列表

		this.branchAcTable.getTableHeader().setResizingAllowed(true);
		this.branchAcTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.branchAcTable.setTableHeaderPopupMenuFactory(null);
		this.branchAcTable.setTableBodyPopupMenuFactory(null);

		this.pwInfoTable.getTableHeader().setResizingAllowed(true);
		this.pwInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		this.pwInfoTable.setTableHeaderPopupMenuFactory(null);
		this.pwInfoTable.setTableBodyPopupMenuFactory(null);

		jscrollPane_ac = new JScrollPane();
		jscrollPane_pw = new JScrollPane();

		jscrollPane_ac.setViewportView(this.branchAcTable);
		jscrollPane_pw.setViewportView(this.pwInfoTable);
		jSplitPane1.setRightComponent(tunnelTopoPanel);
	}

	private void setLayout() {
		this.add(this.jSplitPane1);
		this.jPanel3.setPreferredSize(new Dimension(260, 700));
		this.jSplitPane1.setLeftComponent(this.jPanel3);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 50, 160, 50 };
		layout.columnWeights = new double[] { 0, 0, 0 };
		layout.rowHeights = new int[] { 25, 30, 30, 30, 150, 150, 30, 15, 30, 30, 10};
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2 };
		this.jPanel3.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(this.lblMessage, c);
		this.jPanel3.add(this.lblMessage);

		/** 第一�?名称 */
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(jLabel2, c);
		this.jPanel3.add(jLabel2);
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(etreeNameTextField, c);
		this.jPanel3.add(etreeNameTextField);
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.jButton, c);
		this.jPanel3.add(this.jButton);
		/** 第二�?根端�?*/
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.rootLabel, c);
		this.jPanel3.add(this.rootLabel);
		c.gridx = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		layout.addLayoutComponent(this.rootTextField, c);
		this.jPanel3.add(this.rootTextField);

		/** 第三�?客户 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
//		c.anchor = GridBagConstraints.EAST;
//		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(this.client, c);
		this.jPanel3.add(this.client);
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
//		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.clientComboBox, c);
		this.jPanel3.add(this.clientComboBox);

		/** �?�?ac列表 */
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.branchLabel, c);
		this.jPanel3.add(this.branchLabel);
		c.gridx = 1;
		c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
//		c.anchor = GridBagConstraints.CENTER;
//		c.fill = GridBagConstraints.BOTH;
		layout.addLayoutComponent(this.jscrollPane_ac, c);
		this.jPanel3.add(this.jscrollPane_ac);
		/** �?�?pw列表 */
		c.gridx = 0;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
//		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(this.pwlist, c);
		this.jPanel3.add(this.pwlist);
		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
//		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.jscrollPane_pw, c);
		this.jPanel3.add(this.jscrollPane_pw);

		/** 第七�?*/
		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 10, 5, 5);
		layout.setConstraints(this.jLabel5, c);
		this.jPanel3.add(this.jLabel5);
		c.gridx = 1;
		c.gridy = 6;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.addLayoutComponent(this.activeCheckBox, c);
		this.jPanel3.add(this.activeCheckBox);

		/** 第八�?确定按钮 空出一�?*/
		c.gridx = 2;
		c.gridy = 8;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 10, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(this.oKButton, c);
		this.jPanel3.add(this.oKButton);
	}

	/**
	 * 客户信息下拉列表数据绑定
	 */
	public void clientComboxData() {

		ClientService_MB service = null;
		List<Client> clientList = null;
		DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) clientComboBox.getModel();
		try {
			service = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			clientList = service.refresh();
			defaultComboBoxModel.addElement(new ControlKeyValue("0", "", null));
			for (Client client : clientList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(client.getId() + "", client.getName(), client));
			}
			clientComboBox.setModel(defaultComboBoxModel);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JCheckBox activeCheckBox;
	private javax.swing.JLabel branchLabel;
	private javax.swing.JButton jButton;// 自动命名
	private javax.swing.JTextField etreeNameTextField;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JSplitPane jSplitPane1;
	private PtnButton oKButton;
	private javax.swing.JLabel rootLabel;
	private javax.swing.JTextField rootTextField;
	private javax.swing.JLabel pwlist;
	// End of variables declaration//GEN-END:variables
	private JLabel lblMessage;
	private JLabel client;
	private JComboBox clientComboBox;

	public int getRootId() {
		return rootId;
	}

	public void setRootId(int rootId) {
		this.rootId = rootId;
	}

	public ViewDataTable<PwInfo> getPwInfoTable() {
		return pwInfoTable;
	}

	public void setPwInfoTable(ViewDataTable<PwInfo> pwInfoTable) {
		this.pwInfoTable = pwInfoTable;
	}

	public ViewDataTable<AcPortInfo> getBranchAcTable() {
		return branchAcTable;
	}

	public void setBranchAcTable(ViewDataTable<AcPortInfo> branchAcTable) {
		this.branchAcTable = branchAcTable;
	}

	public List<Node> getSelBranchNodeList() {
		return selBranchNodeList;
	}

	public void setSelBranchNodeList(List<Node> selBranchNodeList) {
		this.selBranchNodeList = selBranchNodeList;
	}

	public ControlKeyValue getRootAcAndSiteInst() {
		return rootAcAndSiteInst;
	}

	public void setRootAcAndSiteInst(ControlKeyValue rootAcAndSiteInst) {
		this.rootAcAndSiteInst = rootAcAndSiteInst;
	}

}