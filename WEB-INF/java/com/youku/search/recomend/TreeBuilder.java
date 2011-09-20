package com.youku.search.recomend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.youku.search.hanyupinyin.Converter;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;

public class TreeBuilder {
	
	public static void createRecomendTreeByDB(Connection conn){
		if(null==conn){
			conn=DataConn.getSearchRecomendConn();
		}
		String sql="select keyword,keyword_py,recomend_type,query_count,result from trieTreeRecommend where ? >= starttime and ? <= endtime";
		PreparedStatement pt=null;
		ResultSet rs=null;
		String cuurtentDate=DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYY_MM_DD);
		try {
			pt=conn.prepareStatement(sql);
			pt.setString(1, cuurtentDate);
			pt.setString(2, cuurtentDate);
			rs=pt.executeQuery();
			String keyword;
			String keyword_py;
			String query_type;
			int result;
			int query_count;
			Entity et;
			while(rs.next()){
				keyword=rs.getString("keyword");
				keyword_py=rs.getString("keyword_py");
				query_type=rs.getString("recomend_type");
				query_count=rs.getInt("query_count");
				result=rs.getInt("result");
				et=new Entity(keyword, query_count, result,null);
				if(query_type.equalsIgnoreCase(Constance.VIDEO)){
					Constance.videoTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.FOLDER)){
					Constance.folderTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.USER)){
					Constance.userTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.BAR)){
					Constance.barTree.insert(et);
				}else if(query_type.equalsIgnoreCase(Constance.All)){
					Constance.videoTree.insert(et);
					Constance.folderTree.insert(et);
					Constance.userTree.insert(et);
					Constance.barTree.insert(et);
				}
			if(!keyword.equalsIgnoreCase(keyword_py)){
				et=new Entity(keyword_py, query_count, result,keyword);
				if(query_type.equalsIgnoreCase(Constance.VIDEO)){
					Constance.ch_videoTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.FOLDER)){
					Constance.ch_folderTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.USER)){
					Constance.ch_userTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.BAR)){
					Constance.ch_barTree.insert(et);
				}else if(query_type.equalsIgnoreCase(Constance.All)){
					Constance.ch_videoTree.insert(et);
					Constance.ch_folderTree.insert(et);
					Constance.ch_userTree.insert(et);
					Constance.ch_barTree.insert(et);
				}
				}
			}
		} catch (Exception e) {
			System.out.println("Date read from table trieTreeRecommend error!");
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
	
	public static void createTreeByDB(String date,Connection conn){
		if(null==conn){
			conn=DataConn.getSearchStatConn();
		}
		String table=new StringBuffer("query_").append(date).toString();
		String sql="select keyword,keyword_py,query_type,query_count,result from "+table+" where result>10 and (query_type='video' or query_type='folder' or query_type='user') order by query_count desc limit 2000000";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			String keyword;
			String keyword_py;
			String query_type;
			int result;
			int query_count;
			Entity et;
			while(rs.next()){
				keyword=rs.getString("keyword");
				boolean flag=FilterUtils.isFilter(keyword);
				if(flag) continue;
				keyword_py=rs.getString("keyword_py");
				query_type=rs.getString("query_type");
				query_count=rs.getInt("query_count");
				result=rs.getInt("result");
				if(query_type.equalsIgnoreCase(Constance.VIDEO)){
					et=new Entity(keyword, query_count, result,null);
					Constance.videoTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.FOLDER)){
					et=new Entity(keyword, query_count, result,null);
					Constance.folderTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.USER)){
					et=new Entity(keyword, query_count, result,null);
					Constance.userTree.insert(et);
				}
			if(!keyword.equalsIgnoreCase(keyword_py)){
				if(query_type.equalsIgnoreCase(Constance.VIDEO)){
					et=new Entity(keyword_py, query_count, result,keyword);
					Constance.ch_videoTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.FOLDER)){
					et=new Entity(keyword_py, query_count, result,keyword);
					Constance.ch_folderTree.insert(et);
				}
				else if(query_type.equalsIgnoreCase(Constance.USER)){
					et=new Entity(keyword_py, query_count, result,keyword);
					Constance.ch_userTree.insert(et);
				}
				}
			}
		} catch (Exception e) {
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
	
public static void createBarTreeByDB(Connection conn){
	if(null==conn){
		conn=DataConn.getYoukubarConn();
		}
		String sql="select a.bar_name,b.count_subject from t_bar a,t_bar_stat b where a.pk_bar=b.fk_bar and a.bar_name is not null";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			String bar_name;
			int count_subject;
			String ch_bar_name;
			Entity et;
			while(rs.next()){
				bar_name=MyUtil.getString(rs.getString("a.bar_name"));
				boolean flag=FilterUtils.isFilter(bar_name);
				if(flag) continue;
				count_subject=rs.getInt("b.count_subject");
				ch_bar_name=Converter.convert(bar_name);
					et=new Entity(bar_name, count_subject, count_subject,null);
					Constance.barTree.insert(et);
				if(!bar_name.equalsIgnoreCase(ch_bar_name)){
					et=new Entity(ch_bar_name, count_subject, count_subject,bar_name);
					Constance.ch_barTree.insert(et);
				}
			}
		} catch (Exception e) {
			System.out.println("Date read from bar database error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("bar db close error!");
				e.printStackTrace();
			}
			
		}
	}

	public static boolean isNotEmpty(String date,Connection conn){
		PreparedStatement pt=null;
		String table=new StringBuffer("query_").append(date).toString();
		String sql="select count(*) as c from "+table;
		ResultSet rs  = null;
		try {
			pt=conn.prepareStatement(sql);
			rs = pt.executeQuery();
			if(rs.next())
				return rs.getInt("c")>0?true:false;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return false;
	}
	
//	private static String filterKeyword(String keyword) {
//		  if(null==keyword||keyword.length()<1) return null;
//		    StringBuilder builder = new StringBuilder();
//		    char[] chars = keyword.toCharArray();
//		    for (char c : chars) {
//		        if (c=='\'') {
//		        	builder.append("\\");
//		            builder.append("'");
//		        } else {
//		            builder.append(c);
//		        }
//		    }
//
//		    return builder.toString().trim().toLowerCase();
//	}
	
}
