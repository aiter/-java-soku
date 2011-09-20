<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.config.*,
				com.youku.soku.index.server.*,
				com.youku.soku.util.*,
				com.youku.soku.index.*,
				com.youku.soku.index.manager.db.*,
				com.youku.soku.index.query.*,
				org.apache.lucene.index.Term,
				org.apache.lucene.index.IndexWriter,
				com.youku.soku.index.analyzer.AnalyzerManager,
				com.youku.search.util.*"%>

<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");


String vids = request.getParameter("vids");
_log.info("delete video:" + vids);
if (vids != null)
{
	String[] vidarr = vids.split(",");
	if (vidarr!= null && vidarr.length > 0){

		IndexWriter indexWriter = new IndexWriter(Config.getVideoIndexPath(),AnalyzerManager.getMyAnalyzer(false) ,false);
		try
		{
			for (String vid:vidarr)
			{
				if (vid != null && !vid.trim().isEmpty())
				{
					String[] arr = vid.split("_");
					if (arr != null && arr.length == 2){
						int site = DataFormat.parseInt(arr[0]);
						int id = DataFormat.parseInt(arr[1]);

						//获取到site和视频id
						VideoQueryManager.getInstance().deleteVideoByDocVideoId(site,id);//从内存删除
						indexWriter.deleteDocuments(new Term("id",site+"_"+id ));
					}
				}
			}
		}
		finally
		{
			indexWriter.flush();
			indexWriter.close();
		}

	}
}
%>