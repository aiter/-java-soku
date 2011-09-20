package com.youku.search.sort.json;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.entity.CategoryCountBean;
import com.youku.search.sort.json.drama.DramaConverter;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.search.AbstractSearchTemplate.SearchResult;
import com.youku.search.sort.search.impl.AdvanceVideoSearch.AdvanceVideoSearchResult;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchOptions;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchResult;
import com.youku.search.util.StringUtil;

public class VideoConverter extends AbstractConverter {

	static Log logger = LogFactory.getLog(VideoConverter.class);

	public static JSONObject convert(Video video) {

		if (video == null) {
			return null;
		}

		try {
			JSONObject object = new JSONObject();

			object.put("vid", video.vid);
			object.put("encodeVid", StringUtil.filterNull(video.encodeVid));
			object.put("title", StringUtil.filterNull(video.title));
			object.put("memo", StringUtil.filterNull(video.memo));
			object.put("tags", StringUtil.filterNull(video.tags));
			object.put("createtime", formatDate(video.createtime));
			object.put("total_pv", video.total_pv);
			object.put("total_comment", video.total_comment);
			object.put("total_fav", video.total_fav);
			object.put("cate_ids", video.cate_ids);
			object.put("logo", StringUtil.filterNull(video.logo));
			object.put("seconds", video.seconds);
			object.put("owner", video.owner);

			String owner_username = video.owner_username;
			object.put("owner_username", StringUtil.filterNull(owner_username));

			object.put("md5", StringUtil.filterNull(video.md5));
			object.put("size", video.size);

			object.put("public_type", video.public_type);

			object.put("ftype", StringUtil.filterNull(video.ftype));

			object.put("title_hl", StringUtil.filterNull(video.title_hl));
			object.put("tags_hl", StringUtil.filterNull(video.tags_hl));
			object.put("username_hl", StringUtil.filterNull(video.username_hl));
			
			if (logger.isDebugEnabled()) {
				object.put("score", video.score);
			}
			
			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(Video video, int dupCount) {

		try {
			JSONObject object = convert(video);

			if (object == null) {
				return null;
			}

			object.put("dup_count", dupCount);

			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(VideoSearchResult searchResult) {

		if (searchResult == null) {
			return null;
		}

		final VideoSearchOptions options = searchResult.options;

		try {
			int total = searchResult.total;

			// items, dupCount
			final int[] dupCount;
			if (options.bitSet.get(options.dupCount)) {
				dupCount = searchResult.dupCount;
			} else {
				dupCount = null;
			}
			JSONObject items = convert(searchResult.page, dupCount);
			items = JSONUtil.newIfNull(items);

			// categories
			JSONObject categories;
			if (options.bitSet.get(options.categoryCount)) {
				categories = VideoCategoryCountBeanConverter
						.convert(searchResult.categoryCount);
				categories = JSONUtil.newIfNull(categories);
			} else {
				categories = null;
			}

			// relation_playlist
			JSONObject folders;
			if (options.bitSet.get(options.relatedFolder)) {
				folders = FolderConverter.convert(searchResult.folderList);
				folders = JSONUtil.newIfNull(folders);
			} else {
				folders = null;
			}

			// drama, zongyi
			JSONObject dramaObject;
			JSONObject zongyiObject;
			if (options.bitSet.get(options.relatedDrama)) {
				dramaObject = DramaConverter.convert(searchResult.drama);
				dramaObject = JSONUtil.newIfNull(dramaObject);

				zongyiObject = DramaConverter.convert(searchResult.zongyi);
				zongyiObject = JSONUtil.newIfNull(zongyiObject);

			} else {
				dramaObject = null;
				zongyiObject = null;
			}

			// suggestion
			String suggestion;
			if (options.bitSet.get(options.suggestionWord)) {
				suggestion = StringUtil.filterNull(searchResult.suggestionWord);
			} else {
				suggestion = null;
			}

			// relevant
			JSONObject relevant;
			if (options.bitSet.get(options.relevantWord)) {
				relevant = JSONUtil.newIfNull(searchResult.relevantWord);
			} else {
				relevant = null;
			}
			
			// majorTerm
			JSONObject majorTerm;
			if (options.bitSet.get(options.majorTerm)) {
				majorTerm = JSONUtil.newIfNull(searchResult.majorTerm);
			} else {
				majorTerm = null;
			}

			// OK!
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("total", total);
			jsonObject.put("items", items);
			jsonObject.put("categories", categories);
			jsonObject.put("relation_playlist", folders);
			jsonObject.put("drama", dramaObject);
			jsonObject.put("zongyi", zongyiObject);
			jsonObject.put(SUGGESTION, suggestion);
			jsonObject.put(RELEVANT_WORDS, relevant);
			jsonObject.put("major_term", majorTerm);

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(List<Video> list, int[] dupCount) {

		if (list == null) {
			return null;
		}

		try {
			JSONObject items = new JSONObject();
			int videoIndex = 0;
			for (ListIterator<Video> i = list.listIterator(); i.hasNext();) {

				Video video = i.next();
				JSONObject videoObject = null;
				int listIndex = i.nextIndex() - 1;

				if (dupCount != null && listIndex < dupCount.length) {
					videoObject = convert(video, dupCount[listIndex]);
				} else {
					videoObject = convert(video);
				}

				if (videoObject != null) {
					items.put(videoIndex + "", videoObject);
					videoIndex++;
				}
			}

			return items;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(AdvanceVideoSearchResult searchResult) {

		if (searchResult == null) {
			return null;
		}

		List<Video> list = searchResult.page;
		int[] dupCount = searchResult.dupCount;
		List<CategoryCountBean> categoryCount = searchResult.categoryCount;

		int total = searchResult.total;

		try {
			JSONObject items = convert(list, dupCount);
			items = JSONUtil.newIfNull(items);

			JSONObject categories = VideoCategoryCountBeanConverter
					.convert(categoryCount);
			categories = JSONUtil.newIfNull(categories);

			// ok
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("total", total);
			jsonObject.put("items", items);
			jsonObject.put("categories", categories);

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(SearchResult<Video> searchResult) {

		if (searchResult == null) {
			return null;
		}

		try {
			JSONObject items = convert(searchResult.page, null);
			items = JSONUtil.newIfNull(items);

			// OK!
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("total", searchResult.total);
			jsonObject.put("items", items);

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

}
