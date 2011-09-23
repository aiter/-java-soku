package com.youku.soku.manage.admin.copyright.action;

import com.opensymphony.xwork2.Action;
import com.youku.soku.manage.admin.copyright.load.CopyrightConf;
import com.youku.soku.manage.admin.copyright.load.CopyrightConfPeer;
import com.youku.soku.manage.admin.copyright.service.CopyrightConfService;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.PageInfo;

public class CopyrightConfAction extends BaseActionSupport {
	private static final long serialVersionUID = 1L;
	private CopyrightConf conf;
	private PageInfo pageInfo;
	private int pageNumber;
	private String keyword;
	private int cid;

	public String save() {
		try {
			if (conf.getId() == 0)
				conf.save();
			else
				CopyrightConfPeer.doUpdate(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public String list() {
		if (null == pageInfo)
			pageInfo = new PageInfo();
		pageInfo.setPageSize(20);
		if (getPageNumber() == 0) {
			setPageNumber(1);
		}
		pageInfo.setCurrentPageNumber(getPageNumber());
		pageInfo = CopyrightConfService.getAllCopyrightConf(pageInfo, keyword);
		return Action.SUCCESS;
	}

	public String destroy() {
		try {
			CopyrightConf _conf = CopyrightConfPeer.retrieveByPK(cid);
			if (null != _conf)
				CopyrightConfPeer.doDelete(_conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public CopyrightConf getConf() {
		return conf;
	}

	public void setConf(CopyrightConf conf) {
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
