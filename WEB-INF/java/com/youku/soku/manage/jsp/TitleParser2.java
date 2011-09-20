package com.youku.soku.manage.jsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.util.DataBase;

public class TitleParser2 {
	public List<String> getTitleList() {
		List<String> titleList = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM cntv_movie";
		try {
			conn = DataBase.getSpiderConnection();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				titleList.add(rs.getString("title"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
		
		return titleList;
	}
	
	public void parse() {
		List<String> titleList = getTitleList();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(String title : titleList) {
			String key = title.replaceFirst("\\d{4}\\-\\d{1,2}\\-\\d{1,2}", "");
			Integer count = map.get(key);
			if(count != null) {
				count++;
				map.put(key, count);
			} else {
				map.put(key, 1);
			}
		}
		
		List<Map.Entry<String, Integer>> infolds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		
		Collections.sort(infolds, new Comparator<Map.Entry<String, Integer>>(){
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		});
		
		for(int i = 0; i < infolds.size(); i++) {
			//System.out.println(infolds.get(i));
			
			if(infolds.get(i).getValue() > 10) {
				System.out.println(infolds.get(i));
				getMovies(infolds.get(i).getKey());
			}
		}
	}
	
	public void parse(String rege) {
		List<String> titleList = getTitleList();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(String title : titleList) {
			String key = title.replaceFirst("\\d{4}年.*第\\d{1,2,3}期", "");
			Integer count = map.get(key);
			if(count != null) {
				count++;
				map.put(key, count);
			} else {
				map.put(key, 1);
			}
		}
		
		List<Map.Entry<String, Integer>> infolds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		
		Collections.sort(infolds, new Comparator<Map.Entry<String, Integer>>(){
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		});
		
		for(int i = 0; i < infolds.size(); i++) {
			//System.out.println(infolds.get(i));
			
			if(infolds.get(i).getValue() > 10) {
				System.out.println(infolds.get(i));
				getMovies(infolds.get(i).getKey());
			}
		}
	}
	public void getMovies(String title) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM cntv_movie where title like '" + title + "%'";
		try {
			conn = DataBase.getSpiderConnection();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				System.out.println(rs.getString("title") + parseVarietyTitle(rs.getString("title")));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
	public static String parseVarietyTitle(String title) {
		Pattern p = Pattern.compile("([\u4e00-\u9fa5])+.* (\\d{4})[年]{1}.*[第]{1}(\\d+)[期]{1}");
		Matcher m = p.matcher(title);
		String result = null;
		if (m.matches()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println(m.group(i));
			}
			
		}
		System.out.println(m.group(1));
		return result;
	}
	
	public static void main(String[] args) {
		String s = "夕阳红 2010年 第1期";
		
		
		System.out.println(s.replaceFirst("([\u4e00-\u9fa5])+.* (\\d{4})[年]{1}.*[第]{1}(\\d+)[期]{1}", ""));
		System.out.println(parseVarietyTitle("非常6 1 2009年 第5期 1-2"));
		
	/*	try {
			Torque.init("/opt/search/WEB-INF/soku-conf/Torque.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TitleParser t = new TitleParser();
		t.parse("\\d{4}[年]{1}.*[第]{1}\\d+[期]{1}");

		try {
			Torque.shutdown();
		} catch (TorqueException e) {
			e.printStackTrace();
		}*/
	}
	
}
