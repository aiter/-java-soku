<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				org.apache.lucene.index.*,
				org.apache.lucene.document.Document"%>

<%
int curpage = DataFormat.parseInt(request.getParameter("p"));
String type = request.getParameter("type");
if (curpage == 0) curpage=1;
int total = 0;

IndexReader indexReader = null;

if (type.equals("video"))
	indexReader = VideoQueryManager.getInstance().getIndexReader();
else if (type.equals("folder"))
	indexReader = FolderQueryManager.getInstance().getIndexReader();
else if (type.equals("user"))
	indexReader = UserQueryManager.getInstance().getIndexReader();
else if (type.equals("barpost"))
	indexReader = BarpostQueryManager.getInstance().getIndexReader();
else if (type.equals("pk"))
	indexReader = PkQueryManager.getInstance().getIndexReader();


total = indexReader.numDocs();


int pagesize=50;
int totalpage  = (total+pagesize-1)/pagesize;
for (int i = total-((curpage-1)*pagesize+1);i>total-curpage*pagesize;i--)
{
	Document doc = indexReader.document(i);
	if (type.equals("video"))
		out.println("vid="+doc.get("vid")+ " title=" +doc.get("title_index_full") + "</br>");
	else if (type.equals("folder"))
		out.println("pkfolder="+doc.get("pkfolder")+ " md5=" +doc.get("md51") + "</br>");
	else if (type.equals("user"))
		out.println("pkuser="+doc.get("pkuser")+ " username=" +doc.get("realname") + "</br>");
	else if (type.equals("barpost"))
		out.println("pkpost="+doc.get("pkpost")+ " subject=" +doc.get("subject") + "</br>");
	else if (type.equals("pk"))
		out.println("pkid="+doc.get("pkid")+ " pk_name=" +doc.get("pk_name") + "</br>");
}

%>