package com.youku.soku.manage.interceptor;

import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;

import com.opensymphony.xwork2.interceptor.Interceptor;
import com.youku.soku.manage.service.UserPermissionService;
import com.youku.soku.manage.torque.AuthPermission;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.torque.UserPermission;

public abstract class BaseManageAuthenticationInterceptor implements
		Interceptor {

	@SuppressWarnings("finally")
	protected boolean hasPermission(User user, String permissionString,
			Map<String, AuthPermission> permissionMap) {

		if (user.getIsActive() != 1) {
			return false;
		}
		
		if (user.getIsSuperUser() != 1) {
			
			try {
				
				List<UserPermission> permissionsList = UserPermissionService
						.findPermissionsByUserId(user.getUserId());
				AuthPermission manageItemPermission = permissionMap
				.get(permissionString);
				for (UserPermission userPermission : permissionsList) {
					if (manageItemPermission != null
							&& userPermission.getPermissionId() == manageItemPermission
									.getId()) {
						return true;
					}
				}
			} catch (TorqueException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return false;
		} else {
			return true;
		}

		

	}
}
