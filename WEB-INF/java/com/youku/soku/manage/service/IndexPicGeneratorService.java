package com.youku.soku.manage.service;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.Request;
import com.youku.soku.config.Config;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.SokuIndexpagePic;
import com.youku.soku.library.load.SokuIndexpagePicPeer;
import com.youku.soku.top.mapping.TopWords;
import com.youku.soku.util.DataBase;

public class IndexPicGeneratorService {

	private Logger logger = Logger.getLogger(this.getClass());

	private static String HOST = Config.getMiddleTierOfflineHost();

	private static String REQUERIED_FIELDS = "show_thumburl hasvideotype firstepisode_videourl";

	private static final int DEFAULT_WORDS_SIZE = 200;

	private static final int DEFAULT_PIC_SIZE = 200;

	private static final String NO_IMG_URL = "http://res.mfs.ykimg.com/051000004DACF16A9792732B23061177.jpg";

	private String[] getOnlineDateAndVersionNo() {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			conn = DataBase.getNewSokuTopConn();
			String sql = "SELECT * FROM top_date where zhidaqu = 'zhidaqu'";

			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				String[] result = { rs.getString("online_date"), rs.getInt("version_no") + "" };
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}

	private List<TopWords> getTopWordsData(int cate, String topDate, int versionNo) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			conn = DataBase.getNewSokuTopConn();
			String tableName = "rankinfo_" + topDate;
			String sql = "SELECT * FROM " + tableName + " where fk_cate_id = ? and version_no = ? order by query_count desc limit ? ";

			pst = conn.prepareStatement(sql);
			pst.setInt(1, cate);
			pst.setInt(2, versionNo);
			pst.setInt(3, DEFAULT_WORDS_SIZE);
			logger.info("=======" + pst.toString());

			rs = pst.executeQuery();
			List<TopWords> result = new ArrayList<TopWords>();
			while (rs.next()) {
				TopWords tw = new TopWords();
				tw.setKeyword(rs.getString("keyword"));
				tw.setProgrammeId(rs.getInt("fk_programme_id"));
				result.add(tw);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return null;
	}

	private String getIndexPicUrl(int contentId) {
		try {
			logger.info("contentId :  ....: " + contentId);
			StringBuilder requestUrl = new StringBuilder();
			requestUrl.append("http://").append(HOST).append("/show.show?q=showid:").append(contentId).append("&fd=")
					.append(URLEncoder.encode(REQUERIED_FIELDS, "utf-8")).append("&ft=json");

			String result = Request.requestGet(requestUrl.toString(), 1000);

			int requestTime = 0;
			while (result == null && requestTime < 5) {
				result = Request.requestGet(requestUrl.toString(), 1000);
				requestTime++;
			}

			if (requestTime == 5 && result == null) {
				logger.error("Request 5 timt, still no result!!!!");
			}

			JSONObject json = new JSONObject(result);
			JSONArray jsArr = json.optJSONArray("results");

			if (jsArr.length() > 0) {
				JSONObject video = jsArr.getJSONObject(0);
				String firstepisodeVideourl = video.optString("firstepisode_videourl");
				if (!StringUtils.isBlank(firstepisodeVideourl)) {
					String picUrl = video.optString("show_thumburl");
					if (!NO_IMG_URL.equals(picUrl)) {
						return video.optString("show_thumburl");
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	private SokuIndexpagePic wrapIndexpagePicObjcet(String programmeName, int contentId, String topDate) {
		String picUrl = getIndexPicUrl(contentId);
		if (picUrl != null) {
			SokuIndexpagePic sp = new SokuIndexpagePic();
			sp.setPicUrl(picUrl);
			sp.setName(programmeName);
			sp.setGenerateDate(topDate);
			return sp;
		} else {
			return null;
		}
	}

	public void generatorPic() {
		String[] topDateInfo = getOnlineDateAndVersionNo();

		if (topDateInfo != null) {
			try {
				String topDate = topDateInfo[0];
				Criteria crit = new Criteria();
				crit.add(SokuIndexpagePicPeer.GENERATE_DATE, topDate);
				List<SokuIndexpagePic> picList = SokuIndexpagePicPeer.doSelect(crit);
				if (picList != null && picList.size() > 1) {
					logger.info("今天的图片数据已经存在，不用再次生成");
					return;
				}
				List<TopWords> teleplayWords = getTopWordsData(1, topDate, Integer.valueOf(topDateInfo[1]));
				List<TopWords> movieWords = getTopWordsData(2, topDate, Integer.valueOf(topDateInfo[1]));
				int teleCounter = 0;
				int movCounter = 0;
				Set<Integer> programmeIdSet = new HashSet<Integer>();

				logger.info("teleplayWords size: " + teleplayWords.size());

				for (int i = 0; i < Math.max(DEFAULT_WORDS_SIZE, Math.min(teleplayWords.size(), movieWords.size())); i++) {
					try {
						TopWords teleplay = teleplayWords.get(i);
						Programme pt = ProgrammePeer.retrieveByPK(teleplay.getProgrammeId());
						SokuIndexpagePic telPic = wrapIndexpagePicObjcet(pt.getName(), pt.getContentId(), topDate);
						if (telPic != null && teleCounter < DEFAULT_PIC_SIZE && !programmeIdSet.contains(pt.getId())) {
							telPic.save();
							teleCounter++;
						}
						programmeIdSet.add(pt.getId());

						TopWords movie = movieWords.get(i);
						Programme pm = ProgrammePeer.retrieveByPK(movie.getProgrammeId());
						SokuIndexpagePic movPic = wrapIndexpagePicObjcet(pm.getName(), pm.getContentId(), topDate);
						if (movPic != null && movCounter < DEFAULT_PIC_SIZE && !programmeIdSet.contains(pm.getId())) {
							movPic.save();
							movCounter++;
						}
						programmeIdSet.add(pm.getId());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			} catch (TorqueException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}
}
