package com.ipph.migratecore.service;

import static org.junit.Assert.*;

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
	
	@Test
	public void testPatentConnection() {
		String appNumber="CN201210424464.6";
		
		String result=patentService.getPatent(appNumber);
		
		assertNotNull(result);
		
		System.out.println(result);
	}

}
