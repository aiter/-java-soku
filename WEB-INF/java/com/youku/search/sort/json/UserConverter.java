package com.youku.search.sort.json;

import static com.youku.search.util.StringUtil.filterNull;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.index.entity.User;
import com.youku.search.sort.entity.CityMap;

public class UserConverter extends AbstractConverter {

	static Log logger = LogFactory.getLog(UserConverter.class);

	public static JSONObject convert(User user) {

		if (user == null) {
			return null;
		}

		try {
			JSONObject object = new JSONObject();

			object.put("pk_user", user.pk_user);
			object.put("user_name", filterNull(user.user_name));
			object.put("gender", user.gender);

			String city = CityMap.getCityById(user.city);
			object.put("city", filterNull(city));

			object.put("icon64", filterNull(user.icon64));
			object.put("user_score", user.user_score);
			object.put("video_count", user.video_count);
			object.put("order_count", user.order_count);
			object.put("last_login_date", formatDate(user.last_login_date));
			object.put("last_content_date", formatDate(user.last_content_date));
			object.put("reg_date", formatDate(user.reg_date));
			object.put("fav_count", user.fav_count);

			return object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public static JSONObject convert(List<User> list, int total) {

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
