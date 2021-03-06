package com.ipph.migratecore.deal.condition;

import java.util.Map;

import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.ConditionModel;
import com.ipph.migratecore.model.FieldModel;

public class ConditionInImpl implements Condition{
	
	private static Condition instance;
	
	private ConditionInImpl() {
	}
	
	public static synchronized Condition getInstance() {
		if(null==instance) {
			instance=new ConditionInImpl();
		}
		return instance;
	}

	@Override
	public String getConditionParam(ConditionModel fieldConditionModel){
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		String value=null;
		
		FieldModel field=fieldConditionModel.getField();
		if(null!=field&&field.getValueType()==FieldValueTypeEnum.FIXED) {
			value=field.getValue();
		}
		
		if(null==value||"".equals(value)){
			//throw new ConditionException("in 条件参数设置问题");
			return null;
		}
		
		String[] values=value.split(",");
		
		if(null!=values&&values.length>0){
			
			StringBuilder condition=new StringBuilder();
			
			condition.append(fieldConditionModel.getType().getName()).append(" (");
			
			for(String v:values){
				condition.append("'").append(v).append("'").append(",");
			}
			
			condition.setLength(condition.length()-1);
			
			condition.append(")");
			
			return condition.toString();
		}
		
		return null;
		
	}

	@Override
	public Object getConditionParamValue(ConditionModel fieldConditionModel,Map<String,Object> rowData) {
		return null;
	}

	@Override
	public boolean isValueSkip() {
		return true;
	}

}
