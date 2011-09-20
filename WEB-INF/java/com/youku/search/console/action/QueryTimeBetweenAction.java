package com.youku.search.console.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Action;
import com.youku.soku.library.quick.top.QuickTopDataWebMgt;
import com.youku.top.quick.QuickVO;

public class QueryTimeBetweenAction implements ServletRequestAware {

	HttpServletRequest request;
	String message;
	String filename;
	String downfilename;
	String downdate = null;
	String lastdate;
	int isMonth = 0;
	Map<String,List<QuickVO>> qvomap = new HashMap<String, List<QuickVO>>();
	int limit = 50;
	
	public String getLastdate() {
		return lastdate;
	}

	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getIsMonth() {
		return isMonth;
	}

	public void setIsMonth(int isMonth) {
		this.isMonth = isMonth;
	}

	public Map<String, List<QuickVO>> getQvomap() {
		return qvomap;
	}

	public void setQvomap(Map<String, List<QuickVO>> qvomap) {
		this.qvomap = qvomap;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFilename() {
		return filename;
	}

	public String getDowndate() {
		return downdate;
	}

	public void setDowndate(String downdate) {
		this.downdate = downdate;
	}

	private void buildBetweenDate(){
		if(isMonth==1){
			this.downdate = QuickTopDataWebMgt.getLastMonthBetween();
			this.lastdate = QuickTopDataWebMgt.getLastLastMonthBetween();
		}else if(isMonth==2){
			this.downdate = QuickTopDataWebMgt.getLastWeekBetween();
			this.lastdate = QuickTopDataWebMgt.getLastLastWeekBetween();
		}else if(isMonth==3){
			this.downdate = QuickTopDataWebMgt.getLastMonth21Between();
			this.lastdate = QuickTopDataWebMgt.getLastLastMonth21Between();
		}
	}
	
	public InputStream getInputStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		putDateToOutputStream(out);
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public void putDateToOutputStream(OutputStream os){
		if(StringUtils.isBlank(downdate)){
			message="时间跨度为空";
			return;
		}
		qvomap = QuickTopDataWebMgt.getInstance().getQuickVODataReturnMapOrderByQueryCount(downdate, limit);
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = null;
			WritableFont writefont = new WritableFont(WritableFont.ARIAL, 20,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.GREEN);
			WritableCellFormat writecellfont = new WritableCellFormat(writefont);
			WritableFont writefontkeyword = new WritableFont(WritableFont.ARIAL, 12,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat writecellfontkeyword = new WritableCellFormat(writefontkeyword);
			int i = 0;
			for(Entry<String, List<QuickVO>> entry:qvomap.entrySet()){
				sheet = workbook.createSheet(entry.getKey(), i);
				sheet.addCell(new jxl.write.Label(0,0,"关键词",writecellfont));
				sheet.addCell(new jxl.write.Label(4,0,downdate,writecellfont));
				sheet.addCell(new jxl.write.Label(10,0,lastdate,writecellfont));
				int j = 1;
				for(QuickVO qvo:entry.getValue()){
					sheet.addCell(new jxl.write.Label(0,j,qvo.getKeyword(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(4,j,""+qvo.getQuery_count1(),writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(10,j,""+qvo.getQuery_count2(),writecellfontkeyword));
					j++;
				}
				i++;
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public String down() {
		return Action.SUCCESS;
	}

	public String query() {
		buildBetweenDate();
		System.out.println(downdate+","+limit);
		if(StringUtils.isBlank(downdate)){
			message="时间跨度为空";
			return Action.SUCCESS;
		}
		qvomap = QuickTopDataWebMgt.getInstance().getQuickVODataReturnMapOrderByQueryCount(downdate, limit);
//		System.out.println("----------"+qvomap.size());
		return Action.SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getDownfilename() {
		filename = buildFileName(downdate,lastdate);
		String downfilename = filename;
		try {
			downfilename = new String(downfilename.getBytes(), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return downfilename;
	}

	public String buildFileName(String down,String last) {
		StringBuilder bf = new StringBuilder();
		bf.append("搜索Top_");
		bf.append(down);
		bf.append("____");
		bf.append(last);
		bf.append(".xls");
		return bf.toString();
	}

}
