package com.ipph.migratecore.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

public class AppUtil implements ApplicationContextAware {

	private static  ApplicationContext applicationContext;
	

	/**
	 * spring启动时注入context
	 */
	public void setApplicationContext(ApplicationContext contex) throws BeansException {
		applicationContext=contex;
	}

	/**
	 * 获取spring的上下文。
	 * @return
	 */
	public static ApplicationContext getContext(){
		return applicationContext;
	}
	
	
	/**
	 * 根据指定的接口或基类获取实现类列表。
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List<Class> getImplClass(Class clazz) throws ClassNotFoundException{
		List<Class> list=new ArrayList<Class>();
		
		Map map= applicationContext.getBeansOfType(clazz);
		for(Object obj : map.values()){
			String name=obj.getClass().getName();
			int pos=name.indexOf("$$");
			if(pos>0){
				name=name.substring(0,name.indexOf("$$")) ; 
			}
			Class cls= Class.forName(name);
			
			list.add(cls);
		}
		return list;
	}
	
	
	/**
	 * 获取接口的实现类实例。
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Map<String,Object> getImplInstance(Class clazz) throws ClassNotFoundException{
		
		Map map= applicationContext.getBeansOfType(clazz);
		
		return map;
	}
	
	/**
	 * 根据类从spring上下文获取bean。
	 * @param cls
	 * @return
	 */
	public  static <C> C getBean(Class<C> cls){
		return applicationContext.getBean(cls);
	}
	
	/**
	 * 根据类名从spring上下文获取bean。
	 * @param cls
	 * @return
	 */
	public static Object getBean(String  beanId){
		return applicationContext.getBean(beanId);
	}

	/**
	 * Spring发布事件。
	 * @param applicationEvent
	 */
	public static void publishEvent(ApplicationEvent applicationEvent){
		applicationContext.publishEvent(applicationEvent);
	}


}
