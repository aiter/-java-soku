package com.youku.soku.manage.shield;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ShieldCategoryService;
import com.youku.soku.manage.torque.ShieldCategory;
import com.youku.soku.manage.torque.ShieldCategoryPeer;


/**
 * <p>Insert or update a ShieldCategory object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class ShieldCategoryAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create shieldCategory action</p>
	 * 
	 * @return The "INPUT" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception{
		
		log.debug("=========ShieldCategory action input==============");
		try {
			if(getShieldCategoryId() == -1) {
				setTask(Constants.CREATE);	
				setShieldCategoryId(-1);
				return INPUT; 
			} else {
				ShieldCategory oldShieldCategory = ShieldCategoryPeer.retrieveByPK(getShieldCategoryId());
				setShieldCategory(oldShieldCategory);
				setShieldCategoryId(oldShieldCategory.getId());
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
	 * <p>Insert or update an shieldCategory</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the ShieldCategorys</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("shieldCategory.list.pageSize");
			LargeSelect largeSelect = ShieldCategoryPeer.findShieldCategoryPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List shieldCategoryList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("ShieldCategoryAction getPageNumber() value is: " + getPageNumber());
			ShieldCategoryService.findShieldCategoryPagination(pageInfo, getSearchWord(), -1);
			
			List<?> shieldCategoryList = pageInfo.getResults();
			//shieldCategoryList = ShieldCategoryPeer.populateObjects(shieldCategoryList);
			pageInfo.setResults(shieldCategoryList);
			log.debug("shieldCategoryList size is: " + shieldCategoryList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an shieldCategory from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		ShieldCategory shieldCategory = ShieldCategoryPeer.retrieveByPK(getShieldCategoryId());
		ShieldCategoryPeer.doDelete(shieldCategory);
		
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
			
			ShieldCategory shieldCategory = ShieldCategoryPeer.retrieveByPK(deleteId);
			ShieldCategoryPeer.doDelete(shieldCategory);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an shieldCategory object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("ShieldCategory name is: " + getShieldCategory().getName());
			
			if (creating) {
				List<ShieldCategory> shieldCategoryList = ShieldCategoryService.findShieldCategoryByName(getShieldCategory().getName());
				boolean haveShieldCategory = shieldCategoryList.size() > 0;
				log.debug("shieldCategoryList size is:" + shieldCategoryList.size());
				if (haveShieldCategory) {
					addActionError(getText("error.shieldCategoryname.unique"));
					return INPUT;
				}
				
				getShieldCategory().save();
			} else {
				log.debug("Task update, shieldCategoryId is " + getShieldCategory().getId());
				ShieldCategory oldShieldCategory = ShieldCategoryPeer.retrieveByPK(getShieldCategoryId());
				oldShieldCategory.setName(getShieldCategory().getName());
				ShieldCategoryPeer.doUpdate(oldShieldCategory);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the ShieldCategory **/
	private ShieldCategory shieldCategory;
	
	/**
	 * <p>create the ShieldCategory object</p>
	 */
	public ShieldCategory getShieldCategory() {
		return shieldCategory;
	}
	
	/**
	 * <p>set the ShieldCategory object</p>
	 * @param shieldCategory ShieldCategory ojbect
	 */
	public void setShieldCategory(ShieldCategory shieldCategory) {
		this.shieldCategory = shieldCategory;
	}
	/**
	 * ShieldCategory id, used for update the shieldCategory object
	 */
	private int shieldCategoryId;
	
	/**
	 * get shieldCategory id
	 * @return shieldCategoryId
	 */
	public int getShieldCategoryId() {
		return shieldCategoryId;
	}
	
	/**
	 * set shieldCategory id
	 * @param shieldCategoryId
	 */
	public void setShieldCategoryId(int shieldCategoryId) {
		this.shieldCategoryId = shieldCategoryId;
	}
	
	/**
	 * current page number, for the shieldCategory list view
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
