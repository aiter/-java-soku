package com.youku.soku.newext.redis.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.youku.soku.config.ExtServerConfig;

/**
 * @author tanxiuguang create on Oct 16, 2011
 */
public class RedisClient {

	private static Logger logger = Logger.getLogger(RedisClient.class);

	private final static int DEFAULT_PORT = 6379;

	private JedisPool pool;

	private static RedisClient redisClinet = new RedisClient();

	private RedisClient() {
		init();
	}

	private void init() {
		// ExtServerConfig.init("/Users/tanxiuguang/work/youku/search/src/WEB-INF/conf/ext.conf");
		String[] servers = ExtServerConfig.getInstance().getStringArray("REDISSERVERS");
		if (servers == null || servers.length <= 0) {
			logger.error("Error REDISERCERS");
		} else {
			JedisPoolConfig config = new JedisPoolConfig();
			// config.maxActive = 1;
			// config.whenExhaustedAction =
			// GenericObjectPool.WHEN_EXHAUSTED_FAIL;
			config.maxActive = 200;
			config.maxIdle = 40;
			config.maxWait = 100;
			// config.setWhenExhaustedAction(whenExhaustedAction);

			int groupNumber = 0;

			try {
				groupNumber = com.youku.search.config.Config.getGroupNumber();
			} catch (Exception e) {

			}

			if (groupNumber == 0) {
				try {
					groupNumber = com.youku.soku.config.Config.getGroupNumber();
				} catch (Exception e) {

				}
			}

			if (servers.length > 1) {
				for (int i = 0; i < servers.length; i++) {
					if (groupNumber == 1) {
						if ((i % 2) == 1) {
							pool = new JedisPool(config, servers[i]);
						}
					} else {
						if ((i % 2) == 0) {
							pool = new JedisPool(config, servers[i]);
						}
					}
				}
			} else {
				pool = new JedisPool(config, servers[0]);
			}

		}
	}

	public static RedisClient getInstance() {
		return redisClinet;
	}

	public JedisPool getJedisPool() {
		return pool;
	}

}
