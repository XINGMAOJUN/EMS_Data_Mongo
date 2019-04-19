package com.persagy.mongo.core.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.persagy.mongo.core.annotation.Month;
import com.persagy.mongo.core.constant.SystemConstant;
import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;
import com.persagy.mongo.core.mvc.pojo.MongoSpecialOperation;
import com.persagy.mongo.core.util.BeanUtils;
import com.persagy.mongo.core.util.IndexValidation;
import com.persagy.mongo.core.util.ShardingCollectionUtils;

@Repository("mongoCoreDao")
public class MongoCoreDaoImpl implements MongoCoreDao {

	@Resource(name = "datastore")
	private MongoDatabase mongoDB;

	/**
	 * 保存
	 */
	@Override
	public <T extends MongoBusinessObject> void save(T object) throws Exception {
		String collectionName = ShardingCollectionUtils.getShardingCollectionName(object);
		this.checkIndex(object, collectionName);
		InsertOneOptions options = new InsertOneOptions();//不开启验证/ 效率更快
		options.bypassDocumentValidation(true);
		mongoDB.getCollection(collectionName).insertOne(BeanUtils.beanToDocument(object),options);
		SystemConstant.collectionSet.add(collectionName);
	}

	/**
	 * 批量保存
	 */
	@Override
	public <T extends MongoBusinessObject> void save(List<T> objectList) throws Exception {
		if (objectList == null || objectList.size() == 0) {
			throw new Exception("批量保存对象不能为空");
		}

		if (!ShardingCollectionUtils.checkMonthSharding(objectList.get(0).getClass())
				&& !ShardingCollectionUtils.checkBuildingSharding(objectList.get(0).getClass())) {
			String collectionName = ShardingCollectionUtils.getShardingCollectionName(objectList.get(0));
			List<Document> list = new ArrayList<>();
			for (T object : objectList) {
				list.add(BeanUtils.beanToDocument(object));
			}
			this.checkIndex(objectList.get(0), collectionName);
			InsertManyOptions options = new InsertManyOptions();//不开启验证/ 效率更快
			options.bypassDocumentValidation(true);
			options.ordered(false);
			mongoDB.getCollection(collectionName).insertMany(list, options);
			SystemConstant.collectionSet.add(collectionName);
		} else {
			for (T object : objectList) {
				this.save(object);
			}
		}
	}

	/**
	 * 
	 * 新建的表建立索引
	 * 
	 * @param object
	 * @param collectionName
	 * @throws Exception
	 */
	private <T extends MongoBusinessObject> void checkIndex(T object, String collectionName) throws Exception {
		if (!SystemConstant.collectionSet.contains(collectionName)) {
			Class<? extends MongoBusinessObject> cls = object.getClass();
			List<IndexModel> indexList = IndexValidation.fieldIndex(cls);
			indexList.addAll(IndexValidation.classIndex(cls));
			mongoDB.getCollection(collectionName).createIndexes(indexList);
			SystemConstant.collectionSet.add(collectionName);
		}
	}

	/**
	 * 更新
	 */
	@Override
	public <T extends MongoBusinessObject> long update(T object, T updataObject) throws Exception {
		Document queryDocument = this.queryDocument(object);
		Document updateDocument = new Document("$set", this.queryDocument(updataObject));

		List<String> shardingCollectionList = ShardingCollectionUtils.getShardingCollection(object, mongoDB);
		if (shardingCollectionList == null || shardingCollectionList.size() == 0) {
			return 0;
		}

		Long count = 0L;
		UpdateOptions options = new UpdateOptions();
		options.bypassDocumentValidation(true);
		for (String collection : shardingCollectionList) {
			count += mongoDB.getCollection(collection).updateMany(queryDocument, updateDocument,options).getModifiedCount();
		}

		return count;
	}

	/**
	 * 刪除
	 */
	@Override
	public <T extends MongoBusinessObject> long remove(T object) throws Exception {
		Document queryDocument = this.queryDocument(object);

		List<String> shardingCollectionList = ShardingCollectionUtils.getShardingCollection(object, mongoDB);
		if (shardingCollectionList == null || shardingCollectionList.size() == 0) {
			return 0;
		}

		Long count = 0L;
		DeleteOptions options = new DeleteOptions();
		for (String collection : shardingCollectionList) {
			count += mongoDB.getCollection(collection).deleteMany(queryDocument,options).getDeletedCount();
		}

		return count;
	}

	/**
	 * 查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MongoBusinessObject> List<T> query(T object) throws Exception {
		// 不分表情况

		if (!ShardingCollectionUtils.checkMonthSharding(object.getClass())
				&& !ShardingCollectionUtils.checkBuildingSharding(object.getClass())) {
			FindIterable<Document> dbCursor = this.queryDBCursor(object);
			Iterator<Document> iterator = dbCursor.iterator();
			List<T> list = new ArrayList<>();
			while (iterator.hasNext()) {
				list.add((T) BeanUtils.documentToBean(iterator.next(), object.getClass().newInstance()));
			}
			return list;
		}

		// ******************************************************************************

		List<T> list = new ArrayList<>();

		Document queryDocument = this.queryDocument(object);
		Document orderDocument = this.orderDocument(object);
		Integer skip = object.getSkip();
		if (skip == null) {
			// throw new Exception("查询条件skip不能为空");
			skip = 0;
		}

		Integer limit = object.getLimit();
		if (limit == null) {
			// throw new Exception("查询条件limit不能为空");
			limit = Integer.MAX_VALUE;
		}

		List<String> shardingCollectionList = ShardingCollectionUtils.getShardingCollection(object, mongoDB);
		if (shardingCollectionList == null || shardingCollectionList.size() == 0) {
			return new ArrayList<>();
		}

		// 按月分表排序
		Map<String, MongoOrder> sortMap = object.getSortMap();
		if (ShardingCollectionUtils.checkMonthSharding(object.getClass())) {
			String column = ((Month) object.getClass().getAnnotation(Month.class)).column();
			if (sortMap.containsKey(column)) {
				MongoOrder order = sortMap.get(column);
				if (order.equals(MongoOrder.Asc)) {
					Collections.sort(shardingCollectionList, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.substring(0, 6).compareTo(o2.substring(0, 6));
						}
					});
				} else if (order.equals(MongoOrder.Desc)) {
					Collections.sort(shardingCollectionList, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o2.substring(0, 6).compareTo(o1.substring(0, 6));
						}
					});
				}
			}
		}

		Integer count = 0;
		Integer skipTemp = 0;
		Integer limitTemp = 0;
		// 剩余条数
		Integer surplusLimit = limit;
		// 剩余跳过数
		Integer surplusSkip = skip;
		for (String collection : shardingCollectionList) {
			if (surplusLimit == 0) {
				break;
			}
			FindIterable<Document> dbCur = mongoDB.getCollection(collection).find(queryDocument).batchSize(2000);
			count = (int) this.count(dbCur);
			if (count >= surplusSkip) {
				skipTemp = surplusSkip;
				surplusSkip = surplusSkip - skipTemp;
			} else {
				skipTemp = count;
				surplusSkip = surplusSkip - skipTemp;
			}
			if (count >= skipTemp + surplusLimit) {
				limitTemp = surplusLimit;
				surplusLimit = surplusLimit - limitTemp;
			} else {
				limitTemp = count - skipTemp;
				surplusLimit = surplusLimit - limitTemp;
			}
			if (orderDocument != null) {
				dbCur = dbCur.sort(orderDocument);
			}
			Iterator<Document> iterator = dbCur.skip(skipTemp).limit(limitTemp).iterator();
			while (iterator.hasNext()) {
				list.add((T) BeanUtils.documentToBean(iterator.next(), object.getClass().newInstance()));
			}
		}
		return list;
	}

	/**
	 * 查询条数
	 */
	@Override
	public <T extends MongoBusinessObject> long count(T object) throws Exception {
		Document queryDocument = this.queryDocument(object);

		List<String> shardingCollectionList = ShardingCollectionUtils.getShardingCollection(object, mongoDB);
		if (shardingCollectionList == null || shardingCollectionList.size() == 0) {
			return 0;
		}

		Long count = 0L;
		for (String collection : shardingCollectionList) {
			FindIterable<Document> document = mongoDB.getCollection(collection).find(queryDocument);
			count += this.count(document);
		}

		return count;
	}

	/**
	 * 统计查询结果集数量
	 * 
	 * @param document
	 * @return
	 */
	private long count(FindIterable<Document> document) {
		Long count = 0L;
		MongoCursor<Document> iterator = document.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			count += 1;
		}
		return count;
	}

	/**
	 * 查询游标
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private <T extends MongoBusinessObject> FindIterable<Document> queryDBCursor(T object) throws Exception {
		String collectionName = ShardingCollectionUtils.getCollectionName(object);

		MongoCollection<Document> dbc = mongoDB.getCollection(collectionName);

		Class tempClass = object.getClass();
		List<Field> fieldList = new ArrayList<>();
		fieldList = this.getAllField(tempClass);

		Document commonDocument = new Document();
		Document orderDocument = new Document();
		Integer skip = null;
		Integer limit = null;
		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			if (field.get(object) == null) {
				continue;
			}

			if (SystemConstant.serialVersionUID.equals(fieldName)) {
				continue;
			}
			// 特殊操作符
			else if (SystemConstant.specialOperateMap.equals(fieldName)) {
				Map<String, List<MongoSpecialOperation>> specialMap = object.getSpecialOperateMap();
				this.special(commonDocument, specialMap);
			}
			// 排序
			else if (SystemConstant.sortMap.equals(fieldName)) {
				Map<String, MongoOrder> sortMap = object.getSortMap();
				for (Entry<String, MongoOrder> entry : sortMap.entrySet()) {
					// mongodb中按字段倒序查询（-1是倒序，1是正序）
					if (entry.getValue().equals(MongoOrder.Asc)) {
						// 递增
						orderDocument.put(entry.getKey(), 1);
					}
					if (entry.getValue().equals(MongoOrder.Desc)) {
						// 递减
						orderDocument.put(entry.getKey(), -1);
					}

				}
			}
			// 跳过记录数
			else if (SystemConstant.skip.equals(fieldName)) {
				skip = object.getSkip();
			}
			// 检索记录数
			else if (SystemConstant.limit.equals(fieldName)) {
				limit = object.getLimit();
			}
			// 普通字段
			else {
				commonDocument.put(fieldName, field.get(object));
			}
		}

		FindIterable<Document> dbCursor = dbc.find(commonDocument); // dbc.find(commonDocument);

		if (orderDocument != null) {
			dbCursor.sort(orderDocument);
		}
		if (skip != null) {
			dbCursor.skip(skip);
		}
		if (limit != null) {
			dbCursor.skip(limit);
		}
		return dbCursor;
	}

	/**
	 * 排序DBObject
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	private <T extends MongoBusinessObject> Document orderDocument(T object) throws Exception {

		Document orderDocument = new Document();

		// 排序
		Map<String, MongoOrder> sortMap = object.getSortMap();
		for (Entry<String, MongoOrder> entry : sortMap.entrySet()) {
			// mongodb中按字段倒序查询（-1是倒序，1是正序）
			if (entry.getValue().equals(MongoOrder.Asc)) {
				// 递增
				orderDocument.put(entry.getKey(), 1);
			}
			if (entry.getValue().equals(MongoOrder.Desc)) {
				// 递减
				orderDocument.put(entry.getKey(), -1);
			}
		}

		return orderDocument;
	}

	/**
	 * 查询对象转DBObject
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private <T extends MongoBusinessObject> Document queryDocument(T object) throws Exception {

		Class tempClass = object.getClass();
		List<Field> fieldList = new ArrayList<>();
		fieldList = this.getAllField(tempClass);

		Document commonDocument = new Document();

		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			if (field.get(object) == null) {
				continue;
			}

			if (SystemConstant.serialVersionUID.equals(fieldName) || SystemConstant.sortMap.equals(fieldName)
					|| SystemConstant.skip.equals(fieldName) || SystemConstant.limit.equals(fieldName)
					|| SystemConstant.buildingForContainer.equals(fieldName)) {
				continue;
			}
			// 特殊操作符
			else if (SystemConstant.specialOperateMap.equals(fieldName)) {
				Map<String, List<MongoSpecialOperation>> specialMap = object.getSpecialOperateMap();
				this.special(commonDocument, specialMap);
			}
			// 普通字段
			else {
				commonDocument.put(fieldName, field.get(object));
			}
		}

		return commonDocument;
	}

	/**
	 * 获取该类及父类所有字段
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<Field> getAllField(Class cls) {
		List<Field> fieldList = new ArrayList<>();
		// 获取该类所有字段（包括父类）
		while (cls != null) {
			// 当父类为null的时候说明到达了最上层的父类(Object类).
			fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
			// 得到父类,然后赋给自己
			cls = cls.getSuperclass();
		}
		return fieldList;
	}

	/**
	 * 特殊操作转DBObject
	 * 
	 * @param specialDBObject
	 * @param specialMap
	 */
	private void special(Document specialDocument, Map<String, List<MongoSpecialOperation>> specialMap) {

		for (Entry<String, List<MongoSpecialOperation>> entry : specialMap.entrySet()) {
			String specialName = entry.getKey();

			for (MongoSpecialOperation specialOperation : entry.getValue()) {

				MongoSpecialOperator specialOperator = specialOperation.getSpecialOperator();
				switch (specialOperator) {

				case $gt:
				case $gte:
				case $eq:
				case $ne:
				case $lt:
				case $lte:
					if (specialDocument.containsKey(specialName)) {
						Document doc = (Document) specialDocument.get(specialName);
						specialDocument.put(specialName,
								doc.append(specialOperator.toString(), specialOperation.getValue()));
					} else {
						specialDocument.put(specialName,
								new Document(specialOperator.toString(), specialOperation.getValue()));
					}

					break;
				case $like:
					Pattern pattern = Pattern.compile(specialOperation.getValue().toString(), Pattern.CASE_INSENSITIVE);
					specialDocument.put(specialName, pattern);
					break;

				case $in:
				case $nin:
					Object[] objs = (Object[]) specialOperation.getValue();
					BasicDBList dbList = new BasicDBList();
					for (Object obj : objs) {
						dbList.add(obj);
					}
					specialDocument.put(specialName, new Document(specialOperator.toString(), dbList));
					break;
				default:
					break;
				}

			}
		}
	}

}
