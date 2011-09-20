<div class="footer">
<div class="footerbox">
<div class="main">
	<div class="logo"><a href="http://www.soku.com"><span class="logosoku"><strong>SOKU搜库</strong></span></a></div>
	<form onsubmit="return dosearch(this);" action="" method="get">
	<div class="sotool">
		<div class="socore">
			<input class="sotxt" type="text" autocomplete="off" name="q" value="<%= WebUtils.htmlEscape(webParam.getQ()) %>" maxlength="200" />
			<input id="headSearchType" name="searchType" type="hidden" value="video" />
			<button class="sobtn" type="submit"><em>搜索</em></button>
		</div>
		<div class="sobrand"><a href="http://www.youku.com" target="_blank"><span class="logoyouku_sl"><strong>优酷旗下网站</strong></span></a></div>
	</div>
	</form>
	<div class="clear"></div>	
</div>
</div>
<div class="copyright">
	Copyright&copy;2011 优酷youku.com版权所有
	<a href="http://www.miibeian.gov.cn/" target="_blank">京ICP证060288号</a>
	<a href="/service/agreement.html" target="_blank">免责声明</a>
	<a href="/service/protocol.html" target="_blank">开放协议</a>
</div></div>

