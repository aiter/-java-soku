package com.youku.soku.manage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.entity.DirectoryPersonInfo;
import com.youku.soku.util.DataBase;

public class DirectoryPersonInfoPersistence {
	
	public DirectoryPersonInfoPersistence() {
		
	}
	
	public List<DirectoryPersonInfo> getDirectoryPersonInfo(int visible,int cate, String top_date, int offset, int limit) {
		
		String condition = "";
		if(visible == 1) {
			condition = " and visible = 0";
		} else if(visible == 0) {
			condition = " and visible = 1";
		} 
		
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		String sql = "SELECT * FROM directory_info_person_"+top_date+" WHERE 1 = 1 " + condition + " ORDER BY order_number ASC limit ?, ?";
		
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DirectoryPersonInfo> resultList = new ArrayList<DirectoryPersonInfo>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
						
			pst.setInt(1, offset);
			pst.setInt(2, limit);
			
			System.out.println(pst.toString());
			rs = pst.executeQuery();
			while(rs.next()) {							
				DirectoryPersonInfo t = new DirectoryPersonInfo();
				t.setTop_date(DataFormat.parseUtilDate(top_date,DataFormat.FMT_DATE_YYYY_MM_DD));
				//t.setKeyword(n);
				buildEntity(t, rs);
				resultList.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return resultList;
	}
	
	
	public int countDirectoryPersonInfo(int cate, String top_date) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT count(*) FROM directory_info_person_"+top_date+"";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			
			rs = pst.executeQuery();
			System.out.println(pst.toString());
			while(rs.next()) {
				int count = rs.getInt(1);
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return 0;
	}
	
	
	
	public DirectoryPersonInfo getDirectoryPersonInfo(int id,String top_date) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT * FROM directory_info_person_"+top_date+" WHERE id = ?";
		
	
		
		System.out.println(sql);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, id);
			
			rs = pst.executeQuery();
			while(rs.next()) {				
				DirectoryPersonInfo t = new DirectoryPersonInfo();
				buildEntity(t, rs);
				t.setTop_date(DataFormat.parseUtilDate(top_date,DataFormat.FMT_DATE_YYYY_MM_DD));
				//t.setKeyword(n);
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return null;
	}
	

	
	private void buildEntity(DirectoryPersonInfo t, ResultSet rs) throws SQLException{
		t.setId(rs.getInt("id"));
		t.setVisible(rs.getInt("visible"));
		t.setSearch_nums(rs.getInt("search_nums"));
		t.setOrder_number(rs.getInt("order_number"));
		t.setUpdate_date(rs.getTimestamp("update_date"));
		t.setName(rs.getString("name"));
		t.setLogo(rs.getString("logo"));
		t.setFk_person_id(rs.getInt("fk_person_id"));
		t.setHead_logo(rs.getString("head_logo"));
		t.setUnion_nums(rs.getInt("union_nums"));
	}
	

	
	public void updateDirectoryPersonInfo(DirectoryPersonInfo td) {
		String sql = "UPDATE directory_info_person_"+DataFormat.formatDate(td.getTop_date(), DataFormat.FMT_DATE_YYYY_MM_DD)+" SET visible = ?, order_number = ?, logo = ? WHERE id = ?";
		System.out.println(sql);
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, td.getVisible());
			pst.setInt(2, td.getOrder_number());
			pst.setString(3, td.getLogo());
			pst.setInt(4, td.getId());
			
			System.out.println(pst.toString());
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	}
	
	public void updateDirectoryPersonInfoOrder(DirectoryPersonInfo td) {
		String sql = "UPDATE directory_info_person_"+DataFormat.formatDate(td.getTop_date(), DataFormat.FMT_DATE_YYYY_MM_DD)+" SET visible = ?, order_number = ? WHERE id = ?";
		System.out.println(sql);
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, td.getVisible());
			pst.setInt(2, td.getOrder_number());
			pst.setInt(3, td.getId());
			
			
			System.out.println(pst.toString());
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	}

	
	public List<DirectoryPersonInfo> getDirectoryPersonInfo(int cate, String top_date, int startOrder, int endOrder) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT * FROM directory_info_person_"+top_date+" WHERE order_number >= ? AND order_number < ? ORDER BY order_number, update_date DESC";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DirectoryPersonInfo> resultList = new ArrayList<DirectoryPersonInfo>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, startOrder);
			pst.setInt(2, endOrder);
			rs = pst.executeQuery();
			String n = null;
			while(rs.next()) {
				DirectoryPersonInfo t = new DirectoryPersonInfo();
				t.setTop_date(DataFormat.parseUtilDate(top_date,DataFormat.FMT_DATE_YYYY_MM_DD));
				t.setName(rs.getString("name"));
				resultList.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return resultList;
	}
	
}
