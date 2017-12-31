package com.nms.db.bean.system;

import java.util.List;

public class NetWork {
	private int netWorkId;
	private int netX;
	private int netY;
	private int isDeleteTopo=0;	//主拓扑刷新时，验证是否为删除动作  0=不是删除  1=删除
	private String netWorkName;
	private String backgroundPath;
	private List<Field> fieldList;// 域中包含的子网或者组
	
	public List<Field> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}
	public String getBackgroundPath() {
		return backgroundPath;
	}
	public void setBackgroundPath(String backgroundPath) {
		this.backgroundPath = backgroundPath;
	}
	public int getNetWorkId() {
		return netWorkId;
	}
	public void setNetWorkId(int netWorkId) {
		this.netWorkId = netWorkId;
	}
	public String getNetWorkName() {
		return netWorkName;
	}
	public void setNetWorkName(String netWorkName) {
		this.netWorkName = netWorkName;
	}
	public int getNetX() {
		return netX;
	}
	public void setNetX(int netX) {
		this.netX = netX;
	}
	public int getNetY() {
		return netY;
	}
	public void setNetY(int netY) {
		this.netY = netY;
	}
	public int getIsDeleteTopo() {
		return isDeleteTopo;
	}
	public void setIsDeleteTopo(int isDeleteTopo) {
		this.isDeleteTopo = isDeleteTopo;
	}
	
}
