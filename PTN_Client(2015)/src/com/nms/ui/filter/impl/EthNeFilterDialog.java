package com.nms.ui.filter.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nms.db.bean.ptn.path.ServiceInfo;
import com.nms.db.enums.EServiceType;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnComboBox;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;

public class EthNeFilterDialog extends EthServiceFilterDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EthNeFilterDialog(Object object)
	{
		super(object);
	}
	
	@Override
	protected int getPanelChildHeight() {
		return 180;
	}
	
	@Override
	protected void initComponent() {
		this.lblCard = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_CARD_NAME));
		this.cmbCard = new PtnComboBox();
		this.lblPwName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PW_NAME));
		this.cmbPwName = new PtnComboBox();
		this.lblNEName = new JLabel(ResourceUtil.srcStr(StringKeysObj.STRING_SITE_NAME));
		this.cmbNEName = new PtnComboBox();
		this.lblPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NAME));
		this.cmbPort = new PtnComboBox();
		this.lblActivatedState = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ACTIVITY_STATUS));
		this.cmbActivatedState = new PtnComboBox();
	}
	
	@Override
	protected void setLayoutChild(JPanel panelChild) {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 80, 220 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40 };
		layout.rowWeights = new double[] { 0.1, 0.1, 0.1 };

		panelChild.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTH;
		
		// 板卡名称
		c.gridx = 0;
		c.gridy = 0;
		layout.setConstraints(this.lblCard, c);
		panelChild.add(this.lblCard);

		c.gridx = 1;
		layout.setConstraints(this.cmbCard, c);
		panelChild.add(this.cmbCard);

		
		// 端口名称
		c.gridx = 0;
		c.gridy = 1;
		layout.setConstraints(this.lblPort, c);
		panelChild.add(this.lblPort);

		c.gridx = 1;
		layout.setConstraints(this.cmbPort, c);
		panelChild.add(this.cmbPort);

		// pw名称
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.lblPwName, c);
		panelChild.add(this.lblPwName);

		c.gridx = 1;
		layout.setConstraints(this.cmbPwName, c);
		panelChild.add(this.cmbPwName);

		// 激活状态
		c.gridx = 0;
		c.gridy = 3;
		layout.setConstraints(this.lblActivatedState, c);
		panelChild.add(this.lblActivatedState);

		c.gridx = 1;
		layout.setConstraints(this.cmbActivatedState, c);
		panelChild.add(this.cmbActivatedState);
	}
	
	@Override
	protected void btnConfirmListener() {

		Object object = super.getObject();
		try {
			ServiceInfo serviceInfo = (ServiceInfo) object;
			serviceInfo.setName(super.getTxtName().getText());
			
			if (this.cmbCard.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbCard.getSelectedItem();
				serviceInfo.setCardId(Integer.parseInt(controlKeyValue.getId()));
			}
			
			if (this.cmbPort.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbPort.getSelectedItem();
				serviceInfo.setAportId(Integer.parseInt(controlKeyValue.getId()));
			}
			
			if (this.cmbPwName.getSelectedIndex() > -1) {
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbPwName.getSelectedItem();
				serviceInfo.setPwId(Integer.parseInt(controlKeyValue.getId()));
			}
			
			if(this.cmbActivatedState.getSelectedIndex()>-1){
				ControlKeyValue controlKeyValue = (ControlKeyValue) this.cmbActivatedState.getSelectedItem();
				serviceInfo.setActiveStatus(Integer.parseInt(controlKeyValue.getId()));
			}

			super.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	@Override
	protected void addListenerChild() {
		getComboBoxDataUtil().initUNIPortData(cmbPort, ConstantUtil.siteId, EServiceType.SITE);
		
		this.cmbCard.addItemListener(new java.awt.event.ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ControlKeyValue controlKeyValue = (ControlKeyValue) e.getItem();
					getComboBoxDataUtil().initUNIPortData(cmbPort, Integer.parseInt(controlKeyValue.getId()), EServiceType.CARD);
				}
			}
		});
	}

	@Override
	protected void loadData() {

		try {
			super.getComboBoxDataUtil().initNEData(this.cmbNEName);
			super.getComboBoxDataUtil().initCardData(this.cmbCard, ConstantUtil.siteId);
			super.getComboBoxDataUtil().initActivatedData(this.cmbActivatedState);
			super.getComboBoxDataUtil().initPWData(this.cmbPwName,1,true);
			loadFormData();
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	private JLabel lblCard;
	private PtnComboBox cmbCard;
}
