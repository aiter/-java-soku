package com.youku.soku.manage.admin;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;

import com.youku.soku.library.load.Category;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.entity.MajorTerm;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.service.CategoryService;
import com.youku.soku.manage.service.MajorTermService;

/**
 * <p>
 * Insert or update a MajorTerm object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class MajorTermAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * <p>
	 * Create majorTerm action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String input() throws Exception {

		log.debug("=========MajorTerm action input==============");

		if (getMajorTermId() == -1) {
			setTask(Constants.CREATE);
			setMajorTermId(-1);
			return INPUT;
		} else {
			MajorTerm oldMajorTerm = MajorTermService
					.getMajorTermById(getMajorTermId());

	
			if (oldMajorTerm == null) {
				setTask(Constants.CREATE);
				setMajorTermId(-1);
				return INPUT;
			}

			setMajorTerm(oldMajorTerm);
			setMajorTermId(oldMajorTerm.getId());
			setTask(Constants.EDIT);
			return INPUT;
		}

	}
	
	
	/**
	 * <p>
	 * Preview Html content
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String preview() throws Exception {

		log.debug("=========MajorTerm action input==============");

		if (getMajorTermId() == -1) {
			setTask(Constants.CREATE);
			setMajorTermId(-1);
			return INPUT;
		} else {
			MajorTerm oldMajorTerm = MajorTermService
					.getMajorTermById(getMajorTermId());

	
			if (oldMajorTerm == null) {
				setTask(Constants.CREATE);
				setMajorTermId(-1);
				return INPUT;
			}
			setMajorTerm(oldMajorTerm);
			
			return "preview";
		}

	}

	/**
	 * <p>
	 * Insert or update an item
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}

	/**
	 * <p>
	 * List the MajorTerms
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() {

		try {
			/*
			 * String pageSize = getText("item.list.pageSize"); LargeSelect
			 * largeSelect =
			 * MajorTermPeer.findMajorTermPagination(Integer.valueOf(pageSize));
			 * log.debug("page number is: " + getPageNumber());
			 * log.debug("total page number is: " +
			 * largeSelect.getTotalPages());
			 * 
			 * if(largeSelect.getTotalPages() < getPageNumber()) { return ERROR;
			 * } List itemList = largeSelect.getPage(getPageNumber());
			 */

			PageInfo pageInfo = new PageInfo();
			pageInfo
					.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log.debug("MajorTermAction getPageNumber() value is: "
					+ getPageNumber());
			MajorTermService.getMajorTermPagination(status, searchWord,
					pageInfo);

			setPageInfo(pageInfo);

			return Constants.LIST;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}

	/**
	 * <p>
	 * Delete an item from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String delete() throws Exception {

		MajorTerm item = MajorTermService.getMajorTermById(getMajorTermId());
		if (item != null) {
			MajorTermService.deleteMajorTerm(item.getId());
		}

		return SUCCESS;
	}

	/**
	 * <p>
	 * Batch delete an majorTerm from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String batchdelete() throws Exception {

		System.out.println("xxx" + Arrays.toString(getBatchdeleteids()));
		for (int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			MajorTerm item = MajorTermService.getMajorTermById(deleteId);
			if (item != null) {
				MajorTermService.deleteMajorTerm(item.getId());
			}
		}

		return SUCCESS;
	}

	/**
	 * <p>
	 * Insert or update an item object to the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String execute() throws Exception {

		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			
			String destUrl = convertUrlText(getMajorTerm().getUrlText());
			
			/*if(destUrl == null) {
				addActionError(getText("所填url不符合规则"));
				return INPUT;
			}*/
			
			if (creating) {
				List<MajorTerm> itemList = MajorTermService
						.getMajorTermByKeyword(getMajorTerm().getKeyword(), getMajorTerm().getCateId());
				boolean haveMajorTerm = itemList.size() > 0;
				log.debug("itemList size is:" + itemList.size());
				if (haveMajorTerm) {
					addActionError(getText("关键字不能重复"));
					return INPUT;
				}
				getMajorTerm().setCreateTime(new Date());
				getMajorTerm().setDestUrl(convertUrlText(getMajorTerm().getUrlText()));
				MajorTermService.insertMajotTerm(getMajorTerm());
			} else {
				List<MajorTerm> itemList = MajorTermService
						.getMajorTermByKeyword(getMajorTerm().getKeyword(), getMajorTerm().getCateId());
				boolean haveMajorTerm = itemList.size() > 0;
				
				log.debug("Task update, majorTerm id is "
						+ getMajorTerm().getId());
				MajorTerm oldMajorTerm = MajorTermService
						.getMajorTermById(getMajorTermId());
				boolean isSameWord = oldMajorTerm.getKeyword().equals(getMajorTerm().getKeyword()) && oldMajorTerm.getCateId() == getMajorTerm().getCateId();
				if (haveMajorTerm && !isSameWord) {
					addActionError(getText("关键字不能重复"));
					return INPUT;
				}
				oldMajorTerm.setKeyword(getMajorTerm().getKeyword());
				oldMajorTerm.setCateId(getMajorTerm().getCateId());
				oldMajorTerm.setHtmlText(getMajorTerm().getHtmlText());
				oldMajorTerm.setUrlText(getMajorTerm().getUrlText());
				oldMajorTerm.setStatus(getMajorTerm().getStatus());
				oldMajorTerm.setDestUrl(convertUrlText(getMajorTerm().getUrlText()));
				oldMajorTerm.setUpdaetTime(new Date());
				oldMajorTerm.setLabel(getMajorTerm().getLabel());
				log.debug("Task doUpdate majorTerm: " + oldMajorTerm);
				MajorTermService.updateMajorTerm(oldMajorTerm);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	private String convertUrlText(String url) {
		
			
		String urlPattern = "http://.*/channel/([^_]*)_([^_]*)_([^_]*)_([^_]*)_([^_]*)_([^_]*)/(.*).html";
		Pattern p = Pattern.compile(urlPattern);
		Matcher m = p.matcher(url);
		
		String destUrl = "http://www.soku.com/channel/subject.jsp?type=$1&area=$2&year=$3&letter=$4&start=$5&limit=$6&channel=$7";
		
		String destPersonUrl = "http://www.soku.com/channel/person.jsp?type=$1&area=$2&sex=$3&letter=$4&start=$5&limit=$6&channel=$7";
		
		String destRegularUrl = "http://www.soku.com//channel/channel.jsp?type=$1&amp;area=$2&amp;year=$3&amp;letter=$4&amp;start=$5&amp;limit=$6&amp;channel=$7";
		if(m.matches()) {
			if(m.group(7).equals("person")) {
				destUrl = destPersonUrl;
			}
			if(m.group(7).matches("(topconcern|fun|music|sports|science|anime)")) {
				destUrl = destRegularUrl;
			}
			for(int i = 0; i <= m.groupCount(); i++) {
				
				if(i == 6) {
					destUrl = destUrl.replace("$" + i, "5");
				} else {
					destUrl = destUrl.replace("$" + i, m.group(i));
				}
			}
			System.out.println(destUrl + "&json=json");
			return destUrl + "&json=json";
		} else {
			String regularTopPattern = "http://.*/channel/(.*).html";
			String regularTopDestUrl = "http://www.soku.com/channel/channel.jsp?channel=$1&limit=5";
			Pattern rp = Pattern.compile(regularTopPattern);
			Matcher rm = rp.matcher(url);
			if(rm.matches()) {
				return regularTopDestUrl.replace("$1", rm.group(1)) + "&json=json";
			}
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(new MajorTermAction().convertUrlText("http://10.101.8.112/channel/topconcern.html"));
	}
	
	
	
	/**
	 * current page number, for the majorTerm list view
	 */
	private int pageNumber;

	/**
	 * the pagination object
	 */
	private PageInfo pageInfo;

	/**
	 * the value for the word for searching
	 */
	private String searchWord;

	/**
	 * MajorTerm id, used for update the majorTerm object
	 */
	private int majorTermId;

	/**
	 * get majorTerm id
	 * 
	 * @return majorTermId
	 */
	public int getMajorTermId() {
		return majorTermId;
	}

	/**
	 * set majorTerm id
	 * 
	 * @param majorTermId
	 */
	public void setMajorTermId(int majorTermId) {
		this.majorTermId = majorTermId;
	}

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

	private MajorTerm majorTerm;

	private int status;

	public MajorTerm getMajorTerm() {
		return majorTerm;
	}

	public void setMajorTerm(MajorTerm majorTerm) {
		this.majorTerm = majorTerm;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public List<Category> getCategoryList() throws TorqueException{
		List<Category> cList = CategoryService.findCategory();
		Category other = new Category();
		other.setId(0);
		other.setName("其他");
		cList.add(other);
		return cList;
	}

}
