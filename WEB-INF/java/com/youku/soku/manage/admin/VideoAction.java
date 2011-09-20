package com.youku.soku.manage.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.soku.library.load.Category;
import com.youku.soku.manage.bo.VideoBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.VideoService;
import com.youku.soku.manage.torque.Video;
import com.youku.soku.manage.torque.VideoPeer;

public class VideoAction extends BaseActionSupport {


	
	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>Create video action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========Video action input==============");
		try {
			
			if(getVideoId() == -1) {
				
				setTask(Constants.CREATE);	
				setVideoId(-1);
				return INPUT; 
			} else {
				Video oldVideo = VideoPeer.retrieveByPK(getVideoId());
				
				setVideo(getVideoBo(oldVideo));
				setVideoId(oldVideo.getVideoId());
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
	 * <p>List the Videos</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() {
		
		try {
			/*String pageSize = getText("video.list.pageSize");
			LargeSelect largeSelect = VideoPeer.findVideoPagination(Integer.valueOf(pageSize));
			log.debug("page number is: " + getPageNumber());
			log.debug("total page number is: " + largeSelect.getTotalPages());
			
			if(largeSelect.getTotalPages() < getPageNumber()) {
				return ERROR;
			}
			List videoList = largeSelect.getPage(getPageNumber());
			*/
			log.debug("the value of itemFilter is: " + getItemFilter());
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("video.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("VideoAction getPageNumber() value is: " + getPageNumber());
			
			
			VideoService.findVideoPagination(pageInfo, getSearchWord(), getItemFilter());
			
			List videoList = pageInfo.getResults();
			//videoList = VideoPeer.populateObjects(videoList);
			List newList = new ArrayList();
			for(Object obj : videoList) {				
				newList.add(getVideoBo((Video) obj));			
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
		Video video = VideoPeer.retrieveByPK(getVideoId());
		VideoPeer.doDelete(video);
		
		return SUCCESS;
	}
	
	/**
	 * <p>Batch delete an Video from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		//List videoList = VideoService.findVideoByKeywordIntervenId(getKeywordIntervenId());
		
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			Video video = VideoPeer.retrieveByPK(deleteId);
			VideoPeer.doDelete(video);
			
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
			log.debug("====VideoAction execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			
			log.debug("the video is:" + getVideo());
			int isValid = 0;
			isValid += requiredFieldsCheck(getVideo().getName(), getText("error.videoname.required"));
			isValid += requiredFieldsCheck(getVideo().getUrl(), getText("error.videourl.required"));
			isValid += requiredFieldsCheck(getVideo().getVideoLength(), getText("error.videolength.required"));
			isValid += requiredFieldsCheck(getVideo().getSource(), getText("error.videosource.required"));
			isValid += requiredFieldsCheck(getVideo().getCategory(), getText("error.videcategory.required"));
			isValid += requiredFieldsCheck(getVideo().getIndexType(), getText("error.videoindextype.required"));
			isValid += requiredFieldsCheck(getVideo().getSort(), getText("error.sort.required"));
			isValid += requiredFieldsCheck(getVideo().getPicturePath(), getText("error.videopicturepath.required"));
			
			/*if(isValid > 0) {
				return INPUT;
			}*/
			
			if (creating) {
				
				List<Video> videoList = VideoService.findVideoByName(getVideo().getName());
				boolean haveVideo = videoList.size() > 0;
				log.debug("videoList size is:" + videoList.size());
				if (haveVideo) {
					addActionError(getText("error.videoname.unique"));
					return INPUT;
				}
				
				getVideo().setCreateDate(new Date());
				getVideo().setModifyDate(new Date());
				Video v = getVideoVo(getVideo());
				v.save();
			} else {
				log.debug("Task update, videoId is " + getVideo().getVideoId());

				Video video = getVideoVo(getVideo());
				Video oldVideo = VideoPeer.retrieveByPK(getVideoId());
				//copyProperties(oldVideo, video);
				oldVideo.setName(video.getName());
				oldVideo.setVideoLength(video.getVideoLength());
				oldVideo.setItemId(video.getItemId());
				oldVideo.setUrl(video.getUrl());
				oldVideo.setCategory(video.getCategory());
				oldVideo.setSource(video.getSource());
				oldVideo.setSort(video.getSort());
				oldVideo.setPicturePath(video.getPicturePath());
				log.debug("***Video object to update = :" + oldVideo);
				oldVideo.setModifyDate(new Date());
				oldVideo.setModified(true);
				VideoPeer.doUpdate(oldVideo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	
	private Video getVideoVo(VideoBo videoBo) throws IllegalAccessException, InvocationTargetException {
		Video video = new Video();
		String videoLength = videoBo.getVideoLength();
		copyProperties(video, videoBo);
		video.setVideoLength(generateVideoLength(videoLength));
		
		return video;
	}
	
	private VideoBo getVideoBo(Video video) throws IllegalAccessException, InvocationTargetException, TorqueException {
		VideoBo videoBo = new VideoBo();
		int videoLength = video.getVideoLength();
		copyProperties(videoBo, video);
		videoBo.setVideoLength(formateVideoLength(videoLength));
		//videoBo.setItemName(TopWordService.getChannelMap().get(videoBo.getItemId()));
		return videoBo;
	}
	
	private int generateVideoLength(String videoLength) {
		String[] times = videoLength.split(":");
		int videoLengthSecond = Integer.valueOf(times[0]) * 60 + Integer.valueOf(times[1]) * 60 + Integer.valueOf(times[2]);
		return videoLengthSecond;
	}
	
	private String formateVideoLength(int videoLength) {
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
	
	/** the object of the Video **/
	private VideoBo video;
	
	/**
	 * <p>create the Video object</p>
	 */
	public VideoBo getVideo() {
		return video;
	}
	
	/**
	 * <p>set the Video object</p>
	 * @param video Video ojbect
	 */
	public void setVideo(VideoBo video) {
		this.video = video;
	}
	/**
	 * Video id, used for update the video object
	 */
	private int videoId;
	
	/**
	 * get video id
	 * @return videoId
	 */
	public int getVideoId() {
		return videoId;
	}
	
	/**
	 * set video id
	 * @param videoId
	 */
	public void setVideoId(int videoId) {
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
	public List getVideoList() {
		return videoList;
	}
	
	/**
	 * set the video list
	 */
	public void setVideoList(List videoList) {
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
	
	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}
	
	private List<Category> channelList;

	public List<Category> getChannelList() {
		//return TopWordService.getAllChannel();
		return null;
	}

	public void setChannelList(List<Category> channelList) {
		this.channelList = channelList;
	}

}
