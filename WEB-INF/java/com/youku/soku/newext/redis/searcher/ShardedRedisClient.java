package com.youku.soku.newext.redis.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.youku.soku.config.ExtServerConfig;

/**
 * @author tanxiuguang create on Oct 16, 2011
 */
public class ShardedRedisClient {

	private static Logger logger = Logger.getLogger(ShardedRedisClient.class);

	private final static int DEFAULT_PORT = 6379;

	private List<JedisShardInfo> shards;

	private ShardedJedisPool pool;

	private static ShardedRedisClient redisClinet = new ShardedRedisClient();

	private ShardedRedisClient() {
		init();
	}

	private void init() {
		//ExtServerConfig.init("/Users/tanxiuguang/work/youku/search/src/WEB-INF/conf/ext.conf");
		String[] servers = ExtServerConfig.getInstance().getStringArray("REDISSERVERS");
		if (servers == null || servers.length <= 0) {
			logger.error("Error REDISERCERS");
		} else {
			shards = new ArrayList<JedisShardInfo>();
			for (String server : servers) {
				shards.add(new JedisShardInfo(server, DEFAULT_PORT));
			}

			Config config = new Config();
			//config.maxActive = 1;
			//config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
			config.maxActive = 200;
			config.maxIdle = 40;
			pool = new ShardedJedisPool(config, shards);
			
		}
	}

	public static ShardedRedisClient getInstance() {
		return redisClinet;
	}

	public ShardedJedisPool getJedisPool() {
		return pool;
	}
	
}
