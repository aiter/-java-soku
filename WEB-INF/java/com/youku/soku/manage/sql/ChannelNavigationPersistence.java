package com.youku.soku.manage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.entity.ChannelNavigation;
import com.youku.soku.util.DataBase;

public class ChannelNavigationPersistence {
	
	public void changeChannelNavigation(String channelName, String navigationText) {
		if(!checkChannelNameExist(channelName)) {
			insertChannelName(channelName);
		} 
		String sql = "UPDATE channel_navigation set navigation_text = ? where fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, navigationText);
			pst.setString(2, channelName);
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	
	}
	
		
	public boolean checkChannelNameExist(String channelName) {
		String sql = "SELECT * FROM channel_navigation where fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, channelName);
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
	
	public List<ChannelNavigation> getChannelNavigation() {
		String sql = "SELECT * FROM channel_navigation";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<ChannelNavigation> result = new ArrayList<ChannelNavigation>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()) {
				ChannelNavigation csw = new ChannelNavigation();
				buildEntity(csw, rs);
				result.add(csw);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return result;
	}
	
	public ChannelNavigation getChannelNavigation(String channelName) {
		String sql = "SELECT * FROM channel_navigation WHERE fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, channelName);
			rs = pst.executeQuery();
			while(rs.next()) {
				ChannelNavigation csw = new ChannelNavigation();
				buildEntity(csw, rs);
				
				return csw;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return null;
	}
	
	private void buildEntity(ChannelNavigation csw, ResultSet rs) throws SQLException{
		csw.setId(rs.getInt("id"));
		csw.setFkChannelName(rs.getString("fk_channel_name"));
		csw.setNavigationText(rs.getString("navigation_text"));
		
		csw.setCreateTime(rs.getDate("create_date"));
	}
	
	public void insertChannelName(String channelName) {
		String sql = "INSERT INTO channel_navigation (navigation_text, fk_channel_name, create_date) VALUES (?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, "");
			pst.setString(2, channelName);
			pst.setTimestamp(3, new Timestamp(new Date().getTime()));
			pst.execute();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	}

}
