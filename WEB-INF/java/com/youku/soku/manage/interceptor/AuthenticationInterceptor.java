package com.youku.soku.manage.interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.UserService;
import com.youku.soku.manage.torque.User;

public class AuthenticationInterceptor implements Interceptor {

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {
		System.out.println("============ AuthenticationInterceptor =========");
		ActionContext actionContext = actionInvocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(StrutsStatics.HTTP_REQUEST);
		

		Map session = actionContext.getSession();
		// User user = (User) session.get(Constants.USER_KEY);
		boolean isAuthenticated = false;
		
		if (session == null || session.get(Constants.USER_KEY) == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (Constants.COOKIE_REMEMBER_ME.equals(cookie.getName())) {
						String value = cookie.getValue();

						if (StringUtils.isNotBlank(value)) {
							String[] split = value
									.split(Constants.COOKIE_SPLITER);
							String userName = split[0];
							String password = split[1];

							try {
								User user = UserService.attemptLogin(userName, password);
								if(user != null) {
									isAuthenticated = true;
									session.put(Constants.USER_KEY, user);		    
								    UserService.constuctPermissonMap(user, session);
								}
							} catch (TorqueException e) {
								e.printStackTrace();
							} finally {
							}

						}
					}
				}
			}
		} else {
			isAuthenticated = true;
		}

		

		if (!isAuthenticated) {
			return Action.LOGIN;
		} else {
			return actionInvocation.invoke();
		}

	}

}
