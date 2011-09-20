package com.youku.soku.manage.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.util.ImageUtil;

public class ProgrammeSiteService {
	
	private static final Logger log = Logger.getLogger(ProgrammeSiteService.class);

	public static List<ProgrammeSite> findProgrammeSiteBySite(
			int siteId) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.SOURCE_SITE, siteId);

		List<ProgrammeSite> telList = ProgrammeSitePeer
				.doSelect(crit);
		return telList;

	}
	
	public static List<ProgrammeSite> findProgrammeSiteById(
			int versionId) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);

		List<ProgrammeSite> telSiteList = ProgrammeSitePeer.doSelect(
				crit);
		return telSiteList;

	}
	
	public static ProgrammeSite getDefaultDisplaySite(int versionId) throws TorqueException{
		Criteria crit = new Criteria();
		
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);

		List<ProgrammeSite> telSiteList = ProgrammeSitePeer.doSelect(
				crit);
		if(telSiteList != null && telSiteList.size() > 0) {
			return telSiteList.get(0);
		} else {
			return null;
		}
	}
	
	
	public static int checkAllSiteBlocked(
			int versionId) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);

		List<ProgrammeSite> telSiteList = ProgrammeSitePeer.doSelect(
				crit);
		
		int blockFlag = Constants.BLOCKED;
		for(ProgrammeSite t : telSiteList) {
			if(t.getBlocked() == Constants.UNBLOCKED) {
				blockFlag = Constants.UNBLOCKED;
				break;
			}
		}
		return blockFlag;

	}
	
	public static int checkVideoCompleted(int versionId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.INTEGRATED_SITE_ID);
		
		List<ProgrammeSite> siteList = ProgrammeSitePeer.doSelect(crit);
		
		if(siteList != null && siteList.size() > 0) {
			ProgrammeSite tsv = siteList.get(0);
			return tsv.getCompleted();
		} else {
			return 0;
		}
	}
	

	public static int getSiteEpisodeCollected(int versionId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.INTEGRATED_SITE_ID);
		
		List<ProgrammeSite> siteList = ProgrammeSitePeer.doSelect(crit);
		
		int episodeCollected = 0;
		for(ProgrammeSite tsv : siteList) {
			if(tsv.getEpisodeCollected() > episodeCollected) {
				episodeCollected = tsv.getEpisodeCollected();
			}
		}
		
		return episodeCollected;
	}
	
	public static void saveSiteLogo(int versionId, String logoUrl) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
		
		List<ProgrammeSite> siteList = ProgrammeSitePeer.doSelect(crit);
		
		if(siteList != null && siteList.size() > 0) {
			ProgrammeSite tsv = siteList.get(0);
			tsv.setFirstLogo(logoUrl);
			
			ProgrammeSitePeer.doUpdate(tsv);
		} 
	}
	

	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static List<ProgrammeSite> findProgrammeSite(int versionId, int siteId)
			throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, siteId);
		List<ProgrammeSite> telList = ProgrammeSitePeer.doSelect(
				crit);
		return telList;

	}
	
	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static List<ProgrammeSite> findProgrammeSite(int versionId)
			throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
		crit.addDescendingOrderByColumn(ProgrammeSitePeer.SOURCE_SITE);
		List<ProgrammeSite> telList = ProgrammeSitePeer.doSelect(
				crit);
		return telList;

	}

	
	public static void updateEpisodeCollected(int programmeSiteId) throws Exception{
		if(programmeSiteId < 0) {
			return;
		}
		
		ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(programmeSiteId);
		List<ProgrammeEpisode> peList = ProgrammeEpisodeService.findProgrammeEpisode(programmeSiteId);
		Programme programme = ProgrammePeer.retrieveByPK(ps.getFkProgrammeId());
		
		int episodeCollected = 0;
		String logo = "";
		if(peList != null) {
			for(ProgrammeEpisode pe : peList) {
				if(!StringUtils.isBlank(pe.getUrl())) {
					episodeCollected++;
				}
				
				if(!StringUtils.isBlank(logo)) {
					logo = pe.getLogo();
				}
			}
			
			ps.setEpisodeCollected(episodeCollected);	
			
			
			
			
			if(ImageUtil.isNoImage(ps.getFirstLogo())) {
				ps.setFirstLogo(logo);
			}
			if(programme.getEpisodeTotal() != 0 && episodeCollected >= programme.getEpisodeTotal()) {
				ps.setCompleted(1);
			} else {
				ps.setCompleted(0);
			}
			
			//ps.setUpdateTime(new Date());
			if(ps.isModified()) {
				ps.setUpdateTime(new Date());
				ProgrammeSitePeer.doUpdate(ps);
			}
			
		}
	}
	
	public static void updateEpisodeLogo(int programmeSiteId) throws Exception{
		ProgrammeSite tv = ProgrammeSitePeer.retrieveByPK(programmeSiteId);
		List<ProgrammeEpisode> teList = ProgrammeEpisodeService.findProgrammeEpisode(programmeSiteId);
		Programme programme = ProgrammePeer.retrieveByPK(tv.getFkProgrammeId());
		log.info("begin updateEpisodeLogo at " + new Date());
		log.info("before update " + tv);
		if(teList != null) {			
			String logo = "";
			for(Iterator<ProgrammeEpisode> it = teList.iterator(); it.hasNext();) {
				ProgrammeEpisode te = it.next();
				logo = te.getLogo();
				
				if(logo != null) {
					break;
				}
			}
			
			if(tv.getFirstLogo() == null || tv.getFirstLogo().equals("") || tv.getFirstLogo().contains("0100641F464A92674312AC000000007CFC75D6-1816-BF85-F8D9-AD5E1B3DAD1B")) {
				tv.setFirstLogo(logo);
			}
			
		
			log.info("after update " + tv);
			ProgrammeSitePeer.doUpdate(tv);
		}
	}

	/**
	 * <p>
	 * Find object list using pagination
	 * </p>
	 * 
	 */
	public static void findProgrammeSitePagination(PageInfo pageInfo, int versionId) throws Exception {

		Criteria crit = new Criteria();

		String whereSql = " Where 1 = 1 ";
		

		
		if (versionId > 0) {
			crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, versionId);
			whereSql += "AND " + ProgrammeSitePeer.FK_PROGRAMME_ID + " = "
					+ versionId;
		}

		int totalRecord = ((Record) ProgrammeSitePeer.executeQuery(
				"SELECT count(*) FROM " + ProgrammeSitePeer.TABLE_NAME
						+ whereSql, ProgrammeSitePeer.DATABASE_NAME).get(0)).getValue(1).asInt();

		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		/*
		 * if(searchWord != null && !"".equals(searchWord)) {
		 * crit.add(ProgrammeSitePeer.NAME, (Object) ( "%" + searchWord +
		 * "%"), Criteria.LIKE); }
		 */

		// crit.addDescendingOrderByColumn(ProgrammeSitePeer.SORT);
		// crit.addDescendingOrderByColumn(ProgrammeSitePeer.INDEX_TYPE);
		crit.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);
		crit.addDescendingOrderByColumn(ProgrammeSitePeer.EPISODE_COLLECTED);		
		
		crit.addDescendingOrderByColumn(ProgrammeSitePeer.CREATE_TIME);

		List result = ProgrammeSitePeer.doSelect(crit);

		log.info(crit.toString());
		log.info(result.size());
		pageInfo.setResults(result);

		// return 0;
	}
	
	/**
	 * 
	 * @param programmeId
	 * @param versionId
	 * @param cate
	 * @param operator
	 * @deprecated  use SokuOldDataImport.importEpisodeFromOldDb instead 
	 */
	/*public static boolean importEpisodeFromOldDb(int programmeId, int versionId, int cate, String operator) {
		Connection conn = null; 
		boolean imported = false;
		int defaultViewOrder = 2;
		try {
			
			log.info("versionId: " + versionId);
			conn = Torque.getConnection("old_soku_library");
			Programme p = ProgrammePeer.retrieveByPK(programmeId);
			int cateId = p.getCate();
			
			if(cate == Constants.TELEPLAY_CATE_ID) {
				Criteria crit = new Criteria();
				crit.add(TeleplaySiteVersionPeer.FK_TELEPLAY_VERSION_ID, versionId);
				List<TeleplaySiteVersion> tsvList = TeleplaySiteVersionPeer.doSelect(crit, conn);
				
				if(tsvList == null) {
					return false;
				}
				
				for(TeleplaySiteVersion tsv : tsvList) {	
					if(tsv.getEpisodeCollected() < 1 || tsv.getSourceSite() == Constants.KU6_SITE_ID
							|| tsv.getBlocked() == 1 || tsv.getSourceSite() == Constants.INTEGRATED_SITE_ID) {
						continue;
					}
					int programmeSiteId = getProgrammeSiteId(programmeId, tsv.getSourceSite());
					log.info("teleplay site version: " + tsv.getId());
					Criteria teCrit = new Criteria();
					teCrit.add(TeleplayEpisodePeer.FK_TELEPLAY_SITE_VERSION_ID, tsv.getId());
					List<TeleplayEpisode> teList = TeleplayEpisodePeer.doSelect(teCrit, conn);
					if(teList != null && teList.size() > 0) {
						for(TeleplayEpisode te: teList) {
							if(!StringUtils.isBlank(te.getUrl())) {
								log.info(te.getUrl());
								ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, te.getOrderId());
								if(pe == null) {
									pe = new ProgrammeEpisode();
									pe.setCreateTime(new Date());
								}
								pe.setUrl(te.getUrl());
								pe.setLogo(te.getLogo());
								pe.setFkProgrammeSiteId(programmeSiteId);
								pe.setOrderId(te.getOrderId());
								pe.setHd(te.getHd());
								pe.setTitle(te.getTitle());
								pe.setUpdateTime(new Date());
								pe.setSeconds(te.getSeconds());
								pe.setUpdateTime(new Date());
								pe.setViewOrder(defaultViewOrder);
								pe.setOrderStage(te.getOrderId());
								pe.save();
								
								imported = true;
								//Log URL change
								logEpisodeUrl(pe, cateId, operator);
							}
						}
						
						updateEpisodeCollected(programmeSiteId);
					}
				}
			}
			
			if(cate == Constants.ANIME_CATE_ID) {	
				Criteria crit = new Criteria();
				crit.add(AnimeSiteVersionPeer.FK_ANIME_VERSION_ID, versionId);
				List<AnimeSiteVersion> tsvList = AnimeSiteVersionPeer.doSelect(crit, conn);
				
				if(tsvList == null) {
					return false;
				}
				for(AnimeSiteVersion tsv : tsvList) {
					if(tsv.getEpisodeCollected() < 1 || tsv.getSourceSite() == Constants.KU6_SITE_ID
							|| tsv.getBlocked() == 1 || tsv.getSourceSite() == Constants.INTEGRATED_SITE_ID) {
						continue;
					}
					int programmeSiteId = getProgrammeSiteId(programmeId, tsv.getSourceSite());
					
					Criteria teCrit = new Criteria();
					teCrit.add(AnimeEpisodePeer.FK_ANIME_SITE_VERSION_ID, tsv.getId());
					List<AnimeEpisode> teList = AnimeEpisodePeer.doSelect(teCrit, conn);
					if(teList != null && teList.size() > 0) {
						for(AnimeEpisode te: teList) {
							if(!StringUtils.isBlank(te.getUrl())) {
								ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, te.getOrderId());
								if(pe == null) {
									pe = new ProgrammeEpisode();
									pe.setCreateTime(new Date());
								}
								pe.setUrl(te.getUrl());
								pe.setLogo(te.getLogo());
								pe.setFkProgrammeSiteId(programmeSiteId);
								pe.setOrderId(te.getOrderId());
								pe.setHd(te.getHd());
								pe.setTitle(te.getTitle());
								pe.setUpdateTime(new Date());
								pe.setSeconds(te.getSeconds());
								pe.setUpdateTime(new Date());
								pe.setViewOrder(defaultViewOrder);
								pe.setOrderStage(te.getOrderId());
								pe.save();
								
								imported = true;
								//Log URL change
								logEpisodeUrl(pe, cateId, operator);
							}
						}						
						updateEpisodeCollected(programmeSiteId);
					}
				}
			
				
			}
			
			if(cate == Constants.VARIETY_CATE_ID) {
				Criteria crit = new Criteria();
				crit.add(VarietySubPeer.FK_VARIETY_ID, versionId);
				
				List<VarietySub> vsList = VarietySubPeer.doSelect(crit, conn);
				if(vsList == null) {
					return false;
				}
				
				List<Integer> orderIdList = new ArrayList<Integer>();
				for(VarietySub vs : vsList) {
					String orderIdStr = "" + vs.getYear() + vs.getMonth() + vs.getDay();
					orderIdList.add(Integer.valueOf(orderIdStr));
				}
				Collections.sort(orderIdList);
				
				Map<Integer, Integer> orderIdMap = new HashMap<Integer, Integer>();
				for(int i = 0; i < orderIdList.size(); i++) {
					orderIdMap.put(orderIdList.get(i), i + 1);
				}
				
				for(VarietySub vs : vsList) {
					Criteria veCrit = new Criteria();
					veCrit.add(VarietyEpisodePeer.FK_VARIETY_SUB_ID, vs.getId());
					String month = (vs.getMonth() > 9) ? (vs.getMonth() + "") : ("0" + vs.getMonth());
					String day = (vs.getDay() > 9) ? (vs.getDay() + "") : ("0" + vs.getDay());
					String orderStage = "" + vs.getYear() + month  + day;
					
					String orderIdStr = "" + vs.getYear() + vs.getMonth() + vs.getDay();
					int orderId = orderIdMap.get(Integer.valueOf(orderIdStr));
					List<VarietyEpisode> veList = VarietyEpisodePeer.doSelect(veCrit, conn);
					if(veList != null && !veList.isEmpty()) {
						int programmeSiteId = -1;
						for(VarietyEpisode ve : veList) {
							if(!StringUtils.isBlank(ve.getUrl())) {
								if(ve.getSourceSite() != Constants.KU6_SITE_ID && ve.getSourceSite() != Constants.INTEGRATED_SITE_ID) {
									programmeSiteId = getProgrammeSiteId(programmeId, ve.getSourceSite());
									ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, orderId);
									if(pe == null) {
										pe = new ProgrammeEpisode();
										pe.setCreateTime(new Date());
									}
									pe.setUrl(ve.getUrl());
									pe.setLogo(ve.getLogo());
									pe.setFkProgrammeSiteId(programmeSiteId);
									pe.setOrderId(orderId);
									pe.setHd(ve.getHd());
									pe.setTitle(ve.getTitle());
									pe.setUpdateTime(new Date());
									pe.setSeconds(ve.getSeconds());
									pe.setUpdateTime(new Date());
									pe.setViewOrder(defaultViewOrder);
									pe.setOrderStage(Integer.valueOf(orderStage));
									pe.save();
									
									imported = true;
									//Log URL change
									logEpisodeUrl(pe, cateId, operator);
								}
							}
							updateEpisodeCollected(programmeSiteId);
						}
						
						if(programmeSiteId != -1) {
							Criteria peCrit = new Criteria();
							peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, programmeSiteId);
							peCrit.addAscendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);
						}
						
						
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
		return imported;
	}*/

	public static void logEpisodeUrl(ProgrammeEpisode pe, int cateId, String operator) throws Exception {
		EpisodeLog el = new EpisodeLog();
		el.setCate(cateId);
		el.setCreateTime(new Date());
		el.setHd(pe.getHd());
		el.setLogo(pe.getLogo());
		el.setOrderId(pe.getOrderId());
		el.setTitle(pe.getTitle());
		el.setUrl(pe.getUrl());
		el.setUpdateTime(new Date());
		el.setSeconds(pe.getSeconds());
		el.setFkProgrammeSiteId(pe.getFkProgrammeSiteId());
		el.setSource("");
		el.setOperator(operator);
		el.save();
	}
	
	public static int getProgrammeSiteId(int programmeId, int sourceSite) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programmeId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, sourceSite);
		
		List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
		if(psList != null && !psList.isEmpty()) {
			return psList.get(0).getId();
		} else {
			ProgrammeSite ps = new ProgrammeSite();
			ps.setFkProgrammeId(programmeId);
			ps.setSourceSite(sourceSite);
			ps.save();
			
			log.info("creating programme site: " + ps);
			return ps.getId();
		}
	}
	
	public static ProgrammeSite getSite(int programmeId, int siteId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programmeId);
		crit.add(ProgrammeSitePeer.SOURCE_SITE, siteId);
		
		List<ProgrammeSite> list = ProgrammeSitePeer.doSelect(crit);
		
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else
		return null;
	}
	
	
	

}
