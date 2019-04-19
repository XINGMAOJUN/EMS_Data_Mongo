package com.persagy.mongo.test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.persagy.mongo.core.dao.MongoCoreDao;
import com.persagy.mongo.core.service.MongoCoreService;
import com.persagy.mongo.test.mindin.MGElectricCurrentData;

public class SendThread implements Runnable {

	private static Logger log = Logger.getLogger(SendThread.class);

	private static String buildingId = "4101020001";

	private MongoCoreDao mongo;

	private Integer funcId;

	private CountDownLatch begin;
	private CountDownLatch end;

	// 创建个构造函数初始化 list,和其他用到的参数
	public SendThread(Integer funcId, CountDownLatch begin, CountDownLatch end, MongoCoreDao mongo) {
		this.funcId = funcId;
		this.begin = begin;
		this.end = end;
		this.mongo = mongo;
	}

	@Override
	public void run() {
		try {
			long count = 0;
			for (int sign = 30000; sign < 32000; sign++) {
				for (int funcid = this.funcId; funcid < funcId + 1000; funcid++) {
					for (int data = 0; data < 100; data++) {
						MGElectricCurrentData electricCurrentData = new MGElectricCurrentData();
						electricCurrentData.setBuildingForContainer(buildingId);
						electricCurrentData.setSign(String.valueOf(sign));
						electricCurrentData.setFuncid(funcid);
						electricCurrentData.setData(data * 1.0);
						long time = System.currentTimeMillis();
						electricCurrentData.setReceivetime(new Date(time + data));

						this.mongo.save(electricCurrentData);
						log.info(" # " + (count++) + "#" + sign + "#" + funcid + "#" + time);

						//Thread.sleep(1);

					}
				}
			}

			// 执行完让线程直接进入等待
			begin.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			// 这里要主要了，当一个线程执行完 了计数要减一不然这个线程会被一直挂起
			// ，end.countDown()，这个方法就是直接把计数器减一的
			end.countDown();
		}

	}

}
