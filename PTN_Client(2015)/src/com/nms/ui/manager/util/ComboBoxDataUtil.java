﻿package com.nms.ui.manager.util;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.ptn.port.PortLagInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.db.bean.system.code.CodeGroup;
import com.nms.db.enums.EActiveStatus;
import com.nms.db.enums.EPwType;
import com.nms.db.enums.EServiceType;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.ptn.port.PortLagService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnComboBox;
import com.nms.ui.manager.keys.StringKeysObj;

/**
 * 下拉列表的数据处理类
 * 
 * @author kk
 * 
 */
public class ComboBoxDataUtil {

	/**
	 * 绑定下拉列表数据
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param identity
	 *            code标识
	 * @throws Exception
	 */
	public void comboBoxData(JComboBox jComboBox, String identity) throws Exception {

		CodeGroup codeGroup = null;
		List<Code> codeList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {

			codeGroup = UiUtil.getCodeGroupByIdentity(identity);
			codeList = codeGroup.getCodeList();
			defaultComboBoxModel = (DefaultComboBoxModel) jComboBox.getModel();
			for (Code code : codeList) {
				if("zh_CN".equals(ResourceUtil.language)){
					defaultComboBoxModel.addElement(new ControlKeyValue(code.getId() + "", code.getCodeName(), code));
				}else{
					defaultComboBoxModel.addElement(new ControlKeyValue(code.getId() + "", code.getCodeENName(), code));
				}
			}
			jComboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			codeGroup = null;
			codeList = null;
			defaultComboBoxModel = null;
		}
	}

	/**
	 * 根据code的主键选中下拉列表
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param selectId
	 *            选中的id
	 */
	public void comboBoxSelect(JComboBox jComboBox, String selectId) {
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			if (((ControlKeyValue) jComboBox.getItemAt(i)).getId().equals(selectId)) {
				jComboBox.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * 根据值选中下拉列表
	 * 
	 * @param jComboBox
	 *            下拉列表对象
	 * @param selectValue
	 *            选中的值
	 */
	public void comboBoxSelectByValue(JComboBox jComboBox, String selectValue) {
		ControlKeyValue controlKeyValue = null;
		String value = null;
		for (int i = 0; i < jComboBox.getItemCount(); i++) {
			controlKeyValue = (ControlKeyValue) jComboBox.getItemAt(i);
			if(controlKeyValue.getObject() != null){
				value = ((Code) controlKeyValue.getObject()).getCodeValue();
				if (null != value && value.equals(selectValue)) {// 判断value不为空，过滤条件中默认的所有对应的value为空
					jComboBox.setSelectedIndex(i);
					return;
				}
			}
		}
	}

	/**
	 * 给下拉列表绑定ETH端口数据 uni
	 * 
	 * @param jComboBox
	 * @throws Exception
	 */
	public void initPortData(JComboBox jComboBox) throws Exception {
		PortService_MB portService = null;
		PortInst portinst_select = null;
		List<PortInst> portInstList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		PortLagInfo portLagInfo = null;
		List<PortLagInfo> portLagInfoList = null;
		PortLagService_MB lagService = null;
		try {
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portinst_select = new PortInst();
			portinst_select.setPortType("UNI");
			portinst_select.setSiteId(ConstantUtil.siteId);
			portInstList = portService.select(portinst_select);

			defaultComboBoxModel = new DefaultComboBoxModel();
			if (null != portInstList && portInstList.size() > 0) {
				for (PortInst portinst : portInstList) {
					// 端口没被lag使用 绑定
					if (portinst.getLagId() == 0) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portinst.getPortId() + "", portinst.getPortName(), portinst));
					}
				}
			}

			// 查询所有LAG
			lagService = (PortLagService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTLAG);

			portLagInfo = new PortLagInfo();
			portLagInfo.setSiteId(ConstantUtil.siteId);
			portLagInfo.setLagStatus(EActiveStatus.ACTIVITY.getValue());
			portLagInfoList = lagService.selectByCondition(portLagInfo);
			for (PortLagInfo lagInfo : portLagInfoList) {
				defaultComboBoxModel.addElement(new ControlKeyValue(lagInfo.getId() + "", "" + "lag/" + lagInfo.getLagID(), lagInfo));
			}

			jComboBox.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(lagService);
		}
	}

	/**
	 * 给下拉列表绑定AC端口数据
	 * 
	 * @param cmbPort
	 *            对应的端口下拉列表
	 * @param cmbAc
	 *            要绑定的AC下拉列表
	 * @throws Exception
	 */
	public void initAcData(JComboBox cmbPort, JComboBox cmbAc) throws Exception {
		AcPortInfoService_MB acInfoService = null;
		List<AcPortInfo> acportInfoList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		AcPortInfo acPortInfo_select = null;
		ControlKeyValue controlKeyValue_port = null;
		try {
			defaultComboBoxModel = new DefaultComboBoxModel();
			if (null != cmbPort.getSelectedItem()) {
				controlKeyValue_port = (ControlKeyValue) cmbPort.getSelectedItem();
				acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
				acPortInfo_select = new AcPortInfo();
				acPortInfo_select.setAcStatus(EActiveStatus.ACTIVITY.getValue());
				if (controlKeyValue_port.getObject() instanceof PortInst) {
					acPortInfo_select.setPortId(Integer.parseInt(controlKeyValue_port.getId()));
				} else {
					acPortInfo_select.setLagId(Integer.parseInt(controlKeyValue_port.getId()));
				}
				acportInfoList = acInfoService.queryByAcPortInfo(acPortInfo_select);

				if (acportInfoList != null && acportInfoList.size() > 0) {
					for (AcPortInfo acPortInfo : acportInfoList) {
						if (acPortInfo.getIsUser() == 0) {
							defaultComboBoxModel.addElement(new ControlKeyValue(acPortInfo.getId() + "", acPortInfo.getName(), acPortInfo));
						}
					}
				}
			}
			cmbAc.setModel(defaultComboBoxModel);

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}

	}

	/**
	 * 判断下拉列表中是否存在相同的项
	 * 
	 * @param cmbAc
	 * @param acId
	 * @return
	 */
	public boolean isExistSameOption(JComboBox cmbAc, String acId) {
		try {
			for (int i = 0; i < cmbAc.getItemCount(); i++) {
				if (((ControlKeyValue) cmbAc.getItemAt(i)).getId().equals(acId)) {
					return true;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		}
		return false;
	}

	/**
	 * 下拉列表绑定网元数据
	 * 
	 * @param cmbNE
	 * @throws Exception
	 */
	public void initNEData(PtnComboBox cmbNE) throws Exception {
		SiteService_MB siteService = null;
		List<SiteInst> siteInstList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			siteInstList = siteService.selectRootSite(ConstantUtil.user);

			defaultComboBoxModel = (DefaultComboBoxModel) cmbNE.getModel();
			defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
			if (siteInstList != null) {
				for (SiteInst site : siteInstList) {
					defaultComboBoxModel.addElement(new ControlKeyValue(site.getSite_Inst_Id() + "", site.getCellId(), site));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(siteService);
			siteInstList = null;
			defaultComboBoxModel = null;
		}
	}

	/**
	 * 绑定NNI端口下拉列表
	 * 
	 * @param comBobox
	 * @param siteId
	 */
	public void initNNIPortData(PtnComboBox comBobox, int id, EServiceType eServiceType) {
		getPortData(comBobox, id, eServiceType, 1);
	}
	
	public void initUNIPortData(PtnComboBox comBobox, int id, EServiceType eServiceType) {
		getPortData(comBobox, id, eServiceType, 2);
	}
	
	public void initPDHPortData(PtnComboBox comBobox, int id, EServiceType eServiceType) {
		getPortData(comBobox, id, eServiceType, 3);
	}
	
	public void getPortData(PtnComboBox comBobox, int id, EServiceType eServiceType, int type)
	{
		DefaultComboBoxModel defaultComboBoxModel = null;
		PortService_MB portService = null;
		PortInst portInst_select = null;
		List<PortInst> portInstList = null;
		try {
			defaultComboBoxModel = (DefaultComboBoxModel) comBobox.getModel();
			defaultComboBoxModel.removeAllElements();

			if (id != 0) {
				// 查询端口数据
				portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
				portInst_select = new PortInst();
				// 根据不同类型配置不同的查询条件
				if (eServiceType == EServiceType.SITE) {
					portInst_select.setSiteId(id);
				} else if (eServiceType == EServiceType.CARD) {
					portInst_select.setCardId(id);
				}
				if(type == 1)
				{
					portInst_select.setPortType("NNI");
				}
				else if(type == 2)
				{
					portInst_select.setPortType("UNI");
				}
				else if(type == 3)
				{
					portInst_select.setPortType("e1");
				}
				portInstList = portService.select(portInst_select);

				defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
				if (portInstList != null) {
					for (PortInst portInst : portInstList) {
						defaultComboBoxModel.addElement(new ControlKeyValue(portInst.getPortId() + "", portInst.getPortName(), portInst));
					}
				}
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(portService);
			portInstList = null;
			defaultComboBoxModel = null;
			portInst_select = null;
		}
	}

	/**
	 * 初始化激活状态下拉列表
	 * 
	 * @param cmbStatus
	 */
	public void initActivatedData(PtnComboBox cmbStatus) {
		cmbStatus.addItem(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
		cmbStatus.addItem(new ControlKeyValue(EActiveStatus.ACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_YES), ""));
		cmbStatus.addItem(new ControlKeyValue(EActiveStatus.UNACTIVITY.getValue() + "", ResourceUtil.srcStr(StringKeysObj.ACTIVITY_NO), ""));
	}

	/**
	 * 初始化pw的下拉列表数据
	 * 
	 * @param cmbPW
	 * @param isEtn
	 *            true=查询ethpw false=查询除eth之外的pw
	 * @throws Exception
	 */
	public void initPWData(PtnComboBox cmbPW, int type, boolean isEth) throws Exception {
		PwInfoService_MB pwInfoService = null;
		PwInfo pwInfo_select = new PwInfo();
		List<PwInfo> pwInfoList = null;
		ListingFilter filter = new ListingFilter();
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			pwInfo_select.setIsSingle(0);
			if(type == 1){
				pwInfo_select.setASiteId(ConstantUtil.siteId);
			}
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			if (isEth) {
				pwInfo_select.setType(EPwType.ETH);
				pwInfoList = (List<PwInfo>) filter.filterList(pwInfoService.selectbyType(pwInfo_select));
				if(type == 1){
					pwInfo_select.setIsSingle(type);
					List<PwInfo> pwList = (List<PwInfo>) filter.filterList(pwInfoService.selectbyType(pwInfo_select));
					if(pwList != null && !pwList.isEmpty()){
						pwInfoList.addAll(pwList);
					}
				}
			} else {
				pwInfo_select.setType(EPwType.PDH);
				pwInfoList = (List<PwInfo>) filter.filterList(pwInfoService.selectNotEth(pwInfo_select));
				if(type == 1){
					pwInfo_select.setIsSingle(type);
					List<PwInfo> pwList = (List<PwInfo>) filter.filterList(pwInfoService.selectNotEth(pwInfo_select));
					if(pwList != null && !pwList.isEmpty()){
						pwInfoList.addAll(pwList);
					}
				}
			}

			if (null != pwInfoList && pwInfoList.size() > 0) {
				defaultComboBoxModel = (DefaultComboBoxModel) cmbPW.getModel();
				defaultComboBoxModel.removeAllElements();
				defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));
				for (PwInfo pwInfo : pwInfoList) {
					defaultComboBoxModel.addElement(new ControlKeyValue(pwInfo.getPwId() + "", pwInfo.getPwName(), pwInfo));
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(pwInfoService);
			pwInfoList = null;
			filter = null;
			defaultComboBoxModel = null;
		}
	}

	/**
	 * 
	 * @param cmbCard
	 * @param siteId
	 * @throws Exception
	 */
	public void initCardData(PtnComboBox cmbCard, int siteId) throws Exception {
		CardService_MB cardService = null;
		List<CardInst> cardInstList = null;
		DefaultComboBoxModel defaultComboBoxModel = null;
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			cardInstList = cardService.selectBySiteId(siteId);

			if (null != cardInstList && cardInstList.size() > 0) {
				defaultComboBoxModel = (DefaultComboBoxModel) cmbCard.getModel();
				defaultComboBoxModel.removeAllElements();

				defaultComboBoxModel.addElement(new ControlKeyValue(0 + "", ResourceUtil.srcStr(StringKeysObj.STRING_ALL), ""));

				for (CardInst cardInst : cardInstList) {
					if (!"FAN".equals(cardInst.getCardName()) && !"PSU".equals(cardInst.getCardName()) 
							&& !"PWR".equals(cardInst.getCardName())&& !"SP16".equals(cardInst.getCardName()) ) {
						defaultComboBoxModel.addElement(new ControlKeyValue(cardInst.getId() + "", cardInst.getCardName(), cardInst));
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(cardService);
			cardInstList = null;
			defaultComboBoxModel = null;
		}
	}
}
