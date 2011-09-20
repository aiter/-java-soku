package com.youku.soku.manage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.entity.MajorTerm;
import com.youku.soku.util.DataBase;

public class MajorTermPersistence {

	public int countMajorTerm(int status, String keyword) {

		String sql = "SELECT count(*) FROM major_term";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuConn();
			if(!StringUtils.isBlank(keyword)) {
				sql += " WHERE keyword like ?";
			}
			pst = conn.prepareStatement(sql);
			
			if(!StringUtils.isBlank(keyword)) {
				pst.setString(1, keyword + "%");
			}
			

			rs = pst.executeQuery();

			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return 0;
	}
	
	public List<MajorTerm> getMajorTerm() {

		String sql = "SELECT * FROM major_term WHERE status = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<MajorTerm> resultList = new ArrayList<MajorTerm>();
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, 1);
			

			rs = pst.executeQuery();

			while (rs.next()) {
				MajorTerm mt = new MajorTerm();
				buildEntity(mt, rs);
				resultList.add(mt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}
	
	public List<MajorTerm> getMajorTerm(int status, String keyword, int offset,
			int limit) {

		String sql = "SELECT * FROM major_term";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<MajorTerm> resultList = new ArrayList<MajorTerm>();
		try {
			conn = DataBase.getSokuConn();
			
			if(!StringUtils.isBlank(keyword)) {
				sql += " WHERE keyword like ?";
			}
			sql += " ORDER BY id desc limit ?, ?";
			pst = conn.prepareStatement(sql);
			
			if(!StringUtils.isBlank(keyword)) {
				pst.setString(1, keyword + "%");
				pst.setInt(2, offset);
				pst.setInt(3, limit);
			} else {
				pst.setInt(1, offset);
				pst.setInt(2, limit);
			}
			
			
			

			rs = pst.executeQuery();

			while (rs.next()) {
				MajorTerm mt = new MajorTerm();
				buildEntity(mt, rs);
				resultList.add(mt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}
	
	public MajorTerm getMajorTermById(int id) {

		String sql = "SELECT * FROM major_term WHERE id = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, id);
			

			rs = pst.executeQuery();

			while (rs.next()) {
				MajorTerm mt = new MajorTerm();
				buildEntity(mt, rs);
				return mt;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}
	
	public List<MajorTerm> getMajorTermByKeyword(String keyword) {

		String sql = "SELECT * FROM major_term WHERE keyword like ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<MajorTerm> resultList = new ArrayList<MajorTerm>();
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			
			pst.setString(1, keyword);
			

			rs = pst.executeQuery();

			while (rs.next()) {
				MajorTerm mt = new MajorTerm();
				buildEntity(mt, rs);
				resultList.add(mt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}
	
	public List<MajorTerm> getMajorTermByKeyword(String keyword, int cateId) {

		String sql = "SELECT * FROM major_term WHERE keyword like ? AND cate_id = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<MajorTerm> resultList = new ArrayList<MajorTerm>();
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			
			pst.setString(1, keyword);
			pst.setInt(2, cateId);

			rs = pst.executeQuery();

			while (rs.next()) {
				MajorTerm mt = new MajorTerm();
				buildEntity(mt, rs);
				resultList.add(mt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}

	private void buildEntity(MajorTerm mt, ResultSet rs) throws SQLException {
		mt.setId(rs.getInt("id"));
		mt.setKeyword(rs.getString("keyword"));
		mt.setHtmlText(rs.getString("html_text"));
		mt.setUrlText(rs.getString("url_text"));
		mt.setStatus(rs.getInt("status"));
		mt.setCreateTime(rs.getTimestamp("create_time"));
		mt.setUpdaetTime(rs.getTimestamp("update_time"));
		mt.setDestUrl(rs.getString("dest_url"));
		mt.setCateId(rs.getInt("cate_id"));
		mt.setLabel(rs.getString("label"));
	}
	
	public void insertMajorTerm(MajorTerm mt) {

		String sql = "INSERT INTO major_term SET keyword = ?, html_text = ?, url_text = ?, status = ?, dest_url = ?, create_time = ?, cate_id = ?, label = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			
			pst.setString(1, mt.getKeyword());
			pst.setString(2, mt.getHtmlText());
			pst.setString(3, mt.getUrlText());
			pst.setInt(4, mt.getStatus());
			pst.setString(5, mt.getDestUrl());
			pst.setTimestamp(6, new Timestamp(mt.getCreateTime().getTime()));
			pst.setInt(7, mt.getCateId());
			pst.setString(8, mt.getLabel());
			pst.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

	}
	
	public void updateMajorTerm(MajorTerm mt) {

		String sql = "UPDATE major_term SET keyword = ?, html_text = ?, url_text = ?, status = ?, update_time = ?, dest_url = ?, cate_id = ?, label = ? where id = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			
			pst.setString(1, mt.getKeyword());
			pst.setString(2, mt.getHtmlText());
			pst.setString(3, mt.getUrlText());
			pst.setInt(4, mt.getStatus());
			pst.setTimestamp(5, new Timestamp(mt.getUpdaetTime().getTime()));
			pst.setString(6, mt.getDestUrl());
			pst.setInt(7, mt.getCateId());
			pst.setString(8, mt.getLabel());
			pst.setInt(9, mt.getId());
			pst.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

	}
	
	public void deleteMajorTerm(int id) {

		String sql = "DELETE FROM major_term WHERE id = ?";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			
			pst.setInt(1, id);

			pst.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

	}
}
