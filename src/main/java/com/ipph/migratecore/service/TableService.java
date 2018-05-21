package com.ipph.migratecore.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ipph.migratecore.dao.BatchTableDao;
import com.ipph.migratecore.dao.TableDao;
import com.ipph.migratecore.deal.exception.ConfigException;
import com.ipph.migratecore.model.ConstraintModel;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SplitModel;
import com.ipph.migratecore.model.SubtableModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.WhereModel;
import com.ipph.migratecore.util.XmlUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TableService {

	@Resource
	private TableDao tableDao;
	@Resource
	private MigrateService migrateService;
	@Resource
	private BatchTableDao batchTableDao;
	
	public List<TableModel> getList(){
		return tableDao.findAll();
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
					if(null!=table){
						setTableJsonData(table);
						tableDao.save(table);
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 校验Table的配置信息
	 * @param table
	 */
	private boolean validateTableModel(TableModel table) {
		
		return false;
	}
	
	public void setTableJsonData(TableModel table){
		if(null!=table.getFiledList()&&table.getFiledList().size()>0) {
			table.setFieldListJson(JSON.toJSONString(table.getFiledList()));
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
	
	public void setTableFieldFromJson(TableModel table){
		if(null!=table.getFieldListJson()) {
			table.setFiledList(JSON.parseArray(table.getFieldListJson(), FieldModel.class));
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
}
