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
import com.youku.soku.manage.service.FeedbackService;
import com.youku.soku.manage.torque.Feedback;
import com.youku.soku.manage.torque.FeedbackPeer;

/**
 * <p>
 * Insert or update a Feedback object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class FeedbackAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());


	/**
	 * <p>
	 * List the Feedback
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
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

			FeedbackService.findFeedbackPagination(pageInfo, startDate,
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
		Feedback fd = FeedbackPeer.retrieveByPK(getFeedbackId());
		log.info("Operator: " + getUserName());
		log.info("Delete Feedback at " + formatLogDate(new Date()));
		log.info(fd);
		
		FeedbackPeer.doDelete(fd.getPrimaryKey());
		return SUCCESS;
	}
	
	
	/**
	 * process a feedback from the persistent store
	 * @return the proper result
	 * @throw exception on any error
	 */
	public String process() throws Exception {
		Feedback fd = FeedbackPeer.retrieveByPK(getFeedbackId());
		log.info("Operator: " + getUserName());
		log.info("Process Feedback at " + formatLogDate(new Date()));
		log.info(fd);
		
		fd.setStatus(1);
		
		FeedbackPeer.doUpdate(fd);
		return SUCCESS;
	}
	
	/**
	 * <p>Feedback detail action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========Feedback action input==============");
		try {			
			Feedback feedback = FeedbackPeer.retrieveByPK(getFeedbackId());
			setFeedback(feedback);
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
	 * Feedback id, used for delete the feedback object
	 */
	private int feedbackId;


	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
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
	
	private Feedback feedback;


	public Feedback getFeedback() {
		return feedback;
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

}
