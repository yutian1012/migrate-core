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
import com.ipph.migratecore.deal.format.JsonMethodFormater;
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
	@Resource
	private TableService tableService;
	
	//申请号信息更正
	private String[] processPatentNo= {"sourceType","errorAppNo","patentNo","dataId","costType"};
	//专利补录
	private String[] processPatentInsert={"sourceType","patentNo","costType","patentName","requestDate","authDate","requestCountry","firstRequest","transferMoney","linkMan","address","linkTel","batch","createTime","status"};
	//上传补充专利补助状态
	private String[] processPatentSupplement={"sourceType","patentNo","dataId","costType"};
	//批次更新--真理历史数据
	private String[] processPatentBatch= {"sourceType","patentNo","dataId","batch"};
	
	private JsonMethodFormater jsonMethodFormater=new JsonMethodFormater();
	
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
		
		if(null!=result) {
			for(Map<String,String> dataRow:result) {
				String[] rule=null;
				if(null!=dataRow.get("0")) {
					rule=getRuleBySourceType(dataRow.get("0"));
				}
				if(null==rule) {
					continue;
				}
        		PatentUploadModel model=getModel(rule, dataRow);
        		if(null!=model) {
        			save(model);
        		}
        	}
		}
	}
	/**
	 * 获取excel上传字段
	 * @param sourceType
	 * @return
	 */
	public String[] getRuleBySourceType(String sourceType) {
		if("申请号更正".equals(sourceType)) {
			return processPatentNo;
		}else if("申请补录".equals(sourceType)) {
			return processPatentInsert;
		}else if("上传补充专利补助状态".equals(sourceType)) {
			return processPatentSupplement;
		} else if("批次更新".equals(sourceType)) {
			return processPatentBatch;
		}
		return null;
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
						}else if(field.getType()==Long.class){
							value=Long.parseLong(row.get(key));
						}else {
							//value=formatUploadData(row.get(key),field.getName());
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
	/*private Object formatUploadData(String value,String fieldName) {
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
	}*/
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
		String result=getUpdateSqlByBatchLogIdAndTableId(batchLogId, tableId, isApply);
		if(null!=result) {
			//写入到文件
			try(FileOutputStream fos=new FileOutputStream(f);){
				//fos.write(result.getBytes(), 0, result.getBytes().length);
				fos.write(result.getBytes());
			}
			
		}
	}
	/**
	 * 获取update语句执行sql
	 * @param batchLogId
	 * @param tableId
	 * @param isApply
	 * @return
	 */
	public String getUpdateSqlByBatchLogIdAndTableId(Long batchLogId,Long tableId,boolean isApply) {
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
			return stringBuffer.toString();
		}
		
		return null;
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
	public void exportInsertSqlByBatchLogIdAndTableId(Long batchLogId,Long tableId,File f,boolean isApply,Boolean isSource) throws FileNotFoundException, IOException {
		//写入到文件
		String result=getPctInsertSqlByBatchLogIdAndTableId(batchLogId, tableId, isApply,isSource);
		if(null!=result) {
			try(FileOutputStream fos=new FileOutputStream(f);){
				fos.write(result.getBytes());
			}
		}
	}
	/**
	 * 获取pct插入sql语句
	 * @param batchLogId
	 * @param tableId
	 * @param isApply
	 * @return
	 */
	public String getPctInsertSqlByBatchLogIdAndTableId(Long batchLogId,Long tableId,boolean isApply,boolean isSource) {
		List<LogModel> logList=logService.getSuccessLogs(batchLogId, tableId, null);
		if(null!=logList&&logList.size()>0) {
			StringBuffer stringBuffer=new StringBuffer();
			for(LogModel log:logList) {
				//获取专利上传对象
				if(isSource) {
					stringBuffer.append(getFromSource(tableId,log.getDataId(), isApply));
				}else {
					stringBuffer.append(getFromPatentUploadModel(log.getDataId(), isApply));
				}
			}
			return stringBuffer.toString();
		}
		return null;

	}
	/**
	 * 从源库中获取信息
	 * @param dataId
	 * @param isApply
	 * @return
	 */
	public String getFromSource(Long tableId,Long dataId,boolean isApply) {
		StringBuffer stringBuffer=new StringBuffer();
		Map<String,Object> result=tableService.getRecord(tableId, dataId);
		
		if(null!=result) {
			stringBuffer.append("insert into wf_patent_support_foreign (id,patentName,applyNo,applyDate,authDate,country,countryCode,applyer,money,statusType,type) values ( ")
			.append(IdGenerator.getId()).append(",");
			
			if(null!=result.get("patentName")) {
				stringBuffer.append("'").append(result.get("patentName")).append("',");
			}else {
				stringBuffer.append("null,");
			}
			
			if(null!=result.get("patentNo")) {
				stringBuffer.append("'").append(result.get("patentNo")).append("',");
			}else {
				stringBuffer.append("null,");
			}
			
			if(null!=result.get("requestDate")) {
				stringBuffer.append("'").append(DateFormatUtil.format((Date)result.get("requestDate"))).append("',");
			}else {
				stringBuffer.append("null,");
			}
			
			if(null!=result.get("authDate")) {
				stringBuffer.append("'").append(DateFormatUtil.format((Date)result.get("authDate"))).append("',");
			}else {
				stringBuffer.append("null,");
			}
			
			if(null!=result.get("requestCountry")) {
				
				String code=(String) jsonMethodFormater.format("{'QT':'CN','MG':'US','ZG':'CN','RB':'JP','OM':'EP'}", result.get("requestCountry"));
				
				stringBuffer.append("'").append(getCountry(code)).append("',")
					.append("'").append(code).append("',");
			}else {
				stringBuffer.append("null,").append("null,");
			}
			
			if(null!=result.get("firstRequest")) {
				stringBuffer.append("'").append(result.get("firstRequest")).append("',");
			}else {
				stringBuffer.append("null,");
			}
			
			if(null!=result.get("transfermoney")) {
				stringBuffer.append(result.get("transfermoney")).append(",");
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
		return stringBuffer.toString();
	}
	
	/**
	 * 从上传信息中获取数据
	 * @param dataId
	 * @param isApply
	 * @return
	 */
	public String getFromPatentUploadModel(Long dataId,boolean isApply) {
		StringBuffer stringBuffer=new StringBuffer();
		PatentUploadModel model=patentUploadDao.findById(dataId).get();

		if(null!=model) {
			stringBuffer.append("insert into wf_patent_support_foreign (id,patentName,applyNo,applyDate,authDate,country,countryCode,applyer,money,statusType,type) values ( ")
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
				stringBuffer.append("'").append(getCountry(model.getRequestCountry())).append("',")
					.append("'").append(model.getRequestCountry()).append("',");
			}else {
				stringBuffer.append("null,").append("null,");
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
		return stringBuffer.toString();
	}
	
	/**
	 * 根据代码获取国家
	 * @param code
	 * @return
	 */
	private String getCountry(String code) {
		if("CN".equals(code)) {
			return "中国";
		}else if("US".equals(code)) {
			return "美国";
		}
		return null;
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
