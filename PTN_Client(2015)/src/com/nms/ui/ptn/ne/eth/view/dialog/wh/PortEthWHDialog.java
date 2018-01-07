﻿package com.nms.ui.ptn.ne.eth.view.dialog.wh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.db.bean.ptn.port.AcPortInfo;
import com.nms.db.bean.system.code.Code;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.tunnel.TunnelService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.CodeConfigItem;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnSpinner;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysObj;
import com.nms.ui.ptn.clock.view.cx.time.TextFiledKeyListener;
import com.nms.ui.ptn.ne.eth.view.dialog.base.PortEthDialog;

/**
 * 
*    
* 项目名称：WuHanPTN2012   
* 类名称：PortEthWHDialog   
* 类描述：   端口基本信息武汉界面
* 创建人：kk   
* 创建时间：2013-7-15 上午11:52:02   
* 修改人：kk   
* 修改时间：2013-7-15 上午11:52:02   
* 修改备注：   
* @version    
*
 */
public class PortEthWHDialog extends PortEthDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5381813758986426586L;
	
	private PortInst portInst = null;
	private JTextField txtFrameWH;
	private JComboBox cmbtypeWH;
	
	/**
	 * 创建一个新的实例
	 */
	public PortEthWHDialog(PortInst portInst){
		
		try {
			this.portInst = portInst;
			this.initComponents();
			this.initData();
			checkPort();
			txtFrameWH=super.getTxtFrame();
			cmbtypeWH=super.getCmbType();
			this.addListener();
			addKeyListenerForTextfield();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 监听事件
	 */
	private void addListener() { 
		try {
			this.cmbtypeWH.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ControlKeyValue key_type = (ControlKeyValue)cmbtypeWH.getSelectedItem();
					if(key_type.getName().equalsIgnoreCase("UNI")){
						txtFrameWH.setText("1518");
					}
					else if(key_type.getName().equalsIgnoreCase("NNI")){
						txtFrameWH.setText("1600");
					}
				}
			});
			txtFrameWH.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					ControlKeyValue key_type = (ControlKeyValue)cmbtypeWH.getSelectedItem();
					if(key_type.getName().equalsIgnoreCase("UNI")){
						/* 区间是否为1518-9600 */ 
							String errorMess = ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_FRAME_WORDS) + ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_FRAME_WORDSNNI);
							int inputNumber = Integer.parseInt(txtFrameWH.getText());
							if (inputNumber < 1518 || inputNumber > 9600) {
								JOptionPane.showMessageDialog(null, errorMess);
								txtFrameWH.setText("1518");
							}
					}
						if(key_type.getName().equalsIgnoreCase("NNI")){
							/* 区间是否为1600-9600 */                                                    
								String errorMess = ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_FRAME_WORDS) +  ResourceUtil.srcStr(StringKeysLbl.LBL_MAX_FRAME_WORDSUNI);
								int inputNumber = Integer.parseInt(txtFrameWH.getText());
								if (inputNumber < 1600 || inputNumber > 9600) {
									JOptionPane.showMessageDialog(null, errorMess);
									txtFrameWH.setText("1600");
								}
					}
				}
				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	/**
	 * 验证端口
	 */
	private void checkPort(){
		if(CodeConfigItem.getInstance().getNoCheck() == 0){//强制取消验证关闭
			//如果被lag使用，端口类型不让修改
			if(this.portInst.getLagId()>0){
				this.getCmbType().setEnabled(false);
				this.getCmbStatus().setEnabled(false);
			}
			
			// 如果被段占用了，不能修改端口的类型
			if (this.portInst.getIsOccupy() != 0) {
				this.getCmbType().setEnabled(false);
				this.getCmbStatus().setEnabled(false);
				this.workCmbModel.setEnabled(false);
			}
			// 如果被ac用过的端口，不能修改端口类型
			List<AcPortInfo> acPortList = getAcPortInfoByPort();
			if (acPortList != null && !acPortList.isEmpty()) {
				this.getCmbType().setEnabled(false);
				this.getCmbStatus().setEnabled(false);
			}
			//单网元没有段，如果被tunnel使用，不能修改端口的类型
			List<Tunnel> tunnels = getTunnelByPort();
			if(tunnels != null && tunnels.size()>0){
				this.getCmbType().setEnabled(false);
				this.getCmbStatus().setEnabled(false);
			}
			
			//如果被qinq占用了,不能修改端口的qinq使能,不能修改类型
//			List<QinqInst> qinqs = getQinqByPort();
//			if(qinqs != null && qinqs.size()>0){
//				this.qinqCmboBox.setEnabled(false);
//				this.getCmbType().setEnabled(false);
//				this.getCmbStatus().setEnabled(false);
//			}
		}
	}
	
//	/**
//	 * 根据siteId和portId查询qinq
//	 * @return
//	 */
//	private List<QinqInst> getQinqByPort() {
//		QinQInstService qinqService = null;
//		List<QinqInst> qinqs = null;
//		try {
//			qinqService = (QinQInstService) ConstantUtil.serviceFactory.newService(Services.QinQ);
//			if(portInst.getPortType().equals("UNI")){
//				qinqs = qinqService.selectByPortIdAndSiteIdAndUni(ConstantUtil.siteId, portInst.getPortId());
//			}else{
//				qinqs = qinqService.selectByPortIdAndSiteId(ConstantUtil.siteId, portInst.getPortId());
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		}finally{
//			UiUtil.closeService(qinqService);
//		}
//		return qinqs;
//	}
	
	/**
	 * 根据siteId和portId查询tunnel
	 * @return
	 */
	private List<Tunnel> getTunnelByPort() {
		TunnelService_MB tunnelService = null;
		List<Tunnel> tunnels = null;
		try {
			tunnelService = (TunnelService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Tunnel);
			tunnels = tunnelService.selectByPortIdAndSiteId(ConstantUtil.siteId,portInst.getPortId());
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(tunnelService);
		}
		return tunnels;
	}
	
	/**
	 * 通过端口号查找ac
	 * @return
	 */
	private List<AcPortInfo> getAcPortInfoByPort() {
		AcPortInfoService_MB acInfoService = null;
		AcPortInfo acportInfo = null;
		try {
			acportInfo = new AcPortInfo();
			acportInfo.setPortId(this.portInst.getPortId());
			acInfoService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo);
			return acInfoService.queryByAcPortInfo(acportInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(acInfoService);
		}
		return null;
	}
	
	/**
	 * 获取端口对象
	 * @param portInst
	 * 			端口对象
	 * @throws Exception
	 */
	public void getPortInst(PortInst portinst)throws Exception {
		
		if(null==portinst){
			throw new Exception("portInst is null");
		}
		portinst.setPortName(super.getTxtName().getText());
		portinst.getPortAttr().setMaxFrameSize(super.getTxtFrame().getText());
		
		ControlKeyValue key_status = (ControlKeyValue)super.getCmbStatus().getSelectedItem();
		portinst.setIsEnabled_code(Integer.parseInt(((Code)key_status.getObject()).getCodeValue()));
		
		ControlKeyValue key_type = (ControlKeyValue)super.getCmbType().getSelectedItem();
		portinst.setPortType(((Code)key_type.getObject()).getCodeValue());
		
		ControlKeyValue key_fluid = (ControlKeyValue)super.getCmbFluid().getSelectedItem();
		portinst.getPortAttr().setFluidControl(Integer.parseInt(key_fluid.getId()));
		
		ControlKeyValue key_model = (ControlKeyValue)this.workCmbModel.getSelectedItem();
		portinst.getPortAttr().setWorkModel(Integer.parseInt(key_model.getId()));
		
		portinst.getPortAttr().setExitLimit(super.getSpnExitSpeed().getTxtData());
		
		ControlKeyValue key_qinq = (ControlKeyValue)this.qinqCmboBox.getSelectedItem();
		portinst.setIsEnabled_QinQ(Integer.parseInt(((Code)key_qinq.getObject()).getCodeValue()));
		
		ControlKeyValue key_laser = (ControlKeyValue)this.laserCmboBox.getSelectedItem();
		portinst.setIsEnabledLaser(Integer.parseInt(((Code)key_laser.getObject()).getCodeValue()));
		
		portinst.setIsEnabledAlarmReversal(this.alarmReversalComboBox.getSelectedIndex());
		
		ControlKeyValue key_servicePort = (ControlKeyValue)this.servicePortComboBox.getSelectedItem();
		portinst.setServicePort(Integer.parseInt(((Code)key_servicePort.getObject()).getCodeValue()));
	}
	
	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private void initData() throws Exception {
		super.getComboBoxDataUtil().comboBoxData(super.getCmbStatus(), "ENABLEDSTATUE");
		
		if("".equals(portInst.getPortAttr().getMaxFrameSize()) || null==portInst.getPortAttr().getMaxFrameSize()){
			super.getTxtFrame().setText("1518");
		}else{
			super.getTxtFrame().setText(portInst.getPortAttr().getMaxFrameSize());
		}
			
		super.getTxtName().setText(portInst.getPortName()+"");
		
		super.getSpnExitSpeed().setTxtData(portInst.getPortAttr().getExitLimit() == null ? "0" : portInst.getPortAttr().getExitLimit());

		super.getComboBoxDataUtil().comboBoxSelectByValue(super.getCmbStatus(), portInst.getIsEnabled_code()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(super.getCmbType(),portInst.getPortType());
		
		super.getComboBoxDataUtil().comboBoxSelect(super.getCmbFluid(),portInst.getPortAttr().getFluidControl()+"");
		super.getComboBoxDataUtil().comboBoxSelect(workCmbModel,portInst.getPortAttr().getWorkModel()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(qinqCmboBox,portInst.getIsEnabled_QinQ()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(laserCmboBox,portInst.getIsEnabledLaser()+"");
		super.getComboBoxDataUtil().comboBoxSelectByValue(servicePortComboBox,portInst.getServicePort()+"");
		alarmReversalComboBox.setSelectedIndex(portInst.getIsEnabledAlarmReversal());
	}

	/**
	 * 初始化控件
	 * @throws Exception
	 */
	private void initComponents() throws Exception{
		 SiteInst site=null;;
		 SiteService_MB siteService = null;
		 String portName = null;
		try {
			portName = portInst.getPortName();
			if(portName.contains("fe")){
				super.spnExitSpeed = new PtnSpinner(super.getChbExitSpeed(), 100000,0, 0,64);
			}else if(portName.contains("ge") || portName.contains("fx")){
				super.spnExitSpeed = new PtnSpinner(super.getChbExitSpeed(), 1000000,0, 0,64);
			}else if(portName.contains("xg")){
				super.spnExitSpeed = new PtnSpinner(super.getChbExitSpeed(), 10000000,0, 0,64);
			}
			
			this.labModel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_MODAL));
			this.workCmbModel = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.workCmbModel, "WORKMODE");
			
			site=new SiteInst();
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			site = siteService.select(portInst.getSiteId());
			
			if(portInst.getPortName().contains("fe")){
				workCmbModel.removeItemAt(4);
				workCmbModel.removeItemAt(1);
			}else if(portInst.getPortName().contains("fx")){
				workCmbModel.removeItemAt(6);
				workCmbModel.removeItemAt(3);
			}
//			if(site.getCellType().contains("703")&&(portInst.getPortName().equals("ge.1.1")||portInst.getPortName().equals("ge.1.2"))){				
//				workCmbModel.removeItemAt(6);
//				workCmbModel.removeItemAt(5);
//				workCmbModel.removeItemAt(3);
//				workCmbModel.removeItemAt(2);
//				
//				
//			}
//			else if(site.getCellType().contains("703")&&portInst.getPortName().contains("ge")){
//				workCmbModel.removeItemAt(6);
//				workCmbModel.removeItemAt(5);
//				workCmbModel.removeItemAt(3);
//				workCmbModel.removeItemAt(2);
//			}
//			else if((site.getCellType().contains("703")||site.getCellType().contains("710"))&&portInst.getPortName().contains("fe")){
//				
//				workCmbModel.removeItemAt(4);
//				workCmbModel.removeItemAt(1);
//			}
//			else if(site.getCellType().contains("703")&&(portInst.getPortName().trim().equals("ge.1.3")||portInst.getPortName().trim().equals("ge.1.4"))){
//				workCmbModel.removeItemAt(6);
//				workCmbModel.removeItemAt(3);
//			}
//			
//			else if(site.getCellType().contains("703")&&portInst.getPortName().contains("fx")){
//				workCmbModel.removeItemAt(6);
//				workCmbModel.removeItemAt(3);
//			}
//			
//			else if(site.getCellType().contains("703")&&(portInst.getPortName().contains("ge"))){
//				workCmbModel.removeItemAt(6);
//				workCmbModel.removeItemAt(5);
//				workCmbModel.removeItemAt(3);
//				workCmbModel.removeItemAt(2);
//			}
			laserEnabled = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_LASER_ENABLE));
			laserCmboBox = new JComboBox();
			alarmReversal = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_REVERSAL));
			alarmReversalComboBox = new JComboBox();
			servicePortState = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SERVICESTATE));
			servicePortComboBox = new JComboBox();
			macAddresslabel = new JLabel("MAC");
			macAddressText = new JTextField("00-85-DE-"+this.getMac(this.portInst.getNumber())+"-EF-"+this.getMac(this.portInst.getNumber()));
			macAddressText.setEditable(false);
			super.getComboBoxDataUtil().comboBoxData(this.laserCmboBox, "LASERENABLED");
			super.getComboBoxDataUtil().comboBoxData(this.servicePortComboBox, "SERVICELOOPSTATE");
			alarmReversalComboBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED_NO));
			alarmReversalComboBox.addItem(ResourceUtil.srcStr(StringKeysObj.ALLCONFIG_FID_ENABLED));
			this.labQinQEnabled = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_QINQ_ENABLE));
			this.qinqCmboBox = new JComboBox();
			super.getComboBoxDataUtil().comboBoxData(this.qinqCmboBox, "ENABLEDSTATUE");
			
			super.addComponent(this, super.getChbExitSpeed(), 4, 3, gridCon);
			super.addComponent(this, super.getSpnExitSpeed(), 5, 3, gridCon);
			super.addComponent(this, labModel, 1, 4, gridCon);
			super.addComponent(this, workCmbModel, 2, 4, gridCon);
			super.addComponent(this, labQinQEnabled, 4, 4, gridCon);
			super.addComponent(this, qinqCmboBox, 5, 4, gridCon);
			super.addComponent(this, laserEnabled, 1, 5, gridCon);
			super.addComponent(this, laserCmboBox, 2, 5, gridCon);
			super.addComponent(this, alarmReversal, 4, 5, gridCon);
			super.addComponent(this, alarmReversalComboBox, 5, 5, gridCon);
			super.addComponent(this, servicePortState, 1, 6, gridCon);
			super.addComponent(this, servicePortComboBox, 2, 6, gridCon);
			super.addComponent(this, macAddresslabel, 4, 6, gridCon);
			super.addComponent(this, macAddressText, 5, 6, gridCon);
			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}finally{
			UiUtil.closeService_MB(siteService);
		}
	}
	
	private String getMac(int num){
		String val = Integer.toHexString(this.portInst.getNumber()).toUpperCase();
		if(val.length() < 2){
			val = "0"+val;
		}
		return val;
	}
	
	/**
	 * <p>
	 * textfield添加监听，只允许输入数字
	 * </p>
	 * @throws Exception
	 */
	private void addKeyListenerForTextfield()throws Exception{
		
		TextFiledKeyListener textFIledKeyListener=null;
		try{
			/* 为1：只接受数字 **/
			textFIledKeyListener = new TextFiledKeyListener(1);
			super.getTxtFrame().addKeyListener(textFIledKeyListener);
		}catch(Exception e){
			
			throw e;
		}
	}

	public JComboBox getAlarmReversalComboBox() {
		return alarmReversalComboBox;
	}

	public void setAlarmReversalComboBox(JComboBox alarmReversalComboBox) {
		this.alarmReversalComboBox = alarmReversalComboBox;
	}

	private JLabel labModel;//工作模式
	private JComboBox workCmbModel;//工作模式下拉列表
	private JLabel labQinQEnabled;//QinQ使能
	private JComboBox qinqCmboBox;//QinQ使能下拉列表
    private JLabel laserEnabled;//激光器使能
    private JComboBox  laserCmboBox;//激光器关闭使能
    private JLabel alarmReversal;//告警反转使能
    private JComboBox alarmReversalComboBox;//告警反转使能
    private JLabel servicePortState;//业务端口环回状态
    private JComboBox  servicePortComboBox;
    private JLabel macAddresslabel;//端口mac地址
    private JTextField macAddressText;
    
}