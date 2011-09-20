<%@page import="java.text.DecimalFormat"%>
<%@page import="com.youku.soku.util.MyUtil"%>
<%@page import="com.youku.soku.web.util.WebUtil"%>
<%@page import="com.youku.search.sort.servlet.search_page.util.MiscUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page import="org.json.*
				"%>
<%
JSONObject result = (JSONObject)request.getAttribute("result");
%>

<% String keyword = request.getParameter("keyword");
	if(keyword==null || keyword.length()==0){
		keyword = result.optString("showname");
	}
	
	keyword=WebUtil.formatHtml(keyword);
	
	String pidStr=request.getParameter("pid");
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.optString("showname")%> 搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css" />
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
</head>

<body>
<div class="window">
<div class="screen">

<%@ include file="inc/header.jsp" %>




<div class="body">
	<div class="typechk">
		<div class="main">
			<ul>
				<li><a href="/v?keyword=<%=MyUtil.urlEncode(keyword) %>&redirect=0&pid=<%=pidStr %>">视频</a></li>	
				<li><a href="/a?keyword=<%=MyUtil.urlEncode(keyword) %>&pid=<%=pidStr %>">专辑</a></li>	
				<!-- <li><a href="/k?keyword=<%=MyUtil.urlEncode(keyword) %>&pid=<%=pidStr %>">知识</a></li> -->	
				<li class="current" style="margin-left:10px;"><span>详情</span></li>	
			</ul>
		</div><!-- body main end -->
	</div><!-- typechk end -->
<div class="layout_21">	
	<div class="maincol">
	<div class="detailinfo">
		<ul class="base">
			<li class="base_name"><h1><%=result.optString("showname")%></h1></li>
			<li class="base_what"><%=result.optString("showcategory")%></li>
			<% String releaseYear = result.optString("releaseyear"); %>
			<li class="base_pub"><%=releaseYear.indexOf("0000")==0?"":releaseYear %></li>
			<li class="base_linkall"><a href="/v?keyword=<%=MyUtil.urlEncode(keyword) %>&redirect=0" target="_blank">查看全部结果&gt;&gt;</a></li>
		</ul><!--base end-->
		
		<%@ include file="inc/category.jsp" %>
	</div><!-- detailinfo end -->						

<!-- 
<div class="mbox">
	<div class="hd">
		<ul class="tb">
		<li class="current"><a href="#">节目预告</a></li>
		<li><a href="#">分集剧情</a></li>
		<li><a href="#">角色关系</a></li>
		</ul>
	</div>
	<div class="bd">
		<div class="textinfo">
			<ul>
				<li><label>安徽卫视：</label><span>每晚19:40 连播1集</span></li>
				<li><label>东方卫视：</label><span>每晚18:20 连播2集</span></li>
				<li><label>上海卫视：</label><span>每晚19:00 连播3集</span></li>
				<li><label>湖南卫视：</label><span>每晚14:20 连播1集</span></li>
				<li><label>重庆卫视：</label><span>每晚17:10 连播2集</span></li>
				<li class="fromyouku"><label>优酷电视：</label><span>更新2集</span></li>
			</ul>
			<div class="clear"></div>
		</div>
	</div>
</div>
 -->

<% boolean tabNotShow = JSONUtil.isEmpty(result,"资讯") && JSONUtil.isEmpty(result,"花絮") && JSONUtil.isEmpty(result,"MV"); %>
<% if(!tabNotShow){ %>
<div class="mbox otype">
<div class="hd">
	<ul class="tb">
	<% String selKey = ""; %>
	<%if(!JSONUtil.isEmpty(result,"资讯")){ %>
		<% selKey = "资讯"; %>
		<li class="current"><a href="javascript:;" >最新动态</a></li>
	<%} %>
	<%if(!JSONUtil.isEmpty(result,"花絮")){ %>
		<% if(selKey.length()==0){ selKey = "花絮"; %>
			<li class="current"><a href="javascript:;">幕后花絮</a></li>
		<%}else { %>
			<li><a href="javascript:;">幕后花絮</a></li>
		<%} %>
	<%} %>
	<%if(!JSONUtil.isEmpty(result,"MV")){ %>
		<% if(selKey.length()==0){  selKey = "MV"; %>
			<li class="current"><a href="javascript:;">音乐MV</a></li>
		<%}else { %>
			<li><a href="javascript:;">音乐MV</a></li>
		<%} %>
	<%} %>
	</ul>
</div><!-- hd end -->
<div class="bd">
	<%if(!JSONUtil.isEmpty(result,"资讯")){ %>
	<div class="collgrid4w" style="display:none;">
		<div class="items">
		<% JSONArray videos0 =  result.optJSONArray("资讯"); %>
		<% if(!JSONUtil.isEmpty(videos0)){ %>
			<%for(int i=0;i<videos0.length() && i<4;i++){ %>
				<%JSONObject v0 = videos0.optJSONObject(i); %>
				<ul class="v">
					<li class="v_link"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"></a></li>
					<li class="v_thumb"><img src="<%=v0.optString("thumburl") %>" alt="<%=v0.optString("title")%>" /></li>
					<% JSONArray streamtypes = v0.optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li class="v_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			<li class="v_time"><span class="num"><%=WebUtil.getSecondAsString((float)v0.getDouble("seconds")) %></span><span class="bg"></span></li>
					<li class="v_title"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"><%=v0.optString("title")%></a></li>
				</ul>
			<%} %>
		<%} %>
		<div class="clear"></div>
		</div><!--items end-->
	</div><!--coll end-->
	<%} %>
	<%if(!JSONUtil.isEmpty(result,"花絮")){ %>
	<div class="collgrid4w" style="display:none;">
		<div class="items">
		<% JSONArray videos0 =  result.optJSONArray("花絮"); %>
		<% if(!JSONUtil.isEmpty(videos0)){ %>
			<%for(int i=0;i<videos0.length() && i<4;i++){ %>
				<%JSONObject v0 = videos0.optJSONObject(i); %>
				<ul class="v">
					<li class="v_link"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"></a></li>
					<li class="v_thumb"><img src="<%=v0.optString("thumburl") %>" alt="<%=v0.optString("title")%>" /></li>
					<% JSONArray streamtypes = v0.optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li class="v_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			<li class="v_time"><span class="num"><%=WebUtil.getSecondAsString((float)v0.getDouble("seconds")) %></span><span class="bg"></span></li>
					<li class="v_title"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"><%=v0.optString("title")%></a></li>
				</ul>
			<%} %>
		<%} %>
		<div class="clear"></div>
		</div><!--items end-->
	</div><!--coll end-->
	<%} %>
	<%if(!JSONUtil.isEmpty(result,"MV")){ %>
	<div class="collgrid4w" style="display:none;">
		<div class="items">
		<% JSONArray videos0 =  result.optJSONArray("MV"); %>
		<% if(!JSONUtil.isEmpty(videos0)){ %>
			<%for(int i=0;i<videos0.length() && i<4;i++){ %>
				<%JSONObject v0 = videos0.optJSONObject(i); %>
				<ul class="v">
					<li class="v_link"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"></a></li>
					<li class="v_thumb"><img src="<%=v0.optString("thumburl") %>" alt="<%=v0.optString("title")%>" /></li>
					<% JSONArray streamtypes = v0.optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li class="v_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			<li class="v_time"><span class="num"><%=WebUtil.getSecondAsString((float)v0.getDouble("seconds")) %></span><span class="bg"></span></li>
					<li class="v_title"><a href="http://v.youku.com/v_show/id_<%=v0.optString("videoid") %>.html" target="_blank" title="<%=v0.optString("title")%>"><%=v0.optString("title")%></a></li>
				</ul>
			<%} %>
		<%} %>
		<div class="clear"></div>
		</div><!--items end-->
	</div><!--coll end-->
	<%} %>

</div><!-- bd end -->
</div> <!-- 最新动态/花絮/MV mbox end -->
<%} %>


<% boolean haveDouban = !JSONUtil.isEmpty(result,"douban"); %>
<% JSONObject doubanJson = result.optJSONObject("douban"); %>
<% if(haveDouban && !JSONUtil.isEmpty(doubanJson,"reviews") && !JSONUtil.isEmpty(doubanJson.optJSONObject("reviews"),"entry")){ /*现在只有豆瓣的评论，所有只有有豆瓣的，才显示评论*/%>
<div class="boxcomment">
<div class="mbox">
	<div class="hd">
		<label>剧评来自:</label>
		<ul class="tb">
		<li class="current"><a href="javascript:;"><img src="/img/partner/ico_dou_16x16.png" /></a></li>
	<!-- 	
		<li><a href="#"><img src="img/partner/ico_sina_16x16.png" /></a></li>
		<li><a href="#"><img src="img/partner/ico_Qzone_16x16.png" /></a></li>
		<li><a href="#"><img src="img/partner/ico_tecent_16x16.png" /></a></li>
	 -->
		</ul>
	</div>
	<div class="bd">
					<% JSONObject doubanReviesResult = doubanJson.optJSONObject("reviews"); %>
					<% if(!JSONUtil.isEmpty(doubanReviesResult)){ %>
						<% JSONArray doubanRevies = doubanReviesResult.optJSONArray("entry"); %>
						<% if(!JSONUtil.isEmpty(doubanRevies)){ %>	
							<!-- 豆瓣 评论 -->
							<div class="comments">
								<%for(int i=0;i<doubanRevies.length();i++){ %>
									<div class="comment">
										<div class="from">
										<span class="user"><a href="<%=doubanRevies.optJSONObject(i).optJSONObject("id").optString("$t").replace("api.douban.com","www.douban.com")%>/" target="_blank"><%=doubanRevies.optJSONObject(i).optJSONObject("title").optString("$t") %></a></span>
										<span class="date"><%=doubanRevies.optJSONObject(i).optJSONObject("updated").optString("$t").substring(0,10) %></span>
										</div>
										
										
										<div class="con">
											<%=doubanRevies.optJSONObject(i).optJSONObject("summary").optString("$t").replace("\r\n","<br/>").replace("...","<a href='"+doubanRevies.optJSONObject(i).optJSONObject("id").optString("$t").replace("api.douban.com","www.douban.com")+"/' target='_blank'>...</a>") %>	
										</div>
									</div>
								<%} %>
							</div> <!-- douban_reviews end -->
						<%} %>
					<%} %>
				
	</div>
</div>
</div><!-- boxcomment end -->
<%} %>
					
</div><!--maincol end-->



<div class="sidecol">
	<div class="boxrating">
		<h3>总评分</h3>
		<div class="fromyouku">
			
				
			<div class="rating_large">
				<%double dRating = result.optDouble("reputation"); %>
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
			</div>
		</div>
		<div class="fromother">
			<ul>
				<%if(haveDouban && !JSONUtil.isEmpty(doubanJson,"avg_rating")){ %>
				<li><span class="ico"><img title="豆瓣" src="/img/partner/ico_dou_16x16.png"/></span><span class="num"><%=result.optJSONObject("douban").optString("avg_rating") %></span></li>
				<%} %>
			</ul>
		</div>
		<div class="clear"></div>
		<% String rating = result.optString("rating"); %>
			<% int up= result.optInt("showtotal_up"); %>
			<% int down= result.optInt("showtotal_down"); %>
			<% JSONObject ratingArray = result.optJSONObject("rating_all"); %>
			<% if(ratingArray!=null && ratingArray.length()==5){up+=ratingArray.optInt("rating5");down+=ratingArray.optInt("rating1");}  %>
			<%if("u".equals(rating)){ %>
			<div class="join" sid="<%=result.optString("showid") %>">
			<div class="return">您已顶过, <label>顶:</label><span class="num"><%=up+1 %></span> <label>踩:</label><span class="num"><%=down %></span></div>
			</div>
			<%}else if("d".equals(rating)){ %>
			<div class="join" sid="<%=result.optString("showid") %>">
			<div class="return">您已踩过, <label>顶:</label><span class="num"><%=up %></span> <label>踩:</label><span class="num"><%=down+1 %></span></div>
			</div>
			<%}else{ %>
			<div class="join" sid="<%=result.optString("showid") %>">
					<div class="upup"><label>顶</label><span class="num"><%=up %></span><div class="ico"></div></div>
					<div class="down"><label>踩</label><span class="num"><%=down %></span><div class="ico"></div></div>
			</div>
			<%} %>
	</div>	<!-- boxrating end -->

<div class="boxactor">
	<% JSONArray actors = result.optJSONArray("actor"); %>
	<% if(!JSONUtil.isEmpty(actors)){ %>
		<h3><%=("综艺".equals(result.optString("showcategory")))?"主持人":"演员" %></h3>
		<%for(int i=0;i<actors.length();i++){ %>
			<% JSONObject actor = actors.getJSONObject(i); %>
			<% JSONArray videos = actor.optJSONArray("video"); %>
			<% if(!JSONUtil.isEmpty(videos)){ %>
			<ul class="actor">
				<li class="photo"><a href="/v?keyword=<%=MyUtil.urlEncode(actor.optString("name")) %>"><img src="<%=actor.optString("pic") %>" alt="<%=actor.optString("name") %>" /></a></li>
				<li class="name"><a href="/v?keyword=<%=MyUtil.urlEncode(actor.optString("name")) %>"><%=actor.optString("name") %></a></li>
				<li class="works">
					<ul>
						<% int cnt = 0; %>
						<%for(int j=0;j<videos.length();j++,cnt++){ %>
						<% String name =  videos.getJSONObject(j).optString("name"); %>
						<li><a href="/detail/show/<%= MyUtil.encodeVideoId(videos.getJSONObject(j).optInt("pid")) %>" title="<%=name %>" target="_blank"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a> [<%= videos.getJSONObject(j).optString("showcategory") %>]</li>
						<%} %>
						<%for(int j=cnt;j<3;j++){ %>
						<li></li>
						<%} %>
					</ul>
				</li>
			</ul>
			<%} %>
		<%} %>	
	<%} %>
</div>						

<div class="boxcommend">
	<h3>相关推荐</h3>
	<!--视频、节目、专题-->
		<%JSONArray recommends = result.optJSONArray("recommendShows"); %>
				<%if(!JSONUtil.isEmpty(recommends)){ %>
					<div class="items">
						<%for(int i=0;i<recommends.length();i++){ %>
						<%JSONObject recommend = recommends.optJSONObject(i); %>
							<ul class="p">
								<li class="p_link"><a href="/v?keyword=<%=MyUtil.urlEncode(recommend.optString("showname"))%>" target="_blank" title="<%=recommend.optString("showname")%>"></a></li>
								<li class="p_thumb"><img src="<%=recommend.optString("show_thumburl") %>" alt="<%=recommend.optString("showname")%>" /></li>
								<% String name = recommend.optString("showname"); %>
								<li class="p_title"><a href="/v?keyword=<%=MyUtil.urlEncode(recommend.optString("showname"))%>" target="_blank" title="<%=recommend.optString("showname")%>"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a> [<%=recommend.optString("showcategory") %>]</li>
								<li class="p_rating">
									<div class="rating">
										<%double dRating1 = recommend.optDouble("reputation"); %>
										<%if(dRating1<1) dRating1=5; %>
										<%int fullNum1 = ((int)dRating1)/2; %>
										<%boolean isPart1 = dRating1>(fullNum1*2); %>
										<%for(int j=1;j<=5;j++){ %>
										<%if(fullNum1>=j){ %>
										<em class="ico__ratefull"></em>
										<%}else{ %>
											<%if(isPart1){isPart1=false; %>
												<em class="ico__ratepart"></em>
											<%}else{ %>
												<em class="ico__ratenull"></em>
											<%} %>
										<%} %>
										<%} %>
										<em class="num"><%=new  DecimalFormat("###.0").format(dRating1) %></em>
									</div>
								</li>
								<li class="p_actor">
									<% String actor = "performer"; %>
									<% actor = "综艺".equals(recommend.optString("showcategory"))?"host":"performer"; %>
									<% JSONArray performers = recommend.optJSONArray(actor); %>
									<% if(!JSONUtil.isEmpty(performers)){ %>
											<%for(int j=0;j<performers.length()&& j<3;j++){ %>
												<% String performerName = performers.opt(j) instanceof JSONObject? ((JSONObject)performers.opt(j)).optString("name"):performers.optString(j);  %>
												<a href="/v?keyword=<%=MyUtil.urlEncode(performerName) %>" target="_blank"><%=performerName %></a>
											<%} %>
									<%} %>
								</li>
							</ul>
						<%} %>
					</div> <!-- items end -->
				<%} %>
</div><!--recommend end-->	
</div><!--sidecol end-->

<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->

<%@ include file="inc/footer.jsp" %>

</div><!--screen end-->
</div><!--widnow end-->

</body>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
</script>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript">
ViewBy.init();
PartPop.init();
Selector.init();
SpeedSelector.init();
PanelExpand.init();
FeedBack.init();
$(function() {
	var tabs1 = $(".otype .hd ul.tb li");	
	var tabsDiv1 = $(".otype .bd .collgrid4w");
	if(tabsDiv1 && tabsDiv1.length>0){
		$(tabsDiv1[0]).css("display","");
	}
	tabs1.click(
		function(){
			tabs1.removeClass("current");
			$(this).addClass("current");
			var i = $.inArray(this, tabs1);
			tabsDiv1.css("display","none");
			tabsDiv1.eq(i).css("display","");		
		}
	);
	
	
	//顶踩,未投票才能显示
	$(".join .upup").click(
	function(){
		$.ajax({
		   type: "POST",
		   url: "/detail/rating/"+$(".join").attr("sid"),
		   data: "rating=5",
		   success: function(msg){
		     if(msg.indexOf("ok")==0){
		    	 	var upupNum = parseInt($(".join .upup .num").html())+1;
				   var downNum = parseInt($(".join .down .num").html());
				   var div = $(".join .upup").parent();
				   if(div){
					   div.html('<div class="return">您已顶过, <label>顶:</label><span class="num">'+upupNum+'</span> <label>踩:</label><span class="num">'+downNum+'</span></div>');
				   }
		   	 }else {
		   		//alert( "Data Saved: " + msg );
		   	 }
		   }
		 });		
	});
	$(".join .down").click(
	function(){
		$.ajax({
		   type: "POST",
		   url: "/detail/rating/"+$(".join").attr("sid"),
		   data: "rating=1",
		   success: function(msg){
			   if(msg.indexOf("ok")==0){
				   var upupNum = parseInt($(".join .upup .num").html());
				   var downNum = parseInt($(".join .down .num").html())+1;
				   var div = $(".join .down").parent();
				   if(div){
					   div.html('<div class="return">您已踩过, <label>顶:</label><span class="num">'+upupNum+'</span> <label>踩:</label><span class="num">'+downNum+'</span></div>');
				   }
			   	 }else {
			   		//alert( "Data Saved: " + msg );
			   	 }
		   }
		 });		
	});
	
	
	$('.show_all').click(function() {
		  if($("#show_all_more").css("display")=="none"){
			  $("#show_all_more").css("display","");
		  }else {
			  $("#show_all_more").css("display","none");
		  }
		  
		  if('显示详情'==$('.show_all').html()){
			  $('.show_all').html("隐藏详情");
			  $('#show_all_more_dot').css("display","none");
		  }else {
			  $('.show_all').html("显示详情");
			  $('#show_all_more_dot').css("display","");
		  }
	});
	
	$('.intro').dblclick(function(){
		$('.show_all').click();
	});
});




//备份 选择站点的js
//var thislabel = $(this).find('label');
//var linkpanels = $('.linkpanels');
//$.each(linkpanels,function(){
//        $(this).css("display","none");
//});
//$('.'+thislabel.attr("id")).css("display","");
_glogParam.keyword='<%=MyUtil.urlEncode(keyword)%>';
library=1;result_count=1;
</script>

<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>
</html>
