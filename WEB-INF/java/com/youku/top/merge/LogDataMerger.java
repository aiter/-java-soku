package com.youku.top.merge;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.top.data_source.SearchStatDataSource;
import com.youku.top.data_source.SokuTopDataSource;
import com.youku.top.quick.QueryMerger;
import com.youku.top.topn.util.KeywordUtil;
import com.youku.top.util.LogUtil;

public class LogDataMerger {
	static Logger logger = Logger.getLogger(QueryMerger.class);
	public JdbcTemplate sokuTopDataSource = new JdbcTemplate(
			SokuTopDataSource.INSTANCE);
	public JdbcTemplate searchStatDataSource = new JdbcTemplate(
			SearchStatDataSource.INSTANCE);
	
	private static LogDataMerger instance =null;
	public static synchronized LogDataMerger getInstance(){
		if(null!=instance)
			return instance;
		instance = new LogDataMerger();
		return instance;
	}
	
	public List<LogDataDay> getMergeQueryKeyword(String merge_table) {
		List<LogDataDay> lds = new ArrayList<LogDataDay>();
		int start = 0;
		int step = 10000;
		List<LogDataDay> lds_step = null;
		while (true) {
			lds_step = getMergeQueryKeyword(merge_table, start, step);
			if (null == lds_step || lds_step.size() < 1)
				break;
			lds.addAll(lds_step);
			start += step;
		}
		return lds;
	}

	public List<LogDataDay> getMergeQueryKeyword(String merge_table,
			int start, int limit) {
		String sql = "select keyword,query_count from " + merge_table
				+ " order by query_count desc limit " + start + "," + limit;
		List<LogDataDay> lds = new ArrayList<LogDataDay>();
		try {
			List list = sokuTopDataSource
					.queryForList(sql);
			Iterator it = list.iterator();
			Map map = null;
			String keyword = null;
			logger.info(sql);
			LogDataDay ld = null;
			while (it.hasNext()) {
				map = (Map) (it.next());
				keyword = (String) map.get("keyword");
				if (StringUtils.isBlank(keyword))
					continue;
				ld = new LogDataDay();
				ld.setKeyword(keyword.trim());
				ld.setQuery_count(DataFormat.parseInt(map.get("query_count")));
				lds.add(ld);
			}
		} catch (Exception e) {
			logger.error(sql, e);
		}
		return lds;
	}

	public List<LogDataDay> getMergeQueryKeywordByTorque(String merge_table) {
		List<LogDataDay> lds = new ArrayList<LogDataDay>();
		int start = 0;
		int step = 10000;
		List<LogDataDay> lds_step = null;
		while (true) {
			lds_step = getMergeQueryKeywordByTorque(merge_table, start, step);
			if (null == lds_step || lds_step.size() < 1)
				break;
			lds.addAll(lds_step);
			start += step;
		}
		return lds;
	}
	
	public List<LogDataDay> getMergeQueryKeywordByTorque(String merge_table,
			int start, int limit) {
		String sql = "select keyword,query_count from " + merge_table
				+ " order by query_count desc limit " + start + "," + limit;
		List<LogDataDay> lds = new ArrayList<LogDataDay>();
		try {
			List<Record> records = BasePeer.executeQuery(sql,"soku_top");
			logger.info(sql);
			LogDataDay ld = null;
			String keyword = null;
			for(Record r:records){
				keyword = r.getValue("keyword").asString();
				if (StringUtils.isBlank(keyword))
					continue;
				ld = new LogDataDay();
				ld.setKeyword(keyword);
				ld.setQuery_count(r.getValue("query_count").asInt());
				lds.add(ld);
			}
		} catch (Exception e) {
			logger.error(sql, e);
		}
		return lds;
	}
	
	public void mergeInsiteQueryKeyword(String table, String merge_table) {
		String sql = null;
		List<LogDataDay> lds = null;
		int start = 0;
		int step = 10000;
		while (true) {
			lds = getInsiteDayQueryKeywords(table, start, step);
			if (null == lds || lds.size() == 0) {
				logger.info("循环终止，end:" + start);
				break;
			}
			for (final LogDataDay ld : lds) {
				sql = "insert into "
						+ merge_table
						+ " (keyword,query_count,sub_keywords) values (?,?,?) ON DUPLICATE KEY UPDATE query_count=query_count+?,sub_keywords=concat(sub_keywords,?)";
				try {
					sokuTopDataSource.update(sql,
							new PreparedStatementSetter() {
								public void setValues(PreparedStatement ps)
										throws SQLException {
									ps.setString(1, ld.getKeyword());
									ps.setInt(2, ld.getQuery_count());
									ps.setString(3, "["+ld.getSrc_keyword()+":"+ld.getQuery_count()+"]");
									ps.setInt(4, ld.getQuery_count());
									ps.setString(5, "["+ld.getSrc_keyword()+":"+ld.getQuery_count()+"]");
								}
							});
				} catch (Exception e) {
					logger.error(e);
				}
			}
			start += step;
		}
	}

	private List<LogDataDay> getInsiteDayQueryKeywords(String table,
			int start, int end) {
		String sql = "select keyword,query_count from "
				+ table
				+ " where query_type='video' and source='youku' and query_count >15 order by query_count desc limit "
				+ start + "," + end;
		List<LogDataDay> lds = new ArrayList<LogDataDay>();
		try {
			List list = searchStatDataSource
					.queryForList(sql);
			Iterator it = list.iterator();
			Map map = null;
			String keyword = null;
			String srckeyword = null;
			logger.info(sql);
			LogDataDay ld = null;
			while (it.hasNext()) {
				map = (Map) (it.next());
				srckeyword = (String) map.get("keyword");
				if (StringUtils.isBlank(srckeyword))
					continue;
				keyword = KeywordUtil.wordFilterTopword(srckeyword);
				if (StringUtils.isBlank(keyword))
					continue;
				ld = new LogDataDay();
				ld.setKeyword(keyword);
				ld.setSrc_keyword(srckeyword);
				ld.setQuery_count(DataFormat.parseInt(map.get("query_count")));
				lds.add(ld);
				if (!srckeyword.equalsIgnoreCase(keyword))
					logger.info("table:" + table + ",src_keyword:" + srckeyword
							+ ",keyword:" + keyword);
			}
		} catch (Exception e) {
			logger.error(sql, e);
		}
		return lds;
	}

	public int execute(String sql) {
		try {
			sokuTopDataSource.execute(sql);
		} catch (Exception e) {
			logger.debug(sql, e);
		}
		return 0;
	}

	public static void main(String[] args) {
		
		try {
			LogUtil.init(Level.INFO,"/opt/log_analyze/merge/log/log.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String table = null;
		String merge_table = null;
		String delete_table = null;
		if (null != args) {
			if (args.length == 3) {
				if (args[0].matches("query_\\d{4}_\\d{2}_\\d{2}"))
					table = args[0];
				if (args[1].matches("merge_query_\\d{4}_\\d{2}_\\d{2}"))
					merge_table = args[1];
				if (args[2].matches("merge_query_\\d{4}_\\d{2}_\\d{2}"))
					delete_table = args[2];
			}
		}
		if (StringUtils.isBlank(table))
			table = "query_"
					+ DataFormat.formatDate(DataFormat.getNextDate(new Date(),
							-1), DataFormat.FMT_DATE_YYYY_MM_DD);
		if (StringUtils.isBlank(merge_table))
			merge_table = "merge_query_"
					+ DataFormat.formatDate(DataFormat.getNextDate(new Date(),
							-1), DataFormat.FMT_DATE_YYYY_MM_DD);

		if (StringUtils.isBlank(delete_table))
			delete_table = "merge_query_"
					+ DataFormat.formatDate(DataFormat.getNextDate(new Date(),
							-7), DataFormat.FMT_DATE_YYYY_MM_DD);
		
		logger.info("table:" + table + ",merge_table:" + merge_table+",delete_table:"+delete_table);

		LogDataMerger.getInstance().execute("create table if not exists " + merge_table
				+ " like merge_table_base");
		LogDataMerger.getInstance().execute("truncate table " + merge_table);
		
		if(!StringUtils.isBlank(delete_table)){
			logger.info("drop merge_table:" + delete_table);
			LogDataMerger.getInstance().execute("drop table if exists " + delete_table);
		}
		
		LogDataMerger.getInstance().mergeInsiteQueryKeyword(table, merge_table);

	}
}
