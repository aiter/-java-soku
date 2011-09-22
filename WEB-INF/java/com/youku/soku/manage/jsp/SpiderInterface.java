package com.youku.soku.manage.jsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSearchNumber;
import com.youku.soku.library.load.ProgrammeSearchNumberPeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.ProgrammeSpiderBo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.ProgrammeEpisodeService;
import com.youku.soku.manage.service.ProgrammeSiteService;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.util.ManageUtil;
import com.youku.soku.util.DataBase;

public class SpiderInterface {

	private Logger logger = Logger.getLogger(this.getClass());

	/*
	 * public String listProgramme() { try { long start =
	 * System.currentTimeMillis(); StringBuilder sb = new StringBuilder();
	 * List<Programme> pList = ProgrammePeer.doSelect(new Criteria()); JSONArray
	 * info = new JSONArray(); int i = 0;
	 * 
	 * for(Programme p : pList) {
	 * 
	 * 
	 * 
	 * Criteria psCrit = new Criteria();
	 * psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
	 * List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
	 * JSONObject siteInfo = new JSONObject(); for(ProgrammeSite ps : psList) {
	 * 
	 * if(ps.getEpisodeCollected() < p.getEpisodeTotal()) { Criteria peCrit =
	 * new Criteria(); peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,
	 * ps.getId()); List<ProgrammeEpisode> peList =
	 * ProgrammeEpisodePeer.doSelect(peCrit); JSONArray episodeInfo = new
	 * JSONArray(); for(ProgrammeEpisode pe : peList) {
	 * if(!StringUtils.isBlank(pe.getUrl())) { episodeInfo.put(pe.getUrl());
	 * i++; } } siteInfo.put(ps.getSourceSite() + "", episodeInfo); } }
	 * 
	 * if(siteInfo.length() > 0) { JSONObject programmeInfo = new JSONObject();
	 * programmeInfo.put("id", p.getId()); programmeInfo.put("name",
	 * p.getName()); programmeInfo.put("siteInfo", siteInfo);
	 * info.put(programmeInfo); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * long end = System.currentTimeMillis(); logger.info("cost: " + (end -
	 * start)); logger.info("info.length" + info.length());
	 * logger.info("url size: " + i); File file = new
	 * File("/opt/search/spider_interface.json"); BufferedWriter bw = new
	 * BufferedWriter(new FileWriter(file)); bw.write(info.toString());
	 * bw.close(); //return info.toString(); } catch (Exception e) {
	 * logger.error(e.getMessage(), e); }
	 * 
	 * return null; }
	 * 
	 * public String listProgramme1() { try { long start =
	 * System.currentTimeMillis(); List<Programme> pList =
	 * ProgrammePeer.doSelect(new Criteria()); JSONArray info = new JSONArray();
	 * 
	 * 
	 * Criteria psCrit = new Criteria();
	 * psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, 9373);
	 * psCrit.add(ProgrammeSitePeer.COMPLETED, 0);
	 * psCrit.add(ProgrammeSitePeer.SOURCE_SITE, 100, Criteria.NOT_EQUAL);
	 * List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
	 * 
	 * int i = 0; int j = 0;
	 * 
	 * for(ProgrammeSite ps : psList) { j++; Criteria peCrit = new Criteria();
	 * peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
	 * List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
	 * JSONObject episodeInfo = new JSONObject(); for(ProgrammeEpisode pe :
	 * peList) { if(!StringUtils.isBlank(pe.getUrl())) {
	 * episodeInfo.put(pe.getOrderId() + "", pe.getUrl()); i++; } } JSONObject
	 * siteInfo = new JSONObject(); siteInfo.put(ps.getSourceSite() + "",
	 * episodeInfo); Programme p =
	 * ProgrammePeer.retrieveByPK(ps.getFkProgrammeId()); JSONObject
	 * programmeInfo = new JSONObject(); programmeInfo.put("id", p.getId());
	 * programmeInfo.put("name", p.getName()); programmeInfo.put("siteInfo",
	 * siteInfo); info.put(programmeInfo); }
	 * 
	 * 
	 * long end = System.currentTimeMillis(); logger.info("methos 1 cost: " +
	 * (end - start)); logger.info("info.length" + info.length());
	 * logger.info("url size: " + i); logger.info("programme site: " + j); File
	 * file = new File("/opt/search/spider_interface1.json"); BufferedWriter bw
	 * = new BufferedWriter(new FileWriter(file)); bw.write(info.toString());
	 * bw.close(); return info.toString(); } catch (Exception e) {
	 * logger.error(e.getMessage(), e); }
	 * 
	 * return null; }
	 * 
	 * 
	 * public String listProgramme2() { Connection conn = null;
	 * PreparedStatement pst = null; ResultSet rs = null; try { long start =
	 * System.currentTimeMillis();
	 * 
	 * conn = DataBase.getLibraryConn(); String sql =
	 * "select p.id, p.name, ps.id, ps.source_site, pe.url, pe.order_id from programme p, programme_site ps, programme_episode pe where p.id = ps.fk_programme_id and pe.fk_programme_site_id = ps.id and ps.completed = 0 and ps.source_site != 100 and ps.source_site != 14"
	 * ; pst = conn.prepareStatement(sql); rs = pst.executeQuery();
	 * 
	 * Map<Integer, List<SpiderInfo>> infoMap = new HashMap<Integer,
	 * List<SpiderInfo>>(); while(rs.next()) { SpiderInfo si = new SpiderInfo();
	 * si.setId(rs.getInt("p.id")); si.setName(rs.getString("p.name"));
	 * si.setSource_site(rs.getInt("ps.source_site"));
	 * si.setOrderId(rs.getInt("pe.order_id"));
	 * si.setUrl(rs.getString("pe.url"));
	 * si.setProgrammeSiteId(rs.getInt("ps.id"));
	 * 
	 * List<SpiderInfo> list = infoMap.get(si.getProgrammeSiteId()); if(list ==
	 * null) { list = new ArrayList<SpiderInfo>();
	 * infoMap.put(si.getProgrammeSiteId(), list); } list.add(si);
	 * 
	 * }
	 * 
	 * 
	 * JSONArray info = new JSONArray();
	 * 
	 * int i = 0; int j = 0;
	 * 
	 * for(Integer key : infoMap.keySet()) {
	 * 
	 * SpiderInfo s = null;
	 * 
	 * JSONObject episodeInfo = new JSONObject(); for(SpiderInfo si :
	 * infoMap.get(key)) { j++; if(s == null) { s = si; }
	 * 
	 * if(!StringUtils.isBlank(si.getUrl())) { episodeInfo.put(si.getOrderId() +
	 * "", si.getUrl()); i++; }
	 * 
	 * } JSONObject siteInfo = new JSONObject(); siteInfo.put(s.getSource_site()
	 * + "", episodeInfo);
	 * 
	 * JSONObject programmeInfo = new JSONObject(); programmeInfo.put("id",
	 * s.getId()); programmeInfo.put("name", s.getName());
	 * programmeInfo.put("siteInfo", siteInfo); info.put(programmeInfo); }
	 * 
	 * 
	 * long end = System.currentTimeMillis(); logger.info("methos 2 cost: " +
	 * (end - start)); logger.info("info.length" + info.length());
	 * logger.info("url size: " + i); logger.info("programme site: " + j); File
	 * file = new File("/opt/search/spider_interface2.json"); BufferedWriter bw
	 * = new BufferedWriter(new FileWriter(file)); bw.write(info.toString());
	 * bw.close(); return info.toString(); } catch (Exception e) {
	 * logger.error(e.getMessage(), e); } finally { JdbcUtil.close(rs, pst,
	 * conn); }
	 * 
	 * return null; }
	 */
	public Map<Integer, ProgrammeSpiderBo> getProgrammeAllBos() {
		Map<Integer, ProgrammeSpiderBo> result = new HashMap<Integer, ProgrammeSpiderBo>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// List<Programme> pList = ProgrammePeer.doSelect(new Criteria());
			// cate=2或付费的节目不扫描
			String sql = "select * from programme where paid=0";

			conn = DataBase.getLibraryConn();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				ProgrammeSpiderBo psb = new ProgrammeSpiderBo();
				psb.setId(rs.getInt("id"));
				psb.setName(rs.getString("name"));
				psb.setState(rs.getString("state"));
				psb.setCate(rs.getInt("cate"));
				psb.setEpisode_total(rs.getInt("episode_total"));
				// psb.setPay(rs.getInt("pay"));
				result.put(psb.getId(), psb);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {

			}
		}
		return result;
	}

	public Map<Integer, ProgrammeSpiderBo> getProgrammeNoPayBos() {
		Map<Integer, ProgrammeSpiderBo> all = this.getProgrammeAllBos();
		Map<Integer, ProgrammeSpiderBo> result = new HashMap<Integer, ProgrammeSpiderBo>();
		for (Integer key : all.keySet()) {
			ProgrammeSpiderBo psb = all.get(key);
			if (psb.getState().equals("normal"))
				result.put(key, psb);
		}
		return result;
	}

	public Map<Integer, ProgrammeSpiderBo> getProgrammeSpiderBos() {
		Map<Integer, ProgrammeSpiderBo> all = this.getProgrammeAllBos();
		Map<Integer, ProgrammeSpiderBo> result = new HashMap<Integer, ProgrammeSpiderBo>();
		for (Integer key : all.keySet()) {
			ProgrammeSpiderBo psb = all.get(key);
			if (psb.getState().equals("normal") && psb.getCate() != 2)
				result.put(key, psb);
		}
		return result;
	}

	public Map<Integer, ProgrammeSpiderBo> getProgrammeCateBos() {
		Map<Integer, ProgrammeSpiderBo> all = this.getProgrammeAllBos();
		Map<Integer, ProgrammeSpiderBo> result = new HashMap<Integer, ProgrammeSpiderBo>();
		for (Integer key : all.keySet()) {
			ProgrammeSpiderBo psb = all.get(key);
			if (psb.getCate() != 2)
				result.put(key, psb);
		}
		return result;
	}

	public Map<Integer, Integer> buildSearchNumberMap() throws Exception {
		Map<Integer, Integer> searchNumberMap = new HashMap<Integer, Integer>();
		List<ProgrammeSearchNumber> programmeSearchNumberMap = ProgrammeSearchNumberPeer
				.doSelect(new Criteria());

		if (programmeSearchNumberMap != null) {
			for (ProgrammeSearchNumber pn : programmeSearchNumberMap) {
				searchNumberMap
						.put(pn.getFkProgrammeId(), pn.getSearchNumber());
			}
		}

		return searchNumberMap;
	}

	// 兼容之前的方法 默认为0
	public String exportEpisodeNotComplete() {
		return exportEpisodeNotComplete(-1, 0);
	}

	// 增加limit参数 可选择性扫描 默认是查询所有的
	public String exportEpisodeNotComplete(int offset, int size) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			long start = System.currentTimeMillis();

			Map<Integer, ProgrammeSpiderBo> psbMap = getProgrammeCateBos();
			conn = DataBase.getLibraryConn();
			// String sql =
			// "select p.id, p.name, p.cate, p.episode_total, ps.id, ps.episode_collected, ps.source_site, pe.url, pe.order_id, pe.order_stage from programme p, programme_site ps, programme_episode pe where p.id = ps.fk_programme_id and pe.fk_programme_site_id = ps.id and ps.completed = 0 and ps.source_site != 100 and ps.source_site != 14 and p.cate != 2 order by pe.order_stage desc";
			// String sql =
			// "select ps.id,ps.fk_programme_id,ps.source_site, ps.episode_collected,pe.url, pe.order_id, pe.order_stage from programme_site ps, programme_episode pe where pe.fk_programme_site_id = ps.id and ps.completed = 0 and ps.source_site != 100 and ps.source_site != 14 order by pe.order_stage desc";

			String psids = "";
			Map<Integer, ProgrammeSite> psMap = new HashMap<Integer, ProgrammeSite>();
			Criteria crit = new Criteria();
			crit.add(ProgrammeSitePeer.COMPLETED, 0);
			crit.add(ProgrammeSitePeer.SOURCE_SITE, 14, Criteria.NOT_EQUAL);
			crit.add(ProgrammeSitePeer.SOURCE_SITE, 100, Criteria.NOT_EQUAL);
			if (offset > -1) {
				crit.setLimit(offset);
				if (size > 0)
					crit.setOffset(size);
			}
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
			if (null == psList || psList.size() == 0)
				return null;
			for (int i = 0; i < psList.size(); i++) {
				ProgrammeSite ps = psList.get(i);
				psMap.put(ps.getId(), ps);
				psids += ps.getId();
				if (i != psList.size() - 1)
					psids += ",";

			}

			String sql = "select fk_programme_site_id, url, order_id, order_stage from programme_episode where fk_programme_site_id in ("
					+ psids + ") order by order_stage desc";
			String limitsql = " limit " + offset;
			String sizesql = "," + size;
			if (offset > -1) {
				sql += limitsql;
				if (size > 0)
					sql += sizesql;
			}
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			Map<Integer, Map<Integer, List<SpiderInfo>>> programmeMap = new HashMap<Integer, Map<Integer, List<SpiderInfo>>>();

			while (rs.next()) {
				int psid = rs.getInt("fk_programme_site_id");
				ProgrammeSite ps = psMap.get(psid);
				if (null == ps)
					continue;
				int pid = ps.getFkProgrammeId();
				ProgrammeSpiderBo psb = psbMap.get(pid);
				if (null == psb)
					continue;
				SpiderInfo si = new SpiderInfo();
				si.setId(pid);
				si.setName(psb.getName());
				si.setEpisodeTotal(psb.getEpisode_total());
				si.setSource_site(ps.getSourceSite());
				si.setProgrammeSiteId(psid);
				si.setEpisodeCollected(ps.getEpisodeCollected());
				si.setUrl(rs.getString("url"));
				si.setOrderId(rs.getInt("order_id"));
				si.setOrderStage(rs.getInt("order_stage"));
				si.setCate(psb.getCate());
				Map<Integer, List<SpiderInfo>> infoMap = programmeMap.get(si
						.getId());
				if (infoMap == null) {
					infoMap = new LinkedHashMap<Integer, List<SpiderInfo>>();
					programmeMap.put(si.getId(), infoMap);
				}

				List<SpiderInfo> list = infoMap.get(si.getProgrammeSiteId());
				if (list == null) {
					list = new ArrayList<SpiderInfo>();
					infoMap.put(si.getProgrammeSiteId(), list);
				}
				list.add(si);

			}
			Map<Integer, String> siteMap = new HashMap<Integer, String>(
					SiteService.getEpisodeSpiderMap());
			JSONArray info = new JSONArray();

			int i = 0;
			int j = 0;

			Map<Integer, Set<String>> lackEpisodeMap = new TreeMap<Integer, Set<String>>();

			for (Integer pk : programmeMap.keySet()) {
				Map<Integer, List<SpiderInfo>> infoMap = programmeMap.get(pk);

				SpiderInfo p = null;
				JSONObject programmeInfo = new JSONObject();
				JSONObject siteInfo = new JSONObject();
				for (Integer key : infoMap.keySet()) {

					SpiderInfo s = null;

					/*
					 * JSONObject episodeInfo = new JSONObject(); for(SpiderInfo
					 * si : infoMap.get(key)) {
					 * 
					 * j++; if(s == null) { s = si; }
					 * 
					 * if(p == null) { p = si; }
					 * if(!StringUtils.isBlank(si.getUrl())) { if(p.getCate() !=
					 * Constants.VARIETY_CATE_ID) {
					 * episodeInfo.put(si.getOrderId() + "", si.getUrl()); }
					 * else { if(episodeInfo.length() == 0) { //综艺只给最新一期的url
					 * episodeInfo.put(si.getOrderStage() + "", si.getUrl()); }
					 * }
					 * 
					 * i++; } }
					 */

					for (SpiderInfo si : infoMap.get(key)) {

						j++;
						if (s == null) {
							s = si;
						}

						if (p == null) {
							p = si;
						}
						if (siteMap.get(s.getSource_site()) != null) {
							if (p.getCate() == Constants.TELEPLAY_CATE_ID) {

								if (p.getEpisodeTotal() != 0) {
									int lackEpisodeCount = p.getEpisodeTotal()
											- s.getEpisodeCollected();
									Set<String> episodeList = lackEpisodeMap
											.get(lackEpisodeCount);
									if (episodeList == null) {
										episodeList = new HashSet<String>();
										lackEpisodeMap.put(lackEpisodeCount,
												episodeList);
									}
									episodeList.add("siteId: "
											+ s.getSource_site() + "name: "
											+ p.getName());
								}
							}
						}
					}

				}
				/**/
			}
			File file = new File("/opt/search/lack_episode.log");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			int totalCount = 0;
			for (int key : lackEpisodeMap.keySet()) {
				bw.write("缺少" + key + "集的 剧集数" + lackEpisodeMap.get(key).size()
						+ "   详细 " + lackEpisodeMap.get(key));
				bw.write("\n");
				totalCount += lackEpisodeMap.get(key).size();
			}

			bw.close();

			long end = System.currentTimeMillis();
			logger.info("cost: " + (end - start));
			logger.info("total count: " + totalCount);
			/*
			 * File file = new File("/opt/search/spider_interface2.json");
			 * BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			 * bw.write(info.toString()); bw.close();
			 */
			return info.toString(4);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}

	// 兼容之前的方法 默认为0
	public String listProgramme3() {
		return listProgramme3(-1, 0);
	}

	// 增加limit参数 可选择性扫描 默认是查询所有的
	public String listProgramme3(int offset, int size) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			long start = System.currentTimeMillis();

			Map<Integer, ProgrammeSpiderBo> psbMap = getProgrammeSpiderBos();
			Map<Integer, Integer> searchNumberMap = buildSearchNumberMap();
			conn = DataBase.getLibraryConn();
			// String sql =
			// "select p.id, p.name, p.cate, ps.id, ps.episode_collected, ps.source_site, pe.url, pe.order_id, pe.order_stage from programme p, programme_site ps, programme_episode pe where p.state = 'normal' and p.id = ps.fk_programme_id and pe.fk_programme_site_id = ps.id and ps.completed = 0 and ps.source_site != 100 and ps.source_site != 14 and p.cate != 2 order by pe.order_stage desc";
			// String sql =
			// "select ps.fk_programme_id,ps.id, ps.episode_collected, ps.source_site, pe.url, pe.order_id, pe.order_stage from programme_site ps, programme_episode pe where pe.fk_programme_site_id = ps.id and ps.completed = 0 and ps.source_site != 100 and ps.source_site != 14 order by pe.order_stage desc";
			String psids = "";
			Map<Integer, ProgrammeSite> psMap = new HashMap<Integer, ProgrammeSite>();
			Criteria crit = new Criteria();
			crit.add(ProgrammeSitePeer.COMPLETED, 0);
			crit.add(ProgrammeSitePeer.SOURCE_SITE, 14, Criteria.NOT_EQUAL);
			crit.add(ProgrammeSitePeer.SOURCE_SITE, 100, Criteria.NOT_EQUAL);
			crit
					.add(ProgrammeSitePeer.EPISODE_COLLECTED, 0,
							Criteria.NOT_EQUAL);
			crit.addGroupByColumn(ProgrammeSitePeer.FK_PROGRAMME_ID);
			if (offset > -1) {
				if (size > 0) {
					crit.setLimit(size);
					crit.setOffset(offset);
				} else
					crit.setLimit(offset);
			}
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
			if (null == psList || psList.size() == 0)
				return null;
			for (int i = 0; i < psList.size(); i++) {
				ProgrammeSite ps = psList.get(i);
				psMap.put(ps.getId(), ps);
				psids += ps.getId();
				if (i != psList.size() - 1)
					psids += ",";

			}
			
			String sql = "select fk_programme_site_id, group_concat(url)urls, group_concat(order_id)ois, group_concat(order_stage)oss from programme_episode where fk_programme_site_id in ("
					+ psids
					+ ") group by fk_programme_site_id order by order_stage desc";

			// String limitsql = " limit " + offset;
			// String sizesql = "," + size;
			// if (offset > -1) {
			// sql += limitsql;
			// if (size > 0)
			// sql += sizesql;
			// }
			
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			Map<Integer, Map<Integer, List<SpiderInfo>>> programmeMap = new HashMap<Integer, Map<Integer, List<SpiderInfo>>>();

			while (rs.next()) {
				int psid = rs.getInt("fk_programme_site_id");
				ProgrammeSite ps = psMap.get(psid);
				if (null == ps) {
					continue;
				}
				int pid = ps.getFkProgrammeId();
				ProgrammeSpiderBo psb = psbMap.get(pid);
				if (null == psb || !psb.getState().equals("normal")) {
					continue;
				}
				if (null == rs.getString("ois")
						|| rs.getString("ois").isEmpty())
					continue;
				logger.debug("**** urls:" + rs.getString("urls") + "*******");
				logger.debug("**** uis:" + rs.getString("ois") + "*******");
				logger.debug("**** oss:" + rs.getString("oss") + "*******");
				String[] urls = rs.getString("urls").split(",");
				String[] ois = rs.getString("ois").split(",");
				String[] oss = rs.getString("oss").split(",");
				for (int i = 0; i < ois.length; i++) {
					SpiderInfo si = new SpiderInfo();
					si.setId(pid);
					si.setName(psb.getName());
					si.setSource_site(ps.getSourceSite());
					si.setProgrammeSiteId(psid);
					si.setEpisodeCollected(ps.getEpisodeCollected());
					if (urls.length-1 < i)
						si.setUrl("");
					else
						si.setUrl(urls[i]);
					if (ois.length-1 < i)
						si.setOrderId(0);
					else
					si.setOrderId(Integer.parseInt(ois[i]));
					if (oss.length-1 < i)
						si.setOrderStage(0);
					else
					si.setOrderStage(Integer.parseInt(oss[i]));
					si.setCate(psb.getCate());
					Map<Integer, List<SpiderInfo>> infoMap = programmeMap
							.get(si.getId());
					if (infoMap == null) {
						infoMap = new LinkedHashMap<Integer, List<SpiderInfo>>();
						programmeMap.put(si.getId(), infoMap);
					}

					List<SpiderInfo> list = infoMap
							.get(si.getProgrammeSiteId());
					if (list == null) {
						list = new ArrayList<SpiderInfo>();
						infoMap.put(si.getProgrammeSiteId(), list);
					}
					list.add(si);
				}
				logger.debug("**** programmeMap:" + programmeMap.size()
						+ "*******");
			}

			Map<Integer, String> siteMap = new HashMap<Integer, String>(
					SiteService.getEpisodeSpiderMap());
			JSONArray info = new JSONArray();

			int i = 0;
			int j = 0;

			for (Integer pk : programmeMap.keySet()) {
				Map<Integer, List<SpiderInfo>> infoMap = programmeMap.get(pk);

				SpiderInfo p = null;
				JSONObject programmeInfo = new JSONObject();
				JSONObject siteInfo = new JSONObject();
				for (Integer key : infoMap.keySet()) {

					SpiderInfo s = null;

					JSONObject episodeInfo = new JSONObject();
					for (SpiderInfo si : infoMap.get(key)) {

						j++;
						if (s == null) {
							s = si;
						}

						if (p == null) {
							p = si;
						}
						if (!StringUtils.isBlank(si.getUrl())) {
							if (p.getCate() != Constants.VARIETY_CATE_ID) {
								episodeInfo.put(si.getOrderId() + "", si
										.getUrl());
							} else {
								if (episodeInfo.length() == 0) { // 综艺只给最新一期的url
									episodeInfo.put(si.getOrderStage() + "", si
											.getUrl());
								}
							}

							i++;
						}
					}

					if (siteMap.get(s.getSource_site()) != null) {
						if (s.getEpisodeCollected() > 0
								|| p.getCate() == Constants.VARIETY_CATE_ID) {

							siteInfo.put(s.getSource_site() + "", episodeInfo);

						}
					}
				}

				programmeInfo.put("id", p.getId());
				programmeInfo.put("name", p.getName());
				programmeInfo.put("cate", p.getCate());
				programmeInfo.put("searchNumber", searchNumberMap
						.get(p.getId()));
				programmeInfo.put("siteInfo", siteInfo);
				info.put(programmeInfo);
			}

			long end = System.currentTimeMillis();
			logger.info("methos 2 cost: " + (end - start));
			logger.info("info.length" + info.length());
			logger.info("url size: " + i);
			logger.info("programme site: " + j);
			/*
			 * File file = new File("/opt/search/spider_interface2.json");
			 * BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			 * bw.write(info.toString()); bw.close();
			 */
			return info.toString(4);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}

	public String listProgrammeNoEpisode() {
		return listProgrammeNoEpisode(-1, 0);
	}

	public String listProgrammeNoEpisode(int offset, int size) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			long start = System.currentTimeMillis();

			Map<Integer, ProgrammeSpiderBo> psbMap = getProgrammeNoPayBos();
			Map<Integer, Integer> searchNumberMap = buildSearchNumberMap();
			conn = DataBase.getLibraryConn();
			// String sql =
			// "select p.id, p.name, p.cate, ps.id, ps.source_site, ps.episode_collected from programme p, programme_site ps where p.state = 'normal' and p.id = ps.fk_programme_id and p.paid=0 and ps.source_site != 100";
			// String sql =
			// "select p.id, p.name, p.cate, group_concat(ps.id)psid, group_concat(ps.source_site)psss, group_concat(ps.episode_collected)psec from programme p, programme_site ps where p.state = 'normal' and p.id = ps.fk_programme_id and p.paid=0 and ps.source_site != 100 group by p.id";
			String pids = "";
			List<ProgrammeSpiderBo> psbList = new ArrayList<ProgrammeSpiderBo>(
					psbMap.values());
			if (offset > -1) {
				if (size > 0) {
					int to = offset + size;
					psbList = psbList.subList(offset, to);
				} else
					psbList = psbList.subList(0, offset);
			}
			for (int i = 0; i < psbList.size(); i++) {
				pids += psbList.get(i).getId();
				if (i != psbList.size() - 1)
					pids += ",";
			}
			String sql = "select fk_programme_id, group_concat(id)psid, group_concat(source_site)psss, group_concat(episode_collected)psec from programme_site where source_site != 100 and fk_programme_id in ("
					+ pids + ") group by fk_programme_id";

			String limitsql = " limit " + offset;
			String sizesql = "," + size;
			// if (offset > -1) {
			// sql += limitsql;
			// if (size > 0)
			// sql += sizesql;
			// }
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			Map<Integer, Map<Integer, List<SpiderInfo>>> programmeMap = new HashMap<Integer, Map<Integer, List<SpiderInfo>>>();

			while (rs.next()) {
				if (null == rs.getString("psss")
						|| rs.getString("psss").isEmpty())
					continue;
				int pid = rs.getInt("fk_programme_id");
				ProgrammeSpiderBo psb = psbMap.get(pid);
				if (null == psb)
					continue;
				String[] psid = rs.getString("psid").split(",");
				String[] psss = rs.getString("psss").split(",");
				String[] psec = rs.getString("psec").split(",");
				for (int i = 0; i < psss.length; i++) {
					SpiderInfo si = new SpiderInfo();
					si.setId(psb.getId());
					si.setName(psb.getName());
					si.setSource_site(Integer.parseInt(psss[i]));
					si.setProgrammeSiteId(Integer.parseInt(psid[i]));
					si.setEpisodeCollected(Integer.parseInt(psec[i]));
					si.setCate(psb.getCate());

					Map<Integer, List<SpiderInfo>> infoMap = programmeMap
							.get(si.getId());
					if (infoMap == null) {
						infoMap = new HashMap<Integer, List<SpiderInfo>>();
						programmeMap.put(si.getId(), infoMap);
					}

					List<SpiderInfo> list = infoMap
							.get(si.getProgrammeSiteId());
					if (list == null) {
						list = new ArrayList<SpiderInfo>();
						infoMap.put(si.getProgrammeSiteId(), list);
					}
					list.add(si);
					infoMap.put(si.getProgrammeSiteId(), list);
				}
			}

			JSONArray info = new JSONArray();

			int i = 0;
			int j = 0;

			for (Integer pk : programmeMap.keySet()) {
				Map<Integer, List<SpiderInfo>> infoMap = programmeMap.get(pk);
				Map<Integer, String> siteMap = new HashMap<Integer, String>(
						SiteService.getEpisodeSpiderMap());

				SpiderInfo p = null;
				JSONObject programmeInfo = new JSONObject();
				JSONObject siteInfo = new JSONObject();
				for (Integer key : infoMap.keySet()) {

					SpiderInfo s = null;

					JSONObject episodeInfo = new JSONObject();
					for (SpiderInfo si : infoMap.get(key)) {

						j++;
						if (s == null) {
							s = si;
						}

						if (p == null) {
							p = si;
						}

					}

					siteMap.remove(s.getSource_site());
					if (s.getEpisodeCollected() == 0) {
						siteInfo.put(s.getSource_site() + "", episodeInfo);

						programmeInfo.put("siteInfo", siteInfo);
					}
				}

				for (Integer siteId : siteMap.keySet()) {
					JSONObject episodeInfo = new JSONObject();
					siteInfo.put(siteId + "", episodeInfo);

				}
				programmeInfo.put("id", p.getId());
				programmeInfo.put("name", p.getName());
				programmeInfo.put("cate", p.getCate());
				programmeInfo.put("searchNumber", searchNumberMap
						.get(p.getId()));
				programmeInfo.put("siteInfo", siteInfo);
				info.put(programmeInfo);
			}

			long end = System.currentTimeMillis();
			logger.info("methos 2 cost: " + (end - start));
			logger.info("info.length" + info.length());
			logger.info("url size: " + i);
			logger.info("programme site: " + j);
			/*
			 * File file = new File("/opt/search/spider_interface2.json");
			 * BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			 * bw.write(info.toString()); bw.close();
			 */
			return info.toString(4);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}

	public boolean handlePostData(String data) {

		boolean isSuccess = false;

		if (StringUtils.isBlank(data)) {
			return isSuccess;
		}

		try {
			JSONArray jsArr = new JSONArray(data);
			for (int i = 0; i < jsArr.length(); i++) {
				JSONObject jsObj = jsArr.getJSONObject(i);
				logger.info(jsObj);

				int programmeId = jsObj.optInt("juji_id");
				int siteId = jsObj.optInt("site_id");
				String url = jsObj.optString("url");
				String pic = jsObj.optString("pic_youku");
				String title = jsObj.optString("title");
				String timeLength = jsObj.optString("time_length");
				int orderId = jsObj.optInt("jishu");

				int programmeSiteId = ProgrammeSiteService.getProgrammeSiteId(
						programmeId, siteId);

				boolean isNew = false;
				ProgrammeEpisode pe = ProgrammeEpisodeService
						.getUniqueProgrammeEpisode(programmeSiteId, orderId);
				if (pe == null) {
					pe = new ProgrammeEpisode();
					isNew = true;
					pe.setFkProgrammeSiteId(programmeSiteId);
					pe.setOrderId(orderId);
					pe.setOrderStage(orderId);
				}

				pe.setUrl(url);
				pe.setTitle(title);
				pe.setLogo(pic);
				pe.setSource("010");
				pe.setUpdateTime(new Date());
				try {
					pe.setSeconds(Double.valueOf(timeLength));
				} catch (NumberFormatException e) {
					logger.info("NumberFormatException");
				}
				if (isNew) {
					pe.save();
				} else {
					ProgrammeEpisodePeer.doUpdate(pe);
				}
				chagneProgrammSource(programmeId);
				logSpiderEpisode(pe);
			}
			isSuccess = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return isSuccess;
	}

	private void logSpiderEpisode(ProgrammeEpisode pe) throws Exception {
		EpisodeLog el = new EpisodeLog();
		el.setFkProgrammeSiteId(pe.getFkProgrammeSiteId());
		el.setOperator("Spider");
		el.setUrl(pe.getUrl());
		el.setOrderId(pe.getOrderId());
		el.setSeconds(pe.getSeconds());
		el.setSource("010");
		el.setTitle(pe.getTitle());
		// logger.info("====" + el);

		el.save();
	}

	private void chagneProgrammSource(int programmeId) throws Exception {
		Programme p = ProgrammePeer.retrieveByPK(programmeId);
		p.setSource(ManageUtil.changeSourceOtherSite(p.getSource()));
		ProgrammePeer.doUpdate(p);
	}

	class SpiderInfo {

		private int id;

		private String name;

		private int programmeSiteId;

		private int source_site;

		private String url;

		private int orderId;

		private int orderStage;

		private int episodeCollected;

		private int cate;

		private int episodeTotal;

		public int getEpisodeTotal() {
			return episodeTotal;
		}

		public void setEpisodeTotal(int episodeTotal) {
			this.episodeTotal = episodeTotal;
		}

		public int getOrderStage() {
			return orderStage;
		}

		public void setOrderStage(int orderStage) {
			this.orderStage = orderStage;
		}

		public int getCate() {
			return cate;
		}

		public void setCate(int cate) {
			this.cate = cate;
		}

		public int getEpisodeCollected() {
			return episodeCollected;
		}

		public void setEpisodeCollected(int episodeCollected) {
			this.episodeCollected = episodeCollected;
		}

		public int getProgrammeSiteId() {
			return programmeSiteId;
		}

		public void setProgrammeSiteId(int programmeSiteId) {
			this.programmeSiteId = programmeSiteId;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSource_site() {
			return source_site;
		}

		public void setSource_site(int sourceSite) {
			source_site = sourceSite;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getOrderId() {
			return orderId;
		}

		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}

	}
}
