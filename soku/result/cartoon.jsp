<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.sort.BaseParameter,
				com.youku.soku.sort.Parameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				org.json.*,
				com.youku.soku.util.MyUtil,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager,
				com.youku.soku.index.manager.db.SiteManager"%>
<%
SearchResult result = (SearchResult)request.getAttribute("content");
int pagesize = Constant.Web.SEARCH_PAGESIZE;
String cost = result.getCost();
Content content = result.getContent();

JSONObject cartoon = content.getCartoon();
JSONArray version_names = cartoon.optJSONArray("version_names");
int current_version_id = cartoon.getInt("current_version_id");
JSONArray episodes = cartoon.optJSONArray("current_site_episode");
JSONObject names = cartoon.optJSONObject("names");
JSONArray sites = cartoon.optJSONArray("current_sites");
int current_site_id = cartoon.getInt("current_site_id");

int cate_id = names.getInt("cate_id");
String encodeName = MyUtil.urlEncode(names.getString("name"));
String encodeKeyword = MyUtil.urlEncode(result.getKeyword());

JSONObject version = cartoon.optJSONObject("current_version");
int completed = version.optInt("released");

int episode_order= cartoon.optInt("episode_order");

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
						
						if (cate == cate_id){
							out.print("<li class='current'><span>");
							out.print(cate_name);
							out.print("</span></li>");
						}
						else{
							out.print("<li><a href=\"/v?keyword=");
							out.print( encodeKeyword);
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
				<div class="statinfo"></div>
				<%if (sites.length()>1){%>
					<div class="selector">

					<form name="filter_form" action="<%=Constant.Web.SEARCH_URL%>" method="get">
					<input type="hidden" name="keyword" value="<%=result.getKeyword()%>">
					<input type="hidden" name="ca" value="<%=cate_id%>">
					<input type="hidden" name="ver" value="<%=version.optInt("id")%>">
					<span class="label">视频筛选:</span>
						
						<select class="sel" name="sv" onchange="filter_form.submit();">
						
						<%for (int i=0;i<sites.length();i++){
							JSONObject site = sites.getJSONObject(i);
							%>
							<option value="<%=site.getInt("id")%>"<%=current_site_id == site.getInt("id")?" selected":""%>><%=SiteManager.getInstance().getName(site.getInt("source_site"))%></option>
						<%}%>
						</select>
						
						</form>
						</div>
				<%}%>
				<div class="clear"></div>	
			</div>
		</div>
		<div class="result">
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
							%><li class="current"><span class="highlight"><%=names.getString("name")%></span><%=versinname%></li><%
						}
						else
						{
							%>
							<li><a href="/v?keyword=<%=encodeName%>&ver=<%=version_id%>&ca=<%=cate_id%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',4,5,<%=i+1%>);"><%=versinname%></a></li>
							<%
						}
					}%>
					</ul>
					
					<div class="clear"></div>
				</div>
			</div>
	
			<div class="queque">
			
				<div class="guide">
					<h3>剧集列表</h3>
					<%
					int times = 1;
					if (episodes.length() > 60){
						times = (episodes.length()+59)/60;
					%>
					<ul>
					<%
						int start,end;
						for (int i = 0;i<times;i++){
						 
							 if(completed==1){
								 start = i*60;
								 end = Math.min((i+1)*60-1,episodes.length()-1);
							 }
							 else
							 {
								 end = i*60;
								 start = Math.min((i+1)*60-1,episodes.length()-1);
							 }

							JSONObject startO = episodes.getJSONObject(start);
							JSONObject endO = episodes.getJSONObject(end);
						%>
							<li id="index_<%=i%>" <%=i==0?"class=\"current\"":""%> onclick="javascript:showEpisode(<%=i%>,<%=times%>);"><span><a>第<%=startO.optString("order_id")%>-<%=endO.optString("order_id")%>集</a></span> <span class="break">|</span></li>
						<%}%>
					</ul>
					<%}%>
					<div class="clear"></div>
				</div>
				
				<%
				for (int m =0;m<times ;m++)
				{
					int end = Math.min((m+1)*60,episodes.length());
				%>
				<div class="items" id="items<%=m%>"<%=m!=0?"style='display:none'":""%>>
					<%for (int i = m*60;i<end;i++){
						JSONObject episode = episodes.getJSONObject(i);
						String title = episode.getString("title");
						String url = episode.getString("url");
						int hd = episode.getInt("hd");
						int id = episode.getInt("id");
						int order = episode.getInt("order_id");
						int start = 0;
						if (episode_order == 1){
							if(i > 20) start = i-20;
						}
						else if (episode_order == 2){
								start =  episodes.length()-i-20;
								if (start <0) start=0;
						}
					%>
					
					<div class="item">
						<ul>
						
						<li class="i_link"><a title="<%=title%>" href="javascript:void(0);"  onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=id%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"></a></li>
						<li class="i_thumb"><img src="<%=WebUtil.formatLogo(episode.getString("logo"))%>" alt="<%=title%>" /></li>
						<li class="i_status">
							<%if(hd==1){%><div class="HD" title="高清"><em>高清</em></div><%}%>
							<div class="time"><%=WebUtil.getSecondAsString((float)episode.getDouble("seconds"))%></div>
							<div class="bg"></div>
						</li>
						<li class="i_title"><a href="javascript:void(0);"  onclick="openVideo('/p?ca=<%=cate_id%>&ep=<%=id%>&st=<%=start%>&lm=40','<%=url%>');clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"> 第<%=episode.getString("order_id")%>集 </a></li>						
						</ul>
					</div>
					<%if ( (i+1)%5==0){%>
					<div class="clear"></div>	
					<%}%>
					<%}%>
					<div class="clear"></div>
				</div><!--items end-->
				<%}%>
			</div>
			
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
	
	<SCRIPT LANGUAGE="JavaScript">library=1;result_count=<%=episodes.length()%>;</SCRIPT>
	<%@ include file="/result/inc/footer.inc" %>
	

	
</div>
</div>
</div>

	
</body>
</html>
