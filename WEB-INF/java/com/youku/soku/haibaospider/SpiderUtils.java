package com.youku.soku.haibaospider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SpiderUtils {
	
	private static Logger logger = Logger.getLogger(SpiderUtils.class);
	private static SpiderUtils instance = null;
	private static final List<String> ips = null;
	
	private SpiderUtils() {
		super();
	}

	public static synchronized SpiderUtils getInstance() {
		if(null==instance)
			instance = new SpiderUtils();
		return instance;
	}
	
	public Document html2Document(String html, String charset) {

		try {
			if(StringUtils.isBlank(html)) return null;
			return nekoHtmlParser(new ByteArrayInputStream(html
					.getBytes(charset)), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document nekoHtmlParser(InputStream in, String charset) {
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
	
	public String getHttpResponseHtmlUseProx(String url, String charset) {
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
	
	public byte[] getHttpResponseByteArr(String url, String charset) {
		 String html = getHttpResponseHtml(url, charset);
		 if(!StringUtils.isBlank(html))
			try {
				return html.getBytes(charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		 return null;
	}
	
	public static void sleep(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep(long t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Set<String> parseStr2Set(String str, String split) {
		Set<String> alias = new HashSet<String>();
		String[] aliasArr = str.split(split);
		if (null != aliasArr) {
			for (String a : aliasArr) {
				if (!StringUtils.isBlank(a))
					alias.add(a.trim());
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}
}
