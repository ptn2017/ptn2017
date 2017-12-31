package com.nms.ui.topology.util;

import java.util.ArrayList;
import java.util.List;

import twaver.Card;
import twaver.Dummy;
import twaver.Element;
import twaver.Group;
import twaver.Node;
import twaver.Port;
import twaver.SubNetwork;
import twaver.TDataBox;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.db.bean.system.user.UserField;
import com.nms.db.bean.system.user.UserInst;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.system.NetService_MB;
import com.nms.model.system.SubnetService_MB;
import com.nms.model.system.user.UserFieldService_MB;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;

/**
 * 创建整个拓扑
 * 
 * @author kk
 * 
 */
public class CreateAllTopo {

	private TDataBox tDataBox = null;
	private CreateElementUtil createElementUtil = null;
	private int level = 2; // 加载级别 1=域级别 2=网元级别 3=板卡级别 4=端口级别
	/**
	 * 创建一个新的实例
	 * 
	 * @param dataBox
	 *            twaver数据对象
	 * @param level
	 *            加载级别 1=域级别 2=网元级别 3=板卡级别 4=端口级别
	 */
	public CreateAllTopo(TDataBox dataBox, int level) {
		this.tDataBox = dataBox;
		this.createElementUtil = new CreateElementUtil();
		this.level = level;
	}

	/**
	 * 开始创建拓扑
	 * 
	 * @param isShowSubnet
	 *            是否显示子网
	 */
	public void createTopo(boolean isShowSubnet,boolean isShowCardName) {
		NetService_MB netService = null;
		List<NetWork> netWorks = null;
		UserFieldService_MB userFieldService = null;
		UserInstServiece_Mb userInstService = null;
		List<UserField> userFieldList = null;
		List<UserInst> userInstList = null;
		UserField userField = null;
		UserInst userInst = null;
		try {
			this.tDataBox.clear();
			// 查询出所有的域
			userField = new UserField();
			userInst = new UserInst();
			userField.setUser_id(ConstantUtil.user.getUser_Id());
			userInst.setUser_Id(ConstantUtil.user.getUser_Id());
			userFieldService = (UserFieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERFIELD);
			userInstService = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			netService = (NetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NETWORKSERVICE);
			userFieldList = userFieldService.queryUserField(userField);
			userInstList  = userInstService.select(userInst);			
			netWorks = netService.select();

				for (int i = 0; i < netWorks.size(); i++) {
					//勾选了查看所有域	
					if(userInstList.get(0).getIsAll() == 1){
					     this.createGroupContent(netWorks.get(i), isShowSubnet,isShowCardName, null);	
					}else{
						//没有勾选所有域时
						if(checkRoot(userFieldList, netWorks.get(i).getNetWorkId())){
							this.createGroupContent(netWorks.get(i), isShowSubnet,isShowCardName, userFieldList);
						}
					}
				}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(netService);
			UiUtil.closeService_MB(userFieldService);
			UiUtil.closeService_MB(userInstService);
		}
	}

	//检查用户是否查看域的权限
	private boolean checkRoot(List<UserField> userFieldList,int fieldId) {
		boolean flag = false;
		try {
			if(userFieldList!= null&& userFieldList.size()>0){
				for(UserField userField :userFieldList){
					if(fieldId == userField.getField_id()){						
						return true;
					}
				}
			}else{
				flag = false;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}
	
	//检查用户是否查看组的权限
	private boolean checkFieldRoot(List<UserField> userFieldList, int fieldId) {
		boolean flag = false;
		try {
			if(userFieldList != null && userFieldList.size() > 0){
				for(UserField userField : userFieldList){
					if(fieldId == userField.getSubId()){						
						return true;
					}
				}
			}else{
				flag = true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag;
	}

	/**
	 * 创建组对象和子网对象
	 * @param userFieldList 
	 * 
	 * @param field
	 */
	private void createGroupContent(NetWork netWork, boolean isShowSubnet,boolean isShowCardName, List<UserField> userFieldList) {
		SubNetwork subNetwork = null;
		FieldService_MB fieldService = null;
		SubnetService_MB subnetService = null;
		List<Field> fieldList_subnet = null; // 子网集合
		Group group = null;
		List<Field> groups = null;
		SiteService_MB siteService = null;
		List<SiteInst> siteInsts = null;
		SiteInst siteInst = null;
		try {
			// 创建域对象
			subNetwork = this.createElementUtil.createSubNetwork(netWork);
			this.tDataBox.addElement(subNetwork);
			// 如果级别大于1 说明要加载网元
			if (this.level > 1) {
				// 加载出所有子网中的网元
				fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
				subnetService = (SubnetService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SUBNETSERVICE);
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				groups = fieldService.queryByNetWorkid(netWork.getNetWorkId());
				for (int i = 0; i < groups.size(); i++) {
					if(this.checkFieldRoot(userFieldList, groups.get(i).getId())){
						// 检查有没有查看组的权限
						Dummy dummy = new Dummy();
						dummy.setUserObject(groups.get(i));
						dummy.setName(groups.get(i).getFieldName());
						dummy.setParent(subNetwork);
						this.tDataBox.addElement(dummy);
						fieldList_subnet = subnetService.querySiteByCondition(groups.get(i));
						siteInst = new SiteInst();
						siteInsts = siteService.queryByFiledId(groups.get(i).getId());
						if (null != fieldList_subnet && fieldList_subnet.size() > 0) {//遍历子网
							// 遍历子网。添加到拓扑中
							for (Field field_subnet : fieldList_subnet) {
								// 创建子网对象
								if (isShowSubnet) {
									group = this.createElementUtil.createGroup(field_subnet, dummy);
									this.tDataBox.addElement(group);
									this.createNode(field_subnet.getSiteInstList(), group,isShowCardName);
								} else {
									this.createNode(field_subnet.getSiteInstList(), dummy,isShowCardName);
								}
							}
						}
						//显示非子网下的网元
						this.createNode(siteInsts, dummy, isShowCardName);
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(subnetService);
			UiUtil.closeService_MB(fieldService);
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 拓扑批量创建网元
	 * 
	 * @param siteInstList
	 *            要创建的网元集合
	 * @param parentElement
	 *            父元素
	 * @throws Exception
	 */
	public void createNode(List<SiteInst> siteInstList, Element parentElement,boolean isShowCardName) throws Exception {
		Node node = null;
		if (null != siteInstList && siteInstList.size() > 0) {
			for (int i = 0; i < siteInstList.size(); i++) {
				node = this.createElementUtil.createNode(siteInstList.get(i), parentElement);
				this.tDataBox.addElement(node);
				// 如果级别大于2 说明是板卡级别 需要加载板卡
				if (this.level > 2) {
					this.createCard(node, true,null,0,false,isShowCardName);
				}
			}
		}
		node = null;
	}

	/**
	 * 加载板卡
	 * 
	 * @param node
	 *            网元节点对象
	 * @param isTmp
	 *            是否为临时的card
	 * @param  typeList  
	 * 			要加载的端口类型的集合 ，null  为全部显示
	 * @param isNNI
	 *		  ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	 *@param   isCapability
	 * 			是否显示性能类型
	 * @throws Exception
	 */
	public void createCard(Node node, boolean isTmp,List<String> typeList,int isNNI,boolean isCapability,boolean isShowName) throws Exception {
		Card card = null;
		SiteInst siteInst = null;
		CardService_MB cardService = null;
		List<CardInst> cardInstList = null;
		CardInst cardInst_select = null;
		Element element=null;
		try {
			//如果node只有一个节点，并且节点不是临时占位的板卡，则结束方法，不去多查一遍
			if (node.childrenSize() == 1) {
				element=(Element) node.getChildren().get(0);
				if(!"CARD_TMP".equals(element.getName())){
					return;
				}
			}
			// 清空此node的子集
			this.removeChild(node);
			// 如果是临时的，创建一个占位板卡 否则创建此网元下的所有板卡
			if (isTmp) {
				card = this.createElementUtil.createCard(null, node,isShowName);
				this.tDataBox.addElement(card);
			} else {
				siteInst = (SiteInst) node.getUserObject();

				// 根据网元查询出网元下所有板卡
				cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
				cardInst_select = new CardInst();
				cardInst_select.setSiteId(siteInst.getSite_Inst_Id());
				cardInstList = cardService.select(cardInst_select);

				for (CardInst cardInst : cardInstList) {
					if(!cardInst.getCardName().equals("FAN")&&!cardInst.getCardName().equals("PSU")
							&& !cardInst.getCardName().equals("PWR")){
						// 创建板卡
						card = this.createElementUtil.createCard(cardInst, node,isShowName);
						this.tDataBox.addElement(card);
						// 如果级别大于3 说明是端口级别 需要加载端口
						if (this.level > 3) {
							//创建端口
							this.createPort(card, isTmp,typeList,isNNI,isCapability);
						}
					}	
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			card = null;
			siteInst = null;
			UiUtil.closeService_MB(cardService);
			cardInstList = null;
			cardInst_select = null;
		}

	}
	
	/**
	 * 加载端口
	 * 
	 * @param node
	 *            板卡节点对象
	 * @param isTmp
	 *            是否为临时的card
	 * @param typeList         null (默认 全部显示，即：eth,pdh,sdh,lag)   
	 * 			要显示的端口类型的集合          
	 * @param isNNI
	 *   ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口 
	 *  @param  isCapability
	 *  	是否显示性能类型
	 * @throws Exception
	 */
	public void createPort(Card node, boolean isTmp,List<String> typeList,int isNNI ,boolean isCapability) throws Exception {
		Port port=null;
		PortService_MB portService = null;
		List<PortInst> portInstList = null;
		CardInst cardInst = null;
		Element element=null;
		SiteInst siteInst=null;
		PortInst portInst=null;
		Node ethNode=null;
		Node sdhNode=null;
 		Node pdhNode=null;
		Node lagNode=null;
		try {
			
			//如果node只有一个节点，并且节点不是临时占位的端口，则结束方法，不去多查一遍
			if (node!=null&&node.childrenSize() == 1) {
				element=(Element) node.getChildren().get(0);
				if(!"PORT_TMP".equals(element.getName())){
					return;
				}
			}
			// 清空此node的子集
			this.removeChild(node);

			// 如果是临时的，创建一个占位端口， 否则创建此板卡下的所有端口
			if (isTmp) {
				port = this.createElementUtil.createPort(null, node);
				this.tDataBox.addElement(port);
			} else {
				if(typeList!=null&&typeList.size()>0){
					for(String type:typeList){
						if(type.equals("ETH")){
							// 创建端口类型节点
							ethNode = this.createElementUtil.createPortType("ETH", node);
							this.tDataBox.addElement(ethNode);
						}else if(type.equals("SDH")){
							// 创建端口类型节点
							sdhNode = this.createElementUtil.createPortType("SDH", node);
							this.tDataBox.addElement(sdhNode);	
						}else if(type.equals("PDH")){
							// 创建端口类型节点
							pdhNode = this.createElementUtil.createPortType("PDH", node);
							this.tDataBox.addElement(pdhNode);	
						}else if(type.equals("LAG")){
							// 创建LAG类型节点
							lagNode = this.createElementUtil.createPortType("LAG", node);
							this.tDataBox.addElement(lagNode);	
							this.createLag(lagNode);
						}
					}
				}else{
					// 创建端口类型节点
					ethNode = this.createElementUtil.createPortType("ETH", node);
					this.tDataBox.addElement(ethNode);
					// 创建端口类型节点
					sdhNode = this.createElementUtil.createPortType("SDH", node);
					this.tDataBox.addElement(sdhNode);	
					// 创建端口类型节点
					pdhNode = this.createElementUtil.createPortType("PDH", node);
					this.tDataBox.addElement(pdhNode);	
					
				}
				
				siteInst=(SiteInst) node.getParent().getUserObject();
				cardInst = (CardInst) node.getUserObject();

				// 根据板卡，网元查询出网元下板卡的所有端口
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				portInst=new PortInst();
				portInst.setCardId(cardInst.getId());
				portInst.setSiteId(siteInst.getSite_Inst_Id());
				portInstList=portService.select(portInst);
				for (PortInst port_Inst : portInstList) {		
					// 创建端口				
					if(isNNI==1&&(port_Inst.getPortType().equals("NNI"))){//只显示NNI 端口
						port = this.createElementUtil.createPort(port_Inst, ethNode);
						this.tDataBox.addElement(port);	
					}else if(isNNI==2&&port_Inst.getPortType().equals("UNI")){//只显示UNI 端口
						port = this.createElementUtil.createPort(port_Inst, ethNode);
						this.tDataBox.addElement(port);	
					}else if(isNNI==0&&(port_Inst.getPortType().equals("UNI")||port_Inst.getPortType().equals("NNI")||port_Inst.getPortType().equals("NONE"))){
						port = this.createElementUtil.createPort(port_Inst, ethNode);// 显示ETH 下所有端口
						this.tDataBox.addElement(port);	
					}
					else if(pdhNode!=null&&port_Inst.getPortType().equals("e1")){
						port = this.createElementUtil.createPort(port_Inst,pdhNode);
						this.tDataBox.addElement(port);	
					}else if(sdhNode!=null&&port_Inst.getPortType().equals("STM1")){
						port = this.createElementUtil.createPort(port_Inst, sdhNode);
						this.tDataBox.addElement(port);	
					}	
					if(isCapability){
						addCapability(port);
					}
					
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			port = null;
			UiUtil.closeService_MB(portService);
			portInstList = null;
			cardInst = null;
			element = null;
			siteInst = null;
			portInst = null;
			siteInst = null;
			ethNode = null;
			sdhNode = null;
			pdhNode = null;
			lagNode = null;			
		}
	}
	/**
	 * 添加lag
	 * @param parent
	 * @param siteInst
	 */
	private void createLag(Node parent) {
		SiteInst site = null;
		PortLagInfo portLagInfoSel;
		List<PortLagInfo> portLagInfoList = null;
		PortLagService_MB portLagService = null;
		PortInst portInst = null ;
		try {
			 site = (SiteInst) parent.getParent().getParent().getBusinessObject();
			portInst = new PortInst(); 
			portLagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);
			portLagInfoSel = new PortLagInfo();
			portLagInfoSel.setSiteId(site.getSite_Inst_Id());
			portLagInfoList = portLagService.selectByCondition(portLagInfoSel);
			for (PortLagInfo portLagInfo : portLagInfoList) {
				if (portLagInfo.getLagID() != 0) {
					Port port = new Port();
					if(null!=portLagInfo.getPortList()&&portLagInfo.getPortList().size()>0){
						portInst = portLagInfo.getPortList().get(0);
					}
					port.setUserObject(portInst);
					port.setBusinessObject(portInst);
					port.setName("lag/"+portLagInfo.getLagID());
					port.setParent(parent);
					port.setSelected(false);
					port.setVisible(true);
					this.tDataBox.addElement(port);	
					//box.addElement(port);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(portLagService);
			portLagInfoList = null;
			site = null;
			portInst = null;
		}
		
	}
	/**
	 * 添加  性能 类型
	 * @param node
	 * 		端口
	 */
	public void addCapability(Node node){
		CapabilityService_MB capabilityService=null;
		Capability capability=null;
		SiteInst site=null;
		PortInst port=null;
		List<Capability> capabilityList=null;
		try {
			capabilityList=new ArrayList<Capability>();
			capabilityService = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			if(node!= null&&node.getUserObject() instanceof PortInst){
				if(node.getParent().getParent().getParent()!=null&&node.getParent().getParent().getParent().getUserObject() instanceof SiteInst){
					 site=(SiteInst)node.getParent().getParent().getParent().getUserObject();
				}
				 port=(PortInst)node.getUserObject();
				
			}
			/**
			 *选中的 端口 不为空
			 *    并且  其父的父节点（网元）不为空
			 */
			if(site!=null&&port!=null){
				/**
				 * 判定
				 * 	端口的类型
				 */
				capability=new Capability();
				if(site.getManufacturer()==1){
					//陈晓
					capability.setManufacturer(2);
				}else{
					//武汉
					capability.setManufacturer(1);
				}
				if(capability.getManufacturer()==2){
					if(port.getPortType().equals("NNI")||port.getPortType().equals("UNI")||port.getPortType().equals("NONE")||port.getPortType().equals("UNDE")){
						//属于 ETH 类型
						capability.setCapabilitytype("ETH");
					}else  if(port.getPortType().equals("STM1")){
						capability.setCapabilitytype("STM1");
					}else  if(port.getPortType().equals("e1")){
						capability.setCapabilitytype("PDH");
					}
				}else{
					capability.setCapabilitytype("PORT");
				}
				
				capabilityList = capabilityService.selectCapaName(capability);
				if(capabilityList!=null&&capabilityList.size()>0){
					for (Capability capab : capabilityList) {
						Node nodeCapability=this.createElementUtil.createCapability(capab,node);
						this.tDataBox.addElement(nodeCapability);	
					}
				}
			}			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(capabilityService);
			capability = null;
			site = null;
			port = null;
			capabilityList = null;
		}		
	}
	/**
	 * 移除element下的所有子网元
	 * 
	 * @param element
	 *            父element
	 */
	private void removeChild(Element element) {
		if (element!=null&&element.getChildren() != null && element.getChildren().size() > 0) {
			// 移除
			for (int j = 0; j < this.tDataBox.getAllElements().size(); j++) {
				Element e = (Element) this.tDataBox.getAllElements().get(j);
				if(e.getName().equals(element.getName())  ){
					int size=e.getChildren().size();
					for (int i = size-1;  i>=0;i--) {				
						this.tDataBox.removeElement((Element) e.getChildren().get(i));					
					}
					break;
				}													
			}
		}
		
		
	}
	
	
	public void setLevel(int level){
		this.level=level;
	}
	/**
	 * 创建网元为跟节点
	 * 
	 * @param siteInst
	 *            要创建的网元对象
	 * @param typeList
	 * 			要显示的端口类型的集合
	 *  @param isNNI 
	 *    ETH下   0 显示ETH 所有端口     1=只显示NNI端口 2=只显示UNI 端口
	 *  @param isCapability
	 *    是否显示性能类型 
	 * @throws Exception
	 */

	public void createNode(SiteInst siteInst,boolean Tmp,List<String> typeList,int isNNI ,boolean isCapability,boolean isShowName) throws Exception {
		Node node = null;
		if (null != siteInst) {
			
				node = this.createElementUtil.createNode(siteInst);
				this.tDataBox.addElement(node);
				// 如果级别大于2 说明是板卡级别 需要加载板卡
				if (this.level > 2) {
					this.createCard(node, Tmp,typeList,isNNI,isCapability,isShowName);
				}
		
		}
		node = null;
	}

	public void createNode(boolean b, List<SiteInst> siteList, List<String> typeList, int isNNI, boolean isShowName) {
		SiteService_MB siteService = null;
		try {
			if(siteList == null){
				siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
				siteList = siteService.select();				
			}
			// 如果级别大于2 说明是板卡级别 需要加载板卡
			if (this.level > 2) {
				this.createCard(siteList, typeList, isNNI, isShowName);
			}else{
				this.createNode(siteList, null, false);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	private void createCard(List<SiteInst> siteList, List<String> typeList, int isNNI, boolean isShowName) {
		this.tDataBox.clear();
		CardService_MB cardService=null;
		try
		{
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			for (SiteInst siteInst : siteList) {
				try {
					Node node = this.createElementUtil.createNode(siteInst);
					this.tDataBox.addElement(node);
					// 根据网元查询出网元下所有板卡
					CardInst cardInst_select = new CardInst();
					cardInst_select.setSiteId(siteInst.getSite_Inst_Id());
					List<CardInst> cardInstList = cardService.select(cardInst_select);
	
					for (CardInst cardInst : cardInstList) {
						if(!cardInst.getCardName().equals("FAN")&&!cardInst.getCardName().equals("PSU")
								&& !cardInst.getCardName().equals("PWR")){
							// 创建板卡
							Card card = this.createElementUtil.createCard(cardInst, node,isShowName);
							this.tDataBox.addElement(card);
							// 如果级别大于3 说明是端口级别 需要加载端口
							if (this.level > 3) {
								//创建端口
								try {
									this.createPort(card, false, typeList, isNNI, false);
								} catch (Exception e) {
									ExceptionManage.dispose(e,this.getClass());
								}
							}
						}	
					}
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}
		}
		catch (Exception e)
		{
			ExceptionManage.dispose(e,this.getClass());
		}
		finally
		{
			UiUtil.closeService_MB(cardService);
		}
		
	}

	public void settDataBox(TDataBox tDataBox) {
		this.tDataBox = tDataBox;
	}
	
	
}
