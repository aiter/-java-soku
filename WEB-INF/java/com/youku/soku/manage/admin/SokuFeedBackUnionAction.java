package com.youku.soku.manage.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.bo.SokuFeedbackVO;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.SokuFeedbackService;
import com.youku.soku.manage.service.SokuFeedbackUnionService;
import com.youku.soku.manage.torque.SokuFeedbackUnion;

public class SokuFeedBackUnionAction extends BaseActionSupport {
	String startTime;
	String endTime;
	int starthour = 23;
	int endhour = 23;

	String name;
	String feeddate;

	List<String> feedbackdates;
	List<Integer> sokubad;
	List<Integer> sokugood;
	List<Integer> youkubad;
	List<Integer> youkugood;
	List<Integer> sokuall;
	List<Integer> youkuall;

	List<Double> sokubadrate;
	List<Double> youkubadrate;

	int sokutotal;
	int youkutotal;
	int sokubadtotal;
	int youkubadtotal;
	int sokugoodtotal;
	int youkugoodtotal;

	String feeddate_str = "";
	String sokuall_str = "";
	String sokubad_str = "";
	String sokugood_str = "";
	String sokubadrate_str = "";
	String youkuall_str = "";
	String youkubad_str = "";
	String youkugood_str = "";
	String youkubadrate_str = "";
	List<SokuFeedbackVO> sfs;

	private static Logger logger = Logger
			.getLogger(SokuFeedBackUnionAction.class);

	public String list() {

		try {
			logger.info("startTime:" + startTime + ",endTime:" + endTime);

			if (!StringUtils.isBlank(endTime)) {
				endTime = endTime.trim();
			}

			if (!StringUtils.isBlank(startTime)) {
				startTime = startTime.trim();
				if (StringUtils.isBlank(endTime)) {
					endTime = DataFormat.formatDate(new Date(),
							DataFormat.FMT_DATE_YYYYMMDD);
				}
			}

			if (StringUtils.isBlank(startTime)) {
				startTime = DataFormat.formatDate(DataFormat.getNextDate(
						new Date(), -8), DataFormat.FMT_DATE_YYYYMMDD);
			}

			if (StringUtils.isBlank(endTime)
					|| endTime.compareTo(DataFormat.formatDate(new Date(),
							DataFormat.FMT_DATE_YYYYMMDD)) >= 0) {
				endTime = DataFormat.formatDate(DataFormat.getNextDate(
						new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);
			}
			String d = null;
			feedbackdates = new ArrayList<String>();
			feedbackdates.add(startTime);
			for (int i = 1;; i++) {

				d = DataFormat.formatDate(DataFormat.getNextDate(
						DataFormat.parseUtilDate(startTime,
								DataFormat.FMT_DATE_YYYYMMDD), i),
						DataFormat.FMT_DATE_YYYYMMDD);

				if (d.compareToIgnoreCase(endTime) > 0) {
					break;
				}

				feedbackdates.add(d);
			}

			feeddate_str = "[";
			int i = 0;
			for (String fd : feedbackdates) {
				if (i != 0)
					feeddate_str += ",";
				else
					i = 1;
				feeddate_str = feeddate_str + "'" + fd + "'";
			}

			feeddate_str += "]";

			List<SokuFeedbackUnion> feeds = SokuFeedbackUnionService
					.getInstance().getSokuFeedbackUnionList(-1, startTime,
							endTime, -1);

			Map<String, Map<Integer, Map<Integer, Integer>>> map = new HashMap<String, Map<Integer, Map<Integer, Integer>>>();

			d = null;
			for (SokuFeedbackUnion sf : feeds) {
				d = DataFormat.formatDate(sf.getFeedbackDate(),
						DataFormat.FMT_DATE_YYYYMMDD);
				if (null == map.get(d))
					map.put(d, new HashMap<Integer, Map<Integer, Integer>>());

				if (null == map.get(d).get(sf.getSource()))
					map.get(d).put(sf.getSource(),
							new HashMap<Integer, Integer>());

				map.get(d).get(sf.getSource())
						.put(sf.getState(), sf.getCount());

			}

			int badcount = 0;
			int googcount = 0;

			sokutotal = 0;
			youkutotal = 0;
			sokubadtotal = 0;
			youkubadtotal = 0;
			sokugoodtotal = 0;
			youkugoodtotal = 0;

			youkugood = new ArrayList<Integer>();
			youkubad = new ArrayList<Integer>();
			youkuall = new ArrayList<Integer>();
			sokugood = new ArrayList<Integer>();
			sokubad = new ArrayList<Integer>();
			sokuall = new ArrayList<Integer>();

			youkubadrate = new ArrayList<Double>();
			sokubadrate = new ArrayList<Double>();

			sfs = new ArrayList<SokuFeedbackVO>();

			for (String fd : feedbackdates) {
				badcount = 0;
				googcount = 0;
				if (null != map.get(fd) && null != map.get(fd).get(1)) {
					badcount = DataFormat.parseInt(map.get(fd).get(1).get(0));
					googcount = DataFormat.parseInt(map.get(fd).get(1).get(1));
				}
				youkugood.add(googcount);
				youkubad.add(badcount);
				youkuall.add(badcount + googcount);
				youkubadrate.add(round(1.0 * badcount / (badcount + googcount),
						2, BigDecimal.ROUND_HALF_UP));

				youkutotal += badcount + googcount;
				youkubadtotal += badcount;
				youkugoodtotal += googcount;

				badcount = 0;
				googcount = 0;

				if (null != map.get(fd) && null != map.get(fd).get(0)) {
					badcount = DataFormat.parseInt(map.get(fd).get(0).get(0));
					googcount = DataFormat.parseInt(map.get(fd).get(0).get(1));
				}
				sokugood.add(googcount);
				sokubad.add(badcount);
				sokuall.add(badcount + googcount);
				sokubadrate.add(round(1.0 * badcount / (badcount + googcount),
						2, BigDecimal.ROUND_HALF_UP));

				sokutotal += badcount + googcount;
				sokubadtotal += badcount;
				sokugoodtotal += googcount;

				sfs.addAll(SokuFeedbackService.getInstance()
						.getSokuFeedbackUnionVO(0, fd, 0));
				sfs.addAll(SokuFeedbackService.getInstance()
						.getSokuFeedbackUnionVO(0, fd, 1));
			}

			sokuall_str = Arrays.toString(sokuall.toArray(new Integer[] {}));
			sokubad_str = Arrays.toString(sokubad.toArray(new Integer[] {}));
			sokugood_str = Arrays.toString(sokugood.toArray(new Integer[] {}));
			sokubadrate_str = Arrays.toString(sokubadrate
					.toArray(new Double[] {}));
			youkuall_str = Arrays.toString(youkuall.toArray(new Integer[] {}));
			youkubad_str = Arrays.toString(youkubad.toArray(new Integer[] {}));
			youkugood_str = Arrays
					.toString(youkugood.toArray(new Integer[] {}));
			youkubadrate_str = Arrays.toString(youkubadrate
					.toArray(new Double[] {}));

		} catch (Exception e) {
			logger.error(e.getStackTrace(), e);
		}

		return Constants.LIST;
	}

	public String hourlist() {

		try {
			logger.info("startTime:" + startTime + ",starthour:" + starthour
					+ ",endTime:" + endTime + ",endhour:" + endhour);

			if (!StringUtils.isBlank(endTime)) {
				endTime = endTime.trim();
				if (endhour > 9)
					endTime = endTime + " " + endhour;
				else
					endTime = endTime + " 0" + endhour;
			}

			if (!StringUtils.isBlank(startTime)) {
				startTime = startTime.trim();
				if (starthour > 9)
					startTime = startTime + " " + starthour;
				else
					startTime = startTime + " 0" + starthour;
				if (StringUtils.isBlank(endTime)) {
					endTime = DataFormat.formatDate(DataFormat.getNextHour(
							new Date(), -1),
							DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
					endTime = StringUtils.substring(endTime, 0, 13);
				}
			}

			if (StringUtils.isBlank(startTime)) {
				startTime = DataFormat.formatDate(DataFormat.getNextDate(
						new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
				startTime = StringUtils.substring(startTime, 0, 13);
			}

			if (StringUtils.isBlank(endTime)
					|| endTime
							.compareTo(StringUtils
									.substring(
											DataFormat
													.formatDate(
															new Date(),
															DataFormat.FMT_DATE_YYYYMMDD_HHMMSS),
											0, 13)) >= 0) {
				endTime = DataFormat.formatDate(DataFormat.getNextHour(
						new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
				endTime = StringUtils.substring(endTime, 0, 13);
			}

			String d = null;
			feedbackdates = new ArrayList<String>();
			feedbackdates.add(startTime);
			for (int i = 1;; i++) {

				Date ffd = DataFormat.getNextHour(DataFormat.parseUtilDate(
						startTime + ":00:00",
						DataFormat.FMT_DATE_YYYYMMDD_HHMMSS), i);
				d = DataFormat
						.formatDate(ffd, DataFormat.FMT_DATE_YYYYMMDDHHMM);
				d = StringUtils.substring(d, 0, 13);
				if (d.compareToIgnoreCase(endTime) > 0) {
					break;
				}

				feedbackdates.add(d);
			}

			feeddate_str = "[";
			int i = 0;
			for (String fd : feedbackdates) {
				if (i != 0)
					feeddate_str += ",";
				else
					i = 1;
				feeddate_str = feeddate_str + "'" + fd + "'";
			}

			feeddate_str += "]";

			List<SokuFeedbackUnion> feeds = SokuFeedbackUnionService
					.getInstance().getSokuFeedbackUnionList(-1, startTime,
							endTime, -1);

			Map<String, Map<Integer, Map<Integer, Integer>>> map = new HashMap<String, Map<Integer, Map<Integer, Integer>>>();

			sfs = new ArrayList<SokuFeedbackVO>();
			d = null;
			for (SokuFeedbackUnion sf : feeds) {

				d = DataFormat.formatDate(sf.getFeedbackDate(),
						DataFormat.FMT_DATE_YYYYMMDD);
				if (sf.getType() > 9)
					d = d + " " + sf.getType();
				else
					d = d + " 0" + sf.getType();

				if (d.compareTo(startTime) < 0)
					continue;
				if (d.compareTo(endTime) > 0)
					continue;

				if (null == map.get(d))
					map.put(d, new HashMap<Integer, Map<Integer, Integer>>());

				if (null == map.get(d).get(sf.getSource()))
					map.get(d).put(sf.getSource(),
							new HashMap<Integer, Integer>());

				map.get(d).get(sf.getSource())
						.put(sf.getState(), sf.getCount());

			}

			int badcount = 0;
			int googcount = 0;

			sokutotal = 0;
			youkutotal = 0;
			sokubadtotal = 0;
			youkubadtotal = 0;
			sokugoodtotal = 0;
			youkugoodtotal = 0;

			youkugood = new ArrayList<Integer>();
			youkubad = new ArrayList<Integer>();
			youkuall = new ArrayList<Integer>();
			sokugood = new ArrayList<Integer>();
			sokubad = new ArrayList<Integer>();
			sokuall = new ArrayList<Integer>();

			youkubadrate = new ArrayList<Double>();
			sokubadrate = new ArrayList<Double>();

			for (String fd : feedbackdates) {
				badcount = 0;
				googcount = 0;
				if (null != map.get(fd) && null != map.get(fd).get(1)) {
					badcount = DataFormat.parseInt(map.get(fd).get(1).get(0));
					googcount = DataFormat.parseInt(map.get(fd).get(1).get(1));
				}
				youkugood.add(googcount);
				youkubad.add(badcount);
				youkuall.add(badcount + googcount);
				youkubadrate.add(round(1.0 * badcount / (badcount + googcount),
						2, BigDecimal.ROUND_HALF_UP));

				youkutotal += badcount + googcount;
				youkubadtotal += badcount;
				youkugoodtotal += googcount;

				badcount = 0;
				googcount = 0;

				if (null != map.get(fd) && null != map.get(fd).get(0)) {
					badcount = DataFormat.parseInt(map.get(fd).get(0).get(0));
					googcount = DataFormat.parseInt(map.get(fd).get(0).get(1));
				}
				sokugood.add(googcount);
				sokubad.add(badcount);
				sokuall.add(badcount + googcount);
				sokubadrate.add(round(1.0 * badcount / (badcount + googcount),
						2, BigDecimal.ROUND_HALF_UP));

				sokutotal += badcount + googcount;
				sokubadtotal += badcount;
				sokugoodtotal += googcount;

				sfs.addAll(SokuFeedbackService.getInstance()
						.getSokuFeedbackUnionVO(0, fd, 0));
				sfs.addAll(SokuFeedbackService.getInstance()
						.getSokuFeedbackUnionVO(0, fd, 1));
			}

			sokuall_str = Arrays.toString(sokuall.toArray(new Integer[] {}));
			sokubad_str = Arrays.toString(sokubad.toArray(new Integer[] {}));
			sokugood_str = Arrays.toString(sokugood.toArray(new Integer[] {}));
			sokubadrate_str = Arrays.toString(sokubadrate
					.toArray(new Double[] {}));
			youkuall_str = Arrays.toString(youkuall.toArray(new Integer[] {}));
			youkubad_str = Arrays.toString(youkubad.toArray(new Integer[] {}));
			youkugood_str = Arrays
					.toString(youkugood.toArray(new Integer[] {}));
			youkubadrate_str = Arrays.toString(youkubadrate
					.toArray(new Double[] {}));

		} catch (Exception e) {
			logger.error(e);
		} finally {
			startTime = StringUtils.substring(startTime, 0, 10);
			endTime = StringUtils.substring(endTime, 0, 10);
		}

		return Constants.LIST;
	}

	public String getFeeddate_str() {
		return feeddate_str;
	}

	public void setFeeddate_str(String feeddate_str) {
		this.feeddate_str = feeddate_str;
	}

	public String getWords() {

		logger.info("name:" + name + ",feeddate:" + feeddate);

		int s = 0;
		int st = 0;
		if (StringUtils.isBlank(name)) {
			writeMsg("");
			return null;
		}
		name = name.trim();
		List<SokuFeedbackVO> sfs = null;
		// if ("站外不喜欢".equalsIgnoreCase(name)) {
		// s = 0;
		// st = 0;
		// sfs = SokuFeedbackService.getInstance().getSokuFeedbackUnionVO(st,
		// feeddate, s);
		// } else if ("站外喜欢".equalsIgnoreCase(name)) {
		// s = 0;
		// st = 1;
		// sfs = SokuFeedbackService.getInstance().getSokuFeedbackUnionVO(st,
		// feeddate, s);
		// } else if ("站内不喜欢".equalsIgnoreCase(name)) {
		// s = 1;
		// st = 0;
		// sfs = SokuFeedbackService.getInstance().getSokuFeedbackUnionVO(st,
		// feeddate, s);
		// } else if ("站内喜欢".equalsIgnoreCase(name)) {
		// s = 1;
		// st = 1;
		// sfs = SokuFeedbackService.getInstance().getSokuFeedbackUnionVO(st,
		// feeddate, s);
		// }
		String msg = "";
		if (null != sfs && sfs.size() > 0) {

			for (SokuFeedbackVO fvo : sfs) {
				msg += "<br/>" + fvo.getKeyword() + " : " + fvo.getCount()
						+ " times";
			}
			writeMsg(msg);
		} else {
			writeMsg(msg);
		}

		logger.info("name:" + name + ",feeddate:" + feeddate + ",msg:" + msg);

		return null;
	}

	public String getSokubad_str() {
		return sokubad_str;
	}

	public void setSokubad_str(String sokubad_str) {
		this.sokubad_str = sokubad_str;
	}

	public String getSokugood_str() {
		return sokugood_str;
	}

	public void setSokugood_str(String sokugood_str) {
		this.sokugood_str = sokugood_str;
	}

	public String getSokubadrate_str() {
		return sokubadrate_str;
	}

	public void setSokubadrate_str(String sokubadrate_str) {
		this.sokubadrate_str = sokubadrate_str;
	}

	public String getYoukuall_str() {
		return youkuall_str;
	}

	public void setYoukuall_str(String youkuall_str) {
		this.youkuall_str = youkuall_str;
	}

	public String getYoukubad_str() {
		return youkubad_str;
	}

	public void setYoukubad_str(String youkubad_str) {
		this.youkubad_str = youkubad_str;
	}

	public String getYoukugood_str() {
		return youkugood_str;
	}

	public void setYoukugood_str(String youkugood_str) {
		this.youkugood_str = youkugood_str;
	}

	public String getYoukubadrate_str() {
		return youkubadrate_str;
	}

	public void setYoukubadrate_str(String youkubadrate_str) {
		this.youkubadrate_str = youkubadrate_str;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFeeddate() {
		return feeddate;
	}

	public void setFeeddate(String feeddate) {
		this.feeddate = feeddate;
	}

	public List<String> getFeedbackdates() {
		return feedbackdates;
	}

	public void setFeedbackdates(List<String> feedbackdates) {
		this.feedbackdates = feedbackdates;
	}

	public List<Integer> getSokubad() {
		return sokubad;
	}

	public void setSokubad(List<Integer> sokubad) {
		this.sokubad = sokubad;
	}

	public List<Integer> getSokugood() {
		return sokugood;
	}

	public void setSokugood(List<Integer> sokugood) {
		this.sokugood = sokugood;
	}

	public List<Integer> getYoukubad() {
		return youkubad;
	}

	public void setYoukubad(List<Integer> youkubad) {
		this.youkubad = youkubad;
	}

	public List<Integer> getYoukugood() {
		return youkugood;
	}

	public void setYoukugood(List<Integer> youkugood) {
		this.youkugood = youkugood;
	}

	public List<Double> getSokubadrate() {
		return sokubadrate;
	}

	public void setSokubadrate(List<Double> sokubadrate) {
		this.sokubadrate = sokubadrate;
	}

	public List<Double> getYoukubadrate() {
		return youkubadrate;
	}

	public void setYoukubadrate(List<Double> youkubadrate) {
		this.youkubadrate = youkubadrate;
	}

	public List<Integer> getSokuall() {
		return sokuall;
	}

	public void setSokuall(List<Integer> sokuall) {
		this.sokuall = sokuall;
	}

	public List<Integer> getYoukuall() {
		return youkuall;
	}

	public void setYoukuall(List<Integer> youkuall) {
		this.youkuall = youkuall;
	}

	public int getSokutotal() {
		return sokutotal;
	}

	public void setSokutotal(int sokutotal) {
		this.sokutotal = sokutotal;
	}

	public int getYoukutotal() {
		return youkutotal;
	}

	public void setYoukutotal(int youkutotal) {
		this.youkutotal = youkutotal;
	}

	public int getSokubadtotal() {
		return sokubadtotal;
	}

	public void setSokubadtotal(int sokubadtotal) {
		this.sokubadtotal = sokubadtotal;
	}

	public int getYoukubadtotal() {
		return youkubadtotal;
	}

	public void setYoukubadtotal(int youkubadtotal) {
		this.youkubadtotal = youkubadtotal;
	}

	public int getSokugoodtotal() {
		return sokugoodtotal;
	}

	public void setSokugoodtotal(int sokugoodtotal) {
		this.sokugoodtotal = sokugoodtotal;
	}

	public int getYoukugoodtotal() {
		return youkugoodtotal;
	}

	public void setYoukugoodtotal(int youkugoodtotal) {
		this.youkugoodtotal = youkugoodtotal;
	}

	public String getSokuall_str() {
		return sokuall_str;
	}

	public void setSokuall_str(String sokuall_str) {
		this.sokuall_str = sokuall_str;
	}

	public void writeMsg(String msg) {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(msg);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public int getStarthour() {
		return starthour;
	}

	public void setStarthour(int starthour) {
		this.starthour = starthour;
	}

	public int getEndhour() {
		return endhour;
	}

	public void setEndhour(int endhour) {
		this.endhour = endhour;
	}

	public List<SokuFeedbackVO> getSfs() {
		return sfs;
	}

	public void setSfs(List<SokuFeedbackVO> sfs) {
		this.sfs = sfs;
	}

	public static double round(double value, int scale, int roundingMode) {
		if (Double.isNaN(value))
			return 0.0;
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}
}
