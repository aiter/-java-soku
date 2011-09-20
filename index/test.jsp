<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.*
				com.youku.search.analyzer.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				java.io.*"%>

<%
long start = System.currentTimeMillis();
for (int i=0;i<1000;i++){
	com.youku.analyzer.danga.MemCached.Result s = AnalyzerManager.analyzeWord("我爱天安门");
	//out.println(s);
}
long end = System.currentTimeMillis();
out.println(end-start);
%>