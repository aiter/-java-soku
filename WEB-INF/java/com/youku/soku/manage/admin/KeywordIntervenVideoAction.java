package com.youku.soku.manage.admin;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.manage.bo.KeywordIntervenVideoBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ItemService;
import com.youku.soku.manage.service.KeywordIntervenService;
import com.youku.soku.manage.service.KeywordIntervenVideoService;
import com.youku.soku.manage.torque.KeywordIntervenVideo;
import com.youku.soku.manage.torque.KeywordIntervenVideoPeer;

public class KeywordIntervenVideoAction extends BaseActionSupport {


	
	private Logger log = Logger.getLogger(this.getClass());
	
	

	/**
	 * <p>Create video action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========KeywordIntervenVideo action input==============");
		try {
			
			if(getKeywordIntervenVideoId() == -1) {
				
				setTask(Constants.CREATE);	
				setKeywordIntervenVideoId(-1);
				return INPUT; 
			} else {
				KeywordIntervenVideo oldKeywordIntervenVideo = KeywordIntervenVideoPeer.retrieveByPK(getKeywordIntervenVideoId());
				
				setKeywordIntervenVideo(getKeywordIntervenVideoBo(oldKeywordIntervenVideo));
				setKeywordIntervenVideoId(oldKeywordIntervenVideo.getVideoId());
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
	 * <p>Insert or update an video</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the KeywordIntervenVideos</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("video.list.pageSize");
			LargeSelect largeSelect = KeywordIntervenVideoPeer.findKeywordIntervenVideoPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List videoList = largeSelect.getPage(getPageNumber());
			*/
			log.debug("the value of itemFilter is: " + getItemFilter());
			
			initKeywordList();
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("video.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("KeywordIntervenVideoAction getPageNumber() value is: " + getPageNumber());
			
			int itemId = -1;
			if(getItemFilter() != null) {
				itemId = Integer.valueOf(getItemFilter());
			}
			KeywordIntervenVideoService.findKeywordIntervenVideoPagination(pageInfo, getKeywordId(), getSearchWord());
			
			List videoList = pageInfo.getResults();
			//videoList = KeywordIntervenVideoPeer.populateObjects(videoList);
			List newList = new ArrayList();
			for(Object obj : videoList) {
				KeywordIntervenVideo video = (KeywordIntervenVideo) obj;
				Date expiredDate = video.getExpiredDate();
				KeywordIntervenVideoBo videoBo = getKeywordIntervenVideoBo((KeywordIntervenVideo) obj);
				if(expiredDate.before(new Date())) {
					videoBo.setName(hightlight(videoBo.getName()));
				}
				newList.add(videoBo);			
			}
			pageInfo.setResults(newList);
			log.debug("videoList size is: " + videoList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	/**
	 * <p>Delete an video from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception {
		KeywordIntervenVideo video = KeywordIntervenVideoPeer.retrieveByPK(getKeywordIntervenVideoId());
		KeywordIntervenVideoPeer.doDelete(video);
		
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
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			
			KeywordIntervenVideo video = KeywordIntervenVideoPeer.retrieveByPK(deleteId);
			KeywordIntervenVideoPeer.doDelete(video);
			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>Insert or update an video object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {			
			log.debug("====KeywordIntervenVideoAction execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			
			log.debug("the video is:" + getKeywordIntervenVideo());
			int isValid = 0;
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getName(), getText("error.videoname.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getUrl(), getText("error.videourl.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getKeywordId(), getText("error.videoitemid.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getVideoLength(), getText("error.videolength.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getSource(), getText("error.videosource.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getCategory(), getText("error.videcategory.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getIndexType(), getText("error.videoindextype.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getSort(), getText("error.sort.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getPicturePath(), getText("error.videopicturepath.required"));
			isValid += requiredFieldsCheck(getKeywordIntervenVideo().getExpiredStrDate(), getText("error.videoexpireddate.required"));
			
			if(isValid > 0) {
				return INPUT;
			}
			
			if (creating) {
				
				List<KeywordIntervenVideo> videoList = KeywordIntervenVideoService.findKeywordIntervenVideoByName(getKeywordIntervenVideo().getName());
				boolean haveKeywordIntervenVideo = videoList.size() > 0;
				log.debug("videoList size is:" + videoList.size());
				if (haveKeywordIntervenVideo) {
					addActionError(getText("error.videoname.unique"));
					return INPUT;
				}
				
				getKeywordIntervenVideo().setCreateDate(new Date());
				getKeywordIntervenVideo().setModifyDate(new Date());
				KeywordIntervenVideo v = getKeywordIntervenVideoVo(getKeywordIntervenVideo());
				v.setKeywordId(getKeywordId());
				v.save();
			} else {
				log.debug("Task update, videoId is " + getKeywordIntervenVideo().getVideoId());

				KeywordIntervenVideo video = getKeywordIntervenVideoVo(getKeywordIntervenVideo());
				KeywordIntervenVideo oldKeywordIntervenVideo = KeywordIntervenVideoPeer.retrieveByPK(getKeywordIntervenVideoId());
				//copyProperties(oldKeywordIntervenVideo, video);
				oldKeywordIntervenVideo.setName(video.getName());
				oldKeywordIntervenVideo.setVideoLength(video.getVideoLength());
				oldKeywordIntervenVideo.setKeywordId(getKeywordId());
				oldKeywordIntervenVideo.setExpiredDate(video.getExpiredDate());
				oldKeywordIntervenVideo.setUrl(video.getUrl());
				oldKeywordIntervenVideo.setCategory(video.getCategory());
				oldKeywordIntervenVideo.setSource(video.getSource());
				oldKeywordIntervenVideo.setSort(video.getSort());
				oldKeywordIntervenVideo.setPicturePath(video.getPicturePath());
				log.debug("***KeywordIntervenVideo object to update = :" + oldKeywordIntervenVideo);
				oldKeywordIntervenVideo.setModifyDate(new Date());
				oldKeywordIntervenVideo.setModified(true);
				KeywordIntervenVideoPeer.doUpdate(oldKeywordIntervenVideo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	
	private KeywordIntervenVideo getKeywordIntervenVideoVo(KeywordIntervenVideoBo videoBo) throws IllegalAccessException, InvocationTargetException, ParseException {
		KeywordIntervenVideo video = new KeywordIntervenVideo();
		String videoLength = videoBo.getVideoLength();
		String expiredStrDate = videoBo.getExpiredStrDate();
		copyProperties(video, videoBo);
		video.setVideoLength(generateKeywordIntervenVideoLength(videoLength));
		video.setExpiredDate(new SimpleDateFormat("yyyy-MM-dd").parse(expiredStrDate));
		return video;
	}
	
	private KeywordIntervenVideoBo getKeywordIntervenVideoBo(KeywordIntervenVideo video) throws IllegalAccessException, InvocationTargetException, TorqueException {
		KeywordIntervenVideoBo videoBo = new KeywordIntervenVideoBo();
		int videoLength = video.getVideoLength();
		Date expiredDate = video.getExpiredDate();
		copyProperties(videoBo, video);
		videoBo.setVideoLength(formateKeywordIntervenVideoLength(videoLength));
		videoBo.setItemName(ItemService.getItemMap().get(videoBo.getKeywordId()));
		videoBo.setExpiredStrDate(new SimpleDateFormat("yyyy-MM-dd").format(expiredDate));
		
		return videoBo;
	}
	
	private String hightlight(String str) {
		return "<span class=\"highlight\">" + str + "</span>";
	}
	
	private int generateKeywordIntervenVideoLength(String videoLength) {
		String[] times = videoLength.split(":");
		int videoLengthSecond = Integer.valueOf(times[0]) * 60 + Integer.valueOf(times[1]) * 60 + Integer.valueOf(times[2]);
		return videoLengthSecond;
	}
	
	private String formateKeywordIntervenVideoLength(int videoLength) {
		int videoLengthHour = videoLength / 3600;
		videoLength %= 3600;
		int videoLengthMinute = videoLength / 60;
		videoLength %= 60;
		int videoLengthSecond = videoLength;
		
		return String.format("%02d:%02d:%02d", videoLengthHour, videoLengthMinute, videoLengthSecond);
	}
	
	private int requiredFieldsCheck(String field, String errorMsg) {
		if(field == null) {
			addActionError(errorMsg);	
			return 1;
		} else if (field.replace(" ", "").length() == 0) {
			addActionError(errorMsg);
			return 1;
		} else {
			return 0;
		}
	}
	
	private int requiredFieldsCheck(Integer field, String errorMsg) {
		if(field == null) {
			addActionError(errorMsg);
			return 1;
		} else if (field == -1) {
			addActionError(errorMsg);
			return 1;
		} else {
			return 0;
		}
	}
	
	private void initKeywordList() throws Exception {
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageSize(Integer.valueOf(getText("item.list.pageSize")));
		if(getPageNumber() == 0) {
			setPageNumber(1);
		}
		pageInfo.setCurrentPageNumber(getPageNumber());
		
		log.debug("KeywordIntervenAction getPageNumber() value is: " + getPageNumber());
		KeywordIntervenService.findKeywordIntervenPagination(pageInfo, null, -1);
		
		List keywordIntervenList = pageInfo.getResults();
		setKeywordList(keywordIntervenList);
	}
	
	/** the object of the KeywordIntervenVideo **/
	private KeywordIntervenVideoBo video;
	
	/**
	 * <p>create the KeywordIntervenVideo object</p>
	 */
	public KeywordIntervenVideoBo getKeywordIntervenVideo() {
		return video;
	}
	
	/**
	 * <p>set the KeywordIntervenVideo object</p>
	 * @param video KeywordIntervenVideo ojbect
	 */
	public void setKeywordIntervenVideo(KeywordIntervenVideoBo video) {
		this.video = video;
	}
	/**
	 * KeywordIntervenVideo id, used for update the video object
	 */
	private int videoId;
	
	/**
	 * get video id
	 * @return videoId
	 */
	public int getKeywordIntervenVideoId() {
		return videoId;
	}
	
	/**
	 * set video id
	 * @param videoId
	 */
	public void setKeywordIntervenVideoId(int videoId) {
		this.videoId = videoId;
	}
	
	/**
	 * current page number, for the video list view
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
	 * the video object list
	 */
	private List videoList;
	
	/**
	 * get the video list
	 */
	public List getKeywordIntervenVideoList() {
		return videoList;
	}
	
	/**
	 * set the video list
	 */
	public void setKeywordIntervenVideoList(List videoList) {
		this.videoList = videoList;
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
	 * Keyword id
	 */
	private int keywordId;
	public int getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	
	private List keywordList;
	
	public List getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List keywordList) {
		this.keywordList = keywordList;
	}
	
	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}

}
