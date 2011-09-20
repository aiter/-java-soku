package com.youku.soku.manage.deadlink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.manage.util.ManageUtil;
import com.youku.soku.util.DataBase;

public class DumpOtherSiteLink {

	private Logger logger = Logger.getLogger(this.getClass());
	
	public String dump(int offset, int limit) {
		try {
			List<String> urlList = dumpUrlsJdbc(offset, limit);
			JSONArray jsArr = new JSONArray();
			if(urlList != null) {
				for(String url : urlList) {
					if(!StringUtils.isBlank(url)) {
						jsArr.put(ManageUtil.string2Json(url));
					}
				}
				logger.info("dump url size: " + urlList.size());
			}
			
			int totalCount = 0;
			if(offset == 0) {
				totalCount = countUrlsJdbc();
			}
			
			JSONObject obj = new JSONObject();
			obj.put("total", totalCount);
			obj.put("result", jsArr);
			
			return obj.toString();
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	
	private List<String> dumpUrlsJdbc(int offset, int limit) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT pe.url from programme_episode pe, programme_site ps where pe.fk_programme_site_id = ps.id and ps.source_site != 14 and ps.source_site != 100";
			if(offset >=0 && limit > 0) {
				sql += " limit ?, ?";
			}
			pst = conn.prepareStatement(sql);
			if(offset >=0 && limit > 0) {
				pst.setInt(1, offset);
				pst.setInt(2, limit);
			}
			rs = pst.executeQuery();
			List<String> result = new ArrayList<String>();
			while(rs.next()) {
				result.add(rs.getString("pe.url"));
			}
			return result;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return null;
	}
	
	private int countUrlsJdbc() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int totalCount = 0;
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT count(1) from programme_episode pe, programme_site ps where pe.fk_programme_site_id = ps.id and ps.source_site != 14 and ps.source_site != 100";
			
			pst = conn.prepareStatement(sql);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				totalCount = rs.getInt(1);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return totalCount;
	}
}
