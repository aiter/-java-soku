package com.youku.search.pool.net.mina;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.PooledByteBufferAllocator;
import org.apache.mina.common.SimpleByteBufferAllocator;

import com.youku.search.util.Constant;

/**
 * 
 * @author gaosong
 * 
 */
public class ByteBufferUtil {

	private static Log logger = LogFactory.getLog(ByteBufferUtil.class
			.getClass());

	{
		initByteBuffer();
	}
	
	public static void initByteBuffer(){
		ByteBuffer.setUseDirectBuffers(false);
		if (ByteBuffer.getAllocator() instanceof PooledByteBufferAllocator) {
			ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
		}
	}

	public static ByteBuffer getByteBuffer() {
		ByteBuffer bb = ByteBuffer.allocate(Constant.Socket.BUFFER_INIT_LENGTH,
				false);
		bb.setAutoExpand(true);
		return bb;
	}

	/**
	 * 从in中读入一串bytes，直到见到第一个eofChar结束 <br>
	 * 注意：调用此方法不会移动in的position <br>
	 * 
	 * @param in
	 * @param offset
	 *            读取in的偏移长度
	 * @param eofChar
	 *            标示读取结束的字符，返回的byte[]不包含此字符
	 * @throws IndexOutOfBoundsException
	 *             - If index is negative or not smaller than the buffer's limit
	 * @return
	 */
	public static byte[] getBytesAbsoluteBeforeEOFChar(java.nio.ByteBuffer in,
			int offset, char eofChar) {
		ByteBuffer buf = getByteBuffer();
		buf.clear();

		for (int i = offset;; i++) {
			byte b = in.get(i);
			if (b == (byte) eofChar) {
				break;
			}

			buf.put(b);
		}
		buf.flip();
		byte[] result = new byte[buf.limit()];
		buf.get(result);

		return result;
	}

	/**
	 * 从in中读入一串bytes，直到见到第一个eofChar结束，并且返回这个bytes的String <br>
	 * 注意：调用此方法会移动position，并且多读一个eofChar <br>
	 * 
	 * @param in
	 * @return
	 * @throws CharacterCodingException
	 */
	public static String getStringBeforeEOFChar(ByteBuffer in, char eofChar,
			CharsetDecoder decoder) throws CharacterCodingException {
		ByteBuffer buf = getByteBuffer();
		buf.clear();

		for (int i = 0;; i++) {
			byte b = in.get();
			if (b == (byte) eofChar) {
				if (i == 0) {
					continue; // 如果第一个字节就是eof不算
				} else {
					break;
				}
			}

			buf.put(b);
		}
		buf.flip();

		return buf.getString(decoder);
	}

	/**
	 * 从in中读取len长度的数据<br>
	 * 注意，in的position会增加len <br>
	 * 
	 * @param in
	 * @param len
	 */
	public static void skipBytes(ByteBuffer in, int len) {
		for (int i = 0; i < len; i++) {
			in.get();
		}
	}

	/**
	 * 从in中读取数据并返回 <br>
	 * 注意，in的position会增加len <br>
	 * 
	 * @param in
	 * @param len
	 * @return
	 */
	public static byte[] getBytes(ByteBuffer in, int len) {
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = in.get();
		}
		return result;
	}

	/**
	 * 从in中读取数据并返回 <br>
	 * 注意：调用此方法不会移动in的position
	 * 
	 * @param in
	 * @param len
	 * @throws IndexOutOfBoundsException
	 *             - If index is negative or not smaller than the buffer's limit
	 * @return
	 */
	public static byte[] getBytesAbsolute(ByteBuffer in, int len) {
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = in.get(i);
		}
		return result;
	}
	
	public static byte[] getBytesAbsolute(ByteBuffer in) {
		byte[] result = new byte[in.remaining()];
		for (int i = 0; i < in.limit(); i++) {
			result[i] = in.get(i);
		}
		return result;
	}

	/**
	 * 从in中读取数据并返回 <br>
	 * 注意：调用此方法不会移动in的position
	 * 
	 * @param in
	 * @param offset
	 *            读取in的偏移长度
	 * @param len
	 * @throws IndexOutOfBoundsException
	 *             - If index is negative or not smaller than the buffer's limit
	 * @return
	 */
	public static byte[] getBytesAbsolute(ByteBuffer in, int offset, int len) {
		byte[] result = new byte[len];
		for (int i = offset, j = 0; i < offset + len; i++, j++) {
			result[j] = in.get(i);
		}
		return result;
	}
	
	public static boolean byteCompare(byte[] b1, byte[] b2) {
		boolean result = true;
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				result = false;
				break;
			}
		}
		return result;
	}
	
}
