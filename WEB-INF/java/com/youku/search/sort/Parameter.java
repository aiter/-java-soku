package com.youku.search.sort;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.search.impl.AdvanceFolderSearch;
import com.youku.search.sort.search.impl.AdvanceVideoSearch;
import com.youku.search.sort.search.impl.VideoSearch.VideoSearchOptions;
import com.youku.search.sort.util.BaseFilter;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.sort.util.filter.GBKEncodingFilter;
import com.youku.search.sort.util.filter.JSONFilter;
import com.youku.search.util.Constant;
import com.youku.search.util.Constant.Socket;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

/**
 * a class for getting arguments from http request
 */
public class Parameter implements Cloneable {

	static Log logger = LogFactory.getLog(Parameter.class);

	static final String ServerHostAddress;

	static {
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		ServerHostAddress = address;
	}

	/**
	 * 给C-Server传递的参数常量
	 * 
	 * @author gaosong
	 */
	public static final class RequestParameterConstant {

		/**
		 * 给C-Server传递的最大Query长度
		 */
		public static final int MAX_QUERY_LENGTH = 37;
		public static final int MAX_MD5_LENGTH = 67;

		/**
		 * 是否按“高清”过滤
		 */
		public static final String HD_ONLY = "0100000000000000";
		public static final String HD_ONLY_FALSE = "0000000000000000";

	}

	public static class ParameterConstant {
		public static final String NA_FALSE = "0";// 不需要分词

		public static final String DEL_MEMCACHE = "delcache";// 刷新memcache
		public static final String RECMD = "recmd";// 刷新推荐内容
		public static final String ORDER_REVERSE = "1";// 反序排序

		public static final int PAGE_SIZE_DEFAULT = 25;// 一页数据
		// public static final int PAGE_SIZE_MAX = 100;// 一页数据
		public static final int PAGE_SIZE_MAX = Integer.MAX_VALUE;// 一页数据

		public static final String LOGIC_OR = "2";//

		public static final String ADVANCE_TRUE_1 = "1";// 高级搜索标志
		public static final String ADVANCE_TRUE_2 = "true";// 高级搜索标志

		public static final int CATE_ID_DEFAULT = 0;// 默认的种类ID

		public static final String HD_ONLY_STR = "1";// 是否只显示清晰视频 0全部 1清晰
		public static final int HD_ONLY_FALSE = 0;
		public static final int HD_ONLY = 1;

		public static final String FEED_BACK_TRUE = "1";

		public static final String HL_TRUE = "true";

	}

	// ==== 供查询使用的参数 ======================================================
	// 第零部分：普通查询、高级查询共同使用的参数
	public String query;// web前端提交的查询关键词，注意：这是小写处理后的结果
	public String queryOriginal;// 2011.1.19加入。保存用户的原始输入
	public int type;// web前端指定的查询类型:com.youku.search.sort.constant.SearchConstant
	public int curPage;// 请求第几页的数据，1为第一页
	public int pageSize;// 每页数据的条数
	public String orderFieldStr;// web前端提交的排序字段
	public int orderField;// 由type和orderFieldStr决定的后台lucene的排序字段
	public boolean reverse;// 查询结果是否反序，默认为反序；正序是指自然顺序
	public String ftype;// 视频格式条件
	public int timeless;// 视频时长小于条件
	public int timemore;// 视频时长大于条件

	public String limitDate;// 视频上传的时间段
	public int date_start; // 视频上传的时间段, 开始日期时间戳除以1000，包含此点
	public int date_end;// 结束日期时间戳除以1000，不包含此点

	public boolean hl;// 是否启用高亮
	public String hl_prefix;// 高亮前缀
	public String hl_suffix;// 高亮后缀
	public int limit_level;// 过滤加密视频
	public String exclude_cates;// 排除掉的分类

	// 第一部分：普通查询使用的参数
	public int logic;// 查询关键词之间的组合方式：AND OR
	public int cateId;// 视频所在分类
	public int partnerId;// 合作者ID
	public boolean na;// 是否需要对query进行分词处理
	public boolean md5;// 普通搜索之md5查重复视频
	public int relNum;// 相关搜索的结果数目，只用于视频搜索和专辑搜索
	public VideoSearchOptions options = new VideoSearchOptions();// 查询结果选项参数

	// 剧集搜索扩充
	public String callback;
	public boolean feedback;

	// 第二部分：高级查询使用的参数
	public boolean isAdvanceSearch;// 是否为高级搜索
	public String[] categories;// 视频所在分类的ID列表，用|分割，例如 1|2|34
	public String[] fields;// 要搜索的字段名，用|分割，例如 author|title|tag
	public int pv;// 最小PV
	public int comments;// 最小评论数
	public int hd;// 是否只显示清晰视频 0全部 1清晰

	// ==== 供管理、修饰、统计等杂项使用的参数 =======================================
	public boolean delMemchache;// 用于管理：刷新memcache缓存
	public int recmdStatus;// 用于管理：刷新memcache中视频推荐的信息
	public boolean human;// 搜索结果显示是否格式化，以便于阅读
	public String queryUrl;// 请求url
	public String _source;// 用于统计：搜索请求的来源
	
	public Parameter(HttpServletRequest request) {

		Map<ParameterNames, String> map = new HashMap<ParameterNames, String>();
		for (ParameterNames name : ParameterNames.values()) {
			map.put(name, request.getParameter(name.name()));
		}

		map.put(ParameterNames.query_url, buildQueryUrl(request));

		init(map);
	}

	public Parameter(Map<ParameterNames, String> map) {
		init(map);
	}

	public void init(Map<ParameterNames, String> map) {

		// ==== 供查询使用的参数 ======================================================
		// 第零部分：普通查询、高级查询共同使用的参数
		String type_param = map.get(ParameterNames.type);
		type = StringUtil.parseInt(type_param, SearchConstant.VIDEO);
		
		md5 = map.get(ParameterNames.md5) != null;
		
		String keyword_param = map.get(ParameterNames.keyword);
		keyword_param = keyword_param == null ? "" : keyword_param;
		queryOriginal = keyword_param;
		if (md5) {
			type = SearchConstant.VIDEO_MD5;
			query = KeywordFilter.filter(keyword_param, RequestParameterConstant.MAX_MD5_LENGTH, false, getFilters(type));
		} else {
			query = KeywordFilter.filter(keyword_param, RequestParameterConstant.MAX_QUERY_LENGTH, true, getFilters(type));
		}

		String curpage_param = map.get(ParameterNames.curpage);
		curPage = parseCurPage(curpage_param);

		String pagesize_param = map.get(ParameterNames.pagesize);
		pageSize = parsePageSize(pagesize_param);

		String orderFieldStr_param = map.get(ParameterNames.orderfield);
		String order_param = map.get(ParameterNames.order);
		if (SearchConstant.isNeedSortType(type)) {
			if (orderFieldStr_param == null || orderFieldStr_param.equals("")) {
				orderFieldStr = "null";
			} else {
				orderFieldStr = orderFieldStr_param;
			}

			orderField = SearchUtil.getLuceneOrderField(type, orderFieldStr);

			if (order_param == null
					|| ParameterConstant.ORDER_REVERSE.equals(order_param)) {
				reverse = true;
			} else {
				reverse = false;
			}
		}

		String ftype_param = map.get(ParameterNames.ftype);
		ftype = ftype_param == null ? null : ftype_param.trim();

		String timeless_param = map.get(ParameterNames.timeless);
		timeless = StringUtil.parseInt(timeless_param, 0);

		String timemore_param = map.get(ParameterNames.timemore);
		timemore = StringUtil.parseInt(timemore_param, 0);

		String limit_date_param = map.get(ParameterNames.limit_date);
		limitDate = limit_date_param;
		{
			if (limitDate == null || limitDate.equals("0")) {
				limitDate = null;
				date_start = 0;
				date_end = 0;
			} else {
				limitDate = limitDate.trim();
				Date startTime = null;
				Date endTime = null;
				Date now = new Date();

				String[] arr = limitDate.split("-");
				if (arr.length == 2) {
					int end = DataFormat.parseInt(arr[0]);
					int start = DataFormat.parseInt(arr[1]);
					startTime = DataFormat.getNextDate(now, 0 - start);
					endTime = DataFormat.getNextDate(now, 0 - end);

					date_start = (int) (startTime.getTime() / 1000);
					date_end = (int) (endTime.getTime() / 1000);
				} else {
					int start = DataFormat.parseInt(limitDate);
					if (start > 0) {
						startTime = DataFormat.getNextDate(now, 0 - start);
						date_start = (int) (startTime.getTime() / 1000);
					}
				}
			}
		}

		String hl_param = map.get(ParameterNames.hl);
		hl = ParameterConstant.HL_TRUE.equals(hl_param);

		String hl_prefix_param = map.get(ParameterNames.hl_prefix);
		hl_prefix = hl_prefix_param;

		String hl_suffix_param = map.get(ParameterNames.hl_suffix);
		hl_suffix = hl_suffix_param;

		String limit_level_param = map.get(ParameterNames.limit_level);
		limit_level = StringUtil.parseInt(limit_level_param, 0);

		String exclude_cates_param = map.get(ParameterNames.exclude_cates);
		appendExclude_cates(exclude_cates_param);

		// 第一部分：普通查询使用的参数
		String logic_param = map.get(ParameterNames.logic);
		if (ParameterConstant.LOGIC_OR.equals(logic_param)) {
			logic = Constant.Operator.OR;
		} else {
			logic = Constant.Operator.AND;
		}

		String cateid_param = map.get(ParameterNames.cateid);
		cateId = StringUtil.parseInt(cateid_param,
				ParameterConstant.CATE_ID_DEFAULT);

		String partner_param = map.get(ParameterNames.partner);
		partnerId = StringUtil.parseInt(partner_param, 0);

		String na_param = map.get(ParameterNames.na);
		if (na_param != null && ParameterConstant.NA_FALSE.equals(na_param)) {
			na = false;
		} else {
			na = true;
		}

		String relnum_param = map.get(ParameterNames.relnum);
		relNum = StringUtil.parseInt(relnum_param, 0);

		String optionsValue = map.get(ParameterNames.video_options);
		options = parseOptions(optionsValue);

		String categories_param = map.get(ParameterNames.categories);
		if (categories_param != null && categories_param.length() > 0) {
			categories = categories_param.split(",");
		}
		if (categories == null || categories.length < 1) {
			categories = null;
		}

		// 剧集搜索扩充
		String callback_param = map.get(ParameterNames.callback);
		if (callback_param != null && callback_param.length() > 0) {
			callback = callback_param;
		} else {
			callback = null;
		}

		String feedback_param = map.get(ParameterNames.feedback);
		if (feedback_param != null
				&& ParameterConstant.FEED_BACK_TRUE.equals(feedback_param)) {
			feedback = true;
		} else {
			feedback = false;
		}

		// 第二部分：高级查询使用的参数
		isAdvanceSearch = false;// 默认为非高级查询
		String advance_param = map.get(ParameterNames.advance);
//		if (type == SearchConstant.VIDEO || type == SearchConstant.FOLDER) {
//			if (ParameterConstant.ADVANCE_TRUE_1.equals(advance_param)
//					|| ParameterConstant.ADVANCE_TRUE_2.equals(advance_param)) {
//				isAdvanceSearch = true;
//			}
//			if (query != null && query.startsWith("{") && query.endsWith("}")) {
//				isAdvanceSearch = true;
//				query = query.substring(1, query.length() - 1);
//			}
//		}

		String fields_param = map.get(ParameterNames.fields);
		if (fields_param != null && fields_param.length() > 0) {
			fields = fields_param.split("\\|");
		}
		if ((fields == null || fields.length == 0) && isAdvanceSearch) {
			if (type == SearchConstant.VIDEO) {// 高级视频搜索
				fields = AdvanceVideoSearch.searchFields;

			} else if (type == SearchConstant.FOLDER) {// 高级专辑搜索
				fields = AdvanceFolderSearch.searchFields;

			} else {
				throw new RuntimeException("未知的高级查询类型：type = " + type);
			}
		}

		String pv_param = map.get(ParameterNames.pv);
		pv = StringUtil.parseInt(pv_param, 0);

		String comments_param = map.get(ParameterNames.comments);
		comments = StringUtil.parseInt(comments_param, 0);

		String hd_param = map.get(ParameterNames.hd);
		if (ParameterConstant.HD_ONLY_STR.equals(hd_param)) {
			hd = ParameterConstant.HD_ONLY;
		} else if ("6".equals(hd_param)) {
			hd = 6;
		} else {
			hd = ParameterConstant.HD_ONLY_FALSE;
		}

		// ==== 供管理、修饰、统计等杂项使用的参数 =======================================
		String admin_param = map.get(ParameterNames.admin);
		if (admin_param != null
				&& ParameterConstant.DEL_MEMCACHE.equals(admin_param)) {
			delMemchache = true;
		} else {
			delMemchache = false;
		}

		if (admin_param != null && ParameterConstant.RECMD.equals(admin_param)) {
			recmdStatus = 1;
		} else {
			recmdStatus = 0;
		}

		human = map.get(ParameterNames.h) != null;

		queryUrl = map.get(ParameterNames.query_url);
		queryUrl = String.valueOf(queryUrl);

		String _source_param = map.get(ParameterNames._source);
		_source = String.valueOf(_source_param);
	}

	public BaseFilter[] getFilters(int type) {
		BaseFilter[] filters = null;
		if (SearchUtil.getIsCServer(type)) {
			filters = new BaseFilter[]{new JSONFilter(), new GBKEncodingFilter()};
		}
		
		if (null == filters) {
			filters = new BaseFilter[0];
		}
		
		return filters;
	}

	private VideoSearchOptions parseOptions(String optionsValue) {

		VideoSearchOptions options = new VideoSearchOptions();

		if (optionsValue == null || optionsValue.length() == 0) {
			return options;// 返回默认值
		}

		// validate
		char[] chars = optionsValue.toCharArray();
		for (char c : chars) {
			if (c == '0' || c == '1') {
				continue;
			}
			return options;// 返回默认值
		}

		// ok
		options.clearAll();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '0') {
				options.bitSet.clear(i);
			} else {
				options.bitSet.set(i);
			}
		}

		return options;
	}

	private int parseCurPage(String curPage_param) {
		return StringUtil.parseInt(curPage_param, 1, 1);
	}

	private int parsePageSize(String pageSize_param) {
		int pageSize = StringUtil.parseInt(pageSize_param,
				ParameterConstant.PAGE_SIZE_DEFAULT, 1);

		if (pageSize > ParameterConstant.PAGE_SIZE_MAX) {
			pageSize = ParameterConstant.PAGE_SIZE_DEFAULT;
		}

		return pageSize;
	}

	public static String buildQueryUrl(HttpServletRequest request) {

		StringBuilder builder = new StringBuilder();

		builder.append("http://");
		builder.append(ServerHostAddress);
		builder.append(request.getRequestURI());

		String queryString = request.getQueryString();
		if (queryString != null) {
			builder.append("?");
			builder.append(queryString);
		}

		return builder.toString();
	}

	@Override
	public Parameter clone() {
		try {
			return (Parameter) super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public Parameter copy() {
		return clone();
	}

	protected Parameter copy(int curPage, int pageSize, int type) {
		Parameter parameter = copy();
		parameter.curPage = parseCurPage(curPage + "");
		parameter.pageSize = parsePageSize(pageSize + "");
		parameter.type = type;
		return parameter;
	}

	// /////////////////////////////////////////////////////////
	public SortedSet<Integer> parseExclude_cates(String cates) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		if (cates != null) {
			String[] ids = cates.split(",");
			for (int i = 0; i < ids.length; i++) {
				int id = StringUtil.parseInt(ids[i], 0);
				if (id != 0) {
					set.add(id);
				}
			}
		}
		return set;
	}

	public String getExclude_cates(SortedSet<Integer> cates) {
		StringBuilder builder = new StringBuilder();
		if (cates != null) {
			for (Integer i : cates) {
				builder.append(String.valueOf(i));
				builder.append(",");
			}

			if (cates.size() > 0) {
				builder.delete(builder.length() - 1, builder.length());
			}
		}

		return builder.toString();
	}

	public void appendExclude_cates(String cates) {
		SortedSet<Integer> new_cates = parseExclude_cates(cates);
		if (!new_cates.isEmpty()) {
			SortedSet<Integer> old_cates = parseExclude_cates(exclude_cates);
			new_cates.addAll(old_cates);

			exclude_cates = getExclude_cates(new_cates);
		}
	}

	public void appendExclude_cates(List<Integer> cates) {
		if (cates != null && !cates.isEmpty()) {
			SortedSet<Integer> new_cates = parseExclude_cates(exclude_cates);
			new_cates.addAll(cates);

			exclude_cates = getExclude_cates(new_cates);
		}
	}

}
