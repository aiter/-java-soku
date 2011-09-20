package com.youku.top.recomend;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.Filter;

public class FilterUtils {
	private static Pattern pattern;
	
	public static void list2Regex(List<String> list){
		if(null==list||list.size()<1){
			System.err.println("null filter words prepared.");
			pattern = null;
			return;
		}
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<list.size();i++){
			if(i!=0)sb.append("|");
			sb.append(list.get(i));
		}
		pattern = Pattern.compile(sb.toString());
	}
	
	public static boolean isMatch(String keyword){
		if(null==keyword)return false;
		if(null==pattern)return false;
		else{
			Matcher matcher  = pattern.matcher(keyword);
			return matcher.find();
		}
	}
	
//	public static boolean isAllMatch(String keyword){
//		if(null==keyword)return false;
//		if(null==Constance.allmatch_filterList||Constance.allmatch_filterList.size()<1)
//			return false;
//		if(Constance.allmatch_filterList.contains(keyword))
//			return true;
//		else return false;
//	}
	
	/**
	 * 是否匹配过滤词 true:过滤 false:不过滤
	 * @param keyword
	 * @return
	 */
	public static boolean isFilter(String keyword){
		if(StringUtils.isBlank(keyword)) return true;
		ShieldInfo s = Filter.getInstance().isShieldWord(keyword, Filter.Source.youku);
		if(null!=s)
			return s.isMatched();
//		System.out.println("屏蔽系统返回null，keyword:"+keyword);
		return false;
	}
}
