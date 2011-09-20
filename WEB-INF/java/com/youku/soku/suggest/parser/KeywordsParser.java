package com.youku.soku.suggest.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.youku.soku.suggest.data.LibraryData;
import com.youku.soku.suggest.data.serialize.ObjectSaverAndLoader;
import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.PersonWork;

public class KeywordsParser {

	private static Logger log = Logger.getLogger(KeywordsParser.class);
	// private static NamesTrieTree tree = new NamesTrieTree();
	
	private static LibraryData data;
	
	public static void init() {
		ObjectSaverAndLoader dataLoader = new ObjectSaverAndLoader();
		data = dataLoader.loader();
	}
	
	public static void dispose() {
		if(data != null) {
			data.clear();
			data = null;
		}
	}
	
	public static List<PersonWork> getPersonWorks(String keyword) {
		if(data != null) {
			Map<String, List<PersonWork>> personMap = data.getPersonWorksMap();
			if(personMap != null) {
				return personMap.get(keyword);
			} else {
				return null;
			}
			
		} else {
			log.info("Library data is not initialized!");
			return null;
		}
		
	}

	public static List<NamesEntity> parse(String keyword) {
		if(data != null) {
			return data.getVersionNamesMap().get(keyword);
		}else {
			log.info("Library data is not initialized!");
			return null;
		}
	}
	

	public static String getViewUrl(NamesEntity ne, int orderNumber) {
		if(data != null) {
			return data.getEpisodeViewUrlMap().get(LibraryData.generateEpisodeViewUrlKey(ne, orderNumber + ""));
		} else {
			log.info("Library data is not initialized!");
			return null;
		}
	}
	
	public static Map<String, List<String[]>> getNewestVarietyMap() {
		return data.getNewestVarietyMap();
	}

}
