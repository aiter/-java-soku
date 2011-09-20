package com.youku.search.pool.net.mina;

import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

/**
 * 
 * @author gaosong
 * 
 */
public class MinaProtocolCodecFactory extends DemuxingProtocolCodecFactory {

	public MinaProtocolCodecFactory(boolean server) {
		if (server) {
			// 在测试项目（myDemo）中 有MinaServer，正式项目中没有
			// super.register(MinaServerDecoder.class);
			// super.register(MinaServerEncoder.class);
		} else {
			ObjectSerializationCodecFactory serializationCodecFactory = new ObjectSerializationCodecFactory();
			ProtocolEncoder serializationProtocolEncoder = serializationCodecFactory.getEncoder();
			
			// Encoder有Type判断所以顺序无所谓
			// Decoder先register的后出来
			super.register(new MinaObjectSerializationEncoder(serializationProtocolEncoder));
			super.register(new MinaObjectSerializationDecoder());
			
			super.register(new MinaClientDecoder());
			super.register(new MinaClientEncoder());
		}
	}
}
