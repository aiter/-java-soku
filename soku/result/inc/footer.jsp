<%@ page contentType="text/html; charset=utf-8" language="java" %>
<div class="footer">
<div class="footerbox">
<div class="main">
	<div class="logo"><a href="/"><span class="logosoku"><strong>SOKU搜库</strong></span></a></div>
	<div class="sotool">
	<form onsubmit="return donewsearch(this);" action="/v" method="get">
		<div class="socore">
			<input class="sotxt" name="keyword" type="text" maxlength="100" value="<%=keyword %>" autocomplete="off" />
			<button class="sobtn" type="submit"><em>搜索</em></button>
		</div>
		<div class="sobrand"><a href="http://www.youku.com" target="_blank"><span class="logoyouku_sl"><strong>优酷旗下网站</strong></span></a></div>
	</form>
	</div>
	<div class="clear"></div>
</div>
</div>
<div class="copyright">
	Copyright&copy;2011 优酷youku.com版权所有
	<a href="http://www.miibeian.gov.cn/" target="_blank">京ICP证060288号</a>
	<a href="/service/agreement.html" target="_blank">免责声明</a>
	<a href="/service/protocol.html" target="_blank">开放协议</a>
</div>
</div><!--footer end-->
