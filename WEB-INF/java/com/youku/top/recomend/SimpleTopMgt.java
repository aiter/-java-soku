//package com.youku.top.recomend;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.commons.lang.StringUtils;
//
//import com.youku.search.util.DataFormat;
//import com.youku.top.JdbcTemplateFactoray;
//import com.youku.top.util.VideoType;
//
//public class SimpleTopMgt {
//	public static Map<String, String> getTopDate() {
//		String sql = "select top_date,fk_channel_name from top_date where top_date is not null and length(top_date)>0";
//		Map<String, String> result = new HashMap<String, String>();
//		try {
//			List list = JdbcTemplateFactoray.sokuTopDataSource
//					.queryForList(sql);
//			Iterator it = list.iterator();
//			Map map = null;
//			String channel = null;
//			String top_date = null;
//			while (it.hasNext()) {
//				map = (Map) (it.next());
//				channel = ("" + map.get("fk_channel_name")).trim();
//				top_date = ("" + map.get("top_date")).trim();
//				if (StringUtils.isBlank(channel)
//						|| StringUtils.isBlank(top_date)
//						|| !top_date.matches("\\d{4}-\\d{2}-\\d{2}"))
//					continue;
//				result.put(channel, top_date);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//	
//	public static List<Entity> getChannelTopData(String channel,String top_date){
//		String sql = "select keyword,union_searchs from top_words where visible=1 and fk_channel_name='"+channel+"' and top_date='"+top_date+"' order by order_number limit 200";
//		List<Entity> entitys  = new ArrayList<Entity>();
//		try {
//			List list = JdbcTemplateFactoray.sokuTopDataSource
//					.queryForList(sql);
//			Iterator it = list.iterator();
//			Map map = null;
//			String name = null;
//			int union_searchs = 0;
//			Set<String> names = new HashSet<String>();
//			while (it.hasNext()) {
//				map = (Map) (it.next());
//				name = ("" + map.get("keyword")).trim();
//				union_searchs = DataFormat.parseInt(map.get("union_searchs"));
//				if (StringUtils.isBlank(name))
//					continue;
//				if(union_searchs>10)
//					union_searchs = union_searchs/10;
//				if(!names.contains(name)){
//					entitys.add(new Entity(name,union_searchs,union_searchs,com.youku.search.hanyupinyin.Converter.convert(name, true),channel2Cate(channel)));
//					names.add(name);
//				}
//				if(names.size()>=50)
//					break;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return entitys;
//	}
//	
//	public static List<Entity> getDirectoryChannelTopData(int cate,String top_date){
//		String date = top_date.replaceAll("-", "_");
//		String sql = "select name,search_nums from directory_info_"+date+" where visible=1 and fk_cate_id="+cate+" order by order_number limit 200";
//		List<Entity> entitys  = new ArrayList<Entity>();
//		try {
//			List list = JdbcTemplateFactoray.sokuTopDataSource
//					.queryForList(sql);
//			Iterator it = list.iterator();
//			Map map = null;
//			String name = null;
//			int union_searchs = 0;
//			Set<String> names = new HashSet<String>();
//			while (it.hasNext()) {
//				map = (Map) (it.next());
//				name = ("" + map.get("name")).trim();
//				union_searchs = DataFormat.parseInt(map.get("search_nums"));
//				if (StringUtils.isBlank(name))
//					continue;
//				if(union_searchs>10)
//					union_searchs = union_searchs/10;
//				if(!names.contains(name)){
//					entitys.add(new Entity(name,union_searchs,union_searchs,com.youku.search.hanyupinyin.Converter.convert(name, true),cate));
//					names.add(name);
//				}
//				if(names.size()>=50)
//					break;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return entitys;
//	}
//	
//	public static List<Entity> getPersonTopData(String top_date){
//		String date = top_date.replaceAll("-", "_");
//		String sql = "select name,search_nums from directory_info_person_"+date+" where visible=1 order by order_number limit 200";
//		List<Entity> entitys  = new ArrayList<Entity>();
//		try {
//			List list = JdbcTemplateFactoray.sokuTopDataSource
//					.queryForList(sql);
//			Iterator it = list.iterator();
//			Map map = null;
//			String name = null;
//			int union_searchs = 0;
//			Set<String> names = new HashSet<String>();
//			while (it.hasNext()) {
//				map = (Map) (it.next());
//				name = ("" + map.get("name")).trim();
//				union_searchs = DataFormat.parseInt(map.get("search_nums"));
//				if (StringUtils.isBlank(name))
//					continue;
//				if(union_searchs>10)
//					union_searchs = union_searchs/10;
//				if(!names.contains(name)){
//					entitys.add(new Entity(name,union_searchs,union_searchs,com.youku.search.hanyupinyin.Converter.convert(name, true),VideoType.person.getValue()));
//					names.add(name);
//				}
//				if(names.size()>=50)
//					break;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return entitys;
//	}
//	
//	private static int channel2Cate(String channel){
//		if(channel.equalsIgnoreCase(VideoType.teleplay.name()))
//			return VideoType.teleplay.getValue();
//		if(channel.equalsIgnoreCase(VideoType.movie.name()))
//			return VideoType.movie.getValue();
//		if(channel.equalsIgnoreCase(VideoType.variety.name()))
//			return VideoType.variety.getValue();
//		if(channel.equalsIgnoreCase(VideoType.anime.name()))
//			return VideoType.anime.getValue();
//		if(channel.equalsIgnoreCase(VideoType.music.name()))
//			return VideoType.music.getValue();
//		if(channel.equalsIgnoreCase(VideoType.person.name()))
//			return VideoType.person.getValue();
//		if(channel.equalsIgnoreCase(VideoType.sports.name()))
//			return VideoType.sports.getValue();
//		if(channel.equalsIgnoreCase(VideoType.science.name()))
//			return VideoType.science.getValue();
//		return 0;
//	}
//}
