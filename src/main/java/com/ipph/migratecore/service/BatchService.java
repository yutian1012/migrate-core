package com.ipph.migratecore.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipph.migratecore.dao.BatchDao;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.model.BatchTableModel;
import com.ipph.migratecore.model.TableModel;

@Service
@Transactional
public class BatchService {
	
	@Resource
	private BatchDao batchDao;
	//中间表
	@Resource
	private BatchTableService batchTableService;
	@Resource
	private TableService tableService;
	@Resource
	private BatchLogService batchLogService;
	
	public Page<BatchModel> getList(Pageable pageable){
		return batchDao.findAll(pageable);
	}
	
	public List<BatchModel> getList(){
		return batchDao.findAll();
	}
	/**
	 * 添加批次
	 * @param batchName
	 * @param tables
	 */
	public void saveBatch(BatchModel batchModel) {
		batchModel.setCreateDate(new Date());
		
		//删除中间表数据
		if(null!=batchModel.getId()) {
			batchTableService.delByBatchId(batchModel.getId());
		}
		
		batchDao.save(batchModel);
		batchTableService.save(batchModel);
		
	}
	/**
	 * 获取批次
	 * @param batchId
	 * @return
	 */
	public BatchModel getBatch(Long batchId) {
		
		BatchModel batchModel=batchDao.findById(batchId).get();
		
		if(null!=batchModel) {
			
			List<TableModel> tableList=new ArrayList<TableModel>();
			
			//中间表数据获取
			List<BatchTableModel> batchTableList=batchTableService.getListByBatchId(batchId);
			
			for(BatchTableModel batchTable:batchTableList) {
				tableList.add(tableService.getById(batchTable.getTableId()));
			}
			
			batchModel.setTableList(tableList);
		}
		
		return batchModel;
	}
	/**
	 * 执行批次
	 * @param batchIdArr
	 */
	public void migrate(Long[] batchIdArr) {
		for(Long batchId:batchIdArr) {
			migrate(batchId);
		}
	}
	/**
	 * 执行批次
	 * @param batchId
	 */
	public void migrate(Long batchId) {
		migrate(batchId,null,null);
	}
	
	/**
	 * 执行子批次
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
	/**
	 * 删除
	 * @param batchId
	 */
	public void del(Long batchId) {
		//中间数据删除
		batchTableService.delByBatchId(batchId);
		batchDao.deleteById(batchId);
	}
}
