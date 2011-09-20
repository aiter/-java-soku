package com.youku.soku.knowledge.timer;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.youku.soku.knowledge.KnowledgeData;
import com.youku.soku.knowledge.KnowledgeDataLoader;
import com.youku.soku.knowledge.KnowledgeDataLoader.KnowledgeDataNode;
import com.youku.soku.knowledge.data.KnowledgeDataHolder;

public class KnowledgeDataLoadTimer {

	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final long delay = 1000 * 2;
	
	private static final long period = 1000 * 60 * 60;
	
	private static int counter = 0;
	
	public void start() {
		
		
		log.info("Knowledge Loader Timer Begin...... ");
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				counter++;
				log.info("开始加载 知识分类.....Counter: " + counter);
				KnowledgeDataNode node = new KnowledgeDataNode();
				KnowledgeDataLoader loader = new KnowledgeDataLoader();
				KnowledgeData data = new KnowledgeData();
				loader.loadDataFromDbToMap(node, data);
				loader.loadKnowledgeRank(data.getKnowledgeRankMap());
				KnowledgeDataHolder.setCurrentKnowledgeData(data);
				/*ObjectSaverAndLoader saver = new ObjectSaverAndLoader("/opt/trietree/", "trietree");
				saver.save(tree);*/
				
				KnowledgeDataNode data1 = data.getNodeData("武术");
				if(data1.getChild() != null) {
					for(KnowledgeDataNode k : data1.getChild()) {
						log.info("knowledge colunm: " + k.getName());
					}
				}
				log.info("知识分类  加载成功");
			}
			
		};
		Timer timer = new Timer();
		timer.schedule(task, delay, period);
	}



}
