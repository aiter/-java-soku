package com.youku.top.quick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.paihangbang.StopWordsGetter;
import com.youku.top.paihangbang.TypeWordsMgt;
import com.youku.top.paihangbang.entity.StopWords;
import com.youku.top.paihangbang.entity.TypeWord;
import com.youku.top.util.LogUtil;

public class QuickTopWordsMgt {
	static Logger logger = Logger.getLogger(QuickTopWordsMgt.class);
	static QueryTopWordsMerger qm = QueryTopWordsMerger.getInstance();

	public static void quickTopWordBuild(String date_between,
			List<String> dates1, List<String> dates2) {
		logger.info("取得指数数据");
		Map<String, Integer> map1 = qm.getQueryMap(dates1);
		Map<String, Integer> map2 = qm.getQueryMap(dates2);
		logger.info("取得各个分类stop words");
		Map<Integer, StopWords> map = StopWordsGetter.getInstance()
				.doGetChennelStopWords();
		logger.info("合并指数数据");
		List<TypeWord> tws = null;
		Map<String, QuickVO> map_date = new HashMap<String, QuickVO>();
		QuickVO qvo = null;
		for (Entry<String, Integer> entry : map1.entrySet()) {
			qvo = new QuickVO();
			qvo.setKeyword(entry.getKey());
			qvo.setQuery_count1(entry.getValue());
			map_date.put(entry.getKey(), qvo);
		}

		for (Entry<String, Integer> entry : map2.entrySet()) {
			qvo = map_date.get(entry.getKey());
			if (null == qvo) {
				qvo = new QuickVO();
				map_date.put(entry.getKey(), qvo);
				qvo.setKeyword(entry.getKey());
			}
			qvo.setQuery_count2(entry.getValue());
		}
		logger.info("指数数据分类");
		List<QuickVO> list = new ArrayList<QuickVO>();
		StopWords swvo = null;
		Map<String, List<TypeWord>> typesmap = TypeWordsMgt.getInstance()
				.typewordGetter();
		for (Entry<String, QuickVO> entry : map_date.entrySet()) {
			tws = typesmap.get(entry.getKey());
			if (null == tws)
				continue;
			for (TypeWord tw : tws) {
				qvo = new QuickVO();
				int cate = tw.getCate();
				if (0 == cate)
					continue;
				swvo = map.get(tw.getCate());
				if (null != swvo) {
					if (swvo.getDeleted_words().contains(entry.getKey()))
						continue;
					if (swvo.getDeleted_words().contains(entry.getKey()))
						qvo.setVisible(0);
				}
				qvo.setKeyword(entry.getKey());
				qvo.setQuery_count1(entry.getValue().getQuery_count1());
				qvo.setQuery_count2(entry.getValue().getQuery_count2());
				qvo.setQuery_count_sub(qvo.getQuery_count1()
						- qvo.getQuery_count2());
				qvo.setCate(tw.getCate());
				list.add(qvo);
			}
		}
		logger.info("保存date_between指数数据");
		qm.saveQuickVO(list, date_between);
	}

	public static String quickDataBuild(String date_between,
			List<String> list1, List<String> list2) {

		if (QuickTopDataMgt.getCounts("select count(*) from quick_"
				+ date_between) > 0)
			return "数据段:" + date_between + "上升最快榜单已经建立，不可重复建立";

		QuickTopDataMgt.execute("create table quick_" + date_between
				+ " like quick_base");

		logger.info("榜单上升最快数据生成开始");

		quickTopWordBuild(date_between, list1, list2);

		logger.info("榜单上升最快数据生成结束");

		return "数据段:" + date_between + "上升最快榜单创建成功";
	}

	public static void main(String[] args) {
		String date_between = "";
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		Date date = null;
		String date1 = "";
		String date2 = "";
		try {
			LogUtil.init(Level.INFO, "/opt/log_analyze/quick/log/log.txt");
			boolean flag = false;
			if (null != args && args.length == 4) {
				flag = true;
				for (String arg : args) {
					if (!arg.matches("\\d{8}")) {
						flag = false;
						break;
					}
				}
			}
			if (flag) {
				date_between = args[2] + "_" + args[3];
				date = DataFormat.parseUtilDate(args[0],
						DataFormat.FMT_DATE_SPECIAL);
				date2 = DataFormat.formatDate(DataFormat.parseUtilDate(args[1],
						DataFormat.FMT_DATE_SPECIAL),
						DataFormat.FMT_DATE_YYYY_MM_DD);
				for (int i = 0;; i++) {
					date1 = DataFormat.formatDate(DataFormat.getNextDate(date,
							i), DataFormat.FMT_DATE_YYYY_MM_DD);
					list2.add(date1);
					if (date1.equalsIgnoreCase(date2))
						break;
				}
				date = DataFormat.parseUtilDate(args[2],
						DataFormat.FMT_DATE_SPECIAL);
				date2 = DataFormat.formatDate(DataFormat.parseUtilDate(args[3],
						DataFormat.FMT_DATE_SPECIAL),
						DataFormat.FMT_DATE_YYYY_MM_DD);
				for (int i = 0;; i++) {
					date1 = DataFormat.formatDate(DataFormat.getNextDate(date,
							i), DataFormat.FMT_DATE_YYYY_MM_DD);
					list1.add(date1);
					if (date1.equalsIgnoreCase(date2))
						break;
				}
				System.out.println(date_between);
				System.out.println(list1);
				System.out.println(list2);
				if (!StringUtils.isBlank(date_between) && list1.size() > 0
						&& list2.size() > 0)
					System.out.println(quickDataBuild(date_between, list1,
							list2));
			} else {
				if (isWeekstartDay()) {
					date_between = "";
					list1 = new ArrayList<String>();
					list2 = new ArrayList<String>();
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.DAY_OF_WEEK, 1);
					cal.add(Calendar.DAY_OF_WEEK, -1);
					Date enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_WEEK, 1);
					Date startdate = cal.getTime();
					String start = DataFormat.formatDate(startdate,
							DataFormat.FMT_DATE_SPECIAL);
					String end = DataFormat.formatDate(enddate,
							DataFormat.FMT_DATE_SPECIAL);
					date_between = new StringBuilder(start).append("_").append(
							end).toString();
					Date temp = null;
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list1.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					cal.add(Calendar.DAY_OF_WEEK, -1);
					enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_WEEK, 1);
					startdate = cal.getTime();
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list2.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					System.out.println(date_between);
					System.out.println(list1);
					System.out.println(list2);
					if (!StringUtils.isBlank(date_between) && list1.size() > 0
							&& list2.size() > 0)
						System.out.println(quickDataBuild(date_between, list1,
								list2));
				}

				if (isMonthstartDay()) {
					date_between = "";
					list1 = new ArrayList<String>();
					list2 = new ArrayList<String>();
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					Date enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					Date startdate = cal.getTime();
					String start = DataFormat.formatDate(startdate,
							DataFormat.FMT_DATE_SPECIAL);
					String end = DataFormat.formatDate(enddate,
							DataFormat.FMT_DATE_SPECIAL);
					date_between = new StringBuilder(start).append("_").append(
							end).toString();
					Date temp = null;
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list1.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					cal.add(Calendar.DAY_OF_MONTH, -1);
					enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					startdate = cal.getTime();
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list2.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					System.out.println(date_between);
					System.out.println(list1);
					System.out.println(list2);
					if (!StringUtils.isBlank(date_between) && list1.size() > 0
							&& list2.size() > 0)
						System.out.println(quickDataBuild(date_between, list1,
								list2));
				}
				if (isMonth21startDay()) {
					date_between = "";
					list1 = new ArrayList<String>();
					list2 = new ArrayList<String>();
					Calendar cal = Calendar.getInstance();
					if (cal.get(Calendar.DAY_OF_MONTH) < 20) {
						cal.set(Calendar.DAY_OF_MONTH, 1);
						cal.add(Calendar.DAY_OF_MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_MONTH, 20);
					Date enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					cal.set(Calendar.DAY_OF_MONTH, 21);
					Date startdate = cal.getTime();
					String start = DataFormat.formatDate(startdate,
							DataFormat.FMT_DATE_SPECIAL);
					String end = DataFormat.formatDate(enddate,
							DataFormat.FMT_DATE_SPECIAL);
					date_between = new StringBuilder(start).append("_").append(
							end).toString();
					Date temp = null;
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list1.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					cal.add(Calendar.DAY_OF_MONTH, -1);
					enddate = cal.getTime();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					cal.set(Calendar.DAY_OF_MONTH, 21);
					startdate = cal.getTime();
					for (int i = 0;; i++) {
						temp = DataFormat.getNextDate(startdate, i);
						list2.add(DataFormat.formatDate(temp,
								DataFormat.FMT_DATE_YYYY_MM_DD));
						if (temp.compareTo(enddate) >= 0)
							break;
					}
					System.out.println(date_between);
					System.out.println(list1);
					System.out.println(list2);
					if (!StringUtils.isBlank(date_between) && list1.size() > 0
							&& list2.size() > 0)
						System.out.println(quickDataBuild(date_between, list1,
								list2));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isWeekstartDay() {
		Calendar cal = Calendar.getInstance();
		int d = cal.get(Calendar.DAY_OF_WEEK);
		if (d == 1)
			return true;
		return false;
	}

	public static boolean isMonthstartDay() {
		Calendar cal = Calendar.getInstance();
		int d = cal.get(Calendar.DAY_OF_MONTH);
		if (d == 1)
			return true;
		return false;
	}

	public static boolean isMonth21startDay() {
		Calendar cal = Calendar.getInstance();
		int d = cal.get(Calendar.DAY_OF_MONTH);
		if (d == 21)
			return true;
		return false;
	}
}
