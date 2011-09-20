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
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.DbUtil;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;
import com.youku.soku.newext.util.StringUtil;
import com.youku.soku.newext.util.comparator.ProgrammeComparator;

/**
 * 综艺加载器
 */
public class VarietyLoader {

	private static Log logger = LogFactory.getLog(VarietyLoader.class);

	final static int MAX_COUNT = Integer.MAX_VALUE;
	final static int limit = 1000;

	public int load(VarietyInfo info,PersonInfo personInfo){

		logger.info("开始加载综艺频道信息 ...");
		// 加载series_programme 系列名和programme的对应关系
		logger.info("开始加载 series_programme 信息...");
		Criteria criteria = new Criteria();
		criteria.add(SeriesPeer.CATE, info.cate_id);
		criteria.addAscendingOrderByColumn(SeriesPeer.ID);
		criteria.setLimit(limit);
		int validCount = 0;
		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
			criteria.setOffset(offset);
			List<Series> list;
			try {
				list = SeriesPeer.doSelect(criteria);
			} catch (TorqueException e) {
				logger.error("综艺频道series获取失败");
				e.printStackTrace();
				return -1;
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
					MiscUtil.putIfAbsent(info.series_programme, seriesName,
							new ArrayList<Programme>());
					
					List<Programme> tmpProgrammeList=info.series_programme.get(seriesName);
					for(Programme programme: programmeList){
						if(tmpProgrammeList.contains(programme)) continue;
						tmpProgrammeList.add(programme);
					}
					info.series_programme.put(seriesName, tmpProgrammeList);
					logger.info("综艺加载系列："+seriesName+" size::"+(tmpProgrammeList==null?"null":tmpProgrammeList.size()));
				}

				validCount++;
			}

			if (list.size() < limit) {
				break;
			}

		}
//		加载完成系列后，按照节目的上线年份倒排
		sortSeriesProgramme(info.series_programme,info);
		
		
		logger.info("加载综艺 series_programme 信息结束");

		// Map<String,List<ProgrammeSite>> name_programmeSite 遍历programme表
		// programmeSite_result
		loadNameProgrammeSite(info,personInfo);

		logger.info("加载综艺结束！");

		return validCount;
	}

	private static void sortSeriesProgramme(
			Map<String, List<Programme>> seriesProgramme, VarietyInfo info) {
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

	/**
	 * load name_programmeSite 结果map 遍历programme表
	 * 
	 * @param info
	 * @throws Exception
	 */
	public static void loadNameProgrammeSite(VarietyInfo info,PersonInfo personInfo){
		// 加载programmeName 和List<ProgrammeSite> 的对应关系 name_programmeSite
		logger.info("开始加载综艺 name_programme 信息...");
		int validCount = 0;
		for (Programme programme : info.programme_programmeSite.keySet()) {
			if(programme.getCate()!=info.cate_id){
				continue;
			}
				
			load(programme, info);
				
			personInfo.addPerson(programme, info);
			validCount++;
		}
		
		//对name--programme_list的list排序
		SortUtil.sortProgrammeList(info.name_programme,info.middMap);

		logger.info("开始加载综艺 name_programmeSite 信息结束");
	}



	/**
	 * 添加单个programme 到 programme_programmeSite
	 * 
	 * @param programme
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static void load(Programme programme, VarietyInfo info){
//        logger.info("加载programme: programme'name:"+programme.getName());
        if(programme==null || info==null) return;
		
		Set<String> programmeNameSet = new HashSet<String>();
		programmeNameSet.add(StringUtils.trimToEmpty(programme.getName().toLowerCase()));
		if(programme.getAlias()!=null && StringUtils.trimToEmpty(programme.getAlias()).length()>0){
			String[] tmpArr=StringUtils.trimToEmpty(programme.getAlias()).split("\\|");
			if(tmpArr!=null && tmpArr.length>0){
				for(String aliasName: tmpArr)
					programmeNameSet.add(StringUtils.trimToEmpty(aliasName.toLowerCase()));
			}
		}
		
		for (String proName : programmeNameSet) {
			MiscUtil.putIfAbsent(info.name_programme, StringUtils
					.trimToEmpty(proName), new ArrayList<Programme>());
			
			if(!info.name_programme.get(StringUtils.trimToEmpty(proName)).contains(programme)){
				info.name_programme.get(StringUtils.trimToEmpty(proName))
				.add(programme);
			}
			
			if(logger.isDebugEnabled()){
				logger.info("name_programme 加载："+proName);
			}
		}

	}

	/*public static String loadProgrammeSiteResult(Programme programme,
			VarietyInfo info)  {
		
		if (programme == null)
			return null;
		
		Criteria criteria = new Criteria();

		criteria.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programme.getId());
		criteria.add(ProgrammeSitePeer.BLOCKED, 0);
		criteria.addAscendingOrderByColumn(ProgrammeSitePeer.SOURCE);

		List<ProgrammeSite> list;
		try {
			list = ProgrammeSitePeer.doSelect(criteria);
		} catch (TorqueException e) {
			logger.error("获取programmeSite list 失败： programme's id:"
					+ programme.getId());
			e.printStackTrace();
			return null;
		}

		if (list == null || list.size() <= 0){
			logger.warn("该programme's id 没有对应的programmeSite:  programme's id: "+programme.getId());
			return null;
		}
			
		String playUrl = null;
		for (ProgrammeSite programmeSite : list) {
			List<ProgrammeEpisode> programmeEpisodeList = DbUtil
					.getProgrammeEpisode(programmeSite,true);
			
			if(programmeEpisodeList==null || programmeEpisodeList.size()<=0 ){
				logger.error("programmeEpisode 不存在： programme's id:"
						+ programme.getId() + "  programmeSite's id:"
						+ programmeSite.getId());
				continue;
			}
			
//			EpisodeComparator comparator=new EpisodeComparator();
//			Collections.sort(programmeEpisodeList,comparator);
			
			if (playUrl == null){
				playUrl = programmeEpisodeList.get(0).getUrl();
			}
			
			info.programmeSite_episode.put(programmeSite, programmeEpisodeList);

		}
		
		return playUrl;
	}*/

}
