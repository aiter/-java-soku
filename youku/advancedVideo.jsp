<%@page import="javax.servlet.http.Cookie"%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="java.util.List" %>

<%@ page import="com.youku.search.sort.entity.CategoryMap.Category" %>
<%@ page import="com.youku.search.sort.json.util.JSONUtil" %>
<%@ page import="com.youku.search.sort.servlet.search_page.WebParam" %>
<%@ page import="com.youku.search.sort.servlet.search_page.WebParamHelper" %>
<%@ page import="com.youku.search.sort.servlet.util.WebUtils" %>
<%@ page import="com.youku.search.sort.servlet.search_page.util.MiscUtils" %>

<%@ page import="org.json.*" %>

<%@ include file="inc/cdn.jsp" %>

<%
String searchType       = (String)request.getAttribute("searchType");
String searchCaption    = (String)request.getAttribute("searchCaption");
String searchExtend     = (String)request.getAttribute("searchExtend");

WebParam webParam       = (WebParam)request.getAttribute("webParam");
%>

<%
String keyword          = (String)request.getAttribute("keyword");
String key1             = (String)request.getAttribute("key1");
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>优酷视频</title>
        <meta name="title" content="优酷视频搜索"> 
        <meta name="description" content="视频搜索 - 优酷网为你提供最为专业全面的视频搜索"> 

        <link href="<%= CDN_SERVER %>/index/css/youku.css" rel="stylesheet" type="text/css" />
        <link href="<%= CDN_SERVER %>/search/css/search.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript">NovaOptions={compatibleMode:true};__LOG__=function(){};Nova={};VERSION='<%= CDN_VERSION %>';</script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/prototype.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/js/nova.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/index/js/common.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/index/js/playlist.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/viewType.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/advince.js"></script>
        <script type="text/javascript" src="<%= STATIC_JS_SERVER %>/search/js/search.js"></script>
    </head>

    <body class="s_soBar">
        <%@ include file="inc/head.jsp" %>

        <form>
	        <div class="s_main">
		        <div class="superSearch">
			        <h1 class="bigTitle">高级搜索</h1>
                    <div class="keywordsSet">
				        <div class="bar">
					        <a class="f_r" href="http://www.youku.com/help/view/fid/3#q7" target="_blank">帮助</a>
    					    <h3 class="title">搜索结果</h3>
	    			    </div>
	
	    			    <div class="item">
					        <div class="label">包含以下<span class="stress">全部</span>关键词</div>
					        <div class="content">
						        <input class="txt" type="text" name="key1" value="<%= WebUtils.htmlEscape(key1) %>"/>
						        <button class="btn" type="button" onclick="advinced_search_submit(this.form,'video')">搜 索</button>
					        </div>
    					    <div class="clear"></div>
    				    </div>
    				    
				        <div class="item">
					        <div class="label">包含以下<span class="stress">完整</span>关键词</div>
					        <div class="content">
						        <input class="txt" type="text" name="key2"/>
					        </div>
					        <div class="clear"></div>
    				    </div>
    				    
				        <div class="item">
					        <div class="label">包含以下<span class="stress">任意一个</span>关键词</div>
					        <div class="content">
						        <input class="txt" type="text" name="key3"/>
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label"><span class="stress">不包含</span>以下关键词</div>
					        <div class="content">
						        <input class="txt" type="text" name="key4"/>
					        </div>
					        <div class="clear"></div>
				        </div>
			        </div>
			        
			        
			        <div class="conditionsSet">
				        <div class="bar">
					        <h3 class="title">搜索范围</h3>
				        </div>
				        
				        <div class="item">
					        <div class="label">关键词位置:</div>
					        <div class="content">
						        <input class="chk" type="checkbox" name="checkAllField" checked="checked" onclick="check_all(this.form.checkAllField,this.form.fields)" /><label>全部</label>
						        <input class="chk" type="checkbox" name="fields" checked="checked" value="title" onclick="check_click(this.form.checkAllField,this.form.fields)"/><label>视频标题</label>
						        <input class="chk" type="checkbox" name="fields" checked="checked" value="memo" onclick="check_click(this.form.checkAllField,this.form.fields)"/><label>视频说明</label>
						        <input class="chk" type="checkbox" name="fields" checked="checked" value="tagsindex" onclick="check_click(this.form.checkAllField,this.form.fields)"/><label>视频标签</label>
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label">上传时间:</div>
					        <div class="content">
						        <select name="limitdate"> 
							        <option selected="selected" value="">全部</option> 
							        <option value="0">今日</option> 
							        <option value="7">本周</option> 
							        <option value="31">本月</option>							
						        </select>
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label">所属分类:</div>
					        <div class="content">
						        <div class="m_b_10">
						        <input class="chk" type="checkbox" name="checkAllCate" checked="checked" onclick="check_all(this.form.checkAllCate,this.form.categories)"/><label>全部分类</label>
						        </div>
						        <div style="padding-left:16px;">
						        <input class="chk" type="checkbox" name="categories" value="91" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>资讯</label>
						        <input class="chk" type="checkbox" name="categories" value="96" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>电影</label>
						        <input class="chk" type="checkbox" name="categories" value="97" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>电视剧</label>
						        <input class="chk" type="checkbox" name="categories" value="86" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>娱乐</label>
						        <input class="chk" type="checkbox" name="categories" value="99" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>游戏</label>
						        <input class="chk" type="checkbox" name="categories" value="100" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>动漫</label>
						        <input class="chk" type="checkbox" name="categories" value="104" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>汽车</label>
						        <input class="chk" type="checkbox" name="categories" value="92" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>原创</label>
						        <input class="chk" type="checkbox" name="categories" value="95" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>音乐</label>
						        <input class="chk" type="checkbox" name="categories" value="89" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>时尚</label>
						        </div>
						        <div style="padding-left:16px;">
						        <input class="chk" type="checkbox" name="categories" value="90" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>母婴</label>
						        <input class="chk" type="checkbox" name="categories" value="88" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>旅游</label>
						        <input class="chk" type="checkbox" name="categories" value="98" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>体育　</label>
						        <input class="chk" type="checkbox" name="categories" value="105" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>科技</label>
						        <input class="chk" type="checkbox" name="categories" value="87" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>教育</label>
						        <input class="chk" type="checkbox" name="categories" value="103" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>生活</label>
						        <input class="chk" type="checkbox" name="categories" value="94" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>搞笑</label>
						        <input class="chk" type="checkbox" name="categories" value="102" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>广告</label>
						        <input class="chk" type="checkbox" name="categories" value="106" checked="checked" onclick="check_click(this.form.checkAllCate,this.form.categories)"/><label>其他</label>
						        </div>
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item"> 
					        <div class="label">视频清晰度:</div> 
					        <div class="content"> 
						        <select name="hd"> 
							        <option selected="selected" value="0">全部</option> 
							        <option value="1">仅显示清晰</option> 
						        </select> 
					        </div> 
					        <div class="clear"></div> 
				        </div> 
				        
				        <div class="item">
					        <div class="label">播放数:</div>
					        <div class="content">
						        <select name="pv"> 
							        <option selected="selected" value="0">全部</option> 
							        <option value="1">1千以下</option> 
							        <option value="2">1千以上</option> 
							        <option value="3">1万以上</option> 
							        <option value="4">10万以上</option> 
							        <option value="5">100万以上</option>							
						        </select> 
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label">评论数:</div>
					        <div class="content">
						        <select name="comments"> 
							        <option selected="selected" value="0">全部</option> 
							        <option value="1">1百以下</option> 
							        <option value="2">1百以上</option> 
							        <option value="3">1千以上</option> 
							        <option value="4">1万以上</option>										
						        </select> 
					        </div>
					        <div class="clear"></div>
				        </div>
    			    </div>
	
	
	    		    <div class="showSet">
				        <div class="bar">
					        <h3 class="title">显示设置</h3>
				        </div>
				        <div class="item">
					        <div class="label">搜索结果显示形式:</div>
					        <div class="content">
					        	<%
					        		String viewType = "detail";
					        		Cookie [] cookies = request.getCookies();
					        		if(cookies!=null && cookies.length>0){
						        		for(Cookie cookie:cookies) {
											if("SearchViewType".equals(cookie.getName())){
												viewType = cookie.getValue();
											}
						        		}
					        		}
					        	%>
						        <select name="viewtype"> 
							        <option <%=(!"img".equals(viewType))?"selected":"" %> value="detail">详细信息</option> 
							        <option <%=("img".equals(viewType))?"selected":"" %> value="img">缩略图</option> 
						        </select> 
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label">搜索结果显示数量:</div>
					        <div class="content">
						        <select name="pagesize">
							        <option selected="selected" value="10">每页显示10条</option>	
							        <option value="20">每页显示20条</option>
							        <option value="50">每页显示50条</option>
							        <option value="100">每页显示100条</option>
						        </select>
					        </div>
					        <div class="clear"></div>
				        </div>
				        
				        <div class="item">
					        <div class="label">搜索结果打开方式:</div>
					        <div class="content">
						        <select name="openmethod"> 
							        <option selected="selected" value="0">在新窗口打开</option> 
							        <option value="1">在当前窗口打开</option> 
						        </select> 
					        </div>
					        <div class="clear"></div>
				        </div>
			        </div>
    		    </div><!--supeSearch end-->
	        </div><!--s_main end-->
        </form>	
        
        
        <!--版权申明及其它-->
        <%@ include file="inc/foot.jsp" %>
        
        <input type="hidden" name="searchTime" id="searchTime" value="<%= System.currentTimeMillis()/1000 %>">
    </body>
</html>
