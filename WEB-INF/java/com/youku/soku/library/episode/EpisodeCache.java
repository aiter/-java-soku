package com.youku.soku.library.episode;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.youku.soku.library.load.EpisodeVideo;

public class EpisodeCache {

	/**
	 * 剧集信息缓存
	 */
	
	/**
	 * programme_site_id -> JSONObject
	 */
	private static Map<Integer, JSONObject> programmeEpisodeMap = new HashMap<Integer, JSONObject>();
	
	/**
	 * videoId -> EpisodeVideo
	 */
	private static Map<String, EpisodeVideo> episodeVideoMap = new HashMap<String, EpisodeVideo>();
	
	
	
}
