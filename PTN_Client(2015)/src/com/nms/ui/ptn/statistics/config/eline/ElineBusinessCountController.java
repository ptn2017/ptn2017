package com.nms.ui.ptn.statistics.config.eline;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.ptn.oam.OamInfo;
import com.nms.db.bean.ptn.oam.OamMepInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.enums.EServiceType;
import com.nms.model.client.ClientService_MB;
import com.nms.model.ptn.oam.OamInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.filter.impl.EthServiceFilterDialog;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.systemconfig.dialog.qos.ComparableSort;

/**
 */
public class ElineBusinessCountController extends AbstractController {

	private final ElineBusinessCountPanel view;
	private ElineInfo elineInfo = null;
	private int total;
	private int now = 0;
	private List<ElineInfo> infos = null;

	public ElineBusinessCountController(ElineBusinessCountPanel view) {
		this.view = view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh() throws Exception {
		ElineInfoService_MB elineInfoServiceMB = null;
		ListingFilter filter = null;
		List<ElineInfo> needs = new ArrayList<ElineInfo>();
		try {
			filter = new ListingFilter();
			elineInfoServiceMB = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);

			if (null == this.elineInfo) {
				this.elineInfo = new ElineInfo();
			}

			infos = (List<ElineInfo>) filter.filterList(elineInfoServiceMB.selectByCondition(this.elineInfo));
			
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
			if (this.view.getOamTable() != null) {
				this.view.getOamTable().clear();
			}
			if (view.getTopoPanel() != null) {
				view.getTopoPanel().clear();
			}
			if (view.getPortNetworkTablePanel() != null) {
				view.getPortNetworkTablePanel().clear();
			}
			if (view.getClientInfoPanel() != null) {
				view.getClientInfoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			if (view.getPwNetworkTablePanel() != null) {
				this.view.getPwNetworkTablePanel().clear();
			}
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			filter = null;
			UiUtil.closeService_MB(elineInfoServiceMB);
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
			initOamPanel();
			initAcPanel();
			this.initSchematizePanel();
			initClientInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwServiceMB = null;
		try {
			ElineInfo elineInfo = this.view.getSelect();
			pwServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			PwInfo pw = pwServiceMB.selectByPwId(elineInfo.getPwId());
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			pwList.add(pw);
			this.view.getPwNetworkTablePanel().initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwServiceMB);
		}
	}

	/**
	 * 列表点击事件（客户信息）
	 */
	private void initClientInfo() {
		ElineInfo elineInfo = null;
		ClientService_MB clientServiceMB = null;
		List<Client> clientList = null;
		try {
			clientServiceMB = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			elineInfo = view.getSelect();

			if (0 != elineInfo.getClientId()) {
				clientList = clientServiceMB.select(elineInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
				this.view.updateUI();
			} else {
				this.view.getClientInfoPanel().clear();
			}

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
			UiUtil.closeService_MB(clientServiceMB);
			clientList = null;
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
		ElineInfo elineInfo = null;
		try {
			elineInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(elineInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
		}
	}

	@SuppressWarnings("unchecked")
	private void initOamPanel() throws Exception {
		OamInfoService_MB oamInfoServiceMB = null;
		ElineInfo elineInfo = null;
		List<OamInfo> oamList = null;
		try {
			elineInfo = this.view.getSelect();
			oamList = new ArrayList<OamInfo>();
			oamInfoServiceMB = (OamInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OamInfo);
			OamInfo oam = new OamInfo();
			OamMepInfo oamMep = new OamMepInfo();
			oamMep.setServiceId(elineInfo.getId());
			oamMep.setObjType(EServiceType.ELINE.toString());
			oam.setOamMep(oamMep);
			oamList = oamInfoServiceMB.queryByServiceId(oam);
			ComparableSort sort = new ComparableSort();
			oamList = (List<OamInfo>) sort.compare(oamList);
			this.view.getOamTable().clear();
			this.view.getOamTable().initData(oamList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(oamInfoServiceMB);
			elineInfo = null;
			oamList = null;
		}
	}

	private void initTopoPanel() {
		ElineInfo info = null;
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

	/**
	 * 绑定AC列表数据
	 * 
	 * @author kk
	 * 
	 * @param
	 * 
	 * @return
	 * 
	 * @Exception 异常对象
	 */
	private void initAcPanel() {
		ElineInfo elineInfo = null;
		List<Integer> acIdList = null;
		try {
			acIdList = new ArrayList<Integer>();
			elineInfo = view.getSelect();

			acIdList.add(elineInfo.getaAcId());
			acIdList.add(elineInfo.getzAcId());

			view.getPortNetworkTablePanel().clear();
			view.getPortNetworkTablePanel().initData(acIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			elineInfo = null;
			acIdList = null;
		}
	}

	@Override
	public void openFilterDialog() throws Exception {
		new EthServiceFilterDialog(this.elineInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.elineInfo = null;
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
		List<ElineInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();
		if (this.view.getOamTable() != null) {
			this.view.getOamTable().clear();
		}
		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getPortNetworkTablePanel() != null) {
			view.getPortNetworkTablePanel().clear();
		}
		if (view.getClientInfoPanel() != null) {
			view.getClientInfoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		if (view.getPwNetworkTablePanel() != null) {
			this.view.getPwNetworkTablePanel().clear();
		}
		this.view.initData(needs);
		this.view.updateUI();
	}
}
