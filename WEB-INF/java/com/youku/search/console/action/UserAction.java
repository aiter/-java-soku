package com.youku.search.console.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.rights.UserMgt;
import com.youku.search.console.pojo.User;
import com.youku.search.console.pojo.UserPeer;
import com.youku.search.console.util.MD5;
import com.youku.search.console.vo.UserVO;

public class UserAction extends ActionSupport implements SessionAware,
		ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(UserAction.class);

	private String name;
	private String password;
	private String newPassword;
	private Map att;
	private HttpServletRequest request;
	private String[] userids;

	private User u;
	private List<UserVO> ul = new ArrayList<UserVO>();

	private UserMgt um = UserMgt.getInstance();

	private String message;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map getAtt() {
		return att;
	}

	public void setAtt(Map att) {
		this.att = att;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public List<UserVO> getUl() {
		return ul;
	}

	public void setUl(List<UserVO> ul) {
		this.ul = ul;
	}

	public String[] getUserids() {
		return userids;
	}

	public void setUserids(String[] userids) {
		this.userids = userids;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String load() {
		message = att.get("message") != null ? (String) att.get("message") : "";
		att.remove("message");
		String userid = (String) att.get("user_id");
		try {
			u = um.findById(userid);
		} catch (NoRowsException e) {
			e.printStackTrace();
			return ERROR;
		} catch (TooManyRowsException e) {
			e.printStackTrace();
			return ERROR;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ERROR;
		} catch (TorqueException e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public String toAdd() {
		return SUCCESS;
	}

	public String check() {
		String name = (String) request.getParameter("name");
		request.removeAttribute("temp_name");
		request.setAttribute("temp_name", name);
		return SUCCESS;
	}

	public String list() {
		message = att.get("message") != null ? (String) att.get("message") : "";
		att.remove("message");
		List<User> userlist=new ArrayList<User>();
		try {
			userlist = um.findAll();
		} catch (TorqueException e1) {
			e1.printStackTrace();
		}
		UserVO uvo = null;
		for (int i = 0; i < userlist.size(); i++) {
			uvo = new UserVO();
			uvo.setU(userlist.get(i));
			try {
				uvo.setResName(Arrays.toString(um.getModulesbyuid(userlist.get(i).getId()).toArray()));
			} catch (TorqueException e) {
				e.printStackTrace();
			}
			ul.add(uvo);
		}
		return SUCCESS;
	}

	public String update() {
		try {
			if (um.login(u.getName(), u.getPassword())[0] != 1) {
				att.remove("message");
				att.put("message", Constants.PWDERR);
				return INPUT;
			}
		} catch (TorqueException e1) {
			e1.printStackTrace();
			return INPUT;
		}
		String userid = (String) att.get("user_id");
		try {
			UserPeer.executeStatement("update user set password='"
					+ MD5.hash(newPassword) + "',true_name='"
					+ u.getTrueName() + "',team='" + u.getTeam() + "',sex="
					+ u.getSex() + ",birth='" + u.getBirth() + "' where id="
					+ userid);
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		try {
			u = um.findById(userid);
		} catch (NoRowsException e) {
			e.printStackTrace();
		} catch (TooManyRowsException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String store() {
		try {
			if (!um.checkName(name)) {
				att.remove("message");
				att.put("message", Constants.NAMEERR);
				return INPUT;
			}
			u.setPassword(MD5.hash(u.getName()));
			u.setLastlogindate(new Date());
			um.save(u);
		} catch (TorqueException e1) {
			e1.printStackTrace();
			att.put("message", Constants.NAMEERR);
			return INPUT;
		} catch (Exception e) {
			e.printStackTrace();
			att.put("message", Constants.NAMEERR);
			return INPUT;
		}
		return SUCCESS;
	}

	public String remove() {
		for (int i = 0; i < userids.length; i++) {
			if (userids[i].equalsIgnoreCase((String) att.get("user_id"))) {
				att.remove("message");
				att.put("message", Constants.REMOVEERR);
				return INPUT;
			}
			try {
				um.deleteByKey(userids[i]);
			} catch (TorqueException e) {
				e.printStackTrace();
				att.remove("message");
				att.put("message", Constants.REMOVEERR);
				return INPUT;
			}
		}

		return SUCCESS;
	}

	public String login() {
		int[] a;
		try {
			a = um.login(name, password);
		} catch (TorqueException e) {
			e.printStackTrace();
			message = Constants.USRPWDERR;
			return INPUT;
		}
		if (a[0] == 1) {
			att.put("user_name", name);
			att.put("user_id", "" + a[1]);
			try {
				att.put("urlmap", um.getPrivilege(a[1]));
			} catch (TorqueException e) {
				e.printStackTrace();
				message = Constants.USRPWDERR;
				return INPUT;
			}
			return SUCCESS;
		} else {
			message = Constants.USRPWDERR;
			return INPUT;
		}
	}

	public String logout() {
		att.remove("user_id");
		att.remove("user_name");
		att.remove("urlmap");
		return SUCCESS;
	}

	public void setSession(Map arg0) {
		this.att = arg0;
	}

	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

}
