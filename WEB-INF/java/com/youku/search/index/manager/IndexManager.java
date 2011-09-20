/**
 * 
 */
package com.youku.search.index.manager;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;

import com.youku.search.index.manager.AffectManager.Affect;
import com.youku.search.index.query.BaseQuery;
import com.youku.search.index.query.FolderQueryManager;
import com.youku.search.index.query.UserQueryManager;
import com.youku.search.index.query.VideoQueryManager;

/**
 * @author william
 *
 */
public class IndexManager {
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	private IndexManager(){
		
	}
	private static IndexManager self = null;

	public synchronized static IndexManager getInstance(){
		
		if(self == null){
			self = new IndexManager();
			self.init();
		}
		return self;
	}
	private void init(){
		
	}
	
	public int delete(AffectManager.Type type)
	{
		List<Integer> affects = AffectManager.getInstance().getAllDeleteFromFile(type);
		
		if (affects == null || affects.isEmpty())
			return 0;
		
		BaseQuery queryManager = null;
		String index = null;
		if (type.name().equals("VIDEO")){
			queryManager = VideoQueryManager.getInstance();
			index = "vid";
		}
		else if (type.name().equals("FOLDER")){
			queryManager = FolderQueryManager.getInstance();
			index = "pkfolder";
		}
		else if (type.name().equals("USER")){
			queryManager = UserQueryManager.getInstance();
			index = "pkuser";
		}
		else
			return 0;
		
		
		int rows = 0;
		
		for (Integer affect:affects){
			try{
				
				int row = queryManager.getIndexReader().deleteDocuments(new Term(index,String.valueOf(affect)));
				_log.info("delete id:" + affect + " affect row :" + row);
				rows += row;
			}catch(Exception e)
			{
				_log.error(e.getMessage(),e);
				continue;
			}
		}
		_log.info("delete :"+ type+",changed :" + rows);
		if (rows >0){
			try {
				queryManager.getIndexReader().flush();
			} catch (CorruptIndexException e) {
				_log.error(e.getMessage(),e);
			} catch (IOException e) {
				_log.error(e.getMessage(),e);
			}
		}
		
		return rows;
	}
	
	
	
}
