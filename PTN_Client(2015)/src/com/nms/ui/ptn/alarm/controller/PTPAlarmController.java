
package com.nms.ui.ptn.alarm.controller;

import java.util.List;

import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.db.bean.alarm.DuanAlarmInfo;
import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.alarm.model.PTPAlarmFilterDialog;
import com.nms.ui.ptn.alarm.view.PTPAlarmPanel;

/**
 * 当前告警事件处理类
 * 
 * @author 
 * 
 */
public class PTPAlarmController extends AbstractController{
	private PTPAlarmPanel view;
	private Integer type = 1;
	private int total;
	private int now = 1;
	List<CurrentAlarmInfo> infos=null;
	private List<CurrentAlarmInfo> needs;
	
	public PTPAlarmController(PTPAlarmPanel view) {
		this.view = view;
	}

	/**
	 * 刷新按钮事件处理方法 先设置过滤条件后，才能显示刷新结果
	 */
	public void refresh() throws Exception{
		CurAlarmService_MB Service = null;
		try {
			Service = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			infos = Service.selectDuanAlarm(type);
			if(infos.size() ==0){
				now = 0;
				view.getNextPageBtn().setEnabled(false);
				view.getPrevPageBtn().setEnabled(false);
			}else{
				now =1;
				if (infos.size() % ConstantUtil.flipNumber == 0) {
					total = infos.size() / ConstantUtil.flipNumber;
				} else {
					total = infos.size() / ConstantUtil.flipNumber + 1;
				}
				if (total == 1) {
					view.getNextPageBtn().setEnabled(false);
					view.getPrevPageBtn().setEnabled(false);
				}else{
					view.getNextPageBtn().setEnabled(true);
					view.getPrevPageBtn().setEnabled(true);
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
			this.view.initData(needs);
			this.view.updateUI();
		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(Service);
		}
	}

	public void filter(Integer type){
		this.type = type;
		try {
			this.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Override
    public void prevPage()throws Exception{
    	now = now-1;
    	if(now == 1){
    		view.getPrevPageBtn().setEnabled(false);
    	}
    	view.getNextPageBtn().setEnabled(true);
    	
    	flipRefresh();
    }
	
	@Override
	public void nextPage() throws Exception {
		now = now+1;
		if(now == total){
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}
	
	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<CurrentAlarmInfo> needs = null;
		if (now * ConstantUtil.flipNumber > infos.size()) {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, infos.size());
		} else {
			needs = infos.subList((now - 1) * ConstantUtil.flipNumber, now * ConstantUtil.flipNumber);
		}
		OperationLogService_MB operationService=null;
		try {
//			operationService = (OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
//			operationService.setdata(needs);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
			UiUtil.closeService_MB(operationService);
		}
		this.view.clear();
		this.view.initData(needs);
		this.view.updateUI();
	}
	
	@Override
	public void openFilterDialog()throws Exception {
		final PTPAlarmFilterDialog filterDialog = new PTPAlarmFilterDialog(this);
		UiUtil.showWindow(filterDialog, 390, 270);
	}

	public List<CurrentAlarmInfo> getCurrInfos() {
		return null;
	}
}
