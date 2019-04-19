package com.persagy.mongo.test.sharding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;
import com.persagy.mongo.core.service.MongoCoreService;
import com.persagy.mongo.test.base.BaseTest;

public class ShardingMongoTest extends BaseTest {

	@Resource(name = "mongoCoreService")
	private MongoCoreService coreService;

	private static String buildingId1 = "1101010001";
	private static String buildingId2 = "1101010002";
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring.xml");
	// CoreService core = (CoreServiceImpl) context.getBean("coreService");

	//@Test
	public void add() throws Exception {
		Student student = new Student();
		student.setId(1L);
		student.setName("张飞");
		student.setAge(33);
		student.setAddress("北京市");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		student.setCtime( sdf.parse("2008-12-31 16:10:12"));
		student.setBuildingForContainer(buildingId2);
		coreService.save(student);
	}

	// @Test
	public void batchAdd() throws Exception {
		Student student1 = new Student();
		student1.setId(1L);
		student1.setName("张飞");
		student1.setAge(33);
		student1.setAddress("北京市");
		student1.setCtime(new Date());
		Student student2 = new Student();
		student2.setId(2L);
		student2.setName("刘备");
		student2.setAge(37);
		student2.setAddress("南京市");
		student2.setCtime(new Date());
		List<Student> list = new ArrayList<>();
		list.add(student1);
		list.add(student2);
		coreService.save(list);
	}

	// @Test
	public void update() throws Exception {
		Student queryStudent = new Student();
		//queryStudent.setName("张飞");
		Student updateStudent = new Student();
		updateStudent.setAge(45);
		System.out.println(coreService.update(queryStudent, updateStudent));
	}

	// @Test
	public void delete() throws Exception {
		Student student = new Student();
		student.setName("张飞");
		System.out.println(coreService.remove(student));
	}

	 @Test
	public void query() throws Exception {
		 Student student = new Student();
		student.setName("张飞");
		 student.setBuildingForContainer("1101010002");
		// student.setCtime(new Date());
		// student.setAge(29);
		 student.setSkip(0);
		 student.setLimit(30);
		student.setSort("ctime", MongoOrder.Asc);
		// student.setSort("name", MongoOrder.Desc);
		// student.setSpecialOperation("id", MongoSpecialOperator.$nin, new Integer[] {1,2,6});
		// student.setMongoSpecialOperation("id", MongoSpecialOperator.$lt, 5);
		 //student.setSpecialOperation("name", MongoSpecialOperator.$like, "张34");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		 student.setMongoSpecialOperation("ctime",MongoSpecialOperator.$gte, sdf.parse("2019-01-02 16:10:12"));
		 student.setMongoSpecialOperation("ctime",MongoSpecialOperator.$lt, sdf.parse("2019-01-18 16:10:12"));
		List<Student> list = coreService.query(student);
		for(Student stu:list) {
			System.out.print(stu.getId()+",");
		}
		
	}

//	@Test
	public void count() throws Exception {
		Student student = new Student();
		//student.setName("张飞");
		System.out.println(coreService.count(student));
	}

}
