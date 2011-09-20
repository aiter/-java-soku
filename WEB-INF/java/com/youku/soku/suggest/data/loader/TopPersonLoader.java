package com.youku.soku.suggest.data.loader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.suggest.entity.PersonWork;
import com.youku.soku.util.DataBase;

public class TopPersonLoader {

	private Logger log = Logger.getLogger(this.getClass());

	private static final String PERONS_CHANNEL = "person";

	private static final int DEFAULT_SIZE = 5000;

	private static final String TAB_NAME_MOVIE = "movie";

	private static final String TAB_NAME_TELEPLAY = "teleplay_version";

	private static final String TAB_NAME_ANIME = "anime_version";

	private static final String TAB_NAME_VARIETY = "variety";

	private static final int PERSON_WORKS_SIZE = 3;

	public List<String> getTopPerson() {

		log.info("获取线上人物排行榜数据........");
		String top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		List<String> result = getDirectoryPersonInfo(top_date, 0, DEFAULT_SIZE);

		if (result != null && result.isEmpty()) {
			int i = 1;
			while (result.isEmpty() && i < 5) {
				log.info("线上人物排行榜数据不存在，尝试去前" + i + "天的数据");
				top_date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -i), DataFormat.FMT_DATE_YYYY_MM_DD);
				result = getDirectoryPersonInfo(top_date, 0, DEFAULT_SIZE);
				i++;
			}
		}

		if (result != null && !result.isEmpty()) {
			log.info("获取线上人物排行榜数据完成");
		} else {
			log.info("获取线上人物排行榜数据失败");
		}

		return result;
	}

	public List<String> getDirectoryPersonInfo(String top_date, int offset, int limit) {

		

		String sql = "SELECT * FROM top_words WHERE cate = 6 and top_date = ? ORDER BY query_count DESC limit ?, ?";

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> resultList = new ArrayList<String>();
		try {
			conn = DataBase.getNewSokuTopConn();
			pst = conn.prepareStatement(sql);

			pst.setString(1, top_date);
			pst.setInt(2, offset);
			pst.setInt(3, limit);

			rs = pst.executeQuery();
			while (rs.next()) {
				resultList.add(rs.getString("keyword"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}

		return resultList;
	}

	private String getPersonWorkName(String names, String versionName) {
		String personWorkName = "";
		if (names != null) {
			personWorkName = names.trim();
		}
		if (versionName != null) {
			personWorkName += versionName.trim();
		}
		return personWorkName;
	}

	private static void selectionSort(List<PersonWork> list) {
		for (int i = 0; i < list.size(); i++) {
			int lowerIndex = i;
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(j).getReleaseTime().compareTo(list.get(lowerIndex).getReleaseTime()) > 0) {
					lowerIndex = j;
				}
			}
			if (lowerIndex != i) {
				PersonWork temp = list.get(lowerIndex);
				list.set(lowerIndex, list.get(i));
				list.set(i, temp);
			}
		}
	}

	public static void main(String[] args) {
		List<PersonWork> l = new ArrayList<PersonWork>();
		PersonWork p1 = new PersonWork();
		p1.setWorkName("1");
		p1.setReleaseTime(DataFormat.parseUtilDate("2010-11-1", DataFormat.FMT_DATE_YYYYMMDD));

		l.add(p1);

		PersonWork p2 = new PersonWork();
		p2.setWorkName("2");
		p2.setReleaseTime(DataFormat.parseUtilDate("2020-11-1", DataFormat.FMT_DATE_YYYYMMDD));

		l.add(p2);

		selectionSort(l);

	}

}
