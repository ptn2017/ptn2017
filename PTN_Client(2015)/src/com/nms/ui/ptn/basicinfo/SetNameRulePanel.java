package com.nms.ui.ptn.basicinfo;



import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import com.nms.db.bean.path.SetNameRule;
import com.nms.ui.frame.ContentView;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTitle;
import com.nms.ui.ptn.basicinfo.controller.SetNameRuleController;
import com.nms.ui.ptn.safety.roleManage.RootFactory;
/**
 * 拓扑管理命名规则Panel
 * @author Administrator
 *
 */
public class SetNameRulePanel extends ContentView<SetNameRule>{

	private static final long serialVersionUID = -4401494086417559560L;
	public SetNameRulePanel(){
		super("SetNameRuleTable",RootFactory.DEPLOY_MANAGE);
		init();
		
	}
	//初始化
	public void init(){	
		try{			
			this.setViewLayout();			
			setLayout();
			this.getController().refresh();
		}catch(Exception e){
			ExceptionManage.dispose(e,this.getClass());
		}
			
	}
	
	private void setViewLayout(){
		getContentPanel().setBorder(BorderFactory.createTitledBorder(ResourceUtil.srcStr(StringKeysTitle.TIT_SETNAMERULE)));
	}
	
	@Override
	public void setTablePopupMenuFactory() {
		setMenuFactory(null);
	}
	@Override
	public Dimension setDefaultSize() {
		return new Dimension(160, ConstantUtil.INT_WIDTH_THREE);
	}
	
	@Override
	public void setController() {
		controller = new SetNameRuleController(this);
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
		
		needRemoveButtons.add(getFilterButton());
		needRemoveButtons.add(getFiterZero());
		needRemoveButtons.add(getSynchroButton());
		needRemoveButtons.add(getSearchButton());
		needRemoveButtons.add(getClearFilterButton());
		needRemoveButtons.add(getInportButton());
	    needRemoveButtons.add(getExportButton());
		return needRemoveButtons;
	}
	


}
