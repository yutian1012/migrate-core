package com.ipph.migratecore.deal.condition;

import java.util.Map;

import com.ipph.migratecore.model.ConditionModel;

public interface Condition {

	public String getConditionParam(ConditionModel conditionModel);
	
	public Object getConditionParamValue(ConditionModel conditionModel,Map<String,Object> rowData);
	
	public boolean isValueSkip();
}
