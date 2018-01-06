package com.nms.ui.lockscreen.controller;

import java.util.Date;
import java.util.TimerTask;

import com.nms.db.bean.system.UserDesginInfo;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.model.system.UserDesginService_Mb;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.util.Services;
import com.nms.ui.Ptnf;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.util.Mybatis_DBManager;
/**
 * 屏幕锁定线程
 * @author Administrator
 *
 */
public class HandLockTimerTask extends TimerTask {
	
    private long endTime;
    private long lockScreenTime =15*60*1000;
    private long closeEndTime;
    private long closeTime = 60*60*1000;
    
	public HandLockTimerTask() {
		
	}
	@Override
	public void run() {
		try {
			ExceptionManage.infor("已经用了的最大连接数"+Mybatis_DBManager.getInstance().getDataSource().getNumBusyConnections(), this.getClass());
			lockScreenTime();
			if(Ptnf.getPtnf().isHandLockScreen()== true){
				endTime=Ptnf.getPtnf().getStartTime()+lockScreenTime;
				closeEndTime = Ptnf.getPtnf().getStartTime()+closeTime;
			}
			if(new Date().getTime()>=endTime&&Ptnf.getPtnf().isHandLockScreen()){
				Ptnf.getPtnf().getHandLockDialog().getPassWord().setText("");
				UiUtil.showWindow(Ptnf.getPtnf().getHandLockDialog(), Ptnf.getPtnf().getHandLockDialog().getWeight(),300);
			}
			if(new Date().getTime()>=closeEndTime&&Ptnf.getPtnf().isHandLockScreen()){
				// 注销
				loginOut();
			}
			Ptnf.getPtnf().setHandLockScreen(true);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}finally{
		}
	}
	
	private void loginOut()throws Exception{
		LoginLogServiece_Mb loginlogServiece = null;
		LoginLog loginlog = null;
		try {
			loginlog = new LoginLog();
			loginlog.setUser_id(ConstantUtil.user.getUser_Id());
			loginlogServiece = (LoginLogServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			loginlogServiece.updateExitLoginLog(loginlog, 1);
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(loginlogServiece);
		}
	}
	
	private void lockScreenTime(){
		
		UserDesginService_Mb serevices=null;
		UserDesginInfo userDesginfo=null;
		
		try {
			serevices = (UserDesginService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.USERDESGINSERIVE);
			userDesginfo = serevices.select(ConstantUtil.user.getUser_Name());
			if(userDesginfo.getIsSelect() == 1)
			{
			  if(userDesginfo.getMinute()!= null && !userDesginfo.getMinute().equals("") && !userDesginfo.getMinute().equals("null"))
			   {
				  lockScreenTime = Long.parseLong(userDesginfo.getMinute())*60*1000;
			   }	
			}else
			{
				lockScreenTime =15*60*1000;
			}
			
			if(userDesginfo.getCloseSelect() == 1){
				if(userDesginfo.getCloseTime() != null && !userDesginfo.getCloseTime().equals("") && !userDesginfo.getCloseTime().equals("null")){
					closeTime = Long.parseLong(userDesginfo.getCloseTime())*60*1000;
				}else{
					closeTime = 60*60*1000;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			userDesginfo = null;
			UiUtil.closeService_MB(serevices);
		}
	}

}
