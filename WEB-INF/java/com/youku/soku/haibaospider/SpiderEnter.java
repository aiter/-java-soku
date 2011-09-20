package com.youku.soku.haibaospider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.top.util.TopWordType.WordType;

public class SpiderEnter {
	private static Logger logger = Logger.getLogger(SpiderEnter.class);
	private static SpiderEnter instance = null;
	private SpiderEnter() {
		super();
	}

	public static synchronized SpiderEnter getInstance() {
		if(null==instance)
			instance = new SpiderEnter();
		return instance;
	}
	
	public Map<String,Integer> getQueryMap(){
		Map<String,Integer> map = null;
		try {
			String res = SpiderUtils.getInstance().getHttpResponseHtml("http://10.103.8.225/index/filedownload", "utf-8");
			List<String> list = Utils.parseStr2List(res, "\\\n");
			if(null==list) return null;
			map = list2Map(list, "\\\t");
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String,Integer> list2Map(List<String> list,String split){
		Map<String,Integer> map = new HashMap<String, Integer>();
		if(null==list||list.size()<1) return map;
		String[] arr = null;
		int query_count = 0;
		String keyword = null;
		for(String str:list){
			if(StringUtils.isBlank(str))
				continue;
			arr = str.split(split);
			if(null==arr||arr.length!=2)
				continue;
			query_count = DataFormat.parseInt(arr[1]);
			if(query_count<1) continue;
			keyword = arr[0];
			if(StringUtils.isBlank(keyword)) continue;
			map.put(keyword.trim(), query_count);
		}
		return map;
	}
	
	public List<ResultVO> spiderMovieData(Map<String,Integer> querys){
		final Map<String,List<MidVO>> map = MidDataGetter.getInstance().movieGetter();
		List<ResultVO> result = new ArrayList<ResultVO>();
		System.out.println("movie:"+map.size());
		List<ResultVO> resvos = new ArrayList<ResultVO>();
		for(Entry<String,List<MidVO>> entry:map.entrySet()){
			ResultVO rev = new ResultVO();
			rev.title = entry.getKey();
			rev.queryCount = DataFormat.parseInt(querys.get(entry.getKey()));
			resvos.add(rev);
		}
		Collections.sort(resvos,new QueryDescComparator());
		
//		int size=resvos.size()>2500?2500:resvos.size();
		final List<ResultVO> list1 = resvos.subList(0, 2000);
//		final List<ResultVO> list2 = resvos.subList(501, 1000);
//		final List<ResultVO> list3 = resvos.subList(1001,1500);
//		final List<ResultVO> list4 = resvos.subList(1501,2000);
//		final List<ResultVO> list5 = resvos.subList(2001,2500);
		
		System.out.println("resvos.size:"+list1.size());
		
		List<String> ress = new ArrayList<String>();
		
		for(ResultVO rev:list1){
			System.out.println(rev.title);
			for(MidVO m:map.get(rev.title)){
				System.out.println("t1:"+m.toString());
				long t = System.currentTimeMillis();
				SpiderUtils.sleep(new Random(200).nextLong());
				List<ResultVO> rs = DoubanDataGetter.dataGetter(m);
				System.out.println("t1:"+rs.size());
				if(null!=rs&&rs.size()>0){
					for(ResultVO r:rs){
						r.queryCount = rev.queryCount;
//								result.add(r);
						System.out.println(r.id+",cost:"+(System.currentTimeMillis()-t));
						ress.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
					}
				}
			}
		}
		try {
			FileUtils.writeLines(new File("/opt/haibao/movie.txt"), ress);
			System.out.println("size:"+ress.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<ResultVO> spiderMovieData1(Map<String,Integer> querys){
		Map<String,List<MidVO>> map = MidDataGetter.getInstance().movieGetter1();
		List<ResultVO> result = new ArrayList<ResultVO>();
		List<ResultVO> rs = null;
		System.out.println("movie:"+map.size());
		List<String> list = new ArrayList<String>();
		List<String> rslist = new ArrayList<String>();
		int query =0;
		int i = 1;
		for(Entry<String,List<MidVO>> entry:map.entrySet()){
			for(MidVO m:entry.getValue()){
				query = 0;
				if(m.dbid<1&&StringUtils.isBlank(m.imdb)){
					query = DataFormat.parseInt(querys.get(m.title));
					if(query<1){
						list.add(m.id+"\t"+m.title);
						System.out.println(m.toString());
						i++;
						continue;
					}
				}
				rs = DoubanDataGetter.getInstance().dataGetter(m);
				if(null!=rs&&rs.size()>0){
					for(ResultVO r:rs){
						r.queryCount = DataFormat.parseInt(querys.get(r.title));
						rslist.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
						result.add(r);
					}
				}
			}
		}
		try {
			FileUtils.writeLines(new File("/opt/nomatch1.txt"), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("no spider:"+i);
		
		try {
			FileUtils.writeLines(new File("/opt/haibao/1.txt"), rslist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public List<ResultVO> spiderMovieData2(Map<String,Integer> querys){
		Map<String,List<MidVO>> map = MidDataGetter.getInstance().movieGetter2();
		List<ResultVO> result = new ArrayList<ResultVO>();
		List<ResultVO> rs = null;
		System.out.println("movie:"+map.size());
		List<String> list = new ArrayList<String>();
		List<String> rslist = new ArrayList<String>();
		int query =0;
		int i = 1;
		for(Entry<String,List<MidVO>> entry:map.entrySet()){
			for(MidVO m:entry.getValue()){
				query = 0;
				if(m.dbid<1&&StringUtils.isBlank(m.imdb)){
					query = DataFormat.parseInt(querys.get(m.title));
					if(query<1){
						list.add(m.id+"\t"+m.title);
						System.out.println(m.toString());
						i++;
						continue;
					}
				}
				rs = DoubanDataGetter.getInstance().dataGetter(m);
				if(null!=rs&&rs.size()>0){
					for(ResultVO r:rs){
						r.queryCount = DataFormat.parseInt(querys.get(r.title));
						rslist.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
						result.add(r);
					}
				}
			}
		}
		try {
			FileUtils.writeLines(new File("/opt/nomatch2.txt"), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			FileUtils.writeLines(new File("/opt/haibao/2.txt"), rslist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("no spider:"+i);
		return result;
	}
	public List<ResultVO> spiderMovieData3(Map<String,Integer> querys){
		Map<String,List<MidVO>> map = MidDataGetter.getInstance().movieGetter3();
		List<ResultVO> result = new ArrayList<ResultVO>();
		List<ResultVO> rs = null;
		System.out.println("movie:"+map.size());
		List<String> list = new ArrayList<String>();
		List<String> rslist = new ArrayList<String>();
		int query =0;
		int i = 1;
		for(Entry<String,List<MidVO>> entry:map.entrySet()){
			for(MidVO m:entry.getValue()){
				query = 0;
				if(m.dbid<1&&StringUtils.isBlank(m.imdb)){
					query = DataFormat.parseInt(querys.get(m.title));
					if(query<1){
						list.add(m.id+"\t"+m.title);
						System.out.println(m.toString());
						i++;
						continue;
					}
				}
				rs = DoubanDataGetter.getInstance().dataGetter(m);
				if(null!=rs&&rs.size()>0){
					for(ResultVO r:rs){
						r.queryCount = DataFormat.parseInt(querys.get(r.title));
						rslist.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
						result.add(r);
					}
				}
			}
		}
		try {
			FileUtils.writeLines(new File("/opt/nomatch3.txt"), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			FileUtils.writeLines(new File("/opt/haibao/3.txt"), rslist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("no spider:"+i);
		return result;
	}
	
	public Map<Integer,ResultVO> parseTxt(){
		Map<Integer,ResultVO> map = new HashMap<Integer, ResultVO>();
		List<String> list = null;
		try {
			list = FileUtils.readLines(new File("C:\\Program Files\\SecureCRT\\download\\haibao.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null==list) return map;
		for(String str:list){
			ResultVO r = new ResultVO();
			String[] aliasArr = str.split("\t");
			if (null != aliasArr) {
				r.id = DataFormat.parseInt(aliasArr[0]);
//				if(r.id == 0)
//					continue;
				r.cate = DataFormat.parseInt(aliasArr[1]);
				r.title = aliasArr[2];
				r.pic = aliasArr[3];
				r.youku_pic = aliasArr[4];
				r.ziliao_url = aliasArr[5];
				r.queryCount = DataFormat.parseInt(aliasArr[6]);
				map.put(r.id, r);
			}
		}
		return map;
	}
	
	public List<ResultVO> parseTeleplay(){
		List<String> list = null;
		try {
			list = FileUtils.readLines(new File("/opt/haibao/teleplay.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null==list) return null;
		List<ResultVO> resvos = new ArrayList<ResultVO>();
		for(String str:list){
			ResultVO r = new ResultVO();
			String[] aliasArr = str.split("\t");
			if (null != aliasArr) {
				r.id = DataFormat.parseInt(aliasArr[0]);
//				if(r.id == 0)
//					continue;
				r.cate = DataFormat.parseInt(aliasArr[1]);
				r.title = aliasArr[2];
				r.pic = aliasArr[3];
				r.youku_pic = aliasArr[4];
				r.ziliao_url = aliasArr[5];
				r.queryCount = DataFormat.parseInt(aliasArr[6]);
				resvos.add(r);
			}
		}
		return resvos;
	}
	
	public List<ResultVO> parseMovie(){
		List<String> list = null;
		try {
			list = FileUtils.readLines(new File("/opt/haibao/movie.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null==list) return null;
		List<ResultVO> resvos = new ArrayList<ResultVO>();
		for(String str:list){
			ResultVO r = new ResultVO();
			String[] aliasArr = str.split("\t");
			if (null != aliasArr) {
				r.id = DataFormat.parseInt(aliasArr[0]);
//				if(r.id == 0)
//					continue;
				r.cate = DataFormat.parseInt(aliasArr[1]);
				r.title = aliasArr[2];
				r.pic = aliasArr[3];
				r.youku_pic = aliasArr[4];
				r.ziliao_url = aliasArr[5];
				r.queryCount = DataFormat.parseInt(aliasArr[6]);
				resvos.add(r);
			}
		}
		return resvos;
	}
	
	public void parse() throws IOException{
//		List<ResultVO> teleplays = parseTeleplay();
//		System.out.println("teleplays:"+teleplays.size());
		List<ResultVO> movies = parseMovie();
		System.out.println("movies:"+movies.size());
		Map<String,List<ResultVO>> map = new HashMap<String, List<ResultVO>>();
//		map.put(WordType.电视剧.name(), teleplays);
		map.put(WordType.电影.name(), movies);
		List<String> list = write2Excel(new FileOutputStream(new File("/opt/haibao/haibao1.xls")), map);
		System.out.println("list:"+list.size());
		FileUtils.writeLines(new File("/opt/haibao/nomatch.txt"), list);
	}
	
	public List<ResultVO> spiderTeleplayData(Map<String,Integer> querys){
		Map<String,List<MidVO>> map = MidDataGetter.getInstance().teleplayGetter();
		List<Hao123VO> haos = Hao123DataGetter.getInstance().teleplayGetter();
		List<ResultVO> result = new ArrayList<ResultVO>();
		List<MidVO> ms = null;
		ResultVO r = null;
		System.out.println("teleplay:"+map.size());
		System.out.println("haos:"+haos.size());
		Map<Integer,ResultVO> tmap = parseTxt();
		System.out.println("tmap:"+tmap.size());
		for(Hao123VO h:haos){
			ms = map.get(h.title);
			if(null!=ms){
				for(MidVO m:ms){
					if(null!=tmap.get(m.id)&&!StringUtils.isBlank(tmap.get(m.id).youku_pic)){
						result.add(tmap.get(m.id));
						continue;
					}
					r = new ResultVO();
					r.id = m.id;
					r.cate = WordType.电视剧.getValue();
					r.pic = h.getPic();
					r.title = m.title;
					r.ziliao_url = h.url;
					r.youku_pic = UploadPic.uploadHaibao(h.pic);
					r.queryCount = DataFormat.parseInt(querys.get(r.title));;
					result.add(r);
				}
			}else{
				r = new ResultVO();
				r.id = 0;
				r.cate = WordType.电视剧.getValue();
				r.pic = h.getPic();
				r.title = h.title;
				r.ziliao_url = h.url;
				r.youku_pic="";
				r.queryCount = DataFormat.parseInt(querys.get(r.title));;
				result.add(r);
			}
		}
		return result;
	}
	
	public List<ResultVO> spiderMovieDataFromHao123(Map<String,Integer> querys){
		Map<String,List<MidVO>> map = MidDataGetter.getInstance().movieGetter();
		List<Hao123VO> haos = Hao123DataGetter.getInstance().movieGetter();
		List<ResultVO> result = new ArrayList<ResultVO>();
		List<MidVO> ms = null;
		ResultVO r = null;
		System.out.println("movies:"+map.size());
		System.out.println("haos:"+haos.size());
		for(Hao123VO h:haos){
			ms = map.get(h.title);
			if(null!=ms){
				String youku_pic = UploadPic.uploadHaibao(h.pic);
				for(MidVO m:ms){
					r = new ResultVO();
					r.id = m.id;
					r.cate = WordType.电影.getValue();
					r.pic = h.getPic();
					r.title = m.title;
					r.ziliao_url = h.url;
					r.youku_pic = youku_pic;
					r.queryCount = DataFormat.parseInt(querys.get(r.title));
					result.add(r);
				}
			}else{
				r = new ResultVO();
				r.id = 0;
				r.cate = WordType.电影.getValue();
				r.pic = h.getPic();
				r.title = h.title;
				r.ziliao_url = h.url;
				r.queryCount = DataFormat.parseInt(querys.get(r.title));
				result.add(r);
			}
		}
		return result;
	}
	
	public void spiderHaibao(){
		Map<String,Integer> querys = getQueryMap();
		System.out.println("querys:"+querys.size());
//		List<ResultVO> movies = spiderMovieData(querys);
//		System.out.println("douban:"+movies.size());
//		Collections.sort(movies,new QueryDescComparator());
//		System.out.println("----douban:"+movies.size());
		List<ResultVO> teleplays = spiderTeleplayData(querys);
		System.out.println("t hao123:"+teleplays.size());
		Collections.sort(teleplays,new QueryDescComparator());
		System.out.println("t-----hao123:"+teleplays.size());
		
		List<ResultVO> movies = spiderMovieDataFromHao123(querys);
		System.out.println("m hao123:"+movies.size());
		Collections.sort(movies,new QueryDescComparator());
		System.out.println("m-----hao123:"+movies.size());
		
		Map<String,List<ResultVO>> map = new HashMap<String, List<ResultVO>>();
		map.put(WordType.电视剧.name(), teleplays);
		map.put(WordType.电影.name(), movies);
		File file = new File("/opt/haibao.xls");
		try {
//			List<String> list = write2Excel(new FileOutputStream(file), map);
			System.out.println("==============");
			List<String> ts = new ArrayList<String>();
			for(ResultVO r:teleplays){
				ts.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
			}
			org.apache.commons.io.FileUtils.writeLines(new File("/opt/haibao/teleplay.txt"), ts);
			System.out.println("==============");
			ts = new ArrayList<String>();
			for(ResultVO r:movies){
				ts.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
			}
			org.apache.commons.io.FileUtils.writeLines(new File("/opt/haibao/movie.txt"), ts);
//			org.apache.commons.io.FileUtils.writeLines(new File("/opt/nohaibao.txt"), list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void spiderHaibaoFromTxt(){
		Map<String,Integer> querys = getQueryMap();
		System.out.println("querys:"+querys.size());
//		List<ResultVO> movies = spiderMovieData(querys);
//		System.out.println("douban:"+movies.size());
//		Collections.sort(movies,new QueryDescComparator());
//		System.out.println("----douban:"+movies.size());
		List<ResultVO> teleplays = spiderTeleplayData(querys);
		System.out.println("hao123:"+teleplays.size());
		Collections.sort(teleplays,new QueryDescComparator());
		System.out.println("-----hao123:"+teleplays.size());
		Map<String,List<ResultVO>> map = new HashMap<String, List<ResultVO>>();
		map.put(WordType.电视剧.name(), teleplays);
//		map.put(WordType.电影.name(), movies);
		File file = new File("/opt/haibao.xls");
		try {
			List<String> list = write2Excel(new FileOutputStream(file), map);
			System.out.println("==============");
			List<String> ts = new ArrayList<String>();
			for(ResultVO r:teleplays){
				ts.add(r.id+"\t"+r.cate+"\t"+r.title+"\t"+r.pic+"\t"+r.youku_pic+"\t"+r.ziliao_url+"\t"+r.queryCount+"\t"+(r.dbid_match?1:0)+"\t"+(r.imdb_match?1:0)+"\t"+(r.title_match?1:0));
			}
			org.apache.commons.io.FileUtils.writeLines(new File("/opt/haibao/haibao.txt"), ts);
			org.apache.commons.io.FileUtils.writeLines(new File("/opt/nohaibao.txt"), list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void spiderMovie(){
		final Map<String,Integer> querys = getQueryMap();
		System.out.println("querys:"+querys.size());
		new Thread(new Runnable() {
			public void run() {
				spiderMovieData1(querys);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				spiderMovieData2(querys);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				spiderMovieData3(querys);
			}
		}).start();
	}
	
	
	public List<String> write2Excel(OutputStream os,Map<String,List<ResultVO>> map){
		List<String> list = new ArrayList<String>();
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
//			WritableFont writefontt1 = new WritableFont(WritableFont.ARIAL, 12,
//					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
//					Colour.SKY_BLUE);
//			WritableFont writefontt3 = new WritableFont(WritableFont.ARIAL, 12,
//					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
//					Colour.YELLOW);
			WritableCellFormat writecellfontkeyword = new WritableCellFormat(writefontkeyword);
//			WritableCellFormat writecellfont1 = new WritableCellFormat(writefontt1);
//			WritableCellFormat writecellfont3 = new WritableCellFormat(writefontt3);
			WritableCellFormat current = writecellfontkeyword;
			int i = 0;
			for(Entry<String, List<ResultVO>> entry:map.entrySet()){
				sheet = workbook.createSheet(entry.getKey(), i);
				sheet.addCell(new jxl.write.Label(0,0,"序号",writecellfont));
				sheet.addCell(new jxl.write.Label(1,0,"标题",writecellfont));
				sheet.addCell(new jxl.write.Label(2,0,"ID",writecellfont));
				sheet.addCell(new jxl.write.Label(3,0,"图片地址",writecellfont));
				sheet.addCell(new jxl.write.Label(4,0,"hao123地址",writecellfont));
				int j = 1;
				int count = 0;
				int k = 0;
				for(ResultVO rv:entry.getValue()){
					
					System.out.println(++k+","+rv.toString());
					
					if(StringUtils.isBlank(rv.getYouku_pic())||rv.youku_pic.trim().equalsIgnoreCase("null")){
						if(!StringUtils.isBlank(rv.pic)){
							int block = 0;
							rv.youku_pic = null;
							while(StringUtils.isBlank(rv.youku_pic)&&block<5){
								System.out.println("is blank,");
								rv.youku_pic = UploadPic.uploadHaibao(rv.pic);
								SpiderUtils.sleep();
								block++;
							}
						}
						if(StringUtils.isBlank(rv.getYouku_pic())||rv.youku_pic.trim().equalsIgnoreCase("null")){
							list.add(rv.id+"\t"+rv.cate+"\t"+rv.title+"\t"+rv.pic+"\t"+rv.youku_pic+"\t"+rv.ziliao_url+"\t"+rv.queryCount);
							continue;
						}
					}
//					if(rv.imdb_match)
//						current = writecellfont1;
//					else if(rv.title_match)
//						current = writecellfont3;
//					else
//						current = writecellfontkeyword;
					
					if(rv.id>0)
						count +=1;
					
					sheet.addCell(new jxl.write.Number(0,j,j,current));
					sheet.addCell(new jxl.write.Label(1,j,rv.getTitle(),current));
					sheet.addCell(new jxl.write.Number(2,j,rv.getId(),current));
//					sheet.addCell(new jxl.write.Label(3,j,rv.getYouku_pic(),current));
					if(!StringUtils.isBlank(rv.getYouku_pic())){
						sheet.addHyperlink(new WritableHyperlink(3, j, new URL("http://g"+(new Random().nextInt(4)+1)+".ykimg.com/"+rv.getYouku_pic())));
					}
//					sheet.addCell(new jxl.write.Label(4,j,rv.getZiliao_url(),current));
					if(!StringUtils.isBlank(rv.getZiliao_url())){
						sheet.addHyperlink(new WritableHyperlink(4, j, new URL(rv.getZiliao_url())));
					}
					j++;
				}
				i++;
				System.out.println(entry.getKey()+":"+count);
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}
	
	public static void main(String[] args){
//		Map<String,Integer> querys = SpiderEnter.getInstance().getQueryMap();
//		System.out.println("querys:"+querys.size());
//		SpiderEnter.getInstance().spiderMovieData(querys);
//		MidVO m = new MidVO();
//		m.title = "天涯赤子心";
//		m.id = 25818;
//		List<ResultVO> rs = DoubanDataGetter.dataGetter(m);
//		System.out.println(rs.size());
		
		try {
			SpiderEnter.getInstance().parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println(SpiderEnter.getInstance().parseTxt().size());
		
	}
	
}
