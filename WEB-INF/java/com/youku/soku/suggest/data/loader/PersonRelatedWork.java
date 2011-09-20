package com.youku.soku.suggest.data.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.suggest.entity.PersonWork;

public class PersonRelatedWork {

	private static final int PERSON_WORKS_SIZE = 3;

	private Logger log = Logger.getLogger(this.getClass());

	public List<PersonWork> getPersonRelatedEpisode(String personName) {
		try {
			String sokuHost = Config.getSokuHost();
			log.info("Get the works of: " + personName);
			List<PersonWork> result = new ArrayList<PersonWork>();
			byte[] bytes = Wget.get("http://" + sokuHost + "/v?keyword=" + personName + "&print");
			JSONObject json = new JSONObject(new String(bytes, "utf-8"));
			JSONObject extJson = json.optJSONObject("ext");
			JSONObject personJson = extJson.optJSONObject("person");
			
			if(personJson != null) {
				JSONArray teleJsArr = personJson.optJSONArray("teleplay");
				JSONArray movieJsArr = personJson.optJSONArray("movie");

				if (teleJsArr != null) {
					for (int i = 0; i < teleJsArr.length(); i++) {
						if (i > PERSON_WORKS_SIZE) {
							break;
						}
						JSONObject teleObj = teleJsArr.getJSONObject(i);
						result.add(getPersonWork(teleObj));
					}
				}

				if (movieJsArr != null) {
					for (int i = 0; i < movieJsArr.length(); i++) {
						if (i > PERSON_WORKS_SIZE) {
							break;
						}
						JSONObject movieObj = movieJsArr.getJSONObject(i);
						PersonWork pw = getPersonWork(movieObj);
						result.add(pw);
					}
				}

				if (result.size() > PERSON_WORKS_SIZE) {
					Collections.sort(result, new Comparator<PersonWork>() {
						@Override
						public int compare(PersonWork o1, PersonWork o2) {
							return o2.getReleaseTime().compareTo(o1.getReleaseTime());
						}
					});
					result = result.subList(0, PERSON_WORKS_SIZE);
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private PersonWork getPersonWork(JSONObject jsonObj) throws JSONException {
		JSONObject names_object = jsonObj.getJSONObject("names_object");
		PersonWork pw = new PersonWork();
		pw.setWorkName(names_object.getString("name"));
		pw.setReleaseTime(DataFormat.parseUtilDate(jsonObj.getString("release_time"), DataFormat.FMT_DATE_YYYYMMDD));

		return pw;
	}
}
