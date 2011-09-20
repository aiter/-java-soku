package com.youku.soku.newext.loader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.monitor.Person;
import com.youku.search.monitor.SmsSender;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.ChannelType;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.ProgrammeSiteType;
import com.youku.soku.newext.util.comparator.SiteComparator;
import com.youku.soku.util.DataBase;

public class UpdateAlias {
	private static Log logger = LogFactory.getLog(UpdateAlias.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final int limit = 1000;
	final int MAX_COUNT = Integer.MAX_VALUE;

	/**
	 * 按照startDate,endDate来更新alias map
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public int doUpdate(Date startDate, Date endDate) {
		logger.info("开始更新aliasInfo ..." + sdf.format(new Date()));
		Cost cost = new Cost();
		AliasInfo aliasInfo = ExtInfoHolder.getCurrentThreadLocal().aliasInfo;
		MovieInfo movieInfo = ExtInfoHolder.getCurrentThreadLocal().movieInfo;
		TeleplayInfo teleplayInfo = ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
		AnimeInfo animeInfo = ExtInfoHolder.getCurrentThreadLocal().animeInfo;
		VarietyInfo varietyInfo = ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
		PersonInfo personInfo = ExtInfoHolder.getCurrentThreadLocal().personInfo;

		Criteria criteria = new Criteria();

		Criteria.Criterion updateTime1 = criteria
				.getNewCriterion(ProgrammeSitePeer.UPDATE_TIME, startDate,
						Criteria.GREATER_THAN);
		Criteria.Criterion updateTime2 = criteria.getNewCriterion(
				ProgrammeSitePeer.UPDATE_TIME, endDate, Criteria.LESS_EQUAL);
		criteria.add(updateTime1.and(updateTime2));
		criteria.add(ProgrammeSitePeer.BLOCKED, 0);

		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.FK_PROGRAMME_ID);

		// 得到该查询条件的总的更新记录条数
		getTotalCount(startDate, endDate);
		int updateProgrammeSiteCount = 0;
		
		String exceptionMessage = "增量更新发生异常：";

		List<ProgrammeSite> list = null;
		logger.info("Current Sql:" + criteria);
		try {
			list = ProgrammeSitePeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("获取programme site list失败");
			logger.error(e.getMessage(), e);

			try {
				logger.info("sending messgage to: " + Person.tanxiuguang.getPhone());
				SmsSender.I.send(exceptionMessage + e.getMessage(), Person.tanxiuguang.getPhone());
			} catch (IOException ex) {
				logger.error(e.getMessage(), e);
			}
		
		}
		// 更新programmeSite_episode map
		if (list != null && list.size() > 0) {
			for (ProgrammeSite programmeSite : list) {
				//logger.info("updatind programme site: " + programmeSite.getId());
				boolean isNewProgramme = false;
				Programme tmpPro = aliasInfo.id_programme.get(programmeSite
						.getFkProgrammeId());
				
				if (tmpPro == null)
					isNewProgramme = true;
				if (!isNewProgramme) {
					// 1）新增站点的情况 2）新增剧集的情况，在updateProgrammeSiteEpisode中已经解决
					// List<ProgrammeSite>
					// tmpProgrammeSiteList=DbUtil.getProgrammeSiteListByProgramme(tmpPro);
					filterProgrammeSite(tmpPro, programmeSite, aliasInfo);

					
				} else {
					// logger.info("updatealias 更新(N)："+tmpPro.getName());
					tmpPro = DbUtil
							.getProgrammeByProgrammeSite(programmeSite);
					if (tmpPro == null || tmpPro.getBlocked() == 1
							|| !tmpPro.getState().equals("normal")) {
						logger.error("programmeSite:"
								+ programmeSite.getId()
								+ " 无法得到对应的programem " + (tmpPro == null ? "programe null" : tmpPro.getBlocked() + tmpPro.getState()));
						continue;
					}
					aliasInfo.id_programme.put(tmpPro.getId(), tmpPro);
					// programme_series
					// AliasInfoLoader.addProgrammeSeries(programme,
					// aliasInfo);

					// middMap
					AliasInfoLoader.addMiddMap(tmpPro, aliasInfo);

					AliasInfoLoader.addProProgrammeSite(tmpPro, aliasInfo);

					if (tmpPro.getPlayUrl() != null
							&& StringUtils.trimToEmpty(tmpPro.getPlayUrl())
									.length() > 0) {

						if (tmpPro.getCate() == ChannelType.MOVIE
								.getValue()) {
							UpdateMovie.doUpdate(tmpPro, movieInfo,
									personInfo);

						} else if (tmpPro.getCate() == ChannelType.TELEPLAY
								.getValue()) {
							UpdateTeleplay.doUpdate(tmpPro, teleplayInfo,
									personInfo);

						} else if (tmpPro.getCate() == ChannelType.ANIME
								.getValue()) {
							UpdateAnime.doUpdate(tmpPro, animeInfo,
									personInfo);

						} else if (tmpPro.getCate() == ChannelType.VARIETY
								.getValue()) {
							UpdateVariety.doUpdate(tmpPro, varietyInfo,
									personInfo);
						}
					}

				}
				updateProgrammeSiteCount++;	
			}
			
		}

		cost.updateEnd();
		logger.info("aliasInfo 更新结束。" + sdf.format(new Date()));
		logger.info("总共耗时(S)：" + cost.getCost() / 1000);
		return updateProgrammeSiteCount;
	}

	private void getTotalCount(Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer sql = new StringBuffer(
				"select count(1) from programme_site ");
		sql
				.append(" WHERE programme_site.blocked=0 AND (programme_site.update_time>'"
						+ sdf.format(startDate)
						+ "' AND "
						+ " programme_site.update_time<='"
						+ sdf.format(endDate)
						+ "')");
		logger.info("the sql:" + sql.toString());

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			pst = conn.prepareStatement(sql.toString());
			
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next()) {
				count = rs.getInt(1);
			}
			
			logger.info("增量加载的programme site个数: " + count);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

	}

	// 对programmeSite 设置 sortMode 并过滤掉没有视频播放地址的站点
	private static void filterProgrammeSite(Programme programme,
			ProgrammeSite programmeSite, AliasInfo aliasInfo) {
		if (programme == null || programmeSite == null)
			return;

		String middStr = aliasInfo.middMap.get(programme.getContentId());
		int programmeSortmode = 0;
		int programmePaid = 0;
		if (middStr != null && middStr.length() > 0) {
			try {
				JSONObject middJson = new JSONObject(middStr);
				programmeSortmode = middJson.optInt("sortmode",
						programmeSortmode);
				programmePaid = middJson.optInt("paid", programmePaid);
			} catch (JSONException e) {
			}
		}
		programme.setPaid(programmePaid);
		if (programmePaid == 1) {// 所有收费节目都不加载
			return;
		}

		programmeSite.setSortmode(programmeSortmode);
		// 添加站点节目-->视频列表
		// TODO 可以根据条件，使视频是倒排还是正排***************************************

		List<ProgrammeEpisode> programmeEpisodeList = null;
		// 只有电影加载order_id=0的视频列表
			programmeEpisodeList = DbUtil.getProgrammeEpisode(programmeSite,
					programmeSortmode == 1, programme.getCate());

		if (programmeEpisodeList == null || programmeEpisodeList.size() == 0) {
			// 如果没有视频列表。移除这个站点
			aliasInfo.programmeSite_episode.remove(programmeSite);
			List<ProgrammeSite> list = aliasInfo.programme_programmeSite.get(programme);
			
			if (list!=null && !list.isEmpty()) {

				// 检查是否只有综合站点和另一个站点,是的话，直接移除综合站点
				
					for (Iterator iterator = list.iterator(); iterator.hasNext();) {
						ProgrammeSite tmpProgrammeSite = (ProgrammeSite) iterator
								.next();
						if (tmpProgrammeSite.getId() == programmeSite.getId()) {
							iterator.remove();
						}
					}
					
					// 将站点排序
					Collections.sort(list, new SiteComparator());
					aliasInfo.programme_programmeSite.put(programme, list);
			}

			
			if (logger.isDebugEnabled()) {
				logger.info("programmeEpisode 不存在： programme's id:"
						+ programme.getId() + "  programmeSite's id:"
						+ programmeSite.getId());
			}
			return;
		}

		programmeSite.setEpisodeCollected(programmeEpisodeList.size());// 设置站点收录的集数
		if (programme.getEpisodeTotal() > 0) {
			/** total为0的都未完成。收录集数大于total的，收录完成 */
			programmeSite
					.setCompleted(programmeSite.getEpisodeCollected() >= programme
							.getEpisodeTotal() ? 1 : 0);
		} else {
			programmeSite.setCompleted(0);
		}

		aliasInfo.programmeSite_episode
				.put(programmeSite, programmeEpisodeList);

		List<ProgrammeSite> list = aliasInfo.programme_programmeSite
				.get(programme);
		if (list == null)
			list = new ArrayList<ProgrammeSite>();

		if (list != null
				&& !list.contains(programmeSite)
				&& programmeSite.getSourceSite() != ProgrammeSiteType.综合
						.getValue()) {
			list.add(programmeSite);
		} else if (list != null && list.contains(programmeSite)) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getId() == programmeSite.getId()) {
					list.set(i, programmeSite);
					break;
				}
			}
		}

		// 去掉无视频链接的节目站点后
		if (!list.isEmpty()) {

			// 检查是否只有综合站点和另一个站点,是的话，直接移除综合站点
			if (list.size() == 2) {
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					ProgrammeSite tmpProgrammeSite = (ProgrammeSite) iterator
							.next();
					if (tmpProgrammeSite.getSourceSite() == ProgrammeSiteType.综合
							.getValue()) {
						iterator.remove();
					}
				}
			}

			// 将站点排序
			Collections.sort(list, new SiteComparator());

			// 设置节目的默认播放链接
			List<ProgrammeEpisode> tmpProgrammeEpisodeList = aliasInfo.programmeSite_episode
					.get(list.get(0));
			if (tmpProgrammeEpisodeList != null
					&& tmpProgrammeEpisodeList.size() > 0) {
				programme.setPlayUrl(tmpProgrammeEpisodeList.get(0).getUrl());
			}

			//添加节目-->节目站点对应关系。
			aliasInfo.programme_programmeSite.put(programme, list);
		}
	}

}
