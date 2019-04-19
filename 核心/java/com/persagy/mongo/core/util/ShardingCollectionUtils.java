package com.persagy.mongo.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.persagy.mongo.core.annotation.Dimension;
import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.annotation.Month;
import com.persagy.mongo.core.constant.SystemConstant;
import com.persagy.mongo.core.enumeration.MongoDimension;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

public class ShardingCollectionUtils {

	/**
	 * 获取分表后集合名
	 * 
	 * @param object
	 * @return
	 * @throws @throws
	 *             Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends MongoBusinessObject> String getShardingCollectionName(T object) throws Exception {

		Class clazz = object.getClass();
		String collectionName = "";

		if (clazz.isAnnotationPresent(Entity.class)) {
			collectionName = ((Entity) clazz.getAnnotation(Entity.class)).value();
		} else {
			String className = clazz.getSimpleName();
			String temp = className.substring(0, 1);
			collectionName = className.replaceFirst(temp, temp.toLowerCase());
		}

		if (clazz.isAnnotationPresent(Month.class)) {
			Annotation anno = clazz.getAnnotation(Month.class);
			String name = ((Month) anno).column();
			Field field = clazz.getDeclaredField(name);
			if (field.getType().equals(Date.class)) {
				// 月份分表
				field.setAccessible(true);
				Date date = (Date) field.get(object);
				if (date == null) {
					throw new Exception(name + "字段不能为空");
				}
				DateFormat format = new SimpleDateFormat("yyyyMM");
				collectionName = format.format(date) + "_" + collectionName;
			}
		}
		if (clazz.isAnnotationPresent(Dimension.class)) {
			Annotation anno = clazz.getAnnotation(Dimension.class);
			if (((Dimension) anno).dimension().equals(MongoDimension.Building)) {
				// 建筑分表
				if (object.getBuildingForContainer() != null && !"".equals(object.getBuildingForContainer())) {
					collectionName = collectionName + "_" + object.getBuildingForContainer();
				} else {
					throw new Exception("建筑分表buildingId不能为空");
				}
			}
		}

		return collectionName;
	}

	/**
	 * 获取集合名
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends MongoBusinessObject> String getCollectionName(T object) throws Exception {

		Class clazz = object.getClass();
		String collectionName = "";

		if (clazz.isAnnotationPresent(Entity.class)) {
			collectionName = ((Entity) clazz.getAnnotation(Entity.class)).value();
		} else {
			String className = clazz.getSimpleName();
			String temp = className.substring(0, 1);
			collectionName = className.replaceFirst(temp, temp.toLowerCase());
		}
		return collectionName;
	}

	/**
	 * 查询bean分表后对应集合名
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends MongoBusinessObject> List<String> getShardingCollection(T object, MongoDatabase mongoDB)
			throws Exception {
		Set<String> collectionSet = collectionSet(mongoDB);
		if (collectionSet == null || collectionSet.size() == 0) {
			return new ArrayList();
		}
		String collectionName = ShardingCollectionUtils.getCollectionName(object);
		List<String> shardingCollectionList = new ArrayList<>();
		Boolean checkMonthSharding = checkMonthSharding(object.getClass());
		Boolean checkBuildingSharding = checkBuildingSharding(object.getClass());

		if (checkMonthSharding && checkBuildingSharding) {
			String buildingId = object.getBuildingForContainer();
			if (buildingId != null) {
				collectionName = "_" + collectionName + "_" + buildingId;
			} else {
				collectionName = "_" + collectionName + "_";
			}

			for (String collection : collectionSet) {
				if (collection.length() > 6 && collection.substring(6).startsWith(collectionName)) {
					shardingCollectionList.add(collection);
				}
			}

		} else if (checkMonthSharding && !checkBuildingSharding) {
			collectionName = "_" + collectionName;

			for (String collection : collectionSet) {

				if (collection.length() > 6 && collection.substring(6).startsWith(collectionName)) {
					shardingCollectionList.add(collection);
				}
			}

		} else if (!checkMonthSharding && checkBuildingSharding) {
			String buildingId = object.getBuildingForContainer();
			if (buildingId != null) {
				collectionName = collectionName + "_" + buildingId;
			} else {
				collectionName = collectionName + "_";
			}

			for (String collection : collectionSet) {
				if (collection.equals(collectionName)) {
					shardingCollectionList.add(collection);
				}
			}

		} else {
			shardingCollectionList.add(collectionName);
		}

		return shardingCollectionList;
	}

	/**
	 * 校验是否按建筑分表
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends MongoBusinessObject> Boolean checkBuildingSharding(Class clazz) {
		if (clazz.isAnnotationPresent(Dimension.class)) {
			Annotation anno = clazz.getAnnotation(Dimension.class);
			if (((Dimension) anno).dimension().equals(MongoDimension.Building)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 校验是否按月分表
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends MongoBusinessObject> Boolean checkMonthSharding(Class clazz) {
		if (clazz.isAnnotationPresent(Month.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取mongo所有集合
	 * 
	 * @return
	 */
	public static Set<String> collectionSet(MongoDatabase mongoDB) {
		//Set<String> collectionSet = new HashSet<>();
		//MongoCursor<String> collections = mongoDB.listCollectionNames().iterator();
		//while (collections.hasNext()) {
			//collectionSet.add(collections.next());
		//}
		
		//return collectionSet;
		return SystemConstant.collectionSet;
	}

	/**
	 * 给object对应的集合创建索引
	 * 
	 * @param object
	 * @param mongoDB
	 * @throws Exception
	 */
	public static <T extends MongoBusinessObject> void createIndex(Class<MongoBusinessObject> cls,
			MongoDatabase mongoDB) throws Exception {
		List<String> collectionNameList = getShardingCollection(cls.newInstance(), mongoDB);
		if (collectionNameList == null || collectionNameList.size() == 0) {
			return;
		}
		for (String collectionName : collectionNameList) {
			List<IndexModel> indexList = IndexValidation.fieldIndex(cls);
			indexList.addAll(IndexValidation.classIndex(cls));
			MongoCursor<Document> currentIndexs = mongoDB.getCollection(collectionName).listIndexes().iterator();
			List<String> currentIndexNames = new ArrayList<>();
			while (currentIndexs.hasNext()) {
				currentIndexNames.add(currentIndexs.next().getString("name"));
			}
			List<IndexModel> list = new ArrayList<>();
			for (String currentIndexName : currentIndexNames) {
				for (IndexModel indexModel : indexList) {
					if (indexModel.getOptions().getName().equals(currentIndexName)) {
						list.add(indexModel);
					}
				}
			}

			indexList.removeAll(list);
			if (indexList != null && indexList.size() > 0) {
				mongoDB.getCollection(collectionName).createIndexes(indexList);
			}
		}
	}
}
