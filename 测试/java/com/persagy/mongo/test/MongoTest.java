package com.persagy.mongo.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;
import com.persagy.mongo.core.service.MongoCoreService;
import com.persagy.mongo.test.base.BaseTest;

public class MongoTest extends BaseTest {

	@Resource(name = "mongoCoreService")
	private MongoCoreService coreService;

	@Test
	public void add() throws Exception {
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setId(1L);
			user.setName("张飞");
			user.setAge(33);
			user.setAddress("北京市");
			user.setCtime(new Date());
			coreService.save(user);
			coreService.remove(new User());
			//coreService.save(user);
		}
	}

	// @Test
	public void batchAdd() throws Exception {
		User user1 = new User();
		user1.setId(1L);
		user1.setName("张飞");
		user1.setAge(33);
		user1.setAddress("北京市");
		user1.setCtime(new Date());
		User user2 = new User();
		user2.setId(2L);
		user2.setName("刘备");
		user2.setAge(37);
		user2.setAddress("南京市");
		user2.setCtime(new Date());
		List<User> list = new ArrayList<>();
		list.add(user1);
		list.add(user2);
		coreService.save(list);
	}

	// @Test
	public void update() throws Exception {
		User queryUser = new User();
		queryUser.setName("刘备");
		User updateUser = new User();
		updateUser.setAge(45);
		System.out.println(coreService.update(queryUser, updateUser));
	}

	// @Test
	public void delete() throws Exception {
		User user = new User();
		user.setName("张飞");
		System.out.println(coreService.remove(user));
	}

	// @Test
	public void query() throws Exception {
		User user = new User();
		// user.setName("张飞");
		// user.setBuildingForContainer("1132312123");
		// user.setCtime(new Date());
		// user.setAge(33);
		// user.setSkip(1);
		// user.setLimit(1);
		user.setSort("age", MongoOrder.Asc);
		user.setSort("name", MongoOrder.Desc);

		user.setMongoSpecialOperation("age", MongoSpecialOperator.$in, new Integer[] { 33 });
		System.out.println(coreService.query(user));
	}

	// @Test
	public void count() throws Exception {
		User user = new User();
		user.setName("刘备");
		System.out.println(coreService.count(user));
	}

}
