package com.youku.top.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class TopVideoArea {
	public static Map<String,Integer> movieAreaMap=new HashMap<String,Integer>();
	public static Map<String,Integer> teleplayAreaMap=new HashMap<String,Integer>();
	
	public static List<MovieArea> movieAreaList=new ArrayList<MovieArea>();
	public static List<TeleplayArea> teleplayAreaList=new ArrayList<TeleplayArea>();
	
	//在map中没有对应的分类都归为“其它”
	public static Map<String,String> midd2movie=new HashMap<String,String>();
	public static Map<String,String> midd2teleplay=new HashMap<String,String>();
	
	static{
		for(MovieArea movieArea: MovieArea.values()){
			movieAreaMap.put(StringUtils.trimToEmpty(movieArea.name()), movieArea.getValue());
			
		}
		
		for(TeleplayArea teleplayArea: TeleplayArea.values()){
			teleplayAreaMap.put(StringUtils.trimToEmpty(teleplayArea.name()), teleplayArea.getValue());
			
		}
		
		movieAreaList=Arrays.asList(MovieArea.values());
		teleplayAreaList=Arrays.asList(TeleplayArea.values());
		
		
		
		midd2movie.put("大陆", "内地");
		midd2movie.put("美国", "美国");
		midd2movie.put("印度", "印度");
		midd2movie.put("香港", "香港");
		midd2movie.put("英国", "英国");
		midd2movie.put("台湾", "台湾");
		midd2movie.put("韩国", "韩国");
		midd2movie.put("日本", "日本");
		
		midd2teleplay.put("大陆","内地");
		midd2teleplay.put("香港","港台");
		midd2teleplay.put("台湾","港台");
		midd2teleplay.put("韩国","韩国");
		midd2teleplay.put("日本","日本");
		
	}
	

/**
 * 10001 : 前一位代表频道， 后三位代表分类
 * @author User
 *
 */	
public enum MovieArea{
		
		内地(2001),
		美国(2002),
		印度(2003),
		香港(2004),
		英国(2005),
		台湾(2006),
		韩国(2007),
		日本(2008),
		其它(2111);
		
		private int value;
		MovieArea( int arg1) {
			value=arg1;
		}
		public int getValue() {
			return value;
		}
		
}
	
public enum TeleplayArea{
	内地(1001),
	港台(1002),
	韩国(1003),
	日本(1004),
	其它(1111);
	
	private int value;
	TeleplayArea( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
}
public enum AnimeArea{
	其它(4111);
	
	private int value;
	AnimeArea( int arg1) {
		value=arg1;
	}
	public int getValue() {
		return value;
	} 
}

}
