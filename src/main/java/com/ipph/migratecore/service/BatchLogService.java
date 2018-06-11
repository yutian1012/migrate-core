package com.ipph.migratecore.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchLogDao;
import com.ipph.migratecore.enumeration.BatchStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.model.TableModel;

@Service
@Transactional
public class BatchLogService {
	
	@Resource
	private BatchLogDao batchLogDao;
	@Resource
	private PatentUploadService patentUploadService;
	@Resource
	private TableService tableService;
	
	public Long save(BatchModel batch,Long parentId) {
		
		BatchLogModel batchLogModel=new BatchLogModel();
		if(null!=parentId) {
			batchLogModel.setParentId(parentId);
		}
		batchLogModel.setBatchId(batch.getId());
		batchLogModel.setBatchNo(System.currentTimeMillis()/1000+"");
		batchLogModel.setCreateDate(new Date());
		batchLogModel.setStatus(BatchStatusEnum.FAIL);
		
		batchLogDao.save(batchLogModel);
		
		return batchLogModel.getId();
		
	}
	
	public void update(Long batchLogId) {
		BatchLogModel batchLogModel=batchLogDao.getOne(batchLogId);
		
		batchLogModel.setStatus(BatchStatusEnum.SUCCESS);
		
		
		batchLogDao.save(batchLogModel);
	}
	/**
	 * 批次执行日志信息
	 * @param batchId
	 * @return
	 */
	public List<BatchLogModel> getBatchLog(Long batchId){
		return batchLogDao.getListByBatchId(batchId);
	}
	
	public List<BatchLogModel> getBatchLogByparentIdIsNull(Long batchId){
		return batchLogDao.getBatchLogByBatchIdAndParentIdIsNull(batchId);
	}
	
	public BatchLogModel getById(Long id) {
		return batchLogDao.getOne(id);
	}
	
	/**
	 * 获取子批次主键数组集合
	 * @param parentBatchLogId
	 * @return
	 */
	public Long[] getSubBatchLogIdArr(Long parentBatchLogId) {
		//获取对应的子批次列表
		List<BatchLogModel> batchLogList=batchLogDao.getListByParentId(parentBatchLogId);
		
		Long[] batchLogIdArr=null;
		if(null!=batchLogList&&batchLogList.size()>0) {
			batchLogIdArr=new Long[batchLogList.size()];
			
			int i=0;
			for(BatchLogModel batchLog:batchLogList) {
				batchLogIdArr[i++]=batchLog.getId();
			}
		}
		return batchLogIdArr;
	}
	/**
	 * 获取最新的子批次记录
	 * @param parentBatchLogId
	 * @return
	 */
	public BatchLogModel findFirstByParentIdOrderByIdDesc(Long parentBatchLogId) {
		return batchLogDao.findFirstByParentIdOrderByIdDesc(parentBatchLogId);
	}
	/**
	 * 获取子批次记录
	 * @param batchId
	 * @param parentId
	 * @return
	 */
	public List<BatchLogModel> getByBatchIdAndParentId(Long batchId,Long parentId){
		return batchLogDao.getListByParentId(parentId);
	}
	/**
	 * 获取执行sql
	 * @param batchLogId
	 * @param tableId
	 * @param isApply
	 * @return
	 */
	public String getSuccessExecuteSql(Long batchLogId,Long tableId,boolean isApply) {
		
		return patentUploadService.getUpdateSqlByBatchLogIdAndTableId(batchLogId, tableId, isApply);
	}
	/**
	 * 导出pct插入sql语句
	 * @param batchLogId
	 * @param tableId
	 * @param isApply
	 * @return
	 */
	public String getPctSuccessExecuteSql(Long batchLogId,Long tableId,boolean isApply,boolean isSource) {
		return patentUploadService.getPctInsertSqlByBatchLogIdAndTableId(batchLogId, tableId, isApply,isSource);
	}
}
