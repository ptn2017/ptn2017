﻿package com.nms.ui.ptn.report.performance;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.db.bean.system.OperationLog;
import com.nms.db.enums.EOperationLogType;
import com.nms.model.perform.CapabilityService_MB;
import com.nms.model.perform.HisPerformanceService_Mb;
import com.nms.model.system.OperationLogService_MB;
import com.nms.model.util.Services;
import com.nms.ui.frame.AbstractController;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ListingFilter;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.performance.model.HisPerformanceFilter;
import com.nms.ui.ptn.performance.view.HisPerformanceExport;
import com.nms.ui.ptn.performance.view.HisPerformanceFilterDialog;

/**
 * 历史性能数据处理类
 * 
 * @author lp
 * 
 */
public class HisPerformanceReportController extends AbstractController {
	
	private HisPerformanceReportPanel view;
	private HisPerformanceFilter filter = null;
	private List<HisPerformanceInfo> hisPerforList = new ArrayList<HisPerformanceInfo>();
	private int direction = 1;//0/1 = 上一页/下一页
	private int totalPage = 1;//总页数
	private int currPage = 1;//当前页数
	private int minId = 0;//当前页面告警数据的最小id
	private int maxId = 0;//当前页面告警数据的最大id
	private int pageCount = 300;//每页大小

	public HisPerformanceReportController(HisPerformanceReportPanel view) {
		this.view = view;
		this.init();
	}
	
	private void init() {
		this.direction = 1;
		this.totalPage = 1;
		this.currPage = 1;
		this.maxId = 0;
		this.minId = 0;
		this.view.getTotalPageLabel().setText(this.totalPage + "");
		this.view.getCurrPageLabel().setText(this.currPage + "");
	}


	// 清空过滤条件
	@Override
	public void clearFilter() {
		this.filter = null;
		this.view.clear();
		this.hisPerforList.clear();
		this.init();
		this.refresh();
	}

	// 打开设置过滤对话框
	@Override
	public void openFilterDialog() {
		final HisPerformanceFilterDialog filterDialog = new HisPerformanceFilterDialog();
		filterDialog.getConfirm().addActionListener(new MyActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					HisPerformanceReportController.this.setFilter(filterDialog);
				} catch (Exception e) {
					ExceptionManage.dispose(e, getClass());
				}
			}
			@Override
			public boolean checking() {
				return checkValue(filterDialog);
			}
		});
		
//		filterDialog.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				filterDialog.dispose();
//			}
//		});
		
		UiUtil.showWindow(filterDialog, 550, 630);
	}
	
	//验证数据的正确性
	private boolean checkValue(HisPerformanceFilterDialog filterDialog) {
		
		try {
			if (filterDialog.validateParams()) {
				return true;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return false;
	}
	private void setFilter(HisPerformanceFilterDialog dialog) {
		try {
			this.filter = dialog.get();
			this.getPerformanceList();
			this.init();
			this.refresh();
//			hisPerforList = new ArrayList<HisPerformanceInfo>();
//			service = (HisPerformanceService) ConstantUtil.serviceFactory.newService(Services.HisPerformance);
//			hisPerforList =  service.queryByFilter(filter);
			//添加日志记录
			AddOperateLog.insertOperLog(dialog.getConfirm(), EOperationLogType.HISPERFORMANCESELECT.getValue(), null);
//			this.view.clear();
//			this.view.initData(hisPerforList);
//			this.view.updateUI();
		    DialogBoxUtil.succeedDialog(dialog, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			dialog.dispose();
		}
	}

	/**
	 * 根据性能类型和性能类别查出对应的code
	 */
	private void getPerformanceList() {
		CapabilityService_MB service = null;
		try {
			String[] typeArr = this.filter.getTypeStr().trim().split(",");
			List<String> capNameList = this.filter.getCapabilityNameList();
			service = (CapabilityService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Capability);
			List<Capability> capaList = service.select();
			if(capaList != null && !capaList.isEmpty()){
				for (String type : typeArr) {
					for (Capability cap : capaList) {
						if(type.equals(cap.getCapabilitytype()) &&
								(capNameList.contains(cap.getCapabilitydesc()) ||
										capNameList.contains(cap.getCapabilitydesc_en()))){
							this.filter.getPerformanceCodeList().add(cap.getCapabilitycode());
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}

	// 从后台查询性能值
	@Override
	public void refresh() {
		try {
			this.updateUI(this.minId-1, 1);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
//		HisPerformanceService service = null;
//		try {
//			hisPerforList = new ArrayList<HisPerformanceInfo>();
//			if (filter == null) {
//				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILTER));
//				return;
//			}
//			else {
//				service = (HisPerformanceService) ConstantUtil.serviceFactory.newService(Services.HisPerformance);
//				hisPerforList = service.queryByFilter(filter);
//				this.view.clear();
//				this.view.initData(hisPerforList);
//				this.view.updateUI();
//			}
//		} catch (Exception e) {
//			ExceptionManage.dispose(e,this.getClass());
//		} finally {
//			UiUtil.closeService(service);
//		}
	}

	public HisPerformanceReportPanel getView() {
		return view;
	}
	
	/**
	 * function:通过一些过滤条件来获取相应的数据
	 * @param infos
	 */
	private void setFilterHisPerformances(List<HisPerformanceInfo> infos) {
		String[] filrertypes = filter.getTypeStr().trim().split(",");

		for (HisPerformanceInfo hisPerformanceInfo : infos) {
			if (hisPerformanceInfo.getCapability() != null) {
				for (int i = 0; i < filrertypes.length; i++) {
					if (filrertypes[i].equals(hisPerformanceInfo.getCapability().getCapabilitytype())) {
						for (String strType : filter.getCapabilityNameList()) {
							if (filter.getFiterZero() > 0) {
								if (hisPerformanceInfo.getCapability().getCapabilitydesc().equalsIgnoreCase(strType)
										&& hisPerformanceInfo.getMonitorCycle() == filter.getMonitorCycle()
										&& hisPerformanceInfo.getPerformanceValue() != 0
										&& hisPerformanceInfo.getObjectName() != null) {
									hisPerforList.add(hisPerformanceInfo);
								}
							} else {
								if (hisPerformanceInfo.getCapability().getCapabilitydesc().equalsIgnoreCase(strType)
										&& hisPerformanceInfo.getMonitorCycle() == filter.getMonitorCycle()
										&& hisPerformanceInfo.getObjectName() != null) {
									hisPerforList.add(hisPerformanceInfo);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 *  界面零值过滤
	 */
	@Override
	public void filterZero(){
		try {
			if(this.filter == null){
				this.filter = new HisPerformanceFilter();
			}
			this.filter.setFiterZero(1);//0/1 = 显示零值/不显示零值
			this.init();
			this.refresh();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	};
	
	@Override
	// 导出
	public void export() throws Exception {
		final HisPerformanceExport filterDialog = new HisPerformanceExport();
		filterDialog.setSize(new Dimension(430, 350));
		filterDialog.setLocation(UiUtil.getWindowWidth(filterDialog.getWidth()), UiUtil.getWindowHeight(filterDialog.getHeight()));
		filterDialog.getConfirm().addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				btnSave(filterDialog.getNowExport(), filterDialog.getAllExport(), filterDialog.getTimeExport(), filterDialog.getTimeTextField(),
						filterDialog);
			}

			@Override
			public boolean checking() {
				return true;
			}
		});
//		filterDialog.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				filterDialog.dispose();
//			}
//		});
		filterDialog.setResizable(false);
		filterDialog.setVisible(true);
	};

	public void btnSave(JRadioButton now, JRadioButton all, JRadioButton timebutton, JTextField time, HisPerformanceExport filterDialog) {
		HisPerformanceService_Mb service = null;
		List<HisPerformanceInfo> infos = null;
		CSVUtil csvUtil = null;
		String[] s = {};
		String path = null;
		String statTime = null;
		String regex = null;
		SimpleDateFormat sdf = null;
		String result="";//日志记录结果
		UiUtil uiUtil = new UiUtil();
		int comfirmResult = 0;
		String csvFilePath = "";
		try {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			csvUtil = new CSVUtil();
			// 导出当前的数据
			if (now.isSelected()) {
				infos = new ArrayList<HisPerformanceInfo>();
				if (this.hisPerforList != null && !this.hisPerforList.isEmpty()) {
					infos.addAll(this.hisPerforList);
				} 
			} else if (all.isSelected()) {
				service = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
				infos = service.select();
			} else if (timebutton.isSelected()) {
				statTime = time.getText();
				// 验证时间是否合乎规则
				regex = "^(((20[0-3][0-9]-(0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9]-(0[2469]|11)-(0[1-9]|[12][0-9]|30))) (20|21|22|23|[0-1][0-9]):[0-5][0-9]:[0-5][0-9])$";
				if (statTime.matches(regex)) {
					if(sdf.parse(statTime).getTime()>new Date().getTime()){
						DialogBoxUtil.succeedDialog(null, ResourceUtil.srcStr(StringKeysTip.STARTTIMEANOWTIME));
						return ;
					}
					service = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
					infos = service.selectTime(statTime);
				} else {
					DialogBoxUtil.succeedDialog(null,ResourceUtil.srcStr(StringKeysTip.DATEREGEXFALSE));
					return ;
				}
			}
			path = csvUtil.showChooserWindow("save", "选择文件", s);
			if(path != null && !"".equals(path)){
				csvFilePath = path + ".csv";
				if(uiUtil.isExistAlikeFileName(csvFilePath)){
					comfirmResult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
					if(comfirmResult == 1){
						return ;
					}
				}
				csvUtil.WriteCsvHisPerformace(csvFilePath, infos);
				AddOperateLog.insertOperLog(filterDialog.getConfirm(), EOperationLogType.EXPORTHISPERFORMANCE.getValue(), result);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
			infos = null;
			csvUtil = null;
			s = null;
			path = null;
			regex = null;
			sdf = null;
			csvFilePath = null;
			result = null;
			filterDialog.dispose();
		}
	};
	
	@Override
	// 导入
	public void inport() throws Exception {
		CSVUtil csvUtil = null;
		String path = null;
		String[] s = {};
		OperationLogService_MB operationService = null;
		OperationLog operationLog = null;
		try {
			csvUtil = new CSVUtil();
			operationLog = new OperationLog();
			path = csvUtil.showChooserWindow("open", "选择文件", s);
			if (path != null && !"".equals(path)) {
				hisPerforList = csvUtil.inportCsvHisPerformace(path);
				operationService=(OperationLogService_MB) ConstantUtil.serviceFactory.newService_MB(Services.OPERATIONLOGSERVIECE);
				operationLog.setOverTime(DateUtil.getDate(DateUtil.FULLTIME));
				operationLog.setUser_id(ConstantUtil.user.getUser_Id());
				operationLog.setOperationType(EOperationLogType.INPORTHISPERFORMANCE.getValue());
				operationLog.setOperationResult(1);
				operationService.insertOperationLog(operationLog);
				this.view.clear();
				this.view.initData(hisPerforList);
				this.view.updateUI();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(operationService);
		}
	};
	
	public void prevPage() {
		this.direction = 0;
		this.currPage -= 1;
		this.pageTurning();
		if(this.currPage == 1){
			this.view.getPrevPageBtn().setEnabled(false);
		}
		this.view.getNextPageBtn().setEnabled(true);
		this.view.getCurrPageLabel().setText(this.currPage + "");
	}

	public void nextPage() {
		this.direction = 1;
		this.currPage += 1;
		this.pageTurning();
		this.view.getPrevPageBtn().setEnabled(true);
		if(this.currPage == this.totalPage){
			this.view.getNextPageBtn().setEnabled(false);
		}
		this.view.getCurrPageLabel().setText(this.currPage + "");
	} 
	
	/**
	 * 翻页
	 */
	private void pageTurning(){
		try {
			int id = 0;
			if(this.direction == 0){
				id = this.minId;
			}else{
				id = this.maxId;
			}
			this.updateUI(id, this.direction);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void updateUI(int id, int type){
		HisPerformanceService_Mb service = null;
		ListingFilter listFilter = null;
		int count = 0;
		try {
			this.hisPerforList.clear();
			listFilter = new ListingFilter();
			service = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
			if (filter == null) {
				// 若过滤条件为空，则显示所有信息
				count = service.selectCount(null, listFilter.getSiteIdListAll());
				this.hisPerforList.addAll(service.selectByPage(type, id, 
						null, listFilter.getSiteIdListAll(), this.pageCount));
			} else {
				// 根据过滤条件查询
				count = service.selectCount(this.filter, this.filter.getSiteInsts());
				this.hisPerforList.addAll(service.selectByPage(type, id,
						this.filter, this.filter.getSiteInsts(), this.pageCount));
			}
			this.totalPage = count%300 == 0 ? count/300 : (count/300+1);
			//如果总页数小于当前页数，说明数据被转储，要重新查询
			if(this.totalPage == 0){
				this.totalPage = 1;
			}
			if(this.totalPage < this.currPage){
				this.init();
				this.refresh();
			}
			this.view.getTotalPageLabel().setText(this.totalPage + "");
			this.view.getCurrPageLabel().setText(this.currPage + "");
			this.view.getPrevPageBtn().setEnabled(true);
			this.view.getNextPageBtn().setEnabled(true);
			if(this.currPage == 1){
				this.view.getPrevPageBtn().setEnabled(false);
//					if(this.totalPage == 1){
//						this.view.getNextPageBtn().setEnabled(false);
//					}else{
//						this.view.getNextPageBtn().setEnabled(true);
//					}
			}
			if(this.currPage == this.totalPage){
				this.view.getNextPageBtn().setEnabled(false);
			}
			if(this.hisPerforList.size() > 0){
				int minid = this.hisPerforList.get(0).getId();
				int maxid = this.hisPerforList.get(this.hisPerforList.size() - 1).getId();
				if(minid < maxid){
					this.minId = minid;
					this.maxId = maxid;
				}else{
					List<HisPerformanceInfo> hisPerforInfoList = new ArrayList<HisPerformanceInfo>();
					for (int i = (this.hisPerforList.size()-1); i >= 0; i--) {
						hisPerforInfoList.add(this.hisPerforList.get(i));
					}
					this.hisPerforList.clear();
					this.hisPerforList.addAll(hisPerforInfoList);
					this.minId = maxid;
					this.maxId = minid;
				}
			}
			this.view.clear();
			this.view.initData(this.hisPerforList);
			this.view.updateUI();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
	}
	
	/**
	 * 跳转到指定页
	 */
	public void goToAction() throws Exception {
		if (CheckingUtil.checking(this.view.getGoToTextField().getText(), CheckingUtil.NUM1_9)) {// 判断填写是否为数字
			int goi = Integer.parseInt(this.view.getGoToTextField().getText());
			int goDir = 0;
			int range = 0;
			if(goi < 1 || goi > this.totalPage){
				DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.TIP_OUT_LIMIT));
				this.view.getGoToTextField().setText("");
				return;
			}
			if(goi > this.currPage){
				 goDir = 1;
				 range = goi-this.currPage-1;
			}else if(goi < this.currPage){
				range = this.currPage-goi-1;
			}else{
				this.refresh();
				return;
			}
			if(goi >= this.totalPage){
				goi = this.totalPage;
				this.view.getNextPageBtn().setEnabled(false);
			}
			if(goi == 1){
				this.view.getPrevPageBtn().setEnabled(false);
			}
			if(goi > 1){
				this.view.getPrevPageBtn().setEnabled(true);
			}
			if(goi < this.totalPage){
				this.view.getNextPageBtn().setEnabled(true);
			}
			this.currPage = goi;
			flipRefresh(goDir, range);
		}else{
			DialogBoxUtil.errorDialog(this.view, ResourceUtil.srcStr(StringKeysTip.MESSAGE_NUMBER));
		}
	}

	/**
	 * @param goDir 0/1 = 跳转页小于当前页(相当于是上一页)/跳转页大于当前页(相当于是上一页)
	 * @param range 跳转页和当前页相差的页数
	 */
	private void flipRefresh(int goDir, int range) {
		HisPerformanceService_Mb service = null;
		try {
			int id = 0;
			if(goDir == 0){
				id = this.minId;
			}else{
				id = this.maxId;
			}
			if(range > 0){
				service = (HisPerformanceService_Mb) ConstantUtil.serviceFactory.newService_MB(Services.HisPerformance);
				if (filter == null) {
					// 若过滤条件为空，则显示所有信息
					id = service.queryIdByGoPage(goDir, range*this.pageCount, id, null, new ListingFilter().getSiteIdListAll());
				} else {
					// 根据过滤条件查询
					id = service.queryIdByGoPage(goDir, range*this.pageCount, id, this.filter, this.filter.getSiteInsts());
				}
				if(id == 0){
					ExceptionManage.dispose(new Exception("跳转指定页的id为0"), this.getClass());
				}else{
					this.updateUI(id, goDir);
				}
			}
			this.updateUI(id, goDir);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		} finally {
			UiUtil.closeService_MB(service);
		}
		
	}
}
