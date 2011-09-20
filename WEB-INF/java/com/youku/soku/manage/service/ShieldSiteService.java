package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.library.Utils;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.ShieldSite;
import com.youku.soku.manage.torque.ShieldSitePeer;
import com.youku.soku.manage.util.ManageUtil;

public class ShieldSiteService extends BaseService{
	

	/**
	 * Find the shieldSite with the name of shieldSiteName
	 * @param shieldSiteName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<ShieldSite> findShieldSiteByName(String shieldSiteName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(ShieldSitePeer.NAME, shieldSiteName);
    	
    	List<ShieldSite> shieldSiteList = ShieldSitePeer.doSelect(crit);
    	return shieldSiteList;
    	
    }
    
    /**
     * <p>Find all the shieldSite</p>
     * @return the list of the shieldSite object
     */
    public static List<ShieldSite> findShieldSite(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldSite> shieldSiteList = ShieldSitePeer.doSelect(crit);
    	return shieldSiteList;
    	
    }
    
    /**
     * <p>build a shieldSite map</p>
     * @return the map of the shieldSite object
     */
    public static Map<Integer, String> getShieldSiteMap(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldSite> shieldSiteList = ShieldSitePeer.doSelect(crit);
    	
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	for(ShieldSite shieldSite : shieldSiteList) {
    		map.put(shieldSite.getId(), shieldSite.getName());
    	}
    	
    	return map;
    	
    }
    
    /**
     * <p>Find shieldSite list using pagination</p>
     * @return the list of the shieldSite object
     */
    public static LargeSelect findShieldSitePagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	ShieldSitePeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldSitePagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) ShieldSitePeer.executeQuery("SELECT count(*) FROM " + ShieldSitePeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = ShieldSitePeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldSitePagination(PageInfo pageInfo, String searchWord, int shieldSiteId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(ShieldSitePeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + ShieldSitePeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) ShieldSitePeer.executeQuery("SELECT count(*) FROM " + ShieldSitePeer.TABLE_NAME + whereSql, ShieldSitePeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(ShieldSitePeer.CREATE_TIME);
        List result = ShieldSitePeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
    
    
    public static int getSiteId(String url) {
    	
    	if(url.charAt(url.length() - 1) != '/') {
    		url += "/";
    	}
    	String domain = Utils.parseDomain(url);
    	int siteId = 0;
    	siteId = ManageUtil.parseSite(url);
		
		return siteId;
    }
}
