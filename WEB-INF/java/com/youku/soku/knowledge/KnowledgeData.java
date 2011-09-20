package com.youku.soku.knowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.knowledge.KnowledgeDataLoader.KnowledgeDataNode;
import com.youku.soku.library.load.KnowledgeColumn;

public class KnowledgeData {
	
	private Map<String, KnowledgeDataNode> knowledgeMap = new HashMap<String, KnowledgeDataNode>();
	
	private KnowledgeDataNode root = new KnowledgeDataNode();
	
	private Map<String, List<KnowledgeColumn>> knowledgeRankMap = new LinkedHashMap<String, List<KnowledgeColumn>>();
	
	public void addData(String key, KnowledgeDataNode node) {
		knowledgeMap.put(key, node);
	}
	
	public KnowledgeDataNode getNodeData(String key) {
		return knowledgeMap.get(key);
	}
	
	public List<String> getChildColumn(String key) {
		List<String> columnList = new ArrayList<String>();
		KnowledgeDataNode data = knowledgeMap.get(key);
		if(data != null) {
			if(data.getChild() != null) {
				for(KnowledgeDataNode k : data.getChild()) {
					columnList.add(k.getName());
				}
			} else {
				for(KnowledgeDataNode k : data.getParent().getChild()) {
					columnList.add(k.getName());
				}
			}
		}
	
		return columnList;
	}
	
	public KnowledgeDataNode getRoot() {
		return root;
	}

	public void setRoot(KnowledgeDataNode root) {
		this.root = root;
	}

	public void clear() {
		knowledgeMap.clear();
	}

	public Map<String, List<KnowledgeColumn>> getKnowledgeRankMap() {
		return knowledgeRankMap;
	}

	public void setKnowledgeRankMap(Map<String, List<KnowledgeColumn>> knowledgeRankMap) {
		this.knowledgeRankMap = knowledgeRankMap;
	}
	
	
}
