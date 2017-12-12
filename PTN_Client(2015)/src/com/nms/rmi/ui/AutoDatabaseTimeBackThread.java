package com.nms.rmi.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.UnLoading;
import com.nms.model.system.LogManagerService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.systemManage.AutoDumpHisPerAndAlarm;

public class AutoDatabaseTimeBackThread extends TimerTask{
 private int label;//标签用来确定是自动转储的 1:告警 2:性能 3:操作日志 4:登录日志
 private int count;//转储的数目
 private String fileAddress ;//文件目录地址

 
 public AutoDatabaseTimeBackThread(long startTime,UnLoading unload){
	 this.label = unload.getUnloadType();
	 this.count = unload.getSpillEntry();
	 this.fileAddress = unload.getFileWay();
	 
 }
 
 public AutoDatabaseTimeBackThread(long startTime,LogManager unload){
	 this.label = unload.getLogType();
	 this.count = 0;
	 this.fileAddress = unload.getFileWay();
	 
 }
	@Override
	public void run() {
      AutoDumpHisPerAndAlarm autoDumpHisPerAndAlarm = new AutoDumpHisPerAndAlarm();
      LogManagerService_MB logService=null;
		try {
			logService = (LogManagerService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LOGMANAGER);
			if(label==3){				
				LogManager log=logService.selectCount(label);
				count=log.getTotalCount();
				int maxId=log.getMaxId();
				maxId=logService.selectCounts(maxId).getTotalCount();
				autoDumpHisPerAndAlarm.autoDump(label, count,maxId,fileAddress);				
			}else{
				autoDumpHisPerAndAlarm.autoDump(label, count,0,fileAddress);				
			}
			
			
				
			
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}finally
		{
			autoDumpHisPerAndAlarm = null;
			UiUtil.closeService_MB(logService);
		}
	}
	

	
	
	public static void main(String[] args) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long s = format.parse("2015-1-24 11:39:15").getTime();
			long s1 = format.parse("2015-1-24 11:54:17").getTime();
			long s2 = 1000*60*15;
			System.out.println(s);
			System.out.println(s1);
			System.out.println(s2);
			System.out.println((s1 - s));
			System.out.println((s1 - s)/s2);
			System.out.println((s1 - s)%s2 < 5*1000);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	


}
