package com.ipph.migratecore.model;

import java.io.Serializable;
import java.util.List;

public class WhereModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<ConditionModel> conditionList;
	
	public List<ConditionModel> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<ConditionModel> conditionList) {
		this.conditionList = conditionList;
	}
}
