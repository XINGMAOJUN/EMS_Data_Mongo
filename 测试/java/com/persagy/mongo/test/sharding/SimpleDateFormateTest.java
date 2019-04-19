package com.persagy.mongo.test.sharding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import freemarker.core.ParseException;

public class SimpleDateFormateTest extends Thread {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private String name;
	private String dateStr;

	public SimpleDateFormateTest(String name, String dateStr) {
		this.name = name;
		this.dateStr = dateStr;
	}

	@Override
	public void run() {

		try {
			Date date = sdf.parse(dateStr);
			System.out.println(name + ": date:" + date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(3);

		executorService.execute(new SimpleDateFormateTest("A", "2018-12-31"));
		executorService.execute(new SimpleDateFormateTest("B", "2018-01-01"));
		executorService.shutdown();
	}
}
