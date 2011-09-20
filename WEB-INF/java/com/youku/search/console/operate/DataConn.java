package com.youku.search.console.operate;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

public class DataConn {
	public static Connection getConn(String dbName) throws TorqueException{
		if(null!=dbName&&dbName.trim().length()>0)
			return Torque.getConnection(dbName);
		else return Torque.getConnection();
	}
	
	public static void releaseConn(Connection conn){
		try {
			if(null!=conn)
			conn.close();
		} catch (SQLException e) {
			System.out.println("[ERROR] db connection close error");
			e.printStackTrace();
		}
	}
	
	public static Connection getLogStatConn() throws TorqueException{
		return getConn("search_stat");
	}
	
	public static Connection getTeleplayConn() throws TorqueException{
		return getConn("searchteleplay");
	}
	
	public static Connection getFilterConn() throws TorqueException{
		return getConn("filter");
	}
	
	public static Connection getYoqooConn() throws TorqueException{
		return getConn("yoqoo");
	}
	
	public static Connection getSearchRecomendConn() throws TorqueException{
		return getConn("searchrecommend");
	}
	
	public static Connection getConsoleConn() throws TorqueException{
		return getConn(null);
	}
	
	public static Connection getWebLogStatConn() throws TorqueException{
		return getConn("search_stat_soku");
	}
}
