package com.youku.soku.newext.redis.benchmark;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.redis.util.SerializeUtil;

public class Benchmark {

	private Jedis jedis;

	private static final int REPEATTIEM = 10000;

	public Benchmark() {
		jedis = new Jedis("10.103.8.53");
	}

	private void testSaveObject() {
		// Programme p = ProgrammePeer.retrieveByPK(96094);
		Programme p = new Programme();
		SerializeUtil<Programme> programmeSearialize = new SerializeUtil<Programme>();

		long start = System.currentTimeMillis();
		for (int i = 0; i < REPEATTIEM; i++) {
			p.setId(i);
			p.setName(i + "programme");
			byte[] bytes = programmeSearialize.objectToBytes(p);
			jedis.set(("object" + i).getBytes(), bytes);
		}

		long end = System.currentTimeMillis();
		System.out.println("Save Object redis cost: " + (end - start));
	}

	private void testSaveJSONObject() {
		Programme p = new Programme();

		long start = System.currentTimeMillis();
		for (int i = 0; i < REPEATTIEM; i++) {
			p.setId(i);
			p.setName(i + "programme");
			JSONObject jsonObj = new JSONObject(p);
			jedis.set("json" + i, jsonObj.toString());
		}

		long end = System.currentTimeMillis();
		System.out.println("Save JSON redis cost: " + (end - start));
	}

	private void testSavePlainText() {
		Programme p = new Programme();
		p.setId(1000);
		p.setName("测试");
		JSONObject jsonObj = new JSONObject(p);
		String value = jsonObj.toString();
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPEATTIEM; i++) {
			jedis.set("plaintext" + i, value + "i");
		}

		long end = System.currentTimeMillis();
		System.out.println("Save plain text redis cost: " + (end - start));
	}

	private void testMulitGet() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPEATTIEM; i++) {
			String[] keys = new String[30];
			for (int keysIdx = 0; keysIdx < 30; keysIdx++) {
				keys[keysIdx] = "plaintext" + (keysIdx + i);
			}

			jedis.mget(keys);
		}

		long end = System.currentTimeMillis();

		System.out.println("mulity get cost: " + (end - start));
	}

	private void testNormalGet() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < REPEATTIEM; i++) {
			jedis.get("plaintext" + i);
		}

		long end = System.currentTimeMillis();
		System.out.println("normal get cost: " + (end - start));
	}

	public static void main(String[] args) {
		Benchmark test = new Benchmark();
		test.testSaveJSONObject();
		test.testSaveObject();
		test.testSavePlainText();

		test.testMulitGet();
		test.testNormalGet();
		
		

		/**
		 * Save JSON redis cost: 1787 
		 * Save Object redis cost: 891 
		 * Save plaintext redis cost: 476
		 * mulit get cost: 2073 
		 * normal get cost: 433
		 */
	}
}
