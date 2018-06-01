package com.ipph.migratecore.service;


import java.text.ParseException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.deal.exception.FormatException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PatentServiceTest {

	@Resource
	private PatentService patentService;
	
	@Test
	public void testPatentConnection() {
		String appNumber="CN201210424464.6";
		
		try {
			patentService.addPatent(appNumber);
		} catch (ParseException | FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAddBatch() {
		Long batchLogId=90L;
		
		patentService.addBatch(batchLogId);
	}
	
	
	@Test 
	public void testIsPatentExistsFromFee() {
		String appNumber="CN201711291121.6";
		System.out.println(patentService.isPatentExistsFromFee(appNumber));
	}
	
	@Test
	public void testAddBatchByThread() {
		Long batchLogId=17692L;
		
		patentService.addBatchByThread(batchLogId);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
