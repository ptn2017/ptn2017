package com.nms.model.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nms.db.bean.system.UnLoading;
import com.nms.db.dao.system.TranferInfoMapper;
import com.nms.model.util.ObjectService_Mybatis;
import com.nms.ui.manager.ExceptionManage;
import com.nms.ui.manager.LoginUtil;
import com.nms.ui.manager.xmlbean.LoginConfig;
import com.nms.ui.ptn.systemManage.bean.TranferInfo;

/**
 * 数据转储
 * 提取sql集合和ID集合
 * @author sy
 *
 */
public class TranferService_Mb extends ObjectService_Mybatis{
	
	private TranferInfoMapper  tranferMapper=null;

	public TranferInfoMapper getMapper() {
		return tranferMapper;
	}

	public void setMapper(TranferInfoMapper tranferMapper) {
		this.tranferMapper = tranferMapper;
	}

	public void setPtnuser(String ptnuser) {
		super.ptnuser = ptnuser;
	}

	public void setSqlSession(SqlSession sqlSession) {
		super.sqlSession = sqlSession;
	}
	
	/**
	 * 根据table名称。 获取table表的个属性的 类型
	 * 
	 * @param tableName
	 *            表名
	 * @return typeList    类型集合
	 * @throws SQLException
	 */

	private  List<String> getTableBeans(String tableName) throws Exception {		
		List<String> typeList = null;
		try {		
			typeList=new ArrayList<String>();
			typeList=this.tranferMapper.tableColumnType(tableName);
		} catch (Exception e) {
			throw e;
		} 
		return typeList;
	}
	/**
	 * 根据unloadType,判断为转储哪个表
	 * @param unload
	 * @param count
	 * 			数据转储的 数目  
	 * @return  sqlList
	 * 			 sql语句的集合
	 * @throws Exception
	 */
	public  List<TranferInfo> getDataStr(UnLoading unload,int count,int total) throws Exception {
		String tableName=null;
		String byTime=null;
		List<TranferInfo> tranferInfoList=null;
		try {
			LoginUtil loginUtil=new LoginUtil();
			LoginConfig loginConfig = loginUtil.readLoginConfig();
			tranferInfoList=new ArrayList<TranferInfo>();
			//在所有转储文件加上文件头，便于用数据库恢复功能恢复数据
			/*添加文件头*********************/
			TranferInfo t1 = new TranferInfo();
			t1.setId(0);
			t1.setSql("/*"+loginConfig.getVersion()+"*/\n");/*V2.1.4*/
			TranferInfo t2 = new TranferInfo();
			t2.setId(0);
			t2.setSql("/*Database: ptn*/\n");/*Database: ptn*/
			tranferInfoList.add(t1);
			tranferInfoList.add(t2);
			/*end*********************/
			if(1==unload.getUnloadType()){
				//告警
				tableName="history_alarm";
				byTime="happenedtime";
			}else if(2==unload.getUnloadType()){
				// 性能
				tableName="history_performance";
				byTime="performancetime";
			}else if(3 == unload.getUnloadType()){
				// 操作日志
				tableName="operation_log";
				byTime="startTime";
				this.getTableStr(tableName, count, tranferInfoList);				
				tableName="operationdatalog";
			}else if(4 == unload.getUnloadType()){
				// 登录日志
				tableName="login_log";
				byTime="startTime";
			}else if(5 == unload.getUnloadType()){
				// 登录日志
				tableName="login_log";
				byTime="startTime";
			}else if(6 == unload.getUnloadType()){
				// 系统日志
				tableName="systemlog";
				byTime="startTime";
			}
			this.getTableStr(tableName, total, tranferInfoList);
		} catch (Exception e) {
			throw e;
		}
		return tranferInfoList;
	}
	
	private void getTableStr(String tableName, int count, List<TranferInfo> tranferInfoList) throws Exception {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String tableDesc = getTableDesc(tableName).substring(1);
			String sql = "select * from " + tableName +" limit 0,?";
			List<String> typeList=getTableBeans(tableName);
			preparedStatement = this.sqlSession.getConnection().prepareStatement(sql);
			preparedStatement.setInt(1, count);
			resultSet = preparedStatement.executeQuery();
			String label = "";
			while (resultSet.next()) {
				TranferInfo tranferInfo = new TranferInfo();
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("insert into " + tableName +"("+tableDesc+") values (");
				// 遍历列集合，取索引
				for (int i = 2; i <= typeList.size(); i++) {
					// 如果是第一次循环。前方不加","
					if (i == 2) {
						label = "";
					} else {
						label = ",";
					}
					// 如果值为null 直接存入NULL 而不是'null'
					if (null != resultSet.getObject(i)) {
						stringBuffer.append(label + "'" + resultSet.getObject(i) + "'");
						
					} else {
						stringBuffer.append(label + "null");
					}
				}
				stringBuffer.append(");\n");
				tranferInfo.setId(resultSet.getInt("id"));
				tranferInfo.setSql(stringBuffer.toString());
				tranferInfoList.add(tranferInfo);
			}
		} catch (Exception e) {
			ExceptionManage.dispose(e, this.getClass());
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}finally{
					resultSet = null;
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (Exception e) {
					ExceptionManage.dispose(e,this.getClass());
				}finally{
					preparedStatement = null;
				}
			}
		}
	}

	/**
	 * 获取表的结构
	 * @param tableName 表名称
	 * @return
	 */
	private String getTableDesc(String tableName) {
		List<String> typeList = null;
		String table = "";
		try {
			typeList=new ArrayList<String>();
			typeList=this.tranferMapper.tableColumnName(tableName);
			if(typeList!=null && typeList.size()>0){
				for(int i=0;i<typeList.size();i++){
					if(!"".equals(typeList.get(i)) && !"id".equals(typeList.get(i))){
						table +=","+typeList.get(i);
					}
				}
			}
		
		} catch (Exception e) {
			ExceptionManage.dispose(e,this.getClass());
		}
		return table;
	}
}
