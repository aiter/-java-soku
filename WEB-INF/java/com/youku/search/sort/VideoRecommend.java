package com.youku.search.sort;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Query;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.sort.MemCache.StoreResult;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.StringUtil;

/**
 * @author Administrator
 * 
 */
public class VideoRecommend {

	private static final String pattern = "yyyy-MM-dd";

	static Log logger = LogFactory.getLog(VideoRecommend.class);

	static final int CACHE_SECONDS = 7200;// 默认缓存2小时

	public VideoRecommend() {
	}

	static class RecommendInfo {
		public String keyword;
		public int type;
		public String type_string;
		public int object_id;
		public int display_order;
	}

	static class Recommend {
		public String keyword;
		public int type;
		public String type_string;
		public List<DisplayOrder> list = new LinkedList<DisplayOrder>();

		/**
		 * 返回一个Map，key是视频（专辑）id，value是视频（专辑）排序的序号。
		 */
		public Map<Integer, Integer> map() {
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (Iterator<DisplayOrder> i = list.iterator(); i.hasNext();) {
				DisplayOrder displayOrder = i.next();
				map.put(displayOrder.id, displayOrder.order);
			}
			return map;
		}
	}

	static class DisplayOrder {
		public int id;
		public int order;

		public DisplayOrder() {
		}

		public DisplayOrder(int id, int order) {
			this.id = id;
			this.order = order;
		}
	}

	/**
	 * 根据查询关键词和查询类型，构建一个cacheKey，然后返回此key对应的缓存内容。缓存内容为视频列表、专辑列表。
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * 
	 */
	public static <E> List<E> getCachedVideoRecommend(String query, int type) {
		if (Config.getRecommendStatus() < 1) {
			return null;
		}

		String cacheKey = SearchUtil.CacheKey.recommend(query, type);
		List<E> list = (List<E>) MemCache.cacheGet(cacheKey);

		return list;
	}

	/**
	 * 从数据库中查询出所有有效的推荐内容，然后缓存起来
	 * 
	 * @throws Exception
	 */
	public void cacheRecommends() throws Exception {
		cacheRecommends(SearchConstant.VIDEO);
		cacheRecommends(SearchConstant.FOLDER);
		cacheRecommends(SearchConstant.MEMBER);
	}

	/**
	 * 从数据库中查询出指定type的推荐列表，然后将此列表缓存起来，
	 */
	public void cacheRecommends(int type) throws Exception {
		try {
			cacheRecommends_(type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private void cacheRecommends_(int type) throws Exception {

		Exception oneException = null;

		List<RecommendInfo> list = getRecommendInfoList(type);
		Map<String, Recommend> map = getRecommendMap(list);

		final int keywordSize = (map == null) ? 0 : map.size();
		if (logger.isInfoEnabled()) {
			logger.info("查询类型: " + type + "; 关键词数目: " + keywordSize);
		}
		if (keywordSize == 0) {
			return;
		}

		int no = 0;
		for (Map.Entry<String, Recommend> i : map.entrySet()) {
			no++;

			String keyword = i.getKey();
			keyword = KeywordFilter.filter(keyword);
			Recommend recommend = i.getValue();

			try {
				List luceneDetaiList = getVideoRecommendDetailList(recommend,
						no, keywordSize);

				if (luceneDetaiList == null || luceneDetaiList.isEmpty()) {
					continue;
				}

				sortByDisplayOrder(luceneDetaiList, recommend);

				String cacheKey = SearchUtil.CacheKey.recommend(keyword, type);
				StoreResult storeResult = MemCache.cacheSet(cacheKey,
						luceneDetaiList, CACHE_SECONDS);

				if (storeResult != StoreResult.success) {
					logger.info("存储到memcache发生错误：storeResult = " + storeResult
							+ "; cacheKey = " + cacheKey);
				}
			} catch (Exception e) {
				// 如果处理某个关键词发生异常，继续处理其余的
				oneException = e;

				logger.error(e.getMessage(), e);
			}
		}

		if (oneException != null) {
			throw oneException;
		}
	}

	/**
	 * 可以通过url方式来刷新缓存内容，改方法返回响应字符串
	 */
	public String getStatus() {
		return ("{\"recmd\":\"OK\"}");
	}

	private static Connection getConnection() {

		Connection connection = null;
		try {
			connection = Torque.getConnection("recommend");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return connection;
	}

	/**
	 * 根据给定的lucene查询字符串，返回符合条件的结果列表
	 */
	private List getDetail(String idQuery, int type) {

		int searchType = SearchUtil.getLuceneSearchType(type);

		InetSocketAddress[] servers = SearchUtil.getLuceneServers(searchType);

		Query object = new Query();
		object.start = 0;
		object.end = 100;
		object.sort = SearchUtil.getLuceneOrderField(type);
		object.reverse = true;
		object.keywords = idQuery;
		object.category = 0;
		object.partner = 0;
		object.operator = Constant.Operator.AND;
		object.field = searchType;
		object.needAnalyze = false;// 重要！

		LockQuery lockQuery = new LockQuery(servers, object);
		MergedResult result = MultiIndexSearcher.I.search(lockQuery);

		return result.list;
	}

	/**
	 * 根据给定的查询类型，返回该类型下的 RecommendInfo 列表
	 */
	private List<RecommendInfo> getRecommendInfoList(int type) throws Exception {

		Connection conn = getConnection();
		ResultSet rs = null;
		PreparedStatement pStmt = null;

		if (conn == null) {
			logger.info("null database Connection");
			return null;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			Date curDate = new Date();
			String dateString = dateFormat.format(curDate);

			String typeString = SearchUtil.getTypeStr(type);

			String sql = "SELECT keyword, object_id, disp_order FROM t_search_recommend "
					+ "WHERE object_type = ? AND expire_date > ? "
					+ "order by keyword asc, disp_order asc ";

			List<RecommendInfo> list = new LinkedList<RecommendInfo>();

			pStmt = conn.prepareStatement(sql);
			pStmt.setString(1, typeString);
			pStmt.setString(2, dateString);

			rs = pStmt.executeQuery();
			while (rs.next()) {
				String keyword = rs.getString("keyword");
				keyword = StringUtil.conv(keyword, "8859_1", "utf8");

				RecommendInfo info = new RecommendInfo();
				info.keyword = keyword;
				info.type = type;
				info.type_string = typeString;
				info.object_id = rs.getInt("object_id");
				info.display_order = rs.getInt("disp_order");

				list.add(info);
			}

			if (logger.isInfoEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("sql: " + sql + "; ");

				builder.append("param(s): ");
				builder.append(Arrays.toString(new Object[] { typeString,
						dateString })
						+ "; ");

				builder.append("search result: "
						+ (list == null ? 0 : list.size()) + "; ");

				logger.info(builder.toString());
			}

			return list;

		} finally {
			JdbcUtil.close(rs, pStmt, conn);
		}
	}

	/**
	 * 将 RecommendInfo list 转化为 map
	 */
	private Map<String, Recommend> getRecommendMap(List<RecommendInfo> list) {

		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, Recommend> map = new LinkedHashMap<String, Recommend>();
		for (ListIterator<RecommendInfo> i = list.listIterator(); i.hasNext();) {
			RecommendInfo info = i.next();
			Recommend recommend = map.get(info.keyword);

			if (recommend == null) {
				recommend = new Recommend();
				recommend.keyword = info.keyword;
				recommend.type = info.type;
				recommend.type_string = info.type_string;

				map.put(recommend.keyword, recommend);
			}

			recommend.list.add(new DisplayOrder(info.object_id,
					info.display_order));
		}

		return map;
	}

	/**
	 * 返回一个lucene查询字符串，类似 (vid:8178751)||(vid:8180347)
	 */
	private String getQuery(Recommend recommend) {

		String field = SearchUtil.getLuceneDocumentPKFieldName(recommend.type);

		StringBuffer idQuery = new StringBuffer();
		for (ListIterator<DisplayOrder> i = recommend.list.listIterator(); i
				.hasNext();) {

			int index = i.nextIndex();
			DisplayOrder displayOrder = i.next();
			if (index > 0) {
				idQuery.append("||");
			}

			idQuery.append("(" + field + ":" + displayOrder.id + ")");
		}

		return idQuery.toString();
	}

	/**
	 * 排序，按照display order信息排序
	 */
	private void sortByDisplayOrder(List list, Recommend recommend) {

		if (list == null || list.isEmpty()) {
			return;
		}

		final String field = SearchUtil
				.getLuceneDocumentObjectPKFieldName(recommend.type);

		try {
			final Field fieldObject = list.get(0).getClass().getField(field);
			final Map<Integer, Integer> idOrderMap = recommend.map();

			// 按照display order从小到大的顺序排序
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					try {
						Integer order1 = idOrderMap.get(fieldObject.getInt(o1));
						Integer order2 = idOrderMap.get(fieldObject.getInt(o2));
						return order1.compareTo(order2);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return 0;
					}
				}
			});

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 查询lucene，返回该关键词、该查询类型对应的推荐视频、专辑、用户列表
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * @see com.youku.search.index.entity.User
	 * 
	 */
	private List getVideoRecommendDetailList(Recommend recommend, int no,
			int total) {

		if (recommend == null || recommend.list.isEmpty()) {
			return null;
		}

		String idQuery = null;
		try {
			idQuery = getQuery(recommend);
		} catch (Exception e) {
			String msg = "构造查询字符串发生异常：keyword = " + recommend.keyword
					+ "; type = " + recommend.type;
			throw new RuntimeException(msg, e);
		}

		List detailList = null;
		try {
			detailList = getDetail(idQuery, recommend.type);
		} catch (Exception e) {
			String msg = "查询索引发生异常：keyword = " + recommend.keyword
					+ "; type ＝ " + recommend.type + "; idQuery = " + idQuery;

			throw new RuntimeException(msg, e);
		}

		if (logger.isInfoEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append(no + "/" + total + ": ");
			builder.append("查询类型: " + recommend.type + "; ");
			builder.append("关键词: " + recommend.keyword + "; ");
			builder.append("idQuery: " + idQuery + "; ");

			final int size = (detailList == null) ? 0 : detailList.size();
			builder.append("查询结果数目: " + size + "; ");

			logger.info(builder.toString());
		}

		return detailList;
	}

	public static void main(String[] args) {
	}

}
