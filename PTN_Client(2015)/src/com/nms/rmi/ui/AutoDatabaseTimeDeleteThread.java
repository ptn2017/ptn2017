package com.nms.rmi.ui;

import java.io.File;
import java.util.TimerTask;
import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.UnLoading;
import com.nms.model.system.LogManagerService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;


public class AutoDatabaseTimeDeleteThread extends TimerTask{
	 private int label;//标签用来确定是自动转储的 1:告警 2:性能 3:操作日志 4:登录日志
	 private String fileAddress ;//文件目录地址

	 
	 public AutoDatabaseTimeDeleteThread(long startTime,UnLoading unload){
		 this.label = unload.getUnloadType();
		 this.fileAddress = unload.getFileWay();
		 
	 }
	 

		@Override
		public void run() {	      
	      LogManagerService_MB logService=null;
			try {
				logService = (LogManagerService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LOGMANAGER);
				delAllFile(this.fileAddress);
				if(label==3|| label==4){
					LogManager log=logService.selectCount(label);
					delAllFile(log.getFileVWay());
					delAllFile(log.getFileWay());
				}
				
			} catch (Exception e) {
				ExceptionManage.dispose(e, getClass());
			}finally
			{
				UiUtil.closeService_MB(logService);
			}
		}
		

		public static boolean delAllFile(String path) {
			boolean flag = false;
			File file = new File(path);
			if (!file.exists()) {
			    return flag;
			}
			if (!file.isDirectory()) {
			    return flag;
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
				    temp = new File(path + tempList[i]);
				} else {
				    temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
				    temp.delete();
				}
				if (temp.isDirectory()) {
				    delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
				    flag = true;
				}
			}
			return flag;
		}		
		
		public static void main(String[] args) {
			
		}
		
		


	}
