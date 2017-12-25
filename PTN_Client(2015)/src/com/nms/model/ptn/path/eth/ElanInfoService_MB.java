package com.nms.model.ptn.path.eth;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.equipment.shelf.SiteInst;
import com.nms.db.bean.ptn.path.eth.ElanInfo;
import com.nms.db.bean.ptn.path.pw.PwInfo;
import com.nms.db.dao.ptn.path.eth.ElanInfoMapper;
import com.nms.model.equipment.shlef.SiteService_MB;
import com.nms.model.ptn.path.pw.PwInfoService_MB;
import com.nms.model.ptn.port.AcPortInfoService_MB;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.model.util.Services;
import com.nms.ui.manager.ConstantUtil;
import com.nms.ui.manager.DateUtil;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.UiUtil;

public class ElanInfoService_MB extends ObjectService_Mybatis{
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	private ElanInfoMapper mapper;

	public ElanInfoMapper getElanInfoMapper() {
		return mapper;
	}

	public void setElanInfoMapper(ElanInfoMapper elanInfoMapper) {
		this.mapper = elanInfoMapper;
	}

	public Map<Integer, List<ElanInfo>> filterSelect(ElanInfo elanInfo) {
		Map<Integer, List<ElanInfo>> elanInfoMap = new HashMap<Integer, List<ElanInfo>>();
		List<ElanInfo> elanServiceList = null;
		AcPortInfoService_MB acService = null;
		List<Integer> acIds = null;
		try {
			elanServiceList = this.mapper.filterSelect(elanInfo);
			this.convertTime(elanServiceList);
			acService = (AcPortInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.AcInfo, this.sqlSession);
			if(elanInfo.getAportId() >0)
			{
				acIds = acService.acByPort(elanInfo.getAportId(), elanInfo.getaSiteId());
			}
			for (ElanInfo elaninfor : elanServiceList) {
				//存在通过端口查询
				if(acIds != null)
				{
					if(!acIds.isEmpty())
					{
						if(acByFilter(acIds,elaninfor.getAmostAcId()) || acByFilter(acIds,elaninfor.getZmostAcId()))
						{
							etreeByFilter(elaninfor,elanInfoMap,elanServiceList);
						}	
					}
				}else
				{
					etreeByFilter(elaninfor,elanInfoMap,elanServiceList);
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return elanInfoMap;
	}
	
	private void etreeByFilter(ElanInfo elaninfor,Map<Integer, List<ElanInfo>> elanInfoMap,List<ElanInfo> elanServiceList)
	{
		int serviceId = elaninfor.getServiceId();
		if (elanInfoMap.get(serviceId) == null) {
			List<ElanInfo> elanInfoList = new ArrayList<ElanInfo>();
			for (ElanInfo elan : elanServiceList) {
				if (elan.getServiceId() == serviceId) {
					elanInfoList.add(elan);
				}
			}
			elanInfoMap.put(serviceId, elanInfoList);
		}
	}
	
	/**
	 * 查询a/Z端是否满足查询条�?	 * @param acIds
	 * @param etreeInfo_result
	 * @return
	 */
	private boolean acByFilter(List<Integer> acIds,String mostAcId)
	{
	 Set<Integer> acList = null;
	 UiUtil uiutil  = null;
		try {
			uiutil = new UiUtil();
			acList = uiutil.getAcIdSets(mostAcId);
			for(Integer acId : acList)
			{
				if(acIds.contains(acId))
				{
					return true;
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, getClass());
		}
		return false;
	}

	public List<ElanInfo> select(int elanId) {
		List<ElanInfo> elaninfoList = null;
		try {
			elaninfoList = this.mapper.selectById(elanId);
			this.convertTime(elaninfoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return elaninfoList;
	}
	
	private void convertTime(List<ElanInfo> elaninfoList) throws ParseException{
		if(elaninfoList != null && elaninfoList.size() > 0){
			for (ElanInfo elanInfo : elaninfoList) {
				elanInfo.setCreateTime(DateUtil.strDate(elanInfo.getCreateTime(), DateUtil.FULLTIME));
				elanInfo.setActivatingTime(DateUtil.strDate(elanInfo.getActivatingTime(), DateUtil.FULLTIME));
			}
		}
	}
	
	public List<ElanInfo> select(ElanInfo elaninfo) throws Exception {
		List<ElanInfo> infoList = null;
		try {
			infoList = this.mapper.selectById(elaninfo.getServiceId());
			this.convertTime(infoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return infoList;
	}

	public List<ElanInfo> selectByServiceId(int serviceId) throws ParseException {
		List<ElanInfo> elanInfos = this.mapper.selectById(serviceId);
		this.convertTime(elanInfos);
		return elanInfos;
	}

	public void search(List<SiteInst> siteInstList) throws Exception {
		List<Integer> siteIdList = null;
		Map<Integer, List<ElanInfo>> map = null;
		Map<Integer, List<ElanInfo>> map_create = null; // 要创建的map
		int createKey = 1; // 创建的map中key标识
		SiteService_MB siteService = null;
		try {
			siteService = (SiteService_MB) ConstantUtil.serviceFactory.newService_MB(Services.SITE, this.sqlSession);
			siteIdList = siteService.getSiteIdList(siteInstList);
			map_create = new HashMap<Integer, List<ElanInfo>>();
			for (SiteInst siteInst : siteInstList) {
				// 查出此网元所有elan信息
				map = this.getMapBySite(siteInst.getSite_Inst_Id());
				// 遍历每一组elan 找出与此elan相匹配的elan
				for (int serviceId : map.keySet()) {
					// 如果此serviceId不在要创建的map中，才去验证。防止重�?//					
					if(map.get(serviceId).size()>1){
						if (!this.serviceIdIsExist(map_create, serviceId)) {
							this.putCreateMap(map_create, createKey, map.get(serviceId), siteIdList);
							createKey++;
						}
					}
				}
			}
			// 如果map中有值，就循环创建每一组elan�?			
			if (map_create.keySet().size() > 0) {
				for (int key : map_create.keySet()) {
					this.createElan(map_create.get(key));
				}
			}
			this.sqlSession.commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 验证serviceid在map中是否存在�?	 * @param map_create 要创建的map集合
	 * @param serviceId 要验证的serviceid
	 * @return
	 * @throws Exception
	 */
	private boolean serviceIdIsExist(Map<Integer, List<ElanInfo>> map_create, int serviceId) throws Exception {
		boolean flag = false;
		try {
			for (int key : map_create.keySet()) {
				for (ElanInfo elanInfo : map_create.get(key)) {
					if (elanInfo.getServiceId() == serviceId) {
						flag = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 根据网元ID查出所有此单网元下的所有elan
	 * @param siteId 网元ID
	 * @return key为serivceId 一组elan 为一条map
	 * @throws Exception
	 */
	private Map<Integer, List<ElanInfo>> getMapBySite(int siteId) throws Exception {
		List<ElanInfo> elanInfoList = null;
		Map<Integer, List<ElanInfo>> map_result = null;
		List<ElanInfo> elanInfoList_value = null;
		try {
			ElanInfo condition = new ElanInfo();
			condition.setSiteId(siteId);
			condition.setIsSingle(1);
			elanInfoList = this.mapper.queryNodeBySite(condition);
			this.convertTime(elanInfoList);
			map_result = new HashMap<Integer, List<ElanInfo>>();
			for (ElanInfo elanInfo : elanInfoList) {
				if (null == map_result.get(elanInfo.getServiceId())) {
					elanInfoList_value = new ArrayList<ElanInfo>();
					elanInfoList_value.add(elanInfo);
					map_result.put(elanInfo.getServiceId(), elanInfoList_value);
				} else {
					map_result.get(elanInfo.getServiceId()).add(elanInfo);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return map_result;
	}
	
	/**
	 * 根据elanInfoList，查询出对应的elan，验证叶是否在siteids集合中，如果在，把此组elan放到map�?	 * @param map_create 创建的map集合
	 * @param key 创建的map中key
	 * @param elanInfoList 要匹配的elan集合
	 * @param siteIds 网元集合
	 * @throws Exception
	 */
	private void putCreateMap(Map<Integer, List<ElanInfo>> map_create, int key, List<ElanInfo> elanInfoList, List<Integer> siteIds) throws Exception {
		List<ElanInfo> elanList = null;
		boolean flag = true;
		try {
			elanList = this.selectElanbypwid(this.getPwIds(elanInfoList));
			// 遍历查询出的elan集合
			for (ElanInfo elanInfo : elanList) {
				// 如果是此elan不在要匹配的elan集合中，并且 网元id不存在网元集合中，则不匹配此elan
				if (!this.elanInfoIsExist(elanInfo, elanInfoList, "id")) {
					if (!siteIds.contains(elanInfo.getaSiteId())) {
						flag = false;
						break;
					} else {
						elanInfoList.addAll(this.selectByServiceId(elanInfo.getServiceId()));
					}
				}
			}
			ArrayList<ElanInfo> elanInfoLitst_create = new ArrayList<ElanInfo>();
			for (ElanInfo elanInfo_element : elanInfoList) {
				if (!this.elanInfoIsExist(elanInfo_element, elanInfoLitst_create, "pwid")) {
					ElanInfo elanInfo = this.getElanByPw(elanInfo_element, elanInfoList);
					if (null != elanInfo) {
						elanInfoLitst_create.add(this.combination(elanInfo_element, elanInfo));
					} else {
						return;
					}
				}
			}
			// 如果通过验证，可以合并，把此组elan数据放到map中，统一做合�?			
			if (flag) {
				map_create.put(key, elanInfoList);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<ElanInfo> selectElanbypwid(List<Integer> idList) throws Exception {
		List<ElanInfo> infoList = null;
		try {
			infoList = this.mapper.queryByPwId(idList);
			this.convertTime(infoList);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return infoList;
	}
	
	/**
	 * 从集合中找出跟elanInfo相同pw的elan
	 * @param elanInfo
	 * @param elanInfoList
	 * @return
	 * @throws Exception
	 */
	private ElanInfo getElanByPw(ElanInfo elanInfo, List<ElanInfo> elanInfoList) throws Exception {
		ElanInfo elanInfo_result = null;
		try {
			for (ElanInfo elanInfo_element : elanInfoList) {
				if (elanInfo.getId() != elanInfo_element.getId() && elanInfo.getPwId() == elanInfo_element.getPwId()) {
					elanInfo_result = elanInfo_element;
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return elanInfo_result;
	}

	/**
	 * 组合两个elan为一条elan对象
	 * @param elanInfo_a a对象
	 * @param elanInfo_z z对象
	 * @return
	 * @throws Exception
	 */
	private ElanInfo combination(ElanInfo elanInfo_a, ElanInfo elanInfo_z) throws Exception {
		ElanInfo elanInfo = new ElanInfo();
		PwInfoService_MB pwInfoService = null;
		try {
			pwInfoService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo, this.sqlSession);
			PwInfo pwInfo = pwInfoService.selectByPwId(elanInfo_a.getPwId());
			if(pwInfo != null){
				elanInfo.setServiceType(elanInfo_a.getServiceType());
				elanInfo.setActiveStatus(elanInfo_a.getActiveStatus());
				elanInfo.setPwId(elanInfo_a.getPwId());
				elanInfo.setCreateUser(elanInfo_z.getCreateUser());
				elanInfo.setCreateTime(DateUtil.getDate(DateUtil.FULLTIME));
				elanInfo.setJobStatus(elanInfo_a.getJobStatus());
				if(pwInfo.getASiteId() == elanInfo_a.getaSiteId()){
					elanInfo.setAxcId(elanInfo_a.getAxcId());
					elanInfo.setZxcId(elanInfo_z.getAxcId());
					elanInfo.setaSiteId(elanInfo_a.getaSiteId());
					elanInfo.setzSiteId(elanInfo_z.getaSiteId());
					elanInfo.setaAcId(elanInfo_a.getaAcId());
					elanInfo.setAmostAcId(elanInfo_a.getAmostAcId());
					elanInfo.setzAcId(elanInfo_z.getaAcId());
					elanInfo.setZmostAcId(elanInfo_z.getAmostAcId());
				}else if(pwInfo.getASiteId() == elanInfo_z.getaSiteId()){
					elanInfo.setAxcId(elanInfo_z.getAxcId());
					elanInfo.setZxcId(elanInfo_a.getAxcId());
					elanInfo.setaSiteId(elanInfo_z.getaSiteId());
					elanInfo.setzSiteId(elanInfo_a.getaSiteId());
					elanInfo.setaAcId(elanInfo_z.getaAcId());
					elanInfo.setAmostAcId(elanInfo_z.getAmostAcId());
					elanInfo.setzAcId(elanInfo_a.getaAcId());
					elanInfo.setZmostAcId(elanInfo_a.getAmostAcId());
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return elanInfo;
	}

	/**
	 * 验证一个elan是否在集合中存在
	 * @param elanInfo
	 * @param elanInfoList
	 * @param type id=比较ID，pwid=比较pwid
	 * @return
	 * @throws Exception
	 */
	private boolean elanInfoIsExist(ElanInfo elanInfo, List<ElanInfo> elanInfoList, String type) throws Exception {
		boolean flag = false;
		try {
			for (ElanInfo elanInfo_element : elanInfoList) {
				if ("id".equals(type)) {
					if (elanInfo.getId() == elanInfo_element.getId()) {
						flag = true;
						break;
					}
				} else if ("pwid".equals(type)) {
					if (elanInfo.getPwId() == elanInfo_element.getPwId()) {
						flag = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 搜索后，组合并且创建elan
	 * @param elanInfoLitst
	 * @throws Exception
	 */
	private void createElan(List<ElanInfo> elanInfoLitst) throws Exception {
		List<ElanInfo> elanInfoLitst_create = null;
		int serviceId = 1;
		String elanName = null;
		ElanInfo elanInfo = null;
		PwInfoService_MB pwService = null;
		try {
			elanInfoLitst_create = new ArrayList<ElanInfo>();
			for (ElanInfo elanInfo_element : elanInfoLitst) {
				if (!this.elanInfoIsExist(elanInfo_element, elanInfoLitst_create, "pwid")) {
					elanInfo = this.getElanByPw(elanInfo_element, elanInfoLitst);
					if (null != elanInfo) {
						elanInfoLitst_create.add(this.combination(elanInfo_element, elanInfo));
					} else {
						return;
					}
				}
			}
			if (elanInfoLitst_create.size() > 0) {
				pwService = (PwInfoService_MB) ConstantUtil.serviceFactory.newService_MB(Services.PwInfo, this.sqlSession);
				// 取serviceid
				Integer maxId = this.mapper.selectMaxServiceId();
				if(maxId != null){
					serviceId = maxId + 1;
				}
				// 设置elan名称
				elanName = "elan_" + new Date().getTime();
				for (ElanInfo elanInfo_element : elanInfoLitst_create) {
					elanInfo_element.setServiceId(serviceId);
					elanInfo_element.setName(elanName);
					// 插入
					this.mapper.insert(elanInfo_element);
//					int elanId = this.mapper.queryByName(elanName);
//					elanInfo_element.setId(elanId);
					// 修改pw关联
					pwService.setUser(elanInfo_element.getPwId(), elanInfo_element);
				}
				// 删除原有的数�?				
				for (ElanInfo elanInfo_element : elanInfoLitst) {
					this.mapper.delete(elanInfo_element.getServiceId());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取elan中的所有pwid
	 * @param elanInfoList elan集合
	 * @return
	 * @throws Exception
	 */
	private List<Integer> getPwIds(List<ElanInfo> elanInfoList) throws Exception {
		List<Integer> pwIds = null;
		try {
			pwIds = new ArrayList<Integer>();
			for (ElanInfo elanInfo : elanInfoList) {
				pwIds.add(elanInfo.getPwId());
			}
		} catch (Exception e) {
			throw e;
		}
		return pwIds;
	}

	public Map<Integer, List<ElanInfo>> selectBySiteId(int siteId) throws Exception {
		return this.convertListToMap(this.mapper.selectBySiteId(siteId));
	}

	/**
	 * 把所有etree按组转为map 
	 * @param etreeInfoList
	 * @return key为组id  value为此组下的etree对象
	 * @throws Exception
	 */
	private Map<Integer, List<ElanInfo>> convertListToMap(List<ElanInfo> elanInfoList) throws Exception{
		Map<Integer, List<ElanInfo>> elanInfoMap = new HashMap<Integer, List<ElanInfo>>();
		List<ElanInfo> elanInfoList_map =null;
		try {
			this.convertTime(elanInfoList);
			if(null!=elanInfoList && elanInfoList.size()>0){
				for (ElanInfo elanInfo : elanInfoList) {
					int serviceId = elanInfo.getServiceId();
					if (elanInfoMap.get(serviceId) == null) {
						elanInfoList_map = new ArrayList<ElanInfo>();
						elanInfoMap.put(serviceId, elanInfoList_map);
					}
					elanInfoMap.get(serviceId).add(elanInfo);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return elanInfoMap;
	}
	
	public List<ElanInfo> selectElan(ElanInfo elaninfo) throws Exception {
		List<ElanInfo> infoList = null;
		try {
			infoList = new ArrayList<ElanInfo>();
			infoList = this.mapper.queryElan(elaninfo);
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return infoList;
	}
	
	public Map<String, List<ElanInfo>> selectSiteNodeBy(int siteid) throws Exception {

		Map<String, List<ElanInfo>> map = new HashMap<String, List<ElanInfo>>();
		List<ElanInfo> infoList = null;
		try {
			ElanInfo elanInfo = new ElanInfo();
			elanInfo.setSiteId(siteid);
			elanInfo.setIsSingle(0);
			infoList = new ArrayList<ElanInfo>();
			infoList = this.mapper.queryNodeBySite(elanInfo);
			int i, j;
			if(infoList!=null && infoList.size()>0){
				for (i = 0; i < infoList.size(); i++) {
					List<ElanInfo> temp = null;
					if (map.get(infoList.get(i).getServiceId() + "/" + infoList.get(i).getServiceType()) == null) {
						temp = new ArrayList<ElanInfo>();
					} else {
						continue;
					}
					for (j = i; j < infoList.size(); j++) {
						if (infoList.get(i).getServiceId() == infoList.get(j).getServiceId()) {
							temp.add(infoList.get(j));
						}
					}
					map.put(infoList.get(i).getServiceId() + "/" + infoList.get(i).getServiceType(), temp);
				}
		    }
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return map;

	}

	/**
	 * 验证名字是否重复
	 * @author kk
	 * @param afterName
	 *            修改之后的名�?	 * @param beforeName
	 *            修改之前的名�?	 * @return
	 * @throws Exception
	 * @Exception 异常对象
	 */
	public boolean nameRepetition(String afterName, String beforeName) throws Exception {
		int result = this.mapper.query_name(afterName, beforeName);
		if (0 == result) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 单网元名称验�?	 * @param afterName
	 * @param beforeName
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public boolean nameRepetitionBySingle(String afterName, String beforeName, int siteId) throws Exception {
		int result = this.mapper.query_nameBySingle(afterName, beforeName, siteId);
		if (0 == result) {
			return false;
		} else {
			return true;
		}

	}
	
	/*
	 * 查询所有的elan业务(每一条可能包含多条业�?
	 */
	public Map<Integer, List<ElanInfo>> select() throws Exception {
		Map<Integer, List<ElanInfo>> elanInfoMap = new HashMap<Integer, List<ElanInfo>>();
		List<ElanInfo> elanServiceList = null;
		try {
			elanServiceList = new ArrayList<ElanInfo>();			
			elanServiceList = this.mapper.queryAll();
			if(elanServiceList!=null && elanServiceList.size()>0){
				for (ElanInfo etreeInfo : elanServiceList) {
					int serviceId = etreeInfo.getServiceId();
					if (elanInfoMap.get(serviceId) == null) {
						List<ElanInfo> elanInfoList = new ArrayList<ElanInfo>();
						for (ElanInfo elan : elanServiceList) {
							if (elan.getServiceId() == serviceId) {
								elanInfoList.add(elan);
							}
						}
						elanInfoMap.put(serviceId, elanInfoList);
					}	
				}
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return elanInfoMap;
	}
	
	/**
	 * 根据网元查询此网元下所有单网元eline业务
	 * @param siteId 网元主键
	 * @return 
	 * @throws Exception
	 */
	public Map<Integer, List<ElanInfo>> selectBySite_node(int siteId) throws Exception{
		
		return this.convertListToMap(this.mapper.selectBySiteAndisSingle(siteId, 1));
	}
	
	/**
	 * 根据网元查询此网元下所有网络eline业务
	 * @param siteId 网元主键
	 * @return 
	 * @throws Exception
	 */
	public Map<Integer, List<ElanInfo>> selectBySite_network(int siteId) throws Exception{
		return this.convertListToMap(this.mapper.selectBySiteAndisSingle(siteId, 0));
	}
	
	/**
	 * 查询网元下的所有vpls
	 * @param siteId
	 * @return
	 */
	public List<ElanInfo> selectVpls(int siteId){
		return mapper.selectVpls(siteId);
	}
}
