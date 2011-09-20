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
				com.youku.search.index.query.*,
				org.apache.lucene.document.Document"%>
<%
int curpage = DataFormat.parseInt(request.getParameter("p"));
String type = request.getParameter("type");

if (curpage == 0) curpage=1;
int order = DataFormat.parseInt(request.getParameter("order"));
IndexReader indexReader = null;


if (type.equals("video"))
	indexReader = IndexReader.open(ServerManager.getVideoIndexPath()+order);
else if (type.equals("folder"))
	indexReader = IndexReader.open(ServerManager.getFolderIndexPath()+order);
else if (type.equals("user"))
	indexReader = IndexReader.open(ServerManager.getUserIndexPath()+order);
else if (type.equals("barpost"))
	indexReader = IndexReader.open(ServerManager.getBarpostIndexPath()+order);
else if (type.equals("pk"))
	indexReader = IndexReader.open(ServerManager.getPkIndexPath()+order);



int total = indexReader.numDocs();

out.println("total="+total + "</br>");

int pagesize=50;
int totalpage  = (total+pagesize-1)/pagesize;
for (int i = total-((curpage-1)*pagesize+1);i>total-curpage*pagesize;i--)
{
	Document doc = indexReader.document(i);
	if (type.equals("video"))
		out.println("vid="+doc.get("id")+ " title=" +doc.get("title") + "</br>");
	else if (type.equals("folder"))
		out.println("pkfolder="+doc.get("pkfolder")+ " title=" +doc.get("title") + "</br>");
	else if (type.equals("user"))
		out.println("pkuser="+doc.get("pkuser")+ " username=" +doc.get("realname") + "</br>");
	else if (type.equals("barpost"))
		out.println("pkpost="+doc.get("pkpost")+ " subject=" +doc.get("subject") + "</br>");
	else if (type.equals("pk"))
		out.println("pkid="+doc.get("pkid")+ " pk_name=" +doc.get("pk_name") + "</br>");
}
try{
	indexReader.close();
}catch(Exception e){}
%>