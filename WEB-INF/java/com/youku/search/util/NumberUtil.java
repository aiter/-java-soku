package com.youku.search.util;

import java.util.Random;

public class NumberUtil {
	
	private static final Random r = new Random(System.currentTimeMillis());

	/**
	 * 
	 * @param min
	 *            大于min
	 * @param max
	 *            小于max
	 * @return
	 */
	public static int getRandomInt(int min, int max) {
		return r.nextInt(max) % (max - min - 1) + min + 1;
	}
}
