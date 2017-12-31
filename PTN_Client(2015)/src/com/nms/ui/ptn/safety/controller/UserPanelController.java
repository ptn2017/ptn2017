package com.nms.ui.ptn.safety.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.nms.db.bean.system.loginlog.LoginLog;
import com.nms.db.bean.system.loginlog.UserLock;
import com.nms.db.bean.system.user.UserInst;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.system.loginlog.LoginLogServiece_Mb;
import com.nms.model.system.loginlog.UserLockServiece_MB;
import com.nms.model.system.user.UserFieldService_MB;
import com.nms.model.system.user.UserInstServiece_Mb;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.UserInfoPanel;
import com.nms.ui.ptn.safety.dialog.AddUserDialog;

/**
 * 用户信息的事件处理
 * @author sy
 *
 */
public class UserPanelController extends AbstractController {
	private  UserInfoPanel userinfopanel;
	
	//实体化
	public UserPanelController(UserInfoPanel userInfoPanel) {
		this.userinfopanel = userInfoPanel;
	}
	//刷新
	@Override
	public void refresh() throws Exception {
		UserInstServiece_Mb userInstServiece = null;
		UserLockServiece_MB userlockServiece = null;
		List<UserInst> userInsts = null;
		try {
			userInsts = new ArrayList<UserInst>();
			userInstServiece = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			userlockServiece = (UserLockServiece_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERLOCKSERVIECE);

			userInsts = userInstServiece.select();
			List<UserInst> userList = new ArrayList<UserInst>();
			for(UserInst user : userInsts){
				if(ConstantUtil.user.getUser_Id() == user.getUser_Id()){
					userList.add(user);
				}else if(ConstantUtil.user.getUser_Id() == user.getManagerId()){
					userList.add(user);
				}
			}
			Iterator<UserInst> i = userList.iterator();
			while (i.hasNext()) {
				UserInst userinst = (UserInst) i.next();
				UserLock userlock = new UserLock();
				userlock.setUser_id(userinst.getUser_Id());
				boolean flag = userlockServiece.findUserLock(userlock);
				if (flag) {
					userinst.setIsLock(1);
				}
			}

			this.userinfopanel.clear();
			this.userinfopanel.initData(userList);
			this.userinfopanel.updateUI();

		} catch (Exception e) {
			throw e;
		} finally {
			UiUtil.closeService_MB(userInstServiece);
			UiUtil.closeService_MB(userlockServiece);
		}
	}
	//修改
	@Override
	public void openUpdateDialog() throws Exception {
		UserInst userInst = this.userinfopanel.getSelect();
//		if ("admin".equals(userInst.getUser_Name())) {// 缺省账户，不允许次操作
//			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_DEFAULTUSER));
//		} else {
		if(UiUtil.isAdmin()){
			new AddUserDialog(userInst, userinfopanel);
		}else{
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
		}
//		}
	}
	
	//新建
	@Override
	public void openCreateDialog() {
		UserInst userInst = new UserInst();
		if(UiUtil.isAdmin()){
			new AddUserDialog(userInst, userinfopanel);
		}else{
			DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
		}
	}
	
	//删除
	@Override
	public void delete() throws Exception {
		UserInst userInst = this.userinfopanel.getSelect();
		UserInstServiece_Mb userInstServiece = null;
		UserFieldService_MB userFieldService = null;
		LoginLogServiece_Mb loginLogServiece = null;
		try {
			loginLogServiece = (LoginLogServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			LoginLog loginLog = new LoginLog();
			loginLog.setUser_id(userInst.getUser_Id());
			List<LoginLog> loginLogs = loginLogServiece.select(loginLog);
			if(loginLogs != null && loginLogs.size()>0 && !(loginLogs.get(0).getLogoutState() >0) && loginLogs.get(0).getUser_id() == userInst.getUser_Id()){
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_IS_ONLINE));
				return;
			}
			if(UiUtil.isNotAdmin()){
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
			}
			userInstServiece = (UserInstServiece_Mb) ConstantUtil.serviceFactory.newService_MB(Services.UserInst);
			userFieldService = (UserFieldService_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERFIELD);
			userFieldService.delete(userInst.getUser_Id());
			userInstServiece.delete(userInst.getUser_Id());
			//添加日志记录
			this.insertOpeLog(EOperationLogType.USERDELETE.getValue(), ResultString.CONFIG_SUCCESS, null, null);	
			DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.userinfopanel.getController().refresh();
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(userInstServiece);			
			UiUtil.closeService_MB(userFieldService);
			UiUtil.closeService_MB(loginLogServiece);
		}
	}
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(null, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTab.TAB_USER_INFO),"");		
	}
	
	@Override
	public boolean deleteChecking() {
		boolean flag = false;
		try {
			UserInst userInst = this.userinfopanel.getSelect();
			String uName = userInst.getUser_Name();
			if("admin".equals(uName) || "admin1".equals(uName) || "admin2".equals(uName)){// 缺省账户，不允许次操作
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_DEFAULTUSER));
				return false;
			} else {
				if(UiUtil.isNotAdmin()){
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
					return  false;
				}
			}
			flag = true;
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return flag;
	}

	/**
	 * 鎖定用戶
	 * 
	 * @throws Exception
	 */
	public void lockUser() throws Exception {
		String operate = ResultString.CONFIG_SUCCESS;
		UserInst userinst = this.userinfopanel.getSelect();
		UserLockServiece_MB userlockServiece = null;
		try {
			String uName = userinst.getUser_Name();
			if("admin".equals(uName) || "admin1".equals(uName) || "admin2".equals(uName)){// 缺省账户，不允许次操作
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_DEFAULTUSER));
				operate= ResultString.CONFIG_FAILED;
				return ;
			}
			if(UiUtil.isAdmin()){
				// LoginLogServiece loginLogServiece = (LoginLogServiece)
				// ConstantUtil.serviceFactory.newService(Services.LOGINLOGSERVIECE);
				userinst.setUser_Name(ConstantUtil.user.getUser_Name());
				userlockServiece = (UserLockServiece_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERLOCKSERVIECE);
				UserLock userlock = new UserLock();
				userlock.setUser_id(userinst.getUser_Id());
				boolean flag = userlockServiece.lockUser(userlock, userinst);
				if (flag) {
					// 用户已经被锁定了
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_LOCK));
					operate=ResultString.CONFIG_FAILED;
					
				}
				// }
			} else {
				// 权限不足
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
				operate=ResultString.CONFIG_FAILED;
			
			}
			//添加日志记录
			this.insertOpeLog(EOperationLogType.USERLOCK.getValue(), operate, null, null);	
			//锁定该用户后,会注销掉该用户
			this.logOut();
			this.userinfopanel.getController().refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally{
			UiUtil.closeService_MB(userlockServiece);
		}
	}
	
	private void logOut()throws Exception{
		LoginLogServiece_Mb loginlogServiece = null;
		try {
			LoginLog loginlog = new LoginLog();
			loginlog.setUser_id(this.userinfopanel.getSelect().getUser_Id());
			loginlogServiece = (LoginLogServiece_Mb)ConstantUtil.serviceFactory.newService_MB(Services.LOGINLOGSERVIECE);
			loginlogServiece.updateExitLoginLog(loginlog, 1);
		} catch (Exception e) {
			throw e;
		}finally{
			UiUtil.closeService_MB(loginlogServiece);
		}
	}

	/**
	 * 解鎖
	 * 
	 * @throws Exception
	 */
	public void clearUser() throws Exception {
		UserInst userinst = this.userinfopanel.getSelect();
		UserLockServiece_MB userlockServiece = null;
		try {
			//判断操作是否成功
			//null  成功，fail  失败
			String operate=ResultString.CONFIG_SUCCESS;
			String uName = userinst.getUser_Name();
			if("admin".equals(uName) || "admin1".equals(uName) || "admin2".equals(uName)){// 缺省账户，不允许次操作
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_DEFAULTUSER));
				operate=ResultString.CONFIG_FAILED;
			} 
			if(UiUtil.isAdmin()){
				UserInst userInst = new UserInst();
				userInst.setUser_Name(ConstantUtil.user.getUser_Name());
				userlockServiece = (UserLockServiece_MB) ConstantUtil.serviceFactory.newService_MB(Services.USERLOCKSERVIECE);
				UserLock userlock = new UserLock();
				userlock.setUser_id(userinst.getUser_Id());
				boolean flag = userlockServiece.clearUser(userlock, userInst);
				if (flag) {
					// 用户没有被锁定
					DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_UNLOCK));
					operate =ResultString.CONFIG_FAILED;
				}
			} else {
				// 权限不足
				DialogBoxUtil.errorDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_USER_POWER));
				operate=ResultString.CONFIG_FAILED;
			}
			//添加日志记录
			this.insertOpeLog(EOperationLogType.USERISLOCK.getValue(), operate, null, null);	
			this.userinfopanel.getController().refresh();
		} catch (Exception e) {
            throw e;
		}finally{
			UiUtil.closeService_MB(userlockServiece);
		}
	}
}
