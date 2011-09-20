package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import com.youku.search.console.vo.KeywordQueryVO;

public class SearchUnion {
	
	public void getTopKeywords(Connection conn,String uniondate,Map<String,KeywordQueryVO> keywordMap){
		//站内查询及结果数
		getInsiteTopKeywords(conn, uniondate, keywordMap);
		//站内查询及结果数，显示，点击
		getOutsiteTopKeywords(conn, uniondate, keywordMap);
	}
	
	public void getInsiteTopKeywords(Connection conn,String uniondate,Map<String,KeywordQueryVO> keywordMap){
		PreparedStatement pt = null;
		ResultSet rs = null;
		String sql="select * from top100_"+uniondate+ " where kind='video' order by query_count desc";
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			String keyword=null;
			KeywordQueryVO kqo=null;
			while(rs.next()){
				keyword = rs.getString("keyword");
				kqo = keywordMap.get(keyword);
				if(null==kqo){
					kqo = new KeywordQueryVO();
					keywordMap.put(keyword, kqo);
				}
				kqo.setKeyword(keyword);
				kqo.setInsearchs(rs.getInt("query_count"));
				kqo.setInresults(rs.getInt("result"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getOutsiteTopKeywords(Connection conn,String uniondate,Map<String,KeywordQueryVO> keywordMap){
		 for(Entry<String, KeywordQueryVO> entry:keywordMap.entrySet()){
			 //站外查询
			 doGetOutsiteTopKeywordSearchs(conn, uniondate, entry.getValue());
			 //站外显示
			 doGetOutsiteTopKeywordViews(conn, uniondate, entry.getValue());
			 //站外点击
			 doGetOutsiteTopKeywordClicks(conn, uniondate, entry.getValue());
		 }
	}
	
	public void doGetOutsiteTopKeywordSearchs(Connection conn,String uniondate,KeywordQueryVO kqo){
		PreparedStatement pt = null;
		ResultSet rs = null;
		String sql="select because,sum(query_count) as searchs from query_"+uniondate+ " where query_type='video' and keyword=? group by because ";
		try{
			pt = conn.prepareStatement(sql);
			pt.setString(1, kqo.getKeyword());
			rs = pt.executeQuery();
			int because=0;
			while(rs.next()){
				because=rs.getInt("because");
				if(1==because)
					kqo.setOutsearchs1(rs.getInt("searchs"));
				else if(2==because)
					kqo.setOutsearchs2(rs.getInt("searchs"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doGetOutsiteTopKeywordViews(Connection conn,String uniondate,KeywordQueryVO kqo){
		PreparedStatement pt = null;
		ResultSet rs = null;
		String sql="select because,sum(query_count) as views from query_"+uniondate+ " where query_type='video' and result>0 and keyword=? group by because ";
		try{
			pt = conn.prepareStatement(sql);
			pt.setString(1, kqo.getKeyword());
			rs = pt.executeQuery();
			int because=0;
			while(rs.next()){
				because=rs.getInt("because");
				if(1==because)
					kqo.setOutviews1(rs.getInt("views"));
				else if(2==because)
					kqo.setOutviews2(rs.getInt("views"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doGetOutsiteTopKeywordClicks(Connection conn,String uniondate,KeywordQueryVO kqo){
		PreparedStatement pt = null;
		ResultSet rs = null;
		String sql="select because,sum(click_count) as clicks from click_"+uniondate+ " where  keyword=? group by because ";
		try{
			pt = conn.prepareStatement(sql);
			pt.setString(1, kqo.getKeyword());
			rs = pt.executeQuery();
			int because=0;
			while(rs.next()){
				because=rs.getInt("because");
				if(1==because)
					kqo.setOutclicks1(rs.getInt("clicks"));
				else if(2==because)
					kqo.setOutclicks2(rs.getInt("clicks"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
