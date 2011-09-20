package com.youku.soku.manage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.soku.manage.bo.SokuFeedbackVO;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.SokuFeedback;
import com.youku.soku.manage.torque.SokuFeedbackPeer;
import com.youku.soku.manage.torque.SokuFeedbackUnionPeer;

public class SokuFeedbackService {
	private static Logger logger = Logger.getLogger(SokuFeedbackService.class);

	private static SokuFeedbackService instance = null;

	private SokuFeedbackService() {
		super();
	}

	public synchronized static SokuFeedbackService getInstance() {
		if (null == instance)
			instance = new SokuFeedbackService();
		return instance;
	}

	public static int saveFeedback(String keyword, int state, String url,
			String message, int source, String host) {
		try {
			return BasePeer
					.executeStatement(
							"insert into soku_feedback (keyword,url,state,message,create_time,ip_host,source) values('"
									+ keyword
									+ "','"
									+ url
									+ "',"
									+ state
									+ ",'"
									+ message
									+ "','"
									+ DataFormat
											.formatDate(
													new Date(),
													DataFormat.FMT_DATE_YYYYMMDD_HHMMSS)
									+ "','" + host + "'," + source + ")",
							"soku");
		} catch (Exception e) {
			logger.error("save error,keyword:" + keyword + ",url:" + url
					+ ",state:" + state + ",message:" + message + ",source:"
					+ source, e);
		}
		return 0;
	}

	public List<SokuFeedback> getSokuFeedbackList(String words, int state,
			String url, String startTime, String endTime, int source)
			throws Exception {
		Criteria crit = new Criteria();
		if (!StringUtils.isBlank(words)) {
			crit.add(SokuFeedbackPeer.KEYWORD,
					(Object) ("%" + words.trim() + "%"), Criteria.LIKE);
		}
		if (-1 != state) {
			crit.add(SokuFeedbackPeer.STATE, state);
		}
		if (-1 != source) {
			crit.add(SokuFeedbackPeer.SOURCE, source);
		}

		if (!StringUtils.isBlank(url)) {
			crit.add(SokuFeedbackPeer.URL, url.trim());
		}

		if (!StringUtils.isBlank(startTime)) {
			crit.add(SokuFeedbackPeer.CREATE_TIME, (Object) startTime,
					Criteria.GREATER_EQUAL);
		}

		if (!StringUtils.isBlank(endTime)) {
			crit.and(SokuFeedbackPeer.CREATE_TIME, (Object) endTime,
					Criteria.LESS_EQUAL);
		}
		crit.addDescendingOrderByColumn(SokuFeedbackPeer.CREATE_TIME);
		return SokuFeedbackPeer.doSelect(crit);

	}

	public void findSokuFeedbackPagination(PageInfo pageInfo, String words,
			int state, String url, String startTime, String endTime, int source)
			throws Exception {

		Criteria crit = new Criteria();

		String whereSql = " Where 1 = 1 ";
		if (!StringUtils.isBlank(words)) {
			crit.add(SokuFeedbackPeer.KEYWORD,
					(Object) ("%" + words.trim() + "%"), Criteria.LIKE);
			whereSql += "AND " + SokuFeedbackPeer.KEYWORD + " LIKE "
					+ ("'%" + words.trim() + "%'");
		}
		if (-1 != state) {
			crit.add(SokuFeedbackPeer.STATE, state);
			whereSql += " AND " + SokuFeedbackPeer.STATE + " = " + state;
		}
		if (-1 != source) {
			crit.add(SokuFeedbackPeer.SOURCE, source);
			whereSql += " AND " + SokuFeedbackPeer.SOURCE + " = " + source;
		}

		if (!StringUtils.isBlank(url)) {
			crit.add(SokuFeedbackPeer.URL, url.trim());
			whereSql += " AND " + SokuFeedbackPeer.URL + " = '" + url.trim()
					+ "'";
		}

		if (!StringUtils.isBlank(startTime)) {
			crit.add(SokuFeedbackPeer.CREATE_TIME, (Object) startTime,
					Criteria.GREATER_EQUAL);
			whereSql += " AND " + SokuFeedbackPeer.CREATE_TIME + " >= '"
					+ startTime + "'";
		}

		if (!StringUtils.isBlank(endTime)) {
			crit.and(SokuFeedbackPeer.CREATE_TIME, (Object) endTime,
					Criteria.LESS_EQUAL);
			whereSql += " AND " + SokuFeedbackPeer.CREATE_TIME + " <= '"
					+ endTime + "'";
		}

		crit.addDescendingOrderByColumn(SokuFeedbackPeer.CREATE_TIME);

		int totalRecord = ((Record) SokuFeedbackPeer.executeQuery(
				"SELECT count(*) FROM " + SokuFeedbackPeer.TABLE_NAME
						+ whereSql, SokuFeedbackPeer.DATABASE_NAME).get(0))
				.getValue(1).asInt();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());

		pageInfo.setTotalRecords(totalRecord);
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		List<SokuFeedback> result = SokuFeedbackPeer.doSelect(crit);

		pageInfo.setResults(result);
	}

	public List<SokuFeedbackVO> getSokuFeedbackUnionVO(int state,
			String feeddate, int source) {
		List<SokuFeedbackVO> sfvos = new ArrayList<SokuFeedbackVO>();
		String sql = "select keyword,count(*) as c from soku_feedback where create_time like '"
				+ feeddate + "%'";
		if (-1 != state) {
			sql = sql + " and state=" + state;
		}
		if (-1 != source) {
			sql = sql + " and source=" + source;
		}
		sql = sql
				+ " group by keyword,source,state order by count(*) desc limit 3";

		try {
			List<Record> records = SokuFeedbackUnionPeer.executeQuery(sql,
					SokuFeedbackUnionPeer.DATABASE_NAME);
			SokuFeedbackVO vo = null;
			for (Record r : records) {
				vo = new SokuFeedbackVO();
				vo.setKeyword(r.getValue("keyword").asString());
				vo.setCount(r.getValue("c").asInt());
				vo.setSource(source);
				vo.setState(state);
				vo.setFeeduniondate(feeddate);
				sfvos.add(vo);
			}

		} catch (Exception e) {
			logger.error(sql, e);
		}
		return sfvos;
	}

}
