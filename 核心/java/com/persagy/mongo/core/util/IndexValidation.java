package com.persagy.mongo.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Collation.Builder;
import com.persagy.mongo.core.annotation.Index;
import com.persagy.mongo.core.annotation.Indexed;
import com.persagy.mongo.core.annotation.Indexes;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;

/**
 * 索引校验
 *
 */
public class IndexValidation {
	@SuppressWarnings("rawtypes")
	public static <T extends MongoBusinessObject> List<IndexModel> fieldIndex(Class cls) throws Exception {
		List<IndexModel> indexList = new ArrayList<>();
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(Indexed.class)) {
				Document document = new Document();
				IndexOptions indexOptions = new IndexOptions();

				Indexed index = field.getAnnotation(Indexed.class);
				document.put(field.getName(), index.value().toIndexValue());
				com.persagy.mongo.core.annotation.IndexOptions indexOptionsAnnotation = index.options();
				indexOptions.background(index.background());
				indexOptions.unique(index.unique());
				indexOptions.name(index.name());
				indexOptions.sparse(index.sparse());
				indexOptions.expireAfter(index.expireAfterSeconds(), TimeUnit.SECONDS);

				com.persagy.mongo.core.annotation.Collation collationAnnotation = indexOptionsAnnotation.collation();

				Builder builder = Collation.builder();
				builder.backwards(collationAnnotation.backwards());
				builder.caseLevel(collationAnnotation.caseLevel());
				builder.locale(collationAnnotation.locale());
				builder.normalization(collationAnnotation.normalization());
				builder.numericOrdering(collationAnnotation.numericOrdering());
				builder.collationAlternate(collationAnnotation.alternate());
				builder.collationCaseFirst(collationAnnotation.caseFirst());
				builder.collationMaxVariable(collationAnnotation.maxVariable());
				builder.collationStrength(collationAnnotation.strength());

				// Collation collation = builder.build();
				// indexOptions.collation(collation);
				indexList.add(new IndexModel(document, indexOptions));
			}
		}
		return indexList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends MongoBusinessObject> List<IndexModel> classIndex(Class cls) throws Exception {
		List<IndexModel> indexList = new ArrayList<>();
		if (cls.isAnnotationPresent(Indexes.class)) {
			Document document = new Document();
			IndexOptions indexOptions = new IndexOptions();
			Indexes indexes = (Indexes) cls.getAnnotation(Indexes.class);
			Index[] indexs = indexes.value();
			for (Index index : indexs) {
				com.persagy.mongo.core.annotation.Field[] fields = index.fields();
				for (com.persagy.mongo.core.annotation.Field field : fields) {
					if (checkCombinedIndexField(cls, field.value())) {
						document.put(field.value(), field.type().toIndexValue());
					} else {
						throw new Exception("索引" + field.value() + "字段不存在");
					}
				}

				indexOptions.background(index.background());
				indexOptions.unique(index.unique());
				indexOptions.name(index.name());
				indexOptions.sparse(index.sparse());
				
				//indexOptions.expireAfter(index.expireAfterSeconds(), TimeUnit.SECONDS);

				com.persagy.mongo.core.annotation.IndexOptions indexOptionsAnnotation = index.options();
				com.persagy.mongo.core.annotation.Collation collationAnnotation = indexOptionsAnnotation.collation();

				Builder builder = Collation.builder();
				builder.backwards(collationAnnotation.backwards());
				builder.caseLevel(collationAnnotation.caseLevel());
				builder.locale(collationAnnotation.locale());
				builder.normalization(collationAnnotation.normalization());
				builder.numericOrdering(collationAnnotation.numericOrdering());
				builder.collationAlternate(collationAnnotation.alternate());
				builder.collationCaseFirst(collationAnnotation.caseFirst());
				builder.collationMaxVariable(collationAnnotation.maxVariable());
				builder.collationStrength(collationAnnotation.strength());

				// Collation collation = builder.build();
				// indexOptions.collation(collation);
				indexList.add(new IndexModel(document, indexOptions));
			}
		}
		return indexList;
	}

	/**
	 * 校验组合索引字段是否正确
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	private static <T extends MongoBusinessObject> Boolean checkCombinedIndexField(Class cls, String fieldName)
			throws InstantiationException, IllegalAccessException {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return true;
			}
		}
		return true;
	}

}
