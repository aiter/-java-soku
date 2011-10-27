package com.youku.soku.newext.util;
/**
 * @author tanxiuguang
 * create on Oct 5, 2011
 */
public class RedisCacheUtil {
	
	public static String getStoreKey(String name, String flag) {
		return name + "_" + flag;
	}

}
