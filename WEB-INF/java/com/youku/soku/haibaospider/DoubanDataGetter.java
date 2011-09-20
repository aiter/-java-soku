package com.youku.soku.haibaospider;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.youku.search.util.DataFormat;
import com.youku.top.util.TopWordType.WordType;


public class DoubanDataGetter {
	
	private static Logger logger = Logger.getLogger(DoubanDataGetter.class);
	private static DoubanDataGetter instance = null;
	private static final String id_pic = "//DIV[@id='mainpic']/A[@class='nbg']/IMG";
	private static final String errpr_pic = "http://img3.douban.com/pics/movie-default-medium.gif";
	
	private static final String items_path = "//TR[@class='item']";
	private static final String items_title = ".//TD/DIV[@class='pl2']/A";
	private static final String items_url = ".//TD/A[@class='nbg']";
	
	private DoubanDataGetter() {
		super();
	}

	public static synchronized DoubanDataGetter getInstance() {
		if(null==instance)
			instance = new DoubanDataGetter();
		return instance;
	}
	
	public static List<ResultVO> dataGetter(MidVO mid){
		List<ResultVO> list = new ArrayList<ResultVO>();
		if(mid.dbid>0){
			mid.dbid_match = true;
			parseDoubanByID(mid, list);
		}else{
			parseDoubanBySearch(mid, list);
		}
		return list;
	}
	
	public static void parseDoubanBySearch(MidVO mid,List<ResultVO> list){
		String url = null;
		int u = 0;
		while(null==url&&u<5){
			try {
				url = "http://movie.douban.com/subject_search?cat=1002&search_text="+URLEncoder.encode(mid.title,"utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			u+=1;
		}
		if(StringUtils.isBlank(url)) return;
		
		String res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		int block = 0;
		while(null==res&&block<2){
			block+=1;
			SpiderUtils.sleep();
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		}
		if(null!=res){
			Document document = SpiderUtils.getInstance().html2Document(res, "utf-8");
			if(null!=document){
				List<DouBanVO>  dbvs = parseSearch(document, items_path);
				if(null!=dbvs&&dbvs.size()>0){
					ResultVO r = null;
					if(!StringUtils.isBlank(mid.imdb)){
						r = parseDoubanByIMDBid(dbvs, mid);
						if(null!=r){
							r.setImdb_match(true);
							list.add(r);
						}
					}
					if(null==r){
						for(DouBanVO dbvo:dbvs){
							if(dbvo.title.contains(mid.title)){
								MidVO m = new MidVO();
								m.id = mid.id;
								m.title = mid.title;
								String s = StringUtils.substringAfterLast(dbvo.url, "subject/");
								if(StringUtils.isBlank(s))
									continue;
								s = s.trim();
								if(s.endsWith("/"))
									s = StringUtils.substringBefore(s, "/");
								int dbid = DataFormat.parseInt(s);
								if(dbid<1) continue;
								m.dbid = dbid;
								parseDoubanByID(m, list);
							}
						}
					}
				}
			}
		}
	}
	
	private static List<DouBanVO> parseSearch(Document document,String tag) {
		NodeList nodes = null;
		List<DouBanVO>  list = new ArrayList<DouBanVO>();
		if (null != document) {
			nodes = parseItems(document, tag);
			if (null != nodes) {
				DouBanVO dbv = null;
				String title = null;
				Set<String> set = null;
				String url = null;
				for(int i=0;i<nodes.getLength();i++){
					title = parseContent(nodes.item(i), items_title);
					if(StringUtils.isBlank(title)) continue;
					set = SpiderUtils.parseStr2Set(title, "/");
					if(null==set)
						continue;
					url = parseUrl(nodes.item(i), items_url);
					if(StringUtils.isBlank(url)) continue;
					dbv = new DouBanVO();
					dbv.getTitle().addAll(set);
					dbv.setUrl(url);
					list.add(dbv);
				}
			}
		}
		return list;
	}
	
	public static ResultVO parseDoubanByIMDBid(List<DouBanVO> list,MidVO mid){
		ResultVO r = null;
		for(DouBanVO dbvo:list){
			r = parseDoubanByIMDBid(dbvo.getUrl(), mid);
			if(null!=r) return r;
		}
		return null;
	}
	
	public static ResultVO parseDoubanByIMDBid(String url,MidVO mid){
		String res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		int block = 0;
		while(null==res&&block<2){
			block+=1;
			SpiderUtils.sleep();
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		}
		if(null!=res){
			Matcher matcher = Pattern.compile("<span class=\"pl\">IMDb链接:</span> <a[^>]*>([^<]*)</a>").matcher(res);
			String imdb = null;
			if(null!=matcher&&matcher.find()){
				imdb = matcher.group(1);
			}
			if(!StringUtils.isBlank(imdb)){
				imdb = imdb.trim();
			}
			if(mid.imdb.equalsIgnoreCase(imdb)){
				Document document = SpiderUtils.getInstance().html2Document(res, "utf-8");
				if(null!=document){
					String pic = parsePic(document, id_pic);
					if(!StringUtils.isBlank(pic)&&!pic.equalsIgnoreCase(errpr_pic)){
						ResultVO r = new ResultVO();
						r.setCate(WordType.电影.getValue());
						r.setId(mid.getId());
						r.setPic(pic);
						r.setTitle(mid.title);
						r.setZiliao_url(url);
//						r.setYouku_pic(UploadPic.uploadHaibao(r.getPic()));
						return r;
					}
				}
			}
		}
		return null;
	}
	
	public static void parseDoubanByID(MidVO mid,List<ResultVO> list){
		String url = "http://movie.douban.com/subject/"+mid.dbid+"/";
		String res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		int block = 0;
		while(null==res&&block<2){
			block+=1;
			SpiderUtils.sleep();
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, "utf-8");
		}
		if(!StringUtils.isBlank(res)){
			Document document = SpiderUtils.getInstance().html2Document(res, "utf-8");
			if(null!=document){
				String pic = parsePic(document, id_pic);
				if(!StringUtils.isBlank(pic)&&!pic.equalsIgnoreCase(errpr_pic)){
					ResultVO r = new ResultVO();
					r.setCate(WordType.电影.getValue());
					r.setId(mid.getId());
					r.setPic(pic);
					r.setTitle(mid.title);
					r.setZiliao_url(url);
					if(mid.isDbid_match())
						r.setDbid_match(mid.isDbid_match());
					else
						r.setTitle_match(true);
//					r.setYouku_pic(UploadPic.uploadHaibao(r.getPic()));
					list.add(r);
				}
			}
		}
	}
	
	private static String parsePic(Document document,String tag) {
		Element element = null;
		if (null != document) {
			try {
				element = (Element) XPathFactory.newInstance().newXPath()
						.evaluate(tag, document, XPathConstants.NODE);
				if (null != element) {
					String pic = element.getAttribute("src");
					if(!StringUtils.isBlank(pic))
						return pic.trim();
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static String parseUrl(Node node,String tag) {
		Element element = null;
		if (null != node) {
			try {
				element = (Element) XPathFactory.newInstance().newXPath()
						.evaluate(tag, node, XPathConstants.NODE);
				if (null != element) {
					String url = element.getAttribute("href");
					if(!StringUtils.isBlank(url))
						return url.trim();
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static String parseContent(Node node,String tag) {
		Element element = null;
		if (null != node) {
			try {
				element = (Element) XPathFactory.newInstance().newXPath()
						.evaluate(tag, node, XPathConstants.NODE);
				if (null != element) {
					String c = element.getTextContent();
					if(!StringUtils.isBlank(c))
						return c.trim();
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static NodeList parseItems(Document document,String tag) {
		NodeList nodes = null;
		if (null != document) {
			try {
				nodes = (NodeList) XPathFactory.newInstance().newXPath()
						.evaluate(tag, document, XPathConstants.NODESET);
				return nodes;
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
