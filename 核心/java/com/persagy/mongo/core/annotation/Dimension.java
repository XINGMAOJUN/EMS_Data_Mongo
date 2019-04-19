package com.persagy.mongo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.persagy.mongo.core.enumeration.MongoDimension;

/**
 * 数据维度
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Dimension {

	//数据维度枚举
	MongoDimension dimension() default MongoDimension.No;
}
