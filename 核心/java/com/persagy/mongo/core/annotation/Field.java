package com.persagy.mongo.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.persagy.mongo.core.enumeration.IndexType;

/**
 * Define a field to be used in an index;
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface Field {
	/**
	 * @return "Direction" of the indexing. Defaults to {@link IndexType#ASC}.
	 *
	 * @see IndexType
	 */
	IndexType type() default IndexType.ASC;

	/**
	 * @return Field name to index
	 */
	String value();

}
