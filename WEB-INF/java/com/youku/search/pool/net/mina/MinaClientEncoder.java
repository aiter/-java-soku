package com.youku.search.pool.net.mina;

import java.nio.charset.UnmappableCharacterException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

import com.youku.search.index.entity.Query;
import com.youku.search.pool.net.parser.AbstractQueryParser;
import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant;

public class MinaClientEncoder extends AbstractMessageEncoder {

	private static final Set<Class<?>> TYPES;
	
	static {
		Set<Class<?>> types = new HashSet<Class<?>>();
		types.add(Query.class);
		TYPES = Collections.unmodifiableSet(types);
	}

	public MinaClientEncoder() {
		super(Constant.Socket.REQUEST_HEAD, Constant.Socket.REQUEST_END);
	}

	@Override
	public Set<Class<?>> getMessageTypes() {
		return TYPES;
	}

	@Override
	protected void encodeBody(IoSession session, Object message, ByteBuffer out)
			throws Exception {
		Query query = (Query) message;
		AbstractQueryParser parser = SearchUtil.getQueryParser(query.getClass());
		
		out.putString(parser.parseBody(query),
				Constant.Socket.MESSAGE_ENCODER.get());	
		
	}

}
