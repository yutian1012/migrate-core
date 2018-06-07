package com.ipph.migratecore.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
		if(null!=patent) {
			patentDao.save(patent);
			return;
		}
		
		if(null!=appNumber&&isPatentExistsFromFee(appNumber)) {
			patent=new PatentInfo();
			patent.setAppNumber(appNumber);
			patent.setStatus("未公开");
			patentDao.save(patent);
		}
	}
	
	
	private ExecutorService executorService=Executors.newFixedThreadPool(10);
	
	//dbName,title,appNumber,appDate,pubNumber,pubDate,patentNo,address,appCoun,proCode,statusCode,initMainIpc,mainIpc,addrCounty,addrCity,pubYear,den,addrProvince,agencyName,divideInitAppNo,iapp,ipub,agentName,applicantName,invent
	private String column="appNumber,title,appDate,statusCode,applicantName";
	
	private class PatentRunnable implements Runnable{
		private String appNumber;
		
		public PatentRunnable(String appNumber) {
			this.appNumber=appNumber;
		}
		
		@Override
		public void run() {
			try {
				addPatent(appNumber);
			} catch (ParseException | FormatException e) {
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
			List<Object> list=logService.getListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,PageRequest.of(i, batch));
			if(null!=list&&list.size()>0) {
				for(Object appNumber:list) {
					executorService.execute(new PatentRunnable((String) appNumber));
				}
				list.clear();
			}
		}
	}
	
	
	/**
	 * 保存专利
	 * @param appNumber
	 * @return
	 * @throws ParseException
	 * @throws FormatException 
	 */
	
	private void addPatent(String appNumber) throws ParseException, FormatException {
		appNumber=(String) formater.format(null, appNumber);
		
		if(!patentDao.existsByAppNumber(appNumber)) {
			PatentInfo patent=procesPatent(appNumber);
			addPatent(patent,appNumber);
		}
		
	}
	
	/**
	 * 批次添加
	 */
	@Deprecated
	public void processNotFound(Long batchLogId) {
		//通过批次日志获取专利的数据
		Long size=logService.countByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION);
		int batch=500;
		for(int i=0;i*batch<size;i++) {
			List<Object> list=logService.getListByBatchLogIdAndMessageType(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,PageRequest.of(i, batch));
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
		JSONObject jsonData=getPatentFromInterface(appNumber);
		if(null!=jsonData) {
			return getPatentFromJson(jsonData);
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
