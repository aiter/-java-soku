package com.youku.soku.manage.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.AuditEpisodeLogBo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.soku.util.DataBase;

public class AuditEpisodeLogService {
	
	private static Logger log = Logger.getLogger(AuditEpisodeLogService.class);

	public static void searchVideoInfo(SearchParameter param, PageInfo pageInfo)
			throws Exception {

		log.info("serarch word: " + param.getSearchWord());
		List<AuditEpisodeLogBo> resultList = new ArrayList<AuditEpisodeLogBo>();

		String mainSql = "programme p, programme_search_number psn WHERE p.id = psn.fk_programme_id and audit_all = 0";
		String likeSql = "";
		if (param.getSearchWord() != null && !param.getSearchWord().equals("")) {
			likeSql = " AND p.name LIKE ? ";
		}

		if (param.getCategoryId() > 0) {
			mainSql += " AND p.cate = " + param.getCategoryId();
		}
		
		if (param.getConcernLevel() > 0) {
			mainSql += " AND p.concern_level = " + param.getConcernLevel();
		}

		if (param.isAccuratelyMatched()) {
			param.setSearchWord(param.getSearchWord());
		} else {
			param.setSearchWord("%" + param.getSearchWord() + "%");
		}

		String countsql = "SELECT COUNT(*) FROM " + mainSql;
		countsql += likeSql;

		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			pstcnt = conn.prepareStatement(countsql);
			if (!likeSql.equals("")) {
				pstcnt.setString(1, param.getSearchWord());
				if (param.getStatus() == SearchParameter.WITH_SERIES) {
					pstcnt.setString(2, param.getSearchWord());
				}
			}
			log.info("pstcnt: " + pstcnt.toString());
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			String sql = "SELECT * FROM " + mainSql + likeSql
					+ " ORDER BY psn.search_number desc LIMIT " + pageInfo.getOffset()
					+ ", " + pageInfo.getPageSize();

			pst = conn.prepareStatement(sql);
			log.info("likeSql " + likeSql);
			if (!likeSql.equals("")) {
				log.info("param.getSearchWord()" + param.getSearchWord());
				pst.setString(1, param.getSearchWord());
				if (param.getStatus() == SearchParameter.WITH_SERIES) {
					pst.setString(2, param.getSearchWord());
				}
			}
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				AuditEpisodeLogBo logBo = new AuditEpisodeLogBo();
				logBo.setId(rs.getInt("p.id"));
				logBo.setName(rs.getString("p.name"));
				logBo.setCateId(rs.getInt("p.cate"));
				logBo.setConcernLevel(rs.getInt("p.concern_level"));
				resultList.add(logBo);
			}
			
			for(AuditEpisodeLogBo logBo : resultList) {
				wrapSiteEpisodeLog(logBo, param.getSiteId());
			}
			
			pageInfo.setResults(resultList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (pstcnt != null) {
					pstcnt.close();
				}
				if (rscnt != null) {
					rscnt.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}

	}
	
	private static void wrapSiteEpisodeLog(AuditEpisodeLogBo auditEpisodeLogBo, int siteId) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, auditEpisodeLogBo.getId());
		List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
		List<AuditEpisodeLogBo.SiteEpisodeLog> siteEpisodeLogList = new ArrayList<AuditEpisodeLogBo.SiteEpisodeLog>();
		List<AuditEpisodeLogBo.SiteEpisodeLog> hasEpisodeSiteList = new ArrayList<AuditEpisodeLogBo.SiteEpisodeLog>();
		if(psList != null && !psList.isEmpty()) {
			for(ProgrammeSite ps : psList) {
				//log.info("siteId" + siteId);
				if(siteId > 0 && ps.getSourceSite() != siteId) {
					continue;
				}
				AuditEpisodeLogBo.SiteEpisodeLog siteEpisode = getSiteEpisodeLog(ps.getId(), ps.getSourceSite());
				
				hasEpisodeSiteList.add(getHasEpisodeSite(ps.getId(), ps.getSourceSite()));
				
				/*//审核特殊显示全剧情况
				if(auditEpisodeLogBo.getConcernLevel() == Constants.CONCERN_FLAG) {
					AuditEpisodeLogBo.SiteEpisodeLog allEpisode = processConcernProgramme(ps.getId(), siteId);
					if(allEpisode.getEpisodeLogList() != null) {
						siteEpisode.getEpisodeLogList().addAll(allEpisode.getEpisodeLogList());
					}
					Collections.sort(siteEpisode.getEpisodeLogList(), new Comparator<EpisodeLog>() {

						@Override
						public int compare(EpisodeLog o1, EpisodeLog o2) {
							return o1.getOrderId() - o2.getOrderId();
						}
					});
				}*/
				siteEpisodeLogList.add(siteEpisode);
			}
		}
		
		auditEpisodeLogBo.setHasEpisodeSiteIds(hasEpisodeSiteList);
		auditEpisodeLogBo.setSiteEpisodeLogList(siteEpisodeLogList);
		auditEpisodeLogBo.setCateName(CategoryService.getCategoryMap().get(auditEpisodeLogBo.getCateId()));
	}
	
	private static AuditEpisodeLogBo.SiteEpisodeLog getSiteEpisodeLog(int psId, int siteId) throws Exception {
		Criteria logCrit = new Criteria();
		logCrit.add(EpisodeLogPeer.FK_PROGRAMME_SITE_ID, psId);
		logCrit.add(EpisodeLogPeer.STATUS, Constants.EPISODE_LOG_NOTHANDLED);
		logCrit.add(EpisodeLogPeer.SOURCE, (Object) "", Criteria.NOT_EQUAL);
		//logCrit.add(EpisodeLogPeer.OPERATOR, (Object) "Spider", Criteria.NOT_EQUAL);
		//log.info(logCrit);
		List<EpisodeLog> logList = EpisodeLogPeer.doSelect(logCrit);
		AuditEpisodeLogBo.SiteEpisodeLog siteLog = new AuditEpisodeLogBo.SiteEpisodeLog();
		siteLog.setSiteId(siteId);
		siteLog.setSiteName(SiteService.getSiteName(siteId));
		//log.info("siteName" + siteLog.getSiteName());
		
		List<EpisodeLog> noSpiderLogList = new ArrayList<EpisodeLog>();
		for(EpisodeLog l : logList) {
			if(StringUtils.isBlank(l.getOperator())) {
				noSpiderLogList.add(l);
			}
		}
		
		siteLog.setEpisodeLogList(noSpiderLogList);
		
		return siteLog;
	}
	
	private static AuditEpisodeLogBo.SiteEpisodeLog getHasEpisodeSite(int psId, int siteId) throws Exception{
		Criteria peCrit = new Criteria();
		peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, psId);
		List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
		if(peList != null && !peList.isEmpty()) {
			AuditEpisodeLogBo.SiteEpisodeLog siteLog = new AuditEpisodeLogBo.SiteEpisodeLog();
			siteLog.setSiteId(psId);
			siteLog.setSiteName(SiteService.getSiteName(siteId));
			return siteLog;
		}
		
		return null;
	}
	
	//重点关注的剧集取出全部剧集链接
	private static AuditEpisodeLogBo.SiteEpisodeLog processConcernProgramme(int psId, int siteId) throws Exception {
		
		Criteria peCrit = new Criteria();
		peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, psId);
		List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
		List<EpisodeLog> logList = new ArrayList<EpisodeLog>();
		log.info("peList size" + peList.size());
		if(peList != null && !peList.isEmpty()) {
			for(ProgrammeEpisode pe : peList) {
				EpisodeLog el = new EpisodeLog();
				el.setTitle(pe.getTitle());
				el.setHd(pe.getHd());
				el.setOrderId(pe.getOrderId());
				el.setUrl(pe.getUrl());
				el.setSeconds(pe.getSeconds());
				el.setId(-1);
				logList.add(el);
			}
		}
	
		AuditEpisodeLogBo.SiteEpisodeLog siteLog = new AuditEpisodeLogBo.SiteEpisodeLog();
		siteLog.setSiteId(siteId);
		siteLog.setSiteName(SiteService.getSiteName(siteId));
		log.info("siteName" + siteLog.getSiteName());
		siteLog.setEpisodeLogList(logList);
		
		return siteLog;
		
	}
	
	public static void checkProgrammeAuditAll(int programmeSiteId) throws Exception{
		
		ProgrammeSite programmeSite = ProgrammeSitePeer.retrieveByPK(programmeSiteId);
		Programme p = ProgrammePeer.retrieveByPK(programmeSite.getFkProgrammeId());
		Criteria pCrit = new Criteria();
		pCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
		pCrit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
		List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(pCrit);
		if(psList != null) {
			for(ProgrammeSite ps : psList) {
				Criteria peCrit = new Criteria();
				peCrit.add(EpisodeLogPeer.FK_PROGRAMME_SITE_ID, ps.getId());
				peCrit.add(EpisodeLogPeer.STATUS, 0);
				List<EpisodeLog> elList = EpisodeLogPeer.doSelect(peCrit);
				
				if(elList == null || elList.isEmpty()) {
					p.setAuditAll(1);
					ProgrammePeer.doUpdate(p);
					log.info("update programm audit flag" + p.getName() + " " + p.getId());
				}
			}
		}
	
	}
	
	
	public static void fixAuditFlag() throws Exception {
		List<Programme> pList = ProgrammePeer.doSelect(new Criteria());
		for(Programme p : pList) {
			Criteria pCrit = new Criteria();
			pCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			pCrit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(pCrit);
			if(psList != null) {
				for(ProgrammeSite ps : psList) {
					ProgrammeSiteService.updateEpisodeCollected(ps.getId());
					Criteria peCrit = new Criteria();
					peCrit.add(EpisodeLogPeer.FK_PROGRAMME_SITE_ID, ps.getId());
					peCrit.add(EpisodeLogPeer.STATUS, 0);
					List<EpisodeLog> elList = EpisodeLogPeer.doSelect(peCrit);
					if(elList == null || elList.isEmpty()) {
						p.setAuditAll(1);
					} else {
						p.setAuditAll(0);
					}
					ProgrammePeer.doUpdate(p);
					log.info("update programm audit flag" + p.getName() + " " + p.getId());

				}
			}
		}
	}

}
