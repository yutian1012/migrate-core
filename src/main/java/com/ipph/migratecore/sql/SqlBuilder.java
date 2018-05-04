package com.ipph.migratecore.sql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.TableModel;

@Component
public class SqlBuilder extends BaseSqlBuilder{
	
	/**
	 * 获取查询语句
	 * @param tableModel
	 * @return
	 * @throws ConditionException 
	 */
	public String getSelectSql(TableModel tableModel){

		if(tableModel.getFiledList().size()==0){//查询或迁移的数据表列字段
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		List<String> fieldList=new ArrayList<>();
		
		//select查询字段
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field||field.getApplyType()==ApplyTypeEnum.TARGET) {
				continue;
			}
			
			fieldList.add(field.getName());
		}
		
		String sql=null;
		
		if(fieldList.size()>0) {
			
			sql=getSelectSql(tableModel.getFrom(), fieldList);
		}
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getFromCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
		return sbuilder.toString();
	}
	/**
	 * 获取查询语句
	 * @param tableModel
	 * @return
	 */
	public String getSelectCountSql(TableModel tableModel){

		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		
		String sql=getCountSql(tableModel.getFrom());
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getFromCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
		return sbuilder.toString();
	}
	/**
	 * 获取insert语句
	 * @param tableModel
	 * @return
	 */
	public String getInsertSql(TableModel tableModel){
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		List<String> fieldList=new ArrayList<>();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field||field.getApplyType()==ApplyTypeEnum.SOURCE){
				continue;
			}
			fieldList.add(field.getName());
		}
		
		if(fieldList.size()>0){
			return getInsertSql(tableModel.getTo(),fieldList);
		}
		return null;
	}
	
	/**
	 * 获取update语句
	 * @param tableModel
	 * @return
	 */
	public String getUpdateSql(TableModel tableModel){
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		List<String> fieldList=new ArrayList<>();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field||field.getApplyType()==ApplyTypeEnum.SOURCE){
				continue;
			}
			fieldList.add(field.getName());
		}
		
		if(fieldList.size()==0) return null;
		
		String sql=getUpdateSql(tableModel.getTo(), fieldList);
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getTargetCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
		return sbuilder.toString();
	}
	
	/**
	 * 获取待更新记录信息是否存在的判断语句
	 * @param tableModel
	 * @return
	 */
	public String getTargetSelectSql(TableModel tableModel){
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		
		String sql=getCountSql(tableModel.getTo());
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getTargetCondition(tableModel.getWhereModel());
		
		if(null!=condition&&!"".equals(condition)){
			sbuilder.append(condition);
			return sbuilder.toString();
		}
		return null;
		
	}
}
