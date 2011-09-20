package com.youku.search.sort.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.highlight.Lighter;
import com.youku.highlight.SeparatorHighLighter;
import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Video;
import com.youku.search.index.entity.store.StoreVideo;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.store.Container;
import com.youku.search.store.ContainerFactory;
import com.youku.search.store.ObjectType;

public class StoreVideoUtil {

	public static final int DEFAULT_HL_LEN = 20;
	
	static Log logger = LogFactory.getLog(StoreVideoUtil.class);

	public static String validHighLight(SearchContext<Video> context,
			List<Video> videoList, IndexSearchResult<Video> indexSearchResult){
		List<List<Serializable>> extraCollections = indexSearchResult.extra;

		if (null == videoList || videoList.size() == 0) {
			return null;
		}

		if (null == extraCollections || extraCollections.size() == 0) {
			logger.error("---------- 无法高亮，切词结果为0，用户Query="
					+ context.p.queryOriginal + ", p.type=" + context.p.type);
			return null;
		}

		List<Serializable> extraList = extraCollections.get(0);
		if (null == extraList || extraList.size() == 0) {
			logger.error("---------- 无法高亮，切词结果为0，用户Query="
					+ context.p.queryOriginal + ", p.type=" + context.p.type);
			return null;
		}

		Serializable extra = extraList.get(0);
		if (null == extra || !(extra instanceof String)) {
			logger.error("---------- 无法高亮，切词结果为空，用户Query="
					+ context.p.queryOriginal + ", p.type=" + context.p.type);
			return null;
		}

		String cutStr = (String) extra;
		if (cutStr.length() == 0) {
			logger.error("---------- 无法高亮，切词结果为空，用户Query="
					+ context.p.queryOriginal + ", p.type=" + context.p.type);
			return null;
		}
		
		cutStr = StringUtils.substringBeforeLast(cutStr, "[").trim();
		if (cutStr.length() == 0) {
			logger.error("---------- 无法高亮，切词结果为空，用户Query="
					+ context.p.queryOriginal + ", p.type=" + context.p.type);
			return null;
		}
		
		return cutStr;
	}
	
	/**
	 * 根据result里的切词信息，为Video填充高亮信息
	 * 
	 * @param videoList
	 * @param extraCollections
	 *            切词信息
	 */
	public static void fillHighLightField(SearchContext<Video> context,
			List<Video> videoList, IndexSearchResult<Video> indexSearchResult) {
		if (!context.p.hl) {
			return;
		}
		
		String cutStr = validHighLight(context, videoList, indexSearchResult);
		if (null == cutStr) {
			return;
		}
		
//		AnalyzerHighLighter ahl = AnalyzerHighLighter.DEFAULT;
		SeparatorHighLighter shl = SeparatorHighLighter.DEFAULT;
		
//		ArrayList<char[]> keywordList = AnalyzerHighLighter.getSortedKeysList(cutStr, true);
		String keywordSplit = StringUtils.join(StringUtils.split(cutStr, ' '), ',');
		
		String prefixStr = context.p.hl_prefix;
		String suffixStr = context.p.hl_suffix;
		
		String title;
		String tags;
		String username;
		for (Video video : videoList) {
			title = video.title;
			username = video.owner_username;
			tags = video.tags;
			
//			video.title_hl = ahl.highlighter(title, keywordList, 20, false);
//			video.username_hl = ahl.highlighter(username, keywordList);
			
			video.title_hl = Lighter.I.getBestString(title, cutStr, DEFAULT_HL_LEN, prefixStr, suffixStr);
			video.username_hl = Lighter.I.getBestString(username, cutStr, DEFAULT_HL_LEN, prefixStr, suffixStr);
			video.tags_hl = shl.highlighter(tags, keywordSplit, prefixStr, suffixStr);
		}
	}

	/**
	 * 从memcached中取到StoreVideoList并且转换为VideoList返回 <br>
	 * 注意，只需要取minLuceneResultCount个StoreVideo就可以了 <br>
	 * 
	 * @param context
	 * @param responseList
	 *            已经去重并且重排序好的ResponseList
	 * @return
	 */
	public static List<Video> getStoreVideoList(SearchContext<Video> context,
			List<DefaultResponse> responseList) {

		int getCount = context.minLuceneResultCount;
		if (responseList.size() < context.minLuceneResultCount) {
			getCount = responseList.size();
		}

		List<Video> resultVideoList = new ArrayList<Video>();
		if (getCount == 0) {
			return resultVideoList;
		}

		int fromIndex = 0; // 每次从tmpIndexSearchResult开始取的Index（包含）
		int toIndex = getCount; // 每次从tmpIndexSearchResult取到的Index（不包含）
		int noResultCount = 0;
		int totalNoResultCount = 0;
		do {
			List<Video> storeVideoList = getVideoList(responseList.subList(
					fromIndex, toIndex));
			resultVideoList.addAll(storeVideoList);
			if (resultVideoList.size() >= getCount) {
				break;
			}
			noResultCount = getCount - resultVideoList.size();
			totalNoResultCount += noResultCount;
			fromIndex = toIndex;
			toIndex = fromIndex + noResultCount;
			if (toIndex > responseList.size()) {
				break;
			}

		} while (true);

		logger.debug("------------ 已完成从MemCached中取StoreVideo，成功取到的个数="
				+ resultVideoList.size() + ", 未取到的个数=" + totalNoResultCount);

		return resultVideoList;
	}

	/**
	 * 从Memcached中取到Video信息 <br>
	 * 由于有的DocID可能取不到，所以返回的Video个数可能小于ResponseList个数 <br>
	 * 
	 * @param responseList
	 * @return
	 */
	private static List<Video> getVideoList(List<DefaultResponse> responseList) {
		List<Video> result = new ArrayList<Video>();

		Container container = ContainerFactory.getContainer();
		StoreVideo storeVideo = null;
		Video video = null;
		for (DefaultResponse response : responseList) {
			String key = ObjectType.YOUKU_VIDEO.getKey(response.docID);
			storeVideo = container.get(key);
			if (null == storeVideo) {
				logger.error("从Memcached容器中取不到storeVideo，key=" + key);
				continue;
			}

			video = new Video();
			video.cate_ids = storeVideo.getCate_ids();
			video.createtime = storeVideo.getCreatetime() * 1000;

			video.ftype = storeVideo.getFtype();
			video.logo = storeVideo.getLogo();

			video.md5 = response.md5;
			video.memo = storeVideo.getMemo();

			video.owner = String.valueOf(storeVideo.getOwner());
			video.owner_username = storeVideo.getOwner_username();

			if (null != storeVideo.getPublic_type() && storeVideo.getPublic_type().length() > 0) {
				video.public_type = Integer.parseInt(storeVideo.getPublic_type());
			}
			
			// 这三个score有什么区别？
			video.lucenescore = response.score;
			video.score = response.score;
			video.youkuscore = response.score;

			video.seconds = String.valueOf(storeVideo.getSeconds());

			video.tags = storeVideo.getTags();

			video.title = storeVideo.getTitle();

			video.total_comment = storeVideo.getTotal_comment();
			video.total_fav = storeVideo.getTotal_fav();
			video.total_pv = storeVideo.getTotal_pv();
			
			video.vid = Integer.parseInt(response.docID);
			video.encodeVid = storeVideo.getEncodeVid();

			// video.size =
			// video.tags_hl =
			// video.title_hl =
			// video.username_hl =

			result.add(video);
		}

		return result;
	}

}
