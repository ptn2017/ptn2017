package com.nms.db.dao.perform;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.perform.Capability;
import com.nms.db.bean.perform.HisPerformanceInfo;



public interface HisPerformanceInfoMapper {
    

    int insert(HisPerformanceInfo record);

    int insertSelective(HisPerformanceInfo record);

    HisPerformanceInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HisPerformanceInfo record);

    int updateByPrimaryKey(HisPerformanceInfo record);
    
    public int selectHisPerformanceCount();
    public int deleteById(Integer id);
    public int deleteByIds(@Param("list")List<Integer> ids);
    
    /**
     * 搜索时，更新性能
     * @param newTunnelId
     * @param tid
     * @param i
     */
	void updateObjectid(Map<String, Object> map);

	List<HisPerformanceInfo> queryByCondition(HisPerformanceInfo condition);

	List<HisPerformanceInfo> queryByConditionTime(String startTime, String endTime);

	int selectCount(Map<String, Object> map);

	List<HisPerformanceInfo> selectByPage(Map<String, Object> map);
		
	public List<HisPerformanceInfo> queryByHisPerfromance(@Param("hisInfo")HisPerformanceInfo hisInfo,@Param("code")int code,@Param("capabilityCode")int capabilityCode,@Param("stime")String stime,@Param("etime")String etime);

	public int queryIdByGoPage(Map<String, Object> map);

	List<HisPerformanceInfo> selectAll();
}