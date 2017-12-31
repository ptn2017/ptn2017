package com.nms.ui.topology.routebusiness.view;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.path.ces.CesInfo;
import com.nms.db.bean.ptn.path.eth.DualInfo;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.eth.ElineInfo;
import com.nms.db.bean.ptn.path.eth.EtreeInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.report.SSProfess;
import com.nms.model.ptn.path.ces.CesInfoService_MB;
import com.nms.model.ptn.path.eth.DualInfoService_MB;
import com.nms.model.ptn.path.eth.ElanInfoService_MB;
import com.nms.model.ptn.path.eth.ElineInfoService_MB;
import com.nms.model.ptn.path.eth.EtreeInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.pw.BusinessNetworkTablePanel;

/**
 */
public class QueryServiceOnPwDialog extends PtnDialog {
	private static final long serialVersionUID = 3248974637851455262L;
	private PwInfo pw = null;
	private BusinessNetworkTablePanel networkTablePanel; //基本信息列表
	
	public QueryServiceOnPwDialog(PwInfo p) {
		try {
			this.pw = p;
			this.initComponent();
			this.initBusinessPanel();
			UiUtil.showWindow(this, 800, 300);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	private void initBusinessPanel() {
		ElineInfoService_MB elineInfoServiceMB = null;
		EtreeInfoService_MB etreeInfoServiceMB = null;
		ElanInfoService_MB elanInfoServiceMB = null;
		CesInfoService_MB cesInfoServiceMB = null;
		DualInfoService_MB dualInfoServiceMB = null;
		List<Integer> pwList = new ArrayList<Integer>();
		SSProfess ss=null;
		List<SSProfess> ssList = null;
		try {
			pwList.add(pw.getPwId());
			elineInfoServiceMB = (ElineInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.Eline);	
			etreeInfoServiceMB = (EtreeInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.EtreeInfo);			
			elanInfoServiceMB = (ElanInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.ElanInfo);	
			cesInfoServiceMB = (CesInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.CesInfo);			
			dualInfoServiceMB = (DualInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.DUALINFO);			
			List<ElineInfo> elineList = elineInfoServiceMB.selectElineByPwId(pwList);
			this.networkTablePanel.clear();
			if(elineList!=null && elineList.size()!=0){
				ss=new SSProfess();
				ss.setName(elineList.get(0).getName());
				ss.setServiceType(elineList.get(0).getServiceType());
				ss.setCreateTime(elineList.get(0).getCreateTime());
				ss.setActiveStatus(elineList.get(0).getActiveStatus());
				ss.setClientName(elineList.get(0).getClientName());
				ssList = new ArrayList<SSProfess>();				
				ssList.add(ss);
				this.networkTablePanel.initData(ssList);
			}else{
				List<EtreeInfo> etreeList = etreeInfoServiceMB.selectEtreeByPwId(pwList);
				if(etreeList!=null && etreeList.size()!=0){
					ss=new SSProfess();
					ss.setName(etreeList.get(0).getName());
					ss.setServiceType(etreeList.get(0).getServiceType());
					ss.setCreateTime(etreeList.get(0).getCreateTime());
					ss.setActiveStatus(etreeList.get(0).getActiveStatus());
					ss.setClientName(etreeList.get(0).getClientName());
					ssList = new ArrayList<SSProfess>();
					ssList.add(ss);
					this.networkTablePanel.initData(ssList);
				}else{
					List<ElanInfo> elanList = elanInfoServiceMB.selectElanbypwid(pwList);
					if(elanList!=null && elanList.size()!=0){
						ss=new SSProfess();
						ss.setName(elanList.get(0).getName());
						ss.setServiceType(elanList.get(0).getServiceType());
						ss.setCreateTime(elanList.get(0).getCreateTime());
						ss.setActiveStatus(elanList.get(0).getActiveStatus());
						ss.setClientName(elanList.get(0).getClientName());
						ssList = new ArrayList<SSProfess>();
						ssList.add(ss);
						this.networkTablePanel.initData(ssList);					
					}else{
						List<CesInfo> cesList = cesInfoServiceMB.selectCesByPwId(pwList);
						if(cesList!=null && cesList.size()!=0){
							ss=new SSProfess();
							ss.setName(cesList.get(0).getName());
							ss.setServiceType(cesList.get(0).getServiceType());
							ss.setCreateTime(cesList.get(0).getCreateTime());
							ss.setActiveStatus(cesList.get(0).getActiveStatus());
							ss.setClientName(cesList.get(0).getClientName());
							ssList = new ArrayList<SSProfess>();
							ssList.add(ss);
							this.networkTablePanel.initData(ssList);	
						}else{
							List<DualInfo> dualList = dualInfoServiceMB.queryByPwId(pw.getPwId());
							if(dualList!=null && dualList.size()!=0){
								ss=new SSProfess();
								ss.setName(dualList.get(0).getName());
								ss.setServiceType(dualList.get(0).getServiceType());
								ss.setCreateTime(dualList.get(0).getCreateTime());
								ss.setActiveStatus(dualList.get(0).getActiveStatus());
								ss.setClientName(dualList.get(0).getClientName());
								ssList = new ArrayList<SSProfess>();
								ssList.add(ss);
								this.networkTablePanel.initData(ssList);
							}else{
								ssList = new ArrayList<SSProfess>();
								this.networkTablePanel.initData(ssList);								
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(elineInfoServiceMB);
			UiUtil.closeService_MB(etreeInfoServiceMB);
			UiUtil.closeService_MB(elanInfoServiceMB);
			UiUtil.closeService_MB(cesInfoServiceMB);
			UiUtil.closeService_MB(dualInfoServiceMB);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_SERVICE_ON_PW));
		networkTablePanel = new BusinessNetworkTablePanel();
		this.add(networkTablePanel);
	}
}
