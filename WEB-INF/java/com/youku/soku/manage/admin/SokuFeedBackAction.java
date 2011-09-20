package com.youku.soku.manage.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.SokuFeedbackService;
import com.youku.soku.manage.torque.SokuFeedback;

public class SokuFeedBackAction extends BaseActionSupport {
	String keyword;
	String url;
	int state = -1;
	int source = -1;
	String message;
	String createTime;
	String startTime;
	String endTime;
	private int pageNumber;
	private PageInfo pageInfo;
	String downfilename;

	private static Logger logger = Logger.getLogger(SokuFeedBackAction.class);

	public String export() {

		return SUCCESS;
	}

	public InputStream getInputStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		putDateToOutputStream(out);
		return new ByteArrayInputStream(out.toByteArray());
	}

	public void putDateToOutputStream(OutputStream os) {
		StringBuilder title = new StringBuilder("导出条件");
		if (!StringUtils.isBlank(endTime)) {
			endTime = endTime.trim();
		}
		if (!StringUtils.isBlank(startTime)) {
			startTime = startTime.trim();
			if (StringUtils.isBlank(endTime)) {
				endTime = DataFormat.formatDate(new Date(),
						DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
			}
			endTime = endTime.trim();
		}
		downfilename = "反馈";

		if (!StringUtils.isBlank(startTime)) {
			title.append(" , 反馈时间范围:" + startTime + "--" + endTime);
			downfilename = downfilename + "-" + startTime + "--" + endTime;
		} else if (!StringUtils.isBlank(endTime)) {
			title.append(" , 反馈截止时间:" + endTime);
			downfilename = downfilename + "-" + endTime;
		}
		downfilename += ".xls";
		if (1 == state)
			title.append(" , 意见:喜欢");
		else if (0 == state)
			title.append(" , 意见:不喜欢");
		if (!StringUtils.isBlank(keyword))
			title.append(" , 关键词:" + keyword.trim());

		if (!StringUtils.isBlank(url)) {
			title.append(" , 入口URL:" + url.trim());
		}
		if (!StringUtils.isBlank(message))
			title.append(" , 建议:" + message.trim());

		try {
			List<SokuFeedback> list = SokuFeedbackService.getInstance()
					.getSokuFeedbackList(keyword, state, url, startTime,
							endTime, source);
			try {
				WritableWorkbook workbook = Workbook.createWorkbook(os);
				WritableSheet sheet = null;
				WritableFont writefont = new WritableFont(WritableFont.ARIAL,
						14, WritableFont.NO_BOLD, false,
						UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
				WritableCellFormat writecellfont = new WritableCellFormat(
						writefont);
				WritableFont writefontkeyword = new WritableFont(
						WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false,
						UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
				WritableCellFormat writecellfontkeyword = new WritableCellFormat(
						writefontkeyword);
				sheet = workbook.createSheet("反馈", 0);
				sheet.getSettings().setDefaultColumnWidth(30);
				sheet.setColumnView(0, 10);
				sheet.mergeCells(0, 0, 5, 0);
				sheet.addCell(new jxl.write.Label(0, 0, title.toString(),
						writecellfont));
				sheet.addCell(new jxl.write.Label(0, 1, "来源", writecellfont));
				sheet.addCell(new jxl.write.Label(1, 1, "意见", writecellfont));
				sheet
						.addCell(new jxl.write.Label(2, 1, "入口URL",
								writecellfont));
				sheet.addCell(new jxl.write.Label(3, 1, "关键词", writecellfont));
				sheet.addCell(new jxl.write.Label(4, 1, "时间", writecellfont));
				sheet.addCell(new jxl.write.Label(5, 1, "建议", writecellfont));
				sheet.addCell(new jxl.write.Label(6, 1, "ip", writecellfont));
				int j = 2;
				String txt = "未知";
				for (SokuFeedback fd : list) {

					if (fd.getSource() == 1)
						txt = "站内";
					else if (fd.getSource() == 0)
						txt = "站外";
					else
						txt = "未知";
					sheet.addCell(new jxl.write.Label(0, j, txt,
							writecellfontkeyword));

					if (fd.getState() == 1)
						txt = "喜欢";
					else if (fd.getState() == 0)
						txt = "不喜欢";
					else
						txt = "未知";
					sheet.addCell(new jxl.write.Label(1, j, txt,
							writecellfontkeyword));
					if (!fd.getUrl().startsWith("http://")
							&& !!fd.getUrl().startsWith("www."))
						sheet.addCell(new jxl.write.Label(2, j, fd.getUrl(),
								writecellfontkeyword));
					else
						sheet.addHyperlink(new WritableHyperlink(2, j, new URL(
								fd.getUrl())));
					sheet.addCell(new jxl.write.Label(3, j, fd.getKeyword(),
							writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(4, j, DataFormat
							.formatDate(fd.getCreateTime(),
									DataFormat.FMT_DATE_YYYYMMDD_HHMMSS),
							writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(5, j, fd.getMessage(),
							writecellfontkeyword));
					sheet.addCell(new jxl.write.Label(6, j, StringUtils
							.isBlank(fd.getIpHost()) ? "" : fd.getIpHost(),
							writecellfontkeyword));
					j++;
				}
				workbook.write();
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getDownfilename() {
		try {
			downfilename = new String(downfilename.getBytes(), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return downfilename;
	}

	public String list() {

		try {
			if (null == pageInfo)
				pageInfo = new PageInfo();
			pageInfo.setPageSize(30);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}

			logger.info("keyword:" + keyword + ",state:" + state + ",url:"
					+ url + ",startTime:" + startTime + ",endTime:" + endTime);

			if (!StringUtils.isBlank(endTime)) {
				endTime = endTime.trim();
			}

			if (!StringUtils.isBlank(startTime)) {
				startTime = startTime.trim();
				if (StringUtils.isBlank(endTime)) {
					endTime = DataFormat.formatDate(new Date(),
							DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
				}
				endTime = endTime.trim();
			}

			pageInfo.setCurrentPageNumber(getPageNumber());
			SokuFeedbackService.getInstance().findSokuFeedbackPagination(
					pageInfo, keyword, state, url, startTime, endTime, source);
			pageInfo.setCurrentPageNumber(getPageNumber());
			setPageInfo(pageInfo);
		} catch (Exception e) {
			logger.error(e);
		}
		return Constants.LIST;
	}

	public void writeMsg(String msg) {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(msg);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

}
