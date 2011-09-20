package com.youku.search.pool.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MinaExecutors {

	private static ThreadPoolExecutor executor;

	static {
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	public static ThreadPoolExecutor common() {
		return executor;
	}

	public static ThreadPoolExecutor io() {
		return executor;
	}

	public static ThreadPoolExecutor filter() {
		return executor;
	}

}
