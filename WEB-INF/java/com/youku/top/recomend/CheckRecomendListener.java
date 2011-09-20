package com.youku.top.recomend;

import java.util.List;
import java.util.TimerTask;


public class CheckRecomendListener extends TimerTask {
	private static boolean isRunning = false;

	@Override
	public void run() {
		System.out.println("搜索联想数据树检测开始");
			if(null==Constance.videoTree||!check())
				rebulitTrieTree();
		System.out.println("搜索联想数据树检测结束");
	}
	
	private boolean check(){
		List<Entity> st = Constance.videoTree.search("康");
		if(null!=st&&st.size()>0)
			return true;
		st = Constance.videoTree.search("快");
		if(null!=st&&st.size()>0)
			return true;
		return false;
	}
	
	public static boolean rebulitTrieTree(){
		if (!isRunning) {
		long frontMemory = Runtime.getRuntime().freeMemory();
		try{
			isRunning = true;
			System.out.println("trie tree rebult start:");
			TreeBuilder.createTreeByInteface();
			System.out.println("trie tree rebult end,use_memory:"
					+ (frontMemory - Runtime.getRuntime().freeMemory()));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			isRunning = false;
		}
		return true;
	} else {
		System.out.println("RecomendListener上一次任务执行还未结束..."); // 上一次任务执行还未结束
		return false;
	}
	}
}
