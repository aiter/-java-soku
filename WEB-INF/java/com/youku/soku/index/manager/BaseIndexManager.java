/**
 * 
 */
package com.youku.soku.index.manager;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

import com.youku.search.index.boost.BoostWriter;
import com.youku.soku.index.analyzer.AnalyzerManager;


/**
 * @author william
 *
 */
public abstract class BaseIndexManager {
	
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	protected String indexPath;
	protected IndexWriter indexWriter = null;
	protected BoostWriter boostWriter = null;
	
	
	public abstract int createIndex();
	
	public String getIndexPath() {
		return indexPath;
	}
	
	public IndexWriter initWriter() throws CorruptIndexException, LockObtainFailedException, IOException
	{
		return initWriter(null,false);
	}
	
	public IndexWriter initWriter(boolean create) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		return initWriter(null,create);
	}
	
	public IndexWriter initWriter(Analyzer a) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		return initWriter(a,false);
	}
	
	public IndexWriter initWriter(Analyzer a,boolean create) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		File dir = new File(indexPath);
		if (!dir.exists()){
			dir.mkdirs();
			create = true;
		}
		else{
			if ( dir.listFiles() == null || dir.listFiles().length == 0) 
				create = true;
		}
		if (a ==null)
			a = AnalyzerManager.getMyAnalyzer(false);
		indexWriter = new IndexWriter(indexPath,a ,create);
		indexWriter.setMergeFactor(10000);
		indexWriter.setMaxBufferedDocs(5000);
		
		boostWriter = new BoostWriter(indexPath,create);
		return indexWriter;
	}
	
	public void closeWriter()
	{
		try {
			if (indexWriter != null){
				indexWriter.close();
				indexWriter = null;
			}
			if (boostWriter != null){
				boostWriter.closeWriter();
				boostWriter = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void optimizeWriter()
	{
		try {
			if (indexWriter != null){
				indexWriter.optimize();
			}
			if (boostWriter != null){
				boostWriter.optimize();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
