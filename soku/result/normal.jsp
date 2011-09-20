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

JSONObject teleplay = content.getTeleplay();

JSONObject cartoon = content.getCartoon();

JSONObject variety = content.getVariety();

JSONObject person = content.getPerson();

JSONObject major = content.getMajor(); //大词直达区

boolean hasDirect = movie!=null || teleplay!=null || cartoon!=null || variety != null || person!=null || major != null;


int ca = DataFormat.parseInt(request.getParameter("ca"));


String encodeKeyword = MyUtil.urlEncode(result.getKeyword());

Parameter param = result.getParam();
JSONArray sites = content.getSites();
String encodeSites = null;
String total_sites = request.getParameter("ts");



%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.getKeyword()%> 搜库 视频搜索</title>
<link rel="Shortcut Icon" href="http://static.youku.com/v1.0.0627/index/img/favicon.ico" />
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
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
				
				<%if (ca == 0){%>
					<li class="current"><span>全部</span></li>
				<%}else{%>
					<li><a href="/v?keyword=<%=encodeKeyword%>">全部</a></li>
				<%}%>
				<%	
				JSONArray tabs = content.getTabs();
				if (tabs != null){
					if (tabs.length() == 1 && tabs.getInt(0) == 6){//人物
						if (person != null){
							JSONObject name = person.getJSONObject("names");
							JSONArray cates = person.optJSONArray("cates");
							
							for (int i = 0;i<cates.length() ;i++)
							{
								int cate_person = cates.getInt(i);
								%>
								<li><a href="/v?keyword=<%=encodeKeyword%>&ca=6&tca=<%=cate_person%>&na=<%=name.getInt("id")%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,<%=cate_person%>,<%=i%>);"><%=CateManager.getInstance().getName(cate_person)%></a></li>
								<%
							}
					
						}
					}
					
					else
					{
						for (int i = 0;i< tabs.length();i++)
						{
							int cate = tabs.getInt(i);
							String cate_name = CateManager.getInstance().getName(cate);
							
							if(cate == ca)
							{
								out.print("<li class=\"current\"><span>");
								out.print(cate_name);
								out.print("</span></li>");
							}
							else
							{
								out.print("<li><a href=\"/v?keyword=");
								out.print(encodeKeyword );
								out.print("&ca=");
								out.print(cate);
								out.print("\" onclick=\"clicklog(this.href,'"+ encodeKeyword +"',1,"+ cate +","+ i +");\">");
								out.print(cate_name);
								out.print("</a>");
								out.print("</li>");
							}
						}
					}
				}
				%>

				</ul>
				
			</div>			
			<div class="filter">
				<div class="statinfo">找到相关视频<%=total%>个</div>
				<div class="selector">
				<form name="filter_form" action="<%=Constant.Web.SEARCH_URL%>" method="get">
				<input type="hidden" name="keyword" value="<%=result.getKeyword()%>">
				<input type="hidden" name="ext" value="2">
				<span class="label">视频筛选:</span>
					<select class="sel" name="time_length" onchange="filter_form.submit();">
						<option value="0"<%=param.time_length == 0?" selected":""%>>所有长度</option>
						<option value="1"<%=param.time_length == 1?" selected":""%>>0&ndash;10 分钟（短视频）</option>
						<option value="2"<%=param.time_length == 2?" selected":""%>>10&ndash;30 分钟（动漫）</option>
						<option value="3"<%=param.time_length == 3?" selected":""%>>30&ndash;60分钟（影视综艺）</option>
						<option value="4"<%=param.time_length == 4?" selected":""%>>≥60分钟（电影）</option>
					</select>
					<select class="sel" name="limit_date" onchange="filter_form.submit();">
						<option value="0"<%=param.limit_date == 0?" selected":""%>>不限发布时间</option>
						<option value="1"<%=param.limit_date == 1?" selected":""%>>一天内</option>
						<option value="7"<%=param.limit_date == 7?" selected":""%>>一周内</option>
						<option value="31"<%=param.limit_date == 31?" selected":""%>>一月内</option>
					</select>
					<select class="sel" name="hd" onchange="filter_form.submit();">
						<option value="0"<%=param.hd == 0?" selected":""%>>不限画质</option>
						<option value="1"<%=param.hd == 1?" selected":""%>>高清</option>
					</select>
					<select class="sel" name="site" onchange="filter_form.submit();">
						<option value="0"<%=param.site == 0?" selected":""%>>不限来源</option>
						<%
						if (sites!=null&& sites.length() > 0){
								StringBuilder sb = new StringBuilder();
								for (int i=0;i<sites.length();i++){
									int site_id = sites.getInt(i);
									sb.append(site_id).append(",");
									String site_name= SiteManager.getInstance().getName(site_id);
									%>
									<option value="<%=site_id%>"<%=param.site == site_id?" selected":""%>><%=site_name%></option>
								<%}
							encodeSites = com.youku.soku.util.MyUtil.getBASE64(sb.substring(0,sb.length()-1));
							
						
						}
						else
						{
						
							if(param.site > 0 && total_sites!=null){
								encodeSites = total_sites;
								String tss = com.youku.soku.util.MyUtil.getFromBASE64(encodeSites);
								String[] ts = tss.split(",");
								for (int i=0;i<ts.length;i++){
									int site_id = com.youku.search.util.DataFormat.parseInt(ts[i]);
									String site_name= com.youku.soku.index.manager.db.SiteManager.getInstance().getName(site_id);
								%>
								<option value="<%=site_id%>"<%=param.site == site_id?" selected":""%>><%=site_name%></option>
								<%
								}
							}
						}%>
					</select>
					<%if (encodeSites!=null){%>
						<input type="hidden" name="ts" value="<%=encodeSites%>">
					<%}%>
					</form>

				</div>
				<div class="clear"></div>	
			</div>
		</div>
		<div class="result">
			<%if (content.getCorrect_word()!=null){%>
			<div class="kLike">
				<span class="label">你是不是要找</span>
				<span class="keys"><a href="/v?keyword=<%=MyUtil.urlEncode(content.getCorrect_word())%>"><%=content.getCorrect_word()%></a></span>
			</div>
			<%}%>

			<%if (hasDirect){%>
				<div class="nonstop">
			<%}%>
			
			<%
			if (major != null)
			{
				Iterator it = major.keys();
				while (it.hasNext())
				{
					String key = (String)it.next();
					int cate_major = DataFormat.parseInt(key);
					JSONObject jsono = major.getJSONObject(key);
					String link = jsono.optString("url");

					if (jsono == null)continue;
					
					JSONArray jsonarr = null;
					String html = null;
					if (jsono.has("json"))
					{
						jsonarr = jsono.getJSONArray("json");
					}
					else if (jsono.has("html"))
					{
						html = jsono.getString("html");
					}
					else
						continue;
					if (html != null){
						out.println(html);
					}
					else
					{
						

			%>
						<!--大词直达区-->

							<div>
								<div class="guide">
									<h3 class="label"><span class="highlight"><%=result.getKeyword()%></span> --- 视频TOP榜单</h3> 
									<div class="viewall"><a href="<%=link%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,<%=key%>,0);">查看全部</a></div>
									<div class="clear"></div>
								</div>
							<div class="items">
							<%
								for (int j= 0;j<jsonarr.length();j++){
									JSONObject one = jsonarr.getJSONObject(j);

									String name = one.getString("name");
									String encodeName = MyUtil.urlEncode(name);
									String logo = one.getString("pic");	
									//String view_url = one.optString("url");	
									

							%>
									<div class="item">
										<ul>
										<li class="i_link"><a title="<%=name%>" href="/v?keyword=<%=encodeName%>" target="_blank" onclick="clicklog('this.href','<%=encodeKeyword%>',4,<%=key%>,0);"></a></li>
										<li class="i_thumb"><img src="<%=WebUtil.formatLogo(logo)%>" /></li>
										
										<li class="i_title"><a title="<%=name%>" href="/v?keyword=<%=encodeName%>" target="_blank" onclick="clicklog('this.href','<%=encodeKeyword%>',4,<%=key%>,0);"><%=name%></a></li>
										</ul>
									</div>
								<%}%>
									
									<div class="clear"></div>
								</div><!--items end-->
							</div><!--movie end-->

						<!--大词直达区结束-->
			<%		}
				}
			}%>
			<!--人物-->

		
<%
	if (person != null){
		JSONObject name = person.getJSONObject("names");
		
		int cate_id = name.getInt("cate_id");
		String person_name = name.getString("name");
		String encodeName = MyUtil.urlEncode(person_name);
		JSONArray cates = person.optJSONArray("cates");
		JSONArray teleplays = person.optJSONArray("teleplay");
		JSONArray movies = person.optJSONArray("movie");
		JSONArray varietys = person.optJSONArray("variety");
		if(tabs.length()>1)
		{
		%>
			<div class="character">
				<div class="guide">
					<h3 class="label"><%=CateManager.getInstance().getName(cate_id)%></h3> 
					<ul class="keys">
					<%
					for (int i = 0;i<cates.length() ;i++)
					{
						int cate_person = cates.getInt(i);
						%>
						<li><a href="/v?keyword=<%=encodeName%>&ca=<%=cate_id%>&tca=<%=cate_person%>&na=<%=name.getInt("id")%>"><%=person_name%><%=CateManager.getInstance().getName(cate_person)%></a></li>
						<%
					}
					%>
					</ul>
					<div class="clear"></div>
				</div>
			</div>
		<%
		}
		else
		{
		%>
		<div class="tv">
		<%
			for (int i = 0;i<cates.length() ;i++)
			{
				int cate_person = cates.getInt(i);
				%>
				<div class="guide">
					<h3 ><a href="/v?keyword=<%=encodeName%>&ca=<%=cate_id%>&tca=<%=cate_person%>&na=<%=name.getInt("id")%>" style="font-weight:normal">查看“<span class="key" style="font-weight:normal;"><%=person_name%></span>”的<%=CateManager.getInstance().getName(cate_person)%></a></h3>
					
					<div class="viewall"><a href="/v?keyword=<%=encodeName%>&ca=<%=cate_id%>&tca=<%=cate_person%>&na=<%=name.getInt("id")%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,1,0);">查看全部</a></div>
					<div class="clear"></div>
				</div>
				
				<%
				if (cate_person == 1){
				%>
					<div class="items">
					<%
					if (teleplays!=null){
						int len = Math.min(teleplays.length(),5);
						for (int j= 0;j<len;j++){
							JSONObject tv = teleplays.getJSONObject(j);
							String title = tv.getString("name");
							int hd = tv.getInt("hd");
							JSONObject names_object = tv.getJSONObject("names_object");
							String tvtitle = names_object.getString("name");
							String encodeTitle = MyUtil.urlEncode(tvtitle);
							%>
							<div class="item">
								<ul>
								<li class="i_link"><a title="<%=tvtitle%>" href="/v?keyword=<%=encodeTitle%>&ver=<%=tv.getString("id")%>&ca=<%=cate_person%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,1,<%=j%>);"></a></li>
								<li class="i_thumb"><img src="<%=WebUtil.formatLogo(tv.getString("logo"))%>" alt="<%=tvtitle%>" /></li>
								<li class="i_status">
									<%if (tv.optInt("completed")==1){%><div class="time"><%=tv.optString("episode_total")%>集全</div><%}%>
									<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
									<div class="bg"></div>
								</li>
								<li class="i_title"><a href="/v?keyword=<%=encodeTitle%>&ver=<%=tv.getString("id")%>&ca=<%=cate_person%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,1,<%=j%>);"><%=tvtitle%><%=title%></a></li>
								</ul>
							</div>
							<%
						}
					}
					%>
					</div>
					<div class="clear"></div>
					<%
				}
				else if (cate_person == 2){
					%>
					<div class="items">
						<%
						if (movies!=null){
							int len = Math.min(movies.length(),5);
							for (int j= 0;j<len;j++){
								JSONObject mov = movies.getJSONObject(j);
								String title = mov.getString("name");
								int hd = mov.getInt("hd");
								JSONObject names_object = mov.getJSONObject("names_object");
								String movietitle = names_object.getString("name");
								String encodeTitle = MyUtil.urlEncode(movietitle);
								%>
								<div class="item">
									<ul>
									<li class="i_link"><a title="<%=movietitle%><%=title%>" href="/v?keyword=<%=encodeTitle%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,2,<%=j%>);"></a></li>
									<li class="i_thumb"><img src="<%=WebUtil.formatLogo(mov.getString("logo"))%>" alt="<%=movietitle%><%=title%>" /></li>
									<li class="i_status">
										<div class="time"><%=WebUtil.getSecondAsString((float)mov.getDouble("seconds"))%></div>
										<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
										<div class="bg"></div>
									</li>
									<li class="i_title"><a href="/v?keyword=<%=encodeTitle%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,2,<%=j%>);"><%=movietitle%><%=title%></a></li>
									</ul>
								</div>
								<%
							}
						}
						%>
						</div>
						<div class="clear"></div>
					<%
				}
				else if (cate_person == 3)
				{
					%>
					<div class="items">
						<%
						if (varietys!=null){
							int len = Math.min(varietys.length(),5);
							for (int j= 0;j<len;j++){
								JSONObject vari = varietys.getJSONObject(j);

								JSONObject names_object = vari.getJSONObject("names");
								String movietitle = names_object.getString("name");
								String name_id = names_object.getString("id");
								String encodeTitle = MyUtil.urlEncode(movietitle);
								%>
								<div class="item">
									<ul>
									<li class="i_link"><a title="<%=movietitle%>" href="/v?keyword=<%=encodeTitle%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,3,<%=j%>);"></a></li>
									<li class="i_thumb"><img src="<%=WebUtil.formatLogo(vari.getString("logo"))%>" alt="<%=movietitle%>" /></li>
									<li class="i_title"><a href="/v?keyword=<%=encodeTitle%>" target="_blank" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,2,<%=j%>);"><%=movietitle%></a></li>
									</ul>
								</div>
								<%
							}
						}
						%>
						</div>
						<div class="clear"></div>
					<%
				}
			}
			%>
	</div>
		<%
		}
	}
%>

<!--人物结束-->

			<%
			if (movie != null){
				JSONObject name = movie.getJSONObject("names");
				JSONArray version_names = movie.getJSONArray("version_names");
				JSONArray versions = movie.getJSONArray("versions");
				int current_version_id = movie.getInt("current_version_id");
				
				int cate_id = name.getInt("cate_id");
				String encodeName = MyUtil.urlEncode(name.getString("name"));
				%>
				<!--电影 分部-->
				<div class="movie">
					<div class="guide">
						<h3 class="label">【<%=CateManager.getInstance().getName(cate_id)%>】</h3> 
						<ul class="section">
						
				<%
				if (ca > 0)//指定分类
				{
					for (int i = 0;i< version_names.length();i++)
					{
						JSONObject version_name = version_names.getJSONObject(i);
						int ver = version_name.getInt("id");
						
						if (ver == current_version_id)
						{
							%>
							<li class="current"><span class="highlight"><%=name.getString("name")%></span> <%=version_name.getString("name")%> </li>
							<%
						}
						else
						{
							%>
							<li><a href="/v?keyword=<%=encodeName%>&ver=<%=ver%>&ca=<%=cate_id%>"><%=name.getString("name")%><%=version_name.getString("name")%></a></li>
							<%
						}
					}
				}
				else
				{
					%><li class="current"><span class="highlight"><%=name.getString("name")%></span></li><%
				}
				%>
						</ul>
						
						<div class="clear"></div>
					</div>
				<div class="items">
				<%

				int j = 0;
				for (int i = 0;i< versions.length();i++)
				{
					
					JSONObject version = versions.getJSONObject(i);
					int hd = version.getInt("hd");
					String view_url = version.optString("view_url");
					if (view_url==null || view_url.isEmpty())
						continue;
					
					//String url = view_url!=null && !view_url.isEmpty()?view_url:("v?keyword="+encodeName+"&ca="+cate_id+"&ver="+version.getString("id"));
					String url = view_url;

					if (versions.length() == 1|| ca > 0){
						int version_id = version.getInt("id");

						if (ca > 0)
						{
							if(current_version_id!=version_id)
								continue;
						}
						
						String memo = version.getString("brief");
						JSONArray performer = version.getJSONArray("performer");
						%>
						<!--电影-->
						<ul class="mIntro">
							<li class="thumb">
								<div class="item">
									<ul>
									<li class="i_link"><a href="<%=url%>" title="<%=name.getString("name")%><%=version.getString("name")%>" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);" target="_blank"></a></li>
									<li class="i_thumb"><img src="<%=WebUtil.formatLogo(version.getString("logo"))%>" alt="<%=version.getString("name")%>" /></li>
									<li class="i_status">
										<%if (hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
										<div class="time"><%=WebUtil.getSecondAsString((float)version.getDouble("seconds"))%></div>
										<div class="bg"></div>
									</li>
									</ul>
								</div>
							</li>
							<li class="title"><%=name.getString("name")%></li>
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
							if (ca > 0)break;
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
						if ( (j+1)%5==0){
							out.println("<div class=\"clear\"></div>");	
							}
						}
						j++;
					}
					%>
						<div class="clear"></div>
					</div><!--items end-->
				</div><!--movie end-->
				<%
			}%>

			<%
			if (teleplay != null){
				JSONObject name = teleplay.getJSONObject("names");
				JSONArray version_names = teleplay.getJSONArray("version_names");
				int current_version_id = teleplay.getInt("current_version_id");
				int cate_id = name.getInt("cate_id");
				String encodeName = MyUtil.urlEncode(name.getString("name"));
				%>
				<!--电视剧-->
				<div class="tv">
					<div class="guide">
						<h3 class="label">【<%=CateManager.getInstance().getName(cate_id)%>】</h3> 
						<ul class="section">
				<%
				
				for (int i = 0;i< version_names.length();i++)
				{
					JSONObject version_name = version_names.getJSONObject(i);
					String versinname = version_name.getString("name");
					int version_id = version_name.getInt("id");
					if (version_id == current_version_id){
						%><li class="current"><a href="/v?keyword=<%=encodeName%>&ver=<%=version_id%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,1,<%=i+1%>);"><span class="highlight"><%=name.getString("name")%></span><%=versinname%></a></li><%
					}
					else
					{
						%>
						<li><a href="/v?keyword=<%=encodeName%>&ver=<%=version_id%>&ca=<%=cate_id%>"  onclick="clicklog(this.href,'<%=encodeKeyword%>',4,1,<%=i+1%>);"><%=versinname%></a></li>
						<%
					}
				}%>
						</ul>
						<%if (ca == 0){%>
							<div class="viewall"><a href="/v?keyword=<%=encodeName%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,1,0);">查看全部</a></div>
						<%}%>
						<div class="clear"></div>
					</div>
					<div class="items">
				
				<%
				JSONArray episodes = teleplay.optJSONArray("current_site_episode");
				if (episodes!=null){
					int size = episodes.length();
					int len = Math.min(size,5);
					for (int i = 0;i<len;i++)
					{
						JSONObject episode = episodes.getJSONObject(i);
						String title = episode.getString("title");
						String url = episode.getString("url");
						boolean is_sohu = url.indexOf("tv.sohu")>0;
						int hd = episode.getInt("hd");
						int order = episode.getInt("order_id");
						int start = 0;
						if (order > 20)start = order - 20;
						%>
						<div class="item">
							<ul>
						
								<li class="i_link"><a title="<%=title%>"  href="javascript:void(0);"  onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=episode.getInt("id")%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
							
							<li class="i_thumb"><img src="<%=WebUtil.formatLogo(episode.getString("logo"))%>" alt="<%=title%>" /></li>
							<li class="i_status">
								<div class="time"><%=WebUtil.getSecondAsString((float)episode.getDouble("seconds"))%></div>
								<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
								<div class="bg"></div>
							</li>
							
								<li class="i_title"><a  href="javascript:void(0);"   onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=episode.getInt("id")%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);">第<%=order%>集</a></li>
							
							</ul>
						</div>
						
						<%
					}
				}
				%>
						<div class="clear"></div>
					</div><!--items end-->
				</div><!--tv end-->
				<%
			}%>
	
			<%
			if (cartoon != null){
				JSONObject name = cartoon.getJSONObject("names");
				JSONArray version_names = cartoon.getJSONArray("version_names");
				int current_version_id = cartoon.getInt("current_version_id");
				int cate_id = name.getInt("cate_id");
				String encodeName = MyUtil.urlEncode(name.getString("name"));
				%>
				<!--动漫-->
				<div class="tv">
					<div class="guide">
						<h3 class="label">【<%=CateManager.getInstance().getName(cate_id)%>】</h3> 
						<ul class="section">
				<%
				
				for (int i = 0;i< version_names.length();i++)
				{
					JSONObject version_name = version_names.getJSONObject(i);
					String versinname = version_name.getString("name");
					int version_id = version_name.getInt("id");
					if (version_id == current_version_id){
						%><li class="current"><a href="/v?keyword=<%=encodeName%>&ver=<%=version_id%>&ca=<%=cate_id%>"  onclick="clicklog(this.href,'<%=encodeKeyword%>',4,5,<%=i+1%>);"><span class="highlight"><%=name.getString("name")%></span><%=versinname%></a></li><%
					}
					else
					{
						%>
						<li><a href="/v?keyword=<%=encodeName%>&ver=<%=version_id%>&ca=<%=cate_id%>"  onclick="clicklog(this.href,'<%=encodeKeyword%>',4,5,<%=i+1%>);"><%=versinname%></a></li>
						<%
					}
				}%>
						</ul>
						<%if (ca == 0){%>
							<div class="viewall"><a href="/v?keyword=<%=encodeName%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,5,0);">查看全部</a></div>
						<%}%>
						<div class="clear"></div>
					</div>
					<div class="items">
				
				<%
				JSONArray episodes = cartoon.optJSONArray("current_site_episode");
				if (episodes!=null){
					int size = episodes.length();
					int len = Math.min(size,5);
					for (int i = 0;i<len;i++)
					{
						JSONObject episode = episodes.getJSONObject(i);
						String title = episode.getString("title");
						String url = episode.getString("url");
						int hd = episode.getInt("hd");
						int order = episode.getInt("order_id");
						int start = 0;
						if (order > 20)start = order - 20;
						%>
						<div class="item">
							<ul>
							
							<li class="i_link"><a title="<%=title%>" href="javascript:void(0);"  onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=episode.getInt("id")%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
							<li class="i_thumb"><img src="<%=WebUtil.formatLogo(episode.getString("logo"))%>" alt="<%=title%>" /></li>
							<li class="i_status">
								<div class="time"><%=WebUtil.getSecondAsString((float)episode.getDouble("seconds"))%></div>
								<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
								<div class="bg"></div>
							</li>
							<li class="i_title"><a href="javascript:void(0);"  onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=episode.getInt("id")%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);">第<%=order%>集</a></li>
							</ul>
						</div>
						
						<%
					}
				}
				%>
						<div class="clear"></div>
					</div><!--items end-->
				</div><!--tv end-->
				<%
			}%>

		
<!--综艺-->
<%
			if (variety != null){
				JSONObject name = variety.getJSONObject("names");
				JSONArray years = variety.getJSONArray("years");
				int current_year_id = variety.getInt("current_year_id");
				int cate_id = name.getInt("cate_id");
				String encodeName = MyUtil.urlEncode(name.getString("name"));
				%>
				<div class="va">
					<div class="guide">
						<h3 class="label">【<%=CateManager.getInstance().getName(cate_id)%>】</h3> 
						<ul class="section">
						
						<%
						for (int i = 0;i<years.length() ;i++)
						{
							JSONObject year = years.getJSONObject(i);
							String year_name = year.getString("year");
							int year_id = year.getInt("id");

							if (year_id == current_year_id){
								%>
								<li class="current"><a href="/v?keyword=<%=MyUtil.urlEncode(name.getString("name"))%>&ver=<%=year_id%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,3,<%=i+1%>);"><span class="highlight"><%=name.getString("name")%><%=year_name%></span></a></li>
								<%
							}
							else
							{
								%>
								<li><a href="/v?keyword=<%=MyUtil.urlEncode(name.getString("name"))%>&ver=<%=year_id%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,3,<%=i+1%>);"><%=year_name%></a></li>
								<%
							}
						}%>
						</ul>
						<div class="viewall"><a href="/v?keyword=<%=MyUtil.urlEncode(name.getString("name"))%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,3,0);">查看全部</a></div>
						<div class="clear"></div>
					</div>
					<div class="items">
				<%
				JSONObject year_episodes = variety.optJSONObject("current_year_episode");
				
				if (year_episodes!=null){
					Iterator it = year_episodes.keys();
					int item_count = 0;
items:				while (it.hasNext())
					{
						String key = (String)it.next();
						JSONArray episodes = year_episodes.getJSONArray(key);
						int size = episodes.length();
						
						for (int i = 0;i<size;i++)
						{
							JSONObject episode = episodes.getJSONObject(i);
							String title = episode.getString("title");
							String url = episode.getString("url");
							int hd = episode.getInt("hd");
							%>
							<div class="item">
								<ul>
								<li class="i_link"><a title="<%=title%>" href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
								<li class="i_thumb"><img src="<%=WebUtil.formatLogo(episode.getString("logo"))%>" alt="<%=title%>" /></li>
								<li class="i_status">
									<div class="time"><%=WebUtil.getSecondAsString((float)episode.getDouble("seconds"))%></div>
									<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
									<div class="bg"></div>
								</li>
								<li class="i_title"><a href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"><%=title%></a>
								<%if (episode.getInt("month") >0){%>
									<br/>
									<%=episode.getString("year")%>年<%=episode.getString("month")%>月<%=episode.getString("day")%>日
								<%}%>
								</li>
								</ul>
							</div>
						
						<%
							item_count++;
							if (item_count==5) 
								break items;
						}
					}
				}
				%>
						<div class="clear"></div>
					</div><!--items end-->	
				</div><!--va end-->	
				<%
			}%>
<!--综艺结束-->


			<%if (hasDirect){%>
			</div><!--nonstop end-->
			<h3 class="label">以下为正常搜索结果：</h3>
			<%}%>
			

			<!--普通项-->
			<div class="items">
			<%
			
			JSONArray items = content.getVideos();
			for (int i = 0;i< items.length();i++)
			{
				JSONObject item = items.optJSONObject(i);
				String url = null;
				String title = null;
				String hltitle = null;
				String tags = null;
				String logo = null;
				
				url = item.getString("url");
				title = item.getString("title");
				hltitle = item.optString("title_hl");
				tags= item.optString("tags");
				logo = WebUtil.formatLogo(item.optString("logo"));
				String hltags = item.optString("tags_hl");
				if(hltitle == null || hltitle.isEmpty())hltitle = title;
			%>
				<div class="item">
					<ul>
					<li class="i_link"><a title="<%=title%>" href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',3,0,<%=i%>);"></a></li>
					<li class="i_thumb"><img src="<%=logo%>" alt="<%=title%>" /></li>
					<li class="i_status">
						<%if(item.optInt("hd")==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
						<div class="time"><%=WebUtil.getSecondAsString((float)item.getDouble("seconds"))%></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="<%=title%>" href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',3,0,<%=i%>);"><%=hltitle%></a></li>
					<li class="i_param"><span class="label">分类:</span> <%=WebUtil.formatTags(tags,hltags)%></li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;"><%=SiteManager.getInstance().getName(DataFormat.parseInt(item.getString("site")))%></span></li>
					</ul>
				</div>
				<%if ( (i+1)%5==0){%>
				<div class="clear"></div>	
				<%}%>
				<%}%>
				<div class="clear"></div>
			</div><!--items end-->
			
			
			
			<!--分页 两种-->
			<%if (param!=null){%>
			<div class="pages">
				<%=PageUtil.getContent(WebUtil.getPagePrefixString(param,request),total,param.page,pagesize)%>
			</div>
			<%}%>
			
			<%if (content.getLike_words()!=null){%>
			<div class="kRelated">
				<h3 class="label">相关搜索</h3>
				<ul class="keys">
					<%for (int i = 0;i<content.getLike_words().length();i++){
					String w = content.getLike_words().getString(i);
					%>
					<li><a href="/v?keyword=<%=MyUtil.urlEncode(w)%>"><%=w%></a></li>
					<%}%>
					</ul>
				<div class="clear"></div>
			</div>
			<%}%>
			
		</div><!--result end-->
	</div><!--soku_master end-->
	
	<SCRIPT LANGUAGE="JavaScript">library=<%=hasDirect?"1":"0"%>;result_count=<%=total%>;</SCRIPT>
	<%@ include file="/result/inc/footer.inc" %>
		
</div>
</div>
</div>

</body>
</html>
