package com.nms.db.bean.alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.equipment.slot.SlotInst;
import com.nms.db.enums.EObjectType;
/**
 * 当前告警屏蔽
 *
 */
public class CurrentAlarmBlock implements Serializable{
	private static final long serialVersionUID = 1L;
	// 监控网元id列表
	private List<SiteInst> siteList;
	// 监控板卡
	private List<SlotInst> slotList;
	//查询类型：网元、网元+槽位
	private EObjectType objectType;
	//告警级别
	private List<Integer> alarmLevel;
    //告警发生时间
	private String happenTime;
	private String happenEndTime;
	//告警类型
	private int warningType;
    // 告警源类型
    private int alarmSrc;
    // 告警源
    private Object alarmBusiness;
    // 关闭告警屏蔽
    private boolean close;
    
    public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public CurrentAlarmBlock() {
    	siteList = new ArrayList<SiteInst>();
    	slotList = new ArrayList<SlotInst>();
	}

	public List<SiteInst> getSiteList() {
		return siteList;
	}

	public void setSiteList(List<SiteInst> siteList) {
		this.siteList = siteList;
	}

	public List<SlotInst> getSlotList() {
		return slotList;
	}

	public void setSlotList(List<SlotInst> slotList) {
		this.slotList = slotList;
	}

	public EObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(EObjectType objectType) {
		this.objectType = objectType;
	}

	public List<Integer> getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(List<Integer> alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(String happenTime) {
		this.happenTime = happenTime;
	}

	public String getHappenEndTime() {
		return happenEndTime;
	}

	public void setHappenEndTime(String happenEndTime) {
		this.happenEndTime = happenEndTime;
	}

	public int getWarningType() {
		return warningType;
	}

	public void setWarningType(int warningType) {
		this.warningType = warningType;
	}

	public int getAlarmSrc() {
		return alarmSrc;
	}

	public void setAlarmSrc(int alarmSrc) {
		this.alarmSrc = alarmSrc;
	}

	public Object getAlarmBusiness() {
		return alarmBusiness;
	}

	public void setAlarmBusiness(Object alarmBusiness) {
		this.alarmBusiness = alarmBusiness;
	}
}
