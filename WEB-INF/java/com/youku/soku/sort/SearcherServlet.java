package com.youku.soku.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.log.RemoteLogger;
import com.youku.search.sort.search.LogInfo;
import com.youku.search.sort.search.LogInfo.Item;
import com.youku.search.util.Constant;
import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.Filter;
import com.youku.soku.web.SearchResult;
import com.youku.soku.web.controller.Receiver.Forward;
import com.youku.soku.web.util.WebUtil;

/**
 * 搜索处理入口点
 */
public class SearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static final String RESPONSE_OK = "ok";
	static final String RESPONSE_ERROR = "error";

	Log logger = LogFactory.getLog(getClass());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");

			String responseString = "";
			Parameter parameter = new Parameter(request);
			if(parameter._only_log){//合并/8/4/1/6/log接口
				handleLog(parameter,response);
				return;
			}
			//本接口，不纠错和相关搜索
			parameter.relWords = false;
			parameter.corWords = false;
			//过滤网站默认有酷6 id=10
			if(!ArrayUtils.contains(parameter.exclude_sites, 10)){
				parameter.exclude_sites = ArrayUtils.add(parameter.exclude_sites, 10);
			}
			
			
			//处理屏蔽词。2010.12.17
			boolean success  = handleShield(parameter);
			
			if(success){
				try {
					JSONObject result = Searcher.search(parameter);
					if (result != null) {
						if (parameter.h) {
							responseString = result.toString(4);
						} else {
							responseString = result.toString();
						}
					}
				} catch (Exception e) {
					logger.error("查询发生异常！query： " + parameter, e);
				}
			}else {
				//TODO 如果需要，可以返回提示屏蔽词信息
			}

			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 记录日志，以前的/8/4/1/6/log日志接口
	 * @param response 
	 * @param p 
	 * @throws IOException 
	 */
	private void handleLog(Parameter p, HttpServletResponse response) throws IOException {
		try {
			if (p.keyword.length() > 0) {
				LogInfo logInfo = buildLogInfo(p);
				RemoteLogger.log(RemoteLogger.sokuQuery, logInfo);

				response.getWriter().print(RESPONSE_OK);
				response.getWriter().flush();
				return;
			}
		} catch (Exception e) {
			logger.error("记录请求日志发生异常！query： " + p, e);
		}

		response.getWriter().flush();
		response.getWriter().print(RESPONSE_ERROR);
	}
	
	LogInfo buildLogInfo(Parameter p) {

		LogInfo info = new LogInfo();

		info.set(Item.query, p.keyword);
		info.set(Item.source, p._source);
		info.set(Item.type, "video");

		if (p.logic == Constant.Operator.AND) {
			info.set(Item.logic, "and");
		} else {
			info.set(Item.logic, "or");
		}

		info.set(Item.order_field, null);
		info.set(Item.order_reverse, p.reverse);
		info.set(Item.page, p.page);
		info.set(Item.cache, true);
		info.set(Item.total_result, 1);
		info.set(Item.page_result, 1);
		info.set(Item.cost, 0);
		info.set(Item.miss, false);
		info.set(Item.cacheKey, "unknow_since_is_log_request");
		info.set(Item.url, p.queryUrl);
		info.set(Item.because, p._because);
		info.set(Item.others, "this_is_a_log_request");

		return info;
	}

	/**
	 * @param parameter
	 */
	private boolean handleShield(Parameter parameter) {
		String keyword = parameter.keyword;
		if (parameter.keyword == null || parameter.keyword.length() == 0) {
			return false;
		}
		
		ShieldInfo shieldInfo = com.youku.soku.shield.Filter.getInstance().isShieldWord(keyword,Filter.Source.soku);
		boolean isShield = false;
		if(shieldInfo!=null){
			isShield = shieldInfo.isMatched();
			logger.info("Filter[屏蔽词]:"+keyword);
		}
		if(isShield){
			 //站内的在这不处理，只处理站点的白名单和黑名单
			List<Integer> whiteSiteList = shieldInfo.getWhiteSiteList();
			List<Integer> blackSiteList = shieldInfo.getBlackSiteList();
			
			logger.info("Filter[屏蔽词]:白名单："+(whiteSiteList==null?null:whiteSiteList.size()));
			logger.info("Filter[屏蔽词]:黑名单："+(blackSiteList==null?null:blackSiteList.size()));
			//两个同时为空，屏蔽所有
			if((whiteSiteList==null || whiteSiteList.size()==0) && (blackSiteList==null || blackSiteList.size()==0)){
				return false;
			}
		}
		
		if(isShield){/* 屏蔽词处理 */
			List<Integer> whiteSiteList = shieldInfo.getWhiteSiteList();
			List<Integer> blackSiteList = shieldInfo.getBlackSiteList();
			if(whiteSiteList!=null && whiteSiteList.size()>0){   //该屏蔽词的白名单站点id 不为空，只展示这些站点的内容
				parameter.include_sites = ArrayUtils.toPrimitive(whiteSiteList.toArray(new Integer[whiteSiteList.size()]));
//				param.include_sites = new int[]{1,2};
				logger.info("Filter[屏蔽词]:白名单ID："+Arrays.toString(parameter.include_sites));
				
				//如果有白名单，也有site参数，但是site不在白名单中
				if(parameter.site>0 && !whiteSiteList.contains(parameter.site)){
					return false;
				}
			}

			if(blackSiteList==null){
				blackSiteList = new ArrayList<Integer>();
			}
			int ku6 = 10;    //特殊添加 酷6=10
			if(!blackSiteList.contains(ku6)){
				blackSiteList.add(ku6);
			}
			if(parameter.exclude_sites!=null && parameter.exclude_sites.length>0){
				for(int exSite:parameter.exclude_sites){
					blackSiteList.add(exSite);
				}
			}
			if(blackSiteList!=null && blackSiteList.size()>0){	  //该屏蔽词的黑名单站点id  不为空，去除这些站点的内容
				if(whiteSiteList!=null && whiteSiteList.size()>0){//从黑名单中移除白名单的站点id
					blackSiteList.removeAll(whiteSiteList);
				}
				parameter.exclude_sites = ArrayUtils.toPrimitive(blackSiteList.toArray(new Integer[blackSiteList.size()]));
				logger.info("Filter[屏蔽词]:黑名单ID："+Arrays.toString(parameter.exclude_sites));
			}
		}
		
		
		return true;
	}
}
