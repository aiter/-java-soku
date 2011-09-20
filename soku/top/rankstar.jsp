<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page
	import="java.util.*,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				org.apache.commons.lang.StringUtils,
				com.youku.soku.util.MyUtil,
				com.youku.search.util.DataFormat,
				org.json.JSONObject,
				com.youku.top.directory_top.entity.LibraryResult,
				com.youku.top.directory_top.entity.Library,
				com.youku.top.directory_top.DirectoryTopUtils,
				com.youku.soku.library.directory.DirectoryUtils,
				com.youku.soku.library.directory.DirectoryPersonDateGetter,
				com.youku.top.topindex.utils.VideoTypeUtil.VideoType"%>
<%
long start_time = System.currentTimeMillis();
String channel = "person";
if (StringUtils.isBlank(channel)||!channel.equalsIgnoreCase("person")){
	response.sendRedirect("/");	
	return ;
}
boolean isAll = channel.equals("all");
if (isAll){
		/* 获取上下文环境 */
		ServletContext sc = getServletContext();
		/* 设置返回地址 */
		RequestDispatcher rd=null;
			rd = sc.getRequestDispatcher("/channel/all.jsp");
		/* forward到结果页面 */
		rd.forward(request, response);
		return;
}

int defaultlimit = 20;
String ps_str=request.getParameter("ps");
String pn_str=request.getParameter("pn");

String view_date = request.getParameter("date");
if(StringUtils.isBlank(view_date)||!view_date.matches("\\d{4}_\\d{2}_\\d{2}"))
	view_date = null;
int ps=DataFormat.parseInt(ps_str,defaultlimit);
int pn=DataFormat.parseInt(pn_str,1);

int start_no = (pn-1)*ps;
int limit = ps;
if(limit>50)limit =50;

int fromIndex=(pn-1)*ps;

String para = "json=json&channel="+channel;
if(!StringUtils.isBlank(view_date)){
	para = para + "&date="+view_date;
}

LibraryResult lr=new LibraryResult();

lr = DirectoryPersonDateGetter.queryReturnList(view_date,0, "0",
		"0", "0", start_no, limit);
List<Library> librarys = lr.getLibrarys();
int size = (null==librarys||librarys.size()<1)?0:librarys.size();
start_no = lr.getNextDocId();
System.out.println("数据返回耗费时间:"+(System.currentTimeMillis()-start_time) +"ms");


%>
<!DOCTYPE HTML>
<html><head>


<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>明星排行榜-搜库</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/soku.js"></script>
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
	<h1 class="rankname">明星排行榜</h1>
	<label>选择排行榜:</label>
	<%@ include file="/top/inc/topnavigate.inc" %>
</div>	
	<div class="rank_stars">
	<div class="items">
		<%
				int bh = 0;
			for(Library library:librarys){
				%>
<div class="order"><span class="ordernum"><%=fromIndex+bh+1 %></span></div>
<ul class="star">
		<li class="photo"><a target="_blank" href="/v?keyword=<%=MyUtil.urlEncode(library.getName()) %>"><img src="<%=DirectoryTopUtils.checkimg(library.getPic())?library.getPic():"http://g1.ykimg.com/0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0" %>"></a></li>
		<li class="name" style="text-align:center;"><a target="_blank" href="/v?keyword=<%=MyUtil.urlEncode(library.getName()) %>"><%=library.getName() %></a></li>
</ul>
				
<%
			if(bh%5==4||bh==size-1){
				%>
<div class="clear"></div>
<%
			}
			bh = bh+1;
			}
			%>
										
				<div class="clear"></div>
					</div>
</div>
<%int total=lr.getTotal();%>
<div class="pager"><%=PageUtil.getNewContent("/channel/person/",".html",total,pn,ps)%></div>


</div><!--layout end-->
</div><!--body end-->

<%@ include file="/result/inc/footer.jsp" %>

</div><!--screen end-->
</div><!--widnow end-->
</body></html>