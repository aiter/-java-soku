package com.youku.search.sort.json;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.BarPost;
import com.youku.search.util.StringUtil;

public class BarPostConverter extends AbstractConverter {

	static Log logger = LogFactory.getLog(BarPostConverter.class);

	public static JSONObject convert(BarPost post) {

		if (post == null) {
			return null;
		}

		try {
			JSONObject object = new JSONObject();

			object.put("pk_post", post.pk_post);
			object.put("pk_bar", post.pk_bar);
			object.put("fk_subject", post.fk_subject);
			object.put("bar_name", StringUtil.filterNull(post.bar_name));
			object.put("subject", StringUtil.filterNull(post.subject));
			object.put("content", StringUtil.filterNull(post.content));
			object.put("videoId", post.videoId);
			object.put("encodeVid", StringUtil.filterNull(post.encodeVid));
			object.put("videologo", StringUtil.filterNull(post.videologo));
			object.put("first", post.first);
			object.put("floor", post.floor);
			object.put("poster_id", post.poster_id);
			object.put("poster_name", StringUtil.filterNull(post.poster_name));
			object.put("post_time", formatDate(post.post_time));
			object.put("last_post_time", formatDate(post.last_post_time));

			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(List<BarPost> list, int total) {

		try {
			JSONObject jsonObject = new JSONObject();
			JSONObject itemsObject = new JSONObject();

			jsonObject.put("total", total);
			jsonObject.put("items", itemsObject);

			if (list == null) {
				return jsonObject;
			}

			int index = 0;
			for (int i = 0; i < list.size(); i++) {
				JSONObject object = convert(list.get(i));
				if (object != null) {
					itemsObject.put(index + "", object);
					index++;
				}
			}

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
}
