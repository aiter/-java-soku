package com.youku.search.console.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.WebSiteUnion;
import com.youku.search.console.vo.website.Unions;

public class WebLogTotalAction {
	List<Unions> unionslist;

	public List<Unions> getUnionslist() {
		return unionslist;
	}

	public void setUnionslist(List<Unions> unionslist) {
		this.unionslist = unionslist;
	}

	public String view(){
		LogInfoWriter.operate_log.info("外网统计----");
		WebSiteUnion wu=new WebSiteUnion();
		Connection conn=null;
		try {
			conn=DataConn.getWebLogStatConn();
			unionslist=wu.showUnions(conn,"select * from web_views order by uniondate desc limit 10");
			wu.showSiteUnions(conn,"select * from web_sites order by uniondate,site,because asc",unionslist);
		} catch (TorqueException e) {
			e.printStackTrace();
			unionslist=new ArrayList<Unions>();
		}finally{
			DataConn.releaseConn(conn);
		}
		return Action.SUCCESS;
	}
}
