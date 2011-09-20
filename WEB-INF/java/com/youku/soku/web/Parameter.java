/**
 * 
 */
package com.youku.soku.web;

import javax.servlet.http.HttpServletRequest;

import com.youku.search.util.DataFormat;
import com.youku.soku.util.Constant;
import com.youku.soku.util.MyUtil;

/**
 * @author 1verge
 *
 */
public class Parameter {
	
	private String keyword;
	private int pageNum;
	private byte sort;
	private byte timeLength;
	private byte dateLimit;
	private byte hd;
	private byte site;
	
	private int version_id;
	private int site_version_id;
	private int cate_id;
	private int name_id;
	private byte ext;
	private int target_cate_id;
	
	
	private String total_sites;
	
	public Parameter(HttpServletRequest request)
	{
		keyword = request.getParameter("word");
		pageNum = DataFormat.parseInt(request.getParameter(ReceiverParamer.PAGENUM),DefaultValue.PAGENUM);
		sort = DataFormat.parseByte(request.getParameter(ReceiverParamer.SORT),DefaultValue.SORT);
		timeLength = DataFormat.parseByte(request.getParameter(ReceiverParamer.TIMELENGTH),DefaultValue.TIMELENGTH);
		dateLimit = DataFormat.parseByte(request.getParameter(ReceiverParamer.DATELIMIT),DefaultValue.DATELIMIT);
		hd = DataFormat.parseByte(request.getParameter(ReceiverParamer.HD),DefaultValue.HD);
		site = DataFormat.parseByte(request.getParameter(ReceiverParamer.SITE),DefaultValue.SITE);
		
		version_id = DataFormat.parseInt(request.getParameter(ReceiverParamer.VERSION_ID));
		site_version_id = DataFormat.parseInt(request.getParameter(ReceiverParamer.SITE_VERSION_ID));
		cate_id = DataFormat.parseInt(request.getParameter(ReceiverParamer.CATE_ID));
		name_id = DataFormat.parseInt(request.getParameter(ReceiverParamer.NAME_ID));
		ext = DataFormat.parseByte(request.getParameter(ReceiverParamer.EXT),DefaultValue.EXT);
		target_cate_id = DataFormat.parseInt(request.getParameter(ReceiverParamer.TARGET_CATE_ID));
		
		//针对指定站点的情况，传递原来所有站点值
		if (site>0)
			total_sites = request.getParameter(ReceiverParamer.TOTAL_SITES);
		request.setAttribute("parameter", this);
	}
	
	public String getPagePrefixString()
	{
		StringBuilder builder = new StringBuilder(Constant.Web.SEARCH_URL);
		builder.append("?");
		if (keyword != null)
		{
			builder.append(ReceiverParamer.KEYWORD+"=");
			builder.append(MyUtil.urlEncode(keyword));
			builder.append("&");
		}
		
		if (sort > 0)
		{
			builder.append(ReceiverParamer.SORT+"=");
			builder.append(sort);
			builder.append("&");
		}
		if (timeLength > 0)
		{
			builder.append(ReceiverParamer.TIMELENGTH+"=");
			builder.append(timeLength);
			builder.append("&");
		}
		if (dateLimit > 0)
		{
			builder.append(ReceiverParamer.DATELIMIT+"=");
			builder.append(dateLimit);
			builder.append("&");
		}
		if (hd > 0)
		{
			builder.append(ReceiverParamer.HD+"=");
			builder.append(hd);
			builder.append("&");
		}
		if (site > 0)
		{
			builder.append(ReceiverParamer.SITE+"=");
			builder.append(site);
			builder.append("&");
		}
		if (ext >0)
		{
			builder.append(ReceiverParamer.EXT+"=");
			builder.append(ext);
			builder.append("&");
		}
		if (total_sites != null)
		{
			builder.append(ReceiverParamer.TOTAL_SITES+"=");
			builder.append(total_sites);
			builder.append("&");
		}
		
		builder.append(ReceiverParamer.PAGENUM + "=");
		
		return builder.toString();
	}
	
	
	

	public int getTarget_cate_id() {
		return target_cate_id;
	}

	public void setTarget_cate_id(int target_cate_id) {
		this.target_cate_id = target_cate_id;
	}

	public String getTotal_sites() {
		return total_sites;
	}

	public void setTotal_sites(String total_sites) {
		this.total_sites = total_sites;
	}

	public byte getExt() {
		return ext;
	}

	public void setExt(byte ext) {
		this.ext = ext;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public byte getSort() {
		return sort;
	}

	public void setSort(byte sort) {
		this.sort = sort;
	}

	public int getCate_id() {
		return cate_id;
	}

	public void setCate_id(int cate_id) {
		this.cate_id = cate_id;
	}

	public int getName_id() {
		return name_id;
	}

	public void setName_id(int name_id) {
		this.name_id = name_id;
	}

	public byte getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(byte timeLength) {
		this.timeLength = timeLength;
	}

	public byte getDateLimit() {
		return dateLimit;
	}

	public int getVersion_id() {
		return version_id;
	}

	public void setVersion_id(int version_id) {
		this.version_id = version_id;
	}

	public int getSite_version_id() {
		return site_version_id;
	}

	public void setSite_version_id(int site_version_id) {
		this.site_version_id = site_version_id;
	}

	public void setDateLimit(byte dateLimit) {
		this.dateLimit = dateLimit;
	}

	public byte getHd() {
		return hd;
	}

	public void setHd(byte hd) {
		this.hd = hd;
	}

	public byte getSite() {
		return site;
	}

	public void setSite(byte site) {
		this.site = site;
	}

	public String getSearchString()
	{
		StringBuilder builder = new StringBuilder("?");
		if (keyword != null)
		{
			builder.append(SendParamer.KEYWORD+"=");
			builder.append(MyUtil.urlEncode(keyword));
			builder.append("&");
		}
		if (pageNum > 1)
		{
			builder.append(SendParamer.PAGENUM+"=");
			builder.append(pageNum);
			builder.append("&");
		}
		if (sort > 0)
		{
			builder.append(SendParamer.SORT+"=");
			builder.append(sort);
			builder.append("&");
		}
		if (timeLength > 0)
		{
			builder.append(SendParamer.TIMELENGTH+"=");
			builder.append(timeLength);
			builder.append("&");
		}
		if (dateLimit > 0)
		{
			builder.append(SendParamer.DATELIMIT+"=");
			builder.append(dateLimit);
			builder.append("&");
		}
		if (hd > 0)
		{
			builder.append(SendParamer.HD+"=");
			builder.append(hd);
			builder.append("&");
		}
		if (site > 0)
		{
			builder.append(SendParamer.SITE+"=");
			builder.append(site);
			builder.append("&");
		}
		if (ext >0)
		{
			builder.append(ReceiverParamer.EXT+"=");
			builder.append(ext);
			builder.append("&");
		}
		builder.append("hl=true&relnum=12&pagesize="+Constant.Web.SEARCH_PAGESIZE);
		return builder.toString();
	}
	
	public String getExtSearchString()
	{
		StringBuilder builder = new StringBuilder("?");
				
		if (version_id > 0)
		{
			builder.append(SendParamer.VERSION_ID+"=");
			builder.append(version_id);
			builder.append("&");
		}
		if (site_version_id > 0)
		{
			builder.append(SendParamer.SITE_VERSION_ID+"=");
			builder.append(site_version_id);
			builder.append("&");
		}
		if (cate_id > 0)
		{
			builder.append(SendParamer.CATE_ID+"=");
			builder.append(cate_id);
			builder.append("&");
		}
		if (name_id > 0)
		{
			builder.append(SendParamer.NAME_ID+"=");
			builder.append(name_id);
			builder.append("&");
		}
		if (target_cate_id > 0)
		{
			builder.append(SendParamer.TARGET_CATE_ID+"=");
			builder.append(target_cate_id);
			builder.append("&");
		}
		
		if (keyword != null){
			builder.append(SendParamer.KEYWORD+"=");
			builder.append(MyUtil.urlEncode(keyword));
			builder.append("&");
		}
		
		builder.append("relnum=12");
		return builder.toString();
		
	}
	
	static class DefaultValue{
		static final int PAGENUM = 1;
		static final byte SORT = 1;
		static final byte TIMELENGTH = 0;
		static final byte DATELIMIT = 0;
		static final byte HD = 0;
		static final byte SITE = 0;
		static final byte EXT = 1;
		
	}
	static class ReceiverParamer{
		static final String KEYWORD = "word";
		static final String PAGENUM = "pn";
		static final String SORT = "s";
		static final String TIMELENGTH = "len";
		static final String DATELIMIT = "dl";
		static final String HD = "hd";
		static final String SITE = "site";
		static final String VERSION_ID = "ver";
		static final String SITE_VERSION_ID = "sv";
		static final String CATE_ID = "ca";
		static final String NAME_ID = "na";
		static final String EXT = "ext";
		static final String TARGET_CATE_ID = "tca";
		static final String TOTAL_SITES = "ts";
	} 
	static class SendParamer{
		static final String KEYWORD = "keyword";
		static final String PAGENUM = "curpage";
		static final String SORT = "orderfield";
		static final String TIMELENGTH = "time_length";
		static final String DATELIMIT = "limit_date";
		static final String HD = "hd";
		static final String SITE = "site";
		static final String VERSION_ID = "version_id";
		static final String SITE_VERSION_ID = "site_version_id";
		static final String CATE_ID = "cate_id";
		static final String NAME_ID = "names_id";
		static final String TARGET_CATE_ID = "target_cate_id";
		static final String EXT = "ext";
	} 
}
