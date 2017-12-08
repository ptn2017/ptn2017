package com.nms.ui.manager.xmlbean;

/**
 * 登陆配置XMLbean
 * 
 * 项目名称：WuHanPTN2012 类名称：LoginConfig 类描述： 创建人：kk 创建时间：2013-6-25 下午04:22:18 修改人：kk 修改时间：2013-6-25 下午04:22:18 修改备注：
 * 
 * @version
 * 
 */
public class LoginConfig {

	private String serviceIp;
	private String username;
	private String version;
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServiceIp() {
		return serviceIp;
	}

	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
