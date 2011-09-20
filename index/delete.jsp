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
int order = DataFormat.parseInt(request.getParameter("order"));
if (order  > 0)
{
	IndexReader indexReader = IndexReader.open(ServerManager.getVideoIndexPath()+order);
	int total = indexReader.numDocs();
	int row = 0;
	for (int i=0;i<total;i++)
	{
		Document doc = indexReader.document(i);
		String vid = doc.get("vid");
		List list = org.apache.torque.util.BasePeer.executeQuery("select pk_video from t_video where is_valid=1 and pk_video="+vid);
		if (list == null || list.isEmpty())
		{
			System.out.println("del:"+vid);
			Term term = new Term("vid", vid);
			indexReader.deleteDocuments(term);
			row ++;
		}
		indexReader.flush();
	}
	System.out.println(order + "删除视频："+row);
}
%>