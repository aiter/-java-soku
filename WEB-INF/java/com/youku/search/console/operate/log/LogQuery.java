package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.LogInfo;
import com.youku.search.util.JdbcUtil;

public class LogQuery {
	
	public String buildQueryTab(String logdate){
		return new StringBuilder("query_").append(logdate).toString();
	}
	
	public List<LogInfo> getLogsByKeyword(String logdate,List<String> keywords,String opt,List<String> types,int limit){
		
		List<LogInfo> es = new ArrayList<LogInfo>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
				String tab = buildQueryTab(logdate);
				String sql = "select keyword,sum(query_count) as qc from "+tab+" where 1=1 ";
				StringBuilder sbf = new StringBuilder();
				if(types.size()>0)
					sbf.append(" and ");
				if(types.size()>1)
					sbf.append(" ( ");
				int j = types.size();
				for(String type:types){
					j--;
					sbf.append(" query_type = '");
					sbf.append(type);
					sbf.append("' ");
					if(0!=j)
						sbf.append(" or ");
				}
				if(types.size()>1)
					sbf.append(" ) ");
				if(keywords.size()>0)
					sbf.append(" and ");
				if(keywords.size()>1)
					sbf.append(" ( ");
				int i = keywords.size();
				for(String keyword:keywords){
					i--;
					sbf.append(" keyword like '%");
					sbf.append(keyword);
					sbf.append("%' ");
					if(i!=0)
						sbf.append(opt);
				}
				if(keywords.size()>1)
					sbf.append(" ) ");
				sql = sql+sbf.toString()+" group by keyword order by qc desc ";
				if(limit>0) sql = sql + " limit "+limit;
				System.out.println(sql);
				conn = DataConn.getLogStatConn();
				pst = conn.prepareStatement(sql);
				rs = pst.executeQuery();
				LogInfo e = null;
				while(rs.next()){
					e = new LogInfo();
					e.setKeyword(rs.getString("keyword"));
					e.setCounts(rs.getInt("qc"));
					es.add(e);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pst);
			JdbcUtil.close(conn);
		}
		
		return es;
	}
	
	
}
