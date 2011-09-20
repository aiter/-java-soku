package com.youku.soku.haibaospider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Hao123DataGetter {
	
	private static final String item_path = "//UL[@id='tv_data']/LI";
	private static final String pic_path = ".//P[@class='poster']/A/SPAN[@class='image']/IMG";
	private static final String url_path = ".//P[@class='name clearfix']/A[@class='title']";
	private static final String actors_path = ".//P[@class='player']/A";
	
	private static final String movie_item_path = "//UL[@id='movie_data']/LI";
	
	private static Hao123DataGetter instance = null;
	private Hao123DataGetter() {
		super();
	}

	public static synchronized Hao123DataGetter getInstance() {
		if(null==instance)
			instance = new Hao123DataGetter();
		return instance;
	}
	
	public List<Hao123VO> movieGetter(){
		List<Hao123VO> list = new ArrayList<Hao123VO>();
		String url = "http://movie.hao123.com/index/?sort=fyb_cat&pn=%1$s";
		for(int i=1;i<101;i++){
			parseMovie(String.format(url, i),list);
			SpiderUtils.sleep();
		}
		return list;
	}
	
	public List<Hao123VO> teleplayGetter(){
		List<Hao123VO> list = new ArrayList<Hao123VO>();
		String url = "http://tv.hao123.com/index/tv_all,tv_all/%1$s";
		for(int i=1;i<103;i++){
			parse(String.format(url, i),list);
			SpiderUtils.sleep();
		}
		return list;
	}
	
	public void parse(String url,List<Hao123VO> list){
		String res = SpiderUtils.getInstance().getHttpResponseHtml(url, "GBK");
		int block = 0;
		while(null==res&&block<2){
			block+=1;
			SpiderUtils.sleep();
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, "GBK");
		}
		if(!StringUtils.isBlank(res)){
			Document document = SpiderUtils.getInstance().html2Document(res, "GBK");
			if(null!=document){
				NodeList nodes = parseItems(document, item_path);
				if(null!=nodes){
					Hao123VO h = null;
					for(int i=0;i<nodes.getLength();i++){
						h = parseHao123VO(nodes.item(i));
						if(null!=h)
							list.add(h);
					}
				}
			}
		}
	}
	
	public void parseMovie(String url,List<Hao123VO> list){
		String res = SpiderUtils.getInstance().getHttpResponseHtml(url, "GBK");
		int block = 0;
		while(null==res&&block<2){
			block+=1;
			SpiderUtils.sleep();
			res = SpiderUtils.getInstance().getHttpResponseHtml(url, "GBK");
		}
		if(!StringUtils.isBlank(res)){
			Document document = SpiderUtils.getInstance().html2Document(res, "GBK");
			if(null!=document){
				NodeList nodes = parseItems(document, movie_item_path);
				if(null!=nodes){
					Hao123VO h = null;
					for(int i=0;i<nodes.getLength();i++){
						h = parseHao123VO(nodes.item(i));
						if(null!=h)
							list.add(h);
					}
				}
			}
		}
	}
	
	
	private NodeList parseItems(Document document,String tag) {
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
	
	private Hao123VO parseHao123VO(Node node) {
		Element element = null;
		Hao123VO h = new Hao123VO();
		if (null != node) {
			try {
				element = (Element) XPathFactory.newInstance().newXPath()
						.evaluate(url_path, node, XPathConstants.NODE);
				if(null!=element){
					String title = element.getTextContent();
					if(StringUtils.isBlank(title))
						return null;
					h.setUrl(element.getAttribute("href"));
					h.setTitle(title.trim());
					String pic = parsePic(node, pic_path);
					if(StringUtils.isBlank(pic))
						return null;
					h.setPic(pic);
					Set<String> set = parseActors(node, actors_path);
					if(null!=set)
						h.setActors(set);
					return h;
				}
				
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String parsePic(Node node,String tag) {
		Element element = null;
		if (null != node) {
			try {
				element = (Element) XPathFactory.newInstance().newXPath()
						.evaluate(tag, node, XPathConstants.NODE);
				if(null!=element){
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
	
	private Set<String> parseActors(Node node,String tag) {
		Set<String> set = new HashSet<String>();
		NodeList nodes = null;
		if (null != node) {
			try {
				nodes = (NodeList) XPathFactory.newInstance().newXPath()
						.evaluate(tag, node, XPathConstants.NODESET);
				if(null!=nodes){
					for(int i=0;i<nodes.getLength();i++){
						String act = nodes.item(i).getTextContent();
						if(!StringUtils.isBlank(act))
							set.add(act.trim());
					}
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		if(set.size()>0)
			return set;
		return null;
	}
	
	public static void main(String[]  args){
		List<Hao123VO> list = Hao123DataGetter.getInstance().movieGetter();
		for(Hao123VO h:list){
			System.out.println(h.toString());
		}
		
	}
}
