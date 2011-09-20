package com.youku.soku.manage.datamaintain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.util.DataBase;

public class RemoveCntvErrorData {
	
	/**
	 * CNTV的直达区专区出现数据错误，链接和剧名对不上。
	 * 删除策略：根据日志(episode_log)表中的数据过滤出cntv的链接，然后去剧集表(programme_episode)中删除
	 */

	private Logger logger = Logger.getLogger(this.getClass());
	
	public void delteteCntvUrl() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> urlList = new ArrayList<String>();
		Map<Integer, String> nameUrlMap = new HashMap<Integer, String>();
		int deleteUrlCount = 0;
		try {
			conn = DataBase.getLibraryConn();
			String sql = "select url from episode_log where operator = 'Spider'";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
		
			while(rs.next()) {
				String url = rs.getString("url");
				urlList.add(url);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		try {
			Criteria crit = new Criteria();
			List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(crit);
			Map<String, List<ProgrammeEpisode>> episodeMap = new HashMap<String, List<ProgrammeEpisode>>();
			for(ProgrammeEpisode pe : peList) {
				List<ProgrammeEpisode> episodeList = episodeMap.get(pe.getUrl());
				if(episodeList == null) {
					episodeList = new ArrayList<ProgrammeEpisode>();
					episodeMap.put(pe.getUrl(), episodeList);
				}
				episodeList.add(pe);
			}
			
			for(String url : urlList) {
				
				List<ProgrammeEpisode> episodeList = episodeMap.get(url);
				if(episodeList != null) {
					
					for(ProgrammeEpisode pe : episodeList) {
						
						ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(pe.getFkProgrammeSiteId());
						String urlInMap = nameUrlMap.get(ps.getFkProgrammeId());
						if(urlInMap == null) {
							urlInMap = pe.getUrl();
						} else {
							urlInMap += pe.getUrl();
						}
						nameUrlMap.put(ps.getFkProgrammeId(), urlInMap);
						
						deleteUrlCount++;
						logger.info("peId: " + pe.getId() + pe.getUrl());
						ProgrammeEpisodePeer.doDelete(pe.getPrimaryKey());
					}
				}
			}
			
			logger.info("delete cntr url count: " + deleteUrlCount);
			File logFile = new File("/opt/soku_data_import_log/spider_error_data_0512");
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
			for(Integer pId : nameUrlMap.keySet()) {
				Programme p = ProgrammePeer.retrieveByPK(pId);				
				bw.write(p.getId() + ", " + p.getName() + ", " + nameUrlMap.get(pId) + "\n");
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		
	}
}
