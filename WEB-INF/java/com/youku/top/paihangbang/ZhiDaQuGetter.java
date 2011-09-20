package com.youku.top.paihangbang;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.search.util.DataFormat;
import com.youku.top.data_source.NewSokuLibraryDataSource;
import com.youku.top.paihangbang.entity.TopWordsEntity;
import com.youku.top.util.TopWordType.WordType;

public class ZhiDaQuGetter {
	
	static Logger logger = Logger.getLogger(ZhiDaQuGetter.class);
	private static ZhiDaQuGetter instance = null;
	public JdbcTemplate newSokuLibraryDataSource = new JdbcTemplate(
			NewSokuLibraryDataSource.INSTANCE);
	
	private ZhiDaQuGetter() {
		super();
	}

	public static synchronized ZhiDaQuGetter getInstance() {
		if(null==instance)
			instance = new ZhiDaQuGetter();
		return instance;
	}
	
//	public void programmeGetter(TopWordsEntity tw){
//		if(tw.getTopwords().getCate() != WordType.综艺.getValue())
//			programmeNoVarietyGetter(tw);
//		//TODO 临时去直达区对应，不保存到type_words
////		else
////			seriesGetter(tw);
//	}
	
//	private void programmeNoVarietyGetter(TopWordsEntity tw){
//		int content_id = 0; 
//		if(tw.getTopwords().getProgrammeId()>0)
//			content_id = programmeContentIdGetter(tw.getTopwords().getProgrammeId());
//		if(content_id==0){
//			
//			//TODO 临时去直达区对应，不保存到type_words
////			programmeIdAndContentGetter(tw);
//		}else
//			tw.setContent_id(content_id);
//	}
//	
//	private int programmeContentIdGetter(int id){
//		String sql="select programme.content_id as content_id from programme where programme.id=? ";
//		try{
//			return JdbcTemplateFactoray.newSokuLibraryDataSource.queryForInt(sql,new Object[]{id},new int[]{Types.INTEGER});
//		}catch(Exception e){
//			logger.error("sql:"+sql+",id:"+id, e);
//		}
//		return 0;
//	}
	
//	private void programmeIdAndContentGetter(TopWordsEntity tw){
//		final String keyword = tw.getTopwords().getKeyword();
//		final int cate = tw.getTopwords().getCate();
//		String sql="select programme.id as id,programme.content_id as content_id from programme,programme_site,programme_episode where programme.name=? and programme.cate=? and programme.blocked=0 and programme.state='normal' and programme.id=programme_site.fk_programme_id and programme_episode.fk_programme_site_id=programme_site.id and programme_episode.url is not null and length(programme_episode.url)>1 limit 1";
//		try{
//			Map map = JdbcTemplateFactoray.newSokuLibraryDataSource.queryForMap(sql,new Object[]{keyword,cate},new int[]{Types.VARCHAR,Types.INTEGER});
//			int id = DataFormat.parseInt(map.get("id"));
////			if(0==id) return null;
//			int content_id = DataFormat.parseInt(map.get("content_id"));
////			if(0==content_id) return null;
//			tw.getTopwords().setProgrammeId(id);
//			tw.setContent_id(content_id);
//		}catch(Exception e){
//			logger.error("sql:"+sql+",name:"+keyword+",cate:"+cate, e);
//		}
//	}
	
	private void seriesGetter(TopWordsEntity tw){
		String sql="select id from series where name = ? and cate=? limit 1";
		final String keyword = tw.getTopwords().getKeyword();
		final int cate = tw.getTopwords().getCate();
		try{
			int id = newSokuLibraryDataSource.queryForInt(sql,new Object[]{keyword,cate},new int[]{Types.VARCHAR,Types.INTEGER});
			if(0==id) return;
			tw.getTopwords().setProgrammeId(id);
		}catch(Exception e){
			logger.error("sql:"+sql+",name:"+keyword+",cate:"+cate, e);
		}
	}
	
	private int programmeIdNoVarietyGetter(final String keyword,final int cate){
		String sql="select programme.id as id from programme,programme_site,programme_episode where programme.name=? and programme.cate=? and programme.blocked=0 and programme.state='normal' and programme.id=programme_site.fk_programme_id and programme_episode.fk_programme_site_id=programme_site.id and programme_episode.url is not null and length(programme_episode.url)>1 limit 1";
		try{
			return newSokuLibraryDataSource.queryForInt(sql,new Object[]{keyword,cate},new int[]{Types.VARCHAR,Types.INTEGER});
		}catch(Exception e){
			logger.error("sql:"+sql+",name:"+keyword+",cate:"+cate, e);
		}
		return 0;
	}
	
	private int seriesIdGetter(final String keyword,final int cate){
		String sql="select id from series where name = ? and cate=? limit 1";
		try{
			return newSokuLibraryDataSource.queryForInt(sql,new Object[]{keyword,cate},new int[]{Types.VARCHAR,Types.INTEGER});
		}catch(Exception e){
			logger.error("sql:"+sql+",name:"+keyword+",cate:"+cate, e);
		}
		return 0;
	}
	
	public int programmeIdGetter(final String keyword,final int cate){
		if(cate == WordType.综艺.getValue())
			return seriesIdGetter(keyword, cate);
		else
			return programmeIdNoVarietyGetter(keyword, cate);
	}
	
	public Map<Integer,Map<String,Integer>> programmeIdNoVarietyGetter(){
		Map<Integer,Map<String,Integer>> result = new HashMap<Integer, Map<String,Integer>>();
		String sql="select distinct programme.id as id,programme.cate as cate,programme.name as name from programme,programme_site,programme_episode where programme.cate!=3 and programme.blocked=0 and programme.state='normal' and programme.id=programme_site.fk_programme_id and programme_episode.fk_programme_site_id=programme_site.id and programme_episode.url is not null and length(programme_episode.url)>1";
		try{
			List list= newSokuLibraryDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			Map map = null;
			while(iterator.hasNext()){
				map = (Map)iterator.next();
				int id = DataFormat.parseInt(map.get("id"));
				int cate = DataFormat.parseInt(map.get("cate"));
				String name = String.valueOf(map.get("name"));
				if(0==id)
					continue;
				if(0==cate)
					continue;
				if(StringUtils.isBlank(name)||name.trim().toLowerCase().equalsIgnoreCase("null"))
					continue;
				name = name.trim();
				if(null==result.get(cate))
					result.put(cate, new HashMap<String,Integer>());
				if(null==result.get(cate).get(name))
					result.get(cate).put(name, id);
			}
		}catch(Exception e){
			logger.error("sql:"+sql, e);
		}
		return result;
	}
	
	public Map<Integer,Map<String,Integer>> seriesIdGetter(){
		Map<Integer,Map<String,Integer>> result = new HashMap<Integer, Map<String,Integer>>();
		String sql="select id,name,cate from series";
		try{
			List list= newSokuLibraryDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			Map map = null;
			while(iterator.hasNext()){
				map = (Map)iterator.next();
				int id = DataFormat.parseInt(map.get("id"));
				int cate = DataFormat.parseInt(map.get("cate"));
				String name = String.valueOf(map.get("name"));
				if(0==id)
					continue;
				if(0==cate)
					continue;
				if(StringUtils.isBlank(name)||name.trim().toLowerCase().equalsIgnoreCase("null"))
					continue;
				name = name.trim();
				if(null==result.get(cate))
					result.put(cate, new HashMap<String,Integer>());
				if(null==result.get(cate).get(name))
					result.get(cate).put(name, id);
			}
		}catch(Exception e){
			logger.error("sql:"+sql, e);
		}
		return result;
	}
}
