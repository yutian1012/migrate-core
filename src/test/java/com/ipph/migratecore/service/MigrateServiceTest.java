package com.ipph.migratecore.service;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.model.TableModel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MigrateServiceTest {
	
	@Resource
	private MigrateService migrateService;
	@Resource
	private TableService tableService;
	@Resource
	private JdbcTemplate destJdbcTemplate;

	@Test
	public void testMigrateTable() {
		try {
			
			Long tableId=49745L;
			
			TableModel table=tableService.getById(tableId);
			
			assertNotNull(table);
			
			//执行更新操作
			migrateService.migrateTable(table,null,null); 
			
			Thread.sleep(3000);
			
			//判断目标数据表的字段是否更新
			String sql="select isApply from z_patent_dest";
			
			List<String> result=destJdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int arg1) throws SQLException {
					return rs.getString(1);
				}});
			
			assertNotNull(result);
			
			assertTrue(null!=result.get(0)&&"1".equals(result.get(0)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
