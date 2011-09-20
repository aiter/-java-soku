package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;

/**
 * 电影加载器
 */
public class MovieLoader {

	private static Log logger = LogFactory.getLog(MovieLoader.class);

	final int MAX_COUNT = Integer.MAX_VALUE;
	final int limit = 1000;

	public void load(MovieInfo info,PersonInfo personInfo) {

		// 加载series_programme 系列名和programme的对应关系
		logger.info("开始加载电影信息。。。");
//		logger.info("开始加载 series_programme 信息...");
//		Criteria criteria = new Criteria();
//		criteria.add(SeriesPeer.CATE, info.cate_id);
//		criteria.addAscendingOrderByColumn(SeriesPeer.ID);
//		criteria.setLimit(limit);
//		int validCount = 0;
//		for (int offset = 0; validCount < MAX_COUNT; offset += limit) {
//			criteria.setOffset(offset);
//			List<Series> list;
//			try {
//				list = SeriesPeer.doSelect(criteria);
//			} catch (TorqueException e) {
//				logger.error("加载series表失败！");
//				e.printStackTrace();
//				return -1;
//			}
//			for (Series series : list) {
//				Set<String> seriesNameSet = new HashSet<String>();
//
//				seriesNameSet = StringUtil.getDistinctName(series.getName(),
//						series.getAlias());
//
//				List<Programme> programmeList = DbUtil
//						.getProgrammeListBySeries(series);
//				
//				if(programmeList==null || programmeList.size()<=0) continue;
//
//				for (String seriesName : seriesNameSet) {
//					MiscUtil.putIfAbsent(info.series_programme, seriesName,
//							new ArrayList<Programme>());
//					info.series_programme.get(seriesName).addAll(programmeList);
//				}
//
//				validCount++;
//			}
//
//			if (list.size() < limit) {
//				break;
//			}
//
//		}

		// 构建检索Map Map<String,List<ProgrammeSite>> name_programmeSite
		// 构建结果map <ProgrammeSite,MovieResult> programmeSite_result
		loadNameProgrammeSite(info,personInfo);

		// 构建人物检索map
//		loadPeopleMovieResult(info);

		logger.info("加载电影信息结束！");

		return;
	}


	/**
	 * 填充ProgrammeSite___episode map 
	 * 
	 * @param info
	 */
	/*public static String loadProgrammeSiteResult(Programme programme, MovieInfo info) {
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
					.getProgrammeEpisode(programmeSite,false);
			
			if(programmeEpisodeList==null || programmeEpisodeList.size()==0 ){
				logger.error("programmeEpisode 不存在： programme's id:"
						+ programme.getId() + "  programmeSite's id:"
						+ programmeSite.getId());
				continue;
			}
			
//			programmeSite.setEpisodeCollected(programmeEpisodeList.size());
			
			
			
			if (playUrl == null){
				playUrl = programmeEpisodeList.get(0).getUrl();
			}
			
			info.programmeSite_episode.put(programmeSite, programmeEpisodeList);

			}
		return playUrl;
	}*/

	

	/**
	 * load name_programmeSite
	 * 
	 * @param programme
	 * @param info
	 * @throws Exception
	 */
	public void loadNameProgrammeSite(MovieInfo info,PersonInfo personInfo){
		// 加载programmeName 和List<ProgrammeSite> 的对应关系 name_programmeSite
		logger.info("开始加载电影 name_programme 信息...");
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
	}


	// 将某个programme 对应的List<ProgrammeSite>加入到 name_programme
	public static void load(Programme programme, MovieInfo info)
			 {
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
			info.name_programme.get(StringUtils.trimToEmpty(proName))
					.add(programme);
			
			if(logger.isDebugEnabled()){
			logger.info("name_programme 加载："+proName);
			}
		}
		
		
	}
	
	

}
