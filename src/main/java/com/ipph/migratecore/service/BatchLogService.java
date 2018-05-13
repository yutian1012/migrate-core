package com.ipph.migratecore.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchLogDao;
import com.ipph.migratecore.enumeration.BatchStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.BatchModel;

@Service
public class BatchLogService {
	
	@Resource
	private BatchLogDao batchLogDao;
	
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
	
	public BatchLogModel getById(Long id) {
		return batchLogDao.getOne(id);
	}
}
