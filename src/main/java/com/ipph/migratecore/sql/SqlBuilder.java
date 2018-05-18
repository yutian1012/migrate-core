package com.ipph.migratecore.sql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldConstraintEnum;
import com.ipph.migratecore.enumeration.TableOperationEnum;
import com.ipph.migratecore.model.ConstraintModel;
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
		
		//设置主键字段
		String pkName=getPkFieldName(tableModel,ApplyTypeEnum.SOURCE);
		if(null!=pkName&&!fieldList.contains(pkName)) {
			fieldList.add(pkName);
		}
		
		String sql=null;
		
		if(fieldList.size()>0) {
			
			sql=getSelectSql(tableModel.getFrom(), fieldList);
		}
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getSourceCondition(tableModel.getWhereModel());
		
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
		
		String condition=getSourceCondition(tableModel.getWhereModel());
		
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
		
		String condition=null;
		
		if(tableModel.getType()==TableOperationEnum.UPDATE) {
			condition=getTargetCondition(tableModel.getWhereModel());
		}else{//insert数据时，判断主键字段值是否已经存在
			condition=getTargetConstraint(tableModel.getConstraintList());
		}
		if(null!=condition&&!"".equals(condition)){
			sbuilder.append(condition);
			return sbuilder.toString();
		}
		
		return null;
	}
	/**
	 * 获取源表的主键字段名
	 * @return
	 */
	private String getPkFieldName(TableModel tableModel,ApplyTypeEnum applyType) {
		
		if(null==tableModel.getSourcePkName()) {
			List<ConstraintModel> constraintList=tableModel.getConstraintList();
			
			for(ConstraintModel constraintModel:constraintList) {
				if(constraintModel.getApplyType()==applyType
						&&FieldConstraintEnum.PRIMARY==constraintModel.getType()&&null!=constraintModel.getField()) {
					tableModel.setSourcePkName(constraintModel.getField().getName().toUpperCase());
				}
			}
		}
		
		return tableModel.getSourcePkName();
	}
	
	/**
	 * 判断源表中是否设置了主键字段
	 * @param tableModel
	 * @return
	 */
	public boolean hasPrimaryKey(TableModel tableModel) {
		
		return null!=getPkFieldName(tableModel,ApplyTypeEnum.SOURCE)?true:false;
	}
	/**
	 * 获取所有字段的查询数据--根据原表主键显示原数据
	 * @param tableModel
	 * @return
	 */
	public String getAllFieldSelectWithPK(TableModel tableModel) {
		if(tableModel.getFiledList().size()==0){//查询或迁移的数据表列字段
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		
		//设置主键字段
		String pkName=getPkFieldName(tableModel,ApplyTypeEnum.SOURCE);
		
		sbuilder.append("select * from ").append(tableModel.getFrom()).append(" where ").append(pkName).append("=?");
		
		return sbuilder.toString();
	}
}
