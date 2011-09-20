package com.youku.soku.suggest.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.PersonWork;

public class LibraryData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8523242139838892274L;
	
	private Map<String, List<NamesEntity>> versionNamesMap = new HashMap<String, List<NamesEntity>>();
	
	private Map<String, List<PersonWork>> personWorksMap = new HashMap<String, List<PersonWork>>();
	
	private Map<String, List<String[]>> newestVarietyMap;
	/**
	 * key format   names + versionName + "_cateId_" + cateId + "_O_" orderNumber (movie order number 0)
	 */
	private Map<String, String> episodeViewUrlMap = new HashMap<String, String>(); 		
	
	public List<NamesEntity> searchVersionNames(String keyword) {
		return versionNamesMap.get(keyword);
	}
	
	public Map<String, List<PersonWork>> getPersonWorksMap() {
		return personWorksMap;
	}
	
	public Map<String, List<NamesEntity>> getVersionNamesMap() {
		return versionNamesMap;
	}
	
	public Map<String, String> getEpisodeViewUrlMap() {
		return episodeViewUrlMap;
	}
	
	
	
	public Map<String, List<String[]>> getNewestVarietyMap() {
		return newestVarietyMap;
	}

	public void setNewestVarietyMap(Map<String, List<String[]>> newestVarietyMap) {
		this.newestVarietyMap = newestVarietyMap;
	}

	public static String generateEpisodeViewUrlKey(NamesEntity e, String orderNumber) {
		return e.getNames().trim() + e.getVersionName().trim() + "_cateId_" + e.getCate() +"_O_" + orderNumber;
	}
	
	public void clear() {
		versionNamesMap.clear();
		personWorksMap.clear();
		episodeViewUrlMap.clear();
		versionNamesMap = null;
		personWorksMap = null;
		episodeViewUrlMap = null;
		
		if(newestVarietyMap != null) {
			newestVarietyMap.clear();
			newestVarietyMap = null;
		}
	}
}
