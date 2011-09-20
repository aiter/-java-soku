package com.youku.soku.manage.admin.library;

import org.apache.log4j.Logger;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.opensymphony.xwork2.Action;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.ProgrammeEpisodeService;

/**
 * @author lyu302
 * 
 *         以节目为显示单位的操作集 exam：youku的非高清视频
 */
public class ProgrammeAction extends BaseActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	private int cate = 0;// 0：默认 1：电视剧 2：电影 5：动漫
	private int ob = 0;// 0：默认 1：发行时间 2：节目搜索热度
	private int resource = -1;// -1：默认 1：有高清资源 0：无高清资源
	private int pageNumber;
	private String searchWord;

	private int peid = 0;

	private PageInfo pageInfo;

	public String plain() {
		log.debug("======start search plain programme=======");
		if (null == pageInfo)
			pageInfo = new PageInfo();
		pageInfo.setPageSize(10);
		if (getPageNumber() == 0) {
			setPageNumber(1);
		}
		pageInfo.setCurrentPageNumber(getPageNumber());
		pageInfo = ProgrammeEpisodeService.getPlainPage(pageInfo, searchWord,
				cate, ob, resource, log);
		log.debug("======end search " + pageInfo.getResults() == null ? -1
				: pageInfo.getResults().size() + " plain programme=======");
		return Action.SUCCESS;
	}

	public String editResource() {
		try {
			log.debug("===== start edit resource ====");
			ProgrammeEpisode pe = ProgrammeEpisodePeer.retrieveByPK(peid);
			if (null == pe)
				return null;
			pe.setResource(resource);
			ProgrammeEpisodePeer.doUpdate(pe);
			log.debug("===== end edit resource ====");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getPeid() {
		return peid;
	}

	public void setPeid(int peid) {
		this.peid = peid;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getOb() {
		return ob;
	}

	public void setOb(int ob) {
		this.ob = ob;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
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

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

}
