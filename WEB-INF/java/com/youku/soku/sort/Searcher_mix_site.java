package com.youku.soku.sort;

import static com.youku.search.util.StringUtil.filterNull;

import java.io.Serializable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.MemCache;
import com.youku.search.sort.MemCache.StoreResult;
import com.youku.search.sort.core.DuplicateSearchResult;
import com.youku.search.sort.core.IndexSearchResult;
import com.youku.search.sort.core.IndexSearcher;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.json.util.JSONUtil;
import com.youku.search.sort.json.util.JSONUtil.ParseResult;
import com.youku.search.sort.log.RemoteLogger;
import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.store.pic.MemCachedPic;
import com.youku.search.util.Constant;
import com.youku.search.util.MyUtil;
import com.youku.search.util.Wget;
import com.youku.soku.config.Config;
import com.youku.soku.index.entity.Video;
import com.youku.soku.index.query.VideoQueryManager.TimeLength;
import com.youku.soku.manage.entity.SiteWeight;
import com.youku.soku.sort.ext.recommend.RecommendInfoHolder;
import com.youku.soku.sort.json.VideoConvertor;
import com.youku.soku.sort.site_weight.SiteWeightHolder;
import com.youku.soku.sort.site_weight.SiteWeightLoaderTimer;
import com.youku.soku.sort.words.CorrectWords;
import com.youku.soku.sort.words.LikeWords;

/**
 * 只搜索视频的接口的新版本，考虑混合站内站外结果，考虑站点之间数据的分布排序
 */
public class Searcher_mix_site {

	public static Searcher_mix_site INSTANCE = new Searcher_mix_site();

	Log logger = LogFactory.getLog(getClass());
	public static int YoukuSiteId = 14;
	public final int YoukuVideoCount = 700;
	final SiteWeightHolder weightHolder = new SiteWeightHolder();

	CorrectWords correctWordsSearcher = new CorrectWords();
	LikeWords likeWordsSearcher = new LikeWords();

	ExecutorService executorService = Executors.newCachedThreadPool();

	public static class MixResult {
		public SearchContext context;
		public int total;
		public boolean miss;
		public List<Video> videos;
		public Map<Video, Integer> objectMap;
		public JSONArray sites;
		public int searchCount;
		public String costString;
	}

	public static class YoukuResult {
		public int total;
		public LinkedList<Video> videos = new LinkedList<Video>();
	}

	public static class SearchStat {
		public int total;
		public int total_new;

		public int recommend;
		public int youku;
		public int youku_new;
		public int soku;
		public int soku_new;
		public Map<String, AtomicInteger> sokuSite = new HashMap<String, AtomicInteger>();

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("total: " + total + "; ");
			builder.append("total_new: " + total_new + "; ");
			builder.append("recommend: " + recommend + "; ");
			builder.append("youku: " + youku + "; ");
			builder.append("youku_new: " + youku_new + "; ");
			builder.append("soku: " + soku + "; ");
			builder.append("soku_new: " + soku_new + "; ");
			builder.append("sokuSite: " + sokuSite + "; ");
			return builder.toString();
		}
	}

	public Searcher_mix_site() {
		new SiteWeightLoaderTimer(weightHolder).load(true);
	}

	public JSONObject search(Parameter parameter) throws Exception {

		if (parameter.keyword == null || parameter.keyword.length() == 0) {
			return null;
		}

		int totalPages = SearchContext.MIX_TOTAL / parameter.pagesize;
		if (SearchContext.MIX_TOTAL % parameter.pagesize != 0) {
			totalPages++;
		}

		if (parameter.page > totalPages) {
			return null;
		}

		// 重要！
		LinkedHashMap<String, SiteWeight> siteWeights = weightHolder.get();
		parameter._mix = true;
		parameter._mix_cache_key = SiteWeightHolder.getKey(siteWeights);

		SearchContext context = new SearchContext(parameter);

		Cost searchCost = new Cost();

		String correctWord = null;
		if(parameter.corWords){//纠错
			correctWord = correctWordsSearcher.getWord(parameter);
		}
		JSONObject likeWords = null;
		if(parameter.relWords){//相关搜索词
			likeWords = likeWordsSearcher.getWord(parameter);
		}

		// 检查缓存
		ParseResult parseResult = getFromCache(context);
		if (parseResult.valid) {
			parseResult.object.put("correct_word", filterNull(correctWord));
			parseResult.object.put("like_words", likeWords == null ? ""
					: likeWords);

			searchCost.updateEnd();

			// log
			if (parameter._ignore_log == false) {
				LogInfo logInfo = buildLogInfo(context, true,
						parseResult.object.optInt("total"), parseResult.object
								.optInt("page"), searchCost.getCost(), false,
						0, null);

				RemoteLogger.log(RemoteLogger.sokuQuery, logInfo);
			}
			
			return parseResult.object;
		}

		// 缓存无效，查询lucene
		MixResult mixResult = getMixResult(siteWeights, context);
		searchCost.updateEnd();
		
		// build
		final JSONObject object;
		{
			int start = (parameter.page - 1) * parameter.pagesize;
			int end = Math.min(start + parameter.pagesize, mixResult.videos
					.size());

			List<Video> page;
			if (start < end) {
				page = mixResult.videos.subList(start, end);
			} else {
				page = new ArrayList<Video>(0);
			}
			
			//TODO 2011.3.25 获取本页数据，有截图的截图信息
			Map<String, Object> picInfos = null;
			if(parameter.picInfo && Config.getPicInfosStatus() > 0){//参数可控和配置可控
				List<String> ids = new ArrayList<String>();
				for (Video video : page) {
					if("14".equals(video.site)){
						ids.add(Integer.toString(MyUtil.decodeVideoUrl(video.url)));
					}
				}
				if(ids.size()>0){
					picInfos = MemCachedPic.cacheGetByVid(ids.toArray(new String[0]));
				}
			}
			object = buildJsonObject(mixResult.total, mixResult.miss, page,
					mixResult.objectMap, mixResult.sites, searchCost,picInfos);
		}

		object.put("correct_word", filterNull(correctWord));
		object.put("like_words", likeWords == null ? "" : likeWords);
		
		storeToMemcache(mixResult.total, mixResult.miss, mixResult.objectMap,
				mixResult.sites, searchCost, mixResult.videos, context);

		// log
		if (parameter._ignore_log == false) {
			LogInfo logInfo = buildLogInfo(context, false, object
					.optInt("total"), object.optInt("page"), searchCost
					.getCost(), mixResult.miss, mixResult.searchCount,
					mixResult.costString);

			RemoteLogger.log(RemoteLogger.sokuQuery, logInfo);
			logger.debug(logInfo);
		}
		return object;
	}

	/**
	 * 先抓取缓存结果
	 */
	protected ParseResult getFromCache(SearchContext context) {
		ParseResult parseResult = new ParseResult();

		if (context.param.__ic) {
			if (logger.isDebugEnabled()) {
				logger.debug("__ic参数存在，无须读取缓存");
			}

			return parseResult;
		}

		try {
			Cost cost = new Cost();

			String cached = (String) MemCache.cacheGet(context.cacheKey, Config
					.getCacheTimeOut());

			int length = cached == null ? -1 : cached.length();

			cost.updateEnd();
			if (logger.isDebugEnabled()) {
				logger.debug("读取缓存的cost: " + cost.getCost() + "; cacheKey: "
						+ context.cacheKey + "; 缓存内容长度: " + length);
			}

			cost.updateStart();
			parseResult = JSONUtil.tryParse(cached);
			cost.updateEnd();
			if (logger.isDebugEnabled()) {
				logger.debug("解析缓存为json的cost: " + cost.getCost()
						+ "; cacheKey: " + context.cacheKey + "; 缓存内容长度: "
						+ length);
			}

		} catch (Exception e) {
			logger.error("查询memcache发生异常！", e);
		}

		return parseResult;
	}

	protected MixResult getMixResult(
			LinkedHashMap<String, SiteWeight> siteWeights, SearchContext context) {

		Cost cost = new Cost();
		SearchStat stat = new SearchStat();

		List<Video> recommendVideos = getFromRecommend(context);
		cost.updateEnd();
		logger.debug("getFromRecommend 耗时："+ cost);
		cost.updateStart();
		
		YoukuResult youkuResult = getFromYouku(context);
		
		cost.updateEnd();
		logger.debug("getFromYouku 耗时："+ cost);
		cost.updateStart();
		
		IndexSearchResult<Video> sokuResult = getFromSoku(context);

		cost.updateEnd();
		logger.debug("getFromLucene 耗时："+ cost);
		cost.updateStart();
		
		stat.recommend = recommendVideos.size();
		stat.youku = stat.youku_new = youkuResult.videos.size();
		stat.soku = stat.soku_new = sokuResult.list.size();
		stat.total = stat.total_new = recommendVideos.size()
				+ youkuResult.total + sokuResult.totalCount;

		// 站外按站点分类
		LinkedHashMap<String, LinkedList<Video>> sokuSiteVideos = new LinkedHashMap<String, LinkedList<Video>>();

		for (Video video : sokuResult.list) {
			LinkedList<Video> videos = sokuSiteVideos.get(video.site);
			if (videos == null) {
				videos = new LinkedList<Video>();
				sokuSiteVideos.put(video.site, videos);
			}
			videos.add(video);

			AtomicInteger count = stat.sokuSite.get(video.site);
			if (count == null) {
				stat.sokuSite.put(video.site, new AtomicInteger(1));
			} else {
				count.incrementAndGet();
			}
		}

		// 打印stat信息
		if (logger.isDebugEnabled()) {
			logger.debug("stat: " + stat);
		}

		// 重新调整站点顺序
		LinkedHashMap<String, SiteWeight> validSiteWeights = new LinkedHashMap<String, SiteWeight>();
		if (siteWeights != null) {
			LinkedHashMap<String, LinkedList<Video>> siteVideos_weightOrder = new LinkedHashMap<String, LinkedList<Video>>();

			for (String site : siteWeights.keySet()) {

				SiteWeight weight = siteWeights.get(site);
				if (weight.getNormalWeight() <= 0) {
					continue;
				}

				if (weight.getFkSiteId() == YoukuSiteId) {
					validSiteWeights.put(site, weight);

				} else if (sokuSiteVideos.containsKey(site)) {

					validSiteWeights.put(site, weight);
					siteVideos_weightOrder.put(site, sokuSiteVideos.get(site));
				}
			}

			siteVideos_weightOrder.putAll(sokuSiteVideos);

			boolean useSiteWeightsOrder = false;
			if (useSiteWeightsOrder) {
				sokuSiteVideos = siteVideos_weightOrder;
			}
		}

		if (logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("\n");
			builder.append("原始的siteWeights: ");
			builder.append(siteWeights);

			builder.append("\n\n");
			builder.append("有效的siteWeights: ");
			builder.append(validSiteWeights);

			logger.debug(builder.toString());
		}

		// 重排序: 开始
		LinkedList<Video> newVideos = new LinkedList<Video>();
		newVideos.addAll(recommendVideos);

		// 重排序: 控制前10条中优酷的数量
		LinkedList<Video> newVideos_first10 = new LinkedList<Video>();
		if (!youkuResult.videos.isEmpty() && !sokuSiteVideos.isEmpty()) {
			final int count = 10;
			final int youku_count_1 = 1;
			final int youku_count_2 = 5;

			ArrayList<Video> youku_1 = new ArrayList<Video>(youku_count_1);
			ArrayList<Video> youku_2 = new ArrayList<Video>(youku_count_2);
			ArrayList<Video> other_videos = new ArrayList<Video>(count);

			for (int i = 0; i < youku_count_1 && !youkuResult.videos.isEmpty(); i++) {
				youku_1.add(youkuResult.videos.removeFirst());
			}

			for (int i = 0; i < youku_count_2 && !youkuResult.videos.isEmpty(); i++) {
				youku_2.add(youkuResult.videos.removeFirst());
			}

			int other_count__ = count - youku_1.size() - youku_2.size();

			LinkedList<String> sites = new LinkedList<String>();
			sites.addAll(sokuSiteVideos.keySet());
			while (!sites.isEmpty() && other_videos.size() < other_count__) {
				String site = sites.removeFirst();
				LinkedList<Video> list = sokuSiteVideos.get(site);

				if (!list.isEmpty()) {
					other_videos.add(list.removeFirst());
					sites.addLast(site);
				}
			}

			other_videos.addAll(youku_2);
			Collections.shuffle(other_videos);

			newVideos_first10.addAll(youku_1);
			newVideos_first10.addAll(other_videos);
		}

		newVideos.addAll(newVideos_first10);

		// 重排序: 头3页百分比控制显示
		final int priorVideosCountMax = 3 * context.param.pagesize
				- newVideos_first10.size();
		int priorVideosCount = 0;

		LinkedHashMap<String, LinkedList<Video>> priorSiteVideos = new LinkedHashMap<String, LinkedList<Video>>();
		priorSiteVideos.put(String.valueOf(YoukuSiteId),
				new LinkedList<Video>());
		for (String site : sokuSiteVideos.keySet()) {
			priorSiteVideos.put(site, new LinkedList<Video>());
		}

		for (String site : validSiteWeights.keySet()) {

			SiteWeight weight = validSiteWeights.get(site);
			LinkedList<Video> siteVideos;

			if (weight.getFkSiteId() == YoukuSiteId) {
				siteVideos = youkuResult.videos;
			} else {
				siteVideos = sokuSiteVideos.get(site);
			}

			int count = Math.max(1, weight.getNormalWeight()
					* priorVideosCountMax / 100);

			LinkedList<Video> priorVideos = priorSiteVideos.get(site);
			for (int i = 0; i < count && !siteVideos.isEmpty(); i++) {
				priorVideos.add(siteVideos.removeFirst());
				priorVideosCount++;
			}
		}

		// 如果百分比没有取满，用没有指定百分比的数据填充
		if (priorVideosCount < priorVideosCountMax) {
			LinkedList<String> allSites = new LinkedList<String>();
			allSites.add(String.valueOf(YoukuSiteId));
			allSites.addAll(sokuSiteVideos.keySet());
			allSites.removeAll(validSiteWeights.keySet());

			while (priorVideosCount < priorVideosCountMax
					&& !allSites.isEmpty()) {

				String site = allSites.removeFirst();
				LinkedList<Video> siteVideos;

				if (site.equals(String.valueOf(YoukuSiteId))) {
					siteVideos = youkuResult.videos;
				} else {
					siteVideos = sokuSiteVideos.get(site);
				}

				if (siteVideos.isEmpty()) {
					continue;
				}

				priorSiteVideos.get(site).add(siteVideos.removeFirst());
				priorVideosCount++;
				allSites.addLast(site);
			}
		}
		// 还没有取满，用youku的填充
		while (priorVideosCount < priorVideosCountMax
				&& !youkuResult.videos.isEmpty()) {
			LinkedList<Video> youkuVideos = priorSiteVideos.get(String
					.valueOf(YoukuSiteId));
			youkuVideos.add(youkuResult.videos.removeFirst());
			priorVideosCount++;
		}

		resort(priorSiteVideos, newVideos);

		LinkedHashMap<String, LinkedList<Video>> allSiteVideos = new LinkedHashMap<String, LinkedList<Video>>();
		allSiteVideos.put(String.valueOf(YoukuSiteId), youkuResult.videos);
		allSiteVideos.putAll(sokuSiteVideos);
		resort(allSiteVideos, newVideos);

		
		cost.updateEnd();
		logger.debug("二次排序 耗时："+ cost);
		cost.updateStart();
		
		// build
		MixResult mixResult = new MixResult();
		mixResult.context = context;
		mixResult.total = stat.total_new;
		mixResult.miss = sokuResult.miss;
		mixResult.videos = newVideos;

		if (sokuResult instanceof DuplicateSearchResult) {
			mixResult.objectMap = ((DuplicateSearchResult) sokuResult).objectMap;
		} else {
			mixResult.objectMap = null;
		}

		mixResult.sites = getSites(context, sokuResult);
		mixResult.searchCount = sokuResult.searchCount;
		mixResult.costString = sokuResult.costString();

		return mixResult;
	}

	List<Video> getFromRecommend(SearchContext context) {

		List<Video> list = RecommendInfoHolder.info.info
				.get(context.param.keyword);

		if (list == null) {
			list = new ArrayList<Video>(0);
		}

		return list;
	}

	YoukuResult getFromYouku(SearchContext context) {

		Parameter p = context.param;
		YoukuResult youkuResult = new YoukuResult();

		if (p.site > 0) {
			if (p.site != YoukuSiteId) {
				logger.debug("不需要查询youku视频数据");
				return youkuResult;
			}
		} else {// 如果没有指定站点，那么检查白名单中，是否有youku 。 by aiter 2010.11.5
			if (p.include_sites != null && p.include_sites.length > 0) {
				boolean includeYouku = false;
				for (int site : p.include_sites) {
					if (site == YoukuSiteId) {
						includeYouku = true;
					}
				}
				if (!includeYouku) {
					logger.debug("白名单不为空，而且不包含youku,所有不需要查询youku视频数据");
					return youkuResult;
				}
			}
		}

		if (p.exclude_sites != null) {
			for (int site : p.exclude_sites) {
				if (site == YoukuSiteId) {
					logger.debug("不需要查询youku视频数据");
					return youkuResult;
				}
			}
		}

		logger.debug("需要查询youku视频数据");

		StringBuilder url = new StringBuilder();
		try {
			/*List<String> ipList = new ArrayList<String>();
			ipList.add("10.101.8.61");
			ipList.add("10.101.8.62");
			ipList.add("10.101.8.63");
			ipList.add("10.101.8.64");
			ipList.add("10.101.8.65");
			ipList.add("10.101.8.66");*/

			url.append("http://");
//			url.append(ipList.get(new Random().nextInt(ipList.size())));
			//2011.2.11 改为使用虚拟ip  by aiter  2.14 modify 10.101.88.211 to 10.103.88.151
//			url.append("10.103.88.151");
			String youkuHost = Config.getYoukuHost();
			if(youkuHost.isEmpty()){
				logger.error("youku host is Invalide");
				return youkuResult;
			}else {
				url.append(youkuHost);
			}
			
			url.append("/search?");

			url.append("keyword=");
			url.append(URLEncoder.encode(p.keyword, "utf8"));
			url.append("&");

			url.append("type=1&");
			url.append("curpage=1&");

			url.append("pagesize=");
			url.append(YoukuVideoCount);
			url.append("&");

			TimeLength timeLength = new TimeLength(p.time_length);
			url.append("timeless=");
			url.append((int) (timeLength.getLess() / 60));
			url.append("&");

			url.append("timemore=");
			url.append((int) (timeLength.getMore() / 60));
			url.append("&");

			url.append("limit_date=");
			url.append(p.limit_date == 0 ? "" : p.limit_date);
			url.append("&");

			url.append("orderfield=null&");
			url.append("order=1&");

			if (p.hd == 1) {
				url.append("ftype=1&");
			}

			url.append("hl=true&");
			url.append("video_options=1000000&");
			url.append("_source=soku&");

			if (logger.isDebugEnabled()) {
				logger.debug("获取youku视频, url: " + url);
			}

			String response = new String(Wget.get(url.toString(), 3000), "utf8");
			JSONObject youku = new JSONObject(response);

			youkuResult.total = youku.optInt("total", 0);

			CategoryMap categoryMap = CategoryMap.getInstance();

			JSONObject items = youku.optJSONObject("items");
			if (items == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("youku返回视频中没有找到 items 属性");
				}
				return youkuResult;
			}

			Iterator<String> iterator = items.keys();
			while (iterator.hasNext()) {
				JSONObject item = items.getJSONObject(iterator.next());

				Video v = new Video();
				v.id = -1 * item.getInt("vid");
				v.title = item.optString("title");
				v.tags = item.optString("tags");

				String uploadTime = item.optString("createtime");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = format.parse(uploadTime);
				v.uploadTime = date.getTime();

				v.logo = item.optString("logo");
				v.seconds = item.optString("seconds");

				v.hd = 0;
				String ftype = item.optString("ftype");
				String[] ftypes = ftype.split(",");
				for (String f : ftypes) {
					if ("1".equals(f.trim())) {
						v.hd = 1;
						break;
					}
				}

				String encodeVid = item.getString("encodeVid");
				v.url = "http://v.youku.com/v_show/id_" + encodeVid + ".html";

				v.site = String.valueOf(YoukuSiteId);
				v.cate = categoryMap.getNameById(item.optInt("cate_ids"));
				v.title_hl = item.optString("title_hl");
				v.tags_hl = item.optString("tags_hl");
				v.score = (float) item.optDouble("score");

				youkuResult.videos.add(v);
			}

		} catch (Exception e) {
			logger.error("处理youku查询结果发生异常: " + url, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("youku返回视频数目: " + youkuResult.videos.size() + "/"
					+ youkuResult.total);
		}

		return youkuResult;
	}

	IndexSearchResult<Video> getFromSoku(SearchContext context) {
		return getFromLucene(context);
	}

	protected IndexSearchResult<Video> getFromLucene(SearchContext context) {

		boolean isDebug = logger.isDebugEnabled();

		IndexSearchResult<Video> searchResult = null;

		if (context.param.site == YoukuSiteId) {
			if (isDebug) {
				logger.debug("只需要查询优酷的数据, 不需要查询soku数据, " + context);
			}
			return new IndexSearchResult<Video>();
		}

		if (context.param.dup) {
			if (isDebug) {
				logger.debug("需要查询soku数据, 去重搜索：" + context);
			}

			searchResult = IndexSearcher.searchWithDuplicate(context.lockQuery,
					context.minLuceneResultCount, context.comparator,
					context.span);
		} else {
			if (isDebug) {
				logger.debug("需要查询soku数据, 不是去重搜索：" + context);
			}

			searchResult = IndexSearcher.search(context.lockQuery,
					context.minLuceneResultCount, context.comparator,
					context.span);
		}

		return searchResult;
	}

	void resort(LinkedHashMap<String, LinkedList<Video>> siteVideos,
			LinkedList<Video> target) {

		int total = 0;
		for (LinkedList<Video> videos : siteVideos.values()) {
			total += videos.size();
		}

		if (total == 0) {
			return;
		}

		TreeMap<Double, Video> map = new TreeMap<Double, Video>();

		int site_index = 0;
		double site_diff = 0.001;

		for (String site : siteVideos.keySet()) {

			LinkedList<Video> videos = siteVideos.get(site);
			if (videos.isEmpty()) {
				continue;
			}

			double site_offset = (site_index++) * site_diff;
			double index_diff = 1.0 * total / videos.size();

			for (ListIterator<Video> i = videos.listIterator(); i.hasNext();) {
				int index = i.nextIndex();
				Video video = i.next();
				map.put(site_offset + index * index_diff, video);
			}
		}

		for (Video video : map.values()) {
			target.add(video);
		}
	}

	protected JSONArray getSites(SearchContext context,
			IndexSearchResult<Video> searchResult) {

		List<List<Serializable>> extra = searchResult.extra;

		LinkedHashSet<Integer> set = new LinkedHashSet<Integer>();

		if (context.param.site == 0) {
			set.add(YoukuSiteId);
		}

		for (List<Serializable> list : extra) {
			if (list == null || list.isEmpty()) {
				continue;
			}

			for (Serializable serializable : list) {
				if (serializable == null) {
					continue;
				}

				try {
					set.addAll((Set<Integer>) serializable);
				} catch (Exception e) {
					logger.error("提取extra信息发生异常, extra: "
							+ serializable.getClass() + "; " + serializable, e);
				}
			}
		}

		JSONArray sites = new JSONArray();
		for (Integer site : set) {
			sites.put(site);
		}
		return sites;
	}

	protected JSONObject buildJsonObject(int total, boolean miss,
			List<Video> page, Map<Video, Integer> objectMap, JSONArray sites,
			Cost searchCost,Map<String, Object> picInfos) throws Exception {

		if (objectMap == null) {
			objectMap = new HashMap<Video, Integer>(0);
		}

		if (sites == null) {
			sites = new JSONArray();
		}

		if (page == null) {
			page = new ArrayList<Video>(0);
		}

		JSONObject object = new JSONObject();
		JSONArray items = new JSONArray();

		object.put("total", total);
		object.put("page", page.size());
		object.put("miss", miss);
		object.put("cost", searchCost.getCost());
		object.put("sites", sites);
		object.put("items", items);

		for (Video video : page) {
			Integer dupCount = objectMap.get(video);
				
//				video.picInfo = (tmp==null)?null:tmp.toString();

			JSONObject videObject = null;
			if (dupCount == null) {
				videObject = VideoConvertor.convert(video, 1);
			} else {
				videObject = VideoConvertor.convert(video, dupCount);
			}
			
			if(!JSONUtil.isEmpty(videObject) && picInfos!=null){
				Object tmp = picInfos.get("p"+MyUtil.decodeVideoUrl(video.url));
				
				videObject.put("pics", filterNull((tmp==null)?null:tmp.toString()));
			}
			items.put(videObject);
		}
		return object;
	}

	protected LogInfo buildLogInfo(SearchContext context, boolean cache,
			int total_result, int page_result, long search_cost, boolean miss,
			int lucene_loop, String lucene_costString) {

		LogInfo info = new LogInfo();

		info.set(Item.query, context.param.keyword);
		info.set(Item.source, context.param._source);
		info.set(Item.type, "video");

		if (context.param.logic == Constant.Operator.AND) {
			info.set(Item.logic, "and");
		} else {
			info.set(Item.logic, "or");
		}

		info.set(Item.order_field, null);
		info.set(Item.order_reverse, context.param.reverse);
		info.set(Item.page, context.param.page);
		info.set(Item.cache, cache);
		info.set(Item.total_result, total_result);
		info.set(Item.page_result, page_result);
		info.set(Item.cost, search_cost);
		info.set(Item.miss, miss);
		info.set(Item.cacheKey, context.cacheKey);
		info.set(Item.url, context.param.queryUrl);
		info.set(Item.because, context.param._because);

		if (cache == false) {
			final String others = "loop:" + lucene_loop + "; " + "index:"
					+ lucene_costString;
			info.set(Item.others, others);
		}

		return info;
	}

	protected void storeToMemcache(final int total, final boolean miss,
			final Map<Video, Integer> objectMap, final JSONArray sites,
			final Cost searchCost, final List<Video> videos,
			final SearchContext context) {

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				store(total, miss, objectMap, sites, searchCost, videos,
						context);
			}
		});
	}

	protected void store(int total, boolean miss,
			Map<Video, Integer> objectMap, JSONArray sites, Cost searchCost,
			List<Video> videos, SearchContext context) {

		int cacheTime = Config.getCacheTimeOut();

		if (cacheTime <= 0) {
			logger.warn("缓存配置时间为 " + cacheTime + "; 不需要缓存其他页的结果。");
			return;
		}

		if (miss) {
			cacheTime = cacheTime / 3 + 60;
		}

		Parameter parameter = context.param;
		int start;
		//2011.5.23 原来将一次所有的结果都缓存。
		//改为：从当页开始，最多到后面的5页
		for (int page = parameter.page; (start = (page - 1) * parameter.pagesize) < videos
				.size() && page<(parameter.page+5); page++) {
			try {
				int end = Math.min(start + parameter.pagesize, videos.size());

				List<Video> pageVideos = videos.subList(start, end);

				String cacheKey = context.getCacheKey(page);
				
				Map<String, Object> picInfos = null;
				if(parameter.picInfo && Config.getPicInfosStatus() > 0){//参数可控和配置可控
					List<String> ids = new ArrayList<String>();
					for (Video video : pageVideos) {
						if("14".equals(video.site)){
							ids.add(Integer.toString(MyUtil.decodeVideoUrl(video.url)));
						}
					}
					if(ids.size()>0){
						picInfos = MemCachedPic.cacheGetByVid(ids.toArray(new String[0]));
					}
				}
				
				String str = buildJsonObject(total, miss, pageVideos,
						objectMap, sites, searchCost,picInfos).toString();

				if (logger.isDebugEnabled()) {
					logger.debug("存储到memcache, key: " + cacheKey);
				}
				StoreResult result = MemCache
						.cacheSet(cacheKey, str, cacheTime);

				if (!result.equals(StoreResult.success)) {
					logger.debug("存储到memcache失败！" + result);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}