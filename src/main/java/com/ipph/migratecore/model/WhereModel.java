package com.ipph.migratecore.model;

import java.util.List;

public class WhereModel implements Cloneable{
	private List<ConditionModel> conditionList;
	
	public List<ConditionModel> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<ConditionModel> conditionList) {
		this.conditionList = conditionList;
	}

	public WhereModel copyWhereModel(){
		try {
			return (WhereModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
