package com.youku.soku.netspeed;

import java.util.HashMap;
import java.util.Map;

public class DefaultSpeed {
	private static Map<Integer,Speed> defaultMap = new HashMap<Integer,Speed>();
	private static final Speed defaultSpeed =   Speed.valueOf(4);
	private static Map<Integer,String> domainSpeedMap = new HashMap<Integer,String>();
	
	static {
		defaultMap.put(Site.优酷网.getValue(), Speed.valueOf(9));
		defaultMap.put(Site.搜狐.getValue(), Speed.valueOf(8));
		defaultMap.put(Site.奇艺网.getValue(), Speed.valueOf(7));
		defaultMap.put(Site.土豆网.getValue(), Speed.valueOf(8));
		
		defaultMap.put(Site.新浪网.getValue(), Speed.valueOf(7));
		defaultMap.put(Site.乐视网.getValue(), Speed.valueOf(7));
		
		defaultMap.put(Site.com56网.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.琥珀网.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.第一视频.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.央视网.getValue(), Speed.valueOf(7));
		defaultMap.put(Site.凤凰网.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.激动网.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.酷6.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.天线视频.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.六间房.getValue(), Speed.valueOf(4));
		defaultMap.put(Site.中关村在线.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.CNTV.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.电影网.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.小银幕.getValue(), Speed.valueOf(6));
		
		
		defaultMap.put(Site.江苏卫视.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.浙江卫视.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.安徽卫视.getValue(), Speed.valueOf(5));
		defaultMap.put(Site.芒果网.getValue(), Speed.valueOf(6));
		defaultMap.put(Site.爱拍游戏.getValue(), Speed.valueOf(6));

		
	}
	
	static {
		domainSpeedMap.put(Site.搜狐.getValue(),"http://tv.sohu.com");
		domainSpeedMap.put(Site.新浪网.getValue(),"http://video.sina.com.cn");
	}
	
	
	public static Speed getSpeed(int site){
		Speed speed = defaultMap.get(site);
		if (speed != null){
			return speed;
		}
		else{
			return defaultSpeed;
		}
	}
	
	public static boolean isSpecialSite(int site){
		return domainSpeedMap.containsKey(site);
	}
	public static boolean isMainDomain(int site,String url){
		return url.startsWith(domainSpeedMap.get(site));
	}
	
	public static Map<Integer, Speed> getDefaultMap() {
		return defaultMap;
	}

	public static Speed getDefaultSpeed() {
		return defaultSpeed;
	}
	
}
