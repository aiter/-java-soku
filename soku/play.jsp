<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.web.*,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.search.util.DataFormat,
				org.json.*"%>
<%
String e = request.getParameter("ep");
String cate = request.getParameter("ca");
String st = request.getParameter("st");
String lm = request.getParameter("lm");
String url = request.getParameter("u");
String change = request.getParameter("cg");

JSONObject content = (JSONObject)request.getAttribute("content");
if (content == null)
{
	if (url!=null)
		response.sendRedirect(url);
	return;
}

JSONObject name = content.getJSONObject("names");

int current_version_id = content.getInt("current_version_id");
JSONObject version = content.getJSONObject("current_version");


int episode_total = content.getInt("episode_total");
int start = content.getInt("episode_start");
int limit = content.getInt("episode_limit");

JSONObject episode = content.getJSONObject("current_episode");

url = episode.getString("url");
e = episode.getString("id");

int current_episode_id = episode.getInt("id");

JSONArray episodes = content.getJSONArray("current_site_episode");
JSONObject current_site_version   = content.getJSONObject("current_site_version");
int total = current_site_version.getInt("episode_collected");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<link type="text/css" rel="stylesheet" href="/css/play.css" />

</head>

<body>
<!--不加载js核心功能仍然可用
右侧引入网页复杂度（渲染过慢） 
直接影响左侧执行效率
-->
<!--列表区-->
<div class="listArea" id="listArea">

	<!--列表控制柄-->
	<div class="handle" id="handle" title="收起"></div>

	<div class="logo"><a href="#" target="_blank"><img src="img/logo_soku_s.gif" alt="Soku" width="136" /></a></div>
	<div class="pack" id="pack">
		<a href="#" target="_blank"><%=name.optString("name")%><%=version.optString("name")%><%=episode.optString("order_id")%></a>
		<span>(全<%=episode_total%>集)</span>
	</div>
	<div class="playing" >
		<div class="item" id="playing">
			<img class="thumb" src="<%=WebUtil.formatLogo(episode.getString("logo"))%>" />
			第<%=episode.optString("order_id")%>集
		</div>
		<div class="mark"></div>
		<%if (content.getBoolean("has_next_site_episode")){%>
		<div class="change"><a href="javascript:changeurl();">播放慢，换一个</a></div>
		<%}%>
	</div>
	<div class="list" id="list">
		<div class="up" id="up"><a href="#"></a></div>
		<div class="items" id="items">		
			
		</div>
		<div class="down" id="down"><a href="#"></a></div>
	</div>
</div>

<script type="text/javascript">
var items=new Array(); 
var total = <%=total%>;
var current = <%=current_episode_id%>;
var limit = 40;
var url = '/pj?ca=<%=cate%>&ep=<%=e%>';

<%
for (int i=0;i<episodes.length();i++)
{
	JSONObject ep = episodes.getJSONObject(i);
	out.println("items["+(DataFormat.parseInt(st)+i)+"]=['"+ep.getString("id")+"','"+ep.getString("order_id")+"','"+WebUtil.formatLogo(ep.getString("logo"))+"','"+ep.getString("url")+"'];");
}%>


function changeurl(){
	location.href="/p?ca=<%=cate%>&ep="+ current +"&st=<%=st%>&lm=<%=lm%>&cg";
}
</SCRIPT>

<script type="text/javascript" src="/js/play.js"></script>
<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script>youkuTracker( "soku=0|0" );</script>
<%if (change != null ){%>
	<script type="text/javascript">
		window.parent.openVideo(null,'<%=url%>');
		window.parent.focusWin();
	</script>
<%}%>
</body>
</html>
