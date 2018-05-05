package com.ipph.migratecore.sql;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.XmlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SqlBuilderTest {
	
	@Resource
	private SqlBuilder sqlBuilder;
	
	@Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();
	
	private List<TableModel> tableList;
	
	@Before
	public void before() {
		try {
			tableList=XmlUtil.parseBySax(XmlUtil.class.getResource("/test.xml").getPath());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetSelectSql() {
		assertNotNull(tableList);
		
		TableModel table=tableList.get(0);
		
		String selectSql=sqlBuilder.getSelectSql(table);
		
		String result="select patentNo from tpatentallo where 1=1  and costtype = ? ";
		log.clearLog();
		
		System.out.println(selectSql);
		
		assertTrue(log.getLog().contains(result));
	}

	@Test
	public void testGetSelectCountSql() {
		assertNotNull(tableList);
		
		TableModel table=tableList.get(0);
		
		String countSql=sqlBuilder.getSelectCountSql(table);
		
		String result="select count(1) num from tpatentallo where 1=1  and costtype = ? ";
		log.clearLog();
		
		System.out.println(countSql);
		
		assertTrue(log.getLog().contains(result));
	}
	
	@Test
	public void testGetUpdateSql() {
		assertNotNull(tableList);
		
		TableModel table=tableList.get(0);
		
		String udpateSql=sqlBuilder.getUpdateSql(table);
		
		String result="update z_patent set isApply = ?  where 1=1  and appNumber = ? ";
		log.clearLog();
		
		System.out.println(udpateSql);
		
		assertTrue(log.getLog().contains(result));
	}
	
	@Test
	public void testGetTargetSelectSql() {
		assertNotNull(tableList);
		
		TableModel table=tableList.get(0);
		
		String targetSelectSql=sqlBuilder.getTargetSelectSql(table);
		
		String result="select count(1) num from z_patent where 1=1  and appNumber = ? ";
		log.clearLog();
		
		System.out.println(targetSelectSql);
		
		assertTrue(log.getLog().contains(result));
	}

}
