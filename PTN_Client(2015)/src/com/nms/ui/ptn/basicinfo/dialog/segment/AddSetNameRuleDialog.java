package com.nms.ui.ptn.basicinfo.dialog.segment;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.nms.db.bean.path.SetNameRule;
import com.nms.db.enums.EOperationLogType;
import com.nms.db.enums.EServiceType;
import com.nms.model.path.NameRuleService_MB;
import com.nms.model.util.Services;
import com.nms.service.impl.util.ResultString;
import com.nms.ui.manager.AddOperateLog;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ControlKeyValue;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.VerifyNameUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.control.PtnTextField;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.basicinfo.SetNameRulePanel;

/**
 * 新建和修改存在一个bug，无法对指定单元格修改格式，只能修改某一列所有编辑格式
 * @author Administrator
 * 
 */
public class AddSetNameRuleDialog extends PtnDialog {

	private static final long serialVersionUID = 1L;
	private JLabel SourceJLabel;// 资源对象
	private JLabel upLabel; // 错误消息文本显示label
	private JLabel typeLabel; // 类型
	private JLabel nameruleLabel; // 规则名称
	private JComboBox SourceComboBox;
	private JTextField nameruleField;// 规则名称文本框
	private JTextField typeField; // 类型文本框
	private PtnButton confirm; // 确认按钮
	private JButton cancel; // 取消按钮
	private JButton addButton; // 增加按钮
	private JButton delButton; // 删除按钮
	private JButton upButton; // 上移按钮
	private JButton downButton; // 下移按钮
	private SetNameRule nameRule = null;
	private JPanel btnPanel = null; // 按钮的面板
	// 规则详情
	private JScrollPane contentPanel;
	private SetNameRulePanel panel;
	private JTable table;
	private JPanel conPanel;
	private JScrollPane tablePane;
	private JPanel btnPanel1 = null; // 按钮的面板
	private DefaultTableModel dtm = null;
	private JComboBox jComboxBoxItem = null;
	private JLabel isUsedJLabel;//是否生效
	private JCheckBox isUsedCheckBox;
	
	/**
	 * 创建一个新的实例
	 * 
	 * @param panel
	 * 
	 * @param loginmanager
	 *            接入设置bean对象 如果是修改操作，传入对象。 新增传入null;
	 * @param loginmanagerPanel
	 *            接入设置列表页面
	 */
	public AddSetNameRuleDialog(SetNameRule nameRule, SetNameRulePanel panel) {
		this.setModal(true);
		try {
			this.nameRule = nameRule;
			this.panel = panel;
			if (nameRule.getId() == 0) {
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_CREATE_NAMERULE));
			} else {
				this.setTitle(ResourceUtil.srcStr(StringKeysTip.TIT_UPDATE_NAMERULE));
			}
			this.setModal(true);
			this.initComponents();
			this.setLayout();
			this.addListener();
			this.initData();
			this.bindingData();
			UiUtil.showWindow(this, 550, 500);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}

	/**
	 * 修改时 绑定数据
	 * 
	 * @throws Exception
	 */
	private void bindingData() throws Exception {

		if (nameRule.getId()>0) {
			this.nameruleField.setText(this.nameRule.getName());
			this.SourceComboBox.setSelectedItem(this.nameRule.getSourcename());
			this.isUsedCheckBox.setSelected(nameRule.getIsUsed()==1?true:false);
		}

	}

	/**
	 * 界面初始化数据数据
	 * 
	 * @throws Exception
	 */
	private void initData() throws Exception {
		initType();

	}

	/*
	 * 初始化下拉菜单
	 */
	private void initType() {
		try {
			DefaultComboBoxModel defaultComboBoxModel = null;
			try {
				defaultComboBoxModel = (DefaultComboBoxModel) SourceComboBox.getModel();
				defaultComboBoxModel.addElement(new ControlKeyValue("0", "SEGMENT"));
				defaultComboBoxModel.addElement(new ControlKeyValue("1", "TUNNEL"));
				defaultComboBoxModel.addElement(new ControlKeyValue("2", "PW"));
				SourceComboBox.setModel(defaultComboBoxModel);
			} catch (Exception e) {
				ExceptionManage.dispose(e, UiUtil.class);
			} finally {
				defaultComboBoxModel = null;
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {

		}

		table.setModel(new DefaultTableModel(new Object[][] {

		},

		new String[] { ResourceUtil.srcStr(StringKeysTip.TIP_NAME_TYPE), ResourceUtil.srcStr(StringKeysTip.TIP_NAME_VALUE) }));

		if (nameRule.getId()>0) {
			dtm = (DefaultTableModel) table.getModel();
			String[] strlist = nameRule.getNamerule().split(" ");
			for(String str : strlist){
				if(str==null || str.equals(""))
				{
					dtm.addRow(new Object[]{"",""});
				}
				else
				{
					dtm.addRow(new Object[]{str.split("\\:")[0],str.split("\\:")[1]});
				}
			}
			dtm.fireTableDataChanged();
			Vector item = new Vector();
			item.add(ResourceUtil.srcStr(StringKeysTip.TIP_STATIC_ATTRIBUTE));
			item.add(ResourceUtil.srcStr(StringKeysTip.TIP_CHANGE_ATTRIBUTE));
			item.add(ResourceUtil.srcStr(StringKeysTip.TIP_CONNECT_ATTRIBUTE));
			jComboxBoxItem = new JComboBox(item);
			jComboxBoxItem.setSelectedIndex(0);
			TableColumn tableColum1 = table.getColumnModel().getColumn(0);
			tableColum1.setCellEditor(new DefaultCellEditor(jComboxBoxItem));
			// table.setValueAt("变量", table.getRowCount()-1, 0);
			jComboxBoxItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					TableColumn tableColum2 = null;
					
					Vector item1 = new Vector();
					item1.add(ResourceUtil.srcStr(StringKeysTip.TIP_LAYERRATE));
					item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT));
					item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_A_SIDE_PORT));
					item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT));
					item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_SIDE_PORT));
					Vector item2 = new Vector();
					item2.add("-");
					item2.add("|");
					item2.add("=");
					item2.add("+");
					item2.add(".");
					item2.add(":");
					item2.add("_");
					item2.add("#");
					item2.add("*");
					item2.add("(");
					item2.add(")");
					item2.add("[");
					item2.add("]");
					item2.add("/");
					item2.add("~");
					item2.add("→");
					JTextField Jtext = new JTextField();
					JTextField Jtext1 = new JTextField();
					Jtext.setText("");
					Jtext1.setVisible(false);
					if (e.getStateChange() == ItemEvent.SELECTED) {
						JComboBox jcb = (JComboBox) e.getSource();
						if (jcb.getSelectedIndex() == 0) {
							tableColum2 = table.getColumnModel().getColumn(1);
							tableColum2.setCellEditor(new DefaultCellEditor(Jtext));
						}else if (jcb.getSelectedIndex() == 1) {
							JComboBox jComboxBoxItem1 = new JComboBox(item1);
							jComboxBoxItem1.setSelectedIndex(0);
							tableColum2 = table.getColumnModel().getColumn(1);
							tableColum2.setCellEditor(new DefaultCellEditor(jComboxBoxItem1));
						} else if (jcb.getSelectedIndex() == 2) {
							JComboBox jComboxBoxItem2 = new JComboBox(item2);
							jComboxBoxItem2.setSelectedIndex(0);
							tableColum2 = table.getColumnModel().getColumn(1);
							tableColum2.setCellEditor(new DefaultCellEditor(jComboxBoxItem2));
						}
					}
				}
			});

		}

	}

	/**
	 * 初始化控件
	 */
	public void initComponents() {

		try {
			this.upLabel = new javax.swing.JLabel("");
			this.typeLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_TYPE));
			this.nameruleLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_NAME));

			this.confirm = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM), false);
			this.SourceComboBox = new JComboBox();
			this.typeField = new PtnTextField(true, 100, upLabel, confirm, this);
			this.nameruleField = new PtnTextField(true, 100, upLabel, confirm, this);
			typeField.setText(ResourceUtil.srcStr(StringKeysTip.TIP_SHOW_TYPE));
			typeField.setEditable(false);
			isUsedJLabel = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_IS_USED));
			isUsedCheckBox = new JCheckBox();
			this.cancel = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CANEL));
			this.btnPanel = new JPanel();
			this.btnPanel1 = new JPanel();
			this.SourceJLabel = new javax.swing.JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SOURCE));

			this.addButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_ADD)); // 增加按钮
			this.delButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_REMOVE)); // 删除按钮
			this.upButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_UP)); // 上移按钮
			this.downButton = new javax.swing.JButton(ResourceUtil.srcStr(StringKeysBtn.BTN_DOWN)); // 下移按钮
			table = new JTable();
			tablePane = new JScrollPane(table);
			conPanel = new JPanel();
			contentPanel = new JScrollPane();
			table.getTableHeader().setResizingAllowed(true);
			tablePane.setPreferredSize(new Dimension(200, 200));
			tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			contentPanel.setViewportView(conPanel);
			contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_SETNAME_DETAIL)));
			contentPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			contentPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}

	}

	/**
	 * 设置布局
	 */
	private void setLayout() {
		this.setBtnLayout();
		this.setContBagLayout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 60, 120, 60, 120 };
		layout.columnWeights = new double[] { 0, 0.1, 0, 0.1 };
		layout.rowHeights = new int[] { 20, 40, 40, 300, 60 };
		layout.rowWeights = new double[] { 0, 0, 0, 0.1, 0 };
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 10, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		// 第一行 错误消息
		c.gridx = 0;
		c.gridy = 0;
		layout.addLayoutComponent(this.upLabel, c);
		this.add(upLabel);

		// 第二行 资源名称
		c.gridx = 0;
		c.gridy = 1;
		layout.setConstraints(this.SourceJLabel, c);
		this.add(SourceJLabel);

		c.gridx = 1;
		c.gridy = 1;
		layout.setConstraints(this.SourceComboBox, c);
		this.add(SourceComboBox);

		c.gridx = 2;
		c.gridy = 1;
		layout.setConstraints(this.typeLabel, c);
		this.add(typeLabel);

		c.gridx = 3;
		c.gridy = 1;
		layout.setConstraints(this.typeField, c);
		this.add(typeField);

		// 第3行 规则名称
		c.gridx = 0;
		c.gridy = 2;
		layout.setConstraints(this.nameruleLabel, c);
		this.add(nameruleLabel);
		c.gridx = 1;
		layout.setConstraints(this.nameruleField, c);
		this.add(nameruleField);
		
		c.gridx = 2;
		c.gridy = 2;
		layout.setConstraints(this.isUsedJLabel, c);
		this.add(isUsedJLabel);
		c.gridx = 3;
		layout.setConstraints(this.isUsedCheckBox, c);
		this.add(isUsedCheckBox);

		// 命名详细规则
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 4;
		layout.setConstraints(this.contentPanel, c);
		this.add(contentPanel);

		// 第五行 按钮
		c.gridy = 4;
		layout.setConstraints(this.btnPanel, c);
		this.add(btnPanel);

	}

	// conPanel布局
	private void setContBagLayout() {
		this.setBtn1Layout();
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 420, 40 };
		layout.columnWeights = new double[] { 0, 0.1 };
		layout.rowHeights = new int[] { 250 };
		layout.rowWeights = new double[] { 0 };
		this.conPanel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		layout.setConstraints(tablePane, c);
		this.conPanel.add(tablePane);

		c.gridx = 1;
		c.insets = new Insets(0, 5, 5, 0);
		layout.setConstraints(btnPanel1, c);
		this.conPanel.add(btnPanel1);

	}

	private void setBtn1Layout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 40 };
		layout.columnWeights = new double[] { 0.1 };
		layout.rowHeights = new int[] { 40, 40, 40, 40 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0 };
		this.btnPanel1.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;

		layout.addLayoutComponent(this.addButton, c);
		this.btnPanel1.add(addButton);
		c.gridy = 1;

		layout.addLayoutComponent(this.delButton, c);
		this.btnPanel1.add(delButton);
		c.gridy = 2;

		layout.addLayoutComponent(this.upButton, c);
		this.btnPanel1.add(upButton);
		c.gridy = 3;

		layout.addLayoutComponent(this.downButton, c);
		this.btnPanel1.add(downButton);
	}

	/**
	 * 设置按钮布局
	 */
	private void setBtnLayout() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 260, 70, 70 };
		layout.columnWeights = new double[] { 0.1, 0, 0 };
		layout.rowHeights = new int[] { 40 };
		layout.rowWeights = new double[] { 0 };
		this.btnPanel.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		layout.addLayoutComponent(this.confirm, c);
		this.btnPanel.add(confirm);
		c.gridx = 2;
		layout.addLayoutComponent(this.cancel, c);
		this.btnPanel.add(cancel);
	}

	public PtnButton getConfirm() {
		return confirm;
	}

	/**
	 * 添加监听
	 */
	private void addListener() {

		confirm.addActionListener(new MyActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (table.isEditing()) {
					table.getCellEditor().stopCellEditing();
				}
				AddSetNameRuleDialog.this.saveUser();

			}

			@Override
			public boolean checking() {
				return true;
			}
		});

		// 添加一行
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dtm = (DefaultTableModel) table.getModel();
				int rowcount = table.getRowCount() + 1;
				dtm.addRow(new Object[rowcount]);
				Vector item = new Vector();
				item.add(ResourceUtil.srcStr(StringKeysTip.TIP_STATIC_ATTRIBUTE));
				item.add(ResourceUtil.srcStr(StringKeysTip.TIP_CHANGE_ATTRIBUTE));
				item.add(ResourceUtil.srcStr(StringKeysTip.TIP_CONNECT_ATTRIBUTE));
				jComboxBoxItem = new JComboBox(item);
				jComboxBoxItem.setSelectedItem(item.get(0));
				TableColumn tableColum1 = table.getColumnModel().getColumn(0);
				tableColum1.setCellEditor(new DefaultCellEditor(jComboxBoxItem));

				jComboxBoxItem.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						TableColumn tableColum2 = null;
						Vector item1 = new Vector();
						item1.add(ResourceUtil.srcStr(StringKeysTip.TIP_LAYERRATE));
						item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_A_PORT));
						item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_A_SIDE_PORT));
						item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_PORT));
						item1.add(ResourceUtil.srcStr(StringKeysLbl.LBL_Z_SIDE_PORT));
						Vector item2 = new Vector();
						item2.add("-");
						item2.add("|");
						item2.add("=");
						item2.add("+");
						item2.add(".");
						item2.add("_");
						item2.add("#");
						item2.add("*");
						item2.add("(");
						item2.add(")");
						item2.add("[");
						item2.add("]");
						item2.add("/");
						item2.add("~");
						item2.add("→");
						JTextField Jtext = new JTextField();
						JTextField Jtext1 = new JTextField();
						Jtext.setText("");
						Jtext1.setVisible(false);
						if (e.getStateChange() == ItemEvent.SELECTED) {
							JComboBox jcb = (JComboBox) e.getSource();
							if (jcb.getSelectedIndex() == 0) {
								tableColum2 = table.getColumnModel().getColumn(1);
								tableColum2.setCellEditor(new DefaultCellEditor(Jtext));
							} else if (jcb.getSelectedIndex() == 1) {
								JComboBox jComboxBoxItem1 = new JComboBox(item1);
								jComboxBoxItem1.setSelectedIndex(0);
								tableColum2 = table.getColumnModel().getColumn(1);
								tableColum2.setCellEditor(new DefaultCellEditor(jComboxBoxItem1));
							} else if (jcb.getSelectedIndex() == 2) {
								JComboBox jComboxBoxItem2 = new JComboBox(item2);
								jComboxBoxItem2.setSelectedIndex(0);
								tableColum2 = table.getColumnModel().getColumn(1);
								tableColum2.setCellEditor(new DefaultCellEditor(jComboxBoxItem2));
//
							}
						}
					}
				});

				table.invalidate();
				dtm.fireTableDataChanged();

			}
		});

		// 删除行
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				dtm = (DefaultTableModel) table.getModel();
				if (row == -1) {
					DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SELECT));
					return;
				} else {
					dtm.removeRow(row);
					dtm.fireTableDataChanged();
					dtm.fireTableStructureChanged();
				}
			}
		});

		// 上移一行
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dtm = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				if (row == -1) {
					DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SELECT));
					return;
				} else {
					if (row == 0) {
						DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_MOVEUP));
						return;
					} else {
						dtm.moveRow(row, row, row - 1);
						dtm.fireTableStructureChanged();
						table.changeSelection(row - 1, 0, false, false);
					}
				}
			}
		});

		// 下移一行
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dtm = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				int rowCount = table.getRowCount() - 1;
				if (row == -1) {
					DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SELECT));
					return;
				} else {
					if (row == rowCount) {
						DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_MOVEDOWN));
						return;
					} else {
						dtm.moveRow(row, row, row + 1);
						dtm.fireTableStructureChanged();
						table.changeSelection(row + 1, 0, false, false);
					}
				}
			}
		});

		// 取消按钮
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();

			}
		});
	}

	/*
	 * 使表格数据瞬间变化
	 */
	public void commitTable(JTable table) {
		int selectR = -1;
		int selectC = -1;
		int newValue = 0;
		JSpinner spinner = null;
		try {
			if(table.getSelectedColumn()==5){
				if(((Boolean)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).booleanValue()){
					table.setValueAt(4, table.getSelectedRow(),  table.getSelectedColumn()+1);
				}else{
					table.setValueAt(0, table.getSelectedRow(),  table.getSelectedColumn()+1);
				}
			}
			if (table.getEditorComponent() != null) {
				selectR = table.getSelectedRow();
				selectC = table.getSelectedColumn();

				if (table.getEditorComponent() instanceof JSpinner) {
					spinner = (JSpinner) table.getEditorComponent();
					if(selectC==6&&((Boolean)table.getValueAt(selectR, selectC-1)).booleanValue()){
						JTextField ff = ((JSpinner.NumberEditor) (spinner.getComponents()[2])).getTextField();
						String value = ff.getText();
						((DefaultEditor) spinner.getEditor()).getTextField().setText(value);
						for (char di : value.replace(",", "").toCharArray()) {
							if (!Character.isDigit(di)) {
								return;
							}
						}
						if ("".equals(value.replace(",", ""))) {
							newValue = 1;
						} else if (Long.parseLong(value.replace(",", "")) >= 255) {
							newValue = 255;
						} else if (Long.parseLong(value.replace(",", "")) <= 1) {
							newValue = 1;
						} else {
							newValue = Integer.parseInt(value.replace(",", ""));
						}
						if ( selectC == 6) {
							spinner.setModel(new SpinnerNumberModel(newValue, 1, 255, 1));
						} 
						spinner.commitEdit();
						if (table.isEditing()) {
							table.getCellEditor().stopCellEditing();
						}
					}else{
						table.setValueAt(0, selectR, selectC);
					}
				} 
			}
		} catch (Exception e) {
			((DefaultEditor) spinner.getEditor()).getTextField().setText(spinner.getValue() + "");
			ExceptionManage.dispose(e,this.getClass());
		}

	}
	
	// 保存信息用户信息
	private void saveUser() {
		
		NameRuleService_MB nameRuleService = null;
		int types=0;
		String beforeName = null;
		StringBuffer nameRole = new StringBuffer();// 命名规则
		StringBuffer nameExample = new StringBuffer();// 命名示例
		List<SetNameRule> setNameRules = null;
		if (this.SourceComboBox.getSelectedItem() == null) {
			DialogBoxUtil.confirmDialog(conPanel, ResourceUtil.srcStr(StringKeysLbl.LBL_NAMERULE_SELSOURCE));
			return;
		}

		try {
			nameRuleService = (NameRuleService_MB) ConstantUtil.serviceFactory.newService_MB(Services.NAMERULESERVICE);
			String source = this.SourceComboBox.getSelectedItem().toString();
			if(isUsedCheckBox.isSelected()){//验证同种类型只能一个生效
				if(nameRule.getId()==0 || (nameRule.getIsUsed() != (isUsedCheckBox.isSelected()?1:0))){
					SetNameRule setNameRule = new SetNameRule();
					setNameRule.setSourcename(source);
					setNameRule.setIsUsed(1);
					setNameRules = new ArrayList<SetNameRule>();
					setNameRules = nameRuleService.select(setNameRule);
					if(setNameRules.size()>0){
						DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.LBL_IS_USED_ONLY));
						return;
					}
				}else{
					
				}
			}
			if(table.getRowCount()==0){
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysLbl.TIP_SET_NAMERULE));
				return;
			}
			for (int j = 0; j < table.getRowCount(); j++) {
				String type = (String) table.getValueAt(j, 0);//字段类型
				String value = "";//取值
				if(type==null)
				{
					DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_SELNAMETYPE));
					return;
				}
				value = (String) table.getValueAt(j, 1);
				if(value==null)
				{
					DialogBoxUtil.errorDialog(this,ResourceUtil.srcStr(StringKeysTip.TIP_NAMERULE));
					return;
				}
				if (type.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CONNECT_ATTRIBUTE))) {
					nameRole.append(ResourceUtil.srcStr(StringKeysTip.TIP_CONNECT_ATTRIBUTE)+":" + value + " ");
					nameExample.append(value);
				} else if (type.equals(ResourceUtil.srcStr(StringKeysTip.TIP_STATIC_ATTRIBUTE))) {
					nameRole.append(ResourceUtil.srcStr(StringKeysTip.TIP_STATIC_ATTRIBUTE)+":" + value + " ");
					nameExample.append(value);
				} else if (type.equals(ResourceUtil.srcStr(StringKeysTip.TIP_CHANGE_ATTRIBUTE))) {
					nameRole.append(ResourceUtil.srcStr(StringKeysTip.TIP_CHANGE_ATTRIBUTE)+":" + value + " ");
					nameExample.append("XXXX");
				}
			}
			
			this.nameRule.setIsUsed(isUsedCheckBox.isSelected()?1:2);
			String name = this.nameruleField.getText().toString();
			// 验证名称是否存在
			if (null != nameRule) {
				beforeName = nameRule.getName();
			}
			VerifyNameUtil verifyNameUtil=new VerifyNameUtil();
			if (verifyNameUtil.verifyNameBySingle(EServiceType.RULENAME.getValue(), this.nameruleField.getText().trim(), beforeName, 0)) {
				DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_NAME_EXIST));
				return;
			}
			this.nameRule.setName(name);
			this.nameRule.setSourcename(source);
			this.nameRule.setNamerule(nameRole.toString().trim());
			this.nameRule.setNameexample(nameExample.toString());
			this.nameRule.setRowcount(table.getRowCount());
			if(nameRule.getId()>0){
				types=1;
			}
			nameRuleService.saveOrUpdate(nameRule);
			if(types==1){
				this.insertOpeLog(EOperationLogType.UPDATESETNAMERULE.getValue(), ResultString.CONFIG_SUCCESS, null, null);					
			}else{
				this.insertOpeLog(EOperationLogType.INSERTNAMERULE.getValue(), ResultString.CONFIG_SUCCESS, null, null);						
			}
			
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
			this.panel.getController().refresh();
			this.dispose();

		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally{
			UiUtil.closeService_MB(nameRuleService);
		}
	}
	private void insertOpeLog(int operationType, String result, Object oldMac, Object newMac){
		AddOperateLog.insertOperLog(confirm, operationType, result, oldMac, newMac, 0,ResourceUtil.srcStr(StringKeysTitle.TIT_SETNAMERULE),"");		
	}
	
}
