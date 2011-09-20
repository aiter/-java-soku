package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.ShieldCategory;
import com.youku.soku.manage.torque.ShieldCategoryPeer;

public class ShieldCategoryService extends BaseService{
	
	/**
	 * Find the shieldCategory display on index page
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static List<ShieldCategory> findIndexShieldCategory(Connection conn) throws Exception{
		Criteria crit = new Criteria();
		
		List<ShieldCategory> shieldCategoryList = ShieldCategoryPeer.doSelect(crit);
		return shieldCategoryList;
	}
	
	/**
	 * Find the shieldCategory with the name of shieldCategoryName
	 * @param shieldCategoryName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<ShieldCategory> findShieldCategoryByName(String shieldCategoryName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(ShieldCategoryPeer.NAME, shieldCategoryName);
    	
    	List<ShieldCategory> shieldCategoryList = ShieldCategoryPeer.doSelect(crit);
    	return shieldCategoryList;
    	
    }
    
    /**
     * <p>Find all the shieldCategory</p>
     * @return the list of the shieldCategory object
     */
    public static List<ShieldCategory> findShieldCategory(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldCategory> shieldCategoryList = ShieldCategoryPeer.doSelect(crit);
    	return shieldCategoryList;
    	
    }
    
    /**
     * <p>build a shieldCategory map</p>
     * @return the map of the shieldCategory object
     */
    public static Map<Integer, String> getShieldCategoryMap(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldCategory> shieldCategoryList = ShieldCategoryPeer.doSelect(crit);
    	
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	for(ShieldCategory shieldCategory : shieldCategoryList) {
    		map.put(shieldCategory.getId(), shieldCategory.getName());
    	}
    	
    	return map;
    	
    }
    
    /**
     * <p>Find shieldCategory list using pagination</p>
     * @return the list of the shieldCategory object
     */
    public static LargeSelect findShieldCategoryPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	ShieldCategoryPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldCategoryPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) ShieldCategoryPeer.executeQuery("SELECT count(*) FROM " + ShieldCategoryPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = ShieldCategoryPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldCategoryPagination(PageInfo pageInfo, String searchWord, int shieldCategoryId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(ShieldCategoryPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + ShieldCategoryPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) ShieldCategoryPeer.executeQuery("SELECT count(*) FROM " + ShieldCategoryPeer.TABLE_NAME + whereSql, ShieldCategoryPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(ShieldCategoryPeer.CREATE_TIME);
        List<?> result = ShieldCategoryPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
