<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.server.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.recomend.*,
				java.sql.*"%>
<%
	Constance.initFilterList();
	Connection conn = DateConn.getConn("filter");
	DateRead.createFilterListByDB(conn);
	DateConn.releaseConn(conn);
	
	System.out.println("Constance.tempFilterList:"+Constance.tempFilterList.size());
	System.out.println("Constance.allmatch_tempFilterList:"+Constance.allmatch_tempFilterList.size());

	if (Constance.filterList == null)
		Constance.filterList =  new ArrayList();

	if (Constance.allmatch_filterList == null)
		Constance.allmatch_filterList =  new ArrayList();

	System.out.println(Arrays.toString(Constance.tempFilterList.toArray()));
	System.out.println(Arrays.toString(Constance.allmatch_tempFilterList.toArray()));

	if(!Constance.filterList.equals(Constance.tempFilterList) || !Constance.allmatch_filterList.equals(Constance.allmatch_tempFilterList))
	{
		Constance.filterList.clear();
		Constance.filterList = Constance.tempFilterList;
		DateRead.list2Regex(Constance.filterList);
		Constance.allmatch_filterList.clear();
		Constance.allmatch_filterList = Constance.allmatch_tempFilterList;
		//重新建索引
		Request.requestGet("http://127.0.0.1/index/interface/index_create.jsp?order=1&type="+Constant.QueryField.STAT);
	}
%>