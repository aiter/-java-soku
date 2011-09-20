<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*"%>

<form name="" action="video.jsp" method="post">
视频ID：<input type="text" name="vid">
<input type="submit" value="提交">
</form>

<%
if (request.getParameter("vid") != null)
{
	int vid = DataFormat.parseInt(request.getParameter("vid"));
	Video video = VideoQueryManager.getInstance().getVideo(vid);
	if (video != null)
	{
		out.print("title="+video.title + "<br>");
		out.print("tags="+video.tags + "<br>");
		out.print("memo="+video.memo + "<br>");
	}
	else
	{
		out.println("not found");
	}
}
%>