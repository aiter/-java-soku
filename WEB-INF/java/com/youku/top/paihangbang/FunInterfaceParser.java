package com.youku.top.paihangbang;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.search.util.DataFormat;
import com.youku.top.MyUtil;
import com.youku.top.UrlUtils;
import com.youku.top.util.TopWordType.WordType;
import com.youku.soku.library.Utils;
import com.youku.soku.top.mapping.TopWords;

public class FunInterfaceParser {
	
	static Logger logger = Logger.getLogger(FunInterfaceParser.class);
	private static final String[] soku_ipaddress = {"10.103.8.131","10.103.8.132","10.103.8.133","10.103.8.134"};
//	public static final String url = "http://10.101.9.2/tools/outsitePlayerTopVideo.php?limit=500&cate=94";
	public static final String url = "http://10.103.9.11/tools/outsitePlayerTopVideo.php?limit=500&cate=94";
	public static boolean isRun = false;
	
	private static FunInterfaceParser instance = null;
	
	
	private FunInterfaceParser() {
		super();
	}

	public static synchronized FunInterfaceParser getInstance() {
		if(null==instance)
			instance = new FunInterfaceParser();
		return instance;
	}
	
	public List<TopWords> parse(String date) throws Exception{
		
		if(isRun){
			throw new Exception("搞笑榜单上一次生成还没有结束");
		}
		isRun = true;
		List<TopWords> tws = new ArrayList<TopWords>();
		try {
			Date d = DataFormat.parseUtilDate(date, DataFormat.FMT_DATE_YYYY_MM_DD);
			String fundate = DataFormat.formatDate(d, DataFormat.FMT_DATE_YYYYMMDD);
			JSONObject alljson = Utils.requestGet(url+"&date="+fundate);
			if(null==alljson) return tws;
			JSONObject json = alljson.getJSONObject(fundate);
			if(null==json) return tws;
			Iterator iterator = json.keys();
			if(null==iterator) return tws;
			JSONObject jobject = null;
			int vid = 0;
			TopWords tw = null;
			logger.info("搞笑分类取得数目,size:"+json.length());
			while(iterator.hasNext()){
				vid = DataFormat.parseInt(iterator.next(),0);
				if(0==vid) continue;
				jobject = json.getJSONObject(""+vid);
				if(null==json) continue;
				tw = new TopWords();
				tw.setKeyword(jobject.getString("title"));
				tw.setQueryCount(DataFormat.parseInt(jobject.getString("vv")));
				tw.setCate(WordType.搞笑.getValue());
				tw.setTopDate(d);
				tw.setIstop(0);
				tw.setUrl(Utils.buildYoukuUrl(MyUtil.encodeVideoId(vid)));
				tw.setPic(picGetByUrl(tw.getKeyword()));
				tws.add(tw);
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
		return tws;
	}
	
	private String picGetByUrl(final String word){
		
		String pic = null;
		JSONObject json = null;
		String url = null;
		int block = 0;
		while(null==json&&block<2){
			block++;
			String addr = soku_ipaddress[new Random().nextInt(4)];
			url = "http://"+addr+"/9/5/2/7/search?keyword="+com.youku.search.util.StringUtil.urlEncode(word, "utf-8");
			logger.debug(url);
			json = Utils.requestGet(url);
			if(null==json)
				UrlUtils.sleep();
		}
		if(null!=json){
			if(!json.isNull("items")){
				JSONArray jsarr = json.optJSONArray("items");
				if(null!=jsarr&&jsarr.length()>0){
					for(int i = 0;i<jsarr.length();i++){
						JSONObject js = jsarr.optJSONObject(i);
						if(!js.isNull("logo")){
							pic = js.optString("logo");
							if(!StringUtils.isBlank(pic)&&!pic.endsWith(".ykimg.com/NA")){
								String p = StringUtils.substringAfterLast(pic, ".ykimg.com/");
								if(!StringUtils.isBlank(p))
									return p;
								else
									return pic;
							}
						}
					}
				}
			}
		}
		logger.info("没有找到图片,word:"+word+",url:"+url+",json:"+json);
		return null;
	}
	
	public int buildFun(String date){
		int count = 0;
		try {
			logger.info("搞笑榜单生成开始");
			List<TopWords> tws = parse(date);
			logger.info("搞笑榜单数据取得,size:"+tws.size());
			for(TopWords tw:tws){
				count += TopWordsMgt.getInstance().topWordSave(tw);
			}
			logger.info("搞笑榜单生成结束,size:"+count);
		} catch (Exception e) {
			logger.error(e);
		}
		return count;
	}
	
	public static void main(String[] args){
		String date = null;
		if(null!=args&&args.length==1&&args[0].matches("\\d{4}_\\d{2}_\\d{2}"))
			date = args[0];
		if(StringUtils.isBlank(date))
			date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
		int c = FunInterfaceParser.getInstance().buildFun(date);
		if(c>400){
			TopDateMgt.getInstance().topDateSave(date, 0, "auto","fun");
		}
	}
}
