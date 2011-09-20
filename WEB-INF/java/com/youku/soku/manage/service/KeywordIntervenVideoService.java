package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.KeywordIntervenVideo;
import com.youku.soku.manage.torque.KeywordIntervenVideoPeer;

public class KeywordIntervenVideoService extends BaseService{
	
	/**
	 * Find videos on index page
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<KeywordIntervenVideo> findIndexKeywordIntervenVideo(Connection conn) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(KeywordIntervenVideoPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.SORT);
		
		List<KeywordIntervenVideo> videoList = KeywordIntervenVideoPeer.doSelect(crit);
		return videoList;
	}

	/**
	 * Find videos on index page
	 * @param itemId
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<KeywordIntervenVideo> findIndexKeywordIntervenVideo(int itemId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(KeywordIntervenVideoPeer.INDEX_TYPE, Constants.INDEX_DISPLAY);
		crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.SORT);
		
		List<KeywordIntervenVideo> videoList = KeywordIntervenVideoPeer.doSelect(crit);
		return videoList;
	}
	
	public static List<KeywordIntervenVideo> findKeywordIntervenVideoByName(String videoName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(KeywordIntervenVideoPeer.NAME, videoName);
    	
    	List<KeywordIntervenVideo> videoList = KeywordIntervenVideoPeer.doSelect(crit);
    	return videoList;
    	
    }
	
	/**
	 * Find videos on index page
	 * @param itemId
	 * @param conn
	 * @return
	 * @throws TorqueException
	 */
	public static List<KeywordIntervenVideo> findVideoByKeywordId(int keywordId) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(KeywordIntervenVideoPeer.KEYWORD_ID, keywordId);
		
		List<KeywordIntervenVideo> videoList = KeywordIntervenVideoPeer.doSelect(crit);
		return videoList;
	}
    
    
    /**
     * <p>Find all the video</p>
     * @return the list of the video object
     */
    public static List<KeywordIntervenVideo> findKeywordIntervenVideo(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<KeywordIntervenVideo> videoList = KeywordIntervenVideoPeer.doSelect(crit);
    	return videoList;
    	
    }
    
    /**
     * <p>Find video list using pagination</p>
     * @return the list of the video object
     */
    public static LargeSelect findKeywordIntervenVideoPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	KeywordIntervenVideoPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKeywordIntervenVideoPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) KeywordIntervenVideoPeer.executeQuery("SELECT count(*) FROM " + KeywordIntervenVideoPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.SORT);
        crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.CREATE_DATE);
        List result = KeywordIntervenVideoPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findKeywordIntervenVideoPagination(PageInfo pageInfo, int keywordId, String searchWord) throws Exception {
    	
        Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(KeywordIntervenVideoPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + KeywordIntervenVideoPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        if(keywordId > 0) {
        	crit.add(KeywordIntervenVideoPeer.KEYWORD_ID, keywordId);
        	whereSql += "AND " + KeywordIntervenVideoPeer.KEYWORD_ID + "=" + keywordId;
        }
     
        int totalRecord = ((Record) KeywordIntervenVideoPeer.executeQuery("SELECT count(*) FROM " + KeywordIntervenVideoPeer.TABLE_NAME + whereSql, KeywordIntervenVideoPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        
        
        crit.addAscendingOrderByColumn(KeywordIntervenVideoPeer.SORT);
        crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(KeywordIntervenVideoPeer.CREATE_DATE);
        List result = KeywordIntervenVideoPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }


}
