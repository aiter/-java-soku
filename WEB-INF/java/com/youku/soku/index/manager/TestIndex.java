/**
 * 
 */
package com.youku.soku.index.manager;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

import com.youku.soku.index.analyzer.AnalyzerManager;
import com.youku.soku.index.analyzer.WholeWord;
import com.youku.soku.index.analyzer.WordProcessor;
import com.youku.soku.index.query.VideoQueryManager;

/**
 * @author 1verge
 *
 */
public class TestIndex {
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	public static void create(String indexPath) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		
		Analyzer a = AnalyzerManager.getMyAnalyzer(false);
		IndexWriter indexWriter = new IndexWriter(indexPath,a ,true);
		indexWriter.setMergeFactor(100000);
		indexWriter.setMaxBufferedDocs(10000);
		
		IndexReader indexReader = null;
		indexReader = VideoQueryManager.getInstance().getIndexReader();
		int total = indexReader.numDocs();
		int i = 0;
		int delnum = 0;
		for (;i<total;i++)
		{
			try {
				if (indexReader.isDeleted(i))
				{
					delnum++;
					_log.info("i="+i + " is deleted");
					continue;
				}
				
				Document doc = indexReader.document(i);
				
				Document document = RsToDocument(doc);
				if(document != null){
					indexWriter.addDocument(document);
					if (i %1000 == 0)
						_log.info(i + "\t" + doc.get("site")+"_"+doc.get("vid"));
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch(IllegalArgumentException e)
			{
				e.printStackTrace();
			}
		}
//		_log.info("indexWriter.docCount()="+indexWriter.docCount());
		_log.info("doc count=" + (i) + "\t del count="+delnum + "\t ok count="+(i-delnum));
		if (indexWriter != null){
			indexWriter.optimize();
			indexWriter.close();
		}
		indexReader.close();
	}
	
	
	private static Document RsToDocument(Document document) 
	{
		Document doc = new Document();
		try
		{
			String domain = document.get("domain"); 
			String vid = document.get("vid");
			doc.add(new Field("id", domain+"_"+vid, Store.NO, Index.UN_TOKENIZED));
	        WholeWord tokens_title = WordProcessor.getWholeWord(document.get("title_store"));
	        
	        if (document.get("title_store")!= null && document.get("title_store").startsWith("北京 市民在回龙观拍摄UFO"))
	        	return null;
	        
	        TokenStream ts = AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(document.get("title_store")),tokens_title);
	        Field f_title_index = new Field("title", ts,Field.TermVector.WITH_POSITIONS_OFFSETS);
	        doc.add(f_title_index);
	        ts.close();
	        
	        String uploadTime = document.get("uploadTime");
	        if (uploadTime!= null)
	        	doc.add(new Field("uploadTime", uploadTime, Store.NO, Index.UN_TOKENIZED));
	        
	        
	        //组织tag和分类
	        
	        String tags = document.get("tags");
			if (tags!= null && tags.length() > 0)
			{
		        Field f_tags_index = new Field("tags_index", AnalyzerManager.getBlankAnalyzer().tokenStream("tags_index",new StringReader(tags.toString().replaceAll(","," "))),Field.TermVector.WITH_POSITIONS_OFFSETS);
		        
		        doc.add(f_tags_index);
			}
			
	        doc.add(new Field("domain",domain, Store.NO, Index.UN_TOKENIZED));
	        doc.add(new Field("hd",document.get("hd"), Store.NO, Index.UN_TOKENIZED));
	        
	        doc.add(new Field("site",document.get("site"), Store.NO, Index.UN_TOKENIZED));
	       
	        
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
        return doc;
	}
}
