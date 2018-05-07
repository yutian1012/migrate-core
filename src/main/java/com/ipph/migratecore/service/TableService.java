package com.ipph.migratecore.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ipph.migratecore.dao.TableDao;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SubtableModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.WhereModel;
import com.ipph.migratecore.util.XmlUtil;

@Service
public class TableService {

	@Resource
	private TableDao tableDao;
	@Resource
	private MigrateService migrateService;
	
	public List<TableModel> getList(){
		return tableDao.findAll();
	}
	
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
	}
	
	public boolean migrateTable(Long tableId){
		TableModel table=getById(tableId);
		if(null!=table){
			migrateService.migrateTable(table);
			return true;
		}
		return false;
	}
	
	public TableModel getById(Long tableId){
		TableModel table=tableDao.getOne(tableId);
		if(null!=table){
			setTableFieldFromJson(table);
		}
		return table;
	}
}
