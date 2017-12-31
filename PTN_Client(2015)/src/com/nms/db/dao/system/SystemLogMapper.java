package com.nms.db.dao.system;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.system.SystemLog;



public interface SystemLogMapper {

    public int insert(SystemLog systemLog);
	public List<SystemLog> select(SystemLog systemLog);
	public int deleteByIds(@Param("idList")List<Integer> idList);
	public List<SystemLog> selectByIdList(@Param("idList")List<Integer> idList);

}