package com.ipph.migratecore.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.PatentInfo;

public class LogDaoImpl implements ILogDao{
	
	@Resource
	private JdbcTemplate migrateJdbcTemplate;
	
	@Override
	public List<LogModel> getListByBatchLogIdAndTableIdAndStatusWithPatent(Long batchLogId, Long tableId,
			LogStatusEnum status, Pageable pageable) {
		
		String sql="select log.id,log.batch_log_id,log.create_date,log.data_id,log.deal_data,log.message,log.message_type,log.status,log.table_id,log.table_name,log.exception,"
				+ "patent.app_number,patent.app_name,patent.app_date,patent.applicant,patent.origin_applicant,patent.status as patentStatus "
				+ "from log_model as log left join patent_info as patent on log.deal_data=patent.app_number "
				+ "where log.batch_log_id=? and log.table_id=? and log.status=? ";
		Object[] param=null;
		if(null==pageable) {
			param=new Object[] {batchLogId,tableId,status.name()};
		}else {
			sql+="limit ?,?";
			param=new Object[] {batchLogId,tableId,status.name(),pageable.getPageNumber(),pageable.getPageSize()};
		}
		
		return migrateJdbcTemplate.query(sql,param,new LogModelRowMapper());
		
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
			log.setException(rs.getString(11));
			
			String appNumber=rs.getString(12);
			if(null!=appNumber) {
				PatentInfo patent=new PatentInfo();
				patent.setAppNumber(appNumber);
				patent.setAppName(rs.getString(13));
				patent.setAppDate(rs.getDate(14));
				patent.setApplicant(rs.getString(15));
				patent.setOriginApplicant(rs.getString(16));
				patent.setStatus(rs.getString(17));
				
				log.setPatentInfo(patent);
			}
			return log;
		}
			
	}
}
