package com.youku.top.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class TopVideoType {
	public static Map<String,Integer> movieTypeMap=new HashMap<String,Integer>();
	public static Map<String,Integer> teleplayTypeMap=new HashMap<String,Integer>();
	
	//在map中没有对应的分类都归为“其它”
	public static Map<String,String> midd2movie=new HashMap<String,String>();
	public static Map<String,String> midd2teleplay=new HashMap<String,String>();
	
	static{
		for(MovieType movieType: MovieType.values()){
			movieTypeMap.put(StringUtils.trimToEmpty(movieType.name()), movieType.getValue());
			
		}
		
		for(TeleplayType teleplayType: TeleplayType.values()){
			teleplayTypeMap.put(StringUtils.trimToEmpty(teleplayType.name()), teleplayType.getValue());
			
		}
		
		midd2movie.put("喜剧", "喜剧");
		midd2movie.put("动作", "动作");
		midd2movie.put("武侠", "动作");
		midd2movie.put("爱情", "爱情");
		midd2movie.put("惊悚", "恐怖");
		midd2movie.put("恐怖", "恐怖");
		midd2movie.put("犯罪", "犯罪");
		midd2movie.put("悬疑", "犯罪");
		midd2movie.put("冒险", "冒险");
		midd2movie.put("科幻", "科幻");
		midd2movie.put("奇幻", "科幻");
		midd2movie.put("战争", "战争");
		midd2movie.put("动画", "动画");
		midd2movie.put("历史", "历史");
		midd2movie.put("剧情", "剧情");
		
		midd2teleplay.put("偶像","偶像剧");
		midd2teleplay.put("言情","言情剧");
		midd2teleplay.put("都市","都市剧");
		midd2teleplay.put("家庭","都市剧");
		midd2teleplay.put("搞笑","喜剧");
		midd2teleplay.put("历史","历史剧");
		midd2teleplay.put("军事","谍战剧");
		midd2teleplay.put("古装","古装剧");
		midd2teleplay.put("武侠","武侠剧");
		midd2teleplay.put("警匪","刑侦剧");
		
		
	}
	

/**
 * 10001 : 前一位代表频道， 后三位代表分类
 * @author User
 *
 */	
public enum MovieType{
		
		喜剧(2001),
		动作(2002),
		爱情(2003),
		恐怖(2004),
		犯罪(2005),
		冒险(2006),
		科幻(2007),
		战争(2008),
		动画(2009),
		历史(2010),
		剧情(2011),
		其它(2111);
		
		private int value;
		MovieType( int arg1) {
			value=arg1;
		}
		public int getValue() {
			return value;
		} 
		
		
}
	
public enum TeleplayType{
	偶像剧(1001),
	言情剧(1002),
	都市剧(1003),
	喜剧(1004),
	历史剧(1005),
	谍战剧(1006),
	古装剧(1007),
	武侠剧(1008),
	刑侦剧(1009),
	其它(1111);
	
	private int value;
	TeleplayType( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
}

public enum AnimeType{
	其它(4111);
	
	private int value;
	AnimeType( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
}
}
