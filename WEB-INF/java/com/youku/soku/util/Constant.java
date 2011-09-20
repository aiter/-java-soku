/**
 * 
 */
package com.youku.soku.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;

import com.youku.soku.index.query.VideoQueryManager;

/**
 * @author 1verge
 *
 */
public class Constant {
	
	public static String DEFAULT_VIDEO_LOGO="http://g1.ykimg.com/0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80";
	public static final String POINT = "•";
	
	public static class QueryField{
		public static final int VIDEO = 1; 	//视频
		public static final int CORRECT = 2; 	//纠错
		public static final int LIKE = 3; 	//相关
		public static final int LIBRARY = 4; 	//直达区目录
	}
	
	public static class ServerType{
		public static final int INDEX = 1; 	//索引
		public static final int SORT = 2; 	//排序
		public static final int INDEX_MANAGER = 3; 	//索引管理
		public static final int CONSOLE = 4; 	//控制台
		public static final int COMMEND = 5; 	//推荐
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
	
	public static class QuerySort{
		
		public static final int SORT_NEW = 1;
		public static final int SORT_SCORE = 2;
		
		
		public static Sort getSort(int sort,boolean reverse)
		{
			if (sort == SORT_SCORE)
			{
				return null;
			}
			
			return VideoQueryManager.SORT_NEW_DESC;
		}
	}
	
	public static class Web{
		public static  final  String SEARCH_URL = "/v";
		public static  final  int SEARCH_PAGESIZE = 20;
	}
	
}
