package com.youku.soku.sort.word_match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.sort.word_match.like.LikeResult;
import com.youku.soku.sort.word_match.like.Modifier;
import com.youku.soku.zhidaqu.v2.DictManager;
import com.youku.soku.zhidaqu.v2.Element;
import com.youku.soku.zhidaqu.v2.TokenType;


public class MatcherResult{
	
	
	private LikeResult likeResult;  //模糊匹配结果

	private String prefixWord ; //前匹配词
	private List<Program> prefixResult;  //前缀匹配结果
	private Map<Integer,MatchType> matchType = null;
	
	TokenType type = null;
	
	public MatcherResult(){
		
	}
	public MatcherResult(TokenType type){
		this.type = type;
	}
	
	public LikeResult getLikeResult() {
		return likeResult;
	}
	public void setLikeResult(LikeResult likeResult) {
		this.likeResult = likeResult;
	}
	public List<Program> getPrefixResult() {
		return prefixResult;
	}
	public void setPrefixResult(List<Program> prefixResult) {
		this.prefixResult = prefixResult;
	}
	
	public String getPrefixWord() {
		return prefixWord;
	}
	public void setPrefixWord(String prefixWord) {
		this.prefixWord = prefixWord;
	}
	
	/**
	 * 返回匹配的结果，按时间倒序排列
	 * @return
	 */
	public List<Program> getMatchResult(){
		List<Program> programs = new ArrayList<Program>();
		if (likeResult != null && likeResult.isValid() ){
			
			//解析修饰词
			if (likeResult.getModifier() != null){
				Modifier modifier = likeResult.getModifier();
				if (modifier.getType() != null && type == null){
					type = modifier.getType();
				}
			}
			if(likeResult.getProgram() != null){
				Element[] els = DictManager.getElements(likeResult.getProgram());
				for (Element el:els){
					if (type != null && type != el.getType()){
						continue;
					}
					Program pro = new Program(likeResult.getProgram().getValue(),el);
					pro.setMatchType(MatchType.LIKE);
					programs.add(pro);
					
					
				}
			}
			else{
				if(likeResult.getPerson() != null){
					Element[] els = DictManager.getElements(likeResult.getPerson());
					for (Element el:els){
						Program pro = new Program(likeResult.getPerson().getValue(),el);
						pro.setMatchType(MatchType.LIKE);
						programs.add(pro);
					}
				}
			}
			
			
		}
		if (prefixResult != null && prefixResult.size() > 0){
			for (Program program:prefixResult){
				if (type != null && type != program.getElement().getType()){
					continue;
				}
				program.setMatchType(MatchType.PREFIX);
				programs.add(program);
				
			}
		}
		
		Collections.sort(programs);
		return programs;
	}
	
	/**
	 * 组织去直达区机器上查询的key
	 * TokenType 指定类型
	 * @return
	 */
	public String getParamKeyAsString(){
		StringBuilder builder=  new StringBuilder();
		matchType = new HashMap<Integer,MatchType>();
		List<Program> programs = getMatchResult();
		if (programs!= null && programs.size() > 0){
			for (int i = 0 ;i < programs.size(); i++){
				Program program = programs.get(i);
				builder.append(program.getKeyword())
				.append("/")
				.append(program.getElement().getType());
				
				if(program.getElement().getType() == TokenType.PERSON && type != null){
					builder.append("/").append(type);
				}
				builder.append(",");
				
				matchType.put(i, program.getMatchType());
			}
		}
		
		if (builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);
		
		return builder.toString();
	}
	
	
	
	public boolean isLikeMatch(int index){
		return matchType!= null && matchType.get(index)==MatchType.LIKE;
	}
	public String toString(){
		StringBuilder builder=  new StringBuilder();
		if (likeResult != null){
			builder.append("模糊匹配结果：\n");
			builder.append(likeResult);
			builder.append("\n");
		}
		if (prefixResult != null && prefixResult.size() > 0){
			builder.append("前匹配结果：\n");
			for (Program program:prefixResult){
				builder.append(program).append("\n");
			}
		}
		return builder.toString();
	}
	
}
  enum MatchType{
	LIKE,PREFIX,
}
