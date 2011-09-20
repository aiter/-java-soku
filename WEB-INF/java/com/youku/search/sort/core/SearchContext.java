package com.youku.search.sort.core;

import java.net.InetSocketAddress;
import java.util.Comparator;

import com.youku.search.index.entity.AdvanceQuery;
import com.youku.search.index.entity.Query;
import com.youku.search.index.entity.SpecialQuery;
import com.youku.search.index.server.ChangeServer;
import com.youku.search.index.server.ChangeServer.AccessControl;
import com.youku.search.index.server.ServerWrapper;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.core.entity.ConvertPage;
import com.youku.search.sort.core.entity.Page;
import com.youku.search.sort.core.entity.Span;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.sort.util.bridge.comparator.ComparatorFactory;
import com.youku.search.util.Constant.Sort;

public class SearchContext<T> {

	public Parameter p;

	public int type;
	public int searchType;
	public int indexPageSize;

	public Page frontPage;
	public Page indexPage;
	public Span span;
	public int minLuceneResultCount;// 从后台lucene取回的最少结果数
	
	public Object query;
	public LockQuery lockQuery;
	
	public String cacheKey;

	// 暂时把泛型去掉，以后可能需要重构这里的逻辑
	// modified by gaosong 2011-06-01
//	public Comparator<T> comparator;
	public Comparator comparator;

	public boolean one_by_one = false;
	
	public ServerWrapper serverWrapper;
	
	public SearchContext(Parameter p, int indexPageSize) {
		this.p = p;

		type = p.type;
		searchType = SearchUtil.getLuceneSearchType(p.type);
		this.indexPageSize = indexPageSize;
		
		frontPage = new Page(p.curPage, p.pageSize);
		
		serverWrapper = buildServers(p);
		
		if (isOneByOne()) {// 多台服务器、按时间排序的情况下
			indexPage = new Page(1, 1);// 用于统计结果总数
			one_by_one = true;
			minLuceneResultCount = 0 + 3 * frontPage.page_size;
		} else {	// 普通字段排序的情况下（不管几台服务器，都只返回一页数据就够了）
			ConvertPage convertPage = new ConvertPage(frontPage, indexPageSize,
					serverWrapper.getServerNum());
			span = convertPage.offset;
			indexPage = convertPage.toPage;
			
			// 由于用户看第一页的需求占70%多，所以只需要取一页数据就可以了
			minLuceneResultCount = span.start + frontPage.page_size;
		}
		
		query = buildQueryObject();// query的构造依赖于indexPage对象
		
		lockQuery = new LockQuery(serverWrapper.getAddresses(), query, p.type);
		
		cacheKey = SearchUtil.CacheKey.common(p);
		
		comparator = ComparatorFactory.getComparator(type, p.orderField, p.reverse);
	}
	
	public ServerWrapper buildServers(Parameter p) {
		ServerWrapper sw = null;
		if (SearchUtil.getIsDynamicServer(p.type)){
			AccessControl accessControl = ChangeServer.I.getCurrentAccessControl();
			InetSocketAddress[] addresses = accessControl.getOnLineServerSockets();
			sw = new ServerWrapper(p, addresses, accessControl);
		} else {
			InetSocketAddress[] addresses = SearchUtil.getLuceneServers(searchType);
			sw = new ServerWrapper(p, addresses);
		}
		
		return sw;
	}
	
	private boolean isOneByOne() {

		if (serverWrapper.getServerNum() <= 1) {
			return false;
		}

		switch (p.orderField) {
		case Sort.SORT_NEW:
		case Sort.SORT_USER_NEW_REG:
		case Sort.SORT_USER_NEW_UPDATE:
		case Sort.SORT_PK_NEW:
		case Sort.SORT_BARPOST_NEW:
			return true;

		default:
			return false;
		}
	}

	private Object buildQueryObject() {

//		if (p.type == SearchConstant.VIDEO_TITLE_TAG
//				|| p.type == SearchConstant.FOLDER_TITLE_TAG) {
//			// VIDEO_TITLE_TAG需要ftype参数
//			return buildSpecialQuery(p.type == SearchConstant.VIDEO_TITLE_TAG);
//		}
		
		// VIDEO_TITLE_TAG修改为走下面的buildQuery逻辑
		// modified by gaosong 2011-07-15
		if (p.type == SearchConstant.FOLDER_TITLE_TAG) {
			return buildSpecialQuery(false);
		}

		if (p.isAdvanceSearch) {
			return buildAdvanceQuery();
		}

		return buildQuery();
	}

	private Query buildQuery() {

		Query query = new Query();
		// 第零部分：普通查询、高级查询共同使用的参数
		query.keywords = p.query;
		query.field = searchType;
		
		query.start = indexPage.start();
		query.end = indexPage.end();
		query.indexPage = indexPage;
		
		query.sort = p.orderField;
		query.orderFieldStr = p.orderFieldStr;
		query.limitDate = p.limitDate;
		
		query.reverse = p.reverse;
		query.ftype = p.ftype;
		query.timeless = p.timeless;
		query.timemore = p.timemore;
		query.highlight = p.hl;
		query.hl_prefix = p.hl_prefix;
		query.hl_suffix = p.hl_suffix;
		query.limit_level = p.limit_level;
		query.exclude_cates = p.exclude_cates;

		// 第一部分：普通查询使用的参数
		query.date_start = p.date_start;
		query.date_end = p.date_end;

		query.operator = p.logic;
		query.category = p.cateId;
		query.categories = joinString(p.categories);
		query.partner = p.partnerId;
		query.needAnalyze = p.na;

		// 由于现在的C-Server不对"md5:"进行特殊处理，所以这里注释掉
		// modified by gaosong 2011-07-21
//		if (p.md5) { // 查询md5相同的视频
//			query.reverse = true;
//			query.keywords = "md5:" + p.query;
//			query.needAnalyze = false;
//		}

		return query;
	}

	private AdvanceQuery buildAdvanceQuery() {
		AdvanceQuery query = new AdvanceQuery();

		// 第零部分：普通查询、高级查询共同使用的参数
		query.keywords = p.query;
		query.field = searchType;
		
		query.start = indexPage.start();
		query.end = indexPage.end();
		query.indexPage = indexPage;
		
		query.sort = p.orderField;
		query.reverse = p.reverse;
		query.ftype = p.ftype;
		query.timeless = p.timeless;
		query.timemore = p.timemore;
		query.highlight = p.hl;
		query.hl_prefix = p.hl_prefix;
		query.hl_suffix = p.hl_suffix;
		query.limit_level = p.limit_level;
		query.exclude_cates = p.exclude_cates;

		// 第二部分：高级查询使用的参数
		query.categories = joinString(p.categories);
		query.fields = joinString(p.fields);
		query.pv = p.pv;
		query.comments = p.comments;
		query.limitDate = p.limitDate;
		query.hd = p.hd;

		return query;
	}

	private SpecialQuery buildSpecialQuery(boolean setFtype) {

		SpecialQuery query = new SpecialQuery();
		query.start = indexPage.start();
		query.end = indexPage.end();
		query.sort = p.orderField;
		query.reverse = p.reverse;

		int index = p.query.indexOf("|");
		if (index == -1) {
			query.title = p.query;
			query.tags = null;
		} else {
			query.title = p.query.substring(0, index);
			query.tags = p.query.substring(index + 1);
		}

		query.category = p.cateId;
		query.exclude_cates = p.exclude_cates;
		query.partner = p.partnerId;
		query.field = searchType;
		query.operator = p.logic;
		query.needAnalyze = p.na;

		if (setFtype) {
			query.ftype = p.ftype;
		}

		query.timeless = p.timeless;
		query.timemore = p.timemore;

		return query;
	}

	private static String joinString(String[] array) {
		if (array == null || array.length == 0) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				result.append(",");
			}
			result.append(array[i]);
		}
		return result.toString();
	}

	public static void main(String[] args) {
		System.out.println(joinString(new String[] { "" }));
		System.out.println(joinString(new String[] { "", "a" }));
		System.out.println(joinString(new String[] {}));
		System.out.println(joinString(null));
		System.out.println(joinString(new String[] { "1", "2", "23" }));

		// /
		String s = "aas|a";
		int index = s.indexOf("|");
		System.out.println(s.substring(0, index));
		System.out.println(s.substring(index + 1));
	}

}
