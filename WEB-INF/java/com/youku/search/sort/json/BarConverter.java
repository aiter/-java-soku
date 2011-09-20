package com.youku.search.sort.json;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.index.entity.Bar;
import com.youku.search.util.StringUtil;

public class BarConverter extends AbstractConverter {

	static Log logger = LogFactory.getLog(BarConverter.class);

	public static JSONObject convert(Bar bar) {

		if (bar == null) {
			return null;
		}

		try {
			JSONObject object = new JSONObject();

			object.put("pk_bar", bar.pk_bar);
			object.put("bar_name", StringUtil.filterNull(bar.bar_name));

			JSONArray cate_ids = new JSONArray();
			JSONArray cate_names = new JSONArray();

			object.put("cate_id", 0);
			object.put("cate_name", "");
			object.put("cate_ids", cate_ids);
			object.put("cate_names", cate_names);

			if (bar.bar_catalog_ids != null && bar.bar_catalog_names != null) {
				for (int i = 0; i < bar.bar_catalog_ids.size()
						&& i < bar.bar_catalog_names.size(); i++) {
					cate_ids.put(bar.bar_catalog_ids.get(i));
					cate_names.put(StringUtil.filterNull(bar.bar_catalog_names
							.get(i)));
				}
			}

			if (cate_ids.length() > 0) {
				object.put("cate_id", cate_ids.get(0));
				object.put("cate_name", cate_names.get(0));
			}

			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(List<Bar> list, int total) {

		try {
			JSONObject jsonObject = new JSONObject();
			JSONObject itemsObject = new JSONObject();

			jsonObject.put("total", total);
			jsonObject.put("items", itemsObject);

			if (list == null) {
				return jsonObject;
			}

			int index = 0;
			for (int i = 0; i < list.size(); i++) {
				JSONObject object = convert(list.get(i));
				if (object != null) {
					itemsObject.put(index + "", object);
					index++;
				}
			}

			return jsonObject;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
}
