package com.persagy.mongo.core.dao;

import java.util.List;

import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

public interface MongoCoreDao {
	
	/**
	 * 单条记录保存
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> void save(T object) throws Exception;
	
	/**
	 * 批量保存
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> void save(List<T> objectList) throws Exception;


	/**
	 * 按条件更新
	 * @param object
	 * @return 受影响行数
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> long update(T object,T updataObject) throws Exception;
	
	
	/**
	 * 按条件删除
	 * @param object
	 * @return 受影响行数
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> long remove(T object) throws Exception;
	
	
	
	
	/**
	 * 按条件查询
	 * @param object
	 * @return 满足条件记录
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> List<T> query(T object) throws Exception;
	

	/**
	 * 按条件查询
	 * @param object
	 * @return 满足条件记录条数
	 * @throws Exception
	 */
	public  <T extends MongoBusinessObject> long count(T object) throws Exception;
}
