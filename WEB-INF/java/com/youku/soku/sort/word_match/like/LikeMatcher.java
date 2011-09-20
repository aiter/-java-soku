package com.youku.soku.sort.word_match.like;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.aword.Word;
import com.youku.search.util.DataFormat;
import com.youku.soku.zhidaqu.v2.TokenType;
import com.youku.soku.zhidaqu.v2.ZhidaquFinder;

public class LikeMatcher {
	static Log logger = LogFactory.getLog(LikeMatcher.class);
	
	private ZhidaquFinder finder;

	public LikeMatcher(ZhidaquFinder finder){
		this.finder = finder;
	}
	
	/**
	 * 模糊查询的业务逻辑
	 * @param keyword
	 * @return
	 */
	public  LikeResult like(String keyword) {

		if(finder == null) return null;
		
		// 获取每个词的Addtion info
		logger.debug("keyword: " + keyword);
		List<Word> words = finder.process(keyword);
		
		LikeResult result = new LikeResult();
		StringBuilder unknowWord = new StringBuilder();
		Modifier modifier = null;
		for (Word word:words){
			logger.debug(word);
			logger.debug(word.getNormalValue());
			logger.debug(word.getValue());
			if (word.getType() == Word.Type.未知){
				unknowWord.append(word.getValue());
				result.setValid(false);
			}
			else if (word.getType() == Word.Type.剧集){
				unknowWord.append(word.getValue());
				result.setProgram(word);
			}
			else if (word.getType() == Word.Type.人物){
				if (result.isValid())
					unknowWord.append(word.getValue());
				result.setPerson(word);
			}
			else if (word.getType() == Word.Type.时间){
				result.setDate(word);
			}
			else if (word.getType() == Word.Type.季){
				result.setVersion(word);
			}
			else if (word.getType() == Word.Type.集){
				result.setEpisode(word);
			}
			else if (word.getType() == Word.Type.数字){
				result.setNumber(DataFormat.parseInt(word.getNormalValue(),0));
			}
			else if (word.getType() == Word.Type.描述词){
				modifier = parseModifier(word,modifier);
			}
		}
		
		//只有一个词
		if (words.size() == 1 ){
			if (result.getNumber() > 0 ){
				unknowWord.append(keyword);
			}
		}
		
		if (modifier != null && !modifier.isEmpty()){
			 result.setModifier(modifier);
		 }
		
		if (unknowWord.length() > 0){
			result.setUnknowWord(unknowWord.toString());
		}
		if( result.getProgram() == null && result.getPerson() == null){
			result.setValid(false);
		}
		
		return result;
	}
	
	private  Modifier parseModifier(Word word,Modifier modifier){
		
		if (modifier == null){
			modifier = new Modifier();
		}
		if (word.getValue().startsWith("电影")){
			modifier.setType(TokenType.MOVIE);
		}
		else if (word.getValue().startsWith("电视剧")){
			modifier.setType(TokenType.TELEPLAY);
		}
		else if (word.getValue().startsWith("综艺")){
			modifier.setType(TokenType.VARIETY);
		}
		else if (word.getValue().startsWith("高清")){
			modifier.setHd(1);
		}
		
		return modifier;
	}
}


