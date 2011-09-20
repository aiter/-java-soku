package com.youku.top.paihangbang;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.top.mapping.TopWords;
import com.youku.top.UrlUtils;
import com.youku.top.merge.LogDataDay;
import com.youku.top.merge.LogDataMerger;
import com.youku.top.paihangbang.entity.StopWords;
import com.youku.top.paihangbang.entity.TopWordsEntity;
import com.youku.top.paihangbang.entity.TypeWord;
import com.youku.top.util.TopDateType;
import com.youku.top.util.TopWordType.WordType;

public class TopWordsDataBuilder {

	private static Logger logger = Logger.getLogger(TopWordsDataBuilder.class);

	private TopWordsDataBuilder() {
		super();
	}

	private static long start = 0L;
	private static TopWordsDataBuilder instance = null;

	public static synchronized TopWordsDataBuilder getInstance() {
		logger.info("排行榜生成--start--");
		start = System.currentTimeMillis();
		if (null != instance)
			return instance;
		else {
			instance = new TopWordsDataBuilder();
			logger.info("取得词性表数据--start--");
			typewordsMap = TypeWordsMgt.getInstance().typewordGetter();
			logger.info("取得词性表数据--end--,size:" + typewordsMap.size() + ",cost:"
					+ (System.currentTimeMillis() - start));
			long start1 = System.currentTimeMillis();
			// logger.info("从中间层取得人物数据--start--");
			// PersonGetter.getInstance().getPersonTypeData(typewordsMap);
			// logger.info("从中间层取得人物数据--end--,包含词性表数据size:" +
			// typewordsMap.size()
			// + ",cost:" + (System.currentTimeMillis() - start1));
			// start1 = System.currentTimeMillis();
			logger.info("取得榜单屏蔽表数据--start--");
			stopwordsMap = StopWordsGetter.getInstance()
					.doGetChennelStopWords();
			logger.info("取得榜单屏蔽表数据--end--" + ",cost:"
					+ (System.currentTimeMillis() - start1));
			start1 = System.currentTimeMillis();
			logger.info("取得大词表数据--start--");
			majorItems = MajorTermGetter.getMajorTermKeywords();
			logger.info("取得大词表数据--end--,size:" + majorItems.size() + ",cost:"
					+ (System.currentTimeMillis() - start1));
			return instance;
		}
	}

	public static Map<String, List<TypeWord>> typewordsMap = null;

	public static Map<Integer, StopWords> stopwordsMap = null;

	public static Set<String> majorItems = null;

	public void topwordBuild(String date) throws Exception {
		TopWordsEntity tw = null;
		logger.info("取得用户搜索词数据--start--");
		List<LogDataDay> logs = LogDataMerger.getInstance()
				.getMergeQueryKeyword("merge_query_" + date);
		logger.info("取得用户搜索词数据--end--,size:" + logs.size());

		List<TypeWord> typewords = null;
		StopWords stopwords = null;

		logger.info("创建新的排行榜词表,并删除5天前的表");
		int isnew = RankinfoMgt.getInstance().rankinfoTableCreate(date);
		int version_no = 0;
		if (isnew == 0) {
			version_no = RankinfoMgt.getInstance().rankinfoTableVersionGetter(
					date);
			version_no += 1;
		}
		RankinfoMgt.getInstance().rankinfoTableDrop();
		Date topdate = DataFormat.parseUtilDate(date,
				DataFormat.FMT_DATE_YYYY_MM_DD);

		int zhidaqu = 0;

		int topsize = 0;

		for (LogDataDay ld : logs) {

			stopwords = stopwordsMap.get(WordType.不限.getValue());
			if (null != stopwords) {
				if (stopwords.getDeleted_words().contains(ld.getKeyword())) {
					logger.info("永久屏蔽词,cate:" + WordType.不限.getValue()
							+ ",keyword:" + ld.getKeyword());
					continue;
				}
			}

			boolean isMajor = false;
			if (null != majorItems) {
				if (majorItems.contains(ld.getKeyword())) {
					logger.info("忽略大词,设置外网不可见," + ld.getKeyword());
					isMajor = true;
				}
			}
			typewords = typewordsMap.get(ld.getKeyword());

			if (null != typewords && typewords.size() > 0) {
				for (TypeWord typeword : typewords) {
					tw = new TopWordsEntity();
					tw.setTopwords(new TopWords());
					stopwords = stopwordsMap.get(typeword.getCate());
					if (null != stopwords) {
						if (stopwords.getDeleted_words().contains(
								ld.getKeyword())) {
							logger.info("永久屏蔽词,cate:" + typeword.getCate()
									+ ",keyword:" + ld.getKeyword());
							continue;
						}
						if (stopwords.getBlocked_words().contains(
								ld.getKeyword())) {
							tw.getTopwords().setVisible(0);
						}
					}
					tw.getTopwords().setTopDate(topdate);
					tw.getTopwords().setCate(typeword.getCate());
					tw.getTopwords().setKeyword(ld.getKeyword());
					tw.getTopwords().setPic(typeword.getPic());

					// 大词不显示?
					// if(isMajor){
					// tw.setVisible(0);
					// }

					tw.getTopwords().setProgrammeId(typeword.getProgramme_id());
					tw.getTopwords().setQueryCount(ld.getQuery_count());

					if (typeword.getCate() == WordType.电视剧.getValue()
							|| typeword.getCate() == WordType.电影.getValue()
							|| typeword.getCate() == WordType.动漫.getValue()) {

						if (tw.getTopwords().getVisible() == 1) {

							ContentParser.parse(tw);

							// 保存
							zhidaqu += RankinfoMgt.getInstance().rankinfoSave(
									tw, version_no);
						}
					} else if (typeword.getCate() == WordType.综艺.getValue()) {
						if (tw.getTopwords().getVisible() == 1) {
							// 对应综艺系列
							ContentParser.setNoneTypeAndArea(tw);
							// 保存
							RankinfoMgt.getInstance().rankinfoSave(tw,
									version_no);
						}
					} else {

						// 取图片
						doPicGet(tw);
					}

					tw.getTopwords().setProgrammeId(typeword.getProgramme_id());

					// 保存
					topsize += TopWordsMgt.getInstance().topWordSave(
							tw.getTopwords());
				}
			} else {
				tw = new TopWordsEntity();
				tw.setTopwords(new TopWords());
				if (isMajor) {
					tw.getTopwords().setVisible(0);
				}
				tw.getTopwords().setTopDate(topdate);
				tw.getTopwords().setCate(WordType.不限.getValue());
				tw.getTopwords().setKeyword(ld.getKeyword());
				tw.getTopwords().setQueryCount(ld.getQuery_count());

				// 保存
				TopWordsMgt.getInstance().topWordSave(tw.getTopwords());
			}
		}

		if (zhidaqu > 500) {
			TopDateMgt.getInstance().topDateSave(date, version_no, "auto",
					TopDateType.TopDate.zhidaqu.name());
		}

		if (topsize > 2000) {
			TopDateMgt.getInstance().topDateSave(date, 0, "auto",
					TopDateType.TopDate.top.name());
		}

		int fc = FunInterfaceParser.getInstance().buildFun(date);
		if (fc > 400) {
			TopDateMgt.getInstance().topDateSave(date, 0, "auto",
					TopDateType.TopDate.fun.name());
		}

		logger.info("删除top_words表20天以前的数据");
		TopWordsMgt.getInstance().topWordsDataDelete();

		logger
				.info("排行榜生成--end--,cost:"
						+ (System.currentTimeMillis() - start));

	}

	public void topwordBuild() throws Exception {
		String uniondate = DataFormat.formatDate(DataFormat.getNextDate(
				new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		topwordBuild(uniondate);
	}

	private void doPicGet(TopWordsEntity tw) {
		String pic = null;
		if (tw.getTopwords().getCate() == WordType.人物.getValue()) {
			pic = getPersonPic(tw.getTopwords().getKeyword());
		} else {

			// 取图片
			pic = TypeWordsMgt.getInstance().picGetter(
					tw.getTopwords().getKeyword());

			// 不进行图片搜索
			// if(StringUtils.isBlank(pic)){
			// pic = picGetByUrl(tw.getKeyword(),tw);
			// }
		}
		if (!StringUtils.isBlank(pic))
			tw.getTopwords().setPic(pic);
	}

	private String getPersonPic(String name) {
		String url = "";
		int block = 0;
		JSONObject json = null;
		while (null == json && block < 2) {
			block++;
			url = UrlUtils.buildPersonUrl(name);
			json = Utils.requestGet(url);
			if (null == json)
				UrlUtils.sleep();
		}
		if (null != json) {
			org.json.JSONArray jarr = json.optJSONArray("results");
			if (null != jarr) {
				JSONObject jobject = jarr.optJSONObject(0);
				if (null != jobject) {
					String pic = jobject.optString("thumburl");
					if (!StringUtils.isBlank(pic)) {
						String l = StringUtils.substringAfterLast(pic,
								".ykimg.com/");
						if (!StringUtils.isBlank(l)) {
							return "1" + l.substring(1);
						}
					}
				}
			}
		}
		return null;
	}
}
