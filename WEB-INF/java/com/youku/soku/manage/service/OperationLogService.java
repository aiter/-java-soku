package com.youku.soku.manage.service;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.DataOperation;
import com.youku.soku.manage.torque.DataOperationPeer;
import com.youku.soku.util.DataBase;

public class OperationLogService {

	public static final String OPERATION_DELETE = "删除";

	public static final String OPERATION_CREATE = "创建";

	public static final String OPERATION_UPDATE = "更新";
	
	public static final String OPERATION_AUDITPASS = "审核通过";
	
	public static final String OPERATION_AUDITFAIL = "审核未通过";

	public static void log(String operator, String type, String tabName,
			String detail) {
		
		if(detail == null) {
			return;
		}
		
		Connection conn = null;
		try {
			conn = DataBase.getSokuConn();
			DataOperation d = new DataOperation();
			d.setOperator(operator);
			d.setType(type);
			d.setTabName(tabName);
			d.setDetail(detail);
			d.setCreatetime(new Date());
			
			d.save(conn);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
	}
	
	public static String getChanges(String beforeUpdate, String afterUpdate) {
		/*if(beforeUpdate.equals(afterUpdate)) {
			return null;
		} else {
			return beforeUpdate + "================================ >" + afterUpdate;
		}*/
		
		return beforeUpdate + "================================ >" + afterUpdate;
	}
	
	
	public void findEpisodePagination(PageInfo pageInfo,
			Date startDate, Date endDate, Connection conn)
			throws Exception {

		Criteria crit = new Criteria();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String whereSql = " where " + DataOperationPeer.CREATETIME + " > '" + format.format(startDate)
							+ "' AND " + DataOperationPeer.CREATETIME + " <= '" + format.format(endDate) + "'";
		int totalRecord = ((Record) DataOperationPeer.executeQuery(
				"SELECT count(*) FROM " + DataOperationPeer.TABLE_NAME
						+ whereSql, true, conn).get(0)).getValue(1).asInt();
	
		
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
	
		System.out.println("totalRecord" + totalRecord);
		crit.add(DataOperationPeer.CREATETIME, startDate, Criteria.GREATER_THAN);
		Criteria.Criterion criterion = crit.getCriterion(DataOperationPeer.CREATETIME);
		criterion.and(
		               crit.getNewCriterion(
		                             criterion.getTable(),
		                             criterion.getColumn(),
		                             endDate,
		                             Criteria.LESS_EQUAL )
		               );
		//crit.add(DataOperationPeer.CREATE_TIME, endDate, Criteria.LESS_EQUAL);
		// crit.addDescendingOrderByColumn(VarietyPeer.SORT);
		// crit.addDescendingOrderByColumn(VarietyPeer.INDEX_TYPE);
		crit.addDescendingOrderByColumn(DataOperationPeer.CREATETIME);

		System.out.println(crit.toString());
		List<DataOperation> result = DataOperationPeer.doSelect(crit, conn);

		pageInfo.setResults(result);

		// return 0;
	}

}
