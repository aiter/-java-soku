package com.youku.soku.manage.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ProtocolSiteService;
import com.youku.soku.manage.torque.ProtocolSite;
import com.youku.soku.manage.torque.ProtocolSitePeer;

/**
 * <p>
 * Insert or update a ProtocolSite object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class ProtocolSiteAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());


	/**
	 * <p>
	 * List the ProtocolSite
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			Date startDate = null;
			if (getStartDate() != null) {
				startDate = format.parse(getStartDate());
			}
			if (getEndDate() == null) {
				setEndDate(format.format(new Date()));
			}
			
			Date endDate = format.parse(getEndDate());			

			log.debug(startDate);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(100);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			ProtocolSiteService.findProtocolSitePagination(pageInfo, startDate,
					endDate);

			setPageInfo(pageInfo);
			return Constants.LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * delete a feedback from the persistent store
	 * @return the proper result
	 * @throw exception on any error
	 */
	public String delete() throws Exception {
		ProtocolSite fd = ProtocolSitePeer.retrieveByPK(getProtocolSiteId());
		log.info("Operator: " + getUserName());
		log.info("Delete ProtocolSite at " + formatLogDate(new Date()));
		log.info(fd);
		
		ProtocolSitePeer.doDelete(fd.getPrimaryKey());
		return SUCCESS;
	}
	
	/**
	 * <p>ProtocolSite detail action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========ProtocolSite action input==============");
		try {			
			ProtocolSite feedback = ProtocolSitePeer.retrieveByPK(getProtocolSiteId());
			setProtocolSite(feedback);
			return INPUT;
		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return INPUT;
	
	}

	

	/**
	 * ProtocolSite id, used for delete the feedback object
	 */
	private int protocolSiteId;


	public int getProtocolSiteId() {
		return protocolSiteId;
	}

	public void setProtocolSiteId(int protocolSiteId) {
		this.protocolSiteId = protocolSiteId;
	}


	/**
	 * current page number, for the feedbcak list view
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

	private String startDate;

	private String endDate;
	

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
	
	private ProtocolSite feedback;


	public ProtocolSite getProtocolSite() {
		return feedback;
	}

	public void setProtocolSite(ProtocolSite feedback) {
		this.feedback = feedback;
	}

}
