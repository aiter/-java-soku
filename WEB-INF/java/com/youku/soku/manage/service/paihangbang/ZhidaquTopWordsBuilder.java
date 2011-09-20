package com.youku.soku.manage.service.paihangbang;

import java.util.List;

import org.apache.log4j.Logger;

import com.youku.soku.top.mapping.TopWords;
import com.youku.top.paihangbang.ContentParser;
import com.youku.top.paihangbang.RankinfoMgt;
import com.youku.top.paihangbang.entity.TopWordsEntity;
import com.youku.top.util.TopWordType.WordType;

public class ZhidaquTopWordsBuilder {

	static Logger logger = Logger.getLogger(ZhidaquTopWordsBuilder.class);
	private static ZhidaquTopWordsBuilder instance = null;

	private ZhidaquTopWordsBuilder() {
		super();
	}

	public static synchronized ZhidaquTopWordsBuilder getInstance() {
		if (null == instance)
			instance = new ZhidaquTopWordsBuilder();
		return instance;
	}

	public int build(List<TopWords> topWords, int version_no) {

		TopWordsEntity topwords = null;
		int count = 0;
		logger.info("topWords.size:" + topWords.size() + " , version_no:"
				+ version_no);
		for (TopWords tw : topWords) {
				topwords = new TopWordsEntity();
				topwords.setTopwords(tw);
				// topwords.setTopwords(new TopWords());
				// topwords.getTopwords().setProgrammeId(tw.getProgrammeId());
				// topwords.getTopwords().setId(tw.getId());
				// topwords.getTopwords().setKeyword(tw.getKeyword());
				// topwords.getTopwords().setCate(tw.getCate());
				// topwords.getTopwords().setQueryCount(tw.getQueryCount());
				// topwords.getTopwords().setTopDate(tw.getTopDate());
				// topwords.getTopwords().setVisible(tw.getVisible());
				ContentParser.parse(topwords);
				// 保存
				count += RankinfoMgt.getInstance().rankinfoSave(topwords,
						version_no);

		}
		return count;
	}

	// public void createOrTruncateTable(String date){
	// RankinfoMgt.rankinfoTableCreate(date);
	// //RankinfoMgt.rankinfoTableTruncate(date);
	// }
}
