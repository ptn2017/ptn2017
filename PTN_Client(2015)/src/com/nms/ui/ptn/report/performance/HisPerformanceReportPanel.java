package com.nms.ui.ptn.report.performance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class HisPerformanceReportPanel extends ContentView<HisPerformanceInfo> {
	private static final long serialVersionUID = 6306612170631181766L;
	private JFileChooser fileChooser=null;

	public HisPerformanceReportPanel() {
		super("hisPerTable",RootFactory.COUNTMODU);
		init();
	}

	public void init() {
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_PERFORMANCE_REPORT)));
		fileChooser=new JFileChooser();
		setLayout();
		try {
//			getController().refresh();
			super.getPrevPageBtn().setEnabled(false);
			super.getNextPageBtn().setEnabled(false);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
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
		needRemoveButtons.add(getAddButton());
		needRemoveButtons.add(getDeleteButton());
		needRemoveButtons.add(getUpdateButton());
		needRemoveButtons.add(getInportButton());
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

	@Override
	public void setController() {
		controller = new HisPerformanceReportController(this);
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	
}
