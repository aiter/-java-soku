<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.index.*,
				com.youku.soku.*,
				com.youku.soku.analyzer.*,
				com.youku.soku.analyzer.danga.MemCached.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				org.apache.lucene.index.*,
				java.io.*,
				org.apache.lucene.queryParser.*,
				org.apache.lucene.search.*,
				org.apache.lucene.document.Document"%>
<%
Result result = null;
long start=0 ,end = 0;
com.youku.soku.Query query = null;
String keywords = null;


if (request.getParameter("keywords") != null)
{
	keywords = (String)request.getParameter("keywords");
	query = new com.youku.soku.Query();
	query.keywords = keywords;
	query.start=0;
	query.end=50;
	
}
%>
<form name="" action="" method="get">
搜索关键字：<input type="text" name="keywords">
<br>
<input type="submit" value="提交">
</form>

<br>
<%=query!=null?query.toString():""%>
<br>
<%
if (request.getParameter("keywords") != null)
{
		QueryParser  parser = new QueryParser ("title",AnalyzerManager.getBlankAnalyzer());
        org.apache.lucene.search.Query q = null;
		q = parser.parse(request.getParameter("keywords"));

		Hits hits = null;
		
		hits = QueryManager.getInstance().getIndexSearcher().search(q);
			
		int len = 50;
		if (hits.length() <= 50) len = hits.length();

			for (int i =0;i<len;i++)
			{
				Document doc = hits.doc(i);
				out.println("site:"+doc.get("site")+"    title:"+hits.doc(i).get("title_store")+"<br>");
				int docid=hits.id(i);
				
				out.println("docid="+docid+"<br>");
				out.println("total score:"+hits.score(i)+"=lucene("+ hits.score(i) +"<br>");
				System.out.println(QueryManager.getInstance().getIndexSearcher().explain(q,hits.id(i))+"<br>");
			}
	
}
%>