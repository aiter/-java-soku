package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.torque.TorqueException;

import com.youku.search.console.operate.DataConn;
import com.youku.search.util.DataFormat;

public class SqlExecuteUtil {
	public static void doExecute(Connection conn,String sql){
		PreparedStatement pt = null;
		try {
			pt = conn.prepareStatement(sql);
			pt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("doExecute,error sql:"+sql);
			e.printStackTrace();
		}finally{
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isExists(Connection conn,String table){
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement("select * from "+table+" limit 1");
			rs = pt.executeQuery();
			if(rs.next())
				return true;
			else return false;
		} catch (SQLException e) {
			System.err.println("isExists,table:"+table);
			e.printStackTrace();
		}finally{
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean isPrepare(String preff,int n){
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		if(n==day){
			String end = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
			String start = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
			String table = new StringBuilder(preff).append(start).append("_").append(end).toString();
			Connection conn = null;
			try {
				conn = DataConn.getLogStatConn();
				boolean isExists = SqlExecuteUtil.isExists(conn, table);
				if(isExists) {
					return false;
				}else{
					return true;
				}
			} catch (TorqueException e) {
				e.printStackTrace();
			}finally{
				DataConn.releaseConn(conn);
			}
		}
		return false;
	}
	
	public static String createTable(String table,String basetable){
		StringBuilder sql = new StringBuilder();
		sql.append("create table if not exists ");
        sql.append(table);
        sql.append(" like ");
        sql.append(basetable);
        return sql.toString();
	}
}
