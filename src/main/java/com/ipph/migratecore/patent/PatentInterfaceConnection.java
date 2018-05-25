package com.ipph.migratecore.patent;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.ipph.migratecore.patent.util.MD5Util;
import com.ipph.migratecore.patent.util.MachineCodeUtil;

public class PatentInterfaceConnection {
	public static String INTERFACEURL=null;//专利检索接口
	public static String INTERFACEPRS=null;//专利法律状态接口
	public static String INTERFACEFEE=null;//专利费用接口
	public static String INTERFACECITE=null;//引证文献接口
	public static String INTERFACEPDF=null;//专利pdf接口
	public static String FOREIGNDB=null;//国外专利数据库
	public static String PATENTALLDB=null;//国外专利数据库
	public static String FILEPATH=null;
	public static String COMMONDISPLAYFIELD=null;
	public static String SYSTEMNAME=null; // 系统名称
	//请求需要将计算机名_mac地址，已经对应的密码传递到服务器，判断是否是合法用户
	//public static String SERVERPASSWORD=AppConfigUtil.get("service.pass");//密码
	//public static String MACHINECODE=ServicePassCipher.getMachineCode();//计算机名_mac地址,使用下划线分隔
	public static String MACHINECODE=MachineCodeUtil.getMacAddress();
	public static String _contentType="application/x-www-form-urlencoded";
	public static String _encode="UTF-8";
	public static String _application="application/x-www-form-urlencoded;charset=UTF-8";
	public static String _jsonApplication="application/json";
	public static String _status="SUCCESS";
	/**
	 * 判断是否可用链接
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean isConnected() {
		boolean flag=false;
		//访问服务器的一个地址，看能不能正常获取链接
		if(null==PatentInterfaceConnection.INTERFACEURL){
			PatentInterfaceConnection.INTERFACEURL="http://api.souips.com:8080/ipms-pi/pat";//"http://59.151.99.154:8080/ipms-pi/pat";
		}
		if(null==PatentInterfaceConnection.INTERFACEPRS){
			PatentInterfaceConnection.INTERFACEPRS="http://api.souips.com:8080/ipms-pi/prs";//"http://59.151.99.154:8080/ipms-pi/prs";
		}
		
        DefaultHttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost(PatentInterfaceConnection.INTERFACEURL);  
        httppost.setHeader("ContentType", PatentInterfaceConnection._jsonApplication);  
        //封装请求参数
        List<NameValuePair> nvps=new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("order", "申请号"));  
        nvps.add(new BasicNameValuePair("displayCols", "appNumber,appDate")); //支持多个字段用逗号连接；为空返回所有字段
        nvps.add(new BasicNameValuePair("dbs", "FMZL"));
        nvps.add(new BasicNameValuePair("exp", "申请号=('CN201210105520.X')"));
        nvps.add(new BasicNameValuePair("from", "0")); //支持多个字段用逗号连接；为空返回所有字段
        nvps.add(new BasicNameValuePair("to", "1"));
        //服务密码
        //nvps.add(new BasicNameValuePair("serverPassword", NewPatentInterfaceConnection.SERVERPASSWORD));  
        nvps.add(new BasicNameValuePair("machinecode", PatentInterfaceConnection.MACHINECODE));
      //服务器加密时间戳
		Map<String,String> passParam=MD5Util.getEncodeParam();
		if(passParam.size()>0){
			for(String key:passParam.keySet()){
				nvps.add(new BasicNameValuePair(key, passParam.get(key)));
			}
		}
        
        // 设置表单提交编码为UTF-8  
        UrlEncodedFormEntity entry;
		try {
			entry = new UrlEncodedFormEntity(nvps, PatentInterfaceConnection._encode);
			entry.setContentType(PatentInterfaceConnection._application);  
	        httppost.setEntity(entry);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}  
        HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode()==200){
				JSONObject resultJson=null;
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					resultJson=JSONObject.parseObject(EntityUtils.toString(entity, "UTF-8"));
					EntityUtils.consume(entity);
				}
				if(resultJson!=null&&resultJson.containsKey("message")&&resultJson.getString("message").equals(PatentInterfaceConnection._status)){
					flag=true;
					resultJson.clear();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(!flag){
			}
			nvps.clear();
			httpclient.getConnectionManager().shutdown();
		}
		return flag;
	}
	/**
	 * 获取文件的存储目录
	 * @return
	 */
	public static String getBaseFilePath(){
		if(null!=PatentInterfaceConnection.FILEPATH&&!"".equals(PatentInterfaceConnection.FILEPATH)){
			File file=new File(PatentInterfaceConnection.FILEPATH);
			if(!file.exists()){
				file.mkdirs();
			}
			if(PatentInterfaceConnection.FILEPATH.endsWith(File.separator)||PatentInterfaceConnection.FILEPATH.endsWith("/")){
				return PatentInterfaceConnection.FILEPATH;
			}else{
				return PatentInterfaceConnection.FILEPATH+File.separator;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(PatentInterfaceConnection.isConnected());
	}
}
