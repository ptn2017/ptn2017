﻿package com.nms.ui.ptn.systemconfig.dialog.siteInitialise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import twaver.table.TTable;
import twaver.table.TTablePopupMenuFactory;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.util.Services;
import com.nms.rmi.ui.util.RmiKeys;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DispatchUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.MyActionListener;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnButton;
import com.nms.ui.manager.keys.StringKeysLbl;
import com.nms.ui.manager.keys.StringKeysMenu;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
import com.nms.ui.ptn.systemconfig.dialog.siteInitialise.controller.SiteInitialiseController;

public class SiteInitialiseConfig  extends ContentView<SiteInst>{
	
	private PtnButton clearButton;//初始化按钮
	private JMenuItem queryLocationSn;//查询本地网元SN
	private JMenuItem queryAdjoinSn;//查询相邻网元
	private JMenuItem setLocationIP;//设置网元ip
	private JMenuItem setremoteIP;//设置远程IP
	private SiteInst siteInst;
	private PtnButton queryAllSnPtnButton;//查询所有网元SN
	private SiteInitialiseController siteInitialiseController;
	private JMenuItem setSN;//设置本网元SN
	private JMenuItem setremoteSN;//设置远程SN
	private JMenuItem setlocationNe;//设置为本地网元
	private SiteInst locationNe;//本地网元
	private JMenuItem querySN;//查询本网元SN
	private JMenuItem queryNeighbour;//查询邻居信息
	private JMenuItem replaceSite;//网元替换
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6254345184245374886L;

	public SiteInitialiseConfig() throws Exception {
		super("snSiteInstTable",RootFactory.DEPLOY_MANAGE);
		init();
	}
	private void init() throws Exception {
		queryLocationSn = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QUERY_LOCATION_SN));
		queryAdjoinSn = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QUERY_ADJOIN_SN));
		setLocationIP = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_IP));
		setremoteIP = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_REMOTE_IP));
		setSN = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_LOCAL_SN));
		setremoteSN = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_REMOTE_SN));
		setlocationNe = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_SET_TO_LOCAL_NE));
		querySN = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QUERY_REMOTE_SN));
		queryNeighbour = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_QUERY_REMOTE_SN));
		replaceSite = new JMenuItem(ResourceUtil.srcStr(StringKeysMenu.MENU_REPLACE_SITE));
		setLayout();
		try {
			if(getController() instanceof SiteInitialiseController){
				siteInitialiseController = (SiteInitialiseController) getController();
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		addActionListention();
	}
	
	private void addActionListention() {
		//查询本地网元SN
		queryLocationSn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.queryLocationSnActionPerformed(siteInst);
			}
		});
		//查询相邻网元SN
		queryAdjoinSn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.queryAdjoinSnActionPerformed(siteInst);
			}
		});
		//查询远程网元SN
		querySN.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInitialiseController.queryRemoteSn(getSelect());
			}
		});
		//设置本地网元IP
		setLocationIP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.setIPorSnActionPerformed(siteInst,1);
			}
		});
		//设置远程网元IP
		setremoteIP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.setIPorSnActionPerformed(siteInst,2);
			}
		});
		//设置本地网元SN
		setSN.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.setIPorSnActionPerformed(siteInst,3);
			}
		});
		//设置远程网元SN
		setremoteSN.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.setIPorSnActionPerformed(siteInst,4);
			}
		});
		//设置为本地网元
		setlocationNe.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(locationNe != null){
					locationNe.setLocationNe(false);
				}
				locationNe =  getSelect();
				locationNe.setLocationNe(true);
				SiteService_MB siteServiceMB = null;
				try {
					siteServiceMB = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE);
					locationNe.setRootIP("");
					siteServiceMB.updateSite(locationNe);
				} catch (Exception e2) {
					ExceptionManage.dispose(e2, SiteInitialiseConfig.class);
				}finally{
					UiUtil.closeService_MB(siteServiceMB);
				}
				siteInitialiseController.refresh();
			}
		});
		
		queryNeighbour.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		replaceSite.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				siteInst = getSelect();
				siteInitialiseController.replaceSite(siteInst);
			}
		});
	}
	
	public void setLayout(){
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
		List<JButton> needButtons = new ArrayList<JButton>();
		needButtons.add(getAddButton());
		needButtons.add(getSearchButton());
		needButtons.add(getDeleteButton());
		needButtons.add(getSynchroButton());
		needButtons.add(getUpdateButton());
		return needButtons;
	}
	
	@Override
	public List<JButton> setAddButtons() {
		
	this.clearButton = new PtnButton(ResourceUtil.srcStr(StringKeysLbl.LBL_RMI_INIT),true,RootFactory.DEPLOY_MANAGE);
	this.clearButton.addActionListener(new MyActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			siteInitialiseController.clearSiteActionPerformed();
		}
		@Override
		public boolean checking() {
			return true;
		}
	});
	
	//查询所有网元SN
	this.queryAllSnPtnButton = new PtnButton(ResourceUtil.srcStr(StringKeysLbl.LBL_QUERY_ALLSN),true,RootFactory.DEPLOY_MANAGE);
	queryAllSnPtnButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			List<SiteInst> siteInsts = getAllSelect();
			try {
				DispatchUtil dispatchUtil = new DispatchUtil(RmiKeys.RMI_SITE);
				for(SiteInst siteInst : siteInsts){
					dispatchUtil.querySn(siteInst, 1);
				}
			} catch (Exception e1) {
				ExceptionManage.dispose(e1,this.getClass());
			}
		}
	});
	List<JButton> needButtons = new ArrayList<JButton>();
	needButtons.add(this.clearButton);
//	needButtons.add(this.queryAllSnPtnButton);
	return needButtons;
	}
	
	@Override
	public void setTablePopupMenuFactory() {
		TTablePopupMenuFactory menuFactory = new TTablePopupMenuFactory() {
			@Override
			public JPopupMenu getPopupMenu(TTable table, MouseEvent evt) {
				if (SwingUtilities.isRightMouseButton(evt)) {
					int count = table.getSelectedRows().length;
					if (count == 1) {
						if(getController() instanceof SiteInitialiseController){
							JPopupMenu menu = new JPopupMenu();
							menu.add(setlocationNe);
							if(locationNe != null && locationNe.getSite_Inst_Id() == getSelect().getSite_Inst_Id()){
								menu.add(queryLocationSn);
//								menu.add(setLocationIP);
//								menu.add(setSN);
							}else if(locationNe != null && locationNe.getSite_Inst_Id() != getSelect().getSite_Inst_Id()){
//								menu.add(setremoteIP);
//								menu.add(setremoteSN);
//								menu.add(querySN);
							}
							menu.add(replaceSite);
							menu.add(queryAdjoinSn);
							menu.show(evt.getComponent(), evt.getX(), evt.getY());
							return menu;
						}
					}
				}
				return null;
			}
		};
		super.setMenuFactory(menuFactory);
	}
	
	@Override
	public void setController() {
		super.controller = new SiteInitialiseController(this);
	}

	public PtnButton getClearButton() {
		return clearButton;
	}

	public void setClearButton(PtnButton clearButton) {
		this.clearButton = clearButton;
	}
	public SiteInst getLocationNe() {
		return locationNe;
	}
	public void setLocationNe(SiteInst locationNe) {
		this.locationNe = locationNe;
	}
	
}
