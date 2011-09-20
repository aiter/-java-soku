package com.youku.soku.manage.admin.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.manage.bo.EpisodeLogBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.util.DataBase;

/**
 * <p>
 * Insert or update a SpiderLog object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class EpisodeLogAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	private static final String TABNAME = "episode_log";

	/**
	 * <p>
	 * Insert or update an spiderLog
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}

	/**
	 * <p>
	 * List the SpiderLogs
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {
			/*
			 * String pageSize = getText("spiderLog.list.pageSize"); LargeSelect
			 * largeSelect =
			 * SpiderLogPeer.findSpiderLogPagination(Integer.valueOf(pageSize));
			 * log.debug("page number is: " + getPageNumber());
			 * log.debug("total page number is: " +
			 * largeSelect.getTotalPages());
			 * 
			 * if(largeSelect.getTotalPages() < getPageNumber()) { return ERROR;
			 * } List spiderLogList = largeSelect.getPage(getPageNumber());
			 */

			// SpiderLogService.findSpiderLogPagination(pageInfo,
			// getSearchWord(), -1);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if (getStartDate() == null) {
				setStartDate(format.format(new Date()));
			}
			if (getEndDate() == null) {
				setEndDate(format.format(new Date()));
			}
			Date startDate = format.parse(getStartDate());
			Date endDate = format.parse(getEndDate());

			log.debug(startDate);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(100);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			

			findEpisodePaginationJdbc(pageInfo, startDate, endDate,
					getSearchUrl(), getOperator(), getWorkType());

	
			setPageInfo(pageInfo);
			return Constants.LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}

	

	public void findEpisodePagination(PageInfo pageInfo, Date startDate,
			Date endDate, String searchUrl, String operator, int workType) throws Exception {

		Calendar c = Calendar.getInstance();

		c.setTime(endDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 59);
		endDate = c.getTime();

		Criteria crit = new Criteria();

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String whereSql = " where " + EpisodeLogPeer.CREATE_TIME + " > '"
				+ format.format(startDate) + "' AND "
				+ EpisodeLogPeer.CREATE_TIME + " <= '" + format.format(endDate)
				+ "'";

		if (!StringUtils.isBlank(searchUrl)) {
			whereSql += " AND url like " + searchUrl;
			crit.add(EpisodeLogPeer.URL, searchUrl);
		}
		
		if (!StringUtils.isBlank(operator)) {
			whereSql += " AND operator like " + operator;
			crit.add(EpisodeLogPeer.OPERATOR, operator);
		}
		
		if(workType == 1) {
			whereSql += " AND source = '" + Constants.ADD_URL_SOURCE + "'";
			crit.add(EpisodeLogPeer.SOURCE, Constants.ADD_URL_SOURCE);
		} else if(workType == 2) {
			/*whereSql += " AND source = '" + Constants.AUTO_SEARCH_FLAG + "'";
			crit.add(EpisodeLogPeer.SOURCE, Constants.AUTO_SEARCH_FLAG);*/
			whereSql += " AND status > 0";
			crit.add(EpisodeLogPeer.STATUS, 0, Criteria.GREATER_THAN);
		}
		int totalRecord = ((Record) EpisodeLogPeer.executeQuery(
				"SELECT count(*) FROM " + EpisodeLogPeer.TABLE_NAME + whereSql,
				EpisodeLogPeer.DATABASE_NAME).get(0)).getValue(1).asInt();

		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.setTotalRecords(totalRecord);
		pageInfo.initCrit(crit);

		crit.add(EpisodeLogPeer.CREATE_TIME, startDate, Criteria.GREATER_THAN);
		Criteria.Criterion criterion = crit
				.getCriterion(EpisodeLogPeer.CREATE_TIME);
		criterion.and(crit.getNewCriterion(criterion.getTable(), criterion
				.getColumn(), endDate, Criteria.LESS_EQUAL));

	
		crit.addDescendingOrderByColumn(EpisodeLogPeer.CREATE_TIME);

		System.out.println(crit.toString());
		List<EpisodeLog> result = EpisodeLogPeer.doSelect(crit);

		pageInfo.setResults(result);

		// return 0;
	}

	public void findEpisodePaginationJdbc(PageInfo pageInfo, Date startDate,
			Date endDate, String searchUrl, String operator, int workType) throws Exception {

		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
			Calendar c = Calendar.getInstance();

			c.setTime(endDate);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 59);
			endDate = c.getTime();


			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String whereSql = " where " + EpisodeLogPeer.UPDATE_TIME + " > '"
					+ format.format(startDate) + "' AND "
					+ EpisodeLogPeer.UPDATE_TIME + " <= '" + format.format(endDate)
					+ "'";

			if (!StringUtils.isBlank(searchUrl)) {
				whereSql += " AND url like ?";
			}
			
			if (!StringUtils.isBlank(operator)) {
				whereSql += " AND operator like ?";
			}
			
			if(workType == 1) {
				whereSql += " AND source = '" + Constants.ADD_URL_SOURCE + "'";
			} else if(workType == 2) {
				whereSql += " AND status >0 ";
			}
			
			pstcnt = conn.prepareStatement("SELECT COUNT(*) FROM episode_log " + whereSql);
			
			if (!StringUtils.isBlank(searchUrl)) {
				pstcnt.setString(1, searchUrl);
				if (!StringUtils.isBlank(operator)) {
					pstcnt.setString(2, operator);
				}
			} else {
				if (!StringUtils.isBlank(operator)) {
					pstcnt.setString(1, operator);
				}
			}
			
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			
			log.info(pstcnt.toString());
			log.info("record count" + recordCount);
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			String sql = "SELECT * FROM episode_log" + whereSql + " LIMIT " + pageInfo.getOffset()
			+ ", " + pageInfo.getPageSize();;

			pst = conn.prepareStatement(sql);
			
			if (!StringUtils.isBlank(searchUrl)) {
				pst.setString(1, searchUrl);
				if (!StringUtils.isBlank(operator)) {
					pst.setString(2, operator);
				}
			} else {
				if (!StringUtils.isBlank(operator)) {
					pst.setString(1, operator);
				}
			}
			
			List<EpisodeLogBo> resultList = new ArrayList<EpisodeLogBo>();
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				EpisodeLogBo log = new EpisodeLogBo();
				log.setId(rs.getInt("id"));
				log.setUpdateTime(rs.getTimestamp("update_time"));
				log.setUrl(rs.getString("url"));
				log.setOperator(rs.getString("operator"));
				log.setSubjectSiteId(rs.getInt("fk_programme_site_id"));
				log.setOrderId(rs.getInt("order_id"));
				log.setTitle(rs.getString("title"));
				log.setHistoryOperation(getHistoryOperation(log.getSubjectSiteId(), log.getOrderId()));
				resultList.add(log);
			}
			
			pageInfo.setResults(resultList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (pstcnt != null) {
					pstcnt.close();
				}
				if (rscnt != null) {
					rscnt.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}


		// return 0;
	}
	
	private List<EpisodeLogBo> getHistoryOperation(int subjectSiteId, int orderId) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT * FROM episode_log WHERE fk_programme_site_id = ? and order_id = ?";
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, subjectSiteId);
			pst.setInt(2, orderId);
			
			List<EpisodeLogBo> resultList = new ArrayList<EpisodeLogBo>();
			//log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				EpisodeLogBo log = new EpisodeLogBo();
				log.setId(rs.getInt("id"));
				log.setUpdateTime(rs.getTimestamp("update_time"));
				log.setUrl(rs.getString("url"));
				log.setOperator(rs.getString("operator"));
				log.setSubjectSiteId(rs.getInt("fk_programme_site_id"));
				log.setOrderId(rs.getInt("order_id"));
				log.setTitle(rs.getString("title"));
				resultList.add(log);
			}
			return resultList;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		return null;
	}

	private int getSubjectLogCount(String tableName, String whereSql, Connection conn) throws Exception {
	

		int totalRecord = ((Record) EpisodeLogPeer
				.executeQuery(
						"SELECT count(*) FROM episode_log e, "
								+ tableName
								+ " t"
								+ whereSql
								+ " and e.tab_name = '"
								+ tableName
								+ "' and e.fk_table_id = t.id and (t.source = 4 or t.source = 5 or t.source = 12)",
						true, conn).get(0)).getValue(1).asInt();

		return totalRecord;
	}
	
	

	private int parseDate(Date date, int flag) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.get(flag);
	}




	/**
	 * SpiderLog id, used for update the spiderLog object
	 */
	private int episodeLogId;

	public int getEpisodeLogId() {
		return episodeLogId;
	}

	public void setEpisodeLogId(int episodeLogId) {
		this.episodeLogId = episodeLogId;
	}

	/**
	 * current page number, for the spiderLog list view
	 */
	private int pageNumber;

	/**
	 * get the current page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * set the current page number
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	private String startDate;

	private String endDate;

	private List<EpisodeLogBo> episodeLogList;

	public List<EpisodeLogBo> getEpisodeLogList() {
		return episodeLogList;
	}

	public void setEpisodeLogList(List<EpisodeLogBo> episodeLogList) {
		this.episodeLogList = episodeLogList;
	}

	/**
	 * the pagination object
	 */
	private PageInfo pageInfo;

	/**
	 * get the pagination object
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * set the pagination object
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	private String operator;
	
	private int workType;
	
	private String searchUrl;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getWorkType() {
		return workType;
	}

	public void setWorkType(int workType) {
		this.workType = workType;
	}

	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
	
	public Map<Integer, String> getWorkTypeMap() {
		Map<Integer, String> workTypeMap = new HashMap<Integer, String>();
		workTypeMap.put(0, "所有");
		workTypeMap.put(1, "添加播放链接");
		workTypeMap.put(2, "审核播放链接");
		
		return workTypeMap;
	}
	
	
	
	

}
