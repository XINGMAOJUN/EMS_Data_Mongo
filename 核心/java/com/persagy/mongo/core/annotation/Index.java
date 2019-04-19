package com.persagy.mongo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Index {

	Field[] fields() default {};

	IndexOptions options() default @IndexOptions();

	boolean background() default true;

	// 被索引键的值必须是ISODate时间类型,例如new Date()类型。如果是非时间类型，则不会自动删除
	long expireAfterSeconds() default -1L;

	String name() default "";

	boolean sparse() default false;

	boolean unique() default false;

}
