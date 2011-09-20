package com.youku.soku.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.Item;
import com.youku.soku.manage.torque.ItemPeer;

public class ItemService extends BaseService{
	
	/**
	 * Find the item display on index page
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static List<Item> findIndexItem() throws Exception{
		Criteria crit = new Criteria();
		crit.add(ItemPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.addDescendingOrderByColumn(ItemPeer.SORT);
		
		List<Item> itemList = ItemPeer.doSelect(crit);
		return itemList;
	}
	
	/**
	 * Find the item with the name of itemName
	 * @param itemName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Item> findItemByName(String itemName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(ItemPeer.NAME, itemName);
    	
    	List<Item> itemList = ItemPeer.doSelect(crit);
    	return itemList;
    	
    }
    
    /**
     * <p>Find all the item</p>
     * @return the list of the item object
     */
    public static List<Item> findItem() throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Item> itemList = ItemPeer.doSelect(crit);
    	return itemList;
    	
    }
    
    /**
     * <p>build a item map</p>
     * @return the map of the item object
     */
    public static Map<Integer, String> getItemMap() throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Item> itemList = ItemPeer.doSelect(crit);
    	
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	for(Item item : itemList) {
    		map.put(item.getItemId(), item.getName());
    	}
    	
    	return map;
    	
    }
    
    /**
     * <p>Find item list using pagination</p>
     * @return the list of the item object
     */
    public static LargeSelect findItemPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	ItemPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findItemPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) ItemPeer.executeQuery("SELECT count(*) FROM " + ItemPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = ItemPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findItemPagination(PageInfo pageInfo, String searchWord, int itemId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(ItemPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + ItemPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) ItemPeer.executeQuery("SELECT count(*) FROM " + ItemPeer.TABLE_NAME + whereSql, ItemPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(ItemPeer.SORT);
        crit.addDescendingOrderByColumn(ItemPeer.INDEX_TYPE);
        List result = ItemPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
