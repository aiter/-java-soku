<%@ page contentType="text/html; charset=utf-8" language="java" %>


<div class="header">
<div class="main">
	<div class="logo"><a target="_blank" href="http://www.soku.com"><span class="logosoku"><strong>SOKU搜库</strong></span></a></div>
	<div class="subnav">
		<a target="_self" href="http://www.soku.com/newtop/all.html" class="gorank"><span class="ico"></span>排行榜</a>
		<a target="_self" href="http://www.soku.com/channel/movie____1.html" class="gomovie"><span class="ico"></span>电影</a>
		<a target="_self" href="http://www.soku.com/channel/teleplay____1.html" class="gotv"><span class="ico"></span>电视剧</a>		
	</div>
	<div class="sotool">
	<form onsubmit="return dosearch(this);" action="" method="get">
		<div class="socore">
						<div class="sooption">
				<div id="headSlected" class="current"><%= WebUtils.htmlEscape(searchCaption) %></div>
				<ul class="options" id="headSel">
					<li><a onclick="return search_show('head','video',this)" href="#">视频</a></li>
                    <li><a onclick="return search_show('head','playlist',this)" href="#">专辑</a></li>
                    <li><a onclick="return search_show('head','bar',this)" href="#">看吧</a></li>
                    <li><a onclick="return search_show('head','user',this)" href="#">空间</a></li>
				</ul>
			</div>
			<div class="clear"></div>
			<input class="sotxt" type="text" autocomplete="off" id="headq" name="q" value="<%= WebUtils.htmlEscape(webParam.getQ()) %>" maxlength="200" />
			<button class="sobtn" type="submit"><em>搜索</em></button>
			<input id="searchdomain" name="searchdomain" type="hidden" value="<%= WebUtils.htmlEscape(searchdomain) %>" />
            <input id="headSearchType" name="searchType" type="hidden" value="<%= WebUtils.htmlEscape(searchType) %>" />
		</div>
		<div class="sobrand"><a href="http://www.youku.com" target="_blank"><span class="logoyouku_sl"><strong>优酷旗下网站</strong></span></a></div>
		<div class="soswitch" id="soswitch" <%= WebUtils.eq(searchType, "video", "style=\"display:block\"", "style=\"display:none\"") %>>
			<ul>
			<li><a href="/v?keyword=<%= WebUtils.htmlEscape(webParam.getQ()) %>">全网搜索</a></li>
			<li class="checked"><a href="#">优酷搜索</a></li>
			</ul>
		</div>
	</form>
	<div id="kubox" class="kubox"></div>
	</div> <!-- sotool end  -->
	<div class="clear"></div>
</div><!--main end-->
</div><!--header end-->
