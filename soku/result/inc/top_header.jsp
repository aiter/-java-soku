<%@ page contentType="text/html; charset=utf-8" language="java" %>

<div class="topbar">
<div class="link"></div>
<div class="logo"><a target="_blank" href="http://www.youku.com"><em>优酷旗下网站</em></a></div>
</div>
<div class="header">
<div class="main">
	<div class="logo"><a href="/"><span class="logosoku"><strong>SOKU搜库</strong></span></a></div>
	<div class="subnav">
		<a target="_self" href="/newtop/all.html" class="gorank"><span class="ico"></span>排行榜</a>
		<a target="_self" href="/channel/movie____1.html" class="gomovie"><span class="ico"></span>电影</a>
		<a target="_self" href="/channel/teleplay____1.html" class="gotv"><span class="ico"></span>电视剧</a>
				
	</div>
	<div class="sotool">
		<div class="socore">
		
			<form onsubmit="return donewsearch(this);" action="/v" method="get">
			<input class="sotxt" name="keyword" type="text" maxlength="100" value="<%=keyword %>" autocomplete="off" id="headq"/>
			<button type="submit" class="sobtn"><em>搜索</em></button>
			
			</form>
		</div>
		<div id="kubox" class="kubox"></div>
	</div>
	<div class="clear"></div>
</div><!--main end-->
</div>
<script type="text/javascript">
		document.getElementById("headq").focus();
		Ab('');
</script>
