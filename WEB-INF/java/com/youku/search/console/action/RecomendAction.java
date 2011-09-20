package com.youku.search.console.action;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.operate.SearchRecomendLog;
import com.youku.search.console.operate.TrietreerecommendOperator;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Trietreerecommend;
import com.youku.search.console.pojo.User;
import com.youku.search.hanyupinyin.Converter;
import com.youku.search.util.DataFormat;

public class RecomendAction implements SessionAware, ServletRequestAware {

	private Trietreerecommend t;
	private String[] tids;
	private Map att;
	private String tid;
	private HttpServletRequest request;
	private List<Trietreerecommend> tl;
	private String keyword;
	int page = 0;
	int maxpage = 0;
	int totalSize=0;
	String encodeKeyword;

	public String getEncodeKeyword() {
		return encodeKeyword;
	}

	public void setEncodeKeyword(String encodeKeyword) {
		this.encodeKeyword = encodeKeyword;
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

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public List<Trietreerecommend> getTl() {
		return tl;
	}

	public void setTl(List<Trietreerecommend> tl) {
		this.tl = tl;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public Trietreerecommend getT() {
		return t;
	}

	public void setT(Trietreerecommend t) {
		this.t = t;
	}

	public String[] getTids() {
		return tids;
	}

	public void setTids(String[] tids) {
		this.tids = tids;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}

	public Map getAtt() {
		return att;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public String toAdd() {
		t=new Trietreerecommend();
		return Action.SUCCESS;
	}

	public String save() {
		User u = null;
		try {
			u = UserMgt.getInstance().findById("" + att.get("user_id"));
		} catch (NoRowsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TooManyRowsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TorqueException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String uname = null;
		if (null != u)
			uname = u.getTrueName();
		t.setCreator(uname);
		if (t.getStarttime() == null)
			t.setStarttime(new Date());
		if (t.getEndtime() == null)
			t.setEndtime(new Date());
		t.setCreatetime(new Timestamp(System.currentTimeMillis()));
		t.setKeywordPy(Converter.convert(t.getKeyword()));
		TrietreerecommendOperator to = new TrietreerecommendOperator();
		Connection conn = null;
		try {
			conn = DataConn.getSearchRecomendConn();
			to.save(t, conn);
			SearchRecomendLog.writeLog(1, "添加", t, null);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public String toUpdate() {
		int id = DataFormat.parseInt(tids[0]);
		if (id == 0)
			return Action.ERROR;
		TrietreerecommendOperator to = new TrietreerecommendOperator();
		Connection conn = null;
		try {
			conn = DataConn.getSearchRecomendConn();
			t =to.getTrietreerecommend(id, conn);
		} catch (NoRowsException e) {
			e.printStackTrace();
			return Action.ERROR;
		} catch (TooManyRowsException e) {
			e.printStackTrace();
			return Action.ERROR;
		} catch (TorqueException e) {
			e.printStackTrace();
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	public String update() {
		TrietreerecommendOperator to = new TrietreerecommendOperator();
		Connection conn = null;
		try {
			conn = DataConn.getSearchRecomendConn();
			Trietreerecommend tempt = to.getTrietreerecommend(t.getId(), conn);
			User u = UserMgt.getInstance().findById("" + att.get("user_id"));
			String uname = null;
			if (null != u)
				uname = u.getTrueName();
			t.setCreator(uname);
			t.setCreatetime(new Timestamp(System.currentTimeMillis()));
			if (t.getStarttime() == null)
				t.setStarttime(new Date());
			if (t.getEndtime() == null)
				t.setEndtime(new Date());
			t.setKeywordPy(Converter.convert(t.getKeyword()));
			to.update(t, conn);
			SearchRecomendLog.writeLog(2, "修改", t, tempt);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public String delete() {
		if (null != tids && tids.length > 0) {
			TrietreerecommendOperator to = new TrietreerecommendOperator();
			Connection conn = null;
			try {
				conn = DataConn.getSearchRecomendConn();
			} catch (TorqueException e) {
				e.printStackTrace();
			}
			if (null != conn) {
				Trietreerecommend tempt = null;
				for (String id : tids) {
					try {
						tempt = to.getTrietreerecommend(
								DataFormat.parseInt(id), conn);
						to.delete(id, conn);
						SearchRecomendLog.writeLog(3, "删除", null, tempt);
					} catch (TorqueException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return Action.SUCCESS;
	}

	public String list() {
		Connection conn = null;
		try {
			conn = DataConn.getSearchRecomendConn();
			listRecomend(conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataConn.releaseConn(conn);
		}
		return Action.SUCCESS;
	}

	public void listRecomend(Connection conn) {
		TrietreerecommendOperator to = new TrietreerecommendOperator();
		if (keyword != null && keyword.trim().length() > 0)
			keyword = keyword.trim();
		totalSize = to.searchSize(keyword, conn);
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
		tl = to.search(keyword, page, Constants.PAGESIZE, conn);
		try {
			encodeKeyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			encodeKeyword = keyword;
		}
		request.setAttribute("cuurpage", page);
		request.setAttribute("cuurmaxpage", maxpage);
	}
}
