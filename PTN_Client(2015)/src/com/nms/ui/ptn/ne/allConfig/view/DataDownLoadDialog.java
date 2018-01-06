package com.nms.ui.ptn.ne.allConfig.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import com.nms.db.bean.equipment.card.CardInst;
import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.enums.EManufacturer;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.equipment.card.CardService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.equipment.slot.SlotService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.manager.util.EquimentDataUtil;
import com.nms.ui.ptn.systemconfig.dialog.fieldConfig.view.FieldConfigRightPanel;
/**
 * 数据下载
 * @author dzy
 */
public class DataDownLoadDialog extends PtnDialog{
	
	private static final long serialVersionUID = 636965342639859200L;
	private JCheckBox basicFunctions; //基础模块
	private JCheckBox port;//端口
	private JCheckBox ptnCoreModel;//PTN核心模块
	private JCheckBox l2vpnModel;//L2VPN模块
	private JCheckBox protect;//保护
	private JCheckBox clockFrequency;//时钟频率
	private JCheckBox ces;//CES
	private JCheckBox alarm;//告警
	private PtnButton btnSave;//保存
	private JButton btnCanel;//取消
	private JCheckBox allSelect;//全选
	private FieldConfigRightPanel fieldConfigRightPanel;
	private JLabel line;
	private String action;//操作方法
	/**
	 * 构造方法,
	 * @param fieldConfigRightPanel
	 * 							主面板
	 * @param modal
	 */
	public DataDownLoadDialog(FieldConfigRightPanel fieldConfigRightPanel, boolean modal,String action) {
		this.setModal(modal);
		this.fieldConfigRightPanel = fieldConfigRightPanel;
		this.action = action;
		try {
			initComponentss();
			this.setLayout();
			this.addListener();
			UiUtil.showWindow(this, 280, 390);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}

	/**
	 * 点击事件
	 */
	private void addListener() {
		this.btnSave.addActionListener(new MyActionListener() {
			@Override
			public boolean checking() {
				return chech();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveActionPerformed(e);

			}
			
		});
		this.btnCanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canelActionPerformed(e);
			}
		});
		this.allSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allSelectActionPerformed(e);
			}
		});
		this.port.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("port");
			}
		});
		this.ptnCoreModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("ptnCoreModel");
			}
		});
		this.l2vpnModel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("l2vpnModel");
			}
		});
		this.protect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("protect");
			}
		});
		this.clockFrequency.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("clockFrequency");
			}
		});
		this.ces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("ces");
			}
		
		});
		this.basicFunctions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("basicFunctions");
			}
		});
		this.alarm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEditor("alarm");
			}
		});
	}
	
	/**
	 * 判断选择是否为空
	 * @return
	 */
	private boolean chech(){
		boolean flag = false;
		try {
			if (basicFunctions.isSelected()== false) {
				if("synchro".equals(this.action)){
					DialogBoxUtil.errorDialog(fieldConfigRightPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_SYNCHRO));
				}else{
					DialogBoxUtil.errorDialog(fieldConfigRightPanel, ResourceUtil.srcStr(StringKeysTip.TIP_SELECT_DATA_DOWNLOAD));
				}
				return false;
			}else{
				flag = true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return flag ;
	}
	/**
	 * 点击事件
	 */
	private void checkBoxEditor(String type) {
		basicFunctions.setEnabled(true);
		port.setEnabled(true);
		ptnCoreModel.setEnabled(true);
		l2vpnModel.setEnabled(true);
		protect.setEnabled(true);
		protect.isSelected();
		clockFrequency.setEnabled(true);
		ces.setEnabled(true);
		this.alarm.setEnabled(true);
		//一下是每个选项的点击规则
		if(port.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(ptnCoreModel.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(l2vpnModel.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
			ptnCoreModel.setSelected(true);
			ptnCoreModel.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(protect.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
			ptnCoreModel.setSelected(true);
			ptnCoreModel.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(clockFrequency.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(ces.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
			ptnCoreModel.setSelected(true);
			ptnCoreModel.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(this.alarm.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
			ptnCoreModel.setSelected(true);
			ptnCoreModel.setEnabled(false);
		}else{
			this.allSelect.setSelected(false);
		}
		if(!basicFunctions.isSelected()){
			basicFunctions.setSelected(false);
			port.setSelected(false);
			ptnCoreModel.setSelected(false);
			l2vpnModel.setSelected(false);
			protect.setSelected(false);
			clockFrequency.setSelected(false);
			ces.setSelected(false);
			this.allSelect.setSelected(false);
		}
	}

	/**
	 * 全选 
	 * @param e
	 */
	private void allSelectActionPerformed(ActionEvent e) {
		if(this.allSelect.isSelected()){
			basicFunctions.setSelected(true);
			basicFunctions.setEnabled(false);
			port.setSelected(true);
			port.setEnabled(false);
			ptnCoreModel.setSelected(true);
			ptnCoreModel.setEnabled(false);
			l2vpnModel.setSelected(true);
			protect.setSelected(true);
			clockFrequency.setSelected(true);
			ces.setSelected(true);
			if("synchro".equals(this.action)){
				this.alarm.setSelected(true);
			}
		}else{
			basicFunctions.setSelected(false);
			basicFunctions.setEnabled(true);
			port.setSelected(false);
			port.setEnabled(true);
			ptnCoreModel.setSelected(false);
			ptnCoreModel.setEnabled(true);
			l2vpnModel.setSelected(false);
			l2vpnModel.setEnabled(true);
			protect.setSelected(false);
			protect.setEnabled(true);
			clockFrequency.setSelected(false);
			clockFrequency.setEnabled(true);
			ces.setSelected(false);
			ces.setEnabled(true);
			if("synchro".equals(this.action)){
				this.alarm.setSelected(false);
				this.alarm.setEnabled(true);
			}
		}
		
		
	}
	/**
	 * 组件初始化
	 * @throws Exception
	 */
	private void initComponentss() throws Exception {
		try {
			if("dataDownLoad".equals(this.action)){
				this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_DATE_DOWNLOAD));
			}
			if("synchro".equals(this.action)){
				this.setTitle(ResourceUtil.srcStr(StringKeysBtn.BTN_SYNCHRO));
			}
			this.basicFunctions = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_BASICFUNCTIONS));
			this.port = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT));
			this.ptnCoreModel = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_PTNCOREMODEL));
			this.l2vpnModel = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_L2VPNMODEL));
			this.protect = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_PROTECT));
			this.clockFrequency = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_CLOCKFREQUENCY));
			this.ces = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_CES));
			this.alarm = new JCheckBox(ResourceUtil.srcStr(StringKeysLbl.LBL_ALARM_ALARM));
			this.allSelect = new JCheckBox(ResourceUtil.srcStr(StringKeysBtn.BTN_ALLSELECT));
			this.btnSave = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_SAVE),true);
			this.btnCanel = new JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.line= new JLabel("_____________________________");
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	/**
	 * 布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] { 120 ,50};
		componentLayout.columnWeights = new double[] { 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 15, 30, 30, 30, 30, 30, 30,10,30,20,15 };
		componentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0 };
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.basicFunctions, c);
		this.add(this.basicFunctions);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.port, c);
		this.add(this.port);
		c.gridy = 2;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.ptnCoreModel, c);
		this.add(this.ptnCoreModel);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.l2vpnModel, c);
		this.add(this.l2vpnModel);
		c.gridy = 4;
		componentLayout.setConstraints(this.protect, c);
		this.add(this.protect);
		
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.clockFrequency, c);
		this.add(this.clockFrequency);
		
		if("synchro".equals(this.action)){
			c.gridy = 6;
			componentLayout.setConstraints(this.ces, c);
			this.add(this.ces);
			c.gridy = 7;
			componentLayout.setConstraints(this.alarm, c);
			this.add(this.alarm);
		}
		
		c.gridy = 8;
		c.gridwidth = 2;
		componentLayout.setConstraints(this.line, c);
		this.add(this.line);
		
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 9;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.allSelect, c);
		this.add(this.allSelect);
		c.insets = new Insets(5, 5, 5, 5);
		/*componentLayout.setConstraints(this.unSelect, c);
		this.add(this.unSelect);*/
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 1;
		componentLayout.setConstraints(this.btnSave, c);
		this.add(this.btnSave);
		c.gridx = 2;
		componentLayout.setConstraints(this.btnCanel, c);
		this.add(this.btnCanel);
	}

	/**
	 * 取消事件
	 * @param evt
	 */
	private void canelActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void saveActionPerformed(java.awt.event.ActionEvent evt) {
		DispatchUtil siteDispatch = null;
		int modelSelected[] = new int[8]; 
		try{
			if(basicFunctions.isSelected()){
				modelSelected[0]=1;
			}
			if(port.isSelected()){
				modelSelected[1]=1;
			}
			if(ptnCoreModel.isSelected()){
				modelSelected[2]=1;
			}
			if(l2vpnModel.isSelected()){
				modelSelected[3]=1;
			}
			if(protect.isSelected()){
				modelSelected[4]=1;
			}
			if(clockFrequency.isSelected()){
				modelSelected[5]=1;
			}
			if(ces.isSelected()){
				modelSelected[6]=1;
			}
			if(alarm.isSelected()){
				modelSelected[7]=1;
			}
			siteDispatch = new DispatchUtil(RmiKeys.RMI_SITE);
			if("dataDownLoad".equals(this.action)){
				String result = siteDispatch.dataDownLoadActionPerformed(fieldConfigRightPanel.getTable().getAllSelect(),modelSelected);
				DialogBoxUtil.succeedDialog(this, result);
				//添加日志记录
				this.btnSave.setOperateKey(EOperationLogType.SITELISTDOWNLODA.getValue());
				btnSave.setResult(1);
			}
			if("synchro".equals(this.action)){
				this.synchro(fieldConfigRightPanel.getTable().getAllSelect(),modelSelected);
				//添加日志记录
				this.btnSave.setOperateKey(EOperationLogType.SITELISTSYNCHRO.getValue());
				this.btnSave.setResult(1);
			}
			this.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			siteDispatch = null;
		}

	}

	/**
	 * 网元同步
	 * @param siteInstList
	 * 					搜索的网元集合
	 * @param modelSelected
	 * 			被选择的模块
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void synchro(List<SiteInst> siteInstList, int[] modelSelected) throws RemoteException, Exception {
		SiteService_MB siteService = null;
		try {
			DispatchUtil portDispatch = new DispatchUtil(RmiKeys.RMI_PORT);
			DispatchUtil qosDispatch=new DispatchUtil(RmiKeys.RMI_QOS);
			DispatchUtil tunnelDispatch = new DispatchUtil(RmiKeys.RMI_TUNNEL);
			DispatchUtil pwDispatch = new DispatchUtil(RmiKeys.RMI_PW);
			DispatchUtil acDispatch = new DispatchUtil(RmiKeys.RMI_AC);
			DispatchUtil elineDispatch = new DispatchUtil(RmiKeys.RMI_ELINE);
			DispatchUtil etreeDispatch = new DispatchUtil(RmiKeys.RMI_ETREE);
			DispatchUtil elanDispatch = new DispatchUtil(RmiKeys.RMI_ELAN);
			DispatchUtil cesDispatch = new DispatchUtil(RmiKeys.RMI_CES);
			DispatchUtil eDispatch = new DispatchUtil(RmiKeys.RMI_E1);
//			DispatchUtil ccnDispatch = new DispatchUtil(RmiKeys.RMI_CCN);
			DispatchUtil ospfInfoDispatch = new DispatchUtil(RmiKeys.RMI_OSPFINFO);
			DispatchUtil ospfAreaDispatch = new DispatchUtil(RmiKeys.RMI_OSPFAREA);
			DispatchUtil redistributeDispatch = new DispatchUtil(RmiKeys.RMI_REDISTRIBUTE);
			DispatchUtil portStmDispatch = new DispatchUtil(RmiKeys.RMI_PORTSTM);
			DispatchUtil portStmTimeslotDispatch = new DispatchUtil(RmiKeys.RMI_PORTSTMTIMESLOT);
			DispatchUtil mcnDispatch = new DispatchUtil(RmiKeys.RMI_MCN);
			DispatchUtil ospfInterfaceDispatch = new DispatchUtil(RmiKeys.RMI_OSPFINTERFACE);
			DispatchUtil portLagDispatch = new DispatchUtil(RmiKeys.RMI_PORTLAG);
			DispatchUtil clockFrequDispatch =new DispatchUtil(RmiKeys.RMI_CLOCKFREQU);
//			DispatchUtil lineClockInterfaceDispatch=new DispatchUtil(RmiKeys.RMI_LINECLOCKINTERFACE);
//			DispatchUtil externalClockDispatch=new DispatchUtil(RmiKeys.RMI_EXTERNALCLOCK);
//			DispatchUtil timePortConfigDispatch=new DispatchUtil(RmiKeys.RMI_TIMEPORTCONFIG);
			DispatchUtil alarmDispatch = new DispatchUtil(RmiKeys.RMI_ALARM);
			DispatchUtil wrappingDispatch = new DispatchUtil(RmiKeys.RMI_WRAPPING);
			DispatchUtil expMappingPhbDispatch = new DispatchUtil(RmiKeys.RMI_EXPMAPPINGPHB);
//			List<SiteInst> list = fieldConfigRightPanel.getTable().getAllSelect();
			List<SiteInst> actionList = new ArrayList<SiteInst>();
			DispatchUtil mspDispatch = new DispatchUtil(RmiKeys.RMI_MSPPROTECT);
			DispatchUtil dualDispatch = new DispatchUtil(RmiKeys.RMI_DUALPROTECT);
			//离线或虚拟网元不同步
			for (SiteInst siteInst : siteInstList) {
				if(1==siteInst.getLoginstatus()&&"0".equals(UiUtil.getCodeById(siteInst.getSiteType()).getCodeValue())){
					actionList.add(siteInst);
				}
			}
			siteService=(SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			for (SiteInst siteInst : actionList) {
//				if(siteService.getManufacturer(siteInst.getSite_Inst_Id()) == EManufacturer.CHENXIAO.getValue()){
//					if(modelSelected[0]==1){
//						this.synchroCard(siteInst); // 同步板卡
//						ospfInfoDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ospf
//						ospfAreaDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步area
//						redistributeDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步重分发
//						mcnDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步MCN
//	//					ccnDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ccn
//						ospfInterfaceDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ospf接口
//					}
//					if(modelSelected[1]==1){
//						expMappingPhbDispatch.synchro(siteInst.getSite_Inst_Id()); //EXP映射
//						portDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步eth端口
//						portLagDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步LAG
//						acDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ac端口
//						eDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步pdh端口
//						portStmDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步SDH端口
//						portStmTimeslotDispatch.synchro(siteInst.getSite_Inst_Id());// 同步SDH时隙
//					}
//					if(modelSelected[2]==1){
//						qosDispatch.synchro(siteInst.getSite_Inst_Id());	//同步qos
//						tunnelDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步tunnel
//						pwDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步pw
//					}
//					if(modelSelected[3]==1){
//						elineDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步eline
//						etreeDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步etree
//						elanDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步elan
//					}
//					if(modelSelected[4]==1){
//						wrappingDispatch.synchro(siteInst.getSite_Inst_Id());//环保护
//						mspDispatch.synchro(siteInst.getSite_Inst_Id());//MSP保护
//						dualDispatch.synchro(siteInst.getSite_Inst_Id());//双规保护
//					}
//					if(modelSelected[5]==1){
////						clockSourceDispatch.synchro(siteInst.getSite_Inst_Id());//同步时钟源
////						externalClockDispatch.synchro(siteInst.getSite_Inst_Id());//同步外时钟接口
////						timePortConfigDispatch.synchro(siteInst.getSite_Inst_Id());//同步端口配置
////						lineClockInterfaceDispatch.synchro(siteInst.getSite_Inst_Id());//同步线路（时钟）接口
//					}
//					if(modelSelected[6]==1){
//						cesDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ces
//					}
//				}else{
//					if(modelSelected[1]==1){
						portDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步eth端口
						portLagDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步LAG
						eDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步pdh端口
//					}
//					if(modelSelected[2]==1){
						tunnelDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步tunnel
						pwDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步pw
//					}
//					if(modelSelected[3]==1){
						elineDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步eline
						etreeDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步etree
						elanDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步elan
//					}
//					if(modelSelected[5]==1){
						clockFrequDispatch.synchro(siteInst.getSite_Inst_Id());//同步时钟源
//					}
//					if(modelSelected[6]==1){
						cesDispatch.synchro(siteInst.getSite_Inst_Id()); // 同步ces
//					}
//				}
//				if(modelSelected[7]==1){
					alarmDispatch.synchroCurrentAlarm(siteInst.getSite_Inst_Id());// 同步告警
//				}
			}
//			ConstantUtil.waitDialog.closeWait();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(siteService);
		}
	}

	/**
	 * 同步板卡
	 * @param siteInst
	 * @throws Exception
	 */
	private void synchroCard(SiteInst siteInst) throws Exception {
		DispatchUtil cardDispatch = null;
		SlotService_MB slotService = null;
		SlotInst slotinst = null;
		List<SlotInst> slotList = null;
		String xmlPath = null;
		CardService_MB cardService = null;
		CardInst cardInst = null;
		EquimentDataUtil equimentDataUtil=new EquimentDataUtil();
		try {
			cardService = (CardService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CARD);
			cardDispatch = new DispatchUtil(RmiKeys.RMI_CARD);
			Map<Integer, String> map = cardDispatch.matchingCard(siteInst.getSite_Inst_Id());
			slotService = (SlotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SLOT);
			slotinst = new SlotInst();
			slotinst.setSiteId(siteInst.getSite_Inst_Id());
			slotList = slotService.select(slotinst);

			for (int i = 0; i < slotList.size(); i++) {
				if (slotList.get(i).getCardId() == 0) {
					//kk改动  读取xml配置时 key为 板卡名称_网元类型
					xmlPath = equimentDataUtil.getXmlPathByCardName(map.get(slotList.get(i).getNumber())+"_"+siteInst.getCellType());
					if (xmlPath != null && !xmlPath.equals("")) {
						cardInst = equimentDataUtil.addCard(xmlPath, slotList.get(i));
						cardService.saveOrUpdate(cardInst);
					}
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			cardDispatch = null;
			UiUtil.closeService_MB(slotService);
			slotinst = null;
			slotList = null;
			xmlPath = null;
			UiUtil.closeService_MB(cardService);
			cardInst = null;
		}

	}
}
