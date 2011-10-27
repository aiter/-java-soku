package com.youku.soku.newext.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.soku.library.load.Programme;
import com.youku.soku.newext.info.AliasInfo;
import com.youku.soku.newext.info.AnimeInfo;
import com.youku.soku.newext.info.ExtInfoHolder;
import com.youku.soku.newext.info.MovieInfo;
import com.youku.soku.newext.info.PersonInfo;
import com.youku.soku.newext.info.TeleplayInfo;
import com.youku.soku.newext.info.VarietyInfo;
import com.youku.soku.newext.util.ChannelType;

public class UpdateRecommendation {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private static final String RELATED_SHOW_HOST = "http://10.103.20.101/api_getShowRelated?pid=XMjAwMA==&pl=2&showid=";
	
	public void doUpdate() {
		logger.info("开始");
		Cost cost = new Cost();
		AliasInfo aliasInfo = ExtInfoHolder.getCurrentThreadLocal().aliasInfo;
		
		MovieInfo movieInfo = ExtInfoHolder.getCurrentThreadLocal().movieInfo;
		TeleplayInfo teleplayInfo = ExtInfoHolder.getCurrentThreadLocal().teleplayInfo;
		AnimeInfo animeInfo = ExtInfoHolder.getCurrentThreadLocal().animeInfo;
		VarietyInfo varietyInfo = ExtInfoHolder.getCurrentThreadLocal().varietyInfo;
		PersonInfo personInfo = null;  //设置为null， 防止更新相关节目时更新人物信息。
		
		List<Programme> programmeList = new ArrayList<Programme>(aliasInfo.id_programme.values());
		
		Map<Integer, String> relatedShowMap = new RelatedShowLoaderAndSaver().load();
		
		updateSeriesMap(teleplayInfo, relatedShowMap);
		updateSeriesMap(movieInfo, relatedShowMap);
		updateSeriesMap(animeInfo, relatedShowMap);
		updateSeriesMap(varietyInfo, relatedShowMap);
		
		for(Programme programme : programmeList) {
			
			try {
				programme.setRecommendation(relatedShowMap.get(programme.getContentId()).toString());
				
				if (programme.getCate() == ChannelType.MOVIE
						.getValue()) {
					UpdateProgramme.doUpdate(programme, movieInfo,
							personInfo);

				} else if (programme.getCate() == ChannelType.TELEPLAY
						.getValue()) {
					UpdateProgramme.doUpdate(programme, teleplayInfo,
							personInfo);

				} else if (programme.getCate() == ChannelType.ANIME
						.getValue()) {
					UpdateProgramme.doUpdate(programme, animeInfo,
							personInfo);

				} else if (programme.getCate() == ChannelType.VARIETY
						.getValue()) {
					UpdateProgramme.doUpdate(programme, varietyInfo,
							personInfo);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			aliasInfo.id_programme.put(programme.getId(), programme);
		}
	
		cost.updateEnd();
		
		logger.info("更新Recommendation 结束.  耗时: " + cost.getCost());
	}
	
	private void updateSeriesMap(AliasInfo info, Map<Integer, String> relatedShowMap) {
		for(String nameKey : info.getSeries_programme().keySet()) {
			
			logger.info("update series: " + nameKey);
			List<Programme> proList = info.getSeries_programme().get(nameKey);
			for(Programme p : proList) {
				p.setRecommendation(relatedShowMap.get(p.getContentId()).toString());
			}
		}
	}
	
	private JSONArray getRelatedShow(int showId) throws Exception {
		//byte[] bytes = Wget.get(RELATED_SHOW_HOST + showId, 3000);
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(RELATED_SHOW_HOST + showId);
		method.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)");
		client.executeMethod(method);
		JSONObject jsonObject = new JSONObject(method.getResponseBodyAsString());
		
		return jsonObject.optJSONArray("results");
	}

	
	public static void main(String[] args) throws Exception {
		System.out.println(new UpdateRecommendation().getRelatedShow(13339));
	}
}
