package com.ipph.migratecore.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.util.IdGenerator;

@Repository
public class LogJdbcDao {
	@Resource(name="migrateJdbcTemplate")
	private JdbcTemplate migrateJdbcTemplate;
	
	public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId){
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? ";
		
		return migrateJdbcTemplate.query(sql, new Object[] {batchLogId,tableId},new LogModelRowMapper());
	}
	
	/*public List<LogModel> getListByBatchLogIdAndTableId(Long batchLogId,Long tableId,Pageable pageable){
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? limit "+pageable.getPageNumber()*pageable.getPageSize()+","+pageable.getPageSize();
		
		return migrateJdbcTemplate.query(sql, new Object[] {batchLogId,tableId},new LogModelRowMapper());
	}*/
	
	/*public List<LogModel> getSuccessListByTableIdAndBatchLogIdIn(Long tableId,Long[] batchLogIdArr,Pageable pageable){
		StringBuffer buf= new StringBuffer("select * from log_model where batch_log_id in (");
		for (int i=0; i< batchLogIdArr.length; i++) {
			buf.append("?");
			if(i!=batchLogIdArr.length-1) {
				buf.append(", ");
			}
		}
		buf.append(") and table_id=? and status=? ");
		buf.append("limit "+pageable.getPageNumber()*pageable.getPageSize()+","+pageable.getPageSize());
		
		Object[] params=new Object[batchLogIdArr.length+2];
		Object[] otherParams=new Object[]{tableId,LogStatusEnum.SUCCESS.name()};
		System.arraycopy(batchLogIdArr, 0, params, 0, batchLogIdArr.length);
		System.arraycopy(otherParams, 0, params, batchLogIdArr.length, otherParams.length);
		
		return migrateJdbcTemplate.query(buf.toString(), params,new LogModelRowMapper());
	}*/
	
	/*public List<LogModel> getFailListByBatchLogIdAndTableId(Long batchLogId,Long tableId,Pageable pageable){
		String sql="select * from log_model "
				+ "where batch_log_id=? and table_id=? and status=? "
				+ "limit "+pageable.getPageNumber()*pageable.getPageSize()+","+pageable.getPageSize();
		
		return migrateJdbcTemplate.query(sql, new Object[] {batchLogId,tableId,LogStatusEnum.FAIL.name()},new LogModelRowMapper());
	}*/
	
	
	/*public List<LogModel> getListByBatchLogIdAndTableIdAndStatus(Long batchLogId,Long tableId,LogStatusEnum status,Pageable pageable){
		
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? and status=? limit "+pageable.getPageNumber()*pageable.getPageSize()+","+pageable.getPageSize();
		return migrateJdbcTemplate.query(sql, new Object[] {batchLogId,tableId,status.name()},new LogModelRowMapper());
	}*/
	
	/*public List<LogModel> getListByBatchLogIdAndTableIdAndMessageType(Long batchLogId,Long tableId,LogMessageEnum messageType,Pageable pageable){
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? and message_type=? limit "+pageable.getPageNumber()*pageable.getPageSize()+","+pageable.getPageSize();
		return migrateJdbcTemplate.query(sql, new Object[] {batchLogId,tableId,messageType.name()},new LogModelRowMapper());
	}*/
	
	public LogModel getByDataIdAndBatchLogId(Long dataId,Long batchLogId) {
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where data_id=? and batch_log_id=? ";
		return migrateJdbcTemplate.queryForObject(sql, new Object[] {dataId,batchLogId},new LogModelRowMapper());
	}
	
	//@Query(" select count(1) as num ,log.status as status from LogModel log where log.batchLogId=?1 and log.tableId=?2 group by log.status ")
	public List<Map<String,Object>> statistic(Long batchLogId,Long tableId){
		
		String sql="select count(1) as num,status as category "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? group by status ";
		
		return migrateJdbcTemplate.queryForList(sql,new Object[] {batchLogId,tableId});
	}
	
	public List<Map<String,Object>> statisticError(Long batchLogId,Long tableId){
		
		String sql="select count(1) as num,message_Type as category "
				+ "from log_model "
				+ "where batch_log_id=? and table_id=? and status=? group by message_Type ";
		
		return migrateJdbcTemplate.queryForList(sql,new Object[] {batchLogId,tableId,LogStatusEnum.FAIL.name()});
	}
	
	public List<Map<String,Object>> statisticByParentBatchLog(Long parentBatchLogId,Long tableId){
		String sql="select count(a.id) as num,a.status as category "
				+ "from log_model as a, batch_log_model as b "
				+ "where b.id=a.batch_log_id and b.parent_id=? and a.table_id=? group by a.status ";
		
		return migrateJdbcTemplate.queryForList(sql,new Object[] {parentBatchLogId,tableId});
	}
	
	public List<Map<String,Object>> getstatisticBybatchLogIdIn(Object[] batchLogIdArr){
		
		StringBuffer buf= new StringBuffer("select count(1) as num,status as category from log_model where batch_log_id in (");
		for (int i=0; i< batchLogIdArr.length; i++) {
			buf.append("?");
			if(i!=batchLogIdArr.length-1) {
				buf.append(", ");
			}
		}
		buf.append(") ");
		
		buf.append("group by status");
		
		return migrateJdbcTemplate.queryForList(buf.toString(), batchLogIdArr); 
	}
	
	public LogModel save(LogModel log) {
		
		String sql="insert into log_model (id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name,exception) values (?,?,?,?,?,?,?,?,?,?,?) ";
		
		log.setId(IdGenerator.genId());
		
		migrateJdbcTemplate.update(sql,new Object[] {log.getId(),log.getBatchLogId(),log.getCreateDate(),log.getDataId(),log.getDealData(),
				log.getMessage(),null!=log.getMessageType()?log.getMessageType().name():null,null!=log.getStatus()?log.getStatus().name():null,log.getTableId(),log.getTableName(),log.getException()});
		
		return log;
	}
	
	public LogModel findById(Long id) {
		String sql="select id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name "
				+ "from log_model "
				+ "where id=? ";
		return migrateJdbcTemplate.queryForObject(sql, new Object[] {id},new LogModelRowMapper());
	}
	
	public void saveAll(List<LogModel> list) {
		String sql="insert into log_model (id,batch_log_id,create_date,data_id,deal_data,message,message_type,status,table_id,table_name) values (?,?,?,?,?,?,?,?,?,?)";
		
		List<Object[]> dataList=new ArrayList<>(list.size());
		
		for(LogModel log:list) {
			
			
			log.setId(IdGenerator.genId());
			dataList.add(new Object[] {log.getId(),log.getBatchLogId(),log.getCreateDate(),log.getDataId(),log.getDealData(),
				log.getMessage(),null!=log.getMessageType()?log.getMessageType().name():null,null!=log.getStatus()?log.getStatus().name():null,log.getTableId(),log.getTableName()});
		}
		
		//批量保存
		migrateJdbcTemplate.batchUpdate(sql, dataList);
	}
	
	class LogModelRowMapper implements RowMapper<LogModel>{

		@Override
		public LogModel mapRow(ResultSet rs, int rowNum) throws SQLException {
			LogModel log=new LogModel();
			log.setId(rs.getLong(1));
			log.setBatchLogId(rs.getLong(2));
			log.setCreateDate(rs.getDate(3));
			log.setDataId(rs.getLong(4));
			log.setDealData(rs.getString(5));
			log.setMessage(rs.getString(6));
			String messageType=rs.getString(7);
			if(null!=messageType&&!"".equals(messageType)) {
				log.setMessageType(LogMessageEnum.valueOf(messageType));
			}
			String status=rs.getString(8);
			if(null!=status&&!"".equals(status)) {
				log.setStatus(LogStatusEnum.valueOf(status));
			}
			log.setTableId(rs.getLong(9));
			log.setTableName(rs.getString(10));
			return log;
		}
			
	}
}
