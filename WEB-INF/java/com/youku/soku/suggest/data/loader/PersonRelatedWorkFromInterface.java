package com.youku.soku.suggest.data.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.console.util.Wget;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.suggest.entity.PersonWork;

public class PersonRelatedWorkFromInterface {

	private static final int PERSON_WORKS_SIZE = 10;

	private Logger log = Logger.getLogger(this.getClass());
	
	private static final String INTERFACE_HOST = "http://10.103.8.217/personpro/search?person=";

	private SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy");
	public List<PersonWork> getPersonRelatedEpisode(String personName) {
		try {
			log.info("Get the works of: " + personName);
			List<PersonWork> result = new ArrayList<PersonWork>();
			byte[] bytes = Wget.get(INTERFACE_HOST + personName + "&h&d");
			JSONObject personObjs = new JSONObject(new String(bytes, "utf-8"));
			
			if(personObjs != null) {
				Iterator it = personObjs.keys();
				while(it.hasNext()) {
					JSONObject personJson = personObjs.optJSONObject((String)it.next());
					JSONArray teleJsArr = personJson.optJSONArray("TELEPLAY");
					JSONArray movieJsArr = personJson.optJSONArray("MOVIE");

					if (teleJsArr != null) {
						for (int i = 0; i < teleJsArr.length(); i++) {
							/*if (i > PERSON_WORKS_SIZE) {
								break;
							}*/
							JSONObject teleObj = teleJsArr.getJSONObject(i);
							result.add(getPersonWork(teleObj, Constants.TELEPLAY_CATE_ID));
						}
					}

					if (movieJsArr != null) {
						for (int i = 0; i < movieJsArr.length(); i++) {
							/*if (i > PERSON_WORKS_SIZE) {
								break;
							}*/
							JSONObject movieObj = movieJsArr.getJSONObject(i);
							PersonWork pw = getPersonWork(movieObj, Constants.MOVIE_CATE_ID);
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
				
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private PersonWork getPersonWork(JSONObject jsonObj, int cate) throws JSONException {
		PersonWork pw = new PersonWork();
		String workName = jsonObj.getString("name");
		if(workName != null) {
			workName = workName.replace(" ", "");
		}
		pw.setWorkName(workName);
		pw.setReleaseTime(getReleaseTime(jsonObj.getString("releaseyear")));
		pw.setCate(cate);
		return pw;
	}
	
	private Date getReleaseTime(String releaseTime) {
		try {
			return fmtDate.parse(releaseTime);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			try {
				return fmtDate.parse("1000");
			} catch(ParseException ex) {
				log.error(ex.getMessage(), ex);
			}
		} 
		return null;
	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		System.out.println(new PersonRelatedWorkFromInterface().getPersonRelatedEpisode("林正英"));
	}
}
