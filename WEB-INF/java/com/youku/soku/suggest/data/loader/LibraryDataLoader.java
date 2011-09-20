package com.youku.soku.suggest.data.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.VideoInfoBo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.entity.DirectoryPersonInfo;
import com.youku.soku.manage.service.VideoInfoService;
import com.youku.soku.suggest.data.LibraryData;
import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.PersonWork;
import com.youku.soku.suggest.parser.WordProcessor;
import com.youku.soku.suggest.util.WordUtil;

public class LibraryDataLoader {

	private Logger log = Logger.getLogger(LibraryDataLoader.class);
	
	private static final int PAID_FLAG = 1;

	public void loadData(LibraryData data) {
		try {
			// data.clear();
			loadProgramme(data.getVersionNamesMap(), data.getEpisodeViewUrlMap());
			loadPersonData(data.getPersonWorksMap());
			data.setNewestVarietyMap(NewestVarietyLoader.getNewestVariety());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void loadProgramme(Map<String, List<NamesEntity>> versionNamesMap, Map<String, String> viewUrlMap) throws Exception {

		log.info("Load Programme TrieTree Data......");
		List<Programme> programmeList = ProgrammePeer.doSelect(new Criteria());
		Set<String> nameSet = new HashSet<String>();

		for (Programme p : programmeList) {
			nameSet.add(p.getName());
		}

		for (Programme p : programmeList) {

			Criteria crit = new Criteria();
			crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			crit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);

			ProgrammeSite ps = null;

			if (psList != null && !psList.isEmpty()) {
				ps = psList.get(0);
			}

			if (ps != null && ps.getEpisodeCollected() > 0) {
				NamesEntity ne = getNamesEntity(p.getName(), p, ps);
				buildProgrammeViewUrlMap(ne, viewUrlMap);
				insertVersionNames(versionNamesMap, ne);
				

				// 将别名也加入
				if (p.getAlias() != null) {
					String[] aliasArr = p.getAlias().split("[|]");
					for (String alias : aliasArr) {
						if (!nameSet.contains(alias)) { //别名不能和已有的剧集名重复
							NamesEntity ane = getNamesEntity(alias, p, ps);
							buildProgrammeViewUrlMap(ane, viewUrlMap);
							insertVersionNames(versionNamesMap, ane);
						}
					}
				}
			}

		}

		log.info("Complete Load Teleplay TrieTree Data");
	}

	private NamesEntity getNamesEntity(String name, Programme p, ProgrammeSite ps) {
		if(name == null) {
			name = "";
		}
		NamesEntity ne = new NamesEntity();
		if (WordProcessor.isChinestWord(name)) { // 去掉名字中的空格。因为综艺节目的命名多含空格
			name = name.replace(" ", "");
		}
		//名字小写，  fix bug 家的N次方
		name = name.toLowerCase();
		ne.setNames(name);

		if (p.getCate() == Constants.TELEPLAY_CATE_ID && p.getEpisodeTotal() == ps.getEpisodeCollected()) {
			ne.setTotalEpisode(p.getEpisodeTotal());
		}
		/*
		 * if (tv.getName() == null) { ne.setVersionName(""); } else { ne.setVersionName(tv.getName()); }
		 */
		ne.setVersionName("");
		ne.setSiteVersionId(ps.getId());
		ne.setCate(p.getCate());
		ne.setProgrammeId(p.getId());
		videoInfoGetter(p, ne);

		return ne;
	}

	private void videoInfoGetter(Programme p, NamesEntity ne) {
		//log.info("Get Video info from youku by http request" + p.getName());
		VideoInfoBo videoInfoBo = new VideoInfoBo();
		videoInfoBo.setContentId(p.getContentId());
		VideoInfoService.videoInfoGetterFromHttp(videoInfoBo);
		ne.setRealeaseTime(WordUtil.parseDate(videoInfoBo.getReleaseDate()));
		ne.setThumb(videoInfoBo.getThumb());
		ne.setPerformers(videoInfoBo.getPerformers());
		ne.setDirectors(videoInfoBo.getDirectors());
		ne.setHosts(videoInfoBo.getHosts());
		ne.setPaid(videoInfoBo.getPaid());
		if(videoInfoBo.isHasYoukuDetail()) {  // 类似海贼王的动漫，站内详情也是没有播放链接的，需要跳转到站外详情页
			ne.setShowIdStr(videoInfoBo.getShowIdStr());
		}
		ne.setHasYoukuDetail(videoInfoBo.isHasYoukuDetail());
	}

	private void buildProgrammeViewUrlMap(NamesEntity e, Map<String, String> viewUrlMap) {
		try {
			
			if(e.getPaid() == PAID_FLAG) {
				return;
			}
			Criteria crit = new Criteria();
			crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, e.getSiteVersionId());
			List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(crit);
			int maxOrderId = 0;

			for (ProgrammeEpisode pe : peList) {
				if (!StringUtils.isBlank(pe.getUrl())) {
					int orderId = pe.getOrderId();
					if (e.getCate() == Constants.VARIETY_CATE_ID) {
						orderId = pe.getViewOrder();
					}
					if(maxOrderId < orderId) {
						maxOrderId = orderId;
					}
					String key = LibraryData.generateEpisodeViewUrlKey(e, orderId + "");
					viewUrlMap.put(key, pe.getUrl());
				}
			}
			e.setMaxOrderId(maxOrderId);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	private void insertVersionNames(Map<String, List<NamesEntity>> versionNamesMap, NamesEntity e) {
		String key = e.getNames().trim() + e.getVersionName().trim();
		List<NamesEntity> list = versionNamesMap.get(key);
		if (list == null) {
			list = new ArrayList<NamesEntity>();
			versionNamesMap.put(key, list);
		}

		// 同名同类型的剧集按时间顺序插入list
		for (int i = 0; i < list.size(); i++) {
			NamesEntity ne = list.get(i);
			if (ne.getCate() == e.getCate()) {
				if (e.getRealeaseTime().after(ne.getRealeaseTime())) {
					list.set(i, e);
				} else {
					continue;
				}
			}
		}
		list.add(e);
	}

	private void loadPersonData(Map<String, List<PersonWork>> personMap) throws Exception {
		log.info("Load Person Data......");
		TopPersonLoader loader = new TopPersonLoader();
		List<String> result = loader.getTopPerson();

		for (String p : result) {
			log.info("load data of person: " + p);
			PersonRelatedWorkFromInterface personDataLoader = new PersonRelatedWorkFromInterface();
			insertPersonWorks(personMap, p, personDataLoader.getPersonRelatedEpisode(p));
		}
		log.info("Complete Load Person Data");
	}

	private void insertPersonWorks(Map<String, List<PersonWork>> personMap, String key, List<PersonWork> pwList) {
		List<PersonWork> list = personMap.get(key);
		if (list == null) {
			list = new ArrayList<PersonWork>();
			personMap.put(key, list);
		}
		if (pwList != null) {
			list.addAll(pwList);
		}
	}

}
