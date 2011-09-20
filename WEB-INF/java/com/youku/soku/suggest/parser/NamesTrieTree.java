package com.youku.soku.suggest.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.suggest.entity.NamesEntity;


public class NamesTrieTree {
	

	
	private TrieNode root = new TrieNode();
	
	public int count = 0;
	
	public TrieNode getRoot() {
		return root;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getAllKeys() {
		if(root.getChild() == null) {
			return null;
		} else {
			return Arrays.toString(root.getChild().keySet().toArray());
		}
	}
	
	public Collection<Character> getKeysCollection() {
		if(root.getChild() == null) {
			return null;
		} else {
			return root.getChild().keySet();
		}
	}
	
	public void insert(NamesEntity e) {
		TrieNode pnode = root;
		String word = e.getNames() + e.getVersionName();
		for(int i = 0; i< word.length(); i++){
			char c = word.charAt(i);
			TrieNode cnode = pnode.getChild(c);
			if(cnode == null){
				cnode = new TrieNode();
				cnode.setKey(c);
				pnode.addChild(cnode);
			}
			if(i == word.length() - 1) {
				cnode.addData(e);
			}
			pnode = cnode;
		}
		
	}
	

	
	public List<NamesEntity> search(String keyword) {
		if(keyword == null || keyword.length() < 1) {
			return null;
		} else {
			TrieNode node = root;
			for(char c : keyword.toCharArray()) {
				node = node.getChild(c);
				if(node == null) {
					return null;
				}
			}
			
			return node.getData();
		}
	}
	
	public static void main(String[] args){
		
		NamesTrieTree tree = new NamesTrieTree();
		NamesEntity entity = new NamesEntity();
		entity.setNames("三国演义");
		entity.setVersionName("");
		entity.setCate(1);
		tree.insert(entity);
		
		NamesEntity entity1 = new NamesEntity();
		entity1.setNames("三国演义");
		entity1.setVersionName("");
		entity1.setCate(2);
		tree.insert(entity1);
		
		NamesEntity entity2 = new NamesEntity();
		entity2.setNames("射雕英雄传");
		entity2.setVersionName("2003版");
		tree.insert(entity2);
		
		NamesEntity entity3 = new NamesEntity();
		entity3.setNames("射雕英雄传");
		entity3.setVersionName("黄日华版");

		tree.insert(entity3);
		
		System.out.println(tree.getAllKeys());
		System.out.println(tree.search("三国演义"));
	}


	class TrieNode {
		
		private char key;
		
		private Map<Character, TrieNode> child;
		
		private int versionNamePostion;
		
		private List<NamesEntity> entityList;
			
		public TrieNode getChild(char c) {
			if(child == null) {
				return null;
			} else {
				return child.get(c);
			}
		}
		
		
		
		
		public void addData(NamesEntity e) {
			if(entityList == null) {
				entityList = new ArrayList<NamesEntity>();
			}
			
			entityList.add(e);
		}

		public List<NamesEntity> getData() {
			return entityList;
		}

		public int getVersionNamePostion() {
			return versionNamePostion;
		}


		public void setVersionNamePostion(int versionNamePostion) {
			this.versionNamePostion = versionNamePostion;
		}

		public char getKey() {
			return key;
		}
		
		public void setKey(char key) {
			this.key = key;
		}
		
		
		public void addChild(TrieNode node) {
			if(child == null) {
				child = new HashMap<Character, TrieNode>();
			} 
			child.put(node.getKey(), node);
			
		}
		
		public Map<Character, TrieNode> getChild() {
			return child;
		}
	}
}
