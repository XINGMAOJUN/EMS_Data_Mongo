package com.persagy.mongo.core.init;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.persagy.mongo.core.constant.SystemConstant;

public class MongoDatabaseFactoryBean extends AbstractFactoryBean<MongoDatabase> {

	// 表示服务器列表(主从复制或者分片)的字符串数组
	private String serverAddress;
	// 数据库名
	private String dbname;
	// 用户名，可为空
	private String username;
	// 密码，可为空
	private String password;
	// 是否确认索引存在，默认false
	private boolean toEnsureIndexes = false;
	// 指定要映射的包名

	private String mapPackage;

	@SuppressWarnings("resource")
	@Override
	protected MongoDatabase createInstance() throws Exception {
		SystemConstant.toEnsureIndexes = this.toEnsureIndexes;
		SystemConstant.mapPackage = this.mapPackage;

		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		// 与数据最大连接数50
		build.connectionsPerHost(50);
		// 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
		build.threadsAllowedToBlockForConnectionMultiplier(50);
		// 与数据库建立连接的timeout设置为1分钟
		build.connectTimeout(1 * 60 * 1000);
		/*
		 * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
		 * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
		 * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
		 */
		build.maxWaitTime(2 * 60 * 1000);
		build.writeConcern(WriteConcern.JOURNALED);

		MongoClientOptions options = build.build();

		MongoCredential credential = MongoCredential.createScramSha1Credential(username, dbname,
				password.toCharArray());
		List<MongoCredential> listm = new ArrayList<MongoCredential>();
		listm.add(credential);
		MongoClient client = new MongoClient(this.getServerAddress(), listm, options);
		MongoDatabase db = client.getDatabase(dbname);

		MongoCursor<String> iterator = db.listCollectionNames().iterator();
		while (iterator.hasNext()) {
			//初始化所有集合
			SystemConstant.collectionSet.add(iterator.next());
		}

		return db;
	}

	@Override
	public Class<?> getObjectType() {
		return MongoDatabase.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

	}

	private ServerAddress getServerAddress() throws Exception {
		try {
			String[] temp = serverAddress.split(":");
			String host = temp[0];
			if (temp.length > 2) {
				throw new IllegalArgumentException("Invalid server address string: " + serverAddress);
			}
			if (temp.length == 2) {
				return new ServerAddress(host, Integer.parseInt(temp[1]));
			} else {
				return new ServerAddress(host);
			}
		} catch (Exception e) {
			throw new Exception("Error while converting serverString to ServerAddressList", e);
		}
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setToEnsureIndexes(boolean toEnsureIndexes) {
		this.toEnsureIndexes = toEnsureIndexes;

	}

	public void setMapPackage(String mapPackage) throws Exception {
		this.mapPackage = mapPackage;
	}

}
