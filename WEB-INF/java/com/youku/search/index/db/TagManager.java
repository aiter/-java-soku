/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class TagManager {
	
	private static TagManager self = null;

	public synchronized static TagManager getInstance(){
		
		if(self == null){
			self = new TagManager();
			self.init();
		}
		return self;
	}
	private void init(){
		initTags();
	}
	
	private HashMap<String ,Integer> tagMap = new HashMap<String ,Integer>();
	public void initTags()
	{
		System.out.println("初始化tags");
		Connection conn = null;
		try {
			conn = Torque.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from t_tag_info");
			while (rs.next())
			{
				tagMap.put(MyUtil.getString(rs.getString("tag_name")),rs.getInt("tag_id"));
			}
			rs.close();
			st.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("结束初始化tags");
	}
	public String getTagids(String tags)
	{
		if (tags==null || tags.length() == 0)return "";
		StringBuffer sb = new StringBuffer();
		String[] arr = tags.split(",");
		for (int i =0;i<arr.length;i++)
		{
			Integer id = tagMap.get(arr[i]);
			if (id == null){
				id = getTagIdFromDb(arr[i]);
				tagMap.put(arr[i],id);
			}
			sb.append( id );
		}
		return sb.toString();
	}
	
	public int getTagIdFromDb(String tag)
	{
		int id = 0;
		Connection conn = null;
		try {
			conn = Torque.getConnection();
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		try {
			PreparedStatement st = conn.prepareStatement("select * from t_tag_info where tag_name=?");
			st.setString(1,tag);
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				id = rs.getInt("tag_id");
			}
			rs.close();
			st.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
	
}
