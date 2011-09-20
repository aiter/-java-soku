package com.youku.soku.manage.datamaintain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.util.JdbcUtil;
import com.youku.soku.util.DataBase;

public class VideoDownloadUtil {
	
	public String getDownloadUrl(int showId) {
		//104245
		try {
			String middleTireUrl = "http://10.103.12.72/video.show?q=show_id%3A" + showId + "%20show_videotype%3A%E6%AD%A3%E7%89%87&fc=&fd=title%20pk_video&pn=1&pl=30&ob=createtime%3Adesc&ft=json&cl=test_page&h=3";
			String urlGenerator = "http://10.101.4.1/encoder_queue_manager/getoriginalvideofile_re.php?id=";
			String result = new String(Wget.get(middleTireUrl));
			Pattern urlPattern = Pattern.compile ("\\\"(http://[^>]+)\\\"");
			StringBuilder builder = new StringBuilder();
			JSONObject jsonObj = new JSONObject(result);
			JSONArray jsArr = jsonObj.optJSONArray("results");
			
			for(int i = 0; i < jsArr.length(); i++) {
				JSONObject videoObj = jsArr.getJSONObject(i);
				String pkVideo = videoObj.optString("pk_video");
				String title = videoObj.optString("title");
				System.out.println(title + pkVideo);
				
				String fileIdStr = getFileId(Integer.valueOf(pkVideo));
				String downloadStr = new String(Wget.get(urlGenerator + fileIdStr));
				Matcher m = urlPattern.matcher(downloadStr);
				if(m.find()) {
					String resultUrl = m.group(1);
					String suffix = resultUrl.substring(resultUrl.lastIndexOf("."));
					builder.append("wget -O " + title.replace(" ", "") + suffix + " " + resultUrl + "\n");
				}
			}
			return builder.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getFileId(int pkVideo) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DataBase.getYoqooConn();
			String sql = "SELECT file_id FROM t_video where pk_video = ?";
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, pkVideo);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				String fileIdStr = rs.getString("file_id");
				
				return fileIdStr;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
		
		return null;
	}


}
