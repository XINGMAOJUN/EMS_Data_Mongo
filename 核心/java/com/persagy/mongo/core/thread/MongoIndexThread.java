package com.persagy.mongo.core.thread;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.persagy.mongo.core.annotation.Entity;
import com.persagy.mongo.core.constant.SystemConstant;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;
import com.persagy.mongo.core.util.PackageScanUtils;
import com.persagy.mongo.core.util.ShardingCollectionUtils;

@Component("mongoIndexThread")
public class MongoIndexThread implements Runnable {

	private static Logger log = Logger.getLogger(MongoIndexThread.class);

	@Resource(name = "datastore")
	private volatile MongoDatabase mongoDB;

	@PostConstruct
	public void init() {
	//	new Thread(this).start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			Thread.sleep(DateUtils.MILLIS_PER_MINUTE * 10);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {
			try {
				log.info(" ******* mongodb索引刷新开始 ****** ");
				SystemConstant.collectionSet.clear();
				MongoCursor<String> iterator = mongoDB.listCollectionNames().iterator();
				while (iterator.hasNext()) {
					//查询初始化所有集合
					SystemConstant.collectionSet.add(iterator.next());
				}
				if (SystemConstant.toEnsureIndexes) {
					Set<Class<?>> classes = PackageScanUtils.getClasses(SystemConstant.mapPackage);
					for (Class<?> cls : classes) {
						if (cls.isAnnotationPresent(Entity.class)) {
							ShardingCollectionUtils.createIndex((Class<MongoBusinessObject>) cls, mongoDB);
						}
					}
				}

				log.info(" ******* mongodb索引刷新成功，10分钟后继续 ****** ");
				Thread.sleep(DateUtils.MILLIS_PER_MINUTE * 10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info(" ******* mongodb索引刷新失败，1分钟后继续 ****** ");
				e.printStackTrace();
				try {
					Thread.sleep(DateUtils.MILLIS_PER_MINUTE);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

}
