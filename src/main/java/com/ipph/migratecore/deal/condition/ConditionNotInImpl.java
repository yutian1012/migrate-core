package com.ipph.migratecore.deal.condition;

import java.util.Map;

import com.ipph.migratecore.model.ConditionModel;

public class ConditionNotInImpl implements Condition{
	
	private static Condition instance;
	
	private ConditionNotInImpl() {
		
	}
	
	public static synchronized Condition getInstance() {
		if(null==instance) {
			instance=new ConditionNotInImpl();
		}
		return instance;
	}

	@Override
	public String getConditionParam(ConditionModel fieldConditionModel){
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		String value=null;//fieldConditionModel.getValue();
		
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
