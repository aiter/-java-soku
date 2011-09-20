/**
 * 
 */
package com.youku.search.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.index.entity.AdvanceQuery;
import com.youku.search.index.entity.Result;
import com.youku.search.index.entity.SpecialQuery;
import com.youku.search.index.query.BarQueryManager;
import com.youku.search.index.query.BarpostQueryManager;
import com.youku.search.index.query.FolderQueryManager;
import com.youku.search.index.query.PkQueryManager;
import com.youku.search.index.query.StatQueryManager;
import com.youku.search.index.query.UserQueryManager;
import com.youku.search.index.query.VideoQueryManager;
import com.youku.search.util.Constant;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class SearchManager {
	protected Log logger = LogFactory.getLog(SearchManager.class);
	
	private VideoQueryManager videoQueryManager = null;
	private FolderQueryManager folderQueryManager = null;
	private UserQueryManager userQueryManager = null;
	private BarpostQueryManager barpostQueryManager = null;
	private PkQueryManager pkQueryManager = null;
	private StatQueryManager statQueryManager = null;
	private BarQueryManager barQueryManager = null;
	
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
		if (MyUtil.isIndexServer(Constant.QueryField.FOLDER)){
			folderQueryManager =  FolderQueryManager.getInstance();
		}
		if (MyUtil.isIndexServer(Constant.QueryField.MEMBER)){
			userQueryManager = UserQueryManager.getInstance();
		}
		if (MyUtil.isIndexServer(Constant.QueryField.BARPOST_SUBJECT)){
			barpostQueryManager = BarpostQueryManager.getInstance();
		}
		if (MyUtil.isIndexServer(Constant.QueryField.PK)){
			pkQueryManager = PkQueryManager.getInstance();
		}
		if (MyUtil.isIndexServer(Constant.QueryField.STAT)){
			statQueryManager = StatQueryManager.getInstance();
		}
		
		if (MyUtil.isIndexServer(Constant.QueryField.BAR)){
			barQueryManager = BarQueryManager.getInstance();
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
		if (obj instanceof com.youku.search.index.entity.Query)
		{
			com.youku.search.index.entity.Query query = (com.youku.search.index.entity.Query)obj;
			logger.debug("RECEIVED QUERY:"+query);
			
			switch(query.field)
			{
				//视频
				case Constant.QueryField.VIDEO:
				{
					r = videoQueryManager.query(query);
					break;
				}
				case Constant.QueryField.STAT_PINYIN:
				{
					r = statQueryManager.correct(query.keywords,query.category);
					break;
				}
				case Constant.QueryField.STAT_KEYWORD:
				{
					r = statQueryManager.fuzzyQuery(query.keywords,query.category,query.start,query.end);
					break;
				}
				//专辑搜索
				case Constant.QueryField.FOLDER:
				{
					r = folderQueryManager.query(query);
					break;
				}
				//铃声搜索  已经不用
				case Constant.QueryField.RING:
				{
//					if (query.needAnalyze){
//						String keywords = AnalyzerManager.analyzeWord(query.keywords);
//						if (keywords == null || keywords.length()==0)
//							r = new Result();
//						else
//							r = ringQueryManager.query(keywords,query.sort,query.reverse,query.start,query.end);
//					}
//					else{
//						r = ringQueryManager.query(query.keywords,query.sort,query.reverse,query.start,query.end);
//					}
					break;
				}
				//会员搜索
				case Constant.QueryField.MEMBER:
				{
					if (query.needAnalyze){
						String  keywords = MyUtil.analyzeWord(true,query.keywords);
						if (keywords == null || keywords.length()==0)
							r = new Result();
						else
							r = userQueryManager.query(keywords,query.sort,query.reverse,query.start,query.end);
					}
					else{
						r = userQueryManager.query(query.keywords,query.sort,query.reverse,query.start,query.end);
					}
//					System.out.println("r.totalCount="+r.totalCount+"\tr.results.size()="+r.results.size());
					break;
				}
				  //看吧主题按标题搜索
				case Constant.QueryField.BARPOST_SUBJECT:
				{
					if (query.needAnalyze){
						String keywords = AnalyzerManager.analyzeWord(query.keywords);
						if (keywords == null || keywords.length()==0)
							r = new Result();
						else
							r = barpostQueryManager.querySubject(keywords,query.sort,query.reverse,query.start,query.end);
					}
					else{
						r = barpostQueryManager.querySubject(query.keywords,query.sort,query.reverse,query.start,query.end);
					}
					break;
				}
				 //看吧主题按作者搜索
				case Constant.QueryField.BARPOST_AUTHOR:
				{
					r = barpostQueryManager.queryAuthor(query.keywords,query.sort,query.reverse,query.start,query.end);
					break;
				}
				 //PK搜索
				case Constant.QueryField.PK:
				{
					if (query.needAnalyze){
						String keywords = AnalyzerManager.analyzeWord(query.keywords);
						if (keywords == null || keywords.length()==0)
							r = new Result();
						else
							r = pkQueryManager.query(keywords,query.sort,query.reverse,query.start,query.end);
					}
					else{
						r = pkQueryManager.query(query.keywords,query.sort,query.reverse,query.start,query.end);
					}
					break;
				}
				//Video tag搜索
				case Constant.QueryField.VIDEOTAG:
				{
					r = videoQueryManager.queryTag(query);
					break;
				}
//				//video tagid搜索
//				case Constant.QueryField.VIDEOTAGID:
//				{
//					r = videoQueryManager.queryTagId(query.keywords,query.category,query.sort,query.reverse,query.start,query.end);
//					break;
//				}
				//Folder tag搜索
				case Constant.QueryField.FOLDERTAG:
				{
					r = folderQueryManager.queryTag(query.keywords,query.category,query.sort,query.reverse,query.start,query.end);
					break;
				}
				//pk tag 搜索
				case Constant.QueryField.PKTAG:
				{
					r = pkQueryManager.queryTag(query.keywords,query.sort,query.reverse,query.start,query.end);
					break;
				}
				
				case Constant.QueryField.BAR:
				{
					if (query.needAnalyze){
						String keywords = AnalyzerManager.analyzeWord(query.keywords);
						if (keywords == null || keywords.length()==0)
							r = new Result();
						else
							r = barQueryManager.query(keywords,query.start,query.end);
					}
					break;
				}
			}
		}
		else if (obj instanceof AdvanceQuery)
		{
			AdvanceQuery query = (AdvanceQuery)obj;
			logger.debug("RECEIVED ADVANCEQUERY:"+query);
//			logger.info(query);
			switch(query.field)
			{
				//视频
				case Constant.QueryField.VIDEO:
				{
					r = videoQueryManager.advanceQuery(query);
					break;
				}
				//专辑搜索
				case Constant.QueryField.FOLDER:
				{
					r = folderQueryManager.advanceQuery(query);
					break;
				}
			}
		}
		else if (obj instanceof SpecialQuery)
		{
			SpecialQuery query = (SpecialQuery)obj;
			logger.debug("RECEIVED SpecailQuery:"+query);
			switch(query.field)
			{
				//视频
				case Constant.QueryField.VIDEO_TITLE_TAG:
				{
					r = videoQueryManager.queryTitleAndTag(query);
					break;
				}
				//专辑
				case Constant.QueryField.FOLDER_TITLE_TAG:
				{
					r = folderQueryManager.queryTitleAndTag(query);
					break;
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
			logger.warn("QUERY WAINING:use long time:"+r.timecost);
//		else
//			logger.info("QUERY cose:"+r.timecost);
//		System.out.println("result==null:"+r==null);
		return r;
	}
	public BarpostQueryManager getBarpostQueryManager() {
		return barpostQueryManager;
	}
	public FolderQueryManager getFolderQueryManager() {
		return folderQueryManager;
	}
	public PkQueryManager getPkQueryManager() {
		return pkQueryManager;
	}
	public StatQueryManager getStatQueryManager() {
		return statQueryManager;
	}
	public UserQueryManager getUserQueryManager() {
		return userQueryManager;
	}
	public VideoQueryManager getVideoQueryManager() {
		return videoQueryManager;
	}
	public BarQueryManager getBarQueryManager() {
		return barQueryManager;
	}
}
