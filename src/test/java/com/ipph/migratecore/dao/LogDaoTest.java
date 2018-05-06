package com.ipph.migratecore.dao;

import static org.junit.Assert.*;

import java.util.Date;

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
	private LogDao logDao;

	@Test
	public void test() {
		Long id=0L;
		
		LogModel model=new LogModel();
		model.setBatchId(0L);
		model.setParentBatchId(0L);
		model.setCreateDate(new Date());
		model.setDataId(0L);
		model.setDealData("test");
		model.setStatus(LogStatusEnum.SUCCESS);
		model.setTableId(0L);
		model.setTableName("tablename");
		model=logDao.save(model);
		
		assertNotNull(model.getId());
		
		id=model.getId();
		
		//从数据库中检索
		LogModel model2=logDao.findById(id).get();
		
		assertNotNull(model2);
		
		//从数据库中删除
		logDao.delete(model2);
		
		assertFalse(logDao.existsById(id));
	}

}
