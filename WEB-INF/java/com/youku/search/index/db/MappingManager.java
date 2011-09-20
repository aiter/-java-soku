/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;

/**
 * @author william
 *
 */
public class MappingManager {
	public static MappingManager instance = null;
	
	public static MappingManager getInstance()
	{
		if ( null == instance ){
			instance = new MappingManager();
		}
		return instance;
	}
	
	private MappingManager(){
		
	} 
	public String getCategory(int object_id,String type)
	{
		Connection conn = null;
		try {
			conn = Database.getMappingConnection();
			return getCategory(object_id,type,conn);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		finally{
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {}
		}
		return null;
	}
	public String getCategory(int object_id,String object_type,Connection conn)
	{
//		Connection conn = null;
		String cateid = ""; 
		Statement st = null;
		ResultSet rs = null;
		try {
//			conn = Database.getMappingConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select * from t_mapping where type='CATEGORY' and object_type='"+ object_type +"' and object_id=" + object_id );
			if (rs != null && rs.next())
			{
				cateid = rs.getString("tag_or_cate_id");
			}
		} catch (Exception e) {
			System.err.println("error:MappingManager.getCategory()"+e.getMessage());
		}
		finally
		{
			try {
				if (rs != null)rs.close();
				if (st != null)st.close();
//				if (conn != null)conn.close();
			}
			catch (SQLException e) {
			}
		}
		return cateid;
	}
		
	public String getTags(int object_id,String type)
	{
		Connection conn = null;
		try {
			conn = Database.getMappingConnection();
			return getTags(object_id,type,conn);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		finally{
			if (conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {}
		}
		return null;
	}
	
	public String getTags(int object_id,String type,Connection conn)
	{
		String sql = "select t.tag_name from t_mapping m,t_tag_info t where m.tag_or_cate_id=t.tag_id and m.object_id="+ object_id +" and m.object_type='"+type+"' and m.type='TAG'";
//		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sb =  new StringBuffer();
		try
		{
//			conn = Database.getMappingConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				sb.append(MyUtil.getString(rs.getString("tag_name"))+",");
			}
		}catch(Exception e)
		{
			System.err.println("error:MappingManager.getTags()"+e.getMessage());
		}
		finally
		{
			try {
				if (rs!= null)rs.close();
				if (st!= null)st.close();
//				if (conn != null)conn.close();
			} catch (SQLException e) {
			}
		}
		int i = sb.lastIndexOf(",");
		if (i > -1)
			return sb.substring(0,i);
		else
			return "";
	}
	
}
