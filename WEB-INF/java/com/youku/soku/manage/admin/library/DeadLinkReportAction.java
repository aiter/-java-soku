package com.youku.soku.manage.admin.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.library.load.UrlCheck;
import com.youku.soku.library.load.UrlCheckPeer;
import com.youku.soku.manage.bo.DeadLinkReportBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.util.DataBase;


public class DeadLinkReportAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public static final int STATUS_HANDLE_MANUAL = 3;

	public String list() throws Exception {

		try {

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

			findEpisodePaginationJdbc(pageInfo, startDate, endDate);
			
			setPageInfo(pageInfo);
			return Constants.LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	public String handle() throws Exception {
		try {

			UrlCheck uc = UrlCheckPeer.retrieveByPK(getReportId());
			uc.setStatus(STATUS_HANDLE_MANUAL);
			UrlCheckPeer.doUpdate(uc);

		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			log.error(e.getMessage(), e);
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}

	public void findEpisodePaginationJdbc(PageInfo pageInfo, Date startDate, Date endDate) throws Exception {

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
			
			String statusSql = "status != " + STATUS_HANDLE_MANUAL;
			if(getStatus() == 1) {
				statusSql = "status = " + STATUS_HANDLE_MANUAL;
			}
			
			String whereSql = " where " + statusSql + " and update_time  > '" + format.format(startDate) + "' AND update_time <= '" + format.format(endDate) + "'";
		
			pstcnt = conn.prepareStatement("SELECT COUNT(*) FROM url_check " + whereSql);

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
			int totalPageNumber = (int) Math.ceil((double) recordCount / pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			String sql = "SELECT * FROM url_check" + whereSql + " ORDER by update_time desc, programme_site_id desc LIMIT " + pageInfo.getOffset() + ", " + pageInfo.getPageSize();

			pst = conn.prepareStatement(sql);

		
			List<DeadLinkReportBo> resultList = new ArrayList<DeadLinkReportBo>();
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				DeadLinkReportBo reportBo = new DeadLinkReportBo();
				reportBo.setId(rs.getInt("id"));
				reportBo.setUpdateTime(rs.getTimestamp("update_time"));
				reportBo.setProgrammeId(rs.getInt("programme_id"));
				reportBo.setProgrammeSiteId(rs.getInt("programme_site_id"));
				reportBo.setOrderStage(rs.getInt("order_stage"));
				reportBo.setStatus(rs.getInt("status"));
				resultList.add(reportBo);
			}

			for (DeadLinkReportBo reportBo : resultList) {
				try {
					
					ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(reportBo.getProgrammeSiteId());
					Programme p = ProgrammePeer.retrieveByPK(ps.getFkProgrammeId());
					reportBo.setProgrammeId(ps.getFkProgrammeId());
					reportBo.setProgrammeName(p.getName());
					reportBo.setSiteName(SiteService.getSiteName(ps.getSourceSite()));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
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

	/**
	 * current page number, for the spiderLog list view
	 */
	private int pageNumber;

	/**
	 * the pagination object
	 */
	private PageInfo pageInfo;

	private String startDate;

	private String endDate;
	
	private int reportId;
	
	private int status;
	

	private List<DeadLinkReportBo> reportList;

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
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

	public List<DeadLinkReportBo> getReportList() {
		return reportList;
	}

	public void setReportList(List<DeadLinkReportBo> reportList) {
		this.reportList = reportList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

}
