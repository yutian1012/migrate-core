package com.ipph.migratecore.dao;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SubtableModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.model.WhereModel;
import com.ipph.migratecore.util.XmlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TableDaoTest {
	
	@Resource
	private TableDao tableDao;

	@Test
	public void test() {
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(XmlUtil.class.getResource("/migrateupdate.xml").getPath());
			
			assertNotNull(tableList);
			
			Long id=0L;
			
			TableModel table=tableList.get(0);
			
			//处理对象的序列化
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
			
			tableDao.save(table);
			
			id=table.getId();
			
			//从数据库中检索
			TableModel model2=tableDao.findById(id).get();
			
			if(null!=model2.getFieldListJson()) {
				model2.setFiledList(JSON.parseArray(model2.getFieldListJson(), FieldModel.class));
			}
			if(null!=model2.getSubTableListJson()) {
				model2.setSubTableList(JSON.parseArray(model2.getSubTableListJson(), SubtableModel.class));
			}
			if(null!=model2.getFormatFieldListJson()) {
				model2.setFormatFieldList(JSON.parseArray(model2.getFormatFieldListJson(), FormatModel.class));
			}
			if(null!=model2.getWhereJson()) {
				model2.setWhereModel(JSON.parseObject(model2.getWhereJson(), WhereModel.class));
			}
			
			assertNotNull(model2);
			
			assertNotNull(model2.getWhereModel());
			
			//从数据库中删除
			tableDao.delete(model2);
			
			assertFalse(tableDao.existsById(id));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	@Transactional
	public void testGetOne() {
		Long id=1L;//数据库中真实存在
		TableModel table=tableDao.getOne(id);
		
		System.out.println(table.getFrom());
	}

}
