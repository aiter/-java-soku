package com.youku.top.topn;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.topn.entity.KeywordQueryVO;
import com.youku.top.topn.util.LogUtil;

public class KeywordWeekUnion {
	
	static Logger logger = Logger.getLogger(KeywordWeekUnion.class);
	
	public void insertTopKeywords(String start,String end){
		if(end==null) end=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
		if(start==null) start=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
		String uniondate = new StringBuilder(start).append("_").append(end).toString();
		
		logger.info("周数据，更新时间段:"+uniondate);
		if(StringUtils.isBlank(uniondate))
			return;
		Map<String,KeywordQueryVO> keywordMap = new HashMap<String, KeywordQueryVO>();
		
		getTopKeywords(uniondate, keywordMap);
		doInsertTopKeywords(uniondate, keywordMap);
		logger.info("周数据更新完成,"+uniondate);
	}
	
	public void getTopKeywords(String uniondate,Map<String,KeywordQueryVO> keywordMap){
		//站内查询及结果数
		getInsiteTopKeywords(uniondate, keywordMap);
		//站外查询及结果数
		getOutsiteTopKeywordTotalSearchs(uniondate, keywordMap);
		//站内外查询，显示，点击
		getOutsiteTopKeywords(uniondate, keywordMap);
	}
	
	public void getInsiteTopKeywords(String uniondate,Map<String,KeywordQueryVO> keywordMap){
		KeywordQueryVO kqo=null;
		String sql="select * from query_youku_"+uniondate+ " where query_type='video' ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
		Iterator it = list.iterator();
		Map map = null;
		String keyword = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			keyword = (String) map.get("keyword");
			kqo = keywordMap.get(keyword);
			if(null==kqo){
				kqo = new KeywordQueryVO();
				keywordMap.put(keyword, kqo);
				kqo.setKeyword(keyword);
			}
			kqo.setInsearchs(DataFormat.parseInt(map.get("query_count")));
			kqo.setInresults(DataFormat.parseInt(map.get("result")));
		}
	}
	
	public void doGetInsiteTopKeywordSearchs(String uniondate,KeywordQueryVO kqo){
		String sql="select query_count,result from query_youku_"+uniondate+ " where query_type='video' and keyword=?  ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql, new Object[]{kqo.getKeyword()}, new int[]{Types.VARCHAR});
		Iterator it = list.iterator();
		Map map = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			kqo.setInsearchs(DataFormat.parseInt(map.get("query_count")));
			kqo.setInresults(DataFormat.parseInt(map.get("result")));
		}
	}
	
	public void doInsertTopKeywords(String uniondate,Map<String,KeywordQueryVO> keywordMap){
		 for(Entry<String, KeywordQueryVO> entry:keywordMap.entrySet()){
			 doInsertKeywordss(entry.getValue(), uniondate);
		 }
	}
	
	
	
	private void doInsertKeywordss(final KeywordQueryVO kqo,String uniondate){
		JdbcTemplateFactoray.mergeLogYoukuDataSource.update("insert ignore into keyword_week_"+uniondate+" (keyword,insearchs,inresults,outsearchs,outresults,maxsearchs,outclicks) values (?,?,?,?,?,?,?)", new PreparedStatementSetter() {  
			                         public void setValues(PreparedStatement ps) throws SQLException {  
				                             ps.setString(1, kqo.getKeyword());  
				                             ps.setInt(2, kqo.getInsearchs());            
				                             ps.setInt(3, kqo.getInresults());  
				                             ps.setInt(4, kqo.getOutsearchs());
				                             ps.setInt(5, kqo.getOutresults());
				                             ps.setInt(6, kqo.getMaxsearchs());
				                             ps.setInt(7, kqo.getOutclicks());
				                         }
				                      });
	}
	
	public void getOutsiteTopKeywords(String uniondate,Map<String,KeywordQueryVO> keywordMap){
		 for(Entry<String, KeywordQueryVO> entry:keywordMap.entrySet()){
			 //站外查询
			 doGetOutsiteTopKeywordSearchs(uniondate, entry.getValue());
			 //站外显示
			 //doGetOutsiteTopKeywordViews(uniondate, entry.getValue());
			 //站外点击
			 doGetOutsiteTopKeywordClicks(uniondate, entry.getValue());
			 
			 if(entry.getValue().getOutsearchs()<1){
				 doGetOutsiteTopKeywordAllSearchs(uniondate, entry.getValue());
				 //doGetOutsiteTopKeywordAllViews(uniondate, entry.getValue());
			 }
			 
			 if(entry.getValue().getInsearchs()<1){
				 doGetInsiteTopKeywordSearchs(uniondate,  entry.getValue());
			 }
		 }
	}
	
	public void getOutsiteTopKeywordTotalSearchs(String uniondate,Map<String,KeywordQueryVO> keywordMap){
		String sql="select keyword,result,sum(query_count) as c from query_soku_"+uniondate+ " where query_type='video' group by keyword ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql);
		Iterator it = list.iterator();
		Map map = null;
		String keyword = null;
		KeywordQueryVO kqo=null;
		while(it.hasNext()){
			map = (Map)(it.next());
			keyword = (String) map.get("keyword");
			kqo = keywordMap.get(keyword);
			if(null==kqo){
				kqo = new KeywordQueryVO();
				keywordMap.put(keyword, kqo);
				kqo.setKeyword(keyword);
			}
			kqo.setOutsearchs(DataFormat.parseInt(map.get("c")));
			kqo.setOutresults(DataFormat.parseInt(map.get("result")));
		}
	}	
	
	public void doGetOutsiteTopKeywordAllSearchs(String uniondate,KeywordQueryVO kqo){
		String sql="select sum(query_count) as searchs,result from query_soku_"+uniondate+ " where query_type='video' and keyword=? group by keyword";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql,new Object[]{kqo.getKeyword()}, new int[]{Types.VARCHAR});
		Iterator it = list.iterator();
		Map map = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			kqo.setOutsearchs(DataFormat.parseInt(map.get("searchs")));
			kqo.setOutresults(DataFormat.parseInt(map.get("result")));
		}
	}
	
	public void doGetOutsiteTopKeywordSearchs(String uniondate,KeywordQueryVO kqo){
		String sql="select sum(query_count) as searchs from query_soku_"+uniondate+ " where query_type='video' and keyword=? ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql,new Object[]{kqo.getKeyword()}, new int[]{Types.VARCHAR});
		Iterator it = list.iterator();
		Map map = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			kqo.setOutsearchs(DataFormat.parseInt(map.get("searchs")));
		}
	}
	
	
	public void doGetOutsiteTopKeywordClicks(String uniondate,KeywordQueryVO kqo){
		String sql="select sum(click_count) as clicks from click_soku_"+uniondate+ " where  keyword=?  ";
		List list = JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForList(sql,new Object[]{kqo.getKeyword()}, new int[]{Types.VARCHAR});
		Iterator it = list.iterator();
		Map map = null;
		while(it.hasNext()){
			map = (Map)(it.next());
			kqo.setOutclicks(DataFormat.parseInt(map.get("clicks")));
		}
	}
	
	//定时任务  每周日6点
	public static void main(String[] args) {
		try {
			LogUtil.init();
			logger.info("每周关键词统计预处理,sleep20s---start---");
			Thread.sleep(20*1000);
			logger.info("每周关键词统计预处理,sleep20s---end---");
			long s=System.currentTimeMillis();
			logger.info(new Date()+"--start--每周关键词统计预处理,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
			KeywordWeekUnion ku=new KeywordWeekUnion();
			if(null!=args&&args.length==2)
				ku.insertTopKeywords(args[0], args[1]);
			else ku.insertTopKeywords(null, null);
			logger.info(new Date()+"--end--每周关键词统计预处理,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
