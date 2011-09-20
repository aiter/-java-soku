package com.youku.search.sort.core.entity;

public class Span {

	public Span() {
	}

	public Span(int start, int end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * 用户视图当前页第一条记录位置-Index集群视图当前页第一条记录位置的偏移量
	 */
	public int start;
	
	/**
	 * start+用户视图的page_size
	 */
	public int end;

	/**
	 * @return 用户视图的page_size
	 */
	public int size() {
		return end - start;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("start: ");
		builder.append(start);
		builder.append(", ");

		builder.append("end: ");
		builder.append(end);
		builder.append(", ");

		builder.append("size: ");
		builder.append(size());

		return builder.toString();
	}

	public static void main(String[] args) {
		Span span = new Span();
		span.start = 2;
		span.end = 10;

		System.out.println(span);

	}
}
