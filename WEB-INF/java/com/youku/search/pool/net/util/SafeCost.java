package com.youku.search.pool.net.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程安全的Cost
 * @author gaosong
 */
public class SafeCost {
	public AtomicLong start = new AtomicLong(0L);
	public AtomicLong end = new AtomicLong(0L);

	public SafeCost() {
		start.getAndSet(System.currentTimeMillis());
	}

	public SafeCost(long start) {
		this.start.getAndSet(start);
	}

	public SafeCost(long start, long end) {
		this.start.getAndSet(start);
		this.end.getAndSet(end);
	}

	public void updateStart() {
		start.getAndSet(System.currentTimeMillis());
	}

	public void updateEnd() {
		end.getAndSet(System.currentTimeMillis());
	}

	public long getCost() {
		return end.get() - start.get();
	}

	public void reset() {
		start.getAndSet(System.currentTimeMillis());
		end.getAndSet(0L);
	}

	@Override
	public String toString() {
		return "cost: " + getCost();
	}
}
