/**
 * 
 */
package com.youku.soku.newext.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.comparator.DownComparator;
import com.youku.soku.newext.util.comparator.EpisodeOrderComparator;
import com.youku.soku.newext.util.comparator.ProgrammeComparator;
import com.youku.soku.newext.util.comparator.SiteComparator;

/**
 * @author liuyunjian
 * 2011-5-11
 */
public class SortUtil {

	/**
	 * 对一个节目下的<br/>
	 * 1、节目站点排序<br/>
	 * 2、节目站点的视频排序<br/>
	 * 3、节目名-->节目列表排序<br/>
	 * 4、节目所有人物-->节目列表排序
	 * @param programme
	 * @param aliasInfo
	 */
	public static void sortProgramme(Programme programme, AliasInfo aliasInfo,PersonInfo personInfo){
		if(programme==null) return;
		
		List<ProgrammeSite> programmeSites = aliasInfo.programme_programmeSite.get(programme);
		
		int programmeSortmode = getSortmode(programme.getContentId(),aliasInfo);
		
		if(programmeSites!=null && programmeSites.size()>0){
			for (ProgrammeSite programmeSite : programmeSites) {//对各个站点节目的ID，排序
				List<ProgrammeEpisode> programmeEpisodes = aliasInfo.programmeSite_episode.get(programmeSite);
				if(programmeEpisodes!=null && programmeEpisodes.size()>0){
					sortEpisodes(programmeSortmode==1,programmeEpisodes);
				}
			}
			
			//对节目的各个站点排序
			sortProgrammeSites(programmeSites);
		}
		
		//节目名-->对应的节目列表排序
		 List<Programme> proList = null;
		if(aliasInfo instanceof AnimeInfo){
			proList = ((AnimeInfo)aliasInfo).name_programme.get(programme.getName());
		}else if (aliasInfo instanceof MovieInfo) {
			proList = ((AnimeInfo)aliasInfo).name_programme.get(programme.getName());
		}else if (aliasInfo instanceof TeleplayInfo) {
			proList = ((AnimeInfo)aliasInfo).name_programme.get(programme.getName());
		}else if (aliasInfo instanceof VarietyInfo) {
			proList = ((AnimeInfo)aliasInfo).name_programme.get(programme.getName());
		}else {
			
		}
		if(proList!=null && proList.size()>0){
			sortProgrammeByDate(proList,aliasInfo.middMap);
		}
		
		//节目中涉及到的人物-->对应的节目列表排序
		sortPersonProgrammes(programme, aliasInfo,personInfo);
	}

	

	/**
	 * 对节目列表，按发布时间倒排
	 * @param proList
	 * @param middMap
	 */
	public static void sortProgrammeByDate(List<Programme> proList,
			Map<Integer, String> middMap) {
		if(proList==null || proList.size()<=0) 
			return;
		
		ProgrammeComparator comparator=new ProgrammeComparator();
		
		for(Programme programme: proList){
			if(programme==null ) continue;
			
			String middStr=middMap.get(programme.getContentId());
			try {
				if(middStr!=null && middStr.length()>0){
					JSONObject middJson=new JSONObject(middStr);
					programme.setReleaseDate(middJson.optString("releasedate"));
				}else {
					programme.setReleaseDate("1979-00-00");
				}
			} catch (JSONException e) {
				programme.setReleaseDate("1979-00-00");
			}
			
		}
		Collections.sort(proList,comparator);
	}

	/**
	 * 对节目的所有站点排序
	 * @param programmeSites
	 */
	private static void sortProgrammeSites(List<ProgrammeSite> programmeSites) {
		if(programmeSites==null || programmeSites.size()==0){
			return;
		}
		Collections.sort(programmeSites, new SiteComparator());
	}
	
	
	/**
	 *  对一个节目中，涉及到的所有人物-->对应的节目进行排序
	 * @param programme
	 * @param aliasInfo
	 */
	private static void sortPersonProgrammes(Programme programme,
			AliasInfo aliasInfo,PersonInfo personInfo) {
		if(programme==null || aliasInfo == null ||aliasInfo.middMap==null || personInfo==null) {
			return;
		}
		String middStr=aliasInfo.middMap.get(programme.getContentId());
		if(middStr==null || middStr.length()<=0) return;
		
		try {
			JSONObject middJson=new JSONObject(middStr);
			
			//需要排重人物ID
			Set<Integer> persons = new HashSet<Integer>();
			JSONArray performerArr=middJson.optJSONArray("performer");
			JSONArray hostArr=middJson.optJSONArray("host");
			JSONArray directorArr=middJson.optJSONArray("director");
			
			
			if(performerArr!=null && performerArr.length()>0){
				for(int i=0;i<performerArr.length();i++){
					Object tmp = performerArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
					}
				}
				
			}
			if(hostArr!=null && hostArr.length()>0 ){
				for(int i=0;i<hostArr.length();i++){
					Object tmp = hostArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
					}
				}
			}
			
			if(directorArr!=null && directorArr.length()>0){
				for(int i=0;i<directorArr.length();i++){
					Object tmp = directorArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
					}
				}
			}
			
			if(persons.size()==0) return;
			
			for (Integer pid : persons) {
				sortPersonProgramme(pid, personInfo, aliasInfo);
			}
			
		} catch (JSONException e) {
			return;
		}
		
	}
	/**
	 * 对一个人物ID-->对应的节目排序
	 * @param personId
	 * @param personInfo
	 * @param aliasInfo
	 */
	public static void sortPersonProgramme(int personId,PersonInfo personInfo,AliasInfo aliasInfo){
		if(personId==0 || personInfo==null || aliasInfo==null|| personInfo.personproMap==null ||personInfo.personproMap.size()==0){
			return;
		}
		List<Programme> proList = personInfo.personproMap.get(personId);
		
		sortProgrammeByDate(proList, aliasInfo.middMap);
	}

	/**
	 * 对站点节目的视频排序
	 * @param reverse
	 * @param programmeEpisodes
	 */
	public static void sortEpisodes(boolean reverse,
			List<ProgrammeEpisode> programmeEpisodes) {
		if(programmeEpisodes==null || programmeEpisodes.size()==0){
			return;
		}
		if(reverse){
			Collections.sort(programmeEpisodes, new DownComparator<ProgrammeEpisode>(new EpisodeOrderComparator()));
			
		}else {
			Collections.sort(programmeEpisodes, new EpisodeOrderComparator());
		}
	}
	
	/**
	 * 节目名称 对应的节目进行排序
	 * @param name_programme
	 * @param middMap
	 */
	public static void sortProgrammeList(Map<String, List<Programme>> name_programme,Map<Integer,String> middMap) {
		if(name_programme!=null && name_programme.size()>0 && middMap!=null && middMap.size()>0){
		
		Set<String> keySet=new HashSet<String>();
		keySet=name_programme.keySet();
		if(keySet==null || keySet.size()<=0) return;
//		ProgrammeComparator comparator=new ProgrammeComparator();
		for(String key: keySet){
			List<Programme> programmeList=name_programme.get(key);
			if(programmeList==null || programmeList.size()<=0) continue;
			sortProgrammeByDate(programmeList, middMap);
			/*for(Programme programme: programmeList){
				if(programme==null ) continue;
				
				String middStr=middMap.get(programme.getContentId());
				try {
					if(middStr!=null && middStr.length()>0){
						JSONObject middJson=new JSONObject(middStr);
						programme.setReleaseDate(middJson.optString("releasedate"));
					}else {
						programme.setReleaseDate("1979-00-00");
					}
				} catch (JSONException e) {
					programme.setReleaseDate("1979-00-00");
				}
				
			}
			Collections.sort(programmeList,comparator);*/
			
		}
		}
	}

	/**
	 * @param contentId 
	 * @param aliasInfo
	 * @return
	 */
	private static int getSortmode(int contentId, AliasInfo aliasInfo) {
		String middStr=aliasInfo.middMap.get(contentId);
		int programmeSortmode = 0;
		if(middStr!=null && middStr.length()>0) {
			try {
				JSONObject middJson=new JSONObject(middStr);
				programmeSortmode = middJson.optInt("sortmode", programmeSortmode);
			} catch (JSONException e) {
			}
		}
		
		return programmeSortmode;
	}
}
