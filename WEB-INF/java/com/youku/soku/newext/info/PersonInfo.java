package com.youku.soku.newext.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public Map<Integer,String> personpicMap=new HashMap<Integer,String>();
	
//	演员节目对应关系
	public Map<Integer,List<Programme>> personproMap=new HashMap<Integer,List<Programme>>();
	
	//人物名称对应人物ID
	public Map<String,Set<Integer>> nameIdsMap=new HashMap<String,Set<Integer>>();
	
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
	public void addPerson(Programme programme,AliasInfo aliasInfo){
		if(programme==null) return;
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
			addPersonpic(programme,persons);
			/** 添加人物名称-->人物ID列表对应关系 */
			addNameIds(persons,idNameMap);
			
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
	private void addPersonpic(Programme programme,Set<Integer> persons) {
		if(persons.size()==0) return;
		String fields="pk_odperson persondesc personname personalias gender  birthday homeplace height  bloodtype nationality occupation thumburl";
		for(Integer personId:persons){
			if(personId<=0 ) continue;
			
			JSONObject picJson=MiddleResourceUtil.getPersonById(personId,fields);
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
	
	/**
	 * 添加用户名称-->用户ID的List
	 */
	private void addNameIds(Set<Integer> persons,Map<Integer, String> idNameMap) {
		if(persons.size()==0) return;
		
		for(Integer personId:persons){
			if(personId<=0 ) continue;
			String personName = idNameMap.get(personId);
			
			MiscUtil.putIfAbsent(nameIdsMap, personName, new HashSet<Integer>());
			
			nameIdsMap.get(personName).add(personId);
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
			for(Set<Integer> list:nameIdsMap.values()){
				list.clear();
			}
			nameIdsMap.clear();
		}
	}
}
