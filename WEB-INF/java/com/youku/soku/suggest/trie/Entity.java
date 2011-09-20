package com.youku.soku.suggest.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.youku.soku.suggest.util.WordUtil;

public class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3514890514759253439L;

	private int id;

	private String word;

	private int queryCount;

	//private boolean personWorksFlag; // 人物相关作品

	private List<Integer> countAddedIds; //记录本节点已添加的节点ID，防止重复添加

	//private boolean isDirect;
	
	/**
	 * @see EntityFlagUtil
	 */
	private byte flag;   


	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte base) {
		this.flag = (byte) (this.flag | base);
	}

	public void addQueryCount(int id) {
		if (countAddedIds == null) {
			countAddedIds = new ArrayList<Integer>();
		}
		countAddedIds.add(id);
	}

	public boolean isIdAdded(int id) {
		if (countAddedIds != null) {
			return countAddedIds.contains(id);
		} else {
			return false;
		}
	}
	
	public List<Integer> getAddedIds() {
		return this.countAddedIds;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}


	public String toString() {
		return "[w: " + word + ", q: " + queryCount + ",  id: "
				+ id + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Entity) {
			return ((Entity) o).getWord().equals(word);
		} else {
			return false;
		}
	}
}
