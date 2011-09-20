package com.youku.search.recomend;

import java.sql.Connection;
import java.util.Date;
import java.util.TimerTask;

import com.youku.search.util.DataFormat;

public class RecomendListener extends TimerTask {
	private static boolean isRunning = false;

	@Override
	public void run() {
		if (!isRunning) {
			long frontMemory = Runtime.getRuntime().freeMemory();
			System.out.println("front_memory:" + frontMemory);
			Connection conn=null;
			Connection barconn=null;
//			Connection pkconn=null;
			Connection recomendconn=null;
			try{
				conn = DataConn.getSearchStatConn();
				String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
			if (TreeBuilder.isNotEmpty(date,conn)) {
				isRunning = true;
				Constance.init();
				long trietreeCreateStart = Runtime.getRuntime().freeMemory();
				System.out.println("date:"+date);
				System.out.println("tree create start:");
				recomendconn=DataConn.getSearchRecomendConn();
				TreeBuilder.createRecomendTreeByDB(recomendconn);
				long treeCreateStart = Runtime.getRuntime().freeMemory();
				System.out.println("recomend tree create success,use_memory:"
						+ (trietreeCreateStart - treeCreateStart));
				TreeBuilder.createTreeByDB(date,conn);
				long barTree = Runtime.getRuntime().freeMemory();
				System.out.println("man tree create success,use_memory:"
						+ (treeCreateStart - barTree));
				barconn = DataConn.getYoukubarConn();
				TreeBuilder.createBarTreeByDB(barconn);
				long pkTree = Runtime.getRuntime().freeMemory();
				System.out.println("bartree create success,use_memory:"
						+ (barTree - pkTree));
//				pkconn = DateConn.getYoukuConn();
//				TreeBuilder.createPKTreeByDB(pkconn);
//				System.out.println("pkTree create success,use_memory:"
//						+ (pkTree - Runtime.getRuntime().freeMemory()));
				System.out.println("use_memory:"
						+ (frontMemory - Runtime.getRuntime().freeMemory()));
			}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DataConn.releaseConn(recomendconn);
				DataConn.releaseConn(barconn);
//				DateConn.releaseConn(pkconn);
				DataConn.releaseConn(conn);
				isRunning = false;
			}
		} else {
			System.out.println("RecomendListener上一次任务执行还未结束..."); // 上一次任务执行还未结束
		}
	}
}
