package com.youku.search.pool.net.mina;

import java.io.UnsupportedEncodingException;

import com.youku.search.util.Constant;
import com.youku.search.util.StringUtil;

public class Request {

	public static final char split = '\t';

	/**
	 * 后续信息总长度（不包括contentLength段但包括END段的长度）
	 */
	public String contentLength;

	/*
	 * 检索描述信息，64位文本 <br>
	 * 7: 带过滤条件的查询 <br>
	 * 35: 按照视频类别（原创、电视剧、电影等）过滤， 此时：videoCategory值为 0..31, 99表示不过滤 <br>
	 */
	public String searchDescription;

	/**
	 * 检索服务描述信息，8位
	 */
	public String searchService;

	/**
	 * 电影、电视剧等等分类，0~22，默认值为250 <br>
	 * 包括了单分类、多分类、单排除分类、多排除分类，凡是单的都需要后面加逗号
	 */
	public static final String DEFAULT_CATEGORY = "250";
	public String categorys;

	/**
	 * flv、hd等等分类，16位
	 */
	public String videoFormat;

	/**
	 * 视频长度信息，表示区间用逗号分割
	 */
	public static final int MAX_VIDEO_LENGTH = 65535;
	public static final String DEFAULT_VIDEO_LENGTH = "0,0";
	public String videoLength = DEFAULT_VIDEO_LENGTH;
	
	public int partnerId;

	/**
	 * 第几个0~50
	 */
	public int pageId;

	public int queryLength;

	public String queryString;

	/**
	 * 创建时间，表示区间用逗号分割
	 */
	public static final String DEFAULT_LIMIT_DATE = "0,0";
	public String limitDate = DEFAULT_LIMIT_DATE;

	private String content = null;
	
	/**
	 * 填充并返回请求内容 <br>
	 * 请求内容指的是不包含ContentLength和RequestTail的中间部分 <br>
	 * 
	 * @return
	 */
	private String getContent() {
		if (null != content) {
			return content;
		}

		StringBuilder sb = new StringBuilder("" + split);
		sb.append(searchDescription + split);
		sb.append(searchService + split);
		sb.append(categorys + split);
		sb.append(videoFormat);
		sb.append(videoLength + split);
		sb.append(String.valueOf(partnerId) + split);
		sb.append(limitDate + split);
		sb.append(String.valueOf(pageId) + split);
		sb.append(String.valueOf(queryLength) + split);
		sb.append(queryString + split);
		
		content = sb.toString();
		return content;
	}

	public String getContentLength() throws UnsupportedEncodingException {
		String contentLength = StringUtil
				.fillWithZero(
						getContent().getBytes(
								Constant.Socket.MESSAGE_CHARSET_NAME).length
								+ Constant.Socket.REQUEST_END.length(), 8);

		return contentLength;
	}

	/**
	 * 将请求体解析为服务端规定格式的字符串返回 <br>
	 * 请求体指的是不包含RequestHead和RequestTail的中间部分 <br>
	 * 在调用此方法前，包括contentLength在内的字段必须都已经填充好 <br>
	 * 
	 * @return
	 */
	public String parseRequest() {
		return contentLength + getContent();
	}
}
