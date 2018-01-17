package com.nms.ui.topology.util;

import java.awt.Color;
import java.util.List;

import twaver.Card;
import twaver.Element;
import twaver.Group;
import twaver.Node;
import twaver.Port;
import twaver.Rack;
import twaver.Slot;
import twaver.SubNetwork;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.EquipInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.NetWork;
import com.nms.model.system.FieldService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.util.TopologyUtil;

/**
 * 拓扑中创建元素辅助类
 * 
 * @author kk
 * 
 */
public class CreateElementUtil {

	/**
	 * 创建域对象
	 * 
	 * @param field
	 *            db层域对象
	 * @return twaver域对象
	 */
	public SubNetwork createSubNetwork(NetWork netWork) {
		SubNetwork subNetwork = new SubNetwork();
		subNetwork.setName(netWork.getNetWorkName());
		subNetwork.setLocation(netWork.getNetX(), netWork.getNetY());
		subNetwork.setUserObject(netWork);
		subNetwork.setColorBackground(new Color(153, 204, 255));
		return subNetwork;
	}

	/**
	 * 创建子网
	 * 
	 * @param field
	 *            DB层子网对象
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver子网对象
	 * @throws Exception
	 */
	public Group createGroup(Field field, Element parentElement) throws Exception {
		Group group = new Group();
		try {
			group.setName(field.getFieldName());
			group.setLocation(field.getFieldX(), field.getFieldY());
			group.setParent(parentElement);
			group.setUserObject(field);
		} catch (Exception e) {
			throw e;
		}
		return group;
	}

	/**
	 * 创建网元
	 * 
	 * @param siteInst
	 *            DB网元对象
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver网元对象
	 * @throws Exception
	 */
	public Node createNode(SiteInst siteInst, Element parentElement) throws Exception {
		Node node = null;
		try {
			node = this.createNode(siteInst);
			node.setParent(parentElement);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return node;
	}

	/**
	 * 创建机架对象
	 * 
	 * @param equipInst
	 *            DB机架对象
	 * @return twaver机架对象
	 */
	public Rack createRack(EquipInst equipInst) {
		Rack rack = new Rack();
		rack.setImage(equipInst.getImagePath());
		rack.setLocation(equipInst.getEquipx(), equipInst.getEquipy());
		rack.setUserObject(equipInst);
		return rack;
	}

	/**
	 * 创建卡槽对象
	 * 
	 * @param slotInst
	 *            db槽位对象
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver槽位对象
	 */
	public Slot createSlot(SlotInst slotInst, Element parentElement) {
		Slot slot = new Slot();
		slot.setImage(slotInst.getImagePath());
		slot.setLocation(slotInst.getSlotx(), slotInst.getSloty());
		slot.setParent(parentElement);
		slot.setUserObject(slotInst);
		return slot;
	}

	/**
	 * 创建板卡对象
	 * 
	 * @param cardInst
	 *            db板卡对象
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver板卡对象
	 */
	public Card createCard(CardInst cardInst, Element parentElement,boolean isShowName) {

		Card card = new Card();
		//如果card对象为null 就创建一个临时卡 用来站位用。
		if(null!=cardInst){
			if(isShowName){
				card.setName(cardInst.getCardName());
			}
			card.setImage(cardInst.getImagePath());
			card.setLocation(cardInst.getCardx(), cardInst.getCardy());
			card.setUserObject(cardInst);
		
		}else{
			card.setName("CARD_TMP");
		}
		card.setParent(parentElement);
		return card;
	}

	/**
	 * 创建端口
	 * 
	 * @param portInst
	 *            db端口对象
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver端口对象
	 */
	@SuppressWarnings("unused")
	public Port createPort(PortInst portInst, Element parentElement) {
		Port port = new Port();
		//如果port对象为null 就创建一个临时卡 用来站位用。
		if(null!=port){
			port.setName(portInst.getPortName());
			port.setImage(portInst.getImagePath());
			port.setLocation(portInst.getPortx(), portInst.getPorty());
			port.setUserObject(portInst);
		} 
		else{
			port.setName("PORT_TMP");
		}
			
		port.setParent(parentElement);
		return port;
	}
	
	public Node createTunnel(Tunnel tunnel, Element parentElement) {
		Node tunnelNode = new Node();
		tunnelNode.setName(tunnel.getTunnelName());
//		tunnelNode.setImage(portInst.getImagePath());
//		tunnelNode.setLocation(portInst.getPortx(), portInst.getPorty());
		tunnelNode.setUserObject(tunnel);
		tunnelNode.setParent(parentElement);
		return tunnelNode;
	}
	
	public Node createPw(PwInfo pw, Element parentElement) {
		Node pwNode = new Node();
		pwNode.setName(pw.getPwName());
//		tunnelNode.setImage(portInst.getImagePath());
//		tunnelNode.setLocation(portInst.getPortx(), portInst.getPorty());
		pwNode.setUserObject(pw);
		pwNode.setParent(parentElement);
		return pwNode;
	}
	
	/**
	 * 创建网元为跟几点
	 * 
	 * @param siteInst
	 *            DB网元对象
	 * @return twaver网元对象
	 * @throws Exception
	 */
	public Node createNode(SiteInst siteInst) throws Exception {
		Node node = null;
		TopologyUtil topologyUtil=new TopologyUtil();
		try {
			node = new Node();
			node.setName(siteInst.getCellId()+"");
			setSiteAttribute(node, siteInst);
			if(siteInst.getCellIcccode() != null && !"".equals(siteInst.getCellIcccode())){ 
				node.putLabelBackground(new Color(Integer.parseInt(siteInst.getCellIcccode()))); 
				node.putBorderColor(new Color(Integer.parseInt(siteInst.getCellIcccode()))); 
			} 
			node.setLocation(siteInst.getSiteX(), siteInst.getSiteY());
			node.setBusinessObject(siteInst);
			if (siteInst.getIsGateway() == 1) {
				node.putLabelIcon(getClass().getResource("/com/nms/ui/images/topo/gateway.png").toString());
			}
			topologyUtil.setNodeImage(node, siteInst);
			node.setUserObject(siteInst);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally
		{
			topologyUtil = null;	
		}
		return node;
	}
	
	private void setSiteAttribute(Node node,SiteInst siteInst){
		
		node.setToolTipText("<html>"+ResourceUtil.srcStr(StringKeysLbl.LBL_NAME)+":  "+siteInst.getCellId()+ "<br>"+
				 ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_IP)+":  "+siteInst.getCellDescribe()+"<br>"+
				 ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TYPE)+":  "+siteInst.getCellType()+"<br>"+
				 ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_BELONG)+":  "+getFieldId(siteInst.getFieldID())+"<br>"+" "+
				 "</html>");
	}
	
	
	private String getFieldId(int siteFieldId)
	{
		FieldService_MB fieldService = null;
		List<Field> fieldList = null;
		Field field = null;
		String fieldName=null;
		try {
			fieldService = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			fieldList = fieldService.selectByFieldId(siteFieldId);
			if(null != fieldList && !fieldList.isEmpty())
			{
				field = fieldList.get(0);				
				fieldName=field.getFieldName();
				
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			UiUtil.closeService_MB(fieldService);
			fieldList = null;
			field = null;
		}
		return fieldName;
	}
	/**
	 * 创建端口类型节点
	 * 
	 * @param type
	 *            端口类型 --（eth,pdh,sdh,LAG）
	 * @param parentElement
	 *            拓扑父元素
	 * @return twaver板卡对象
	 */
	public Node createPortType(String type, Node parentElement) {

		Node node=new Node();
		//如果card对象为null 就创建一个临时卡 用来站位用。
		if(null!=type&&!type.equals("")){
			node.setName(type);
		}else{
			node.setName("PORTTYPE_TMP");
		}
		node.setParent(parentElement);
		return node;
	}
	/**
	 * 创建 端口下，的性能类型节点
	 * @param capab
	 * 			性能类型对象
	 * @param no
	 *  		父节点 ：端口
	 * @return
	 */
	public Node createCapability(Capability capab, Node no) {
		Node node = new Node();
		node.setParent(no);
		node.setName(capab.getCapabilityname());
		node.setUserObject(capab);
		return node;
	}

}
