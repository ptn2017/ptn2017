package com.nms.db.bean.system;

import com.nms.ui.frame.ViewDataObj;
import com.nms.ui.manager.ExceptionManage;

public class LogManager extends ViewDataObj {

	private static final long serialVersionUID = -3665962652764776557L;
	private int logType;//日志类型
	private String startTime;
	private int timeLimit;//时间限制
	private int cellType;//激活状态
	private int vcellType;//激活状态
	private int volumeLimit;//时间限制
	private String fileWay;//文件路径
	private String fileVWay;//文件路径
	private int id;
	private int fileModel;
	private int totalCount;
	private int maxId;
	
	public String getFileVWay() {
		return fileVWay;
	}

	public void setFileVWay(String fileVWay) {
		this.fileVWay = fileVWay;
	}
	
	public int getVcellType() {
		return vcellType;
	}

	public void setVcellType(int vcellType) {
		this.vcellType = vcellType;
	}

	public int getCellType() {
		return cellType;
	}

	public void setCellType(int cellType) {
		this.cellType = cellType;
	}

	public int getMaxId() {
		return maxId;
	}

	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getFileModel() {
		return fileModel;
	}

	public void setFileModel(int fileModel) {
		this.fileModel = fileModel;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getVolumeLimit() {
		return volumeLimit;
	}

	public void setVolumeLimit(int volumeLimit) {
		this.volumeLimit = volumeLimit;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getFileWay() {
		return fileWay;
	}

	public void setFileWay(String fileWay) {
		this.fileWay = fileWay;
	}

	public LogManager() {
		super();
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public void putObjectProperty() {
		try {
			getClientProperties().put("id",this.getID());
			getClientProperties().put("timeLimit",this.getTimeLimit());
			getClientProperties().put("volumeLimit",this.getVolumeLimit());
			getClientProperties().put("fileWay",this.getFileWay());
			getClientProperties().put("fileVWay",this.getFileVWay());
			getClientProperties().put("logType",UnLoadFactory.trans(this.getLogType()));
			if(this.getCellType()==0){
				getClientProperties().put("cellType",true);
			}else {
				getClientProperties().put("cellType",false);
			}
			if(this.getVcellType()==0){
				getClientProperties().put("vcellType",true);
			}else {
				getClientProperties().put("vcellType",false);
			}			

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
}
