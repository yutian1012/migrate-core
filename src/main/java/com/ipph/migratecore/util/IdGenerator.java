package com.ipph.migratecore.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
	
	private static final AtomicInteger atomicInteger = new AtomicInteger(0);
	
	/**
	 * 保证1毫秒能的主键生成不操作10000条即可
	 * 另外要保证系统时间不会存在问题
	 * @return
	 */
	public static String getId() {
		
		StringBuilder stringBuilder=new StringBuilder();
        //当前系统时间戳精确到毫秒  
		stringBuilder.append(System.currentTimeMillis());
        
        int intValue = atomicInteger.getAndIncrement();
        if (atomicInteger.get() >= 8999) {
        	atomicInteger.set(0);
        }
        
        stringBuilder.append(intValue+1000);
        
        return stringBuilder.toString();
    }   
	
	public static Long genId() {
		return Long.parseLong(getId());
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(IdGenerator.getId());
		System.out.println(IdGenerator.genId());
	}
}
