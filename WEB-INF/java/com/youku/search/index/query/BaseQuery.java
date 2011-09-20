/**
 * 
 */
package com.youku.search.index.query;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.youku.search.index.boost.BoostReader;
import com.youku.search.index.schedule.UpdateIndexSchedule;
import com.youku.search.util.Constant;
import com.youku.search.util.DataFormat;

/**
 * @author william
 *
 */
public abstract class BaseQuery {
	
	private static ThreadLocal<IndexSearcher> localSeracher = new ThreadLocal<IndexSearcher>();
	
	protected static SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter("<span class=\"highlight\">","</span>");
	protected static String fragmentSeparator = "...";
	
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
	
	private IndexReader tmp_reader = null;
	private IndexSearcher tmp_searcher = null;
	private Directory tmp_dir = null;
	
	
	/**
	 * 索引加载到内存
	 *
	 */
	public synchronized void initReader()
	{
		try {
			if (
					indexType == Constant.QueryField.VIDEO ||
					indexType == Constant.QueryField.FOLDER ||
					indexType == Constant.QueryField.STAT ){
				dir = new RAMDirectory(indexPath);
			}
			else{
				dir = FSDirectory.getDirectory(indexPath);
			}
			
			indexReader = IndexReader.open(dir);
			indexSearcher = new IndexSearcher(indexReader);
			
			if (indexType == Constant.QueryField.VIDEO || 
					indexType == Constant.QueryField.FOLDER)
				BoostReader.read(indexPath);
			
		} catch (IOException e) {
			System.err.print("error:" + e.getMessage());
		}
	}
	
	
	public void reopenIndex() throws CorruptIndexException, IOException
	{
		long start = System.currentTimeMillis();
		if (
				indexType == Constant.QueryField.VIDEO ||
				indexType == Constant.QueryField.FOLDER ||
				indexType == Constant.QueryField.STAT ){
			System.out.println("开始初始化索引");
			Directory dir1 = new RAMDirectory(indexPath);
			System.out.println("打开dir完成，用时："+(System.currentTimeMillis()-start));
			IndexReader indexReader1 = IndexReader.open(dir1);
			System.out.println("打开reader完成，用时："+(System.currentTimeMillis()-start));
			IndexSearcher indexSearcher1 = new IndexSearcher(indexReader1);
			System.out.println("打开索引完成，用时："+(System.currentTimeMillis()-start));
			
			tmp_reader= indexReader;
			tmp_searcher = indexSearcher;
			tmp_dir = dir;
			synchronized(indexReader){
				this.indexReader = indexReader1;
			}
			synchronized(indexSearcher){
				this.indexSearcher = indexSearcher1;
			}
			synchronized(dir){
				dir = dir1;
			}
			if (indexType == Constant.QueryField.VIDEO || 
					indexType == Constant.QueryField.FOLDER)
				BoostReader.reRead(indexPath);
			
		}
		else{
			
//			IndexReader r = indexReader.reopen();
//			if (r!=indexReader)
//			{
//				 indexSearcher.close();
//				 indexReader.close();
//				 indexReader = r;
//			     //利用indexReader.reopen()获取新的indexReader,并作为IndexSearcher的参数创建一个新的indexSearcher
//			     indexSearcher=new IndexSearcher(indexReader);
//			}
			
			System.out.println("开始初始化索引");
			
			Directory dir1 = FSDirectory.getDirectory(indexPath);
			System.out.println("打开dir完成，用时："+(System.currentTimeMillis()-start));
			IndexReader indexReader1 = IndexReader.open(dir1);
			System.out.println("打开reader完成，用时："+(System.currentTimeMillis()-start));
			IndexSearcher indexSearcher1 = new IndexSearcher(indexReader1);
			
			System.out.println("打开索引完成，用时："+(System.currentTimeMillis()-start));
			
			tmp_reader= indexReader;
			tmp_searcher = indexSearcher;
			tmp_dir = dir;
			
			synchronized(indexReader){
				this.indexReader = indexReader1;
			}
			synchronized(indexSearcher){
				this.indexSearcher = indexSearcher1;
			}
			synchronized(dir){
				dir = dir1;
			}
			
		}
		
		
//		启动关闭临时索引计划，1分钟后执行
		Timer m_timer = new Timer();
		UpdateIndexSchedule s = new UpdateIndexSchedule(this);
//		m_timer.schedule(s,DataFormat.getNextMinute(new Date(),1));
		m_timer.schedule(s,DataFormat.getDate(new Date(),10));
		
		
	}
	public synchronized void closeTmpReader() throws IOException
	{
		if (tmp_reader != null)
		{
			tmp_reader.close();
			tmp_reader = null;
		}
		if (tmp_searcher != null)
		{
			tmp_searcher.close();
			tmp_searcher = null;
		}
		if (tmp_dir != null)
		{
			tmp_dir.close();
			tmp_dir = null;
		}
	}
	
	public IndexSearcher getLocalSearcher(){
		IndexSearcher thisSearcher = localSeracher.get();
		if (thisSearcher == null){
			localSeracher.set(indexSearcher);
			return indexSearcher;
		}
		return thisSearcher;
	}
	
	public void removeLocalSearcher()
	{
		localSeracher.remove();
	}
}
