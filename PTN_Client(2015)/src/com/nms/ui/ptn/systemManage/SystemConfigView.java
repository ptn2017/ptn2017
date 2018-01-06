package com.nms.ui.ptn.systemManage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nms.db.bean.system.SystemConfig;
import com.nms.ui.manager.CheckingUtil;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DialogBoxUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysBtn;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysTab;
import com.nms.ui.manager.keys.StringKeysTip;
import com.nms.ui.ptn.safety.roleManage.RootFactory;

public class SystemConfigView extends JPanel {
	private static final long serialVersionUID = 5673448152495549821L;
	private PtnButton OKButton;
//	private PtnButton syncButton;
	//private JButton cancelButton;
	private JLabel HuIdentifier;//主机唯一标识符
	private JTextField HuIdentifierText;
	private JLabel companyName;//系统名称
	private JTextField companyNameText;
	private JLabel describe;//系统描述
	private JTextField describeText;
	private JLabel companyIp;//IP地址
	private JTextField companyIpText;	
	private JLabel port;//端口号
	private JPasswordField portText;	
	private JLabel username;//用户名
	private JTextField usernameText;
	private JLabel password;//密码
	private JPasswordField passwordText;	
	private JLabel stauts;// 状态
	private JTextField statusText;	
	
	private JLabel PtnHuIdentifier;//主机唯一标识符
	private JTextField PtnHuIdentifierText;
	private JLabel ptnName;//系统名称
	private JTextField ptnNameText;
	private JLabel ptnDescribe;//系统描述
	private JTextField ptnDescribeText;
	private JLabel ptnIp;//IP地址
	private JTextField ptnIpText;	
	private JLabel ptnPort;//端口号
	private JPasswordField ptnPortText;	
	private JLabel ptnusername;//用户名
	private JTextField ptnusernameText;
	private JLabel ptnpassword;//密码
	private JPasswordField ptnpasswordText;	
	
	private JPanel buttonPanel = null;
//	private JPanel buttonPanel1 = null;
//	private JLabel lblTitle;
//	private JLabel lblTitle1;
	private JPanel titlePanel;
	private JPanel contentPanel;
	private JScrollPane jScrollPane;
	private SystemConfig system;
	private SystemConfig omcSystem;

	public SystemConfigView() {
		try {
			initComponents();
			setLayout();
			initData();
			addButtonListener();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void addButtonListener() {
		try {
			this.OKButton.addActionListener(new MyActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					saveData();
				}

				@Override
				public boolean checking() {
					return true;
				}
			});
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void initData() {
		this.readData();
		// 主机信息
		this.HuIdentifierText.setText(this.system.getSystemId());
		this.companyNameText.setText(this.system.getName());
		this.describeText.setText(this.system.getDescripe());
		this.companyIpText.setText(this.system.getIp());
		this.portText.setText(this.system.getPort() == 0 ? "":this.system.getPort()+"");
		this.usernameText.setText(this.system.getUserName());
		this.passwordText.setText(this.system.getPassWord());
		this.statusText.setText(this.system.getStatus());
		// OMC
		this.PtnHuIdentifierText.setText(this.omcSystem.getSystemId());
		this.ptnNameText.setText(this.omcSystem.getName());
		this.ptnDescribeText.setText(this.omcSystem.getDescripe());
		this.ptnIpText.setText(this.omcSystem.getIp());
		this.ptnPortText.setText(this.omcSystem.getPort() == 0 ? "":this.system.getPort()+"");
		this.ptnusernameText.setText(this.omcSystem.getUserName());
		this.ptnpasswordText.setText(this.omcSystem.getPassWord());
	}
	
	public void readData() {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		NodeList nodeList = null;
		Element element = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			// 使用DocumentBuilderFactory构建DocumentBulider
			builder = factory.newDocumentBuilder();
			// 使用DocumentBuilder的parse()方法解析文件
			doc = builder.parse(new File(System.getProperty("user.dir") + "/config/SystemConfig.xml"));
			this.system = new SystemConfig();
			this.omcSystem = new SystemConfig();
			nodeList = doc.getElementsByTagName("node");
			for (int i = 0; i < nodeList.getLength(); i++) {
				element = (Element) nodeList.item(i);
				if ("config".equals(element.getAttribute("type"))) {
					this.system.setSystemId(element.getElementsByTagName("systemId").item(0).getTextContent());
					this.system.setName(element.getElementsByTagName("name").item(0).getTextContent());
					this.system.setDescripe(element.getElementsByTagName("descripe").item(0).getTextContent());
					this.system.setIp(element.getElementsByTagName("ip").item(0).getTextContent());
					String port = element.getElementsByTagName("port").item(0).getTextContent();
					if(port != null && !"".equals(port)){
						this.system.setPort(Integer.parseInt(port));
					}
					this.system.setUserName(element.getElementsByTagName("userName").item(0).getTextContent());
					this.system.setPassWord(element.getElementsByTagName("passWord").item(0).getTextContent());
					this.system.setStatus(element.getElementsByTagName("status").item(0).getTextContent());
					this.omcSystem.setSystemId(element.getElementsByTagName("omcsystemId").item(0).getTextContent());
					this.omcSystem.setName(element.getElementsByTagName("omcname").item(0).getTextContent());
					this.omcSystem.setDescripe(element.getElementsByTagName("omcdescripe").item(0).getTextContent());
					this.omcSystem.setIp(element.getElementsByTagName("omcip").item(0).getTextContent());
					port = element.getElementsByTagName("omcport").item(0).getTextContent();
					if(port != null && !"".equals(port)){
						this.omcSystem.setPort(Integer.parseInt(port));
					}
					this.omcSystem.setUserName(element.getElementsByTagName("omcuserName").item(0).getTextContent());
					this.omcSystem.setPassWord(element.getElementsByTagName("omcpassWord").item(0).getTextContent());
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, LoginUtil.class);
		} finally {
			factory = null;
			builder = null;
			doc = null;
			nodeList = null;
			element = null;
		}
	}

	private void saveData() {
		if(!check()){
			return;
		}else{
			this.writeData();
			DialogBoxUtil.succeedDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_CONFIG_SUCCESS));
		}
	}
	
	public void writeData() {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		NodeList nodeList = null;
		Element element = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			// 使用DocumentBuilderFactory构建DocumentBulider
			builder = factory.newDocumentBuilder();
			// 使用DocumentBuilder的parse()方法解析文件
			doc = builder.parse(new File(System.getProperty("user.dir") + "/config/SystemConfig.xml"));
			// root = doc.getDocumentElement();
			nodeList = doc.getElementsByTagName("node");
			for (int i = 0; i < nodeList.getLength(); i++) {
				element = (Element) nodeList.item(i);
				if ("config".equals(element.getAttribute("type"))) {
					element.getElementsByTagName("systemId").item(0).setTextContent(this.HuIdentifierText.getText());
					element.getElementsByTagName("name").item(0).setTextContent(this.companyNameText.getText());
					element.getElementsByTagName("descripe").item(0).setTextContent(this.describeText.getText());
					element.getElementsByTagName("ip").item(0).setTextContent(this.companyIpText.getText());
					element.getElementsByTagName("port").item(0).setTextContent(String.valueOf(this.portText.getPassword()));
					element.getElementsByTagName("userName").item(0).setTextContent(this.usernameText.getText());
					element.getElementsByTagName("passWord").item(0).setTextContent(String.valueOf(this.passwordText.getPassword()));
					element.getElementsByTagName("status").item(0).setTextContent(this.statusText.getText());
					element.getElementsByTagName("omcsystemId").item(0).setTextContent(this.PtnHuIdentifierText.getText());
					element.getElementsByTagName("omcname").item(0).setTextContent(this.ptnNameText.getText());
					element.getElementsByTagName("omcdescripe").item(0).setTextContent(this.ptnDescribeText.getText());
					element.getElementsByTagName("omcip").item(0).setTextContent(this.ptnIpText.getText());
					element.getElementsByTagName("omcport").item(0).setTextContent(String.valueOf(this.ptnPortText.getPassword()));
					element.getElementsByTagName("omcuserName").item(0).setTextContent(this.ptnusernameText.getText());
					element.getElementsByTagName("omcpassWord").item(0).setTextContent(String.valueOf(this.ptnpasswordText.getPassword()));
				}
			}
			output(element);
		} catch (Exception e) {
			ExceptionManage.dispose(e, LoginUtil.class);
		} finally {
			factory = null;
			builder = null;
			doc = null;
			nodeList = null;
			element = null;
		}
	}

	private void output(Node node) {
		// 将node的XML字符串输出到控制台
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(node);
			StreamResult result = new StreamResult(System.getProperty("user.dir") + "/config/SystemConfig.xml");
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			ExceptionManage.dispose(e, this.getClass());
		} catch (TransformerException e) {
			ExceptionManage.dispose(e, this.getClass());
		}
	}
	
	private void initComponents() {
		try {
			titlePanel = new JPanel();
			titlePanel.setBorder(BorderFactory.createEtchedBorder());
			
			titlePanel.setSize(300, ConstantUtil.INT_WIDTH_THREE);
			contentPanel = new JPanel();
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(contentPanel);
			jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

			PtnHuIdentifier = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_OMC_IDENTIFER));
			PtnHuIdentifierText = new JTextField();
			ptnName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_NAME));			
			ptnNameText = new JTextField();			
			ptnDescribe = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DESCRIBE));
			ptnDescribeText = new JTextField();
			ptnIp = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_IP));
			ptnIpText = new JTextField();			
			ptnPort = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM));
			ptnPortText = new JPasswordField();			
			ptnusername = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_USERNAME));
			ptnusernameText = new JTextField();		
			ptnpassword = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_PASSWORD));
			ptnpasswordText = new JPasswordField();			
					
			HuIdentifier = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_IDENTIFER));
			HuIdentifierText = new JTextField();
			companyName = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_NAME));			
			companyNameText = new JTextField();			
			describe = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_DESCRIBE));
			describeText = new JTextField();
			companyIp = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_IP));
			companyIpText = new JTextField();			
			port = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_PORT_NUM));
			portText = new JPasswordField();			
			username = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_USERNAME));
			usernameText = new JTextField();		
			password = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_HOST_PASSWORD));
			passwordText = new JPasswordField();
			stauts = new JLabel(ResourceUtil.srcStr(StringKeysLbl.LBL_SYSTEM_STATUS));
			statusText = new JTextField();

//			syncButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true,RootFactory.CORE_MANAGE);
			OKButton = new PtnButton(ResourceUtil.srcStr(StringKeysBtn.BTN_CONFIRM),true,RootFactory.SATYMODU);
			buttonPanel = new JPanel();
//			buttonPanel1 = new JPanel();
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private void setLayout(){
		setTopGridBagLayout();
		setBottomGridBagLayout();
		setButtonLayout();
		GridBagConstraints c = null;
		c = new GridBagConstraints();
		GridBagLayout contentLayout = new GridBagLayout();
		this.setLayout(contentLayout);
		contentLayout.columnWeights = new double[] { 1.0 };
		contentLayout.rowHeights = new int[] { 300, 300, 80};
		contentLayout.rowWeights = new double[] { 0, 0, 0};
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(10, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;
		contentLayout.setConstraints(titlePanel, c);
		this.add(titlePanel);
		c.gridy = 1;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(jScrollPane, c);
		this.add(jScrollPane);
		c.gridy = 2;
		c.insets = new Insets(0, 0, 10, 0);
		contentLayout.setConstraints(buttonPanel, c);
		this.add(buttonPanel);
	}
	
	private void setTopGridBagLayout()  {
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints = new GridBagConstraints();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 200, 300, 200, 300 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 15, 15, 15 };
			gridBagLayout.rowWeights = new double[] { 0, 0, 0 };
			titlePanel.setLayout(gridBagLayout);
			titlePanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.OMC_PARAMETER_CONFIG)));
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(PtnHuIdentifier, gridBagConstraints);
			titlePanel.add(PtnHuIdentifier);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(PtnHuIdentifierText, gridBagConstraints);
			titlePanel.add(PtnHuIdentifierText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(ptnName, gridBagConstraints);
			titlePanel.add(ptnName);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(ptnNameText, gridBagConstraints);
			titlePanel.add(ptnNameText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ptnDescribe, gridBagConstraints);
			titlePanel.add(ptnDescribe);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ptnDescribeText, gridBagConstraints);
			titlePanel.add(ptnDescribeText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ptnIp, gridBagConstraints);
			titlePanel.add(ptnIp);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(ptnIpText, gridBagConstraints);
			titlePanel.add(ptnIpText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ptnPort, gridBagConstraints);
			titlePanel.add(ptnPort);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ptnPortText, gridBagConstraints);
			titlePanel.add(ptnPortText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ptnusername, gridBagConstraints);
			titlePanel.add(ptnusername);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(ptnusernameText, gridBagConstraints);
			titlePanel.add(ptnusernameText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(ptnpassword, gridBagConstraints);
			titlePanel.add(ptnpassword);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(ptnpasswordText, gridBagConstraints);
			titlePanel.add(ptnpasswordText);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void setBottomGridBagLayout()  {
		GridBagConstraints gridBagConstraints = null;
		try {
			gridBagConstraints = new GridBagConstraints();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 200, 300, 200, 300 };
			gridBagLayout.columnWeights = new double[] { 0, 0, 0, 0 };
			gridBagLayout.rowHeights = new int[] { 15, 15, 15 };
			gridBagLayout.rowWeights = new double[] { 0, 0, 0 };
			contentPanel.setLayout(gridBagLayout);
			contentPanel.setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTab.SYSTEM_PARAMETER_CONFIG)));
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.fill = GridBagConstraints.BOTH;

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(HuIdentifier, gridBagConstraints);
			contentPanel.add(HuIdentifier);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(HuIdentifierText, gridBagConstraints);
			contentPanel.add(HuIdentifierText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(companyName, gridBagConstraints);
			contentPanel.add(companyName);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(companyNameText, gridBagConstraints);
			contentPanel.add(companyNameText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(describe, gridBagConstraints);
			contentPanel.add(describe);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(describeText, gridBagConstraints);
			contentPanel.add(describeText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(companyIp, gridBagConstraints);
			contentPanel.add(companyIp);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 1;
			gridBagLayout.setConstraints(companyIpText, gridBagConstraints);
			contentPanel.add(companyIpText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(port, gridBagConstraints);
			contentPanel.add(port);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(portText, gridBagConstraints);
			contentPanel.add(portText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(username, gridBagConstraints);
			contentPanel.add(username);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 2;
			gridBagLayout.setConstraints(usernameText, gridBagConstraints);
			contentPanel.add(usernameText);
			
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(password, gridBagConstraints);
			contentPanel.add(password);
			
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(passwordText, gridBagConstraints);
			contentPanel.add(passwordText);
			
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(stauts, gridBagConstraints);
			contentPanel.add(stauts);
			
			gridBagConstraints.gridx = 3;
			gridBagConstraints.gridy = 3;
			gridBagLayout.setConstraints(statusText, gridBagConstraints);
			contentPanel.add(statusText);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	/**
	 * 按钮所在panel布局
	 */
	private void setButtonLayout() {
		GridBagConstraints gridBagConstraints = null;
		GridBagLayout gridBagLayout = null;
		try {
			gridBagLayout = new GridBagLayout();
			gridBagConstraints = new GridBagConstraints();
			gridBagLayout.columnWidths = new int[] { 20, 20 };
			gridBagLayout.columnWeights = new double[] { 1.5, 0 };
			gridBagLayout.rowHeights = new int[] { 21,50 };
			gridBagLayout.rowWeights = new double[] { 0 };

			gridBagConstraints.insets = new Insets(5, 5, 5, 0);
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagLayout.setConstraints(OKButton, gridBagConstraints);
			buttonPanel.add(OKButton);
//			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
//			gridBagConstraints.gridx = 3;
//			gridBagConstraints.gridy = 0;
//			gridBagLayout.setConstraints(cancelButton, gridBagConstraints);

//			buttonPanel.add(cancelButton);
			buttonPanel.setLayout(gridBagLayout);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}

	private boolean check(){
		if (this.companyIpText.getText() != null && !"".equals(this.companyIpText.getText()) && 
				!CheckingUtil.checking(this.companyIpText.getText(), CheckingUtil.IP_REGULAR)) { // 判断填写是否为ip格式
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_IPERROR_SEARCH));
			return false;
		}
		String port = String.valueOf(this.portText.getPassword());
		if (port != null && !"".equals(port) &&
				!CheckingUtil.checking(port, CheckingUtil.NUMBER_REGULAR)) { // 判断填写是否为端口格式
			
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.MESSAGE_VALUE_SCOPE));
			return false;
		}
		if (this.ptnIpText.getText() != null && !"".equals(this.ptnIpText.getText()) &&  
				!CheckingUtil.checking(this.ptnIpText.getText(), CheckingUtil.IP_REGULAR)) { // 判断填写是否为ip格式
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.TIP_IPERROR_SEARCH));
			return false;
		}
		port = String.valueOf(this.ptnPortText.getPassword());
		if (port != null && !"".equals(port) &&
				!CheckingUtil.checking(port, CheckingUtil.NUMBER_REGULAR)) { // 判断填写是否为端口格式
			DialogBoxUtil.errorDialog(this, ResourceUtil.srcStr(StringKeysTip.MESSAGE_VALUE_SCOPE));
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		Properties properties = System.getProperties(); 
		Iterator it = properties.entrySet().iterator(); 
		System.setProperty("os.name", "WinXp");
		System.setProperty("user.name", "WinXp");
		while(it.hasNext()) 
		{ 
		  Entry entry = (Entry)it.next(); 
		  System.out.print(entry.getKey()+"="); 
		  System.out.println(entry.getValue()); 
		} 

	}
}
