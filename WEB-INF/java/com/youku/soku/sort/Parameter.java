package com.youku.soku.sort;

import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.youku.common.http.ResinChineseUrl;
import com.youku.search.sort.KeywordFilter;
import com.youku.search.util.Constant;
import com.youku.search.util.StringUtil;
import com.youku.soku.util.Constant.QuerySort;

public class Parameter extends BaseParameter {

	public static final int KEYWORD_MAX_WORDS = 100;

	public static final int DEFAULT_PAGE = 1;
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int DEFAULT_PAGE_MAX = 35;

	// 查询参数
	public int page = DEFAULT_PAGE;// 第几页
	public int pagesize = DEFAULT_PAGE_SIZE;// 每页多少数据
	public int hd;// 是否只显示清晰视频 0全部 1清晰
	public int limit_date;// //时间参数
	public int site; // 来源站点ID
	public int time_length; // 时长限制
	public int logic;// 查询关键词之间的组合方式：AND OR
	public boolean na;// 是否需要对query进行分词处理
	public int sort;// 排序规则
	public boolean reverse;// 查询结果是否反序，正序是指自然顺序
	public boolean hl = true;// 是否启用高亮
	public String hl_prefix;// 高亮前缀
	public String hl_suffix;// 高亮后缀
	public boolean dup;// 是否去重
	public int relnum;// 相关搜索的结果数目
	public int[] exclude_sites;// 排除站点id
	public int[] include_sites;// 只包含站点id  2010.11.02
	public boolean relWords = true;// 是否搜索相关词
	public boolean corWords = true;// 是否纠错
	public boolean picInfo = true;// 是否需要截图信息
	public boolean redirect = true;// 是否跳转详情页
	
	public int cateid;  //视频分类，用于专辑过滤。默认值为0，表示不做限制
	

	public boolean ext = true;// 默认检查直达

	public boolean _mix = false;// 是否混合站内、推荐、站外视频；默认false
	public String _mix_cache_key;// 混合参数信息，用于生成cache key

	// 管理
	public boolean __ic = false;// 是否ignore cache
	public boolean _ignore_log = false;// 查询数据时，是否忽略log记录
	
	public boolean _only_log = false;// 是否只记录日志

	// 杂项
	public boolean h = false;// 是否美化输出, human readable
	public String _source;// 查询请求的来源：youku来自youku.com soku来自soku.com
	public String _because;// 查询请求的原因：1站内空结果 2没版权
	public String queryUrl;

	public ResinChineseUrl chineseUrl = null;
	
	public Parameter() {
	}

	public Parameter(HttpServletRequest request) {
		// 查询参数
		chineseUrl = (ResinChineseUrl)request.getAttribute(ResinChineseUrl.STORE_KEY);
		
		keyword = getParameter(request,"keyword");
		
		keyword = KeywordFilter.filter(keyword, KEYWORD_MAX_WORDS);

		page = StringUtil.parseInt(getParameter(request,"curpage"),
				DEFAULT_PAGE, DEFAULT_PAGE, DEFAULT_PAGE_MAX);

		pagesize = StringUtil.parseInt(getParameter(request,"pagesize"),
				DEFAULT_PAGE_SIZE, 1, 100);

		// 如果值为1，则只显示高清视频；否则显式全部视频
		hd = 0;
		if ("1".equals(getParameter(request,"hd"))) {
			hd = 1;
		}

		limit_date = StringUtil.parseInt(getParameter(request,"limit_date"), 0,
				0);

		site = StringUtil.parseInt(getParameter(request,"site"), 0, 0);

		time_length = StringUtil.parseInt(getParameter(request,"time_length"),
				0, 0);

		// 如果值为2，则为逻辑或；否则，为逻辑与
		if ("2".equals(getParameter(request,"logic"))) {
			logic = Constant.Operator.OR;
		} else {
			logic = Constant.Operator.AND;
		}

		// 如果值为0，则不需要分词；否则，需要分词
		if ("0".equals(getParameter(request,"na"))) {
			na = false;
		} else {
			na = true;
		}

		// sort字段取值为lucene后端的值
		sort = StringUtil.parseInt(getParameter(request,"orderfield"),
				QuerySort.SORT_NEW);

		// 如果没有指定该参数，或者参数值为1，则为反序；否则为正序（自然顺序，asc）
		reverse = getParameter(request,"order") == null
				|| "1".equals(getParameter(request,"order"));

		// 如果参数值为true，则为高亮；否则为false
		if (getParameter(request,"hl") != null)
			hl = "true".equals(getParameter(request,"hl"));
		hl_prefix = getParameter(request,"hl_prefix");
		hl_suffix = getParameter(request,"hl_suffix");

		// 如果dup参数值为0，表示不去重；否则，表示去重
		dup = !("0".equals(getParameter(request,"dup")));

		relnum = StringUtil.parseInt(getParameter(request,"relnum"), 10);
		// 如果rel参数值为0，表示不搜相关词
		relWords = !("0".equals(getParameter(request,"rel")));
		// 如果cor参数值为0，表示不纠错
		corWords = !("0".equals(getParameter(request,"cor")));
		// 如果pic参数值为0，表示返回图片信息
		picInfo = !("0".equals(getParameter(request,"pic")));
		redirect = !("0".equals(getParameter(request,"redirect")));

		exclude_sites = parseExcludeSites(getParameter(request,"exclude_sites"));
	
		cateid = StringUtil.parseInt(getParameter(request,"cateid"), 0, 0);

		// 2表示不检查直达，其他都检查
		ext = !"2".equals(getParameter(request,"ext"));

		// 管理
		__ic = getParameter(request,"__ic") != null;
		_ignore_log = getParameter(request,"_ignore_log") != null;
		_only_log = getParameter(request,"_only_log") != null;

		// 杂项
		h = getParameter(request,"h") != null;
		_source = getParameter(request,"_source");
		_because = getParameter(request,"_because");

		queryUrl = buildQueryUrl(request);
	}

	private int[] parseExcludeSites(String exclude_sites) {
		if (exclude_sites == null) {
			return null;
		}

		String[] sitesArray = exclude_sites.split(",");
		if (sitesArray == null || sitesArray.length == 0) {
			return null;
		}

		TreeSet<Integer> sites = new TreeSet<Integer>();
		for (String idStirng : sitesArray) {
			int id = StringUtil.parseInt(idStirng, 0, 0);
			if (id > 0) {
				sites.add(id);
			}
		}

		if (sites.isEmpty()) {
			return null;
		}

		int[] validSitesArray = new int[sites.size()];
		int index = 0;
		for (Integer site : sites) {
			validSitesArray[index++] = site.intValue();
		}
		return validSitesArray;
	}

	private String getParameter(HttpServletRequest request,String paramName){
		if (chineseUrl != null){
			return chineseUrl.getParameter(paramName);
		}
		else{
			return request.getParameter(paramName);
		}
	}
	
	private String buildQueryUrl(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder("http://");

		try {
			builder.append(Constant.localIp);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			builder.append("null");
		}

		builder.append(request.getRequestURI());

		String queryString = request.getQueryString();
		if (queryString != null) {
			builder.append("?");
			builder.append(queryString);
		}

		return builder.toString();
	}
}
