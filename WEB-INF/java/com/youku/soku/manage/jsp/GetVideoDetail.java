package com.youku.soku.manage.jsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;
import com.youku.soku.manage.common.CommonVideo;

public class GetVideoDetail {
	
	public static void getYoukuContent() throws SQLException, TorqueException{
		
		
		String sql = "SELECT * FROM t_video WHERE pk_video = "
				+ 43157605;
		String imgHost = "http://g" + (System.currentTimeMillis() % 4 + 1)
				+ ".ykimg.com/";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = Torque.getConnection("youku");
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				String thumb0 = rs.getString("thumb0");
				String thumb4 = rs.getString("thumb4");
				double second = rs.getDouble("seconds");
				
				String title = rs.getString("title");
				
				String categoryName = rs.getString("category_names");
				
				String ownerName = rs.getString("category_names");
				
				System.out.format("title: %s, category %s, owner: %s", MyUtil.getString(title), MyUtil.getString(categoryName), MyUtil.getString(ownerName));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[ERROR] in the funciton updateYoukuContent");
			throw e;
		} catch (TorqueException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out
						.println("[ERROR] in close connection in the funcion updateYoukuContent");
			}
		}
	}

}
