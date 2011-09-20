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

import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class RingManager {
	public static RingManager instance = null;
	
	public static RingManager getInstance()
	{
		if ( null == instance ){
			instance = new RingManager();
		}
		return instance;
	}
	
	private RingManager(){
		
	}
	
	public int getMaxId() throws TorqueException
	{
		int result = 0;
		Connection conn = Database.getRingConnection();
		String sql = "select max(id) as id from t_12530";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("id");
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
	
	public List<Document> getRings(int start,int end,Connection conn)
	{
		String sql = "SELECT id,cid,cname,csinger,cprice,cdate FROM t_12530 where id>="+start + " and id<"+end;
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = ringRsToDocument(rs);
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
	
	private Document ringRsToDocument(ResultSet rs) throws SQLException
	{
		Document doc = new Document();
		doc.add(new Field("id", rs.getString("id"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("cid", rs.getString("cid"), Store.YES, Index.NO));
		doc.add(new Field("cname", MyUtil.getString(rs.getString("cname")), Store.YES, Index.TOKENIZED));
		doc.add(new Field("csinger",MyUtil.getString(rs.getString("csinger")), Store.YES, Index.TOKENIZED));
		doc.add(new Field("cprice",rs.getString("cprice") , Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("cdate", String.valueOf(rs.getTimestamp("cdate").getTime()), Store.YES, Index.UN_TOKENIZED));
		
		return doc;
	}
}
