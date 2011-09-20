package com.youku.soku.manage.service;



import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.Hotword;
import com.youku.soku.manage.torque.HotwordPeer;

public class HotwordService extends BaseService{
	
	/**
	 * Find hot words on index page
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Hotword> findIndexVideo() throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(HotwordPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.addDescendingOrderByColumn(HotwordPeer.SORT);
		
		List<Hotword> hotwordList = HotwordPeer.doSelect(crit);
		return hotwordList;
	}
	
	/**
	 * Find hot words on index page
	 * @param itemId
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Hotword> findIndexVideo(int itemId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(HotwordPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.add(HotwordPeer.ITEM_ID, itemId);
		crit.addDescendingOrderByColumn(HotwordPeer.SORT);
		
		List<Hotword> hotwordList = HotwordPeer.doSelect(crit);
		return hotwordList;
	}
	
	public static List<Hotword> findHotwordByName(String hotwordName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(HotwordPeer.NAME, hotwordName);
    	
    	List<Hotword> hotwordList = HotwordPeer.doSelect(crit);
    	return hotwordList;
    	
    }
	
	public static List<Hotword> findHotwordByItemId(int itemId) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(HotwordPeer.ITEM_ID, itemId);
    	
    	List<Hotword> hotwordList = HotwordPeer.doSelect(crit);
    	return hotwordList;
    	
    }
    
    /**
     * <p>Find all the hotword</p>
     * @return the list of the hotword object
     */
    public static List<Hotword> findHotword() throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Hotword> hotwordList = HotwordPeer.doSelect(crit);
    	return hotwordList;
    	
    }
    
    /**
     * <p>Find hotword list using pagination</p>
     * @return the list of the hotword object
     */
    public static LargeSelect findHotwordPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	HotwordPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findHotwordPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) HotwordPeer.executeQuery("SELECT count(*) FROM " + HotwordPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = HotwordPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findHotwordPagination(PageInfo pageInfo, String searchWord, int itemId) throws Exception {
    	
    	
    	Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(HotwordPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + HotwordPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        if(itemId > 0) {
        	crit.add(HotwordPeer.ITEM_ID, itemId);
        	whereSql += "AND " + HotwordPeer.ITEM_ID + "=" + itemId;
        }
        
        int totalRecord = ((Record) HotwordPeer.executeQuery("SELECT count(*) FROM " + HotwordPeer.TABLE_NAME + whereSql, HotwordPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
       
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        if(searchWord != null && !"".equals(searchWord)) {
        	crit.add(HotwordPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);	
        }
        
        if(itemId >0) {
        	crit.add(HotwordPeer.ITEM_ID, itemId);
        }
        
        crit.addDescendingOrderByColumn(HotwordPeer.SORT);
        //crit.addDescendingOrderByColumn(HotwordPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(HotwordPeer.CREATE_DATE);
        
        List result = HotwordPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
}
