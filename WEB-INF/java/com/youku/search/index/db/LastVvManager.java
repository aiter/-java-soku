/**
 * 
 */
package com.youku.search.index.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.search.util.Database;

/**
 * @author 1verge
 * 获取最后15天vv
 */
public class LastVvManager {
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	private static String table_prefix = "vv_";
	private String table;
	
	public static LastVvManager instance = null;
	
	public static LastVvManager getInstance()
	{
		if ( null == instance ){
			instance = new LastVvManager();
		}
		
		return instance;
	}
	
	private LastVvManager(){
		
	}
	
	public String initTable()
	{
		table = getTableName();
		return table;
	}
	
	@SuppressWarnings("unchecked")
	public Vv getVv(int vid)
	{
		if(table == null)
			table = getTableName();
		
		Connection conn = null;
		try {
			conn = Database.getLastVvConnection();
			List list = BasePeer.executeQuery("select vv from "+table+" where vid=" + vid,false,conn);
			
			if (list!=null && list.size()>0)
			{
				int total = ((Record)list.get(0)).getValue("vv").asInt();
				return new Vv(total);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		} 
		finally
		{
			if (conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return null;
	}
	
	private String getTableName()
	{
		Date now = new Date();
		
		for (int i=1;i<6;i++){
			Date yestoday = DataFormat.getNextDate(now, 0-i);
			String date = DataFormat.formatDate(yestoday, DataFormat.FMT_DATE_SPECIAL);
			
			String table = table_prefix+date;
			if (checkTable(table)){
				System.out.println("last vv table:" + table);
				return table;
			}
		}
		
		return null;
	}
	
	private  boolean checkTable(String table)
	{
		Connection conn = null;
		try
		{
			conn = Database.getLastVvConnection();
			String sql = "show tables like '"+ table +"'";
			List list = BasePeer.executeQuery(sql,false,conn);
			return list!=null && list.size() > 0;
		}
		catch(Exception e){
		}
		finally
		{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {}
		}
		return false;
	}
	
	public class Vv{
		
		int vv = 0;
		
		public Vv(int vv)
		{
			this.vv = vv;
		}
		public int getValue()
		{
			return vv;
		}
	}
	
}
