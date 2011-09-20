<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.analyzer.*
				com.youku.search.analyzer.danga.MemCached.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.data.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				org.apache.lucene.search.*,
				org.apache.lucene.index.*,
				com.youku.search.index.query.*,
				org.apache.lucene.document.Document,
				org.apache.lucene.analysis.*,
				com.youku.search.console.teleplay.*,
				java.io.*,
				com.youku.search.analyzer.*,
				org.json.*,
				com.youku.search.config.*,
				com.youku.search.index.*,
				org.apache.lucene.search.FieldCache"%>



<%
int[] tc = FieldCache.DEFAULT.getInts(VideoQueryManager.getInstance().getIndexReader(),"total_comment");
out.println(tc.length);
%>