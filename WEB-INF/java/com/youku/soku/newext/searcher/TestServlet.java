package com.youku.soku.newext.searcher;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.PersonInfo;


/**
 * 详情页演员接口
 */
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(TestServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) {
		
		StringBuffer responseString = new StringBuffer();;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			
			PersonInfo info = ExtInfoHolder.getCurrentThreadLocal().personInfo;
			
			
			String name=request.getParameter("person");
			List<Integer> idSet = info.nameIdsMap.get(name);
			
			StringBuffer returnBf=new StringBuffer();
			if(idSet!=null && idSet.size()>0){
				returnBf.append(name+" 对应ID的个数："+idSet.size());
				returnBf.append("<br/>");
				for (Integer pid : idSet) {
					List<Programme> pList=info.personproMap.get(pid);
					returnBf.append(pid).append(" 对应的节目数"+(pList==null?0:pList.size()));
					returnBf.append("<br/>");
				}
				
			}else {
				returnBf.append(name+" 没有对应的ID");
			}
			
			
			response.getWriter().print(returnBf.toString());
			response.getWriter().flush();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("error:"+e.getMessage());
		} 
	}

	
}
