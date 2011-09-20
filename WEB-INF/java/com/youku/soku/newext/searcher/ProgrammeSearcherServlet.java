package com.youku.soku.newext.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.util.ChannelType;


/**
 * 详情页programme接口
 */
public class ProgrammeSearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(ProgrammeSearcherServlet.class);

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
			
			Programme programme=null;
			 programme = info.id_programme.get(Integer.valueOf(pid));
			 
			
			JSONObject resultJson = null;
			if(programme!=null){
				
				MovieInfo minfo=null;
				TeleplayInfo tinfo=null;
				AnimeInfo ainfo=null;
				VarietyInfo vinfo=null;
				if(programme.getCate()==ChannelType.MOVIE.getValue()){/**电影*/
					minfo=ExtInfoHolder.getCurrentThreadLocal().movieInfo;
					resultJson=MovieSearcher.genJson(programme,minfo,null);
					resultJson.put("channel", ChannelType.MOVIE.name());
				}else if(programme.getCate()==ChannelType.TELEPLAY.getValue()){/**电视剧*/
					tinfo=ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
					resultJson=TeleplaySearcher.genJson(programme,tinfo,null);
					resultJson.put("channel", ChannelType.TELEPLAY.name());
				}else if(programme.getCate()==ChannelType.VARIETY.getValue()){/**综艺*/
					vinfo=ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
					resultJson=VarietySearcher.genJson(programme,vinfo,null);
					resultJson.put("channel", ChannelType.VARIETY.name());
				}else if(programme.getCate()==ChannelType.ANIME.getValue()){/**动漫*/
					ainfo=ExtInfoHolder.getCurrentThreadLocal().animeInfo;
					resultJson=AnimeSearcher.genJson(programme,ainfo,null);
					resultJson.put("channel", ChannelType.ANIME.name());
				}
				
			}else{
				logger.warn("没有对应的programme 对象： pid:"+pid);
				responseString="{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
				
			}
			
			
			if(resultJson==null){
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

		} catch (Exception e) {
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
