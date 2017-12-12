package com.nms.db.dao.system;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.nms.db.bean.system.LogManager;



public interface LogManagerMapper {
  

	public List<LogManager> selectAll();

	public int update(LogManager unload);

	public LogManager selectCount(@Param("label")Integer label);
	
	public LogManager selectCounts(@Param("label")Integer label);
	
	public int selectMaxId(@Param("label")Integer label);
}