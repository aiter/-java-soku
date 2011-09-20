package com.youku.search.pool.impl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

/**
 * 
 * @author gaosong
 * @deprecated 未完成
 */
public class CharsetEncoderPool extends SimplePool<CharsetEncoder> {

	CharsetEncoderPool(String poolName, int corePoolSize, int minPoolSize,
			List<CharsetEncoder> initItems) {
		super(poolName, corePoolSize, minPoolSize, initItems);
	}

	@Override
	protected CharsetEncoder createItem() {
		return Charset.forName("GBK").newEncoder();
	}

	@Override
	protected boolean destroyItem(CharsetEncoder item) {
		item = null;
		return true;
	}

	@Override
	protected boolean activateItem(CharsetEncoder item) {
		item.reset();
		return true;
	}

	@Override
	protected boolean validateItem(CharsetEncoder item) {
		return true;
	}

	@Override
	protected boolean passivateObject(CharsetEncoder item) {
		item.reset();
		return true;
	}

}
