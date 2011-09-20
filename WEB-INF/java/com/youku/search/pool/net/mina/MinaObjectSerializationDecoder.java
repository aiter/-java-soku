package com.youku.search.pool.net.mina;

import java.io.ObjectStreamConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.BufferDataException;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class MinaObjectSerializationDecoder extends MessageDecoderAdapter {

	private final ClassLoader classLoader;

	private int maxObjectSize = 1048576; // 1MB

	private static final int OBJECT_HEAD_LENGTH = 4;

	protected static Log logger = LogFactory
			.getLog(MinaObjectSerializationDecoder.class);

	public MinaObjectSerializationDecoder() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public MinaObjectSerializationDecoder(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new NullPointerException("classLoader");
		}
		this.classLoader = classLoader;
	}

	/**
	 * Returns the allowed maximum size of the object to be decoded. If the size
	 * of the object to be decoded exceeds this value, this decoder will throw a
	 * {@link BufferDataException}. The default value is <tt>1048576</tt> (1MB).
	 */
	public int getMaxObjectSize() {
		return maxObjectSize;
	}

	/**
	 * Sets the allowed maximum size of the object to be decoded. If the size of
	 * the object to be decoded exceeds this value, this decoder will throw a
	 * {@link BufferDataException}. The default value is <tt>1048576</tt> (1MB).
	 */
	public void setMaxObjectSize(int maxObjectSize) {
		if (maxObjectSize <= 0) {
			throw new IllegalArgumentException("maxObjectSize: "
					+ maxObjectSize);
		}

		this.maxObjectSize = maxObjectSize;
	}

	@Override
	public MessageDecoderResult decodable(IoSession session, ByteBuffer in) {
		try {
			if (!in.prefixedDataAvailable(OBJECT_HEAD_LENGTH, maxObjectSize)) {
				return MessageDecoderResult.NEED_DATA;
			}
		} catch (BufferDataException e) {
			return MessageDecoderResult.NOT_OK;
		}

		// 判断是不是应该用这个Decoder
		if (decodeHead(in) >= 0) {
			return MessageDecoderResult.OK;
		} else {
			return MessageDecoderResult.NOT_OK;
		}
	}

	@Override
	public MessageDecoderResult decode(IoSession session, ByteBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (!in.prefixedDataAvailable(OBJECT_HEAD_LENGTH, maxObjectSize)) {
			return MessageDecoderResult.NEED_DATA;
		}

		Object obj = in.getObject(classLoader);
		out.write(obj);

		if (logger.isDebugEnabled()) {
			logger.debug("------------ 对象序列化解码完成，被反序列化的对象："
					+ obj.getClass().getSimpleName());
		}

		return MessageDecoderResult.OK;
	}

	private int decodeHead(ByteBuffer in) {
		for (int headOffset = 0; headOffset < in.remaining(); headOffset++) {
			// 剩下的数据不够头的长度
			if (OBJECT_HEAD_LENGTH + headOffset > in.remaining()) {
				break;
			}

			// 验证序列化对象的STREAM_MAGIC和STREAM_VERSION
			if (in.getShort(headOffset) == ObjectStreamConstants.STREAM_MAGIC
					&& in.getShort(headOffset + 2) == ObjectStreamConstants.STREAM_VERSION) {
				return headOffset;
			}

		}

		return -1;
	}

}
