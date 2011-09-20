/**
 * 
 */
package com.youku.soku.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.Result;
import com.youku.soku.Query;
import com.youku.soku.index.query.VideoQueryManager;
import com.youku.soku.index.query.WordQueryManager;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author 1verge
 *
 */
public class SearchManager {
protected Log logger = LogFactory.getLog(SearchManager.class);
	
	private VideoQueryManager videoQueryManager = null;
	private WordQueryManager wordQueryManager = null;
	private SearchManager(){
		
	}
	private static SearchManager self = null;

	public static SearchManager getInstance(){
		
		if(self == null){
			self = new SearchManager();
			self.init();
		}
		return self;
	}
	private synchronized void init(){
		if (MyUtil.isIndexServer(Constant.QueryField.VIDEO)){
			videoQueryManager = VideoQueryManager.getInstance();
		}
		if (MyUtil.isIndexServer(Constant.QueryField.CORRECT)){
			wordQueryManager = WordQueryManager.getInstance();
		}
		
	}
	
	/**
	 * 搜索
	 * @param keywords
	 * @return
	 */
	
	
	public Object processQuery(Object obj){
		Result r = null;
		long start = System.currentTimeMillis();
		if (obj instanceof com.youku.soku.Query)
		{
			com.youku.soku.Query query = (com.youku.soku.Query)obj;
			logger.debug("RECEIVED QUERY:"+query);
			
			switch(query.field)
			{
				//视频
				case Constant.QueryField.VIDEO:
				{
					r = videoQueryManager.query(query);
					break;
				}
				case Constant.QueryField.CORRECT:
				{
					r = wordQueryManager.correct(query.keywords);
					break;
				}
				case Constant.QueryField.LIKE:
				{
					r = wordQueryManager.fuzzyQuery(query.keywords,query.start,query.end);
					break;
				}
				case Constant.QueryField.LIBRARY:
				{
					
				}
			}
		}
		else
		{
			logger.error("ERROR:RECEIVED UNKOWN QUERY");
		}
		if (r == null)
			r = new Result();
		
		r.timecost = (int)(System.currentTimeMillis() - start);

		if (r.timecost>=1000)
			logger.warn("QUERY WAINING:use long time:"+r.timecost+",queryObject:"+((Query)obj).toString());
		
		logger.debug("cost:"+r.timecost);
		logger.debug("RESULT COUNT:"+r.totalCount);
		return r;
	}
	
	public VideoQueryManager getVideoQueryManager() {
		return videoQueryManager;
	}
	public WordQueryManager getWordQueryManager() {
		return wordQueryManager;
	}
}
