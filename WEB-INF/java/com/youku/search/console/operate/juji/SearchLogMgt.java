package com.youku.search.console.operate.juji;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.SearchLog;
import com.youku.search.console.pojo.SearchLogPeer;
import com.youku.search.util.DataFormat;

public class SearchLogMgt {
static Log logger = LogFactory.getLog(SearchLogMgt.class);
	
	private SearchLogMgt(){}
	private static SearchLogMgt instance = null;
	
	public static synchronized SearchLogMgt getInstance() {
		if(null!=instance)
			return instance;
		else{
			instance = new SearchLogMgt();
			return instance;
		} 
	}
	
	public void executeSql(String sql){
		try {
			SearchLogPeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void insert(String words,String url,int stop){
		SearchLog sl = new SearchLog();
		sl.setWords(words);
		sl.setUrl(url);
		sl.setStop(stop);
		sl.setSearchTime(DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD));
		try {
			sl.save();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public int getSearchLogNum(String search_time,int stop){
		Criteria criteria = new Criteria();
		criteria.add(SearchLogPeer.SEARCH_TIME,search_time);
		if(stop>0){
			criteria.add(SearchLogPeer.STOP,stop);
		}
		try {
			List<SearchLog> sls = SearchLogPeer.doSelect(criteria);
			if(null!=sls)
				return sls.size();
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
}
