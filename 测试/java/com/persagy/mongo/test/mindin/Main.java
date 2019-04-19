package com.persagy.mongo.test.mindin;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;

public class Main {

	private static Logger log = Logger.getLogger(Main.class);
	private static String buildingId = "4101020001";

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

		

		for (int i = 0; i < 1000; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			MGElectricCurrentData query = new MGElectricCurrentData();
			query.setBuildingForContainer(buildingId);
			query.setSign("1001");
			query.setFuncid(10301);
			query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$gte, sdf.parse("2019-01-21 00:00:00"));
			query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$lt, sdf.parse("2019-01-22 00:00:00"));
			query.setSort("receivetime", MongoOrder.Asc);
			long startTime = System.currentTimeMillis();
			MongoCoreDao mongo = (MongoCoreDao) context.getBean("mongoCoreDao");
			List<MGElectricCurrentData> dataList = mongo.query(query);
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			if (time < 1000) {
				System.out.println(i + "   " + time + "  @#  " + dataList.size());
			} else {
				System.err.println(i + "   " + time + "  @#  " + dataList.size());
			}

			// Thread.sleep(1);
		}

	}

}
