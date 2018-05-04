package com.ipph.migratecore.deal.condition;

import com.ipph.migratecore.model.FieldConditionModel;

public class ConditionIsNullImpl implements Condition{

	@Override
	public String getConditionParam(FieldConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getConditionType())
			return null;
		
		return fieldConditionModel.getConditionType().getName();
	}

	@Override
	public Object getConditionParamValue(FieldConditionModel fieldConditionModel) {
		return null;
	}

	@Override
	public boolean isValueSkip() {
		return true;
	}

}
