package com.ipph.migratecore.deal.condition;

import com.ipph.migratecore.model.ConditionModel;

public interface Condition {

	public String getConditionParam(ConditionModel conditionModel);
	
	public Object getConditionParamValue(ConditionModel conditionModel);
	
	public boolean isValueSkip();
}
