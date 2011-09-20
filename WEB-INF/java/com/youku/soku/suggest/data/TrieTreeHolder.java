package com.youku.soku.suggest.data;

import com.youku.soku.suggest.trie.TrieTree;

public class TrieTreeHolder {
	
	private static TrieTree tree = new TrieTree();
	
	private static ThreadLocal<TrieTree> localThread = new ThreadLocal<TrieTree>();
	
	public static void setCurreantTrieTree(TrieTree tree_) {
		tree.clear();
		tree = tree_;
	}
	
	public static TrieTree getCurrentThreadLocal() {
		
		TrieTree localTree = localThread.get();
		
		if(localTree == null) {
			localTree = tree;
			localThread.set(localTree);
		}
		return localTree;
	}
	
	public static void removeCurrentTrheadLocal() {
		localThread.remove();
	}
	
}
