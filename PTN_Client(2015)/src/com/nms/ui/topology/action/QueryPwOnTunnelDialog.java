package com.nms.ui.topology.action;

import java.util.ArrayList;
import java.util.List;

import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.bean.ptn.path.tunnel.Tunnel;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.UiUtil;
import com.nms.ui.manager.control.PtnDialog;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.business.pw.PwNetworkTablePanel;

/**
 */
public class QueryPwOnTunnelDialog extends PtnDialog {
	private static final long serialVersionUID = 3248974637851455262L;
	private Tunnel tunnel = null;
	private PwNetworkTablePanel pwNetworkTablePanel; //基本信息列表
	
	public QueryPwOnTunnelDialog(Tunnel t) {
		try {
			this.tunnel = t;
			this.initComponent();
			this.initPwNetworkTablePanel();
			UiUtil.showWindow(this, 800, 300);
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initPwNetworkTablePanel() {
		PwInfoService_MB pwServiceMB = null;
		PwInfo pwInfo =null;
		try {
			pwInfo=new PwInfo();
			pwInfo.setTunnelId(tunnel.getTunnelId());
			pwServiceMB = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo);			
			List<PwInfo> pwList = new ArrayList<PwInfo>();
			pwList=(List<PwInfo>) pwServiceMB.selectFilter(pwInfo);
			if(pwList ==null){
				pwList = new ArrayList<PwInfo>();
			}
			this.pwNetworkTablePanel.clear();
			this.pwNetworkTablePanel.initData(pwList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			UiUtil.closeService_MB(pwServiceMB);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PW_ON_TUNNEL));
		pwNetworkTablePanel = new PwNetworkTablePanel();
		this.add(pwNetworkTablePanel);
	}
}
