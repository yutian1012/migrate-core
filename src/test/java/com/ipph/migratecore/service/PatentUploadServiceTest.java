package com.ipph.migratecore.service;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PatentUploadServiceTest {
	
	String filePath="D:\\work\\file\\eip\\湖南长沙\\数据初始化操作\\补充";
	
	@Resource
	private PatentUploadService patentUploadService;
	
	@Test
	public void testExportApplyUpdateSql() throws FileNotFoundException, IOException {
		Long batchLogId=19699L;
		Long tableId=19686L;
		boolean isApply=true;
		File f=new File(filePath+"\\exportApplyUpdate.sql");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportUpdateSqlByBatchLogId(batchLogId,tableId,f,isApply);
	}
	
	@Test
	public void testExportAuthUpdateSql() throws FileNotFoundException, IOException {
		Long batchLogId=19699L;
		Long tableId=19687L;
		boolean isApply=false;
		File f=new File(filePath+"\\exportAuthUpdate.sql");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportUpdateSqlByBatchLogId(batchLogId,tableId,f,isApply);
	}
	
	
	@Test
	public void testExportPctApplyInsertSql() throws FileNotFoundException, IOException {
		Long batchLogId=19700L;
		Long tableId=19688L;
		boolean isApply=true;
		File f=new File(filePath+"\\exportPctApplyInsert.sql");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportInsertSqlByBatchLogId(batchLogId,tableId,f,isApply);
	}
	
	@Test
	public void testExportPctAuthInsertSql() throws FileNotFoundException, IOException {
		Long batchLogId=19700L;
		Long tableId=19689L;
		boolean isApply=false;
		File f=new File(filePath+"\\exportPctAuthInsert.sql");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportInsertSqlByBatchLogId(batchLogId,tableId,f,isApply);
	}

}
