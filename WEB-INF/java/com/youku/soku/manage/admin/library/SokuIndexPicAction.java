package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.youku.search.sort.MemCache;
import com.youku.soku.library.load.SokuIndexpagePic;
import com.youku.soku.library.load.SokuIndexpagePicPeer;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.SokuIndexPicService;

/**
 * <p>
 * Insert or update a SpiderLog object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class SokuIndexPicAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

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


			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(100);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			
			String newestDate = SokuIndexPicService.getNewestPicDate();
			SokuIndexPicService.findEpisodePaginationJdbc(pageInfo, newestDate, getStatus());

	
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	public String block() throws Exception {
		SokuIndexpagePic pic = SokuIndexpagePicPeer.retrieveByPK(getPicId());
		pic.setBlocked(1);
		SokuIndexpagePicPeer.doUpdate(pic);
		
		return SUCCESS;
	}

	public String unblock() throws Exception {
		SokuIndexpagePic pic = SokuIndexpagePicPeer.retrieveByPK(getPicId());
		pic.setBlocked(0);
		SokuIndexpagePicPeer.doUpdate(pic);
		
		return SUCCESS;
	}

	public String listJson() throws Exception {
		String jsonStr = SokuIndexPicService.generateJSONResult();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
		return null;
	}
	
	public String online() throws Exception {
		String jsonStr = SokuIndexPicService.generateJSONResult();
		MemCache.cacheSet("index_pic", jsonStr, 36000);
		
		return SUCCESS;
	}


	private int picId;
	

	public int getPicId() {
		return picId;
	}

	public void setPicId(int picId) {
		this.picId = picId;
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


	private List<SokuIndexpagePic> indexPicList;

	

	public List<SokuIndexpagePic> getIndexPicList() {
		return indexPicList;
	}

	public void setIndexPicList(List<SokuIndexpagePic> indexPicList) {
		this.indexPicList = indexPicList;
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

	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Map<Integer, String> getStausMap() {
		Map<Integer, String> stausMap = new HashMap<Integer, String>();
		stausMap.put(0, "正常");
		stausMap.put(1, "屏蔽");
		
		return stausMap;
	}
	
	
	
	

}
