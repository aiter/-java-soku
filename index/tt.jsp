<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.*
				com.youku.search.analyzer.*
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
				org.apache.lucene.search.highlight.*,
				org.apache.lucene.document.Document"%>
<%
IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		IndexWriter indexWriter = null;
		try {
	    	 indexWriter = new IndexWriter("/opt/index/video/online/9",AnalyzerManager.getMyAnalyzer(false) ,false);
	    	//删除boost
	    	indexReader = IndexReader.open("/opt/index/video/online/9");
			indexSearcher =  new IndexSearcher(indexReader);
			int nums = indexReader.numDocs();
			
			for (int i =0;i<nums;i++)
			{
				if (!indexReader.isDeleted(i))
				{
					Document doc = indexReader.document(i);
					int vid = DataFormat.parseInt(doc.get("vid"));
					if (vid <= 54002075){
						System.out.println("delete " + vid);
						//标记物理删除
						indexWriter.deleteDocuments(new Term("vid",vid+""));
					}
				}
				
			}
			
			
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}
		finally{
			
			indexReader.close();
			indexSearcher.close();

			indexWriter.optimize();
			indexWriter.close();
		}%>