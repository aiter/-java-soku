package com.youku.top.paihangbang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.youku.soku.library.Utils;
import com.youku.top.paihangbang.entity.StopWords;

public class StopWordsGetter {
	static Logger logger = Logger.getLogger(StopWordsGetter.class);
	private static StopWordsGetter instance = null;
	
	public static synchronized StopWordsGetter getInstance() {
		if(null==instance)
			instance = new StopWordsGetter();
		return instance;
	}
	
	public Map<Integer,StopWords> doGetChennelStopWords(){
		Map<Integer,StopWords> map = new HashMap<Integer,StopWords>();
		try{
		String sql="select cate,blocked_words,deleted_words from channel_stop_words";
		List<StopWords> list = TopDateMgt.newSokuTopDataSource.query(sql,new RowMapper(){
			@Override
			public StopWords mapRow(ResultSet rs, int arg1) throws SQLException {
				StopWords sw = new StopWords();
				sw.setCate(rs.getInt("cate"));
				String stops_str = rs.getString("blocked_words");
				if(!StringUtils.isBlank(stops_str)){
					List<String> stops = Utils.parseStr2List(stops_str, "\\|");
					if(null!=stops){
						sw.getBlocked_words().addAll(stops);
					}
				}
				String deletes_str = rs.getString("deleted_words");
				if(!StringUtils.isBlank(deletes_str)){
					List<String> deletes = Utils.parseStr2List(deletes_str, "\\|");
					if(null!=deletes){
						sw.getDeleted_words().addAll(deletes);
					}
				}
				
				return sw;
			}});
		
		for(StopWords sw:list){
			map.put(sw.getCate(), sw);
		}
		}catch(Exception e){
			logger.error(e);
		}
		return map;
	}
}
