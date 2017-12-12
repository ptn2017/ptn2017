package com.nms.model.system;

import java.util.List;
import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.system.LogManager;
import com.nms.db.bean.system.UnLoading;
import com.nms.db.dao.system.LogManagerMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;


public class LogManagerService_MB extends ObjectService_Mybatis {
    private LogManagerMapper mapper = null;
	
	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;		
	}
	
	public LogManagerMapper getMapper() {
		return mapper;
	}

	public void setMapper(LogManagerMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * 查询全部code
	 * @return code 集合
	 * @throws Exception
	 */
	public List<LogManager> selectAll() throws Exception {
		List<LogManager> logList = null;
		try {
			logList = this.mapper.selectAll();			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return logList;
	}
	

	
	/**
	 * 新增或修改code对象，通过code.getId()来判断是修改还是新增
	 * 
	 * @param code
	 *            实体
	 * @return 执行成功的记录数
	 * @throws Exception
	 */
	public int update(LogManager unload) throws Exception {

		if (unload == null) {
			throw new Exception("code is null");
		}

		int result = 0;
		try {			
			result = this.mapper.update(unload);			
           this.sqlSession.commit();
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		}
		return result;
	}
	
	public LogManager selectCount(int a)throws Exception {
		LogManager log=null;		
		try {
			log = this.mapper.selectCount(a);			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return log;
	}

	public int selectCounts1(int a)throws Exception {
		int result=0;		
		try {
			result = this.mapper.selectMaxId(a);			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return result;
	}	
	
	public LogManager selectCounts(int a)throws Exception {
		LogManager log=null;		
		try {
			log = this.mapper.selectCounts(a);			
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return log;
	}
	
}
