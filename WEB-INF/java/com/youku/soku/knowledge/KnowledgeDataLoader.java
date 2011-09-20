package com.youku.soku.knowledge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.KnowledgeColumn;
import com.youku.soku.library.load.KnowledgeColumnPeer;

public class KnowledgeDataLoader {

	private Logger log = Logger.getLogger(this.getClass());

	/*
	 * public void printKnowledgeTree() { printKnowledgeTree(0, root); }
	 */

	public static void printKnowledgeTree(int step, KnowledgeDataNode node) {
		if (node == null) {
			return;
		}
		System.out.println(node.getName());
		List<KnowledgeDataNode> dataList = node.child;
		if (dataList != null) {
			for (KnowledgeDataNode kdn : dataList) {
				for (int i = 0; i < step; i++) {
					System.out.print("-");
				}
				printKnowledgeTree(step + 4, kdn);
			}
		}
	}

	public void loadDataFromDb(KnowledgeDataNode root) {
		try {
			loadSubColumn(0, root, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void loadDataFromDbToMap(KnowledgeDataNode root, KnowledgeData map) {
		try {
			loadSubColumn(0, root, map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void loadSubColumn(int parentId, KnowledgeDataNode pNode, KnowledgeData map) throws Exception {

		if (pNode == null) {
			return;
		}
		Criteria crit = new Criteria();
		crit.add(KnowledgeColumnPeer.PARENT_ID, parentId);
		List<KnowledgeColumn> kcList = KnowledgeColumnPeer.doSelect(crit);

		if (kcList != null) {

			for (KnowledgeColumn kc : kcList) {
				KnowledgeDataNode node = new KnowledgeDataNode();
				node.setName(kc.getName());
				node.setId(kc.getId());
				node.setParentId(kc.getParentId());
				node.setPic(kc.getPic());
				node.parent = pNode;
				if (pNode.child == null) {
					pNode.child = new ArrayList<KnowledgeDataNode>();
				}
				pNode.child.add(node);

				loadSubColumn(kc.getId(), node, map);
				if (map != null) {
					map.addData(node.name, node);
				}
			}
		}
	}

	public void loadKnowledgeRank(Map<String, List<KnowledgeColumn>> knowledgeRankMap) {

	
		try {
			Criteria crit = new Criteria();
			crit.add(KnowledgeColumnPeer.PARENT_ID, 0);
			List<KnowledgeColumn> kcList = KnowledgeColumnPeer.doSelect(crit);

			if (kcList != null) {

				for (KnowledgeColumn kc : kcList) {
					Criteria subCrit = new Criteria();
					subCrit.add(KnowledgeColumnPeer.PARENT_ID, kc.getId());
					List<KnowledgeColumn> subList = KnowledgeColumnPeer.doSelect(subCrit);
					
					knowledgeRankMap.put(kc.getName(), subList);
				}
			}
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void clear(KnowledgeDataNode node) {
		if (node == null) {
			return;
		}
		List<KnowledgeDataNode> dataList = node.child;
		if (dataList != null) {
			for (KnowledgeDataNode kdn : dataList) {
				clear(kdn);
			}
			dataList.clear();
		}
	}

	public static class KnowledgeDataNode {

		private String name;

		private String pic;

		private int id;

		private int parentId;

		private KnowledgeDataNode parent;

		private List<KnowledgeDataNode> child;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public List<KnowledgeDataNode> getChild() {
			return child;
		}

		public void setChild(List<KnowledgeDataNode> child) {
			this.child = child;
		}

		public KnowledgeDataNode getParent() {
			return parent;
		}

		public void setParent(KnowledgeDataNode parent) {
			this.parent = parent;
		}

		public void clear() {
			if (child != null) {
				child.clear();
				child = null;
			}
		}

	}
}
