package com.youku.soku.manage.admin.copyright.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.torque.Torque;

import com.opensymphony.xwork2.Action;
import com.youku.soku.manage.admin.copyright.load.ActConfig;
import com.youku.soku.manage.admin.copyright.load.ActConfigPeer;
import com.youku.soku.manage.admin.copyright.service.ActConfigService;
import com.youku.soku.manage.admin.copyright.util.CopyrightSpiderTimer;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.PageInfo;
import common.Logger;

public class ActConfigAction extends BaseActionSupport {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(this.getClass());

	private ActConfig conf;
	private PageInfo pageInfo;
	private int pageNumber;
	private String keyword;
	private int cid;

	public String input() {
		try {
			if (cid == 0)
				return "input";
			conf = ActConfigPeer.retrieveByPK(cid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "input";
	}

	public String save() {
		try {
			log.debug("*** save conf:"+conf+conf.getId()==0+" ***");
			if (conf.getId() == 0)
				conf.save();
			else {
				conf.setNew(false);
				ActConfigPeer.doUpdate(conf);
			}
			log.debug("*** save conf:"+conf+" ***");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "input";
	}

	public String list() {
		if (null == pageInfo)
			pageInfo = new PageInfo();
		pageInfo.setPageSize(20);
		if (getPageNumber() == 0) {
			setPageNumber(1);
		}
		pageInfo.setCurrentPageNumber(getPageNumber());
		pageInfo = ActConfigService.getAllActConfig(pageInfo, keyword);
		return Action.SUCCESS;
	}

	public String spider() {
		String result = ActConfigService.spider(cid, log);

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(result );
		return null;
	}

	public String spiderTimer() {
		CopyrightSpiderTimer timer =new CopyrightSpiderTimer();
		log.debug("******* start conf spider timer:"+timer+" *****");
		timer.start();
		log.debug("******* end conf spider timer:"+timer+" *****");
		return null;
	}

	public String destroy() {
		try {
			ActConfig _conf = ActConfigPeer.retrieveByPK(cid);
			if (null != _conf)
				ActConfigPeer.doDelete(_conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public ActConfig getConf() {
		return conf;
	}

	public void setConf(ActConfig conf) {
		this.conf = conf;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

}
