package com.youku.soku.sort.json;

import static com.youku.search.util.StringUtil.filterNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.soku.index.entity.Video;
import com.youku.soku.index.manager.db.SiteManager;

public class VideoConvertor {

	static Log logger = LogFactory.getLog(VideoConvertor.class);

	public static JSONObject convert(Video video) throws Exception {
		return convert(video, 1);
	}

	public static JSONObject convert(Video video, int dupCount)
			throws Exception {

		JSONObject object = new JSONObject();

		object.put("title", filterNull(video.title));
		object.put("title_hl", filterNull(video.title_hl));
		object.put("tags", filterNull(video.tags));
		object.put("tags_hl", filterNull(video.tags_hl));
		object.put("uploadTime", video.uploadTime);

		String logo = filterNull(video.logo);
		if (logo.equals("") || logo.equalsIgnoreCase("na")) {
			logo = com.youku.soku.util.Constant.DEFAULT_VIDEO_LOGO;
		}
		object.put("logo", logo);

		object.put("seconds", filterNull(video.seconds));
		object.put("hd", video.hd);
		object.put("url", filterNull(video.url));
		object.put("site", filterNull(video.site));
		object.put("site_name", filterNull(getSiteName(video.site)));
		object.put("cate", filterNull(video.cate));
		object.put("dup_count", dupCount);
//		object.put("score", video.score);

		return object;
	}

	static String getSiteName(String siteId) {
		try {
			int id = Integer.parseInt(siteId);
			return SiteManager.getInstance().getName(id);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
}
