package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.library.load.Category;
import com.youku.soku.library.load.CategoryPeer;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesPeer;
import com.youku.soku.library.load.SeriesSubject;
import com.youku.soku.library.load.SeriesSubjectPeer;
import com.youku.soku.manage.bo.SeriesBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.CategoryService;
import com.youku.soku.manage.service.SeriesService;
import com.youku.soku.manage.service.UserOperationService;

public class SeriesAction extends BaseActionSupport{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * <p>Create category action</p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception on any error
	 */
	public String input() throws Exception {
		
		log.debug("=========Category action input==============");
		try {			
			if(getSeriesId() == -1) {
				setTask(Constants.CREATE);	
				setSeriesId(-1);
				return INPUT; 
			} else {
				Series oldSeries = SeriesPeer.retrieveByPK(getSeriesId());
				setSeries(getSeriesBo(oldSeries));
				setSeriesId(oldSeries.getId());
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
	 * <p>Insert or update an category</p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>List the Seriess</p>
	 *  
	 * @return the "list" result for this mapping
	 * @throws Exception on any error
	 */
	public String list() throws Exception{
		
		try {
			
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(Integer.valueOf(getText("series.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			
			log.debug("SeriesAction getPageNumber() value is: " + getPageNumber());
			log.debug("SeriesAction pageInfo current pageNumber value is: " + getPageNumber());
			log.debug("SeriesAction getSearchWord is: " + getSearchWord());
			int categoryId = -1;
			if(getCategoryFilter() != null) {
				categoryId = Integer.valueOf(getCategoryFilter());
			}
			SeriesService.findSeriesPagination(pageInfo, getSearchWord(), categoryId);
			
			List seriesList = pageInfo.getResults();
			log.debug("how word list is: " + seriesList);
			//seriesList = SeriesPeer.populateObjects(seriesList);
			List newList = new ArrayList();
			if(seriesList == null) {
				seriesList = new ArrayList();
			}
			for(Object obj : seriesList) {	
				log.debug((Series) obj);
				newList.add(getSeriesBo((Series) obj));			
			}
			pageInfo.setResults(newList);
			log.debug("seriesList size is: " + seriesList.size());
			setPageInfo(pageInfo);
			
			return Constants.LIST;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return Constants.LIST;
	}
	
	/**
	 * search series and return the json result;
	 * @return the proper result
	 * @throw Exception on any error
	 */
	public String listJson() throws Exception {
		log.debug("SeriesAction list json");
		
		List<Series> seriesList = SeriesService.searchSeries(getSearchWord());
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < seriesList.size(); i++) {
			JSONObject json = new JSONObject(getSeriesBo(seriesList.get(i)));
			jsonArray.put(i, json);
		}
		
		
		log.debug(jsonArray.toString());
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(jsonArray.toString());
		return null;
	}
	
	/**
	 * <p>Delete an series from the persistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String delete() throws Exception{
		log.debug(" ** delete series action ");
		log.debug(" ====try** delete series action ");
		
		return delete(getSeriesId());
		
	}
	
	/**
	 * <p>Batch delete an Series from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			String result = delete(deleteId);
			if(result == null) {
				return null;
			}			
		}		
		return SUCCESS;
	}
	
	private String delete(int id) throws Exception {
		Criteria crit = new Criteria();
		crit.add(SeriesSubjectPeer.FK_SERIES_ID, id);
		List<SeriesSubject> ssList = SeriesSubjectPeer.doSelect(crit);
		
		if(ssList.size() > 0) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			String errorMsg = "存在关联系列，不能删除";
			out.print("<script type='text/javascript'>alert('" + errorMsg +"'); window.history.back();</script>");
			return null;
		} else {		
			Series series = SeriesPeer.retrieveByPK(id);
			log.debug(series);
			log.info("Operator: " + getUserName());
			log.info("Delete Series at " + formatLogDate(new Date()));
			log.info(series);
			SeriesPeer.doDelete(series);
			UserOperationService.logOperateSeries(Constants.USER_OPERATION_DELETE, getUserName(), series, "");
			return SUCCESS;
		}
	}
	
	/**
	 * <p>Insert or update an series object to the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String execute() throws Exception {
		
		try {		
			
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("Series name is: " + getSeries().getName());
			
			if (creating) {
				
				List<Series> seriesList = SeriesService.findSeriesByName(getSeries().getName());
				boolean haveSeries = seriesList.size() > 0;
				log.debug("seriesList size is:" + seriesList.size());
				
				if (haveSeries) {
					addActionError(getText("error.seriesname.unique"));
				}
				
				getSeries().setCreateTime(new Date());
				getSeries().setUpdateTime(new Date());
				Series h = getSeriesVo(getSeries());
				log.debug(h);
				log.info("Operator: " + getUserName());
				log.info("Save Series at " + formatLogDate(new Date()));
				log.info(h);	
				h.setName(h.getName().trim());
				h.setAlias(h.getAlias().trim());
				setSearchWord(h.getName());
				
				h.save();
				UserOperationService.logOperateSeries(Constants.USER_OPERATION_ADD, getUserName(), h, "");
			} else {			
				
				log.debug("Task update, seriesId is " + getSeries().getId());
				Series oldSeries = SeriesPeer.retrieveByPK(getSeriesId());
				
				if(!oldSeries.getName().equals(getSeries().getName())) {
					List<Series> seriesList = SeriesService.findSeriesByName(getSeries().getName());
					boolean haveSeries = seriesList.size() > 0;
					log.debug("seriesList size is:" + seriesList.size());
					
					if (haveSeries) {
						addActionError(getText("error.seriesname.unique"));
					}
				}
				log.info("Operator: " + getUserName());
				String detail = "Update Series at " + formatLogDate(new Date());
				detail += "Before Update: " + oldSeries;
				oldSeries.setName(getSeries().getName().trim());
				oldSeries.setAlias(getSeries().getAlias().trim());
				oldSeries.setCate(getSeries().getCate());
				oldSeries.setUpdateTime(new Date());
				detail += "after Update: " + oldSeries; 
				log.info(detail);
				SeriesPeer.doUpdate(oldSeries);
				UserOperationService.logOperateSeries(Constants.USER_OPERATION_UPDATE, getUserName(), oldSeries, "");
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	private String getCategory(int cateId) throws TorqueException{
		Category cate = CategoryPeer.retrieveByPK(cateId);
		return cate.getName();
	}
	
	private SeriesBo getSeriesBo(Series series) throws IllegalAccessException, InvocationTargetException, TorqueException{
		
		SeriesBo seriesBo = new SeriesBo();
		copyProperties(seriesBo, series);
		seriesBo.setCategory(getCategory(seriesBo.getCate()));		
		return seriesBo;
	}
	
	private Series getSeriesVo(SeriesBo seriesBo) throws IllegalAccessException, InvocationTargetException {
		
		Series series = new Series();
		copyProperties(series, seriesBo);
		
		return series;
	}
	
	/** the object of the Series **/
	private SeriesBo series;
	
	/**
	 * <p>create the Series object</p>
	 */
	public SeriesBo getSeries() {
		return series;
	}
	
	/**
	 * <p>set the Series object</p>
	 * @param series Series ojbect
	 */
	public void setSeries(SeriesBo series) {
		this.series = series;
	}
	/**
	 * Series id, used for update the series object
	 */
	private int seriesId;
	
	/**
	 * get series id
	 * @return seriesId
	 */
	public int getSeriesId() {
		return seriesId;
	}
	
	/**
	 * set series id
	 * @param seriesId
	 */
	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}
	
	/**
	 * current page number, for the series list view
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
	
	/**
	 * set the value of categoryFilter
	 */
	public void setCategoryFilter(String categoryFilter) {
		this.categoryFilter = categoryFilter;
	}
	
		
	public List<Category> getCategoryList() throws TorqueException{
		return CategoryService.findCategory();
	}

	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}
	

}
