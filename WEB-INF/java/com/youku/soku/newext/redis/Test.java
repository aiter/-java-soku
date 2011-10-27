package com.youku.soku.newext.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.json.JSONObject;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author tanxiuguang create on Oct 5, 2011
 */
public class Test {

	public static void main(String[] args) throws Exception {
		
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo("10.103.8.9", 6379));
		shards.add(new JedisShardInfo("10.103.8.53", 6379));
		ShardedJedisPool pool = new ShardedJedisPool(new Config(), shards);
		
		ShardedJedis jedis = pool.getResource();
		//System.out.println(new JSONObject(jedis.get("蜗居_TELEPLAY")).toString(4));
		
		for(int i = 0; i < 10000; i++) {
			jedis = pool.getResource();
			jedis.get("蜗居_TELEPLAY");
			
			
			System.out.println(i);
			pool.returnResource(jedis);
		}
	}

}
