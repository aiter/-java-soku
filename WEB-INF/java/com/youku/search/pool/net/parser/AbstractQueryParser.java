package com.youku.search.pool.net.parser;

import java.util.BitSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.BasePageQuery;
import com.youku.search.pool.net.mina.Request;

/**
 * 
 * @author gaosong <br>
 * 
 * @see 将某种Query对象解析为客户端需要的字符串 <br>
 */
public abstract class AbstractQueryParser<T extends BasePageQuery> {

	protected static Log logger = LogFactory.getLog(AbstractQueryParser.class);
	
	protected static class HDOptions {
		private final byte[] hdOptions;
		private boolean hasSet = false;
		public static final int LENGTH = 16;
		
		public HDOptions() {
			this.hdOptions = new byte[LENGTH];
			for (int i = 0; i < hdOptions.length; i++) {
				hdOptions[i] = (byte)0;
			}
		}
		
		public void setBit(int bitIndex){
			this.hdOptions[bitIndex] = (byte)1;
			hasSet |= true;
		}
		
		public boolean hasSet(){
			return hasSet;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < hdOptions.length; i++) {
				if (hdOptions[i] == (byte)1) {
					builder.append('1');
				} else {
					builder.append('0');
				}
			}
			return builder.toString();
		}
	}

	/**
	 * 发给C-Server的64位描述信息
	 * 
	 * @author gaosong
	 */
	protected static class VideoSearch64BOptions {
		private BitSet bitSet = new BitSet(64);
	
		/**
		 * 按照视频类别（原创、电视剧、电影等）过滤
		 */
		public void filterByCategorys() {
			bitSet.set(7);
			bitSet.set(35);
		}
	
		/**
		 * 按照高清画质过滤
		 */
		public void filterByHD() {
			bitSet.set(7);
			bitSet.set(34);
		}
	
		/**
		 * 按照视频时长过滤 <br>
		 * 根据参数设置64位的相应位，并且返回request.videoLength的值
		 * 
		 * @param min
		 *            大于等于n分钟
		 * @param max
		 *            小于n分钟
		 */
		public String filterByVideoTime(int min, int max) {
			min = min*60;
			max = max*60;
			if (min == 0 && max == 0) {
				return Request.DEFAULT_VIDEO_LENGTH;
			} else if (min >= 0 && min < max) {
				bitSet.set(7);
				bitSet.set(33);
				return "" + min + "," + max;
			} else if (min > 0 && max == 0) {
				bitSet.set(7);
				bitSet.set(33);
				return "" + min + "," + Request.MAX_VIDEO_LENGTH;
			} else if (min > 0 && min == max) {
				bitSet.set(7);
				bitSet.set(33);
				return "" + min + "," + max;
			} else {
				return Request.DEFAULT_VIDEO_LENGTH;
			}
		}
	
		/**
		 * 按 最新发布 过滤
		 */
		public void filterByCreateTime() {
			bitSet.set(5);
		}
	
		/**
		 * 按 最多播放 过滤
		 */
		public void filterByTotalPv() {
			bitSet.set(6);
		}
	
		/**
		 * 按 评论
		 */
		public void filterByComment() {
			bitSet.set(8);
		}
	
		/**
		 * 按 收藏
		 */
		public void filterByFavorite() {
			bitSet.set(9);
		}
	
		/**
		 * 按 标签
		 */
		public void filterByTag() {
			bitSet.set(15);
		}
	
		/**
		 * 按照发布时间
		 */
		public void filterByPubTime() {
			bitSet.set(7);
			bitSet.set(32);
		}
		
		/**
		 * 按照MD5
		 */
		public void filterByMD5() {
			bitSet.set(3);
		}
		
		public void filterByExcludeCategorys(){
			bitSet.set(7);
			bitSet.set(35);
			bitSet.set(37);
		}
		
		public void filterByPartnerId(){
			bitSet.set(7);
			bitSet.set(36);
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < bitSet.size(); i++) {
				if (bitSet.get(i)) {
					builder.append('1');
				} else {
					builder.append('0');
				}
			}
			return builder.toString();
		}
	
	}

	/**
	 * 只在QueryParserFactory工厂中构造，外面不要构造此对象
	 */
	AbstractQueryParser() {
	}

	public abstract String parseBody(T query) throws Exception;
	
}
