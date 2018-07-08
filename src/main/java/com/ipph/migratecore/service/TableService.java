package com.ipph.migratecore.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ipph.migratecore.dao.BatchTableDao;
import com.ipph.migratecore.dao.TableDao;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.ConstraintModel;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SplitModel;
import com.ipph.migratecore.model.SubtableModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.WhereModel;
import com.ipph.migratecore.table.TableMetaModel;
import com.ipph.migratecore.util.XmlUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class TableService {

	@Resource
	private TableDao tableDao;
	@Resource
	private MigrateService migrateService;
	@Resource
	private BatchTableDao batchTableDao;
	@Resource
	private MigrateTableService migrateTableService;
	
	public Page<TableModel> getList(Pageable pageable){
		return tableDao.findAll(pageable);
	}
	
	/**
	 * 上传table定义的xml文档
	 * @param in
	 * @return
	 */
	public boolean uploadXml(InputStream in){
		try {
			List<TableModel> tableList=XmlUtil.parseBySax(in);
			
			if(null!=tableList&&tableList.size()>0){
				
				for(TableModel table :tableList){
					if(validateTableModel(table)) {
						save(table);
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			return false;
		}
		return true;
	}
	/**
	 * 保存由页面定义的table配置信息
	 * @param table
	 * @return
	 */
	public boolean saveTableJson(TableModel table) {
		if(null!=table) {
			processTableField(table);
		}
		return save(table);
	}
	
	/**
	 * 保存数据
	 * @param table
	 * @return
	 */
	public boolean save(TableModel table) {
		if(null!=table){
			setTableJsonData(table);
			tableDao.save(table);
		}
		return true;
	}
	
	/**
	 * 校验Table的配置信息
	 * @param table
	 */
	private boolean validateTableModel(TableModel table) {
		
		return true;
	}
	/**
	 * 设置table的json字段，用于存放到数据库中持久保存
	 * @param table
	 */
	public void setTableJsonData(TableModel table){
		if(null!=table.getFieldList()&&table.getFieldList().size()>0) {
			table.setFieldListJson(JSON.toJSONString(table.getFieldList()));
		}
		if(null!=table.getSubTableList()&&table.getSubTableList().size()>0) {
			table.setSubTableListJson(JSON.toJSONString(table.getSubTableList()));
		}
		if(null!=table.getFormatFieldList()&&table.getFormatFieldList().size()>0) {
			table.setFormatFieldListJson(JSON.toJSONString(table.getFormatFieldList()));
		}
		if(null!=table.getWhereModel()&&null!=table.getWhereModel().getConditionList()&&table.getWhereModel().getConditionList().size()>0) {
			table.setWhereJson(JSON.toJSONString(table.getWhereModel()));
		}
		if(null!=table.getConstraintList()&&table.getConstraintList().size()>0) {
			table.setConstraintListJson(JSON.toJSONString(table.getConstraintList()));
		}
		if(null!=table.getSplitFieldList()&&table.getSplitFieldList().size()>0) {
			table.setSplitFieldListJson(JSON.toJSONString(table.getSplitFieldList()));
		}
	}
	/**
	 * 用于从数据库中获取对象，并将json值解析设置到相应的数据字段上
	 * @param table
	 */
	public void setTableFieldFromJson(TableModel table){
		if(null!=table.getFieldListJson()) {
			table.setFieldList(JSON.parseArray(table.getFieldListJson(), FieldModel.class));
		}
		if(null!=table.getSubTableListJson()) {
			table.setSubTableList(JSON.parseArray(table.getSubTableListJson(), SubtableModel.class));
		}
		if(null!=table.getFormatFieldListJson()) {
			table.setFormatFieldList(JSON.parseArray(table.getFormatFieldListJson(), FormatModel.class));
		}
		if(null!=table.getWhereJson()) {
			table.setWhereModel(JSON.parseObject(table.getWhereJson(), WhereModel.class));
		}
		if(null!=table.getConstraintListJson()) {
			table.setConstraintList(JSON.parseArray(table.getConstraintListJson(), ConstraintModel.class));
		}
		if(null!=table.getSplitFieldListJson()) {
			table.setSplitFieldList(JSON.parseArray(table.getSplitFieldListJson(),SplitModel.class));
		}
	}
	/**
	 * 处理Table数据，页面设置的信息，并未对source字段做处理
	 * 这里，单独处理source字段集合
	 * @param table
	 */
	public void processTableField(TableModel table) {
		if(null==table.getFieldList()||table.getFieldList().size()==0) {
			return ;
		}
		
		List<FieldModel> sourceFieldList=new ArrayList<>(table.getFieldList().size());
		for(FieldModel fieldModel:table.getFieldList()) {
			if(null!=fieldModel&&FieldValueTypeEnum.FIELD==fieldModel.getValueType()) {
				FieldModel sourceFieldModel=new FieldModel();
				sourceFieldModel.setName(fieldModel.getValue());
				sourceFieldModel.setApplyType(ApplyTypeEnum.SOURCE);
				sourceFieldList.add(sourceFieldModel);
			}
		}
		
		
		
		if(sourceFieldList.size()>0) {
			table.getFieldList().addAll(sourceFieldList);
		}
	}
	
	/**
	 * 执行迁移操作
	 * @param tableId
	 * @param batchLogId
	 * @return
	 */
	@Deprecated
	public boolean migrateTable(Long tableId,Long batchLogId){
		TableModel table=getById(tableId);
		return migrateTable(table, batchLogId,null);
	}
	/**
	 * 执行迁移操作
	 * @param tableId
	 * @param batchLogId
	 * @return
	 */
	public boolean migrateTable(TableModel table,Long batchLogId,Long parentLogId){
		
		if(log.isDebugEnabled()) {
			log.debug("migrate table "+table.getFrom());
		}
		
		boolean flag=true;
		if(null!=table){
			try {
				migrateService.migrateTable(table,batchLogId,parentLogId);
			} catch (ConfigException e) {
				flag=false;
			}
		}
		return flag;
	}
	/**
	 * 获取TableModel定义信息
	 * @param tableId
	 * @return
	 */
	public TableModel getById(Long tableId){
		TableModel table=tableDao.findById(tableId).get();//getOne(tableId);
		if(null!=table){
			setTableFieldFromJson(table);
		}
		return table;
	}
	/**
	 * 编辑页面获取
	 * @param tableId
	 * @return
	 */
	public TableModel get2EditById(Long tableId) {
		TableModel table=getById(tableId);
		if(null!=table&&null!=table.getFieldList()) {
			Iterator<FieldModel> iter=table.getFieldList().iterator();
			while(iter.hasNext()) {
				FieldModel fieldModel=iter.next();
				if(ApplyTypeEnum.SOURCE==fieldModel.getApplyType()) {
					iter.remove();
				}
			}
		}
		return table;
	}
	
	/**
	 * 获取表记录详细数据
	 * @param tableId
	 * @param dataId
	 * @return
	 */
	public Map<String, Object> getRecord(Long tableId,Long dataId){
		TableModel table=getById(tableId);
		return migrateService.getRecord(table,dataId);
	}
	/**
	 * 删除
	 * @param tableId
	 */
	public void del(Long tableId) {
		//先删除中间表数据
		batchTableDao.deleteByTableId(tableId);
		tableDao.deleteById(tableId);
	}
	/**
	 * 获取源库中的数据表信息列表
	 * @return
	 */
	public List<String> getSourceTables(){
		return migrateTableService.getSourceTables();
	}
	/**
	 * 获取目标数据库中的表信息列表
	 * @return
	 */
	public List<String> getTargetTables(){
		return migrateTableService.getTargetTables();
	}
	/**
	 * 获取表定义信息
	 * @param isSourceTable
	 * @param tableName
	 * @return
	 */
	public TableMetaModel getMetaTableByName(boolean isSourceTable,String tableName){
		if(isSourceTable) {
			return migrateTableService.getSourceTable(tableName);
		}else {
			return migrateTableService.getTargetTable(tableName);
		}
	}
}
