package com.nms.ui.ptn.ne.pri.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import com.nms.db.bean.ptn.qos.QosMappingAttr;
import com.nms.db.bean.ptn.qos.QosMappingMode;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysLbl;

public class PriPanel extends JPanel{
	
	private JTable legTable;
	private JScrollPane legJScrollPanel;
	private JPanel upPanel;
	private QosMappingMode qosMappingMode;
	
	public PriPanel(QosMappingMode qosMappingMode)throws Exception{
		try{
		this.qosMappingMode=qosMappingMode;
		initComponents();
		jTableColumsHide(legTable,0);
		setUpLayout();
		setLayout();
		setDefaultTable();
		initData();
		lock();
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}
		}
	private void lock(){
		this.txtName.setEditable(false);
		this.legTable.setEnabled(false);
		
	}
	private void initData(){
		if(null!=qosMappingMode){
		this.txtName.setText(qosMappingMode.getName());
	}
	}
	private void initComponents() throws Exception {
		upPanel=new JPanel();
		legTable=new JTable();
		legJScrollPanel = new JScrollPane();
		legJScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		legJScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		legTable.getTableHeader().setResizingAllowed(true);
		legTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		legTable.setModel(new DefaultTableModel(new Object[][] {
				
		}, new String[] { "序号", "PHB", "PRI" }) {
			Class[] types = new Class[] {  java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == 0 )
					return false;
				return true;
			}
		}

		);
		legJScrollPanel.setViewportView(legTable);
		
		lblName=new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAME));
		txtName=new JTextField();
	}
	/**
	 * 选取默认值填入支路信息表中
	 */
	private void setDefaultTable() {
		if(null!=qosMappingMode){
		List<QosMappingAttr> qosMappingAttrList=qosMappingMode.getQosMappingAttrList();
		DefaultTableModel defaultTableModel = null;
		defaultTableModel = (DefaultTableModel) legTable.getModel();
		defaultTableModel.getDataVector().clear();
		defaultTableModel.fireTableDataChanged();
		Object[] obj = null;
		for(int i=0;i<qosMappingAttrList.size();i++){
			obj = new Object[] {i+1,qosMappingAttrList.get(i).getGrade(),String.valueOf(qosMappingAttrList.get(i).getValue())};
			defaultTableModel.addRow(obj);
		}
		legTable.setModel(defaultTableModel);
		}
	}

	private void setUpLayout() {
		GridBagLayout componentLayout = new GridBagLayout();// 网格布局
		componentLayout.columnWidths = new int[] { 0, 100, 660, 0 };
		componentLayout.columnWeights = new double[] { 0, 0, 0, 0 };
		componentLayout.rowHeights = new int[] { 15, 40};
		componentLayout.rowWeights = new double[] { 0, 0, 0, 0 };
		this.upPanel.setLayout(componentLayout);
		
		GridBagConstraints gridCon = new GridBagConstraints();
		gridCon.fill = GridBagConstraints.HORIZONTAL;
		gridCon.insets = new Insets(5, 5, 5, 5);
		gridCon.anchor = GridBagConstraints.CENTER;

		gridCon.gridx = 1;
		gridCon.gridy = 1;
		gridCon.gridheight = 1;
		gridCon.gridwidth = 1;
		componentLayout.setConstraints(this.lblName, gridCon);
		this.upPanel.add(this.lblName);
		gridCon.gridx = 2;
		gridCon.gridwidth = 1;
		gridCon.gridheight = 1;
		componentLayout.setConstraints(this.txtName, gridCon);
		this.upPanel.add(this.txtName);
	}
	
	/**
	 * 设置布局
	 */
	private void setLayout() {
		GridBagLayout componentLayout = new GridBagLayout();
		componentLayout.columnWidths = new int[] {800 };
		componentLayout.columnWeights = new double[] { 0 };
		componentLayout.rowHeights = new int[] { 100,200};
		componentLayout.rowWeights = new double[] { 0,0,0};
		this.setLayout(componentLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;        
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		componentLayout.setConstraints(this.upPanel, c);
		this.add(this.upPanel);
		
		c.gridy=1;
		componentLayout.setConstraints(this.legJScrollPanel, c);
		this.add(this.legJScrollPanel);
	}
	
	private void jTableColumsHide(JTable jTable, int count) {

		jTable.getColumnModel().getColumn(count).setMinWidth(40);
		jTable.getColumnModel().getColumn(count).setMaxWidth(40);

	}

	private JLabel lblName;
	private JTextField txtName;
}
