package com.youku.top.paihangbang;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.top.data_source.SokuDataSource;

public class MajorTermGetter {
	
	static Logger logger = Logger.getLogger(MajorTermGetter.class);
	public static JdbcTemplate sokuDataSource = new JdbcTemplate(
			SokuDataSource.INSTANCE);
	
	public static Set<String> getMajorTermKeywords(){
		Set<String> keywords = new HashSet<String>();
		String sql="select keyword from major_term where status=1";
		try{
			List list = sokuDataSource.queryForList(sql);
			Iterator<?> iterator = list.iterator();
			Map map = null;
			while(iterator.hasNext()){
				map = (Map) iterator.next();
				keywords.add(String.valueOf(map.get("keyword")).trim());
			}
		}catch(Exception e){
			logger.error("sql:"+sql, e);
		}
		return keywords;
	}
	
}
