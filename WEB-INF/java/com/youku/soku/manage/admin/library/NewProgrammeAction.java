package com.youku.soku.manage.admin.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.bo.NewProgrammeBo;
import com.youku.soku.manage.bo.NewProgrammeEpisode;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.CategoryService;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.util.DataBase;

/**
 * <p>
 * Insert or update a SpiderLog object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class NewProgrammeAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * <p>
	 * List the SpiderLogs
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(20);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			getNewProgrammePagination(pageInfo, getSearchWord());
			setPageInfo(pageInfo);
			return Constants.LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}

	
	public String delete() throws Exception {

		try {
			deleteNewProgramme(getProgrammeId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}



	
	private void getNewProgrammePagination(PageInfo pageInfo, String searchWord) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSpider23Connection();
			
			int recordCount = getNewProgrammeCount();
			
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			
			String mainSql = "SELECT * from program p, new_program np where np.program_id = p.id";
			String likeSql = "";
			if(!StringUtils.isBlank(searchWord)) {
				likeSql = " AND p.name like ?";
			}
				
			String sql = mainSql + likeSql + " ORDER BY p.name, p.cate LIMIT " + pageInfo.getOffset() + ", "
					+ pageInfo.getPageSize();
			pst = conn.prepareStatement(sql);
			if(!StringUtils.isBlank(searchWord)) {
				pst.setString(1, searchWord + '%');
			}
			List<NewProgrammeBo> resultList = new ArrayList<NewProgrammeBo>();
			//log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				NewProgrammeBo p = new NewProgrammeBo();
				
				p.setId(rs.getInt("np.id"));
				p.setSiteId(rs.getInt("p.site"));
				p.setName(rs.getString("p.name"));
				log.info("p.name" + p.getName());
				p.setCate(rs.getInt("p.cate"));
				p.setAlias(rs.getString("p.alias"));
				p.setType(rs.getString("p.type"));
				p.setDirector(rs.getString("p.director"));
				p.setYear(rs.getString("p.year"));
				p.setActor(rs.getString("p.actors"));
				p.setImage(rs.getString("p.image"));
				p.setHd(rs.getInt("p.hd"));
				p.setEpisodeTotal(rs.getInt("p.episode_total"));
				p.setEpisodeCollected(rs.getInt("p.episode_collected"));
				p.setPlayDefault(rs.getString("p.play_default"));
				p.setCreatTime(rs.getTimestamp("p.create_time"));
				p.setUrls(getProgrammeUrls(rs.getInt("p.id")));
				p.setCateName(CategoryService.getCategoryMap().get(rs.getInt("p.cate")));
				p.setSiteName(SiteService.getSitesMap().get(p.getSiteId()));
				resultList.add(p);
			}
			pageInfo.setResults(resultList);
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
	}
	
	private void deleteNewProgramme(int id) {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSpiderConnection();
			
			String sql = "DELETE FROM new_program where id = " + id;
			
			pst = conn.prepareStatement(sql);
			pst.execute(sql);
			
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
	
	}
	
	private List<NewProgrammeEpisode> getProgrammeUrls(int programmeId) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getSpiderConnection();
			
			String sql = "SELECT * from episode WHERE fk_program_id = ?";
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, programmeId);
			List<NewProgrammeEpisode> resultList = new ArrayList<NewProgrammeEpisode>();
			rs = pst.executeQuery();
			while (rs.next()) {
				NewProgrammeEpisode p = new NewProgrammeEpisode();
				p.setOrderId(rs.getInt("order_id"));
				p.setUrl(rs.getString("url"));
				
				log.info(p.getUrl());
				resultList.add(p);
			}
			return resultList;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		return null;
	}

	private int getNewProgrammeCount() throws Exception {
	
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int recordCount = 0;
		try {
			conn = DataBase.getSpiderConnection();
			String sql = "SELECT count(*) FROM program";
			
			pst = conn.prepareStatement(sql);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				recordCount = rs.getInt(1);
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		return recordCount;
	}
	
	

	private int parseDate(Date date, int flag) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.get(flag);
	}




	
	/**
	 * current page number, for the spiderLog list view
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

	
	private int programmeId;

	public int getProgrammeId() {
		return programmeId;
	}


	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}
	
	private String searchWord;

	public String getSearchWord() {
		return searchWord;
	}






	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	
	

}
