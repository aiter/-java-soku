package com.youku.search.sort.entity;

import java.util.HashMap;

public class ChannelMap {

	private static HashMap<Integer, String> id2Channel;

	static {
		id2Channel = new HashMap<Integer, String>();

		id2Channel.put(91, "rd");
		id2Channel.put(92, "yc");
		id2Channel.put(96, "dy");
		id2Channel.put(97, "ds");
		id2Channel.put(98, "ty");
		id2Channel.put(95, "yy");
		id2Channel.put(99, "yx");
		id2Channel.put(100, "dm");
		id2Channel.put(104, "qc");
		id2Channel.put(105, "kj");
	}

	public static String getChannelById(int id) {

		String ch = id2Channel.get(id);
		return ch == null ? "" : ch;
	}
}
