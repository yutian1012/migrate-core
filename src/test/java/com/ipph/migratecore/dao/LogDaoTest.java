package com.ipph.migratecore.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.LogModel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LogDaoTest {
	
	@Resource
	//private LogDao logDao;
	private LogJdbcDao logJdbcDao;

	@Test
	public void test() {
		Long id=0L;
		
		LogModel model=new LogModel();
		model.setCreateDate(new Date());
		model.setDataId(0L);
		model.setDealData("test");
		model.setStatus(LogStatusEnum.SUCCESS);
		model.setTableId(0L);
		model.setTableName("tablename");
		model=logJdbcDao.save(model);
		
		assertNotNull(model.getId());
		
		id=model.getId();
		
		//从数据库中检索
		LogModel model2=logJdbcDao.findById(id);//logDao.findById(id).get();
		
		assertNotNull(model2);
		
		//从数据库中删除
		/*logDao.delete(model2);
		
		assertFalse(logDao.existsById(id));*/
	}
	
	@Test
	public void testBatch() {
		
		List<LogModel> list=new ArrayList<>();
		
		LogModel model=new LogModel();
		model.setCreateDate(new Date());
		model.setDataId(0L);
		model.setDealData("test");
		model.setStatus(LogStatusEnum.SUCCESS);
		model.setTableId(0L);
		model.setTableName("tablename");
		list.add(model);
		
		LogModel model2=new LogModel();
		model.setCreateDate(new Date());
		model.setDataId(0L);
		model.setDealData("test");
		model.setStatus(LogStatusEnum.SUCCESS);
		model.setTableId(0L);
		model.setTableName("tablename");
		
		list.add(model2);
		
		logJdbcDao.saveAll(list);
	}

}
