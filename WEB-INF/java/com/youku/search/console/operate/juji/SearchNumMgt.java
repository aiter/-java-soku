package com.youku.search.console.operate.juji;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.SearchNum;
import com.youku.search.console.pojo.SearchNumPeer;

public class SearchNumMgt {
static Log logger = LogFactory.getLog(SearchNumMgt.class);
	
	private SearchNumMgt(){}
	private static SearchNumMgt instance = null;
	
	public static synchronized SearchNumMgt getInstance() {
		if(null!=instance)
			return instance;
		else{
			instance = new SearchNumMgt();
			return instance;
		} 
	}
	
	public void executeSql(String sql){
		try {
			SearchNumPeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public SearchNum getSearchNumByWords(String words){
		Criteria criteria = new Criteria();
		criteria.add(SearchNumPeer.WORDS,words);
		List<SearchNum> rs;
		try {
			rs = SearchNumPeer.doSelect(criteria);
			if(null!=rs&&rs.size()>0) return rs.get(0);
		} catch (Exception e) {
			logger.error(e);
		}
		
		return null;
	}
}
