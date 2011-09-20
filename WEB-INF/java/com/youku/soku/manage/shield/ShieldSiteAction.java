package com.youku.soku.manage.shield;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.entity.ShieldSiteConstants;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ShieldSiteService;
import com.youku.soku.manage.torque.ShieldSite;
import com.youku.soku.manage.torque.ShieldSitePeer;


/**
 * <p>Insert or update a ShieldSite object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class ShieldSiteAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create shieldSite action</p>
	 * 
	 * @return The "INPUT" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception{
		
		log.debug("=========ShieldSite action input==============");
		try {
			if(getShieldSiteId() == -1) {
				setTask(Constants.CREATE);	
				setShieldSiteId(-1);
				return INPUT; 
			} else {
				ShieldSite oldShieldSite = ShieldSitePeer.retrieveByPK(getShieldSiteId());
				setShieldSite(oldShieldSite);
				setShieldSiteId(oldShieldSite.getId());
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
	 * <p>Insert or update an shieldSite</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the ShieldSites</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("shieldSite.list.pageSize");
			LargeSelect largeSelect = ShieldSitePeer.findShieldSitePagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List shieldSiteList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("ShieldSiteAction getPageNumber() value is: " + getPageNumber());
			ShieldSiteService.findShieldSitePagination(pageInfo, getSearchWord(), -1);
			
			List shieldSiteList = pageInfo.getResults();
			//shieldSiteList = ShieldSitePeer.populateObjects(shieldSiteList);
			pageInfo.setResults(shieldSiteList);
			log.debug("shieldSiteList size is: " + shieldSiteList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an shieldSite from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		ShieldSite shieldSite = ShieldSitePeer.retrieveByPK(getShieldSiteId());
		ShieldSitePeer.doDelete(shieldSite);
		
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
			
			ShieldSite shieldSite = ShieldSitePeer.retrieveByPK(deleteId);
			ShieldSitePeer.doDelete(shieldSite);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an shieldSite object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("ShieldSite name is: " + getShieldSite().getName());
			
			if (creating) {
				List<ShieldSite> shieldSiteList = ShieldSiteService.findShieldSiteByName(getShieldSite().getName());
				boolean haveShieldSite = shieldSiteList.size() > 0;
				log.debug("shieldSiteList size is:" + shieldSiteList.size());
				if (haveShieldSite) {
					addActionError(getText("error.shieldSitename.unique"));
					return INPUT;
				}
				getShieldSite().setSiteId(ShieldSiteService.getSiteId(getShieldSite().getUrl()));
				getShieldSite().save();
			} else {
				log.debug("Task update, shieldSiteId is " + getShieldSite().getId());
				ShieldSite oldShieldSite = ShieldSitePeer.retrieveByPK(getShieldSiteId());
				oldShieldSite.setName(getShieldSite().getName());
				oldShieldSite.setUrl(getShieldSite().getUrl());
				oldShieldSite.setSiteCategory(getShieldSite().getSiteCategory());
				oldShieldSite.setSiteLevel(getShieldSite().getSiteLevel());
				oldShieldSite.setSiteId(ShieldSiteService.getSiteId(getShieldSite().getUrl()));
				ShieldSitePeer.doUpdate(oldShieldSite);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the ShieldSite **/
	private ShieldSite shieldSite;
	
	/**
	 * <p>create the ShieldSite object</p>
	 */
	public ShieldSite getShieldSite() {
		return shieldSite;
	}
	
	/**
	 * <p>set the ShieldSite object</p>
	 * @param shieldSite ShieldSite ojbect
	 */
	public void setShieldSite(ShieldSite shieldSite) {
		this.shieldSite = shieldSite;
	}
	/**
	 * ShieldSite id, used for update the shieldSite object
	 */
	private int shieldSiteId;
	
	/**
	 * get shieldSite id
	 * @return shieldSiteId
	 */
	public int getShieldSiteId() {
		return shieldSiteId;
	}
	
	/**
	 * set shieldSite id
	 * @param shieldSiteId
	 */
	public void setShieldSiteId(int shieldSiteId) {
		this.shieldSiteId = shieldSiteId;
	}
	
	/**
	 * current page number, for the shieldSite list view
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

	public Map<Integer, String> getRadioSiteLevelMap() {
		return ShieldSiteConstants.RADIOSITELEVELMAP;
	}
	
	public Map<Integer, String> getRadioSiteCategoryMap() {
		return ShieldSiteConstants.RADIOSITECATEGORYMAP;
	}
	

}
