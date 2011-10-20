package com.youku.soku.newext.searcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.sort.Parameter;
import com.youku.soku.newext.info.TypeConstant;


/**
 * 排行榜入口点
 */
public class TopSearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(TopSearcherServlet.class);

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
			
			String programmeIds=request.getParameter("programmeId");
			String cate_str=request.getParameter("cate");
//			String pageNo_str=request.getParameter("pageNo");
//			String limit_str=request.getParameter("limit");
			int cate_id=2;
//			int pageNo=1,limit=25;
			try{
				cate_id=new Integer(cate_str).intValue();
//				pageNo=new Integer(pageNo_str).intValue();
//				limit= new Integer(limit_str).intValue();
			}catch(Exception e){
				logger.warn("cate 出错"+cate_str);
				cate_id=TypeConstant.NameType.MOVIE;
			}
			if(cate_id!=TypeConstant.NameType.MOVIE && cate_id!=TypeConstant.NameType.TELEPLAY &&
					cate_id!=TypeConstant.NameType.VARIETY && cate_id!= TypeConstant.NameType.ANIME && cate_id != TypeConstant.NameType.DOCUMENTARY){
				logger.error("cate 类型出错");
				response.getWriter().print("");
				response.getWriter().flush();
			}
			
			if(programmeIds==null || StringUtils.trimToEmpty(programmeIds).length()<=0 ){
				logger.error("programmeIds 出错");
				response.getWriter().print("");
				response.getWriter().flush();
			}
			
		
			
			
			//在直达区中不处理屏蔽词
			boolean success  = true;
			
			if(success){
				try {
					String result = Searcher.searchTop(programmeIds,cate_id);
					responseString = result;
					
					}catch (Exception e) {
					logger.error("排行榜数据查询异常！programmeIds:" + programmeIds+" Cate_id:"+cate_id);
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

	
}
