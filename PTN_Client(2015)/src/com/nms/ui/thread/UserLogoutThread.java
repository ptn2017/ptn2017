package com.nms.ui.thread;

import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.main.Main;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.util.CodeConfigItem;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.xmlbean.LoginConfig;

/**
 * 用户注销线程检测
 * 
 * @author kk
 * 
 */
public class UserLogoutThread implements Runnable {
	private boolean flag = true;
	private Thread thread = null;
	
	public UserLogoutThread() {
		Thread.currentThread().setName("UserLogoutThread");
	}

	@Override
	public void run() {
		LoginLogServiece_Mb loginLogServiece = null;	//用户日志service
		LoginLog loginLog = null;	//查询条件  根据userid查询
		try {
//			loginLogServiece = (LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			loginLog = new LoginLog();
			loginLog.setUser_id(ConstantUtil.user.getUser_Id());
			loginLog.setLoginTime(ConstantUtil.loginTime);
//			synchronized (this) {
				while (true) {
					if (flag) {
						loginLogServiece = (LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
					   	if (!loginLogServiece.findLoginTime(loginLog)) {
							//记录日志,该用户被注销
	//						loginLog.setLogoutState(1);
							loginLogServiece.updateExitLoginLog(loginLog, 1);
							DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USERONLINE_LOGOUT));
							setLoginInfo();
							Thread.sleep(2000);
							String command = "java -jar ptn.jar";
							Runtime.getRuntime().exec(command);
							Thread.sleep(1000);
							System.exit(0);
						}
						Thread.sleep(5000);
						UiUtil.closeService_MB(loginLogServiece);
					}
				}
//			}

		} catch (Exception e) {
			flag = false;
			ExceptionManage.dispose(e,this.getClass());
		} finally{
			UiUtil.closeService_MB(loginLogServiece);
		}
	}
	
	private void setLoginInfo(){
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setServiceIp(CodeConfigItem.getInstance().getBackUpIp());
		LoginUtil loginUtil = new LoginUtil();
		loginUtil.writeLoginConfig(loginConfig);
	}
	
	/**
	 * 线程暂停
	 */
	public void setSuspendFlag() {
		try {
			if(thread != null){
				if(!thread.isInterrupted()){
					thread.stop();
				}
				thread = null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			thread = null;
		}
		
	}
	
	/**
	 * 唤醒线程
	 */
	public  void setResume() {
		if (null == thread) {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	public void setFlga(boolean flga) {
		this.flag = flga;
	}

}
