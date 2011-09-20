package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.website.ClickView;

public class WebSiteClickMgt {
	public List<ClickView> showClicks(String sql){
		List<ClickView> cvs=new ArrayList<ClickView>();
		Connection conn=null;
		try{
			conn=DataConn.getWebLogStatConn();
			PreparedStatement pt = null;
			ResultSet rs = null;
			ClickView cv = null;
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
				cv=new ClickView();
				cv.setKeyword(rs.getString("keyword"));
				
				cvs.add(cv);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(conn);
		}
		return cvs;
	}
	
	
}
