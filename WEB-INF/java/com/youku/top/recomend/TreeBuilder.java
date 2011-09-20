package com.youku.top.recomend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;
import com.youku.top.index.db.LogKeywordMgt;

public class TreeBuilder {
	
	public static List<Entity> readFile(String fileName) {
		List<Entity> result = new ArrayList<Entity>();
		try {
			BufferedReader d = new BufferedReader(new FileReader(fileName));
			String s = d.readLine();
			Entity entity = null;
			String[] tags = null;
			while ( null != s) {
				if(StringUtils.isBlank(s)){
					s = d.readLine();
					continue;
				}
				entity =  new Entity();
				tags = s.split("\t");
				if(null!=tags&&tags.length==2){
					entity.setKeyword(stopWordsFilter(tags[0].trim()).trim());
					if(StringUtils.isBlank(entity.keyword)){
						s = d.readLine();
						continue;
					}
					boolean flag=FilterUtils.isFilter(entity.keyword);
					if(flag){
						s = d.readLine();
						continue;
					}
					entity.setKeyword_py(com.youku.search.hanyupinyin.Converter.convert(entity.getKeyword(), true));
					entity.setSearchTimes(DataFormat.parseInt(tags[1],0));
					result.add(entity);
				}
				s = d.readLine();
			}
		} catch (Exception e) {
			System.err.println("读取文件错误:"+e);
		}
		return result;
	}
	
	public static void createTreeByFile(String date){
		List<Entity> result = readFile(LogKeywordMgt.dir+"keyword_"+date+".txt");
		createTreeByFile(result);
	}
	
	public static void createTreeByFile(List<Entity> result){
		if(null==result||result.size()<1) return;
		Constance.init();
		for(Entity entity:result){
			//不显示付费
			if(Constance.fufeis.contains(entity)) continue;
			
			Constance.videoTree.insert(entity);
			if(!entity.keyword.equalsIgnoreCase(entity.keyword_py)){
				Constance.ch_videoTree.insert(new Entity(entity.keyword_py,entity.searchTimes,entity.searchTimes,entity.keyword,entity.type));
			}
		}
	}
	
	public static void createTreeByInteface(){
		List<Entity> result = Converter.convert();
		
		//取出排行榜数据
		//过滤屏蔽词
//		result.addAll(getTopChannelEntitys());
		
		createTreeByFile(result);
	}
	
	public static boolean isNotEmpty(String date){
		File file = new File(LogKeywordMgt.dir+"keyword_"+date+".txt");
		if(file.exists()&&file.length()>0){
			return true;
		}
		return false;
	}
	
	public static String stopWordsFilter(String keyword){
		Set<Character> set = Constant.StopWords.getStopSet();
	    StringBuilder builder = new StringBuilder();
	    char[] chars = keyword.toCharArray();
	    for (char c : chars) {
	        if (set.contains(c)) {
	            builder.append(" ");
	        } else {
	            builder.append(c);
	        }
	    }
		return builder.toString();
	}
}
