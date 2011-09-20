package com.youku.soku.manage.service;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;
import com.youku.search.util.Request;
import com.youku.soku.library.Utils;
import com.youku.soku.manage.common.CommonVideo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.util.ManageUtil;
import com.youku.soku.util.DataBase;

public class CommonVideoService {
	
	private static Logger logger = Logger.getLogger(CommonVideoService.class);
	
	public static CommonVideo getCommonVideoInfo(String url, int siteId) throws SQLException, TorqueException{
		try {
			if (siteId == 14) {
				return updateYoukuContent(url);
			} else {
				return updateOtherSiteContent(url, siteId);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return null;
	}
	
	
	public static CommonVideo updateYoukuContent(String url) throws SQLException, TorqueException{
		if(url == null || url.equals("")) {
			return null;
		}
		CommonVideo cv = new CommonVideo();
		int videoId = MyUtil.decodeVideoUrl(url);
		String sql = "SELECT thumb0, thumb4, seconds, title FROM t_video WHERE pk_video = "
				+ videoId;
		String imgHost = "http://g" + (System.currentTimeMillis() % 4 + 1)
				+ ".ykimg.com/";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = Torque.getConnection("youku");
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				String thumb0 = rs.getString("thumb0");
				String thumb4 = rs.getString("thumb4");
				double second = rs.getDouble("seconds");
				
				String title = rs.getString("title");
				
				cv.setTitle(MyUtil.getString(title));

				if (thumb0 != null && !thumb0.equals("DEFAULT")) {
					cv.setLogo(imgHost + thumb0);
				} else {
					cv.setLogo(imgHost + thumb4);
				}

				cv.setSeconds(second);
			}
			cv.setYouku(true);
			return cv;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[ERROR] in the funciton updateYoukuContent");
			throw e;
		} catch (TorqueException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out
						.println("[ERROR] in close connection in the funcion updateYoukuContent");
			}
		}
	}
	
	
	public static CommonVideo updateOtherSiteContent(String url, int siteId) throws SQLException, TorqueException {
		if(url == null || url.equals("")) {
			return null;
		}
		if(siteId == Constants.INTEGRATED_SITE_ID) {
			String domain = Utils.parseDomain(url);
			if(!StringUtils.isBlank(domain)) {
				
				Connection spiderconn = null;
				try {
					
					siteId = ManageUtil.parseSite(domain);
					if(siteId == 14) {
						return updateYoukuContent(url);						
					}
				} catch (TorqueException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					JdbcUtil.close(spiderconn);
				}
			}
		}
		CommonVideo cv = new CommonVideo();
		Movie m = getVideoInfoFromSpider(url);
		
		if(m == null) {
			//UpdateInfoUtil.saveFirst(url);
			saveUrlToDownloadPic(url);
			cv.setLogo("NA");
		} else {
			if(m.getPicYouku() == null || m.getPicYouku().equals("")) {
				cv.setLogo("NA");
			} else {
				cv.setLogo(m.getPicYouku());
			}
			cv.setTitle(m.getTitle());
			cv.setHd(m.getHd());
			cv.setYouku(false);
			cv.setSeconds(m.getTimeLength());
		}
		
		return cv;

	}
	
	private static void saveUrlToDownloadPic(String url) {
		logger.info("saveUrlToDownloadPic" + url);
		url = URLEncoder.encode(url);
		String requestUrl = "http://10.12.0.23:8080/spider/firstDownloadPicByVideoUrl.jsp?url=" + url;
		String responseStr = Request.requestGet(requestUrl);
	}
	
	public static boolean checkSearchKeys(String tableName, int id, String searchKey) throws SQLException{
		boolean result = true;
		if(!StringUtils.isBlank(searchKey)) {
			if(searchKey.indexOf("|") > -1) {
				String[] keys = searchKey.split("[|]");
				for(String key : keys) {
					result = checkSearchKeyUnique(tableName, id, key);
					if(!result) {
						return false;
					}
				}
			} else {
				result = checkSearchKeyUnique(tableName, id, searchKey);
			}
		}
		
		return result;
	}

	
	private static boolean checkSearchKeyUnique(String tableName, int id, String searchKey) throws SQLException {
		if(!StringUtils.isBlank(searchKey)) {
			String sql = "SELECT searchkeys FROM " + tableName + " WHERE searchkeys LIKE ?";
			if(id > 0) {
				sql += " AND id != " + id;
			}
			PreparedStatement pst = null;
			ResultSet rs = null;
			System.out.println("searchKey" + searchKey);
			Connection conn = null;
			try {
				conn = DataBase.getLibraryConn();
				pst = conn.prepareStatement(sql);
				pst.setString(1, "%" + searchKey + "%");
				rs = pst.executeQuery();
				
				while(rs.next()) {
					String searchKeys = rs.getString(1);
					
					System.out.println("searchKeys" + searchKeys);
					if(searchKeys.indexOf("|") > -1) {
						String[] keys = searchKeys.split("[|]");
						for(String aKey : keys) {
							if(searchKey.equals(aKey)){
								return false;
							}
						}
					} else {
						if(searchKey.equals(searchKeys)) {
							return false;
						}
					}
				}
				
				return true;
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				JdbcUtil.close(rs, pst, conn);
			}
			
		}
		
		return true;
		
	}
	
	private static class Movie {
		private int id;
		private String title;
		private String picSource;
		private String picYouku;
		private String url;
		private int timeLength;
		private int hd;
		
		
		public int getId() {
			return id;
		}


		public void setId(int id) {
			this.id = id;
		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public String getPicSource() {
			return picSource;
		}


		public void setPicSource(String picSource) {
			this.picSource = picSource;
		}


		public String getPicYouku() {
			return picYouku;
		}


		public void setPicYouku(String picYouku) {
			this.picYouku = picYouku;
		}


		public String getUrl() {
			return url;
		}


		public void setUrl(String url) {
			this.url = url;
		}


		public int getTimeLength() {
			return timeLength;
		}


		public void setTimeLength(int timeLength) {
			this.timeLength = timeLength;
		}


		public int getHd() {
			return hd;
		}


		public void setHd(int hd) {
			this.hd = hd;
		}


		@Override
		public String toString() {
			return "Movie [hd=" + hd + ", id=" + id + ", picSource="
					+ picSource + ", picYouku=" + picYouku + ", timeLength="
					+ timeLength + ", title=" + title + ", url=" + url + "]";
		}
		
		
	}
	
	public static Movie getVideoInfoFromSpider(String url) {
		logger.info("getVideoInfoFromSpider" + url);
		String requestUrl = "http://10.12.0.23:8080/spider/getVideoByUrl.jsp?url=" + url;
		String responseStr = Request.requestGet(requestUrl, 500);
		Movie m = new Movie();
		
		try {
			JSONObject json = new JSONObject(responseStr);
			m.id = json.optInt("id");
			m.title = json.optString("title");
			m.picSource = json.optString("picSource");
			m.picYouku = json.optString("picYouku");
			m.url = json.optString("url");
			m.timeLength = json.optInt("timeLength");
			m.hd = json.optInt("hd");
		} catch (JSONException e) {
			logger.info(e.getMessage(), e);
		}
		
		return m;
	}
	
	public static void main(String[] args) {
		System.out.println(getVideoInfoFromSpider("http://www.qiyi.com/dianying/20101220/n139811.html").getPicYouku());
	}
}
