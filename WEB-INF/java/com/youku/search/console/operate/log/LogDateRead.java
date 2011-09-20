package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.youku.search.console.operate.DataConn;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;

public class LogDateRead {
	
	public DailyDate getLastDayDate(String date,Connection conn){
		Map<String,Integer> datemap=new ConcurrentHashMap<String, Integer>();
		DailyDate dd=new DailyDate();
		if(StringUtils.isBlank(date)||!date.matches("\\d{4}_\\d{2}_\\d{2}"))
			date=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
		dd.setDate(date.replaceAll("_", "-"));
		String table=new StringBuffer("query_").append(date).toString();
		String sql="select query_type, sum(query_count) as num from "+table+" where ( query_type='VIDEO' or query_type='FOLDER' or query_type='PK' or query_type='USER' or query_type='BAR' or query_type='ADVVIDEO' or query_type='ADVFOLDER') group by query_type ";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				datemap.put(rs.getString("query_type"), rs.getInt("num"));
			}
		} catch (Exception e) {
			System.out.println("read date from "+table+" error!");
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		dd.setDatemap(datemap);
		return dd;
	}
	
	public DailyDate getLastDayPage1Date(DailyDate dd,Connection conn){
		String date=dd.getDate().replaceAll("-", "_");
		String table=new StringBuffer("turn_").append(date).append("_s").toString();
		String sql="select sum(query_count) as num from "+table+" where page=1 and ( query_type='VIDEO' or query_type='FOLDER' or query_type='PK' or query_type='USER' or query_type='BAR' or query_type='ADVVIDEO' or query_type='ADVFOLDER')";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			if(rs.next()){
				dd.getDatemap().put("page1Total", rs.getInt("num"));
			}
		} catch (Exception e) {
			System.out.println("read date from "+table+" error!");
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return dd;
	}
	
	public void insertDateToUnion(DailyDate dd,Connection conn){
		Map<String,Integer> datemap=dd.getDatemap();
		String date=dd.getDate();
		date=date.replace("_", "-");
		if(null!=datemap&&datemap.size()>0){
		for(String t:datemap.keySet()){
		String sql="insert into query_union values (null,'"+date+"',"+datemap.get(t)+",'"+t+"')";
		PreparedStatement pt=null;
		Statement s=null;
		String sql1="delete from query_union where uniondate='"+date+"' and type='"+t+"'";
		try {
			s=conn.createStatement();
			s.execute(sql1);
			pt=conn.prepareStatement(sql);
			pt.executeUpdate();
		} catch (Exception e) {
			System.out.println("insert data to query_union error!");
			e.printStackTrace();
		}finally{
			JdbcUtil.close(s);
			JdbcUtil.close(pt);
		}
		}
		}
	}
	
	
	public void timeTask(){
		LogDateUnion(null);
	}
	
	public void LogDateUnion(String date){
		Connection conn = null;
		try {
			conn = DataConn.getLogStatConn();
			DailyDate dd=getLastDayDate(date,conn);
			getLastDayPage1Date(dd,conn);
			insertDateToUnion(dd,conn);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			DataConn.releaseConn(conn);
		}
	}
	
	class DailyDate{
		String date;
		Map<String,Integer> datemap;
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public Map<String, Integer> getDatemap() {
			return datemap;
		}
		public void setDatemap(Map<String, Integer> datemap) {
			this.datemap = datemap;
		}
		
	}
	
	public static void main(String[] args) {
//		Map<String,Integer> datemap=new HashMap<String, Integer>();
//		datemap.put("a", 1);
//		datemap.put("b", 2);
//		datemap.put("c", 3);
//		datemap.put("d", 4);
//		datemap.put("e", 5);
//		System.out.println(datemap.get("g"));
		System.out.println(String.format("%.2f", 125.4567588));
	}
}
