package com.ipph.migratecore.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchDao;
import com.ipph.migratecore.dao.BatchTableDao;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.model.BatchTableModel;
import com.ipph.migratecore.model.TableModel;

@Service
public class BatchService {
	
	@Resource
	private BatchDao batchDao;
	@Resource
	private BatchTableDao batchTableDao;
	@Resource
	private TableService tableService;
	@Resource
	private BatchLogService batchLogService;
	
	public List<BatchModel> getList(){
		return batchDao.findAll();
	}
	
	public void add(String batchName,String[] tables) {
		//保存对象
		BatchModel batchModel=new BatchModel();
		batchModel.setBatchName(batchName);
		batchModel.setCreateDate(new Date());
		
		batchDao.save(batchModel);
		
		Long batchId=batchModel.getId();
		
		for(String tableId:tables) {
			BatchTableModel batchTableModel =new BatchTableModel();
			batchTableModel.setBatchId(batchId);
			batchTableModel.setTableId(Long.parseLong(tableId));
			batchTableDao.save(batchTableModel);
		}
	}
	
	public BatchModel getBatch(Long batchId) {
		
		BatchModel batchModel=batchDao.getOne(batchId);
		
		if(null!=batchModel) {
			
			List<TableModel> tableList=new ArrayList<TableModel>();
			
			List<BatchTableModel> batchTableList=batchTableDao.getListByBatchId(batchId);
			
			for(BatchTableModel batchTable:batchTableList) {
				tableList.add(tableService.getById(batchTable.getTableId()));
			}
			
			batchModel.setTableList(tableList);
			
		}
		
		return batchModel;
	}
	/**
	 * 执行批次
	 * @param batchId
	 */
	public void migrate(Long batchId) {
		migrate(batchId,null,null);
	}
	
	/**
	 * 执行批次
	 * @param batchId
	 */
	public void migrate(Long batchId,Long parentId,Long tableId) {
		BatchModel batch=getBatch(batchId);
		
		if(null!=batch) {
			//执行批次前记录日志
			Long batchLogId=batchLogService.save(batch,parentId);
			
			List<TableModel> tableList=batch.getTableList();
			
			if(null!=tableList&&tableList.size()>0) {
				for(TableModel table:tableList) {
					if(null==tableId||table.getId().longValue()==tableId) {
						tableService.migrateTable(table,batchLogId,parentId);
					}
				}
			}
			
			//执行批次后更新记录
			batchLogService.update(batchLogId);
		}
	}
}
