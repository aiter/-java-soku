package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.TopQuickVO;
import com.youku.search.console.vo.TopQuickVO.TopView;

public class TopQuickView {
	
	public static Map<String,TopQuickVO> topmap=new ConcurrentHashMap<String,TopQuickVO>();
	
	public TopQuickVO getTopQuickVO(String date){
		TopQuickVO tqv= new TopQuickVO();
		String cuur_date=date.replace("-", "_");
//		String befor_date=DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(cuur_date,DataFormat.FMT_DATE_YYYY_MM_DD),-1), DataFormat.FMT_DATE_YYYY_MM_DD);
		TopQuicker tq=new TopQuicker();
		tqv.setDate(date);
		Connection conn=null;
		try{
			conn=DataConn.getLogStatConn();
			List<TopView> total=tq.getTotalTopQuickDate(cuur_date,conn);
			if(null!=total&&total.size()>0)
				tqv.getTopviewmap().put("total",total);
			List<TopView> video=tq.getVideoTopQuickDate(cuur_date,conn);
			if(null!=video&&video.size()>0)
				tqv.getTopviewmap().put("video",video);
			List<TopView> folder=tq.getFolderTopQuickDate(cuur_date,conn);
			if(null!=folder&&folder.size()>0)
				tqv.getTopviewmap().put("folder",folder);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(conn);
		}
		return tqv;
	}

}
