package com.youku.soku.manage.admin.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeForwardWord;
import com.youku.soku.library.load.ProgrammeForwardWordPeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.manage.bo.ProgrammeForwardWordBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ProgrammeForwardWordService;
import com.youku.soku.manage.service.UserOperationService;
import com.youku.soku.manage.util.SearchParameter;

public class ProgrammeForwardWordAction extends BaseActionSupport {
	
	//old version
	
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
			if(getSearchWord() != null) {
				searchWord = getSearchWord().trim();
			}
			
			SearchParameter param = new SearchParameter();
			param.setSearchWord(searchWord);
			param.setAccuratelyMatched(getAccuratelyMatched() == 1 ? true : false);
			param.setCategoryId(categoryId);
			param.setFowardWordStatus(getForwardWordStatus());

			ProgrammeForwardWordService.searchProgrammeInfo(param, pageInfo);
			
			setPageInfo(pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		return Constants.LIST;
	}
	
	public String input() throws Exception {
		try {
			Programme p = ProgrammePeer.retrieveByPK(getProgrammeId());
			ProgrammeForwardWordBo pfwBo = new ProgrammeForwardWordBo();
			pfwBo.setProgrammeId(getProgrammeId());
			pfwBo.setProgrammeName(p.getName());
			ProgrammeForwardWordService.getProgrammeForwardWord(pfwBo);
			setPfwBo(pfwBo);
			setTask(Constants.EDIT);
			return INPUT;
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

			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Criteria crit = new Criteria();
			crit.add(ProgrammeForwardWordPeer.FK_PROGRAMME_ID, getProgrammeId());
			List<ProgrammeForwardWord> pfwList = ProgrammeForwardWordPeer.doSelect(crit);	
			ProgrammeForwardWordBo pfwBo = getPfwBo();
			List<String> forwardWordList = new ArrayList<String>();
			if(pfwBo != null) {
				String[] words = pfwBo.getForwardWords().split("[|]");
				for(String word : words) {
					forwardWordList.add(word);
				}
			}
			
			Date startTime = null;
			Date expireTime = null;
			if(getStartTime() != null) {
				startTime = format.parse(getStartTime());
			} 
			if(getExpireTime() != null) {
				expireTime = format.parse(getExpireTime());
			}
			
			StringBuilder builder = new StringBuilder();
			if(pfwList != null) {
				for(ProgrammeForwardWord pfw : pfwList) {
					if(builder.length() > 0) {
						builder.append('|');
					}
					builder.append(pfw.getForwardWord());
					int wordIndex = forwardWordList.indexOf(pfw.getForwardWord());
					log.info("pfw.getForwardWord()" + pfw.getForwardWord());
					log.info("wordIndex" + wordIndex);
					if(wordIndex > -1) {
						pfw.setStartTime(startTime);
						pfw.setExpireTime(expireTime);
						forwardWordList.remove(wordIndex);
						ProgrammeForwardWordPeer.doUpdate(pfw);
					} else {
						ProgrammeForwardWordPeer.doDelete(pfw.getPrimaryKey());
					}					
				}
			}
			
			if(!forwardWordList.isEmpty()) {
				for(String word : forwardWordList) {
					if(StringUtils.isBlank(word)){
						continue;
					}
					ProgrammeForwardWord pfw = new ProgrammeForwardWord();
					pfw.setForwardWord(word);
					
					pfw.setStartTime(startTime);
					pfw.setExpireTime(expireTime);
					pfw.setFkProgrammeId(getProgrammeId());
					pfw.save();
				}
			}
			
			builder.append("====>").append(getForwardWord());
			
			//UserOperationService.logOperateForwardWord("forward_word", getUserName(), getProgrammeId(), builder.toString());
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}

	
	private int pageNumber;

	private PageInfo pageInfo;
	
	private int accuratelyMatched;
	
	private int programmeId;
	
	private ProgrammeForwardWordBo pfwBo;
	
	private String forwardWord;
	
	private String startTime;
	
	private String expireTime;
	
	private int forwardWordStatus = -1;
	
	
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

	public String getForwardWord() {
		return forwardWord;
	}

	public void setForwardWord(String forwardWord) {
		this.forwardWord = forwardWord;
	}

	public ProgrammeForwardWordBo getPfwBo() {
		return pfwBo;
	}

	public void setPfwBo(ProgrammeForwardWordBo pfwBo) {
		this.pfwBo = pfwBo;
	}

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public int getAccuratelyMatched() {
		return accuratelyMatched;
	}

	public void setAccuratelyMatched(int accuratelyMatched) {
		this.accuratelyMatched = accuratelyMatched;
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
	
	public Map<Integer, String> getForwardWordStatusMap() {
		return SearchParameter.getForwardWordStatusMap();	
	}
	
	public Map<Integer, String> getDirectCategory() throws Exception {
		return SearchParameter.getDirectCategory();
	}
}
