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
import com.youku.soku.manage.torque.ProtocolSite;
import com.youku.soku.manage.torque.ProtocolSitePeer;
import com.youku.soku.util.DataBase;

public class ProtocolSiteService {
	
	 public static void findProtocolSitePagination(PageInfo pageInfo,
				Date startDate, Date endDate)
				throws Exception {
	    	
	    	Calendar c = Calendar.getInstance();
	    	c.setTime(endDate);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 59);
			endDate = c.getTime();
			
			if(startDate == null) {
				//c.setTime(new Date());
				c.set(Calendar.YEAR, 2000);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				startDate = c.getTime();
			}

			Criteria crit = new Criteria();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String whereSql = " where " + ProtocolSitePeer.CREATETIME + " >= '" + format.format(startDate)
								+ "' AND " + ProtocolSitePeer.CREATETIME + " < '" + format.format(endDate) + "'";
			int totalRecord = ((Record) ProtocolSitePeer.executeQuery(
					"SELECT count(*) FROM " + ProtocolSitePeer.TABLE_NAME
							+ whereSql, ProtocolSitePeer.DATABASE_NAME).get(0)).getValue(1).asInt();
		
			
			int totalPageNumber = (int) Math.ceil((double) totalRecord
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(totalRecord);
			pageInfo.initCrit(crit);
		
			System.out.println("whereSql" + whereSql);
			System.out.println("totalRecord" + totalRecord);
			crit.add(ProtocolSitePeer.CREATETIME, startDate, Criteria.GREATER_EQUAL);
			Criteria.Criterion criterion = crit.getCriterion(ProtocolSitePeer.CREATETIME);
			criterion.and(
			               crit.getNewCriterion(
			                             criterion.getTable(),
			                             criterion.getColumn(),
			                             endDate,
			                             Criteria.LESS_THAN )
			               );

			//crit.add(ProtocolSitePeer.CREATETIME, endDate, Criteria.LESS_EQUAL);
			// crit.addDescendingOrderByColumn(VarietyPeer.SORT);
			// crit.addDescendingOrderByColumn(VarietyPeer.INDEX_TYPE);
			crit.addDescendingOrderByColumn(ProtocolSitePeer.CREATETIME);

			List<ProtocolSite> result = ProtocolSitePeer.doSelect(crit);

			pageInfo.setResults(result);

			// return 0;
		}
	
	public static void saveProtocolSite(ProtocolSite site) throws TorqueException {
    	Connection conn = null;
    	
    	try {
    		conn = DataBase.getSokuConn();
    		site.save(conn);
    	} catch (TorqueException e) {
    		e.printStackTrace();
    		throw e;
    	} finally {
    		JdbcUtil.close(conn);
    	}    	
    	
    }
}
