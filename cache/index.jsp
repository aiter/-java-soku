<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*" %>

<%
String key = request.getParameter("keyword");
String cacheType = request.getParameter("ctype");
String cacheKey = null;
String clear = request.getParameter("clear");
Object theClass = null;
Object value = null;

if(key != null){
	com.youku.search.sort.Parameter p = new com.youku.search.sort.Parameter(request);
	if("2".equals(cacheType)){
		cacheKey = com.youku.search.sort.util.bridge.SearchUtil.CacheKey.music(p.query,p.type);
	}else if("1".equals(cacheType)) {
		cacheKey = com.youku.search.sort.util.bridge.SearchUtil.CacheKey.common(p);
	}else {
		cacheKey = key;
	}
    value = com.youku.search.sort.MemCache.cacheGet(cacheKey);
    if(value == null){
        //
    }else{
        theClass = value.getClass();        
        if(clear != null){
            com.youku.search.sort.MemCache.cacheSet(key, " ", 1);
        }
        
    }
}
%>

<html>
<head>
<title>MemCache测试页面</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
</head>
<body>
<form method="get" enctype="application/x-www-form-urlencoded">
<p>
<select name="ctype">
	<option value="1">视频结果</optioin>
	<option value="2" <%="2".equals(cacheType)?"selected":"" %>>版权音乐</optioin>
	<option value="0" <%="0".equals(cacheType)?"selected":"" %>>直接输入key</optioin>
</select>
key: <input type="text" name="keyword" value="<%= key %>" size="50" /></p>
<p>清空key: <input type="checkbox" name="clear" value="1" /></p>
<p><input type="submit" value="提交" /></p>
<p>查询结果：</p>
<ol>
	<li>cacheKey: <%= cacheKey %></li>
    <li>class: <%= theClass %></li>	
	<li>value: <%= value %></li>
	
</ol>
</form>
</body>
</html>