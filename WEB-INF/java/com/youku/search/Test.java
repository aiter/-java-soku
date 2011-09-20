/**
 * 
 */
package com.youku.search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.search.util.Request;





/**
 * @author 1verge
 *
 */
public class Test {
	
public static void main(String[] args) throws JSONException{
	JSONArray data = new JSONArray();
	try {
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("e:/Noname3.txt")));
		String line = null;
		
		List<Integer> movies =new ArrayList<Integer>();
		List<Integer> teleplays =new ArrayList<Integer>();
		
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while ( (line = r.readLine()) != null)
		{
			int id = DataFormat.parseInt(line);
			if (i<50)
				movies.add(id);
			else
				teleplays.add(id);
			
			i++;
		}
		for (int j = 0 ;j < movies.size() ; j ++)
		{
			int id = movies.get(j);
			builder.append(id).append(",");
			
			builder.append(teleplays.get(j)).append(",");
			
			if(j%10 == 0){
				getStr(builder.deleteCharAt(builder.length()-1).toString(),data);
				
				builder = new StringBuilder();
			}
		}
		
		if (builder.length() > 0){
			getStr(builder.deleteCharAt(builder.length()-1).toString(),data);
		}
		r.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	JSONObject da = new JSONObject();
	da.put("data", data);
	
	System.out.println(da);
	System.out.println(da.toString(4));
}


	public static void getStr(String ids,JSONArray data){
		String str = Request.requestGet("http://10.103.8.217/top/search?programmeId="+ids+"&cate=1");
		
		System.out.println("http://10.103.8.217/top/search?programmeId="+ids+"&cate=1");
		JSONObject json;
		try {
			json = new JSONObject(str);
			JSONArray array = json.optJSONArray("array");
			
			
			for (int i = 0 ;i < array.length() ;i ++){
				JSONObject obj = array.optJSONObject(i);
				JSONObject programme = obj.optJSONObject("programme");
				String url = programme.optString("url");
				String pic = programme.optString("pic");
				if (pic.endsWith("0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80"))
					continue;
				
				String name = programme.optString("name");
				DecimalFormat df=new DecimalFormat("#.0");
				float rate = DataFormat.parseFloat(programme.optString("score"),5f);
				StringBuilder resultBf = new StringBuilder();
				
				 int fullInt=new Double(rate/2).intValue();
			        int index=0;
			        for( index=0;index<fullInt;index++){
			                resultBf.append("2");
			        }

			        if(rate/2-fullInt>0){
			                resultBf.append("1");
			                index++;
			        }


			        for( ;index<5;index++){
			                resultBf.append("0");
			        }
			    JSONObject da = new JSONObject();
			    da.put("name",name);
			    da.put("pic",pic);
			    da.put("url",url);
			    da.put("rate",df.format(rate));
			    da.put("star",resultBf);
			    
			    data.put(da);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
