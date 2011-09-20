package com.youku.soku.manage.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.torque.Torque;

public class VideoNameExport {
	public static void exportNames() throws SQLException, Exception{

		
		File file = new File("/home/eddie/videonames");
		if(!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		int totalCount = 34575878;
		
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			int startId = 2635680;			
			int limits = 1000;
			//int endId = startId + limits;
			conn = Torque.getConnection("youku");
			while((startId + limits) < totalCount) {
				//System.out.println("startid : " + startId + "endId" + (startId + limits));
				String sql = "SELECT pk_video, title FROM t_video where pk_video > " + startId + " And pk_video < " + (startId + limits);	
				
				pst = conn.prepareStatement(sql);
				rs = pst.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("pk_video");
					String title = rs.getString("title");	
					//System.out.println(title);
					if(title == null)
						continue;
					String record = new String(title.getBytes("ISO-8859-1"));
					bw.write(id + "   " + record + "\n");
				}
				
				startId += 100;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[ERROR] in the funciton updateYoukuContent");
			throw e;
		} catch (Exception e) {
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
				Torque.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out
						.println("[ERROR] in close connection in the funcion updateYoukuContent");
			}
		}
	
	}
}
