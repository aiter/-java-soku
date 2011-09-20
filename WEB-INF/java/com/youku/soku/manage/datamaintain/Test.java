package com.youku.soku.manage.datamaintain;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.Request;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;


public class Test {
	
	private static int no_img_count = 0;
	
	static int lineNum = 1;
	
	public static void run() throws Exception {
		//File file = new File("E:/MyDocument/中间层数据.csv");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/opt/allmovie_csv.csv"), "GBK"));
		WritableWorkbook workbook = Workbook.createWorkbook(new File("/opt/allmovie1.xls")); 
		WritableSheet sheet = workbook.createSheet("First Sheet", 0); 
		
		Label label = new Label(1, 0, "showname");
		sheet.addCell(label);
		label = new Label(2, 0, "showalias");
		sheet.addCell(label);
		label = new Label(3, 0, "show_thumburl");
		sheet.addCell(label);
		label = new Label(4, 0, "releasedate");
		sheet.addCell(label);
		label = new Label(5, 0, "showlength");
		sheet.addCell(label);
		label = new Label(6, 0, "area");
		sheet.addCell(label);
		label = new Label(7, 0, "movie_genre");
		sheet.addCell(label);
		label = new Label(8, 0, "director");
		sheet.addCell(label);
		label = new Label(9, 0, "person");
		sheet.addCell(label);
		label = new Label(10, 0, "personCharacter");
		
		sheet.addCell(label);
		
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.CATE, 1);
		crit.addAscendingOrderByColumn(ProgrammePeer.CONTENT_ID);
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		for(Programme p : pList) {
			bw.write(getData(p.getContentId(), sheet));
		}
		workbook.write();
		workbook.close();
		
		bw.write("no_img_count" + no_img_count);
		bw.flush();
		bw.close();
		System.out.println(no_img_count);
	}
	
	private static String getData(int showId, WritableSheet sheet) {
		String url = "http://10.103.88.54/show.show?q=showid:" + showId + "&fc=&fd=showname%20show_thumburl%20releasedate%20showlength%20area%20movie_genre%20tv_genre%20showalias%20director%20performer&pn=0&pl=100&ob=showtotal_vv%3Adesc&ft=json&cl=test_page&h=3";
		
		try {
			String result = Request.requestGet(url, 5000);
			JSONObject json = new JSONObject(result);
			JSONArray jsArr = json.optJSONArray("results");
			
			StringBuilder builder = new StringBuilder();
			System.out.println(showId);
			if(jsArr.length() > 0) {
				for(int i = 0; i < jsArr.length(); i++) {

					JSONObject video = jsArr.getJSONObject(i);
					System.out.println(video.optString("showname"));
					builder.append(video.optString("showname").replace(",", "")).append(",");
					builder.append(video.optString("showalias").replace(",", "")).append(",");
					builder.append(video.optString("show_thumburl").replace(",", "")).append(",");
					if("http://res.mfs.ykimg.com/051000004DACF16A9792732B23061177.jpg".equals(video.optString("show_thumburl"))) {
						no_img_count++;
					}
					builder.append(video.optString("releasedate").replace(",", "")).append(",");
					builder.append(video.optString("showlength").replace(",", "")).append(",");
					builder.append(video.optString("area").replace(",", "")).append(",");
					builder.append(video.optString("movie_genre").replace(",", "")).append(",");
					JSONArray directorsArr = video.optJSONArray("director");
					builder.append(personJsonArrayToString(directorsArr).replace(",", "")).append(",");
					JSONArray performerArr = video.optJSONArray("performer");
					builder.append(personJsonArrayToString(performerArr).replace(",", "")).append(",");
					builder.append(personCharacterJsonArrayToString(performerArr).replace(",", "")).append("\n");
					
					lineNum = i + lineNum;
				
					Label label = new Label(1, lineNum, video.optString("showname"));
					sheet.addCell(label);
					label = new Label(2, lineNum, video.optString("showalias"));
					sheet.addCell(label); 
					label = new Label(3, lineNum, video.optString("show_thumburl"));
					sheet.addCell(label); 
					label = new Label(4, lineNum, video.optString("releasedate"));
					sheet.addCell(label); 
					
					
					
					label = new Label(5, lineNum, video.optString("showlength"));
					sheet.addCell(label); 
					
					
					label = new Label(6, lineNum, video.optString("area"));
					sheet.addCell(label); 
					
					label = new Label(7, lineNum, video.optString("movie_genre"));
					sheet.addCell(label); 
					
					label = new Label(8, lineNum, personJsonArrayToString(directorsArr));
					sheet.addCell(label);
					
					label = new Label(9, lineNum, personJsonArrayToString(performerArr));
					sheet.addCell(label);
					
					label = new Label(10, lineNum, personCharacterJsonArrayToString(performerArr));
					sheet.addCell(label);
				}
				
			}
			return builder.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String personJsonArrayToString(JSONArray jsArr) throws JSONException {
		StringBuilder result = new StringBuilder();
		if(jsArr != null && jsArr.length() > 0) {
			for(int i = 0; i < jsArr.length(); i++) {
				if(result.length() > 0) {
					result.append(",");
				}
				JSONObject jsObj = jsArr.optJSONObject(i);
				if(jsObj != null) {
					result.append(jsObj.optString("name"));
				}
				
			}
		}
		
		return result.toString();
	}
	
	private static String personCharacterJsonArrayToString(JSONArray jsArr) throws JSONException {
		StringBuilder result = new StringBuilder();
		if(jsArr != null && jsArr.length() > 0) {
			for(int i = 0; i < jsArr.length(); i++) {
				if(result.length() > 0) {
					result.append(",");
				}
				JSONObject jsObj = jsArr.optJSONObject(i);
				if(jsObj != null) {
					result.append(jsObj.optString("character"));
				}
				
			}
		}
		
		return result.toString();
	}

}
