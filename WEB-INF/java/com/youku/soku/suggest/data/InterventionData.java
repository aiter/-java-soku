package com.youku.soku.suggest.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;

import com.youku.soku.suggest.data.loader.InterventionDataLoader;
import com.youku.soku.suggest.orm.TrieWords;
import com.youku.soku.suggest.orm.TrieWordsBlock;
import com.youku.soku.suggest.orm.TrieWordsSuggest;

public class InterventionData {
	
	private Map<String, TrieWords> trieWordsMap = new HashMap<String, TrieWords>();
	
	private Map<String, TrieWordsBlock> trieWordsBlock = new HashMap<String, TrieWordsBlock>();

	private Map<Integer, List<TrieWordsSuggest>> trieWordsSuggest = new HashMap<Integer, List<TrieWordsSuggest>>();
	
	public Map<String, TrieWords> getTrieWordsMap() {
		return trieWordsMap;
	}

	public void setTrieWordsMap(Map<String, TrieWords> trieWordsMap) {
		this.trieWordsMap = trieWordsMap;
	}

	public Map<String, TrieWordsBlock> getTrieWordsBlock() {
		return trieWordsBlock;
	}

	public void setTrieWordsBlock(Map<String, TrieWordsBlock> trieWordsBlock) {
		this.trieWordsBlock = trieWordsBlock;
	}
	

	public Map<Integer, List<TrieWordsSuggest>> getTrieWordsSuggest() {
		return trieWordsSuggest;
	}

	public void setTrieWordsSuggest(
			Map<Integer, List<TrieWordsSuggest>> trieWordsSuggest) {
		this.trieWordsSuggest = trieWordsSuggest;
	}

	public void clear() {
		trieWordsBlock.clear();
		trieWordsBlock = null;
		trieWordsMap.clear();
		trieWordsMap = null;
		trieWordsSuggest.clear();
		trieWordsSuggest = null;
	}

}
