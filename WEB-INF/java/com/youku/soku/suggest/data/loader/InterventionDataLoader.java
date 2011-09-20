package com.youku.soku.suggest.data.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.suggest.data.InterventionData;
import com.youku.soku.suggest.orm.TrieWords;
import com.youku.soku.suggest.orm.TrieWordsBlock;
import com.youku.soku.suggest.orm.TrieWordsBlockPeer;
import com.youku.soku.suggest.orm.TrieWordsPeer;
import com.youku.soku.suggest.orm.TrieWordsSuggest;
import com.youku.soku.suggest.orm.TrieWordsSuggestPeer;

/**
 * 人工干预下拉提示，没有启用
 * @author tanxiuguang
 *
 */

public class InterventionDataLoader {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public void loadData(InterventionData interventionData) {
		try {
			loadTrieWords(interventionData.getTrieWordsMap());
			loadTrieWordsBlock(interventionData.getTrieWordsBlock());
			loadTrieWordsSuggest(interventionData.getTrieWordsSuggest());
		} catch (TorqueException e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	public void loadTrieWords(Map<String, TrieWords> trieWordsMap) throws TorqueException {
		List<TrieWords> trieWordsList = TrieWordsPeer.doSelect(new Criteria());
		for(TrieWords words : trieWordsList) {
			trieWordsMap.put(words.getKeyword(), words);
		}
	}
	
	public void loadTrieWordsBlock(Map<String, TrieWordsBlock> trieWordsBlockMap) throws TorqueException {
		List<TrieWordsBlock> trieWordsBlockList = TrieWordsBlockPeer.doSelect(new Criteria());
		for(TrieWordsBlock blockWords : trieWordsBlockList) {
			log.info("blockWords.getKeyword()" + blockWords.getKeyword());
			trieWordsBlockMap.put(blockWords.getKeyword(), blockWords);
		}
		log.info("trieWordsBlockMap" + trieWordsBlockMap);
	}
	
	public void loadTrieWordsSuggest(Map<Integer, List<TrieWordsSuggest>> trieWordsSuggestMap) throws TorqueException {
		List<TrieWordsSuggest> trieWordsSuggest = TrieWordsSuggestPeer.doSelect(new Criteria());
		for(TrieWordsSuggest suggestWords : trieWordsSuggest) {
			List<TrieWordsSuggest> suggestWordsList = trieWordsSuggestMap.get(suggestWords.getFkKeywordId());
			if(suggestWordsList == null) {
				suggestWordsList = new ArrayList<TrieWordsSuggest>();
			}
			suggestWordsList.add(suggestWords);
			trieWordsSuggestMap.put(suggestWords.getFkKeywordId(), suggestWordsList);
		}
	}
	
	
}
