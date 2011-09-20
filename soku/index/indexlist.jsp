<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.util.*,
				com.youku.soku.index.query.*,
				org.apache.lucene.index.*,
				org.apache.lucene.document.Document,
				com.youku.search.util.*"%>

<%
int curpage = DataFormat.parseInt(request.getParameter("p"));
if (curpage == 0) curpage=1;
int total = 0;

IndexReader indexReader = null;

indexReader = VideoQueryManager.getInstance().getIndexReader();

total = indexReader.numDocs();


int pagesize=50;
int totalpage  = (total+pagesize-1)/pagesize;
/**
for (int i = total-((curpage-1)*pagesize+1);i>total-curpage*pagesize;i--)
{
	Document doc = indexReader.document(i);
	out.println("site="+doc.get("site")+"vid="+doc.get("vid")+ " title=" +doc.get("title_store") + " create_day:"+ VideoQueryManager.getInstance().get(i) +"</br>");
	out.println(VideoQueryManager.getInstance().isInIndex(DataFormat.parseInt(doc.get("vid")),DataFormat.parseInt(doc.get("site")))+"<br/>");

}
*/

for (int i = (curpage-1)*pagesize;i<curpage*pagesize-1;i++)
{
	if (!indexReader.isDeleted(i))
	{
		Document doc = indexReader.document(i);
		
		out.println("site="+doc.get("site")+"vid="+doc.get("vid")+ " title=" +doc.get("title_store") + " create_day:"+ VideoQueryManager.getInstance().get(i) +"</br>");
		out.println(VideoQueryManager.getInstance().isInIndex(DataFormat.parseInt(doc.get("vid")),DataFormat.parseInt(doc.get("site")))+"<br/>");
	}

}
%>