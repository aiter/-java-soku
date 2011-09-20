package com.youku.search.sort;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCache {

	public enum StoreResult {
		success, fail, bad_cache_time
	};

	public static MemCachedClient getClient() {
		MemCachedClient client = new MemCachedClient();
		client.setSanitizeKeys(false);
		return client;
	}

	public static String processKey(String key) {
		if (key == null || key.trim().length() == 0) {
			throw new IllegalArgumentException("key不能为null、不能为空字符串");
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] bytes = digest.digest(key.getBytes("utf8"));

			StringBuilder builder = new StringBuilder();
			for (byte b : bytes) {
				String s = "0" + Integer.toHexString(b & 0xFF);
				builder.append(s.substring(s.length() - 2));
			}

			return builder.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 总是检查远程的缓存服务器
	 */
	public static Object cacheGet(String key) {
		return cacheGet(key, 1);
	}

	/**
	 * 如果cacheSeconds大于0，才检查远程的缓存服务器
	 */
	public static Object cacheGet(String key, int cacheSeconds) {
		if (cacheSeconds > 0) {
			MemCachedClient client = getClient();
			key = processKey(key);
			Object o = client.get(key);
			return o;
		}

		return null;
	}

	/**
	 * 缓存结果
	 */
	public static StoreResult cacheSet(String key, Object o, int cacheSeconds) {

		if (cacheSeconds <= 0) {
			return StoreResult.bad_cache_time;
		}

		Calendar later = Calendar.getInstance();
		later.add(Calendar.SECOND, cacheSeconds);
		Date laterTime = later.getTime();

		MemCachedClient client = getClient();
		key = processKey(key);

		boolean result = client.set(key, o, laterTime);
		if (result) {
			return StoreResult.success;
		}
		return StoreResult.fail;
	}

	public static StoreResult cacheReplace(String key, Object o,
			int cacheSeconds) {

		if (cacheSeconds <= 0) {
			return StoreResult.bad_cache_time;
		}

		Calendar later = Calendar.getInstance();
		later.add(Calendar.SECOND, cacheSeconds);
		Date laterTime = later.getTime();

		MemCachedClient client = getClient();
		key = processKey(key);

		boolean result = client.replace(key, o, laterTime);
		if (result) {
			return StoreResult.success;
		}
		return StoreResult.fail;
	}

	public static void main(String[] args) {
		if (args.length > 0 && args[0] != null) {
			SockIOPool pool = SockIOPool.getInstance();
			String[] servers = { "10.101.168.105:11211" };
			pool.setServers(servers);
			pool.setInitConn(5);
			pool.setMinConn(1);
			pool.setMaxConn(50);
			pool.setMaxIdle(21600000);
			pool.setMaintSleep(30);
			pool.setNagle(false);
			pool.setSocketTO(2000);
			pool.setSocketConnectTO(0);
			pool.initialize();

			Object object = cacheGet(args[0]);
			System.out.println("key:" + args[0] + " object:" + object);
		}
	}
}
