<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.sort.BaseParameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				com.youku.soku.util.MyUtil,
				org.json.*,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager"%>
<%
BaseParameter param = (BaseParameter)request.getAttribute("parameter");
SearchResult result = (SearchResult)request.getAttribute("content");
int pagesize = Constant.Web.SEARCH_PAGESIZE;
String cost = result.getCost();
Content content = result.getContent();
int total = content.getTotal();

JSONArray items = content.getVideos();
JSONObject person = content.getPerson();
JSONArray cates = person.optJSONArray("cates");
JSONObject names = person.optJSONObject("names");

int current_cate = person.optInt("current_cate");
String encodeName = MyUtil.urlEncode(names.getString("name"));
String encodeKeyword = MyUtil.urlEncode(result.getKeyword());

JSONArray teleplays = person.optJSONArray("teleplay");
JSONArray movies = person.optJSONArray("movie");
JSONArray varietys = person.optJSONArray("variety");
if (teleplays!=null)total += teleplays.length();
if (movies!=null)total += movies.length();
if (varietys!=null)total += varietys.length();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.getKeyword()%> 搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="http://static.youku.com/js/prototype.js"></script>
<link rel="Shortcut Icon" href="http://static.youku.com/v1.0.0627/index/img/favicon.ico" />
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
				<li><span><a href="?keyword=<%=MyUtil.urlEncode(result.getKeyword())%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,0,0);">全部</a></span></li>
				<%
				JSONArray tabs = content.getTabs();
				if (tabs != null){
					if (tabs.length() == 1 && tabs.getInt(0) == 6){//人物
						if (person != null){
							
							for (int i = 0;i<cates.length() ;i++)
							{
								int cate_person = cates.getInt(i);
								if (current_cate == cate_person)
								{
										out.print("<li class='current'><span>");
										out.print(CateManager.getInstance().getName(cate_person));
										out.print("</span></li>");
								}
								else
								{
								%>
									<li><a href="/v?keyword=<%=encodeKeyword%>&ca=6&tca=<%=cate_person%>&na=<%=names.getInt("id")%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,<%=cate_person%>,<%=i%>);"><%=CateManager.getInstance().getName(cate_person)%></a></li>
								<%
								}
							}
					
						}
					}
					else
					{
						for (int i = 0;i< tabs.length();i++)
						{
							int cate = tabs.getInt(i);
							String cate_name = CateManager.getInstance().getName(cate);
							
							if (cate == 6){
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
				}
				%>
					
					
				</ul>
				
			</div>
			
			<div class="filter">
				<div class="statinfo">找到相关视频<%=total%>个</div>
				<%if (items != null){%>
				<div class="selector">
					
				</div>
					<%}%>
				<div class="clear"></div>	
			</div>
		</div>
		
		<div class="result">
			
			<%
			if (teleplays != null){
			%>
			<div class="guide">
				<h3 class="label">【<%=names.getString("name")%>演过的电视剧】</h3>
				
				<div class="clear"></div>
			</div>
			<!--人物>电视剧-->
			<div class="items">
			<%
			for (int i = 0;i<teleplays.length();i++){
				JSONObject teleplay = teleplays.getJSONObject(i);
				JSONObject oname = teleplay.getJSONObject("names_object");

				String title = oname.getString("name");
				String encodeTitle = MyUtil.urlEncode(title);
			%>
				<div class="item">
					<ul>
					<li class="i_link"><a title="<%=title%>" href="/v?keyword=<%=encodeTitle%>&ca=<%=current_cate%>&ver=<%=teleplay.getInt("id")%>" target="_blank" onclick="clicklog('/v?keyword=<%=encodeTitle%>&ca=<%=current_cate%>&ver=<%=teleplay.getInt("id")%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
					<li class="i_thumb"><img src="<%=WebUtil.formatLogo(teleplay.getString("logo"))%>" alt="<%=title%>" /></li>
					<li class="i_status">
						<%if(teleplay.optInt("hd")==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
						<%if (teleplay.optInt("completed")==1){%><div class="status"><%=teleplay.optString("episode_total")%>集全</div><%}%>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a href="/v?keyword=<%=encodeTitle%>&ca=<%=current_cate%>&ver=<%=teleplay.getInt("id")%>" target="_blank" onclick="clicklog('/v?keyword=<%=encodeTitle%>&ca=<%=current_cate%>&ver=<%=teleplay.getInt("id")%>','<%=encodeKeyword%>',2,0,<%=i%>);"><%=title%></a></li>
					<li class="i_param"><span class="label">地区:</span> <span><%=teleplay.optString("area")%></span></li>
					<li class="i_param"><span class="label">上映:</span> <span class="num"><%=teleplay.optString("release_time")%></span></li>
					<li class="i_param"><span class="label">主演:</span> 
					<%WebUtil.formatJSONArray(teleplay.getJSONArray("performer"),out);%>
					</li>
					<li class="i_param"><span class="label">类型:</span> <%WebUtil.formatJSONArray(teleplay.getJSONArray("cates"),out);%></li>
					</ul>
				</div>
				<%if ( (i+1)%5==0){%>
				<div class="clear"></div>	
				<%}%>
				<%}%>
				<div class="clear"></div>
			</div><!--items end-->
			<%}%>
	

			<%
			
			if (movies != null){
			%>
			<div class="guide">
				<h3 class="label">【<%=names.getString("name")%>演过的电影】</h3>
				
				<div class="clear"></div>
			</div>
			<div class="items">
			<%
				
			for (int i = 0;i<movies.length();i++){
				JSONObject movie = movies.getJSONObject(i);
				JSONObject oname = movie.getJSONObject("names_object");
				
				String view_url = movie.optString("view_url");
				if (view_url==null || view_url.isEmpty())
					continue;
				
//				String title = movie.optString("name");
//				if (title == null || title.isEmpty())
String			 title = oname.getString("name");

				String encodeTitle = MyUtil.urlEncode(title);
			%>
				<div class="item">
					<ul>
					<li class="i_link"><a title="<%=title%>" href="<%=view_url%>" target="_blank" onclick="clicklog('<%=view_url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
					<li class="i_thumb"><img src="<%=WebUtil.formatLogo(movie.getString("logo"))%>" alt="<%=title%>" /></li>
					<li class="i_status">
						<%if(movie.optInt("hd")==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
						<div class="time"><%=WebUtil.getSecondAsString((float)movie.optDouble("seconds"))%></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a href="<%=view_url%>" target="_blank" onclick="clicklog('<%=view_url%>','<%=encodeKeyword%>',2,0,<%=i%>);"><%=title%></a></li>
					<li class="i_param"><span class="label">地区:</span> <span><%=movie.optString("area")%></span></li>
					<li class="i_param"><span class="label">上映:</span> <span class="num"><%=movie.optString("release_time")%></span></li>
					<li class="i_param"><span class="label">主演:</span> 
					<%WebUtil.formatJSONArray(movie.getJSONArray("performer"),out);%>
					</li>
					<li class="i_param"><span class="label">类型:</span>
					<%WebUtil.formatJSONArray(movie.getJSONArray("cates"),out);%>
					</li>
					</ul>
				</div>
				<%if ( (i+1)%5==0){%>
				<div class="clear"></div>	
				<%}%>
				<%}%>
				<div class="clear"></div>
			</div><!--items end-->
			<%}%>
	

			<%
			
			if (varietys != null){
			%>
			<div class="guide">
				<h3 class="label">【<%=names.getString("name")%>的综艺节目】</h3>
				
				<div class="clear"></div>
			</div>
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
								<%if ( (j+1)%5==0){%>
								<div class="clear"></div>	
								<%}%>
								<%
							}
						}
						%>
			</div><!--items end-->
			<div class="clear"></div>	
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
	<SCRIPT LANGUAGE="JavaScript">library=1;result_count=<%=total%>;</SCRIPT>
	<%@ include file="/result/inc/footer.inc" %>
	
	
	
</div>
</div>
</div>

</body>
</html>
