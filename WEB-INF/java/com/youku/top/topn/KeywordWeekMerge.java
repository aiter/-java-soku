package com.youku.top.topn;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.data_source.SearchSokuStatDataSource;
import com.youku.top.data_source.SearchStatDataSource;
import com.youku.top.topn.entity.KeywordWeekVO;
import com.youku.top.topn.util.LogUtil;

public class KeywordWeekMerge {
	
	static Logger logger = Logger.getLogger(KeywordWeekMerge.class);
	public JdbcTemplate searchSokuStatDataSource = new JdbcTemplate(
			SearchSokuStatDataSource.INSTANCE);
	public JdbcTemplate searchStatDataSource = new JdbcTemplate(
			SearchStatDataSource.INSTANCE);
	
	public List<KeywordWeekVO> getInsiteDayTopQueryKeywords(String table){
		String sql="select * from "+table+ " where query_type='video' order by query_count desc limit 50000";
//		System.out.println(sql);
		try{
		return searchStatDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){}
		return null;
	}
	
	public List<KeywordWeekVO> getOutsiteDayTopQueryKeywords(String table){
		String sql="select * from "+table+ " where query_type='video' order by query_count desc limit 50000";
//		System.out.println(sql);
		try{
		return searchSokuStatDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){}
		return null;
	}
	
	public List<KeywordWeekVO> getDayTopClickKeywords(String table){
		String sql="select keyword,sum(click_count) c,type from "+table+" where type = 'video' group by keyword,because order by c desc limit 50000";
//		System.out.println(sql);
		try{
		return searchSokuStatDataSource.query(sql, KeywordWeekVOMapper.kwvomapper);
		}catch(Exception e){}
		return null;
	}
	
	public void unionInsiteQueryKeyword(String table,String preff,List<String> uniondates){
		String sql = null;
		List<KeywordWeekVO> kwos = null;
		//合并一周数据,添加或更新数据
		for(String uniondate:uniondates){
			kwos = getInsiteDayTopQueryKeywords( new StringBuilder(preff).append(uniondate).toString());
			if(null==kwos||kwos.size()<50000){
				logger.warn(new StringBuilder(preff).append(uniondate).toString()+":"+(null==kwos?0:kwos.size()));
			}
			if(null==kwos) continue;
			for(final KeywordWeekVO kwo:kwos){
				if(null==kwo) continue;
				try{
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
				}catch(Exception e){}
			}
		}
	}
	
	public void unionOutsiteQueryKeyword(String table,String preff,List<String> uniondates){
		String sql = null;
		List<KeywordWeekVO> kwos = null;
		//合并一周数据,添加或更新数据
		for(String uniondate:uniondates){
			kwos = getOutsiteDayTopQueryKeywords( new StringBuilder(preff).append(uniondate).toString());
			if(null==kwos) continue;
			for(final KeywordWeekVO kwo:kwos){
				if(null==kwo) continue;
				try{
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
				}catch(Exception e){}
			}
		}
	}
	
	public void unionClickKeyword(String table,String preff,List<String> uniondates){
		String sql = null;
		List<KeywordWeekVO> kwos = null;
		//合并一周数据,添加或更新数据
		for(String uniondate:uniondates){
			kwos = getDayTopClickKeywords(new StringBuilder(preff).append(uniondate).toString());
			if(null==kwos) continue;
			for(final KeywordWeekVO kwo:kwos){
				if(null==kwo) continue;
				try{
				sql = "insert into "+table+"  (keyword,type,because,click_count) values (?,?,?,?) ON DUPLICATE KEY UPDATE click_count=click_count+?";
				JdbcTemplateFactoray.mergeLogYoukuDataSource.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, kwo.getKeyword());
						ps.setString(2, kwo.getQuery_type());
						ps.setInt(3, kwo.getBecause());
						ps.setInt(4, kwo.getQuery_count());
						ps.setInt(5, kwo.getQuery_count());
					}
				});
				}catch(Exception e){}
			}
		}
	}
	
	public void merge(String start,String end) throws Exception{
		logger.info(new Date()+"--start--每周关键词合并,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
		
		if(end==null) end=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
		
		if(start==null) start=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
		
		String insiteTable = new StringBuilder("query_youku_").append(start).append("_").append(end).toString();
		String outsiteTable = new StringBuilder("query_soku_").append(start).append("_").append(end).toString();
		
		List<String> uniondates = new ArrayList<String>();
		String temp = null;
		String breadDate = DataFormat.formatDate(DataFormat.parseUtilDate(end,DataFormat.FMT_DATE_SPECIAL), DataFormat.FMT_DATE_YYYY_MM_DD);
		for(int i=0;;i++){
			temp = DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(start,DataFormat.FMT_DATE_SPECIAL),i), DataFormat.FMT_DATE_YYYY_MM_DD);
			uniondates.add(temp);
			if(temp.equalsIgnoreCase(breadDate))
				break;
		}
		
		logger.info("更新 table:"+insiteTable);
		//站内查询
		unionInsiteQueryKeyword(insiteTable,"query_",uniondates);
		logger.info("站内搜索数据更新完毕");
		logger.info("更新 table:"+outsiteTable);
		
		//站外查询
		unionOutsiteQueryKeyword(outsiteTable,"query_",uniondates);
		
		
		logger.info("站外搜索数据更新完毕");
		//站外点击
		unionClickKeyword(new StringBuilder("click_soku_").append(start).append("_").append(end).toString(),"click_",uniondates);
		
		logger.info("站外点击数据更新完毕");
		
	}
	
	
	//定时任务  每周日4点
	public static void main(String[] args) {
		try {
			LogUtil.init();
			logger.info("每周关键词合并,sleep20s---start---");
			Thread.sleep(20*1000);
			logger.info("每周关键词合并,sleep20s---end---");
			KeywordWeekMerge kwm = new KeywordWeekMerge();
			if(null!=args&&args.length==2)
				kwm.merge(args[0], args[1]);
			else kwm.merge(null, null);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
