package com.youku.soku.manage.interceptor;

import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.service.UserPermissionService;
import com.youku.soku.manage.torque.AuthPermission;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.torque.UserPermission;

public class ManageFeedbackAuthInterceptor implements Interceptor {


    public void destroy () {}

    public void init() {}

    public String intercept(ActionInvocation actionInvocation) throws Exception {

        Map session = actionInvocation.getInvocationContext().getSession();

        User user = (User) session.get(Constants.USER_KEY);

        boolean isAuthenticated = (null!=user);

        if (!isAuthenticated) {
            return Action.LOGIN;            
        }
        else {
        	boolean isPermit = false;
        	if (user.getIsSuperUser() != 1) {
				Map<String, AuthPermission> permissionMap = (Map<String, AuthPermission>) session
						.get(Constants.PERMISSION_MAP_KEY);
				AuthPermission manageItemPermission = permissionMap
						.get(Constants.MANAGE_FEEDBACK);
				try {
					List<UserPermission> permissionsList = UserPermissionService
					.findPermissionsByUserId(user.getUserId());
					
					for (UserPermission userPermission : permissionsList) {
						if (manageItemPermission != null && userPermission.getPermissionId() == manageItemPermission
								.getId()) {
							isPermit = true;
							break;
						}
					}
				} catch (TorqueException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			} else {
				isPermit = true;
			}
        	
        	if(user.getIsActive() != 1) {
        		isPermit = false;
        	}
        	
			if(isPermit) {
            	return actionInvocation.invoke();
            } else {
            	return Constants.DENIED;
            }
        	
        }

    }

}
