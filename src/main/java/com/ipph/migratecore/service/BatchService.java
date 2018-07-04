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
import com.ipph.migratecore.dao.BatchTableDao;
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
	private BatchTableDao batchTableDao;
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
	public void add(String batchName,String[] tables) {
		//保存对象
		BatchModel batchModel=new BatchModel();
		batchModel.setBatchName(batchName);
		batchModel.setCreateDate(new Date());
		
		batchDao.save(batchModel);
		
		Long batchId=batchModel.getId();
		
		//添加数据到中间表
		for(String tableId:tables) {
			BatchTableModel batchTableModel =new BatchTableModel();
			batchTableModel.setBatchId(batchId);
			batchTableModel.setTableId(Long.parseLong(tableId));
			batchTableDao.save(batchTableModel);
		}
	}
	/**
	 * 获取批次
	 * @param batchId
	 * @return
	 */
	public BatchModel getBatch(Long batchId) {
		
		BatchModel batchModel=batchDao.getOne(batchId);
		
		if(null!=batchModel) {
			
			List<TableModel> tableList=new ArrayList<TableModel>();
			
			//中间表数据获取
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
		batchTableDao.deleteByBatchId(batchId);
		batchDao.deleteById(batchId);
	}
}
