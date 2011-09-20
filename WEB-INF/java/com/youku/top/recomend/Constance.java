package com.youku.top.recomend;

import java.util.HashSet;
import java.util.Set;

public class Constance {
	
	public static TrieTree videoTree;
	
	public static TrieTree ch_videoTree;
	
	public static Set<Entity> fufeis = new HashSet<Entity>();
	
	public static void init(){
		videoTree=new TrieTree();
		
		ch_videoTree=new TrieTree();
		
	}
}
