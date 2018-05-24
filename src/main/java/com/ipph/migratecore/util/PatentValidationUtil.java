package com.ipph.migratecore.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class PatentValidationUtil {
	/**
	 * 判断是否是合法的申请号
	 * @param appNumber
	 * @return
	 */
	public static boolean isValidAppNumber(String appNumber){
		try{
			String[] zlNumber=getZlNumber(appNumber);
			if(null!=zlNumber&&!StringUtils.isEmpty(zlNumber[0])){
				return validation(zlNumber[0],zlNumber[1]);
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	/**
	 * 是否是格式化规范的pct号码
	 * @param pctNo
	 * @return
	 */
	public static boolean isValidPctNo(String pctNo){
		if(StringUtils.isEmpty(pctNo)){
			return false;
		}
		Pattern pattern=Pattern.compile("^PCT[/]CN(?:19|20\\d{2})[/]\\d{6}$");
		return pattern.matcher(pctNo).matches();
	}
	
	/**
	 * 校验授权号
	 * @param authNo
	 * @return
	 */
	public static boolean isValidpctAuthNo(String pctAuthNo){
		if(StringUtils.isEmpty(pctAuthNo)){
			return false;
		}
		Pattern pattern=Pattern.compile("^((CN\\d{9})|(US\\d{7})|(KR\\d{9})|(JP\\d{7})|(EP\\d{7})|(RU\\d{10}))$");
		return pattern.matcher(pctAuthNo).matches();
	}
	
	private static String[] getZlNumber(String appNumber){
		String[] zlNumber=new String[2];//第1个位置存放专利号，第二个wiz存放校验位
		if(!StringUtils.isEmpty(appNumber)){
			appNumber=appNumber.trim();
			Pattern p=Pattern.compile("^([a-zA-Z]{2})?+\\d{8}(\\d{4})?+(\\.[\\d|X|x])?+");
			Matcher m=p.matcher(appNumber);
			if(m.matches()){
				p=Pattern.compile("^[a-zA-Z]{2}(\\d+?)(\\.[\\d|X|x])?");
				m=p.matcher(appNumber);
				if(m.matches()){
					zlNumber[0]=m.group(1);
					if(appNumber.indexOf(".")!=-1){
						zlNumber[1]=appNumber.substring(appNumber.lastIndexOf(".")+1);
					}
				}
				else if(appNumber.indexOf(".")!=-1){
					zlNumber[0]=appNumber.substring(0,appNumber.lastIndexOf("."));
					zlNumber[1]=appNumber.substring(appNumber.lastIndexOf(".")+1);
				}
				else{
					zlNumber[0]=appNumber;
				}
			}
		}
		return zlNumber;
	}
	/**
	 * 根据校验位校验专利是否正确
	 * @param zlNumber
	 * @param validation
	 * @return
	 */
	private static boolean validation(String zlNumber,String validation){
		Pattern p=Pattern.compile("^(85|86|87|88)(1|2|3|8|9)\\d+");
		Matcher m=p.matcher(zlNumber);
		if("".equals(validation)){
			if(m.matches())
				return true;
		}
		if(zlNumber.length()==8){
			//p=Pattern.compile("^(89|90|91|92|93|94|95|96|97|98|99|00|01|02|03)(1|2|3|8|9)\\d+");
			p=Pattern.compile("^(85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|00|01|02|03)(1|2|3|8|9)\\d+");
			m=p.matcher(zlNumber);
			if(!m.matches())
				return false;
		}
		int[] numbers=new int[zlNumber.length()];
		
		int sum=0;
		for(int i=0, mul=2;i<numbers.length;i++,mul++){
			if(mul>9) mul=2;
			sum+=Integer.parseInt(zlNumber.substring(i,i+1))*mul;
		}
		int flag=sum%11;
		if(flag==10&&"x".equalsIgnoreCase(validation)){
			return true;
		}
		else{
			return (flag+"").equalsIgnoreCase(validation);
		}
	}
	
	
	public static String getPatentInterfaceDbName(String appNumber){
		if(PatentValidationUtil.isForeignPatent(appNumber)) return null;
		String number="";
		String[] zlNumberArr=getZlNumber(appNumber);
		if(null!=zlNumberArr&&null!=zlNumberArr[0])
		{
			String zlNumber=zlNumberArr[0];
			Pattern p=Pattern.compile("^(85|86|87|88)(1|2|3|8|9)\\d+");
			Matcher m=p.matcher(zlNumber);
			if(m.matches()){
				number=zlNumber.substring(2,3);
			}
			else if(zlNumber.length()==8){
				p=Pattern.compile("^(89|90|91|92|93|94|95|96|97|98|99|00|01|02|03)(1|2|3|8|9)\\d+");
				m=p.matcher(zlNumber);
				if(m.matches()){
					number=zlNumber.substring(2,3);
				}
			}
			else{
				number=zlNumber.substring(4,5);
			}
		}
			
		if(number.equals("1")||number.equals("8")){
			return "fmzl";
		}
		else if(number.equals("2")||number.equals("9")){
			return "syxx";
		}
		else if(number.equals("3")){
			return "wgzl";
		}
		else{
			return null;
		}
	}
	/**
	 * 判断是否是国外专利
	 * @param appNumber
	 * @return
	 */
	public static boolean isForeignPatent(String appNumber){
		if(null!=appNumber&&!"".equals(appNumber)){
			if(appNumber.toUpperCase().startsWith("CN")||appNumber.toUpperCase().startsWith("ZL")){
				return false;
			}
		}
		return true;
	}
	/**
	 * 获取申请号中国家电前两位字符
	 * @param appNumber
	 * @return
	 */
	public static String getCountryEn(String appNumber){
		if(isStartWithCharacter(appNumber)){
			Pattern p=Pattern.compile("^([a-zA-Z]{2})[\\w.]+");
			Matcher m=p.matcher(appNumber);
			if(m.matches()){
				return m.group(1);
			}
		}
		return "";
	}
	/**
	 * 判断是否以字母开头
	 * @param str
	 */
	public static boolean isStartWithCharacter(String str){
		Pattern p=Pattern.compile("^[A-Za-z]+.*");
		Matcher m=p.matcher(str);
		if(m.matches()){
			return true;
		}
		return false;
	}
}
