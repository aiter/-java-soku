package com.youku.search.drama.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.drama.Drama;
import com.youku.search.drama.Episode;
import com.youku.search.drama.EpisodeVideo;
import com.youku.search.drama.Version;
import com.youku.search.drama.Drama.Type;
import com.youku.search.sort.json.drama.DramaConverter;
import com.youku.search.sort.json.drama.EpisodeVideoConverter;

/**
 * Drama信息的本机缓存
 */
public class DramaCache {

	static Log logger = LogFactory.getLog(DramaCache.class);

	/**
	 * drama_id -> drama json string
	 */
	private static Map<String, JSONObject> dramaMap = new ConcurrentHashMap<String, JSONObject>();

	/**
	 * episode_id -> *
	 */
	private static Map<Integer, Episode> episodeMap = new ConcurrentHashMap<Integer, Episode>();

	/**
	 * video_id -> *
	 */
	private static Map<String, EpisodeVideoInfo> videoMap = new ConcurrentHashMap<String, EpisodeVideoInfo>();

	/**
	 * @param videoId
	 *            video id
	 * @return 与该video id对应的drama json串，以及该video所在的drama、version、episode、episode
	 *         order等信息
	 */
	public static JSONObject getByVideoId(String videoId) {

		EpisodeVideoInfo o = videoMap.get(videoId);
		if (o == null) {
			return null;
		}

		JSONObject json = getByDramaId(o.drama_id);
		if (json == null) {
			return null;
		}

		try {
			return buildJsonObject(o, json);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param dramaId
	 *            剧集id
	 * @return 对应的drama json串
	 */
	public static JSONObject getByDramaId(String dramaId) {
		return dramaMap.get(dramaId);
	}

	public synchronized static void init(List<Drama> list,
			boolean initEpisodeVideo) {

		if (list == null || list.isEmpty()) {
			return;
		}

		// 预处理drama信息
		// ...

		// 定义临时map
		Map<String, JSONObject> tmpDramaMap = new HashMap<String, JSONObject>();
		Map<String, EpisodeVideoInfo> tmpVideoMap = new HashMap<String, EpisodeVideoInfo>();
		Map<Integer, Episode> tmpEpisodeMap = new ConcurrentHashMap<Integer, Episode>();

		for (Drama drama : list) {
			if (drama.getVersions() == null || drama.getVersions().isEmpty()) {
				continue;
			}

			// tmpDramaMap
			JSONObject json = DramaConverter.convert(drama, Type.DRAMA);
			if (json != null) {
				tmpDramaMap.put(drama.getId() + "", json);
			}

			// tmpEpisodeMap
			// tmpVideoMap
			if (!initEpisodeVideo) {
				continue;
			}
			for (Version version : drama.getVersions()) {
				if (version.getEpisodes() == null
						|| version.getEpisodes().isEmpty()) {
					continue;
				}

				for (Episode episode : version.getEpisodes()) {
					if (episode.getVideos() == null
							|| episode.getVideos().isEmpty()) {
						continue;
					}

					tmpEpisodeMap.put(episode.getId(), episode); // tmpEpisodeMap

					// 构建cache对象,下面的每一集的每个视频都是指向此对象
					EpisodeVideoInfo info = new EpisodeVideoInfo(""
							+ drama.getId(), version.getId(), episode.getId(),
							episode.getOrder(), episode.getIslock());

//					for (EpisodeVideo video : episode.getVideos()) {
//						tmpVideoMap.put(video.getVideo_id() + "", info); // tmpVideoMap
//					}
					
					EpisodeVideo video =  null;//从2010.11.15.开始isLock的episode下的视频，不再加载到内存。
					for (Iterator<EpisodeVideo> iterator=episode.getVideos().iterator(); iterator.hasNext();) {
						video = iterator.next();
						tmpVideoMap.put(video.getVideo_id() + "", info);
						if(episode.getIslock()==1){
							iterator.remove();
						}
					}
				}
			}
		}

		dramaMap = tmpDramaMap;

		if (initEpisodeVideo) {
			episodeMap = tmpEpisodeMap;
			videoMap = tmpVideoMap;
		}
	}

	/**
	 * 返回与参数episodeVideoId属于同一episode的视频
	 */
	public static JSONObject getEpisodeVideos(int episodeVideoId, int count) {
		if (count < 1) {
			throw new IllegalArgumentException("count must > 0, while is: "
					+ count);
		}

		Episode episode = getEpisode(episodeVideoId);
		if (episode == null) {
			return null;
		}

		List<EpisodeVideo> list = episode.getVideos();
		if (list == null || list.isEmpty()) {
			return null;
		}

		List<EpisodeVideo> videos = new ArrayList<EpisodeVideo>();
		for (EpisodeVideo video : list) {
			if (videos.size() == count) {
				break;
			}
			if (video.getVideo_id() != episodeVideoId) {
				videos.add(video);
			}
		}

		try {
			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray();

			object.put("items", array);
			for (EpisodeVideo video : videos) {
				JSONObject item = EpisodeVideoConverter.convert(video);

				if (item != null) {
					array.put(item);
				}
			}

			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回参数episodeVideoId所属于的episode
	 */
	public static Episode getEpisode(int episodeVideoId) {
		EpisodeVideoInfo info = videoMap.get(String.valueOf(episodeVideoId));
		if (info == null) {
			return null;
		}

		Episode episode = episodeMap.get(info.episode_id);
		return episode;
	}

	private static JSONObject buildJsonObject(EpisodeVideoInfo info,
			JSONObject dramaJson) {

		try {
			JSONObject object = new JSONObject();
			object.put("show", info.version_id);
			object.put("show_info", info.toJsonObject());
			object.put("drama", dramaJson);

			return object;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		EpisodeVideoInfo o = new EpisodeVideoInfo("1", "3", 55, 9, 0);
		JSONObject dramaJson = new JSONObject();

		JSONObject object = buildJsonObject(o, dramaJson);

		System.out.println(object.toString(4));
	}

}
