package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;

/**
 * 电视剧加载器
 */
public class TeleplayLoader {

	private static Log logger = LogFactory.getLog(TeleplayLoader.class);

	final int MAX_COUNT = Integer.MAX_VALUE;
	final int limit = 1000;

	public void load(TeleplayInfo info,PersonInfo personInfo) {

		// 加载series_programme 系列名和programme的对应关系
		logger.info("开始加载电视剧信息。。。");
		
		// 构建检索Map Map<String,List<ProgrammeSite>> name_programmeSite
		// 构建结果map <ProgrammeSite,MovieResult> programmeSite_result
		loadNameProgrammeSite(info,personInfo);

		// 构建人物检索map
//		loadPeopleMovieResult(info);

		logger.info("加载电视剧信息结束！");

		return;
	}

//	private void loadPeopleMovieResult(MovieInfo info) {
//		// TODO Auto-generated method stub
//		List<MovieResult> movieResult = new ArrayList<MovieResult>();
//		movieResult.addAll(info.programmeSite_result.values());
//		for (MovieResult tmpMovieResult : movieResult) {
//			String peoples=tmpMovieResult.getPerformer();
//			if(peoples==null )continue;
//			String[] performers = peoples.split(",");
//			if (performers != null) {
//				for (int i = 0; i < performers.length; i++) {
//					MiscUtil.putIfAbsent(info.people_movieResult, StringUtils
//							.trimToEmpty(performers[i]),
//							new ArrayList<MovieResult>());
//					info.people_movieResult.get(
//							StringUtils.trimToEmpty(performers[i])).add(
//							tmpMovieResult);
//
//				}
//
//			}
//		}
//
//	}

	/**
	 * 填充ProgrammeSite___episode map
	 * 
	 * @param info
	 */
	/*public static String loadProgrammeSiteResult(Programme programme,
			TeleplayInfo info) {
		if (programme == null)
			return null;
		
		int completed=0;
		String middStr=info.middMap.get(programme.getContentId());
		if(middStr!=null && middStr.length()>0){
			try {
				completed=new JSONObject(middStr).optInt("completed");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
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
					.getProgrammeEpisode(programmeSite,completed==0);
			
			if(programmeEpisodeList==null || programmeEpisodeList.size()<=0){
				logger.error("programmeEpisode 不存在： programme's id:"
						+ programme.getId() + "  programmeSite's id:"
						+ programmeSite.getId());
				continue;
			}
			
//			if(completed==0){
//				EpisodeComparator comparator=new EpisodeComparator();
//				Collections.sort(programmeEpisodeList,comparator);
//			}
			
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
	public void loadNameProgrammeSite(TeleplayInfo info,PersonInfo personInfo){
		// 加载programmeName 和List<ProgrammeSite> 的对应关系 name_programmeSite
		logger.info("开始加载电视剧 name_programme 信息...");
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
	public static void load(Programme programme, TeleplayInfo info)
			 {
//		logger.info("电视剧name_programme 加载影片："+programme.getName());
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
