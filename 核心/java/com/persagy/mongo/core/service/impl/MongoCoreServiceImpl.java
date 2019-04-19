package com.persagy.mongo.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.core.mvc.pojo.MongoBusinessObject;
import com.persagy.mongo.core.service.MongoCoreService;

@Service("mongoCoreService")
public class MongoCoreServiceImpl implements MongoCoreService {

	@Resource(name = "mongoCoreDao")
	private MongoCoreDao mongoCoreDao;

	@Override
	public <T extends MongoBusinessObject> void save(T object) throws Exception {
		mongoCoreDao.save(object);
	}

	@Override
	public <T extends MongoBusinessObject> void save(List<T> objectList) throws Exception {
		mongoCoreDao.save(objectList);
	}

	@Override
	public <T extends MongoBusinessObject> long update(T object, T updataObject) throws Exception {
		return mongoCoreDao.update(object, updataObject);
	}

	@Override
	public <T extends MongoBusinessObject> long remove(T object) throws Exception {
		return mongoCoreDao.remove(object);
	}

	@Override
	public <T extends MongoBusinessObject> List<T> query(T object) throws Exception {
		return mongoCoreDao.query(object);
	}

	@Override
	public <T extends MongoBusinessObject> long count(T object) throws Exception {
		return mongoCoreDao.count(object);
	}

}
