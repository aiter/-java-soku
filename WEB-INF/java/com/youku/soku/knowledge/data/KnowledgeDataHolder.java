package com.youku.soku.knowledge.data;

import com.youku.soku.knowledge.KnowledgeData;

public class KnowledgeDataHolder {
	
	private static KnowledgeData data = new KnowledgeData();
	
	private static ThreadLocal<KnowledgeData> localThread = new ThreadLocal<KnowledgeData>();
	
	public static void setCurrentKnowledgeData(KnowledgeData _data) {
		//data.clear();
		//KnowledgeDataMap temp = data;
		data = _data;
	//	temp.clear();
	}
	
	public static KnowledgeData getCurrentThreadLocal() {
		KnowledgeData localData = localThread.get();
		if(localData == null) {
			localData = data;
			localThread.set(data);
		}
		return localData;
	}
	
	public static void removeCurrentThreadLocal() {
		localThread.remove();
	}

}
