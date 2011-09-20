<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.console.operate.log.*,java.util.*,com.youku.search.util.*"%>
<%
long s=System.currentTimeMillis();
String end = request.getParameter("end");
if(end==null) end=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
String start = request.getParameter("start");
if(start==null) start=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
System.out.println(new Date()+"--start--每周关键词统计预处理,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
KeywordWeekUnion ku=new KeywordWeekUnion();
ku.insertTopKeywords(new StringBuilder(start).append("_").append(end).toString());
System.out.println(new Date()+"--end--每周关键词统计预处理,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));
 %>