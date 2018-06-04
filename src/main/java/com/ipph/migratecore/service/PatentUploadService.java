package com.ipph.migratecore.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.ipph.migratecore.dao.PatentUploadDao;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.PatentUploadModel;
import com.ipph.migratecore.patent.util.DateFormatUtil;
import com.ipph.migratecore.util.IdGenerator;

@Service
public class PatentUploadService {
	@Resource
	private PatentUploadDao patentUploadDao;
	@Resource
	private LogService logService;
	
	@Transactional
	public void save(PatentUploadModel model) {
		patentUploadDao.save(model);
	}
	
	public List<PatentUploadModel> list(Pageable pageable){
		return patentUploadDao.findAll(pageable).getContent();
	}
	
	/**
	 * 上传内容
	 * @param result
	 * @throws ParseException 
	 */
	public void uploadXls(List<Map<String,String>> result) throws ParseException {
		
		//定义匹配规则
		String[] rule= {"patentNo","costType","patentName","requestDate","authDate","requestCountry","firstRequest","transferMoney","linkMan","address","linkTel","batch","createTime","status","sourceType","errorAppNo"};
		
		if(null!=result) {
			for(Map<String,String> dataRow:result) {
        		PatentUploadModel model=getModel(rule, dataRow);
        		if(null!=model) {
        			save(model);
        		}
        	}
		}
	}
	
	/**
	 * 通过匹配规则封装数据对象
	 * @param rule
	 * @param row
	 * @return
	 * @throws ParseException 
	 */
	private PatentUploadModel getModel(String[] rule,Map<String,String> row) throws ParseException {
		if(null!=row&&row.size()>0) {
			PatentUploadModel model=new PatentUploadModel();
			for(String key:row.keySet()) {
				int cellNum=Integer.parseInt(key);
				if(cellNum<rule.length) {
					String fieldName=rule[cellNum];
					if(null!=fieldName) {
						if(null==row.get(key)||"".equals(row.get(key))) {
							continue;
						}
						Field field=ReflectionUtils.findField(PatentUploadModel.class, fieldName);
						Object value=row.get(key);
						if(field.getType() == Date.class) {
							value=DateFormatUtil.parse(row.get(key),"yyyy/MM/dd");
						}else if(field.getType() == Integer.class){
							value=Integer.parseInt(row.get(key));
						}else {
							value=formatUploadData(row.get(key),field.getName());
						}
						ReflectionUtils.makeAccessible(field);
						ReflectionUtils.setField(field, model,value);
					}
				}
			}
			
			//处理国家
			processCountry(model);
			
			return model;
		}
		return null;
	}
	/**
	 * 格式化上传数据
	 * @param value
	 * @return
	 */
	private Object formatUploadData(String value,String fieldName) {
		if("costType".equals(fieldName)) {
			if((value.indexOf("国外申请")!=-1)) {
				return "SQ5";
			}else if(value.indexOf("国外授权")!=-1){
				return "SQ6";
			} else if(value.indexOf("授权")!=-1) {
				return "SQ2";
			}else if(value.indexOf("申请")!=-1) {
				return "SQ1";
			}
		}
		return value;
	}
	/**
	 * 处理国家
	 * @param model
	 */
	private void processCountry(PatentUploadModel model) {
		if(null!=model) {
			String patentNo=model.getPatentNo();
			String costType=model.getCostType();
			if(null!=patentNo&&null!=costType) {
				if(costType.equals("SQ5")||costType.equals("SQ6")) {
					if(patentNo.toUpperCase().indexOf("US")!=-1) {
						model.setRequestCountry("US");
					}else {
						model.setRequestCountry("CN");
					}
				}
			}
		}
	}
	
	/**
	 * 导出update语句
	 * @param batchLogId
	 * @param f
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void exportUpdateSqlByBatchLogId(Long batchLogId,Long tableId,File f,boolean isApply) throws FileNotFoundException, IOException {
		List<LogModel> logList=logService.getSuccessLogs(batchLogId, tableId, null);
		if(null!=logList&&logList.size()>0) {
			StringBuffer stringBuffer=new StringBuffer();
			for(LogModel log:logList) {
				stringBuffer.append("update z_patent set ");
				if(isApply) {
					stringBuffer.append("isApply=1");
				}else {
					stringBuffer.append("isCity=1");
				}
				
				stringBuffer.append(" where appNumber='").append(log.getDealData()).append("';");
			}
			
			//写入到文件
			try(FileOutputStream fos=new FileOutputStream(f);){
				fos.write(stringBuffer.toString().getBytes(), 0, stringBuffer.toString().getBytes().length);
			}
		}
	}
	
	/**
	 * 导出insert语句
	 * @param batchLogId
	 * @param tableId
	 * @param f
	 * @param isApply
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void exportInsertSqlByBatchLogId(Long batchLogId,Long tableId,File f,boolean isApply) throws FileNotFoundException, IOException {
		List<LogModel> logList=logService.getSuccessLogs(batchLogId, tableId, null);
		if(null!=logList&&logList.size()>0) {
			StringBuffer stringBuffer=new StringBuffer();
			for(LogModel log:logList) {
				//获取专利上传对象
				PatentUploadModel model=patentUploadDao.findById(log.getDataId()).get();

				if(null!=model) {
					stringBuffer.append("insert into wf_patent_support_foreign (id,patentName,applyNo,applyDate,authDate,country,applyer,money,statusType,type) values ( ")
					.append(IdGenerator.getId()).append(",");
					if(null!=model.getPatentName()) {
						stringBuffer.append("'").append(model.getPatentName()).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getPatentNo()) {
						stringBuffer.append("'").append(model.getPatentNo()).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getRequestDate()) {
						stringBuffer.append("'").append(DateFormatUtil.format(model.getRequestDate())).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getAuthDate()) {
						stringBuffer.append("'").append(DateFormatUtil.format(model.getAuthDate())).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getRequestCountry()) {
						stringBuffer.append("'").append(model.getRequestCountry()).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getFirstRequest()) {
						stringBuffer.append("'").append(model.getFirstRequest()).append("',");
					}else {
						stringBuffer.append("null,");
					}
					if(null!=model.getTransferMoney()) {
						stringBuffer.append(model.getTransferMoney()).append(",");
					}else {
						stringBuffer.append("null,");
					}
					stringBuffer.append("1,");
					if(isApply) {
						stringBuffer.append("0");
					}else {
						stringBuffer.append("1");
					}
					
					stringBuffer.append(");");
				}
			}
			
			//写入到文件
			try(FileOutputStream fos=new FileOutputStream(f);){
				fos.write(stringBuffer.toString().getBytes(), 0, stringBuffer.toString().getBytes().length);
			}
		}
	}
	
	/**
	 * 获取待导入的申请号信息
	 * @param batchLogId
	 * @param tableId
	 * @param f
	 * @param isApply
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void exportPatent2ImportExcel(Long batchLogId,Long tableId,File f) throws FileNotFoundException, IOException {
		List<LogModel> logList=logService.getPatentFailLogs(batchLogId, tableId, null);
		
		StringBuffer stringBuffer=new StringBuffer();
		if(null!=logList) {
			for(LogModel log:logList) {
				if(null!=log&&null!=log.getPatentInfo()&&!"未公开".equals(log.getPatentInfo().getStatus())) {
					stringBuffer.append(log.getPatentInfo().getAppNumber()).append(System.getProperty("line.separator"));
				}
			}
		}
		//写入到文件
		try(FileOutputStream fos=new FileOutputStream(f);){
			fos.write(stringBuffer.toString().getBytes(), 0, stringBuffer.toString().getBytes().length);
		}
	}
}
