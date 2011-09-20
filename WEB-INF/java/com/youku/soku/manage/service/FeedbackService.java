package com.youku.soku.manage.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.Feedback;
import com.youku.soku.manage.torque.FeedbackPeer;
import com.youku.soku.util.DataBase;

public class FeedbackService {
	
    
    /**
     * <p>Find all the names</p>
     * @return the list of the names object
     */
    public static List<Feedback> findFeedback() throws TorqueException {
    	
    	Criteria crit = new Criteria();
    	
    	List<Feedback> FeedbackList = FeedbackPeer.doSelect(crit);
    	return FeedbackList;
    	
    }
  
    public static void findFeedbackPagination(PageInfo pageInfo,
			Date startDate, Date endDate)
			throws Exception {
    	
    	Calendar c = Calendar.getInstance();
    	c.setTime(endDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 59);
		endDate = c.getTime();

		Criteria crit = new Criteria();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String whereSql = " where " + FeedbackPeer.CREATETIME + " >= '" + format.format(startDate)
							+ "' AND " + FeedbackPeer.CREATETIME + " < '" + format.format(endDate) + "'";
		int totalRecord = ((Record) FeedbackPeer.executeQuery(
				"SELECT count(*) FROM " + FeedbackPeer.TABLE_NAME
						+ whereSql, FeedbackPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
	
		
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.setTotalRecords(totalRecord);
		pageInfo.initCrit(crit);
	
		System.out.println("totalRecord" + totalRecord);
		crit.add(FeedbackPeer.CREATETIME, startDate, Criteria.GREATER_EQUAL);
		Criteria.Criterion criterion = crit.getCriterion(FeedbackPeer.CREATETIME);
		criterion.and(
		               crit.getNewCriterion(
		                             criterion.getTable(),
		                             criterion.getColumn(),
		                             endDate,
		                             Criteria.LESS_THAN )
		               );

		//crit.add(FeedbackPeer.CREATETIME, endDate, Criteria.LESS_EQUAL);
		// crit.addDescendingOrderByColumn(VarietyPeer.SORT);
		// crit.addDescendingOrderByColumn(VarietyPeer.INDEX_TYPE);
		crit.addDescendingOrderByColumn(FeedbackPeer.CREATETIME);

		List<Feedback> result = FeedbackPeer.doSelect(crit);

		pageInfo.setResults(result);

		// return 0;
	}
    
	public static void saveFeedback(Feedback fd) throws TorqueException {
    	 Connection conn = null;
    	
    	try {
    		conn = DataBase.getSokuConn();
    		fd.save(conn);
    	} catch (TorqueException e) {
    		e.printStackTrace();
    		throw e;
    	} finally {
    		JdbcUtil.close(conn);
    	}    	
    	
    }


}
