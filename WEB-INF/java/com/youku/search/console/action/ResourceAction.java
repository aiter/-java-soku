package com.youku.search.console.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.rights.MenuMgt;
import com.youku.search.console.operate.rights.ResourceMgt;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Menu;
import com.youku.search.console.pojo.Resource;
import com.youku.search.console.vo.ResourceVO;

public class ResourceAction extends ActionSupport implements ServletRequestAware {
	private String[] resourceids;
	private String resid;
	private Resource r;
	private List<ResourceVO> rl = new ArrayList<ResourceVO>();
	private HttpServletRequest request;
	private List<Menu> ml = new ArrayList<Menu>();
	private String[] menuids;
	private UserMgt um = UserMgt.getInstance();
	private ResourceMgt rm = ResourceMgt.getInstance();

	public String[] getMenuids() {
		return menuids;
	}

	public void setMenuids(String[] menuids) {
		this.menuids = menuids;
	}

	public List<Menu> getMl() {
		return ml;
	}

	public void setMl(List<Menu> ml) {
		this.ml = ml;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getResid() {
		return resid;
	}

	public void setResid(String resid) {
		this.resid = resid;
	}

	public String[] getResourceids() {
		return resourceids;
	}

	public void setResourceids(String[] resourceids) {
		this.resourceids = resourceids;
	}

	public Resource getR() {
		return r;
	}

	public void setR(Resource r) {
		this.r = r;
	}
	

	public List<ResourceVO> getRl() {
		return rl;
	}

	public void setRl(List<ResourceVO> rl) {
		this.rl = rl;
	}

	public String list() {
		try {
			rl = rm.findAllResVO();
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String toAdd() {
		return SUCCESS;
	}

	public String store() {
		try {
			rm.save(r);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;

	}
	
	public String match() {
		try {
			if(null!=menuids){
			String menuid=menuids[0];
			rm.match(resid, menuid);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String unMatch() {
		try {
			resid=request.getParameter("resid");
			rm.unmatch(resid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String toMatch() {
		MenuMgt mm = MenuMgt.getInstance();
		try {
			resid=request.getParameter("resid");
			ml =mm.findMenusResource(false);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String remove() {
		for (int i = 0; i < resourceids.length; i++){
			try {
				rm.deleteByKey(resourceids[i]);
			} catch (TorqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
}
