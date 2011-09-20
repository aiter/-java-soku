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
import com.youku.soku.manage.service.HotwordService;
import com.youku.soku.manage.service.ItemService;
import com.youku.soku.manage.service.VideoService;
import com.youku.soku.manage.torque.Item;
import com.youku.soku.manage.torque.ItemPeer;


/**
 * <p>Insert or update a Item object to the persistent store.</p>
 * @author tanxiuguang
 *
 */
public class ItemAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create item action</p>
	 * 
	 * @return The "INPUT" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception{
		
		log.debug("=========Item action input==============");
		try {
			if(getItemId() == -1) {
				setTask(Constants.CREATE);	
				setItemId(-1);
				return INPUT; 
			} else {
				Item oldItem = ItemPeer.retrieveByPK(getItemId());
				setItem(oldItem);
				setItemId(oldItem.getItemId());
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
	 * <p>Insert or update an item</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the Items</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("item.list.pageSize");
			LargeSelect largeSelect = ItemPeer.findItemPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List itemList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("ItemAction getPageNumber() value is: " + getPageNumber());
			ItemService.findItemPagination(pageInfo, getSearchWord(), -1);
			
			List itemList = pageInfo.getResults();
			//itemList = ItemPeer.populateObjects(itemList);
			pageInfo.setResults(itemList);
			log.debug("itemList size is: " + itemList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an item from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		
		List videoList = VideoService.findVideoByItemId(getItemId());
		List hotwordList = HotwordService.findHotwordByItemId(getItemId());
		
		if(videoList.size() > 0 || hotwordList.size() > 0) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			String errorMsg = getText("error.item.delete");
			out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
			return null;
		} else {		
			Item item = ItemPeer.retrieveByPK(getItemId());
			ItemPeer.doDelete(item);
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
			
			List videoList = VideoService.findVideoByItemId(deleteId);
			List hotwordList = HotwordService.findHotwordByItemId(deleteId);
			
			if(videoList.size() > 0 || hotwordList.size() > 0) {
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				String errorMsg = getText("error.item.delete");
				out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
				return null;
			} else {		
				Item item = ItemPeer.retrieveByPK(deleteId);
				ItemPeer.doDelete(item);
			}
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an item object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("Item name is: " + getItem().getName());
			
			if (creating) {
				List<Item> itemList = ItemService.findItemByName(getItem().getName());
				boolean haveItem = itemList.size() > 0;
				log.debug("itemList size is:" + itemList.size());
				if (haveItem) {
					addActionError(getText("error.itemname.unique"));
					return INPUT;
				}
				
				getItem().save();
			} else {
				log.debug("Task update, itemId is " + getItem().getItemId());
				Item oldItem = ItemPeer.retrieveByPK(getItemId());
				oldItem.setName(getItem().getName());
				ItemPeer.doUpdate(oldItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	/** the object of the Item **/
	private Item item;
	
	/**
	 * <p>create the Item object</p>
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * <p>set the Item object</p>
	 * @param item Item ojbect
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	/**
	 * Item id, used for update the item object
	 */
	private int itemId;
	
	/**
	 * get item id
	 * @return itemId
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * set item id
	 * @param itemId
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * current page number, for the item list view
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
