package com.youku.top.topn;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetUtil {
	
	public static boolean isNotNull(ResultSet rs,String column){
		try {
			if(rs.findColumn(column)>0)
				return true;
			else return false;
		} catch (SQLException e) {
		}
		return false;
	}
}
