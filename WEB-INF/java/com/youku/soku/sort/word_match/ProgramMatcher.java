package com.youku.soku.sort.word_match;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import com.youku.aword.dict.Dict;
import com.youku.soku.sort.word_match.like.LikeMatcher;
import com.youku.soku.sort.word_match.like.LikeResult;
import com.youku.soku.sort.word_match.prefix.PrefixMatcher;
import com.youku.soku.zhidaqu.v2.DictManager;
import com.youku.soku.zhidaqu.v2.Element;
import com.youku.soku.zhidaqu.v2.TokenType;
import com.youku.soku.zhidaqu.v2.ZhidaquFinder;

public class ProgramMatcher {
	
	private static ProgramMatcher sokuMatcher = new ProgramMatcher();
	private static ProgramMatcher youkuMatcher = new ProgramMatcher();
	
	static final int DEFAULT_COUNT = 10;
	
	static Log logger = LogFactory.getLog(ProgramMatcher.class);

	public  LikeMatcher likeMatcher;
	public  PrefixMatcher prefixMatcher;
	
	public  MatcherResult match(String keyword){
		return match(keyword,null,DEFAULT_COUNT);
	}
	public  MatcherResult match(String keyword,TokenType type){
		return match(keyword,type,DEFAULT_COUNT);
	}
	/**
	 * 匹配主方法
	 * @param keyword
	 * @return
	 */
	public  MatcherResult match(String keyword,TokenType type,int limit){
	
		if (keyword == null || likeMatcher == null || prefixMatcher == null) return null;
		
		logger.debug("limit="+limit);
		
		long start = System.currentTimeMillis();
		
		MatcherResult result = null;
		
		String prefixMatchWord = null;
		
		boolean find = false;
		
		LikeResult likeResult = likeMatcher.like(keyword);
		logger.debug(likeResult);
		List<Program> prefixResult = null;
		if (likeResult != null && likeResult.isValid()){
			find = true;
		}
		//
		
		if (isPrefixMatch (likeResult)&&  keyword.length() > 1)//前匹配
		{
			if (likeResult!=null && likeResult.getUnknowWord()!=null){
				if (likeResult.getUnknowWord().length() > 1)
					prefixMatchWord = likeResult.getUnknowWord();
			}
			
			if (prefixMatchWord != null){
				logger.debug("前匹配关键字："+prefixMatchWord);
				
				prefixResult = prefixMatcher.find(prefixMatchWord);
				if(prefixResult != null && prefixResult.size() > 0){
					find = true;
				}
			}
		}
		if (find){
			result = new MatcherResult(type);
			if (likeResult!= null ) result.setLikeResult(likeResult);
			if (prefixResult != null ){
				result.setPrefixResult(prefixResult);
				result.setPrefixWord(prefixMatchWord);
			}
			
			result = filter(result, limit);
		}
		
		logger.debug("模糊匹配耗时："+ (System.currentTimeMillis() - start));
		return result;
	}
	/**
	 * 是否进入前匹配规则
	 * @param likeResult
	 * @return
	 */
	private boolean isPrefixMatch(LikeResult likeResult){
//		return !(likeResult != null 
//				 && likeResult.isValid() 
//				 && (likeResult.getEpisode()!=null || likeResult.getModifier()!= null || likeResult.getNumber() > 0 ));
		return true;
	}
	
	/**
	 * 排序去重
	 * @param result
	 * @return
	 */
	private MatcherResult filter(MatcherResult result,int limit){
		
		int count = 0;
		
		//系列去重用Set
		Set<Integer> seriesSet = new HashSet<Integer>();
		Set<Integer> programSet = new HashSet<Integer>();
		if (result.getLikeResult()!= null && result.getLikeResult().isValid() && result.getLikeResult().getProgram()!= null){
			Element[] els = DictManager.getElements(result.getLikeResult().getProgram());
			for (Element el:els){
				if (el.getSeries() > 0)
					seriesSet.add(el.getSeries());
				if (el.getProgramId() > 0)
					programSet.add(el.getProgramId());
				logger.debug("like result add program set:"+result.getLikeResult().getProgram().getValue() + "=" + el.getProgramId());
			}
			count ++;
		}
		if (result.getPrefixResult()!=null){
			//去重
			Iterator<Program> it = result.getPrefixResult().iterator();
			while (it.hasNext()){
				Program program = it.next() ;
				
				//前匹配结果中去掉已经模糊匹配出来的节目
				if (programSet.contains(program.getElement().getProgramId())  ){
					it.remove();
					continue;
				}
				else{
					if(program.getElement().getProgramId() > 0)
						programSet.add(program.getElement().getProgramId());
					logger.debug("prefix result add program set:"+program.getKeyword() + "=" + program.getElement().getProgramId());
				}
				
				//根据系列号去重
				if (program.getElement().getSeries() > 0){
					if (seriesSet.contains(program.getElement().getSeries())){
						it.remove();
						continue;
					}
					else{
						seriesSet.add(program.getElement().getSeries());
					}
				}
				if (count++ >= limit){
					it.remove();
				}
			}
			
		}
				
		
		return result;
	}
	
	
	public LikeMatcher getLikeMatcher() {
		return likeMatcher;
	}
	public void setLikeMatcher(LikeMatcher likeMatcher) {
		this.likeMatcher = likeMatcher;
	}
	public PrefixMatcher getPrefixMatcher() {
		return prefixMatcher;
	}
	public void setPrefixMatcher(PrefixMatcher prefixMatcher) {
		this.prefixMatcher = prefixMatcher;
	}
	
	public static ProgramMatcher getSokuMatcher() {
		return sokuMatcher;
	}
	public static ProgramMatcher getYoukuMatcher() {
		return youkuMatcher;
	}
	public static void main(String[] args) throws Exception{
		BasicConfigurator.configure();

		String d;
		d = "/home/tanxiuguang/work/search/src/WEB-INF/java/com/youku/soku/sort/word_match/";

		PrefixMatcher prefixMatcher = new PrefixMatcher();
		Dict dict = new WordMatchLoader(d).load(prefixMatcher);
		ProgramMatcher.getYoukuMatcher().setLikeMatcher( new LikeMatcher(new ZhidaquFinder(dict)));
		ProgramMatcher.getYoukuMatcher().setPrefixMatcher(prefixMatcher);
		
		MatcherResult result = ProgramMatcher.getYoukuMatcher().match("快乐",null,20);
		
		if (result!=null){
			System.out.println(result);
			System.out.println(result.getParamKeyAsString());
		}
		else
			System.out.println("not found");
	}
}





