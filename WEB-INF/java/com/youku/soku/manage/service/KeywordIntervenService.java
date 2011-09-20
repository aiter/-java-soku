package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.KeywordInterven;
import com.youku.soku.manage.torque.KeywordIntervenPeer;

public class KeywordIntervenService extends BaseService{
	

	
	/**
	 * Find the item with the name of itemName
	 * @param itemName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<KeywordInterven> findKeywordIntervenByName(String itemName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(KeywordIntervenPeer.NAME, itemName);
    	
    	List<KeywordInterven> itemList = KeywordIntervenPeer.doSelect(crit);
    	return itemList;
    	
    }
    
    /**
     * <p>Find all the item</p>
     * @return the list of the item object
     */
    public static List<KeywordInterven> findKeywordInterven(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<KeywordInterven> itemList = KeywordIntervenPeer.doSelect(crit);
    	return itemList;
    	
    }
    
    /**
     * <p>Find item list using pagination</p>
     * @return the list of the item object
     */
    public static LargeSelect findKeywordIntervenPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	KeywordIntervenPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKeywordIntervenPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) KeywordIntervenPeer.executeQuery("SELECT count(*) FROM " + KeywordIntervenPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = KeywordIntervenPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKeywordIntervenPagination(PageInfo pageInfo, String searchWord, int itemId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(KeywordIntervenPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + KeywordIntervenPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) KeywordIntervenPeer.executeQuery("SELECT count(*) FROM " + KeywordIntervenPeer.TABLE_NAME + whereSql, KeywordIntervenPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        List result = KeywordIntervenPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
