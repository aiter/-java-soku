package com.youku.soku.sort.word_match.prefix;

import java.util.List;

import com.youku.soku.sort.word_match.Program;
import com.youku.soku.zhidaqu.v2.Element;

public class PrefixMatcher {
	
	private  TrieTree tree = new TrieTree();
	
	public  void addProgram(String word,Element element){
		tree.insert(new Program(word.toLowerCase(),element));
	}
	
	public  List<Program> find(String keyword){
		if (tree != null)
			return tree.search(keyword.toLowerCase());
		
		return null;
		
	}
	
	
}
