package com.youku.soku.manage.util;

import java.util.ArrayList;
import java.util.List;

public class LanguageAndArea {
	
	private static List<String> languageList;
	
	private static List<String> areaList;
	
	private static List<String> allAreaList;
	
	private static List<String> cateList;
	
	private static List<String> teleplayCateList;
	
	private static List<String> animeCateList;
	
	private static List<String> movieCateList;
	
	private static List<String> varietyCateList;

	public static final String AREA_OTHRES = "其他";
	
	public static final String AREA_ALL = "所有";
		
	public static List<String> getLanguageList() {
		if(languageList == null) {
			languageList = new ArrayList<String>();
			languageList.add("国语");
			languageList.add("英语");
			languageList.add("粤语");
			languageList.add("韩语");
			languageList.add("法语");
			languageList.add("日语");
			languageList.add("德语");
			languageList.add("西班牙语");
			languageList.add("俄语");
			languageList.add("希腊语");
			languageList.add("越南语");
			languageList.add("葡萄牙语");
			languageList.add("其他");
		}
		return languageList;
	}
	
	public static String getAreaString(String newArea, String oldArea) {
		if(oldArea != null && !areaList.contains(oldArea)) {
			if(!AREA_OTHRES.equals(newArea)) {
				return newArea;
			} else {
				return oldArea;
			}
		} else {
			return newArea;
		}
	
	}
	

	public static List<String> getAreaList() {
		if(areaList == null) {
			areaList = new ArrayList<String>();
			areaList.add("其他");
			areaList.add("大陆");
			areaList.add("香港");
			areaList.add("台湾");
			areaList.add("日本");
			areaList.add("韩国");
			areaList.add("美国");
			areaList.add("英国");
			areaList.add("法国");
			areaList.add("德国");
			areaList.add("泰国");
			areaList.add("印度");
			areaList.add("意大利");
			areaList.add("西班牙");
			areaList.add("加拿大");
			
			
		}
		return areaList;
	}
	
	public static List<String> getSelectAreaList() {
		if(allAreaList == null) {
			allAreaList = new ArrayList<String>();
			allAreaList.add("所有");
			allAreaList.add("其他");
			allAreaList.add("大陆");
			allAreaList.add("香港");
			allAreaList.add("台湾");
			allAreaList.add("日本");
			allAreaList.add("韩国");
			allAreaList.add("美国");
			allAreaList.add("英国");
			allAreaList.add("法国");
			allAreaList.add("德国");
			allAreaList.add("泰国");
			allAreaList.add("印度");
			allAreaList.add("意大利");
			allAreaList.add("西班牙");
			allAreaList.add("加拿大");
			
			
		}
		return allAreaList;
	}
	
	public static List<String> getCateList() {
		if(cateList == null) {
			cateList = new ArrayList<String>();
			cateList.add("爱情");
			cateList.add("科幻");
			cateList.add("动作");
			cateList.add("冒险");
			cateList.add("科幻");
		}
		return cateList;
	}
	
	
	public static List<String> getTeleplayCateList() {
		if(teleplayCateList == null) {
			teleplayCateList = new ArrayList<String>();
			teleplayCateList.add("时装");
			teleplayCateList.add("武侠");
			teleplayCateList.add("历史");
			teleplayCateList.add("偶像");
			teleplayCateList.add("言情");
			teleplayCateList.add("警匪");
			teleplayCateList.add("都市");
			teleplayCateList.add("农村");
			teleplayCateList.add("儿童");
			teleplayCateList.add("古装");
			teleplayCateList.add("科幻");
			teleplayCateList.add("军事");
			teleplayCateList.add("搞笑");
			teleplayCateList.add("家庭");
			teleplayCateList.add("神话");
			teleplayCateList.add("鬼怪");
			teleplayCateList.add("情景剧");
			teleplayCateList.add("励志");
			teleplayCateList.add("悬疑");
			teleplayCateList.add("古装");
			teleplayCateList.add("伦理");
			teleplayCateList.add("爱情");
			teleplayCateList.add("名作改编");
			teleplayCateList.add("动作");
			teleplayCateList.add("纪录片");
			teleplayCateList.add("侦缉");
			
		}
		return teleplayCateList;
	}

	public static List<String> getAnimeCateList() {
		if(animeCateList == null) {
			animeCateList = new ArrayList<String>();
			animeCateList.add("励志");
			animeCateList.add("机战");
			animeCateList.add("运动");
			animeCateList.add("美少女");
			animeCateList.add("热血");
			animeCateList.add("运动");
			animeCateList.add("武侠格斗");
			animeCateList.add("浪漫爱情");
			animeCateList.add("侦探推理");
			animeCateList.add("魔幻冒险");
			animeCateList.add("体育竞技");
			animeCateList.add("战争灾难");
			animeCateList.add("魔法格斗");
			animeCateList.add("激动战斗");
			animeCateList.add("温馨恋爱");
			animeCateList.add("警匪");
			animeCateList.add("搞笑");
			animeCateList.add("真人");
			animeCateList.add("战争");
			animeCateList.add("神魔");
			animeCateList.add("悬疑");
			
		}
		return animeCateList;
	}

	public static List<String> getMovieCateList() {
		if(movieCateList == null) {
			movieCateList = new ArrayList<String>();
			movieCateList.add("剧情");
			movieCateList.add("喜剧");
			movieCateList.add("爱情");
			movieCateList.add("动作");
			movieCateList.add("惊悚");
			movieCateList.add("犯罪");
			movieCateList.add("恐怖");
			movieCateList.add("冒险");
			movieCateList.add("纪录片");
			movieCateList.add("悬疑");
			movieCateList.add("动画");
			movieCateList.add("科幻");
			movieCateList.add("奇幻");
			movieCateList.add("战争");
			movieCateList.add("歌舞");
			movieCateList.add("西部");
			movieCateList.add("传记");
			movieCateList.add("历史");
			movieCateList.add("武侠");
			movieCateList.add("戏曲");
			movieCateList.add("儿童");
		}
		return movieCateList;
	}

	public static List<String> getVarietyCateList() {
		if(varietyCateList == null) {
			varietyCateList = new ArrayList<String>();
			
			varietyCateList.add("选秀");
			varietyCateList.add("真人秀");
			varietyCateList.add("晚会");
			varietyCateList.add("益智");
			varietyCateList.add("游戏");
			varietyCateList.add("旅游");
			varietyCateList.add("美食");
			varietyCateList.add("音乐");
			varietyCateList.add("舞蹈");
			varietyCateList.add("体育");
			varietyCateList.add("时尚");
			varietyCateList.add("搞笑");
			varietyCateList.add("脱口秀");
			varietyCateList.add("访谈");
			varietyCateList.add("综合");
			varietyCateList.add("演唱会");
			varietyCateList.add("游艺");
			varietyCateList.add("典礼");
		}
		return varietyCateList;
	}


}
