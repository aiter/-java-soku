/**
 * 
 */
package com.youku.search.index.db;

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
import org.apache.torque.TorqueException;

import com.youku.search.index.entity.Video;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class BarPostManager {
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	public static final int post_limit_number = 3;

	public static BarPostManager instance = null;
	public static BarPostManager getInstance()
	{
		if ( null == instance ){
			instance = new BarPostManager();
		}
		return instance;
	}
	
	private BarPostManager(){
		
	}
	
	public int getMaxId() throws TorqueException
	{
		int result = 0;
		Connection conn = Database.getBarConnection();
		String sql = "select max(pk_post) as pk_post from t_bar_post";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_post");
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
	
	public List<Document> getSubjects(int start,int end,Connection conn)
	{
		String sql = "select t_bar_post.*,t_bar.pk_bar,t_bar.bar_name,t_bar_subject.videoid,t_bar_subject.last_post_time " +
				"from t_bar_post,t_bar,t_bar_subject " +
				"where t_bar_post.first=1 and t_bar.closed=0 and t_bar_post.fk_subject=t_bar_subject.pk_subject and t_bar_subject.fk_bar=t_bar.pk_bar and t_bar_post.deleted=0 and pk_post>="+start + " and pk_post<" + end;
		_log.info(sql);
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = rsToDocument(rs);
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
	
	private Document rsToDocument(ResultSet rs) throws SQLException
	{
		int owner = rs.getInt("poster_id");
		if (UserManager.getInstance().isLimit(owner))//如果用户被限制，则不被搜索出
		{
			return null;
		}
		Document doc = new Document();
		doc.add(new Field("pkpost", rs.getString("pk_post"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("pk_bar", rs.getString("pk_bar"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("fk_subject", rs.getString("fk_subject"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("bar_name", MyUtil.getString(rs.getString("bar_name")), Store.YES, Index.NO));
		if (rs.getInt("first") == 0)//主题帖
			doc.add(new Field("subject", MyUtil.getString(rs.getString("subject")), Store.YES, Index.TOKENIZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
		else
			doc.add(new Field("subject", MyUtil.getString(rs.getString("subject")), Store.YES, Index.NO));
		
		String content =  MyUtil.getString(rs.getString("content"));
		if (content.length() > 500)
			content =  content.substring(0,500);
		doc.add(new Field("content", content, Store.YES, Index.TOKENIZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
		
		
		String videoid = rs.getString("videoid");
		if (videoid != null && DataFormat.parseInt(videoid) > 0)
		{
			doc.add(new Field("videoId", videoid, Store.YES, Index.NO));
			Video video = VideoManager.getInstance().getVideo(DataFormat.parseInt(doc.get("videoId")));
			if (video != null)
			{
				doc.add(new Field("videoLogo",video.logo , Store.YES, Index.NO));
				doc.add(new Field("encodeVid",video.encodeVid , Store.YES, Index.NO));
			}
		}
		
		doc.add(new Field("first", rs.getString("first"), Store.YES, Index.NO));
		doc.add(new Field("floor", rs.getString("floor"), Store.YES, Index.NO));
		doc.add(new Field("poster_id", rs.getString("poster_id"), Store.YES, Index.NO));
		doc.add(new Field("poster_name", MyUtil.getString(rs.getString("poster_name")), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("post_time", String.valueOf(rs.getTimestamp("post_time").getTime()), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("last_post_time", String.valueOf(rs.getTimestamp("last_post_time").getTime()), Store.YES, Index.NO));
		
		
		return doc;
	}
	
	
}
