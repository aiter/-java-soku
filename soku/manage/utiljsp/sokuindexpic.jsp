<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.youku.search.sort.MemCache, com.youku.soku.manage.service.SokuIndexPicService" %>
<%
String jsonStr = SokuIndexPicService.generateJSONResult();
MemCache.cacheSet("index_pic", jsonStr, 36000);

//MemCache.cacheSet("index_pic", "xxxxx", 36000);
String r = (String) MemCache.cacheGet("index_pic");
out.print(r);
%>