package com.youku.soku.newext.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.util.MiscUtil;
import com.youku.soku.newext.util.SortUtil;
import com.youku.soku.newext.util.middleResource.MiddleResourceUtil;
//
///**
// * 人、人角色
// */
public class PersonInfo implements Serializable {
//
	private static final long serialVersionUID = 1L;
	
	private static Log logger = LogFactory.getLog(PersonInfo.class);
	
	
//  人物头像map
	public Map<Integer,String> personpicMap=new ConcurrentHashMap<Integer,String>();
	
//	演员节目对应关系
	public Map<Integer,List<Programme>> personproMap=new ConcurrentHashMap<Integer,List<Programme>>();
	
	//人物名称对应人物ID
	public Map<String,List<Integer>> nameIdsMap=new ConcurrentHashMap<String,List<Integer>>();
	
	//人物作为嘉宾参加的节目信息，节目信息为json array
	public Map<Integer, String> personGuestProgrammeMap = new HashMap<Integer, String>();
	
	public void sortPersonProgramme(AliasInfo aliasInfo) {
//		Map<String,List<Programme>> personProgrammeMap= personproMap;
		if(personproMap==null || personproMap.size()<=0) return;
		
		Set<Integer> keySet=new HashSet<Integer>();
		keySet=personproMap.keySet();
		if(keySet==null || keySet.size()<=0) return;
//		ProgrammeComparator comparator=new ProgrammeComparator();
		for(Integer key: keySet){
			List<Programme> programmeList=personproMap.get(key);
			if(programmeList==null || programmeList.size()<=0) continue;
			
			SortUtil.sortProgrammeByDate(programmeList, aliasInfo.middMap);
			/*for(Programme programme: programmeList){
				if(programme==null ) continue;
				
				String middStr=aliasInfo.middMap.get(programme.getContentId());
				try {
					JSONObject middJson=new JSONObject(middStr);
					programme.setReleaseDate(middJson.optString("releasedate"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					programme.setReleaseDate("1979-00-00");
				}
				
				
			}
			Collections.sort(programmeList,comparator);*/
			
		}
		
	}
	
	public void addPerson(Programme programme, AliasInfo aliasInfo){
		addPerson(programme, aliasInfo, null, null);
	}
	
	public void addPerson(Programme programme,AliasInfo aliasInfo, JSONArray programmeGuestInfo, Map<Integer, JSONObject> personInfoMap){
		if(programme==null) return;
		
		//添加人物看点
		if(programmeGuestInfo != null) {
			for(int i = 0; i < programmeGuestInfo.length(); i++) {
				JSONObject videoInfoJSONObject = programmeGuestInfo.optJSONObject(i);
				JSONArray guestInfo = videoInfoJSONObject.optJSONArray("guest");
				if(guestInfo != null) {
					for(int j = 0; j < guestInfo.length(); j++) {
						JSONObject guestJSONObject = guestInfo.optJSONObject(j);
						int personId = guestJSONObject.optInt("id");
						String resultInfo = personGuestProgrammeMap.get(personId);
						JSONArray resultJSONArray;
						if(resultInfo == null) {
							resultJSONArray = new JSONArray();
						} else {
							try {
								resultJSONArray = new JSONArray(resultInfo);
							} catch (JSONException e) {
								logger.error(e.getMessage(), e);
								resultJSONArray = new JSONArray();
							}
						}
						
						try {
							JSONObject point = new JSONObject();
							point.put("title", programme.getName() + "  " + videoInfoJSONObject.optString("title"));
							point.put("videoid", videoInfoJSONObject.optString("videoid"));
							point.put("start", guestJSONObject.optString("start"));
							resultJSONArray.put(point);
						} catch (JSONException e) {
							logger.error(e.getMessage(), e);
						}
						
						List<JSONObject> guestPointList = new ArrayList<JSONObject>();
						for(int k = 0; k < resultJSONArray.length(); k++) {
							guestPointList.add(resultJSONArray.optJSONObject(k));
						}
						
						Collections.sort(guestPointList, new Comparator<JSONObject> () {
							@Override
							public int compare(JSONObject o1, JSONObject o2) {
								return o2.optString("show_videostage").compareTo(o1.optString("show_videostage"));
							}
						});
						
						JSONArray sortedResultJSONArray = new JSONArray();
						for(JSONObject obj : guestPointList) {
							sortedResultJSONArray.put(obj);
						}
						personGuestProgrammeMap.put(personId, sortedResultJSONArray.toString());
					}
				}
			}
		}
		
		
		String middStr=aliasInfo.middMap.get(programme.getContentId());
		if(middStr==null || middStr.length()<=0) return;
		
		try {
			JSONObject middJson=new JSONObject(middStr);
			
			//需要排重人物ID
			Set<Integer> persons = new HashSet<Integer>();
			Map<Integer, String> idNameMap = new HashMap<Integer, String>();
			JSONArray performerArr=middJson.optJSONArray("performer");
			JSONArray hostArr=middJson.optJSONArray("host");
			JSONArray directorArr=middJson.optJSONArray("director");
			
			
			if(performerArr!=null && performerArr.length()>0){
				for(int i=0;i<performerArr.length();i++){
					Object tmp = performerArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
						idNameMap.put(personId, (tmp instanceof JSONObject)?((JSONObject)tmp).optString("name"):tmp.toString());
					}
				}
				
			}
			if(hostArr!=null && hostArr.length()>0 ){
				for(int i=0;i<hostArr.length();i++){
					Object tmp = hostArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
						idNameMap.put(personId, (tmp instanceof JSONObject)?((JSONObject)tmp).optString("name"):tmp.toString());
					}
				}
			}
			
			if(directorArr!=null && directorArr.length()>0){
				for(int i=0;i<directorArr.length();i++){
					Object tmp = directorArr.get(i);
					int personId = (tmp instanceof JSONObject)?((JSONObject)tmp).optInt("id"):0;
					if(personId>0){
						persons.add(personId);
						idNameMap.put(personId, (tmp instanceof JSONObject)?((JSONObject)tmp).optString("name"):tmp.toString());
					}
				}
			}
			
			if(persons.size()==0) return;
			
			/** 添加人物ID-->节目对应关系 */
			addPersonProgramme(programme,persons);
			/** 添加人物ID-->人物信息对应关系 */
			addPersonpic(programme,persons, personInfoMap);
			/** 添加人物名称-->人物ID列表对应关系 */
			addNameIds(persons,idNameMap, personInfoMap);
			
		} catch (JSONException e) {
			logger.error("构造json对象失败："+middStr);
			return;
		}
	}

	private void addPersonProgramme(Programme programme,Set<Integer> persons) {
		if(persons.size()==0) return;
		
		for(Integer personId:persons){
			if(personId<=0 ) continue;
			
			MiscUtil.putIfAbsent(personproMap, personId, new ArrayList<Programme>());
			
			
			if(!personproMap.get(personId).contains(programme)){
				personproMap.get(personId).add(programme);
				
			}
			
		}
	}

	/**
	 * 中间层查找和设置内存一个节目的，演员、主持人、导演的对应关系
	 * <p>设置用户ID-->用户信息Map</p>
	 * <p>添加用户名称-->用户ID的List</p>
	 */
	private void addPersonpic(Programme programme,Set<Integer> persons, Map<Integer, JSONObject> personInfoMap) {
		if(persons.size()==0) return;
		for(Integer personId:persons){
			if(personId<=0 ) continue;
			
			JSONObject picJson = null;
			if(personInfoMap == null) {
				picJson=getPersonJson(personId, 2);
			} else {
				picJson = personInfoMap.get(personId);
			}
			if(picJson!=null){
				String picUrl=picJson.toString();
				if(picUrl!=null && StringUtils.trimToEmpty(picUrl).length()>0){
					
					if(!personpicMap.containsKey(personId))
						personpicMap.put(personId,picUrl );
				}
			}else{
				if(logger.isDebugEnabled()){
					logger.info("人物："+personId+"  中间层信息为空");
				}
				personpicMap.put(personId,"");
			}
		}
	}
	
	private JSONObject getPersonJson(int personId, int maxRetriveTime) {
		String fields = "state pk_odperson persondesc personname personalias gender  birthday homeplace height  bloodtype nationality occupation thumburl total_vv";
		JSONObject perJson = null;
		for(int i = 0; i < maxRetriveTime; i++) {
			perJson = MiddleResourceUtil.getPersonById(personId, fields);
			if(perJson != null) {
				return perJson;
			}
		}
		
		
		return null;
	}
	
	/**
	 * 添加用户名称-->用户ID的List
	 */
	private void addNameIds(Set<Integer> persons,Map<Integer, String> idNameMap, final Map<Integer, JSONObject> personInfoMap) {
		if(persons.size()==0) return;
		
		JSONObject perJson = null;
		
		Map<String, Set<Integer>> tmpPersonNameIdsMap = new HashMap<String, Set<Integer>>();
		final Map<Integer, JSONObject> tmpPersonMap = new HashMap<Integer, JSONObject>();  //临时存放的person中间层数据，防止随后的排序再次查中间层
		
		for(Integer personId:persons){
			boolean isNormalPerson = false;
			if(personInfoMap != null) {
				perJson = personInfoMap.get(personId);
			} else {
				perJson = getPersonJson(personId, 4);
				tmpPersonMap.put(personId, perJson);
			}
			
			if(perJson != null) {
				isNormalPerson = "normal".equals(perJson.optString("state"));
			}
			
			if(isNormalPerson) {
				if(personId<=0 ) continue;
				String personName = idNameMap.get(personId);
				
				MiscUtil.putIfAbsent(tmpPersonNameIdsMap, personName, new HashSet<Integer>());
				
				tmpPersonNameIdsMap.get(personName).add(personId);
				
				//人物别名命中 add on 20110905  update on 20111012
				JSONArray aliasArr = perJson.optJSONArray("personalias");
				if(aliasArr != null) {
					for(int i = 0; i < aliasArr.length(); i++) {
						JSONObject aliasObj = aliasArr.optJSONObject(i);
						String alias = aliasObj.optString("alias");
						if(alias.length() > 0) {
							MiscUtil.putIfAbsent(tmpPersonNameIdsMap, alias, new HashSet<Integer>());
							tmpPersonNameIdsMap.get(alias).add(personId);
						}
					}
				}
			
			}
		
		}
		
		//同名人物排序
		for(String key : tmpPersonNameIdsMap.keySet()) {
			Set<Integer> personIds = tmpPersonNameIdsMap.get(key);
			
			if(personIds == null) {
				continue;
			}
			
			List<Integer> personIdList = nameIdsMap.get(key);
			if(personIdList == null) {
				personIdList = new ArrayList<Integer>();
				nameIdsMap.put(key, personIdList);
			}
			personIdList.addAll(personIds);
			
			if(personIds.size() > 1) {
				Collections.sort(personIdList, new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
						JSONObject person1Info = (personInfoMap != null) ? personInfoMap.get(o1) : tmpPersonMap.get(o1);
						JSONObject person2Info = (personInfoMap != null) ? personInfoMap.get(o2) : tmpPersonMap.get(o2);
						
						return person2Info.optInt("total_vv") - person1Info.optInt("total_vv");
					}
					
				});
			}
			
		}
		
	}
	
	public String info() {
		return "{PersonInfo:: person_pic's size: " + personpicMap.size() + "; "
				+ "person_programme's size:"+personproMap.size()+"; "
				+ "name_idList's size:"+nameIdsMap.size()+" }";
	}
	
	public void destroy() {
		personpicMap.clear();
		
		if(personproMap!=null){
			for(List<Programme> list:personproMap.values()){
				list.clear();
			}
			personproMap.clear();
		}
		
		if(nameIdsMap!=null){
			for(List<Integer> list:nameIdsMap.values()){
				list.clear();
			}
			nameIdsMap.clear();
		}
	}
}
