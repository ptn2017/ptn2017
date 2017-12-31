package com.nms.ui.ptn.safety.controller;

import java.util.List;
import com.nms.db.bean.system.LogManager;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.ptn.safety.LogManagerPanel;
import com.nms.ui.ptn.safety.dialog.LogManagerDialog;
import com.nms.ui.ptn.systemManage.ReadUnloadXML;

/**
 * 主界面按钮事件处理
 * @author Administrator
 *
 */
public class LogManagerController extends AbstractController {
	private LogManagerPanel view=null;
	LogManager logManager=null;
	List<LogManager> logManagerList=null;

	public LogManagerController(LogManagerPanel unloadingPanel) {
		this.view = unloadingPanel;
	}

	public LogManagerController() {
		super();
	}

	@Override
	public void refresh() throws Exception {
		try {
			logManagerList = ReadUnloadXML.selectLog();
            for(int i=logManagerList.size()-1;i>=0;i--){
				if(logManagerList.get(i).getLogType()==2){
					logManagerList.remove(i);					
				}
				
			}
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
		final LogManagerDialog dialog = new LogManagerDialog(this);
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
	
	
	public LogManagerPanel getView() {
		return view;
	}

	public void setView(LogManagerPanel view) {
		this.view = view;
	}

	
}
