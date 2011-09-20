package com.youku.search.console.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.rights.MenuMgt;
import com.youku.search.console.operate.rights.ResourceMgt;
import com.youku.search.console.operate.rights.RoleMgt;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.Menu;
import com.youku.search.console.pojo.Role;
import com.youku.search.console.pojo.User;

public class RoleAction extends ActionSupport implements SessionAware {
	private Map att;
	private String[] roleids;
	private Role ro;
	private List<Role> rol = new ArrayList<Role>();
	private String[] menuids;
	private String[] uids;

	private UserMgt um = UserMgt.getInstance();
	private RoleMgt rom = RoleMgt.getInstance();
	private MenuMgt menum = MenuMgt.getInstance();
	private List<User> ul = new ArrayList<User>();
	private List<Menu> ml = new ArrayList<Menu>();
	

	public List<Menu> getMl() {
		return ml;
	}

	public void setMl(List<Menu> ml) {
		this.ml = ml;
	}

	public Map getAtt() {
		return att;
	}

	public void setAtt(Map att) {
		this.att = att;
	}

	public List<User> getUl() {
		return ul;
	}

	public void setUl(List<User> ul) {
		this.ul = ul;
	}

	public String[] getUids() {
		return uids;
	}

	public void setUids(String[] uids) {
		this.uids = uids;
	}
	

	public String[] getMenuids() {
		return menuids;
	}

	public void setMenuids(String[] menuids) {
		this.menuids = menuids;
	}

	public String[] getRoleids() {
		return roleids;
	}

	public void setRoleids(String[] roleids) {
		this.roleids = roleids;
	}

	public Role getRo() {
		return ro;
	}

	public void setRo(Role ro) {
		this.ro = ro;
	}

	public List<Role> getRol() {
		return rol;
	}

	public void setRol(List<Role> rol) {
		this.rol = rol;
	}

	public String list() {
		try {
			rol = rom.findAll();
		} catch (TorqueException e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String toAdd() {
		return SUCCESS;
	}

	public String store() {
		try {
			rom.save(ro);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;

	}

	public String remove() {
		boolean f=false;
		for (int i = 0; i < roleids.length; i++){
			try {
				rom.deleteByKey(roleids[i]);
			} catch (TorqueException e) {
				f=true;
				e.printStackTrace();
			}
		}
		if(!f)
		return SUCCESS;
		else return ERROR;
	}

	public String toMatch() {
		try {
			ml =menum.findAll();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		att.remove("roids");
		att.put("roids", roleids);
		return SUCCESS;
	}

	public String toUnMatch() {
		try{
			ml = rom.findModulesByRoleId(roleids[0]);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		att.remove("roid");
		att.put("roid", roleids[0]);
		return SUCCESS;
	}

	public String toAuthorize() {
		try {
			ul = um.findAll();
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		att.remove("roids");
		att.put("roids", roleids);
		return SUCCESS;
	}

	public String toUnAuthorize() {
		try {
			ul = rom.findUserByRoleId(roleids[0]);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		att.remove("roid");
		att.put("roid", roleids[0]);
		return SUCCESS;
	}

	public String match() {
		roleids = (String[]) att.get("roids");
		for (int i = 0; i < roleids.length; i++){
			try {
				rom.match(roleids[i], menuids);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public String unMatch() {
		try {
			rom.unMatch((String) att.get("roid"), menuids);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String authorize() {
		roleids = (String[]) att.get("roids");
		for (int i = 0; i < roleids.length; i++)
			try {
				rom.authorize(roleids[i], uids);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return SUCCESS;
	}

	public String unAuthorize() {
		try {
			rom.unAuthorize((String) att.get("roid"), uids);
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}
}
