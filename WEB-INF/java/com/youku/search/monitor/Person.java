package com.youku.search.monitor;

public class Person {
	public static final Person luwei = new Person("luwei@youku.com",
			"13520573272");
	public static final Person wubin = new Person("wubin@youku.com",
			"13488750198");
	public static final Person tanxiuguang = new Person(
			"tanxiuguang@youku.com", "15210089086");
	public static final Person shifangfang = new Person(
			"shifangfang@youku.com", "13552846664");
	public static final Person gaosong = new Person("gaosong@youku.com", "13520806042");
	public static final Person zhenghailong = new Person(
			"zhenghailong@youku.com", "13520441880");

	private String[] emails = null;
	private String phone = null;

	private Person(String email, String phone) {
		this(new String[] { email }, phone);
	}

	private Person(String[] emails, String phone) {
		this.emails = emails;
		this.phone = phone;
	}

	public String[] getEmails() {
		return emails;
	}

	public String getPhone() {
		return phone;
	}

}
