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

import com.ipph.migratecore.model.MigrateModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.XmlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MigrateDaoTest {
	
	@Resource
	private MigrationDao migrationDao;
	@Resource
	private MigrateDao migrateDao;
	
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
	
	@Test
	public void testMigratePatentPct() {
		
		//清空数据表
		String sql="delete from wf_patent_support_foreign_dest";
		destJdbcTemplate.execute(sql);
		
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(XmlUtil.class.getResource("/patentPctApply_test.xml").getPath());
			assertNotNull(tableList);
			
			TableModel table=tableList.get(0);
			
			MigrateModel migrateModel=new MigrateModel();
			
			long total=migrateDao.getTotal(table);
			
			migrateModel.setType(table.getType());
			migrateModel.setTableModel(table);
			migrateModel.setTotal(total);
			migrateModel.setStart(0);
			migrateModel.setSize(3000);
			
			migrateDao.migrate(migrateModel);
			
			String result="select count(1) from wf_patent_support_foreign_dest";
			
			long num=destJdbcTemplate.queryForObject(result, Long.class);
			
			assertTrue(num>0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMigrateCopyright() {
		
		//清空数据表
		String sql="delete from cs_copyright_dest";
		destJdbcTemplate.execute(sql);
		
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(XmlUtil.class.getResource("/copyright_test.xml").getPath());
			assertNotNull(tableList);
			
			TableModel table=tableList.get(0);
			
			MigrateModel migrateModel=new MigrateModel();
			
			long total=migrateDao.getTotal(table);
			
			migrateModel.setType(table.getType());
			migrateModel.setTableModel(table);
			migrateModel.setTotal(total);
			migrateModel.setStart(0);
			migrateModel.setSize(3000);
			
			migrateDao.migrate(migrateModel);
			
			String result="select count(1) from cs_copyright_dest";
			
			long num=destJdbcTemplate.queryForObject(result, Long.class);
			
			assertTrue(num>0);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testMigrateSubCopyright() {
		
		//清空数据表
		String sql="delete from cs_copyright_owner_dest";
		destJdbcTemplate.execute(sql);
		
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(XmlUtil.class.getResource("/copyright_sub_owner_test.xml").getPath());
			
			assertNotNull(tableList);
			
			TableModel table=tableList.get(0);
			
			MigrateModel migrateModel=new MigrateModel();
			
			long total=migrateDao.getTotal(table);
			
			migrateModel.setType(table.getType());
			migrateModel.setTableModel(table);
			migrateModel.setTotal(total);
			migrateModel.setStart(0);
			migrateModel.setSize(3000);
			
			migrateDao.migrate(migrateModel);
			
			String result="select count(1) from cs_copyright_owner_dest";
			
			long num=destJdbcTemplate.queryForObject(result, Long.class);
			
			assertTrue(num>0);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
