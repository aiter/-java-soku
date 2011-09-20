<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.index.*,
				com.youku.soku.*,
				com.youku.soku.config.Config,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				org.apache.lucene.index.*,
				java.io.*,
				com.youku.search.util.*,
				 com.youku.soku.index.manager.*,
				org.apache.lucene.queryParser.*,
				org.apache.lucene.search.*,
				org.apache.lucene.document.*,
				org.apache.lucene.index.IndexWriter,com.youku.soku.index.manager.db.*,com.youku.so.om.*,com.youku.soku.index.analyzer.AnalyzerManager"%>
<%
org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");

List<AssembleDoc> videos = null;
Site site = SiteManager.getInstance().getSite(15);

try {
	IndexWriter indexWriter = new IndexWriter("/opt/index/video/online/15",AnalyzerManager.getMyAnalyzer(false) ,false);
	indexWriter.setMergeFactor(10000);
	indexWriter.setMaxBufferedDocs(5000);


	for (int i=1;i<1000;i++){
		indexWriter.deleteDocuments(new Term("id","15_"+i ) );
	}

		//继续添加
		int  last = 1;
all:	while (true)
		{
			videos = VideoManager.getInstance().getVideos(last,1000,site);
			if (videos!=null && videos.size()>0)
			{
				//先把视频添加到索引
				_log.info("新增video："+videos.size());
				for (int i=0;i<videos.size();i++)
				{
					AssembleDoc doc = videos.get(i);
					if(doc != null && doc.getDoc()!=null){
						if (DataFormat.parseInt(doc.get("vid")) >= 67063)
						{
							break all;
						}
						try {
							indexWriter.addDocument(doc.getDoc());
							_log.info("add video:"+doc.get("id"));
						
						} catch (Exception e) {
							_log.error("addIndex Error:" + e.getMessage(),e);
						}
					}
				}
				last = DataFormat.parseInt(videos.get(videos.size()-1).get("vid"));
		}
	}
	indexWriter.optimize();
	indexWriter.close();
} catch (Exception e) {
	e.printStackTrace();
	_log.error("addIndex Error:"+e.getMessage(),e);
}

_log.info("over");
%>