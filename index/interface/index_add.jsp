<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.analyzer.*
				com.youku.search.analyzer.danga.MemCached.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				org.apache.lucene.search.*,
				org.apache.lucene.index.*,
				com.youku.search.index.query.*"%>
<%

VideoIndexManager m = new VideoIndexManager(1,100,ServerManager.getVideoIndexPath()+9);
m.addIndex(58721836,58721837);


%>