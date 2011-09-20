<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.util.DataFormat,
				com.youku.soku.sort.BaseParameter,
				com.youku.soku.web.SearchResult,
				com.youku.soku.web.util.*,
				com.youku.soku.util.Constant,
				org.json.*,
				com.youku.soku.util.MyUtil,
				com.youku.soku.web.SearchResult.Content,
				com.youku.soku.web.db.CateManager,
				com.youku.soku.index.manager.db.SiteManager"%>
<%
BaseParameter param = (BaseParameter)request.getAttribute("parameter");
SearchResult result = (SearchResult)request.getAttribute("content");
int pagesize = Constant.Web.SEARCH_PAGESIZE;
String cost = result.getCost();
Content content = result.getContent();

JSONObject variety = content.getVariety();
JSONArray years = variety.optJSONArray("years");
int current_year_id = variety.getInt("current_year_id");
JSONObject totals = variety.optJSONObject("current_year_episode");
JSONObject names = variety.optJSONObject("names");
int cate_id = names.getInt("cate_id");

String encodeKeyword = MyUtil.urlEncode(result.getKeyword());
int total = 0;
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
				
				<li><span><a href="?keyword=<%=MyUtil.urlEncode(result.getKeyword())%>" onclick="clicklog(this.href,'<%=encodeKeyword%>',1,0,0);">全部</a></span></li>
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
				<div class="statinfo"></div>
				
				<div class="clear"></div>	
			</div>
		</div>
		<div class="result">
			
			<div class="guide">
				<ul class="section">
				<%
				String name = MyUtil.urlEncode(names.getString("name"));
				for (int i = 0;i<years.length() ;i++)
				{
					JSONObject year = years.getJSONObject(i);
					String year_name = year.getString("year");
					int year_id = year.getInt("id");

					if (year_id == current_year_id){
						%>
						<li class="current"><span class="highlight"><%=names.getString("name")%><%=year_name%></span></li>
						<%
					}
					else
					{
						%>
						<li><a href="/v?keyword=<%=MyUtil.urlEncode(names.getString("name"))%>&ver=<%=year_id%>&ca=<%=cate_id%>"  onclick="clicklog(this.href,'<%=encodeKeyword%>',4,3,<%=i+1%>);"><%=year_name%></a></li>
						<%
					}
				}%>
				</ul>
				<div class="clear"></div>
			</div>

			
			<div class="queue">
			<%
			Iterator it = totals.keys();
			
			while (it.hasNext())
			{
				String year = (String)it.next();
				%>
				<h3 class="label"><%=DataFormat.parseInt(year)>0?year+"月":""%></h3>	
				<div class="items">
					<%
					int row = 0;
						JSONArray episodes = totals.getJSONArray(year);

						total += episodes.length();

						for (int i = 0;i<episodes.length();i++){
							row++;	//用来排版的行数
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
							<%if(hd==1){%> <div class="HD" title="高清"><em>高清</em></div><%}%>
							<div class="time"><%=WebUtil.getSecondAsString((float)episode.optDouble("seconds"))%></div>
							<div class="bg"></div>
						</li>
						<li class="i_title"><a href="<%=url%>" target="_blank" onclick="clicklog('<%=url%>','<%=encodeKeyword%>',2,0,<%=i%>);"><%=title%></a></li>
						<%if (episode.getInt("month") >0){%>
							<li class="i_date"><span><%=episode.optString("year")%>-<%=episode.optString("month")%>-<%=episode.optString("day")%></span></li>
						<%}%>
						</ul>
					</div>
					<%if ( row%5==0){%>
					<div class="clear"></div>	
					<%}%>
					<%}%>
				</div><!--items end-->
				<div class="clear"></div>
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
	<SCRIPT LANGUAGE="JavaScript">library=1;result_count=<%=total%>;</SCRIPT>
	<%@ include file="/result/inc/footer.inc" %>
	
</div>
</div>
</div>

	
</body>
</html>
