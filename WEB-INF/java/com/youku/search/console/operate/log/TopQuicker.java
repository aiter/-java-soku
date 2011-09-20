package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.youku.search.console.vo.TopQuickVO;
import com.youku.search.console.vo.TopQuickVO.TopView;

public class TopQuicker {

	public List<TopView> getTotalTopQuickDate(String date1,Connection conn) {
		// System.out.println("getTotalTopQuickDate========================start");
		String table1 = new StringBuffer("top_").append(date1).toString();
		String sql = "select keyword,query_type,a_query_count,a_result,b_query_count,b_result,rate from "
				+ table1
				+ " where kind='total'";
		//System.out.println("sql=" + sql);
		PreparedStatement pt = null;
		ResultSet rs = null;
		TopView tv = null;
		List<TopView> tvlist = new ArrayList<TopView>();
		try {
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
				tv = new TopQuickVO().getTopView();
				tv.setKeyword(rs.getString("keyword"));
				tv.setQuery_type(rs.getString("query_type"));
				tv.setA_query_count(rs.getInt("a_query_count"));
				tv.setA_result(rs.getInt("a_result"));
				tv.setB_query_count(rs.getInt("b_query_count"));
				tv.setB_result(rs.getInt("b_result"));
				tv.setRate(String.format("%.2f", (rs.getDouble("rate")*100)));
				tvlist.add(tv);
			}
		} catch (Exception e) {
			System.out.println("[ERROR] search date  where date=" + date1
					+ " in function getTotalTopQuickDate");
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
			} catch (SQLException e) {
				System.out
						.println("[ERROR] db close error in function getTotalTopQuickDate!");
				e.printStackTrace();
			}
		}
		return tvlist;
	}

	public List<TopView> getVideoTopQuickDate(String date1,Connection conn) {
		String table1 = new StringBuffer("top_").append(date1).toString();
		String sql = "select keyword,query_type,a_query_count,a_result,b_query_count,b_result,rate from "
				+ table1
				+ " where kind='video'";
		// System.out.println("sql="+sql);
		PreparedStatement pt = null;
		ResultSet rs = null;
		TopView tv = null;
		List<TopView> tvlist = new ArrayList<TopView>();
		try {
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
				tv = new TopQuickVO().getTopView();
				tv.setKeyword(rs.getString("keyword"));
				tv.setQuery_type(rs.getString("query_type"));
				tv.setA_query_count(rs.getInt("a_query_count"));
				tv.setA_result(rs.getInt("a_result"));
				tv.setB_query_count(rs.getInt("b_query_count"));
				tv.setB_result(rs.getInt("b_result"));
				tv.setRate(String.format("%.2f", (rs.getDouble("rate")*100)));
				tvlist.add(tv);
			}

		} catch (Exception e) {
			System.out.println("[ERROR] search date where date=" + date1
					+ " in function getVideoTopQuickDate");
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
			} catch (SQLException e) {
				System.out
						.println("[ERROR] db close error in function getVideoTopQuickDate!");
				e.printStackTrace();
			}
		}
		return tvlist;
	}

	public List<TopView> getFolderTopQuickDate(String date1,Connection conn) {
		// System.out.println("getFolderTopQuickDate========================start");
		String table1 = new StringBuffer("top_").append(date1).toString();
		String sql = "select keyword,query_type,a_query_count,a_result,b_query_count,b_result,rate from "
				+ table1
				+ " where kind='folder'";
		PreparedStatement pt = null;
		ResultSet rs = null;
		TopView tv = null;
		List<TopView> tvlist = new ArrayList<TopView>();
		try {
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
				tv = new TopQuickVO().getTopView();
				tv.setKeyword(rs.getString("keyword"));
				tv.setQuery_type(rs.getString("query_type"));
				tv.setA_query_count(rs.getInt("a_query_count"));
				tv.setA_result(rs.getInt("a_result"));
				tv.setB_query_count(rs.getInt("b_query_count"));
				tv.setB_result(rs.getInt("b_result"));
				tv.setRate(String.format("%.2f", (rs.getDouble("rate")*100)));
				tvlist.add(tv);
			}

		} catch (Exception e) {
			System.out.println("[ERROR] search date where date=" + date1
					+ " in function getFolderTopQuickDate");
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
			} catch (SQLException e) {
				System.out
						.println("[ERROR] db close error in function getFolderTopQuickDate!");
				e.printStackTrace();
			}
		}
		return tvlist;
	}

	public static void main(String[] args) {
		System.out.println(String.format("%.2f", 5.34543));
	}
}
