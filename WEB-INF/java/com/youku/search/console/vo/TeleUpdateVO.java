package com.youku.search.console.vo;


public class TeleUpdateVO {
	int tid;
	int version_count;
	int is_valid=1;
	String keyword;
	int pid;
	String aliasStr;
	String idstr;
	TeleCate tc=new TeleCate();
	String exclude;
	
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	public TeleCate getTc() {
		return tc;
	}
	public void setTc(TeleCate tc) {
		this.tc = tc;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getVersion_count() {
		return version_count;
	}
	public void setVersion_count(int version_count) {
		this.version_count = version_count;
	}
	public int getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(int is_valid) {
		this.is_valid = is_valid;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public String getAliasStr() {
		return aliasStr;
	}
	public void setAliasStr(String aliasStr) {
		this.aliasStr = aliasStr;
	}
	public String getIdstr() {
		return idstr;
	}
	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("{keyword:");
		builder.append(keyword);
		builder.append(",");
		builder.append("aliasStr:");
		builder.append(aliasStr);
		builder.append(",");
		builder.append("tc:");
		builder.append(tc.toString());
		builder.append("}");
		return builder.toString();
	}
}
