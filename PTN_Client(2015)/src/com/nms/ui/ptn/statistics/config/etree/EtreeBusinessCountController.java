package com.nms.ui.ptn.statistics.config.etree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nms.db.bean.client.Client;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.model.client.ClientService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
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

/**
 */
public class EtreeBusinessCountController extends AbstractController {
	private EtreeBusinessCountPanel view;
	private Map<Integer, List<EtreeInfo>> etreeMap = null;
	private EtreeInfo etreeInfo=null;
	private int total;
	private int now = 0;
	private List<EtreeInfo> infos = null;
	public EtreeBusinessCountController(EtreeBusinessCountPanel view) {
		this.view = view;
	}


	/**
	 * 根据选中的etree返回此etree中所有etree记录
	 * 
	 * @return 全量的etree数据
	 * @throws Exception
	 */
	private List<EtreeInfo> getSelectEtree() throws Exception {
		List<EtreeInfo> etreeInfoList = null;
		try {
			// 根据选中的一条etree数据的组ID 在map中找到此etree包含的所有数据。
			etreeInfoList = new ArrayList<EtreeInfo>();
			for (EtreeInfo etreeinfo : this.view.getAllSelect()) {
				etreeInfoList.addAll(this.etreeMap.get(etreeinfo.getServiceId()));
			}
		} catch (Exception e) {
			throw e;
		}
		return etreeInfoList;
	}

	@Override
	public void refresh() throws Exception {
		EtreeInfoService_MB service = null;
		ListingFilter listingFilter = null;
		List<EtreeInfo> needs = new ArrayList<EtreeInfo>();
		try {
			listingFilter = new ListingFilter();
			service = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			
			if(null == this.etreeInfo){
				this.etreeInfo=new EtreeInfo();
			}
			
			etreeMap = service.filterSelect(this.etreeInfo);
			
			infos = new ArrayList<EtreeInfo>();
			if (null != etreeMap && etreeMap.size() > 0) {
				for (Map.Entry<Integer, List<EtreeInfo>> entrySet : etreeMap.entrySet()) {
					if (listingFilter.filterByList(entrySet.getValue())) {
						infos.add(entrySet.getValue().get(0));
					}
				}
			}
			
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
			if (view.getPortNetworkTablePanel() != null) {
				view.getPortNetworkTablePanel().clear();
			}
			if (view.getPwNetworkTablePanel() != null) {
				view.getPwNetworkTablePanel().clear();
			}
			if (view.getClientInfoPanel() != null) {
				view.getClientInfoPanel().clear();
			}
			if (view.getSchematize_panel() != null) {
				view.getSchematize_panel().clear();
			}
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			listingFilter = null;
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
			this.initAcPanel();
			this.initSchematizePanel();
			initClientInfo();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwService = null;
		try {
			List<EtreeInfo> etreeInfoList = this.getSelectEtree();
			pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			for (EtreeInfo etree : etreeInfoList) {
				PwInfo pw = pwService.selectByPwId(etree.getPwId());
				pwList.add(pw);
			}
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
		EtreeInfo etreeInfo = null;
		ClientService_MB clientService = null;
		List<Client> clientList = null;
		try {
			clientService = (ClientService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CLIENTSERVICE);
			etreeInfo = view.getSelect();
			if (0 != etreeInfo.getClientId()) {
				clientList = clientService.select(etreeInfo.getClientId());
				this.view.getClientInfoPanel().clear();
				this.view.getClientInfoPanel().initData(clientList);
			} else {
				this.view.getClientInfoPanel().clear();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
			UiUtil.closeService_MB(clientService);
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
		EtreeInfo etreeInfo = null;
		try {
			etreeInfo = view.getSelect();
			this.view.getSchematize_panel().clear();
			this.view.getSchematize_panel().initData(etreeInfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
		}
	}

	private void initTopoPanel() throws Exception {
		EtreeInfo info = null;
		try {
			info = view.getSelect();
			view.getTopoPanel().clear();
			view.getTopoPanel().initData(info);
		} catch (Exception e) {
			throw e;
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
		EtreeInfo etreeInfo = null;
		List<Integer> acIdList = null;
		EtreeInfoService_MB etreeService = null;
		List<EtreeInfo> etreeInfoList = null;
		UiUtil uiUtil = null;
		Set<Integer> acIdSet = null;
		try {
			etreeInfo = view.getSelect();
			uiUtil = new UiUtil();
			acIdSet = new HashSet<Integer>();
			// 查询出此tree的所有信息
			etreeService = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);
			etreeInfoList = etreeService.select(etreeInfo);

			for (int i = 0; i < etreeInfoList.size(); i++) {
				acIdSet.addAll(uiUtil.getAcIdSets(etreeInfoList.get(i).getAmostAcId()));
				acIdSet.addAll(uiUtil.getAcIdSets(etreeInfoList.get(i).getZmostAcId()));
			}
			acIdList = new ArrayList<Integer>(acIdSet);
			view.getPortNetworkTablePanel().clear();
			view.getPortNetworkTablePanel().initData(acIdList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			etreeInfo = null;
			acIdList = null;
			UiUtil.closeService_MB(etreeService);
			etreeInfoList = null;
			uiUtil = null;
			acIdSet = null;
		}
	}

	@Override
	public void openFilterDialog() throws Exception {
		new EthServiceFilterDialog(this.etreeInfo);
		this.refresh();
	}

	// 清除过滤
	public void clearFilter() throws Exception {
		this.etreeInfo=null;
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
		List<EtreeInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		this.view.clear();
		if (view.getTopoPanel() != null) {
			view.getTopoPanel().clear();
		}
		if (view.getPortNetworkTablePanel() != null) {
			view.getPortNetworkTablePanel().clear();
		}
		if (view.getPwNetworkTablePanel() != null) {
			view.getPwNetworkTablePanel().clear();
		}
		if (view.getClientInfoPanel() != null) {
			view.getClientInfoPanel().clear();
		}
		if (view.getSchematize_panel() != null) {
			view.getSchematize_panel().clear();
		}
		this.view.initData(needs);
		this.view.updateUI();
	}
}
