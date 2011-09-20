package com.youku.top.recomend;

import java.util.TimerTask;

public class RecomendListener extends TimerTask {
	private static boolean isRunning = false;
	
	@Override
	public void run() {
		if (!isRunning) {
			long frontMemory = Runtime.getRuntime().freeMemory();
			System.out.println("front_memory:" + frontMemory);
			try{
				isRunning = true;
				System.out.println("tree create start:");
				TreeBuilder.createTreeByInteface();
				System.out.println("use_memory:"
						+ (frontMemory - Runtime.getRuntime().freeMemory()));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				isRunning = false;
			}
		} else {
			System.out.println("RecomendListener上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}
}
