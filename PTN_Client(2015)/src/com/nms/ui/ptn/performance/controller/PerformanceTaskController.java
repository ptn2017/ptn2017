﻿package com.nms.ui.ptn.performance.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.perform.PerformanceTaskInfo;
import com.nms.db.enums.EMonitorCycle;
import com.nms.db.enums.EObjectType;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.perform.PerformanceTaskService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.performance.model.PerformanceTaskFilter;
import com.nms.ui.ptn.performance.view.CreatePerforTaskDialog;
import com.nms.ui.ptn.performance.view.PerforTaskFilterDialog;
import com.nms.ui.ptn.performance.view.PerformanceTaskPanel;

/**
 * 长期性能任务控制类
 * 
 * @author lp
 */
public class PerformanceTaskController extends AbstractController {

	private PerformanceTaskPanel view;
	private PerformanceTaskFilter filter;

	public PerformanceTaskController(PerformanceTaskPanel view) {
		this.view = view;
	}
	/*
	 * 打开创建长期性能任务对话框
	 */
	@Override
	public void openCreateDialog() throws Exception {
		try {
			this.view.getTable().clearSelection();
			final CreatePerforTaskDialog dialog = new CreatePerforTaskDialog(this.view);
			dialog.getConfirm().addActionListener(new MyActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					if (dialog.validateParams()) {
						((PerformanceTaskController) (view.getController())).create(dialog);
					}
				}
				@Override
				public boolean checking() {
					return true;
				}
			});
			if(ResourceUtil.language.equals("zh_CN")){
				UiUtil.showWindow(dialog, 500, 550);
			}else{
				UiUtil.showWindow(dialog, 500, 560);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		}
	}
	/*
	 * 创建
	 */
	public void create(final CreatePerforTaskDialog dialog) {
		String resultStr = "";
		try {
			List<PerformanceTaskInfo> taskInfoList = dialog.get();
		    //验证名称
			if(!isExitName(taskInfoList,true)){
				DialogBoxUtil.errorDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return ;
			}
			resultStr = createPerformance(taskInfoList,true);
			//添加日志记录
			for (int i = 0; i < taskInfoList.size(); i++) {
				AddOperateLog.insertOperLog(dialog.getConfirm(), EOperationLogType.PERFORMANCETASKINSERT.getValue(), resultStr, 
				null, taskInfoList.get(i), taskInfoList.get(i).getSiteInst().getSite_Inst_Id(), taskInfoList.get(i).getTaskName(), "PerformanceTaskInfo");
			}
			DialogBoxUtil.succeedDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.refresh();
			dialog.dispose();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
		}
	}
	/*
	 * 清空过滤条件
	 */
	@Override
	public void clearFilter() {
		filter = null;
		refresh();
	}

	/*
	 * 打开设置过滤对话框
	 */
	@Override
	public void openFilterDialog() {
		
		final PerforTaskFilterDialog filterDialog = new PerforTaskFilterDialog();
		filterDialog.setSize(new Dimension(500, 560));
		filterDialog.setLocation(UiUtil.getWindowWidth(filterDialog.getWidth()), UiUtil.getWindowHeight(filterDialog.getHeight()));
		filterDialog.getConfirm().addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PerformanceTaskController.this.setFilter(filterDialog);
			}
			@Override
			public boolean checking() {
				return true;
			}
		});
		filterDialog.setVisible(true);
	}

	/*
	 * 设置过滤条件
	 */
	private void setFilter(PerforTaskFilterDialog dialog) {
		List<PerformanceTaskInfo> taskList = null;
		PerformanceTaskService_MB service = null;
		try {
			filter = new PerformanceTaskFilter();
			if(dialog.get(filter))
			{
				service = (PerformanceTaskService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PerformanceTask);
				taskList = service.queryByFilter(filter);
			}else
			{
				taskList = new ArrayList<PerformanceTaskInfo>();
			}
			//添加日志记录
			dialog.getConfirm().setOperateKey(EOperationLogType.PERFORMANCETASKFILTERSELECT.getValue());
			dialog.getConfirm().setResult(1);
			view.clear();
			view.initData(taskList);
			view.updateUI();
			DialogBoxUtil.succeedDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			taskList = null;
			dialog.dispose();
		}
	}

	/*
	 * 删除长期性能任务方法(non-Javadoc)
	 */
	@Override
	public void delete() throws Exception {
		
		List<PerformanceTaskInfo> infoList = null;
		DispatchUtil performanceDispatch = null;
		try {
			performanceDispatch = new DispatchUtil(RmiKeys.RMI_PERFORMANCE);
			infoList = view.getAllSelect();
			String result = performanceDispatch.excuteDelete(infoList);
			//添加日志记录
			PtnButton deleteButton=this.view.getDeleteButton();
			deleteButton.setOperateKey(EOperationLogType.PERFORMANCETASKDELETE.getValue());
			int operationResult=0;
			if(result.equals(ResultString.CONFIG_SUCCESS)){
				operationResult=1;
			}else{
				operationResult=2;
			}
			deleteButton.setResult(operationResult);
			for (int j = 0; j < infoList.size(); j++) {
				AddOperateLog.insertOperLog(deleteButton, EOperationLogType.PERFORMANCETASKDELETE.getValue(), result, 
				null, infoList.get(j), infoList.get(j).getSiteInst().getSite_Inst_Id(), infoList.get(j).getTaskName(), "PerformanceTaskInfo");
			}
			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			infoList = null;
			performanceDispatch = null;
		}
	}

	/*
	 * 打开修改对话框(non-Javadoc)
	 */
	@Override
	public void openUpdateDialog() throws Exception {
		final CreatePerforTaskDialog dialog = new CreatePerforTaskDialog(this.view);
		
		dialog.getConfirm().addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (dialog.validateParams()) {
					PerformanceTaskController.this.create(dialog);
				}
			}
			@Override
			public boolean checking() {
				return true;
			}
		});
		if(ResourceUtil.language.equals("zh_CN")){
			dialog.setSize(new Dimension(480, 560));
		}else{
			dialog.setSize(new Dimension(510, 600));
		}
		dialog.setLocation(UiUtil.getWindowWidth(dialog.getWidth()), UiUtil.getWindowHeight(dialog.getHeight()));
		dialog.setVisible(true);

	}

	/*
	 * 修改后台数据
	 */
	private void update(CreatePerforTaskDialog dialog) {
		int result=0;//日志记录结果
		String resultStr = "";
		List<PerformanceTaskInfo> infoList = null;
		try {
			infoList = new ArrayList<PerformanceTaskInfo>();
			infoList = dialog.get();
			resultStr = createPerformance(infoList,false);
			//添加日志记录
			dialog.getConfirm().setOperateKey(EOperationLogType.PERFORMANCETASKUPDATE.getValue());
			if(resultStr.equals(ResultString.CONFIG_SUCCESS)){
				result=1;
			}else{
				result=2;
			}
			AddOperateLog.insertOperLog(dialog.getConfirm(), EOperationLogType.PERFORMANCETASKUPDATE.getValue(), resultStr, 
					null, infoList.get(0), infoList.get(0).getSiteInst().getSite_Inst_Id(), infoList.get(0).getTaskName(), "PerformanceTaskInfo");
			
			DialogBoxUtil.succeedDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			dialog.dispose();
			dialog.getConfirm().setResult(result);
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			dialog.dispose();
		} finally {
			result=0;
			resultStr = "";
			infoList = null;
		}
	}

	public void disposet(CreatePerforTaskDialog dialog){
		try {
			DialogBoxUtil.succeedDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			dialog.dispose();
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	// 刷新长期性能任务table表
	@SuppressWarnings("unchecked")
	@Override
	public void refresh() {
		List<PerformanceTaskInfo> taskList = null;
		ListingFilter listFilter=null;
		PerformanceTaskService_MB service = null;
		try {
			listFilter = new ListingFilter();
			service = (PerformanceTaskService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PerformanceTask);
			taskList = (List<PerformanceTaskInfo>) listFilter.filterList(service.select());
			view.clear();
			view.initData(taskList);
			view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	/**
	 * 返回所有已经创建长期性能任务的所有监控网元
	 * 
	 * @throws Exception
	 */
	public List<Integer> getAllTaskSiteInstList() throws Exception {
		PerformanceTaskService_MB service = null;
		List<PerformanceTaskInfo> taskList = null;
		List<Integer> siteInsts = null;
		try {
			service = (PerformanceTaskService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PerformanceTask);
			taskList = service.select();
			siteInsts = new ArrayList<Integer>();
			for (PerformanceTaskInfo info : taskList) {
				// 若板卡为空，则是网元监控
				if (info.getObjectType() != null && info.getObjectType() == EObjectType.SITEINST) {
					siteInsts.add(info.getSiteInst().getSite_Inst_Id());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		} finally {
			taskList = null;
			UiUtil.closeService_MB(service);
		}
		return siteInsts;
	}

	/**
	 * 返回所有已经创建长期性能任务的监控槽位
	 * 
	 * @throws Exception
	 */
	public List<Integer> getAllTaskSlotInstList() throws Exception {
		PerformanceTaskService_MB service = null;
		List<PerformanceTaskInfo> taskList = null;
		List<Integer> slotInsts = null;
		try {
			service = (PerformanceTaskService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PerformanceTask);
			taskList = service.select();
			slotInsts = new ArrayList<Integer>();
			for (PerformanceTaskInfo info : taskList) {
				if (info.getObjectType() != null && info.getObjectType() == EObjectType.SLOTINST) {
					slotInsts.add(info.getObjectId());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
			throw e;
		} finally {
			taskList = null;
			UiUtil.closeService_MB(service);
		}
		return slotInsts;
	}

	public PerformanceTaskPanel getView() {
		return view;
	}
	/**
	 * function:根据任务对象来创建多条性能任务
	 * @param taskInfoList
	 * @param flag true 表示是新建 false 修改
	 * @return
	 */
	private String createPerformance(List<PerformanceTaskInfo> taskInfoList,boolean flag){
		
		DispatchUtil performanceDispatch = null;	
		String resultStr = ResultString.CONFIG_SUCCESS;
		String taskName = null;
		PerformanceTaskInfo taskInfo  = null;
		try {
			performanceDispatch = new DispatchUtil(RmiKeys.RMI_PERFORMANCE);
			if(taskInfoList != null && taskInfoList.size()>0){
				for(int i =0 ; i< taskInfoList.size();i++){
					taskInfo = taskInfoList.get(i);
//					if(taskInfo.getTaskLabel()==1){
						taskInfo.setThreadName(taskInfo.getTaskName());
						if(flag){
							if(taskInfoList.size() == 1){
								taskInfo.setTaskName(taskInfo.getTaskName());
							}else{
								taskInfo.setTaskName(taskInfo.getTaskName()+"_"+(i+1));
							}
						}else{
							taskInfo.setTaskName(taskInfo.getTaskName());
						}
							//监控当前15分钟的数据性能
							if(taskInfo.getMonitorCycle().getValue()==1 || taskInfo.getMonitorCycle().getValue() ==3 || taskInfo.getMonitorCycle().getValue()==4){
								taskInfo.setPerformanceType(0);
								taskInfo.setPerformanceCount(0);
								taskInfo.setPerformanceBeginCount(0);
							}
							//监控当前24分钟的数据性能
							else{
								taskInfo.setPerformanceCount(255);
								taskInfo.setPerformanceBeginCount(0);
								taskInfo.setPerformanceType(32);
							}
								resultStr = performanceDispatch.excuteInsert(taskInfo);
						
//					}else if(taskInfo.getTaskLabel()==2){
//						
//						PerformanceTaskInfo taskInfoOther=new PerformanceTaskInfo();
//						taskInfoOther=taskInfo;
//						taskName=taskInfo.getTaskName();
//						if(taskInfoOther!=null){
//							taskInfoOther.setThreadName(taskName);
//							if(flag){
//								if(taskInfoList.size() == 1){
//									taskInfoOther.setTaskName(taskInfoOther.getTaskName()+"_01");
//								}else{
//									taskInfoOther.setTaskName(taskInfoOther.getTaskName()+"_"+(i+1)+"_01");
//								}
//							}else{
//								taskInfo.setTaskName(taskInfo.getTaskName());
//							}
//							
//								//当前15分钟
//								taskInfo.setPerformanceType(0);
//								taskInfo.setPerformanceCount(0);
//								taskInfo.setPerformanceBeginCount(0);
//								
//								// 保存长期性能任务
//								resultStr = performanceDispatch.excuteInsert(taskInfoOther);
//						}
//						
//						// 保存长期性能任务
//						taskInfo.setId(0);
//						taskInfo.setThreadName(taskName+"_"+taskInfoOther.getSiteInst().getCellId()+"_02");
//						if(flag){
//							if(taskInfoList.size() == 1){
//								taskInfo.setTaskName(taskInfo.getTaskName()+"_02");
//							}else{
//								taskInfo.setTaskName(taskName+"_"+(i+1)+"_02");
//							}
//						}else{
//							taskInfo.setTaskName(taskInfo.getTaskName());
//						}
//							taskInfo.setMonitorCycle(EMonitorCycle.forms(2));
//							//当前24小时数据性能
//							taskInfo.setPerformanceCount(255);
//							taskInfo.setPerformanceBeginCount(0);
//							taskInfo.setPerformanceType(32);
//							resultStr = performanceDispatch.excuteInsert(taskInfo);
//					}
				}
			}
	   } catch (Exception e) {
		 ExceptionManage.dispose(e,this.getClass());
	 }
	return resultStr;
  }
	
	private boolean isExitName(String taskName){
		VerifyNameUtil verifyNameUtil = new VerifyNameUtil();
		try {
			
		return 	verifyNameUtil.verifyName(EServiceType.PERFORMANCETASK.getValue(), taskName.trim(), "");
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return true;
	}
	
	/**
	 * 验证性能名称是否相同
	 * @param taskInfoList
	 * @param flag
	 * @return
	 */
	private boolean isExitName(List<PerformanceTaskInfo> taskInfoList,boolean flag){
		PerformanceTaskInfo taskInfo  = null;
		String taskName = "";
		try {
			if(taskInfoList != null && taskInfoList.size()>0){
				for(int i =0 ; i< taskInfoList.size();i++){
					taskInfo = taskInfoList.get(i);
					if(taskInfo.getTaskLabel()==1){
						taskName = taskInfo.getTaskName();
						if(flag && taskInfoList.size()>1){
							taskName = taskInfo.getTaskName()+"_"+(i+1);
						}
						if(isExitName(taskName)){
							return false;
						}
					}else if(taskInfo.getTaskLabel()==2){
						taskName = taskInfo.getTaskName();
						if(flag && taskInfoList.size()>1){
							taskName = taskInfo.getTaskName()+"_"+(i+1)+"_01";
						}
						if(isExitName(taskName)){
							return false;
						}
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return true;
	}
	
}
