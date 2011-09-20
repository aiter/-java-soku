package com.youku.search.sort.servlet.search_page;

import static java.lang.String.valueOf;

import java.util.LinkedHashMap;
import java.util.Map;

import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.constant.SearchConstant;
import com.youku.search.sort.servlet.util.WebUtils;
import com.youku.search.util.StringUtil;

public class WebParamHelper {
	public enum ParameterYouku {
		//普通查询、高级查询共同使用的参数
		q,orderby,lengthtype,hd,cateid,source,type,page
		
		//高级查询使用的参数
		,pagesize,fields,limitdate,categories,pv,comments
		
		//bar
		,sbt
	}

	public static WebParam parse(Map<String, String> web) {

		WebParam param = new WebParam();

		String q = web.get("q");
		//q = KeywordFilter.filter(q);
		q = (q==null)?"":q.trim();
		param.setQ(q);

		int orderby = StringUtil.parseInt(web.get("orderby"), 1);
		param.setOrderby(orderby);

		int lengthtype = StringUtil.parseInt(web.get("lengthtype"), 0);
		param.setLengthtype(lengthtype);

		int hd = StringUtil.parseInt(web.get("hd"), 0, 0, 6);
		param.setHd(hd);

		int cateid = StringUtil.parseInt(web.get("cateid"), 0, 0);
		param.setCateid(cateid);

		String source = web.get("source");
		param.setSource(source);

		String type = web.get("type");
		param.setType(type);

		int page = StringUtil.parseInt(web.get("page"), 1, 1, 100);
		param.setPage(page);

		// 高级搜索
		int pagesize = StringUtil.parseInt(web.get("pagesize"), 20, 0, 100);
		param.setPagesize(pagesize);

		String fields = web.get("fields");
		if (fields != null) {
			param.setFields(fields);
			param.setAdvance(true);
		}

		String limitdate = web.get("limitdate");
		if (limitdate != null) {
			param.setLimitdate(limitdate);
			param.setAdvance(true);
		}

		String categories = web.get("categories");
		if (categories != null) {
			param.setCategories(categories);
			param.setAdvance(true);
		}

		int pv = StringUtil.parseInt(web.get("pv"), 0, 0);
		if (web.containsKey("pv")) {
			param.setPv(pv);
			param.setAdvance(true);
		}

		int comments = StringUtil.parseInt(web.get("comments"), 0, 0);
		if (web.containsKey("comments")) {
			param.setComments(comments);
			param.setAdvance(true);
		}

		// bar
		String sbt = web.get("sbt");
		if (WebParam.TYPE_POST.equals(sbt) || WebParam.TYPE_USER.equals(sbt)) {
			param.setSbt(sbt);
		} else {
			param.setSbt(WebParam.TYPE_BAR);
		}

		return param;
	}

	public static void merge(WebParam target, Map<String, String> map) {

		if (target == null || map == null || map.isEmpty()) {
			return;
		}

		WebParam param = parse(map);

		String q = param.getQ();
		if (map.containsKey("q") && q != null) {
			target.setQ(q);
		}

		int orderby = param.getOrderby();
		if (map.containsKey("orderby")) {
			target.setOrderby(orderby);
		}

		int lengthtype = param.getLengthtype();
		if (map.containsKey("lengthtype")) {
			target.setLengthtype(lengthtype);
		}

		int hd = param.getHd();
		if (map.containsKey("hd")) {
			target.setHd(hd);
		}

		int cateid = param.getCateid();
		if (map.containsKey("cateid")) {
			target.setCateid(cateid);
		}

		String source = param.getSource();
		if (map.containsKey("source")) {
			target.setSource(source);
		}

		String type = param.getType();
		if (map.containsKey("type")) {
			target.setType(type);
		}

		int page = param.getPage();
		if (map.containsKey("page")) {
			target.setPage(page);
		}

		// 高级搜索
		int pagesize = param.getPagesize();
		if (map.containsKey("pagesize")) {
			target.setPagesize(pagesize);
		}

		String fields = param.getFields();
		if (map.containsKey("fields") && fields != null) {
			target.setFields(fields);
			target.setAdvance(true);
		}

		String limitdate = param.getLimitdate();
		if (map.containsKey("limitdate") && limitdate != null) {
			target.setLimitdate(limitdate);
//			target.setAdvance(true);
		}

		String categories = param.getCategories();
		if (map.containsKey("categories") && categories != null) {
			target.setCategories(categories);
//			target.setAdvance(true);
		}

		int pv = param.getPv();
		if (map.containsKey("pv")) {
			target.setPv(pv);
			target.setAdvance(true);
		}

		int comments = param.getComments();
		if (map.containsKey("comments")) {
			target.setComments(comments);
			target.setAdvance(true);
		}

		// bar
		String sbt = param.getSbt();
		if (map.containsKey("sbt")) {
			target.setSbt(sbt);
		}
	}

	public static String encode(WebParam param, String params) {

		if (param == null) {
			return "";
		}

		param = param.clone();

		Map<String, String> map = QueryStringParser.split(params);
		merge(param, map);

		StringBuilder builder = new StringBuilder();

		String q = param.getQ();
		builder.append("q_");
		builder.append(WebUtils.urlEncode(q));

		int orderby = param.getOrderby();
		if (orderby > 0) {
			builder.append("_orderby_");
			builder.append(orderby);
		}

		int lengthtype = param.getLengthtype();
		if (lengthtype > 0) {
			builder.append("_lengthtype_");
			builder.append(lengthtype);
		}

		int hd = param.getHd();
		if (hd > 0) {
			builder.append("_hd_");
			builder.append(hd);
		}

		int cateid = param.getCateid();
		if (cateid > 0) {
			builder.append("_cateid_");
			builder.append(cateid);
		}

		String source = param.getSource();
		if (source != null) {
			builder.append("_source_");
			builder.append(WebUtils.urlEncode(source));
		}

		String type = param.getType();
		if (type != null) {
			builder.append("_type_");
			builder.append(WebUtils.urlEncode(type));
		}

		int page = param.getPage();
		if (page > 1) {
			builder.append("_page_");
			builder.append(page);
		}

		// 高级搜索
		int pagesize = param.getPagesize();
		if (pagesize > 0 && param.isAdvance()) {
			builder.append("_pagesize_");
			builder.append(pagesize);
		}

		String fields = param.getFields();
		if (fields != null && param.isAdvance()) {
			builder.append("_fields_");
			builder.append(WebUtils.urlEncode(fields));
		}

		String limitdate = param.getLimitdate();
		if (limitdate != null && param.isAdvance()) {
			builder.append("_limitdate_");
			builder.append(WebUtils.urlEncode(limitdate));
		}

		String categories = param.getCategories();
		if (categories != null && param.isAdvance()) {
			builder.append("_categories_");
			builder.append(WebUtils.urlEncode(categories));
		}

		int pv = param.getPv();
		if (pv > 0 && param.isAdvance()) {
			builder.append("_pv_");
			builder.append(pv);
		}

		int comments = param.getComments();
		if (comments > 0 && param.isAdvance()) {
			builder.append("_comments_");
			builder.append(comments);
		}

		// bar
		String sbt = param.getSbt();
		if (sbt != null && !WebParam.TYPE_BAR.equals(sbt)) {
			builder.append("_sbt_");
			builder.append(sbt);
		}

		return builder.toString();
	}

	public static Map<ParameterNames, String> convertVideo(WebParam web) {

		Map<ParameterNames, String> map = new LinkedHashMap<ParameterNames, String>();

		map.put(ParameterNames._source, "youku");

		map.put(ParameterNames.keyword, web.getQ());

		if ("tag".equals(web.getType())) {
			map.put(ParameterNames.type, valueOf(SearchConstant.VIDEOTAG));
		} else {
			map.put(ParameterNames.type, valueOf(SearchConstant.VIDEO));
		}

		map.put(ParameterNames.cateid, String.valueOf(web.getCateid()));
		map.put(ParameterNames.pagesize, String.valueOf(web.getPagesize()));
		map.put(ParameterNames.curpage, String.valueOf(web.getPage()));

		int orderby = web.getOrderby();
		switch (orderby) {
		case 2:
			map.put(ParameterNames.orderfield, "createtime");
			break;

		case 3:
			map.put(ParameterNames.orderfield, "total_pv");
			break;

		case 4:
			map.put(ParameterNames.orderfield, "total_comment");
			break;

		case 5:
			map.put(ParameterNames.orderfield, "total_fav");
			break;

		case 1:
		default:
			map.put(ParameterNames.orderfield, "null");
			break;
		}

		int lengthtype = web.getLengthtype();
		switch (lengthtype) {
		case 1:
			map.put(ParameterNames.timeless, "10");
			map.put(ParameterNames.timemore, "0");
			break;

		case 2:
			map.put(ParameterNames.timeless, "30");
			map.put(ParameterNames.timemore, "10");
			break;

		case 3:
			map.put(ParameterNames.timeless, "60");
			map.put(ParameterNames.timemore, "30");
			break;

		case 4:
			map.put(ParameterNames.timemore, "60");
			break;

		default:
			break;
		}

		if (web.getHd() == 1) {
			map.put(ParameterNames.ftype, "1");
		}
		if (web.getHd() == 6) {
			map.put(ParameterNames.ftype, "6");
		}

		// if(!empty($this->excludeCateIds)){//不在指字分类下搜索
		// $options['exclude_cates']=$this->excludeCateIds;
		// }

		map.put(ParameterNames.relnum, "12");
		map.put(ParameterNames.order, "1");

		// 高级搜索选项
		if (web.isAdvance()) {
//			map.put(ParameterNames.advance, "1");
		}

		String fields = web.getFields();
		if (fields != null) {
			fields = fields.replace("tagsindex", "tags_index");
		}
		map.put(ParameterNames.fields, fields);

		map.put(ParameterNames.limit_date, web.getLimitdate());
		map.put(ParameterNames.categories, web.getCategories());
		map.put(ParameterNames.hd, String.valueOf(web.getHd()));
		map.put(ParameterNames.pv, String.valueOf(web.getPv()));
		map.put(ParameterNames.comments, String.valueOf(web.getComments()));
		// 高级搜索选项结束

		// if(!empty($cateid))$options['categories']=$cateid;

		// 高亮选项
		map.put(ParameterNames.hl, "true");
		// $options['hl_prefix']='<span class="highlight">';
		// $options['hl_suffix']='</span>';

		return map;
	}

	public static Map<ParameterNames, String> convertFolder(WebParam web) {

		Map<ParameterNames, String> map = new LinkedHashMap<ParameterNames, String>();

		map.put(ParameterNames._source, "youku");

		map.put(ParameterNames.keyword, web.getQ());

		if ("tag".equals(web.getType())) {
			map.put(ParameterNames.type, valueOf(SearchConstant.FOLDERTAG));
		} else {
			map.put(ParameterNames.type, valueOf(SearchConstant.FOLDER));
		}

		map.put(ParameterNames.cateid, String.valueOf(web.getCateid()));
		map.put(ParameterNames.pagesize, String.valueOf(web.getPagesize()));
		map.put(ParameterNames.curpage, String.valueOf(web.getPage()));

		int orderby = web.getOrderby();
		switch (orderby) {
		case 2:
			map.put(ParameterNames.orderfield, "total_pv");
			break;

		case 3:
			map.put(ParameterNames.orderfield, "update_time");
			break;

		case 4:
			map.put(ParameterNames.orderfield, "total_comment");
			break;

		case 1:
		default:
			map.put(ParameterNames.orderfield, "null");
			break;
		}

		map.put(ParameterNames.relnum, "12");
		map.put(ParameterNames.order, "1");

		// 高级搜索选项
		if (web.isAdvance()) {
			map.put(ParameterNames.advance, "1");
		}

		String fields = web.getFields();
		if (fields != null) {
			fields = fields.replace("tagsindex", "tags_index");
			fields = fields.replace("foldername", "folder_name");
		}
		map.put(ParameterNames.fields, fields);

		map.put(ParameterNames.limit_date, web.getLimitdate());
		map.put(ParameterNames.categories, web.getCategories());
		map.put(ParameterNames.pv, String.valueOf(web.getPv()));
		map.put(ParameterNames.comments, String.valueOf(web.getComments()));
		// 高级搜索选项结束

		// 高亮选项
		// map.put(ParameterNames.hl, "true");
		// $options['hl_prefix']='<span class="highlight">';
		// $options['hl_suffix']='</span>';

		return map;
	}

	public static Map<ParameterNames, String> convertUser(WebParam web) {

		//
		web.setPagesize(12);

		//			
		Map<ParameterNames, String> map = new LinkedHashMap<ParameterNames, String>();

		map.put(ParameterNames._source, "youku");

		map.put(ParameterNames.keyword, web.getQ());
		map.put(ParameterNames.type, valueOf(SearchConstant.MEMBER));

		map.put(ParameterNames.pagesize, String.valueOf(web.getPagesize()));
		map.put(ParameterNames.curpage, String.valueOf(web.getPage()));

		int orderby = web.getOrderby();
		switch (orderby) {
		case 2:
			map.put(ParameterNames.orderfield, "video_count");
			break;

		case 3:
			map.put(ParameterNames.orderfield, "fav_count");
			break;

		case 4:
			map.put(ParameterNames.orderfield, "reg_date");
			break;

		case 5:
			map.put(ParameterNames.orderfield, "last_content_date");
			break;

		case 6:
			map.put(ParameterNames.orderfield, "user_score");
			break;

		case 1:
		default:
			map.put(ParameterNames.orderfield, "null");
			break;
		}

		map.put(ParameterNames.relnum, "0");
		map.put(ParameterNames.order, "1");

		// 高亮选项
		map.put(ParameterNames.hl, "true");
		// $options['hl_prefix']='<span class="highlight">';
		// $options['hl_suffix']='</span>';

		return map;
	}

	public static Map<ParameterNames, String> convertBar(WebParam web) {

		//
		web.setPagesize(10);

		//			
		Map<ParameterNames, String> map = new LinkedHashMap<ParameterNames, String>();

		map.put(ParameterNames._source, "youku");

		map.put(ParameterNames.keyword, web.getQ());

		if (WebParam.TYPE_POST.equals(web.getSbt())) {
			String searchType = valueOf(SearchConstant.BARPOST_SUBJECT);
			map.put(ParameterNames.type, searchType);
		} else if (WebParam.TYPE_USER.equals(web.getSbt())) {
			String searchType = valueOf(SearchConstant.BARPOST_AUTHOR);
			map.put(ParameterNames.type, searchType);
		} else {
			String searchType = valueOf(SearchConstant.BAR);
			map.put(ParameterNames.type, searchType);
		}

		map.put(ParameterNames.pagesize, String.valueOf(web.getPagesize()));
		map.put(ParameterNames.curpage, String.valueOf(web.getPage()));

		int orderby = web.getOrderby();
		switch (orderby) {
		case 2:
			map.put(ParameterNames.orderfield, "post_time");
			map.put(ParameterNames.order, "0");
			break;

		case 1:
		default:
			map.put(ParameterNames.orderfield, "post_time");
			map.put(ParameterNames.order, "1");
			break;
		}

		map.put(ParameterNames.relnum, "0");

		// 高亮选项
		map.put(ParameterNames.hl, "false");
		// $options['hl_prefix']='<span class="highlight">';
		// $options['hl_suffix']='</span>';

		return map;
	}

	public static void main(String[] args) {

		String query = "q_mm_orderby_1";
		LinkedHashMap<String, String> web = QueryStringParser.parse(query);
		Map<ParameterNames, String> map = convertVideo(parse(web));
		System.out.println(map);

		query = "q__orderby_1_lengthtype_1_hd_1_cateid_92_page_3";
		web = QueryStringParser.parse(query);
		map = convertVideo(parse(web));
		System.out.println(map);

		query = "q_%E6%AF%94%E8%B5%9B%E7%8E%B0%E5%9C%BA_type_tag_orderby_1_lengthtype_2_cateid_92";
		web = QueryStringParser.parse(query);
		map = convertVideo(parse(web));
		System.out.println(map);
	}
}
