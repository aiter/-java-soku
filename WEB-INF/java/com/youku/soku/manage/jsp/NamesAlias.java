package com.youku.soku.manage.jsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.soku.util.DataBase;

public class NamesAlias {
	// 找出系列别名和系列名重复的数据

	public void parse() {
		Map<String, String> names = new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM names";
		try {
			conn = DataBase.getLibraryConn();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				names.put(rs.getString("name"), rs.getString("alias"));
			}

			for (String key : names.keySet()) {
				String alias = names.get(key);
				
				if(alias != null && alias.indexOf("|") > 0) {
					String[] as = alias.split("[|]");
					
					for(String a : as) {
						if (names.get(a) != null) {
							System.out.printf("name: %s   alias: %s \n", key, alias);
							System.out.printf("name: %s   alias: %s \n", a, names
									.get(a));
						}
					}
				}
				else if (names.get(alias) != null) {
					System.out.printf("name: %s   alias: %s \n", key, alias);
					System.out.printf("name: %s   alias: %s \n", alias, names
							.get(alias));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Torque.closeConnection(conn);
		}
	}

	public static void main(String[] args) {
		try {
			Torque.init("/opt/search/WEB-INF/soku-conf/Torque.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		NamesAlias na = new NamesAlias();
		na.parse();

		try {
			Torque.shutdown();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
}
