package com.ipph.migratecore.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
		
		patentUploadService.exportInsertSqlByBatchLogIdAndTableId(batchLogId,tableId,f,isApply,false);
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
		
		patentUploadService.exportInsertSqlByBatchLogIdAndTableId(batchLogId,tableId,f,isApply,false);
	}
	
	@Test
	public void exportApplyPatent2ImportExcel() throws IOException {
		Long batchLogId=19699L;//
		Long tableId=19686L;
		File f=new File(filePath+"\\importApply.txt");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportPatent2ImportExcel(batchLogId,tableId,f);
		
		getImportedPatent2UpdSql("importApply.sql", f,true);
		
	}
	
	@Test
	public void exportAuthPatent2ImportExcel() throws IOException {
		Long batchLogId=19699L;//
		Long tableId=19686L;
		File f=new File(filePath+"\\importAuth.txt");
		
		if(!f.exists()) {
			f.createNewFile();
		}
		
		patentUploadService.exportPatent2ImportExcel(batchLogId,tableId,f);
		
		getImportedPatent2UpdSql("importAuth.sql", f,false);
	}
	
	private void getImportedPatent2UpdSql(String fileName,File f,boolean isApply) throws IOException {
		File f2=new File(filePath+"\\"+fileName);
		
		if(!f2.exists()) {
			f2.createNewFile();
		}
		
		StringBuffer stringBuffer=new StringBuffer();
		try(BufferedReader  br=new BufferedReader(new FileReader(f))){
			String appNumber=null;
			while((appNumber=br.readLine())!=null) {
				String sql=getSql(appNumber, isApply);
				if(null!=sql) {
					stringBuffer.append(sql);
				}
			}
		}
		
		if(stringBuffer.length()>0) {
			//写入到文件
			try(FileOutputStream fos=new FileOutputStream(f2);){
				fos.write(stringBuffer.toString().getBytes(), 0, stringBuffer.toString().getBytes().length);
			}
		}
	}
	
	private String getSql(String appNumber,boolean isApply) {
		if(null==appNumber||"".equals(appNumber)) {
			return null;
		}
		
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append("update z_patent set ");
		if(isApply) {
			stringBuffer.append("isApply=1");
		}else {
			stringBuffer.append("isCity=1");
		}
		
		stringBuffer.append(" where appNumber='").append(appNumber).append("';");
		
		return stringBuffer.toString();
	}

}
