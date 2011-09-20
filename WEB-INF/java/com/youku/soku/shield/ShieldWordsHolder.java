package com.youku.soku.shield;


public class ShieldWordsHolder {
	
	private static ShieldWordsInfo wordsInfo = new ShieldWordsInfo();
	
	private static ThreadLocal<ShieldWordsInfo> threadLocal = new ThreadLocal<ShieldWordsInfo>();
	
	public static void setCurrentWordsInfo(ShieldWordsInfo wordsInfo_) {
		wordsInfo = wordsInfo_;
	}
	
	public static ShieldWordsInfo getCurrentThreadLocal() {
		ShieldWordsInfo info = threadLocal.get();
		
		if(info == null) {
			info = wordsInfo;
			threadLocal.set(info);
		}
		
		return info;
	}
	
	public static void removeCurrentThreadLocal() {
		threadLocal.remove();
	}
}
