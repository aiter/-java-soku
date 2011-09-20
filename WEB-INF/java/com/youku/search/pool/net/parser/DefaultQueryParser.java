package com.youku.search.pool.net.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youku.search.index.entity.Query;
import com.youku.search.pool.net.mina.Request;
import com.youku.search.pool.net.parser.ResponseParser.ParserException;
import com.youku.search.sort.Parameter.RequestParameterConstant;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;
import com.youku.search.util.Constant.QueryField;
import com.youku.search.util.Constant.VideoCategoryConstant;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

public class DefaultQueryParser extends AbstractQueryParser<Query> {
	
	public static final DefaultQueryParser I = new DefaultQueryParser();
	
	private DefaultQueryParser() {
	}
	
	@Override
	public String parseBody(Query query) throws Exception {

		Request request = new Request();
		request.searchService = Constant.Socket.SEARCH_SERVICE;

		// C-Server分页规则：0~4第一页，5~9第二页
		request.pageId = (query.indexPage.page_no - 1) * 5;

		fillRequestParams(query, request);
		
		request.queryLength = query.keywords
			.getBytes(Constant.Socket.MESSAGE_CHARSET_NAME).length;
		request.queryString = query.keywords;
		
		// 计算contentLength必须放最后
		request.contentLength = request.getContentLength();

		return request.parseRequest();
	}

	public void fillRequestParams(Query query, Request request) throws ParserException {
		AbstractQueryParser.VideoSearch64BOptions options = new AbstractQueryParser.VideoSearch64BOptions();

		// 按分类过滤
		int indexSingleCateId = SearchUtil.getVideoCategory(query.category);
		String frontCateIds = ((query.categories == null || query.categories.length() == 0) ? null : query.categories);
		String frontExcludeCateIds = ((query.exclude_cates == null || query.exclude_cates.length() == 0) ? null : query.exclude_cates);
		if (null != frontCateIds) {
			List<Integer> indexCateIdList = convertCategorys(frontCateIds);
			if (indexCateIdList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (Integer indexCateId : indexCateIdList) {
					sb.append(String.valueOf(indexCateId)).append(',');
				}
				request.categorys = sb.toString();
				options.filterByCategorys();
			}
		} else if (null != frontExcludeCateIds) {
			List<Integer> indexCateIdList = convertCategorys(frontExcludeCateIds);
			if (indexCateIdList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (Integer indexCateId : indexCateIdList) {
					sb.append(String.valueOf(indexCateId)).append(',');
				}
				request.categorys = sb.toString();
				options.filterByExcludeCategorys();
			}
		} else if (indexSingleCateId == VideoCategoryConstant.DEFAULT) {
			request.categorys = String.valueOf(indexSingleCateId) + ",";
		} else {
			request.categorys = String.valueOf(indexSingleCateId) + ",";
			options.filterByCategorys();
		}
		if (null == request.categorys) {
			request.categorys = Request.DEFAULT_CATEGORY;
		}
		
		// 是否高清
		if (null != query.ftype && query.ftype.length() != 0) {
			HDOptions hdBitSet = new HDOptions();
			String[] ftypes = query.ftype.split(",");
			for (String ftype : ftypes) {
				int ftypeInt = DataFormat.parseInt(ftype, 0);
				if (ftypeInt > 0 && ftypeInt < HDOptions.LENGTH) {
					hdBitSet.setBit(ftypeInt);
				}
			}
			
			if (hdBitSet.hasSet()) {
				options.filterByHD();
				request.videoFormat = hdBitSet.toString();
			} else {
				request.videoFormat = RequestParameterConstant.HD_ONLY_FALSE;
			}
		} else {
			request.videoFormat = RequestParameterConstant.HD_ONLY_FALSE;
		}
		
		// 按时长过滤
		request.videoLength = options.filterByVideoTime(query.timemore,
				query.timeless);
		
		// 排序规则
		if (null != query.orderFieldStr
				&& !query.orderFieldStr.equalsIgnoreCase("null")) {
			// 按最新发布
			if (query.orderFieldStr.equalsIgnoreCase("createtime")) {
				options.filterByCreateTime();
			}
			// 按最多播放
			else if (query.orderFieldStr.equalsIgnoreCase("total_pv")) {
				options.filterByTotalPv();
			}
			// 按评论
			else if (query.orderFieldStr.equalsIgnoreCase("total_comment")) {
				options.filterByComment();
			}
			// 按收藏
			else if (query.orderFieldStr.equalsIgnoreCase("total_fav")) {
				options.filterByFavorite();
			}
			// 传入非法值
			else {
			}
		}

		// 按标签搜索
		if (query.field == QueryField.VIDEOTAG) {
			options.filterByTag();
		}

		// 按创建时间
		if (null != query.limitDate && !query.limitDate.equalsIgnoreCase("0")) {
			String[] limitDateSplit = query.limitDate.split("-");
			if (limitDateSplit.length == 1 && DataFormat.parseInt(limitDateSplit[0], -1) > 0) {
				request.limitDate = "0," + Integer.parseInt(limitDateSplit[0]);
				options.filterByPubTime();
			} else if (limitDateSplit.length == 2 && DataFormat.parseInt(limitDateSplit[0], -1) >= 0 && DataFormat.parseInt(limitDateSplit[1], -1) > 0) {
				request.limitDate = "" + Integer.parseInt(limitDateSplit[0]) + "," + Integer.parseInt(limitDateSplit[1]);
				options.filterByPubTime();
			} else {
			}
		}
		
		// 按MD5
		if (query.field == QueryField.VIDEO_MD5) {
			query.keywords = StringUtil.convertMD5ToFinger(query.keywords);
			options.filterByMD5();
		}
		
		// 按合作伙伴ID
		if (query.partner > 0) {
			request.partnerId = query.partner;
			options.filterByPartnerId();
		}
		
		request.searchDescription = options.toString();
	}

	/**
	 * 将前端的categoryId数组转为后端的categoryId数组
	 * @param frontCategorys
	 * @return
	 */
	public List<Integer> convertCategorys(String frontCategorys) {
		List<Integer> cateList = new ArrayList<Integer>();
		if (null == frontCategorys || frontCategorys.length() == 0) {
			return cateList;
		}
		
		String[] categoryArray = frontCategorys.split(",");
		for (String cate : categoryArray) {
			cate = cate.trim();
			if (cate.length() == 0) {
				continue;
			}
			
			int cateInt = SearchUtil.getVideoCategory(DataFormat.parseInt(cate, 0));
			if (cateInt != 0) {
				cateList.add(cateInt);
			}
		}
		
		return cateList;
	}

}
