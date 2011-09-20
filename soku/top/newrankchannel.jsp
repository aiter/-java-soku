<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page
	import="java.util.*,
				org.apache.commons.lang.StringUtils,
				com.youku.soku.util.MyUtil,
				com.youku.search.util.DataFormat,
				com.youku.soku.top.directory.DirectoryDateGetter,
				com.workingdogs.village.Record"%>

<% //电视剧
//String channel ="teleplay";
String channel=request.getParameter("channel");
String limit=request.getParameter("limit");

//out.print("channel:"+channel);


if(channel==null) channel="all";
int defaultLimit=DataFormat.parseInt(limit, 50);

if(defaultLimit>50) defaultLimit=50;

List<Record> programmeArr = null,teleplayArr=null ,movieArr=null,varietyArr=null,
musicArr=null,animeArr=null, personArr=null,sportsArr=null,scienceArr=null, funArr=null   ;
if(channel.equals("all")){
	defaultLimit=10;
	movieArr=DirectoryDateGetter.queryNewTop("movie",defaultLimit);
	teleplayArr=DirectoryDateGetter.queryNewTop("teleplay",defaultLimit);
	varietyArr=DirectoryDateGetter.queryNewTop("variety",defaultLimit);
	musicArr=DirectoryDateGetter.queryNewTop("music",defaultLimit);
	animeArr=DirectoryDateGetter.queryNewTop("anime",defaultLimit);
	personArr=DirectoryDateGetter.queryNewTop("person",defaultLimit);
	sportsArr=DirectoryDateGetter.queryNewTop("sports",defaultLimit);
	scienceArr=DirectoryDateGetter.queryNewTop("science",defaultLimit);
	funArr=DirectoryDateGetter.queryNewTop("fun",defaultLimit);
	
}else{
	programmeArr = DirectoryDateGetter.queryNewTop(channel,defaultLimit);
}


%>
<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
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
</div><div class="layout_16">
	<div class="sidecol">
	<div class="ranktree">
<ul>
<%if(channel.equals("all")){ %>
<li class="current"><span>排行榜首页</span></li>
<%}else{ %>
<li><a href="/newtop/all.html">排行榜首页</a></li>
<%} %>

<%if(channel.equals("teleplay")){ %>
<li class="current"><span>电视剧</span></li>
<%}else{ %>
<li><a href="/newtop/teleplay.html">电视剧</a></li>
<%} %>

<%if(channel.equals("movie")){ %>
<li class="current"><span>电影</span></li>
<%}else{ %>
<li><a href="/newtop/movie.html">电影</a></li>
<%} %>

<%if(channel.equals("variety")){ %>
<li class="current"><span>综艺</span></li>
<%}else{ %>
<li><a href="/newtop/variety.html">综艺</a></li>
<%} %>

<%if(channel.equals("anime")){ %>
<li class="current"><span>动漫</span></li>
<%}else{ %>
<li><a href="/newtop/anime.html">动漫</a></li>
<%} %>

<%if(channel.equals("music")){ %>
<li class="current"><span>音乐</span></li>
<%}else{ %>
<li><a href="/newtop/music.html">音乐</a></li>
<%} %>

<%if(channel.equals("person")){ %>
<li class="current"><span>明星</span></li>
<%}else{ %>
<li><a href="/newtop/person.html">明星</a></li>
<%} %>

<%if(channel.equals("sports")){ %>
<li class="current"><span>体育</span></li>
<%}else{ %>
<li><a href="/newtop/sports.html">体育</a></li>
<%} %>

<%if(channel.equals("science")){ %>
<li class="current"><span>科教</span></li>
<%}else{ %>
<li><a href="/newtop/science.html">科教</a></li>
<%} %>

<%if(channel.equals("fun")){ %>
<li class="current"><span>搞笑</span></li>
<%}else{ %>
<li><a href="/newtop/fun.html">搞笑</a></li>
<%} %>
</ul>
</div>	</div><!--sidecol end-->
	<div class="maincol">
		<%if(!channel.equals("all")){ %>
		<div class="rank50">
	<div class="rh">
		<h3><%if(channel.equals("teleplay")){out.print("电视剧");}
		else if(channel.equals("movie")){out.print("电影");}
		else if(channel.equals("anime")){out.print("动漫");}
		else if(channel.equals("variety")){out.print("综艺");}
		else if(channel.equals("music")){out.print("音乐");}
		else if(channel.equals("person")){out.print("明星");}
		else if(channel.equals("sports")){out.print("体育");}
		else if(channel.equals("science")){out.print("科教");}
		else if(channel.equals("fun")){out.print("搞笑");}
			%></h3><em><%if(channel.equals("fun")){out.print("播放量");}
									else{out.print("搜索量");}%></em>
	</div>
	<div class="rb">
		<ol>
		<%
		  if(programmeArr!=null && programmeArr.size()>0){
			 for(int i=0;i<programmeArr.size();i++){
				Record eleProgramme=programmeArr.get(i); 
			 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString());
			 	
			 	String url="/v?keyword="+MyUtil.urlEncode(k);
			 	if(channel.equals("fun")) 
			 		url=StringUtils.trimToEmpty(eleProgramme.getValue("url").asString());
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="<%=url%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		}else{
			out.print("没有榜单结果");
		}
		%>
			
		</ol>
	</div>
</div>
<%}else{ %>
<div class="rank10coll">
<div class="items">

<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/teleplay.html">电视剧</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
		    if(teleplayArr!=null && teleplayArr.size()>0){
				 for(int i=0;i<teleplayArr.size();i++){
					Record eleProgramme=teleplayArr.get(i); 
				 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString());
			 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/teleplay.html">完整榜单</a>
	</div>
</div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/movie.html">电影</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
		    if(movieArr!=null && movieArr.size()>0){
				 for(int i=0;i<movieArr.size();i++){
					Record eleProgramme=movieArr.get(i); 
				 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString());
			 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/movie.html">完整榜单</a>
	</div>
</div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/variety.html">综艺</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
		 if(varietyArr!=null && varietyArr.size()>0){
			 for(int i=0;i<varietyArr.size();i++){
				Record eleProgramme=varietyArr.get(i); 
			 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString());
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/variety.html">完整榜单</a>
	</div>
</div>

<div class="clear"></div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/anime.html">动漫</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
				 if(animeArr!=null && animeArr.size()>0){
					 for(int i=0;i<animeArr.size();i++){
						Record eleProgramme=animeArr.get(i); 
					 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString());
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/anime.html">完整榜单</a>
	</div>
</div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/music.html">音乐</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
			  if(musicArr!=null && musicArr.size()>0){
					 for(int i=0;i<musicArr.size();i++){
						Record eleProgramme=musicArr.get(i); 
					 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString()); 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/music.html">完整榜单</a>
	</div>
</div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/person.html">明星</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
			  if(personArr!=null && personArr.size()>0){
					 for(int i=0;i<personArr.size();i++){
						Record eleProgramme=personArr.get(i); 
					 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString()); 
			 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/person.html">完整榜单</a>
	</div>
</div>

<div class="clear"></div>


<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/sports.html">体育</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
			  if(sportsArr!=null && sportsArr.size()>0){
					 for(int i=0;i<sportsArr.size();i++){
						Record eleProgramme=sportsArr.get(i); 
					 	String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString()); 
			 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/sports.html">完整榜单</a>
	</div>
</div>

<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/science.html">科教</a></h3><em>搜索量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
		    if(scienceArr!=null && scienceArr.size()>0){
				 for(int i=0;i<scienceArr.size();i++){
					Record eleProgramme=scienceArr.get(i); 
					String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString()); 
			 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="/v?keyword=<%=MyUtil.urlEncode(k)%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/science.html">完整榜单</a>
	</div>
</div>

<div class="rank10">
	<div class="rh">
		<h3><a href="/newtop/fun.html">搞笑</a></h3><em>播放量</em>
	</div>
	<div class="rb">
		<ol>
		    <%
				  if(funArr!=null && funArr.size()>0){
						 for(int i=0;i<funArr.size();i++){
							Record eleProgramme=funArr.get(i); 
							String k = StringUtils.trimToEmpty(eleProgramme.getValue("keyword").toString()); 
		  
		%>
			<li>
			<span class="order"><%=i+1 %></span>
			<span class="skey"><a href="<%=StringUtils.trimToEmpty(eleProgramme.getValue("url").asString())%>" 
				target="_blank"><%=k%></a></span>
			<span class="stat"><%=eleProgramme.getValue("query_count")%></span>
			</li>
		<%}
		  }
		%>
		</ol>
	</div>
	<div class="rf">
		<a href="/newtop/fun.html">完整榜单</a>
	</div>
</div>






<div class="clear"></div>
</div><!--ranks end-->
</div>
<%} %>		
</div><!--maincol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->

<%@ include file="/result/inc/footer.jsp" %>
</div><!--screen end-->
</div><!--widnow end-->
</body>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
</script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>
</html>