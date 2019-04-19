package com.persagy.mongo.core.enumeration;

/**
 * 支持特殊操作类型
 */
public enum MongoSpecialOperator {
	$gt(), $gte, $eq, $ne, $lt, $lte, $like, $in, $nin;// $and, $or;
	//$gt(>)  $gte(>=)  $lt(<)  $lte(<=)  $ne(<>)
	
	
	public static MongoSpecialOperator getByString(String str){
		MongoSpecialOperator[] array = MongoSpecialOperator.values();
		for(MongoSpecialOperator obj : array){
			if(obj.toString().equals(str)){
				return obj;
			}
		}
		return null;
	}
}
 