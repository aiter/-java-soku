package com.youku.search.pool.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledExecutors {

	private static ScheduledExecutorService executor;

	static {
		executor = Executors.newScheduledThreadPool(2);
	}

	private ScheduledExecutors() {
	}

	public static ScheduledExecutorService common() {
		return executor;
	}

}
