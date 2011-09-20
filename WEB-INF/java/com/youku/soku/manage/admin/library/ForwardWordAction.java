package com.youku.soku.manage.admin.library;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.ForwardWord;
import com.youku.soku.library.load.ForwardWordPeer;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeForwardWord;
import com.youku.soku.library.load.ProgrammeForwardWordPeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ForwardWordService;
import com.youku.soku.manage.service.UserOperationService;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.soku.util.MyUtil;

public class ForwardWordAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	public String list() throws Exception {
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(100);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			int categoryId = -1;
			if (getCategoryFilter() != null && !getCategoryFilter().equals("")) {
				categoryId = Integer.valueOf(getCategoryFilter());
			}
			String searchWord = null;
			if (getSearchWord() != null) {
				searchWord = getSearchWord().trim();
			}

			ForwardWordService.searchProgrammeInfo(searchWord, categoryId, pageInfo);

			setPageInfo(pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		return Constants.LIST;
	}

	public String input() throws Exception {
		try {

			if (getForwardWordId() == -1) {
				setTask(Constants.CREATE);
				setForwardWordId(-1);
				return INPUT;
			} else {
				ForwardWord word = ForwardWordPeer.retrieveByPK(getForwardWordId());
				setForwardWord(word);
				setTask(Constants.EDIT);
				return INPUT;
			}

		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			log.error(e.getMessage(), e);
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		}
		return INPUT;

	}

	/**
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}

	public String execute() throws Exception {

		try {
			String forwardUrl = getForwardWord().getForwardUrl();

			if (forwardUrl.indexOf("?keyword=") > -1) {
				forwardUrl = forwardUrl.substring(0, forwardUrl.indexOf("?keyword="));
			}
			forwardUrl = forwardUrl.trim();

			boolean creating = Constants.CREATE.equals(getTask());
			if (creating) {
				
				List<ForwardWord> fwList = ForwardWordService.getForwardWord(getForwardWord().getForwardWord());
				if(fwList != null && !fwList.isEmpty()) {
					addActionError(getText("该跳转词已经存在"));
					return INPUT;
				}
				
				ForwardWord word = new ForwardWord();
				word.setForwardUrl(forwardUrl);
				word.setForwardWord(getForwardWord().getForwardWord());
				word.setCate(getForwardWord().getCate());
				word.setCreateTime(new Date());
				word.setUpdateTime(new Date());

				word.save();
				String detail = "word: " + word.getForwardWord() + "url: " + word.getForwardUrl();
				UserOperationService.logOperateForwardWord("create_forward_word", getUserName(), detail);
			} else {
				ForwardWord word = ForwardWordPeer.retrieveByPK(getForwardWordId());
				List<ForwardWord> fwList = ForwardWordService.getForwardWord(getForwardWord().getForwardWord());
				if(fwList != null && !fwList.isEmpty() && !word.getForwardWord().equals(getForwardWord().getForwardWord())) {
					addActionError(getText("该跳转词已经存在"));
					return INPUT;
				}
				String detail = "oldword: " + word.getForwardWord() + "oldurl: " + word.getForwardUrl();
				word.setForwardUrl(forwardUrl);
				word.setForwardWord(getForwardWord().getForwardWord());
				word.setCate(getForwardWord().getCate());
				word.setCreateTime(new Date());
				word.setUpdateTime(new Date());

				ForwardWordPeer.doUpdate(word);
				detail += "word: " + word.getForwardWord() + "url: " + word.getForwardUrl();
				UserOperationService.logOperateForwardWord("update_forward_word", getUserName(), detail);
			}

		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}

	public String delete() throws Exception {
		try {
			ForwardWord word = ForwardWordPeer.retrieveByPK(getForwardWordId());
			ForwardWordPeer.doDelete(word);
			String detail = "word: " + word.getForwardWord() + "url: " + word.getForwardUrl();
			UserOperationService.logOperateForwardWord("delete_forward_word", getUserName(), detail);
		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			log.error(e.getMessage(), e);
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

	public String importOldForwardWord() {
		try {
			String urlHead = "http://www.soku.com/detail/show/";
			List<ProgrammeForwardWord> forwardWordList = ProgrammeForwardWordPeer.doSelect(new Criteria());
			for (ProgrammeForwardWord pfw : forwardWordList) {
				String url = urlHead + MyUtil.encodeVideoId(pfw.getFkProgrammeId());
				Programme p = ProgrammePeer.retrieveByPK(pfw.getFkProgrammeId());
				ForwardWord word = new ForwardWord();
				word.setForwardUrl(url);
				word.setForwardWord(pfw.getForwardWord());
				word.setCate(p.getCate());
				word.setCreateTime(new Date());
				word.setUpdateTime(new Date());

				word.save();
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}

		return SUCCESS;
	}

	private int pageNumber;

	private PageInfo pageInfo;

	private ForwardWord forwardWord;

	private String startTime;

	private String expireTime;

	private int forwardWordStatus = -1;

	private int forwardWordId;

	public int getForwardWordId() {
		return forwardWordId;
	}

	public void setForwardWordId(int forwardWordId) {
		this.forwardWordId = forwardWordId;
	}

	public int getForwardWordStatus() {
		return forwardWordStatus;
	}

	public void setForwardWordStatus(int forwardWordStatus) {
		this.forwardWordStatus = forwardWordStatus;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public ForwardWord getForwardWord() {
		return forwardWord;
	}

	public void setForwardWord(ForwardWord forwardWord) {
		this.forwardWord = forwardWord;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

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
	 * the value for category filter
	 */
	private String categoryFilter;

	/**
	 * get the value of categoryFilter
	 */
	public String getCategoryFilter() {
		return categoryFilter;
	}

	public void setCategoryFilter(String categoryFilter) {
		this.categoryFilter = categoryFilter;
	}

	public Map<Integer, String> getForwardWordCategory() throws Exception {
		return SearchParameter.getForwardWordCategory();
	}
}
