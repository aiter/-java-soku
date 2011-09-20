package com.youku.soku.manage.deadlink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.suggest.util.WordUtil;
import com.youku.soku.util.DataBase;

public class YoukuUrlCheck extends BaseUrlCheck{
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	
	
	private void checkYoukuUrl(int offset, int limit) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT pe.id, pe.url from programme_episode pe, programme_site ps where pe.fk_programme_site_id = ps.id and (ps.source_site = 14 or ps.source_site = 100)";
			if(offset >=0 && limit > 0) {
				sql += " limit ?, ?";
			}
			pst = conn.prepareStatement(sql);
			if(offset >=0 && limit > 0) {
				pst.setInt(1, offset);
				pst.setInt(2, limit);
			}
			rs = pst.executeQuery();
			while(rs.next()) {
				String url = rs.getString("pe.url");
				int id = rs.getInt("pe.id");
				int vid = WordUtil.decodeVideoId(url);
				if(!isValidVideo(vid)) {
					logger.info("pe.id: " + id + "dead url: " + url);
					handleYoukuDeadLink(id);
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
	}
	
	private void handleYoukuDeadLink(int peId) throws Exception {
		ProgrammeEpisode pe = ProgrammeEpisodePeer.retrieveByPK(peId);
		markDeadLinkEpisode(pe);
	}
	
	public boolean isValidVideo(int vid){
		String hql = "select is_valid,public_type from t_video where pk_video="
				+ vid;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = DataBase.getYoqooConn();
			pst = conn.prepareStatement(hql);
			rs = pst.executeQuery();
			if (rs.next()) {
				if(1!=rs.getInt("is_valid"))
					return false;
				if(0!=rs.getInt("public_type"))
					return false;
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
		return true;
	}

	@Override
	public void checkAll() {
		long startTime = System.currentTimeMillis();
		checkYoukuUrl(-1, -1);		
		long endTime = System.currentTimeMillis();
		logger.info("Youku dead link check cost: " + (endTime - startTime));
	}

}
