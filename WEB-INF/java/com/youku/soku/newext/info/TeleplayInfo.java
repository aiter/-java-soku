package com.youku.soku.newext.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.newext.info.TypeConstant.NameType;


/**
 * 电视剧
 */
public class TeleplayInfo extends AliasInfo {

	
	public TeleplayInfo(AliasInfo aliasInfo) {
		super(NameType.TELEPLAY);
		this.programme_programmeSite=aliasInfo.programme_programmeSite;
		this.programme_series=aliasInfo.programme_series;
		this.id_programme=aliasInfo.id_programme;
		this.programmeSite_episode=aliasInfo.programmeSite_episode;
		this.middMap=aliasInfo.middMap;
	}

	private static final long serialVersionUID = 1L;

//系列的名称，别名-------------List<Programme>
	public Map<String ,List<Programme>> series_programme=new HashMap<String,List<Programme>>();
	
//	 (名称 + 版本) | (搜索词) 为key   (给前端提供搜索服务的map)
	public Map<String, List<Programme>> name_programme = new HashMap<String, List<Programme>>();
//	结果map
//    public Map<ProgrammeSite,List<ProgrammeEpisode>> programmeSite_episode=new HashMap<ProgrammeSite,List<ProgrammeEpisode>>(100000);



	public String info() {
		return "{TeleplayInfo:: name_programme's size: " + name_programme.size() + "; "+
				"series_programme's size:"+series_programme.size()+"}";
	}
	
	public void destroy() {
		id_programme.clear();
		programme_series.clear();
		for(List<ProgrammeSite> list:programme_programmeSite.values()){
			list.clear();
		}
		programme_programmeSite.clear();
		
		for(List<ProgrammeEpisode> list:programmeSite_episode.values()){
			list.clear();
		}
		programme_programmeSite.clear();
		middMap.clear();
		
		for(List<Programme> list:series_programme.values()){
			list.clear();
		}
		series_programme.clear();
		
		for(List<Programme> list:name_programme.values()){
			list.clear();
		}
		name_programme.clear();
	}

}
