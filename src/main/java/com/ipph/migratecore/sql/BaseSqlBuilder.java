package com.ipph.migratecore.sql;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.ipph.migratecore.deal.condition.ConditionContext;
import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.model.ConditionModel;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.WhereModel;

public class BaseSqlBuilder {
	
	@Resource
	private ConditionContext conditionContext;
	/**
	 * 获取查询统计数量语句
	 * @param tableName
	 * @return
	 */
	protected String getCountSql(String tableName){
		StringBuilder sbuilder=new StringBuilder();
		
		sbuilder.append("select count(1) num from ").append(tableName);
		
		return sbuilder.toString();
	}
	/**
	 * 获取查询语句
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	protected String getSelectSql(String tableName,List<String> selectFieldList){
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("select ");
		for(String field:selectFieldList){
			sbuilder.append(field).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);//去掉末尾的逗号
		sbuilder.append(" from ").append(tableName);
		
		return sbuilder.toString();
	}
	
	/**
	 * 获取插入语句
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	protected String getInsertSql(String tableName,List<String> insertFieldList){
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("insert into ").append(tableName).append(" (");
		for(String field:insertFieldList){
			sbuilder.append(field).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(" )").append(" VALUES (");
		for(String field:insertFieldList){
			sbuilder.append("?").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(")");
		return sbuilder.toString();
	}
	/**
	 * 获取更新语句
	 * @param tableName
	 * @param updateFieldList
	 * @return
	 */
	protected String getUpdateSql(String tableName,List<String> updateFieldList){
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("update ").append(tableName).append(" set ");
		
		//to有值，from无值作为待更新的字段
		for(String field:updateFieldList){
			sbuilder.append(field).append(" = ? ").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);

		return sbuilder.toString();
	}
	
	/**
	 * 获取查询的where条件
	 * @param whereModel
	 * @return
	 */
	protected String getFromCondition(WhereModel whereModel){
		List<ConditionModel> conditionList=new ArrayList<>();
		if(null!=whereModel){
			for(ConditionModel condition:whereModel.getConditionList()){
				if(null==condition
						||null==condition.getField()
						||condition.getApplyType()==ApplyTypeEnum.TARGET){
					continue;
				}
				
				conditionList.add(condition);
			}
		}
		return getWhereByConditionField(conditionList);
	}
	/**
	 * 获取目标表的where条件
	 * @param whereModel
	 * @return
	 */
	protected String getTargetCondition(WhereModel whereModel){
		List<ConditionModel> conditionList=new ArrayList<>();
		if(null!=whereModel){
			for(ConditionModel condition:whereModel.getConditionList()){
				if(null==condition
						||null==condition.getField()
						||condition.getApplyType()==ApplyTypeEnum.SOURCE){
					continue;
				}
				
				conditionList.add(condition);
			}
		}
		return getWhereByConditionField(conditionList);
	}
	
	
	/**
	 * 获取sql语句的where条件
	 * 使用Condition字段获取条件
	 * @param conditionList
	 * @return
	 * @throws ConditionException 
	 */
	private String getWhereByConditionField(List<ConditionModel> conditionList){
		
		StringBuilder sbuilder=new StringBuilder();
		//where条件构造
		if(null!=conditionList&&conditionList.size()>0){
			
			sbuilder.append(" where 1=1 ");
			
			for(ConditionModel condition:conditionList){
				
				Object v=conditionContext.getConditionParam(condition);
				
				FieldModel field=condition.getField();
				
				if(null!=v&&null!=field){
					sbuilder.append(" and ").append(field.getName()).append(" ").append(v);
				}
			}
		}
		return sbuilder.toString();
	}
}
