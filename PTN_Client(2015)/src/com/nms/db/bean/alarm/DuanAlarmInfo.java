package com.nms.db.bean.alarm;

import com.nms.ui.ptn.alarm.model.AlarmInfo;

public class DuanAlarmInfo extends AlarmInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -263705992824860027L;
	private int id;
	private String name;
	private String siteName;
	private String time;
	private String warningNote;
	private String warningName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWarningNote() {
		return warningNote;
	}
	public void setWarningNote(String warningNote) {
		this.warningNote = warningNote;
	}
	public String getWarningName() {
		return warningName;
	}
	public void setWarningName(String warningName) {
		this.warningName = warningName;
	}
}
