package com.nms.ui.manager.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import twaver.AlarmModel;
import twaver.Card;
import twaver.CheckableFilter;
import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.Port;
import twaver.Slot;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.path.Segment;
import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.system.Field;
import com.nms.db.bean.system.code.Code;
import com.nms.db.bean.system.code.CodeGroup;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.system.code.CodeGroupService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.xmlbean.EquipmentType;
import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.ui.ptn.safety.roleManage.RoleRoot;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.topology.NetworkElementPanel;

/**
 * 拓扑视图辅助类
 * @author kk
 *
 */
public class TopologyUtil {
	
	/**
	 * 创建拓扑按钮
	 * 
	 * @param imageSrc
	 * @param action
	 */
	public void createTopoButton(String imageSrc, Action action, JToolBar toolBar, String title) {
		Icon menuIconPlace = new ImageIcon(new UiUtil().getClass().getResource(imageSrc));
		JButton buttonPlance = toolBar.add(action);
		buttonPlance.setIcon(menuIconPlace);
//		if (title.equals(ResourceUtil.srcStr(StringKeysObj.AUTO_MATCHING))) {
//			roleRoot.setItemEnbale(buttonPlance, RootFactory.TOPOLOGY_MANAGE);
//		} else if (title.equals(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE_PLACE))) {
//			roleRoot.setItemEnbale(buttonPlance, RootFactory.TOPOLOGY_MANAGE);
//		} else {
//			buttonPlance.setEnabled(true);
//		}
		buttonPlance.setToolTipText(title);

	}
	
	/**
	 * 更新拓扑图上的告警数量
	 * 
	 * @author kk
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	public void updateSiteInstAlarm(CurAlarmService_MB curAlarmService) throws Exception {
		AlarmModel alarmModel = null;
		List<Element> elements = null;
		List<CurrentAlarmInfo> currentAlarmInfoList = null;
		Node node = null;
		SiteInst siteInst = null;
		Map<Integer, List<CurrentAlarmInfo>> dbAlarmMap = null;
//		CurAlarmService curAlarmService = null;
		try {
			// 查询数据库中所有告警
//			curAlarmService = (CurAlarmService) ConstantUtil.serviceFactory.newService(Services.CurrentAlarm);
			dbAlarmMap = curAlarmService.selectAll();
			// 获取拓扑的告警对象 并清空
			alarmModel = NetworkElementPanel.getNetworkElementPanel().getBox().getAlarmModel();
			// alarmModel.clear();
			// 获取拓扑图上的所有元素
			elements = NetworkElementPanel.getNetworkElementPanel().getBox().getAllElements();
			for (Element element : elements) {
				if (element instanceof Node) {
					node = (Node) element;
					if (node.getUserObject() != null && element.getUserObject() instanceof SiteInst) {
						siteInst = (SiteInst) element.getUserObject();
						if (siteInst.getLoginstatus() == 1) {
							// 网元现有告警数
							int alarmNum = alarmModel.getAlarmsByElement(node).size();
							// 获取此网元在数据库中的告警集合
							if (null == dbAlarmMap.get(siteInst.getSite_Inst_Id())) {
								currentAlarmInfoList = new ArrayList<CurrentAlarmInfo>();
							} else {
								currentAlarmInfoList = dbAlarmMap.get(siteInst.getSite_Inst_Id());
							}
							// currentAlarmInfoList=getAlarm(siteInst.getSite_Inst_Id(), "site", null);

							// 如果网元现有告警数与数据库告警数不相同，才去刷新该网元的告警
							if (alarmNum != currentAlarmInfoList.size()) {
								// 移除此网元的告警 并且添加新告警
								alarmModel.removeAlarmsByElement(node);
								for (CurrentAlarmInfo currentAlarmInfo : currentAlarmInfoList) {
									currentAlarmInfo.setElementID(node.getID());
									alarmModel.addAlarm(currentAlarmInfo);
								}
							}
						}
					}
				}else if(element instanceof Link){
					Link link = (Link)element;
					Object userObj = link.getUserObject();
					if(userObj != null && (userObj instanceof Segment)){
						Segment seg = (Segment) userObj;
						int aSiteId = seg.getASITEID();
						int aPortId = seg.getAPORTID();
						int zSiteId = seg.getZSITEID();
						int zPortId = seg.getZPORTID();
						//获取A,Z端网元下该端口的link los告警
						CurrentAlarmInfo linkAlarm = this.getLinkAlarm(curAlarmService,aSiteId, aPortId);
						//如果一端有link los告警,则光纤拓扑显示红色
						if(linkAlarm.getId() > 0){
							link.putLinkColor(Color.RED);
						}else{
							//如果A端没有link los告警,则查Z端
							linkAlarm = this.getLinkAlarm(curAlarmService,zSiteId, zPortId);
							if(linkAlarm.getId() > 0){
								link.putLinkColor(Color.RED);
							}else{
								//如果Z端也没有link los告警,则查两端的tms rdi告警
								CurrentAlarmInfo rdiAlarm = this.getTmsRdiAlarm(curAlarmService,aSiteId, seg.getId());
								//如果一端有tms rdi告警,则光纤拓扑显示黄色
								if(rdiAlarm.getId() > 0){
									link.putLinkColor(Color.YELLOW);
								}else{
									//如果A端没有tms rdi告警,则查Z端
									rdiAlarm = this.getTmsRdiAlarm(curAlarmService,zSiteId, seg.getId());
									if(rdiAlarm.getId() > 0){
										link.putLinkColor(Color.YELLOW);
									}else{
										link.putLinkColor(Color.GREEN);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			alarmModel = null;
			elements = null;
			currentAlarmInfoList = null;
			node = null;
			siteInst = null;
			dbAlarmMap = null;
		}
	}
	
	/**
	 * 获取该网元下的tmsRdi告警
	 * @throws Exception 
	 */
	private CurrentAlarmInfo getTmsRdiAlarm(CurAlarmService_MB currService,int siteId, int segmentId) throws Exception {
		CurrentAlarmInfo c = new CurrentAlarmInfo();
		try {
			c.setSiteId(siteId);
			c.setObjectId(segmentId);
			c.setAlarmCode(117);
			c.setAlarmLevel(3);
			List<CurrentAlarmInfo> currAlarmList = currService.select(c);
			if(currAlarmList.size() == 1){
				return currAlarmList.get(0);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
		}
		return c;
	}

	/**
	 * 获取该网元该端口下的linkLos告警
	 * @throws Exception 
	 */
	private CurrentAlarmInfo getLinkAlarm(CurAlarmService_MB currService,int siteId, int portId) throws Exception {
		CurrentAlarmInfo c = new CurrentAlarmInfo();
		PortService_MB portService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			PortInst port = portService.selectPortybyid(portId);
			if(port != null && port.getPortId() > 0){
				c.setSiteId(siteId);
				c.setObjectId(port.getNumber());
				c.setAlarmCode(11);
				c.setAlarmLevel(5);
				List<CurrentAlarmInfo> currAlarmList = currService.select(c);
				if(currAlarmList.size() == 1){
					return currAlarmList.get(0);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(portService);
		}
		return c;
	}

	/**
	 * 在拓扑上通过网元显示不同的图片
	 * 
	 * @author kk
	 * @throws Exception
	 * @Exception 异常对象
	 */
	public void setNodeImage(Node node, SiteInst siteInst) throws Exception {
		EquipmentType equipmentType = null;
		String path = null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			equipmentType = equimentDataUtil.getEquipmentType(siteInst.getCellType());
			if (null != equipmentType) {
				path = equipmentType.getImagePath();
			}

			// 虚拟网元
			if ("1".equals(UiUtil.getCodeById(siteInst.getSiteType()).getCodeValue())) {
				node.setImage(UiUtil.class.getResource(path + "netype_0.png").toString());
			} //离线网元
			else if ("2".equals(UiUtil.getCodeById(siteInst.getSiteType()).getCodeValue())) {
				node.setImage(UiUtil.class.getResource(path + "ne_off.png").toString());
			}
			else {
				// 在线后托管的网元
				if (siteInst.getLoginstatus() == 0) {
					node.setImage(UiUtil.class.getResource(path + "ne_1.png").toString());
				} else {
					// 在线网元
					node.setImage(UiUtil.class.getResource(path + "ne_0.png").toString());
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			equipmentType = null;
			path = null;
		}

	}
	
	/**
	 * 根据段是设置link的宽度
	 * 
	 * @param segment
	 *            段对象
	 * @param link
	 *            link对象
	 * @throws Exception
	 */
	public void setLinkWidth(Segment segment, Link link) throws Exception {

		if (null == segment) {
			throw new Exception("segment is null");
		}

		if (null == link) {
			throw new Exception("link is null");
		}
		Code code = null;
		CodeGroup codeGroup = null;
		CodeGroupService_MB codeGroupService = null;
		List<CodeGroup> codeGroupList = null;
		link.putLinkWidth(3);
//		try {
//			
//			if (null == segment.getSpeedSegment() || "".equals(segment.getSpeedSegment())) {
//				link.putLinkWidth(3);
//			} else {
//				codeGroupService = (CodeGroupService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CodeGroup);
//				// 查询code对象
//				code = UiUtil.getCodeById(Integer.parseInt(segment.getSpeedSegment()));
//				codeGroup = new CodeGroup();
//				codeGroup.setId(code.getCodeGroupId());
//				// 根据code组ID 查询出组对象
//				codeGroupList = codeGroupService.select(codeGroup);
//
//				// 获取组对象
//				if (null != codeGroupList && codeGroupList.size() == 1) {
//					codeGroup = codeGroupList.get(0);
//
//					// 如果标识为 WORKMODE 说明为武汉的设备 否则是晨晓的
//					if ("WORKMODE".equals(codeGroup.getCodeIdentily())) {
//						// 根据速率不同，设置link的宽度
//						if ("1".equals(code.getCodeValue())) { // 1000M
//							link.putLinkWidth(5);
//						} else if ("2".equals(code.getCodeValue())) { // 100M
//							link.putLinkWidth(3);
//						} else if ("3".equals(code.getCodeValue())) { // 10M
//							link.putLinkWidth(1);
//						} else { // 自协商
//							link.putLinkWidth(3);
//						}
//					} else {
//
//						if ("2".equals(code.getCodeValue()) || "5".equals(code.getCodeValue())) { // 10G
//							link.putLinkWidth(7);
//						} else if ("1".equals(code.getCodeValue()) || "4".equals(code.getCodeValue())) { // 1G
//							link.putLinkWidth(5);
//						} else if ("0".equals(code.getCodeValue()) || "3".equals(code.getCodeValue())) { // 100M
//							link.putLinkWidth(3);
//						} else {
//							link.putLinkWidth(3);
//						}
//					}
//
//				} else {
//					link.putLinkWidth(3);
//				}
//			}
//
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			UiUtil.closeService_MB(codeGroupService);
//		}

	}
	
	/**
	 * 树的域过滤器
	 */
	public CheckableFilter FieldFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Node && element.getUserObject() instanceof Field) {
				return true;
			}
			if (element instanceof Node && element.getUserObject() instanceof SiteInst) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 树的网元过滤器
	 */
	public CheckableFilter SiteInstFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Node && element.getUserObject() instanceof SiteInst) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 树的板卡过滤器
	 */
	public CheckableFilter CardFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Card) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 树的板卡过滤器
	 */
	public CheckableFilter SlotFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Slot) {
				return true;
			}
			return false;
		}
	};

	public CheckableFilter CardAndPortFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Card || element instanceof Port) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 树的端口过滤器
	 */
	public CheckableFilter PortFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Port) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 树的性能类型过滤器
	 */
	public CheckableFilter PortAndCapabilityFilter = new CheckableFilter() {
		@Override
		public boolean isCheckable(Element element) {
			if (element instanceof Node && element.getUserObject() instanceof Capability) {
				return true;
			}
			if (element instanceof Port) {
				return true;
			}
			return false;
		}
	};
}
