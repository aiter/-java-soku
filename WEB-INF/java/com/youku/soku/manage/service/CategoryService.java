package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.library.load.Category;
import com.youku.soku.library.load.CategoryPeer;
import com.youku.soku.manage.common.PageInfo;

public class CategoryService extends BaseService{
	
	private static Map<Integer, String> categoryMap;
	
	public static Map<Integer, String> getCategoryMap() throws Exception {
		if(categoryMap == null) {
			categoryMap = new HashMap<Integer, String>();
			List<Category> categoryList = getAllCategory();
			
			for(Category c : categoryList) {
				categoryMap.put(c.getId(), c.getName());
			}
		}
		Collections.unmodifiableMap(categoryMap);
		return categoryMap;
	}
	
	/**
	 * Find the category display on index page
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static List<Category> getAllCategory() throws Exception{
		Criteria crit = new Criteria();
		//crit.add(CategoryPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		//crit.addDescendingOrderByColumn(CategoryPeer.SORT);
		
		List<Category> categoryList = CategoryPeer.doSelect(crit);
		return categoryList;
	}
	
	/**
	 * Find the category which has direct
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static List<Category> getDirectCategory() throws Exception{
		Criteria crit = new Criteria();
		//crit.add(CategoryPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		//crit.addDescendingOrderByColumn(CategoryPeer.SORT);
		
		List<Category> categoryList = CategoryPeer.doSelect(crit);
		return categoryList;
	}
	
	/**
	 * Find the category with the name of categoryName
	 * @param categoryName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Category> findCategoryByName(String categoryName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(CategoryPeer.NAME, categoryName);
    	
    	List<Category> categoryList = CategoryPeer.doSelect(crit);
    	return categoryList;
    	
    }
    
    /**
     * <p>Find all the category</p>
     * @return the list of the category object
     */
    public static List<Category> findCategory(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Category> categoryList = CategoryPeer.doSelect(crit);
    	return categoryList;
    	
    }
    
    /**
     * <p>Find all the category</p>
     * @return the list of the category object
     */
    public static List<Category> findCategory() throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Category> categoryList = CategoryPeer.doSelect(crit);
    	return categoryList;
    	
    }
    
    /**
     * <p>Find category list using pagination</p>
     * @return the list of the category object
     */
    public static LargeSelect findCategoryPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	CategoryPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findCategoryPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) CategoryPeer.executeQuery("SELECT count(*) FROM " + CategoryPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = CategoryPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findCategoryPagination(PageInfo pageInfo, String searchWord, int categoryId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(CategoryPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + CategoryPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) CategoryPeer.executeQuery("SELECT count(*) FROM " + CategoryPeer.TABLE_NAME + whereSql, CategoryPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
/*        crit.addDescendingOrderByColumn(CategoryPeer.SORT);
        crit.addDescendingOrderByColumn(CategoryPeer.INDEX_TYPE);*/
        List result = CategoryPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
