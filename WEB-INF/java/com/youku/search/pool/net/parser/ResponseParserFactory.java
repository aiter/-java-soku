package com.youku.search.pool.net.parser;

public class ResponseParserFactory {

	public final static ResponseParserFactory I = new ResponseParserFactory();

	/**
	 * 不要使用ThreadLocal，有可能内存堆积，导致FullGC应用停顿严重
	 */
//	private ThreadLocal<MockResponseParser> mockParserThreadLocal = new ThreadLocal<MockResponseParser>() {
//		protected MockResponseParser initialValue() {
//			return new MockResponseParser();
//		};
//
//		public MockResponseParser get() {
//			MockResponseParser parser = super.get();
//			parser.clear();
//			return parser;
//		};
//	};

	private ResponseParserFactory() {
	}
	
	/**
	 * 初步分析responseBody以得到合适的ResponseParser对象
	 * 
	 * @param responseBody
	 * @return
	 */
	public ResponseParser getParser(String responseBody) {
		return new DefaultResponseParser();
	}

}
