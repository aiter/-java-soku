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
import com.youku.search.index.db.LastVvManager.Vv;
import com.youku.search.index.entity.StreamType;
import com.youku.search.index.entity.Video;
import com.youku.search.index.entity.index.IndexVideo;
import com.youku.search.index.entity.store.StoreVideo;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class NewVideoManager {
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static NewVideoManager instance = null;
	
	public static NewVideoManager getInstance()
	{
		if ( null == instance ){
			instance = new NewVideoManager();
		}
		return instance;
	}
	
	private NewVideoManager(){
		
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
	
	public List<NewAssembleDoc> getVideos(int start,int end) throws TorqueException
	{
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		List<NewAssembleDoc> list = new ArrayList<NewAssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and progress_flag=1 and pk_video>="+start+" and pk_video<"+end+" order by pk_video";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				NewAssembleDoc doc = RsToDocument(rs);

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
	
	public List<Integer> getInvalidVideoList(int start,int end){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<Integer> list = new ArrayList<Integer>();
		String sql = "select pk_video from t_video where is_valid=0 and pk_video>="+start+" and pk_video<"+end+" order by pk_video desc";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()){
				list.add(rs.getInt("pk_video"));
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
	
	/**
	 * 临时使用
	 * @param start
	 * @param end
	 * @return
	 */
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
	public List<NewAssembleDoc> getVideos(int[] vids) throws TorqueException
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
		List<NewAssembleDoc> list = new ArrayList<NewAssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and progress_flag=1 and pk_video in ("+ value.substring(0,value.length()-1) +") order by pk_video";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				NewAssembleDoc doc = RsToDocument(rs);
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
	
	public List<NewAssembleDoc> getVideos(int start,Date endTime,int limit) throws TorqueException
	{
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<NewAssembleDoc> list = new ArrayList<NewAssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and pk_video>="+start+" and createtime<'"+ end +"' order by pk_video limit "+ limit;
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				NewAssembleDoc doc = RsToDocument(rs);
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
	
	public List<NewAssembleDoc> getVideos(Date startTime,Date endTime) throws TorqueException
	{
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<NewAssembleDoc> list = new ArrayList<NewAssembleDoc>();
		String sql = "select * from t_video where is_valid=1  and createtime>='" + start + "' and createtime<'"+ end +"' order by pk_video";
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				NewAssembleDoc doc = RsToDocument(rs);
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
	
	public List<NewAssembleDoc> getVideos(int minVideo,int maxVideo,Date startTime,Date endTime) throws TorqueException
	{
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<NewAssembleDoc> list = new ArrayList<NewAssembleDoc>();
		String sql = "select * from t_video where is_valid=1 and pk_video>="+minVideo+" and pk_video<"+maxVideo+" and createtime>='" + start + "' and createtime<'"+ end +"' order by pk_video";
		_log.info(sql);
		try {
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				NewAssembleDoc doc = RsToDocument(rs);
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
	
	public NewAssembleDoc getVideoAsDocument(int vid)
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
	
	private NewAssembleDoc RsToDocument(ResultSet rs)
	{
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
	        String title = MyUtil.getString(rs.getBytes("title"));
	        if (title == null ) return null;
	        
			long createtime = rs.getTimestamp("createtime").getTime()/1000;
			int total_comment = rs.getInt("total_comment");
			int total_fav =  rs.getInt("total_fav");
			
			String memo = MyUtil.getString(rs.getBytes("memo"));
			if (memo != null && memo.length() > 30){
	        	memo = memo.substring(0,30);
	        }
			 
			String logo = rs.getString("thumb0");
		    if (logo ==null || logo.length() == 0 || logo.equals("DEFAULT"))
		    	logo = rs.getString("thumb4");
			
		    int seconds = (int)DataFormat.parseFloat(rs.getString("seconds"),0);
		    String owner_username = MyUtil.getString(rs.getBytes("owner_username"),"");
			
		  //获取合作方
	        int partnerId = PartnerManager.getInstance().getPartnerId(vid);
	        
	        String tags = MappingManager.getInstance().getTags(vid,"VIDEO");
	        String cates =MappingManager.getInstance().getCategory(vid,"VIDEO");
	        
	      //获取vv
	        int vv = VvManager.getInstance().getVv(vid);
	        vv = vv==0?  r.nextInt(10) : vv;
	        
	        String md5 = rs.getString("file_id"); //修改成用file_id 去重
	        int total_down = rs.getInt("total_down");
	        if (total_down > 0){
	        	//获取file_id
	        	Video v = getVideo(total_down);
	        	if (v != null)
	        		md5 = v.md5;
	        }
	        
	        StreamType streamtypes = new StreamType(rs.getInt("streamtypes"));
	        Vv lastvv = LastVvManager.getInstance().getVv(vid);
	        
	        IndexVideo index = new IndexVideo();
			index.setTitle(title);
			index.setVid(vid);
			index.setMemo(memo);
			index.setSeconds(seconds);
	        index.setCreatetime(createtime);
	        index.setTotal_comment(total_comment);
	        
	        index.setTotal_fav(total_fav);
	        index.setOwner_username(owner_username);
	        
	        if (!VideoPermissionManager.getInstance().disallow(VideoPermissionManager.Flag.VALID_FOR_MOBILE, vid)){
		        //如果没有禁止移动设备则索引
		        index.setFtype(streamtypes.getValue());
	        }
	         
	         index.setTags(tags);
	         index.setCate_ids(DataFormat.parseInt(cates));
	        
	        index.setPartnerId(partnerId);
	        index.setTotal_pv(vv);
	        index.setMd5(md5);
	        if (lastvv!= null)
	        	index.setLastvv(lastvv.getValue());
	        

			//--------------------------存储字段-----------------------------
	        
	        StoreVideo store = new StoreVideo();
	        store.setEncodeVid(MyUtil.encodeVideoId(vid));
	        store.setTitle(title);
	        store.setMemo(memo);
	        store.setLogo(logo);
	        store.setOwner(owner);
	        store.setOwner_username(owner_username);
	        store.setPublic_type(MyUtil.formatString(rs.getString("public_type")));
	        store.setTags(tags);
	        store.setCate_ids(DataFormat.parseInt(cates));
	        store.setTotal_pv(vv);
	        store.setSeconds(seconds);
	        store.setCreatetime(createtime);
	        store.setTotal_comment(total_comment);
	        store.setTotal_fav(total_fav);
	        store.setFtype(streamtypes.getValue());
	        
	        
	        NewAssembleDoc result = new NewAssembleDoc();
	        result.setIndex(index);
	        result.setStore(store);
	        result.setKey(String.valueOf(vid));
	        
	        
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
	
	
}
