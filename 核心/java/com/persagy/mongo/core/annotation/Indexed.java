package com.persagy.mongo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.persagy.mongo.core.enumeration.IndexDirection;

/**
 * @author KJ
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Indexed {

	IndexDirection value() default IndexDirection.ASC;

	//后台创建索引减少数据库写锁占用时间
	boolean background() default true;

	long expireAfterSeconds() default -1L;

	String name();

	boolean sparse() default false;

	boolean unique() default false;

	IndexOptions options() default @IndexOptions();
}
