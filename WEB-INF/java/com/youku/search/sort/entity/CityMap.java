package com.youku.search.sort.entity;

import java.util.HashMap;

public class CityMap {
	private static HashMap<Integer, String> id2City;

	static {
		id2City = new HashMap<Integer, String>();

		id2City.put(0, "安徽省");
		id2City.put(1, "北京市");
		id2City.put(2, "重庆市");
		id2City.put(3, "福建省");
		id2City.put(4, "甘肃省");
		id2City.put(5, "广东省");
		id2City.put(6, "广西");
		id2City.put(7, "贵州省");
		id2City.put(8, "海南省");
		id2City.put(9, "河北省");
		id2City.put(10, "河南省");
		id2City.put(11, "黑龙江省");
		id2City.put(12, "湖北省");
		id2City.put(13, "湖南省");
		id2City.put(14, "吉林省");
		id2City.put(15, "江苏省");
		id2City.put(16, "江西省");
		id2City.put(17, "辽宁省");
		id2City.put(18, "内蒙古");
		id2City.put(19, "宁夏");
		id2City.put(20, "青海省");
		id2City.put(21, "山东省");
		id2City.put(22, "山西省");
		id2City.put(23, "陕西省");
		id2City.put(24, "上海市");
		id2City.put(25, "四川省");
		id2City.put(26, "天津市");
		id2City.put(27, "西藏");
		id2City.put(28, "新疆");
		id2City.put(29, "云南省");
		id2City.put(30, "浙江省");
		id2City.put(31, "香港特别行政区");
		id2City.put(32, "澳门特别行政区");
		id2City.put(33, "台湾省");
		id2City.put(34, "海外");
		id2City.put(35, "保密");
	}

	public static String getCityById(int id) {

		String city = id2City.get(id);
		return city == null ? "" : city;
	}

	public static void main(String[] args) {
		String city = CityMap.getCityById(10);
		System.out.println("city: " + city);
	}
}
