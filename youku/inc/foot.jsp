<%@ page contentType="text/html; charset=utf-8" language="java" %>

	<div class="soku_footer">
		<div class="main">
			<div class="soku_tool">
				<form onsubmit="return dosearch(this);" action="" method="get">
				<div class="tool inner">
					<div class="soselect">
						<div id="footSlected" class="option_current" onclick="show_search_type('foot')"><%= WebUtils.htmlEscape(searchCaption) %></div>
						<div class="option_arrow" onclick="show_search_type('foot')"><span class="arrow"><span>v</span></span></div>
						<div class="clear"></div>
						<ul id="footSel" class="options" style="display:none" onmouseout="drop_mouseout('foot');" onmouseover="drop_mouseover('foot')">
							<li><a onclick="return search_show('foot','video',this)" href="#">视频</a></li>
							<li><a onclick="return search_show('foot','playlist',this)" href="#">专辑</a></li>
							<li><a onclick="return search_show('foot','bar',this)" href="#">看吧</a></li>
							<li><a onclick="return search_show('foot','user',this)" href="#">空间</a></li>
						</ul>
					</div>
					<input class="sotext" type="text" autocomplete="off" id="footq" name="q" value="<%= WebUtils.htmlEscape(webParam.getQ()) %>" maxlength="200"/>
					<button class="sobtn" type="submit">搜索</button>
					<input id="searchdomain" name="searchdomain" type="hidden" value="<%= WebUtils.htmlEscape(searchdomain) %>" />
					<input id="footSearchType" name="searchType" type="hidden" value="<%= WebUtils.htmlEscape(searchType) %>" />
					<div class="clear"></div>
				</div>
				</form>
				<div class="assit"><a href="javascript:advancedsearch();void(0);" charset="0-822-1">高级搜索</a> | <a href="http://www.youku.com/pub/youku/help/index.shtml" target="_blank" charset="0-822-2">帮助</a> • <a href="http://www.youku.com/service/feed/" target="_blank" charset="0-822-3">反馈</a></div>
			</div>
		</div>
	</div>
	
	<div class="s_miniFooter">
		<div class="footerBox">
			<div class="copyright">
				Copyright&copy;<%= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) %> 优酷 youku.com 版权所有 <a href="http://www.miibeian.gov.cn/" target="_blank">京ICP证060288号</a> <a href="http://www.hd315.gov.cn/beian/view.asp?bianhao=010202006082400023" target="_blank"><span class="icp" title="经营性网站备案"></span></a>
			</div>
		</div>
	</div>
<script type="text/javascript">document.getElementById("headq").focus(); Ab(false, true);</script>
<script type="text/javascript" src="http://urchin.lstat.youku.com/index/js/urchin.js"></script>
<script type="text/javascript">urchinTracker();</script>
