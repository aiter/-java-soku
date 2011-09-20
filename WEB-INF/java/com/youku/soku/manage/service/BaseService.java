package com.youku.soku.manage.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.ItemPeer;

public class BaseService {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	 /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findItemPagination(PageInfo pageInfo, BasePeer basePeer) throws Exception {
    	int totalRecord = ((Record) ItemPeer.executeQuery("SELECT count(*) FROM " + ItemPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        System.out.println("totalRecord of Item is: " + totalRecord);
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        System.out.println("total page number is: " + totalPageNumber);
        System.out.println("total page size is: " + pageInfo.getPageSize());
        if(pageInfo.getCurrentPageNumber() > totalPageNumber) {
            pageInfo.setCurrentPageNumber(totalPageNumber);
        }
        //LargeSelect largeSelect = new LargeSelect(crit, pageSize);
       
        crit.setOffset(pageInfo.getPageSize() * (pageInfo.getCurrentPageNumber() - 1));
        System.out.println("current page number is: " + pageInfo.getCurrentPageNumber());
        crit.setLimit(pageInfo.getPageSize());
        List result = ItemPeer.doSelect(crit);
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.setResults(result);
       
        //return 0;
    }
/*    private static Connection conn;
    
    private static int connectionCount = 0;
    
    protected static Connection getConnection() throws TorqueException{
    	if(conn == null) {
    	connectionCount++;
		System.out.println("^^^^^^^^^^^^^^^connection count is: " + connectionCount);    	
    	conn = Torque.getConnection("soku");
    	}
		return conn;
	}*/

}
