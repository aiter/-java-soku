<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.*"%>
<%
String k = request.getParameter("k");
Checker checker = Checker.getInstance();
if (k != null)
{
	long start = System.currentTimeMillis();
	out.println(checker.check(k));
	System.out.println("cost=" + (System.currentTimeMillis() - start));
}
%>