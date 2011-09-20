<%@page import="java.text.DecimalFormat"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>

<%-- 特殊处理几个搜索词，为广告显示 “汽车“”恋爱“”功夫“ --%>
  <% List<String> lorealparis = new ArrayList<String>(); 
  lorealparis.add("汽车");
  lorealparis.add("恋爱");
  lorealparis.add("功夫");
  lorealparis.add("剑道");
  lorealparis.add("吴彦祖");
  lorealparis.add("斯诺克");
  boolean isLorealparis = lorealparis.contains(webParam.getQ());
  %>
  <%if(isLorealparis){ %>
 		<div id='ab_404'></div>
 	<%} %>
<%-- 特殊处理几个搜索词，为广告显示 “汽车“”恋爱“”功夫“ end     关联rightAd.jsp,如果是这几个搜索词，页面会出现两个id=ab_404的div，这个利用dom中，优先取前面的div，所以暂时不删除后面的那个div（因为这个是临时的） --%>
      		
<% if(!JSONUtil.isEmpty(cms) || !JSONUtil.isEmpty(recommendShows)) { %>
<div class="boxcommend">
	<h3>相关推荐</h3>
	<!--视频、节目、专题-->
	<div class="items">
		<% if(!JSONUtil.isEmpty(cms)) { %>
         <% for(int i = 0; i < cms.length(); i++) { %>
             <% JSONObject cmstopic = cms.optJSONObject(i); %>
             <% if(JSONUtil.isEmpty(cmstopic)) { continue; } %>
			<ul class="t">
				<li class="t_cover">
					<a href="<%= cmstopic.optString("cmsurl") %>" target="_blank">
						<img src="<%= WebUtils.eq(cmstopic.optString("thumburl"), "", CDN_SERVER + "/search/images/nopic_128x96.gif", cmstopic.optString("thumburl")) %>" title="<%= WebUtils.htmlEscape(cmstopic.optString("title")) %>" alt="<%= WebUtils.htmlEscape(cmstopic.optString("title")) %>" />
					</a>
				</li>
				<li class="t_title">
					<a href="<%= cmstopic.optString("cmsurl") %>" target="_blank" title="<%= WebUtils.htmlEscape(cmstopic.optString("title")) %>">[专题] <%= WebUtils.htmlEscape(cmstopic.optString("title", ""), 10, "...") %></a>
				</li>
				<li class="t_desc"><%= WebUtils.htmlEscape(cmstopic.optString("desc"), 30, "...") %></li>
			</ul>
		<% } %>
                                   
        <% } %>

		<% if(!JSONUtil.isEmpty(recommendShows)) { %>
          	<% for(int i = 0; i < recommendShows.length() && i<3; i++) { %>
               <% JSONObject show = recommendShows.optJSONObject(i); %>
               <% if(JSONUtil.isEmpty(show)) { continue; } %>
               <% String showCategory = show.optString("showcategory"); %>
               <%if("综艺".equals(showCategory)){ %>
		<ul class="v">
			<li class="v_link"><a href="http://www.youku.com/show_page/id_z<%= show.optString("showid") %>.html" target="_blank" title="<%= WebUtils.htmlEscape(show.optString("showname")) %>"></a></li>
			<li class="v_thumb"><img src="<%= show.optString("show_thumburl") %>" alt="<%= WebUtils.htmlEscape(show.optString("showname")) %>" /></li>
			<li class="v_time"><span class="num"></span><span class="bg"></span></li>
			<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
	       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
	 		<li class="v_ishd"><span class="ico__SD" title="超清"></span></li>
	       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
	       <li class="v_ishd"><span class="ico__HD" title="高清"></span></li>
	       <%} %>
				
			<li class="v_title"><a href="http://www.youku.com/show_page/id_z<%= show.optString("showid") %>.html" target="video" title="<%= WebUtils.htmlEscape(show.optString("showname")) %>"><%= WebUtils.htmlEscape(show.optString("showname")) %></a></li>
			<li class="v_stat"><label>播放:</label><span class="num"><%=show.optString("showtotal_vv") %></span></li>
		</ul>
			<%}else { %>
		<ul class="p">
			<li class="p_link"><a href="http://www.youku.com/show_page/id_z<%= show.optString("showid") %>.html" target="_blank" title="<%= WebUtils.htmlEscape(show.optString("showname")) %>"></a></li>
			<li class="p_thumb"><img src="<%= show.optString("show_thumburl") %>" alt="<%= WebUtils.htmlEscape(show.optString("showname")) %>" /></li>
			<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
	       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
	 		<li class="p_ishd"><span class="ico__SD" title="超清"></span></li>
	       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
	       <li class="p_ishd"><span class="ico__HD" title="高清"></span></li>
	       <%} %>			
			<li class="p_title"><a href="http://www.youku.com/show_page/id_z<%= show.optString("showid") %>.html" target="_blank" title="<%= WebUtils.htmlEscape(show.optString("showname")) %>"><%= WebUtils.htmlEscape(show.optString("showname")) %></a> [<%=show.optString("showcategory") %>]</li>
			<li class="p_rating"><div class="rating">
			<%double dRating = show.optDouble("reputation"); %>
				<%if(dRating<1) dRating=5; %>
				<%int fullNum = ((int)dRating)/2; %>
				<%boolean isPart = dRating>(fullNum*2); %>
				<%for(int j=1;j<=5;j++){ %>
				<%if(fullNum>=j){ %>
				<em class="ico__ratefull"></em>
				<%}else{ %>
					<%if(isPart){isPart=false; %>
						<em class="ico__ratepart"></em>
					<%}else{ %>
						<em class="ico__ratenull"></em>
					<%} %>
				<%} %>
				<%} %>
				<em class="num"><%=new  DecimalFormat("###.0").format(dRating) %></em>
			</div></li>
			<% JSONArray performer = show.optJSONArray("performer"); %>
             <% if(!JSONUtil.isEmpty(performer)) { %>
			<li class="p_actor">
				<% for(int j = 0; i < performer.length() && j < 3; j++) { %>
                           <% if (j>0) { %>/<% } %>
                           <%String performerStr = (performer.opt(j) instanceof JSONObject)?performer.optJSONObject(j).optString("name"):performer.optString(j); %>
                           <a href="/search_video/q_<%= WebUtils.urlEncode(performerStr) %>" target="_blank" title="<%= WebUtils.htmlEscape(performerStr) %>" charset="814-10-<%= i+1 %>"><%= WebUtils.htmlEscape(performerStr) %></a>
                       <% } %>
			</li>
			<% } %>
		</ul>
		<% } %>
		<% } %>
		<% } %>
			</div><!--items end-->
</div><!--recommend end-->

     <% } %>      