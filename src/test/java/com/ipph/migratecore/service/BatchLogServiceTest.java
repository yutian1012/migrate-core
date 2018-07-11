package com.ipph.migratecore.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.model.BatchLogModel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BatchLogServiceTest {
	
	@Resource
	private BatchLogService batchLogService;

	@Test
	public void test() {
		List<BatchLogModel> batchLogList=batchLogService.getList(true);
		
		assertNotNull(batchLogList);
		
		assertTrue(batchLogList.size()>0);
	}

}
