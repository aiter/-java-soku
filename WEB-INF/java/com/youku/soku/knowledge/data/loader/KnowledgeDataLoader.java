package com.youku.soku.knowledge.data.loader;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.KnowledgeColumn;
import com.youku.soku.library.load.KnowledgeColumnPeer;

public class KnowledgeDataLoader {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public void loadDataFromDb() {
		try {
			Criteria crit = new Criteria();
			crit.add(KnowledgeColumnPeer.PARENT_ID, 0);
			List<KnowledgeColumn> kcList = KnowledgeColumnPeer.doSelect(crit);
			
			
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		} 
	}
	
	
}
