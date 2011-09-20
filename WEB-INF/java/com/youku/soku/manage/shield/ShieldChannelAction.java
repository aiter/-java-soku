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
import com.youku.soku.manage.service.ShieldChannelService;
import com.youku.soku.manage.torque.ShieldChannel;
import com.youku.soku.manage.torque.ShieldChannelPeer;


/**
 * <p>Insert or update a ShieldChannel object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class ShieldChannelAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create shieldChannel action</p>
	 * 
	 * @return The "INPUT" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception{
		
		log.debug("=========ShieldChannel action input==============");
		try {
			if(getShieldChannelId() == -1) {
				setTask(Constants.CREATE);	
				setShieldChannelId(-1);
				return INPUT; 
			} else {
				ShieldChannel oldShieldChannel = ShieldChannelPeer.retrieveByPK(getShieldChannelId());
				setShieldChannel(oldShieldChannel);
				setShieldChannelId(oldShieldChannel.getId());
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
	 * <p>Insert or update an shieldChannel</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the ShieldChannels</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("shieldChannel.list.pageSize");
			LargeSelect largeSelect = ShieldChannelPeer.findShieldChannelPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List shieldChannelList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("ShieldChannelAction getPageNumber() value is: " + getPageNumber());
			ShieldChannelService.findShieldChannelPagination(pageInfo, getSearchWord(), -1);
			
			List shieldChannelList = pageInfo.getResults();
			//shieldChannelList = ShieldChannelPeer.populateObjects(shieldChannelList);
			pageInfo.setResults(shieldChannelList);
			log.debug("shieldChannelList size is: " + shieldChannelList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an shieldChannel from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		ShieldChannel shieldChannel = ShieldChannelPeer.retrieveByPK(getShieldChannelId());
		ShieldChannelPeer.doDelete(shieldChannel);
		
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
			
			ShieldChannel shieldChannel = ShieldChannelPeer.retrieveByPK(deleteId);
			ShieldChannelPeer.doDelete(shieldChannel);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an shieldChannel object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("ShieldChannel name is: " + getShieldChannel().getName());
			
			if (creating) {
				List<ShieldChannel> shieldChannelList = ShieldChannelService.findShieldChannelByName(getShieldChannel().getName());
				boolean haveShieldChannel = shieldChannelList.size() > 0;
				log.debug("shieldChannelList size is:" + shieldChannelList.size());
				if (haveShieldChannel) {
					addActionError(getText("error.shieldChannelname.unique"));
					return INPUT;
				}
				
				getShieldChannel().save();
			} else {
				log.debug("Task update, shieldChannelId is " + getShieldChannel().getId());
				ShieldChannel oldShieldChannel = ShieldChannelPeer.retrieveByPK(getShieldChannelId());
				oldShieldChannel.setName(getShieldChannel().getName());
				ShieldChannelPeer.doUpdate(oldShieldChannel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the ShieldChannel **/
	private ShieldChannel shieldChannel;
	
	/**
	 * <p>create the ShieldChannel object</p>
	 */
	public ShieldChannel getShieldChannel() {
		return shieldChannel;
	}
	
	/**
	 * <p>set the ShieldChannel object</p>
	 * @param shieldChannel ShieldChannel ojbect
	 */
	public void setShieldChannel(ShieldChannel shieldChannel) {
		this.shieldChannel = shieldChannel;
	}
	/**
	 * ShieldChannel id, used for update the shieldChannel object
	 */
	private int shieldChannelId;
	
	/**
	 * get shieldChannel id
	 * @return shieldChannelId
	 */
	public int getShieldChannelId() {
		return shieldChannelId;
	}
	
	/**
	 * set shieldChannel id
	 * @param shieldChannelId
	 */
	public void setShieldChannelId(int shieldChannelId) {
		this.shieldChannelId = shieldChannelId;
	}
	
	/**
	 * current page number, for the shieldChannel list view
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
