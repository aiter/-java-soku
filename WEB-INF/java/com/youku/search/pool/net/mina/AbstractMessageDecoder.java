package com.youku.search.pool.net.mina;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.youku.search.util.Constant;

public abstract class AbstractMessageDecoder extends MessageDecoderAdapter {

	protected static Log logger = LogFactory.getLog(AbstractMessageDecoder.class);

	protected final String head;
	protected final byte[] headBytes;
	protected final int headDataLength;
	
	protected final String end;
	protected final byte[] endBytes;
	
	protected final String PREFIX_KEY = this.getClass().getSimpleName() + "_SESSION_"; 
	protected final String READ_HEAD = "READ_HEAD";
	protected final String READ_CONTENT_LENGTH = "READ_CONTENT_LENGTH";
	
	public AbstractMessageDecoder(String head, String end) {
		// 头标示
		this.head = head;
		this.headDataLength = head.length();
		this.headBytes = this.head.getBytes();

		// 尾标示
		this.end = end;
		this.endBytes = this.end.getBytes();
	}

	@Override
	public MessageDecoderResult decodable(IoSession session, ByteBuffer in) {
		if (in.remaining() < headDataLength + 8) {
			return MessageDecoderResult.NEED_DATA;
		}

		// 报文头验证
		int headOffset = decodeHead(in);
		if (headOffset >= 0) {
			return MessageDecoderResult.OK;
		} else {
			return MessageDecoderResult.NOT_OK;
		}
	}

	@Override
	public MessageDecoderResult decode(IoSession session, ByteBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		boolean successDecode = false;
		boolean needData = false;
		Object body = null;
		try {
			if (!messageComplete(session, in)) {
				needData = true;
				return MessageDecoderResult.NEED_DATA;
			} else {
				body = decodeBody(session, in);
			}
			
			// Return NEED_DATA if the body is not fully read.
			if (body == null) {
				Integer contentLength = (Integer)session.getAttribute(PREFIX_KEY + READ_CONTENT_LENGTH);
				logger.error("已经完成的messageComplete，不应该NEED_DATA，in.remaining()="
						+ in.remaining() + ", contentLength=" + contentLength.intValue());
				
				return MessageDecoderResult.NEED_DATA;
			} else {
				// Skip Tail
				byte[] tailBytes = ByteBufferUtil.getBytes(in, 8);
				if (!ByteBufferUtil.byteCompare(tailBytes, endBytes)) {
					throw new ProtocolDecoderException(
							"--- 解码结束字符出错，allIn=" + getAllIn(session, in));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("allIn=" + getAllIn(session, in));
				}
				
				successDecode = true;
			}
		} finally {
			if (!needData || successDecode) {
				clearSessionAttribute(session);	
			}
			if (successDecode && null != body) {
				out.write(body);
			}
		}
		
		return MessageDecoderResult.OK;
	}

	/**
	 * 验证是否符合头部标示<br>
	 * 注意，此方法不移动ByteBuffer的position<br>
	 * 
	 * @param in
	 * @return 符合返回头的第一个字节的偏移量，不符合返回-1
	 * @throws UnsupportedEncodingException
	 */
	private int decodeHead(ByteBuffer in) {
		for (int headOffset = 0; headOffset < in.remaining(); headOffset++) {
			// 剩下的数据不够头的长度
			if (headDataLength + headOffset > in.remaining()) {
				break;
			}

			byte[] inBytes = ByteBufferUtil.getBytesAbsolute(in, headOffset,
					headDataLength);

			if (null != inBytes
					&& ByteBufferUtil.byteCompare(inBytes, headBytes)) {
				return headOffset;
			}
		}

		return -1;
	}

	/**
	 * 判断数据是否完整
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private boolean messageComplete(IoSession session, ByteBuffer in) throws Exception {
		Object readHeadMark = session.getAttribute(PREFIX_KEY + READ_HEAD);
		// 报文头验证
		if (null == readHeadMark) {
			int headOffset = decodeHead(in);
			if (headOffset >= 0) {
				ByteBufferUtil.skipBytes(in, headOffset + headDataLength);
				session.setAttribute(PREFIX_KEY + READ_HEAD);
			} else {
				return false;
			}
		}

		Integer contentLength = (Integer)session.getAttribute(PREFIX_KEY + READ_CONTENT_LENGTH);
		if (null == contentLength) {
			if (in.remaining() < 8) {
				return false;
			}
			
			String contentLengthStr = new String(
					ByteBufferUtil.getBytes(in, 8),
					Constant.Socket.MESSAGE_CHARSET_NAME);
			if (!StringUtils.isNumeric(contentLengthStr)) {
				throw new ProtocolDecoderException(
						"--- 解码contentLength出错, contentLengthStr="
								+ contentLengthStr);
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("sessionId="+session.hashCode()+", contentLengthStr=" + contentLengthStr);
			}
			
			contentLength = Integer.parseInt(contentLengthStr);
			if (contentLength.intValue() <= 0) {
				throw new ProtocolDecoderException(
						"------------ 解码contentLength出错, contentLengthStr="
								+ contentLengthStr);
			}
			
			session.setAttribute(PREFIX_KEY + READ_CONTENT_LENGTH, contentLength);
		}
		
		if (in.remaining() < contentLength.intValue()) {
			return false;
		} else {
			return true;
		}
		
//		// 验证结束标志，从结束标示的第一个字节开始，读8个字节（结束标示）
//		boolean hasEOF = false;
//		for (int eofOffset = last - 8; eofOffset > headDataLength + 8; eofOffset--) {
//			byte[] eof = ByteBufferUtil.getBytesAbsolute(in, eofOffset, 8);
//
//			if (ByteBufferUtil.byteCompare(eof, this.endBytes)) {
//				hasEOF = true;
//				logger.debug("------------ 解码结束字符串完成");
//				break;
//			}
//		}
//		
//		if (!hasEOF) {
//			logger.debug("contentLength=" + session.getAttribute(PREFIX_KEY + READ_CONTENT_LENGTH) + "allIn=" + getAllIn(session, in));
//			clearSessionAttribute(session);
//			throw new ProtocolDecoderException(
//					"------------ 解码结束字符出错 ------------");
//		}
//
//		return true;
	}

	public String getAllIn(IoSession session, ByteBuffer in)
			throws UnsupportedEncodingException {
		InetSocketAddress address = (InetSocketAddress)session.getRemoteAddress();
		String hostAddress = address.getAddress().getHostAddress();
		
		StringBuilder sb = new StringBuilder();
		sb.append("[sessionHashCode=").append(session.hashCode()).append('\n');
		sb.append("address=").append(hostAddress).append('\n');
		sb.append("ByteBuffer=").append(new String(
				ByteBufferUtil.getBytesAbsolute(in, in.limit()),
				Constant.Socket.MESSAGE_CHARSET_NAME)).append(']');
		
		return sb.toString();
	}
	
	private void clearSessionAttribute(IoSession session) {
		session.setAttribute(PREFIX_KEY + READ_HEAD, null);
		session.setAttribute(PREFIX_KEY + READ_CONTENT_LENGTH, null);
	}

	/**
	 * @return 如果还没有完整的数据则返回null（相当于告诉mina MessageDecoderResult.NEED_DATA）
	 */
	protected abstract Object decodeBody(IoSession session, ByteBuffer in)
			throws Exception;

}
