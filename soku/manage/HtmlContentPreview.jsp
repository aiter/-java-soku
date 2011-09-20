<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搞笑电影Soku 视频搜索</title>
<link type="text/css" rel="stylesheet" href="http://www.soku.com/css/soku.css" />
<script type="text/javascript" src="http://static.youku.com/js/prototype.js"></script>
<script type="text/javascript" src="http://static.youku.com/js/nova.js"></script>
<script type="text/javascript" src="http://static.youku.com/search/js/searchprompt.js"></script>
<script type="text/javascript" src="/js/sotool.js"></script>
</head>

<body>
<div class="window">
<div class="screen">
<div class="wrap">

	

	<div class="soku_topbar">
		<div class="main">
			<div class="nav">
			<ul>
			<li><a href="http://www.youku.com" target="_blank">优酷首页</a></li>
			<li><a href="http://movie.youku.com" target="_blank">电影</a></li>
			<li><a href="http://tv.youku.com" target="_blank">电视</a></li>
			<li><a href="http://zy.youku.com" target="_blank">综艺</a></li>
			</ul>
			</div>
			<div>Soku •  优酷旗下网站</div>
		</div>
	</div>
<div class="soku_header">
		<div class="main">
		<div class="logo"><a href="/"><img src="/img/logo_soku_beta.gif" width="172" height="30" alt="Soku" /></a></div>
		<div class="soku_tool">
			<div class="tool outer" id="tool">
			<form onsubmit="return dosearch(this);" action="" method="get">
					<div class="soselect">
						<div id="headSlected" class="option_current" onclick="show_search_type('head')">视频</div>
						<div class="option_arrow" onclick="show_search_type('head')"><span class="arrow"><span>v</span></span></div>
						<div class="clear"></div>
						<ul id="headSel" class="options" style="display:none" onmouseout="drop_mouseout('head');" onmouseover="drop_mouseover('head')">
							<li><a onclick="return search_show('head','video',this)" href="#">视频</a></li>

							<li><a onclick="return search_show('head','playlist',this)" href="#">专辑</a></li>
							<li><a onclick="return search_show('head','bar',this)" href="#">看吧</a></li>
							<li><a onclick="return search_show('head','user',this)" href="#">空间</a></li>
						</ul>
					</div>
					<input type="hidden" name="from" value="2">
					<input class="sotext" type="text" autocomplete="off" id="headq" name="keyword" value="搞笑电影" maxlength="100"/>
					<button class="sobtn" type="submit">搜索</button>

					
					<input id="searchdomain" name="searchdomain" type="hidden" value="http://www.soku.com">
					<input id="headSearchType" name="searchType" type="hidden" value="video">
					
					<div class="clear"></div>

					<div class="soswitch" id="soswitch" >
						<input type="radio" name="socondition" id="inner"/><label id="lb_inner">优酷搜索</label>
						<input type="radio" name="socondition" checked="checked" id="outer"/><label id="lb_outer">全网搜索</label>
					</div>

					
					<div id="searchextend0" class="soextend">
						<input id="sbtPost" class="radio" type="radio" onclick="csbt(this,this.form.sbts);" name="sbt" value="post"/><label for="sbtPost">搜贴子</label>
						<input id="sbtUser" class="radio" type="radio" onclick="csbt(this,this.form.sbts);" name="sbt" value="user"/><label for="sbtUser">按作者搜</label>
						<input type="hidden" name="sbts" value="bar"/>
					</div>
					
					</form>

				
				</div>
		
				<div class="assit"><a href="javascript:;" onclick="window.open('/service/feedback.html?u='+encodeURIComponent(window.location.href)+'&k=搞笑电影');return false;">意见反馈</a></div>
			</div><!--soku_tool end-->
		</div><!--main end-->
	</div><!--soku_header end-->

	<div class="soku_master">
		<div class="control">
			
			<div class="groupby">	
				<ul class="tabs">
				
				
					<li class="current"><span>全部</span></li>
				
				

				</ul>
				
			</div>			
			<div class="filter">
				<div class="statinfo">找到相关视频10808个</div>
				<div class="selector">
				<form name="filter_form" action="/v" method="get">
				<input type="hidden" name="keyword" value="搞笑电影">
				<input type="hidden" name="ext" value="2">
				<span class="label">视频筛选:</span>
					<select class="sel" name="time_length" onchange="filter_form.submit();">
						<option value="0" selected>所有长度</option>
						<option value="1">0&ndash;10 分钟（短视频）</option>
						<option value="2">10&ndash;30 分钟（动漫）</option>
						<option value="3">30&ndash;60分钟（影视综艺）</option>
						<option value="4">≥60分钟（电影）</option>
					</select>
					<select class="sel" name="limit_date" onchange="filter_form.submit();">
						<option value="0" selected>不限发布时间</option>
						<option value="1">一天内</option>
						<option value="7">一周内</option>
						<option value="31">一月内</option>
					</select>
					<select class="sel" name="hd" onchange="filter_form.submit();">
						<option value="0" selected>不限画质</option>
						<option value="1">高清</option>
					</select>
					<select class="sel" name="site" onchange="filter_form.submit();">
						<option value="0" selected>不限来源</option>
						
									<option value="14">优酷网</option>
								
									<option value="1">土豆网</option>
								
									<option value="2">56网</option>
								
									<option value="3">新浪网</option>
								
									<option value="6">搜狐</option>
								
									<option value="17">乐视网</option>
								
									<option value="4">琥珀网</option>
								
									<option value="16">电影网</option>
								
									<option value="8">凤凰网</option>
								
									<option value="9">激动网</option>
								
					</select>
					
						<input type="hidden" name="ts" value="MTQsMSwyLDMsNiwxNyw0LDE2LDgsOQ==">
					
					</form>

				</div>
				<div class="clear"></div>	
			</div>
		</div>
		<div class="result">
			

			
				<div class="nonstop">
			
			
			
						<!--大词直达区-->
						<s:property value="majorTerm.htmlText" escape="false"/>
							

						<!--大词直达区结束-->
			
			<!--人物-->

		


<!--人物结束-->

			

			
	
			

		
<!--综艺-->

<!--综艺结束-->


			
			</div><!--nonstop end-->
			<h3 class="label">以下为正常搜索结果：</h3>
			
			

			<!--普通项-->
			<div class="items">
			
				<div class="item">
					<ul>
					<li class="i_link"><a title="【高清】电影视频 CF超感人 超搞笑 斗剧3  之《疯狂的C4》" href="http://v.youku.com/v_show/id_XMjAwMjA4MTQ0.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjAwMjA4MTQ0.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,0);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0100641F464C6DD6DDA02F00BE3EC43682CA4C-A703-A6C9-37B9-665A14446077" alt="【高清】电影视频 CF超感人 超搞笑 斗剧3  之《疯狂的C4》" /></li>
					<li class="i_status">
						<div class="HD" title="高清"><em>高清</em></div>
						<div class="time">18:08</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="【高清】电影视频 CF超感人 超搞笑 斗剧3  之《疯狂的C4》" href="http://v.youku.com/v_show/id_XMjAwMjA4MTQ0.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjAwMjA4MTQ0.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,0);">【高清】<span class="highlight">电影</span>视频 CF超感人 超<span class="highlight">搞笑</span> 斗剧3  之《疯狂的C4》</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E7%A9%BF%E8%B6%8A%E7%81%AB%E7%BA%BF" target="_blank">穿越火线</a> <a href="/v?keyword=%E6%96%97%E5%89%A73" target="_blank">斗剧3</a> <a href="/v?keyword=%E6%84%9F%E4%BA%BA" target="_blank">感人</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="陈佩斯搞笑小电影-赶场" href="http://v.blog.sohu.com/u/vw/4580775" target="_blank" onclick="clicklog('http://v.blog.sohu.com/u/vw/4580775','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,1);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0900641F464A7EB3B600000000000000000000-0000-0000-0000-00006D213740" alt="陈佩斯搞笑小电影-赶场" /></li>
					<li class="i_status">
						
						<div class="time">15:35</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="陈佩斯搞笑小电影-赶场" href="http://v.blog.sohu.com/u/vw/4580775" target="_blank" onclick="clicklog('http://v.blog.sohu.com/u/vw/4580775','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,1);">陈佩斯<span class="highlight">搞笑</span>小<span class="highlight">电影</span>-赶场</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">搜狐</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="史上最搞笑电影" href="http://v.youku.com/v_show/id_XOTAwNDE3ODg=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XOTAwNDE3ODg=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,2);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0100011F464C240694F6C6006AB462D9EA39F7-36C1-68FD-BB50-AF98EE6321BA" alt="史上最搞笑电影" /></li>
					<li class="i_status">
						
						<div class="time">96:25</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="史上最搞笑电影" href="http://v.youku.com/v_show/id_XOTAwNDE3ODg=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XOTAwNDE3ODg=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,2);">史上最<span class="highlight">搞笑</span><span class="highlight">电影</span></a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=xx" target="_blank">xx</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="情圣国语版，周星驰无厘头搞笑电影" href="http://v.youku.com/v_show/id_XODU5NzMzODg=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XODU5NzMzODg=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,3);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0100011F46480F14E8E8A7005588D9E3AC927C-A6BE-E5C4-54DA-002347137B76" alt="情圣国语版，周星驰无厘头搞笑电影" /></li>
					<li class="i_status">
						
						<div class="time">92:48</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="情圣国语版，周星驰无厘头搞笑电影" href="http://v.youku.com/v_show/id_XODU5NzMzODg=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XODU5NzMzODg=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,3);">情圣国语版，周星驰无厘头<span class="highlight">搞笑</span><span class="highlight">电影</span></a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E9%A6%99%E6%B8%AF" target="_blank">香港</a> <a href="/v?keyword=%E7%BB%8F%E5%85%B8" target="_blank">经典</a> <a href="/v?keyword=%E5%96%9C%E5%89%A7" target="_blank">喜剧</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="【国语中字】周星驰经典搞笑电影：国产凌凌漆 （国产007） " href="http://v.youku.com/v_show/id_XMjEwNzY2MzQw.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjEwNzY2MzQw.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,4);"></a></li>
					<li class="i_thumb"><img src="http://g2.ykimg.com/0100641F464CA4AAC46A0A00583772C889FC31-5A98-934A-2C77-86D46308BD11" alt="【国语中字】周星驰经典搞笑电影：国产凌凌漆 （国产007） " /></li>
					<li class="i_status">
						
						<div class="time">80:08</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="【国语中字】周星驰经典搞笑电影：国产凌凌漆 （国产007） " href="http://v.youku.com/v_show/id_XMjEwNzY2MzQw.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjEwNzY2MzQw.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,4);">【国语中字】周星驰经典<span class="highlight">搞笑</span><span class="highlight">电影</span>：国产凌凌漆 （国产007） </a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E5%91%A8%E6%98%9F%E9%A9%B0" target="_blank">周星驰</a> <a href="/v?keyword=%E8%A2%81%E5%92%8F%E4%BB%AA" target="_blank">袁咏仪</a> <a href="/v?keyword=%E5%90%B4%E5%AD%9F%E8%BE%BE" target="_blank">吴孟达</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				<div class="clear"></div>	
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="视频：黄色电影原来是这样拍成（搞笑短片）" href="http://real.joy.cn/video/1874656.htm" target="_blank" onclick="clicklog('http://real.joy.cn/video/1874656.htm','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,5);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0900641F464846007300000000000000000000-0000-0000-0000-000050AB389A" alt="视频：黄色电影原来是这样拍成（搞笑短片）" /></li>
					<li class="i_status">
						
						<div class="time"></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="视频：黄色电影原来是这样拍成（搞笑短片）" href="http://real.joy.cn/video/1874656.htm" target="_blank" onclick="clicklog('http://real.joy.cn/video/1874656.htm','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,5);">视频：黄色<span class="highlight">电影</span>原来是这样拍成（<span class="highlight">搞笑</span>短片）</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=" target="_blank"></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">激动网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="这个片段让我笑抽了，据说已经笑死了9999人了。" href="http://www.56.com/u28/v_NTQ3NDIwODk.html" target="_blank" onclick="clicklog('http://www.56.com/u28/v_NTQ3NDIwODk.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,6);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0900641F46488C3BAC00000000000000000000-0000-0000-0000-0000559C6EEF" alt="这个片段让我笑抽了，据说已经笑死了9999人了。" /></li>
					<li class="i_status">
						
						<div class="time"></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="这个片段让我笑抽了，据说已经笑死了9999人了。" href="http://www.56.com/u28/v_NTQ3NDIwODk.html" target="_blank" onclick="clicklog('http://www.56.com/u28/v_NTQ3NDIwODk.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,6);">这个片段让我笑抽了，据说已经笑死了9999人了。</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> <a href="/v?keyword=%E6%90%9E%E7%AC%91%E7%89%87%E6%AE%B5" target="_blank">搞笑片段</a> <a href="/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1" target="_blank"><span class="highlight">搞笑电影</span></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">56网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="CF搞笑电影《失败》" href="http://www.tudou.com/programs/view/5bHDX2wGBGI/" target="_blank" onclick="clicklog('http://www.tudou.com/programs/view/5bHDX2wGBGI/','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,7);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0900641F4647CE13EC00000000000000000000-0000-0000-0000-0000B7DF3E60" alt="CF搞笑电影《失败》" /></li>
					<li class="i_status">
						
						<div class="time">04:22</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="CF搞笑电影《失败》" href="http://www.tudou.com/programs/view/5bHDX2wGBGI/" target="_blank" onclick="clicklog('http://www.tudou.com/programs/view/5bHDX2wGBGI/','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,7);">CF<span class="highlight">搞笑</span><span class="highlight">电影</span>《失败》</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">土豆网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="逃学英雄传国语版（完整），王晶经典搞笑喜剧电影.郭富城、张敏、吴孟达主演" href="http://v.youku.com/v_show/id_XOTA5NTc1NTI=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XOTA5NTc1NTI=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,8);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0100641F464B67822C866A01519F23D9126503-A2A2-4043-A393-7B89F5301B4E" alt="逃学英雄传国语版（完整），王晶经典搞笑喜剧电影.郭富城、张敏、吴孟达主演" /></li>
					<li class="i_status">
						
						<div class="time">85:02</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="逃学英雄传国语版（完整），王晶经典搞笑喜剧电影.郭富城、张敏、吴孟达主演" href="http://v.youku.com/v_show/id_XOTA5NTc1NTI=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XOTA5NTc1NTI=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,8);">逃学英雄传国语版（完整），王晶经典<span class="highlight">搞笑</span>喜剧<span class="highlight">电影</span>.郭富城、张敏、吴孟达主演</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E9%A6%99%E6%B8%AF" target="_blank">香港</a> <a href="/v?keyword=%E7%BB%8F%E5%85%B8" target="_blank">经典</a> <a href="/v?keyword=%E5%96%9C%E5%89%A7" target="_blank">喜剧</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="有只僵尸暗恋你 国语DVD高清版 08王晶最新搞笑电影" href="http://v.youku.com/v_show/id_XNTA0OTUwMTI=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XNTA0OTUwMTI=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,9);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0100641F46490B2BEB1172010FBE5236AF1487-DC68-8923-3F02-5BC283246417" alt="有只僵尸暗恋你 国语DVD高清版 08王晶最新搞笑电影" /></li>
					<li class="i_status">
						
						<div class="time">92:22</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="有只僵尸暗恋你 国语DVD高清版 08王晶最新搞笑电影" href="http://v.youku.com/v_show/id_XNTA0OTUwMTI=.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XNTA0OTUwMTI=.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,9);">有只僵尸暗恋你 国语DVD高清版 08王晶最新<span class="highlight">搞笑</span><span class="highlight">电影</span></a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E7%8E%8B%E6%99%B6" target="_blank">王晶</a> <a href="/v?keyword=%E6%9C%89%E5%8F%AA%E5%83%B5" target="_blank">有只僵</a> <a href="/v?keyword=%E5%B0%B8%E6%9A%97%E6%81%8B%E4%BD%A0" target="_blank">尸暗恋你</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				<div class="clear"></div>	
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="林长安搞笑无厘头电影《十月秋风》主题曲吉他弹唱" href="http://v.youku.com/v_show/id_XMjEwMzQ0MTgw.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjEwMzQ0MTgw.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,10);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0100641F464CA2B6C88969012356B52A40ECC8-7A0C-30DD-4538-81A6A2F56D99" alt="林长安搞笑无厘头电影《十月秋风》主题曲吉他弹唱" /></li>
					<li class="i_status">
						<div class="HD" title="高清"><em>高清</em></div>
						<div class="time">02:01</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="林长安搞笑无厘头电影《十月秋风》主题曲吉他弹唱" href="http://v.youku.com/v_show/id_XMjEwMzQ0MTgw.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMjEwMzQ0MTgw.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,10);">林长安<span class="highlight">搞笑</span>无厘头<span class="highlight">电影</span>《十月秋风》主题曲吉他弹唱</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%9E%97%E9%95%BF%E5%AE%89" target="_blank">林长安</a> <a href="/v?keyword=%E5%90%89%E4%BB%96%E5%BC%B9%E5%94%B1" target="_blank">吉他弹唱</a> <a href="/v?keyword=%E5%8D%81%E6%9C%88%E7%A7%8B%E9%A3%8E" target="_blank">十月秋风</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="《搞笑》敢问路在何方《陈雷》" href="http://www.tudou.com/programs/view/7T8qXjceWcE/" target="_blank" onclick="clicklog('http://www.tudou.com/programs/view/7T8qXjceWcE/','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,11);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0900641F464898403200000000000000000000-0000-0000-0000-000016F10AF8" alt="《搞笑》敢问路在何方《陈雷》" /></li>
					<li class="i_status">
						
						<div class="time">01:47</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="《搞笑》敢问路在何方《陈雷》" href="http://www.tudou.com/programs/view/7T8qXjceWcE/" target="_blank" onclick="clicklog('http://www.tudou.com/programs/view/7T8qXjceWcE/','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,11);">《<span class="highlight">搞笑</span>》敢问路在何方《陈雷》</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E5%9C%A8%E9%82%A3%E9%81%A5%E8%BF%9C%E7%9A%84%E5%9C%B0%E6%96%B9" target="_blank">在那遥远的地方</a> <a href="/v?keyword=%E6%90%9E%E7%AC%91%E5%8A%A8%E7%94%BB" target="_blank">搞笑动画</a> <a href="/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1" target="_blank"><span class="highlight">搞笑电影</span></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">土豆网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="美人(超级B一级).视频 电影 搞笑" href="http://www.56.com/u92/v_NTQ3MjMyMzM.html" target="_blank" onclick="clicklog('http://www.56.com/u92/v_NTQ3MjMyMzM.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,12);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0900641F4647F7886F00000000000000000000-0000-0000-0000-0000C98DCE6E" alt="美人(超级B一级).视频 电影 搞笑" /></li>
					<li class="i_status">
						
						<div class="time"></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="美人(超级B一级).视频 电影 搞笑" href="http://www.56.com/u92/v_NTQ3MjMyMzM.html" target="_blank" onclick="clicklog('http://www.56.com/u92/v_NTQ3MjMyMzM.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,12);">美人(超级B一级).视频 <span class="highlight">电影</span> <span class="highlight">搞笑</span></a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> <a href="/v?keyword=%E7%94%B5%E5%BD%B1" target="_blank"><span class="highlight">电影</span></a> <a href="/v?keyword=%E7%BE%8E%E4%BA%BA" target="_blank">美人</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">56网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="嘻游记 电影搞笑片段" href="http://movie.joy.cn/movie/detail/70014871.htm" target="_blank" onclick="clicklog('http://movie.joy.cn/movie/detail/70014871.htm','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,13);"></a></li>
					<li class="i_thumb"><img src="http://g4.ykimg.com/0900641F464A57D5E400000000000000000000-0000-0000-0000-0000D82F4E4D" alt="嘻游记 电影搞笑片段" /></li>
					<li class="i_status">
						
						<div class="time">11:22</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="嘻游记 电影搞笑片段" href="http://movie.joy.cn/movie/detail/70014871.htm" target="_blank" onclick="clicklog('http://movie.joy.cn/movie/detail/70014871.htm','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,13);">嘻游记 <span class="highlight">电影</span><span class="highlight">搞笑</span>片段</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=" target="_blank"><span class="highlight"></a> <a href="/v?keyword=%E7%94%B5%E5%BD%B1" target="_blank">电</span>影</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">激动网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="被逼的-搞笑mtv手机MTV MP4电影3GP电影AVI格式手机视频" href="http://v.blog.sohu.com/u/vw/4534846" target="_blank" onclick="clicklog('http://v.blog.sohu.com/u/vw/4534846','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,14);"></a></li>
					<li class="i_thumb"><img src="http://g4.ykimg.com/0900641F46484C72BD00000000000000000000-0000-0000-0000-00008D1DB972" alt="被逼的-搞笑mtv手机MTV MP4电影3GP电影AVI格式手机视频" /></li>
					<li class="i_status">
						
						<div class="time">02:29</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="被逼的-搞笑mtv手机MTV MP4电影3GP电影AVI格式手机视频" href="http://v.blog.sohu.com/u/vw/4534846" target="_blank" onclick="clicklog('http://v.blog.sohu.com/u/vw/4534846','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,14);">被逼的-<span class="highlight">搞笑</span>mtv手机MTV MP4<span class="highlight">电影</span>3GP<span class="highlight">电影</span>AVI格式手机视频</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E8%A2%AB%E9%80%BC%E7%9A%84%E6%90%9E%E7%AC%91" target="_blank">被逼的搞笑</a> <a href="/v?keyword=%E6%90%9E%E7%AC%91" target="_blank"><span class="highlight">搞笑</span></a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">搜狐</span></li>
					</ul>
				</div>
				
				<div class="clear"></div>	
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="周渝民新电影好友捧场 搞笑风格获赞" href="http://www.letv.com/ptv/vplay/879034.html" target="_blank" onclick="clicklog('http://www.letv.com/ptv/vplay/879034.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,15);"></a></li>
					<li class="i_thumb"><img src="http://g2.ykimg.com/0900641F46496F283900000000000000000000-0000-0000-0000-0000068707DC" alt="周渝民新电影好友捧场 搞笑风格获赞" /></li>
					<li class="i_status">
						
						<div class="time">01:57</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="周渝民新电影好友捧场 搞笑风格获赞" href="http://www.letv.com/ptv/vplay/879034.html" target="_blank" onclick="clicklog('http://www.letv.com/ptv/vplay/879034.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,15);">周渝民新<span class="highlight">电影</span>好友捧场 <span class="highlight">搞笑</span>风格获赞</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E5%91%A8%E6%B8%9D%E6%B0%91" target="_blank">周渝民</a> <a href="/v?keyword=%E6%90%9E%E7%AC%91%E9%A3%8E%E6%A0%BC" target="_blank">搞笑风格</a> <a href="/v?keyword=%E7%88%B1%E4%BD%A0%E4%B8%80%E4%B8%87%E5%B9%B4" target="_blank">爱你一万年</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">乐视网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="男选手在剑道馆中能否反败为胜 续演搞笑电影片段" href="http://www.m1905.com/news/20100729/362912.shtml" target="_blank" onclick="clicklog('http://www.m1905.com/news/20100729/362912.shtml','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,16);"></a></li>
					<li class="i_thumb"><img src="http://g1.ykimg.com/0900641F4648C17D9400000000000000000000-0000-0000-0000-0000FE7F6045" alt="男选手在剑道馆中能否反败为胜 续演搞笑电影片段" /></li>
					<li class="i_status">
						
						<div class="time">03:00</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="男选手在剑道馆中能否反败为胜 续演搞笑电影片段" href="http://www.m1905.com/news/20100729/362912.shtml" target="_blank" onclick="clicklog('http://www.m1905.com/news/20100729/362912.shtml','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,16);">男选手在剑道馆中能否反败为胜 续演<span class="highlight">搞笑</span><span class="highlight">电影</span>片段</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E6%88%91%E7%9A%84%E9%87%8E%E8%9B%AE%E5%A5%B3%E5%8F%8B" target="_blank">我的野蛮女友</a> <a href="/v?keyword=%E7%88%B1%E7%A7%80%E7%94%B5%E5%BD%B1" target="_blank">爱秀电影</a> <a href="/v?keyword=%E7%88%B1%E7%A7%80%E7%94%B5%E5%BD%B1" target="_blank">爱秀电影</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">电影网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="电影报道周小斌片场搞笑 江珊当妈不易" href="http://video.sina.com.cn/p/ent/m/c/2010-05-31/190861018561.html" target="_blank" onclick="clicklog('http://video.sina.com.cn/p/ent/m/c/2010-05-31/190861018561.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,17);"></a></li>
					<li class="i_thumb"><img src="http://g4.ykimg.com/0900641F464A6151EE00000000000000000000-0000-0000-0000-000025210BB5" alt="电影报道周小斌片场搞笑 江珊当妈不易" /></li>
					<li class="i_status">
						
						<div class="time"></div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="电影报道周小斌片场搞笑 江珊当妈不易" href="http://video.sina.com.cn/p/ent/m/c/2010-05-31/190861018561.html" target="_blank" onclick="clicklog('http://video.sina.com.cn/p/ent/m/c/2010-05-31/190861018561.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,17);"><span class="highlight">电影</span>报道周小斌片场<span class="highlight">搞笑</span> 江珊当妈不易</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=" target="_blank"></a> <a href="/v?keyword=CCTV6%E3%80%8A%E4%B8%AD%E5%9B%BD%E7%94%B5%E5%BD%B1%E6%8A%A5%E9%81%93%E3%80%8B" target="_blank">CCTV6《中国电影报道》</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">新浪网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="搞笑-小电影 周星驰搞笑" href="http://play.hupo.tv/tv/10950853.html" target="_blank" onclick="clicklog('http://play.hupo.tv/tv/10950853.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,18);"></a></li>
					<li class="i_thumb"><img src="http://g3.ykimg.com/0900641F46499346EA00000000000000000000-0000-0000-0000-0000F5F23EFA" alt="搞笑-小电影 周星驰搞笑" /></li>
					<li class="i_status">
						
						<div class="time">01:00</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="搞笑-小电影 周星驰搞笑" href="http://play.hupo.tv/tv/10950853.html" target="_blank" onclick="clicklog('http://play.hupo.tv/tv/10950853.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,18);"><span class="highlight">搞笑</span>-小<span class="highlight">电影</span> 周星驰<span class="highlight">搞笑</span></a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=" target="_blank"></a> <a href="/v?keyword=%E5%A8%B1%E4%B9%90%E5%85%AB%E5%8D%A6" target="_blank">娱乐八卦</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">琥珀网</span></li>
					</ul>
				</div>
				
				
				<div class="item">
					<ul>
					<li class="i_link"><a title="香港搞笑电影【流氓师表】" href="http://v.youku.com/v_show/id_XMTg2MTE1Mjg4.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMTg2MTE1Mjg4.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,19);"></a></li>
					<li class="i_thumb"><img src="http://g2.ykimg.com/0100641F464C75ED90968D01F182DC33FFEB6D-8150-0913-9229-4A3DC13D68C7" alt="香港搞笑电影【流氓师表】" /></li>
					<li class="i_status">
						
						<div class="time">95:12</div>
						<div class="bg"></div>
					</li>
					<li class="i_title"><a title="香港搞笑电影【流氓师表】" href="http://v.youku.com/v_show/id_XMTg2MTE1Mjg4.html" target="_blank" onclick="clicklog('http://v.youku.com/v_show/id_XMTg2MTE1Mjg4.html','%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1',3,0,19);">香港<span class="highlight">搞笑</span><span class="highlight">电影</span>【流氓师表】</a></li>
					<li class="i_param"><span class="label">分类:</span> <a href="/v?keyword=%E9%A6%99%E6%B8%AF%E5%96%9C%E5%89%A7%E7%89%87" target="_blank">香港喜剧片</a> </li>
					<li class="i_param"><span class="label">来源:</span> <span style="color:#008000;">优酷网</span></li>
					</ul>
				</div>
				
				<div class="clear"></div>	
				
				
				<div class="clear"></div>
			</div><!--items end-->
			
			
			
			<!--分页 两种-->
			
			<div class="pages">
				<span class="current">1</span>
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=2'>2</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=3'>3</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=4'>4</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=5'>5</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=6'>6</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=7'>7</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=8'>8</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=9'>9</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=10'>10</a> 
<a href='/v?keyword=%E6%90%9E%E7%AC%91%E7%94%B5%E5%BD%B1&orderfield=1&ext=2&curpage=2'> 下一页&gt; </a>
			</div>
			
			
			
			
		</div><!--result end-->
	</div><!--soku_master end-->
	
	<SCRIPT LANGUAGE="JavaScript">library=1;result_count=10808;</SCRIPT>
	<div class="soku_footer">
		<div class="main">
			<div class="soku_tool">
			<form name="f" action="/v">
			
				<div class="tool"><input class="sotext" type="text" name="keyword" value="搞笑电影"/><button class="sobtn" type="submit">搜索</button> </div>
				<div class="assit"><a href="javascript:;" onclick="window.open('/service/feedback.html?u='+encodeURIComponent(window.location.href)+'&k=搞笑电影');return false;">意见反馈</a></div>
				<input type="hidden" name="from" value="3">
			</form>
			</div>
		</div>
	</div>
<!--public minifooter-->
	<script type="text/javascript" src="/js/s_minifooter.js"></script>
	
	<script type="text/javascript" src="http://lstat.youku.com/urchin.js"></script>
	<script>youkuTracker( "soku=" + library + "|" + result_count );</script>
		
</div>
</div>
</div>

</body>
</html>
