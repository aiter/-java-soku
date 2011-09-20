package com.youku.search.sort.util.bridge;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import com.youku.search.index.entity.BaseQuery;
import com.youku.search.pool.net.parser.AbstractQueryParser;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;

public class SearchUtil {

	public static class CacheKey {

		public static String common(Parameter p) {
			StringBuilder buffer = new StringBuilder();

			// 第零部分：普通查询、高级查询共同使用的参数
			buffer.append(p.query + "_");
			buffer.append(p.type + "_");
			buffer.append(p.curPage + "_");
			buffer.append(p.pageSize + "_");
			buffer.append(p.orderFieldStr + "_");
			buffer.append(p.reverse + "_");
			buffer.append(p.ftype + "_");
			buffer.append(p.timeless + "_");
			buffer.append(p.timemore + "_");
			buffer.append(p.hl + "_");
			buffer.append(p.hl_prefix + "_");
			buffer.append(p.hl_suffix + "_");
			buffer.append(p.limit_level + "_");
			buffer.append(p.exclude_cates + "_");

			buffer.append(p.isAdvanceSearch + "_");// note this

			if (p.isAdvanceSearch) {
				// 第二部分：高级查询使用的参数
				buffer.append(Arrays.toString(p.categories) + "_");
				buffer.append(Arrays.toString(p.fields) + "_");
				buffer.append(p.limitDate + "_");
				buffer.append(p.pv + "_");
				buffer.append(p.comments + "_");
				buffer.append(p.limitDate + "_");
				buffer.append(p.hd + "_");

			} else {
				// 第一部分：普通查询使用的参数
				buffer.append(p.date_start + "_");
				buffer.append(p.date_end + "_");

				buffer.append(p.logic + "_");
				buffer.append(p.cateId + "_");
				buffer.append(p.partnerId + "_");
				buffer.append(p.na + "_");
				buffer.append(p.md5 + "_");
				buffer.append(p.relNum + "_");
				buffer.append(p.options + "_");
			}

			return buffer.toString();
		}

		public static String recommend(String query, int type) {
			return getTypeStr(type) + "_recommend_" + query;
		}

		public static String music(String query, int type) {
			return getTypeStr(type) + "_music_" + query;
		}

		public static String suggestionWords(Parameter p) {
			return "Suggestion_words_" + p.type + "_" + p.query;
		}

		public static String relavantWords(Parameter p) {
			return "Relevant_words_" + p.type + "_" + p.query + "_" + p.relNum;
		}

	}

	/**
	 * 根据web前端的查询类型值，返回一个相应的文本描述，同时该文本也是数据库中相应列的取值
	 */
	public static String getTypeStr(int type) {
		return getValue(BridgeMap.TypeColumnName, type);
	}

	/**
	 * 根据web前端指定的查询类型和排序字段的组合，返回后台lucene的排序方式。
	 */
	public static int getLuceneOrderField(int type, String orderFieldStr) {

		AbstractSortComparator sortComparator = getValue(
				BridgeMap.SortComparator, type);

		int orderField = sortComparator.getOrderField(orderFieldStr);
		return orderField;
	}
	
	/**
	 * @param type
	 *            web前端指定的查询类型
	 * @param luceneOrderField
	 *            后台lucene排序常量
	 * @param reverse
	 *            是否反序
	 * @return Comparator，供前台聚合查询结果时排序使用。返回null表示java默认排序
	 */
	public static <T> Comparator<T> getComparator(int type,
			int luceneOrderField, boolean reverse) {

		AbstractSortComparator<T> sortComparator = getValue(
				BridgeMap.SortComparator, type);

		Comparator comparator = sortComparator.getComparator(luceneOrderField,
				reverse);

		return comparator;
	}
	
	/**
	 * 
	 * @param type web前端指定的查询类型
	 * @param innerOrderFieldStr 内部指定的排序规则（在各个Comparator实现中的orderFields字段中定义）
	 * @param reverse 是否倒序
	 * @return
	 */
	public static <T> Comparator<T> getComparator(int type,
			String innerOrderFieldStr, boolean reverse) {
		
		AbstractSortComparator sortComparator = getValue(
				BridgeMap.SortComparator, type);
		
		int orderField = sortComparator.getOrderField(innerOrderFieldStr);
		
		Comparator comparator = sortComparator.getComparator(orderField, reverse);
		
		return comparator;
	}

	/**
	 * 在web前端没有指明排序字段的情况下，根据web前端的查询类型，返回后台lucene默认的排序方式。
	 */
	public static int getLuceneOrderField(int type) {
		return getLuceneOrderField(type, "null");
	}

	/**
	 * 把web前端的查询类型值转换为后台lucene的查询类型值
	 */
	public static int getLuceneSearchType(int type) {
		return getValue(BridgeMap.SearchType, type);
	}

	/**
	 * web前端的查询类型 --> 后台lucene的索引文件中索引字段名
	 * 
	 * @see org.apache.lucene.document.Field
	 */
	public static String getLuceneDocumentPKFieldName(int type) {
		return getValue(BridgeMap.EntityPKFieldIndexName, type);
	}

	/**
	 * 根据web前端的查询类型，返回后台lucene的索引文档document对应的java类的主键字段名称
	 * 
	 * @see com.youku.search.index.entity.Video
	 * @see com.youku.search.index.entity.Folder
	 * @see com.youku.search.index.entity.User
	 * @see com.youku.search.index.entity.BarPost
	 * @see com.youku.search.index.entity.Pk
	 */
	public static String getLuceneDocumentObjectPKFieldName(int type) {
		return getValue(BridgeMap.EntityPKFieldName, type);
	}

	/**
	 * 根据后台lucene查询类型，返回相应索引文件所在的server地址
	 */
	public static InetSocketAddress[] getLuceneServers(int luceneSearchType) {
		return getValue(BridgeMap.LuceneServer, luceneSearchType);
	}
	
	/**
	 * 根据web前端的视频分类 ，返回后台C-Server的视频分类
	 */
	public static int getVideoCategory(int cateId){
		return getValue(BridgeMap.VideoCategory, cateId);
	}
	
	/**
	 * 根据web前端的查询类型，返回此类查询的后台是否是多台动态服务器（走c-servers.xml）
	 * 
	 * @return
	 */
	public static boolean getIsDynamicServer(int type){
		return BridgeMap.IsDynamicServer.contains(type);
	}
	
	/**
	 * 根据web前端的查询类型，返回此类查询的后台是否是C-Server服务器（走c-servers.xml）
	 * 
	 * @return
	 */
	public static boolean getIsCServer(int type){
		return BridgeMap.IsCServer.contains(type);
	}
	
	public static AbstractQueryParser getQueryParser(Class queryClass){
		return getValue(BridgeMap.QueryParserType, queryClass);
	}
	
	public static <V> V getValue(Map<Class, V> map, Class className) {
		if (map.containsKey(className)) {
			return map.get(className);
		}

		throw new IllegalArgumentException("未知的类型：class: " + className.getSimpleName());
	}
	
	public static <V> V getValue(Map<Integer, V> map, Integer type) {
		if (map.containsKey(type)) {
			return map.get(type);
		}

		throw new IllegalArgumentException("未知的类型：type: " + type);
	}
}
