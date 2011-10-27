package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;
import com.youku.soku.newext.util.StringUtil;
import com.youku.soku.newext.util.comparator.ProgrammeComparator;

public class ProgrammeLoader {

	private static Log logger = LogFactory.getLog(ProgrammeLoader.class);

	public void load(AliasInfo info, PersonInfo personInfo, MiddTierResourceBuilder middTierResourceBuilder) {

		// 加载series_programme 系列名和programme的对应关系
		logger.info("开始加载节目信息。。。");

		loadSeries(info);
		loadNameProgrammeSite(info, personInfo, middTierResourceBuilder);

		logger.info("加载节目信息结束！");

		return;
	}
	
	private void loadSeries(AliasInfo info) {
		logger.info("开始加载 series_programme 信息...");
		Criteria criteria = new Criteria();
		criteria.add(SeriesPeer.CATE, info.getCate_id());
		criteria.addAscendingOrderByColumn(SeriesPeer.ID);

		List<Series> list;
		try {
			list = SeriesPeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("series获取失败");
			return;
		}
		for (Series series : list) {
			Set<String> seriesNameSet = new HashSet<String>();

			seriesNameSet = StringUtil.getDistinctName(series.getName(),
					series.getAlias());
			

			List<Programme> programmeList = DbUtil
					.getProgrammeListBySeries(series);
			if (programmeList == null || programmeList.size()<=0)
				continue;
			
			if(seriesNameSet==null || seriesNameSet.size()<=0) continue;

			for (String seriesName : seriesNameSet) {
				MiscUtil.putIfAbsent(info.getSeries_programme(), seriesName,
						new ArrayList<Programme>());
				
				List<Programme> tmpProgrammeList=info.getSeries_programme().get(seriesName);
				for(Programme programme: programmeList){
					if(tmpProgrammeList.contains(programme)) continue;
					programme = info.id_programme.get(programme.getId());
					tmpProgrammeList.add(programme);
				}
				info.getSeries_programme().put(seriesName, tmpProgrammeList);
				logger.info("加载系列："+seriesName+" size::"+(tmpProgrammeList==null?"null":tmpProgrammeList.size()));
			}

		}

		
//		加载完成系列后，按照节目的上线年份倒排
		sortSeriesProgramme(info.getSeries_programme(),info);
		
		logger.info("加载 series_programme 信息结束");
	}

	/**
	 * load name_programmeSite
	 * 
	 * @param programme
	 * @param info
	 * @throws Exception
	 */
	public void loadNameProgrammeSite(AliasInfo info, PersonInfo personInfo, MiddTierResourceBuilder middTierResourceBuilder) {
		// 加载programmeName 和List<ProgrammeSite> 的对应关系 name_programmeSite
		logger.info("开始加载电影 name_programme 信息...");
		int validCount = 0;
		for (Programme programme : info.id_programme.values()) {
			
			if (programme.getCate() != info.getCate_id()) {
				continue;
			}
			load(programme, info);
			JSONArray programmeGuestInfo = middTierResourceBuilder.getVideoGuestMap().get(programme.getContentId());
			personInfo.addPerson(programme, info, programmeGuestInfo, middTierResourceBuilder.getPersonInfoMap());
			validCount++;
		}

		// 对name--programme_list的list排序
		SortUtil.sortProgrammeList(info.getName_programme(), info.middMap);
	}

	// 将某个programme 对应的List<ProgrammeSite>加入到 name_programme
	public static void load(Programme programme, AliasInfo info) {
		
		if (programme == null || info == null)
			return;

		Set<String> programmeNameSet = new HashSet<String>();
		programmeNameSet.add(StringUtils.trimToEmpty(programme.getName().toLowerCase()));

		if (programme.getAlias() != null && StringUtils.trimToEmpty(programme.getAlias()).length() > 0) {
			String[] tmpArr = StringUtils.trimToEmpty(programme.getAlias()).split("\\|");
			if (tmpArr != null && tmpArr.length > 0) {
				for (String aliasName : tmpArr)
					programmeNameSet.add(StringUtils.trimToEmpty(aliasName.toLowerCase()));
			}
		}

		try {
			String jsonStr = info.middMap.get(programme.getContentId());
			if (jsonStr != null) {
				JSONObject jsonObj = new JSONObject(info.middMap.get(programme.getContentId()));
				JSONArray searchKeys = jsonObj.optJSONArray("showkeyword");
				if (searchKeys != null) {
					for (int i = 0; i < searchKeys.length(); i++) {
						logger.debug("showkeyword: " + searchKeys.getString(i));
						programmeNameSet.add(searchKeys.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}

		for (String proName : programmeNameSet) {
			MiscUtil.putIfAbsent(info.getName_programme(), StringUtils.trimToEmpty(proName), new ArrayList<Programme>());
			info.getName_programme().get(StringUtils.trimToEmpty(proName)).add(programme);

			if (logger.isDebugEnabled()) {
				logger.debug("name_programme 加载：" + proName);
			}
		}

	}
	
	
	private static void sortSeriesProgramme(Map<String, List<Programme>> seriesProgramme, AliasInfo info) {
		if(seriesProgramme==null || seriesProgramme.size()<=0) return;
		
		logger.info("info.middMap's size:"+info.middMap.size());
		
		Set<String> keySet=new HashSet<String>();
		keySet=seriesProgramme.keySet();
		if(keySet==null || keySet.size()<=0) return;
		ProgrammeComparator comparator=new ProgrammeComparator();
		for(String key: keySet){
			List<Programme> programmeList=seriesProgramme.get(key);
			if(programmeList==null || programmeList.size()<=0) continue;
			for(Programme programme: programmeList){
				if(programme==null ) continue;
				
				String middStr=info.middMap.get(programme.getContentId());
				try {
//					System.out.println("middStr:"+middStr);
					if(middStr!=null){
						JSONObject middJson=new JSONObject(middStr);
						programme.setReleaseDate(middJson.optString("releasedate"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					programme.setReleaseDate("1979-00-00");
				}
				System.out.println("over");
				
				if(programme.getReleaseDate() == null) {
					programme.setReleaseDate("1979-00-00");
				}
			}
			Collections.sort(programmeList,comparator);
			
		}
		
	}

}
