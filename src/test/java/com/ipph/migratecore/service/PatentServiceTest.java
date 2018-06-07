package com.ipph.migratecore.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PatentServiceTest {

	@Resource
	private PatentService patentService;
	
	/*@Test
	public void testPatentConnection() {
		String appNumber="CN201210424464.6";
		
		try {
			patentService.addPatent(appNumber);
		} catch (ParseException | FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	/*@Test
	public void testPatentInfoList() {
		List<String> appNumberList=new ArrayList<>();
		
		appNumberList.add("CN201720396828.2");
		appNumberList.add("CN201710546883.X");
		
		patentService.processPatent(appNumberList);
		
		PatentInfo patent=patentService.findByAppNumber("CN201720396828.2");
		
		assertNotNull(patent);
		
	}*/
	
	/*@Test 
	public void testIsPatentExistsFromFee() {
		String appNumber="CN201711291121.6";
		System.out.println(patentService.isPatentExistsFromFee(appNumber));
	}*/
	
	/*@Test
	public void testProcessNotFound() {
		Long batchLogId=90L;
		
		patentService.processNotFound(batchLogId);
	}*/
	
	@Test
	public void testProcessNotFoundExceptionByThread() {
		Long batchLogId=17692L;
		
		patentService.processNotFoundExceptionByThread(batchLogId);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testProcessFormatException() {
		Long batchLogId=1L;
		
		
	}
	
}
