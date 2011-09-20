package com.youku.search.index.entity.index;

import com.youku.search.util.MyUtil;

public class IndexVideo {
	
	private int vid;
	private String title;
	private String tags;
	private String memo;
	private long createtime;
	private int total_pv;
	private int total_comment;
	private int total_fav;
	private int cate_ids;
    private int seconds;
    private String owner_username;
    private String md5;
    private String ftype;
    private int partnerId;
    private int lastvv;
    
    private static String separator = "\t";
    
    public String toString(){
    	StringBuilder builder = new StringBuilder();
    	builder.append(title).append(separator);
    	builder.append(separator);
    	builder.append(vid).append(separator);
    	builder.append(seconds).append(separator);
    	builder.append(createtime).append(separator);
    	builder.append(total_comment).append(separator);
    	builder.append(total_fav).append(separator);
    	builder.append(owner_username).append(separator);
    	builder.append(MyUtil.formatString(ftype)).append(separator);
    	builder.append(MyUtil.formatString(tags)).append(separator);
    	builder.append(cate_ids).append(separator);
    	builder.append(partnerId).append(separator);
    	builder.append(total_pv).append(separator);
    	builder.append(md5).append(separator);
    	builder.append(lastvv);
    	
    	return builder.toString();
    }
    
    
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getTotal_pv() {
		return total_pv;
	}
	public void setTotal_pv(int total_pv) {
		this.total_pv = total_pv;
	}
	public int getTotal_comment() {
		return total_comment;
	}
	public void setTotal_comment(int total_comment) {
		this.total_comment = total_comment;
	}
	public int getTotal_fav() {
		return total_fav;
	}
	public void setTotal_fav(int total_fav) {
		this.total_fav = total_fav;
	}
	public int getCate_ids() {
		return cate_ids;
	}
	public void setCate_ids(int cate_ids) {
		this.cate_ids = cate_ids;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	public String getOwner_username() {
		return owner_username;
	}
	public void setOwner_username(String owner_username) {
		this.owner_username = owner_username;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}


	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}


	public int getPartnerId() {
		return partnerId;
	}


	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}


	public int getLastvv() {
		return lastvv;
	}


	public void setLastvv(int lastvv) {
		this.lastvv = lastvv;
	}


	public static String getSeparator() {
		return separator;
	}


	public static void setSeparator(String separator) {
		IndexVideo.separator = separator;
	}
    
    
}
