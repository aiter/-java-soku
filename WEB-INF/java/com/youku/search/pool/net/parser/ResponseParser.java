package com.youku.search.pool.net.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.pool.net.ResultHolder;

/**
 * @author gaosong
 * @see 将服务端返回的Response字符串解析为客户端需要的ResultHolder对象 <br>
 *
 * @param <T>
 */
public abstract class ResponseParser<T> {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	/**
	 * 只在ResponseParserFactory工厂中构造，外面不要构造此对象
	 */
	ResponseParser() {
	}
	
	public abstract ResultHolder<T> parseResponse(String responseBody) throws Exception;
	
	/**
	 * 为了重用对象，在parse后必须将此对象的字段清空
	 */
	public abstract void clear();
	
	public static final class ParserException extends Exception {

		private static final long serialVersionUID = -667851537203819288L;

		public ParserException() {
			super();
		}

		public ParserException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public ParserException(String message) {
			super(message);
		}
		
		public ParserException(Throwable cause) {
			super(cause);
		}
		
	}
	
}
