package com.youku.search.pool.net.mina;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

import com.youku.search.pool.net.ResultHolder;
import com.youku.search.pool.net.parser.ResponseParser;
import com.youku.search.pool.net.parser.ResponseParserFactory;
import com.youku.search.util.Constant;

public class MinaClientDecoder extends AbstractMessageDecoder {

	public MinaClientDecoder() {
		super(Constant.Socket.RESPONSE_HEAD, Constant.Socket.RESPONSE_END);
	}

	@Override
	protected Object decodeBody(IoSession session, ByteBuffer in)
			throws Exception {
		Integer contentLength = (Integer)session.getAttribute(PREFIX_KEY + READ_CONTENT_LENGTH);
		if (in.remaining() < contentLength.intValue()) {
			return null;
		}

		String responseBody = new String(ByteBufferUtil.getBytes(in,
				contentLength.intValue() - 8), Constant.Socket.MESSAGE_CHARSET_NAME);
		ResponseParser parser = ResponseParserFactory.I.getParser(responseBody);
		ResultHolder resultHolder = parser.parseResponse(responseBody);

		return resultHolder;
	}

}
