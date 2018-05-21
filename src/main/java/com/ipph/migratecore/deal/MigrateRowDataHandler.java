package com.ipph.migratecore.deal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ipph.migratecore.deal.condition.ConditionContext;
import com.ipph.migratecore.deal.exception.FormatException;
import com.ipph.migratecore.deal.exception.SplitException;
import com.ipph.migratecore.deal.format.FormaterContext;
import com.ipph.migratecore.deal.split.SpliterContext;
import com.ipph.migratecore.enumeration.ApplyTypeEnum;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.ConditionModel;
import com.ipph.migratecore.model.FieldModel;
import com.ipph.migratecore.model.FormatModel;
import com.ipph.migratecore.model.SplitModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.util.IdGenerator;

/**
 * 数据集行处理类
 */
@Component
public class MigrateRowDataHandler {
	
	public static final String pkName="pkName";
	
	@Resource
	private FormaterContext formaterContext;
	@Resource
	private ConditionContext conditionContext;
	@Resource
	private SpliterContext spliterContext;
	
	/**
	 * 获取数据源查询条件参数值
	 * @param table
	 * @return
	 */
	public Object[] handleSourceFieldCondition(TableModel table) {
		if(null!=table.getWhereModel()){
			
			List<ConditionModel> conditionList=new ArrayList<>();
			
			for(ConditionModel conditionModel:table.getWhereModel().getConditionList()){
				
				if(null==conditionModel
						||null==conditionModel.getField()
						||conditionModel.getApplyType()==ApplyTypeEnum.TARGET){
					continue;
				}
				
				conditionList.add(conditionModel);
			}
			
			if(conditionList.size()>0) {
				return handleFieldCondition(conditionList,null);
			}
		}
		return null;
	}
	/**
	 * 获取目标数据查询条件
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handleTargetFieldCondtion(Map<String,Object> row,TableModel table)throws FormatException{
		
		if(null!=table.getWhereModel()){
			
			List<ConditionModel> conditionList=new ArrayList<>();
			
			for(ConditionModel conditionModel:table.getWhereModel().getConditionList()){
				
				if(null==conditionModel
						||null==conditionModel.getField()
						||conditionModel.getApplyType()==ApplyTypeEnum.SOURCE){
					continue;
				}
				
				conditionList.add(conditionModel);
			}
			
			if(conditionList.size()>0) {
				return handleFieldCondition(conditionList,row);
			}
		}
		
		return null;
	}
	
	/**
	 * 获取目标数据限定条件（主要针对主键值进行处理）
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handleTargetConstraintCondtion(Map<String,Object> row,TableModel table)throws FormatException{
		
		if(row.containsKey(table.getSourcePkName().toUpperCase())) {
			Object[] result=new Object[] {row.get(table.getSourcePkName().toUpperCase())};
			
			return result;
					
		}
		return null;
	}
	
	/**
	 * 获取条件参数值
	 * @param conditionList
	 * @return
	 */
	private Object[] handleFieldCondition(List<ConditionModel> conditionList,Map<String,Object> row) {
		
		if(null==conditionList||conditionList.size()==0) {
			return null;
		}
		
		List<Object> result=new ArrayList<>();
		
		for(ConditionModel conditionModel:conditionList) {
			
			if(conditionContext.isValueSkip(conditionModel)){
				continue;
			}
			Object obj=conditionContext.getConditionParamValue(conditionModel,row);
			if(null!=obj){
				//针对多值的情况，如in，not in
				if(obj instanceof Object[]){
					for(Object o:(Object[])obj){
						result.add(o);
					}
				}
			}
			result.add(obj);
		}
		
		return result.toArray();
	}
	/**
	 * 对结果集数据行先进性格式化操作
	 * @param table
	 * @param rowData
	 * @throws FormatException 
	 */
	public void handleFormatRowData(TableModel table,Map<String,Object> rowData) throws FormatException {
		List<FormatModel> formatList=table.getFormatFieldList();
		
		if(null==formatList||formatList.size()==0) {
			return;
		}
		
		for(FormatModel format:formatList) {
			Object value=formaterContext.getFormatedValue(format,rowData.get(format.getFiledName().toUpperCase()));
			
			rowData.put(format.getFiledName().toUpperCase(), value);
		}
	}
	
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handleMigrateField(Map<String,Object> row,TableModel table)throws FormatException{
		//先对数据进行格式化操作
		//handleFormatRowData(table, row);
		
		Object[] field=handleTargetField(row,table);
	
		int fieldLen = field!=null?field.length:0;
		
		Object[] where=handleTargetFieldCondtion(row, table);
		
		int whereLen=where!=null?where.length:0;
		
		if(fieldLen+whereLen>0){
			Object[] result=new Object[fieldLen+whereLen];
			
			if(fieldLen>0){
				System.arraycopy(field, 0, result, 0, fieldLen);
			}
			
			if(whereLen>0){
				System.arraycopy(where, 0, result, fieldLen, whereLen);
			}
			return result;
		}
		
		return null;
	}
	
	/**
	 * 处理字段参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	private Object[] handleTargetField(Map<String,Object> row,TableModel table) throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field || field.getApplyType()==ApplyTypeEnum.SOURCE) {
				continue;
			}
			
			if(field.getValueType()==FieldValueTypeEnum.FIXED) {
				result.add(field.getValue());
			}else if(field.getValueType()==FieldValueTypeEnum.GENCODE){//生成主键
				result.add(IdGenerator.genId());
			}else {
				result.add(row.get(field.getValue().toUpperCase()));
			}
		}
		
		return result.toArray();
	}
	/**
	 * 处理日志字段
	 * @param row
	 * @param table
	 * @return
	 */
	public Map<String,Object> handleForLog(TableModel table,Map<String,Object> row){
		Map<String,Object> data=new HashMap<String,Object>();
		
		data.put(MigrateRowDataHandler.pkName, row.get(table.getSourcePkName().toUpperCase()));
		
		List<FieldModel> fieldList=table.getFiledList();
		
		for(FieldModel field:fieldList) {
			if(field.isForLog()) {
				data.put(field.getName(), row.get(field.getName().toUpperCase()));
			}
		}
		return data;
	}
	/**
	 * 处理字段拆分
	 * @param table
	 * @param row
	 * @return
	 * @throws SplitException 
	 */
	public int handleSplitRowData(TableModel table,Map<String,Object> rowData) throws SplitException {
		
		int size=1;
		
		List<SplitModel> splitModelList=table.getSplitFieldList();
		
		if(null==splitModelList||splitModelList.size()==0) {
			return size;
		}
		
		for(SplitModel splitModel:splitModelList) {
			List<Object> value=spliterContext.getSplitedValue(splitModel, rowData.get(splitModel.getFiledName().toUpperCase()));
			
			rowData.put(splitModel.getFiledName().toUpperCase(), value);
			size=value.size();
		}
		return size;
	}
	
}
