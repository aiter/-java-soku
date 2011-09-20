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
import com.youku.search.index.entity.User;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;
import com.youku.search.util.boost.FolderBoost;
import com.youku.search.util.mail.MailSender;

/**
 * @author william
 *
 */
public class FolderManager {
	
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static final int last_video_number = 3;
	public static FolderManager instance = null;
	
	public static FolderManager getInstance()
	{
		if ( null == instance ){
			instance = new FolderManager();
		}
		return instance;
	}
	
	private FolderManager(){
		
	}
	
	public int getMaxFolderId() throws TorqueException
	{
		int result = 0;
		Connection conn = Database.getFolderConnection();
		String sql = "select max(pk_folder) as pk_folder from t_folder";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_folder");
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
	
	public List<AssembleDoc> getFolders(int start,int end) throws TorqueException
	{
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select f.pk_folder,f.fk_owner,f.folder_type,f.folder_name,f.create_time,f.update_time,f.content_total,f.total_pv,f.logo,f.total_seconds,f.description,total_comment from t_folder f where f.content_total>0  and pk_folder>="+start + " and pk_folder<"+end;
		try {
			 conn = Database.getFolderConnection();
			 
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = folderRsToDocument(rs,conn);
				if (doc!=null)list.add(doc);
			}
				
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage()+"\n" + sql);
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
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	public List<AssembleDoc> getFolders(int start,Date endTime) throws TorqueException
	{
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String sql = "select f.pk_folder,f.fk_owner,f.folder_type,f.folder_name,f.create_time,f.update_time,f.content_total,f.total_pv,f.logo,f.total_seconds,f.description,total_comment from t_folder f where f.content_total>0 and pk_folder>="+start + " and f.create_time<'"+ end +"'";
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			 conn = Database.getFolderConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = folderRsToDocument(rs,conn );
				if (doc!=null)
					list.add(doc);
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage()+"\n" + sql);
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
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	public List<AssembleDoc> getFolders(int minFolder,int maxFolder,Date startTime,Date endTime) throws TorqueException
	{
		String start = DataFormat.formatDate(startTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String end = DataFormat.formatDate(endTime,DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		String sql = "select f.pk_folder,f.fk_owner,f.folder_type,f.folder_name,f.create_time,f.update_time,f.content_total,f.total_pv,f.logo,f.total_seconds,f.description,total_comment from t_folder f where f.content_total>0  and pk_folder>="+minFolder + " and pk_folder<" + maxFolder + " and f.create_time>='"+ start +"' and f.create_time<'"+ end +"'";
		
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			 conn = Database.getFolderConnection();
			 
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = folderRsToDocument(rs,conn);
				if (doc!=null)list.add(doc);
			}
		} catch (TorqueException e) {
			try {
				MailSender.send("luwei@youku.com","创建专辑索引过程中连接数据库失败，start+"+start + " end="+end,e.getMessage()+"\n" + sql);
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
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		return list;
	}
	
	public List<AssembleDoc> getFolders(int[] fids) throws TorqueException
	{
		if (fids == null || fids.length == 0)
			return null;
		
		StringBuffer value = new StringBuffer();
		for (int i=0;i<fids.length;i++)
		{
			value.append(fids[i]+",");
		}
		List<AssembleDoc> list = new ArrayList<AssembleDoc>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "select f.pk_folder,f.fk_owner,f.folder_type,f.folder_name,f.create_time,f.update_time,f.content_total,f.total_pv,f.logo,f.total_seconds,f.description,total_comment from t_folder f where f.content_total>0  and pk_folder>=";
		try {
			 conn = Database.getFolderConnection();
		
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				AssembleDoc doc = folderRsToDocument(rs,conn);
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
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		return list;
	}
	
	private AssembleDoc folderRsToDocument(ResultSet rs,Connection folderConn)
	{
		Document doc = new Document();
		try
		{
			int owner = rs.getInt("fk_owner");
			if (UserManager.getInstance().isLimit(owner))//如果用户被限制，则不被搜索出
			{
				return null;
			}
			int pk_folder = rs.getInt("pk_folder");
			
			doc.add(new Field("pkfolder", String.valueOf(pk_folder), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("fk_owner", String.valueOf(owner), Store.YES, Index.NO));
			
			int content_total = rs.getInt("content_total");
			float total_seconds = rs.getFloat("total_seconds");
			
			long lastUpdate = rs.getTimestamp("update_time")!=null?rs.getTimestamp("update_time").getTime():rs.getTimestamp("create_time").getTime();
			
			doc.add(new Field("update_time",String.valueOf(lastUpdate), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("content_total",String.valueOf(content_total) , Store.YES, Index.UN_TOKENIZED));
			
			String logo = rs.getString("logo");
			if ( logo != null )
				doc.add(new Field("logo", rs.getString("logo"), Store.YES, Index.NO));
			doc.add(new Field("total_seconds", String.valueOf(total_seconds), Store.YES, Index.NO));
			
			
			String folder_name = MyUtil.getString(rs.getString("folder_name"));
			doc.add(new Field("folder_name_store", folder_name, Store.YES, Index.NO));
			
			String description  = MyUtil.getString(rs.getString("description"));
			if (description != null)
			{
				if (description.length() > 60) 
					description = description.substring(0,60);
				
				doc.add(new Field("description", description , Store.YES, Index.TOKENIZED));
			}
			
			doc.add(new Field("total_comment", rs.getString("total_comment"), Store.YES, Index.NO));
			
			
			String cates = MappingManager.getInstance().getCategory(pk_folder,"FOLDER");
			String tags = MappingManager.getInstance().getTags(pk_folder,"FOLDER");
			
			
			doc.add(new Field("cate_ids", cates, Store.YES, Index.UN_TOKENIZED));
			
			Field f_tags = new Field("tags", tags,Field.Store.YES,Field.Index.NO);
	        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(tags.replaceAll(","," "))));
	        doc.add(f_tags);
	        doc.add(f_tags_index);
	        
	        
	        doc.add(new Field("create_day",DataFormat.formatDate(rs.getDate("create_time"),DataFormat.FMT_DATE_SPECIAL), Store.NO, Index.UN_TOKENIZED));
	        String s = getPvIndex(rs.getInt("total_pv"));
	        
	        doc.add(new Field("pv_index", AnalyzerManager.getBlankAnalyzer().tokenStream("pv_index",new StringReader(s))));
	        
	        //获取最后三个视频
	        List<Record> list = getLastVideo(doc,last_video_number,folderConn);
	        int i = 1;
	        StringBuffer videoTitle =  new StringBuffer();
	        if (list !=null){
				for(Record record:list)
				{
					String title = MyUtil.getString(record.getValue("title").asString());
					if (title == null) continue;
					doc.add(new Field("order_no"+i, MyUtil.formatString(record.getValue("order_no").asString()), Store.YES, Index.NO));
					doc.add(new Field("video_title"+i, title, Store.YES, Index.NO));
					doc.add(new Field("video_seconds"+i, MyUtil.formatString(record.getValue("seconds").asString()), Store.YES, Index.NO));
					doc.add(new Field("md5"+i, MyUtil.formatString(record.getValue("md5").asString()), Store.YES, Index.NO));
					videoTitle.append(" ").append(title);
					i++;
				}
				
				WholeWord tokens_videos = WordProcessor.getWholeWord(videoTitle.toString());
				Field f_videoTitle_index = new Field("video_title_index", AnalyzerManager.getMyAnalyzer().tokenStream(new StringReader(videoTitle.toString()),tokens_videos));
		        doc.add(f_videoTitle_index);
		       
	        }

	      //加入同义词
			String synonym = WordProcessor.getSynonym(folder_name);
			String newTitle = synonym != null? folder_name+synonym : folder_name ;
	        
	        WholeWord tokens_name = WordProcessor.getWholeWord(newTitle);
			Field f_name_index = new Field("folder_name", AnalyzerManager.getMyAnalyzer().tokenStream(new StringReader(newTitle),tokens_name),Field.TermVector.WITH_POSITIONS_OFFSETS);
	        doc.add(f_name_index);
	        
	        
	        //获取用户名
	        User user = UserManager.getInstance().getUser(owner);
	        if (user == null || user.user_name == null)
	        	return null;
	        doc.add(new Field("user_name", user.user_name, Store.YES, Index.NO));
	        
	        //获取vv
	        int vv = VvManager.getInstance().getFolderVv(pk_folder);
	        doc.add(new Field("total_pv",String.valueOf(vv) , Store.YES, Index.UN_TOKENIZED));
	        
	        float boost = FolderBoost.getBoost(content_total,vv,total_seconds,lastUpdate,folder_name,description).all();
	        
	        AssembleDoc result = new AssembleDoc(doc,boost);
	        return result;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Record> getLastVideo(Document doc,int limit,Connection conn)
	{
		
		String sql = "select f.order_no,v.title,v.seconds,v.md5 from t_folder_content f,t_video v where f.fk_folder="+doc.get("pkfolder")+" and f.fk_object_id=v.pk_video order by add_time desc limit "+limit;
		try {
			return BasePeer.executeQuery(sql,false,conn);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
}
