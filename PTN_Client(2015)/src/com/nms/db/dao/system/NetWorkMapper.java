package com.nms.db.dao.system;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nms.db.bean.system.NetWork;


public interface NetWorkMapper {
    int deleteByPrimaryKey(Integer networkid);

   

    int insertSelective(NetWork record);

    NetWork selectByPrimaryKey(Integer networkid);

    int updateByPrimaryKeySelective(NetWork record);

    int updateByPrimaryKey(NetWork record);
    
    public List<NetWork> select();

	List<NetWork> queryByUserIdField(@Param("isAll")int isAll, @Param("user_Id")int user_Id);
	
	/**
	 * 更新
	 * @param netWork
	 * @return
	 */
	public int update(NetWork netWork);
	
	public int insert(NetWork netWork);
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<NetWork> selectByNetWorkId(NetWork net);
	
	/**
	 * 更新
	 * @param netWork
	 * @return
	 */
	public int delete(NetWork netWork);
}