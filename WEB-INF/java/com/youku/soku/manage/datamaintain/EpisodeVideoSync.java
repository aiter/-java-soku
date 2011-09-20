package com.youku.soku.manage.datamaintain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.EpisodeVideo;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.util.DataBase;

public class EpisodeVideoSync {
	
	/**
	 * 同步剧集库中的相同视频，就是根据MD5算出的和剧集库中特定一集的视频相同的视频。这样用户在看这一系列视频是都会出来相同的播放列表
	 * 
	 * 用到的两个接口
	 * http://tv.so.youku.com/search?type=20&keyword=10248253&na=0&callback=drama.callback
	 * http://tv.so.youku.com/search?type=20&keyword=10248253&feedback=1&callback=feedBackCallback&pagesize=3
	 */
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final char DELIMITER = '\t';
	
	private Map<String, Integer> buildTeleplayNameMap() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		try {
			conn = DataBase.getTeleplayConn();
			String sql = "select pn.name, pv.name, pv.alias, pv.id, pv.cate from play_name pn, play_version pv, teleplay t where pn.fk_teleplay_id = t.id and pv.fk_teleplay_id = t.id";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				String playName = rs.getString("pn.name");
				String versionName = rs.getString("pv.name");
				String alias = rs.getString("pv.alias");
				int teleplayVersionId = rs.getInt("pv.id");  //play_version 表的ID，episode唯一键是fk_version_id
				int pvCate = rs.getInt("pv.cate");   // 100 动漫， 97 电视剧
				int cate = 0;
				
				if(pvCate == 100) {
					cate = Constants.ANIME_CATE_ID;
				} else if(pvCate == 97) {
					cate = Constants.TELEPLAY_CATE_ID;
				}
				addIdToMap(teleplayVersionId, playName + versionName.trim(), cate, resultMap);
				if(!StringUtils.isBlank(alias)) {
					addIdToMap(teleplayVersionId, alias, cate, resultMap);
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return resultMap;
	}
	
	private Map<String, Integer> buildProgrammeNameMap() {
		

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		try {
			conn = DataBase.getLibraryConn();
			String sql = "select id, name, cate from programme where state = 'normal' and (cate = 1 or cate = 5)";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				String progarmmeName = rs.getString("name");
				int programmeId = rs.getInt("id");
				int cate = rs.getInt("cate");
				addIdToMap(programmeId, progarmmeName, cate, resultMap);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return resultMap;
	
	}
	
	
	private void addIdToMap(int id, String name, int cate, Map<String, Integer> resultMap) {
		
		if(cate ==0) {
			logger.error("cate = 0, 错误的类型！");
		}
		
		String key = name + "==" + cate;
		if(resultMap.get(key) != null) {
			logger.error("名字不唯一！ + " + name);
			resultMap.remove(key);
		} else {
			resultMap.put(key, id);
		}
	}
	
	private int getTeleplayEpisodeId(int fkVersionId, int orderId, String url) {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		
		try {
			conn = DataBase.getTeleplayConn();
			String sql = "select id, encode_video_id from episode where fk_version_id = ? and order_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, fkVersionId);
			pst.setInt(2, orderId);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				int episodeId = rs.getInt("id");
				String teleplayUrl = rs.getString("encode_video_id");
				if(!url.contains(teleplayUrl)) {
					logger.info("different url === fkVersionId: " + fkVersionId + " OrderId: " + orderId + "url: " + url);
				}
				
				return episodeId;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
	
		return -1;
	}
	
	private void saveEpisodeVideo(int fkEpisodeId, int fkProgrammeEpisodeId) {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		
		try {
			conn = DataBase.getTeleplayConn();
			String sql = "select * from episode_video where fk_episode_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, fkEpisodeId);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				EpisodeVideo ev = new EpisodeVideo();
				ev.setEncodeVideoId(rs.getString("encode_video_id"));
				ev.setFileId(rs.getString("file_id"));
				ev.setLogo(rs.getString("logo"));
				ev.setSeconds(rs.getDouble("seconds"));
				ev.setShield(rs.getInt("shield"));
				ev.setSourceName(rs.getString("source_name"));
				ev.setStatusNotify(rs.getInt("status_notify"));
				ev.setVideoId(rs.getInt("video_id"));
				ev.setFkProgrammeEpisodeId(fkProgrammeEpisodeId);
				ev.setUpdateTime(new Date());
				
				ev.save();
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
	
	}
	
	
	public void run() {
		Map<String, Integer> programmeNameMap = buildProgrammeNameMap();
		Map<String, Integer> teleplayNameMap = buildTeleplayNameMap();
		
		int totalMatchedCount = 0;
		List<String> matchedNames = new ArrayList<String>();
		
		for(String pName : programmeNameMap.keySet()) {
			Integer teleplayVersionId = teleplayNameMap.get(pName);
			
			if(teleplayVersionId != null) {
				logger.info("Programme: " + pName + " Matched");
				totalMatchedCount++;
				matchedNames.add(pName);
				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programmeNameMap.get(pName));
				psCrit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
				try {
					List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
					if(psList != null && !psList.isEmpty()) {
						ProgrammeSite ps = psList.get(0);
						Criteria peCrit = new Criteria();
						peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
						List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
						
						for(ProgrammeEpisode pe : peList) {
							if(!StringUtils.isBlank(pe.getUrl())) {
								int teleplayEpisodeId = getTeleplayEpisodeId(teleplayVersionId, pe.getOrderId(), pe.getUrl());
								saveEpisodeVideo(teleplayEpisodeId, pe.getId());
							}
						}
					}
				} catch (TorqueException e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				
			}
			
		}
		
		logger.info("matchedNames.size: " + matchedNames.size());
		
		
		Set<Integer> teleplayVersionIdSet = new HashSet<Integer>(teleplayNameMap.values());
		
		for(String matchedName : matchedNames) {
			int teleplayVersionId = teleplayNameMap.get(matchedName);
			teleplayVersionIdSet.remove(teleplayVersionId);
		}
		logger.info("teleplayVersionIdSet.size: " + teleplayVersionIdSet.size());
		try {
			File file = new File("/opt/soku_data/unmatched_teleplay.data");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(Integer id : teleplayVersionIdSet) {
				logUnMatchTeleplay(id, writer);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("totalMatchedCount: " + totalMatchedCount);
	}
	
	
	private void logUnMatchTeleplay(int teleplayVersionId, Writer writer) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		
		try {
			conn = DataBase.getTeleplayConn();
			String sql = "select t.id, pv.name, pv.alias, pv.id, pv.cate from play_version pv, teleplay t where pv.fk_teleplay_id = t.id and pv.id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, teleplayVersionId);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				String versionName = rs.getString("pv.name");
				String alias = rs.getString("pv.alias");
				//int teleplayVersionId = rs.getInt("pv.id");  //play_version 表的ID，episode唯一键是fk_version_id
				int teleplayId = rs.getInt("t.id");
				int pvCate = rs.getInt("pv.cate");   // 100 动漫， 97 电视剧
				int cate = 0;
				
				if(pvCate == 100) {
					cate = Constants.ANIME_CATE_ID;
				} else if(pvCate == 97) {
					cate = Constants.TELEPLAY_CATE_ID;
				}
				StringBuilder builder = new StringBuilder();
				builder.append(cate).append(DELIMITER).append(getTeleplayName(teleplayId)).append(DELIMITER).append(versionName).append(DELIMITER).append(teleplayVersionId).append(DELIMITER).append("\n");
				//writer.write(cate + DELIMITER + playName + DELIMITER + versionName + DELIMITER + teleplayVersionId + "\n");
				writer.write(builder.toString());
				
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
	}
	
	
	private String getTeleplayName(int teleplayId) {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null; 
		
		try {
			conn = DataBase.getTeleplayConn();
			String sql = "select name from play_name where fk_teleplay_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, teleplayId);
			rs = pst.executeQuery();
			
			StringBuilder builder = new StringBuilder();
			while(rs.next()) {
				if(builder.length() > 0) {
					builder.append("|");
				}
				
				builder.append(rs.getString("name"));
			}
			
			return builder.toString();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		return null;
	}

	
}
