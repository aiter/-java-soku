<<<<<<< .mine
<%@page contentType="text/html;charset=utf-8"%>
<%@page
	import="java.util.*,
				org.apache.commons.lang.StringUtils,
				com.youku.search.util.DataFormat,
				org.json.JSONArray,
				org.json.JSONObject,
				com.youku.soku.top.directory.DirectoryDateGetter,
				com.youku.top.util.VideoType" 
%>
<%
String channel = request.getParameter("channel");
int cate = 0;
if (StringUtils.isBlank(channel)){
	channel = VideoType.topconcern.name();
}
channel = channel.trim();

String limit_str = request.getParameter("limit");

int limit = 25;
if (!StringUtils.isBlank(limit_str))
limit = DataFormat.parseInt(limit_str, 10);
if(limit>300) limit=300;
JSONObject json = null;

json = DirectoryDateGetter.queryTopJson(channel,limit);
out.clear();
if(json!=null){
	out.print(json.toString());	
}else{
	out.print("查询错误！");
}

%>