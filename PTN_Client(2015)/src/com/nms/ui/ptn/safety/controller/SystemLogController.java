package com.nms.ui.ptn.safety.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.bean.system.SystemLog;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.system.SystemLogService_MB;
import com.nms.model.util.ExportExcel;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.safety.SystemLogManagerPanel;
import com.nms.ui.ptn.safety.dialog.OperationLogFilterDialog;
import com.nms.ui.ptn.safety.dialog.SystemLogFilterDialog;

/**
 * 主界面按钮事件处理
 * @author Administrator
 *
 */
public class SystemLogController extends AbstractController {
	private SystemLogManagerPanel view=null;
	LogManager logManager=null;
	private int total;
	private int now = 1;
	List<SystemLog> infos=null;
	private List<SystemLog> needs;
	private SystemLog filter;

	public SystemLogController(SystemLogManagerPanel unloadingPanel) {
		this.view = unloadingPanel;
	}

	public SystemLogController() {
		super();
	}

	@Override
	public void refresh() throws Exception {
		SystemLogService_MB systemLogService_MB = null;
		try {
			systemLogService_MB = (SystemLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SYSTEMLOG);
			if(filter == null ){
				filter = new SystemLog();
			}
			infos = systemLogService_MB.select(filter);
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
			this.view.initData(needs);
			this.view.updateUI();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}

	}
	/**
	 * 修改按钮
	 */
	public void openUpdateDialog()throws Exception{
		
//		final SystemLogManagerPanel dialog = new SystemLogManagerPanel(this);
//		UiUtil.showWindow(dialog, 380, 340);

	}
	/**
	 * 导入
	* @author sy
	
	* @Exception 异常对象
	 */
	public void inport() throws Exception{
	//	InportDialog dialog = new InportDialog(this);
	//	UiUtil.showWindow(dialog, 300, 180);
	
	}
	
	@Override
	public void openFilterDialog()throws Exception {
		final SystemLogFilterDialog filterDialog = new SystemLogFilterDialog();
		filterDialog.getConfirm().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {

				try {
					SystemLogController.this.setFilter(filterDialog);
					SystemLogController.this.insertOpeLog(EOperationLogType.OPERAIONLOGSELECT.getValue(),ResultString.CONFIG_SUCCESS, null, null);			
				} catch (Exception e) {
					ExceptionManage.dispose(e, OperationLogPanelController.class);
				}
				
			}
		});

		UiUtil.showWindow(filterDialog, 390, 270);
	}
	
	private void setFilter(SystemLogFilterDialog filterDialog) {
		filter.setQueryStartTime(filterDialog.getStartChooseTime().getText());
		filter.setQueryEndTime(filterDialog.getOverChooseTime().getText());
		if(!filterDialog.getOperationLogTypeField().getSelectedItem().toString().equals("全部")){
			filter.setOperationType(filterDialog.getOperationLogTypeField().getSelectedItem().toString());
		}
		int result=0;
		if(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_ISUCCESS).equals(filterDialog.getOperationResultBox().getSelectedItem())){
			result=1;
		}else if(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT_FALSE).equals(filterDialog.getOperationResultBox().getSelectedItem())){
			result=2;
		}
		filter.setOperationResult(result);
		try {
			this.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void clearFilter()throws Exception{
		filter=null;
		this.refresh();
		this.insertOpeLog(EOperationLogType.OPERAIONLOGCLEARSELECT.getValue(),ResultString.CONFIG_SUCCESS, null, null);			
	}
	
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TT_OPERATION_SELECT_LOG),"");		
	}
	
	
	public SystemLogManagerPanel getView() {
		return view;
	}

	public void setView(SystemLogManagerPanel view) {
		this.view = view;
	}

	public SystemLog getFilter() {
		return filter;
	}

	public void setFilter(SystemLog filter) {
		this.filter = filter;
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
	
	private void flipRefresh() {
		view.getCurrPageLabel().setText(now + "");
		List<SystemLog> needs = null;
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
		now = now+1;
		if(now == total){
			view.getNextPageBtn().setEnabled(false);
		}
		view.getPrevPageBtn().setEnabled(true);
		flipRefresh();
	}
	
	@Override
	public void export()throws Exception{
		List<SystemLog> infos = null;
		String result;
		ExportExcel export=null;
		// 得到页面信息
		try {
			infos =  this.view.getTable().getAllElement();
			export=new ExportExcel();
			//得到bean的集合转为  String[]的List
			List<String[]> beanData=export.tranListString(infos,"systemLogTable");
			//导出页面的信息-Excel
			result=export.exportExcel(beanData, "systemLogTable");
			//添加操作日志记录
			this.insertOpeLog(EOperationLogType.EXPORTLOGINLOG.getValue(),result, null, null);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infos = null;
			result=null;
			export=null;
		}
	}
}
