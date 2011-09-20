package com.youku.search.pool.net.mina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.youku.search.util.Constant;

/**
 * 
 * @author gaosong
 *
 * 对C-Server发送消息的协议编码
 */
public abstract class AbstractMessageEncoder implements MessageEncoder {

	protected static Log logger = LogFactory
			.getLog(AbstractMessageEncoder.class);

	private String head;

	private String end;

	public AbstractMessageEncoder(String head, String end) {
		this.head = head;
		this.end = end;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {

		ByteBuffer buf = ByteBufferUtil.getByteBuffer();
		buf.clear();

		// Encode a header
		buf.putString(head, Constant.Socket.MESSAGE_ENCODER.get());

		// Encode a body
		encodeBody(session, message, buf);

		// Encode a eof
		buf.putString(end, Constant.Socket.MESSAGE_ENCODER.get());

		buf.flip();
		if (logger.isDebugEnabled()) {
			byte[] bb = ByteBufferUtil.getBytesAbsolute(buf, buf.limit());
			String logStr = new String(bb, Constant.Socket.MESSAGE_CHARSET_NAME);
			logger.debug("------------ Socket编码完成，编码阶段的整个Request：\n" + logStr);
		}
		out.write(buf);

	}

	protected abstract void encodeBody(IoSession session, Object message,
			ByteBuffer out) throws Exception;
	
}
