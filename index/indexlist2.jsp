<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				org.apache.lucene.document.Document"%>

<%
int curpage = DataFormat.parseInt(request.getParameter("p"));
if (curpage == 0) curpage=1;

int total = StatQueryManager.getInstance().getIndexReader().numDocs();
int pagesize=50;
int totalpage  = (total+pagesize-1)/pagesize;
for (int i = total-((curpage-1)*pagesize+1);i>total-curpage*pagesize;i--)
{
	Document doc = StatQueryManager.getInstance().getIndexReader().document(i);
	out.println("keyword="+doc.get("keyword")+"||name="+doc.get("keyword_py_index") + "</br>");
}

%>