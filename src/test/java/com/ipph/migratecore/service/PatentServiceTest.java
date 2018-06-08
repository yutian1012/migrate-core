package com.ipph.migratecore.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipph.migratecore.model.PatentInfo;

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
	
	/*@Test
	public void testProcessPatent() throws ParseException {
		String applicant="乐山师范学院";
		String appDate="2015.07.09";
		String patentName="采用计算机验证码技术的大数据分析系统";
		String dbType="syxx";
		
		List<PatentInfo> list=patentService.processPatent(applicant, appDate, patentName, dbType);
		
		assertNotNull(list);
		
		assertEquals(1, list.size());
		
	}*/
	
	@Test
	public void testProcessNotFoundExceptionByThread() {
		Long batchLogId=13L;
		
		patentService.processNotFoundExceptionByThread(batchLogId);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testProcessFormatExceptionByThread() {
		Long batchLogId=13L;
		
		patentService.processFormatExceptionByThread(batchLogId);
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
