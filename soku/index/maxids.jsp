<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.om.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.youku.search.util.*"%>
<%
List<Site> sites = SiteManager.getInstance().getSiteList();
	int[] maxids = IndexManager.getMaxIds(sites);
	if (maxids == null || maxids.length == 0){
		return;
	}
	
	StringBuffer sb = new StringBuffer();
	for (int s:maxids){
		sb.append(s).append(",");
	}

	String ids = sb.substring(0,sb.length()-1);

	out.println(ids);
%>