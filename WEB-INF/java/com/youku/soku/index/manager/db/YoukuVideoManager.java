package com.youku.soku.index.manager.db;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
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

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import com.youku.search.index.db.MappingManager;
import com.youku.search.util.DataFormat;
import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.analyzer.WholeWord;
import com.youku.soku.index.analyzer.WordProcessor;
import com.youku.soku.index.om.Domain;
import com.youku.soku.index.om.Site;
import com.youku.soku.sort.boost.VideoBoost;
import com.youku.soku.util.DataBase;
import com.youku.soku.util.MyUtil;
import com.youku.soku.web.util.WebUtil;


public class YoukuVideoManager extends BaseVideoManager{
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public final static int site_id = 14;
	private static final float norm = 26491846f/36330000;   //优酷有效视频比例
	
	private static YoukuVideoManager self = null;

	public static YoukuVideoManager getInstance(){
		
		if(self == null){
				self = new YoukuVideoManager();
		}
		return self;
	}
	
	public List<AssembleDoc> getVideos(int start,int limit,Site site)
	{
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where pk_video>="+start +" and is_valid=1 and progress_flag=1  order by pk_video limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			list = BasePeer.executeQuery(sql,false,conn);
			if (list!=null)
			{
				for (int i=0;i<list.size();i++)
				{
					Record record = list.get(i);
					AssembleDoc doc = RsToDocument(site_id,(float)site.getWeight(),record);
					if (doc != null)
						result.add(doc);
				}
			}
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return result;
	}
	
	public List<AssembleDoc> getVideos(Date startTime,Date endTime,int limit ,Site site)
	{
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		List<Record> list = null;
		String sql = "select * from t_video where is_valid=1 and createtime>='"+start+"' and createtime<'"+ end +"' order by pk_video limit "+ limit;
		_log.info(sql);
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			list = BasePeer.executeQuery(sql,false,conn);
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
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return result;
	}
	
	public List<AssembleDoc> getVideos(int start,int limit,Date endTime,Site site)
	{
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and pk_video>="+start +" and createtime<'"+ end +"' order by pk_video"+" limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			list = BasePeer.executeQuery(sql,false,conn);
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
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return result;
	}
	
	public int getTotalVideoCount()
	{
		int total = 0;
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			List<Record> onesite = BasePeer.executeQuery("select max(pk_video) as totalCount from t_video",false,conn);
			if (onesite!= null && onesite.size()>0)
			{
				total = onesite.get(0).getValue("totalCount").asInt();
				total = Math.round(total * norm);
			}
		} catch (TorqueException e) {
			e.printStackTrace();
		} catch (DataSetException e) {
			e.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		_log.info("youku video size=" + total);
		return total;
	}
	
	public Record getVideo(int id,Site site)
	{
		String sql = "select * from t_video where pk_video=" + id;
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			List list = BasePeer.executeQuery(sql,false,conn);
			if (list!= null && list.size() > 0)
			{
				Record record = (Record)list.get(0);
				return record;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return null;
	}
	
	public List<AssembleDoc> getVideosDesc(int end,int limit,Site site)
	{
		List<AssembleDoc> result = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where "+ (end>0?("pk_video<"+end +" and "):"") +" is_valid=1 and progress_flag=1  order by pk_video desc limit "+ limit;
		_log.info(sql);
		List<Record> list = null;
		Connection conn = null;
		try {
			conn = DataBase.getYoukuConnection();
			list = BasePeer.executeQuery(sql,false,conn);
			if (list!=null)
			{
				for (int i=0;i<list.size();i++)
				{
					Record record = list.get(i);
					AssembleDoc doc = RsToDocument(site_id,(float)site.getWeight(),record);
					if (doc != null)
						result.add(doc);
				}
			}
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		finally
		{
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return result;
	}
	
	public AssembleDoc getVideoAsDocument(int id,Site site) {
		Record record = getVideo(id,site);
		if (record != null)
		{
			return RsToDocument(site_id,0.0f,record);
		}
		return null;
	}
	
	private AssembleDoc RsToDocument(int site,float weight,Record rs) 
	{
		
		Document doc = new Document();
		try
		{
			int id = rs.getValue("pk_video").asInt();
			String url = getUrl(id);
			List<Domain> domains = DomainManager.getInstance().getDomainsByUrlWithInsert(site,url);
			
			if (domains == null || domains.size()==0){
				return null;
			}
			
			doc.add(new Field("id", site+"_"+id, Store.NO, Index.UN_TOKENIZED));
			
			String title = MyUtil.getString(rs.getValue("title").asString());
			
			doc.add(new Field("title_store",WebUtil.formatHtml(title), Store.YES, Index.NO));
	        WholeWord tokens_title = WordProcessor.getWholeWord(title);
	        TokenStream ts = AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(title),tokens_title);
	        Field f_title_index = new Field("title", ts,Field.TermVector.WITH_POSITIONS_OFFSETS);
	        doc.add(f_title_index);
	        ts.close();
	        
	        Date uploadTime = rs.getValue("createtime").asUtilDate();
	        if (uploadTime!= null){
	        	doc.add(new Field("uploadTime", String.valueOf(uploadTime.getTime()/1000), Store.YES, Index.NO));
	        	doc.add(new Field("create_day",DataFormat.formatDate(uploadTime,DataFormat.FMT_DATE_SPECIAL), Store.NO, Index.UN_TOKENIZED));
	        }
	        String logo = rs.getValue("thumb0").asString();
	        if (logo ==null || logo.length() == 0 || logo.equals("DEFAULT"))
	        	logo = rs.getValue("thumb4").asString();
	        
	        doc.add(new Field("logo",logo, Store.YES, Index.NO));
	        float seconds = DataFormat.parseFloat(rs.getValue("seconds").asString(),0f);
	        doc.add(new Field("seconds",seconds+"", Store.YES, Index.UN_TOKENIZED));
//	        doc.add(new Field("timelength_index", Constant.TimeLength.getLessIndex(seconds), Store.NO, Index.TOKENIZED));
	        
	        //组织tag和分类
	        
	        Connection mappingConn = DataBase.getYoukuMappingConnection();
	        String tags = null;
	        String cate = null;
	        try{
		         tags = MappingManager.getInstance().getTags(id,"VIDEO",mappingConn);
		         cate =MappingManager.getInstance().getCategory(id,"VIDEO",mappingConn);
	        }finally
	        {
	        	if (mappingConn!=null )mappingConn.close();
	        }
	        StringBuffer sb = new StringBuffer();
			if (tags!=null && !tags.isEmpty()){
				sb.append(tags);
			}
			
			if (cate!=null && !cate.isEmpty())
				sb.append(",").append(YoukuCateManager.getInstance().getName(DataFormat.parseInt(cate)));
			
			if (sb.length() > 0)
			{
				Field f_tags = new Field("tags", WebUtil.formatHtml(sb.toString()),Field.Store.YES,Field.Index.NO);
		        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(sb.toString().replaceAll(","," "))),Field.TermVector.WITH_POSITIONS_OFFSETS);
		        
		        doc.add(f_tags);
		        doc.add(f_tags_index);
			}
			
	        doc.add(new Field("url",getUrlNoDomain(url), Store.YES, Index.NO));
	        
	        int streamtypes = rs.getValue("streamtypes").asInt();
	        if (com.youku.search.util.MyUtil.contains(0x08, streamtypes))
	        {
	        	doc.add(new Field("hd","1", Store.YES, Index.UN_TOKENIZED));
	        }
	        doc.add(new Field("site",site+"", Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("vid", id+"", Store.YES, Index.NO));
	       
	        //组织域名
	        StringBuffer dms = new StringBuffer();
	        for(Domain dm:domains){
	        	dms.append(dm.getId()).append(" ");
	        }
	        doc.add(new Field("domain",domains.get(0).getId()+"", Store.YES, Index.NO));
	        
	        Field domain_index = new Field("domain_index", AnalyzerManager.getBlankAnalyzer().tokenStream("domain_index",new StringReader(dms.substring(0,dms.length()-1))));
	        doc.add(domain_index);
	        
	        float boost = (float) VideoBoost.getBoost(seconds).all();
	        if(boost < 0.1)
	        	boost = 0f;
//	        AssembleDoc result = new AssembleDoc(doc,boost);
	        
	        return null;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	
	}
	
	private String getUrl(int vid)
	{
		return "http://v.youku.com/v_show/id_"+ MyUtil.encodeVideoId(vid) +".html";
	}
	
}
