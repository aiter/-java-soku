package com.youku.search.sort.util.bridge;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.youku.search.config.Config;
import com.youku.search.index.entity.BaseQuery;
import com.youku.search.index.entity.Query;
import com.youku.search.index.server.CServerManager;
import com.youku.search.pool.net.parser.AbstractQueryParser;
import com.youku.search.pool.net.parser.DefaultQueryParser;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.search.resort.AbstractVideoResort;
import com.youku.search.sort.search.resort.Video15DaysResort;
import com.youku.search.sort.search.resort.VideoCreatedTimeResort;
import com.youku.search.sort.search.resort.VideoResort;
import com.youku.search.sort.search.resort.VideoUnresort;
import com.youku.search.sort.servlet.ChangeParam;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.BarPostSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.BarSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.FolderSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.PkSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.ResponseSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.RingSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.UserSortComparator;
import com.youku.search.sort.util.bridge.comparator.impl.VideoSortComparator;
import com.youku.search.util.Constant.CateId;
import com.youku.search.util.Constant.QueryField;
import com.youku.search.util.Constant.VideoCategoryConstant;

/**
 * 前台的web和后端的lucene、数据库等之间的常数映射
 * <ol>
 * <li>查询类型的映射: SearchType</li>
 * <li>查询类型的映射</li>
 * <li>查询类型的映射</li>
 * </ol>
 */
public class BridgeMap {

	/**
	 * web前端的查询类型 --> 后台lucene的查询类型
	 */
	public final static Map<Integer, Integer> SearchType;

	/**
	 * web前端的查询类型 --> 后台推荐数据库中字段的名字
	 */
	public final static Map<Integer, String> TypeColumnName;

	/**
	 * web前端的查询类型 --> 后台lucene的索引文档document对应的java类的主键字段名称
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * @see com.youku.search.index.entity.User
	 * @see com.youku.search.index.entity.BarPost
	 * @see com.youku.search.index.entity.Pk
	 */
	public final static Map<Integer, String> EntityPKFieldName;

	/**
	 * web前端的查询类型 --> 后台lucene的索引文件中索引字段名
	 * 
	 * @see org.apache.lucene.document.Field
	 */
	public final static Map<Integer, String> EntityPKFieldIndexName;

	/**
	 * 后台lucene的查询类型 --> 后台lucene的索引服务器
	 */
	public final static Map<Integer, InetSocketAddress[]> LuceneServer;

	/**
	 * web前端的查询类型 --> 后台AbstractSortComparator
	 * 
	 * @see com.youku.search.sort.util.bridge.comparator.AbstractSortComparator
	 */
	public final static Map<Integer, AbstractSortComparator> SortComparator;
	
	/**
	 * 前端不洗脸时使用的排序实例 <br>
	 * web前端的查询类型 --> 排序实例
	 */
	public final static Map<Integer, AbstractVideoResort> UNResortInstance;
	
	/**
	 * 前端洗脸时使用的排序实例 <br>
	 * web前端的查询类型 --> 排序实例
	 */
	public final static Map<Integer, AbstractVideoResort> ResortInstance;
	public final static Map<Integer, AbstractVideoResort> ResortByCreatedTimeInstance;
	
	/**
	 * web前端的视频分类 --> 后台C-Server的视频分类
	 */
	public final static Map<Integer, Integer> VideoCategory;
	
	/**
	 * web前端的查询类型 --> 是否需要动态Server
	 */
	public final static Set<Integer> IsDynamicServer;
	
	/**
	 * web前端的查询类型 --> 是否访问C-Server
	 */
	public final static Set<Integer> IsCServer;
	
	public final static Map<Class, AbstractQueryParser> QueryParserType;
	
	public static String info() {
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("all maps are ok!");
	}
	
	static {
		QueryParserType = new FinalMap<Class, AbstractQueryParser>();
		
		QueryParserType.put(Query.class, DefaultQueryParser.I);
	}

	static {
		SearchType = new FinalMap<Integer, Integer>();
		
		SearchType.put(SearchConstant.VIDEO, QueryField.VIDEO);
		SearchType.put(SearchConstant.VIDEOTAG, QueryField.VIDEOTAG);
		SearchType.put(SearchConstant.VIDEO_ONLY, QueryField.VIDEO);
		SearchType.put(SearchConstant.VIDEO_TITLE_TAG, QueryField.VIDEO_TITLE_TAG);
		
		// 按MD5搜索改为QueryField.VIDEO_MD5类型，以区分VIDEO类型
		// modified by gaosong 2011-07-14
		SearchType.put(SearchConstant.VIDEO_MD5, QueryField.VIDEO_MD5);
//		SearchType.put(SearchConstant.VIDEO_MD5, QueryField.VIDEO);
		
		SearchType.put(SearchConstant.FOLDER, QueryField.FOLDER);
		SearchType.put(SearchConstant.FOLDERTAG, QueryField.FOLDERTAG);
		SearchType.put(SearchConstant.FOLDER_TITLE_TAG, QueryField.FOLDER_TITLE_TAG);

		SearchType.put(SearchConstant.MEMBER, QueryField.MEMBER);

		SearchType.put(SearchConstant.BARPOST_SUBJECT, QueryField.BARPOST_SUBJECT);
		SearchType.put(SearchConstant.BARPOST_AUTHOR, QueryField.BARPOST_AUTHOR);
		SearchType.put(SearchConstant.BAR, QueryField.BAR);

		SearchType.put(SearchConstant.PK, QueryField.PK);

		SearchType.put(SearchConstant.RING, QueryField.RING);
		SearchType.put(SearchConstant.RING_2, QueryField.RING);

		SearchType.put(SearchConstant.STAT_PINYIN, QueryField.STAT_PINYIN);
		SearchType.put(SearchConstant.STAT_KEYWORD, QueryField.STAT_KEYWORD);
		
		SearchType.put(SearchConstant.KNOWLEDGE, QueryField.VIDEO);
	}

	static {
		// TODO: 将来要清理一下这里的内容
		TypeColumnName = new FinalMap<Integer, String>();

		TypeColumnName.put(SearchConstant.VIDEO, "VIDEO");
		TypeColumnName.put(SearchConstant.VIDEOTAG, "VIDEO");

		TypeColumnName.put(SearchConstant.FOLDER, "PLAYLIST");
		TypeColumnName.put(SearchConstant.FOLDERTAG, "PLAYLIST");

		TypeColumnName.put(SearchConstant.MEMBER, "USER");

		TypeColumnName.put(SearchConstant.BARPOST_SUBJECT, "BarPost");
		TypeColumnName.put(SearchConstant.BARPOST_AUTHOR, "BarPost");

		TypeColumnName.put(SearchConstant.PK, "PK");

		TypeColumnName.put(SearchConstant.STAT_PINYIN, "PINYIN");
		TypeColumnName.put(SearchConstant.STAT_KEYWORD, "RELEVANT");
	}

	static {
		EntityPKFieldName = new FinalMap<Integer, String>();

		EntityPKFieldName.put(SearchConstant.VIDEO, "vid");
		EntityPKFieldName.put(SearchConstant.VIDEOTAG, "vid");
		EntityPKFieldName.put(SearchConstant.VIDEO_MD5, "vid");

		EntityPKFieldName.put(SearchConstant.FOLDER, "pk_folder");
		EntityPKFieldName.put(SearchConstant.FOLDERTAG, "pk_folder");

		EntityPKFieldName.put(SearchConstant.MEMBER, "pk_user");

		EntityPKFieldName.put(SearchConstant.BARPOST_SUBJECT, "pk_post");
		EntityPKFieldName.put(SearchConstant.BARPOST_AUTHOR, "poster_name");

		EntityPKFieldName.put(SearchConstant.PK, "pk_pk");

		EntityPKFieldName.put(SearchConstant.RING, "cid");
		EntityPKFieldName.put(SearchConstant.RING_2, "cid");

		EntityPKFieldName.put(SearchConstant.STAT_PINYIN, "id");
		EntityPKFieldName.put(SearchConstant.STAT_KEYWORD, "id");
	}

	static {
		EntityPKFieldIndexName = new FinalMap<Integer, String>();

		EntityPKFieldIndexName.put(SearchConstant.VIDEO, "vid");
		EntityPKFieldIndexName.put(SearchConstant.VIDEOTAG, "vid");
		EntityPKFieldIndexName.put(SearchConstant.VIDEO_MD5, "vid");

		EntityPKFieldIndexName.put(SearchConstant.FOLDER, "pkfolder");
		EntityPKFieldIndexName.put(SearchConstant.FOLDERTAG, "pkfolder");

		EntityPKFieldIndexName.put(SearchConstant.MEMBER, "pkuser");

		EntityPKFieldIndexName.put(SearchConstant.BARPOST_SUBJECT, "pkpost");
		EntityPKFieldIndexName.put(SearchConstant.BARPOST_AUTHOR, "postername");

		EntityPKFieldIndexName.put(SearchConstant.PK, "pkpk");

		EntityPKFieldIndexName.put(SearchConstant.RING, "cid");
		EntityPKFieldIndexName.put(SearchConstant.RING_2, "cid");

		EntityPKFieldIndexName.put(SearchConstant.STAT_PINYIN, "id");
		EntityPKFieldIndexName.put(SearchConstant.STAT_KEYWORD, "id");
	}
	
	static {
		IsDynamicServer = new HashSet<Integer>();
		
		IsDynamicServer.add(SearchConstant.VIDEO);
		IsDynamicServer.add(SearchConstant.VIDEOTAG);
		IsDynamicServer.add(SearchConstant.VIDEO_TITLE_TAG);
		IsDynamicServer.add(SearchConstant.VIDEO_ONLY);
	}
	
	static {
		IsCServer = new HashSet<Integer>();
		
		IsCServer.add(SearchConstant.VIDEO);
		IsCServer.add(SearchConstant.VIDEOTAG);
		IsCServer.add(SearchConstant.VIDEO_TITLE_TAG);
		IsCServer.add(SearchConstant.VIDEO_ONLY);
		IsCServer.add(SearchConstant.VIDEO_MD5);
	}
	
	static {
		LuceneServer = new FinalMap<Integer, InetSocketAddress[]>();
		
		// MD5检索的情况下，走单独的C-Server
		LuceneServer.put(QueryField.VIDEO_MD5, CServerManager.getMD5ServerAddress());
//		LuceneServer.put(QueryField.VIDEOTAG, Config.getVideoIndexSocket());
//		LuceneServer.put(QueryField.FULL_MATCH, Config.getVideoIndexSocket());
//		LuceneServer.put(QueryField.VIDEO_TITLE_TAG, Config.getVideoIndexSocket());

		LuceneServer.put(QueryField.FOLDER, Config.getFolderIndexSocket());
		LuceneServer.put(QueryField.FOLDERTAG, Config.getFolderIndexSocket());
		LuceneServer.put(QueryField.FOLDER_TITLE_TAG, Config.getFolderIndexSocket());

		LuceneServer.put(QueryField.MEMBER, Config.getUserIndexSocket());

		LuceneServer.put(QueryField.BARPOST_SUBJECT, Config.getBarIndexSocket());
		LuceneServer.put(QueryField.BARPOST_AUTHOR, Config.getBarIndexSocket());
		LuceneServer.put(QueryField.BAR, Config.getBarIndexSocket());

		LuceneServer.put(QueryField.PK, Config.getPkIndexSocket());

		LuceneServer.put(QueryField.RING, Config.getRingIndexSocket());

		LuceneServer.put(QueryField.STAT_PINYIN, Config.getBarIndexSocket());
		LuceneServer.put(QueryField.STAT_KEYWORD, Config.getBarIndexSocket());
	}
	
	static {
		UNResortInstance = new FinalMap<Integer, AbstractVideoResort>();
		
		UNResortInstance.put(SearchConstant.VIDEO, VideoUnresort.I);
		UNResortInstance.put(SearchConstant.VIDEOTAG, VideoCreatedTimeResort.I);
		UNResortInstance.put(SearchConstant.VIDEO_ONLY, VideoUnresort.I);
		UNResortInstance.put(SearchConstant.VIDEO_TITLE_TAG,VideoUnresort.I);
	}
	
	static {
		ResortInstance = new FinalMap<Integer, AbstractVideoResort>();
		
		ResortInstance.put(SearchConstant.VIDEO, VideoResort.I);
//		ResortInstance.put(SearchConstant.VIDEOTAG, Video15DaysResort.I);
		ResortInstance.put(SearchConstant.VIDEOTAG, VideoCreatedTimeResort.I);
		ResortInstance.put(SearchConstant.VIDEO_ONLY, VideoResort.I);
		ResortInstance.put(SearchConstant.VIDEO_TITLE_TAG, VideoUnresort.I);
	}
	
	static {
		ResortByCreatedTimeInstance = new FinalMap<Integer, AbstractVideoResort>();
		
		ResortByCreatedTimeInstance.put(SearchConstant.VIDEO, VideoCreatedTimeResort.I);
		ResortByCreatedTimeInstance.put(SearchConstant.VIDEOTAG, VideoCreatedTimeResort.I);
		ResortByCreatedTimeInstance.put(SearchConstant.VIDEO_ONLY, VideoCreatedTimeResort.I);
		ResortByCreatedTimeInstance.put(SearchConstant.VIDEO_TITLE_TAG,VideoCreatedTimeResort.I);
	}
	
	static {
		SortComparator = new FinalMap<Integer, AbstractSortComparator>();
		
		// 所有的VIDEO查询都会使用C-Server，所以更改排序比较器
		// modified by gaosong 2011-06-01
//		SortComparator.put(SearchConstant.VIDEO, VideoSortComparator.I);
		SortComparator.put(SearchConstant.VIDEO, ResponseSortComparator.I);
		SortComparator.put(SearchConstant.VIDEOTAG, ResponseSortComparator.I);
		SortComparator.put(SearchConstant.VIDEO_MD5, ResponseSortComparator.I);
		SortComparator.put(SearchConstant.VIDEO_ONLY, ResponseSortComparator.I);
		SortComparator.put(SearchConstant.VIDEO_TITLE_TAG, ResponseSortComparator.I);
		
		SortComparator.put(SearchConstant.FOLDER, FolderSortComparator.I);
		SortComparator.put(SearchConstant.FOLDERTAG, FolderSortComparator.I);
		SortComparator.put(SearchConstant.FOLDER_TITLE_TAG, FolderSortComparator.I);

		SortComparator.put(SearchConstant.MEMBER, UserSortComparator.I);

		SortComparator.put(SearchConstant.BARPOST_SUBJECT,
				BarPostSortComparator.I);
		SortComparator.put(SearchConstant.BARPOST_AUTHOR,
				BarPostSortComparator.I);
		SortComparator.put(SearchConstant.BAR, BarSortComparator.I);

		SortComparator.put(SearchConstant.PK, PkSortComparator.I);

		SortComparator.put(SearchConstant.RING, RingSortComparator.I);
		SortComparator.put(SearchConstant.RING_2, RingSortComparator.I);

		SortComparator.put(SearchConstant.DRAMA, VideoSortComparator.I);
		SortComparator.put(SearchConstant.KNOWLEDGE, VideoSortComparator.I);
		
		// 这两种查询不走web前端（没有com.youku.search.sort.Parameter对象）
		// SortComparator.put(SearchConstant.TYPE_STAT_PINYIN, null);
		// SortComparator.put(SearchConstant.TYPE_STAT_KEYWORD, null);
	}
	
	static {
		VideoCategory = new FinalMap<Integer, Integer>();
		VideoCategory.put(CateId.DEFAULT, VideoCategoryConstant.DEFAULT);
		VideoCategory.put(CateId.ADVERTISEMENT, VideoCategoryConstant.ADVERTISEMENT);
		VideoCategory.put(CateId.CAR, VideoCategoryConstant.CAR);
		VideoCategory.put(CateId.COMIC, VideoCategoryConstant.COMIC);
		VideoCategory.put(CateId.DRAMA, VideoCategoryConstant.DRAMA);
		VideoCategory.put(CateId.ENTERTAINMENT, VideoCategoryConstant.ENTERTAINMENT);
		VideoCategory.put(CateId.FASHION, VideoCategoryConstant.FASHION);
		VideoCategory.put(CateId.FUN, VideoCategoryConstant.FUN);
		VideoCategory.put(CateId.GAME, VideoCategoryConstant.GAME);
		VideoCategory.put(CateId.INFOMATION, VideoCategoryConstant.INFOMATION);
		VideoCategory.put(CateId.LIFE, VideoCategoryConstant.LIFE);
		VideoCategory.put(CateId.MOTHERS_AND_NEWBORNS, VideoCategoryConstant.MOTHERS_AND_NEWBORNS);
		VideoCategory.put(CateId.MOVIE, VideoCategoryConstant.MOVIE);
		VideoCategory.put(CateId.MUSIC, VideoCategoryConstant.MUSIC);
		VideoCategory.put(CateId.ORIGINAL, VideoCategoryConstant.ORIGINAL);
		VideoCategory.put(CateId.OTHERS, VideoCategoryConstant.OTHERS);
		VideoCategory.put(CateId.SELF_TIMER, VideoCategoryConstant.SELF_TIMER);
		VideoCategory.put(CateId.SHOWS, VideoCategoryConstant.SHOWS);
		VideoCategory.put(CateId.SPORT, VideoCategoryConstant.SPORT);
		VideoCategory.put(CateId.TEACH, VideoCategoryConstant.TEACH);
		VideoCategory.put(CateId.TECHNOLOGY, VideoCategoryConstant.TECHNOLOGY);
		VideoCategory.put(CateId.TRAVEL, VideoCategoryConstant.TRAVEL);
		VideoCategory.put(CateId.WOMEN, VideoCategoryConstant.WOMEN);
		VideoCategory.put(CateId.WORLDCUP, VideoCategoryConstant.WORLDCUP);
	}

	// ----------------------------------------------------------------------
	static class FinalMap<K, V> extends ConcurrentHashMap<K, V> {

		private static final long serialVersionUID = -5729176205841641195L;

		@Override
		public V put(K key, V value) {
			if (containsKey(key)) {
				throw new IllegalStateException("这个key已经有对应的value了！ key = "
						+ key + ", value = " + get(key));
			}

			return super.put(key, value);
		}

		public V putInternal(K key, V value) {
			return super.put(key, value);
		}
	}

	public static void configChanged(ChangeParam param) {

		FinalMap<Integer, InetSocketAddress[]> servers = (FinalMap) LuceneServer;

		InetSocketAddress[] addresses = Config.getVideoIndexSocket();

		servers.putInternal(QueryField.VIDEO, addresses);
		servers.putInternal(QueryField.VIDEOTAG, addresses);
		servers.putInternal(QueryField.FULL_MATCH, addresses);
		servers.putInternal(QueryField.VIDEO_TITLE_TAG, addresses);
	}
}
