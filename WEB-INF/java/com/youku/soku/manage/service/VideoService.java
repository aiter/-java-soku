package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.Video;
import com.youku.soku.manage.torque.VideoPeer;

public class VideoService extends BaseService{
	
	/**
	 * Find videos on index page
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Video> findIndexVideo() throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(VideoPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.addDescendingOrderByColumn(VideoPeer.SORT);
		
		List<Video> videoList = VideoPeer.doSelect(crit);
		return videoList;
	}

	/**
	 * Find videos on index page
	 * @param itemId
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<Video> findIndexVideo(int itemId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(VideoPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.add(VideoPeer.ITEM_ID, itemId);
		crit.addDescendingOrderByColumn(VideoPeer.SORT);
		
		List<Video> videoList = VideoPeer.doSelect(crit);
		return videoList;
	}
	
	public static List<Video> findVideoByName(String videoName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(VideoPeer.NAME, videoName);
    	
    	List<Video> videoList = VideoPeer.doSelect(crit);
    	return videoList;
    	
    }
	
	public static List<Video> findVideoByItemId(int itemId) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(VideoPeer.ITEM_ID, itemId);
    	
    	List<Video> videoList = VideoPeer.doSelect(crit);
    	return videoList;
    	
    }
	
	public static List<Video> getVideoByChannel(String channel) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(VideoPeer.ITEM_ID, channel);
		
		List<Video> videoList = VideoPeer.doSelect(crit);

    	return null;
    }
    
    
    
    /**
     * <p>Find all the video</p>
     * @return the list of the video object
     */
    public static List<Video> findVideo(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Video> videoList = VideoPeer.doSelect(crit);
    	return videoList;
    	
    }
    
    /**
     * <p>Find video list using pagination</p>
     * @return the list of the video object
     */
    public static LargeSelect findVideoPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	VideoPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findVideoPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) VideoPeer.executeQuery("SELECT count(*) FROM " + VideoPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        crit.addDescendingOrderByColumn(VideoPeer.SORT);
        crit.addDescendingOrderByColumn(VideoPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(VideoPeer.CREATE_DATE);
        List result = VideoPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findVideoPagination(PageInfo pageInfo, String searchWord, String item) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(VideoPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + VideoPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        if(!StringUtils.isBlank(item)) {
        	crit.add(VideoPeer.ITEM_ID, item);
        	whereSql += "AND " + VideoPeer.ITEM_ID + "='" + item + "'";
        }
        int totalRecord = ((Record) VideoPeer.executeQuery("SELECT count(*) FROM " + VideoPeer.TABLE_NAME + whereSql, VideoPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        
        crit.addDescendingOrderByColumn(VideoPeer.SORT);
        crit.addDescendingOrderByColumn(VideoPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(VideoPeer.CREATE_DATE);
        List result = VideoPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }


}
