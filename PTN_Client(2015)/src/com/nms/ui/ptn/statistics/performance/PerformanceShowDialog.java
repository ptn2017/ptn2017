package com.nms.ui.ptn.statistics.performance;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.nms.db.bean.perform.HisPerformanceInfo;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysBtn;

public class PerformanceShowDialog extends PtnDialog {
	private static final long serialVersionUID = 6400471791309478897L;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel;
	private PerformanceInfoPanel1 performancePanel;
	private JPanel buttonPanel;
	private List<HisPerformanceInfo> dataList;
	private PtnButton exportBtn;

	public PerformanceShowDialog(List<HisPerformanceInfo> list) {
		this.dataList = list;
		init();
		UiUtil.showWindow(this, 1300, 700);
	}

	public void init() {
		initComponents();
		setLayout();
		addListention();
		initData();
	}

	private void addListention() {
		exportBtn.addActionListener(new MyActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					performancePanel.export();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}
			}

			@Override
			public boolean checking() {
				
				return true;
			}
		});
	}

	public void initComponents() {
		exportBtn = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_EXPORT),false);
		contentScrollPane = new JScrollPane();
		contentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		contentPanel = new JPanel();
//		contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.TAB_CURRALARM)));
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		screenSize.height = 220;
//		screenSize.width = 120;
		this.performancePanel = new PerformanceInfoPanel1();
		contentScrollPane.setViewportView(this.performancePanel);
		buttonPanel = new JPanel();
	}

	private void setButtonLayout() {
		GridBagLayout buttonLayout = new GridBagLayout();
		buttonLayout.columnWidths = new int[] { 40, 40, 40, 180, 80 };
		buttonLayout.columnWeights = new double[] { 0, 0, 0, 0.4, 0 };
		buttonLayout.rowHeights = new int[] { 40 };
		buttonLayout.rowWeights = new double[] { 0 };
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		buttonPanel.setLayout(buttonLayout);
		// 操作菜单按钮布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonLayout.setConstraints(exportBtn, c);
		buttonPanel.add(exportBtn);
	}

	private void setLayout() {
		setButtonLayout();
		GridBagLayout contentLayout = new GridBagLayout();
		contentPanel.setLayout(contentLayout);
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(buttonPanel, c);
		contentPanel.add(buttonPanel);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.4;
		contentLayout.setConstraints(contentScrollPane, c);
		contentPanel.add(contentScrollPane);
		GridBagLayout panelLayout = new GridBagLayout();
		this.setLayout(panelLayout);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		panelLayout.setConstraints(contentPanel, c);
		this.add(contentPanel);
	}

	private void initData() {
		this.performancePanel.initData(dataList);
	}
}
