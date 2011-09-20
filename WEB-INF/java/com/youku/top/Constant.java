/**
 * 
 */
package com.youku.top;

import org.apache.lucene.queryParser.QueryParser;

public class Constant {
	
	public static String DEFAULT_VIDEO_LOGO="http://g4.ykimg.com/0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0";
	
	public static class QueryField{
		public static final int VIDEO = 1; 	//视频
		public static final int CORRECT = 2; 	//纠错
		public static final int LIKE = 3; 	//相关
		public static final int QUICK = 4; 	//上升最快
		public static final int LOG = 5; 	//日志
		public static final int LOGLIKE = 6; 	//日志相关
		public static final int LIBRARY = 7; 	//目录排行榜
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
}
