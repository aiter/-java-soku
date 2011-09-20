/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.torque.TorqueException;

import com.youku.search.index.entity.User;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class UserManager {
	
	public static final int last_video_number = 2;
	public static UserManager instance = null;
	private Set<Integer> limitUsers = new HashSet<Integer>();
	
	public static UserManager getInstance()
	{
		if ( null == instance ){
			instance = new UserManager();
		}
		return instance;
	}
	
	private UserManager(){
		
	}
	
	public int getMaxId() throws TorqueException
	{
		Connection conn = Database.getYoqooUserConnection();
		int result = 0;
		String sql = "select max(pk_user) as pk_user from t_user";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_user");
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
	
	public List<Document> getUsers(int start,int end,Connection conn)
	{
		String sql = "select * from t_user where pk_user>="+start + " and pk_user<" + end + " and status=1";
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = userRsToDocument(rs);
				if (doc != null){
					list.add(doc);
				}
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
	
	
	private Document userRsToDocument(ResultSet rs) throws SQLException
	{
		Document doc = new Document();
		
		int pkuser = rs.getInt("pk_user");
		if (UserManager.getInstance().isLimit(pkuser))//如果用户被限制，则不被搜索出
		{
			return null;
		}
		
		doc.add(new Field("pkuser", rs.getString("pk_user"), Store.YES, Index.UN_TOKENIZED));
		String username = MyUtil.getString(rs.getString("user_name"));
		
		doc.add(new Field("user_name", username, Store.YES, Index.TOKENIZED));
//		doc.add(new Field("realname", username, Store.YES, Index.NO));
		String city = rs.getString("city") != null ? rs.getString("city"): "35"; //如果为null，替换为35，未知
		doc.add(new Field("city", city, Store.YES, Index.NO));
		String gender = rs.getString("gender") != null ? rs.getString("gender"): "-1"; //如果为null，替换为-1，未知
		doc.add(new Field("gender", gender, Store.YES, Index.NO));
		
		String icon64 = rs.getString("icon64");
		if (icon64 != null)
			doc.add(new Field("icon64", icon64, Store.YES, Index.NO));
		
		doc.add(new Field("score", rs.getString("stat_score_total"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("video_count", rs.getString("stat_content_total"), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("order_count", rs.getString("stat_sub_total"), Store.YES, Index.UN_TOKENIZED));
		
		Date last_login_date = rs.getTimestamp("last_login_date");
		if (last_login_date != null)
			doc.add(new Field("last_login_date", String.valueOf(last_login_date.getTime()), Store.YES, Index.NO));
		
		Date last_content_date = rs.getTimestamp("last_content_date");
		if (last_content_date != null)
			doc.add(new Field("last_content_date",String.valueOf(last_content_date.getTime()), Store.YES, Index.UN_TOKENIZED));
		
		doc.add(new Field("reg_date", String.valueOf(rs.getTimestamp("reg_date").getTime()), Store.YES, Index.UN_TOKENIZED));
		doc.add(new Field("fav_count", rs.getString("stat_fav_total"), Store.YES, Index.UN_TOKENIZED));
		return doc;
	}
	
	
	public User getUser(int pk_user)
	{
		Connection conn = null;
		
		User user = null;
		String sql = "select user_name from t_user where pk_user="+pk_user;
		Statement st = null;
		ResultSet rs = null;
		try
		{
			conn = Database.getYoqooUserConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				user = new User();
				user.user_name = MyUtil.getString(rs.getString("user_name"));
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
		return user;
	}
	
	public Document getUserAsDocument(int pk_user)
	{
		Connection conn = null;
		
		String sql = "select * from t_user where pk_user="+pk_user;
		Statement st = null;
		ResultSet rs = null;
		try
		{
			conn = Database.getYoqooUserConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				return userRsToDocument(rs);
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
		return null;
	}
	
	public Set<Integer> loadLimitUser()
	{
		Set<Integer> users = new HashSet<Integer>();
		Connection conn = null;
		
		String sql = "select fk_user from t_user_limit";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			conn = Database.getYoqooUserConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				users.add(rs.getInt("fk_user"));
				System.out.println("limit user:" + rs.getInt("fk_user"));
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
		synchronized(limitUsers){
			limitUsers.clear();
			limitUsers.addAll(users);
		}
		
		return limitUsers;
	}
	
	public boolean isLimit(int user_id)
	{
		return limitUsers!=null?limitUsers.contains(user_id):false;
	}
	
}
