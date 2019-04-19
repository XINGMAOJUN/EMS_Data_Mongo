package com.persagy.mongo.test.mindin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.core.enumeration.MongoOrder;
import com.persagy.mongo.core.enumeration.MongoSpecialOperator;

@Component("DBComponent")
public class DBComponent {

	@Resource(name = "mongoCoreDao")
	protected MongoCoreDao mongoCoreDao;

	private static String buildingId = "4101020001";

	public List<MGElectricCurrentData> findStatData() throws Exception {

		List<String> meterList = new ArrayList<>();
		for (int i = 1001; i < 1100; i++) {
			meterList.add(String.valueOf(i));
		}
		Random rand = new Random();
		List<Integer> funcIdList = new ArrayList<>();
		funcIdList.add(10201);
		funcIdList.add(10202);
		funcIdList.add(10203);
		funcIdList.add(10207);
		funcIdList.add(10208);
		funcIdList.add(10209);
		funcIdList.add(10301);
		funcIdList.add(10302);
		funcIdList.add(10303);
		
		MGElectricCurrentData query = new MGElectricCurrentData();
		query.setBuildingForContainer("4101020001");
		query.setSign(meterList.get(rand.nextInt(99)));
		query.setFuncid(funcIdList.get(rand.nextInt(8)));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$gte, sdf.parse("2019-01-21 00:00:00"));
		query.setMongoSpecialOperation("receivetime", MongoSpecialOperator.$lt, sdf.parse("2019-01-22 00:00:00"));
		query.setSort("receivetime", MongoOrder.Asc);

		return mongoCoreDao.query(query);
	}

}
