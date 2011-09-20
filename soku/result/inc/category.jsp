<%@page import="com.youku.soku.netspeed.Speed"%>
<%@page import="java.util.Map"%>
<%@page import="com.youku.soku.netspeed.SpeedDb"%>
<%@page import="com.youku.search.util.StringUtil"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.youku.soku.util.MyUtil"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@page import="org.json.JSONArray"%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<div class="detail">
<%!
String getClientIp(HttpServletRequest request){
	String clientIp=request.getHeader("X-Forwarded-For");
	if(clientIp != null){
			int pos = clientIp.indexOf(",");
			if (pos > -1)clientIp = clientIp.substring(0,pos);
	}
	return clientIp;
}
%>
	<% JSONObject result_inner = result; 
	String keyword_inner = keyword;
	String clientIp=getClientIp(request);
	%>
	<%String category =result_inner.optString("showcategory");  %>
			<% if("综艺".equals(category)){  %>
	<div class="G">
		<ul class="p">
			<li class="p_link p_link_site"><a href="javascript:;" target="_blank" title="<%=result_inner.optString("showname") %>" onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="1"></a></li>
			<li class="p_thumb"><img src="<%=result_inner.optString("show_thumburl") %>" alt="<%=result_inner.optString("showname") %>" /></li>
			<li class="p_status"><span class="status"></span><span class="bg"></span></li>
			<li class="p_ishd"><span class="ico__" title=""></span></li>			
		</ul>
	</div><!--G end-->
	<div class="T">
		<!--
		.long	宽
		.short	窄
		.cross	通
		-->
		<ul class="params">
			<% JSONArray persons = result_inner.optJSONArray("person");  %>
			<li class="long">
			<label>主持人:</label>
				<%JSONArray personArr = JSONUtil.getPerson(persons,"host"); %>
				<%if(!JSONUtil.isEmpty(personArr)){ %><span>
					<%for(int i=0;i<personArr.length()&& i<6;i++){ %>
					 <a href="/v?keyword=<%=MyUtil.urlEncode(personArr.optString(i)) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="1" _log_directpos="3"><%=personArr.optString(i) %></a>
					 <%if(i<personArr.length()-1 && i<5){ %>
					 /
					 <%} %>
				<%} %></span>
				<%}else{ %>
					<span>未知</span>
				<%} %>
			</li>
			<li class="short"><label>电视台:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("station")," ","未知") %></span></li>
			<% String showReleasedate =result_inner.optString("releasedate");  %>
			<li class="long"><label>首播:</label><span><%=showReleasedate.length()==0?"未知":showReleasedate %></span></li>
			<li class="short"><label>类型:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("variety_genre"),"/","未知") %></span></li>
			<li class="cross"><label>地区:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("area"),"/","未知") %></span></li>
			<% String updateNotice = result_inner.optString("update_notice"); %>
			<% if(updateNotice.length()>0){  %>
				<li class="cross ">
						<label>更新预告:</label><span class="base_update"  style="color:#ff0000;"><%=updateNotice %></span>
				</li>
			<% } %>
		</ul>
		<div class="clear"></div>
	</div><!--T end-->
	<div class="clear"></div>
	
		<% JSONObject videoAll = result_inner.optJSONObject("videos");  %>
		<% if(!JSONUtil.isEmpty(videoAll)){ %>
		<% List<String> siteKeys = new ArrayList<String>(); %>
		<% String firstUrl = ""; %>
		<% for (Iterator iterator = videoAll.keys(); iterator.hasNext();) {%>
			<% String siteKey = (String)iterator.next(); %>
			<% siteKeys.add(siteKey); %>
			<% JSONObject siteJson = videoAll.optJSONObject(siteKey);  %>
			<% JSONArray siteVideos = siteJson.optJSONArray("episodes");  %>
			<div class="linkpanels site<%=siteKey %>" style="display:<%=siteKey.equals(siteKeys.get(0))?"":"none" %>">
			<%
				List idList=Arrays.asList(65183, 113208,113770,113771,113809,113814,113815,114764,
		              64403, 113195, 113747,  113748,  113749,  114788,104547, 119443,
		              65173,  113050,  113739 ,114768 , 120125 , 122188 ,64004, 65176,
		              113041,  113221,64255,124719, 89789, 115712,115754, 65049, 116659, 116712,
		              89790, 116607, 65126, 79061, 79204, 81538,105685, 113775, 113776, 114809,116338,115075,113202 );
			
				int pid = MyUtil.decodeVideoId(request.getParameter("pid"));
				boolean isShowTitle = idList.contains(pid);
			%>
				
				<!-- 显示分集列表 -->
					<% if(!JSONUtil.isEmpty(siteVideos)){  %>
						<% if(isShowTitle){ %>
						
							<ul class="linkpanel panel_2">
						<% boolean isMore = siteVideos.length()>9;  %>
						<% int contentLength=0; %>
						<% int siteVideosIndex=0; %>
						<% for(siteVideosIndex=0;siteVideosIndex<siteVideos.length() && contentLength<9;siteVideosIndex++){ %>
							
							<% JSONObject video = siteVideos.optJSONObject(siteVideosIndex); %>
							<%if(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage"))) continue;  %>
							<% if(firstUrl.length()==0){firstUrl=video.optString("url");} %>
							<li><a href="<%=video.optString("url") %>" target="_blank" title="<%=video.optString("name") %>" onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):MyUtil.formatViewStage(video.optString("orderStage")) %>：&nbsp;<%=video.optString("name") %></a></li>
							<% contentLength++; %>
						<%  } %>
						<%if(isMore){ %>
							<li class="handle"><span>全部</span></li>
						<%} %>
						</ul>
						<div class="clear"></div>
						<%if(isMore){ %>
							<div class="panellocation">
								<div class="panelexpand">						
									<ul class="linkpanel panel_2">
										<% for(;siteVideosIndex<siteVideos.length();siteVideosIndex++){ %>
											<% JSONObject video = siteVideos.optJSONObject(siteVideosIndex); %>
											<%if(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage"))) continue;  %>
											<li><a href="<%=video.optString("url") %>" target="_blank" title="<%=video.optString("name") %>"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):MyUtil.formatViewStage(video.optString("orderStage")) %>：&nbsp;<%=video.optString("name") %></a></li>
											<% if((siteVideosIndex+1)%15==0){ %>
											<% } %>
										<% } %>
									</ul>
									<div class="clear"></div>
								</div><!--panelexpand end-->
							</div>	<!-- panellocation end -->
							<div class="clear"></div>
						<%} %>
						<%}else { %>
						<ul class="linkpanel panel_5">
						<% boolean isMore = siteVideos.length()>14;  %>
						<% int contentLength=0; %>
						<% int siteVideosIndex=0; %>
						<% for(siteVideosIndex=0;siteVideosIndex<siteVideos.length() && contentLength<14;siteVideosIndex++){ %>
							<% JSONObject video = siteVideos.optJSONObject(siteVideosIndex); %>
							<%if(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage"))) continue;  %>
							<% if(firstUrl.length()==0){firstUrl=video.optString("url");} %>
							<li><a href="<%=video.optString("url") %>" target="_blank" title="<%=video.optString("name") %>"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):MyUtil.formatViewStage(video.optString("orderStage")) %></a></li>
							<%contentLength++; %>
						<% } %>
						<%if(isMore){ %>
							<li class="handle"><span>全部</span></li>
						<%} %>
						</ul>
						<div class="clear"></div>
						<%if(isMore){ %>
							<div class="panellocation">
								<div class="panelexpand">						
									<ul class="linkpanel panel_5">
										<% for(;siteVideosIndex<siteVideos.length();siteVideosIndex++){ %>
											<% JSONObject video = siteVideos.optJSONObject(siteVideosIndex); %>
											<%if(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage"))) continue;  %>
											<li><a href="<%=video.optString("url") %>" target="_blank" title="<%=video.optString("name") %>"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):MyUtil.formatViewStage(video.optString("orderStage")) %></a></li>
											<% if((siteVideosIndex+1)%15==0){ %>
											<% } %>
										<% } %>
									</ul>
									<div class="clear"></div>
								</div><!--panelexpand end-->
							</div>	<!-- panellocation end -->
							<div class="clear"></div>
						<%} %>
						
					<% } %>
					<%} %>
					</div> <!-- linkpanels end -->
			<%} %>
	
		<div class="gotoplay">
			<div class="btnplay_large"><a href="<%=firstUrl %>" target="_blank" onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="5">播放</a></div>
			<%
			int [] sites = new int[siteKeys.size()];
			int count = 0;
			for(String siteKey:siteKeys){
				sites[count++]=StringUtil.parseInt(siteKey,0);
			}
			Map<Integer,Speed> sitesMap = SpeedDb.getSpeedMap(clientIp,sites);
			%>
			<div class="source <%=siteKeys.size()==1?"source_one":"" %>">
					<% JSONArray streamtypes = videoAll.optJSONObject(siteKeys.get(0)).optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
				<div class="check"><label id="site<%=siteKeys.get(0) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>" _log_pos="1" _log_ct="3" title="<%=videoAll.optJSONObject(siteKeys.get(0)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(0)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(0),0)).getValue() %>"></div></div>
				<%if(siteKeys.size()>1){ %>
				<ul class="other">
					<% for (int i=1;i<siteKeys.size();i++) {%>
					<% streamtypes = videoAll.optJSONObject(siteKeys.get(i)+"").optJSONArray("streamtypes"); %>
					<% stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li><label id="site<%=siteKeys.get(i) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>" _log_pos="1" _log_ct="3" title="<%=videoAll.optJSONObject(siteKeys.get(i)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(i)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(i),0)).getValue() %>"></div></li>
					<% } %>
				</ul>
				<%} %>
			</div>
		</div><!-- gotoplay end -->
		<%}else { %>
			<div class="gotoplay">
				<div class="btnplay_large_disable"><em>播放</em></div>
			</div><!-- gotoplay end -->	
		<%} %>
	
	
	<div class="like">
		<% JSONArray series = result_inner.optJSONArray("series"); %>
		<% if(!JSONUtil.isEmpty(series)){ %>
		<label>相关系列:</label>
			<%for(int i=0;i<series.length();i++){ %> 
			<a href="/detail/show/<%=MyUtil.encodeVideoId(series.getJSONObject(i).optInt("id")) %>" target="_blank"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="3" _log_directpos="7"><%=series.getJSONObject(i).optString("name") %></a>
			<%} %> 
		<%} %>
	</div>
	<% 
		int defaultSize = 150;
		String personDesc =result_inner.optString("showdesc").replace("\r\n","<br/>");
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
	<%}else if("电影".equals(category)){  %>
			<!-- 电影 分部-->
	<div class="G">
		<ul class="p pv">
			<li class="p_link p_link_site"><a href="javascript:;" target="_blank" title="<%=result_inner.optString("showname") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="1"></a></li>
			<% String img = result_inner.optString("show_vthumburl"); %>
			<li class="p_thumb"><img src="<%=img.length()==0?result_inner.optString("show_thumburl"):img %>" alt="<%=result_inner.optString("showname") %>" /></li>
			<li class="p_status"><span class="status"></span><span class="bg"></span></li>
			<li class="p_ishd"><span class="ico__" title=""></span></li>			
		</ul>
	</div><!--G end-->
	<div class="T">
		<!--
		.long	宽
		.short	窄
		.cross	通
		-->
		<ul class="params">
		<% JSONArray persons = result_inner.optJSONArray("person");  %>
			<li class="long"><label>主演:</label>
				<%JSONArray performerArr = JSONUtil.getPerson(persons,"performer"); %>
				<%if(!JSONUtil.isEmpty(performerArr)){ %><span>
					<%for(int i=0;i<performerArr.length()&& i<6;i++){ %>
					 <a href="/v?keyword=<%=MyUtil.urlEncode(performerArr.optString(i)) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="3"><%=performerArr.optString(i)%></a>
					 <%if(i<performerArr.length()-1 && i<5){ %>
					 /
					 <%} %>
				<%} %></span>
				<%}else{ %>
					<span>未知</span>
				<%} %>
			</li>
			<li class="short"><label>地区:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("area"),"/","未知") %></span></li>
			<li class="long"><label>导演:</label>
				<%JSONArray directorArr = JSONUtil.getPerson(persons,"director"); %>
				<%if(!JSONUtil.isEmpty(directorArr)){ %><span>
					<%for(int i=0;i<directorArr.length()&& i<3;i++){ %>
					 <a href="/v?keyword=<%=MyUtil.urlEncode(directorArr.optString(i)) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="3"><%=directorArr.optString(i) %></a>
					 <%if(i<directorArr.length()-1 && i<2){ %>
					 /
					 <%} %>
				<%} %></span>
				<%}else{ %>
					<span>未知</span>
				<%} %>
			</li>
			<li class="short"><label>类型:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("movie_genre"),"/","未知") %></span></li>
			<li class="long"><label>编剧:</label><span><%=JSONUtil.joinPerson(persons," ",3,"scenarist","未知") %></span></li>
			
			<%
			String distributor = "未知";
			JSONArray distributors = result_inner.optJSONArray("distributor");
			if (distributors!= null && distributors.length() > 0){
				StringBuilder builder = new StringBuilder();
				for (int i = 0 ; i < distributors.length() ; i++){
					builder.append(distributors.optJSONObject(i).opt("name"));
				}
				distributor = builder.toString();
			}
			%>
			<li class="short"><label>发行公司:</label><span><%= distributor %></span></li>
			<% String releaseDate = result_inner.optString("releasedate"); %>
			<li class="long"><label>上映时间:</label><span><%=releaseDate.indexOf("0000")==0?"未知":releaseDate %></span></li>
			<%
			String production = "未知";
			JSONArray productions = result_inner.optJSONArray("production");
			if (productions!= null && productions.length() > 0){
				StringBuilder builder = new StringBuilder();
				for (int i = 0 ; i < productions.length() ; i++){
					builder.append(productions.optJSONObject(i).opt("name"));
				}
				production = builder.toString();
			}
			%>
			
			<li class="short"><label>制作公司:</label><span><%= production %></span></li>
			<li class="cross"><label>别名:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("showalias"),"/","未知") %></span></li>
			<% String updateNotice = result_inner.optString("update_notice"); %>
			<% if(updateNotice.length()>0){  %>
				<li class="cross ">
						<label>更新预告:</label><span class="base_update" style="color:#ff0000;"><%=updateNotice %></span>
				</li>
			<% } %>
		</ul>
		<div class="clear"></div>
		<% JSONObject videoAll = result_inner.optJSONObject("videos");  %>
		<% if(!JSONUtil.isEmpty(videoAll)){ %>
		<% List<String> siteKeys = new ArrayList<String>(); %>
		<% String firstUrl = ""; %>
		<div style="display:none">
		<% for (Iterator iterator = videoAll.keys(); iterator.hasNext();) {%>
			<% String siteKey = (String)iterator.next(); %>
			<% siteKeys.add(siteKey); %>
			<% JSONObject siteJson = videoAll.optJSONObject(siteKey);  %>
			<% JSONArray siteVideos = siteJson.optJSONArray("episodes");  %>
			<div class="linkpanels site<%=siteKey %>" style="display:<%=siteKey.equals(siteKeys.get(0))?"":"none" %>">
				
				<!-- 显示分集列表 -->
					<% if(!JSONUtil.isEmpty(siteVideos)){  %>
					<ul class="linkpanel panel_15">
						<% for(int i=0;i<siteVideos.length();i++){ %>
							<% JSONObject video = siteVideos.optJSONObject(i); %>
							<% if(firstUrl.length()==0){firstUrl=video.optString("url");} %>
							<li><a href="<%=video.optString("url") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="4"></a></li>
						<% } %>
						</ul>
						<div class="clear"></div>
					<% } %>
					</div> <!-- linkpanels end -->
		<%} %>
		</div>
	
		<div class="gotoplay">
			<div class="btnplay_large"><a href="<%=firstUrl %>" target="_blank"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="5">播放</a></div>
			<%
			int [] sites = new int[siteKeys.size()];
			int count = 0;
			for(String siteKey:siteKeys){
				sites[count++]=StringUtil.parseInt(siteKey,0);
			}
			Map<Integer,Speed> sitesMap = SpeedDb.getSpeedMap(clientIp,sites);
			%>
			<div class="source <%=siteKeys.size()==1?"source_one":"" %>">
					<% JSONArray streamtypes = videoAll.optJSONObject(siteKeys.get(0)).optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
				<div class="check"><label id="site<%=siteKeys.get(0) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"  _log_pos="1" _log_ct="2" title="<%=videoAll.optJSONObject(siteKeys.get(0)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(0)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(0),0)).getValue() %>"></div></div>
				<%if(siteKeys.size()>1){ %>
				<ul class="other">
					<% for (int i=1;i<siteKeys.size();i++) {%>
					<% streamtypes = videoAll.optJSONObject(siteKeys.get(i)).optJSONArray("streamtypes"); %>
					<% stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li><label id="site<%=siteKeys.get(i) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"  _log_pos="1" _log_ct="2" title="<%=videoAll.optJSONObject(siteKeys.get(i)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(i)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(i),0)).getValue() %>"></div></li>
					<% } %>
				</ul>
				<%} %>
			</div>
		</div><!-- gotoplay end -->
		<% }else { %>
				<div class="gotoplay">
				<div class="btnplay_large_disable"><em>播放</em></div>
			</div><!-- gotoplay end -->	
		<%} %>
	
		<div class="like">
		<% JSONArray series = result_inner.optJSONArray("series"); %>
		<% if(!JSONUtil.isEmpty(series)){ %>
		<label>相关系列:</label>
			<%for(int i=0;i<series.length();i++){ %> 
			<a href="/detail/show/<%=MyUtil.encodeVideoId(series.getJSONObject(i).optInt("id")) %>" target="_blank"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="2" _log_directpos="7"><%=series.getJSONObject(i).optString("name") %></a>
			<%} %> 
		<%} %>
		</div>
		<% 
		int defaultSize = 150;
		String personDesc =result_inner.optString("showdesc").replace("\r\n","<br/>");
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
			
	<%} else {  %>
	<!-- 电视剧 动漫 及其他 分部-->
		<% String  showcategory = result_inner.optString("showcategory");  %>
		<% int cateId = "动漫".equals(showcategory)?5:("电视剧".equals(showcategory)?1:0);  %>
		<div class="G">
		<ul class="p">
			<li class="p_link p_link_site"><a href="javascript:;" target="_blank" title="<%=result_inner.optString("showname") %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="1"></a></li>
			<li class="p_thumb"><img src="<%=result_inner.optString("show_thumburl") %>" alt="<%=result_inner.optString("showname") %>" /></li>
			<li class="p_status"><span class="status"></span><span class="bg"></span></li>
			<li class="p_ishd"><span class="ico__" title=""></span></li>			
		</ul>
	</div><!--G end-->
	<div class="T">
		<!--
		.long	宽
		.short	窄
		.cross	通
		-->
		<ul class="params">
			<% JSONArray persons = result_inner.optJSONArray("person");  %>
			<li class="long"><label>主演:</label>
			<%JSONArray performerArr = JSONUtil.getPerson(persons,"performer"); %>
				<%if(!JSONUtil.isEmpty(performerArr)){ %><span>
					<%for(int i=0;i<performerArr.length()&& i<6;i++){ %>
					 <a href="/v?keyword=<%=MyUtil.urlEncode(performerArr.optString(i)) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="3"><%=performerArr.optString(i) %></a>
					 <%if(i<performerArr.length()-1 && i<5){ %>
					 /
					 <%} %>
				<%} %></span>
				<%}else{ %>
					<span>未知</span>
				<%} %>
			</li>
			<li class="short"><label>地区:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("area"),"/","未知") %></span></li>
			<li class="long"><label>导演:</label>
			<%JSONArray directorArr = JSONUtil.getPerson(persons,"director"); %>
				<%if(!JSONUtil.isEmpty(directorArr)){ %><span>
					<%for(int i=0;i<directorArr.length()&& i<3;i++){ %>
					 <a href="/v?keyword=<%=MyUtil.urlEncode(directorArr.optString(i)) %>"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="3"><%=directorArr.optString(i) %></a>
					 <%if(i<directorArr.length()-1 && i<2){ %>
					 /
					 <%} %>
				<%} %></span>
				<%}else{ %>
					<span>未知</span>
				<%} %>
			</li>
			<li class="short"><label>类型:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("tv_genre"),"/","未知") %></span></li>
			<li class="long"><label>编剧:</label><span><%=JSONUtil.joinPerson(persons," ",3,"scenarist","未知") %></span></li>
			<li class="short"><label>发行公司:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("issuer")," ","未知") %></span></li>
			<li class="long"><label>监制:</label><span><%=JSONUtil.joinPerson(persons," ",3,"executive_producer","未知") %></span></li>
			<% String releaseDate = result_inner.optString("releasedate"); %>
			<li class="short"><label>上映时间:</label><span><%=releaseDate.indexOf("0000")==0?"未知":releaseDate %></span></li>
			<li class="cross"><label>别名:</label><span><%=JSONUtil.array2String(result_inner.optJSONArray("showalias"),"/","未知") %></span></li>
			<% String updateNotice = result_inner.optString("update_notice"); %>
			<% if(updateNotice.length()>0){  %>
				<li class="cross">
						<label>更新预告:</label><span  class="base_update"  style="color:#ff0000;"><%=updateNotice %></span>
				</li>
			<% } %>
		</ul>
		<div class="clear"></div>
	</div><!--T end-->
	<div class="clear"></div>
	
		<% JSONObject videoAll = result_inner.optJSONObject("videos");  %>
		<% if(!JSONUtil.isEmpty(videoAll)){ %>
		<% List<String> siteKeys = new ArrayList<String>(); %>
		<% String firstUrl = "";  %>
		<% for (Iterator iterator = videoAll.keys(); iterator.hasNext();) {%>
			<% String siteKey = (String)iterator.next(); %>
			<% siteKeys.add(siteKey); %>
			<% JSONObject siteJson = videoAll.optJSONObject(siteKey);  %>
			<% JSONArray siteVideos = siteJson.optJSONArray("episodes");  %>
			<div class="linkpanels site<%=siteKey %>" style="display:<%=siteKey.equals(siteKeys.get(0))?"":"none" %>">
				
				<!-- 显示分集列表 -->
					<% if(!JSONUtil.isEmpty(siteVideos)){  %>
					<ul class="linkpanel panel_15">
					<% boolean isMore = siteVideos.length()>43;  %>
						<% for(int i=0;i<siteVideos.length() && i<43;i++){ %>
							<% JSONObject video = siteVideos.optJSONObject(i); %>
							<% if(firstUrl.length()==0){firstUrl=video.optString("url");} %>
							<li><a href="<%=video.optString("url") %>" target="_blank"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):video.optString("orderStage") %></a></li>
							<% if((i+1)%15==0){ %>
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
									<ul class="linkpanel panel_15">
										<% for(int i=43;i<siteVideos.length();i++){ %>
											<% JSONObject video = siteVideos.optJSONObject(i); %>
											<li><a href="<%=video.optString("url") %>" target="_blank"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="4"><%=(video.optString("orderStage").length()==0||"0".equals(video.optString("orderStage")))?video.optString("orderId"):video.optString("orderStage") %></a></li>
											<% if((i+1)%15==0){ %>
											<% } %>
										<% } %>
									</ul>
									<div class="clear"></div>
								</div><!--panelexpand end-->
							</div>	<!-- panellocation end -->
							<div class="clear"></div>
						<%} %>
						
					<% } %>
					</div> <!-- linkpanels end -->
			<%} %>
	
		<div class="gotoplay">
			<div class="btnplay_large"><a href="<%=firstUrl %>" target="_blank"  onclick="sokuClickStat(this, true);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="5">播放</a></div>
			<%
			int [] sites = new int[siteKeys.size()];
			int count = 0;
			for(String siteKey:siteKeys){
				sites[count++]=StringUtil.parseInt(siteKey,0);
			}
			Map<Integer,Speed> sitesMap = SpeedDb.getSpeedMap(clientIp,sites);
			%>
			<div class="source <%=siteKeys.size()==1?"source_one":"" %>">
					<% JSONArray streamtypes = videoAll.optJSONObject(siteKeys.get(0)).optJSONArray("streamtypes"); %>
					<% String stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
				<div class="check"><label id="site<%=siteKeys.get(0) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"  _log_pos="1" _log_ct="<%=cateId %>" title="<%=videoAll.optJSONObject(siteKeys.get(0)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(0)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(0),0)).getValue() %>"></div></div>
				<%if(siteKeys.size()>1){ %>
				<ul class="other">
					<% for (int i=1;i<siteKeys.size();i++) {%>
					<% streamtypes = videoAll.optJSONObject(siteKeys.get(i)).optJSONArray("streamtypes"); %>
					<% stype = ""; %>
					<% stype = JSONUtil.contain(streamtypes,"hd")?"HD":stype; %>
					<% stype = JSONUtil.contain(streamtypes,"hd2")?"SD":stype; %>
					<li><label id="site<%=siteKeys.get(i) %>" stype="<%=stype %>"  stypename="<%="SD".equals(stype)?"超请":("HD".equals(stype)?"高清":"") %>"  _log_pos="1" _log_ct="<%=cateId %>" title="<%=videoAll.optJSONObject(siteKeys.get(i)).optString("display_status") %>"><%=videoAll.optJSONObject(siteKeys.get(i)).optString("name") %>:</label><div class="speed speed<%=sitesMap.get(StringUtil.parseInt(siteKeys.get(i),0)).getValue() %>"></div></li>
					<% } %>
				</ul>
				<%} %>
			</div>
		</div><!-- gotoplay end -->
		<% }else { %>
				<div class="gotoplay">
				<div class="btnplay_large_disable"><em>播放</em></div>
			</div><!-- gotoplay end -->	
		<%} %>
	
	
	<div class="like">
		<% JSONArray series = result_inner.optJSONArray("series"); %>
		<% if(!JSONUtil.isEmpty(series)){ %>
		<label>相关系列:</label>
			<%for(int i=0;i<series.length();i++){ %> 
			<a href="/detail/show/<%=MyUtil.encodeVideoId(series.getJSONObject(i).optInt("id")) %>" target="_blank"  onclick="sokuClickStat(this);" _log_type="2" _log_pos="1" _log_ct="<%=cateId %>" _log_directpos="7"><%=series.getJSONObject(i).optString("name") %></a>
			<%} %> 
		<%} %>
		</div>
	<% 
		int defaultSize = 150;
		String personDesc =result_inner.optString("showdesc").replace("\r\n","<br/>");
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
	<%}  %>
	
</div><!--detail end-->	