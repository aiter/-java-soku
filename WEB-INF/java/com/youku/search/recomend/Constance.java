package com.youku.search.recomend;

public class Constance {
	public static String VIDEO="video";
	public static String FOLDER="folder";
	public static String USER="user";
	public static String PK="pk";
	public static String BAR="bar";
	public static String All="all";
	public static String FRONT_FOLDER="playlist";
	
	public static TrieTree videoTree;
	public static TrieTree folderTree;
	public static TrieTree userTree;
	public static TrieTree pkTree;
	public static TrieTree barTree;
	
	public static TrieTree ch_videoTree;
	public static TrieTree ch_folderTree;
	public static TrieTree ch_userTree;
	public static TrieTree ch_pkTree;
	public static TrieTree ch_barTree;
	
	public static void init(){
		
		if(null!=videoTree) videoTree.clear();
		if(null!=folderTree) folderTree.clear();
		if(null!=userTree) userTree.clear();
		if(null!=pkTree) pkTree.clear();
		if(null!=barTree) barTree.clear();
		
		videoTree=new TrieTree();
		folderTree=new TrieTree();
		userTree=new TrieTree();
		pkTree=new TrieTree();
		barTree=new TrieTree();
		
		if(null!=ch_videoTree) ch_videoTree.clear();
		if(null!=ch_folderTree) ch_folderTree.clear();
		if(null!=ch_userTree) ch_userTree.clear();
		if(null!=ch_pkTree) ch_pkTree.clear();
		if(null!=ch_barTree) ch_barTree.clear();
		
		ch_videoTree=new TrieTree();
		ch_folderTree=new TrieTree();
		ch_userTree=new TrieTree();
		ch_pkTree=new TrieTree();
		ch_barTree=new TrieTree();
		
	}
}
