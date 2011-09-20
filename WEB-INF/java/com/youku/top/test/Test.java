package com.youku.top.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.UrlUtils;
import com.youku.top.data_source.NewSokuLibraryDataSource;
import com.youku.top.data_source.NewSokuTopDataSource;
import com.youku.top.quick.QueryTopWordsMerger;
import com.youku.top.util.LogUtil;
import com.youku.top.util.TopWordType.WordType;

public class Test {
	
	static Logger logger = Logger.getLogger(Test.class);
	public static JdbcTemplate newSokuTopDataSource = new JdbcTemplate(
			NewSokuTopDataSource.INSTANCE);
	public static JdbcTemplate newSokuLibraryDataSource = new JdbcTemplate(
			NewSokuLibraryDataSource.INSTANCE);
	
	public static List<Integer> lines = new ArrayList<Integer>();
	static{
		
		try {
			List<String> ls =  FileUtils.readLines(new File("/opt/log_analyze/h.txt"));
			for(String s:ls){
				lines.add(DataFormat.parseInt(s));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		try {
			LogUtil.init(Level.INFO,"/opt/log_analyze/soku_top/logt.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String,List<Info>> map = parseAll();
		
		try {
			write2Excel(new FileOutputStream("/opt/log_analyze/nohaibao.xls"), map);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void write2Excel(OutputStream os,Map<String,List<Info>> map){
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = null;
			WritableFont writefont = new WritableFont(WritableFont.ARIAL, 14,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.GREEN);
			WritableCellFormat writecellfont = new WritableCellFormat(writefont);
			WritableFont writefontkeyword = new WritableFont(WritableFont.ARIAL, 12,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat writecellfontkeyword = new WritableCellFormat(writefontkeyword);
			int i = 0;
			for(Entry<String, List<Info>> entry:map.entrySet()){
				sheet = workbook.createSheet(entry.getKey(), i);
				sheet.addCell(new jxl.write.Label(0,0,"官方库ID",writecellfont));
				sheet.addCell(new jxl.write.Label(1,0,"标题",writecellfont));
				sheet.addCell(new jxl.write.Label(2,0,"url",writecellfont));
				sheet.addCell(new jxl.write.Label(3,0,"年代",writecellfont));
				sheet.addCell(new jxl.write.Label(4,0,"地区",writecellfont));
				sheet.addCell(new jxl.write.Label(5,0,"主演",writecellfont));
				int j = 1;
				int k = 0;
				for(Info rv:entry.getValue()){
					
					System.out.println(++k+","+rv.toString());
					
					sheet.addCell(new jxl.write.Number(0,j,rv.content_id,writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(1,j,rv.getTitle(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(2,j,rv.getUrl(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Number(3,j,rv.getYear(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(4,j,rv.getArea(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(5,j,rv.getActors(),writecellfontkeyword));
					j++;
				}
				i++;
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	public static Map<String,List<Info>> parse(){
		Map<String,List<Info>> map = new HashMap<String,List<Info>>();
		List<Integer> teleplays = getProgrammeId(1);
		List<Info> teleplaysInfo = new ArrayList<Info>();
		for(int tid:teleplays){
			
			List<Info> infos = getNoVpicInfo(tid, 1);
			if(null!=infos&&infos.size()>0)
				teleplaysInfo.addAll(infos);
		}
		List<Integer> movies = getProgrammeId(2);
		List<Info> moviesInfo = new ArrayList<Info>();
		for(int mid:movies){
			List<Info> infos = getNoVpicInfo(mid, 2);
			if(null!=infos&&infos.size()>0)
				moviesInfo.addAll(infos);
		}
		map.put(WordType.电视剧.name(), teleplaysInfo);
		map.put(WordType.电影.name(), moviesInfo);
		return map;
	}
	
	public static Map<String,List<Info>> parseAll(){
		Map<String,List<Info>> map = new HashMap<String,List<Info>>();
		Map<String,Integer> querymap = QueryTopWordsMerger.getInstance().getQueryMap("2011_05_18");
		List<Integer> teleplays = getProgrammeIdFromProgramme(1);
		List<Info> teleplaysInfo = new ArrayList<Info>();
		for(int tid:teleplays){
			
			List<Info> infos = getNoVpicInfo(tid, 1,querymap);
			if(null!=infos&&infos.size()>0)
				teleplaysInfo.addAll(infos);
		}
		logger.info("teleplays size:"+teleplaysInfo.size());
		Collections.sort(teleplaysInfo,new InfoDescComparator());
		logger.info("teleplays sort complete");
		List<Integer> movies = getProgrammeIdFromProgramme(2);
		List<Info> moviesInfo = new ArrayList<Info>();
		for(int mid:movies){
			List<Info> infos = getNoVpicInfo(mid, 2,querymap);
			if(null!=infos&&infos.size()>0)
				moviesInfo.addAll(infos);
		}
		logger.info("movies size:"+moviesInfo.size());
		Collections.sort(moviesInfo,new InfoDescComparator());
		logger.info("movies sort complete");
//		List<Integer> varietys = getProgrammeIdFromProgramme(3);
//		List<Info> varietysInfo = new ArrayList<Info>();
//		for(int vid:varietys){
//			List<Info> infos = getNoVpicInfo(vid, 3,querymap);
//			if(null!=infos&&infos.size()>0)
//				varietysInfo.addAll(infos);
//		}
//		logger.info("varietys size:"+varietysInfo.size());
//		Collections.sort(varietysInfo,new InfoDescComparator());
//		logger.info("varietys sort complete");
//		List<Integer> animes = getProgrammeIdFromProgramme(5);
//		List<Info> animesInfo = new ArrayList<Info>();
//		for(int aid:animes){
//			List<Info> infos = getNoVpicInfo(aid, 5,querymap);
//			if(null!=infos&&infos.size()>0)
//				animesInfo.addAll(infos);
//		}
//		logger.info("animes size:"+animesInfo.size());
//		Collections.sort(animesInfo,new InfoDescComparator());
//		logger.info("animes sort complete");
		
		map.put(WordType.电视剧.name(), teleplaysInfo);
		map.put(WordType.电影.name(), moviesInfo);
//		map.put(WordType.综艺.name(), varietysInfo);
//		map.put(WordType.动漫.name(), animesInfo);
		return map;
	}
	
	public static List<Integer> getProgrammeId(int cate){
		String sql = "select distinct(fk_programme_id) as fk_programme_id  from rankinfo_2011_05_12 where fk_programme_id >0  and fk_cate_id="+cate;
		List<Integer> result = new ArrayList<Integer>();
		try{
			List list = newSokuTopDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			int programmeid = 0;
			Map map = null;
			while(iterator.hasNext()){
				map = (Map)iterator.next();
				programmeid = DataFormat.parseInt(map.get("fk_programme_id"));
				if(programmeid>0)
					result.add(programmeid);
			}
		}catch(Exception e){
			logger.error(sql,e);
		}
		logger.info("cate:"+cate+",result.size:"+result.size());
		return result;
	}
	
	public static List<Integer> getProgrammeIdFromProgramme(int cate){
		String sql = "select distinct(programme.id) as id from programme,programme_site,programme_episode where programme.cate="+cate+" and programme.blocked=0 and programme.state='normal' and programme.id=programme_site.fk_programme_id and programme_episode.fk_programme_site_id=programme_site.id and programme_episode.url is not null and length(programme_episode.url)>1";
		List<Integer> result = new ArrayList<Integer>();
		try{
			List list = newSokuLibraryDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			int programmeid = 0;
			Map map = null;
			while(iterator.hasNext()){
				map = (Map)iterator.next();
				programmeid = DataFormat.parseInt(map.get("id"));
				if(programmeid>0)
					result.add(programmeid);
			}
		}catch(Exception e){
			logger.error(sql,e);
		}
		logger.info("cate:"+cate+",result.size:"+result.size());
		return result;
	}
	
	public static List<Info> getNoVpicInfo(int ids,int cate){
		return getNoVpicInfo(ids, cate, new HashMap<String, Integer>());
	}
	
	public static List<Info> getNoVpicInfo(int ids,int cate,Map<String,Integer> querymap){
		String url = "http://10.103.8.217/top/search?programmeId="+ids+"&cate="+cate;
		JSONObject jsonObject = null;
		int blocked = 0;
		while (null == jsonObject && blocked < 3) {
			blocked++;
			jsonObject = Utils.requestGet(url);
			if (null == jsonObject)
				UrlUtils.sleep();
		}
		List<Info> infos = new ArrayList<Info>();
		Info info = null;
		if (null != jsonObject) {
			JSONArray jarr = jsonObject.optJSONArray("array");
			if (null != jarr) {
				for(int i=0;i<jarr.length();i++){
				JSONObject json = jarr.optJSONObject(i);
				if (null != json) {
					JSONObject programme = json.optJSONObject("programme");
					if (null != programme) {
						String playurl = programme.optString("url");
						boolean hasUrl = true;
						if (StringUtils.isBlank(playurl)
								|| playurl.trim().toLowerCase().equalsIgnoreCase(
										"null")) {
							hasUrl = false;
						}
						boolean haihaibao = true;
						String haibao = programme.optString("vpic");
						if (StringUtils.isBlank(haibao)
								|| haibao.trim().toLowerCase().equalsIgnoreCase(
										"null")||haibao.contains("0900641F464A7BBE7400000000000000000000-0000-0000-0000-00005B2F7B0E")) {
							haihaibao = false;
						}
						if(hasUrl&&!haihaibao){
							info = new Info();
							info.content_id = programme.optInt("contentId");
							if(lines.contains(info.content_id))
								continue;
							infos.add(info);
							info.title = programme.optString("name");
							info.count = DataFormat.parseInt(querymap.get(info.title));
							info.url = playurl;
							info.year = DataFormat.parseInt(programme
									.optString("releaseyear"));
							JSONArray arr = programme.optJSONArray("performer");
							if(null!=arr){
								StringBuilder sb = new StringBuilder();
								for(int j=0;j<arr.length();j++){
									if(j!=0)
										sb.append("|");
									sb.append(arr.optJSONObject(j).optString("name"));
								}
								if(sb.length()>0)
									info.actors = sb.toString();
							}
							JSONObject midd = json.optJSONObject("midd");
							if (null != midd) {
								JSONArray area_arr = midd.optJSONArray("area");
								info.area = Utils.parseToStr(area_arr);
							}
						}
					}
				}
			}
				}
			}
		return infos;
	}
	
}
