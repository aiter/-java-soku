package com.youku.search.drama.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youku.search.drama.timer.EpisodeVideoLoadTimerTask;
import com.youku.search.util.StringUtil;

public class UpdateDramaCacheServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected synchronized void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		List<Integer> ids = parseParam(request);

		new Timer().schedule(new EpisodeVideoLoadTimerTask(ids), 1000L);

		//
		StringBuilder builder = new StringBuilder();
		builder.append("<p>working...</p>");
		builder.append("<p>drama id(s): ").append(ids).append("</p>");

		response.getWriter().print(builder);
	}

	private List<Integer> parseParam(HttpServletRequest request) {

		String[] id_param = request.getParameterValues("id");
		if (id_param == null) {
			return null;
		}

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < id_param.length; i++) {
			int id = StringUtil.parseInt(id_param[i], 0, 0);
			if (id > 0 && !list.contains(id)) {
				list.add(id);
			}
		}

		return list;
	}

}
