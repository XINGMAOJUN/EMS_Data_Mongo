package com.persagy.mongo.core.constant;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 系统常量
 * 
 * @author KJ
 *
 */
public class SystemConstant {

	public static final ObjectMapper jsonMapper = new ObjectMapper();
	public static final ObjectMapper xmlMapper = new XmlMapper();

	public static final String serialVersionUID = "serialVersionUID";
	public static final String skip = "skip";
	public static final String limit = "limit";
	public static final String specialOperateMap = "specialOperateMap";
	public static final String sortMap = "sortMap";
	public static final String buildingForContainer = "buildingForContainer";
	// 指定要映射的包名
	public static String mapPackage = "";
	//是否确认索引
	public static boolean toEnsureIndexes = false;
	//shujuj集合set
	public static Set<String> collectionSet =  Collections.synchronizedSet(new HashSet<String>());

	static {
		jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

}
