package com.youku.soku.manage.questionnaire;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnswerMap {

	public static Map<String, String> getProblem1Answer() {
		Map<String, String> problem1Answer = new HashMap<String, String>();
		problem1Answer.put("1", "每天");
		problem1Answer.put("2", "每周");
		problem1Answer.put("3", "偶尔");
		problem1Answer.put("4", "几乎不用");

		Collections.unmodifiableMap(problem1Answer);
		return problem1Answer;
	}

	public static Map<String, String> getProblem2Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "搜库");
		problemAnswer.put("2", "百度");
		problemAnswer.put("3", "优酷");
		problemAnswer.put("3", "Google");
		problemAnswer.put("4", "奇艺");
		problemAnswer.put("5", "搜狗");
		problemAnswer.put("6", "搜狐视频");
		problemAnswer.put("7", "搜搜");
		problemAnswer.put("8", "土豆");

		
		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}

	public static Map<String, String> getProblem3Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "片名");
		problemAnswer.put("2", "片名");
		problemAnswer.put("3", "表示类别的词");

		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}

	public static Map<String, String> getProblem4Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "快速浏览搜索结果");
		problemAnswer.put("2", "先利用页面的筛选工具对结果进行筛选");

		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}

	public static Map<String, String> getProblem5Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "视频时长");
		problemAnswer.put("2", "视频来源网站");
		problemAnswer.put("3", "发布者");
		problemAnswer.put("4", "画质");
		problemAnswer.put("5", "播放数");
		problemAnswer.put("6", "网络速度");
		problemAnswer.put("7", "截图");
		problemAnswer.put("8", "发布时间");
		problemAnswer.put("9", "分段观看");


		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}

	public static Map<String, String> getProblem6Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "电视剧");
		problemAnswer.put("2", "电影");
		problemAnswer.put("3", "综艺");
		problemAnswer.put("4", "动漫");
		problemAnswer.put("5", "体育");
		problemAnswer.put("6", "原创");
		problemAnswer.put("7", "科技");
		problemAnswer.put("8", "汽车");
		problemAnswer.put("9", "音乐");
		problemAnswer.put("10", "教育");
		problemAnswer.put("11", "游戏");
		
		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}
	
	public static Map<String, String> getProblem7Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "留意到了");
		problemAnswer.put("2", "留意到了");
		problemAnswer.put("3", "没有留意");
		
		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}
	
	public static Map<String, String> getProblem8Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "公司");
		problemAnswer.put("2", "学校");
		problemAnswer.put("3", "家");
		problemAnswer.put("4", "网吧");
		problemAnswer.put("5", "公共场所");

		
		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}
	
	
	public static Map<String, String> getProblem9Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "门户网站");
		problemAnswer.put("2", "社交网站");
		problemAnswer.put("3", "即时聊天工具");
		problemAnswer.put("4", "论坛");
		problemAnswer.put("5", "订阅信息");
		problemAnswer.put("6", "邮件");
		problemAnswer.put("7", "博客和微博客");
		problemAnswer.put("8", "搜索");
		problemAnswer.put("9", "专业网站");
		
		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}
	
	public static Map<String, String> getProblem10Answer() {
		Map<String, String> problemAnswer = new HashMap<String, String>();
		problemAnswer.put("1", "网络新闻");
		problemAnswer.put("2", "网络游戏");
		problemAnswer.put("3", "网络购物");
		problemAnswer.put("4", "网络音乐");
		problemAnswer.put("5", "网络视频");
		problemAnswer.put("6", "论坛");
		problemAnswer.put("7", "搜索引擎");
		problemAnswer.put("8", "电子邮件");
		problemAnswer.put("9", "社交网站");
		problemAnswer.put("10", "即时通信");
		problemAnswer.put("11", "博客和微博");
		problemAnswer.put("12", "网络炒股");

		Collections.unmodifiableMap(problemAnswer);
		return problemAnswer;
	}
	
	public static Map<String, String> getUserArea() {
		Map<String, String> userArea = new HashMap<String, String>();
		userArea.put("1", "北京");
		userArea.put("2", "上海");
		userArea.put("3", "广东");
		userArea.put("4", "黑龙江");
		userArea.put("5", "吉林");
		userArea.put("6", "辽宁");
		userArea.put("7", "天津");
		userArea.put("8", "安徽");
		userArea.put("9", "江苏");
		userArea.put("10", "浙江");
		userArea.put("11", "陕西");
		userArea.put("12", "湖北");
		userArea.put("13", "湖南");
		userArea.put("14", "甘肃");
		userArea.put("15", "四川");
		userArea.put("16", "山东");
		userArea.put("17", "福建");
		userArea.put("18", "河南");
		userArea.put("19", "重庆");
		userArea.put("20", "云南");
		userArea.put("21", "河北");
		userArea.put("22", "江西");
		userArea.put("23", "山西");
		userArea.put("24", "贵州");
		userArea.put("25", "广西");
		userArea.put("26", "内蒙古");
		userArea.put("27", "宁夏");
		userArea.put("28", "青海");
		userArea.put("29", "新疆");
		userArea.put("30", "海南");
		userArea.put("31", "西藏");
		userArea.put("32", "香港");
		userArea.put("33", "澳门");
		userArea.put("34", "台湾");


		Collections.unmodifiableMap(userArea);
		return userArea;
	}
	
	public static Map<String, String> gerUserProfession() {
		Map<String, String> userProfession = new HashMap<String, String>();
		userProfession.put("1", "金融");
		userProfession.put("2", "计算机、网络");
		userProfession.put("3", "市场、公关");
		userProfession.put("4", "媒体、影视");
		userProfession.put("5", "咨询、法律");
		userProfession.put("5", "快速消费品");
		userProfession.put("6", "餐饮、旅游");
		userProfession.put("7", "贸易、进出口");
		userProfession.put("8", "房地产、建筑");
		userProfession.put("9", "电子、通信");
		userProfession.put("10", "美术、设计、创意");
		userProfession.put("11", "生物、医疗、制药");
		userProfession.put("12", "在校学生");

		Collections.unmodifiableMap(userProfession);
		return userProfession;
	}
	
	public static Map<String, String> getUserAge() {
		Map<String, String> userAge = new HashMap<String, String>();
		userAge.put("18", "18岁以下");
		userAge.put("24", "18-24岁");
		userAge.put("30", "25-30岁");
		userAge.put("35", "31-35岁");
		userAge.put("40", "36-40岁");
		userAge.put("41", "40岁以上");
		
		Collections.unmodifiableMap(userAge);
		return userAge;
	}

	
	public static Map<String, String> getUserEducation() {
		Map<String, String> userEducation= new HashMap<String, String>();
		userEducation.put("1", "高中及以下");
		userEducation.put("2", "大专");
		userEducation.put("3", "本科");
		userEducation.put("4", "硕士");
		userEducation.put("5", "博士及以上");

		Collections.unmodifiableMap(userEducation);
		return userEducation;
	}
	
	public static Map<String, String> getUserSex() {
		Map<String, String> userSex= new HashMap<String, String>();
		userSex.put("1", "男");
		userSex.put("2", "女");

		Collections.unmodifiableMap(userSex);
		return userSex;
	}
}
