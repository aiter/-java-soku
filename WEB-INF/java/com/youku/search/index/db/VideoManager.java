/**
 * 
 */
package com.youku.search.index.db;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.analyzer.WholeWord;
import com.youku.search.analyzer.WordProcessor;
import com.youku.search.index.TimeLength;
import com.youku.search.index.db.LastVvManager.Vv;
import com.youku.search.index.entity.Video;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;
import com.youku.search.util.boost.VideoBoost_Test;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class VideoManager {
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static VideoManager instance = null;
	
	public static VideoManager getInstance()
	{
		if ( null == instance ){
			instance = new VideoManager();
		}
		return instance;
	}
	
	private VideoManager(){
		
	}
	
	private Random r = new Random();
	public int getMaxId() throws TorqueException
	{
		int result = 0;
		Connection conn = Database.getYoqooConnection();
		String sql = "select max(pk_video) as pk_video from t_video";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_video");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn!= null)conn.close();
			} catch (SQLException e) {
			}
		}
		
		return result;
	}
	
	public List<AssembleDoc> getVideos(int start,int end) throws TorqueException
	{
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and progress_flag=1 and pk_video>="+start+" and pk_video<"+end+" order by pk_video";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = RsToDocument(rs);

				if (doc!=null){
					list.add(doc);
				}
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败",e.getMessage()+"\n" + sql);
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			throw e;
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	public List<Document> getInvalidVideos(int start,int end)
	{
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		String sql = "select * from t_video where is_valid=0 and pk_video>="+start+" and pk_video<"+end+" order by pk_video desc";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = new Document();
				
				Date createtime = rs.getTimestamp("createtime");
				int vid = rs.getInt("pk_video");
				String title = MyUtil.getString(rs.getBytes("title"));
				String memo = MyUtil.getString(rs.getBytes("memo"));
				if (title == null ) return null;
		        
				doc.add(new Field("title",title, Store.YES, Index.TOKENIZED));
		        
		        if (memo != null && memo.trim().length() > 0)
		        {
		        	if (memo.length() > 100)
		        		memo = memo.substring(0,100);
		        	 WholeWord tokens_memo = WordProcessor.getWholeWord(memo);
		        	
		        	doc.add(new Field("memo", AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(memo),tokens_memo),Field.TermVector.WITH_POSITIONS_OFFSETS));
		        }
		        	
		        
		        doc.add(new Field("vid", vid+"", Store.YES, Index.UN_TOKENIZED));
		        doc.add(new Field("file_id", rs.getString("file_id"), Store.YES, Index.NO));
		        
		        doc.add(new Field("size", MyUtil.formatString(rs.getString("size")), Store.YES, Index.NO));
		        doc.add(new Field("thumb0", MyUtil.formatString(rs.getString("thumb0")), Store.YES, Index.NO));
		        doc.add(new Field("thumb3", MyUtil.formatString(rs.getString("thumb3")), Store.YES, Index.NO));
		        doc.add(new Field("thumb6", MyUtil.formatString(rs.getString("thumb6")), Store.YES, Index.NO));
		        
		        float  seconds = rs.getFloat("seconds");
		        doc.add(new Field("seconds",seconds+"", Store.YES, Index.UN_TOKENIZED));
		        
		        doc.add(new Field("createtime", createtime.getTime()+"", Store.YES, Index.NO));
				list.add(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	public List<AssembleDoc> getVideos(int[] vids) throws TorqueException
	{
		if (vids == null || vids.length == 0)
			return null;
		
		StringBuffer value = new StringBuffer();
		for (int i=0;i<vids.length;i++)
		{
			value.append(vids[i]+",");
		}
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and progress_flag=1 and pk_video in ("+ value.substring(0,value.length()-1) +") order by pk_video";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = RsToDocument(rs);
				if (doc!=null)list.add(doc);
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败",e.getMessage()+"\n" + sql);
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			throw e;
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();  
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public List<AssembleDoc> getVideos(int start,Date endTime,int limit) throws TorqueException
	{
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and pk_video>="+start+" and createtime<'"+ end +"' order by pk_video limit "+ limit;
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = RsToDocument(rs);
				if (doc != null)
					list.add(doc);
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败",e.getMessage()+"\n" + sql);
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			throw e;
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public List<Integer> getLastVideoIds(Date startTime,int limit) throws TorqueException
	{
		String date = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<Integer> list = new ArrayList<Integer>();
		String sql = "select pk_video from t_video where createtime>'"+ date +"' limit "+limit;
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				list.add(rs.getInt("pk_video"));
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败",e.getMessage()+"\n" + sql);
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			throw e;
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public List<AssembleDoc> getVideos(int minVideo,int maxVideo,Date startTime,Date endTime) throws TorqueException
	{
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and pk_video>="+minVideo+" and pk_video<"+maxVideo+" and createtime>='" + start + "' and createtime<'"+ end +"' order by pk_video";
		_log.info(sql);
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = RsToDocument(rs);
				if (doc != null)
					list.add(doc);
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败",e.getMessage()+"\n" + sql);
			} catch (Exception e2) {
			}
			_log.error(e.getMessage(),e);
			throw e;
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public AssembleDoc getVideoAsDocument(int vid)
	{
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select * from t_video where is_valid=1 and pk_video="+vid;

		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				return RsToDocument(rs);
			}
		}
		catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
		finally
		{
			try {
		        if (rs != null)	rs.close();
		        if (st != null)st.close();
		        if (conn != null)conn.close();
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	private AssembleDoc RsToDocument(ResultSet rs)
	{
		Document doc = new Document();
		try
		{
			int owner = rs.getInt("owner");
			if (UserManager.getInstance().isLimit(owner))//如果用户被限制，则不被搜索出
			{
				_log.info("owner is limit:"+owner);
				return null;
			}
			int vid = rs.getInt("pk_video");
			
			//查是否屏蔽
	        int rate_total  = rs.getInt("rate_total");
	        if (MyUtil.contains(0x4 ,rate_total)){
	        	_log.info("video is limit:"+vid);
				return null;
	        }
			
//			long time1 = System.currentTimeMillis();
			Date createtime = rs.getTimestamp("createtime");
			
			int total_comment = rs.getInt("total_comment");
			int total_fav =  rs.getInt("total_fav");
			String title = MyUtil.getString(rs.getBytes("title"));
			String memo = MyUtil.getString(rs.getBytes("memo"));
			
			if (title == null ) return null;
	        
			doc.add(new Field("title_store",title, Store.YES, Index.NO));
			
			//加入同义词
			String synonym = WordProcessor.getSynonym(title);
			String newTitle = synonym != null? title+synonym : title ;
				
	        WholeWord tokens_title = WordProcessor.getWholeWord(newTitle);

	        TokenStream ts = AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(newTitle),tokens_title);
	        
	        Field f_title_index = new Field("title",ts ,Field.TermVector.WITH_POSITIONS_OFFSETS);
	        doc.add(f_title_index);
	        
	        
//			String k = WordProcessor.formatTeleplayName(title);
				
//	        doc.add(new Field("title_index_full",k, Store.NO, Index.UN_TOKENIZED));
	        
	        if (memo != null && memo.trim().length() > 0)
	        {
	        	if (memo.length() > 30)
	        		memo = memo.substring(0,30);
//	        	 WholeWord tokens_memo = WordProcessor.getWholeWord(memo);
	        	
	        	doc.add(new Field("memo_store",memo, Store.YES, Index.NO));
//	        	doc.add(new Field("memo", AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(memo),tokens_memo)));
	        }
	        	
	        
	        doc.add(new Field("vid", vid+"", Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("encodeVid", MyUtil.encodeVideoId(vid), Store.YES, Index.NO));
	        
	        doc.add(new Field("createtime", createtime.getTime()+"", Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("total_comment", total_comment+"", Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("total_fav", total_fav+"", Store.YES, Index.UN_TOKENIZED));
	        
	        String logo = rs.getString("thumb0");
	        if (logo ==null || logo.length() == 0 || logo.equals("DEFAULT"))
	        	logo = rs.getString("thumb4");
	        
	        doc.add(new Field("logo", MyUtil.formatString(logo), Store.YES, Index.NO));
	        doc.add(new Field("size", MyUtil.formatString(rs.getString("size")), Store.YES, Index.NO));
	        
	        float  seconds = rs.getFloat("seconds");
	        doc.add(new Field("seconds",seconds+"", Store.YES, Index.UN_TOKENIZED));
	        doc.add(new Field("owner",owner+"", Store.YES, Index.NO));
	        
	        String owner_username = MyUtil.getString(rs.getBytes("owner_username"),"");
	        doc.add(new Field("owner_username_index",MyUtil.toLowerCase(owner_username), Store.NO, Index.UN_TOKENIZED));
	        doc.add(new Field("owner_username",owner_username, Store.YES, Index.NO));
	        doc.add(new Field("public_type",MyUtil.formatString(rs.getString("public_type")), Store.YES, Index.NO));
	        
	        String streamtypes = getStreamtypes(rs.getInt("streamtypes"));
	        if (streamtypes != null && !VideoPermissionManager.getInstance().disallow(VideoPermissionManager.Flag.VALID_FOR_MOBILE, vid)){
		        //如果没有禁止移动设备则索引
		       doc.add(new Field("streamtypes",streamtypes, Store.YES, Index.TOKENIZED));
	       }
//	        long time2 = System.currentTimeMillis();
//	        _log.info("time1="+(time2-time1));
	        String tags = null;
	        String cates = null;
	         tags = MappingManager.getInstance().getTags(vid,"VIDEO");
	         cates =MappingManager.getInstance().getCategory(vid,"VIDEO");
	         
	        if (tags!= null){
		        Field f_tags = new Field("tags", tags,Field.Store.YES,Field.Index.NO);
		        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(tags.replaceAll(","," "))));
		        
		        doc.add(f_tags);
		        doc.add(f_tags_index);
			}
//	        long time3 = System.currentTimeMillis();
//	        _log.info("time2="+(time3-time2));
			if (cates != null){
				doc.add(new Field("categories",cates, Store.YES, Index.UN_TOKENIZED));
			}
	        
	        //获取合作方
	        int partnerId = PartnerManager.getInstance().getPartnerId(vid);
	        if (partnerId > 0)
	        	doc.add(new Field("partnerId", partnerId+"", Store.YES, Index.UN_TOKENIZED));
	        
//	        long time4 = System.currentTimeMillis();
//	        _log.info("time3="+(time4-time3));
	        //获取vv
	        int vv = VvManager.getInstance().getVv(vid);
	        doc.add(new Field("total_pv",(vv==0?1+r.nextInt(10):vv)+"" , Store.YES, Index.UN_TOKENIZED));
	        
//	        long time5 = System.currentTimeMillis();
//	        _log.info("time4="+(time5-time4));
	        
	        String md5 = rs.getString("file_id"); //修改成用file_id 去重
	        int total_down = rs.getInt("total_down");
	        if (total_down > 0){
	        	//获取file_id
	        	Video v = getVideo(total_down);
	        	if (v != null)
	        		md5 = v.md5;
	        }
	        
			if (md5 != null){
				md5 = md5.replaceAll("-", "").substring(30);
				doc.add(new Field("md5", md5.toLowerCase(), Store.YES, Index.UN_TOKENIZED));
			}
//			long time6 = System.currentTimeMillis();
//	        _log.info("time5="+(time6-time5));
	        
	        //高级搜索字段
	        doc.add(new Field("create_day",DataFormat.formatDate(createtime,DataFormat.FMT_DATE_SPECIAL), Store.NO, Index.UN_TOKENIZED));
	        doc.add(new Field("pv_index", AnalyzerManager.getBlankAnalyzer().tokenStream("pv_index",new StringReader(getPvIndex(vv)))));
	        doc.add(new Field("comment_index", AnalyzerManager.getBlankAnalyzer().tokenStream("comment_index",new StringReader(getCommentIndex(total_comment)))));
	        String timelengthIndex = TimeLength.getLessIndex(seconds);
	        doc.add(new Field("timelength", AnalyzerManager.getBlankAnalyzer().tokenStream("timelength",new StringReader(timelengthIndex))));
	        
	        //设置boost
	        boolean ishd = streamtypes!=null && streamtypes.startsWith("1");
	       
//	        float boost = VideoBoost.getBoost(createtime.getTime(),vv,total_fav,total_comment,seconds,title,memo,ishd).all();
	        
	        float boost = 0 ;
			Vv lastvv = LastVvManager.getInstance().getVv(vid);
			if (lastvv != null){
		        VideoBoost_Test bo = VideoBoost_Test.getBoost(lastvv.getValue(),createtime.getTime(),ishd,seconds);
				 boost = (float)bo.all();
			}
//			long time7 = System.currentTimeMillis();
//	        _log.info("time6="+(time7-time6));
		    AssembleDoc result = new AssembleDoc(doc,boost);
	        return result;
		}catch(Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public Video getVideo(int vid)
	{
		Video video = null;
		String sql = "select pk_video,title,thumb0,thumb4,seconds,file_id,rate_total from t_video where is_valid=1 and pk_video=" + vid;
		try {
			List list = BasePeer.executeQuery(sql);
			if (list!= null && list.size() > 0)
			{
				Record record = (Record)list.get(0);
				
				//查是否屏蔽
		        int rate_total  = record.getValue("rate_total").asInt();
		        if (MyUtil.contains(0x4 ,rate_total)){
		        	_log.info("video is limit:"+vid);
					return null;
		        }
				
				video = new Video ();
				video.vid = record.getValue("pk_video").asInt();
				video.title = MyUtil.getString(record.getValue("title").asString());
				video.logo = record.getValue("thumb0").asString();
				if (video.logo ==null || video.logo.length() == 0 || video.logo.equals("DEFAULT"))
					video.logo= record.getValue("thumb4").asString();
				video.seconds = record.getValue("seconds").asString();
				video.md5 = record.getValue("file_id").asString();
				video.encodeVid = MyUtil.encodeVideoId(video.vid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return video;
	}
	
	public String getStreamtypes(int streamtypes)
	{
		if (streamtypes < 4)
			return null;
		
		StringBuffer sb = new StringBuffer();
		
		
		if (MyUtil.contains(0x08,streamtypes))//mp4
		{
			sb.append("1,");
		}
		if (MyUtil.contains(0x10,streamtypes))//3gp
		{
			sb.append("2,");
		}
		if (MyUtil.contains(0x04,streamtypes))//h263FLV
		{
			sb.append("3,");
		}
		if (MyUtil.contains(0x20,streamtypes))//3GPHD  
		{
			sb.append("4,");
		}
		if (MyUtil.contains(0x02,streamtypes))//h264FLV
		{
			sb.append("5,");
		}
		if (MyUtil.contains(0x01,streamtypes))//hd2 720p (超清)
		{
			sb.append("6,");
		}
		if (sb.length() > 0)
			return sb.substring(0,sb.length()-1);
		else 
			return null;
	}
	
	private static String getPvIndex(int pv)
	{
		if (pv >1000000)
			return  "2 3 4 5";
		else if (pv >100000)
			return  "2 3 4";
		else if (pv >10000)
			return  "2 3";
		else if (pv >1000)
			return  "2";
		else
			return "1";
	}
	
	private static String getCommentIndex(int count)
	{
		if (count >10000)
			return  "2 3 4";
		else if (count >1000)
			return  "2 3";
		else if (count >100)
			return  "2";
		else
			return "1";
	}
	
	
}
