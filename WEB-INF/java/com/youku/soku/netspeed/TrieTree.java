package com.youku.soku.netspeed;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;


public class TrieTree {
	
	public TrieTree()
	{
		
	}
	
	private TrieNode root = new TrieNode();

	public TrieNode getRoot() {
		return root;
	}

	// private List<Program> entityList = new ArrayList<Program>();
	public void insert(Node entity) {
		// 根结点保存所有entity
		TrieNode pnode = root;
		String key = entity.getKey();
		String[] arr =  key.split("_");
		for (String k:arr) {
			TrieNode cnode = pnode.getChild(k);
			if (cnode == null) {
				cnode = new TrieNode();
				cnode.setKey(k);
				pnode.addChild(cnode);
			}
			cnode.addData(entity.getSite(),entity.getCount());
			pnode = cnode;
		}
	}

	public void remove(TrieNode  node) {
		
		HashMap<String, TrieNode> map = node.getChild();
		if (map!=null && map.size() > 0){
			Collection<TrieNode> values = map.values();
			for(TrieNode n:values){
				remove(n);
			}
			map.clear();
			map = null;
		}
	}

	
	public void destroy(){
		remove(root);
	}
	
	public Speed search(String keyword) {
		if (null == keyword || keyword.length() < 1)
			return null;
		
		TrieNode node = root;
		StringTokenizer itr = new StringTokenizer(keyword, "_");
		
		int i = 0;
		while(itr.hasMoreTokens()){
			TrieNode child = node.getChild(itr.nextToken());
			if (child != null) {
				node = child;
			}
			else{
				if (i < 2){
					return null;
				}
//				else if (i == 2){
//					System.out.println(":<font color=red size=6 >" + node.getSpeed().getValue() +"（在全国找到匹配的网速）</font><br/>");
//				}
//				else if (i == 3){
//					System.out.println(":<font color=red size=6>" + node.getSpeed().getValue() +"（在省级找到匹配的网速）</font><br/>");
//				}
//				else if (i == 4){
//					System.out.println(":<font color=red size=6>" + node.getSpeed().getValue() +"（在市级找到匹配的网速）</font><br/>");
//				}
				
				return node.getSpeed();
			}
			i++;
		}
//		System.out.println("在市所在运营商找到匹配的网速:"+node.getSpeed().getValue());
		return node.getSpeed();
	}
	public Speed search(String keyword,StringBuilder out) throws IOException {
		if (null == keyword || keyword.length() < 1)
			return null;
		
			
		TrieNode node = root;
		StringTokenizer itr = new StringTokenizer(keyword, "_");
		int i = 0;
			while(itr.hasMoreTokens()){
				
				TrieNode child = node.getChild(itr.nextToken());
				if (child != null) {
					node = child;
				}
				else{
					if (i < 2){
						return null;
					}else if (i == 2){
							out.append(":<font color=red size=6 >" + node.getSpeed().getValue() +"</font><font color=green size=2>（在全国找到匹配的网速）</font><br/>");
					}
					else if (i == 3){
						out.append(":<font color=red size=6>" + node.getSpeed().getValue() +"</font><font color=green size=2>（在省级找到匹配的网速）</font><br/>");
					}
					else if (i == 4){
						out.append(":<font color=red size=6>" + node.getSpeed().getValue() +"</font><font color=green size=2>（在市级找到匹配的网速）</font><br/>");
					}
						
					return node.getSpeed();
				}
				i ++;
			}
			out.append(":<font color=red size=6>" + node.getSpeed().getValue() +"</font><font color=green size=2>（在市所在运营商找到匹配的网速）</font><br/>");
		return node.getSpeed();
	}
}

class TrieNode {
	private String key;

	private HashMap<String, TrieNode> child;

//	private HashMap<Integer,Count> data;
//	
//	private HashMap<Integer,Speed> speed ;

	private Count data;
	
	private Speed speed ;
	
	public TrieNode getChild(String k) {
		if (child == null) {
			return null;
		}
		return child.get(k);
	}

	public void addData(int site,Count count) {
//		if (data == null) {
//			data = new HashMap<Integer,Count>();
//			data.put(site, count);
//		}
//		else{
//			if (data.containsKey(site)){
//				Count c = data.get(site);
//				c.setCost(c.getCost() + count.getCost());
//				c.setDown(c.getDown() + count.getDown());
//				c.setTimes(c.getTimes() + count.getTimes());
//			}
//			else{
//				data.put(site, count);
//			}
//		}
		data = count;
	}

	public void removeData() {
		data = null;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Count getData() {
		return data;
	}

	public void addChild(TrieNode node) {
		if (child == null) {
			child = new HashMap<String, TrieNode>();
		}
		child.put(node.getKey(), node);
	}

	public HashMap<String, TrieNode> getChild() {
		return child;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
}
