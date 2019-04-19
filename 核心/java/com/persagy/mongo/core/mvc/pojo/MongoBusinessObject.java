package com.persagy.mongo.core.mvc.pojo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.persagy.mongo.core.constant.SystemConstant;
import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;

/**
 * 业务对象基类
 */

public abstract class MongoBusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	// 跳过记录数
	private Integer skip;
	// 检索记录数
	private Integer limit;
	// 建筑分表
	private String buildingForContainer;

	// 字段单表特殊操作
	private Map<String, List<MongoSpecialOperation>> specialOperateMap = new LinkedHashMap<String, List<MongoSpecialOperation>>();
	// 排序
	private Map<String, MongoOrder> sortMap = new LinkedHashMap<String, MongoOrder>();

	public void setSpecialOperateMap(Map<String, List<MongoSpecialOperation>> specialOperateMap) {
		this.specialOperateMap = specialOperateMap;
	}

	public void setSortMap(Map<String, MongoOrder> sortMap) {
		this.sortMap = sortMap;
	}

	public Integer getSkip() {
		return skip;
	}

	public void setSkip(Integer skip) {
		this.skip = skip;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getBuildingForContainer() {
		return buildingForContainer;
	}

	public void setBuildingForContainer(String buildingForContainer) {
		this.buildingForContainer = buildingForContainer;
	}

	public Map<String, MongoOrder> getSortMap() {
		return sortMap;
	}

	public Map<String, List<MongoSpecialOperation>> getSpecialOperateMap() {
		return specialOperateMap;
	}

	/**
	 * 设置排序
	 * 
	 * @param fieldName
	 *            对象字段
	 * @param order
	 *            排序类型
	 */
	public void setSort(String fieldName, MongoOrder order) {

		Boolean isFind = false;
		try {
			BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyList) {
				String realFieldName = propertyDescriptor.getName();
				if (realFieldName.equalsIgnoreCase(fieldName)) {
					fieldName = realFieldName;
					isFind = true;
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}

		if (!isFind) {
			return;
		}

		sortMap.put(fieldName, order);
	}

	/**
	 * 设置特殊操作
	 * 
	 * @param fieldName
	 *            对象字段
	 * @param operator
	 *            特殊操作
	 * @param value
	 *            特殊操作值
	 */
	public void setMongoSpecialOperation(String fieldName, MongoSpecialOperator operator, Object value) {

		Boolean isFind = false;
		try {
			BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyList = bi.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyList) {
				String realFieldName = propertyDescriptor.getName();
				if (realFieldName.equalsIgnoreCase(fieldName)) {
					fieldName = realFieldName;
					isFind = true;
				}
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}

		if (!isFind) {
			return;
		}

		List<MongoSpecialOperation> list = specialOperateMap.get(fieldName);
		if (list == null) {
			list = new ArrayList<MongoSpecialOperation>();
			specialOperateMap.put(fieldName, list);
		}
		MongoSpecialOperation operation = new MongoSpecialOperation();
		operation.setSpecialOperator(operator);
		operation.setValue(value);
		list.add(operation);
	}

	@Override
	public String toString() {

		String objectString = null;
		try {
			objectString = SystemConstant.jsonMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectString;
	}

}
