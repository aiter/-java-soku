package com.youku.soku.pos_analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.youku.soku.pos_analysis.entity.KeywordCategoryVO;
import com.youku.top.topn.util.KeywordUtil;

public class KeywordAnalysis {
	
	public KeywordCategoryVO parse(String keyword){
		KeywordCategoryVO kcvo = InitAnalysis.getInstance().getKeywordCategoryMaps().get(keyword);
		if(null==kcvo){
			kcvo = new KeywordCategoryVO();
		}
		if(InitAnalysis.getInstance().getPersons().contains(keyword)){
			kcvo.setPerson(true);
			kcvo.getCategory_name_list().clear();
			kcvo.getCategory_name_list().add(InitAnalysis.PERSON);
		}
		return kcvo;
	}
	
	
	/**
	 * 返回的是分类的中文名称
	 * @param keyword
	 * @return
	 */
	public List<String> parseType(String keyword){
		if(StringUtils.isBlank(keyword)) return null;
		String type = typeSimpleParser(keyword);
		List<String> list = new ArrayList<String>();
		if(!StringUtils.isBlank(type)){
			list.clear();
			list.add(type);
		}else{
			KeywordCategoryVO kcvo = parse(keyword);
			if(null!=kcvo&&kcvo.getCategory_name_list().size()>0)
				return kcvo.getCategory_name_list();
		}
		if(list.size()<1){
			String merge_keyword = KeywordUtil.wordFilterTopword(keyword);
			KeywordCategoryVO kcvo = parse(merge_keyword);
			if(null!=kcvo&&kcvo.getCategory_name_list().size()>0)
				return kcvo.getCategory_name_list();
		}
		return null;
	}
	
	public KeywordCategoryVO parseTypeReturnKeywordCategoryVO(String keyword){
		if(StringUtils.isBlank(keyword)) return null;
		String type = typeSimpleParser(keyword);
		KeywordCategoryVO kcvo = parse(keyword);
		if(!StringUtils.isBlank(type)){
			if(null==kcvo){
				kcvo = new KeywordCategoryVO();
			}
			kcvo.getCategory_name_list().clear();
			kcvo.getCategory_name_list().add(type);
		}else{
			if(null!=kcvo&&kcvo.getCategory_name_list().size()>0)
				return kcvo;
		}
		
		if(null==kcvo||kcvo.getCategory_name_list().size()<1){
			String merge_keyword = KeywordUtil.wordFilterTopword(keyword);
			kcvo = parse(merge_keyword);
			if(null!=kcvo&&kcvo.getCategory_name_list().size()>0){
				kcvo.setKeyword(keyword);
				kcvo.setMerge(true);
				kcvo.setMerge_keyword(merge_keyword);
				return kcvo;
			}
		}
		return kcvo;
	}
	
	public String typeSimpleParser(String keyword){
		if(keyword.endsWith("电视剧")||Pattern.compile("第?[ ]*[\\d]+[ ]*集$").matcher(keyword).find())
			return "电视剧";
		if(keyword.endsWith("电影"))
			return "电影";
		if(keyword.endsWith("综艺"))
			return "综艺";
		if(keyword.endsWith("音乐")||keyword.endsWith("mv"))
			return "音乐";
		if(keyword.endsWith("动漫")||keyword.endsWith("动画片"))
			return "动漫";
		if(keyword.endsWith("游戏"))
			return "游戏";
		return null;
	}
}
