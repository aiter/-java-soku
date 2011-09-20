package com.youku.soku.manage.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.bo.HotwordBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.HotwordService;
import com.youku.soku.manage.service.ItemService;
import com.youku.soku.manage.torque.Hotword;
import com.youku.soku.manage.torque.HotwordPeer;

public class HotwordAction extends BaseActionSupport {


	
	private Logger log = Logger.getLogger(this.getClass());
	
	
	
	
	/**
	 * <p>Create item action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========Item action input==============");
		try {			
			if(getHotwordId() == -1) {
				setTask(Constants.CREATE);	
				setHotwordId(-1);
				return INPUT; 
			} else {
				Hotword oldHotword = HotwordPeer.retrieveByPK(getHotwordId());
				setHotword(getHotwordBo(oldHotword));
				setHotwordId(oldHotword.getWordId());
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
	 * <p>List the Hotwords</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() throws Exception{
		
		try {
			/*String pageSize = getText("item.list.pageSize");
			LargeSelect largeSelect = HotwordPeer.findHotwordPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List itemList = largeSelect.getPage(getPageNumber());
			*/
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("hotword.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("HotwordAction getPageNumber() value is: " + getPageNumber());
			log.debug("HotwordAction pageInfo current pageNumber value is: " + getPageNumber());
			log.debug("HotwordAction getSearchWord is: " + getSearchWord());
			int itemId = -1;
			if(getItemFilter() != null) {
				itemId = Integer.valueOf(getItemFilter());
			}
			HotwordService.findHotwordPagination(pageInfo, getSearchWord(), itemId);
			
			List hotwordList = pageInfo.getResults();
			log.debug("how word list is: " + hotwordList);
			//hotwordList = HotwordPeer.populateObjects(hotwordList);
			List newList = new ArrayList();
			for(Object obj : hotwordList) {	
				log.debug((Hotword) obj);
				newList.add(getHotwordBo((Hotword) obj));			
			}
			pageInfo.setResults(newList);
			log.debug("hotwordList size is: " + hotwordList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an hotword from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception{
		log.debug(" ** delete hotword action ");
		log.debug(" ====try** delete hotword action ");
		Hotword hotword = HotwordPeer.retrieveByPK(getHotwordId());
		log.debug(hotword);
		HotwordPeer.doDelete(hotword);
		
		return SUCCESS;
	}
	
	/**
	 * <p>Batch delete an Hotword from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			Hotword hotword = HotwordPeer.retrieveByPK(deleteId);
			log.debug(hotword);
			HotwordPeer.doDelete(hotword);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an hotword object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {		
			
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("Hotword name is: " + getHotword().getName());
			
			if (creating) {
				
				List<Hotword> hotwordList = HotwordService.findHotwordByName(getHotword().getName());
				boolean haveHotword = hotwordList.size() > 0;
				log.debug("hotwordList size is:" + hotwordList.size());
				
				if (haveHotword) {
					addActionError(getText("error.hotwordname.unique"));
					return INPUT;
				}
				
				getHotword().setCreateDate(new Date());
				getHotword().setModifyDate(new Date());
				Hotword h = getHotwordVo(getHotword());
				log.debug(h);
				h.save();
			} else {
				log.debug("Task update, hotwordId is " + getHotword().getWordId());
				Hotword oldHotword = HotwordPeer.retrieveByPK(getHotwordId());
				oldHotword.setName(getHotword().getName());
				oldHotword.setIndexType(getHotword().getIndexType());
				oldHotword.setItemId(getHotword().getItemId());
				oldHotword.setSort(getHotword().getSort());
				getHotword().setModifyDate(new Date());
				HotwordPeer.doUpdate(oldHotword);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	private HotwordBo getHotwordBo(Hotword hotword) throws IllegalAccessException, InvocationTargetException, TorqueException {
		
		HotwordBo hotwordBo = new HotwordBo();
		copyProperties(hotwordBo, hotword);
		log.debug(getItemMap());
		hotwordBo.setItemName(ItemService.getItemMap().get(hotwordBo.getItemId()));
		
		return hotwordBo;
	}
	
	private Hotword getHotwordVo(HotwordBo hotwordBo) throws IllegalAccessException, InvocationTargetException {
		
		Hotword hotword = new Hotword();
		copyProperties(hotword, hotwordBo);
		
		return hotword;
	}
	
	/** the object of the Hotword **/
	private HotwordBo hotword;
	
	/**
	 * <p>create the Hotword object</p>
	 */
	public HotwordBo getHotword() {
		return hotword;
	}
	
	/**
	 * <p>set the Hotword object</p>
	 * @param hotword Hotword ojbect
	 */
	public void setHotword(HotwordBo hotword) {
		this.hotword = hotword;
	}
	/**
	 * Hotword id, used for update the hotword object
	 */
	private int hotwordId;
	
	/**
	 * get hotword id
	 * @return hotwordId
	 */
	public int getHotwordId() {
		return hotwordId;
	}
	
	/**
	 * set hotword id
	 * @param hotwordId
	 */
	public void setHotwordId(int hotwordId) {
		this.hotwordId = hotwordId;
	}
	
	/**
	 * current page number, for the hotword list view
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
	 * the hotword object list
	 */
	private List hotwordList;
	
	/**
	 * get the hotword list
	 */
	public List getHotwordList() {
		return hotwordList;
	}
	
	/**
	 * set the hotword list
	 */
	public void setHotwordList(List hotwordList) {
		this.hotwordList = hotwordList;
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
	
	/**
	 * the value for item filter
	 */
	private String itemFilter;
	
	/**
	 * get the value of itemFilter
	 */
	public String getItemFilter() {
		return itemFilter;
	}
	
	/**
	 * set the value of itemFilter
	 */
	public void setItemFilter(String itemFilter) {
		this.itemFilter = itemFilter;
	}

	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}
}
