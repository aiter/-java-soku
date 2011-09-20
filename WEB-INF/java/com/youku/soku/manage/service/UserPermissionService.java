package com.youku.soku.manage.service;

import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.manage.torque.UserPermission;
import com.youku.soku.manage.torque.UserPermissionPeer;

public class UserPermissionService extends BaseService{
	
	public static List<UserPermission> findPermissionsByUserId(int userId) throws TorqueException {    	
    	Criteria crit = new Criteria();
    	crit.add(UserPermissionPeer.USER_ID, userId);
    	
    	List<UserPermission> permissionList = UserPermissionPeer.doSelect(crit);
    	return permissionList;
    }
	
	public static void deletePermissionsByUserId(int userId) throws TorqueException {    	
    	Criteria crit = new Criteria();
    	crit.add(UserPermissionPeer.USER_ID, userId);
    	
    	UserPermissionPeer.doDelete(crit);
    	
    }
}
