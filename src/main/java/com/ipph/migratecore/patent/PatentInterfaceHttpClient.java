package com.ipph.migratecore.patent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipph.migratecore.patent.util.DateFormatUtil;
import com.ipph.migratecore.patent.util.MD5Util;
import com.ipph.migratecore.util.PatentValidationUtil;

/**
 * 调用专利接口查询数据
 */
@Component
public class PatentInterfaceHttpClient {
	//一下两个变量是针对系统中存在变更数据时，一个库中的专利存在多条的情况下，获取数据的选择
	private final static String PUBDATEDESC="-公开（公告）日";//获取最新的专利数据
	private final static String PUBDATEASC="+公开（公告）日";//获取原始专利数据
	/**
	 * 根据申请号获取专利信息
	 * @param appNumber
	 * @return
	 */
	public JSONObject getPatentByAppNumber(String appNumber)throws Exception{
		String dbName=PatentValidationUtil.getPatentInterfaceDbName(appNumber);//.toUpperCase();
		if("fmzl".equalsIgnoreCase(dbName)) dbName="fmsq";//默认检索发明授权
		return getPatentByAppNumber(appNumber,dbName);
	}
	
	public JSONObject getOriginPatentByAppNumber(String appNumber)throws Exception{
		String dbName=PatentValidationUtil.getPatentInterfaceDbName(appNumber);//.toUpperCase();
		if("fmzl".equalsIgnoreCase(dbName)) dbName="fmsq";//默认检索发明授权
		return getOriginPatentByAppNumber(appNumber,dbName);
	}
	/**
	 * 根据申请号和数据库名获取专利
	 * @param appNumber
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPatentByAppNumber(String appNumber,String dbName)throws Exception{
		return getPatentByAppNumber(appNumber, dbName, null);
	}
	
	public JSONObject getOriginPatentByAppNumber(String appNumber,String dbName)throws Exception{
		return getOriginPatentByAppNumber(appNumber, dbName, null);
	}
	
	public JSONObject getPatentByAppNumber(String appNumber,String dbName,String displayCols)throws Exception{
		return getPatentByAppNumber(appNumber, dbName, displayCols, PatentInterfaceHttpClient.PUBDATEDESC);
	}
	
	public JSONObject getOriginPatentByAppNumber(String appNumber,String dbName,String displayCols)throws Exception{
		return getPatentByAppNumber(appNumber, dbName, displayCols, PatentInterfaceHttpClient.PUBDATEASC);
	}
	
	public JSONObject getPatentByAppNumber(String appNumber,String dbName,String displayCols,String order)throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("from", "0"));  
		nvps.add(new BasicNameValuePair("to", "1")); 
		if(null==dbName){//检索国外专利
			if(null==PatentInterfaceConnection.FOREIGNDB||"".equals(PatentInterfaceConnection.FOREIGNDB)){
				PatentInterfaceConnection.FOREIGNDB="USPATENT,JPPATENT,EPPATENT,KRPATENT,TWZL";
			}
			if("TW".equalsIgnoreCase(PatentValidationUtil.getCountryEn(appNumber))){//TWZL没有旧申请号字段
				nvps.add(new BasicNameValuePair("exp","申请号='"+appNumber+"'"));
				nvps.add(new BasicNameValuePair("dbs", PatentInterfaceConnection.FOREIGNDB));
			}else{
				nvps.add(new BasicNameValuePair("exp","旧申请号='"+appNumber+"%' OR 申请号='"+appNumber+"'"));
				nvps.add(new BasicNameValuePair("dbs", PatentInterfaceConnection.FOREIGNDB));
			}
		}else{
			nvps.add(new BasicNameValuePair("exp","申请号='"+appNumber+"'"));
			nvps.add(new BasicNameValuePair("dbs", dbName));
		}
		if(null!=order&&!"".equals(order)){
			nvps.add(new BasicNameValuePair("order", order));//-表降序
		}else{
			nvps.add(new BasicNameValuePair("order", PatentInterfaceHttpClient.PUBDATEDESC));//降序
		}
		if(null!=displayCols&&!"".equals(displayCols)){
			nvps.add(new BasicNameValuePair("displayCols", displayCols));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		if(null!=arrayJson&&arrayJson.size()>0){
			return arrayJson.getJSONObject(0);
		}
		return null;
	}
	/**
	 * 根据申请号和数据库名获取专利
	 * @param appNumber
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public JSONArray getPatentList(List<String> appNumberList,String patType,int from,int to)throws Exception{
		return getPatentList(getListParameters(appNumberList,"申请号"), patType, from, to, null);
	}
	/**
	 * 根据申请号和数据库名获取专利
	 * @param appNumber
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public JSONArray getPatentList(List<String> appNumberList,String patType,int from,int to,String displayCols)throws Exception{
		return getPatentList(getListParameters(appNumberList,"申请号"), patType, from, to, displayCols);
	}
	/**
	 * 根据条件和数据库名获取专利
	 * @param appNumber
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public JSONArray getPatentList(String condition,String patType,int from,int to)throws Exception{
		return getPatentList(condition, patType, from, to, null);
	}
	/**
	 * 根据申请号和数据库名获取专利
	 * @param appNumber
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public JSONArray getPatentList(String condition,String patType,int from,int to,String displayCols)throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("from", from+""));  
		nvps.add(new BasicNameValuePair("to", to+"")); 
		nvps.add(new BasicNameValuePair("exp",condition));
		nvps.add(new BasicNameValuePair("dbs", patType));
		nvps.add(new BasicNameValuePair("order", "+申请日"));//-表示降序
		if(null!=displayCols&&!"".equals(displayCols)){
			nvps.add(new BasicNameValuePair("displayCols", displayCols));
		}else{
			if(null==PatentInterfaceConnection.COMMONDISPLAYFIELD||"".equals(PatentInterfaceConnection.COMMONDISPLAYFIELD)){
				PatentInterfaceConnection.COMMONDISPLAYFIELD="dbName,title,appNumber,appDate,pubNumber,pubDate,patentNo,address,appCoun,proCode,statusCode,initMainIpc,mainIpc,addrCounty,addrCity,pubYear,den,addrProvince,agencyName,divideInitAppNo,iapp,ipub,agentName,applicantName,inventroName";
			}
			nvps.add(new BasicNameValuePair("displayCols", PatentInterfaceConnection.COMMONDISPLAYFIELD));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		}
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			return (JSONArray) resultJson.get("results");
		}
		return null;
	}
	/**
	 * 获取法律状态
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLaws(String appNumber)throws Exception{
		return getLaws(appNumber,null);
	}
	/**
	 * 获取法律状态
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLaws(String appNumber,Date date) throws Exception{
		return getLaws(appNumber, date, 0, 500);
	}
	/**
	 * 获取法律状态
	 * @param appNumber
	 * @param date
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLaws(String appNumber,Date date,int from,int to) throws Exception{
		return getLaws(appNumber, date, from, to, null);
	}
	/**
	 * 获取法律状态
	 * @param appNumber
	 * @param date
	 * @param from
	 * @param to
	 * @param displayCols
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLaws(String appNumber,Date date,int from,int to,String displayCols) throws Exception{
		return getLaws(appNumber, date, from, to, displayCols, null);
	}
	
	public JSONArray getLaws(String appNumber,Date date,int from,int to,String displayCols,String order) throws Exception{
		StringBuilder exp=new StringBuilder();
        exp.append("申请号=%"+appNumber+"%");
        if(date!=null){
        	exp.append(" and 法律状态公告日 >="+DateFormatUtil.format(date,"yyyy.MM.dd"));
        }
        return getLawsByCondition(exp.toString(),from,to,displayCols,order);
	}
	/**
	 * 获取法律状态
	 * @param condition
	 * @param from
	 * @param to
	 * @param displayCols
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLawsByCondition(String condition,int from,int to,String displayCols,String order) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("exp",condition));
		nvps.add(new BasicNameValuePair("from", from+""));
		nvps.add(new BasicNameValuePair("to", to+""));
		if(null!=order&&!"".equals(order)){
			nvps.add(new BasicNameValuePair("order", order));
		}else{//默认排序方式
			nvps.add(new BasicNameValuePair("order", "-法律状态公告日"));//法律状态公告日降序排列
		}
		if(null!=displayCols&&!"".equals(displayCols)){
			nvps.add(new BasicNameValuePair("displayCols",displayCols));//获取族号对应的申请
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEPRS||"".equals(PatentInterfaceConnection.INTERFACEPRS)){
        	PatentInterfaceConnection.INTERFACEPRS="http://api.souips.com:8080/ipms/prs";
        }
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEPRS);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}

	
	/**
	 * 获取同族信息
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFamily(String familyNo) throws Exception{
		return getFamily(familyNo,0,500);
	}
	
	public JSONArray getFamily(String familyNo,int from ,int to) throws Exception{
		return getFamily(familyNo, from, to, null, null);
	}
	
	public JSONArray getFamily(String familyNo,int from ,int to,String db,String displayCols) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        StringBuilder exp=new StringBuilder();
        exp.append("族号="+familyNo+"");
		nvps.add(new BasicNameValuePair("exp",exp.toString()));
		if(null==db||"".equals(db)){
			if(null==PatentInterfaceConnection.PATENTALLDB||"".equals(PatentInterfaceConnection.PATENTALLDB)){
				db="FMZL,FMSQ,SYXX,WGZL,TWZL,HKPATENT,ATPATENT,AUPATENT,DEPATENT,RUPATENT,FRPATENT,KRPATENT,CAPATENT,USPATENT,JPPATENT,SEPATENT,CHPATENT,ESPATENT,ITPATENT,GBPATENT,APPATENT,GCPATENT,EPPATENT,WOPATENT,ASPATENT,OTHERPATENT";
			}else{
				db=PatentInterfaceConnection.PATENTALLDB;
			}
		}
		nvps.add(new BasicNameValuePair("dbs", db));
		nvps.add(new BasicNameValuePair("from", from+""));
		nvps.add(new BasicNameValuePair("to", to+""));
		if(null!=displayCols&&!"".equals(displayCols)){
			nvps.add(new BasicNameValuePair("displayCols",displayCols));
		}else{
			nvps.add(new BasicNameValuePair("displayCols", "appNumber,pubNumber"));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	
	/**
	 * 获取引证信息
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getCites(String appNumber) throws Exception{
		return getCites(appNumber,null);
	}
	
	public JSONArray getCites(String appNumber,Date date) throws Exception{
		return getCites(appNumber, date, 0, 500);
	}
	
	public JSONArray getCites(String appNumber,Date date,int from ,int to) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        StringBuilder exp=new StringBuilder();
        exp.append("申请号=("+appNumber+","+appNumber.replaceFirst("^[a-zA-Z]{2}","")+")");
        if(date!=null){
        	exp.append(" and 公开（公告）日 >="+DateFormatUtil.format(date,"yyyyMMdd"));
        }
		nvps.add(new BasicNameValuePair("exp",exp.toString()));
		nvps.add(new BasicNameValuePair("from", from+""));
		nvps.add(new BasicNameValuePair("to", to+""));
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACECITE||"".equals(PatentInterfaceConnection.INTERFACECITE)){
        	PatentInterfaceConnection.INTERFACECITE="http://api.souips.com:8080/ipms/cite";
        }
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACECITE);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	/**
	 * 获取费用记录
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFees(String appNumber) throws Exception{
		return getFees(appNumber, null, false, 0, 500);
	}
	/**
	 * 获取费用
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFees(String appNumber,Date date,boolean eq) throws Exception{
		return getFees(appNumber, date, eq, 0, 500);
	}
	/**
	 *  获取费用
	 * @param appNumber
	 * @param date
	 * @param eq 缴费日期是否等于给定日期
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFees(String appNumber,Date date,boolean eq,int from ,int to) throws Exception{
		return getFees(appNumber, date, eq, from, to, null);
	}
	
	public JSONArray getFees(String appNumber,Date date,boolean eq,int from ,int to,String displayCols) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        StringBuilder exp=new StringBuilder();
        exp.append("applyNum_new=%"+appNumber+"%");
        if(date!=null){
        	if(eq){
        		exp.append(" and HKDate ="+DateFormatUtil.format(date,"yyyy.MM.dd"));
        	}else{
        		exp.append(" and HKDate >"+DateFormatUtil.format(date,"yyyy.MM.dd"));
        	}
        }
		nvps.add(new BasicNameValuePair("exp", exp.toString()));
		nvps.add(new BasicNameValuePair("from", from+""));
		nvps.add(new BasicNameValuePair("to", to+""));
		if(null!=displayCols&&!"".equals(displayCols)){
			nvps.add(new BasicNameValuePair("displayCols",displayCols));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEFEE||"".equals(PatentInterfaceConnection.INTERFACEFEE)){
			PatentInterfaceConnection.INTERFACEFEE="http://api.souips.com:8080/ipms/fee";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEFEE);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	
	/**
	 * 获取费用
	 * @param appNumber
	 * @return
	 * @throws Exception
	 */
	public JSONArray getYearFees(String appNumber,boolean isLast) throws Exception{
		return getYearFees(appNumber, isLast, 0, 500);
	}
	
	public JSONArray getYearFees(String appNumber,boolean isLast,int from ,int to) throws Exception{
		return getYearFees(appNumber, isLast, from, to, null);
	}
	
	public JSONArray getYearFees(String appNumber,boolean isLast,int from ,int to,String order) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        StringBuilder exp=new StringBuilder();
        exp.append("applyNum_new=%"+appNumber+"%");
        exp.append(" and FeeType=%年费%");
        if(isLast){
        	nvps.add(new BasicNameValuePair("to", "1"));
        }else{
        	nvps.add(new BasicNameValuePair("to", to+""));
        }
		nvps.add(new BasicNameValuePair("exp", exp.toString()));
		nvps.add(new BasicNameValuePair("from", from+""));
		if(null!=order&&!"".equals(order)){
			nvps.add(new BasicNameValuePair("order", order));
		}else{
			nvps.add(new BasicNameValuePair("order", "-HKDate"));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEFEE||"".equals(PatentInterfaceConnection.INTERFACEFEE)){
			PatentInterfaceConnection.INTERFACEFEE="http://api.souips.com:8080/ipms/fee";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEFEE);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	
	public JSONArray getYearFees(String condition,int from ,int to,String order) throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("to", to+""));
		nvps.add(new BasicNameValuePair("exp", condition));
		nvps.add(new BasicNameValuePair("from", from+""));
		if(null!=order&&!"".equals(order)){
			nvps.add(new BasicNameValuePair("order", order));
		}else{
			nvps.add(new BasicNameValuePair("order", "-HKDate"));
		}
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEFEE||"".equals(PatentInterfaceConnection.INTERFACEFEE)){
			PatentInterfaceConnection.INTERFACEFEE="http://api.souips.com:8080/ipms/fee";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEFEE);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	
	 /**
	  * 检索接口获取专利的公开号和公开日/授权号或授权日
	  * @param appNumber
	  * @param dbName
	  * @param patentMap
	  */
	@Deprecated
	 public JSONArray getPubAuthNo(String appNumber,String dbName)throws Exception{
		 //封装请求参数
		 List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		 nvps.add(new BasicNameValuePair("from", "0"));  
		 nvps.add(new BasicNameValuePair("to", "2")); 
		 if(null!=dbName&&!"".equals(dbName)){
			 nvps.add(new BasicNameValuePair("dbs", dbName));
		 }else{
			 nvps.add(new BasicNameValuePair("dbs", "FMZL,FMSQ"));
		 }
		 nvps.add(new BasicNameValuePair("order", "-公开（公告）日"));//-表降序
		 nvps.add(new BasicNameValuePair("exp","申请号='"+appNumber+"'"));
		 nvps.add(new BasicNameValuePair("displayCols","appNumber,pubNumber,pubDate,dbName"));
		 //请求路径
		 if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			 PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		 }
		 JSONArray arrayJson=null;
		 JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		 if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			 arrayJson=(JSONArray) resultJson.get("results");
		 }
		 return arrayJson;
	}
	 
	 /**
	  * 获取库中的更新申请号集合
	  * @param appNumber
	  * @param dbName
	  * @return
	  * @throws Exception
	  */
	@Deprecated
	 public JSONArray getAppNoList(List<String> appNumberList,String dbName)throws Exception{
		 //封装请求参数
		 List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		 nvps.add(new BasicNameValuePair("from", "0"));  
		 nvps.add(new BasicNameValuePair("to", appNumberList.size()+"")); 
		 if(null!=dbName&&!"".equals(dbName)){
			 nvps.add(new BasicNameValuePair("dbs", dbName));
		 }else{
			 nvps.add(new BasicNameValuePair("dbs", "FMSQ,WGZL,SYXX"));
		 }
		 nvps.add(new BasicNameValuePair("order", "-公开（公告）日"));//-表降序
		 //nvps.add(new BasicNameValuePair("exp","申请号='"+appNumber+"'"));
		 //nvps.add(getApplycodeParameters(appNumberList,"申请号"));
		 StringBuilder exp=new StringBuilder();
	     exp.append(getListParameters(appNumberList,"申请号"));
	     nvps.add(new BasicNameValuePair("exp", exp.toString()));
		 
		 nvps.add(new BasicNameValuePair("displayCols","appNumber"));
		 //请求路径
		 if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			 PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		 }
		 JSONArray arrayJson=null;
		 JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		 if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			 arrayJson=(JSONArray) resultJson.get("results");
		 }
		 return arrayJson;
	}
	 
	 /**
	  * 获取费用库中已更新费用的申请号集合
	  * @param appNumber
	  * @param dbName
	  * @return
	  * @throws Exception
	  */
	 public JSONArray getAppNoListByFee(List<String> appNumberList,Date feeDate)throws Exception{
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        StringBuilder exp=new StringBuilder();
        exp.append(getListParameters(appNumberList,"applyNum_new"));
        exp.append(" and HKDate >="+DateFormatUtil.format(feeDate,"yyyy.MM.dd"));
		nvps.add(new BasicNameValuePair("exp", exp.toString()));
		nvps.add(new BasicNameValuePair("from", "0"));
		nvps.add(new BasicNameValuePair("to", appNumberList.size()*100+""));
		nvps.add(new BasicNameValuePair("displayCols","appNumber"));
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEFEE||"".equals(PatentInterfaceConnection.INTERFACEFEE)){
			PatentInterfaceConnection.INTERFACEFEE="http://api.souips.com:8080/ipms/fee";
		}
		JSONArray arrayJson=null;
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEFEE);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			arrayJson=(JSONArray) resultJson.get("results");
		}
		return arrayJson;
	}
	 
	public int getTotalSize(String condition,String dbName,String displayCols)throws Exception{
		int totalSize=0;
		//封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("from", "0"));  
		nvps.add(new BasicNameValuePair("to", "1")); 
		nvps.add(new BasicNameValuePair("dbs", dbName));
		nvps.add(new BasicNameValuePair("exp",condition));
		nvps.add(new BasicNameValuePair("displayCols",displayCols));
		//请求路径
		if(null==PatentInterfaceConnection.INTERFACEURL||"".equals(PatentInterfaceConnection.INTERFACEURL)){
			PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms/pat";
		}
		
		JSONObject resultJson=getByHttpClient(nvps,PatentInterfaceConnection.INTERFACEURL);
		if(null!=resultJson&&resultJson.get("message").equals("SUCCESS")){
			totalSize=resultJson.getInteger("total");
		}
		return totalSize;
	}
	 
	 /**
		 * 通过HttpClient获取网络数据
		 * @return
		 */
	private JSONObject getByHttpClient(List<NameValuePair> nvps,String url)throws Exception{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try{
			//请求超时
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,200000);
			//读取超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
			//内容编码
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			//添加服务器端过滤信息
			nvps.add(new BasicNameValuePair("machinecode", PatentInterfaceConnection.MACHINECODE));
			//服务器加密时间戳
			Map<String,String> passParam=MD5Util.getEncodeParam();
			if(passParam.size()>0){
				for(String key:passParam.keySet()){
					nvps.add(new BasicNameValuePair(key, passParam.get(key)));
				}
			}
			
			HttpPost httpPost = new HttpPost(url);
			//服务密码
			//nvps.add(new BasicNameValuePair("serverPassword", PatentInterfaceConnection.SERVERPASSWORD));
			//nvps.add(new BasicNameValuePair("machineCode", PatentInterfaceConnection.MACHINECODE));
			// 设置表单提交编码为UTF-8  
			httpPost.setHeader("ContentType", PatentInterfaceConnection._jsonApplication);
			UrlEncodedFormEntity entry = new UrlEncodedFormEntity(nvps, PatentInterfaceConnection._encode);  
			entry.setContentType(PatentInterfaceConnection._application);  
			httpPost.setEntity(entry);
			//发送post请求
			System.out.println("请求地址："+url);
			System.out.println("参数："+nvps);
			HttpResponse response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity = response.getEntity();
				JSONObject resultJson=JSONObject.parseObject(EntityUtils.toString(entity,"UTF-8"));
				System.out.println(resultJson.toString());
				nvps.clear();
				httpPost.abort();//释放资源
				EntityUtils.consume(entity);//销毁资源  
				return resultJson;
			}
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
	    return null;
	}
	 

	/**
	 * 封装申请号
	 * 
	 * @param nvpsList
	 */
	public String getListParameters(List<String> appNumbers,String colName) {
		StringBuffer appcode = new StringBuffer();
		if (appNumbers.size() >= 1) {
			appcode.append(colName).append("=( ");
			for (String code : appNumbers) {
				if (null!=code&&!"".equals(code))
					appcode.append("'" + code + "',");
			}
			return appcode.substring(0, appcode.length() - 1)+")";
		}
		return "";
	}
	
}
