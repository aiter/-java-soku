package com.youku.soku.suggest;

import java.util.List;
import java.util.Map;

import com.youku.soku.suggest.data.TrieTreeHolder;
import com.youku.soku.suggest.trie.DirectEntity;
import com.youku.soku.suggest.trie.TrieTree;

public class DirectEntityCommand {
	
	public static void main(String[] args) {
		try {
			TrieTree tree = TrieTreeHolder.getCurrentThreadLocal();	
			Map<String, List<DirectEntity>> directEntityMap = tree.getDirectEntityMapp();
			if(args.length < 2) {
				System.out.println("Error command!");
				System.out.println("Usage: -ls key, -rm key");
			} else {
				String operatType = args[0];
				String keyword = args[1];
				
				System.out.println("operat type: " + operatType + " keyword: " + keyword);
				if(operatType.equals("-ls")) {
					List<DirectEntity> resultList = directEntityMap.get(keyword);
					for(DirectEntity d : resultList) {
						System.out.println(d);
					}
				} else if(operatType.equals("-rm")) {
					directEntityMap.remove(keyword);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TrieTreeHolder.removeCurrentTrheadLocal();
		}
		
	}
}
