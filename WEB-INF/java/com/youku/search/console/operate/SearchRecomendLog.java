package com.youku.search.console.operate;

import com.youku.search.console.pojo.Trietreerecommend;

public class SearchRecomendLog {
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("RECOMENDLOG");
	
	public static void writeLog(int type,String title,Trietreerecommend t,Trietreerecommend tempt){
		if(type==1)
			_log.info(title+"\t"+t.toString());
		if(type==2)
			_log.info(title+"\t修改后:"+t.toString()+"\t修改前:"+tempt.toString());
		if(type==3)
			_log.info(title+"\t"+tempt.toString());
	}
}
