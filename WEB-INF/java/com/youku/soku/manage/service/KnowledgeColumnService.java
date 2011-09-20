package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.library.load.KnowledgeColumn;
import com.youku.soku.library.load.KnowledgeColumnPeer;
import com.youku.soku.manage.common.PageInfo;

public class KnowledgeColumnService {
	
	private static Logger log = Logger.getLogger(KnowledgeColumnService.class);


	

	
	public static List<KnowledgeColumn> findKnowledgeColumnByName(String namesName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(KnowledgeColumnPeer.NAME, namesName);
    	
    	List<KnowledgeColumn> namesList = KnowledgeColumnPeer.doSelect(crit);
    	return namesList;
    	
    }
	
	public static List<KnowledgeColumn> findKnowledgeColumnByParentId(int id) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(KnowledgeColumnPeer.PARENT_ID, id);
    	
    	List<KnowledgeColumn> namesList = KnowledgeColumnPeer.doSelect(crit);
    	return namesList;
    	
    }
    
    /**
     * <p>Find all the names</p>
     * @return the list of the names object
     */
    public static List<KnowledgeColumn> findKnowledgeColumn(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<KnowledgeColumn> namesList = KnowledgeColumnPeer.doSelect(crit);
    	return namesList;
    	
    }
    
    /**
     * <p>Find names list using pagination</p>
     * @return the list of the names object
     */
    public static LargeSelect findKnowledgeColumnPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	KnowledgeColumnPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKnowledgeColumnPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) KnowledgeColumnPeer.executeQuery("SELECT count(*) FROM " + KnowledgeColumnPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = KnowledgeColumnPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKnowledgeColumnPagination(PageInfo pageInfo, String searchWord, int parentId) throws Exception {
    	
    	
    	Criteria crit = new Criteria();
        
        String whereSql = "";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(KnowledgeColumnPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql = " WHERE " + KnowledgeColumnPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        } else if(parentId == 0){
        	whereSql = " WHERE level = 1";
        	crit.add(KnowledgeColumnPeer.LEVEL, 1);
        } else {
        	whereSql = " WHERE parent_id = " + parentId;
        	crit.add(KnowledgeColumnPeer.PARENT_ID, parentId);
        }
        
        
     
        
        int totalRecord = ((Record) KnowledgeColumnPeer.executeQuery("SELECT count(*) FROM " + KnowledgeColumnPeer.TABLE_NAME + whereSql, KnowledgeColumnPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
       
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
      
        
        
        //crit.addDescendingOrderByColumn(KnowledgeColumnPeer.SORT);
        //crit.addDescendingOrderByColumn(KnowledgeColumnPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(KnowledgeColumnPeer.CREATE_TIME);
        
        List result = KnowledgeColumnPeer.doSelect(crit);        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
	public static List<KnowledgeColumn> searchKnowledgeColumn(String searchWord) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(KnowledgeColumnPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
    	
    	List<KnowledgeColumn> namesList = KnowledgeColumnPeer.doSelect(crit);
    	return namesList;
    	
    }


}
