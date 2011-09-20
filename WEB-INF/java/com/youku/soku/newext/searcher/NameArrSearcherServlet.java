package com.youku.soku.newext.searcher;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.soku.newext.util.StringUtil;


/**
 * 模糊搜索入口
 */
public class NameArrSearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(NameArrSearcherServlet.class);

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
			
			String names=request.getParameter("names");
			String site=request.getParameter("site");
			int size=StringUtil.parseInt(request.getParameter("size"), 0);
			String h=request.getParameter("h");
			if(names==null || StringUtils.trimToEmpty(names).length()<=0){
				logger.warn("请输入names参数");
				responseString="{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
			}
			
			String[] nameArr=names.split(",");
			JSONObject resultJson = Searcher.searchNameArray(nameArr,site,size);
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
			logger.error("nameArrSearch查询出错："+e.getMessage());
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
