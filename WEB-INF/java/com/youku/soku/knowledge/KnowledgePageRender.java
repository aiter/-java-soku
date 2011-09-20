package com.youku.soku.knowledge;

import java.util.List;

import com.youku.soku.knowledge.KnowledgeDataLoader.KnowledgeDataNode;

public class KnowledgePageRender {
	
	public static void displayKnowledgeTree(int step, KnowledgeDataNode node, StringBuilder builder) {
		if(node == null) {
			return;
		}
		builder.append("<li><div>" + node.getName()) 
		.append("<a href='/KnowledgeColumn_input.do?parentId=" + node.getId() + "&knowledgeColumnId=-1'><font color='blue'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+</font></a>")
					.append("<a href='/KnowledgeColumn_delete.do?knowledgeColumnId=" + node.getId() +"'><font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x</font></a></div>");

		
		if(node.getChild() != null) {
			builder.append("<input type=\"checkbox\" ");
			if(step == 0) {
				builder.append("checked=\"checked\"");
			}
			builder.append("/>");
		}
		builder.append("<ol>");
		//builder.append(node.getName());
		List<KnowledgeDataNode> dataList = node.getChild();
		if(dataList != null) {			
			for(KnowledgeDataNode kdn : dataList) {
				if(kdn.getChild() == null) {
					//builder.append("<li class=\"file\">" + kdn.getName());
				}
				displayKnowledgeTree(step + 1, kdn, builder);
				if(kdn.getChild() == null) {
					//builder.append("</li>");
				}
			}
			
		}
		builder.append("</ol></li>");
	}
	
	public static String displayKnowledgeTree(KnowledgeDataNode root) {
		StringBuilder builder = new StringBuilder();
		displayKnowledgeTree(0, root, builder);
		return builder.toString();
	}
	
	
	public static void displayKnowledgeRank(int step, KnowledgeDataNode node, StringBuilder builder) {
		if(node == null) {
			return;
		}
		builder.append("<li><label>" + node.getName() + "</label>");
		if(node.getChild() != null) {
			builder.append("<input type=\"checkbox\" />");
		}
		builder.append("<ol>");
		//builder.append(node.getName());
		List<KnowledgeDataNode> dataList = node.getChild();
		if(dataList != null) {			
			for(KnowledgeDataNode kdn : dataList) {
				if(kdn.getChild() == null) {
					//builder.append("<li class=\"file\">" + kdn.getName());
				}
				displayKnowledgeTree(step + 1, kdn, builder);
				if(kdn.getChild() == null) {
					//builder.append("</li>");
				}
			}
			
		}
		builder.append("</ol></li>");
	}
	
	public static String displayKnowledgeRank(KnowledgeDataNode root) {
		StringBuilder builder = new StringBuilder();
		displayKnowledgeTree(0, root, builder);
		return builder.toString();
	}

}
