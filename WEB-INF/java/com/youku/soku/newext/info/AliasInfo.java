package com.youku.soku.newext.info;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.Series;

/**
 * 某一类别下的别名信息
 */
public class AliasInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Log logger = LogFactory.getLog(getClass());
	
//	Map<programme,Series>
	public  Map<Integer,Programme> id_programme=new HashMap<Integer,Programme>();
	
//	Map<programme,Series>
	public  Map<Programme,Series> programme_series=new HashMap<Programme,Series>();
	
//	Map<Programme,ProgrammeSite>
	public  Map<Programme,List<ProgrammeSite>>  programme_programmeSite=new HashMap<Programme,List<ProgrammeSite>>();
	
//	Map<ProgrammeSite,Programme>
//	public  Map<ProgrammeSite, Programme> programmeSite_programme=new HashMap<ProgrammeSite,Programme>(100000) ;
	
	//站点-->对应视频列表
	public  Map<ProgrammeSite, List<ProgrammeEpisode>> programmeSite_episode = new HashMap<ProgrammeSite, List<ProgrammeEpisode>>();
	
//	中间层资源map  key: content_id 
	public Map<Integer,String> middMap=new HashMap<Integer,String>();
	



	public int cate_id;

	public AliasInfo(int cate_id) {
		this.cate_id = cate_id;
	}
	
	public AliasInfo(){};
	
	public String info() {
		return "{AliasInfo:: programme_series's size: " + programme_series.size() + "; "
				+" id_programme's size:"+id_programme.size()+"  ;"
				+" programme_programmeSite's size:"+ programme_programmeSite.size()+"  ;"
				+" programmeSite_episode's size:"+programmeSite_episode.size()+"  ;"
				+" middMap's size: "+middMap.size();
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
	}

}
