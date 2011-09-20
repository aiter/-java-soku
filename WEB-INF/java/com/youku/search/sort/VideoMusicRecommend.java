package com.youku.search.sort;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;

import com.youku.search.config.Config;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.Lock.MergedResult;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.MemCache.StoreResult;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.search.MultiIndexSearcher;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;

/**
 * 将有版权的音乐，导入memcached中。
 * 
 * @author Administrator
 */
public class VideoMusicRecommend {

	static Log logger = LogFactory.getLog(VideoMusicRecommend.class);

	public static final int CACHE_SECONDS = 7200;// 默认缓存2小时

	public VideoMusicRecommend() {
	}

	static class RecommendInfo {
		public String keyword;
		public int type;
		public String type_string;
		public int object_id;
	}

	/**
	 * 根据查询关键词和查询类型，构建一个cacheKey，然后返回此key对应的缓存内容。
	 * 缓存内容为视频列表、专辑列表。
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * 
	 */
	public static <E> List<E> getCachedVideoMusic(String query, int type) {
		if (Config.getCopyrightMusicStatus() < 1) {//TODO 需要配置
			return null;
		}

		String cacheKey = SearchUtil.CacheKey.music(query, type);
		List<E> list = (List<E>) MemCache.cacheGet(cacheKey);
		return list;
	}

	/**
	 * 从数据库中查询出所有有版权的音乐内容，然后缓存起来
	 * 
	 * @throws Exception
	 */
	public void cacheMusic() throws Exception {
		cacheMusic(SearchConstant.VIDEO);
	}

	/**
	 * 从数据库中查询出指定type的有版权的音乐内容，然后将此列表缓存起来，
	 */
	public void cacheMusic(int type) throws Exception {
		try {
			cacheMusic_(type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private void cacheMusic_(int type) throws Exception {

		Exception oneException = null;

		Cost dbCost = new Cost();
		List<RecommendInfo> list = getMusicInfoList(type);
		dbCost.updateEnd();

		final int keywordSize = (list == null) ? 0 : list.size();
		if (logger.isInfoEnabled()) {
			logger.info("查询类型: " + type + "; 关键词数目: " + keywordSize+";cost:"+dbCost.getCost());
		}
		if (keywordSize == 0) {
			return;
		}

		int no = 0;
		for (RecommendInfo info:list) {
			no++;

			try {
				List luceneDetaiList = getVideoRecommendDetailList(info,
						no, keywordSize);

				if (luceneDetaiList == null || luceneDetaiList.isEmpty()) {
					continue;
				}

				String cacheKey = SearchUtil.CacheKey.music(info.keyword, type);
				StoreResult storeResult = MemCache.cacheSet(cacheKey,
						luceneDetaiList, CACHE_SECONDS);

				if (storeResult != StoreResult.success) {
					logger.info("存储到memcache发生错误：storeResult = " + storeResult
							+ "; cacheKey = " + cacheKey);
				}
				if(logger.isDebugEnabled()){
					logger.debug(cacheKey+"'s size="+luceneDetaiList.size()+":"+CACHE_SECONDS+":"+storeResult);
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
			connection = Torque.getConnection("soku_library");
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
	 * 根据给定的查询类型，返回该类型下的 具有版权的音乐 列表
	 */
	private List<RecommendInfo> getMusicInfoList(int type) throws Exception {
		Connection conn = getConnection();
		ResultSet rs = null;
		PreparedStatement pStmt = null;

		if (conn == null) {
			logger.info("null database Connection");
			return null;
		}

		try {
			String typeString = SearchUtil.getTypeStr(type);

			String sql = "select m.name name,ms.view_url url from music m,music_site ms where ms.fk_music_id=m.id and source_site=14 and m.have_right=1 and ms.view_url is not null ";

			List<RecommendInfo> list = new LinkedList<RecommendInfo>();

			pStmt = conn.prepareStatement(sql);

			rs = pStmt.executeQuery();
			int videoId = 0;
			while (rs.next()) {
				videoId = 0;
				videoId = parseVideoId(rs.getString("url"));
				if(videoId==0){
					continue;
				}
				
				String keyword = rs.getString("name");
				//搜索时，会对搜索词处理，所有这个完全对应词，也需要处理一下
				keyword = KeywordFilter.filter(keyword);
//				keyword = StringUtil.conv(keyword, "8859_1", "utf8");

				RecommendInfo info = new RecommendInfo();
				info.keyword = keyword;
				info.type = type;
				info.type_string = typeString;
				info.object_id = videoId;

				list.add(info);
			}

			if (logger.isInfoEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("sql: " + sql + "; ");

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
	 * 从url中，解析出站内视频ID
	 * http://v.youku.com/v_show/id_XMjA2MTA3NDEy.html
	 * @param string
	 * @return
	 */
	private int parseVideoId(String vUrl) {
		if(vUrl==null || vUrl.trim().isEmpty())
			return 0;
		
		int videoId = 0;
		videoId = MyUtil.decodeVideoUrl(vUrl);
		return videoId;
	}

	/**
	 * 返回一个lucene查询字符串，类似 (vid:8178751)||(vid:8180347)
	 */
	private String getQuery(RecommendInfo recommend) {

//		String field = SearchUtil.getLuceneDocumentPKFieldName(recommend.type);
		String field = "vid";

		StringBuffer idQuery = new StringBuffer();
//		for (ListIterator<DisplayOrder> i = recommend.list.listIterator(); i
//				.hasNext();) {
//
//			int index = i.nextIndex();
//			DisplayOrder displayOrder = i.next();
//			if (index > 0) {
//				idQuery.append("||");
//			}

			idQuery.append("(" + field + ":" + recommend.object_id + ")");
//		}

		return idQuery.toString();
	}

	/**
	 * 查询lucene，返回该关键词、该查询类型对应的推荐视频、专辑、用户列表
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * @see com.youku.search.index.entity.User
	 * 
	 */
	private List getVideoRecommendDetailList(RecommendInfo recommend, int no,
			int total) {

		if (recommend == null) {
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
			//简单高亮处理 2010.11.22 
			highlighter(detailList,recommend.keyword);
		} catch (Exception e) {
			String msg = "查询索引发生异常：keyword = " + recommend.keyword
					+ "; type ＝ " + recommend.type + "; idQuery = " + idQuery;

			throw new RuntimeException(msg, e);
		}

		if (logger.isDebugEnabled()) {
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

	/**
	 * @param detailList
	 */
	private void highlighter(List detailList,String keywords) {
		if(detailList==null || detailList.isEmpty()){
			return;
		}
		
		Video video = null;
		for (Object object : detailList) {
			if(object instanceof Video){
				video = (Video)object;
				String title = video.title;
				int begin = title.toLowerCase().indexOf(keywords);
				if(begin>=0 && begin+keywords.length()<=title.length()){
					String keywords0 = title.substring(begin, begin+keywords.length());
					video.title_hl=title.replace(keywords0, "<span class=\"highlight\">"+keywords0+"</span>");
				}
			}
		}
	}

	public static void main(String[] args) {
	}

}
