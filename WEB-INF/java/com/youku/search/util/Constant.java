/**
 * 
 */
package com.youku.search.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.queryParser.QueryParser;

import com.youku.search.index.query.BarpostQueryManager;
import com.youku.search.index.query.FolderQueryManager;
import com.youku.search.index.query.PkQueryManager;
import com.youku.search.index.query.UserQueryManager;
import com.youku.search.index.query.VideoQueryManager;
import com.youku.search.pool.net.mina.AbstractMessageDecoder;
import com.youku.search.pool.net.mina.AbstractMessageEncoder;

/**
 * @author william
 *
 */
public class Constant {
	
	public static String localIp ;
	static {
		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询类型
	 */
	public static class QueryField{
		public static final int VIDEO = 1; 	//视频
		public static final int FOLDER = 2;	//专辑
		public static final int MEMBER = 3;	//会员
		public static final int BAR = 4;	//看吧
		public static final int BARPOST_SUBJECT = 5;	//看吧帖子按标题搜索
		public static final int BARPOST_AUTHOR = 6;		//看吧帖子按作者搜索
		public static final int PK = 7;		//pk擂台
		public static final int VIDEOTAG = 8;		//tag搜索
		public static final int VIDEOTAGID = 9;		//tagID搜索
		public static final int FOLDERTAG = 10;		//tag搜索
		public static final int FOLDERTAGID = 11;	//tagID搜索
		public static final int PKTAG = 12;		//pktag搜索
		public static final int BARPOST_POST = 13;	//看吧帖子
		
		public static final int STAT = 14;	//搜索统计
		public static final int STAT_PINYIN = 15; 	//搜索拼音,纠错使用
		public static final int STAT_KEYWORD = 16; 	//搜索相关
		
		public static final int RING = 17; 	//铃声12530
		
		public static final int FULL_MATCH = 18; 	//完全匹配
		public static final int VIDEO_TITLE_TAG = 19; 	//相关视频
		public static final int FOLDER_TITLE_TAG = 20; 	//相关专辑
		
		/**
		 * add by gaosong 2011-07-14 <br>
		 * MD5匹配
		 */
		public static final int VIDEO_MD5 = 21; 
		
		public static int getCodeByStr(String str)
		{
			if (str.equals("video"))
				return VIDEO;
			else if (str.equals("folder"))
				return FOLDER;
			else if (str.equals("user"))
				return MEMBER;
			else if (str.equals("bar"))
				return BARPOST_SUBJECT;
			else if (str.equals("pk"))
				return PK;
			else if (str.equals("ring"))
				return RING;
			return 0;
		}
		public static String getNameString(int type) {
			if (type == Constant.QueryField.VIDEO) {
				return "video";
			} else if (type == Constant.QueryField.FOLDER) {
				return "folder";
			} else if (type == Constant.QueryField.MEMBER) {
				return "user";
			} else if (type == Constant.QueryField.PK) {
				return "pk";
			} else if (type == Constant.QueryField.BARPOST_SUBJECT) {
				return "barpost";
			} else if (type == Constant.QueryField.BAR) {
				return "bar";
			} else if (type == Constant.QueryField.STAT) {
				return "stat";
			} else if (type == Constant.QueryField.RING) {
				return "ring";
			}
			throw new IllegalArgumentException("未知的搜索类型" + type);
		}
	}
	
	public static class Sort{
		public static final int SORT_SCORE = 0;
		
		public static final int SORT_NEW = 1;
		public static final int SORT_PV = 2;
		public static final int SORT_COMMENT = 3;
		public static final int SORT_FAV = 4;
		
		public static final int SORT_USER_NEW_REG = 5;
		public static final int SORT_USER_NEW_UPDATE = 6;
		public static final int SORT_USER_VIDEO_COUNT = 7;
		public static final int SORT_USER_FAV_COUNT = 8;
		public static final int SORT_USER_SCORE = 9;
		
		public static final int SORT_PK_NEW = 10;
		public static final int SORT_PK_VIDEO_COUNT = 11;
		public static final int SORT_PK_PV = 12;
		public static final int SORT_PK_VOTE_COUNT = 13;
		public static final int SORT_PK_ACTOR_COUNT = 14;
		
		public static final int SORT_BARPOST_NEW = 15;
		
		//Ring
		public static final int SORT_RING_DATE = 16;
		public static final int SORT_RING_PRICE = 17;
		
		public static final int SORT_FOLDER_VIDEOCOUNT = 18;
		
		// 前端洗脸
		public static final int RESORT_CREATE_TIME = 19;
		public static final int RESORT_15DAYS = 20;
		
		
		
		public static org.apache.lucene.search.Sort getVideoSort(int sort,boolean reverse)
		{
			if (sort == SORT_NEW)
			{
				return reverse?VideoQueryManager.SORT_NEW_DESC:VideoQueryManager.SORT_NEW_ASC;
			}
			else if (sort == SORT_PV)
			{
				return reverse?VideoQueryManager.SORT_PV_DESC:VideoQueryManager.SORT_PV_ASC;
			}
			else if (sort == SORT_COMMENT)
			{
				return reverse?VideoQueryManager.SORT_COMMENT_DESC:VideoQueryManager.SORT_COMMENT_ASC;
			}
			else if (sort == SORT_FAV)
			{
				return reverse?VideoQueryManager.SORT_FAV_DESC:VideoQueryManager.SORT_FAV_ASC;
			}
			return null;
		}
		
		public static org.apache.lucene.search.Sort getFolderSort(int sort,boolean reverse)
		{
			if (sort == SORT_NEW)
			{
				return reverse?FolderQueryManager.SORT_NEW_DESC:FolderQueryManager.SORT_NEW_ASC;
			}
			else if (sort == SORT_PV)
			{
				return reverse?FolderQueryManager.SORT_PV_DESC:FolderQueryManager.SORT_PV_ASC;
			}
			else if (sort == SORT_FOLDER_VIDEOCOUNT)
			{
				return reverse?FolderQueryManager.SORT_VIDEOCOUNT_DESC:FolderQueryManager.SORT_VIDEOCOUNT_ASC;
			}
			
			return null;
		}
		
		public static org.apache.lucene.search.Sort getUserSort(int sort,boolean reverse)
		{
			if (sort == SORT_USER_NEW_REG)
			{
				return reverse?UserQueryManager.SORT_NEW_REG_DESC:UserQueryManager.SORT_NEW_REG_ASC;
			}
			else if (sort == SORT_USER_NEW_UPDATE)
			{
				return reverse?UserQueryManager.SORT_NEW_REG_UPDATE_DESC:UserQueryManager.SORT_NEW_UPDATE_ASC;
			}
			else if (sort == SORT_USER_VIDEO_COUNT)
			{
				return reverse?UserQueryManager.SORT_VIDEOCOUNT_DESC:UserQueryManager.SORT_VIDEOCOUNT_ASC;
			}
			else if (sort == SORT_USER_FAV_COUNT)
			{
				return reverse?UserQueryManager.SORT_FAV_DESC:UserQueryManager.SORT_FAV_ASC;
			}
			else if (sort == SORT_USER_SCORE)
			{
				return reverse?UserQueryManager.SORT_USERSCORE_DESC:UserQueryManager.SORT_USERSCORE_ASC;
			}
			
			return null;
		}
		
		public static org.apache.lucene.search.Sort getPkSort(int sort,boolean reverse)
		{
			if (sort == SORT_PK_NEW)
			{
				return reverse?PkQueryManager.SORT_NEW_DESC:PkQueryManager.SORT_NEW_ASC;
			}
			else if (sort == SORT_PK_VIDEO_COUNT)
			{
				return reverse?PkQueryManager.SORT_VIDEOCOUNT_DESC:PkQueryManager.SORT_VIDEOCOUNT_ASC;
			}
			else if (sort == SORT_PK_PV)
			{
				return reverse?PkQueryManager.SORT_PV_DESC:PkQueryManager.SORT_PV_ASC;
			}
			else if (sort == SORT_PK_VOTE_COUNT)
			{
				return reverse?PkQueryManager.SORT_VOTECOUNT_DESC:PkQueryManager.SORT_VOTECOUNT_ASC;
			}
			else if (sort == SORT_PK_ACTOR_COUNT)
			{
				return reverse?PkQueryManager.SORT_ACTORCOUNT_DESC:PkQueryManager.SORT_ACTORCOUNT_ASC;
			}
			
			return null;
		}
		public static org.apache.lucene.search.Sort getBarPostSort(int sort,boolean reverse)
		{
			if (sort == SORT_BARPOST_NEW)
			{
				return reverse?BarpostQueryManager.SORT_NEW_DESC:BarpostQueryManager.SORT_NEW_ASC;
			}
			
			
			return null;
		}
		
		
	}
	
	/**
	 * web前端的视频分类常量
	 * @author gaosong
	 */
	public static class CateId {
		public static final int DEFAULT = 0;  // 默认的种类ID
		public static final int INFOMATION = 91;  // 资讯
		public static final int ORIGINAL = 92; 	// 原创
		public static final int MOVIE = 96;	// 电影
		public static final int DRAMA = 97;	// 电视剧
		public static final int SPORT = 98;	// 体育 
		public static final int MUSIC = 95; // 音乐
		public static final int GAME = 99;	// 游戏
		public static final int COMIC = 100;  // 动漫
		public static final int WOMEN = 93;	// 女性
		public static final int FUN = 94;	// 搞笑
		public static final int SELF_TIMER = 101;	// 自拍
		public static final int ADVERTISEMENT = 102;	// 广告
		public static final int LIFE = 103;	// 生活
		public static final int CAR = 104; // 汽车
		public static final int TECHNOLOGY = 105;	// 科技
		public static final int FASHION = 89;	// 时尚
		public static final int MOTHERS_AND_NEWBORNS = 90;	// 母婴
		public static final int TRAVEL = 88;	// 旅游
		public static final int TEACH = 87;	// 教育
		public static final int ENTERTAINMENT = 86;	// 娱乐
		public static final int SHOWS =85;	// 综艺
		public static final int WORLDCUP = 84;	// 世界杯
		public static final int OTHERS = 106;	// 其他
	}
	
	/**
	 * 后台C-Server的视频分类
	 * @author gaosong
	 */
	public static class VideoCategoryConstant {
		public static final int DEFAULT = 250;  // 默认的种类ID
		public static final int INFOMATION = 0;  // 资讯
		public static final int ORIGINAL = 1; 	// 原创
		public static final int MOVIE = 2;	// 电影
		public static final int DRAMA = 3;	// 电视剧
		public static final int SPORT = 4;	// 体育 
		public static final int MUSIC = 5; // 音乐
		public static final int GAME = 6;	// 游戏
		public static final int COMIC = 7;  // 动漫
		public static final int WOMEN = 8;	// 女性
		public static final int FUN = 9;	// 搞笑
		public static final int SELF_TIMER = 10;	// 自拍
		public static final int ADVERTISEMENT = 11;	// 广告
		public static final int LIFE = 12;	// 生活
		public static final int CAR = 13; // 汽车
		public static final int TECHNOLOGY = 14;	// 科技
		public static final int FASHION = 15;	// 时尚
		public static final int MOTHERS_AND_NEWBORNS = 16;	// 母婴
		public static final int TRAVEL = 17;	// 旅游
		public static final int TEACH = 18;	// 教育
		public static final int ENTERTAINMENT = 19;	// 娱乐
		public static final int SHOWS = 20;	// 综艺
		public static final int WORLDCUP = 21;	// 世界杯
		public static final int OTHERS = 22;	// 其他
	}
	
	public static class Operator
	{
		public static final int AND = 1;
		public static final int OR = 2;
		
		public static QueryParser.Operator getOperator(int code)
		{
			if (code == AND)
				return QueryParser.AND_OPERATOR;
			else
				return QueryParser.OR_OPERATOR;
		}
	}
	
	public static class StopWords
	{
		private static Set<Character> stopSet = null;
		private static char[] stopWords = new char[]{
			'\r','\n',',','.','。','？','！','，','；','：','“','”','‘','’','（','）','—','…','《','》','-','[',']','(',')','!','【','】','*','?','\\','/','+','~',':'
			,'{','}','"','\'','^','<','>'
		};
		
		static {
			stopSet = new HashSet<Character>();
		    for (int i=0;i<stopWords.length;i++)
		    	stopSet.add(stopWords[i]);
		}
		
		public static Set<Character> getStopSet()
		{
			return stopSet;
		}
	}
	
	/**
	 * @author gaosong
	 * Mina Client会用到的有关通讯方面的常量
	 */
	public static class Socket {
		public static final String MESSAGE_CHARSET_NAME = "GBK";

		public static final ThreadLocal<CharsetEncoder> MESSAGE_ENCODER = new ThreadLocalEncoder();

		private static class ThreadLocalEncoder extends ThreadLocal<CharsetEncoder> {
			@Override
			protected CharsetEncoder initialValue() {
				return Charset.forName("GBK").newEncoder();
			}
		}

		public static final ThreadLocal<CharsetDecoder> MESSAGE_DECODER = new ThreadLocalDecoder();

		private static class ThreadLocalDecoder extends ThreadLocal<CharsetDecoder> {
			@Override
			protected CharsetDecoder initialValue() {
				return Charset.forName("GBK").newDecoder();
			}
		}

		public static final String REQUEST_HEAD = "QSEpsL01QSBEAACA";

		public static final String REQUEST_END = "QS55AACA";

		public static final String RESPONSE_HEAD = "QSBEAACA";
		
		public static final String RESPONSE_END = "QS55AACA";
		
		// 固定的8位检索服务描述信息
		public static final String SEARCH_SERVICE = "00101001";
		
		// ByteBuffer初始化时的长度
		public static final int BUFFER_INIT_LENGTH = 1024;
		
		/**
		 * 单台C-Server一次请求返回的最多数量
		 */
		public static final int INDEX_PAGE_SIZE = 48;
	}
	
	public static class LogCategory {
		public static final String ServerStateLog = "ServerStateLog";
		public static final String ServerStatInfo = "ServerStatInfo";
	}
	
}
