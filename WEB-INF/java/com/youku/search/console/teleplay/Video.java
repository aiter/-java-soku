/**
 * 
 */
package com.youku.search.console.teleplay;

public class Video{
	private int vid;
	private String encodeVid;
	private String title;
	private String logo;
	private float seconds ;
	private String index;
	private String file_id;
	private int source_type;
	
	public int getSource_type() {
		return source_type;
	}
	public void setSource_type(int sourceType) {
		source_type = sourceType;
	}
	public String getEncodeVid() {
		return encodeVid;
	}
	public void setEncodeVid(String encodeVid) {
		this.encodeVid = encodeVid;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	
	public float getSeconds() {
		return seconds;
	}
	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}
	
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	
	public String getFile_id() {
		return file_id;
	}
	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}
	public String toString()
	{
		return "vid:"+vid + "\n"+
				"encodeVid:"+encodeVid + "\n"+
				"title:"+title + "\n"+
				"logo:"+logo + "\n"+
				"seconds:"+seconds + "\n"+
				"file_id:"+file_id + "\n";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + vid;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Video other = (Video) obj;
		if (vid != other.vid)
			return false;
		return true;
	}

	
}
