package com.nms.db.bean.system;

public class SystemConfig {
	private String systemId;
	private String name;
	private String descripe;
	private String ip;
	private int port;
	private String userName;
	private String passWord;
	private String status;
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescripe() {
		return descripe;
	}
	public void setDescripe(String descripe) {
		this.descripe = descripe;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
