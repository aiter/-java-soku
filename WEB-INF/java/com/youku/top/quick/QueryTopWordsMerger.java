package com.youku.top.quick;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.console.util.Wget;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.paihangbang.entity.StopWords;
import com.youku.top.util.TopWordType.WordType;

public class QueryTopWordsMerger {
	static Logger logger = Logger.getLogger(QueryTopWordsMerger.class);
	private QueryTopWordsMerger(){}
	private static QueryTopWordsMerger instance = null;
	public static QueryTopWordsMerger getInstance(){
		if(null==instance)
			return new QueryTopWordsMerger();
		return instance;
	}
	
	
	public List<QuickVO> getNamesByCate(int cate,Map<String,Integer> map1,Map<String,Integer> map2,StopWords swvo,Map<String,Set<String>> topmap,Map<String,Set<String>> music_topmap){
		List<QuickVO>  list = new ArrayList<QuickVO>();
		QuickVO qvo = null;
		int query_count1 = 0;
		int query_count2 = 0;
		int query_count_sub = 0;
		if(null!=topmap&&topmap.size()>0){
			for(Entry<String,Set<String>> entry:topmap.entrySet()){
				if(StringUtils.isBlank(entry.getKey())) continue;
				if(swvo.getDeleted_words().contains(entry.getKey())) continue;
				
				if(cate!=WordType.音乐.getValue()){
					music_topmap.remove(entry.getKey());
				}
				qvo = new QuickVO();
				if(swvo.getBlocked_words().contains(entry.getKey())) {
					qvo.setVisible(0);
				}
				query_count1 = 0;
				query_count2 = 0;
				query_count_sub = 0;
				for(String n:entry.getValue()){
					query_count1 = query_count1 + DataFormat.parseInt(map1.get(n));
					query_count2 = query_count2 + DataFormat.parseInt(map2.get(n));
				}
				query_count_sub = query_count1 - query_count2;
				qvo.setKeyword(entry.getKey());
				qvo.setQuery_count1(query_count1);
				qvo.setQuery_count2(query_count2);
				qvo.setQuery_count_sub(query_count_sub);
				qvo.setCate(cate);
				list.add(qvo);
			}
		}
		return list;
	}
	
	
	
	public Map<String,Integer> getQueryMap(String date){
		Map<String,Integer> map = null;
		try {
			byte[] bytes = Wget.get("http://10.103.8.225/index/filedownload?date="+date);
			String res = new String(bytes,"utf-8");
			List<String> list = Utils.parseStr2List(res, "\\\r\\\n");
			if(null==list) return null;
			map = FileUtils.list2Map(list, "\\\t");
			Thread.sleep(50);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String,Integer> getQueryMap(List<String> dates){
		Map<String,Integer> map = new HashMap<String, Integer>();
		Map<String,Integer> tempmap = null;
		int query_count = 0;
		for(String date:dates){
			tempmap = getQueryMap(date);
			if(null!=tempmap&&tempmap.size()>0){
				for(Entry<String, Integer> entry:tempmap.entrySet()){
					query_count = DataFormat.parseInt(map.get(entry.getKey()));
					map.put(entry.getKey(), query_count+entry.getValue());
				}
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public void saveQuickVO(List<QuickVO> list,String date_between){
		if(null==list||list.size()<1) return;
		Collections.sort(list,new QuickVOComparator());
		AtomicLong al = new AtomicLong();
		int num = 0;
		for(QuickVO qvo:list){
			num = (int)al.incrementAndGet();
			qvo.setOrder_number(num);
			saveQuickVO(qvo,date_between);
		}
	}
	
	public void saveQuickVO(final QuickVO qvo,String date_between){
		try{
			String sql = "insert ignore into quick_"+date_between+" (keyword,cate,query_count1,query_count2,query_count_sub,order_number,visible) values (?,?,?,?,?,?,?)";
			QuickTopDataMgt.sokuTopDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, qvo.getKeyword());
					ps.setInt(2,qvo.getCate());
					ps.setInt(3, qvo.getQuery_count1());
					ps.setInt(4, qvo.getQuery_count2());
					ps.setInt(5, qvo.getQuery_count_sub());
					ps.setInt(6,qvo.getOrder_number());
					ps.setInt(7,qvo.getVisible());
				}
			});
			}catch(Exception e){
				logger.error(e);
			}
	}
}
