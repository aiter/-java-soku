package com.youku.soku.manage.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class EpisodeStatusSearchHelper {
	
	public static EpisodeStatusSearchHelper INSTANCE = new EpisodeStatusSearchHelper();
	
	
	
	private EpisodeStatusSearchHelper() {
		initStatusMap();
	}
	
	private Map<Integer, String> statusMap;
	
	private Map<Integer, String> movieStatusMap;
	
	private Map<Integer, String> videoInfoStatusMap;
	
	public Map<Integer, String> getVideoInfoStatusMap() {
		return videoInfoStatusMap;
	}

	public void setVideoInfoStatusMap(Map<Integer, String> videoInfoStatusMap) {
		this.videoInfoStatusMap = videoInfoStatusMap;
	}

	public Map<Integer, String> getMovieStatusMap() {
		return movieStatusMap;
	}

	public void setMovieStatusMap(Map<Integer, String> movieStatusMap) {
		this.movieStatusMap = movieStatusMap;
	}

	public Map<Integer, String> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<Integer, String> statusMap) {
		this.statusMap = statusMap;
	}

	private void initStatusMap() {
		statusMap = new LinkedHashMap<Integer, String>();
		statusMap.put(-1, "所有");
		statusMap.put(1, "有效");	
		statusMap.put(2, "无效");	
		statusMap.put(3, "未收录完");	
		statusMap.put(4, "已收录完");	
		statusMap.put(5, "没有剧集");	
		statusMap.put(6, "中间有空集");
		
		movieStatusMap = new LinkedHashMap<Integer, String>();
		movieStatusMap.put(-1, "所有");
		movieStatusMap.put(1, "有效");
		movieStatusMap.put(2, "无效");
	}
	
	public String constructStatusSearchSql(int status) {
		String sql = "";
		if(status == 1) {
			sql += " AND blocked = 0";
		} else if (status ==2) {
			sql += " AND blocked = 1";
		} else if(status == 3) {
			sql += " AND completed = 0";
		} else if(status == 4) {
			sql += " AND completed = 1";
		} else if(status == 5) {
			sql += " AND episode_collected = 0";
		} else if(status == 6) {
			sql += " AND mid_empty = 1";
		}
		
		return sql;
	}
	
}
