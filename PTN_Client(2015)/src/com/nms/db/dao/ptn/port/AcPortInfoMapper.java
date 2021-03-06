package com.nms.db.dao.ptn.port;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.ptn.port.AcPortInfo;

public interface AcPortInfoMapper {
    int deleteByPrimaryKey(Integer id);

    public int insert(AcPortInfo record);

    int insertSelective(AcPortInfo record);

    AcPortInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AcPortInfo record);

    int updateByPrimaryKey(AcPortInfo record);

	public List<AcPortInfo> queryByCondition(AcPortInfo acInfo);

	public void update(AcPortInfo acPortInfo);
	/**
	 * 通过acIdList批量查询
	 */
	public List<AcPortInfo> queryByAcIdCondition(@Param("acIds")List<Integer> idList);

	public List<Integer> selectByPortId(@Param("portId")int portId);

	int query_nameBySingle(@Param("afterName")String afterName, @Param("beforeName")String beforeName, @Param("siteId")int siteId);

	void setUser(int getaAcId, int i);
	
	List<AcPortInfo> selectByCondition_synchro(AcPortInfo acPortInfo);
	
	void deleteIds(@Param("acIdList")List<Integer> acIdList);
}