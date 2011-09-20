package com.youku.soku.netspeed;

public enum Site {
	土豆网(1),
	com56网(2),
	新浪网(3),
	琥珀网(4),
	第一视频(5), //无
	搜狐(6),
	央视网(7),
	凤凰网(8),
	激动网(9),
	酷6(10),
	天线视频(11), //wu
	六间房(12),
	中关村在线(13),
	优酷网(14),
	CNTV(15),
	电影网(16),
	乐视网(17),
	小银幕(18), //无
	奇艺网(19),
	江苏卫视(20), 
	浙江卫视(21), //无
	安徽卫视(23), //无
	芒果网(24), 
	爱拍游戏(25); //无
	
	private int value;
	
	Site(int v){
		this.value = v;
	}
	
	public int getValue(){
		return value;
	}
	
}
