package com.youku.soku.library.quick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.top.topn.entity.KeywordWeekVO;

public class QuickDataVO {
	
	public String date;
	public Map<String,List<KeywordWeekVO>> map = new HashMap<String,List<KeywordWeekVO>>();
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, List<KeywordWeekVO>> getMap() {
		return map;
	}
	public void setMap(Map<String, List<KeywordWeekVO>> map) {
		this.map = map;
	}
	
}
