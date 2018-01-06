﻿package com.nms.ui.ptn.systemconfig.dialog.siteInitialise;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.shelf.EquipInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.bean.system.Field;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.equipment.slot.SlotService_MB;
import com.nms.model.system.FieldService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.manager.xmlbean.EquipmentType;
import com.nms.ui.ptn.basicinfo.dialog.site.AddSiteDialog;

public class SiteSNTable  extends PtnDialog  {
	private static final long serialVersionUID = -4046119221737730984L;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private JTable jTable;
	private JPanel buttonPanel;
	private List<SiteInst> siteInsts;
	private JButton saveButton;
	private JButton canceButton;
	private SiteSNTable siteSNTable;
	public SiteSNTable(List<SiteInst> siteInsts) {
		this.siteInsts = siteInsts;
		siteSNTable = this;
		init();
	}

	/*
	 * 初始化界面和数据
	 */
	public void init() {
		initComponents();
		setLayout();
		initData(siteInsts);
		addListeners();
	}



	public void initComponents() {
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysMenu.MENU_QUERY_ADJOIN_SN)));
		jTable = new JTable();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height = 220;
		screenSize.width = 120;
		jTable.setPreferredScrollableViewportSize(screenSize);
		jTable.getTableHeader().setResizingAllowed(true);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		jTable.setDragEnabled(false);
		jTable.setModel(new DefaultTableModel(new Object[][] {
		}, new String[] { ResourceUtil.srcStr(StringKeysObj.ORDER_NUM), ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME), ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_IP),ResourceUtil.srcStr(StringKeysLbl.LBL_SN_NUMBER), ResourceUtil.srcStr(StringKeysLbl.LBL_SITE_TYPE), ResourceUtil.srcStr(StringKeysLbl.LBL_GROUP_ID)})
		);
		contentScrollPane.setViewportView(jTable);
		buttonPanel = new JPanel();
		saveButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE));
		canceButton = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
	}

	/*
	 * 工具按钮布局
	 */
	public void setButtonLayout() {
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 80, 10));
		buttonPanel.add(canceButton);
		buttonPanel.add(saveButton);
	}

	public void setLayout() {
		setButtonLayout();
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(contentScrollPane, c);
		contentPanel.add(contentScrollPane);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.4;
		contentLayout.setConstraints(buttonPanel, c);
		contentPanel.add(buttonPanel);
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(contentPanel, c);
		this.add(contentPanel);
	}

	/*
	 * 初始化数据
	 */
	public void initData(List<SiteInst> siteInsts) {
		DefaultTableModel defaultTableModel = null;
		defaultTableModel = (DefaultTableModel) jTable.getModel();
		defaultTableModel.getDataVector().clear();
		defaultTableModel.fireTableDataChanged();
		Object[] obj = null;
		if (siteInsts != null && siteInsts.size()>0) {
			for (int i = 0; i < siteInsts.size(); i++) {
			//初始值
				obj = new Object[] {i+1, siteInsts.get(i).getCellId(), 
						siteInsts.get(i).getCellDescribe(),
						siteInsts.get(i).getSn(),
						siteInsts.get(i).getCellType(), 
						siteInsts.get(i).getFieldID(), };
				
				        defaultTableModel.addRow(obj);
			}
			jTable.setModel(defaultTableModel);
		}
	}
	
	private void addListeners(){
		canceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteSNTable.dispose();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveActionPerformed();
			}
		});
	}
	
	private void saveActionPerformed(){
		SiteService_MB siteService = null;
		List<SiteInst> insts = null;
		FieldService_MB fieldServiceMB = null;
		DispatchUtil dispatchUtil = null;
		try {
			dispatchUtil = new DispatchUtil(RmiKeys.RMI_SITE);
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			fieldServiceMB = (FieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Field);
			List<Field> fields = fieldServiceMB.selectField();
			List<SiteInst> siteInstList = siteService.selectByFileId(fields.get(0).getId());
			List<Integer> has = new ArrayList<Integer>();
			List<Integer> canuse = new ArrayList<Integer>();
			if(siteInstList != null){
				for (int i = 0; i < siteInstList.size(); i++) {
					has.add(Integer.parseInt(siteInstList.get(i).getSite_Hum_Id()));
				}
			}
			for (int i = 1; i < 254; i++) {
				if(!has.contains(i)){
					canuse.add(i);
				}
			}
			
			for(SiteInst inst : siteInsts){
				insts = siteService.selectBySn(inst.getSn());
				String type = inst.getCellType();
				if(insts != null && insts.size() == 0){
					EquimentDataUtil equimentDataUtil = new EquimentDataUtil();
					EquipmentType equipmentType = null;
					equipmentType = equimentDataUtil.getEquipmentType(transformCellType(inst,Integer.parseInt(inst.getCellType())));
					
					if (equipmentType != null) {
						inst.setEquipInst(this.getEquipInst(equipmentType.getXmlPath()));
					}
					inst.setCellId("sn_"+canuse.get(0));
					inst.setFieldID(fields.get(0).getId());
					inst.setSite_Hum_Id(canuse.get(0)+"");
					inst.setSwich(0+"");
					dispatchUtil.excuteInsert(inst);
					addCard(inst, equimentDataUtil,type);
					canuse.remove(canuse.get(0));
				}else if(insts != null && !inst.getRootIP().equals(insts.get(0).getRootIP())){
					insts.get(0).setRootIP(inst.getRootIP());
					siteService.saveOrUpdate(insts.get(0));
				}
			}
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
			UiUtil.closeService_MB(fieldServiceMB);
		}
	}
	
	private String transformCellType(SiteInst siInst,int value){
		if(value == 100){
			siInst.setCellType("710A");
		}else if(value == 101){
			siInst.setCellType("710A");
		}else if(value == 102){
			siInst.setCellType("ETN-5000");
//			if(CodeConfigItem.getInstance().getIconImageShowOrHide() ==4){
//				siInst.setCellType("710");
//			}else{
//				siInst.setCellType("710A");
//			}
		}else if(value == 200){
			siInst.setCellType("703A");
		}else if(value == 201){
			siInst.setCellType("703B");
		}else if(value == 203){
			siInst.setCellType("703-1A");
		}else if(value == 204){
			siInst.setCellType("703-2A");
		}else if(value == 205){
			siInst.setCellType("703-1D");
		}else if(value == 206){
			siInst.setCellType("703-2D");
		}else if(value == 207){
			siInst.setCellType("703-1A");
		}else if(value == 208){
			siInst.setCellType("703-2A-2");
		}else if(value == 209){
			siInst.setCellType("703-4A");
		}else if(value == 210){
			siInst.setCellType("703-5A");
		}else if(value == 215){
			siInst.setCellType("ETN-200-204");
		}else if(value == 216){
			siInst.setCellType("ETN-200-204E");
		}
		return siInst.getCellType();
	}
	
	
	private void addCard(SiteInst siteInst,EquimentDataUtil equimentDataUtil,String type){
		SlotService_MB slotService = null;
		CardService_MB cardService = null;
		CardInst cardInst = null;
		SiteService_MB siteServiceMB = null;
		try {
			slotService = (SlotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SLOT);
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInst = siteServiceMB.selectBySn(siteInst.getSn()).get(0);
			SlotInst slotInst = new SlotInst();
			slotInst.setSiteId(siteInst.getSite_Inst_Id());
			if("100".equals(type)){
				siteInst.setCellType("710A");
				slotInst.setBestCardName("MCU1");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/soz/card_710_mc.xml", slotInst);
			}else if("101".equals(type)){
				siteInst.setCellType("710B");
				slotInst.setBestCardName("MCU");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/sozb/card_710b_mcu.xml", slotInst);
//			}else if("102".equals(type)){
//				if(CodeConfigItem.getInstance().getIconImageShowOrHide() ==4){
//					siteInst.setCellType("710");
//					slotInst.setBestCardName("XCTS1");
//					slotInst = slotService.select(slotInst).get(0);
//					ExceptionManage.infor(slotInst.getSiteId()+",,,"+slotInst.getId(),this.getClass());
//					cardInst = equimentDataUtil.addCard("config/topo/card/yixun/card_710_yixun_xcts1.xml", slotInst);
//				}else{
//					siteInst.setCellType("710A");
//					slotInst.setBestCardName("MCU1");
//					slotInst = slotService.select(slotInst).get(0);
//					cardInst = equimentDataUtil.addCard("config/topo/card/soz/card_710_mc2.xml", slotInst);
//				}
			}else if("203".equals(type)){
				siteInst.setCellType("703-1A");
				slotInst.setBestCardName("703-1A_CARD");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/szt/card_703-1A.xml", slotInst);
			}else if("204".equals(type)){
				siteInst.setCellType("703-2A");
				slotInst.setBestCardName("703-2A_CARD");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/sztc/card_703-2A.xml", slotInst);
			}else if("209".equals(type)){
				siteInst.setCellType("703-4A");
				slotInst.setBestCardName("703-4A_CARD");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/szt4a/card_703-4A.xml", slotInst);
			}else if("210".equals(type)){
				siteInst.setCellType("703-5A");
				slotInst.setBestCardName("703-5A_CARD");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/szt/card_703-5A.xml", slotInst);
			}else if("216".equals(type)){
				siteInst.setCellType("ETN-200-204E");
				slotInst.setBestCardName("ETN-200-204E");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/jcszt2a/card_703-2A.xml", slotInst);
			}else if("215".equals(type)){
				siteInst.setCellType("ETN-200-204");
				slotInst.setBestCardName("ETN-200-204");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/jcszt2c/card_703-2C.xml", slotInst);
			}else if("102".equals(type)){
				siteInst.setCellType("ETN-5000");
				slotInst.setBestCardName("MCU1");
				slotInst = slotService.select(slotInst).get(0);
				cardInst = equimentDataUtil.addCard("config/topo/card/yixun/card_710_yixun_xcts1.xml", slotInst);
			}
			cardService.saveOrUpdate(cardInst);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(slotService);
			UiUtil.closeService_MB(cardService);
			UiUtil.closeService_MB(siteServiceMB);
		}
		
	}
	/**
	 * 读取XML获取机架和槽
	 * 
	 * @return
	 * @throws Exception
	 */
	public EquipInst getEquipInst(String xmlPath) throws Exception {

		EquipInst equipInst = null;
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		org.w3c.dom.Element root = null;
		NodeList nodeList = null;
		Element parent = null;
		NodeList childList = null;
		Element child = null;
		List<SlotInst> slotInstList = null;
		SlotInst slotInst = null;

		try {
			equipInst = new EquipInst();
			factory = DocumentBuilderFactory.newInstance();
			// 使用DocumentBuilderFactory构建DocumentBulider
			builder = factory.newDocumentBuilder();
			// 使用DocumentBuilder的parse()方法解析文件
			doc = builder.parse(AddSiteDialog.class.getClassLoader().getResource(xmlPath).toString());
			root = doc.getDocumentElement();
			nodeList = root.getElementsByTagName("equipment");

			for (int i = 0; i < nodeList.getLength(); i++) {
				parent = (org.w3c.dom.Element) nodeList.item(i);

				equipInst.setImagePath(parent.getAttribute("imagePath"));
				equipInst.setEquipx(Integer.parseInt(parent.getAttribute("x")));
				equipInst.setEquipy(Integer.parseInt(parent.getAttribute("y")));

				slotInstList = new ArrayList<SlotInst>();
				childList = parent.getElementsByTagName("slot");
				for (int j = 0; j < childList.getLength(); j++) {
					child = (Element) childList.item(j);
					slotInst = new SlotInst();
					slotInst.setImagePath(child.getAttribute("imagePath"));
					slotInst.setSlotx(Integer.parseInt(child.getAttribute("x")));
					slotInst.setSloty(Integer.parseInt(child.getAttribute("y")));
					slotInst.setSlotType(child.getAttribute("type"));
					slotInst.setBestCardName(child.getAttribute("bestCardName"));
					slotInst.setMasterCardAddress(child.getAttribute("masterCardAddress"));
					if (child.getAttribute("number").length() > 0) {
						slotInst.setNumber(Integer.parseInt(child.getAttribute("number")));
					}
					slotInstList.add(slotInst);
				}
				equipInst.setSlotlist(slotInstList);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			factory = null;
			builder = null;
			doc = null;
			root = null;
			nodeList = null;
			parent = null;
			childList = null;
			child = null;
			slotInstList = null;
			slotInst = null;
		}
		return equipInst;
	}
}
