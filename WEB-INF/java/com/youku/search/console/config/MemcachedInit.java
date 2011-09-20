package com.youku.search.console.config;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.danga.MemCached.SockIOPool;

public class MemcachedInit {
	private static MemcachedInit self = null;
	private static Properties prop = new Properties();

	public synchronized static MemcachedInit getInstance() {
		if (self == null) {
			self = new MemcachedInit();
		}
		return self;
	}

	public void init(String file) {

		try {
			Configuration p = new PropertiesConfiguration(Constants.ROOT
					+ Constants.CONFIGHOME + file);
			String[] server = p.getStringArray("server");
			int initconn = p.getInt("initconn");
			int minconn = p.getInt("minconn");
			int maxconn = p.getInt("maxconn");
			int maxidle = p.getInt("maxidle");
			int maintsleep = p.getInt("maintsleep");
			boolean nagle = p.getBoolean("nagle");

			int socketreadtimeout = p.getInt("socketreadtimeout");
			int socketconntimeout = p.getInt("socketconntimeout");
			boolean compress = p.getBoolean("compress");
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(server);
			pool.setInitConn(initconn);
			pool.setMinConn(minconn);
			pool.setMaxConn(maxconn);
			pool.setMaxIdle(maxidle);
			pool.setMaintSleep(maintsleep);
			pool.setNagle(nagle);
			pool.setSocketTO(socketreadtimeout);
			pool.setSocketConnectTO(socketconntimeout);
			pool.initialize();
			// MemCachedClient mcc = new MemCachedClient();
			// mcc.setCompressEnable(compress);

		} catch (Exception e) {
			System.err.println(e + "file " + file + " not found");
			e.printStackTrace();
		} finally {

		}

	}
}
