package com.youku.soku.sort.word_match.prefix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.youku.soku.sort.word_match.Program;


public class TrieTree {
	
	public TrieTree()
	{
		
	}
	
	private TrieNode root = new TrieNode();

	public TrieNode getRoot() {
		return root;
	}

	// private List<Program> entityList = new ArrayList<Program>();
	public void insert(Program entity) {
		// 根结点保存所有entity
		// root.addData(entity);
		TrieNode pnode = root;
		for (int i = 0; i < entity.getKeyword().length(); i++) {
			char c = entity.getKeyword().charAt(i);
			TrieNode cnode = pnode.getChild(c);
			if (cnode == null) {
				cnode = new TrieNode();
				cnode.setKey(c);
				pnode.addChild(cnode);
			}
			cnode.addData(entity);
			pnode = cnode;
		}
	}

	public void remove(Program entity) {
		TrieNode pnode = root;
		for (int i = 0; i < entity.getKeyword().length(); i++) {
			char c = entity.getKeyword().charAt(i);
			pnode = pnode.getChild(c);
			if (pnode == null)
				return;
			pnode.removeDate(entity);
		}
		pnode = null;
	}

	public List<Program> search(String keyword) {
		if (null == keyword || keyword.length() < 1)
			return null;
		TrieNode node = root;
		for (int i = 0; i < keyword.length(); i++) {
			char c = keyword.charAt(i);
			node = node.getChild(c);
			if (node == null) {
				return null;
			}
		}
		return node.getData();
	}
	
}

class TrieNode {
	private char key;

	private HashMap<Character, TrieNode> child;

	private List<Program> data;

	public TrieNode getChild(char c) {
		if (child == null) {
			return null;
		}
		return child.get(c);
	}

	public void addData(Program entity) {
		if (data == null) {
			data = new ArrayList<Program>();
		}
		data.add(entity);
		SelectionSort s = new SelectionSort();
		data = s.sort(data, SelectionSort.RETURNSIZE);
	}

	public void removeDate(Program entity) {
		if (data == null)
			return;
		data.remove(entity);
	}

	public char getKey() {
		return key;
	}

	public void setKey(char key) {
		this.key = key;
	}

	public List<Program> getData() {
		return data;
	}

	public void addChild(TrieNode node) {
		if (child == null) {
			child = new HashMap<Character, TrieNode>();
		}
		child.put(node.getKey(), node);
	}

	public HashMap<Character, TrieNode> getChild() {
		return child;
	}

}
