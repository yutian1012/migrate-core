package com.ipph.migratecore.patent.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class MachineCodeUtil {
	/**
	 * 获取主机的mac地址
	 * @return
	 */
	public static String getMacAddress(){
		StringBuffer sb = new StringBuffer("");
		InetAddress address=null;
		try {
			address = InetAddress.getLocalHost();
			byte[] mac=NetworkInterface.getByInetAddress(address).getHardwareAddress();
			for(int i=0; i<mac.length; i++) {
				//字节转换为整数
				int temp = mac[i] & 0xff;
				String str = Integer.toHexString(temp);
				if(str.length()==1) {//格式化显示的位数
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
				sb.append("-");
			}
			if(sb.length()>1){
				return sb.substring(0,sb.length()-1).toUpperCase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
