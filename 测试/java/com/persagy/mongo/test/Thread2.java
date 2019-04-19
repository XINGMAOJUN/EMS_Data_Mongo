package com.persagy.mongo.test;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.persagy.mongo.core.service.MongoCoreService;
import com.persagy.mongo.test.mindin.MGElectricCurrentData;

@Component("Thread2")
public class Thread2 implements Runnable {

	private static Logger log = Logger.getLogger(Thread2.class);

	private static String buildingId = "4101020001";

	@Resource(name = "mongoCoreService")
	private MongoCoreService coreService;

	@PostConstruct
	public void init() {
	//	new Thread(this).start();
	}

	@Override
	public void run() {

		long count = 0;
		for (int sign = 30000; sign < 32000; sign++) {
			for (int funcid = 70000; funcid < 71000; funcid++) {
				for (int data = 0; data < 100; data++) {
					MGElectricCurrentData electricCurrentData = new MGElectricCurrentData();
					electricCurrentData.setBuildingForContainer(buildingId);
					electricCurrentData.setSign(String.valueOf(sign));
					electricCurrentData.setFuncid(funcid);
					electricCurrentData.setData(data * 1.0);
					long time = System.currentTimeMillis();
					electricCurrentData.setReceivetime(new Date(time));
					try {
						coreService.save(electricCurrentData);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info(" # " + (count++) + "#" + sign + "#" + funcid + "#" + time);
				}
			}
		}
	}

}
