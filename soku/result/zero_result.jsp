<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@page import="com.youku.search.sort.json.util.JSONUtil"%>
<%@page import="com.youku.soku.netspeed.SpeedDb"%>
<%@page
	import="java.util.*,com.youku.soku.sort.Parameter,com.youku.soku.web.SearchResult,com.youku.soku.web.util.*,
			com.youku.soku.util.Constant,com.youku.soku.util.MyUtil,org.json.*,com.youku.soku.web.SearchResult.Content,
			com.youku.search.util.DataFormat,
			com.youku.soku.index.manager.db.SiteManager,
			org.apache.commons.lang.StringUtils"%>
<%  
	String clientIp=request.getRemoteAddr();
	SearchResult result = (SearchResult) request
			.getAttribute("content");
	int pagesize = Constant.Web.SEARCH_PAGESIZE;
	String cost = result.getCost();
	Content content = result.getContent();
	

	
	String encodeKeyword = MyUtil.urlEncode(result.getKeyword());
	
	Parameter param = result.getParam();
	
	String url="/v?keyword="+encodeKeyword;
	
	String pre_time_length_str=request.getParameter("time_length");
	String pre_limit_date_str=request.getParameter("limit_date");
	String pre_hd_str=request.getParameter("hd");
	String pre_site_str=request.getParameter("site");
	
	String pre_time="",pre_date="",pre_hd="",pre_site="";
	
	if(pre_time_length_str!=null){
	  pre_date=pre_date+"&time_length="+pre_time_length_str;
	  pre_hd=pre_hd+"&time_length="+pre_time_length_str;
	  pre_site=pre_site+"&time_length="+pre_time_length_str;
	}
	
	if(pre_limit_date_str!=null){
	  pre_time=pre_time+"&limit_date="+pre_limit_date_str;
	  pre_hd=pre_hd+"&limit_date="+pre_limit_date_str;
	  pre_site=pre_site+"&limit_date="+pre_limit_date_str;
	}
	
	if(pre_hd_str!=null){
		pre_time=pre_time+"&hd="+pre_hd_str;
		pre_date=pre_date+"&hd="+pre_hd_str;
	  pre_site=pre_site+"&hd="+pre_hd_str;
	}
	
	if(pre_site_str!=null){
		pre_time=pre_time+"&site="+pre_site_str;
		pre_date=pre_date+"&site="+pre_site_str;
	    pre_hd=pre_hd+"&site="+pre_site_str;
	}
%>
<%! String printStar(Double db){
	StringBuffer resultBf=new StringBuffer();
	if(db<=0) return "";
	int fullInt=new Double(db/2).intValue(); 
	int index=0;
	for( index=0;index<fullInt;index++){
		resultBf.append("<em class='ico__ratefull'></em>");
	}
	
	if(db/2-fullInt>0){
		resultBf.append("<em class='ico__ratepart'></em>");
		index++;
	}
	
	
	for( ;index<5;index++){
		resultBf.append("<em class='ico__ratenull'></em>");
	}
	resultBf.append("<em class='num'>"+db+"</em>");
	
	return resultBf.toString();
}%>
<!DOCTYPE HTML>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=result.getKeyword()%> 搜库 视频搜索</title>
<link type="text/css" rel="stylesheet" href="/css/soku.css">
<script type="text/javascript" src="/js/autocomplete1.1.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
</head><body>
<div class="window">
<div class="screen">

<% String keyword = request.getParameter("keyword");
        if(keyword==null || keyword.length()==0){
                keyword = result.getKeyword();
        }
%>
<%@ include file="inc/header.jsp" %>

<div class="body">
<div class="typechk">
<div class="main">
		<ul>
	<li class="current"><span>视频</span></li>	<li><a href="/a?keyword=<%=keyword %>">专辑</a></li>			</ul>
	</div>
</div><div class="layout_16">	
	<div class="sidecol">
				<div class="filter">
<div class="panel">
				<div class="item">		
			<label>时长:</label>
			<ul class="col1">
			<li class="<%=(pre_time_length_str!=null && pre_time_length_str.equals("0"))?"current":"" %>"><a href="<%=url+pre_time+"&time_length=0"%>" target="_self">不限</a></li>
			<li class="<%=(pre_time_length_str!=null && pre_time_length_str.equals("1"))?"current":"" %>"><a href="<%=url+pre_time+"&time_length=1"%>" target="_self">0-10分钟</a></li>
			<li class="<%=(pre_time_length_str!=null && pre_time_length_str.equals("2"))?"current":"" %>"><a href="<%=url+pre_time+"&time_length=2"%>">10-30分钟</a></li>
			<li class="<%=(pre_time_length_str!=null && pre_time_length_str.equals("3"))?"current":"" %>"><a href="<%=url+pre_time+"&time_length=3"%>">30-60分钟</a></li>
			<li class="<%=(pre_time_length_str!=null && pre_time_length_str.equals("4"))?"current":"" %>"><a href="<%=url+pre_time+"&time_length=4"%>">60分钟以上</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="item">		
			<label>发布时间:</label>
			<ul>
			<li class="<%=(pre_limit_date_str!=null && pre_limit_date_str.equals("0"))?"current":"" %>"><a href="<%=url+pre_date+"&limit_date=0"%>">不限</a></li>
			<li class="<%=(pre_limit_date_str!=null && pre_limit_date_str.equals("1"))?"current":"" %>"><a href="<%=url+pre_date+"&limit_date=1"%>">一天</a></li>
			<li class="<%=(pre_limit_date_str!=null && pre_limit_date_str.equals("7"))?"current":"" %>"><a href="<%=url+pre_date+"&limit_date=7"%>">一周</a></li>
			<li class="<%=(pre_limit_date_str!=null && pre_limit_date_str.equals("30"))?"current":"" %>"><a href="<%=url+pre_date+"&limit_date=30"%>">一月</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="item">
			<label>画质:</label>
			<ul>
			<li class="<%=(pre_hd_str!=null && pre_hd_str.equals("0"))?"current":"" %>"><a href="<%=url+pre_hd+"&hd=0"%>">不限</a></li>
			<li class="<%=(pre_hd_str!=null && pre_hd_str.equals("1"))?"current":""%>"><a href="<%=url+pre_hd+"&hd=1"%>">高清</a></li>
			</ul>
			<div class="clear"></div>
		</div>
		<div class="item">		
			<label>来源网站:</label>
			<ul>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("0"))?"current":"" %>"><a href="<%=url+pre_site+"&site=0"%>">不限</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("14"))?"current":"" %>"><a href="<%=url+pre_site+"&site=14"%>">优酷</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("1"))?"current":"" %>"><a href="<%=url+pre_site+"&site=1"%>">土豆</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("17"))?"current":"" %>"><a href="<%=url+pre_site+"&site=17"%>">乐视</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("2"))?"current":"" %>"><a href="<%=url+pre_site+"&site=2"%>">56</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("9"))?"current":"" %>"><a href="<%=url+pre_site+"&site=9"%>">激动</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("19"))?"current":"" %>"><a href="<%=url+pre_site+"&site=19"%>">奇艺</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("8"))?"current":"" %>"><a href="<%=url+pre_site+"&site=8"%>">凤凰</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("3"))?"current":"" %>"><a href="<%=url+pre_site+"&site=3"%>">新浪</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("6"))?"current":"" %>"><a href="<%=url+pre_site+"&site=6"%>">搜狐</a></li>
			<li class="<%=(pre_site_str!=null && pre_site_str.equals("15"))?"current":"" %>"><a href="<%=url+pre_site+"&site=15"%>">CNTV</a></li>
			</ul>
			<div class="clear"></div>
		</div>
						<div class="clear"></div>
</div><!--panel end-->	
</div><!--filter end-->	</div><!--sidecol end-->
	<div class="maincol">
	<%if (content!=null && content.getCorrect_word()!=null){%>
			<div class="keylike">你是不是要找： "<a href="/v?keyword=<%=MyUtil.urlEncode(content.getCorrect_word())%>"><%=content.getCorrect_word()%></a>" </div>
			
			<%}%>
				<div class="direct">

                </div>																
<div class="viewchange">
	
	
</div>
<div class="result">

<div class="null">
	<div class="sorry">
		抱歉，没有找到<span class="key"><%=keyword%></span>的相关视频。	</div>
	<h3>建议您：</h3>
	<ul>
		<li>• 检查输入的关键词是否有误。</li>
		<li>• 缩短关键词。</li>
		<li>• 使用相近、相同或其他语义的关键词。</li>
		<li>• 放宽筛选条件。</li>
	</ul>
</div>

</div><!--result end-->
<%if (content!=null && content.getLike_words()!=null){%>
			<div class="relkeys">
				<label>相关搜索:</label>
				<ul>
					<%for (int i = 0;i<content.getLike_words().length();i++){
					String w = content.getLike_words().getString(i);
					%>
					<li><a href="/v?keyword=<%=MyUtil.urlEncode(w)%>"><%=w%></a></li>
					<%}%>
					</ul>
				<div class="clear"></div>
			</div>
			<%}%>
	</div><!--maincol end-->
	<div class="clear"></div>
</div><!--layout end-->
</div><!--body end-->
<%@ include file="inc/footer.jsp" %>
</div><!--screen end-->
</div><!--widnow end-->
<div class="feedwin">
	<div title="用户反馈:您喜欢新版搜库吗?" class="handle"></div>
	<form action="/v" method="post" id="feedbackform">
	<input type="hidden" name="keyword"  id="keyword"  value="<%=result.getKeyword() %>"/>
	<input type="hidden" name="source" id="source" value="<%=request.getHeader("Referer")%>"/>
	<input type="hidden" name="state" id="state" value=""/>
	<div class="feed" style="display:none;">
		<div title="关闭" class="close"></div>
		<div class="step">
			<h3 class="title">您喜欢新版搜库吗？</h3>
			<div class="like"><div class="ico"></div><label>喜欢</label></div>
			<div class="unlike"><div class="ico"></div><label>不喜欢</label></div>
			<div class="clear"></div>
			<div class="input">
				<textarea name="message" id="message">倾听您的建议(100字以内)</textarea>
				<button type="button">提交</button>
			</div>
		</div>
		</div>
		</form>
		<div class="step step3" style="display:none;" >
			<div class="thank">感谢您的参与。</div>
		</div>
	</div>

</body>
<script type="text/javascript" src="/js/sotool.js"></script>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
		FeedBack.init();
</script>
<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
<script type="text/javascript">
if(typeof youkuTracker == "function") {
	youkuTracker( "soku=" + library + "|" + result_count );sokuHz();
}
</script>

</html>
