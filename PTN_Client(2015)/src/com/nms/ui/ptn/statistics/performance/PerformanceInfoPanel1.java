package com.nms.ui.ptn.statistics.performance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.alarm.service.CSVUtil;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class PerformanceInfoPanel1 extends ContentView<HisPerformanceInfo> {
	private static final long serialVersionUID = 6306612170631181766L;
	private JFileChooser fileChooser = null;

	public PerformanceInfoPanel1() {
		super("hisPerformanceTable",RootFactory.PROFOR_MANAGE);
		init();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysLbl.LBL_PERFORMANCE_LIST)));
		fileChooser = new JFileChooser();
		setLayout();
	}

	private void setLayout() {
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(getContentPanel(), c);
		this.add(getContentPanel());
	}

	@Override
	public List<JButton> setNeedRemoveButtons() {
		List<JButton> needRemoveButtons = new ArrayList<JButton>();
		needRemoveButtons.add(getRefreshButton());
		needRemoveButtons.add(getExportButton());
		return needRemoveButtons;
	}

	/*
	 * 初始化数据
	 */
	@Override
	public void initData(List<HisPerformanceInfo> infos) {
		if (infos != null && infos.size() > 0) {
			for (HisPerformanceInfo info : infos) {
				info.putClientProperty();
				this.add(info);
			}
		}

	}

	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	@Override
	public void setController() {
	}
	
	public void export() throws Exception {
		CSVUtil csvUtil = null;
		String[] s = {};
		String path = null;
		UiUtil uiUtil = new UiUtil();
		int comfirmResult = 0;
		String csvFilePath = "";
		try {
			csvUtil = new CSVUtil();
			// 导出当前的数据
			path = csvUtil.showChooserWindow("save", "选择文件", s);
			if(path != null && !"".equals(path)){
				csvFilePath = path + ".csv";
				if(uiUtil.isExistAlikeFileName(csvFilePath)){
					comfirmResult = DialogBoxUtil.confirmDialog(null, ResourceUtil.srcStr(StringKeysTip.TIP_CHOOSE_FILEPATHHASEXIT));
					if(comfirmResult == 1){
						return;
					}
				}
				csvUtil.WriteCsvHisPerformace(csvFilePath, this.getAllSelect());
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
}
