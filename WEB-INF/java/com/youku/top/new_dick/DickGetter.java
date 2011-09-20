package com.youku.top.new_dick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.DickResult;
import com.youku.top.util.LogUtil;
import com.youku.top.util.VideoType;

public class DickGetter {

	static Logger logger = Logger.getLogger(DickGetter.class);

	public static void main(String[] args) {
		try {
			LogUtil.init(Level.INFO, "/opt/log_analyze/new_dick/log/log.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("字典生成开始");
		long t = System.currentTimeMillis();
		if (null != args && args.length == 2 && args[1].matches("\\d{1}")) {
			writeDick(args[0], DataFormat.parseInt(args[1]));
		} else {
			writeDick("/opt/log_analyze/new_dick/");
		}
		logger.info("字典生成结束,cost:" + (System.currentTimeMillis() - t) + " ms");
	}

	/**
	 * 默认生成soku字典
	 * 
	 * @param basePath
	 */
	private static void writeDick(String basePath) {
		Map<String, Collection<DickResult>> map = directSokuTopWordBuild();
		if (null == map)
			return;
		for (Entry<String, Collection<DickResult>> entry : map.entrySet()) {
			driectChannelTopWordBuild(entry.getValue(), entry.getKey(),
					basePath);
		}
	}

	/**
	 * 
	 * @param basePath
	 *            生成字典目录(最后要斜杠) 如:/opt/tmp/
	 */
	public static void writeDick(String basePath, int source) {
		Map<String, Collection<DickResult>> map = null;
		if (1 == source)
			map = directYoukuTopWordBuild();
		else if (2 == source)
			map = directSokuTopWordBuild();
		if (null == map)
			return;
		for (Entry<String, Collection<DickResult>> entry : map.entrySet()) {
			driectChannelTopWordBuild(entry.getValue(), entry.getKey(),
					basePath);
		}
	}

	private static void driectChannelTopWordBuild(Collection<DickResult> drset,
			String channel, String dir) {
		String filename = dir + channel + ".txt";
		List<String> list = new ArrayList<String>();
		for (DickResult dr : drset) {
			// count = KeywordBuilder.getQueryCont(dr.getKeyword(),table);
			// if(count>0){
			list.add(dr.getKeyword() + "\t" + (dr.isHasRight() ? 1 : 0) + "\t"
					+ (StringUtils.isBlank(dr.getYear()) ? "" : dr.getYear())
					+ "\t" + dr.getSubject_id() + "\t"
					+ (dr.getSeries_id() < 1 ? "" : dr.getSeries_id()));
			// }
		}
		logger.info(channel + " : " + list.size());
		try {
			FileUtils.writeLines(new File(filename), list);
		} catch (Exception e) {
			logger.info("字典文件写入失败,filename:" + filename, e);
		}
	}

	/**
	 * 从直达区库生成字典
	 * 
	 * @return
	 */
	private static Map<String, Collection<DickResult>> directSokuTopWordBuild() {
		logger.info("直达区字典生成开始");
		long t = System.currentTimeMillis();
		Map<String, Collection<DickResult>> map = new HashMap<String, Collection<DickResult>>();
		Collection<DickResult> persons = new HashSet<DickResult>();
		map.put(VideoType.teleplay.name(),
				KeywordBuilder.getprogrammeSearchNames(VideoType.teleplay
						.getValue(), persons));
		logger.info("生成电视剧字典,time:" + (System.currentTimeMillis() - t) + " ms");
		long t1 = System.currentTimeMillis();
		map
				.put(VideoType.variety.name(), KeywordBuilder
						.getprogrammeSearchNames(VideoType.variety.getValue(),
								persons));
		logger.info("生成综艺字典,time:" + (System.currentTimeMillis() - t1) + " ms");
		t1 = System.currentTimeMillis();
		map.put(VideoType.anime.name(), KeywordBuilder.getprogrammeSearchNames(
				VideoType.anime.getValue(), persons));
		logger.info("生成动漫字典,time:" + (System.currentTimeMillis() - t1) + " ms");
		t1 = System.currentTimeMillis();
		map.put(VideoType.movie.name(), KeywordBuilder.getprogrammeSearchNames(
				VideoType.movie.getValue(), persons));
		logger.info("生成电影字典,time:" + (System.currentTimeMillis() - t1) + " ms");
		t1 = System.currentTimeMillis();
		map.put(VideoType.person.name(), persons);
		logger.info("生成人物字典,time:" + (System.currentTimeMillis() - t1) + " ms");
		logger.info("直达区字典生成结束,cost:" + (System.currentTimeMillis() - t)
				+ " ms");
		return map;
	}

	/**
	 * 从中间层取得数据生成youku字典
	 * 
	 * @return
	 */
	private static Map<String, Collection<DickResult>> directYoukuTopWordBuild() {
		logger.info("直达区字典生成开始");
		long t = System.currentTimeMillis();
		Set<DickResult> persons = new HashSet<DickResult>();

		Map<String, Collection<DickResult>> dickmap = KeywordBuilder
				.getprogrammeSearchNamesFromMidd(persons);
		dickmap.put(VideoType.person.name(), persons);
		logger.info("直达区字典生成结束,cost:" + (System.currentTimeMillis() - t)
				+ " ms");
		return dickmap;
	}

	/**
	 * 生成字典
	 * 
	 * @param source
	 *            1:youku,2:soku
	 * @return
	 */
	public static Map<String, Collection<DickResult>> directTopWordBuild(
			int source) {
		if (1 == source)
			return directYoukuTopWordBuild();
		else if (2 == source)
			return directSokuTopWordBuild();
		return null;
	}
}
