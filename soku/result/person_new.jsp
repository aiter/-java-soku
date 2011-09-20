<%@page import="com.youku.soku.manage.util.ImageUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.youku.soku.util.MyUtil"%>
<%@page import="com.youku.soku.web.util.WebUtil"%>
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
		keyword = result.optString("name");
	}
	
	keyword=WebUtil.formatHtml(keyword);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.optString("name")%> 搜库 视频搜索</title>
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
			    <% String personName=request.getParameter("pid");%>
				<li><a href="/v?keyword=<%=MyUtil.urlEncode(keyword) %>&redirect=0&personName=<%=MyUtil.urlEncode(personName)%>">视频</a></li>	
				<li><a href="/a?keyword=<%=MyUtil.urlEncode(keyword) %>&personName=<%=MyUtil.urlEncode(personName)%>">专辑</a></li>	
				<!-- <li><a href="/k?keyword=<%=MyUtil.urlEncode(keyword) %>&personName=<%=personName%>">知识</a></li> -->	
				<li class="current" style="margin-left:10px;"><span>详情</span></li>	
			</ul>
		</div><!-- body main end -->
	</div><!-- typechk end -->
	
	
	<div class="layout_21">	
		<div class="maincol">
			<div class="detailinfo">
				<ul class="base">
					<li class="base_name"><h1><%=result.optString("name")%></h1></li>
					<li class="base_linkall"><a href="/v?keyword=<%=MyUtil.urlEncode(keyword) %>&redirect=0" target="_blank">查看全部结果&gt;&gt;</a></li>
				</ul><!--base end-->
				
				<% JSONObject detailObject = result.optJSONObject("detail"); %>
				<% if(!JSONUtil.isEmpty(detailObject)){ %>
				<div class="detail">
					<div class="G">
						<% String thumburl = detailObject.optString("thumburl"); %>
						<% if(ImageUtil.isYoukuImage(detailObject.optString("thumburl"))){ %>
						<% thumburl = thumburl.replace("ykimg.com/0","ykimg.com/1");%>
						<%} %>
						<%if(thumburl==null ||thumburl.length()==0){ %>
						<% 		thumburl= ImageUtil.getDisplayUrl("0900641F46497500B200000000000000000000-0000-0000-0000-0000F74CBDED"); %>
						<%}%>
						<div class="photo"><img src="<%=thumburl %>"/></div>
					</div><!--G end-->
					<div class="T">
						<!--
						.long	宽
						.short	窄
						.cross	通
						-->
						<ul class="params">
							<li class="short"><label>性别:</label><span><%="F".equalsIgnoreCase(detailObject.optString("gender"))?"女":("M".equalsIgnoreCase(detailObject.optString("gender"))?"男":"") %></span></li>
							<li class="long"><label>国籍/地区:</label><span><%=JSONUtil.array2String(detailObject.optJSONArray("nationality")," ","未知") %></span></li>
							<li class="short"><label>血型:</label><span><%=detailObject.optString("bloodtype") %>型</span></li>
							<li class="long"><label>职业:</label><span><%=JSONUtil.array2String(detailObject.optJSONArray("occupation")," ","未知") %></span></li>
							<% String personHeight =detailObject.optString("height");  %>
							<li class="short"><label>身高:</label><span><%=(personHeight.length()<2)?"未知":personHeight %></span></li>
							<% String personBirthday =detailObject.optString("birthday");  %>
							<li class="long"><label>生日:</label><span><%=(personBirthday.length()==0 || personBirthday.indexOf("0000")==0)?"未知":personBirthday %></span></li>
							<li class="short"><label>别名:</label><span><%=JSONUtil.array2String(detailObject.optJSONArray("personalias"),"/","未知") %></span></li>
							<% String personHomeplace =detailObject.optString("homeplace");  %>
							<li class="long"><label>出生地:</label><span><%=(personHomeplace.length()==0)?"未知":personHomeplace %></span></li>
						</ul>
						<div class="clear"></div>
						<% 
							int defaultSize = 350;
							String personDesc =detailObject.optString("persondesc").replace("\r\n","<br/>");
							if("null".equals(personDesc)){
								personDesc="";
							}
							String partOne = personDesc;
							String pertTwo = "";
							if(personDesc.length()>defaultSize){
								int brBegin = personDesc.indexOf("<br/>",defaultSize-"<br/>".length());
								if(brBegin<=0 || brBegin>defaultSize){
								}else {
									defaultSize=brBegin;
								}
								partOne=personDesc.substring(0,defaultSize);
								pertTwo=personDesc.substring(defaultSize);
							}
						%>
						<div class="intro"><%=partOne %>
						<%if(pertTwo.length()>0){ %><span id="show_all_more_dot">...</span>
						<span id="show_all_more" style="display:none;">
						<%=pertTwo %>
						</span>
						<a href="javascript:;" class="show_all">显示详情</a>
						<%} %>
						</div>		
					</div><!--T end-->
					<div class="clear"></div>
				</div><!--detail end-->	
				<%} %>
			</div>						
	
	
			<% JSONArray movies = result.optJSONArray("MOVIE"); %>
			<% JSONArray teleplaies = result.optJSONArray("TELEPLAY"); %>
			<% JSONArray zys = result.optJSONArray("VARIETY"); %>
			
			<% 
			Comparator<JSONObject> showComparator = new Comparator<JSONObject>(){
				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					if(JSONUtil.isEmpty(o1) && JSONUtil.isEmpty(o2)){
						return 0;
					}
					if(JSONUtil.isEmpty(o1)){
						return 1;
					}
					if(JSONUtil.isEmpty(o2)){
						return -1;
					}
					int releaseyear1 = o1.optInt("releaseyear");
					int releaseyear2 = o2.optInt("releaseyear");
						
					return releaseyear1==releaseyear2?0:releaseyear2-releaseyear1;
				}
			};
				List<JSONObject> shows = new ArrayList<JSONObject>();
				if(!JSONUtil.isEmpty(movies)){
					for(int i=0;i<movies.length();i++){
						JSONObject show = movies.optJSONObject(i);
						if(JSONUtil.isEmpty(show)){
							continue;
						}
						try{
							show.put("showcategory","电影");
						}catch(Exception e){
						}
						shows.add(show);
					}
				}
				if(!JSONUtil.isEmpty(teleplaies)){
					for(int i=0;i<teleplaies.length();i++){
						JSONObject show = teleplaies.optJSONObject(i);
						if(JSONUtil.isEmpty(show)){
							continue;
						}
						try{
							show.put("showcategory","电视剧");
						}catch(Exception e){
						}
						shows.add(show);
					}
				}
				if(!JSONUtil.isEmpty(zys)){
					for(int i=0;i<zys.length();i++){
						JSONObject show = zys.optJSONObject(i);
						if(JSONUtil.isEmpty(show)){
							continue;
						}
						try{
							show.put("showcategory","综艺");
						}catch(Exception e){
						}
						shows.add(show);
					}
				}
				if(!shows.isEmpty()){
					Collections.sort(shows,showComparator);
				}
			%>
			<div class="boxwork">
				<div class="mbox">
					<%if(!JSONUtil.isEmpty(movies) || !JSONUtil.isEmpty(teleplaies) || !JSONUtil.isEmpty(zys)){ %>
					<div class="hd">
						<ul class="tb">
						<li class="current"><a href="javascript:;">全部</a></li>
						<%if(!JSONUtil.isEmpty(movies)){ %>
						<li><a href="javascript:;">电影</a></li>
						<%} %>
						<%if(!JSONUtil.isEmpty(teleplaies)){ %>
						<li><a href="javascript:;">电视剧</a></li>
						<%} %>
						<%if(!JSONUtil.isEmpty(zys)){ %>
						<li><a href="javascript:;">综艺</a></li>
						<%} %>
						</ul>
					</div>
					<%} %>
					<div class="bd">
						
						<%if(!JSONUtil.isEmpty(movies) || !JSONUtil.isEmpty(teleplaies) || !JSONUtil.isEmpty(zys)){ %>
						<div class="workscoll worksall" >
							<div class="works">
								<%if(shows.size()>0){ %>
								<% for(Iterator iter =shows.iterator();iter.hasNext();){ %>
								<% JSONObject show = (JSONObject)iter.next(); %>
								<% String name = show.optString("name"); %>
								<ul class="p">
									<li class="p_link"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="1" target="_blank" title="<%=name %>"></a></li>
									<li class="p_thumb"><img src="<%=show.optString("pic") %>" alt="<%=name %>" /></li>
									<% boolean needBg = false; %>
									<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
									<% String stype = ""; %>
									<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
									<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
									<% String displayStatus = show.optString("display_status");  %>
									<% if("电影".equals(show.optString("showcategory"))){ %>
									<% displayStatus = WebUtil.getSecondAsString((float)show.getDouble("seconds")); %>
									<%} %>
									<% needBg = (stype.length()>0 || displayStatus.length()>0); %>
									<% if(needBg){ %>
									<li class="p_status"><span class="status"><%=displayStatus %></span><span class="bg"></span></li>
									<%} %>
									<li class="p_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			
									<li class="p_title"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>" onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="2" target="_blank" title="<%=name %>"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a></li>
									<li class="p_typepub"><%=show.optString("releaseyear") %> <%=show.optString("showcategory") %></li>
									<li class="p_rating"><div class="rating">
										<%double dRating1 = show.optDouble("score"); %>
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
										</div></li>
										<%if(show.optString("url").length()==0){ %>
											<li class="p_play"><div class="btnplay"><a href="javascript:;" target="_blank">播放</a></div></li>
										<%}else{ %>
											<li class="p_play"><div class="btnplay"><a href="<%=show.optString("url") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="5" target="_blank">播放</a></div></li>
										<%} %>
								</ul>
								<%} %>
								<%} %>
							</div><!--works end-->
						</div><!--coll end-->
						<%}/** 全部完*/ %>
						<%if(!JSONUtil.isEmpty(movies)){ %>
						<div class="workscoll worksmovie" style="display:none;">
							<div class="works">
								<% for(int i=0;i<movies.length();i++){ %>
								<% JSONObject show = movies.getJSONObject(i); %>
								<% if(JSONUtil.isEmpty(show))continue; %>
								<% String name = show.optString("name"); %>
								<ul class="p">
									<li class="p_link"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="1" target="_blank" title="<%=name %>"></a></li>
									<li class="p_thumb"><img src="<%=show.optString("pic") %>" alt="<%=name %>" /></li>
									<% boolean needBg = false; %>
									<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
									<% String stype = ""; %>
									<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
									<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
									<% String displayStatus = WebUtil.getSecondAsString((float)show.getDouble("seconds"));  %>
									<% needBg = (stype.length()>0 || displayStatus.length()>0); %>
									<% if(needBg){ %>
									<li class="p_status"><span class="status"><%=displayStatus %></span><span class="bg"></span></li>
									<%} %>
									<li class="p_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			
									<li class="p_title"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="2" target="_blank" title="<%=name %>"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a></li>
									<li class="p_typepub"><%=show.optString("releaseyear") %> 电影</li>
									<li class="p_rating"><div class="rating">
										<%double dRating1 = show.optDouble("score"); %>
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
										</div></li>
									<li class="p_play"><div class="btnplay"><a href="<%=show.optString("url") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="5" target="_blank">播放</a></div></li>
								</ul>
								<%} %>
							</div><!--works end-->
						</div><!--coll end-->
						<%}/** 电影完*/ %>
						<%if(!JSONUtil.isEmpty(teleplaies)){ %>
						<div class="workscoll workstv" style="display:none;">
							<div class="works">
								<% for(int i=0;i<teleplaies.length();i++){ %>
								<% JSONObject show = teleplaies.getJSONObject(i); %>
								<% if(JSONUtil.isEmpty(show))continue; %>
								<% String name = show.optString("name"); %>
								<ul class="p">
									<li class="p_link"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="1" target="_blank" title="<%=name %>"></a></li>
									<li class="p_thumb"><img src="<%=show.optString("pic") %>" alt="<%=name %>" /></li>
									<% boolean needBg = false; %>
									<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
									<% String stype = ""; %>
									<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
									<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
									<% String displayStatus = show.optString("display_status");  %>
									<% needBg = (stype.length()>0 || displayStatus.length()>0); %>
									<% if(needBg){ %>
									<li class="p_status"><span class="status"><%=displayStatus %></span><span class="bg"></span></li>
									<%} %>
									<li class="p_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			
									<li class="p_title"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="2" target="_blank" title="<%=name %>"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a></li>
									<li class="p_typepub"><%=show.optString("releaseyear") %> 电视剧</li>
									<li class="p_rating"><div class="rating">
										<%double dRating1 = show.optDouble("score"); %>
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
										</div></li>
									<li class="p_play"><div class="btnplay"><a href="<%=show.optString("url") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="5" target="_blank">播放</a></div></li>
								</ul>
								<%} %>
							</div><!--works end-->
						</div><!--coll end-->
						<%}/** 电视剧完*/ %>
						<%if(!JSONUtil.isEmpty(zys)){ %>
						<div class="workscoll workszy" style="display:none;">
							<div class="works">
								<% for(int i=0;i<zys.length();i++){ %>
								<% JSONObject show = zys.getJSONObject(i); %>
								<% if(JSONUtil.isEmpty(show))continue; %>
								<% String name = show.optString("name"); %>
								<ul class="p">
									<li class="p_link"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="1" target="_blank" title="<%=name %>"></a></li>
									<li class="p_thumb"><img src="<%=show.optString("pic") %>" alt="<%=name %>" /></li>
									<% boolean needBg = false; %>
									<% JSONArray streamtypes = show.optJSONArray("streamtypes"); %>
									<% String stype = ""; %>
									<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
									<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
									<% String displayStatus = show.optString("display_status");  %>
									<% needBg = (stype.length()>0 || displayStatus.length()>0); %>
									<% if(needBg){ %>
									<li class="p_status"><span class="status"><%=displayStatus %></span><span class="bg"></span></li>
									<%} %>
									<li class="p_ishd"><span class="ico__<%=stype %>" title="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"></span></li>			
									<li class="p_title"><a href="/detail/show/<%=MyUtil.encodeVideoId(show.optInt("pid")) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="2" target="_blank" title="<%=name %>"><%=name.length()>9?WebUtil.subString(name,0,9)+"...":name %></a></li>
									<li class="p_typepub"><%=show.optString("releaseyear") %> 综艺</li>
									<li class="p_rating"><div class="rating">
										<%double dRating1 = show.optDouble("score"); %>
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
										</div></li>
									<li class="p_play"><div class="btnplay"><a href="<%=show.optString("url") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="6" _log_directpos="5" target="_blank">播放</a></div></li>
								</ul>
								<%} %>
							</div><!--works end-->
						</div><!--coll end-->
						<%}/** 综艺完*/ %>
					</div><!-- bd end -->
				</div><!-- mbox end -->
			</div><!-- boxwork end -->			
						
		</div><!--maincol end-->
		
	<div class="clear"></div>
	</div><!--layout_21 end-->
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
_glogParam.keyword='<%=MyUtil.urlEncode(keyword)%>';
$(function() {
	
	var tabs1 = $(".boxwork .mbox .hd ul.tb li");	
	var tabsDiv1 = $(".boxwork .mbox .bd .workscoll");
	if(tabsDiv1 && tabsDiv1.length>0){
		$(tabsDiv1[0]).css("display","");
	}
	tabs1.click(function(){
		tabs1.removeClass("current");
		$(this).addClass("current");
		var i = $.inArray(this, tabs1);
		tabsDiv1.css("display","none");
		tabsDiv1.eq(i).css("display","");		
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
</script>

<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>
</html>
