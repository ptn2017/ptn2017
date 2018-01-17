package com.nms.ui.ptn.performance.view;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.UnLoads;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;

/**
 *
 */
public class PerformanceSaveController extends AbstractController {
	private PerformanceSavePanel view=null;
	LogManager logManager=null;
	List<LogManager> logManagerList=null;

	public PerformanceSaveController(PerformanceSavePanel unloadingPanel) {
		this.view = unloadingPanel;
	}

	public PerformanceSaveController() {
		super();
	}

	@Override
	public void refresh() throws Exception {
		try {
			logManagerList = ReadUnloadXML.selectLog();
//            for(int i=logManagerList.size()-1;i>=0;i--){
//				if(logManagerList.get(i).getLogType()==2){
//					logManagerList.remove(i);					
//				}
//				
//			}
			List<LogManager> list = new ArrayList<LogManager>();
			for(LogManager log : logManagerList){
				if(log.getLogType() == UnLoads.PERFORMANCE15){
					list.add(log);
				}else if(log.getLogType() == UnLoads.PERFORMANCE24){
					list.add(log);
				}
			}
			logManagerList.clear();
			logManagerList.addAll(list);
			this.view.clear();
			this.view.initData(logManagerList);
			this.view.updateUI();

		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			logManagerList=null;
		}

	}
	/**
	 * 修改按钮
	 */
	public void openUpdateDialog()throws Exception{
		final PerformanceSaveDialog dialog = new PerformanceSaveDialog(this);
		UiUtil.showWindow(dialog, 380, 340);

	}
	/**
	 * 导入
	* @author sy
	
	* @Exception 异常对象
	 */
	public void inport() throws Exception{
	//	InportDialog dialog = new InportDialog(this);
	//	UiUtil.showWindow(dialog, 300, 180);
	
	}
	
	
	public PerformanceSavePanel getView() {
		return view;
	}

	public void setView(PerformanceSavePanel view) {
		this.view = view;
	}

	
}
