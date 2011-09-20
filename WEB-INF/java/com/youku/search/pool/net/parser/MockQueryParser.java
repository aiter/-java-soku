package com.youku.search.pool.net.parser;

import java.io.UnsupportedEncodingException;

import com.youku.search.index.entity.Query;
import com.youku.search.pool.net.mina.Request;
import com.youku.search.util.Constant;

public class MockQueryParser extends AbstractQueryParser<Query> {

	@Override
	public String parseBody(Query t) throws UnsupportedEncodingException {

		Request request = new Request();
//		request.searchDescription = "0000000100000000000000000000000000010000000000000000000000000000";
		request.searchDescription = "0000000000000000000000000000000000000000000000000000000000000000";
		request.searchService = Constant.Socket.SEARCH_SERVICE;
//		request.videoCategory = 2;
		request.categorys = "250,";
		request.videoFormat = "0000000000000000";
		request.videoLength = "0,0";
		request.partnerId = 0;
		// C-Server是0~4第一页，5~9第二页
		request.pageId = (t.indexPage.page_no - 1) * 5;
		request.queryLength = t.keywords
				.getBytes(Constant.Socket.MESSAGE_CHARSET_NAME).length;
		request.queryString = t.keywords;
		
		request.contentLength = request.getContentLength();
		
		return request.parseRequest();
	}

}
