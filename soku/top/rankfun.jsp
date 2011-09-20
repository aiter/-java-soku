<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page import="java.util.*,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.soku.library.top.TopContentMgt,
				com.youku.soku.util.MyUtil,
				com.youku.soku.library.directory.DirectoryUtils,
				org.json.JSONObject,
				org.apache.commons.lang.StringUtils,
				com.youku.top.directory_top.entity.LibraryResult,
				com.youku.top.directory_top.entity.Library,
				com.youku.top.directory_top.DirectoryTopUtils,
				com.youku.top.topindex.utils.VideoTypeUtil.VideoType"%>
<%@page import="com.youku.search.util.DataFormat"%>
<%
long start = System.currentTimeMillis();
String channel = request.getParameter("channel");
if (channel == null){
	response.sendRedirect("/");	
	return ;
}
boolean channelExist = false;
for(VideoType vt:VideoType.values()){
	if(vt.name().equalsIgnoreCase(channel)){
		channelExist = true;
		break;
	}
}

if(!channelExist) channel="fun";

int defaultlimit = 20;

String ps_str=request.getParameter("ps");
String pn_str=request.getParameter("pn");

int ps=DataFormat.parseInt(ps_str,defaultlimit);
int pn=DataFormat.parseInt(pn_str,1);

int start_no = (pn-1)*ps;
int limit = ps;
if(limit>50)limit =50;

int fromIndex=(pn-1)*ps;

String isJson = request.getParameter("json");
String view_date = request.getParameter("date");
if(StringUtils.isBlank(view_date)||!view_date.matches("\\d{4}_\\d{2}_\\d{2}"))
	view_date = null;
String para = "json=json&channel="+channel;
if(!StringUtils.isBlank(view_date)){
	para = para + "&date="+view_date;
}

LibraryResult lr=new LibraryResult();

lr = TopContentMgt.queryReturnLibraryResult(channel,view_date,start_no,limit);
start_no = lr.getNextDocId();
List<Library> librarys = lr.getLibrarys();
int size = (null==librarys||librarys.size()<1)?0:librarys.size();

String topName="搞笑"; 
if(channel.equals("fun")){
	topName="搞笑";
}else if(channel.equals("music")){
	topName="音乐";
}else if(channel.equals("sports")){
	topName="体育";
}else if(channel.equals("science")){
	topName="科技";
}
%>
<!DOCTYPE HTML>
<html><head>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=topName%>排行榜-搜库</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
</head><body>
<div class="window">
<div class="screen">

<% String keyword = request.getParameter("keyword");
	keyword="";
%>
<%@ include file="/result/inc/header.jsp" %>

<div class="body">
<div class="typechk">
<div class="main">
	</div>
</div><div class="layout">

<div class="ranktype">
	<h1 class="rankname"><%=topName %>排行榜</h1>
	<label>选择排行榜:</label>
	<%@ include file="/top/inc/topnavigate.inc" %>
</div>	
	<div class="rank_videos">
	<div class="items">
	
	<%              boolean isMusic = channel.equals("music");
					int i=0;
						if (librarys != null)
						{
							String link = null;
							for(Library library:librarys){
								link = library.getUrl();
								if(StringUtils.isBlank(link))	
									link = "/v?keyword="+MyUtil.urlEncode(library.getName());
								if (isMusic)
									link = link + " MV";
				%>	
		<div class="order"><span class="ordernum"><%=fromIndex+i+1 %></span></div>
		<ul class="v">
			<li class="v_link">
			<a href="<%=link%>" target="_blank" title="<%=library.getName()%>"></a></li>
			<li class="v_thumb"><img src="<%=DirectoryTopUtils.checkimg(library.getPic())?library.getPic():"http://g1.ykimg.com/0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0" %>" alt="<%=library.getName()%>"></li>
			<li class="v_ishd"><span class="" title=""></span></li>
			<li class="v_time"><span class="num"></span><span class="bg"></span></li>
			<%String littleName=StringUtils.trimToEmpty(library.getName());
				if(littleName.length()>15){
					littleName=littleName.substring(0,15)+"...";
				}
			%>
			<li class="v_title" style="text-align:center;"><a href="<%=link %>" target="_blank" title="<%=library.getName()%>"><%=littleName%></a></li>
		</ul>
		
		<%
					if((i+1) % 5==0)
						out.println("<div class=\"clear\"></div>");	
					i++;
					}
					}%>
	

												
				<div class="clear"></div>
					</div>
</div>


<%
  int total=lr.getTotal();
 %>
  <div class="pager"><%=PageUtil.getNewContent("/channel/"+channel+"/",".html",total,pn,ps)%></div>

</div><!--layout end-->
</div><!--body end-->

<%@ include file="/result/inc/footer.jsp" %>

</div><!--screen end-->
</div><!--widnow end-->
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/soku.js"></script>
</body></html>