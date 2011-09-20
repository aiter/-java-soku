/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.analyzer.WholeWord;
import com.youku.soku.index.analyzer.WordProcessor;
import com.youku.soku.index.om.Domain;
import com.youku.soku.index.om.Site;
import com.youku.soku.sort.boost.VideoBoost;
import com.youku.soku.web.util.WebUtil;

/**
 * @author 1verge
 *
 */
public class VideoManager extends BaseVideoManager{

	final static String STATE_OK = "create";
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	private static VideoManager self = null;

	public static VideoManager getInstance(){
		
		if(self == null){
				self = new VideoManager();
		}
		return self;
	}
	
	@SuppressWarnings("unchecked")
	public List<AssembleDoc> getVideos(int start,int limit,Site site)
	{
		
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from "+ site.getTablename() +" where id>="+start +" order by id"+" limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		try {
			list = BasePeer.executeQuery(sql);
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				Record record = list.get(i);
				AssembleDoc doc = RsToDocument(site.getId(),(float)site.getWeight(),record);
				if (doc != null)
					result.add(doc);
			}
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<AssembleDoc> getVideos(Date startTime,Date endTime,int limit ,Site site)
	{
		
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		List<Record> list = null;
		String sql = "select * from "+ site.getTablename() +" where createTime>='"+start+"' and createTime<'"+ end +"'  order by id limit "+limit;
		_log.info(sql);
		try {
			list = BasePeer.executeQuery(sql);
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				Record record = list.get(i);
				AssembleDoc doc = RsToDocument(site.getId(),(float)site.getWeight(),record);
				
				if (doc != null)
					result.add(doc);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<AssembleDoc> getVideos(int start,int limit,Date endTime,Site site)
	{
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from "+ site.getTablename() +" where id>="+start +" and createTime<'"+ end +"'  order by id"+" limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		try {
			list = BasePeer.executeQuery(sql);
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				Record record = list.get(i);
				AssembleDoc doc = RsToDocument(site.getId(),(float)site.getWeight(),record);
				if (doc != null)
					result.add(doc);
			}
		}
		return result;
	}
	
	/**
	 * 获取视频总数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int getTotalVideoCount()
	{
		int total = 0;
		List<Site> sites = SiteManager.getInstance().getValidSiteList();
		for (Site site:sites)
		{
			try {
				_log.info("select count(id) as totalCount from "+ site.getTablename() + " where state='create'");
				List<Record> onesite = BasePeer.executeQuery("select count(id) as totalCount from "+ site.getTablename() + " where state='create'");
				if (onesite!= null && onesite.size()>0)
				{
					int thecount  = onesite.get(0).getValue("totalCount").asInt() ;
					_log.info(site.getTablename() + "=" + thecount);
					total += thecount;
				}
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
		_log.info("other site video size=" + total);
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public Record getVideo(int id,Site site)
	{
		String sql = "select * from "+ site.getTablename() +" where id="+id;
		List<Record> list = null;
		try {
			list = BasePeer.executeQuery(sql);
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AssembleDoc> getVideosDesc(int end,int limit,Site site)
	{
		
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from "+ site.getTablename() + (end>0?(" where id<"+end ):"") +" order by id desc"+" limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		try {
			list = BasePeer.executeQuery(sql);
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		if (list!=null)
		{
			for (int i=0;i<list.size();i++)
			{
				Record record = list.get(i);
				AssembleDoc doc = RsToDocument(site.getId(),(float)site.getWeight(),record);
				result.add(doc);
			}
		}
		return result;
	}
	
	public AssembleDoc getVideoAsDocument(int id,Site site)
	{
		Record record = getVideo(id,site);
		if (record !=null)
		{
			return RsToDocument(site.getId(),(float)site.getWeight(),record);
		}
		return null;
	}
	
	private AssembleDoc RsToDocument(int site,float weight,Record rs) 
	{
		AssembleDoc assembleDoc = new AssembleDoc();
		try
		{
			int id = rs.getValue("id").asInt();
			String state = rs.getValue("state").asString();
			
			assembleDoc.setId(id);
			
			if(state == null || !STATE_OK.equals(state)) return assembleDoc;
			
			
			String url = rs.getValue("url").asString();
			List<Domain> domains = DomainManager.getInstance().getDomainsByUrlWithInsert(site,url);
			
			if (domains == null || domains.size()==0){
				return assembleDoc;
			}
			
			Document doc = new Document();
			
			String title = rs.getValue("title").asString();
			if (title == null || title.length()==0){
				_log.error("site:(" + site + ")\t vid(" + rs.getValue("id").asString() + ").title = null");
				return assembleDoc;
			}
			
			doc.add(new Field("title_store",WebUtil.formatHtml(title), Store.YES, Index.NO));
	        WholeWord tokens_title = WordProcessor.getWholeWord(title);
	        TokenStream ts = AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(title),tokens_title);
	        Field f_title_index = new Field("title",ts ,Field.TermVector.WITH_POSITIONS_OFFSETS);
	        doc.add(f_title_index);
	        ts.close();
	        
	        Date uploadTime = rs.getValue("uploadTime").asUtilDate();
	        if (uploadTime == null)
	        	uploadTime = rs.getValue("createTime").asUtilDate();
	        
	        if (uploadTime!= null){
	        	doc.add(new Field("uploadTime", String.valueOf(uploadTime.getTime()/1000), Store.YES, Index.NO));
	        	doc.add(new Field("create_day",DataFormat.formatDate(uploadTime,DataFormat.FMT_DATE_SPECIAL), Store.NO, Index.UN_TOKENIZED));
	        }
	        
	        String logo = rs.getValue("picYouku").asString();
	        if (logo == null || logo.isEmpty() || logo.equals("NA")){
	        	logo = "0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80";
	        }
	        else{
	        	if (logo.startsWith("http")){
	        		logo = logo.substring(logo.lastIndexOf("/")+1,logo.length());
	        	}
	        }
	        doc.add(new Field("logo", logo , Store.YES, Index.NO));
	        
	        int seconds = rs.getValue("timeLength").asInt();
	        doc.add(new Field("seconds",seconds+"", Store.YES, Index.UN_TOKENIZED));
	        
	        //组织tag和分类
	        String tags = rs.getValue("tag").asString();
	        String cate = rs.getValue("channel").asString();
	        
	        StringBuffer sb = new StringBuffer();
			if (tags!=null && tags.trim().length() > 0)
				sb.append(tags);
			
			if (cate!=null && cate.trim().length() > 0)
				sb.append(",").append(cate);
			
			if (sb.length() > 0)
			{
				Field f_tags = new Field("tags", WebUtil.formatHtml(sb.toString()),Field.Store.YES,Field.Index.NO);
		        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(sb.toString().replaceAll(","," "))),Field.TermVector.WITH_POSITIONS_OFFSETS);
		        
		        doc.add(f_tags);
		        doc.add(f_tags_index);
			}
			
	        doc.add(new Field("url",getUrlNoDomain(url), Store.YES, Index.NO));
	        int hd = rs.getValue("hd").asInt();
	        if (hd >0)
	        	doc.add(new Field("hd",hd+"", Store.YES, Index.UN_TOKENIZED));
	        
	        
	        doc.add(new Field("vid", rs.getValue("id").asString(), Store.YES, Index.NO));
	       
	        int trueSite = 0;
	        //组织域名
	        StringBuffer dms = new StringBuffer();
	        for(Domain dm:domains){
	        	trueSite = dm.getSiteId();
	        	dms.append(dm.getId()).append(" ");
	        }
	        doc.add(new Field("domain",domains.get(0).getId()+"", Store.YES, Index.NO));
	        doc.add(new Field("site",String.valueOf(trueSite), Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("id", trueSite+"_"+rs.getValue("id").asString(), Store.NO, Index.UN_TOKENIZED));
	        
	        Field domain_index = new Field("domain_index", AnalyzerManager.getBlankAnalyzer().tokenStream("domain_index",new StringReader(dms.substring(0,dms.length()-1))));
	        doc.add(domain_index);
	        
	        float boost = (float) VideoBoost.getBoost(seconds).all();
	        if(boost < 0.1)
	        	boost = 0f;
	        
	        assembleDoc.setDoc(doc);
	        assembleDoc.setBoost(boost);
	        
	        return assembleDoc;
		}catch(Exception e)
		{
			_log.error(e.getMessage(),e);
			return null;
		}
	}
}
