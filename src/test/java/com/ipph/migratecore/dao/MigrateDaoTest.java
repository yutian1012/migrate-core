package com.ipph.migratecore.dao;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.XmlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MigrateDaoTest {
	
	@Resource
	private MigrationDao migrationDao;
	
	@Resource(name="destJdbcTemplate")
	private JdbcTemplate destJdbcTemplate;
	
	@Before
	public void before() {
		String sql="update migratedest set useremail=null";
		destJdbcTemplate.update(sql);
	}

	@Test
	public void testUpdate() {
		try {
			List<TableModel> tableList=XmlUtil.parseBySax(XmlUtil.class.getResource("/migrateupdate.xml").getPath());
			
			assertNotNull(tableList);
			
			TableModel table=tableList.get(0);
			
			//执行更新操作
			migrationDao.update(table,null,null,0,100); 
			
			//判断目标数据表的字段是否更新
			String sql="select useremail from migratedest";
			
			List<String> result=destJdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					return rs.getString(1);
				}});
			
			assertNotNull(result);
			
			assertTrue(null!=result.get(0)&&!"".equals(result.get(0)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void migratePatent() {
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(XmlUtil.class.getResource("/patent_test.xml").getPath());
			assertNotNull(tableList);
			
			TableModel table=tableList.get(0);
			
			//执行更新操作
			migrationDao.update(table,null,null,0,100); 
			
			//判断目标数据表的字段是否更新
			String sql="select isApply from z_patent_dest";
			
			List<String> result=destJdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					return rs.getString(1);
				}});
			
			assertNotNull(result);
			
			assertTrue(null!=result.get(0)&&"1".equals(result.get(0)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
