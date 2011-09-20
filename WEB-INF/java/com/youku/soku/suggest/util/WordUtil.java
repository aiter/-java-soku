package com.youku.soku.suggest.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.youku.search.hanyupinyin.Converter;
import com.youku.search.hanyupinyin.PinyinConverter;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.suggest.data.InterventionData;
import com.youku.soku.suggest.entity.EpisodeViewUrl;
import com.youku.soku.suggest.entity.NamesEntity;
import com.youku.soku.suggest.entity.PersonWork;
import com.youku.soku.suggest.entity.VideoCheckResult;
import com.youku.soku.suggest.orm.TrieWords;
import com.youku.soku.suggest.orm.TrieWordsSuggest;
import com.youku.soku.suggest.parser.KeywordsParser;

public class WordUtil {

	private static final Logger log = Logger.getLogger(WordUtil.class);
	private static final int PERSON_WORKS_SIZE = 3;

	private static final HanyuPinyinOutputFormat format;
	static {
		format = new HanyuPinyinOutputFormat();

		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	private static InterventionData interventionData;

	public static int counter = 0;

	public static void init() {
		KeywordsParser.init();
		interventionData = new InterventionData();
		//InterventionDataLoader interventionDataLoader = new InterventionDataLoader();
		//interventionDataLoader.loadData(interventionData);
	}

	public static void dispose() {
		KeywordsParser.dispose();
		interventionData.clear();
	}

	public static VideoCheckResult isVideo(String keyword) {
		String regex = "([a-zA-Z\u4E00-\u9FA5]*)(\\d+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(keyword);
		VideoCheckResult vcr = new VideoCheckResult();
		String name = null;
		int number = 0;
		if (m.matches()) {
			name = m.group(1);
			if (!StringUtils.isBlank(m.group(2))) {
				try {
					number = Integer.valueOf(m.group(2));
				} catch (NumberFormatException e) {
					// log.error(e.getMessage(), e);
				}
			}
		}
		if (name == null) {
			name = keyword;
		}
		List<NamesEntity> neList = KeywordsParser.parse(name);
		
		boolean movieWithNUmberFlag = false;
		if (!keyword.equals(name)) {
			List<NamesEntity> movieNameWithNumber = KeywordsParser.parse(name + number); // 去掉可能的存在的空格
			if (movieNameWithNumber != null) {
				movieWithNUmberFlag = true;
				if (neList == null) {
					neList = movieNameWithNumber;
				} else {
					neList.addAll(movieNameWithNumber);
				}
			}
		}

		String viewUrl = "";
		if (neList != null) {

			// log.info("Load url of: " + neList);
			for (NamesEntity ne : neList) {
				
				/*if("火影忍者4".equals(ne.getNames())) {
					continue;
				}*/

				if (Constants.MOVIE_CATE_ID == ne.getCate() && !(!movieWithNUmberFlag && number > 0)) {
					viewUrl = KeywordsParser.getViewUrl(ne, 1);
					vcr.setVideo(true);
					vcr.setMovie(true);
					vcr.setMovieName(ne.getNames());
					if (!StringUtils.isBlank(viewUrl)) {
						vcr.setViewUrl(viewUrl);
					}
				}
				// 电视剧特殊处理
				if (Constants.TELEPLAY_CATE_ID == ne.getCate()) {
					viewUrl = KeywordsParser.getViewUrl(ne, number);
					vcr.setTeleplay(true);
					vcr.setVideo(true);
					vcr.setTeleplayName(ne.getNames()); //
					if (!StringUtils.isBlank(viewUrl)) {
						vcr.setViewUrl(viewUrl);
					}
				}

				if (Constants.VARIETY_CATE_ID == ne.getCate()) {
					viewUrl = KeywordsParser.getViewUrl(ne, number);
					vcr.setVideo(true);
					vcr.setVariety(true);
					vcr.setVarietyName(ne.getNames());
					vcr.setViewUrl(viewUrl);
					if (!StringUtils.isBlank(viewUrl)) {
						vcr.setViewUrl(viewUrl);
					}
				}

				if (Constants.ANIME_CATE_ID == ne.getCate()) {
					viewUrl = KeywordsParser.getViewUrl(ne, number);
					vcr.setVideo(true);
					vcr.setAnime(true);
					vcr.setAnimeName(ne.getNames());
					if (!StringUtils.isBlank(viewUrl)) {
						vcr.setViewUrl(viewUrl);
					}
				}

			}
		}
		return vcr;
	}

	public static NamesEntity getProgrammeEntity(String word, int cateId) {
		List<NamesEntity> neList = KeywordsParser.parse(word);
		NamesEntity result = null;
		NamesEntity resultHasDetailPage = null;
		if (neList != null) {
			for (NamesEntity ne : neList) {
				if(ne.getCate() == cateId) {
					result = ne;
					if(ne.isHasYoukuDetail()) {
						resultHasDetailPage = ne;
					}
				}
				
			}
		}
		
		if(resultHasDetailPage != null) {
			return resultHasDetailPage;
		} else {
			return result;
		}
	}

	public static List<EpisodeViewUrl> getTeleplayViewUrls(String word, boolean retriveAll) throws Exception {
		List<NamesEntity> neList = KeywordsParser.parse(word);

		if (neList != null) {
			for (NamesEntity ne : neList) {
				if (ne.getCate() == Constants.TELEPLAY_CATE_ID) {
					List<EpisodeViewUrl> result = new ArrayList<EpisodeViewUrl>();
					ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(ne.getSiteVersionId());
					int lastNumber = ps.getEpisodeCollected();

					result.add(new EpisodeViewUrl(1, KeywordsParser.getViewUrl(ne, 1)));
					result.add(new EpisodeViewUrl(lastNumber, KeywordsParser.getViewUrl(ne, lastNumber)));
					if (retriveAll) {
						if (ps.getCompleted() == 1) {
							for (int i = 2; i < lastNumber; i++) {
								result.add(new EpisodeViewUrl(i, KeywordsParser.getViewUrl(ne, i)));
							}
						}
					} else {
						if (lastNumber > 2) {
							Random random = new Random();
							int ranNum = 0;
							while ((ranNum = random.nextInt(lastNumber)) <= 1) {

							}
							result.add(new EpisodeViewUrl(ranNum, KeywordsParser.getViewUrl(ne, ranNum)));
						}
					}
					return result;
				}
			}
		}

		return null;
	}

	public static List<EpisodeViewUrl> getAnimeViewUrls(String word, boolean retriveAll) throws Exception {
		List<NamesEntity> neList = KeywordsParser.parse(word);

		if (neList != null) {
			for (NamesEntity ne : neList) {
				if (ne.getCate() == Constants.ANIME_CATE_ID) {
					List<EpisodeViewUrl> result = new ArrayList<EpisodeViewUrl>();
					//ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(ne.getSiteVersionId());
					//int lastNumber = ps.getEpisodeCollected();
					int lastNumber = ne.getMaxOrderId();

					result.add(new EpisodeViewUrl(lastNumber, KeywordsParser.getViewUrl(ne, lastNumber)));
					if (retriveAll) {

						for (int i = 2; i < lastNumber; i++) {
							result.add(new EpisodeViewUrl(i, KeywordsParser.getViewUrl(ne, i)));
						}
					
					} else {
						if (lastNumber > 2) {
							Random random = new Random();
							int ranNum = 0;
							while ((ranNum = random.nextInt(lastNumber)) <= 1) {

							}
							result.add(new EpisodeViewUrl(ranNum, KeywordsParser.getViewUrl(ne, ranNum)));
						}
					
					}
					return result;
				}
			}
		}

		return null;
	}

	public static void main(String[] args) throws Exception {

	/*	PersonRelatedWork prw = new PersonRelatedWork();
		List<PersonWork> list = prw.getPersonRelatedEpisode("张艺谋");
		for (PersonWork pw : list) {
			System.out.println(pw.getWorkName() + pw.getReleaseTime());
		}
		Random random = new Random();
		int ranNum = -0;
		while ((ranNum = random.nextInt(15)) <= 1) {

		}
		System.out.println(ranNum);*/
		
		System.out.println(Converter.convert("还珠格格"));
		System.out.println(Converter.convert("還珠格格"));
	}

	public static List<PersonWork> getPersonRelatedEpisode(String word) {
		return KeywordsParser.getPersonWorks(word);
	}

	public static boolean isPerson(String word) {
		return KeywordsParser.getPersonWorks(word) != null;
	}

	public static boolean isSpecialVideo(String word) { // 唐伯虎点秋香2
		return KeywordsParser.parse(word) != null;
	}

	public static boolean isAnime(String word) {
		List<NamesEntity> result = KeywordsParser.parse(word);
		if (result == null) {
			return false;
		} else {
			for (NamesEntity ne : result) {
				if (ne.getCate() == Constants.ANIME_CATE_ID) {
					return true;
				}
			}
			return false;
		}
	}

	public static int decodeVideoId(String s) {
		if (!StringUtils.isBlank(s) && s.indexOf("youku.com") > -1) {
			s = s.substring(s.indexOf("id_") + 3, s.lastIndexOf("."));
			s = getFromBASE64(s.substring(1, s.length()));
			return DataFormat.parseInt(s) >> 2;
		} else {
			return 0;
		}

	}

	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public static String encodeVideoId(int id) {
		if (id == 0) {
			return "";
		} else {
			return "http://v.youku.com/v_show/id_X" + getBASE64(String.valueOf(id << 2)) + ".html";
		}

	}
	
	public static String encodeProgrammeId(int id) {
		if (id == 0) {
			return "";
		} else {
			return getBASE64(String.valueOf(id << 2));
		}

	}

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	public static String convertFirstLetter(String input, boolean toLowerCase) {

		if (StringUtils.isBlank(input))
			return null;

		char[] chars = input.toCharArray();

		String result = "";
		StringBuilder builder = new StringBuilder();
		for (char c : chars) {
			if (c > 128) {
				try {
					String[] s = PinyinHelper.toHanyuPinyinStringArray(c, format);
					if (s != null && s.length > 0) {
						String a = s[0];
						builder.append(a.charAt(0));
					}
				} catch (Exception e) {
				}
			} else
				builder.append(c);
		}

		result = builder.toString();

		StringBuilder letters = new StringBuilder();
		char[] cs = result.toCharArray();
		for (char cc : cs) {
			if (toLowerCase)
				letters.append(("" + cc).toLowerCase());
			else
				letters.append("" + cc);
		}
		return letters.toString();
	}

	public static Date parseDate(String releaseTimeStr) {
		Date releaseTime = DataFormat.parseUtilDate(releaseTimeStr, DataFormat.FMT_DATE_YYYYMMDD);
		if (releaseTime == null) {
			releaseTime = DataFormat.parseUtilDate("1900-01-01", DataFormat.FMT_DATE_YYYYMMDD);
		}
		return releaseTime;
	}

	public static String parseYear(Date date) {
		if (date != null) {
			return DataFormat.formatDate(date, DataFormat.FMT_DATE_YYYY);
		}
		return null;
	}

	public static String getPYFirstLetter(String word) {
		TrieWords trieword = interventionData.getTrieWordsMap().get(word);
		if (trieword != null && !StringUtils.isBlank(trieword.getKeywordFwPy())) {
			return trieword.getKeywordFwPy();
		} else {
			return convertFirstLetter(word, true);
		}
	}

/*	public static String getYinyin(String word) {
		TrieWords trieword = interventionData.getTrieWordsMap().get(word);
		if (trieword != null && !StringUtils.isBlank(trieword.getKeywordAllPy())) {
			return trieword.getKeywordAllPy();
		} else {
			return Converter.convert(word);
		}
	} 
*/
	public static Set<String> getPinyin(String word) {
		return PinyinConverter.getPinyin(word);
	}
	

	public static boolean isInterventionBlockWord(String word) {
		return interventionData.getTrieWordsBlock().get(word) != null;
	}

	public static List<TrieWordsSuggest> getInterventionSuggest(String word) {
		TrieWords trieword = interventionData.getTrieWordsMap().get(word);
		if (trieword != null) {
			return interventionData.getTrieWordsSuggest().get(trieword.getId());
		} else {
			return null;
		}
	}

	public static List<String> getInterventionWords() {
		return new ArrayList<String>(interventionData.getTrieWordsMap().keySet());
	}
	
}
