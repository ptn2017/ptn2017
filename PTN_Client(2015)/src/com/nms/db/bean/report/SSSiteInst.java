package com.nms.db.bean.report;

import com.nms.ui.frame.ViewDataObj;

public class SSSiteInst extends ViewDataObj{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5319534028838652187L;

	@Override
	public void putObjectProperty() {
		this.putClientProperty("id",this.getId());
		this.putClientProperty("SiteName",this.getSiteName());
		this.putClientProperty("SiteType",this.getNeType());
		this.putClientProperty("PortType",this.getPortType());
		this.putClientProperty("PortCount",this.getPortCount());
		this.putClientProperty("PortUsed",this.getPortUsed());
		this.putClientProperty("PortUnUsed",this.getPortUnUsed());
		this.putClientProperty("UsedRate",this.getUsedRate());
		this.putClientProperty("siteStatus",this.getSiteStatus());
		this.putClientProperty("slotNum",this.getSlotNum());
		this.putClientProperty("cardType",this.getCardType());
		this.putClientProperty("hardWareVersion",this.getHardWareVersion());
		this.putClientProperty("softWareVersion",this.getSoftWareVersion());
		this.putClientProperty("cellTime",this.getCellTime());
	}
	private String id; //序号
	private String SiteName; //网元名称
	private int siteId;
	private String PortType; //端口类型
	private String PortCount; //端口数量
	private String PortUsed; //已使用端口
	private String PortUnUsed; //空闲
	private String UsedRate; //使用率
	private String NeType;
	private String siteStatus;// 网元状态
	private String slotNum;// 网元槽位
	private String cardType;// 网元板卡类型
	private String hardWareVersion;// 网元硬件版本
	private String softWareVersion;// 网元软件版本
	private String cellTime;// 网元启用时间
	
	public String getSiteStatus() {
		return siteStatus;
	}
	
	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}
	
	public String getSlotNum() {
		return slotNum;
	}
	
	public void setSlotNum(String slotNum) {
		this.slotNum = slotNum;
	}
	
	public String getCardType() {
		return cardType;
	}
	
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	public String getHardWareVersion() {
		return hardWareVersion;
	}
	
	public void setHardWareVersion(String hardWareVersion) {
		this.hardWareVersion = hardWareVersion;
	}
	
	public String getSoftWareVersion() {
		return softWareVersion;
	}
	
	public void setSoftWareVersion(String softWareVersion) {
		this.softWareVersion = softWareVersion;
	}
	
	public String getCellTime() {
		return cellTime;
	}
	
	public void setCellTime(String cellTime) {
		this.cellTime = cellTime;
	}
	
	public String getNeType()
	{
		return NeType;
	}
	
	public void setNeType(String neType)
	{
		NeType = neType;
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSiteName() {
		return SiteName;
	}
	public void setSiteName(String siteName) {
		SiteName = siteName;
	}
	public String getPortType() {
		return PortType;
	}
	public void setPortType(String portType) {
		PortType = portType;
	}
	public String getPortCount() {
		return PortCount;
	}
	public void setPortCount(String portCount) {
		PortCount = portCount;
	}
	public String getPortUsed() {
		return PortUsed;
	}
	public void setPortUsed(String portUsed) {
		PortUsed = portUsed;
	}
	public String getPortUnUsed() {
		return PortUnUsed;
	}
	public void setPortUnUsed(String portUnUsed) {
		PortUnUsed = portUnUsed;
	}
	public String getUsedRate() {
		return UsedRate;
	}
	public void setUsedRate(String usedRate) {
		UsedRate = usedRate;
	}

}
