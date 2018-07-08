package com.ipph.migratecore.service;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchTableDao;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.model.BatchTableModel;
import com.ipph.migratecore.model.TableModel;

@Service
@Transactional
public class BatchTableService {
	@Resource
	private BatchTableDao batchTableDao;
	/**
	 * 根据批次主键删除中间表数据
	 * @param batchId
	 * @return
	 */
	public int delByBatchId(Long batchId) {
		return batchTableDao.deleteByBatchId(batchId);
	}
	
	public int save(BatchModel batchModel) {
		int result=0;
		//添加数据到中间表
		for(TableModel tableModel:batchModel.getTableList()) {
			BatchTableModel batchTableModel =new BatchTableModel();
			batchTableModel.setBatchId(batchModel.getId());
			batchTableModel.setTableId(tableModel.getId());
			batchTableDao.save(batchTableModel);
			result++;
		}
		return result;
	}
	
	public List<BatchTableModel> getListByBatchId(Long batchId){
		return batchTableDao.getListByBatchId(batchId);
	}
}
