package com.ipph.migratecore.deal.format;

import com.ipph.migratecore.deal.exception.FormatException;

import lombok.Getter;
import lombok.Setter;

/**
 * 格式化链
 * 这里同时使用了代理和链式方法实现
 * 让所有的format类都实现该抽象方法
 * 问题：格式化异常信息可能被隐藏了，抛出的异常时链的最后一个处理异常
 */
@Getter
@Setter
public abstract class FormaterChain implements Formater{
	
	private Formater formater;
	
	private Formater next;
	
	@Override
	public Object format(String args, Object value) throws FormatException {
		Object result=null;
		try {
			
			result=this.formater.format(args, value);
			
		}catch (FormatException e) {//通过异常判断是否已经正确格式化完毕了
			if(null!=next) {
				result= next.format(args, value);
			}
		}
		
		return result;
	}
}
