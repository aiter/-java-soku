package com.youku.soku.sort.ext.recommend;

import static com.youku.soku.sort.ext.MiscUtil.putIfAbsent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.soku.index.entity.Video;
import com.youku.soku.manage.torque.KeywordInterven;
import com.youku.soku.manage.torque.KeywordIntervenPeer;
import com.youku.soku.manage.torque.KeywordIntervenVideo;
import com.youku.soku.manage.torque.KeywordIntervenVideoPeer;

public class RecommendLoader {

	static final Log logger = LogFactory.getLog(RecommendLoader.class);

	public static class Data {
		public Map<Integer, List<KeywordIntervenVideo>> keyword_video = new HashMap<Integer, List<KeywordIntervenVideo>>();
		public Map<Integer, KeywordInterven> id_keyword = new HashMap<Integer, KeywordInterven>();
	}

	public static class DataConverter {
		public static RecommendInfo convert(Data data) {
			RecommendInfo recommendInfo = new RecommendInfo();

			for (Map.Entry<Integer, KeywordInterven> entry : data.id_keyword
					.entrySet()) {
				Integer id = entry.getKey();
				KeywordInterven keyword = entry.getValue();

				List<KeywordIntervenVideo> intervenVideos = data.keyword_video
						.get(id);
				List<Video> videos = new ArrayList<Video>();
				for (KeywordIntervenVideo intervenVideo : intervenVideos) {
					recommendInfo.videoCount++;
					videos.add(VideoConverter.convert(intervenVideo));
				}

				final String keywordName = keyword.getName();
				if (recommendInfo.info.containsKey(keywordName)) {
					logger.warn("keyword重复出现了！ " + keywordName);
				}
				recommendInfo.info.put(keywordName, videos);
				recommendInfo.keywordCount++;
			}

			return recommendInfo;
		}
	}

	public static class VideoConverter {
		public static Video convert(KeywordIntervenVideo intervenVideo) {
			Video video = new Video();

			video.id = -1 * intervenVideo.getVideoId();
			video.title = intervenVideo.getName();
			video.url = intervenVideo.getUrl();
			video.seconds = intervenVideo.getVideoLength() + "";
			video.site = intervenVideo.getSource();
			video.tags = intervenVideo.getCategory();
			video.logo = intervenVideo.getPicturePath();

			return video;
		}
	}

	public RecommendInfo load() {
		try {
			logger.info("开始加载推荐视频信息...");

			Data data = new Data();
			loadVideo__(data);
			loadKeyword__(data);

			RecommendInfo info = DataConverter.convert(data);

			logger.info("加载推荐视频信息完毕, " + info);

			return info;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	private void loadVideo__(Data data) throws Exception {

		Criteria criteria = new Criteria();

		criteria.add(KeywordIntervenVideoPeer.EXPIRED_DATE, new Date(),
				SqlEnum.GREATER_THAN);

		criteria.addAscendingOrderByColumn(KeywordIntervenVideoPeer.KEYWORD_ID);
		criteria.addAscendingOrderByColumn(KeywordIntervenVideoPeer.SORT);

		final int limit = 300;
		criteria.setLimit(limit);

		int offset = 0;
		while (true) {
			criteria.setOffset(offset);
			List<KeywordIntervenVideo> videos = KeywordIntervenVideoPeer
					.doSelect(criteria);
			if (videos == null || videos.isEmpty()) {
				break;
			}

			for (KeywordIntervenVideo video : videos) {
				putIfAbsent(data.keyword_video, video.getKeywordId(),
						new ArrayList<KeywordIntervenVideo>());

				data.keyword_video.get(video.getKeywordId()).add(video);
			}

			offset += limit;
		}
	}

	private void loadKeyword__(Data data) throws Exception {

		List<Integer> ids = new ArrayList<Integer>(data.keyword_video.keySet());

		final int totalIds = ids.size();
		if (totalIds == 0) {
			return;
		}

		final int limit = 300;
		for (int fromIndex = 0; fromIndex < totalIds; fromIndex += limit) {
			int toIndex = Math.min(fromIndex + limit, totalIds);

			Criteria criteria = new Criteria();
			criteria.add(KeywordIntervenPeer.KEYWORD_ID, ids.subList(fromIndex,
					toIndex), SqlEnum.IN);

			List<KeywordInterven> list = KeywordIntervenPeer.doSelect(criteria);
			for (KeywordInterven keyword : list) {
				data.id_keyword.put(keyword.getKeywordId(), keyword);
			}
		}
	}
}
