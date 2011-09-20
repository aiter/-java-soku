package com.youku.top.recomend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrieTree {
	private TrieNode root = new TrieNode();

	public TrieNode getRoot() {
		return root;
	}

	// private List<Entity> entityList = new ArrayList<Entity>();
	public void insert(Entity entity) {
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

	public void remove(Entity entity) {
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

	public void removeByPy(Entity entity) {
		TrieNode pnode = root;
		for (int i = 0; i < entity.getKeyword().length(); i++) {
			char c = entity.getKeyword().charAt(i);
			pnode = pnode.getChild(c);
			if (pnode == null)
				return;
			pnode.removeDateByPy(entity);
		}
		List<Entity> et = pnode.getData();
		for (Entity e : et) {
			if (e.keyword_py.contains(entity.keyword_py)) {
				pnode = null;
				break;
			}

		}
	}

	public List<Entity> search(String keyword) {
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
	private List<Entity> data;

	public TrieNode getChild(char c) {
		if (child == null) {
			return null;
		}
		return child.get(c);
	}

	public void addData(Entity entity) {
		if (data == null) {
			data = new ArrayList<Entity>();
		}
		if (!data.contains(entity))
			data.add(entity);
		else {
			int i = data.indexOf(entity);
			if (i != -1) {
				Entity e = data.get(i);
				e.type = entity.type;
				e.valueNum = entity.valueNum;
//				data.set(i, e);
			}
		}
		SelectionSort s = new SelectionSort();
		data = s.sort(data, SelectionSort.RETURNSIZE);
	}

	public void removeDate(Entity entity) {
		if (data == null)
			return;
		data.remove(entity);
	}

	public void removeDateByPy(Entity entity) {
		if (data == null)
			return;
		for (int i = 0; i < data.size(); i++) {
			Entity s = data.get(i);
			if (s.getKeyword_py().contains(entity.keyword_py))
				data.remove(entity);
		}
	}

	public char getKey() {
		return key;
	}

	public void setKey(char key) {
		this.key = key;
	}

	public List<Entity> getData() {
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
