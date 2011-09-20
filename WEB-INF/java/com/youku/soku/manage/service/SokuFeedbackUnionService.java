package com.youku.soku.manage.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Criteria.Criterion;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.torque.SokuFeedbackUnion;
import com.youku.soku.manage.torque.SokuFeedbackUnionPeer;

public class SokuFeedbackUnionService {
	private static Logger logger = Logger
			.getLogger(SokuFeedbackUnionService.class);

	private static SokuFeedbackUnionService instance = null;

	private SokuFeedbackUnionService() {
		super();
	}

	public synchronized static SokuFeedbackUnionService getInstance() {
		if (null == instance)
			instance = new SokuFeedbackUnionService();
		return instance;
	}

	public static void main(String[] args) {
		String startTime = "2011-06-15 05:12:13";
		String sTime = StringUtils.substring(startTime, 0, 13);
		// String stime_suff = StringUtils.substringAfterLast(startTime, sTime);
		// if (!StringUtils.isBlank(stime_suff)) {
		// stime_suff = stime_suff.trim();
		// stime_suff = StringUtils.substring(stime_suff, 0, 2);
		// }
		System.out.println(sTime);
		System.out.println(DataFormat.formatDate(DataFormat.getNextHour(
				new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS));
	}

	public List<SokuFeedbackUnion> getSokuFeedbackUnionList(int state,
			String startTime, String endTime, int source) throws Exception {
		Criteria crit = new Criteria();
		if (-1 != state) {
			crit.add(SokuFeedbackUnionPeer.STATE, state);
		}
		if (-1 != source) {
			crit.add(SokuFeedbackUnionPeer.SOURCE, source);
		}

		String sTime = StringUtils.substring(startTime, 0, 10);
		String stime_suff = StringUtils.substringAfterLast(startTime, sTime);
		if (!StringUtils.isBlank(stime_suff)) {
			stime_suff = stime_suff.trim();
			stime_suff = StringUtils.substring(stime_suff, 0, 2);
		}
		int sh = DataFormat.parseInt(stime_suff);

		String eTime = StringUtils.substring(endTime, 0, 10);
		String etime_suff = StringUtils.substringAfterLast(endTime, eTime);
		if (!StringUtils.isBlank(etime_suff)) {
			etime_suff = etime_suff.trim();
			etime_suff = StringUtils.substring(etime_suff, 0, 2);
		}
		int eh = DataFormat.parseInt(etime_suff);

		Criterion crit1 = crit.getNewCriterion(
				SokuFeedbackUnionPeer.FEEDBACK_DATE, (Object) sTime,
				Criteria.GREATER_EQUAL);
		Criterion crit2 = crit.getNewCriterion(
				SokuFeedbackUnionPeer.FEEDBACK_DATE, (Object) eTime,
				Criteria.LESS_EQUAL);

		crit1.and(crit2);
		crit.add(crit1);

		if (sh == 0 && eh == 0) {
			Criterion crit3 = crit.getNewCriterion(SokuFeedbackUnionPeer.TYPE,
					(Object) (-1), Criteria.EQUAL);
			Criterion crit4 = crit.getNewCriterion(SokuFeedbackUnionPeer.TYPE,
					(Object) (-1), Criteria.EQUAL);
			crit3.and(crit4);
			crit.add(crit3);
		}

		System.out.println(crit.toString());
		return SokuFeedbackUnionPeer.doSelect(crit);

	}

}
