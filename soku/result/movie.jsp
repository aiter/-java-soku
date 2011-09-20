<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.sort.Parameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.soku.util.MyUtil,
				org.json.*,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager,
				com.youku.search.util.DataFormat,
				com.youku.soku.index.manager.db.SiteManager
				"%>
<%
SearchResult result = (SearchResult)request.getAttribute("content");
int pagesize = Constant.Web.SEARCH_PAGESIZE;
String cost = result.getCost();
Content content = result.getContent();
int total = content.getTotal();

JSONObject movie = content.getMovie();

int cate_id = DataFormat.parseInt(request.getParameter("ca"));


String encodeKeyword = MyUtil.urlEncode(result.getKeyword());
JSONObject name = movie.getJSONObject("names");
JSONArray version_names = movie.getJSONArray("version_names");
JSONArray versions = movie.getJSONArray("versions");
int current_version_id = movie.getInt("current_version_id");

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.getKeyword()%> 搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<link rel="Shortcut Icon" href="http://static.youku.com/v1.0.0627/index/img/favicon.ico" />
<script type="text/javascript" src="http://static.youku.com/js/prototype.js"></script>
<script type="text/javascript" src="http://static.youku.com/js/nova.js"></script>
<script type="text/javascript" src="http://static.youku.com/search/js/searchprompt.js"></script>
<script type="text/javascript" src="/js/sotool.js"></script>
</head>

<body>
<div class="window">
<div class="screen">
<div class="wrap">

	

	<%@ include file="/result/inc/header.inc" %>

	<div class="soku_master">
		<div class="control">
			
			<div class="groupby">	
				<ul class="tabs">
				
				<li><span><a href="?keyword=<%=encodeKeyword%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,0,0);">全部</a></span></li>
				<%
				JSONArray tabs = content.getTabs();
				if (tabs != null){
					for (int i = 0;i< tabs.length();i++)
					{
						int cate = tabs.getInt(i);
						String cate_name = CateManager.getInstance().getName(cate);
						
						if (cate == 2){
							out.print("<li class='current'><span>");
							out.print(cate_name);
							out.print("</span></li>");
						}
						else{
							out.print("<li><a href=\"/v?keyword=");
							out.print( MyUtil.urlEncode(result.getKeyword()));
							out.print("&ca=");
							out.print(cate);
							out.print("\" onclick=\"clicklog(this.href,'"+ encodeKeyword +"',1,"+ cate +","+ i +");\">");
							out.print(cate_name);
							out.print("</a>");
							out.print("</li>");
						}
					}
				}
				%>

				</ul>
				
			</div>			
			<div class="filter">
				<div class="statinfo">找到相关视频<%=versions.length()%>个</div>
				
				<div class="clear"></div>	
			</div>
		</div>
		<div class="result">
				<!--电影 分部-->
				<div class="movie">

				<div class="items">
				<%


				for (int i = 0;i< versions.length();i++)
				{
					JSONObject version = versions.getJSONObject(i);
					int hd = version.getInt("hd");
					String view_url = version.optString("view_url");
					String url = view_url!=null && !view_url.isEmpty()?view_url:("v?keyword="+encodeKeyword+"&ca="+cate_id+"&ver="+version.getString("id"));

					if (versions.length() == 1){
						
						String memo = version.getString("brief");
						JSONArray performer = version.getJSONArray("performer");
						%>
						<!--电影-->
						<ul class="mIntro">
							<li class="thumb">
								<div class="item">
									<ul>
									<li class="i_link"><a href="<%=url%>" title="<%=name.getString("name")%><%=version.getString("name")%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
									<li class="i_thumb"><img src="<%=WebUtil.formatLogo(version.getString("logo"))%>" alt="<%=version.getString("name")%>" /></li>
									<li class="i_status">
										<%if (hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
										<div class="time"><%=WebUtil.getSecondAsString((float)version.getDouble("seconds"))%></div>
										<div class="bg"></div>
									</li>
									</ul>
								</div>
							</li>
							<li class="title"><span class="highlight"><%=name.getString("name")%></span><%=version.getString("name")%></li>
							<%if (view_url!=null && !view_url.isEmpty()){%>
								<li class="goplay"><a href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"><span class="btn" title="播放影片"><em>播放影片</em></span></a></li>
							<%}%>
							<li class="director"><span class="label">导演:</span> <%WebUtil.formatJSONArray(version.getJSONArray("director"),out);%></li>
							<li class="class"><span class="label">类型:</span>  <%WebUtil.formatJSONArray(version.getJSONArray("cates"),out);%></li>
							<%if (performer!=null){%>
								<li class="actor"><span class="label">主演:</span><%WebUtil.formatJSONArray(performer,out);%></li>
							<%}%>
							<li class="lang"><span class="label">语种:</span> <%=version.getString("language")%></li>
							<li class="intro">
								<span class="label">简介:</span> 
								<span id="S1"><%=WebUtil.subString(memo,0,100)%>...</span>
								<%if (!version.getString("detail_url").isEmpty()){%><a id="H1" href="<%=version.getString("detail_url")%>">查看详情</a><%}%>
							</li>
						</ul>

						
					<%
						}else{
					%>

						<div class="item">
							<ul>
							<li class="i_link"><a title="<%=version.getString("name")%>" href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
							<li class="i_thumb"><img src="<%=WebUtil.formatLogo(version.getString("logo"))%>" /></li>
							<li class="i_status">
								<div class="time"><%=WebUtil.getSecondAsString((float)version.getDouble("seconds"))%></div>
								<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
								<div class="bg"></div>
							</li>
							<li class="i_title"><a title="<%=version.getString("name")%>" href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"><%=version.getString("name")%></a></li>
							</ul>
						</div>

						
					<%
						if ( (i+1)%5==0){
							out.println("<div class=\"clear\"></div>");	
							}
						}
					}
					%>
						<div class="clear"></div>
					</div><!--items end-->
				</div><!--movie end-->
			
			
			
		</div><!--result end-->
	</div><!--soku_master end-->
	<SCRIPT LANGUAGE="JavaScript">library=1;result_count=<%=versions.length()%>;</SCRIPT>
	<%@ include file="/result/inc/footer.inc" %>
		
</div>
</div>
</div>

</body>
</html>
