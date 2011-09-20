package com.youku.soku.newext.info;

public class ExtInfoHolder {

	private static ExtInfo info = new ExtInfo();
	private static ThreadLocal<ExtInfo> threadLocal = new ThreadLocal<ExtInfo>();

	public static ExtInfo getCurrent() {
		return info;
	}

	public static void setCurrent(ExtInfo extInfo) {
		info = extInfo;
	}

	public static ExtInfo getCurrentThreadLocal() {
		ExtInfo i = threadLocal.get();

		if (i == null) {
			i = info;
			threadLocal.set(i);
		}
		
		//TODO 测试，更新后，有的线程还指向老数据
		//如果是老数据，就删除，再取一次  begin
		if(i.isDestroy){
			removeCurrentThreadLocal();
		
			i = threadLocal.get();
	
			if (i == null) {
				i = info;
				threadLocal.set(i);
			}
		}
		//如果是老数据，就删除，再取一次  end
		
		return i;
	}

	public static void removeCurrentThreadLocal() {
		threadLocal.remove();
	}
}
