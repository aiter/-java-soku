/**
 * 
 */
package com.youku.soku.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;

import com.danga.MemCached.Logger;
import com.youku.common.http.ResinChineseUrl;
import com.youku.search.pool.net.util.Cost;
import com.youku.search.sort.KeywordFilter;
import com.youku.soku.library.load.timer.ForwardWord;
import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.Filter;
import com.youku.soku.sort.Parameter;
import com.youku.soku.sort.SearcherSoku;
import com.youku.soku.util.MyUtil;
import com.youku.soku.util.Constant.QuerySort;
import com.youku.soku.web.controller.Receiver;
import com.youku.soku.web.controller.Receiver.Forward;
import com.youku.soku.web.util.WebUtil;


/**
 * @author 1verge
 *
 */
public class SearchService {
	static Logger logger = Logger.getLogger(SearchService.class.getName());
	
	public static SearchResult search(HttpServletRequest request)
	{
		long start = System.currentTimeMillis();
		Cost cost = new Cost(start);
		
		SearchResult result = null;
		//检查关键字
		String keyword = request.getParameter("keyword");
		keyword = KeywordFilter.filter(keyword, 100);
		
		if (keyword == null || keyword.trim().isEmpty()){
			
			//重新按其他格式读取字符串
			ResinChineseUrl _request = new ResinChineseUrl(request);
			
			keyword = _request.getParameter("keyword");
			
			if (keyword == null || keyword.trim().isEmpty()){
				result = new SearchResult(Forward.ZERO_RESULT);
				result.setKeyword(" ");
				return result;	
			}
		}
		
//		if (true){
//			result = new SearchResult(Forward.ZERO_RESULT);
//			result.setKeyword(" ");
//			return result;
//		}
		//检查过滤器
//		if (Filter.getInstance().is_bad_words_regex(keyword)){
//			result = new SearchResult(Forward.ERROR);
//			result.setKeyword( WebUtil.formatHtml(keyword));
//			return result;
//		}
		
		ShieldInfo shieldInfo = com.youku.soku.shield.Filter.getInstance().isShieldWord(keyword,Filter.Source.soku);
		boolean isShield = false;
		if(shieldInfo!=null){
			isShield = shieldInfo.isMatched();
			logger.debug("Filter[屏蔽词]:"+keyword);
		}
		/**
		 * 	   private boolean matched;        //是否屏蔽  false时不需要处理，true需要处理下面3个list
	     *     private List<Integer> shieldChannel; //所屏蔽的站内频道id  为空的话，屏蔽所有频道
	     *     private List<Integer> whiteSiteList; //该屏蔽词的白名单站点id 不为空，只展示这些站点的内容 
	     *     private List<Integer> blackSiteList; //该屏蔽词的黑名单站点id  不为空，去除这些站点的内容  
	     *     两个同时为空，屏蔽所有
		 */
		if(isShield){
			 //站内的在这不处理，只处理站点的白名单和黑名单
			List<Integer> whiteSiteList = shieldInfo.getWhiteSiteList();
			List<Integer> blackSiteList = shieldInfo.getBlackSiteList();
			
			logger.debug("Filter[屏蔽词]:白名单："+(whiteSiteList==null?null:whiteSiteList.size()));
			logger.debug("Filter[屏蔽词]:黑名单："+(blackSiteList==null?null:blackSiteList.size()));
			//两个同时为空，屏蔽所有
			if((whiteSiteList==null || whiteSiteList.size()==0) && (blackSiteList==null || blackSiteList.size()==0)){
				result = new SearchResult(Forward.ERROR);
				result.setKeyword( WebUtil.formatHtml(keyword));
				return result;
			}
		}
		cost.updateEnd();
		logger.debug("检查屏蔽词耗时:"+cost.getCost());
		
		
		JSONObject json= null;
		
		
			Parameter param = new Parameter(request);
			param.exclude_sites = new int[]{10};
			
			cost.updateStart();
			
			if(isShield){/* 屏蔽词处理 */
				List<Integer> whiteSiteList = shieldInfo.getWhiteSiteList();
				List<Integer> blackSiteList = shieldInfo.getBlackSiteList();
				if(whiteSiteList!=null && whiteSiteList.size()>0){   //该屏蔽词的白名单站点id 不为空，只展示这些站点的内容
					param.include_sites = ArrayUtils.toPrimitive(whiteSiteList.toArray(new Integer[whiteSiteList.size()]));
					logger.debug("Filter[屏蔽词]:白名单ID："+Arrays.toString(param.include_sites));
					
					//如果有白名单，也有site参数，但是site不在白名单中
					if(param.site>0 && !whiteSiteList.contains(param.site)){
						result = new SearchResult(Forward.ERROR);
						result.setKeyword( WebUtil.formatHtml(keyword));
						return result;
					}
				}

				if(blackSiteList==null){
					blackSiteList = new ArrayList<Integer>();
				}
				int ku6 = 10;    //特殊添加 酷6=10
				if(!blackSiteList.contains(ku6)){
					blackSiteList.add(ku6);
				}
				if(blackSiteList!=null && blackSiteList.size()>0){	  //该屏蔽词的黑名单站点id  不为空，去除这些站点的内容
					if(whiteSiteList!=null && whiteSiteList.size()>0){//从黑名单中移除白名单的站点id
						blackSiteList.removeAll(whiteSiteList);
					}
					param.exclude_sites = ArrayUtils.toPrimitive(blackSiteList.toArray(new Integer[blackSiteList.size()]));
					logger.debug("Filter[屏蔽词]:黑名单ID："+Arrays.toString(param.exclude_sites));
				}
			}
			
			cost.updateEnd();
			logger.debug("第二次检查屏蔽词耗时:"+cost.getCost());
			cost.updateStart();
			
//			String ext = request.getParameter("ext");
//			
//			String time_length=request.getParameter("time_length");
//			String limit_date=request.getParameter("limit_date");
//			String site =request.getParameter("site");
//			String hd=request.getParameter("hd");
			int time_length=param.time_length;
			int limit_date=param.limit_date;
			int site=param.site;
			int hd=param.hd;
			
			if((time_length!=0) || (limit_date!=0) || (hd!=0)){
				param.ext=false;
			}
			
			//TODO 添加直接跳转到详情页的逻辑
			if(param.redirect && param.page<=1 && time_length==0 &&
					limit_date==0 && site==0 && hd==0){
				String redirectUrl = ForwardWord.getInstance().getProgrammeId(keyword);
				if(redirectUrl!=null && redirectUrl.trim().length()>0){
					result = new SearchResult(new Forward("redirect"+redirectUrl.trim()+"?keyword="+MyUtil.urlEncode(keyword)));
					return result;	
				}
			}
			
			cost.updateEnd();
			logger.debug("直接跳转到详情页的逻辑耗时:"+cost.getCost());
			cost.updateStart();
			
			
//			param.exclude_sites = new int[]{4,5,7,8,10,11,12,16,18,20,21,22,23,24,25};
			
			//先写死，下次升级直接从索引端去掉
			if (param.include_sites!= null ){
				List<Integer> list = Arrays.asList(new Integer[]{1,19,9,6,3,17,2,14,15});
				list.retainAll(Arrays.asList(param.include_sites));
				param.include_sites = ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()]));
			}
			else{
				param.include_sites = new int[]{1,19,9,6,3,17,2,14,15};
			}

			try {
				
				json = SearcherSoku.search(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cost.updateEnd();
			logger.debug("从索引查询排序耗时:"+cost.getCost());
			cost.updateStart();
			
			result =  new SearchResult(json);
			
			cost.updateEnd();
			logger.debug("格式化JSON对象耗时:"+cost.getCost());
			cost.updateStart();
			
			//如果没结果，再次用或查询
			if (result.getContent().getVideos() == null || result.getContent().getVideos().length() == 0){
				param.logic = 2;
				param.sort = QuerySort.SORT_SCORE;
				try {
					json = SearcherSoku.search(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				result =  new SearchResult(json);
				
				cost.updateEnd();
				logger.debug("无结果二次查询耗时:"+cost.getCost());
				cost.updateStart();
			}
			
			result.setParam(param);
		
		
		result.setForward(Receiver.getForward(result));
		result.setKeyword( WebUtil.formatHtml(keyword));
		
		logger.debug(result.getForward().toString());
		
		long time = System.currentTimeMillis() - start;
		result.setCost(time + "");
		
		logger.debug("total cost:"+time);
		return result;
	}

}
