package com.youku.search.console.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.rights.MenuMgt;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Menu;
import com.youku.search.console.vo.LeftMenuVO;
import com.youku.search.console.vo.MenuVO;
import com.youku.search.util.DataFormat;

public class MenuAction extends ActionSupport implements ServletRequestAware{
	private UserMgt um = UserMgt.getInstance();
	private MenuMgt mm = MenuMgt.getInstance();
	
	private List<MenuVO> ml = new ArrayList<MenuVO>();
	Menu m;
	String[] menuids;
	private List<LeftMenuVO> lmvol = new ArrayList<LeftMenuVO>();
	private Map<Integer,LeftMenuVO> lmvomap = new LinkedHashMap<Integer, LeftMenuVO>();
	private HttpServletRequest request;
	

	public List<MenuVO> getMl() {
		return ml;
	}

	public void setMl(List<MenuVO> ml) {
		this.ml = ml;
	}

	public Menu getM() {
		return m;
	}

	public void setM(Menu m) {
		this.m = m;
	}

	public String[] getMenuids() {
		return menuids;
	}

	public void setMenuids(String[] menuids) {
		this.menuids = menuids;
	}

	public List<LeftMenuVO> getLmvol() {
		return lmvol;
	}

	public void setLmvol(List<LeftMenuVO> lmvol) {
		this.lmvol = lmvol;
	}

	public String menuList() {
		ActionContext ctx = ActionContext.getContext();
		Map s = ctx.getSession();
		try {
			lmvomap = um.getPrivilege(DataFormat.parseInt((String) s.get("user_id")));
			um.printLeftMenuVO("====",lmvomap);
			if(null!=lmvomap&&lmvomap.size()>0){
				Iterator<Integer> iterator=lmvomap.keySet().iterator();
				while(iterator.hasNext()){
					lmvol.add(lmvomap.get(iterator.next()));
				}
			}
		} catch (TorqueException e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String list() {
		try {
			ml = mm.searchMenu();
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String toAdd() {
		m=new Menu();
		int fatch_id=DataFormat.parseInt(request.getParameter("fatherid"), -1);
		m.setFatherId(fatch_id);
		return SUCCESS;
	}

	public String store() {
		try {
			mm.save(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;

	}

	public String remove() {
		for (int i = 0; i < menuids.length; i++){
			try {
				mm.deleteByKey(menuids[i]);
			} catch (TorqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}
	
	public String toWelcome() {
		return SUCCESS;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
}
