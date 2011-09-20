package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.website.WebTopView;
import com.youku.search.util.StringUtil;

public class WebSiteViewsMgt {
	
	private List<WebTopView> showKeywords(String sql){
		List<WebTopView> tvs=new ArrayList<WebTopView>();
		Connection conn=null;
		try{
			conn=DataConn.getWebLogStatConn();
			PreparedStatement pt = null;
			ResultSet rs = null;
			WebTopView tv = null;
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
				tv=new WebTopView();
				tv.setKeyword(rs.getString("keyword"));
				tv.setTotal(rs.getInt("total"));
				tv.setViews(rs.getInt("views"));
				tvs.add(tv);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(conn);
		}
		return tvs;
	}
	
	private List<WebTopView> showTopKeywords(String cuur_date){
			String table1 = new StringBuffer("web_view_").append(cuur_date).toString();
			String sql = "select keyword,total,views from "
					+ table1+" where views!=0 order by views desc limit 100 ";
		return showKeywords(sql);
	}
	
	private List<WebTopView> showTopKeywords(String cuur_date,String keyword,int flag){
		String table1 = new StringBuffer("web_view_").append(cuur_date).toString();
		StringBuffer whereSql=new StringBuffer(" where views!=0 ");
		if(StringUtil.isNotNull(keyword)){
			if(flag==0) whereSql.append("and keyword like '%").append(keyword).append("%'");
			else whereSql.append("and keyword = '").append(keyword).append("'");
		}
		whereSql.append(" order by views desc limit 100");
		String sql = "select keyword,total,views from "
				+ table1+whereSql.toString();
		return showKeywords(sql);
	}
	
	public List<WebTopView> show(String cuur_date,String keyword,int flag){
		if(StringUtil.isNotNull(keyword))
			return showTopKeywords(cuur_date,keyword,flag);
		else return showTopKeywords(cuur_date);
	}
}
