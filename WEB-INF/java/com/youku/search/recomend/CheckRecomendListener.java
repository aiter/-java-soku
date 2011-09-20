package com.youku.search.recomend;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.youku.search.util.DataFormat;

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
		Connection conn=null;
		Connection barconn=null;
//		Connection pkconn=null;
		Connection recomendconn=null;
		try{
			conn = DataConn.getSearchStatConn();
			String date = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
			for(int i=2;i<7;i++){
				if (TreeBuilder.isNotEmpty(date,conn))
					break;
				date = DataFormat.formatDate(DataFormat.getNextDate(new Date(),-i), DataFormat.FMT_DATE_YYYY_MM_DD);
			}
		if (TreeBuilder.isNotEmpty(date,conn)) {
			isRunning = true;
			System.out.println("date:"+date);
			System.out.println("trie tree rebult start:");
			Constance.init();
			recomendconn=DataConn.getSearchRecomendConn();
			TreeBuilder.createRecomendTreeByDB(recomendconn);
			System.out.println("recomend tree rebulit success");
			TreeBuilder.createTreeByDB(date,conn);
			System.out.println("main tree rebulit success");
			barconn = DataConn.getYoukubarConn();
			TreeBuilder.createBarTreeByDB(barconn);
			System.out.println("bar tree rebulit success");
//			pkconn = DateConn.getYoukuConn();
//			TreeBuilder.createPKTreeByDB(pkconn);
			System.out.println("trie tree rebult end,use_memory:"
					+ (frontMemory - Runtime.getRuntime().freeMemory()));
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(recomendconn);
			DataConn.releaseConn(barconn);
//			DateConn.releaseConn(pkconn);
			DataConn.releaseConn(conn);
			isRunning = false;
		}
		return true;
	} else {
		System.out.println("RecomendListener上一次任务执行还未结束..."); // 上一次任务执行还未结束
		return false;
	}
	}
}
