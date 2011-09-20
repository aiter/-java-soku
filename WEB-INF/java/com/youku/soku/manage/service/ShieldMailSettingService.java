package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.ShieldMailSetting;
import com.youku.soku.manage.torque.ShieldMailSettingPeer;

public class ShieldMailSettingService extends BaseService{
	


    /**
     * <p>Find all the shieldMailSetting</p>
     * @return the list of the shieldMailSetting object
     */
    public static List<ShieldMailSetting> findShieldMailSetting(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<ShieldMailSetting> shieldMailSettingList = ShieldMailSettingPeer.doSelect(crit);
    	return shieldMailSettingList;
    	
    }
    

    
    /**
     * <p>Find shieldMailSetting list using pagination</p>
     * @return the list of the shieldMailSetting object
     */
    public static LargeSelect findShieldMailSettingPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	ShieldMailSettingPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldMailSettingPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) ShieldMailSettingPeer.executeQuery("SELECT count(*) FROM " + ShieldMailSettingPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = ShieldMailSettingPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findShieldMailSettingPagination(PageInfo pageInfo, String searchWord, int shieldMailSettingId) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(ShieldMailSettingPeer.EMAIL, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + ShieldMailSettingPeer.EMAIL + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        
        int totalRecord = ((Record) ShieldMailSettingPeer.executeQuery("SELECT count(*) FROM " + ShieldMailSettingPeer.TABLE_NAME + whereSql, ShieldMailSettingPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(ShieldMailSettingPeer.CREATE_TIME);
        List result = ShieldMailSettingPeer.doSelect(crit);
        
        pageInfo.setResults(result);
        
        //return 0;
    }
}
