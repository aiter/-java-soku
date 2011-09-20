package com.youku.search.sort.benchmark;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.youku.search.index.entity.Video;
import com.youku.search.pool.net.LockQuery;
import com.youku.search.pool.net.mina.ByteBufferUtil;
import com.youku.search.sort.Parameter;
import com.youku.search.sort.ParameterNames;
import com.youku.search.sort.core.SearchContext;
import com.youku.search.util.Constant;
import com.youku.search.util.NetUtil;
import com.youku.search.util.NumberUtil;
import com.youku.search.util.StringUtil.EncodingSet;

public class MockQueryBuilder {

	protected static Log logger = LogFactory.getLog(MockQueryBuilder.class);

	/**
	 * 测试用的语料库
	 */
	private final File queryDataFile;
	
	private final EncodingSet encoding;

	private int fileSize;
	
	/**
	 * 必须设置为只读的才会线程安全
	 */
	private MappedByteBuffer mapBuf;

	MockQueryBuilder(String queryDataFilePath, EncodingSet encoding) {
		this.queryDataFile = new File(queryDataFilePath);
		this.encoding = encoding;
		init(queryDataFilePath);
	}
	
	private void init(String queryDataFilePath) {
		try {
			RandomAccessFile raFile = new RandomAccessFile(queryDataFile, "r");
			FileChannel fc = raFile.getChannel();
			fileSize = (int) fc.size();
			
			logger.info("开始加载语料库，文件大小=" + fileSize);
			mapBuf = fc.map(MapMode.READ_ONLY, 0, fileSize);
			logger.info("加载语料库完毕！");

		} catch (Throwable e) {
			throw new IllegalStateException("初始化RequestBuilder错误", e);
		}

	}
	
	public Parameter buildMockParameter(String type, String mockKeyword, String orderFieldStr) throws UnsupportedEncodingException{
		return buildMockParameter(type, mockKeyword, orderFieldStr, 0);
	}
	
	public Parameter buildMockParameter(String type, String orderFieldStr) throws UnsupportedEncodingException{
		String mockKeyword = readRandomLine(encoding);
		return buildMockParameter(type, mockKeyword, orderFieldStr, 0);
	}
	
	public Parameter buildMockParameter(String type, String orderFieldStr, int hd) throws UnsupportedEncodingException{
		String mockKeyword = readRandomLine(encoding);
		return buildMockParameter(type, mockKeyword, orderFieldStr, hd);
	}
	
	public Parameter buildMockParameter(String type) throws UnsupportedEncodingException{
		String mockKeyword = readRandomLine(encoding);
		return buildMockParameter(type, mockKeyword, "null", 0);
	}
	
	public Parameter buildMockParameter(String type, String mockKeyword, String orderFieldStr, int hd) throws UnsupportedEncodingException{
		Map<ParameterNames, String> map = new HashMap<ParameterNames, String>();
		map.put(ParameterNames._source, "youku");
		map.put(ParameterNames.keyword, mockKeyword);
		map.put(ParameterNames.type, type);
		map.put(ParameterNames.cateid, "0");
		map.put(ParameterNames.pagesize, "20");
		map.put(ParameterNames.curpage, "1");
		map.put(ParameterNames.orderfield, orderFieldStr);
		map.put(ParameterNames.relnum, "12");
		map.put(ParameterNames.order, "1");
		map.put(ParameterNames.fields, null);
		map.put(ParameterNames.limit_date, null);
		map.put(ParameterNames.categories, null);
		
		map.put(ParameterNames.hd, String.valueOf(hd));
		map.put(ParameterNames.ftype, String.valueOf(hd));
		
		map.put(ParameterNames.pv, "0");
		map.put(ParameterNames.comments, "0");
		map.put(ParameterNames.hl, "true");
		map.put(ParameterNames.query_url, "http://" + NetUtil.getFirstLocalIP() + "/search_video/q_" + URLEncoder.encode(mockKeyword, EncodingSet.UTF8.getEncode()));
		
		return new Parameter(map);
	}

	public LockQuery buildMockLockQuery() throws UnsupportedEncodingException {
		MockHttpServletRequest httpReq = new MockHttpServletRequest("GET", "/search_video/");
		httpReq.addParameter("keyword", readRandomLine(encoding));
		Parameter p = new Parameter(httpReq);
		SearchContext<Video> context = new SearchContext<Video>(p, 48);

		return (LockQuery) context.lockQuery;
	}
	
	/**
	 * @param maxLength 最大长度，单位byte。如果超过此长度则读另一行，直到找到<=maxLength的行
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	String readRandomLine(int maxLength, EncodingSet encoding)
			throws UnsupportedEncodingException {
		String line;
		int lineLength;

		do {
			line = readRandomLine(encoding);
			lineLength = line.getBytes(encoding.getEncode()).length;
		} while (lineLength > maxLength);

		return line;
	}

	/**
	 * 从语料库中随机读取一行字符
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	String readRandomLine(EncodingSet encoding) throws UnsupportedEncodingException {
		String line = null;

		// 从语料库文件中随机取一个字节
		int startIndex = NumberUtil.getRandomInt(-1, fileSize);
		byte b = mapBuf.get(startIndex);

		while (true) {
			try {
				if ((char) b == '\n') {
					// 拿到第一个\n和第二个\n之间的串
					byte[] lineBytes = ByteBufferUtil
							.getBytesAbsoluteBeforeEOFChar(mapBuf,
									startIndex + 1, '\n');
					line = new String(lineBytes, encoding.getEncode()).trim();
					if (line.length() <= 1) {
						startIndex += lineBytes.length + 1;
						b = mapBuf.get(startIndex);
						continue;
					}

					break;
				} else {
					b = mapBuf.get(++startIndex);
				}
			} catch (IndexOutOfBoundsException e) {
				line = readRandomLine(encoding);
				break;
			}
		}

		return line;
	}
	
}