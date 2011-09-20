package com.youku.soku.newext.util;

import java.util.HashMap;
import java.util.Map;

public enum ProgrammeSiteType {
	土豆网(1),
	com56(2),
	新浪网(3),
	琥珀网(4),
	第一视频(5),
	搜狐(6),
	央视网(7),
	凤凰网(8),
	激动网(9),
	酷6(10),
	天线视频(11),
	六间房(12),
	中关村在线(13),
	优酷网(14),
	CNTV(15),
	电影网(16),
	乐视网(17),
	小银幕(18),
	奇艺网(19),
	江苏卫视(20),
	浙江卫视(21),
	安徽卫视(23),
	芒果网(24),
	爱拍游戏(25),
	综合(100);
	
	
	public static  Map<Integer,String> siteMap=new HashMap<Integer,String>();
	static{
		for(ProgrammeSiteType eleSite:ProgrammeSiteType.values()){
			siteMap.put(eleSite.value, eleSite.name());
		}
	}
	
	
	private int value;
	private ProgrammeSiteType(int value) {
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}

}
