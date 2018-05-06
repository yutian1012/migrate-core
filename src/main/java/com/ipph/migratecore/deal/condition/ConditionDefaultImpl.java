package com.ipph.migratecore.deal.condition;

import java.util.Map;

import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.ConditionModel;
import com.ipph.migratecore.model.FieldModel;

public class ConditionDefaultImpl implements Condition{
	
	private static Condition instance;
	
	private ConditionDefaultImpl() {
	}
	
	public static synchronized Condition getInstance() {
		if(null==instance) {
			instance=new ConditionDefaultImpl();
		}
		return instance;
	}

	@Override
	public String getConditionParam(ConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getType())
			return null;
		
		return fieldConditionModel.getType().getName()+" ? ";
	}

	@Override
	public Object getConditionParamValue(ConditionModel fieldConditionModel,Map<String,Object> rowData) {
		if(null==fieldConditionModel||null==fieldConditionModel.getField())
			return null;
		
		FieldModel field=fieldConditionModel.getField();
		
		if(field.getValueType()==FieldValueTypeEnum.FIXED) {
			return field.getValue();
		}else {
			return rowData.get(field.getValue().toUpperCase());
		}
	}

	@Override
	public boolean isValueSkip() {
		return false;
	}

}
