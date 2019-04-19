package com.persagy.mongo.core.mvc.pojo;

import com.persagy.mongo.core.enumeration.MongoSpecialOperator;

/**
 * 特殊操作类
 */
public class MongoSpecialOperation {

	// 操作符
	private MongoSpecialOperator specialOperator;
	// 值
	private Object value;

	public MongoSpecialOperator getSpecialOperator() {
		return specialOperator;
	}

	public void setSpecialOperator(MongoSpecialOperator specialOperator) {
		this.specialOperator = specialOperator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SpecialOperation [specialOperator=" + specialOperator + ", value=" + value + "]";
	}

}
