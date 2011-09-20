package com.youku.soku.newext.searcher;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.util.ChannelType;


/**
 * 对外提供中间层信息接口
 */
public class MiddleResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Log logger = LogFactory.getLog(getClass());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) {
		
		String responseString = "";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			
			String pid_str=request.getParameter("pid");
			String h=request.getParameter("h");
			if(pid_str==null || StringUtils.trimToEmpty(pid_str).length()<=0){
				logger.warn("请输入pid参数");
				responseString="{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
			}
			
			int pid=new Integer(pid_str).intValue();
			
			AliasInfo info = ExtInfoHolder.getCurrentThreadLocal().aliasInfo;
			
			if(info==null){
				logger.warn("直达区内部错误");
				responseString="{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
				
			}
			
			Programme programme=null;
			 programme = info.id_programme.get(Integer.valueOf(pid));
			 
			JSONObject resultJson = new JSONObject();
			if(programme!=null){
				String middStr=info.middMap.get(programme.getContentId());
				if(middStr!=null && middStr.length()>0){
					JSONObject middJson=new JSONObject(middStr);
					resultJson.put("pid", programme.getId());
					resultJson.put("name", programme.getName());
					
					resultJson.put("releaseyear", middJson.optInt("releaseyear"));
					resultJson.put("url", programme.getPlayUrl());
					
					if(programme.getCate()==ChannelType.MOVIE.getValue()){
						resultJson.put("type", middJson.optJSONArray("movie_genre"));
					}else if(programme.getCate()==ChannelType.TELEPLAY.getValue()){
						resultJson.put("type", middJson.optJSONArray("tv_genre"));
					}else if(programme.getCate()==ChannelType.ANIME.getValue()){
						resultJson.put("type", middJson.optJSONArray("anime_genre"));
					}else if(programme.getCate()==ChannelType.VARIETY.getValue()){
						resultJson.put("type", middJson.optJSONArray("variety_genre"));
					}
					
					resultJson.put("area", middJson.optJSONArray("area"));
				}
				
			}else{
				logger.warn("没有对应的programme 对象： pid:"+pid);
				responseString="{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
				
			}
			
			
			if(h!=null){
				responseString=resultJson.toString(4);
			}else{
				responseString = resultJson.toString();
			}
			
					
			
			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (IOException e ) {
			logger.error("查询出错："+e.getMessage());
			e.printStackTrace();
			responseString="{}";
			try {
				response.getWriter().print(responseString);
				response.getWriter().flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}catch (JSONException e){
			logger.error("查询出错："+e.getMessage());
			e.printStackTrace();
			responseString="{}";
			try {
				response.getWriter().print(responseString);
				response.getWriter().flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	
}
