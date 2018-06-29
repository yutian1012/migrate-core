package com.ipph.migratecore.enumeration;

import com.ipph.migratecore.deal.condition.Condition;
import com.ipph.migratecore.deal.condition.ConditionDefaultImpl;
import com.ipph.migratecore.deal.condition.ConditionInImpl;
import com.ipph.migratecore.deal.condition.ConditionIsNullImpl;

public enum FieldConditionTypeEnum {
	LIKE("like") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},LLIKE("like") {
		@Override
		public Condition getConditionHandler() {
			return null;
		}
	},RLIKE("like") {
		@Override
		public Condition getConditionHandler() {
			return null;
		}
	},EQUAL("=") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},NEQUAL("!=") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},IN("in") {
		@Override
		public Condition getConditionHandler() {
			return ConditionInImpl.getInstance();
		}
	},NOTIN("not in") {
		@Override
		public Condition getConditionHandler() {
			return ConditionInImpl.getInstance();
		}
	},GT(">") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},GE(">=") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},LT("<") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},LE("<=") {
		@Override
		public Condition getConditionHandler() {
			return ConditionDefaultImpl.getInstance();
		}
	},ISNULL("is null") {
		@Override
		public Condition getConditionHandler() {
			return ConditionIsNullImpl.getInstance();
		}
	},ISNOTNULL("is not null") {
		@Override
		public Condition getConditionHandler() {
			return ConditionIsNullImpl.getInstance();
		}
	};
	private String name;
	private FieldConditionTypeEnum(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	public abstract Condition getConditionHandler();
}
