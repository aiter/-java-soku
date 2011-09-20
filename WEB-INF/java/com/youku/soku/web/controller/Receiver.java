/**
 * 
 */
package com.youku.soku.web.controller;

import com.youku.soku.web.SearchResult;
import com.youku.soku.web.SearchResult.Content;


/**
 * @author 1verge
 * 控制根据返回结果跳转去向
 */
public class Receiver {
	
	
	public static Forward getForward(SearchResult result)
	{
		if (result == null || result.getContent() == null)
			return Forward.ZERO_RESULT;
		
		Content content = result.getContent();
		
		if (content.getVideos() != null)
		{
			if (content.getVideos().length()>0)
				return Forward.NORMAL;
			else 
				return Forward.ZERO_RESULT;
		}
		else if (content.getTeleplay() != null)
			return Forward.TELEPLAY;
		else if (content.getCartoon() != null)
			return Forward.CARTOON;
		else if (content.getVariety() != null)
			return Forward.VARIETY;
		else if (content.getPerson() != null)
			return Forward.PERSON;
		else if (content.getMovie() != null)
			return Forward.MOVIE;
		
		return Forward.ZERO_RESULT;
	}
	
	public static class Forward{
		
		private String forward;
		public Forward(String s){
			this.forward = s;
		}
		public String toString(){
			return forward;
		}
		public String getForward(){
			return forward;
		}
		
		
//		public static final Forward NORMAL = new Forward("/result/normal.jsp");	//正常搜索结果
		public static final Forward NORMAL = new Forward("/result/so.jsp");	//测试
		public static final Forward ZERO_RESULT = new Forward("/result/zero_result.jsp");	//正常搜索结果
		
		public static final Forward PERSON = new Forward("/result/person.jsp");	//人物页
		public static final Forward TELEPLAY = new Forward("/result/teleplay.jsp");		//电视剧页
		public static final Forward VARIETY = new Forward("/result/variety.jsp");	//综艺节目
		public static final Forward CARTOON = new Forward("/result/cartoon.jsp");	//动漫
		public static final Forward MOVIE = new Forward("/result/movie.jsp");	//电影
		public static final Forward ERROR = new Forward("/result/error.jsp");	//错误页面
	}
	
}
