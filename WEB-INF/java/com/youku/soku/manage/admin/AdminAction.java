package com.youku.soku.manage.admin;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.CookiesAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.UserService;
import com.youku.soku.manage.torque.User;

public class AdminAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware, CookiesAware{
	
	private boolean rememberMe;
	
	private String name;		
	
	private String password;	
	
	private HttpServletResponse response;
	
	private HttpServletRequest request;
	
	private Map cookies; 
		
	 /**
     * <p>Logging output for this plug in instance.</p>
     */
	private Logger log = Logger.getLogger(this.getClass());

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
	
	
	public String list() {
		log.debug("===========log out=========");
		/*Map session = getSession();
		session.clear();*/
		
		return SUCCESS;
	}
	
	public String input() {
		log.debug("===========log input=========");
		Map session = getSession();
		if (session != null && session.get(Constants.USER_KEY) != null) {
			return SUCCESS;
		}
		return INPUT;
	}
	
	

	public String execute() throws Exception{
		

		log.debug("===========execute=========");
		Map session = getSession();
		if (session != null && session.get(Constants.USER_KEY) != null) {
			return SUCCESS;
		}
		
		String userName = getName();
		String password = encryptPassword(getPassword());
				
		User user = UserService.attemptLogin(userName, password);
		if(user == null) {
			addActionError(getText("error.usernameorpassword"));
			return INPUT;
		} else {
		    session.put(Constants.USER_KEY, user);		    
		    UserService.constuctPermissonMap(user, session);
		    if(rememberMe) {
		    	Cookie cookie = new Cookie(Constants.COOKIE_REMEMBER_ME, userName + Constants.COOKIE_SPLITER + password);
		    	cookie.setMaxAge(60 * 60 * 24 * 14);
		    	response.addCookie(cookie);
		    }   
		    
		    UserService.constuctPermissonMap(user, session);
		    //log.debug("===="+session.get(Constants.PERMISSION_MAP_KEY)+"==");
		    boolean isShield = UserService.hasShieldSystemPermission(session);
		    
		 	setShieldUser(isShield);
		 	session.put(Constants.SHIELD_USER_KEY, isShield);
		 	log.debug("=========login with "+isShield+"==========");
		 	if(user.getIsSuperUser()!=1 && isShield){
			 	log.debug("=========login success with "+isShield+"==========");
		 		return "shield";
		 	}
			return SUCCESS;			
		}
	
	}
	
	public String leave() {
		log.debug("===========log out=========");
		System.out.println("=======log out");
		Map session = getSession();
		//session.clear();
		session.remove(Constants.USER_KEY);
		
		Cookie[] cookies = request.getCookies();

		System.out.println(cookies);
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if (Constants.COOKIE_REMEMBER_ME.equals(cookie.getName())) {
					cookie.setValue("");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
		return INPUT;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setCookiesMap(Map cookies) {
		this.cookies = cookies;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private boolean shieldUser;

	public boolean isShieldUser() {
		return shieldUser;
	}

	public void setShieldUser(boolean shieldUser) {
		this.shieldUser = shieldUser;
	}

}
