package com.nms.ui.ptn.alarm.controller;

import java.util.ArrayList;
import java.util.List;

import com.nms.model.alarm.CurAlarmService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.UiUtil;
import com.nms.db.bean.alarm.CurrentAlarmInfo;
import com.nms.ui.ptn.alarm.view.TopoAlamTable;

/**
 * 当前告警事件处理类
 * 
 */
public class AlarmController {
	private TopoAlamTable view;
	private int aramLenvel;

	public AlarmController(TopoAlamTable view, int aramLenvel) {
		this.aramLenvel = aramLenvel;
		this.view = view;
		refresh();
	}

	/**
	 * 刷新按钮事件处理方法 先设置过滤条件后，才能显示刷新结果
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		List<CurrentAlarmInfo> currInfos = null;
		CurAlarmService_MB service = null;
		CurrentAlarmInfo condition = null;
		ListingFilter listingFilter=null;
		List<CurrentAlarmInfo> currentAlarmInfoList = null;
		try {
			currentAlarmInfoList = new ArrayList<CurrentAlarmInfo>();
			listingFilter=new ListingFilter();			
			currInfos = new ArrayList<CurrentAlarmInfo>();
			service = (CurAlarmService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CurrentAlarm);
			condition = new CurrentAlarmInfo();
			condition.setWarningLevel_temp(aramLenvel);
			currentAlarmInfoList.addAll(service.select(condition));
			currentAlarmInfoList.addAll(service.alarmByAlarmLevel(aramLenvel));
			List<CurrentAlarmInfo> alarmList = new ArrayList<CurrentAlarmInfo>();
			// 过滤掉已经确认的告警
			if(currentAlarmInfoList != null){
				for(CurrentAlarmInfo c : currentAlarmInfoList){
					if(c.getConfirmtime() == null){
						alarmList.add(c);
					}
				}
			}
//			currInfos = (List<CurrentAlarmInfo>) listingFilter.filterList(service.queryCurByAlarmlevel(aramLenvel));
			currInfos = (List<CurrentAlarmInfo>) listingFilter.filterList(alarmList);
			
			this.view.getBox().clear();
			this.view.initData(currInfos);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			currInfos = null;
			condition=null;
			listingFilter=null;
		}
	}

	public TopoAlamTable getView() {
		return view;
	}
}
