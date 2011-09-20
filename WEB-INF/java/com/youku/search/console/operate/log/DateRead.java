package com.youku.search.console.operate.log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.torque.TorqueException;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.Turn;
import com.youku.search.console.vo.ViewTotal;
import com.youku.search.util.DataFormat;

public class DateRead {
	public static DateRead instance=null;
//	public Formatter formatter = new Formatter();
	public static Map<String,ViewTotal> vtmap=new ConcurrentHashMap<String, ViewTotal>();
	
	public static DateRead getInstance() {
		if(null!=instance)
		return instance;
		else return new DateRead();
	}

	public ViewTotal getView(Connection conn,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		ViewTotal vt=new ViewTotal();
		getTotalRateByDB(conn,vt,table);
		getrate2ByDB(conn, vt, table);
		getrate3ByDB(conn, vt, table);
		getrate4ByDB(conn, vt, table);
		getrate5ByDB(conn, vt, table);
		return vt;
	}
	
	public void getTotalRateByDB(Connection conn,ViewTotal vt,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		String sql="select sum(query_count) as sumrate from "+table+" where query_type='VIDEO'  and char_length(keyword)>1  ";
		String sql1="select sum(query_count) as sumrate1 from "+table+" where page=1 and  query_type='VIDEO' and char_length(keyword)>1  ";
		String sql2="select sum(query_count) as sumrate2 from "+table+" where page<3 and  query_type='VIDEO' and char_length(keyword)>1  ";
		String sql3="select sum(query_count) as sumrate3 from "+table+" where page<4 and  query_type='VIDEO' and char_length(keyword)>1  ";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			float totalrate;
			float totalrate1;
			float totalrate2;
			float totalrate3;
			if(rs.next()){
				totalrate=rs.getFloat("sumrate");
				if(0==totalrate){
					vt.setTotalRate1(0);
					vt.setTotalRate2(0);
					vt.setTotalRate3(0);
				}else{
					if(null!=rs)
						rs.close();
					if(null!=pt)pt.close();
					pt=conn.prepareStatement(sql1);
					rs=pt.executeQuery();
					if(rs.next()){
						totalrate1=rs.getFloat("sumrate1");
						vt.setTotalRate1((totalrate1/totalrate)*100);
					}
					if(null!=rs)
						rs.close();
					if(null!=pt)pt.close();
					pt=conn.prepareStatement(sql2);
					rs=pt.executeQuery();
					if(rs.next()){
						totalrate2=rs.getFloat("sumrate2");
						vt.setTotalRate2((totalrate2/totalrate)*100);
					}
					if(null!=rs)
						rs.close();
					if(null!=pt)pt.close();
					pt=conn.prepareStatement(sql3);
					rs=pt.executeQuery();
					if(rs.next()){
						totalrate3=rs.getFloat("sumrate3");
						vt.setTotalRate3((totalrate3/totalrate)*100);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Date read from database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error!");
				e.printStackTrace();
			}
		}
	}
	
	private void getrate2ByDB(Connection conn,ViewTotal vt,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		String _sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and total_query_count>20 and page=2 and char_length(keyword)>1   order by rate desc limit 0,500";
//		System.out.println("sql="+sql);
		String sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and query_page1_count>5 and page=2 and char_length(keyword)>1   order by rate desc limit 0,500";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			try{
				pt=conn.prepareStatement(sql);
				rs=pt.executeQuery();
			}catch(Exception e){
				pt=conn.prepareStatement(_sql);
				rs=pt.executeQuery();
			}
			String keyword;
			float rate;
			String query_type;
			float rate_remain;
			int i=0;
			Turn t;
			Turn[] tarr=new Turn[500];
			while(rs.next()){
				t=new Turn();
				keyword=rs.getString("keyword");
				query_type=rs.getString("query_type");
				rate=rs.getFloat("rate");
				rate_remain=rs.getFloat("rate_remain");
				t.setKeyword(keyword);
				t.setQuery_type(query_type);
//				t.setRate(formatter.format("%.2f", rate*100));
				t.setRate(new BigDecimal (rate*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				t.setRate_remain(new BigDecimal (rate_remain*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				tarr[i]=t;
				i=i+1;
			}
			vt.getTurnmap().put(2, tarr);
		} catch (SQLException e) {
			System.out.println("Date read from database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error!");
				e.printStackTrace();
			}
			
		}
	}
	
	private void getrate3ByDB(Connection conn,ViewTotal vt,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		String _sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and total_query_count>20 and page=3 and char_length(keyword)>1   order by rate desc limit 0,500";
//		System.out.println("sql="+sql);
		String sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and query_page1_count>5 and page=3 and char_length(keyword)>1   order by rate desc limit 0,500";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			try{
				pt=conn.prepareStatement(sql);
				rs=pt.executeQuery();
			}catch(Exception e){
				pt=conn.prepareStatement(_sql);
				rs=pt.executeQuery();
			}
			String keyword;
			float rate;
			float rate_remain;
			String query_type;
			int i=0;
			Turn t;
			Turn[] tarr=new Turn[500];
			while(rs.next()){
				t=new Turn();
				keyword=rs.getString("keyword");
				query_type=rs.getString("query_type");
				rate=rs.getFloat("rate");
				rate_remain=rs.getFloat("rate_remain");
				t.setKeyword(keyword);
				t.setQuery_type(query_type);
				t.setRate(new BigDecimal (rate*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				t.setRate_remain(new BigDecimal (rate_remain*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				tarr[i]=t;
				i=i+1;
			}
			vt.getTurnmap().put(3, tarr);
		} catch (SQLException e) {
			System.out.println("Date read from database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error!");
				e.printStackTrace();
			}
			
		}
	}
	
	private void getrate4ByDB(Connection conn,ViewTotal vt,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		String _sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and total_query_count>20 and page=4 and char_length(keyword)>1   order by rate desc limit 0,500";
//		System.out.println("sql="+sql);
		String sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and query_page1_count>5 and page=4 and char_length(keyword)>1   order by rate desc limit 0,500";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			try{
				pt=conn.prepareStatement(sql);
				rs=pt.executeQuery();
			}catch(Exception e){
				pt=conn.prepareStatement(_sql);
				rs=pt.executeQuery();
			}
			String keyword;
			float rate;
			float rate_remain;
			String query_type;
			int i=0;
			Turn t;
			Turn[] tarr=new Turn[500];
			while(rs.next()){
				t=new Turn();
				keyword=rs.getString("keyword");
				query_type=rs.getString("query_type");
				rate=rs.getFloat("rate");
				rate_remain=rs.getFloat("rate_remain");
				t.setKeyword(keyword);
				t.setQuery_type(query_type);
				t.setRate(new BigDecimal (rate*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				t.setRate_remain(new BigDecimal (rate_remain*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				tarr[i]=t;
				i=i+1;
			}
			vt.getTurnmap().put(4, tarr);
		} catch (SQLException e) {
			System.out.println("Date read from database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error!");
				e.printStackTrace();
			}
			
		}
	}
	
	private void getrate5ByDB(Connection conn,ViewTotal vt,String table) throws TorqueException{
		if(null==conn)conn=DataConn.getLogStatConn();
		String _sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and total_query_count>20 and page=5 and char_length(keyword)>1   order by rate desc limit 0,500";
//		System.out.println("sql="+sql);
		String sql="select keyword,query_type,rate,rate_remain from "+table+" where (query_type!='drama' and query_type!='video_md5') and query_page1_count>5 and page=5 and char_length(keyword)>1   order by rate desc limit 0,500";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			try{
				pt=conn.prepareStatement(sql);
				rs=pt.executeQuery();
			}catch(Exception e){
				pt=conn.prepareStatement(_sql);
				rs=pt.executeQuery();
			}
			String keyword;
			float rate;
			float rate_remain;
			String query_type;
			int i=0;
			Turn t;
			Turn[] tarr=new Turn[500];
			while(rs.next()){
				t=new Turn();
				keyword=rs.getString("keyword");
				query_type=rs.getString("query_type");
				rate=rs.getFloat("rate");
				rate_remain=rs.getFloat("rate_remain");
				t.setKeyword(keyword);
				t.setQuery_type(query_type);
				t.setRate(new BigDecimal (rate*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				t.setRate_remain(new BigDecimal (rate_remain*100).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
				tarr[i]=t;
				i=i+1;
			}
			vt.getTurnmap().put(5, tarr);
		} catch (SQLException e) {
			System.out.println("Date read from database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error!");
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		BigDecimal b= new BigDecimal (3.24757335755f);
		System.out.println(b.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue());
		String s="2222-22-22".replaceAll("-", "_");
		System.out.println(s);
		System.out.println("2222-22-22".compareToIgnoreCase("2222-32-2"));
		
		System.out.println("2008_10_11".compareToIgnoreCase(DataFormat.formatDate(new Date(),DataFormat.FMT_DATE_YYYY_MM_DD)));
	}
}
