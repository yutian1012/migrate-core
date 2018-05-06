package com.ipph.migratecore.dao;

import static org.junit.Assert.*;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.enumeration.BatchStatusEnum;
import com.ipph.migratecore.model.BatchModel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BatchDaoTest {
	
	@Resource
	private BatchDao batchDao;

	@Test
	public void testAdd() {
		
		Long id=0L;
		
		BatchModel model=new BatchModel();
		model.setSize(100);
		model.setStatus(BatchStatusEnum.FAIL);
		model.setCreateDate(new Date());
		model=batchDao.save(model);
		
		assertNotNull(model.getId());
		
		id=model.getId();
		
		//从数据库中检索
		BatchModel model2=batchDao.findById(id).get();
		
		assertNotNull(model2);
		
		//从数据库中删除
		batchDao.delete(model2);
		
		assertFalse(batchDao.existsById(id));
	}
}
