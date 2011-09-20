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
import com.youku.soku.manage.entity.ChannelStopWords;
import com.youku.soku.util.DataBase;

public class ChannelStopWordPersistence {
	
	public void changeChannelStopWords(String channelName, String stopwords) {
		if(!checkChannelNameExist(channelName)) {
			insertChannelName(channelName);
		} 
		String sql = "UPDATE channel_stop_words set keywords = ? where fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, stopwords);
			pst.setString(2, channelName);
			pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
	
	}
	
	public void changeChannelDeleteWords(String channelName, String deletewords) {
		if(!checkChannelNameExist(channelName)) {
			insertChannelName(channelName);
		} 
		String sql = "UPDATE channel_stop_words set delete_keywords = ? where fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, deletewords);
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
		String sql = "SELECT * FROM channel_stop_words where fk_channel_name = ?";
		
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
	
	public List<ChannelStopWords> getChannelStopWords() {
		String sql = "SELECT * FROM channel_stop_words";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<ChannelStopWords> result = new ArrayList<ChannelStopWords>();
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()) {
				ChannelStopWords csw = new ChannelStopWords();
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
	
	public ChannelStopWords getChannelStopWords(String channelName) {
		String sql = "SELECT * FROM channel_stop_words WHERE fk_channel_name = ?";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getSokuTopConn();
			pst = conn.prepareStatement(sql);
			pst.setString(1, channelName);
			rs = pst.executeQuery();
			while(rs.next()) {
				ChannelStopWords csw = new ChannelStopWords();
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
	
	private void buildEntity(ChannelStopWords csw, ResultSet rs) throws SQLException{
		csw.setId(rs.getInt("id"));
		csw.setFkChannelName(rs.getString("fk_channel_name"));
		csw.setKeywords(rs.getString("keywords"));
		csw.setDeleteWords(rs.getString("delete_keywords"));
		csw.setCreateTime(rs.getDate("create_date"));
	}
	
	public void insertChannelName(String channelName) {
		String sql = "INSERT INTO channel_stop_words (keywords, fk_channel_name, create_date) VALUES (?, ?, ?)";
		
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
