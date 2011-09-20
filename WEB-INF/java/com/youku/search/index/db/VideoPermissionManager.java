package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.youku.search.util.MyUtil;
import com.youku.search.util.Database;

public class VideoPermissionManager {
protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static VideoPermissionManager instance = null;
	
	private HashMap<Integer,Set<Integer>> permissionMap = new HashMap<Integer,Set<Integer>>();
	
	public enum Flag{
		
		VALID_FOR_MOBILE(0x8000);//屏蔽移动设备
		
		private int value;
		Flag( int arg1) {
			value=arg1;
		}
		public int getValue() {
			return value;
		} 
		
	}
	
	public static VideoPermissionManager getInstance()
	{
		if ( null == instance ){
			instance = new VideoPermissionManager();
		}
		return instance;
	}
	
	private VideoPermissionManager(){
		
	}
	
	public void loadPermission()
	{
		_log.info("load permission");
		Set<Integer> vids = new HashSet<Integer>();
		Connection conn = null;
		
		String sql = "select * from t_video_permission where flags>=" + Flag.VALID_FOR_MOBILE.value;
		Statement st = null;
		ResultSet rs = null;
		try
		{
			conn = Database.getYoqooConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				int flags = rs.getInt("flags");
				if (MyUtil.contains(Flag.VALID_FOR_MOBILE.value, flags)){
					vids.add(rs.getInt("fk_video"));
					
//					_log.info(" permission mobile :" + rs.getInt("fk_video"));
				}
			}
			
			permissionMap.put(Flag.VALID_FOR_MOBILE.value, vids);
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
		if (vids != null)
		{
			_log.info("permission size= " + vids.size());
		}
		_log.info("load permission over");
	}
	
	public Set<Integer> getPermissions(Flag flag)
	{
		return permissionMap.get(flag.value);
	}
	
	public boolean disallow(Flag flag,int id){
		Set<Integer> set = getPermissions(flag);
		if (set!= null)
			return set.contains(id);
		
		return false;
	}
}
