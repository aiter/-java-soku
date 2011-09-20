package com.youku.search.sort.core.entity;

/**
 * 页码转换
 */
public class ConvertPage {

	/**
	 * 原来页
	 */
	public Page fromPage;

	/**
	 * 目标页
	 */
	public Page toPage;

	/**
	 * 原来的页在结果页中的偏移量
	 */
	public Span offset = new Span();

	/**
	 * 
	 * 页码转换。目标页的大小为toPage_size。
	 */
	public ConvertPage(Page fromPage, int toPage_size) {

		int toPage_no = fromPage.start() / toPage_size + 1;
		int toPage_start = (toPage_no - 1) * toPage_size;

		this.fromPage = fromPage;
		this.toPage = new Page(toPage_no, toPage_size);

		offset.start = fromPage.start() - toPage_start;
		offset.end = offset.start + fromPage.page_size;
	}

	/**
	 * 
	 * 页码转换。把 toPage_size * toPage_count
	 * 作为目标页的大小进行转换，但是该结果页的page_size为给定的参数toPage_size。
	 * 
	 */
	public ConvertPage(Page fromPage, int toPage_size, int toPage_count) {
		this(fromPage, toPage_size * toPage_count);
		toPage.page_size = toPage_size;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("fromPage: ");
		builder.append(fromPage);
		builder.append(", ");

		builder.append("toPage: ");
		builder.append(toPage);
		builder.append(", ");

		builder.append("offset: ");
		builder.append(offset);

		return builder.toString();
	}

	public static void main(String[] args) {
		for (int i = 5; i < 15; i++) {
			Page fromPage = new Page(i, 12);
			ConvertPage convertPage = new ConvertPage(fromPage, 50, 2);
			System.out.println(convertPage);
		}
	}
}
