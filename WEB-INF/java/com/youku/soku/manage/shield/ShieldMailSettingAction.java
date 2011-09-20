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
import com.youku.soku.manage.service.ShieldMailSettingService;
import com.youku.soku.manage.torque.ShieldMailSetting;
import com.youku.soku.manage.torque.ShieldMailSettingPeer;


/**
 * <p>Insert or update a ShieldMailSetting object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class ShieldMailSettingAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create shieldMailSetting action</p>
	 * 
	 * @return The "INPUT" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception{
		
		log.debug("=========ShieldMailSetting action input==============");
		try {
			if(getShieldMailSettingId() == -1) {
				setTask(Constants.CREATE);	
				setShieldMailSettingId(-1);
				return INPUT; 
			} else {
				ShieldMailSetting oldShieldMailSetting = ShieldMailSettingPeer.retrieveByPK(getShieldMailSettingId());
				setShieldMailSetting(oldShieldMailSetting);
				setShieldMailSettingId(oldShieldMailSetting.getId());
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
	 * <p>Insert or update an shieldMailSetting</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the ShieldMailSettings</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("shieldMailSetting.list.pageSize");
			LargeSelect largeSelect = ShieldMailSettingPeer.findShieldMailSettingPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List shieldMailSettingList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("ShieldMailSettingAction getPageNumber() value is: " + getPageNumber());
			ShieldMailSettingService.findShieldMailSettingPagination(pageInfo, getSearchWord(), -1);
			
			List shieldMailSettingList = pageInfo.getResults();
			//shieldMailSettingList = ShieldMailSettingPeer.populateObjects(shieldMailSettingList);
			pageInfo.setResults(shieldMailSettingList);
			log.debug("shieldMailSettingList size is: " + shieldMailSettingList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an shieldMailSetting from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		ShieldMailSetting shieldMailSetting = ShieldMailSettingPeer.retrieveByPK(getShieldMailSettingId());
		ShieldMailSettingPeer.doDelete(shieldMailSetting);
		
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
			
			ShieldMailSetting shieldMailSetting = ShieldMailSettingPeer.retrieveByPK(deleteId);
			ShieldMailSettingPeer.doDelete(shieldMailSetting);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an shieldMailSetting object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("ShieldMailSetting name is: " + getShieldMailSetting().getEmail());
			
			if (creating) {
			
				
				getShieldMailSetting().save();
			} else {
				log.debug("Task update, shieldMailSettingId is " + getShieldMailSetting().getId());
				ShieldMailSetting oldShieldMailSetting = ShieldMailSettingPeer.retrieveByPK(getShieldMailSettingId());
				oldShieldMailSetting.setEmail(getShieldMailSetting().getEmail());
				oldShieldMailSetting.setPeriods(getShieldMailSetting().getPeriods());
				ShieldMailSettingPeer.doUpdate(oldShieldMailSetting);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the ShieldMailSetting **/
	private ShieldMailSetting shieldMailSetting;
	
	/**
	 * <p>create the ShieldMailSetting object</p>
	 */
	public ShieldMailSetting getShieldMailSetting() {
		return shieldMailSetting;
	}
	
	/**
	 * <p>set the ShieldMailSetting object</p>
	 * @param shieldMailSetting ShieldMailSetting ojbect
	 */
	public void setShieldMailSetting(ShieldMailSetting shieldMailSetting) {
		this.shieldMailSetting = shieldMailSetting;
	}
	/**
	 * ShieldMailSetting id, used for update the shieldMailSetting object
	 */
	private int shieldMailSettingId;
	
	/**
	 * get shieldMailSetting id
	 * @return shieldMailSettingId
	 */
	public int getShieldMailSettingId() {
		return shieldMailSettingId;
	}
	
	/**
	 * set shieldMailSetting id
	 * @param shieldMailSettingId
	 */
	public void setShieldMailSettingId(int shieldMailSettingId) {
		this.shieldMailSettingId = shieldMailSettingId;
	}
	
	/**
	 * current page number, for the shieldMailSetting list view
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
