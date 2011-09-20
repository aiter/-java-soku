package com.youku.soku.pos_analysis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.youku.top.paihangbang.TypeWordsMgt;
import com.youku.top.util.TopWordType.WordType;

public class BaiduTopSpider {

	private static Logger logger = Logger.getLogger(BaiduTopSpider.class);
	private static BaiduTopSpider instance = new BaiduTopSpider();

	public static BaiduTopSpider getInstance() {
		return instance;
	}

	private static final String KEY = "//DIV[@class='list']/TABLE/TBODY/TR/TD[@class='key']/A";

	public List<String> getTopMovieName(){
		String html = instance.getHttpResponseHtml("http://top.baidu.com/buzz.php?p=movie","GBK");
		Document document = instance.html2Document(html, "GBK");
		return instance.parse(document);
	}
	
	public List<String> getTopAllTeleplayName(){
		List<String> list = getTopTeleplayName();
		List<String> uslist = getTopUsTeleplayName();
		list.addAll(uslist);
		return list;
	}
	
	public List<String> getTopTeleplayName(){
		String html = instance.getHttpResponseHtml("http://top.baidu.com/buzz.php?p=tv","GBK");
		Document document = instance.html2Document(html, "GBK");
		return instance.parse(document);
	}
	
	public List<String> getTopUsTeleplayName(){
		String html = instance.getHttpResponseHtml("http://top.baidu.com/buzz.php?p=ustv","GBK");
		Document document = instance.html2Document(html, "GBK");
		return instance.parse(document);
	}
	
	public void baiduTopSpiderSave(){
		logger.info("抓取百度电影榜单并保存start----");
		List<String> list = getTopMovieName();
		int rows = 0;
		for(String word:list){
			rows +=TypeWordsMgt.getInstance().typeWordSaveFromBaidu(word, WordType.电影.getValue());
		}
		logger.info("抓取百度电影榜单并保存end,抓取个数:"+list.size()+",保存个数:"+rows+"----");
		logger.info("抓取百度电视剧，美剧榜单并保存start----");
		list = getTopAllTeleplayName();
		rows = 0;
		for(String word:list){
			rows +=TypeWordsMgt.getInstance().typeWordSaveFromBaidu(word, WordType.电视剧.getValue());
		}
		logger.info("抓取百度电视剧，美剧榜单并保存end,抓取个数:"+list.size()+",保存个数:"+rows+"----");
	}
	
	private List<String> parse(Document document) {
		List<String> list = new ArrayList<String>();
		NodeList nodes = null;
		if (null != document) {
			try {
				nodes = (NodeList) XPathFactory.newInstance().newXPath()
						.evaluate(KEY, document, XPathConstants.NODESET);
				if (null != nodes) {
					for (int i = 0; i < nodes.getLength(); i++) {
						list.add(nodes.item(i).getTextContent());
					}
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private Document html2Document(String html, String charset) {

		try {
			if(StringUtils.isBlank(html)) return null;
			return nekoHtmlParser(new ByteArrayInputStream(html
					.getBytes(charset)), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Document nekoHtmlParser(InputStream in, String charset) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));

			DOMParser parser = new DOMParser();

			parser.setFeature("http://xml.org/sax/features/namespaces", false);
			parser
					.setProperty(
							"http://cyberneko.org/html/properties/names/elems",
							"upper");
			parser
					.setProperty(
							"http://cyberneko.org/html/properties/names/attrs",
							"lower");

			parser.parse(new InputSource(reader));
			return parser.getDocument();

		} catch (UnsupportedEncodingException e) {
			logger.error(Level.WARNING + "unsupported encoding:(" + charset
					+ ")");
		} catch (SAXException e) {
			logger.error(Level.WARNING + "parser html error");
		} catch (IOException e) {
			logger.error(Level.WARNING + "read html source error");
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public String getHttpResponseHtml(String url, String charset) {
		if (!url.toLowerCase().trim().startsWith("http")) {
			url = "http://" + url.trim();
		}

		HttpMethod method = null;

		try {
			HttpClient client = new HttpClient();

			method = new GetMethod(url);

			method.setFollowRedirects(false);
			method.setRequestHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			// method.getParams().setParameter("http.socket.timeout",
			// new Integer(1000));
			client.getParams().setCookiePolicy(
					CookiePolicy.BROWSER_COMPATIBILITY);

			client.getHttpConnectionManager().getParams().setConnectionTimeout(
					10000);
			client.getHttpConnectionManager().getParams().setSoTimeout(50000);

			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(3, true));
			method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET,
					charset);

			int status = client.executeMethod(method);

			if (status == HttpStatus.SC_OK) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(method
							.getResponseBodyAsStream(), charset));
					StringBuffer buffer = new StringBuffer();
					String line = null;
					while ((line = in.readLine()) != null) {
						if (!line.trim().equals("")) {
							buffer.append(line + "\n");
						}
					}
					return buffer.toString();
				} finally {
					in.close();
				}

			} else {
				logger.error(url + Level.WARNING + "http status error");
			}
		} catch (UnknownHostException e) {
			logger.error(Level.WARNING + "unknown host exception");
		} catch (ConnectTimeoutException e) {
			logger.error(Level.WARNING + "socket timeout exception");
		} catch (SocketTimeoutException e) {
			logger.error(Level.WARNING + "socket timeout exception");
		} catch (SocketException e) {
			logger.error(Level.WARNING + "socket exception");
		} catch (HttpException e) {
			logger.error(Level.WARNING + "http exception");
		} catch (IOException e) {
			logger.error(Level.WARNING + "http io exception");
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return null;
	}
}
