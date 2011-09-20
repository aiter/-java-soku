package com.youku.search.sort.core.entity;


public class Page {
	public Page() {
	}

	public Page(int page_no, int page_size) {
		this.page_no = page_no;
		this.page_size = page_size;
	}

	public int page_no;
	public int page_size;

	public int start() {
		return (page_no - 1) * page_size;
	}

	public int end() {
		return page_no * page_size;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("page_no: ");
		builder.append(page_no);
		builder.append(", ");

		builder.append("page_size: ");
		builder.append(page_size);
		builder.append(", ");

		builder.append("start: ");
		builder.append(start());
		builder.append(", ");

		builder.append("end: ");
		builder.append(end());

		return builder.toString();
	}

	public static void main(String[] args) {
		Page page = new Page();
		page.page_no = 2;
		page.page_size = 10;

		System.out.println(page);

		ConvertPage convertPage = new ConvertPage(page, 9);
		System.out.println(convertPage);

	}
}
