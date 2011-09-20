/**
 * 
 */
package com.youku.top.index.query;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.youku.top.index.boost.BoostReader;


/**
 * @author william
 *
 */
public abstract class BaseQuery {
	
	protected IndexSearcher indexSearcher = null;
	protected IndexReader indexReader = null;
	protected Directory dir = null;
	protected String indexPath = null;
	protected int indexType ;//索引类型
	
	public IndexReader getIndexReader() {
		return indexReader;
	}
	
	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}
	
	
	/**
	 * 索引加载到内存
	 *
	 */
	public synchronized void initReader()
	{
		try {
			
			File f = new File(indexPath);
			if(!f.exists()) {
				indexSearcher = null;
				return;
			}
			dir = FSDirectory.getDirectory(indexPath);
			
			indexReader = IndexReader.open(dir);
			indexSearcher = new IndexSearcher(indexReader);
			
			BoostReader.read(indexPath);
			
		} catch (IOException e) {
			System.err.print("error:" + e.getMessage());
		}
	}
	
	public synchronized void close(){
		try{
			if(null!=indexSearcher)
				indexSearcher.close();
			if(null!=indexReader)
				indexReader.close();
			dir.close();
		}catch(Exception e){
			e.printStackTrace();
			System.err.print("error:" + e.getMessage());
		}
	}
	
	public synchronized void reopenIndex()
	{
		try {
		File f = new File(indexPath);
		if(!f.exists()) {
			indexSearcher = null;
			return;
		}
		
		dir = FSDirectory.getDirectory(indexPath);
		
		if(null==indexReader)
			indexReader = IndexReader.open(dir);
		else indexReader.reopen();
		indexSearcher = new IndexSearcher(indexReader);
		
		BoostReader.read(indexPath);
		} catch (IOException e) {
			System.err.print("error:" + e.getMessage());
		}
		
	}

}
