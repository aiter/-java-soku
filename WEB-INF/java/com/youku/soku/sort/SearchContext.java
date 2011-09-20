package com.youku.soku.sort;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.LockQuery;
import com.youku.search.sort.core.entity.ConvertPage;
import com.youku.search.sort.core.entity.Page;
import com.youku.search.sort.core.entity.Span;
import com.youku.soku.Query;
import com.youku.soku.config.Config;
import com.youku.soku.index.server.Server;
import com.youku.soku.index.server.ServerManager;
import com.youku.soku.util.Constant;

public class SearchContext {

	public static final int DEFAULT_INDEX_PAGE_SIZE = 50;// 索引每页数据

	public static final int MIX_TOTAL = 750;

	Log logger = LogFactory.getLog(getClass());

	public Parameter param;// 前台请求参数

	public Page frontPage;// 查询分页

	// lucene 相关参数
	public int indexPageSize;// 索引每页数据
	public Page indexPage;// 索引分页
	public int minLuceneResultCount;
	public LockQuery lockQuery;
	public Comparator comparator;

	// 其他
	public Span span;// 前台结果页在lucene查询结果中的偏移量

	// 查询结果缓存的key
	public String cacheKey;

	public SearchContext(HttpServletRequest request) {
		this(new Parameter(request), DEFAULT_INDEX_PAGE_SIZE);
	}

	public SearchContext(HttpServletRequest request, int indexPageSize) {
		this(new Parameter(request), indexPageSize);
	}

	public SearchContext(Parameter parameter) {
		this(parameter, DEFAULT_INDEX_PAGE_SIZE);
	}

	public SearchContext(Parameter parameter, int indexPageSize) {

		param = parameter;

		frontPage = new Page(param.page, param.pagesize);

		this.indexPageSize = indexPageSize;

		InetSocketAddress[] luceneServers = getLuceneServers();
		final int luceneServerNum = luceneServers.length;

		if (param._mix) {
			ConvertPage convertPage = new ConvertPage(frontPage, indexPageSize,
					luceneServerNum);
			span = convertPage.offset;
			indexPage = new Page(1, indexPageSize);
			minLuceneResultCount = MIX_TOTAL;
		} else if (luceneServerNum <= 1) {// 只有一台服务器的情况下
			ConvertPage convertPage = new ConvertPage(frontPage, indexPageSize,
					luceneServerNum);
			span = convertPage.offset;
			indexPage = convertPage.toPage;
			minLuceneResultCount = span.start + 1 * frontPage.page_size;

		} else {// 多台服务器、普通字段排序的情况下
			ConvertPage convertPage = new ConvertPage(frontPage, indexPageSize,
					luceneServerNum);
			span = convertPage.offset;
			indexPage = convertPage.toPage;
			minLuceneResultCount = span.start + 5 * frontPage.page_size;
		}

		lockQuery = new LockQuery(luceneServers, buildQueryObject());

		cacheKey = getCacheKey();

		comparator = ComparatorFactory.get(param.sort, param.reverse);
	}

	/**
	 * 返回InetSocketAddress数组，不为null
	 */
	private InetSocketAddress[] getLuceneServers() {

		final int currentGroup = Config.getGroupNumber();

		List<Server> list = ServerManager.getVideoServers(currentGroup);

		List<InetSocketAddress> luceneServers = new ArrayList<InetSocketAddress>();

		if (list != null) {
			for (Server server : list) {
				luceneServers.add(new InetSocketAddress(server.getIp(), server
						.getPoolport()));
			}
		}

		return luceneServers.toArray(new InetSocketAddress[0]);
	}

	protected Object buildQueryObject() {
		Query query = new Query();
		query.field = Constant.QueryField.VIDEO;
		query.keywords = param.keyword;
		query.start = indexPage.start();
		query.end = indexPage.end();
		query.hd = param.hd;
		query.limitDate = param.limit_date;
		query.site = param.site;
		query.timeLength = param.time_length;
		query.operator = param.logic;
		// query.needAnalyze = param.na;

		query.sort = param.sort;
		query.reverse = param.reverse;
		query.exclude_sites = param.exclude_sites;
		query.include_sites = param.include_sites;

		query.highlight = param.hl;
		query.hl_prefix = param.hl_prefix;
		query.hl_suffix = param.hl_suffix;

		return query;
	}

	/**
	 * 该查询请求对应的查询结果缓存key
	 */
	public String getCacheKey() {
		return getCacheKey(param.page);
	}

	/**
	 * 该查询请求对应的查询结果缓存key；指定第几页
	 */
	public String getCacheKey(int page) {
		StringBuilder builder = new StringBuilder();

		builder.append(param.keyword + "_");
		builder.append(page + "_");
		builder.append(param.pagesize + "_");
		builder.append(param.hd + "_");
		builder.append(param.limit_date + "_");
		builder.append(param.site + "_");
		builder.append(param.time_length + "_");
		builder.append(param.logic + "_");
		builder.append(param.na + "_");
		builder.append(param.sort + "_");
		builder.append(param.reverse + "_");
		builder.append(Arrays.toString(param.exclude_sites) + "_");
		builder.append(Arrays.toString(param.include_sites) + "_");//TODO 每添加一个字符，原来的缓存全部无法获取
		builder.append(param.hl + "_");
		builder.append(param.hl_prefix + "_");
		builder.append(param.hl_suffix + "_");
		builder.append(param.dup + "_");

		builder.append(param._mix + "_");
		builder.append(param._mix_cache_key + "_");

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("keyword: " + param.keyword + "; ");
		builder.append("frontPage: " + frontPage + "; ");
		builder.append("indexPageSize: " + indexPageSize + "; ");
		builder.append("indexPage: " + indexPage + "; ");
		builder.append("minLuceneResultCount: " + minLuceneResultCount + "; ");
		builder.append("lockQuery: " + lockQuery + "; ");
		builder.append("comparator: " + comparator + "; ");
		builder.append("span: " + span + "; ");
		builder.append("cacheKey: " + cacheKey + "; ");

		return builder.toString();
	}

}