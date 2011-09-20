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
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.analyzer.AnalyzerManager;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class PkManager {
	
	public static final int last_video_number = 3;
	public static PkManager instance = null;
	
	public static PkManager getInstance()
	{
		if ( null == instance ){
			instance = new PkManager();
		}
		return instance;
	}
	
	private PkManager(){
		
	}
	public int getMaxId() throws TorqueException
	{
		int result = 0;
		Connection conn = Torque.getConnection();
		String sql = "select max(pk_pk) as pk_pk from t_pk";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_pk");
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
	public List<Document> getPks(int start,int end,Connection conn)
	{
		String sql = "select * from t_pk where pk_pk>="+start + " and pk_pk<" + end;
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = pkRsToDocument(rs);
				list.add(doc);
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
			} catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	private Document pkRsToDocument(ResultSet rs) throws SQLException
	{
		Document doc = new Document();
		String pk = rs.getString("pk_pk");
		doc.add(new Field("pkid", pk, Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("pk_name", MyUtil.getString(rs.getString("pk_name")), Store.YES, Index.TOKENIZED));
		doc.add(new Field("logo", MyUtil.formatString(rs.getString("logo")), Store.YES, Index.NO));
		doc.add(new Field("status", rs.getString("status"), Store.YES, Index.NO));
		String description = MyUtil.getString(rs.getString("descr"));
		if (description != null && description.length() > 0)
		{
			if (description.length() > 500)
				doc.add(new Field("description",description.substring(0,500) , Store.YES, Index.TOKENIZED));
			else
				doc.add(new Field("description",description , Store.YES, Index.TOKENIZED));
		}
		doc.add(new Field("begintime", String.valueOf(rs.getTimestamp("begintime").getTime()), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("endtime", String.valueOf(rs.getTimestamp("endtime").getTime()), Store.YES, Index.NO));
		doc.add(new Field("owner", rs.getString("owner"), Store.YES, Index.NO));
		doc.add(new Field("user_name", MyUtil.getString(rs.getString("owner_username")), Store.YES, Index.NO));
		doc.add(new Field("video_count", rs.getString("total_content"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("vote_count", rs.getString("total_vote"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("actor_count", rs.getString("total_actor"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("total_pv", rs.getString("total_pv"), Store.YES, Index.UN_TOKENIZED));
		String tags = MappingManager.getInstance().getTags(rs.getInt("pk_pk"),"PK");
		
		Field f_tags = new Field("tags", tags,Field.Store.YES,Field.Index.NO);
        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(tags.replaceAll(","," "))));
        
        doc.add(f_tags);
        doc.add(f_tags_index);
        
		return doc;
	}
	
	public Document getLastVideo(Document doc,Connection conn)
	{
		String sql = "select t_video.pk_video,t_video.title,t_video.seconds from t_pk_actor,t_pk_content,t_video where t_pk_actor.pk_actor_id=t_pk_content.fk_actor_id and t_pk_content.video_id=t_video.pk_video and fk_pk="+ doc.get("pkid") +" order by t_pk_content.createtime desc limit " + last_video_number;
		Statement st = null;
		ResultSet rs = null;
		int i = 1;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				doc.add(new Field("video"+i+"_id", MyUtil.encodeVideoId(rs.getInt("pk_video")), Store.YES, Index.NO));
				doc.add(new Field("video" +i+ "_title", MyUtil.getString(rs.getString("title")), Store.YES, Index.NO));
				doc.add(new Field("video" +i+ "_seconds", rs.getString("seconds"), Store.YES, Index.NO));
				i++;
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
			} catch (SQLException e) {
			}
		}
		return doc;
	}
}
