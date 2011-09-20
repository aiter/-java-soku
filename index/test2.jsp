<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.query.*,
				com.youku.search.analyzer.*
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				com.youku.search.index.bin.*,
				java.io.*,
				com.youku.search.index.server.ServerManager,
				com.youku.search.index.entity.*"%>

<%
ObjectContainer container = VideoQueryManager.getInstance().objContainer;
HashMap<Integer,Object> map = container.map;

Set<Integer> set = map.keySet();
for(int id:set){
	Object o = map.get(id);
	Video v = (Video)o;
	if (com.youku.search.index.MemCached.cacheGet("video_" + v.vid) == null){
		boolean result = com.youku.search.index.MemCached.cacheSet("video_"+v.vid,v,24 * 60 * 60*1000);
		System.out.println("put to cache "+ v.vid +" result:" + result);
//		try{
//			Thread.sleep(10);
//		}catch(Exception e){}
	}
	
}

//boolean result = com.youku.search.index.MemCached.cacheSet("video_"+video.vid,video,24 * 60 * 60*1000);
//out.println("put to cache result:" + result);
%>