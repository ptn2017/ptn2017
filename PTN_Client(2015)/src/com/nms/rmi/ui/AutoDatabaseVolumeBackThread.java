package com.nms.rmi.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.nms.db.bean.system.DataBaseInfo;
import com.nms.db.bean.system.LogManager;
import com.nms.model.system.DataBaseService_MB;
import com.nms.model.system.LogManagerService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.alarm.AlarmTools;
import com.nms.ui.ptn.systemManage.AutoDumpHisPerAndAlarm;

public class AutoDatabaseVolumeBackThread implements Runnable{
 private int label;//标签用来确定是自动转储的 1:告警 2:性能 3:操作日志 4:登录日志
 private int volumn;//转储的数目
 private String fileAddress ;//文件目录地址
 private boolean flag = true;
 
 public AutoDatabaseVolumeBackThread(long startTime,LogManager unload){
	 this.volumn=unload.getVolumeLimit();
	 this.label = unload.getLogType();
	 this.fileAddress = unload.getFileVWay();
	 
 }
 
 
 
	@Override
	public void run() {
      AutoDumpHisPerAndAlarm autoDumpHisPerAndAlarm = new AutoDumpHisPerAndAlarm();
      int countCurrent = 0;
      DataBaseService_MB dataBaseService  =  null;
      DataBaseInfo dataBaseInfo = null;
      DataBaseInfo dataBaseInfo1 = null;
      LogManagerService_MB logService=null;
      AlarmTools  alarmTools = new AlarmTools(1);
		try {
			while(flag){
				try {
					dataBaseService = (DataBaseService_MB)ConstantUtil.serviceFactory.newService_MB(Services.DATABASEINFO);
					logService = (LogManagerService_MB) ConstantUtil.serviceFactory.newService_MB(Services.LOGMANAGER);
					if(label==3){
						dataBaseInfo=dataBaseService.selectTableInfo("operation_log",2);
						dataBaseInfo1=dataBaseService.selectTableInfo("operationdatalog",2);
						countCurrent= ((int)(dataBaseInfo.getDataSize()+dataBaseInfo1.getDataSize())/1024);
						if(countCurrent > volumn){
							LogManager log=logService.selectCount(label);
							int count=log.getTotalCount();
							int maxId=log.getMaxId();
							maxId=logService.selectCounts(maxId).getTotalCount();					
							autoDumpHisPerAndAlarm.autoDump(label, count,maxId,fileAddress);
							alarmTools.createMointorClintAndService(1069, 5, "OPERATION LOG",0);
						}
					}else if(label==4){
						dataBaseInfo=dataBaseService.selectTableInfo("login_log",2);
						countCurrent= ((int)(dataBaseInfo.getDataSize())/1024);
						if(countCurrent > volumn){
							LogManager log=logService.selectCount(label);
							int count=log.getTotalCount();					
							autoDumpHisPerAndAlarm.autoDump(label, count,0,fileAddress);
							alarmTools.createMointorClintAndService(1070, 5, "LOGIN LOG",0);
						}
					}				
				} catch (Exception e) {
					ExceptionManage.dispose(e, getClass());
				} finally{
					UiUtil.closeService_MB(dataBaseService);
					UiUtil.closeService_MB(logService);
				}
				
				Thread.sleep(60*1000);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		} finally {
			autoDumpHisPerAndAlarm = null;
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
	
	
	public void stop(){
		flag = false;
//		timer.cancel();
//		timer = null;
	}

}
