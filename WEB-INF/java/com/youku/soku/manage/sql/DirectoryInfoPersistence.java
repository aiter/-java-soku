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
import com.youku.soku.manage.entity.DirectoryInfo;
import com.youku.soku.util.DataBase;

public class DirectoryInfoPersistence {
	
	public DirectoryInfoPersistence() {
		
	}
	
	public List<DirectoryInfo> getDirectoryInfo(int visible,int cate, String top_date, int offset, int limit, String keyword) {
		
		String condition = "";
		if(visible == 1) {
			condition = "and visible = 0";
		} else if(visible == 0) {
			condition = "and visible = 1";
		} 
		
		if(!StringUtils.isBlank(keyword)) {
			condition += " and name like ?";
		}
		
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		String sql = "SELECT * FROM directory_info_"+top_date+" WHERE fk_cate_id = ? " + condition + " ORDER BY order_number, year DESC limit ?, ?";
		
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DirectoryInfo> resultList = new ArrayList<DirectoryInfo>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			
			if(!StringUtils.isBlank(keyword)) {
				pst.setString(2, keyword + "%");
				pst.setInt(3, offset);
				pst.setInt(4, limit);
			} else {
				pst.setInt(2, offset);
				pst.setInt(3, limit);
			}
			
			
			System.out.println(pst.toString());
			rs = pst.executeQuery();
			while(rs.next()) {							
				DirectoryInfo t = new DirectoryInfo();
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
	

	public List<DirectoryInfo> getDirectoryInfoDefaultView(int cate, String top_date,
			int offset, int limit, int lastQueryId, String keyword) {

		if (top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		String condition = "";
		if(!StringUtils.isBlank(keyword)) {
			condition += " and keyword like ?";
		}
		if (StringUtils.isBlank(top_date)
				|| !top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(),
					-1), DataFormat.FMT_DATE_YYYY_MM_DD);
		String sql = "SELECT * FROM directory_info_"
				+ top_date
				+ " WHERE fk_cate_id = ? and visible = 1" + condition + " ORDER BY order_number, year DESC limit ?, ?";

		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DirectoryInfo> resultList = new ArrayList<DirectoryInfo>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			
			if(!StringUtils.isBlank(keyword)) {
				pst.setString(2, keyword);
				pst.setInt(3, lastQueryId);
				pst.setInt(4, limit + 300);
			} else {
				pst.setInt(2, lastQueryId);
				pst.setInt(3, limit + 300);
			}
			

			System.out.println(pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				DirectoryInfo t = new DirectoryInfo();
				t.setTop_date(DataFormat.parseUtilDate(top_date,
						DataFormat.FMT_DATE_YYYY_MM_DD));
				// t.setKeyword(n);
				buildEntity(t, rs);
				int idx = resultList.indexOf(t);
				if(idx > -1) {
					DirectoryInfo oldInfo = resultList.get(idx);
					if(oldInfo.getYear() < t.getYear()) {
						resultList.set(idx, t);
					}
				} else {
					resultList.add(t);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}
	
	
	public int countDirectoryInfo(int cate, String top_date, String keyword) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT count(*) FROM directory_info_"+top_date+" WHERE fk_cate_id = ? ";
		
		if(!StringUtils.isBlank(keyword)) {
			sql += " and name like ?";
		}
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			if(!StringUtils.isBlank(keyword)) {
				pst.setString(2, keyword + "%");
			}
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
	
	public int checkUniqueDirectoryInfo(int cate, String top_date,int fk_names_id) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT count(*) FROM directory_info_"+top_date+" WHERE fk_cate_id = ?  and fk_names_id = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			pst.setInt(2, fk_names_id);
			rs = pst.executeQuery();
			
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
	
	public DirectoryInfo getDirectoryInfo(int id,String top_date) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT * FROM directory_info_"+top_date+" WHERE id = ?";
		
	
		
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
				DirectoryInfo t = new DirectoryInfo();
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
	

	
	private void buildEntity(DirectoryInfo t, ResultSet rs) throws SQLException{
		t.setId(rs.getInt("id"));
		t.setVisible(rs.getInt("visible"));
		t.setUnion_searchs(rs.getInt("search_nums"));
		t.setOrder_number(rs.getInt("order_number"));
		t.setUpdate_date(rs.getTimestamp("update_date"));
		t.setKeyword(rs.getString("name"));
		t.setPic(rs.getString("pic"));
		t.setFk_cate_id(rs.getInt("fk_cate_id"));
		t.setFk_names_id(rs.getInt("fk_names_id"));
		t.setVersion_id(rs.getInt("version_id"));
		t.setYear(rs.getInt("year"));
	}
	

	
	public void updateDirectoryInfo(DirectoryInfo td) {
		String sql = "UPDATE directory_info_"+DataFormat.formatDate(td.getTop_date(), DataFormat.FMT_DATE_YYYY_MM_DD)+" SET visible = ?, order_number = ?, pic = ? WHERE id = ?";
		System.out.println(sql);
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, td.getVisible());
			pst.setInt(2, td.getOrder_number());
			pst.setString(3, td.getPic());
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
	
	public void updateDirectoryInfoOrder(DirectoryInfo td) {
		String sql = "UPDATE directory_info_"+DataFormat.formatDate(td.getTop_date(), DataFormat.FMT_DATE_YYYY_MM_DD)+" SET visible = ?, order_number = ? WHERE fk_cate_id = ? and fk_names_id = ?";
		System.out.println(sql);
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, td.getVisible());
			pst.setInt(2, td.getOrder_number());
			pst.setInt(3, td.getFk_cate_id());
			pst.setInt(4, td.getFk_names_id());
			
			System.out.println(pst.toString());
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	}

	
	public List<DirectoryInfo> getDirectoryInfo(int cate, String top_date, int startOrder, int endOrder) {
		if(top_date != null) {
			top_date = top_date.replace("-", "_");
		}
		if(StringUtils.isBlank(top_date)||!top_date.matches("\\d{4}_\\d{2}_\\d{2}"))
			top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);

		String sql = "SELECT * FROM directory_info_"+top_date+" WHERE fk_cate_id = ?  AND order_number >= ? AND order_number < ? ORDER BY order_number, update_date DESC";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DirectoryInfo> resultList = new ArrayList<DirectoryInfo>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			pst.setInt(2, startOrder);
			pst.setInt(3, endOrder);
			rs = pst.executeQuery();
			String n = null;
			while(rs.next()) {
				DirectoryInfo t = new DirectoryInfo();
				t.setTop_date(DataFormat.parseUtilDate(top_date,DataFormat.FMT_DATE_YYYY_MM_DD));
				t.setKeyword(rs.getString("name"));
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
