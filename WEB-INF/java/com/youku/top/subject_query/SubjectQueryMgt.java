package com.youku.top.subject_query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.torque.util.BasePeer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.workingdogs.village.Record;
import com.workingdogs.village.Value;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.data_source.SearchStatDataSource;
import com.youku.top.util.WordLengthDescComparator;

public class SubjectQueryMgt {
	
	public static JdbcTemplate searchStatDataSource = new JdbcTemplate(
			SearchStatDataSource.INSTANCE);
	
	public static void main(String[] args) {
		//取得词
//		List<String> list = Utils.readFile("/opt/p.txt", "utf-8");
//		for(String p:list){
//			insertSubject(p, null, "person");
//		}
//		list = Utils.readFile("/opt/t.txt", "utf-8");
//		String alias = null;
//		for(String t:list){
//			alias = StringUtils.substringBetween(t, "(", ")");
//			if(StringUtils.isBlank(alias))
//				alias = null;
//			t = t.replaceAll("\\([^\\)]*\\)", "").trim();
//			if(StringUtils.isBlank(t)) continue;
//			insertSubject(t, alias, "subject");
//		}
		
		//crontab
		String date = null;
		if(null!=args&&args.length==1&&args[0].matches("\\d{4}_\\d{2}_\\d{2}"))
			date = args[0];
		doDailyQueryCount(date);
	}
	
	public static void doDailyQueryCount(String date){
		Map<Integer,String> map = selectSubject();
		Set<String> keywords = null;
		int query_count = 0;
		if(StringUtils.isBlank(date)||!date.matches("\\d{4}_\\d{2}_\\d{2}")){
			date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		}
		Set<String> set = new HashSet<String>();
		for(Entry<Integer, String> entry:map.entrySet()){
			keywords = Utils.parseStr2Set(entry.getValue(), "\\|");
			if(null==keywords||keywords.size()<1)
				continue;
			set.addAll(keywords);
		}
		List<String> list = new ArrayList<String>();
		list.addAll(set);
		System.out.println("搜索词排好序");
		Collections.sort(list, new WordLengthDescComparator());
		int min_length = list.get(list.size()-1).length();
		Map<String,Integer> querys = new HashMap<String, Integer>();
		ConcurrentHashMap<String,Integer> singles = getAllQueryCont("query_"+date,min_length);
		System.out.println("取得所有搜索量,"+singles.size());
		for(String str:list){
			query_count = 0;
			for(Entry<String, Integer> s:singles.entrySet()){
				if(Pattern.compile(str).matcher(s.getKey()).find()){
					query_count +=s.getValue();
					singles.remove(s.getKey());
				}
			}
			if(query_count>0)
				querys.put(str, query_count);
		}
		singles.clear();
		for(Entry<Integer, String> entry:map.entrySet()){
			keywords = Utils.parseStr2Set(entry.getValue(), "\\|");
			if(null==keywords||keywords.size()<1)
				continue;
			int union_querys = 0;
			for(String k:keywords)
				union_querys += DataFormat.parseInt(querys.get(k));
			if(union_querys>0)
				insertQuery(entry.getKey(), union_querys , date);
		}
	}
	
	public static JSONArray getDailySubjectQuerysByTorque(String date){
		JSONArray jarr = new JSONArray();
		try{
			String sql = "select subject_query.keyword,subject_query.search_keys,subject_query_daily.query_count from subject_query, subject_query_daily where subject_query.id = subject_query_daily.fk_subject_query_id and subject_query.visible=1 and subject_query_daily.query_date = '"+date+"' order by subject_query_daily.query_count desc";
			List<Record> list = BasePeer.executeQuery(sql,"youkuLog");
			String keyword = null;
			String search_keys = null;
			int query_count = 0;
			JSONObject json = null;
			Value v = null;
			for(Record r:list){
				query_count = r.getValue("subject_query_daily.query_count").asInt();
				if(0==query_count)
					continue;
				search_keys = null;
				json = new JSONObject();
				keyword = r.getValue("subject_query.keyword").asString();
				if(!StringUtils.isBlank(keyword)&&!keyword.equalsIgnoreCase("null")){
					json.put("keyword", keyword);
					v =	r.getValue("subject_query.search_keys");
					if(!v.isNull()){
						search_keys = v.asString();
					}
					if(!StringUtils.isBlank(search_keys)&&!search_keys.equalsIgnoreCase("null")){
						json.put("search_keys", search_keys);
					}
					json.put("query_count", query_count);
					jarr.put(json);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return jarr;
	}
	
	public static JSONArray getDailySubjectQuerys(String date){
		JSONArray jarr = new JSONArray();
		try{
			String sql = "select subject_query.keyword,subject_query.search_keys,subject_query_daily.query_count from subject_query, subject_query_daily where subject_query.id = subject_query_daily.fk_subject_query_id and subject_query_daily.query_date = ? ";
			List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql,new Object[]{date},new int[]{Types.VARCHAR});
			Iterator it = list.iterator();
			Map map = null;
			String keyword = null;
			String search_keys = null;
			int query_count = 0;
			JSONObject json = null;
			while(it.hasNext()){
				map = (Map)(it.next());
				query_count = DataFormat.parseInt(map.get("subject_query_daily.query_count"));
				if(0==query_count)
					continue;
				keyword = ""+map.get("subject_query.keyword");
				if(!StringUtils.isBlank(keyword)&&!keyword.equalsIgnoreCase("null")){
					json = new JSONObject();
					json.put("keyword", keyword);
					search_keys = ""+map.get("subject_query.search_keys");
					if(!StringUtils.isBlank(search_keys)&&!search_keys.equalsIgnoreCase("null")){
						json.put("search_keys", search_keys);
					}
					json.put("query_count", query_count);
					jarr.put(json);
				}
			}
		}catch(Exception e){}
		return jarr;
	}
	
	public static ConcurrentHashMap<String,Integer> getAllQueryCont(String table,int min_length) {
		ConcurrentHashMap<String,Integer> results = new ConcurrentHashMap<String, Integer>();
		List list = null;
		try {
			list = searchStatDataSource.queryForList("select keyword ,query_count from "
					+ table + " where query_count>5 and query_type='video' and source='youku' ");
		} catch (Exception e) {
		}
		if(null==list) return results;
		Iterator it = list.iterator();
		Map map = null;
		String n = null;
		int count = 0;
		int querys = 0;
		while(it.hasNext()){
			map = (Map)(it.next());
			count = DataFormat.parseInt(map.get("query_count"));
			if(0==count)
				continue;
			n = ""+map.get("keyword");
			if(!StringUtils.isBlank(n)&&!n.equalsIgnoreCase("null")){
				if(n.length()<min_length)
					continue;
				results.put(n, count+DataFormat.parseInt(results.get(n)));
			}
		}
		return results;
	}
	
//	public static int getQueryCont(Set<String> keywords,String keyword, String table) {
//		List list = null;
//		try {
//			list = JdbcTemplateFactoray.searchStatDataSource.queryForList("select keyword ,query_count from "
//					+ table + " where keyword like ? and query_type='video' and source='youku' order by query_count desc",
//					new Object[] { "%"+keyword+"%"}, new int[] { Types.VARCHAR });
//		} catch (Exception e) {
//			String word = KeywordUtil.analyzer(keyword);
//			if(keyword.equalsIgnoreCase(word)) return 0;
//			if(!StringUtils.isBlank(word)){
//				try{
//				list = JdbcTemplateFactoray.searchStatDataSource.queryForList("select keyword ,query_count from "
//						+ table + " where keyword like ? and query_type='video' and source='youku' order by query_count desc",
//						new Object[] { "%"+word+"%" }, new int[] { Types.VARCHAR });
//				}catch(Exception e1){
//				}
//			}
//		}
//		if(null==list) return 0;
//		Iterator it = list.iterator();
//		Map map = null;
//		String n = null;
//		int count = 0;
//		int querys = 0;
//		while(it.hasNext()){
//			map = (Map)(it.next());
//			count = DataFormat.parseInt(map.get("query_count"));
//			if(0==count)
//				continue;
//			n = ""+map.get("keyword");
//			if(!StringUtils.isBlank(n)&&!n.equalsIgnoreCase("null")){
//				if(keywords.contains(n)) continue;
//				querys +=count;
//				keywords.add(n);
//			}
//		}
//		return querys;
//	}
	
	public static Map<Integer,String> selectSubject(){
		Map<Integer,String> results = new HashMap<Integer, String>();
		String sql = "select id,keyword,search_keys from subject_query";
		try{
			List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
			Iterator it = list.iterator();
			Map map = null;
			StringBuilder keyword = null;
			String n = null;
			int id = 0;
			while(it.hasNext()){
				map = (Map)(it.next());
				id = DataFormat.parseInt(map.get("id"));
				if(0==id)
					continue;
				keyword = new StringBuilder();
				n = ""+map.get("keyword");
				if(!StringUtils.isBlank(n)&&!n.equalsIgnoreCase("null")){
					keyword.append(n);
					n = ""+map.get("search_keys");
					if(!StringUtils.isBlank(n)&&!n.equalsIgnoreCase("null")){
						keyword.append("|");
						keyword.append(n);
					}
				}
				if(keyword.length()<1) continue;
				results.put(id, keyword.toString());
			}
		}catch(Exception e){}
		return results;
	}
	
	
	public static void insertSubject(final String keyword,final String search_keys,final String type){
		try{
			String sql = "insert ignore into subject_query (keyword,search_keys,keyword_type) values (?,?,?)";
			JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, keyword);
					if(StringUtils.isBlank(search_keys))
						ps.setNull(2,Types.VARCHAR);
					else
						ps.setString(2, search_keys);
					ps.setString(3, type);
				}
			});
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void insertQuery(final int fk_subject_query_id,final int query_count,final String query_date){
		try{
			String sql = "insert ignore into subject_query_daily (fk_subject_query_id,query_count,query_date) values (?,?,?)";
			JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, fk_subject_query_id);
					ps.setInt(2, query_count);
					ps.setString(3, query_date);
				}
			});
			}catch(Exception e){}
	}
	
}
