package com.youku.soku.manage.admin;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.KeywordIntervenService;
import com.youku.soku.manage.service.KeywordIntervenVideoService;
import com.youku.soku.manage.torque.KeywordInterven;
import com.youku.soku.manage.torque.KeywordIntervenPeer;


/**
 * <p>Insert or update a Keyword object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class KeywordIntervenAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create keywordInterven action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========KeywordInterven action input==============");
		try {
			if(getKeywordIntervenId() == -1) {
				setTask(Constants.CREATE);	
				setKeywordIntervenId(-1);
				return INPUT; 
			} else {
				KeywordInterven oldKeywordInterven = KeywordIntervenPeer.retrieveByPK(getKeywordIntervenId());
				setKeywordInterven(oldKeywordInterven);
				setKeywordIntervenId(oldKeywordInterven.getKeywordId());
				setTask(Constants.EDIT);
				return INPUT;
			}
		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return INPUT;
	
	}
	
	/**
	 * <p>Insert or update an keywordInterven</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the KeywordIntervens</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("keywordInterven.list.pageSize");
			LargeSelect largeSelect = KeywordIntervenPeer.findKeywordIntervenPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List keywordIntervenList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("KeywordIntervenAction getPageNumber() value is: " + getPageNumber());
			KeywordIntervenService.findKeywordIntervenPagination(pageInfo, getSearchWord(), -1);
			
			List keywordIntervenList = pageInfo.getResults();
			//keywordIntervenList = KeywordIntervenPeer.populateObjects(keywordIntervenList);
			pageInfo.setResults(keywordIntervenList);
			log.debug("keywordIntervenList size is: " + keywordIntervenList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an keywordInterven from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		//List videoList = VideoService.findVideoByKeywordIntervenId(getKeywordIntervenId());
		List videoList = KeywordIntervenVideoService.findVideoByKeywordId(getKeywordIntervenId());
		
		if(videoList.size() > 0) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			String errorMsg = getText("error.keyword.internel.delete");
			out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
			return null;
		} else {		
			KeywordInterven keywordInterven = KeywordIntervenPeer.retrieveByPK(getKeywordIntervenId());
			KeywordIntervenPeer.doDelete(keywordInterven);
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Delete an keywordInterven from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		//List videoList = VideoService.findVideoByKeywordIntervenId(getKeywordIntervenId());
		
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			
			List videoList = KeywordIntervenVideoService.findVideoByKeywordId(deleteId);
			
			if(videoList.size() > 0) {
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				String errorMsg = getText("error.keyword.internel.delete");
				out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
				return null;
			} else {		
				KeywordInterven keywordInterven = KeywordIntervenPeer.retrieveByPK(deleteId);
				KeywordIntervenPeer.doDelete(keywordInterven);
			}
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an keywordInterven object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("KeywordInterven name is: " + getKeywordInterven().getName());
			
			if (creating) {
				List<KeywordInterven> keywordIntervenList = KeywordIntervenService.findKeywordIntervenByName(getKeywordInterven().getName());
				boolean haveKeywordInterven = keywordIntervenList.size() > 0;
				log.debug("keywordIntervenList size is:" + keywordIntervenList.size());
				if (haveKeywordInterven) {
					addActionError(getText("error.keywordIntervenname.unique"));
					return INPUT;
				}
				
				getKeywordInterven().save();
			} else {
				log.debug("Task update, keywordIntervenId is " + getKeywordInterven().getKeywordId());
				KeywordInterven oldKeywordInterven = KeywordIntervenPeer.retrieveByPK(getKeywordIntervenId());
				oldKeywordInterven.setName(getKeywordInterven().getName());
				KeywordIntervenPeer.doUpdate(oldKeywordInterven);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the KeywordInterven **/
	private KeywordInterven keywordInterven;
	
	/**
	 * <p>create the KeywordInterven object</p>
	 */
	public KeywordInterven getKeywordInterven() {
		return keywordInterven;
	}
	
	/**
	 * <p>set the KeywordInterven object</p>
	 * @param keywordInterven KeywordInterven ojbect
	 */
	public void setKeywordInterven(KeywordInterven keywordInterven) {
		this.keywordInterven = keywordInterven;
	}
	/**
	 * KeywordInterven id, used for update the keywordInterven object
	 */
	private int keywordIntervenId;
	
	/**
	 * get keywordInterven id
	 * @return keywordIntervenId
	 */
	public int getKeywordIntervenId() {
		return keywordIntervenId;
	}
	
	/**
	 * set keywordInterven id
	 * @param keywordIntervenId
	 */
	public void setKeywordIntervenId(int keywordIntervenId) {
		this.keywordIntervenId = keywordIntervenId;
	}
	
	/**
	 * current page number, for the keywordInterven list view
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
	
	/**
	 * the value for the word for searching
	 */
	private String searchWord;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	
	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}



}
