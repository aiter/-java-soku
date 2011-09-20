package com.youku.soku.shield;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.soku.manage.entity.HitRolesConstants;
import com.youku.soku.manage.entity.ShieldSiteConstants;
import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.manage.torque.DeterminerWordRelation;
import com.youku.soku.manage.torque.DeterminerWordRelationPeer;
import com.youku.soku.manage.torque.ShieldSite;
import com.youku.soku.manage.torque.ShieldSitePeer;
import com.youku.soku.manage.torque.ShieldWordRelation;
import com.youku.soku.manage.torque.ShieldWordRelationPeer;
import com.youku.soku.manage.torque.ShieldWords;
import com.youku.soku.manage.torque.ShieldWordsPeer;

public class DataLoader {

	private Logger logger = Logger.getLogger(this.getClass());

	private ShieldWordsInfo wordsInfo;
	
	private Map<Integer, String> videoIdNameMap;
	
	private Map<String, Integer> folderNameIdMap;

	public DataLoader(ShieldWordsInfo wordsInfo) {
		this.wordsInfo = wordsInfo;
	}
	

	public void init() {
		wordsInfo.clear();
		/*try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}*/
		initVideoFolderIdNameMap();
		initAccurateHitMatcher(wordsInfo);
		initContainHitWords(wordsInfo);
		initFuzzyHitWords(wordsInfo);
		
	}
	
	private void initVideoFolderIdNameMap() {
		List<Category> videoList = CategoryMap.getInstance().videoList;
		List<Category> folderList = CategoryMap.getInstance().folderList;
		
		videoIdNameMap = new HashMap<Integer, String>();
		folderNameIdMap = new HashMap<String, Integer>();
		
		for(Category vc : videoList) {
			videoIdNameMap.put(vc.getId(), vc.getName());
		}
		
		for(Category vc : folderList) {
			folderNameIdMap.put(vc.getName(), vc.getId());
		}
	}

	private void initAccurateHitMatcher(ShieldWordsInfo wordsInfo) {
		logger.info("加载精确匹配的屏蔽词 ----start----");
		if (wordsInfo.getAccurateHitWordsMap() == null) {
			wordsInfo.setAccurateHitWordsMap(new HashMap<String, ShieldInfo>());
		}
		loadDataFromDb(HitRolesConstants.ACCURATE_HITS, wordsInfo
				.getAccurateHitWordsMap());
		logger.info("加载精确匹配的屏蔽词 ----end----");
	}

	private void initContainHitWords(ShieldWordsInfo wordsInfo) {
		logger.info("加载包含匹配的屏蔽词 ----start----");
		if (wordsInfo.getContainHitWordsMap() == null) {
			wordsInfo.setContainHitWordsMap(new HashMap<String, ShieldInfo>());
		}

		loadDataFromDb(HitRolesConstants.CONTAINS_HITS, wordsInfo
				.getContainHitWordsMap());
		// loadtestdata(wordsInfo.getContainHitWordsMap());
		logger.info("加载包含匹配的屏蔽词 ----end----");
	}

	private void initFuzzyHitWords(ShieldWordsInfo wordsInfo) {
		logger.info("加载模糊匹配的屏蔽词 ----start----");
		if (wordsInfo.getFuzzyHitWordsMap() == null) {
			wordsInfo.setFuzzyHitWordsMap(new HashMap<String, ShieldInfo>());
		}

		loadDataFromDb(HitRolesConstants.FUZZY_HITS, wordsInfo
				.getFuzzyHitWordsMap());
		logger.info("加载模糊匹配的屏蔽词 ----end----");
	}

	public ShieldWordsInfo getWordsInfo() {
		return wordsInfo;

	}

	private Map<String, ShieldInfo> loadDataFromDb(int hitRole,
			Map<String, ShieldInfo> wordsMap) {
		Criteria crit = new Criteria();
		crit.add(ShieldWordsPeer.HIT_ROLE, hitRole);

		List<ShieldWords> result = null;
		try {
			result = ShieldWordsPeer.doSelect(crit);
		} catch (TorqueException e) {
			logger.error(e.getMessage(), e);
		}

		StringBuilder logInfo = new StringBuilder();
		StringBuilder words = new StringBuilder();
		for (ShieldWords sw : result) {
			if(sw.getExpireTime() != null && sw.getExpireTime().before(new Date())) {
				logger.info("Expire Word: " + sw.getWord());
				continue;
			}
			if(sw.getStartTime() != null && sw.getStartTime().after(new Date())) {
				logger.info("not start Word: " + sw.getWord());
				continue;
			}
			ShieldInfo si = buildShieldInfo(sw);
			if(si != null) {
				wordsMap.put(si.getKeyword(), si);
				logInfo.append(si);
				words.append(si.getKeyword()).append('|');
			}
		}

		//logger.info(logInfo.toString());
		//logger.info(words.toString());
		return wordsMap;
	}

	private ShieldInfo buildShieldInfo(ShieldWords sw) {
		try {
			List<Category> channelList = CategoryMap.getInstance().videoList;
			List<DeterminerWordRelation> determinerWordRelationList = DeterminerWordRelationPeer
					.doSelect(new Criteria().add(
							DeterminerWordRelationPeer.FK_WORD_ID, sw.getId()));
			List<ShieldWordRelation> shieldWordRelationList = ShieldWordRelationPeer
					.doSelect(new Criteria().add(
							ShieldWordRelationPeer.FK_WORD_ID, sw.getId()));

			ShieldInfo si = new ShieldInfo();
			List<Integer> channelIdList = new ArrayList<Integer>();

			for (ShieldWordRelation c : shieldWordRelationList) {
				channelIdList.add(c.getFkShieldChannelId());
			}

			si.setShieldChannel(channelIdList);
			si.setFolderChannel(getFolderList(channelIdList));
			if (determinerWordRelationList != null
					&& !determinerWordRelationList.isEmpty()) {
				try {
					DeterminerWordRelation dwr = determinerWordRelationList
							.get(0);

					int siteCategory = dwr.getSiteCategory();
					int siteLevel = dwr.getSiteLevel();

					Criteria crit = new Criteria();
					crit.add(ShieldSitePeer.SITE_CATEGORY, siteCategory);
					crit.add(ShieldSitePeer.SITE_LEVEL, siteLevel,
							Criteria.GREATER_THAN);
					System.out.println(crit.toString());
					List<ShieldSite> shieldSiteList = ShieldSitePeer
							.doSelect(crit);

					if (siteCategory == ShieldSiteConstants.WHITELISTS_SITE) {
						Criteria critw = new Criteria();
						critw.add(ShieldSitePeer.SITE_CATEGORY,
								ShieldSiteConstants.NORMAL_SITE);
						critw.add(ShieldSitePeer.SITE_LEVEL, siteLevel,
								Criteria.GREATER_EQUAL);

						List<ShieldSite> shieldSiteListw = ShieldSitePeer
								.doSelect(critw);
						shieldSiteList.addAll(shieldSiteListw);
					}

					List<Integer> siteIdList = new ArrayList<Integer>();
					for (ShieldSite ss : shieldSiteList) {
						siteIdList.add(ss.getSiteId());
					}

					if (siteCategory == ShieldSiteConstants.WHITELISTS_SITE) {
						si.setWhiteSiteList(siteIdList);
					} else {
						si.setBlackSiteList(siteIdList);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			si.setKeyword(sw.getWord().toLowerCase());
			si.setYoukuEffect(sw.getYoukuEffect());
			si.setOthersEffect(sw.getOthersEffect());
			return si;
		} catch (TorqueException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private List<Integer> getFolderList(List<Integer> channelList) {
		
		if(channelList == null) {
			return null;
		}
		List<Integer> result = new ArrayList<Integer>();
		
		for(Integer channelId : channelList) {
			String channelName = videoIdNameMap.get(channelId);
			
			if(channelName != null) {
				Integer folderId = folderNameIdMap.get(channelName);
				if(folderId != null) {
					result.add(folderId);
				}
			}
		}
		
		return result;
	}

	public void loadtestdata(Map<String, ShieldInfo> wordsMap) {
		try {
			File data = new File("E:\\MyDocument\\soku\\data\\test.txt");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(data), "gbk"));
			String s = null;
			int i = 0;
			while ((s = br.readLine()) != null) {
				ShieldInfo si = new ShieldInfo();
				si.setKeyword(s);
				wordsMap.put(si.getKeyword(), si);
				i++;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
