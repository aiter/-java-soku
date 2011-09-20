<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="com.youku.soku.util.MyUtil"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.youku.search.sort.servlet.util.WebUtils"%>
<%@page import="com.youku.search.sort.servlet.search_page.WebParam"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%! String getUrl(String showid,String pid,String showIdPre,String pidPre){
		if(showid!=null && StringUtils.trimToEmpty(showid).length()>0){
			return showIdPre+showid+".html";
		}else{
			return pidPre+com.youku.search.util.MyUtil.encodeVideoId(new Integer(pid).intValue());
		}
	
}

String getPic(String vthumburl,String thumburl){
	if(StringUtils.trimToEmpty(vthumburl).length()>0  && !vthumburl.endsWith("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")){
		return StringUtils.trimToEmpty(vthumburl);
	}else if(StringUtils.trimToEmpty(thumburl).length()>0 && !thumburl.endsWith("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80")){
		return StringUtils.trimToEmpty(thumburl);
	}else{//竖版soku无截图http://g1.ykimg.com/0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E
		return "";
	}
}

String getPicCss(String vthumburl,String thumburl){
	if(StringUtils.trimToEmpty(vthumburl).length()>0  && !vthumburl.endsWith("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")){
		return "";
	}else {
		return "horiz";
	}
	
}

String getItemCss(String vthumburl,String thumburl){
	if(StringUtils.trimToEmpty(vthumburl).length()>0 && !vthumburl.endsWith("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")){
		return "";
	}else if(StringUtils.trimToEmpty(thumburl).length()>0 && !thumburl.endsWith("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80")){
		return "";
	}else{
		return "item_thumbnull";
	}

}


String formatVideoStage(String videoseq ,String videoStageStr){
	String returnStr="";
	if(videoStageStr.length()==0||"0".equals(videoStageStr)){
		returnStr=videoseq;
	}else{
		returnStr=videoStageStr;
	}
	
	try{
		 SimpleDateFormat fromSdf=new SimpleDateFormat("yyyyMMdd");
		 SimpleDateFormat toSdf=new SimpleDateFormat("yyyy-MM-dd");
		 Date fromDate=fromSdf.parse(new Integer(returnStr).toString());
		 returnStr=toSdf.format(fromDate);
		 }catch(Exception e){
	         System.out.print(e.getMessage());
	        
		 }
	return returnStr;
}
%>
<% WebParam webParam_inner = webParam;
   int DESCLENGTH=77;
   int MOVIELENGTH=146;
   int TVLENGTH=100;
   int VARILENGTH=65;%>
<% JSONObject result_inner = result; %>
                    
<% { %>
<% JSONObject person_info = JSONUtil.getProperty(result_inner, JSONObject.class, "person_info"); %>
<% if(!JSONUtil.isEmpty(person_info)) { %>
    <% String pk_odperson = person_info.optString("pk_odperson"); %>
    <% if(pk_odperson.length() > 0) { %>
        <script language="javascript">var search_personid = <%= pk_odperson %>;</script>
    <% } %>
<% } %>
<% } %>

<% { %>
<% String search_showids = JSONUtil.getProperty(result_inner, String.class, "search_showids"); %>
<% if(search_showids != null && search_showids.length() > 0) {%>
    <script language="javascript">var search_showids='<%= search_showids %>';</script>
<% } %>
<% } %>

<div class="direct">

<% JSONArray odshows = result_inner.optJSONArray("odshows"); %>
<% JSONArray personOdshows = result_inner.optJSONArray("person_odshows"); %>
<% int directCount=0; %>
<% Set<Integer> showsIdSet = new HashSet<Integer>(); %>  
<% if(!JSONUtil.isEmpty(odshows)){ %>
       <% for(int i=0;i<odshows.length() && directCount<10;i++){ %>
       	<% JSONObject odshow = odshows.optJSONObject(i); %>
       	<% int showId = odshow.optInt("pk_odshow");
       	   if((odshow.optJSONArray("videos")==null || odshow.optJSONArray("videos").length()==0) && (odshow.optJSONArray("remains")==null || odshow.optJSONArray("remains").length()==0)) continue;%>
       	<% if(showsIdSet.contains(showId)){continue;}else{showsIdSet.add(showId);} %>
       	<% String showcategory = odshow.optString("showcategory");
       		directCount++;%>
       		<%if("综艺".equalsIgnoreCase(showcategory)){ %>
       				<!--综艺-->
						<div class="item <%=getItemCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl"))%>">
							<div class="zy">
								<ul class="base">
									<li class="base_name"><h1><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank"><%=odshow.optString("highLightName","").length()>0?(odshow.optString("highLightName")+(odshow.optString("aliasName","").length()>0?"("+odshow.optString("aliasName")+")":"")):odshow.optString("showname") %></a></h1></li>
									<li class="base_pub"><%=odshow.optString("releaseyear").equals("0000")?"":odshow.optString("releaseyear")%></li>
									<% JSONArray stationArray = odshow.optJSONArray("station"); %>
									<% if(JSONUtil.isEmpty(stationArray)){ stationArray = odshow.optJSONArray("tv_station");} %>
									<% String station = ""; %>
									<% Object stationObj = null; %>
									<% if(!JSONUtil.isEmpty(stationArray)){ %>
										<% stationObj = stationArray.opt(0); %>
										<% station = (stationObj instanceof JSONObject)?((JSONObject)stationObj).optString("name"):stationObj.toString(); %>
									<%} %>
									<li class="base_what"><%=station%>&nbsp;&nbsp;</li>
									<li class="base_rating"><div class="rating">
									<%double dRating = odshow.optDouble("reputation"); %>
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
								</ul><!--base end-->
								<div class="detail">
								<%String pic=getPic(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl")); %>
								<%if(pic.length()>0){ %>
									<div class="G">
										<ul class="p pv">
											<li class="p_link"><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank" title="<%=odshow.optString("showname") %>"></a></li>
											<li class="p_thumb"><img class="<%=getPicCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl"))%>"  src="<%=pic%>" alt="<%=odshow.optString("showname") %>" /></li>
											<li class="p_status"><span class="status"><%=odshow.optString("display_status") %></span><span class="bg"></span></li>
											<% if(odshow.optInt("paid")==1){ %>
											<li class="p_ispaid"></li>		
											<%} %>
											<% JSONArray streamtypes = odshow.optJSONArray("streamtypes"); %>
									       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
									 		<li class="p_ishd"><span class="ico__SD" title="超清"></span></li>
									       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
									       <li class="p_ishd"><span class="ico__HD" title="高清"></span></li>
									       <%} %>
										</ul>
										<div class="clear"></div>
									</div><!--G end-->
								<%} %>
									<div class="T">
										<ul class="params">
											<li class="long"><label>主持人:</label>
											<%JSONArray personArr = odshow.optJSONArray("host"); %>
											<%if(!JSONUtil.isEmpty(personArr)){ %><span>
												<%for(int k=0;k<personArr.length()&& k<6;k++){ %>
													 <a href="/search_video/q_<%=WebUtils.urlEncode(personArr.optJSONObject(k).optString("name")) %>" ><%=personArr.optJSONObject(k).optString("name") %></a>
													 <%if(k<personArr.length()-1 && k<5){ %>
													 &nbsp;/
													 <%} %>
												<%} %></span>
											<%}else{ %>
												<span>未知</span>
											<%} %></li>
											<li class="short"><label>地区:</label><span><%=JSONUtil.array2String(odshow.optJSONArray("area"),"/","") %></span></li>
			
										</ul>
										<div class="clear"></div>
										<%String littleDesc=StringUtils.trimToEmpty(odshow.optString("showdesc"));
										  if(littleDesc.length()>VARILENGTH) littleDesc=littleDesc.substring(0,VARILENGTH)+"...";
										%>
										<div class="intro"><%=littleDesc%>
											<%if(littleDesc.length() > 0) {%>
												<a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank">查看详情&gt;&gt;</a>
											<%} %>
										</div>
										
										<%
											List idList=Arrays.asList(58272,59267,59271,59272,59275,59276,60284,102636,58150,59441,59443,59445,60379
													,102193,58151,59430,60378,90871,103949,103956,58197,58198,59328,60281,102668,104400
													,58266,59284,59287,59289,59296,60306,102664,60332,102665,58152,58140,102650,60794,63575
													,58268,59248,59251,59255,59257,59261,59265,60307,102685,61323,61660,102449,107131,61539
													,105135,61328,61458,103958,61078,102723,102147,61217,61450,61285,61420,61421,67876,78216
													,104054,58271,59348,59350,60285,102466,107468,90198,102442,108119,106873,113767,35906,17246
													,58194,58271,59348,59350,60285,61654,61655,61658,24342,26855,50584,24272,59253,59254,60298
													,102647,58275,59524,59525,60279,102697,83948,104446,89049,99943,107552,107553,107554,107555
													,107556,107557,14786,58169,60303,74216,102635,106691,58262,60804,62198,63584,66188,102745,105275
													,78076,78078,103915,58264,58265,60294,58203,59259,60286,104041,58210,59406,60369,102741,58273,59326
													,59335,59345,59351,59364,59367,60288,70534,75537,58276,59418,59419,59420,59421,60346,102438,106709
													,55228,54562,13891,43840,17058,54558,54561,55227,55758,55760,60930,60931,61332,104431,12530,13782,64505
													,58267,59268,59269,59270,59277,79598,102704,107301,60397,102194,58256,59300,60341,99534,102667,58797,60772
													,78075,104150,58263,59294,59295,60411,59542,59544,59548,58193,59370,90236,91153,63513,65178,103936,107321
													,58199,58200,59353,59354,59358,59359,59360,60282,104018,102147,61217,61450,60310,102195,106725,60857,102310,79093,102445,111829);
										
											boolean isShowTitle = idList.contains(odshow.optInt("pk_odshow"));
										%>
										
										<!--5*3 日期型 超出有扩展-->
											<% JSONArray videoAll = odshow.optJSONArray("videos");  %>
											<% String firstUrl = "";  %>
											<% if(!JSONUtil.isEmpty(videoAll)){ %>
											<% if(isShowTitle){ %>
												<% int showCount = 7; %>
												<% int lineCount = 2; %>
												<% int currentEpisodeNumber = odshow.optInt("currentEpisodeNumber",0); %>
											<div class="linkpanels">
												<ul class="linkpanel panel_2">
													<% boolean isMore = videoAll.length()>showCount;  %>
													<% for(int j=0;j< videoAll.length() && j<showCount;j++){ %>
														<% JSONObject video = videoAll.optJSONObject(j); %>
														<% if(firstUrl.length()==0){firstUrl="http://v.youku.com/v_show/id_"+video.optString("videoid")+".html";} %>
														<!-- <% if(currentEpisodeNumber>0 && currentEpisodeNumber==video.optInt("show_videostage")){ %>
															
												<li class="aim">
																<div class="aimtip">
																<div onclick="this.parentNode.style.display = 'none';" class="close"></div>
																<div class="arrow"></div>
																	<%=video.optString("title") %>
																</div>
																<a title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><span><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></span> <%=video.optString("title") %></a>
																</li>
														<%}else{ %>
															<li><a title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><span><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></span> <%=video.optString("title") %></a></li>
														<%} %> -->
													<li><a title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><span><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></span> <%=video.optString("title") %></a></li>
														<% if((j+1)%lineCount==0){ %>
														<% } %>
													<% } %>
													<%if(isMore){ %>
														<li class="handle"><span>全部</span></li>
													<%} %>
												</ul>
												<div class="clear"></div>
												<%if(isMore){ %>
													<div class="panellocation">
														<div class="panelexpand">						
															<ul class="linkpanel panel_2">
																<% for(int j=showCount;j<videoAll.length();j++){ %>
																	<% JSONObject video = videoAll.optJSONObject(j); %>
																	<li><a  title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><span><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></span> <%=video.optString("title") %></a></li>
																	<% if((j+1)%lineCount==0){ %>
																	<% } %>
																<% } %>
															</ul>
															<div class="clear"></div>
														</div><!--panelexpand end-->
													</div>	<!-- panellocation end -->
													<div class="clear"></div>
												<%} %>
											</div>
											<%}else { %>
											<% int showCount = 11; %>
											<% int lineCount = 4; %>
											<% int currentEpisodeNumber = odshow.optInt("currentEpisodeNumber",0); %>
											<div class="linkpanels">
												<ul class="linkpanel panel_4">
													<% boolean isMore = videoAll.length()>showCount;  %>
													<% for(int j=0;j< videoAll.length() && j<showCount;j++){ %>
														<% JSONObject video = videoAll.optJSONObject(j); %>
														<% if(firstUrl.length()==0){firstUrl="http://v.youku.com/v_show/id_"+video.optString("videoid")+".html";} %>
														<% if(currentEpisodeNumber>0 && currentEpisodeNumber==video.optInt("show_videostage")){ %>
															<li class="aim">
																<div class="aimtip">
																<div class="close" onclick="this.parentNode.style.display = 'none';"></div>
																<div class="arrow"></div>
																	<%=video.optString("title") %>
																</div>
																<a  title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></a>
																</li>
														<%}else{ %>
															<li><a title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></a></li>
														<%} %>
														<% if((j+1)%lineCount==0){ %>
														<% } %>
													<% } %>
													<%if(isMore){ %>
														<li class="handle"><span>全部</span></li>
													<%} %>
												</ul>
												<div class="clear"></div>
												<%if(isMore){ %>
													<div class="panellocation">
														<div class="panelexpand">						
															<ul class="linkpanel panel_4">
																<% for(int j=showCount;j<videoAll.length();j++){ %>
																	<% JSONObject video = videoAll.optJSONObject(j); %>
																	<li><a title="<%=video.optString("title") %>"  href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=formatVideoStage(video.optString("show_videoseq"),video.optString("show_videostage"))%></a></li>
																	<% if((j+1)%lineCount==0){ %>
																	<% } %>
																<% } %>
															</ul>
															<div class="clear"></div>
														</div><!--panelexpand end-->
													</div>	<!-- panellocation end -->
													<div class="clear"></div>
												<%} %>
											</div>
											<%} %>
											<%} %>
										<div class="gotoplay">
													<% if(StringUtils.trimToEmpty(firstUrl).length()==0){
															String trailerVideoUrl=odshow.optString("trailer_videourl");
															if(trailerVideoUrl!=null && trailerVideoUrl.length()>0){%>
																<div class="btnplay_large"><a href="<%=trailerVideoUrl%>" target="_blank">播放</a></div>
															<%}else{ %>
																<div class="btnplay_large_disable"><em>播放</em></div>
															<%} %>
												<%}else { %>
													<div class="btnplay_large"><a href="<%=firstUrl %>" target="_blank">播放</a></div>
												<%} %>
											
										</div>
										<% JSONArray seriesShows = odshow.optJSONArray("series"); %>
									<% if(!JSONUtil.isEmpty(seriesShows)){ %>
										<div class="like">
											<label>相关系列:</label> 
											<% for(int j=0;j<seriesShows.length();j++){ %>
											<% JSONObject seriesShow = seriesShows.optJSONObject(j);  %>
											<a href="<%=getUrl(seriesShow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank"><%=seriesShow.optString("showname") %></a> 
											<% } %>
										</div>
									<%} %>
									</div><!--T end-->
									<div class="clear"></div>
								</div><!--detail end-->
							</div><!--zy end-->
						</div><!--item end-->
       		<%}else if("电影".equalsIgnoreCase(showcategory)){ %>
       				<!--电影-->
					<div class="item <%=getItemCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl"))%>">
						<div class="movie">
							<ul class="base">
								<li class="base_name"><h1><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank"><%=odshow.optString("highLightName","").length()>0?(odshow.optString("highLightName")+(odshow.optString("aliasName","").length()>0?"("+odshow.optString("aliasName")+")":"")):odshow.optString("showname") %></a></h1></li>
								<li class="base_pub"><%=odshow.optString("releaseyear").equals("0000")?"":odshow.optString("releaseyear")%></li>
								<li class="base_what">电影</li>
								<li class="base_what"><%if(odshow.optInt("showsum_vv")>0 && odshow.optInt("paid")==0 &&odshow.optInt("paid")==0){ %>
													   总播放：<%= WebUtils.formatNumber(odshow.optInt("showsum_vv"), "###,###")%>
													   <%}%>
								</li>
								<li class="base_what"><%String commentStr=StringUtils.trimToEmpty(odshow.optString("showtotal_comment"));
														if(commentStr.length()>0 && !commentStr.equals("0") &&odshow.optInt("paid")==0 ){ %>评论：<%=commentStr%><%}%></li>
								<li class="base_rating"><div class="rating">
								<%double dRating = odshow.optDouble("reputation"); %>
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
							</ul><!--base end-->
							<div class="detail">
							<%String pic=getPic(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl")); %>
							<%if(pic.length()>0){ %>
								<div class="G">
									<ul class="p pv">
										<li class="p_link"><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank" title="<%=odshow.optString("showname") %>"></a></li>
										<li class="p_thumb"><img class="<%=getPicCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl")) %>"  src="<%=pic%>" alt="<%=odshow.optString("showname") %>" /></li>
										<li class="p_status"><span class="status"><%=odshow.optString("display_status") %></span><span class="bg"></span></li>
										<% if(odshow.optInt("paid")==1){ %>
										<li class="p_ispaid"></li>		
										<%} %>
										<% JSONArray streamtypes = odshow.optJSONArray("streamtypes"); %>
								       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
								 		<li class="p_ishd"><span class="ico__SD" title="超清"></span></li>
								       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
								       <li class="p_ishd"><span class="ico__HD" title="高清"></span></li>
								       <%} %>
									</ul>
									<div class="clear"></div>
								</div><!--G end-->
							<%} %>
								<div class="T">
									<!--
									.long	宽
									.short	窄
									.cross	通
									-->
									<ul class="params">
										<li class="long"><label>主演:</label>
											<%JSONArray personArr = odshow.optJSONArray("performer"); %>
											<%if(!JSONUtil.isEmpty(personArr)){ %><span>
												<%for(int k=0;k<personArr.length()&& k<6;k++){ %>
													 <a href="/search_video/q_<%=WebUtils.urlEncode(personArr.optJSONObject(k).optString("name")) %>" ><%=personArr.optJSONObject(k).optString("name") %></a>
													 <%if(k<personArr.length()-1 && k<5){ %>
													 &nbsp;/
													 <%} %>
												<%} %></span>
											<%}else{ %>
												<span>未知</span>
											<%} %></li>
										<li class="short"><label>类型:</label><span><%=JSONUtil.array2String(odshow.optJSONArray("movie_genre"),"/","") %></span></li>
										<li class="long"><label>导演:</label>
											<%JSONArray directorArr = odshow.optJSONArray("director"); %>
											<%if(!JSONUtil.isEmpty(directorArr)){ %><span>
												<%for(int k=0;k<directorArr.length()&& k<6;k++){ %>
													 <a href="/search_video/q_<%=WebUtils.urlEncode(directorArr.optJSONObject(k).optString("name")) %>" ><%=directorArr.optJSONObject(k).optString("name") %></a>
													 <%if(k<directorArr.length()-1 && k<5){ %>
													 &nbsp;
													 <%} %>
												<%} %></span>
											<%}else{ %>
												<span>未知</span>
											<%} %>
										</li>
										<li class="short"><label>地区:</label><span><%=JSONUtil.array2String(odshow.optJSONArray("area"),"/","") %></span></li>
										<li class="long"><label>别名:</label><span><%=JSONUtil.array2String(odshow.optJSONArray("showalias"),"/","") %></span></li>
										<% JSONArray distributors=odshow.optJSONArray("distributor");
										   StringBuffer distributorsStr=new StringBuffer("未知");
										   if(distributors!=null && distributors.length()>0){
											   distributorsStr.setLength(0);
											  for(int distributorIndex=0;distributorIndex<distributors.length() && distributorIndex<1 ;distributorIndex++){
												  distributorsStr.append(distributors.optJSONObject(distributorIndex).optString("name"));
												  
											  }
										   }%>
										<li class="short"><label>发行:</label><span><%=distributorsStr.toString()%></span></li>
									</ul>
									<div class="clear"></div>
									<%String littleDesc=StringUtils.trimToEmpty(odshow.optString("showdesc"));
										  if(littleDesc.length()>MOVIELENGTH) littleDesc=littleDesc.substring(0,MOVIELENGTH)+"...";
									%>
									<div class="intro"><%=littleDesc%>
										<%if(littleDesc.length() > 0) {%>
											<a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank">查看详情&gt;&gt;</a>
										<%} %>
									</div>
									<div class="gotoplay"> <!-- 正片没播放链接，就放预告片（trailer_videourl）， 暂无播放 （所有的播放地址） -->
										<% JSONArray videoAll = odshow.optJSONArray("videos");  %>
										<% if(JSONUtil.isEmpty(videoAll)){
												String trailerVideoUrl=odshow.optString("trailer_videourl");
												if(trailerVideoUrl!=null && trailerVideoUrl.length()>0){%>
													<div class="btnplay_large"><a href="<%=trailerVideoUrl%>" target="_blank">播放</a></div>
												<%}else{ %>
											<div class="btnplay_large_disable"><em>播放</em></div>
												<%} %>
										<%}else { %>
											<div class="btnplay_large"><a href="http://v.youku.com/v_show/id_<%=videoAll.optJSONObject(0).optString("videoid") %>.html" target="_blank">播放</a></div>
										<%} %>
									</div>
									
									<% JSONArray remains = odshow.optJSONArray("remains"); %>
                                    <% if(!JSONUtil.isEmpty(remains)){ %>
                                    <div class="like">
                                        <span class="label">相关片段:</span>
                                        <% for(int j = 0; j < remains.length(); j++) { %>
                                            <% JSONObject remainvideo = remains.optJSONObject(j); %>
                                            <% if(remainvideo == null){ continue; } %>
                                            <a href="http://v.youku.com/v_show/id_<%= remainvideo.optString("videoid") %>.html"  target="_blank" title="<%= WebUtils.htmlEscape(remainvideo.optString("title")) %>" charset="801-102"><%= WebUtils.htmlEscape(remainvideo.optString("title"), 15, "...") %></a>
                                        <% } %>
                                    </div>
                                    <% } %>
								</div><!--T end-->
								<div class="clear"></div>
							</div><!--detail end-->
						</div><!--movie end-->
					</div><!--item end-->
       		<%}else { %>
       			<!--电视剧 动漫-->
					<div class="item <%=getItemCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl"))%>">
						<div class="tv">
							<ul class="base">
								<li class="base_name"><h1><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank"><%=odshow.optString("highLightName","").length()>0?(odshow.optString("highLightName")+(odshow.optString("aliasName","").length()>0?"("+odshow.optString("aliasName")+")":"")):odshow.optString("showname") %></a></h1></li>
								<li class="base_pub"><%=odshow.optString("releaseyear").equals("0000")?"":odshow.optString("releaseyear")%></li>
								<li class="base_what"><%=odshow.optInt("episode_total",0)==0?"":odshow.optInt("episode_total")+"集" %><%=odshow.optString("showcategory") %></li>
								<li class="base_what"><%if(odshow.optInt("showsum_vv")>0 &&odshow.optInt("paid")==0){ %>
													   总播放：<%= WebUtils.formatNumber(odshow.optInt("showsum_vv"), "###,###")%>
													   <%}%></li>
								<li class="base_what"><%String commentStr=StringUtils.trimToEmpty(odshow.optString("showtotal_comment"));
														if(commentStr.length()>0 && !commentStr.equals("0") &&odshow.optInt("paid")==0){ %>评论：<%=commentStr%><%}%>
								</li>
								<li class="base_rating"><div class="rating">
									<%double dRating = odshow.optDouble("reputation"); %>
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
							</ul><!--base end-->
							<div class="detail">
							<%String pic=getPic(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl")); %>
							<%if(pic.length()>0){ %>
									<div class="G">
										<ul class="p pv">
										<li class="p_link"><a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank" title="<%=odshow.optString("showname") %>"></a></li>
										<li class="p_thumb"><img class="<%=getPicCss(odshow.optString("show_vthumburl"),odshow.optString("show_thumburl")) %>" src="<%=pic%>" alt="<%=odshow.optString("showname") %>" /></li>
										<li class="p_status"><span class="status"><%=odshow.optString("display_status") %></span><span class="bg"></span></li>
										<% if(odshow.optInt("paid")==1){ %>
										<li class="p_ispaid"></li>		
										<%} %>
										<% JSONArray streamtypes = odshow.optJSONArray("streamtypes"); %>
								       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
								 		<li class="p_ishd"><span class="ico__SD" title="超清"></span></li>
								       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
								       <li class="p_ishd"><span class="ico__HD" title="高清"></span></li>
								       <%} %>
									</ul>
									<ul class="rel">
										<li class="update"><%=odshow.optString("update_notice") %></li>
									</ul>
									<div class="clear"></div>
								</div><!--G end-->
							<%} %>
								<div class="T">
									<ul class="params">
									<%if(odshow.optString("showcategory").equals("电视剧")){ %>
										<li class="long"><label>主演:</label>
											<%JSONArray personArr = odshow.optJSONArray("performer"); %>
											<%if(!JSONUtil.isEmpty(personArr)){ %><span>
												<%for(int k=0;k<personArr.length()&& k<5;k++){ %>
													 <a href="/search_video/q_<%=WebUtils.urlEncode(personArr.optJSONObject(k).optString("name")) %>" ><%=personArr.optJSONObject(k).optString("name") %></a>
													 <%if(k<personArr.length()-1 && k<4){ %>
													 &nbsp;/
													 <%} %>
												<%} %></span>
											<%}else{ %>
												<span>未知</span>
											<%} %>
										</li>
										<% String genre = ""; 
											if("电视剧".equals(showcategory)){
												genre="tv_genre";
											}else if("动漫".equals(showcategory)){
												genre="anime_genre";
											}else if("体育".equals(showcategory)){
												genre="sports_genre";
											}else if("教育".equals(showcategory)){
												genre="edu_genre";
											}
										%>
										<li class="short"><label>类型:</label><span><%=JSONUtil.array2String(odshow.optJSONArray(genre),"/","") %></span></li>
										<li class="long"><label>导演:</label>
											<%JSONArray directorArr = odshow.optJSONArray("director"); %>
											<%if(!JSONUtil.isEmpty(directorArr)){ %><span>
												<%for(int k=0;k<directorArr.length()&& k<6;k++){ %>
													 <a href="/search_video/q_<%=WebUtils.urlEncode(directorArr.optJSONObject(k).optString("name")) %>" ><%=directorArr.optJSONObject(k).optString("name") %></a>
													 <%if(k<directorArr.length()-1 && k<5){ %>
													 &nbsp;
													 <%} %>
												<%} %></span>
											<%}else{ %>
												<span>未知</span>
											<%} %>	
										</li>
										<li class="short"><label>地区:</label><span><%=JSONUtil.array2String(odshow.optJSONArray("area"),"/","") %></span></li>
										<%} %>
									</ul>
									<div class="clear"></div>
									<%String littleDesc=StringUtils.trimToEmpty(odshow.optString("showdesc"));
										  if(littleDesc.length()>TVLENGTH) littleDesc=littleDesc.substring(0,TVLENGTH)+"...";
									%>
									<div class="intro"><%=littleDesc%>
										<%if(littleDesc.length() > 0) {%>
											<a href="<%=getUrl(odshow.optString("showid"),odshow.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank">查看详情&gt;&gt;</a>
										<%} %>
									</div>
									<% JSONArray videoAll = odshow.optJSONArray("videos");%>
									<% String firstUrl = "";  %>
									<% if(!JSONUtil.isEmpty(videoAll)){ %>
									<% int showCount = 28; %>
									<% int lineCount = 10; %>
									<% int currentEpisodeNumber = odshow.optInt("currentEpisodeNumber",0); %>
									<div class="linkpanels">
										<ul class="linkpanel panel_10">
											<% boolean isMore = videoAll.length()>showCount;  %>
											<% for(int j=0;j< videoAll.length() && j<showCount;j++){ %>
												<% JSONObject video = videoAll.optJSONObject(j); %>
												<% if(firstUrl.length()==0){firstUrl="http://v.youku.com/v_show/id_"+video.optString("videoid")+".html";} %>
												<!-- <% if(currentEpisodeNumber==j+1){ %>
													<li class="aim">
														<div class="aimtip">
														<div class="close" onclick="this.parentNode.style.display = 'none';"></div>
														<div class="arrow"></div>
															<%=video.optString("title") %>
														</div>
														<a href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=(video.optString("show_videostage").length()==0||"0".equals(video.optString("show_videostage")))?video.optString("show_videoseq"):video.optString("show_videostage") %></a>
														</li>
												<%}else{ %>
													<li><a href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=(video.optString("show_videostage").length()==0||"0".equals(video.optString("show_videostage")))?video.optString("show_videoseq"):video.optString("show_videostage") %></a></li>
												<%} %> -->
												
												<li><a href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=(video.optString("show_videostage").length()==0||"0".equals(video.optString("show_videostage")))?video.optString("show_videoseq"):video.optString("show_videostage") %></a></li>
												<% if((j+1)%lineCount==0){ %>
												<% } %>
											<% } %>
											<%if(isMore){ %>
												<li class="handle"><span>全部</span></li>
											<%} %>
										</ul>
										<div class="clear"></div>
										<%if(isMore){ %>
											<div class="panellocation">
												<div class="panelexpand">						
													<ul class="linkpanel panel_10">
														<% for(int j=showCount;j<videoAll.length();j++){ %>
															<% JSONObject video = videoAll.optJSONObject(j); %>
															<li><a href="http://v.youku.com/v_show/id_<%=video.optString("videoid") %>.html" target="_blank" ><%=(video.optString("show_videostage").length()==0||"0".equals(video.optString("show_videostage")))?video.optString("show_videoseq"):video.optString("show_videostage") %></a></li>
															<% if((j+1)%lineCount==0){ %>
															<% } %>
														<% } %>
													</ul>
													<div class="clear"></div>
												</div><!--panelexpand end-->
											</div>	<!-- panellocation end -->
											<div class="clear"></div>
										<%} %>
									</div>
									<%} %>
									<div class="gotoplay">
										<% if(StringUtils.trimToEmpty(firstUrl).length()==0){
															String trailerVideoUrl=odshow.optString("trailer_videourl");
															if(trailerVideoUrl!=null && trailerVideoUrl.length()>0){%>
																<div class="btnplay_large"><a href="<%=trailerVideoUrl%>" target="_blank">播放</a></div>
															<%}else{ %>
																<div class="btnplay_large_disable"><em>播放</em></div>
															<%} %>
												<%}else { %>
													<div class="btnplay_large"><a href="<%=firstUrl %>" target="_blank">播放</a></div>
												<%} %>
									</div>
									<% JSONArray remains = odshow.optJSONArray("remains"); %>
                                    <% if(!JSONUtil.isEmpty(remains)){ %>
                                    <div class="like">
                                        <span class="label">相关片段:</span>
                                        <% for(int j = 0; j < remains.length(); j++) { %>
                                            <% JSONObject remainvideo = remains.optJSONObject(j); %>
                                            <% if(remainvideo == null){ continue; } %>
                                            <a href="http://v.youku.com/v_show/id_<%= remainvideo.optString("videoid") %>.html"  target="_blank" title="<%= WebUtils.htmlEscape(remainvideo.optString("title")) %>" charset="801-102"><%= WebUtils.htmlEscape(remainvideo.optString("title"), 15, "...") %></a>
                                        <% } %>
                                    </div>
                                    <% } %>
								</div><!--T end-->
								<div class="clear"></div>
							</div><!--detail end-->
						</div><!--tv end-->
					</div><!--item end-->
       		<%} %>
		<% } %>             
<% } %>             
		

<% if(!JSONUtil.isEmpty(personOdshows)){ %>
	<!--人物-->
	<%for(int i=0;i<personOdshows.length() && directCount<10;i++){ %>
	<% JSONObject personOdshow = personOdshows.optJSONObject(i); %>
	<% JSONArray videoAll = personOdshow.optJSONArray("videos"); 
	   directCount++;%>
	<% if(!JSONUtil.isEmpty(videoAll)){ %>
<div class="item">
	<div class="figure">
		<ul class="base">
		<%String detailPersonUrl="/detail/person/"+com.youku.search.util.MyUtil.encodeVideoId(new Integer(personOdshow.optString("personid")).intValue())+"?keyword="+WebUtils.htmlEscape(webParam.getQ()); %>
			<li class="base_name"><h1><a href="<%=detailPersonUrl %>" target="_blank"><span class="highlight"><%=personOdshow.optString("personname") %></span></a></h1></li>
			<li class="base_link"><a target="_blank" href="<%=detailPersonUrl %>">全部影视作品&gt;&gt;</a></li>
		</ul><!--base end-->
		<div class="detail">
			<div class="workscoll">
				<div class="works">
						<% for(int k=0;k<videoAll.length() && k<4;k++){ %>
						<% JSONObject video = videoAll.optJSONObject(k); %>
						<%String pic=getPic(video.optString("show_vthumburl"),video.optString("show_thumburl"));
						//横版无截图
						  if(pic.length()==0) pic="http://g1.ykimg.com/0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E";
						  %>
						<ul class="p pv">
							<li class="p_link"><a href="<%=getUrl(video.optString("showid"),video.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target="_blank" title="<%=video.optString("showname") %>"></a></li>
							<li class="p_thumb"><img  class="<%=getPicCss(video.optString("show_vthumburl"),video.optString("show_thumburl")) %>" src="<%=pic%>" alt="<%=video.optString("showname") %>" /></li>
							<li class="p_status"><span class="status"><%=video.optString("display_status") %></span><span class="bg"></span></li>
							<% if(video.optInt("paid")==1){ %>
											<li class="p_ispaid"></li>		
											<%} %>
							<% JSONArray streamtypes = video.optJSONArray("streamtypes"); %>
					       <% if(JSONUtil.contain(streamtypes, "hd2")) { %>
					 		<li class="p_ishd"><span class="ico__SD" title="超清"></span></li>
					       <% }else if(JSONUtil.contain(streamtypes, "hd")){ %>
					       <li class="p_ishd"><span class="ico__HD" title="高清"></span></li>
					       <%} %>
							<li class="p_title"><a href="<%=getUrl(video.optString("showid"),video.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>" target=""><%=video.optString("showname") %></a></li>
							<li class="p_typepub"><%=video.optString("showcategory") %> <%=video.optString("releaseyear") %></li>
							<li class="p_rating"><div class="rating">
							<%double dRating = video.optDouble("reputation"); %>
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
							 
							<li class="p_play"><div class="btnplay">
							<%if(video.optString("firstepisode_videourl")==null || video.optString("firstepisode_videourl").length()==0){ %>
								<%String trailerVideoUrl=video.optString("trailer_videourl");
								if(trailerVideoUrl!=null && trailerVideoUrl.length()>0){%>
								<a href="<%=trailerVideoUrl%>" target="_blank">播放</a>
								<%}else{ %>
								<div class="btnplay_large_disable"><em>播放</em></div>
								<%} %>
							<%}else{ %>
								<a href="<%=video.optString("firstepisode_videourl") %>" target="_blank">播放</a></div></li>
							<%} %>
						</ul>
					<%} %>
				
				<div class="clear"></div>
				<ul class="txtlist">
				<%for(int k=4;k<videoAll.length()&& k<20;k++){ %>
				<%JSONObject video=videoAll.optJSONObject(k);
				  String littleName=video.optString("showname");
				  if(littleName.length()>6) littleName=littleName.substring(0,6)+"...";
				  if(video.optString("showcategory").equals("电影") ||video.optString("showcategory").equals("综艺")){%>
				  	<li><a title="<%=video.optString("showname")%>" target="_blank" href="<%=getUrl(video.optString("showid"),video.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>"><%=littleName %></a> <span><%=video.optString("releaseyear")%> <%=video.optString("showcategory") %></span></li>
				  <%}else if(video.optString("showcategory").equals("电视剧") ||video.optString("showcategory").equals("动漫")){%>
				  	<li><a title="<%=video.optString("showname")%>" target="_blank" href="<%=getUrl(video.optString("showid"),video.optString("pk_grogramme"),"http://www.youku.com/show_page/id_z","http://www.soku.com/detail/show/")%>"><%=littleName %></a> <span><%=video.optString("display_status")%> <%=video.optString("showcategory") %></span></li>
				  <%} %>
				  <%} %>
				</ul>
				</div><!--works end-->
			</div><!--coll end-->
		</div>
	</div>
</div>		
<%} %>
<%} %>
<%} %>



</div><!--direct end-->
                    
                    