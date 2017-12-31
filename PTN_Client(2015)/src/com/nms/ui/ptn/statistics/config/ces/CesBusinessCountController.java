package com.nms.ui.ptn.statistics.config.ces;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.equipment.port.PortInst;
import com.nms.db.bean.equipment.port.PortStmTimeslot;
import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.enums.ECesType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.equipment.port.PortService_MB;
import com.nms.model.equipment.port.PortStmTimeslotService_MB;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.filter.impl.CesFilterDialog;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.business.ces.bean.CesPortTableBean;

/**
 */
public class CesBusinessCountController extends AbstractController {

	private CesBusinessCountPanel view;
	private CesInfo cesInfo = null;
	private List<CesInfo> infos = new ArrayList<CesInfo>();
	private int total;
	private int now = 0;
	
	public CesBusinessCountController(CesBusinessCountPanel view) {
		this.view = view;
		try {
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh() throws Exception {
		
		CesInfoService_MB service = null;
		ListingFilter filter = null;
		List<CesInfo> needs = new ArrayList<CesInfo>();
		try {
			filter = new ListingFilter();
			service = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);
			
			if(null==this.cesInfo){
				this.cesInfo=new CesInfo();
			}
			
			infos = (List<CesInfo>) filter.filterList(service.filterSelect(this.cesInfo));
			
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getGoToJButton().setEnabled(false);
			}else{
				now =1;
				if (infos.size() % ConstantUtil.flipNumber == 0) {
					total = infos.size() / ConstantUtil.flipNumber;
				} else {
					total = infos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getGoToJButton().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getGoToJButton().setEnabled(true);
				}
				if (infos.size() - (now - 1) * ConstantUtil.flipNumber > ConstantUtil.flipNumber) {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, ConstantUtil.flipNumber);
				} else {
					needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size() - (now - 1) * ConstantUtil.flipNumber);
				}
			}
			view.getCurrPageLabel().setText(now+"");
			view.getTotalPageLabel().setText(total + "");
			view.getPrevPageBtn().setEnabled(false);
			
			this.view.clear();

			if (view.getTopoPanel() != null) {
				view.getTopoPanel().clear();
			}
			if (view.getClientInfoPanel() != null) {
				view.getClientInfoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			if (view.getCesPortNetworkTablePanel() != null) {
				view.getCesPortNetworkTablePanel().clear();
			}
			if (view.getPwNetworkTablePanel() != null){
				this.view.getPwNetworkTablePanel().clear();
			}
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			filter = null;
			UiUtil.closeService_MB(service);
			filter = null;
		}
	}

	/**
	 * 选中一条记录后，查看详细信息
	 */
	@Override
	public void initDetailInfo() {
		try {
			initTopoPanel();
			this.initPwNetworkTablePanel();
			this.initPortPanel();
			this.initSchematizePanel();
			initClientInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwService = null;
		try {
			CesInfo cesInfo = this.view.getSelect();
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo pw = pwService.selectByPwId(cesInfo.getPwId());
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			pwList.add(pw);
			this.view.getPwNetworkTablePanel().initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwService);
		}
	}

	/**
	 * 列表点击事件（客户信息）
	 */
	private void initClientInfo() {
		CesInfo cesInfo = null;
		ClientService_MB clientService = null;
		List<Client> clientList = null;
		try {
			clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			cesInfo = view.getSelect();
			if (0 != cesInfo.getClientId()) {
				clientList = clientService.select(cesInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			cesInfo = null;
			clientList = null;
			UiUtil.closeService_MB(clientService);
		}

	}

	/**
	 * 初始化图形化界面数据
	 * 
	 * @author kk
	 * 
	 * @Exception 异常对象
	 */
	private void initSchematizePanel() {
		CesInfo cesInfo = null;
		try {
			cesInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(cesInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			cesInfo = null;
		}
	}

	/**
	 * 绑定端口列表数据
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void initPortPanel() {

		CesInfo cesInfo = null;
		List<CesPortTableBean> cesPortList = null;
		try {
			cesPortList = new ArrayList<CesPortTableBean>();
			cesInfo = view.getSelect();
			if (ECesType.PDH.getValue() == cesInfo.getCestype()) {
				cesPortList.add(this.getPdhPort(cesInfo.getaAcId())); // a端pdh
				cesPortList.add(this.getPdhPort(cesInfo.getzAcId())); // z端pdh
			} else if (ECesType.PDHSDH.getValue() == cesInfo.getCestype()) {
				cesPortList.add(this.getPdhPort(cesInfo.getaAcId())); // a端pdh
				cesPortList.add(this.getSdhPort(cesInfo.getzAcId())); // z端sdh
			} else if (ECesType.SDH.getValue() == cesInfo.getCestype()) {
				cesPortList.add(this.getSdhPort(cesInfo.getaAcId())); // a端sdh
				cesPortList.add(this.getSdhPort(cesInfo.getzAcId())); // z端sdh
			} else if (ECesType.SDHPDH.getValue() == cesInfo.getCestype()) {
				cesPortList.add(this.getSdhPort(cesInfo.getaAcId())); // a端sdh
				cesPortList.add(this.getPdhPort(cesInfo.getzAcId())); // z端pdh
			}
			this.view.getCesPortNetworkTablePanel().clear();
			this.view.getCesPortNetworkTablePanel().initData(cesPortList);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			cesInfo = null;
			cesPortList = null;
		}
	}

	/**
	 * 
	 * 获取sdh端口bean
	 * 
	 * @author kk
	 * 
	 * @param id
	 *            时隙表主键
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private CesPortTableBean getSdhPort(int id) throws Exception {

		CesPortTableBean cesPortTableBean = null;
		PortStmTimeslot portStmTimeslot = null;
		SiteService_MB siteServiceMB = null;
		PortService_MB portServiceMB = null;
		PortStmTimeslotService_MB portStmTimeslotService = null;
		try {
			portServiceMB = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			cesPortTableBean = new CesPortTableBean();
			portStmTimeslotService = (PortStmTimeslotService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORTSTMTIMESLOT);
			portStmTimeslot = portStmTimeslotService.selectById(id);
			cesPortTableBean.setObjectname(portStmTimeslot.getTimeslotnumber());
			cesPortTableBean.setPorttype("SDH");
			cesPortTableBean.setSitename(siteServiceMB.getSiteName(portStmTimeslot.getSiteId()));
			cesPortTableBean.setPortname(portServiceMB.getPortname(portStmTimeslot.getPortid()));
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portServiceMB);
			UiUtil.closeService_MB(siteServiceMB);
			UiUtil.closeService_MB(portStmTimeslotService);
		}
		return cesPortTableBean;
	}

	/**
	 * 
	 * 获取pdh端口bean
	 * 
	 * @author kk
	 * 
	 * @param id
	 *            时隙表主键
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @Exception 异常对象
	 */
	private CesPortTableBean getPdhPort(int id) throws Exception {

		CesPortTableBean cesPortTableBean = null;
		PortService_MB portService = null;
		PortInst portInst = null;
		List<PortInst> portList = null;
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
			cesPortTableBean = new CesPortTableBean();
			portService = (PortService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PORT);
			portInst = new PortInst();
			portInst.setPortId(id);
			portList = portService.select(portInst);

			if (null != portList && portList.size() == 1) {
				portInst = portList.get(0);
				cesPortTableBean.setObjectname(portInst.getPortName());
				cesPortTableBean.setPorttype("PDH");
				cesPortTableBean.setSitename(siteService.getSiteName(portInst.getSiteId()));
				cesPortTableBean.setPortname(portInst.getPortName());
			} else {
				cesPortTableBean = null;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(portService);
			UiUtil.closeService_MB(siteService);
		}
		return cesPortTableBean;
	}

	private void initTopoPanel() {
		CesInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			info = null;
		}
	}

	@Override
	public void openFilterDialog() throws Exception {
		new CesFilterDialog(this.cesInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.cesInfo = null;
		this.refresh();
	}
	
	@Override
	public void prevPage() throws Exception {
		now = now - 1;
		if (now == 1) {
			view.getPrevPageBtn().setEnabled(false);
		}
		view.getNextPageBtn().setEnabled(true);

		flipRefresh();
	}

	@Override
	public void goToAction() throws Exception {

		if (CheckingUtil.checking(view.getGoToTextField().getText(), CheckingUtil.NUM1_9)) {// 判断填写是否为数字
			Integer goi = Integer.parseInt(view.getGoToTextField().getText());
			if(goi>= total){
				goi = total;
				view.getNextPageBtn().setEnabled(false);
			}
			if(goi == 1){
				view.getPrevPageBtn().setEnabled(false);
			}
			if(goi > 1){
				view.getPrevPageBtn().setEnabled(true);
			}
			if(goi<total){
				view.getNextPageBtn().setEnabled(true);
			}
			now = goi;
			flipRefresh();
		}else{
			DialogBoxUtil.errorDialog(view, ResourceUtil.srcStr(StringKeysTip.MESSAGE_NUMBER));
		}
	}

	@Override
	public void nextPage() throws Exception {
		now = now + 1;
		if (now == total) {
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}

	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<CesInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();

		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getClientInfoPanel() != null) {
			view.getClientInfoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		if (view.getCesPortNetworkTablePanel() != null) {
			view.getCesPortNetworkTablePanel().clear();
		}
		if (view.getPwNetworkTablePanel() != null){
			this.view.getPwNetworkTablePanel().clear();
		}
		this.view.initData(needs);
		this.view.updateUI();
	}
}
