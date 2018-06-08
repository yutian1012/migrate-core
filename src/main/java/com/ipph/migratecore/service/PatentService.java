package com.ipph.migratecore.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipph.migratecore.dao.PatentDao;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.deal.format.PatentNoFormater;
import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.PatentInfo;
import com.ipph.migratecore.patent.PatentInterfaceHttpClient;
import com.ipph.migratecore.patent.util.DateFormatUtil;

@Service
//@Transactional
public class PatentService {
	@Resource
	private PatentDao patentDao;
	@Resource 
	private LogService logService;
	@Resource
	private PatentInterfaceHttpClient patentInterfaceHttpClient;
	@Resource
	private PatentInfoCheckService patentInfoCheckService;
	@Resource
	private TableService tableService;
	
	private PatentNoFormater formater=new PatentNoFormater();
	
	public PatentInfo findByAppNumber(String appNumber) {
		return patentDao.findByAppNumber(appNumber);
	}
	
	/**
	 * 添加专利数据
	 * @param patent
	 * @param appNumber
	 */
	@Transactional
	private void addPatent(PatentInfo patent,String appNumber) {
		
		if(patent==null) {
			if(null!=appNumber&&isPatentExistsFromFee(appNumber)) {
				patent=new PatentInfo();
				patent.setAppNumber(appNumber);
				patent.setStatus("未公开");
			}
		}
		
		if(null!=patent&&!patentDao.existsByAppNumber(patent.getAppNumber())) {
			patentDao.save(patent);
		}
	}
	
	
	private ExecutorService executorService=Executors.newFixedThreadPool(10);
	
	//dbName,title,appNumber,appDate,pubNumber,pubDate,patentNo,address,appCoun,proCode,statusCode,initMainIpc,mainIpc,addrCounty,addrCity,pubYear,den,addrProvince,agencyName,divideInitAppNo,iapp,ipub,agentName,applicantName,invent
	private String column="appNumber,title,appDate,statusCode,applicantName";
	
	private class PatentRunnable implements Runnable{
		private String appNumber;
		private Long oId;
		private Long batchLogId;
		private String costType;
		
		public PatentRunnable(String appNumber,Long oId,Long batchLogId,String costType) {
			this.appNumber=appNumber;
			this.oId=oId;
			this.batchLogId=batchLogId;
			this.costType=costType;
		}
		
		@Override
		public void run() {
			try {
				checkPatent(procesPatent(appNumber),oId,appNumber,batchLogId,costType);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 多线程方式获取专利数据
	 * 获取申请号，单个申请号作为一个线程查询专利数据
	 * @param batchLogId
	 */
	public void processNotFoundExceptionByThread(Long batchLogId) {
		Long size=logService.countByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION);
		int batch=500;
		for(int i=0;i*batch<size;i++) {
			List<LogModel> list=logService.getListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,PageRequest.of(i, batch));
			if(null!=list&&list.size()>0) {
				for(LogModel logModel:list) {
					Map<String,Object> result=tableService.getRecord(logModel.getTableId(),logModel.getDataId());
					if(null!=result) {
						String oAppNumber=(String)result.get("patentNo");//获取源申请号
						String costType=(String)result.get("costType");//获取申请类型
						executorService.execute(new PatentRunnable(oAppNumber,logModel.getDataId(),batchLogId,costType));
						//executorService.execute(new PatentRunnable(logModel.getDealData(),logModel.getDataId(),batchLogId));
						
					}
					
				}
				list.clear();
			}
		}
	}
	private class PatentFormatExceptionRunnable implements Runnable{
		//private String appNumber;
		private String applicant;//申请人
		private String appDate;//申请日
		private String patentName;//专利名称
		private String dbType;//数据库
		private Long oId;//历史数据主键
		private Long batchLogId;
		private String oAppNumber;
		private String costType;
		
		public PatentFormatExceptionRunnable(String applicant,String appDate,String patentName,String dbType,Long oId,Long batchLogId,String oAppNumber,String costType) {
			this.applicant=applicant;
			this.appDate=appDate;
			this.patentName=patentName;
			this.dbType=dbType;
			this.oId=oId;
			this.batchLogId=batchLogId;
			this.oAppNumber=oAppNumber;
			this.costType=costType;
		}
		
		@Override
		public void run() {
			try {
				List<PatentInfo> patentList=processPatent(applicant, appDate, patentName, dbType);
				
				checkPatent(patentList,oId,batchLogId,oAppNumber,costType);
				/*if(null!=patentList) {
				}*/
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 多线程方式格式化错误处理
	 * 获取申请号，单个申请号作为一个线程查询专利数据
	 * @param batchLogId
	 */
	public void processFormatExceptionByThread(Long batchLogId) {
		//获取table信息
		
		Long size=logService.countByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.FORMART_EXCEPTION);
		int batch=500;
		for(int i=0;i*batch<size;i++) {
			//List<Object> list=logService.getDealDataListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,PageRequest.of(i, batch));
			List<LogModel> list=logService.getListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.FORMART_EXCEPTION,PageRequest.of(i, batch));
			if(null!=list&&list.size()>0) {
				for(LogModel logModel:list) {
					Map<String,Object> result=tableService.getRecord(logModel.getTableId(),logModel.getDataId());
					if(null!=result) {
						Date appDate=(Date) result.get("requestDate");
						String oAppNumber=(String)result.get("patentNo");//获取源申请号
						String costType=(String)result.get("costType");//获取申请类型
						executorService.execute(new PatentFormatExceptionRunnable((String)result.get("firstRequest"), 
								null!=appDate?DateFormatUtil.format(appDate, "yyyy.MM.dd"):null, 
								(String)result.get("patentName"), 
								getDbType(costType), 
								logModel.getDataId(),
								batchLogId,
								oAppNumber,
								costType));
					}
				}
				list.clear();
			}
		}
	}
	
	@Transactional
	public void checkPatent(List<PatentInfo> patentList,Long oId,Long batchLogId,String oAppNumber,String costType) {
		StringBuilder stringBuilder=new StringBuilder();
		if(null!=patentList) {
			//存储专利信息
			for(PatentInfo patent:patentList) {
				//patentDao.save(patent);
				if(null!=patent) {
					addPatent(patent,null);
				}
				stringBuilder.append(patent.getAppNumber()).append(",");
			}
		}
		//处理check信息
		patentInfoCheckService.save(oId,LogMessageEnum.FORMART_EXCEPTION,stringBuilder.length()>0?stringBuilder.toString():null,batchLogId,oAppNumber,costType);
	}
	
	@Transactional
	public void checkPatent(PatentInfo patent,Long oId,String appNumber,Long batchLogId,String costType) {
		addPatent(patent, appNumber);
		//处理check信息
		patentInfoCheckService.save(oId,LogMessageEnum.NOFOUND_EXCEPTION,null!=patent?patent.getAppNumber():null,batchLogId,appNumber,costType);
	}
	/**
	 * 获取专利接口数据库源
	 * @param costType
	 * @return
	 */
	private String getDbType(String costType) {
		if(null!=costType) {
			if("SQ1".equals(costType)||"SQ3".equals(costType)) {
				return "fmzl,syxx,wgzl";
			}else if("SQ2".equals(costType)||"SQ4".equals(costType)) {
				return "fmsq,syxx,wgzl";
			}
		}
		return "fmzl,syxx,wgzl";
	}
	
	
	/**
	 * 保存专利
	 * @param appNumber
	 * @return
	 * @throws ParseException
	 * @throws FormatException 
	 */
	/*private void addPatent(String appNumber) throws ParseException, FormatException {
		appNumber=(String) formater.format(null, appNumber);
		
		if(!patentDao.existsByAppNumber(appNumber)) {
			PatentInfo patent=procesPatent(appNumber);
			addPatent(patent,appNumber);
		}
		
	}*/
	
	/**
	 * 批次添加
	 */
	@Deprecated
	public void processNotFound(Long batchLogId) {
		//通过批次日志获取专利的数据
		Long size=logService.countByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION);
		int batch=500;
		for(int i=0;i*batch<size;i++) {
			List<Object> list=logService.getDealDataListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,PageRequest.of(i, batch));
			if(null!=list&&list.size()>0) {
				List<String> appNumberList=new ArrayList<>(list.size()*2);
				for(Object temp:list) {
					String appNumber=null;
					try {
						appNumber = (String) formater.format(null,temp);
						if(null!=appNumber) {
							//判断数据库中是否已经存在了
							if(!patentDao.existsByAppNumber(appNumber)) {
								appNumberList.add(appNumber);
							}
						}
					} catch (FormatException e) {
						e.printStackTrace();
					}
				}
				if(appNumberList.size()>0) {
					processPatent(appNumberList);
				}
				
				appNumberList.clear();
				list.clear();
			}
		}
	}
	/**
	 * 处理批次
	 * 不能处理检索不到专利
	 * @param batchLogId
	 * @param pageable
	 */
	@Deprecated
	private void processPatent(List<String> appNumberList) {
		if(appNumberList.size()>0) {
			JSONArray array=getPatentListFromInterface(appNumberList);
			if(null!=array) {
				for(int i=0;i<array.size();i++) {
					if(null!=array.getJSONObject(i)) {
						try {
							PatentInfo patent=getPatentFromJson(array.getJSONObject(i));
							if(null!=patent) {
								patentDao.save(patent);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				array.clear();
			}
		}
	}
	
	/**
	 * 获取专利对象
	 * @param appNumber
	 * @return
	 * @throws ParseException
	 */
	private PatentInfo procesPatent(String appNumber) throws ParseException {
		//先从户籍科中获取
		PatentInfo patent=patentDao.findByAppNumber(appNumber);
		if(null!=patent) {
			return patent;
		}
		
		//否则再从数据库中查找
		JSONObject jsonData=getPatentFromInterface(appNumber);
		if(null!=jsonData) {
			return getPatentFromJson(jsonData);
		}
		return null;
	}
	/**
	 * 获取专利信息
	 * @param applicant
	 * @param appDate
	 * @param patentName
	 * @param dbType
	 * @return
	 * @throws ParseException
	 */
	public List<PatentInfo> processPatent(String applicant,String appDate,String patentName,String dbType) throws ParseException{
		StringBuilder stringBuilder=new StringBuilder();
		
		if(patentName==null||"".equals(patentName)) {
			return null;
		}
		
		stringBuilder.append("名称='").append(patentName).append("'");
		if(null!=appDate&&!"".equals(appDate)) {
			stringBuilder.append(" and 申请日='").append(appDate).append("'");
		}
		if(null!=applicant&&!"".equals(applicant)) {
			stringBuilder.append(" and 申请（专利权）人='").append(applicant).append("'");
		}
		
		JSONArray jsonArray=getPatentListFromInterfaceByCondtion(stringBuilder.toString(),dbType);
		
		if(null!=jsonArray&&jsonArray.size()>0) {
			List<PatentInfo> patentInfoList=new ArrayList<>(jsonArray.size());
			for(int i=0;i<jsonArray.size();i++) {
				PatentInfo patent=getPatentFromJson(jsonArray.getJSONObject(i));
				if(null!=patent) {
					patentInfoList.add(patent);
				}
			}
			return patentInfoList;
		}
		return null;
	}
	
	/**
	 * 解析json数据
	 * @param jsonData
	 * @return
	 * @throws ParseException 
	 */
	private PatentInfo getPatentFromJson(JSONObject jsonData) throws ParseException {
		PatentInfo patent=new PatentInfo();
		patent.setAppName(jsonData.getString("title"));
		patent.setAppNumber(jsonData.getString("appNumber"));
		patent.setAppDate(DateFormatUtil.parse(jsonData.getString("appDate"),"yyyy.MM.dd"));
		String status=jsonData.getString("statusCode");
		patent.setStatus(null!=status?getPatentStatus2CN(status):"无效");
		patent.setOriginApplicant(jsonData.getString("applicantName"));
		return patent;
	}
	
	/**
	 * 专利状态
	 * @param status
	 * @return
	 */
	private static String getPatentStatus2CN(String status){
		if(null==status||"".equals(status))return status;
		if(Pattern.matches("^1.*", status)){
			return "有效";
		}else if(Pattern.matches("^2.*", status)){
			return "无效";
		}else{
			return "在审";
		}
	}
	/**
	 * 从接口检索获取json
	 * @param appNumber
	 * @return
	 */
	private JSONObject getPatentFromInterface(String appNumber) {
		try {
			//String dbName=PatentValidationUtil.getPatentInterfaceDbName(appNumber);
			return patentInterfaceHttpClient.getPatentByAppNumber(appNumber,"fmzl,syxx,wgzl,fmsq",column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/**
	 * 获取专利集合
	 * @param appNumberList
	 * @return
	 */
	private JSONArray getPatentListFromInterface(List<String> appNumberList) {
		try {
			return patentInterfaceHttpClient.getPatentList(appNumberList, "fmzl,fmsq,syxx,wgzl", 0, appNumberList.size()*2, column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 通过查询条件查询专利
	 * @param condition
	 * @param dbType
	 * @return
	 */
	private JSONArray getPatentListFromInterfaceByCondtion(String condition,String dbType) {
		try {
			return patentInterfaceHttpClient.getPatentList(condition, dbType, 0, 2,column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 判断专利是否在费用接口中存在
	 * @param appNumber
	 * @return
	 */
	private boolean isPatentExistsFromFee(String appNumber) {
		try {
			JSONArray array=patentInterfaceHttpClient.getFees(appNumber);
			if(null!=array&&array.size()>0) {
				System.out.println(array.toJSONString());
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
