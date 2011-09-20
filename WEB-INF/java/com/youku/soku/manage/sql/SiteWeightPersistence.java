package com.youku.soku.manage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.entity.SiteWeight;
import com.youku.soku.util.DataBase;

public class SiteWeightPersistence {
	
	public void changeSiteWeight(SiteWeight siteWeight) {
		if(!checkSiteExist(siteWeight.getFkSiteId())) {
			insertSite(siteWeight.getFkSiteId());
		} 
		String sql = "UPDATE site_weight set normal_weight = ?, library_weight = ? where fk_site_id = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, siteWeight.getNormalWeight());
			pst.setInt(2, siteWeight.getLibraryWeight());
			pst.setInt(3, siteWeight.getFkSiteId());
			pst.execute();
			System.out.println("changeSiteWeight" + pst.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	
	}
	
	public boolean checkSiteExist(int siteId) {
		String sql = "SELECT * FROM site_weight where fk_site_id = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, siteId);
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next()) {
				count = rs.getInt(1);				
			}
			
			return count > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return false;
	}
	
	public SiteWeight getSiteWeight(int siteId) {
		String sql = "SELECT * FROM site_weight where fk_site_id = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, siteId);
			rs = pst.executeQuery();
			SiteWeight sw = new SiteWeight();
			while(rs.next()) {
				buildEntity(rs, sw);
				return sw;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return null;
	}
	
	public List<SiteWeight> getAllSiteWeight() {
		String sql = "SELECT * FROM site_weight";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<SiteWeight> result = new ArrayList<SiteWeight>();
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				SiteWeight sw = new SiteWeight();
				buildEntity(rs, sw);
				result.add(sw);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return result;
	}
	
	private void buildEntity(ResultSet rs, SiteWeight sw) throws SQLException {
		sw.setFkSiteId(rs.getInt("fk_site_id"));
		sw.setId(rs.getInt("id"));
		sw.setNormalWeight(rs.getInt("normal_weight"));
		sw.setLibraryWeight(rs.getInt("library_weight"));
		sw.setUpdateTime(rs.getTimestamp("update_time"));
	}
	
	public void insertSite(int siteId) {
		String sql = "INSERT INTO site_weight (fk_site_id, normal_weight, library_weight) VALUES (?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuConn();
			pst = conn.prepareStatement(sql);
			pst.setInt(1, siteId);
			pst.setInt(2, 0);
			pst.setInt(3, 0);
			pst.execute();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	}

}
