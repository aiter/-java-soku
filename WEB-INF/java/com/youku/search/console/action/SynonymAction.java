package com.youku.search.console.action;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.SynonymMgt;
import com.youku.search.console.pojo.Synonym;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

public class SynonymAction implements ServletRequestAware{
	HttpServletRequest request;
	List<Synonym> synonyms;
	int page = 0;
	int maxpage = 0;
	String keyword;
	int totalSize;
	String encodeKeyword;
	Synonym synonym;
	String result;
	int status = 1;
	String[] ids;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxpage() {
		return maxpage;
	}

	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getEncodeKeyword() {
		return encodeKeyword;
	}

	public void setEncodeKeyword(String encodeKeyword) {
		this.encodeKeyword = encodeKeyword;
	}

	public Synonym getSynonym() {
		return synonym;
	}

	public void setSynonym(Synonym synonym) {
		this.synonym = synonym;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String toAdd() {
		synonym = new Synonym();
		return Action.SUCCESS;
	}

	// public String add() {
	// if(!StringUtil.isNotNull(synonym.getKeywords())){
	// return Action.ERROR;
	// }
	// SynonymMgt smgt=new SynonymMgt();
	// try {
	// synonym.setKeywords(StringUtil.ToDBC(synonym.getKeywords()));
	// smgt.insertSynonym(synonym);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return Action.SUCCESS;
	// }

	public String select() {
		if (StringUtil.isNotNull(keyword))
			keyword = StringUtil.ToDBC(keyword.trim());
		else
			keyword = null;
		doSelect(keyword, status);
		try {
			encodeKeyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			encodeKeyword = keyword;
		}
		return Action.SUCCESS;
	}

	public void doSelect(String keyword,int status) {
		try {
			totalSize = SynonymMgt.getInstance().findSynonymsCounts(keyword, status);
		} catch (TorqueException e) {
			totalSize = 0;
			e.printStackTrace();
		}
		if (totalSize % Constants.PAGESIZE == 0)
			maxpage = totalSize / Constants.PAGESIZE;
		else
			maxpage = totalSize / Constants.PAGESIZE + 1;
		if (page < 1)
			page = 1;
		if (maxpage < 1)
			maxpage = 1;
		if (page > maxpage)
			page = maxpage;
		try {
			synonyms = SynonymMgt.getInstance().findSynonyms(keyword, page, Constants.PAGESIZE,
					status);
		} catch (TorqueException e) {
			synonyms = new ArrayList<Synonym>();
			e.printStackTrace();
		}
		request.setAttribute("cuurpage", page);
		request.setAttribute("cuurmaxpage", maxpage);
	}

	public String delete() {
		if (null != ids && ids.length > 0) {
			try {
				SynonymMgt.getInstance().deleteSynonyms(ids);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		doSelect(null,0);
		status=0;
		return Action.SUCCESS;
	}

	public String toUpdate() {
		int synonymId = DataFormat.parseInt(request.getParameter("synonymId"));
		if (synonymId < 1)
			return Action.ERROR;
		try {
			synonym = SynonymMgt.getInstance().findSynonym(synonymId);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public String doUpdate() {
		if (!StringUtil.isNotNull(synonym.getKeywords())) {
			try {
				SynonymMgt.getInstance().deleteSynonym(synonym);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				synonym.setKeywords(StringUtil.ToDBC(synonym.getKeywords()));
				synonym.setState(1);
				if (synonym.getId() > 0)
					SynonymMgt.getInstance().updateSynonym(synonym);
				else
					SynonymMgt.getInstance().insertSynonym(synonym);
			} catch (TorqueException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		doSelect(null,1);
		return Action.SUCCESS;
	}

	public String update() {
		if (null != ids && ids.length > 0) {
			int state = DataFormat.parseInt(request.getParameter("state"));
			try {
				SynonymMgt.getInstance().updateSynonyms(ids, state);
			} catch (TorqueException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			status=1;
		}
		doSelect(null,1);
		return Action.SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
}
