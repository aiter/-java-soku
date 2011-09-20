package com.youku.soku.manage.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.SokuIndexpagePic;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.util.DataBase;

public class SokuIndexPicService {
	
	private static Logger log = Logger.getLogger(SokuIndexPicService.class);
	
	
	public static void findEpisodePaginationJdbc(PageInfo pageInfo, String newestDate, int status) throws Exception {

		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
		
			
			String whereSql = " WHERE blocked = ? and generate_date = ?";
			
			pstcnt = conn.prepareStatement("SELECT COUNT(*) FROM soku_indexpage_pic " + whereSql);
			
			pstcnt.setInt(1, status);
			pstcnt.setString(2, newestDate);
			
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			
			log.info(pstcnt.toString());
			log.info("record count" + recordCount);
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			String sql = "SELECT * FROM soku_indexpage_pic" + whereSql + " order by id LIMIT " + pageInfo.getOffset()
			+ ", " + pageInfo.getPageSize();;

			pst = conn.prepareStatement(sql);
			
			pst.setInt(1, status);
			pst.setString(2, newestDate);
			
			List<SokuIndexpagePic> resultList = new ArrayList<SokuIndexpagePic>();
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				SokuIndexpagePic indexPic = new SokuIndexpagePic();
				indexPic.setId(rs.getInt("id"));
				indexPic.setName(rs.getString("name"));
				indexPic.setBlocked(rs.getInt("blocked"));
				indexPic.setGenerateDate(rs.getString("generate_date"));
				indexPic.setPicUrl(rs.getString("pic_url"));
				resultList.add(indexPic);
			}
			log.info("reuslt size: " + resultList.size());
			pageInfo.setResults(resultList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (pstcnt != null) {
					pstcnt.close();
				}
				if (rscnt != null) {
					rscnt.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}


		// return 0;
	}
	
	public static String getNewestPicDate() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT MAX(generate_date) FROM soku_indexpage_pic";
			
			pst = conn.prepareStatement(sql);
			
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				String generateDate =  rs.getString(1);
				return generateDate;
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		return null;
	}
	
	public static String generateJSONResult() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"data\":[");
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(100);	
			pageInfo.setCurrentPageNumber(1);
			
			String newestDate = SokuIndexPicService.getNewestPicDate();
			findEpisodePaginationJdbc(pageInfo, newestDate, 0);
			List<SokuIndexpagePic> resultList = pageInfo.getResults();
			Collections.shuffle(resultList);
			int counter = 0;
			for(SokuIndexpagePic p : resultList) {
				if(counter > 0) {
					builder.append(',');
				}
				builder.append("{\"name\":\"").append(p.getName()).append("\", \"pic\":\"").append(p.getPicUrl()).append("\"}");
				counter++;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		builder.append("]}");
		return builder.toString();

	}

}
