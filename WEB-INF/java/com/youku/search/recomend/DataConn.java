package com.youku.search.recomend;

import java.sql.Connection;

import org.apache.torque.Torque;

public class DataConn {
	private static Connection getConn(String dbName){
		Connection conn=null;
			try {
				conn=Torque.getConnection(dbName);
			} catch (Exception e1) {
				System.out.println(dbName+" db connection get error!");
				e1.printStackTrace();
			}
			return conn;
	}
	
	public static void releaseConn(Connection conn){
		if(null!=conn)
			Torque.closeConnection(conn);
	}
	
	public static Connection getSearchStatConn(){
			return getConn("search_stat");
	}
	
	public static Connection getYoukubarConn(){
		return getConn("youkubar");
	}
	
	public static Connection getYoukuConn(){
		return getConn("yoqoo");
	}
	
//	public static Connection getFilterConn(){
//		return getConn("filter");
//	}
	
	public static Connection getSearchRecomendConn(){
		return getConn("searchrecommend");
	}
}
