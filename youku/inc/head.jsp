<%@ page contentType="text/html; charset=utf-8" language="java" %>

    <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/index/js/popup.js"></script> 
    <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/searchprompt.js"></script> 

    <div class="soku_topbar">
        <div class="main">
            <ul class="nav">
                <li><a href="http://www.youku.com" charset="0-820-1">优酷首页</a></li>
                <li><a href="http://movie.youku.com/" charset="0-820-2">电影</a></li>
                <li><a href="http://tv.youku.com/" charset="0-820-3">电视</a></li>
                <li><a href="http://zy.youku.com/" charset="0-820-4">综艺</a></li>
                <li><a href="http://index.youku.com/vr_top/cate_search.html" charset="0-820-5">排行榜</a></li>
            </ul>

            <div>搜库 • 优酷旗下网站</div>
        </div>
    </div>
    
    <div class="soku_header">
        <div class="main">
            <div class="logo">
                <a href="http://www.soku.com/" charset="0-821-1"><img src="<%= CDN_SERVER %>/search/images/logo_soku_beta.gif" width="172" height="30" alt="SOKU搜库" /></a>
            </div>
            
            <div class="soku_tool">
                <div class="tool inner" id="tool">
                    <form onsubmit="return dosearch(this);" action="" method="get">
                        <div class="soselect">
                            <div id="headSlected" class="option_current" onclick="show_search_type('head')"><%= WebUtils.htmlEscape(searchCaption) %></div>
                            <div class="option_arrow" onclick="show_search_type('head')"><span class="arrow"><span>v</span></span></div>
                            <div class="clear"></div>
                            <ul id="headSel" class="options" style="display:none" onmouseout="drop_mouseout('head');" onmouseover="drop_mouseover('head')">
                                <li><a onclick="return search_show('head','video',this)" href="#">视频</a></li>
                                <li><a onclick="return search_show('head','playlist',this)" href="#">专辑</a></li>
                                <li><a onclick="return search_show('head','bar',this)" href="#">看吧</a></li>
                                <li><a onclick="return search_show('head','user',this)" href="#">空间</a></li>
                            </ul>
                        </div>
                    
                        <input class="sotext" type="text" autocomplete="off" id="headq" name="q" value="<%= WebUtils.htmlEscape(webParam.getQ()) %>" maxlength="200" />
                        <button class="sobtn" type="submit">搜索</button>
                        <input id="searchdomain" name="searchdomain" type="hidden" value="<%= WebUtils.htmlEscape(searchdomain) %>" />
                        <input id="headSearchType" name="searchType" type="hidden" value="<%= WebUtils.htmlEscape(searchType) %>" />
                    
                        <div class="clear"></div>

                        <div class="soswitch" id="soswitch" <%= WebUtils.eq(searchType, "bar", "style=\"display:none\"", "") %>>
                            <input type="radio" name="socondition" checked="checked" id="inner"/><label id="lb_inner">优酷搜索</label>
                            <input type="radio" name="socondition" id="outer"/><label id="lb_outer">全网搜索</label>
                        </div>
                        
                        <div id="searchextend0" class="soextend" <%= WebUtils.eq(searchType, "bar", "style=\"display:block\"", "") %>>
                            <input id="sbtPost" class="radio" type="radio" onclick="csbt(this,this.form.sbts);" name="sbt" value="post" <%= WebUtils.eq(searchExtend, "post", "checked=\"checked\"", "") %>  />
                            <label for="sbtPost">搜贴子</label>
                            
                            <input id="sbtUser" class="radio" type="radio" onclick="csbt(this,this.form.sbts);" name="sbt" value="user" <%= WebUtils.eq(searchExtend, "user", "checked=\"checked\"", "") %> />
                            <label for="sbtUser">按作者搜</label>
                            
                            <input id="sbts" type="hidden" name="sbts" value="<%= WebUtils.htmlEscape(searchExtend) %>"/>
                        </div>
                        
                    </form>
                </div>
                
                <div class="assit"><a href="javascript:advancedsearch();void(0);" charset="0-821-2">高级搜索</a> | <a href="http://www.youku.com/pub/youku/help/index.shtml" target="_blank" charset="0-821-3">帮助</a> • <a href="http://www.youku.com/service/feed/" target="_blank" charset="0-821-4">反馈</a></div>

            </div>
        </div>
    </div>
