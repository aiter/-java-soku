package com.youku.soku.newext.loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;
import com.youku.soku.newext.util.StringUtil;
import com.youku.soku.newext.util.comparator.ProgrammeComparator;


public class UpdateVariety {
	private static Log logger = LogFactory.getLog(UpdateVariety.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final static int MAX_COUNT = Integer.MAX_VALUE;
	final static int limit = 1000;

	public static void doUpdate(Programme programme, VarietyInfo varietyInfo,
			PersonInfo personInfo) {
		logger.debug("更新varietyInfo ...添加programme:" + programme.getName());
		
		Set<String> programmeNameSet = new HashSet<String>();
		programmeNameSet.add(StringUtils.trimToEmpty(programme.getName().toLowerCase()));
		if (programme.getAlias() != null
				&& StringUtils.trimToEmpty(programme.getAlias()).length() > 0) {
			String[] tmpArr = StringUtils.trimToEmpty(programme.getAlias())
					.split("\\|");
			if (tmpArr != null && tmpArr.length > 0) {
				for (String aliasName : tmpArr)
					programmeNameSet.add(StringUtils.trimToEmpty(aliasName.toLowerCase()));
			}
		}

		if (programmeNameSet == null || programmeNameSet.size() == 0)
			return;
		// 构造节目名/节目别名-->节目列表（列表中含一个或多个节目） 结构
		load(programme, varietyInfo, programmeNameSet);

		// 人物头像 和 人物-->节目列表（列表中含一个或多个节目）
		personInfo.addPerson(programme, varietyInfo);

		// 对name--programme_list的list排序
		for (String proName : programmeNameSet) {
			List<Programme> programmeList = varietyInfo.name_programme
					.get(proName);
			if (programmeList == null || programmeList.size() <= 0)
				continue;
			SortUtil.sortProgrammeByDate(programmeList, varietyInfo.middMap);
		}
		logger.debug("更新varietyInfo。。。 添加programme:" + programme.getName()
				+ "  结束");
	}
	
	// 将某个programme 对应的List<ProgrammeSite>加入到 name_programme
	private static void load(Programme programme, VarietyInfo info,
			Set<String> programmeNameSet) {
		if (programme == null || info == null)
			return;

		if (programmeNameSet == null || programmeNameSet.size() == 0)
			return;

		for (String proName : programmeNameSet) {
			MiscUtil.putIfAbsent(info.name_programme, StringUtils
					.trimToEmpty(proName), new ArrayList<Programme>());

			if (!info.name_programme.get(StringUtils.trimToEmpty(proName))
					.contains(programme)) {
				info.name_programme.get(StringUtils.trimToEmpty(proName)).add(
						programme);

				if (logger.isDebugEnabled()) {
					logger.info("name_programme 加载：" + proName);
				}
			}

		}

	}

	public static void updateSeries(Date startDate, Date endDate,
			VarietyInfo varietyInfo, PersonInfo personInfo) {
		logger.info("更新varietyInfo ...更新系列");
		Criteria criteria = new Criteria();
		Criteria.Criterion updateTime1=criteria.getNewCriterion(ProgrammePeer.UPDATE_TIME, startDate,Criteria.GREATER_THAN);
		Criteria.Criterion updateTime2=criteria.getNewCriterion(ProgrammePeer.UPDATE_TIME, endDate,Criteria.LESS_EQUAL);
		criteria.add(updateTime1.and(updateTime2));
		criteria.add(SeriesPeer.CATE, varietyInfo.cate_id);
		criteria.addAscendingOrderByColumn(SeriesPeer.ID);
		criteria.setLimit(limit);
		int validCount = 0;
		
		Set<String> totalSeriesNameSet=new HashSet<String>();
        
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<Series> list;
			try {
				list = SeriesPeer.doSelect(criteria);
			} catch (TorqueException e) {
				logger.error("综艺频道series获取失败");
				e.printStackTrace();
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
				
				totalSeriesNameSet.addAll(seriesNameSet);

				for (String seriesName : seriesNameSet) {
					MiscUtil.putIfAbsent(varietyInfo.series_programme, seriesName,
							new ArrayList<Programme>());
					
					List<Programme> tmpProgrammeList=varietyInfo.series_programme.get(seriesName);
					for(Programme programme: programmeList){
						if(tmpProgrammeList.contains(programme)) continue;
						tmpProgrammeList.add(programme);
					}
					varietyInfo.series_programme.put(seriesName, tmpProgrammeList);
					logger.info("综艺加载系列："+seriesName);
				}

				validCount++;
			}

			if (list.size() < limit) {
				break;
			}

		}
//		加载完成系列后，按照节目的上线年份倒排
		sortSeriesProgramme(varietyInfo.series_programme,varietyInfo,totalSeriesNameSet);
		
		
		logger.info("加载综艺 series_programme 信息结束");
		
	}
	private static void sortSeriesProgramme(
			Map<String, List<Programme>> seriesProgramme, VarietyInfo info, Set<String> totalSeriesNameSet) {
		if(seriesProgramme==null || seriesProgramme.size()<=0) return;
		
		logger.info("info.middMap's size:"+info.middMap.size());
		
		Set<String> keySet=new HashSet<String>();
		keySet=totalSeriesNameSet;
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
					}else {
						programme.setReleaseDate("1979-00-00");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					programme.setReleaseDate("1979-00-00");
				}
				System.out.println("over");
				
				
			}
			Collections.sort(programmeList,comparator);
			
		}
	
}
}
