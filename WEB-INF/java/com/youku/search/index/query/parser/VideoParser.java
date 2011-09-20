package com.youku.search.index.query.parser;

public class VideoParser extends BaseParser{

	public static final String TITLE = "title";
	public static final String TAG = "tag";
	public static final String MEMO = "memo";
	public static final String USER = "user";
	
	@Override
	public String[] getFields() {
		return new String[]{TITLE,TAG,MEMO,USER};
	}

}
