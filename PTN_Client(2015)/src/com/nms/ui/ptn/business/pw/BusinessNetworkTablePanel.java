package com.nms.ui.ptn.business.pw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import com.nms.db.bean.report.SSProfess;
import com.nms.ui.frame.ViewDataTable;

public class BusinessNetworkTablePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4955133785031558871L;
	private JScrollPane scrollPane;
	private ViewDataTable<SSProfess> table;
	private final String TABLENAME = "businessPanel";

	public BusinessNetworkTablePanel() {
		initComponents();
		setLayout();
	}

	public void clear() {
		table.clear();
	}

	public void initData(List<SSProfess> businessList) {
		if (businessList != null && businessList.size() > 0) {
			table.initData(businessList);
		}
	}

	private void initComponents() {
		table = new ViewDataTable<SSProfess>(TABLENAME);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setTableHeaderPopupMenuFactory(null);
		table.setTableBodyPopupMenuFactory(null);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	private void setLayout() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		GridBagLayout qosGridBagLayout = new GridBagLayout();
		this.setLayout(qosGridBagLayout);
		addComponent(this, scrollPane, 0, 0, 0.5, 1.0, 0, 1, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), GridBagConstraints.NORTHWEST, gridBagConstraints);
	}

	private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, int fill, Insets insets, int anchor, GridBagConstraints gridBagConstraints) {
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.weightx = weightx;
		gridBagConstraints.weighty = weighty;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.fill = fill;
		gridBagConstraints.insets = insets;
		gridBagConstraints.anchor = anchor;
		panel.add(component, gridBagConstraints);
	}

	public ViewDataTable<SSProfess> getTable() {
		return table;
	}
}
