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
import com.youku.soku.manage.torque.ShieldChannel;
import com.youku.soku.manage.torque.ShieldChannelPeer;

public class ShieldChannelService extends BaseService{
	

	/**
	 * Find the shieldChannel with the name of shieldChannelName
	 * @param shieldChannelName
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<ShieldChannel> findShieldChannelByName(String shieldChannelName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(ShieldChannelPeer.NAME, shieldChannelName);
    	
    	List<ShieldChannel> shieldChannelList = ShieldChannelPeer.doSelect(crit);
    	return shieldChannelList;
    	
    }
    
    /**
     * <p>Find all the shieldChannel</p>
     * @return the list of the shieldChannel object
     */
    public static List<ShieldChannel> findShieldChannel(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldChannel> shieldChannelList = ShieldChannelPeer.doSelect(crit);
    	return shieldChannelList;
    	
    }
    
    /**
     * <p>build a shieldChannel map</p>
     * @return the map of the shieldChannel object
     */
    public static Map<Integer, String> getShieldChannelMap(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldChannel> shieldChannelList = ShieldChannelPeer.doSelect(crit);
    	
    	Map<Integer, String> map = new HashMap<Integer, String>();
    	for(ShieldChannel shieldChannel : shieldChannelList) {
    		map.put(shieldChannel.getId(), shieldChannel.getName());
    	}
    	
    	return map;
    	
    }
    
    /**
     * <p>Find shieldChannel list using pagination</p>
     * @return the list of the shieldChannel object
     */
    public static LargeSelect findShieldChannelPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	ShieldChannelPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldChannelPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) ShieldChannelPeer.executeQuery("SELECT count(*) FROM " + ShieldChannelPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = ShieldChannelPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldChannelPagination(PageInfo pageInfo, String searchWord, int shieldChannelId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(ShieldChannelPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + ShieldChannelPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) ShieldChannelPeer.executeQuery("SELECT count(*) FROM " + ShieldChannelPeer.TABLE_NAME + whereSql, ShieldChannelPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(ShieldChannelPeer.CREATE_TIME);
        List result = ShieldChannelPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
