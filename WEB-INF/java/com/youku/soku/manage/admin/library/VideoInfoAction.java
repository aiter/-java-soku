package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.library.load.SeriesSubject;
import com.youku.soku.library.load.SeriesSubjectPeer;
import com.youku.soku.manage.bo.VideoInfoBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.service.UserOperationService;
import com.youku.soku.manage.service.VideoInfoService;
import com.youku.soku.manage.service.paihangbang.TopWordsService;
import com.youku.soku.manage.util.LanguageAndArea;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.top.util.TopWordType;

/**
 * Insert or update a videoInfo object to the persistent store
 * 
 * @author tanxiuguang
 * 
 */
public class VideoInfoAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	
	/**
	 * <p>Show Programme Detail</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========Category action input==============");
		try {			
			Programme p = ProgrammePeer.retrieveByPK(getVideoId());
			VideoInfoBo videoBo = new VideoInfoBo();
			videoBo.setId(p.getId());
			videoBo.setContentId(p.getContentId());
			videoBo.setName(p.getName());
			videoBo.setCategory(p.getCate());
			videoBo.setEpisodeTotal(p.getEpisodeTotal());
			videoBo.setSource(p.getSource());
			videoBo.setBlock(p.getBlocked());
			videoBo.setAlias(p.getAlias());
			
			VideoInfoService.videoInfoGetter(videoBo);
			VideoInfoService.videoSeriesCompact(videoBo);
			
			setVideoInfoBo(videoBo);
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
	 * <p>
	 * List the VideoInfos
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {
			log.info("VideoInfoAction list method execute. ");

			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer
					.valueOf(getText("videoInfo.list.pageSize")));
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
			
			int status = -1;
			if (getStatusFilter() > 0 ) {
				status = Integer.valueOf(getStatusFilter());
			}
			
			
			boolean onlySerials = getListSerials() != null && "1".equals(getListSerials());
			
			SearchParameter param = new SearchParameter();
			param.setSearchWord(searchWord);
			param.setAccuratelyMatched(getAccuratelyMatched() == 1 ? true : false);
			param.setStatus(status);
			param.setLatestUpdate(getLatestUpdate() == 1 ? true : false);
			param.setCategoryId(categoryId);
			param.setConcernLevel(getConcernLevel());
			
			
			VideoInfoService.searchVideoInfo(param, pageInfo);
			

			List<VideoInfoBo> videoInfoList = pageInfo.getResults();
			// videoInfoList =
			// VideoInfoPeer.populateObjects(videoInfoList);

			
			pageInfo.setResults(getDisplayList(videoInfoList, onlySerials));
			
			setPageInfo(pageInfo);

			
			return Constants.LIST;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Constants.LIST;
	}
	
	public String block() throws Exception {
		try {
			Programme p = ProgrammePeer.retrieveByPK(getVideoId());
			p.setBlocked(Constants.BLOCKED);
			ProgrammePeer.doUpdate(p);
			log.info("Operator: " + getUserName());
			log.info("Block Programme: " + formatLogDate(new Date()));
			log.info(p);
			UserOperationService.logBlockProgramme("blcok", getUserName(), p);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String unblock() throws Exception {
		try {
			Programme p = ProgrammePeer.retrieveByPK(getVideoId());
			p.setBlocked(Constants.UNBLOCKED);
			ProgrammePeer.doUpdate(p);
			log.info("Operator: " + getUserName());
			log.info("UnBlock Programme: " + formatLogDate(new Date()));
			log.info(p);
			UserOperationService.logBlockProgramme("unblcok", getUserName(), p);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String concern() throws Exception {
		try {
			Programme p = ProgrammePeer.retrieveByPK(getVideoId());
			p.setConcernLevel(Constants.CONCERN_FLAG);
			ProgrammePeer.doUpdate(p);
			log.info("Operator: " + getUserName());
			log.info("Concern Programme: " + formatLogDate(new Date()));
			log.info(p);
			UserOperationService.logBlockProgramme("concern", getUserName(), p);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String unconcern() throws Exception {
		try {
			Programme p = ProgrammePeer.retrieveByPK(getVideoId());
			p.setConcernLevel(Constants.NOT_CONCERN_FLAG);
			ProgrammePeer.doUpdate(p);
			log.info("Operator: " + getUserName());
			log.info("UnConcern Programme: " + formatLogDate(new Date()));
			log.info(p);
			UserOperationService.logBlockProgramme("unconcern", getUserName(), p);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	/** 
	 * Ajax update programme order
	 * 
	 * @return a json array
	 * @throws Exception on any error
	 */
	public String update() throws Exception{
		String status = "success";
		try {
			Criteria crit = new Criteria();
			crit.add(SeriesSubjectPeer.PROGRAMME_ID, getVideoId());
			List<SeriesSubject> ssList = SeriesSubjectPeer.doSelect(crit);
			log.info(crit);
			if(ssList != null && ssList.size() > 0) {
				SeriesSubject ss = ssList.get(0);
				log.info("Operator: " + getUserName());
				log.info("Update Series Order at: " + formatLogDate(new Date()));
				String updateDetail = "before update: " + ss;		
				ss.setOrderId(getOrderId());
				updateDetail += "after update: " + ss;
				SeriesSubjectPeer.doUpdate(ss);
				log.info(updateDetail);
				UserOperationService.logChangeSeries("change series order", getUserName(), ss, updateDetail);
			}
		} catch (Exception e) {
			status = "fail";
			log.error(e.getMessage(), e);
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"status\":\"" + status + "\"}");
		return null;
	}
	
	private List<VideoInfoBo> getDisplayList(List<VideoInfoBo> videoInfoList, boolean onlySerials) {
		Map<String, List<VideoInfoBo>> resultsMap = new LinkedHashMap<String, List<VideoInfoBo>>();
		if (videoInfoList == null) {
			videoInfoList = new ArrayList<VideoInfoBo>();
		}
		for (VideoInfoBo vi : videoInfoList) {
		
//			List<VideoInfoBo> telList = null;
//			if(!StringUtils.isBlank(vi.getSeriesName())) {
//				telList = resultsMap.get(vi.getSeriesName() + vi.getCategory());
//			} else {
//				vi.setSeriesName(vi.getName());
//			}
			List<VideoInfoBo> telList = resultsMap.get(vi.getSeriesName() + vi.getCategory());
			if (telList == null) {
				telList = new ArrayList<VideoInfoBo>();
				VideoInfoBo videoSerial = new VideoInfoBo();
				videoSerial.setSeriesName(vi.getSeriesName());
				videoSerial.setId(vi.getSeriesId());
				videoSerial.setSerialAlias(vi.getSerialAlias());
				videoSerial.setCategory(Constants.VIDEO_SERIAL);
				telList.add(videoSerial);
				resultsMap.put(vi.getSeriesName() + vi.getCategory(), telList);
			}
			telList.add(vi);
		}
		List<VideoInfoBo> resultsList = new ArrayList<VideoInfoBo>();
		log.info(resultsMap.keySet());
		for (List<VideoInfoBo> telList : resultsMap.values()) {
			for (int i = 0; i < telList.size(); i++) {
				VideoInfoBo telBo = telList.get(i);
				log.info("telBo.getSeriesName()" + telBo.getSeriesName());
				if(i == 0 && !StringUtils.isBlank(telBo.getSeriesName())) {
					if(telList.size() > 2) {
						resultsList.add(telBo);
					}
				} else {
					if(telList.size() > 2) {
						//telBo.setVersionName(telBo.getName());
						telBo.setSeriesName("");	
					}
					/*if(telList.size() == 2 ) {
						//telBo.setVersionName(telBo.getName());
						//telBo.setVersionName("<span class=\"highlight\">无版本名</span>");
					//	telBo.setSeriesName(telBo.getName());	
						telBo.setName("");
					}*/
					
					if(onlySerials) {
						if(telList.size() > 2) {
							resultsList.add(telBo);
						}
					} else {
						resultsList.add(telBo);
					}
					
				}
			}
		}
		
		return resultsList;
	}
	
	/**
	 * <p>
	 * List the VideoInfos
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list_serials() throws Exception {

		try {

			 

			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer
					.valueOf(getText("videoInfo.list.pageSize")));
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log.debug("VideoInfoAction getPageNumber() value is: "
					+ getPageNumber());
			log
					.debug("VideoInfoAction pageInfo current pageNumber value is: "
							+ getPageNumber());
			log.debug("VideoInfoAction getSearchWord is: "
					+ getSearchWord());
						
			

			List<VideoInfoBo> videoInfoList = pageInfo.getResults();
			// videoInfoList =
			// VideoInfoPeer.populateObjects(videoInfoList);

			
			pageInfo.setResults(getDisplayList(videoInfoList, true));
			log.debug("videoInfoList size is: "
					+ videoInfoList.size());
			setPageInfo(pageInfo);

			
			return Constants.LIST;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Constants.LIST;
	}

	
	/**
	 * current page number, for the videoInfo list view
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
	

	/**
	 * <p>
	 * Batch delete an Movie from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String changeSerial() throws Exception {

		for (String videoIds : getBatVideoids()) {
			log.debug("changeSerial: " + videoIds);
			String [] ids = videoIds.split(",");
			int category = Integer.valueOf(ids[1]);
			int videoId = Integer.valueOf(ids[0]);
			
			Series n = SeriesPeer.retrieveByPK(getSerialId());
			
			if(n.getCate() != category) {
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				String errorMsg = "系列类型不匹配";
				out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
				
				log.info("系列类型不匹配！");
				return null;
			}
			
			SeriesSubject ss = new SeriesSubject();
			ss.setFkSeriesId(getSerialId());
			ss.setProgrammeId(videoId);
			log.info("Operator: " + getUserName());
			ss.save();
			UserOperationService.logChangeSeries(Constants.USER_OPERATION_CHANGE_SERIES, getUserName(), ss, "");
			log.info("Change Series" + ss);
		}

		return SUCCESS;
	}
	
	/**
	 * <p>
	 * Batch delete an Movie from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String deleteSerial() throws Exception {
		
		boolean noSeries = false;
		
		if(getBatVideoids() == null) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			String errorMsg = "没有对应系列";
			out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
			return null;
		}

		for (String videoIds : getBatVideoids()) {
			log.debug("changeSerial: " + videoIds);
			String [] ids = videoIds.split(",");
			int category = Integer.valueOf(ids[1]);
			int videoId = Integer.valueOf(ids[0]);
			
			Programme p = ProgrammePeer.retrieveByPK(videoId);
			
			Criteria crit = new Criteria();
			crit.add(SeriesSubjectPeer.PROGRAMME_ID, p.getId());
			List<SeriesSubject> ssList = SeriesSubjectPeer.doSelect(crit);
			
			if(ssList.size() == 0) {
				HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
				String errorMsg = p.getName() + "没有对应系列";
				out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
				return null;
			}
			
			if(ssList.size() > 0) {
				SeriesSubject ss = ssList.get(0);
				SeriesSubjectPeer.doDelete(ss.getPrimaryKey());
				
				log.info("Operator: " + getUserName());
				UserOperationService.logChangeSeries(Constants.USER_OPERATION_DELETE_SERIES, getUserName(), ss, "");
				log.info("Delete Series" + ss);
			}
			
		}

		return SUCCESS;
	}
	
	private int num = 0;
	
	public String exportXls(){
		log.debug("********** start export cate:"+categoryFilter+"*********");
		int cate = Integer.parseInt(categoryFilter);
		String cateName = "所有";
		if(cate > 0)
			cateName = TopWordType.wordTypeMap.get(cate);
		String fileName;
		try {
			fileName = cateName + "剧集"+ ".xls";
			HttpServletResponse response=ServletActionContext.getResponse();
			response.reset();
			response.setContentType("application/x-msdownload");  
		    response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(),"iso8859-1"));  
		    ServletOutputStream sos = response.getOutputStream(); 
		    
		    jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(sos);
		    wwb = VideoInfoService.exportXls(accuratelyMatched,num, cate,cateName, wwb);
		    if(null==wwb)
		    	return null;
		    wwb.write();
		    wwb.close();
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String[] batVideoids;

	private int serialId;
	
	private int accuratelyMatched;
	
	private String listSerials;

	private int statusFilter = -1;

	private int latestUpdate;
	
	private String area;

	private int videoId;
	
	private int orderId;
	
	private int concernLevel = -1;
	

	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public int getConcernLevel() {
		return concernLevel;
	}


	public void setConcernLevel(int concernLevel) {
		this.concernLevel = concernLevel;
	}


	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	private Map<Integer, String> statusMap;

	public String getListSerials() {
		return listSerials;
	}

	public void setListSerials(String listSerials) {
		this.listSerials = listSerials;
	}

	public int getSerialId() {
		return serialId;
	}

	public void setSerialId(int serialId) {
		this.serialId = serialId;
	}

	public int getAccuratelyMatched() {
		return accuratelyMatched;
	}

	public void setAccuratelyMatched(int accuratelyMatched) {
		this.accuratelyMatched = accuratelyMatched;
	}

	public String[] getBatVideoids() {
		return batVideoids;
	}

	public void setBatVideoids(String[] batVideoids) {
		this.batVideoids = batVideoids;
	}

	public Map<Integer, String> getStatusMap() {
		return SearchParameter.getStatusMap();
	}

	public Map<Integer, String> getDirectCategory() throws Exception {
		return SearchParameter.getDirectCategory();
	}

	public int getLatestUpdate() {
		return latestUpdate;
	}

	public void setLatestUpdate(int latestUpdate) {
		this.latestUpdate = latestUpdate;
	}

	public int getStatusFilter() {
		return statusFilter;
	}

	public void setStatusFilter(int statusFilter) {
		this.statusFilter = statusFilter;
	}
	
	private VideoInfoBo videoInfoBo;

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}
	
	public VideoInfoBo getVideoInfoBo() {
		return videoInfoBo;
	}

	public void setVideoInfoBo(VideoInfoBo videoInfoBo) {
		this.videoInfoBo = videoInfoBo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	public List<String> getSelectAreaList() {
		return LanguageAndArea.getSelectAreaList();
	}
	
	public Map<Integer, String> getSitesMap() {
		return SiteService.getSitesMap();
	}
}
