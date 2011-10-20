
package com.youku.soku.newext.searcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.util.JSONUtil;
import com.youku.soku.util.ChannelType;

/**
 * 详情页演员接口  演员--programme list的对应关系
 */
public class PersonProgrammeSearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory
			.getLog(PersonProgrammeSearcherServlet.class);

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

			String person = request.getParameter("person");
			String pid = request.getParameter("pid");
			int personId = 0;
			if(pid!=null){
				try {
					personId = Integer.parseInt(pid);
				} catch (Exception e) {
				}
			}
			if(personId==0){//兼容，直接用person传pid
				try {
					personId = Integer.parseInt(person);
				} catch (Exception e) {
				}
			}
			String h = request.getParameter("h");
			if ((person == null || person.trim().length() <= 0) && (personId==0)) {
				logger.warn("请输入person或pid参数");
				responseString = "{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
			}
			person = StringUtils.trimToEmpty(person);
			JSONObject returnJson = new JSONObject();

			PersonInfo info = ExtInfoHolder.getCurrentThreadLocal().personInfo;
			
			AliasInfo aliasInfo=ExtInfoHolder.getCurrentThreadLocal().aliasInfo;
			
			if (personId>0) {
				returnJson = PeopleSearcher.getDetailJsonByid(info,personId,aliasInfo);
			}else if(person!=null && person.length()>0){
				returnJson = PeopleSearcher.genDetailJson(info, person,aliasInfo);
			} 


			if (returnJson == null) {
				responseString = "{}";
				response.getWriter().print(responseString);
				response.getWriter().flush();
				return;
			}

			if (h != null) {
				responseString = returnJson.toString(4);
			} else {
				responseString = returnJson.toString();
			}

			response.getWriter().print(responseString);
			response.getWriter().flush();

		} catch (JSONException e) {
			logger.error("personpro查询出错：" + e.getMessage());
			e.printStackTrace();
			responseString = "{}";
			try {
				response.getWriter().print(responseString);
				response.getWriter().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 生成人物--电视剧电影对应关系列表
	/*private JSONObject genListJson(PersonInfo info, String person) {
		JSONObject resultJson = new JSONObject();
		String personPic = StringUtils.trimToEmpty(info.personpicMap
				.get(person));

		List<Programme> programmeList = info.personproMap.get(person);
		try {
			resultJson.put("name", person);
			if (personPic != null && personPic.length() > 0) {

				JSONObject personInfo = new JSONObject(personPic);
				if (personInfo != null) {
					resultJson.put("pic", StringUtils.trimToEmpty(personInfo
							.optString("thumburl")));
				} else {
					resultJson.put("pic", "");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("构造json对象失败： " + e.getMessage());

		}

		Set<JSONObject> movieList = new HashSet<JSONObject>();
		Set<JSONObject> teleList = new HashSet<JSONObject>();
		if (programmeList != null && programmeList.size() > 0) {
			for (Programme programme : programmeList) {
				if (programme.getCate() == ChannelType.MOVIE.getValue()) {
					JSONObject tmpJson=new JSONObject();
					try {
						tmpJson.put("name",StringUtils.trimToEmpty(programme.getName()));
						tmpJson.put("pid",programme.getId());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					movieList.add(tmpJson);
				} else if (programme.getCate() == ChannelType.TELEPLAY
						.getValue()) {
					
					JSONObject tmpJson=new JSONObject();
					try {
						tmpJson.put("name",StringUtils.trimToEmpty(programme.getName()));
						tmpJson.put("pid",programme.getId());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					teleList.add(tmpJson);

				}
			}
		}
		try {
			resultJson.put(ChannelType.MOVIE.name(), movieList);
			resultJson.put(ChannelType.TELEPLAY.name(), teleList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("构造json对象失败： " + e.getMessage());
		}

		return resultJson;

	}*/

}
