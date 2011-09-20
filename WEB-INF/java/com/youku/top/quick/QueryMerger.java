package com.youku.top.quick;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.data_source.SearchStatDataSource;
import com.youku.top.topn.KeywordWeekVOMapper;
import com.youku.top.topn.entity.KeywordWeekVO;
import com.youku.top.util.LogUtil;

public class QueryMerger {
	
	static Logger logger = Logger.getLogger(QueryMerger.class);
	public JdbcTemplate searchStatDataSource = new JdbcTemplate(
			SearchStatDataSource.INSTANCE);
	
	private void unionInsiteQueryKeyword(String table,String preff,List<String> uniondates){
		String sql = null;
		List<KeywordWeekVO> kwos = null;
		try{
		//合并一周数据,添加或更新数据
		for(String uniondate:uniondates){
			kwos = getInsiteDayTopQueryKeywords( new StringBuilder(preff).append(uniondate).toString());
			if(null==kwos||kwos.size()<50000){
				logger.warn(new StringBuilder(preff).append(uniondate).toString()+":"+(null==kwos?0:kwos.size()));
			}
			for(final KeywordWeekVO kwo:kwos){
				if(null==kwo) continue;
				sql = "insert into "+table+" (keyword,query_type,result,query_count) values (?,?,?,?) ON DUPLICATE KEY UPDATE result=(result*query_count+?)/(query_count+?),query_count=query_count+?";
				JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, kwo.getKeyword());
						ps.setString(2, kwo.getQuery_type());
						ps.setInt(3, kwo.getResult());
						ps.setInt(4, kwo.getQuery_count());
						ps.setLong(5, ((long)kwo.getResult())*kwo.getQuery_count());
						ps.setInt(6, kwo.getQuery_count());
						ps.setInt(7, kwo.getQuery_count());
					}
				});
			}
		}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	private List<KeywordWeekVO> getInsiteDayTopQueryKeywords(String table){
		String sql="select * from "+table+ " where query_count>15";
//		System.out.println(sql);
		try{
		return searchStatDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){
			logger.error(e);
		}
		return null;
	}
	
	private List<KeywordWeekVO> getInsiteUnionQueryKeywords(String table){
		String sql="select * from "+table+ " where query_count>15";
//		System.out.println(sql);
		try{
		return JdbcTemplateFactoray.mergeLogYoukuDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){
			logger.error(e);
		}
		return null;
	}
	
	public void buildQueryInfos(String table,String preff,List<String> uniondates,String before_table){
		unionInsiteQueryKeyword(table, preff, uniondates);
		List<KeywordWeekVO> kvos = getInsiteUnionQueryKeywords(table);
		Map<String,Map<String,KeywordWeekVO>> beforekvos = getQueryCount(before_table);
		if(null==kvos||null==beforekvos) {
			logger.warn(table+" or "+before_table+"  size : 0");
		}else{
			double rate = 0;
			KeywordWeekVO beforekvo = null;
			int qc = 0;
			Map<String,KeywordWeekVO> map = null;
			for(KeywordWeekVO kvo:kvos){
				map = beforekvos.get(kvo.getQuery_type());
				if(null!=map){
					beforekvo = map.get(kvo.getKeyword());
					if(null==beforekvo) 
						qc = 0;
					else qc = beforekvo.getQuery_count();
				}else
					qc = 0;
				rate = function_weight(kvo.getQuery_count(), qc);
				kvo.setRate((float)rate);
				updateQuickRate(kvo, table);
			}
		}
	}
	
	private Map<String,Map<String,KeywordWeekVO>> getQueryCount(String table){
		Map<String,KeywordWeekVO> map = null;
		Map<String,Map<String,KeywordWeekVO>> result = new HashMap<String,Map<String,KeywordWeekVO>>();
		List<KeywordWeekVO> kvos = getInsiteUnionQueryKeywords(table);
		if(null==kvos) return result;
		for(KeywordWeekVO kvo:kvos){
			synchronized (kvo) {
				map = result.get(kvo.getQuery_type());
				if(null==map){
					map = new HashMap<String, KeywordWeekVO>();
					result.put(kvo.getQuery_type(), map);
				}
				map.put(kvo.getKeyword(), kvo);
			}
		}
		return result;
	}
	
	private double function_weight(int query_count1,int query_count2){
		if(query_count2<15) return Math.abs(Math.log(query_count1*1.0/15));
		else if(query_count1<query_count2) return Math.abs(query_count1*1.0/query_count2);
		else if(query_count1==query_count2) return 1;
		else return Math.abs(Math.log10((query_count1*1.0/query_count2)*(query_count1-query_count2)));
	}
	
	private void updateQuickRate(KeywordWeekVO kvo,String table){
		String sql = "update "+table+" set quick_rate=? where id=? ";
		try{
		JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new Object[]{kvo.getRate(),kvo.getId()}, new int[]{Types.FLOAT,Types.INTEGER});
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	public static void main(String[] args) {
		String end = null;
		String start = null;
		String before_date = null;
		if(null!=args&&args.length==3){
			start = args[0];
			end = args[1];
			before_date = args[2];
		}else{
			end = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
			start = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
			String beforeend = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-8), DataFormat.FMT_DATE_SPECIAL);
			String beforestart = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-14), DataFormat.FMT_DATE_SPECIAL);
			before_date = new StringBuilder(beforestart).append("_").append(beforeend).toString();
		}
		
		List<String> uniondates = new ArrayList<String>();
		String temp = null;
		String breadDate = DataFormat.formatDate(DataFormat.parseUtilDate(end,DataFormat.FMT_DATE_SPECIAL), DataFormat.FMT_DATE_YYYY_MM_DD);
		for(int i=0;;i++){
			temp = DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(start,DataFormat.FMT_DATE_SPECIAL),i), DataFormat.FMT_DATE_YYYY_MM_DD);
			uniondates.add(temp);
			if(temp.equalsIgnoreCase(breadDate))
				break;
		}
		
		try {
			LogUtil.init(Level.INFO,"/opt/log_analyze/quick/log/log.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		QueryMerger qm = new QueryMerger();
		qm.buildQueryInfos(new StringBuilder("week_query_youku_").append(start).append("_").append(end).toString(), "query_", uniondates, new StringBuilder("week_query_youku_").append(before_date).toString());
		
	}
}
