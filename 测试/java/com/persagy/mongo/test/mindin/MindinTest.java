package com.persagy.mongo.test.mindin;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;
import com.persagy.mongo.core.service.MongoCoreService;
import com.persagy.mongo.test.base.BaseTest;

public class MindinTest extends BaseTest {

	private static Logger log = Logger.getLogger(MindinTest.class);

	@Resource(name = "mongoCoreService")
	private MongoCoreService coreService;
	
	@Resource(name = "DBComponent")
	private DBComponent DBComponent;

	private static String buildingId = "4101020001";

	@Test
	public void query() throws Exception {
		for (int i = 0; i < 1000; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			MGElectricCurrentData query = new MGElectricCurrentData();
//			query.setBuildingForContainer(buildingId);
//			query.setSign("1001");
//			query.setFuncid(10301);
//			query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$gte, sdf.parse("2019-01-18 00:00:00"));
//			query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$lt, sdf.parse("2019-01-19 00:00:00"));
//			query.setSort("receivetime", MongoOrder.Asc);
			long startTime = System.currentTimeMillis();
		//	List<MGElectricCurrentData> dataList = coreService.query(query);
			List<MGElectricCurrentData> dataList =  DBComponent.findStatData();
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
