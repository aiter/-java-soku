package com.youku.soku.shield;

import org.apache.log4j.Logger;

import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.manage.util.JFConverter;
import com.youku.soku.shield.matcher.AccurateHitMatcher;
import com.youku.soku.shield.matcher.ContainHitMatcher;
import com.youku.soku.shield.matcher.FuzzyHitMatcher;
import com.youku.soku.shield.matcher.HitMatcher;

public class Filter {
	
	private static Logger logger = Logger.getLogger(Filter.class);

	private static Filter filter = new Filter();
	
	public enum Source {youku, soku};

	private JFConverter converter;
	private FilterChain filterChain;

	public static Filter getInstance() {
		return filter;
	}

	private Filter() {
		converter = new JFConverter();
		filterChain = new FilterChain() {
			@Override
			public ShieldInfo filter(ShieldWordsInfo wordsInfo, String keyword, Source source) {
				
				if(wordsInfo == null) {
					return null;
				}
				
				keyword = converter.traditionalized(keyword);
				keyword = keyword.toLowerCase();
				HitMatcher matchers[] = new HitMatcher[] {
						new AccurateHitMatcher(wordsInfo),
						new ContainHitMatcher(wordsInfo),
						new FuzzyHitMatcher(wordsInfo) };
				ShieldInfo result = null;
				for (HitMatcher matcher : matchers) {
					result = matcher.match(keyword);
					if (result != null) {
						if(source == Source.youku) {
							result.setMatched(result.getYoukuEffect() == 1);
						} else if(source == Source.soku) {
							result.setMatched(result.getOthersEffect() == 1);
						}
						
						return result;
					}
				}

				return null;
			}

		};
		
	}
	


	public ShieldInfo isShieldWord(String keyword, Source source) {

		try {
			ShieldWordsInfo wordsInfo = ShieldWordsHolder
					.getCurrentThreadLocal();
			/*keyword = converter.traditionalized(keyword);
			keyword = keyword.toLowerCase();

			HitMatcher matchers[] = new HitMatcher[] {
					new AccurateHitMatcher(wordsInfo),
					new ContainHitMatcher(wordsInfo),
					new FuzzyHitMatcher(wordsInfo) };
			ShieldInfo result = null;
			for (HitMatcher matcher : matchers) {
				result = matcher.match(keyword);
				if (result != null) {
					if(source == Source.youku) {
						result.setMatched(result.getYoukuEffect() == 1);
					} else if(source == Source.soku) {
						result.setMatched(result.getOthersEffect() == 1);
					}
					
					return result;
				}
			}

			return null;*/
			return filterChain.filter(wordsInfo, keyword, source);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			ShieldWordsHolder.removeCurrentThreadLocal();
		}

		return null;
	}

	abstract static class FilterChain {
		public ShieldInfo filter(ShieldWordsInfo wordsInfo, String keyword, Source source) {
			return null;
		}
	}


	//for build trie tree
	public ShieldInfo isShieldWord(String keyword, ShieldWordsInfo wordsInfo) {
		return filterChain.filter(wordsInfo, keyword, Source.youku);
	}
}
