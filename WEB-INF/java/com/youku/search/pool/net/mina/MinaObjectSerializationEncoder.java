package com.youku.search.pool.net.mina;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.youku.search.pool.net.QueryHolder;

public class MinaObjectSerializationEncoder implements MessageEncoder {

	private static final Set<Class<?>> TYPES;

	private ProtocolEncoder serializationProtocolEncoder;

	protected static Log logger = LogFactory
			.getLog(MinaObjectSerializationEncoder.class);

	static {
		Set<Class<?>> types = new HashSet<Class<?>>();
		types.add(QueryHolder.class);
		TYPES = Collections.unmodifiableSet(types);
	}

	@Override
	public Set<Class<?>> getMessageTypes() {
		return TYPES;
	}

	public MinaObjectSerializationEncoder(
			ProtocolEncoder serializationProtocolEncoder) {
		this.serializationProtocolEncoder = serializationProtocolEncoder;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		this.serializationProtocolEncoder.encode(session, message, out);

		if (logger.isDebugEnabled()) {
			logger.debug("------------ 对象序列化编码完成，被序列化的对象："
					+ message.getClass().getSimpleName());
		}
	}

}
