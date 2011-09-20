package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.manage.common.PageInfo;

public class SeriesService {
	
	private static Logger log = Logger.getLogger(SeriesService.class);

	public static Series getUniqueSeries(String namesName, int categoryId) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(SeriesPeer.NAME, namesName);
    	crit.add(SeriesPeer.CATE, categoryId);
    	
    	List<Series> namesList = SeriesPeer.doSelect(crit);
    	
    	if(namesList != null && namesList.size() > 0) {
    		return namesList.get(0);
    	} else {
    		return null;
    	}
    	
    }
	
	public static int updateSeries(String namesName, String alias, int categoryId, String userName) throws Exception {
		
		Series names = getUniqueSeries(namesName, categoryId);
		if(namesName != null) {
			namesName = namesName.trim();
		}
		if(alias != null) {
			alias = alias.trim();
		}
		
		if(names == null) {			
			Series newSeries = new Series();
			newSeries.setName(namesName);
			newSeries.setAlias(alias);
			newSeries.setCate(categoryId);
			newSeries.setCreateTime(new Date());
			newSeries.setUpdateTime(new Date());
			
			log.info("Operator: " + userName);
			log.info("Create a new names " + new Date());
			newSeries.save();
			log.info(newSeries);
			return newSeries.getId();
		} else {
			if(!alias.equals(names.getAlias())) {
				log.info("Operator: " + userName);
				log.info("Update a names alias" + new Date());
				log.info("Series before update is: " + names);
				names.setAlias(alias);
				names.setUpdateTime(new Date());
				SeriesPeer.doUpdate(names);
				log.info("Series after update is: " + names);
			}
			
			return names.getId();
		}
	}

	
	public static List<Series> findSeriesByName(String namesName) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(SeriesPeer.NAME, namesName);
    	
    	List<Series> namesList = SeriesPeer.doSelect(crit);
    	return namesList;
    	
    }
	
	public static List<Series> findSeriesByItemId(int categoryId) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(SeriesPeer.CATE, categoryId);
    	
    	List<Series> namesList = SeriesPeer.doSelect(crit);
    	return namesList;
    	
    }
    
    /**
     * <p>Find all the names</p>
     * @return the list of the names object
     */
    public static List<Series> findSeries(Connection conn) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Series> namesList = SeriesPeer.doSelect(crit);
    	return namesList;
    	
    }
    
    /**
     * <p>Find names list using pagination</p>
     * @return the list of the names object
     */
    public static LargeSelect findSeriesPagination(int pageSize) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	LargeSelect largeSelect = new LargeSelect(crit, pageSize);
    	
    	SeriesPeer.doSelect(crit);
    	
    	return largeSelect;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findSeriesPagination(PageInfo pageInfo) throws Exception {
    	int totalRecord = ((Record) SeriesPeer.executeQuery("SELECT count(*) FROM " + SeriesPeer.TABLE_NAME).get(0)).getValue(1).asInt();
        Criteria crit = new Criteria();
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        List result = SeriesPeer.doSelect(crit);
        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
    /**
     * <p>Find object list using pagination</p>
     * 
     */
    public static void findSeriesPagination(PageInfo pageInfo, String searchWord, int categoryId) throws Exception {
    	
    	
    	Criteria crit = new Criteria();
        
        String whereSql = " Where 1 = 1 ";
        if(searchWord != null && !searchWord.equals("")) {
        	crit.add(SeriesPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
        	whereSql += "AND " + SeriesPeer.NAME + " LIKE " + ( "'%" + searchWord + "%'");
        }
        
        if(categoryId > 0) {
        	crit.add(SeriesPeer.CATE, categoryId);
        	whereSql += "AND " + SeriesPeer.CATE + "=" + categoryId;
        }
        
        int totalRecord = ((Record) SeriesPeer.executeQuery("SELECT count(*) FROM " + SeriesPeer.TABLE_NAME + whereSql, SeriesPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
       
       
        int totalPageNumber = (int) Math.ceil((double)totalRecord / pageInfo.getPageSize());
        pageInfo.setTotalPageNumber(totalPageNumber);
        pageInfo.initCrit(crit);
        if(searchWord != null && !"".equals(searchWord)) {
        	crit.add(SeriesPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);	
        }
        
        if(categoryId >0) {
        	crit.add(SeriesPeer.CATE, categoryId);
        }
        
        //crit.addDescendingOrderByColumn(SeriesPeer.SORT);
        //crit.addDescendingOrderByColumn(SeriesPeer.INDEX_TYPE);
        crit.addDescendingOrderByColumn(SeriesPeer.CREATE_TIME);
        
        List result = SeriesPeer.doSelect(crit);        
        pageInfo.setResults(result);
       
        //return 0;
    }
    
	public static List<Series> searchSeries(String searchWord) throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	crit.add(SeriesPeer.NAME, (Object) ( "%" + searchWord + "%"), Criteria.LIKE);
    	
    	List<Series> namesList = SeriesPeer.doSelect(crit);
    	return namesList;
    	
    }


}
