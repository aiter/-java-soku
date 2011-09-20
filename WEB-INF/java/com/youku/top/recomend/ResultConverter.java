package com.youku.top.recomend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.top.recomend.Result.Item;



public class ResultConverter {
	
	private static Object jsonNullIfNull(Object value) {
        if (value == null) {
            return JSONObject.NULL;
        }

        return value;
    }
	
	public static JSONObject convert(Result rs) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("keyword", jsonNullIfNull(rs.keyword));
		JSONArray jarr = new JSONArray();
		if(null!=rs.items){
			for(Item item:rs.items){
				jarr.put(convert(item));
			}
		}
		json.put("result", jarr);
		json.put("cost", rs.cost);
		return json;
	}
	
	private static JSONObject convert(Result.Item item) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("keyword", jsonNullIfNull(item.keyword));
		json.put("count",item.count);
		json.put("type",item.type);
		return json;
	}
}
