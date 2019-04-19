package com.persagy.mongo.test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.client.MongoDatabase;
import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.test.mindin.MGElectricCurrentData;
import com.persagy.mongo.test.mindin.MindinTest;

public class Main {

	private static Logger log = Logger.getLogger(Main.class);

	private static String buildingId = "4101020001";

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

		// MongoDatabase core = (MongoDatabase) context.getBean("datastore");

		MongoCoreDao mongo = (MongoCoreDao) context.getBean("mongoCoreDao");

		ExecutorService executor = Executors.newFixedThreadPool(30); // 创建一个线程池，数量和开启线程的数量一样
		// 创建两个个计数器
		CountDownLatch begin = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(30);
		// 循环创建线程
		for (int i = 1; i < 30; i++) {
			// 线程类
			SendThread mythead = new SendThread(10000 * i, begin, end,mongo);
			// 这里执行线程的方式是调用线程池里的executor.execute(mythead)方法。

			executor.execute(mythead);
		}

		begin.countDown();
		end.await();

		// 执行完关闭线程池
		executor.shutdown();

	}

	public static void main2() {

		Integer skip = 12;
		Integer limit = 18;

		Integer count = 0;
		Integer skipTemp = 0;
		Integer limitTemp = 0;
		Integer surplusLimit = limit;
		Integer surplusSkip = skip;
		for (int i = 0; i < 10; i++) {

			count = 10;
			if (count >= surplusSkip) {
				skipTemp = surplusSkip;
				surplusSkip = surplusSkip - skipTemp;
			} else {
				skipTemp = count;
				surplusSkip = surplusSkip - skipTemp;
			}
			if (count >= skipTemp + surplusLimit) {
				limitTemp = surplusLimit;
				surplusLimit = surplusLimit - limitTemp;
			} else {
				limitTemp = count - skipTemp;
				surplusLimit = surplusLimit - limitTemp;
			}
			System.out.println("skipTemp:" + skipTemp + " limitTemp:" + limitTemp + " surplusSkip:" + surplusSkip
					+ " surplusLimit:" + surplusLimit);
			if (surplusLimit == 0) {
				break;
			}
		}

	}

}
