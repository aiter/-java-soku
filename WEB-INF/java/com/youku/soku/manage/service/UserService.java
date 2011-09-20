package com.youku.soku.manage.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.LargeSelect;

import com.workingdogs.village.Record;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.torque.AuthPermission;
import com.youku.soku.manage.torque.AuthPermissionPeer;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.torque.UserPeer;
import com.youku.soku.manage.torque.UserPermission;

public class UserService extends BaseService {

	public static List<User> findUserByName(String userName)
			throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(UserPeer.NAME, userName);

		List<User> userList = UserPeer.doSelect(crit);
		return userList;

	}

	/**
	 * <p>
	 * Find all the user
	 * </p>
	 * 
	 * @return the list of the user object
	 */
	public static List<User> findUser(Connection conn) throws TorqueException {

		Criteria crit = new Criteria();

		List<User> userList = UserPeer.doSelect(crit);
		return userList;

	}

	/**
	 * <p>
	 * Find user list using pagination
	 * </p>
	 * 
	 * @return the list of the user object
	 */
	public static LargeSelect findUserPagination(int pageSize)
			throws TorqueException {

		Criteria crit = new Criteria();
		LargeSelect largeSelect = new LargeSelect(crit, pageSize);

		UserPeer.doSelect(crit);

		return largeSelect;
	}

	/**
	 * <p>
	 * Find object list using pagination
	 * </p>
	 * 
	 */
	public static void findUserPagination(PageInfo pageInfo)
			throws Exception {
		int totalRecord = ((Record) UserPeer.executeQuery(
				"SELECT count(*) FROM " + UserPeer.TABLE_NAME).get(0))
				.getValue(1).asInt();
		Criteria crit = new Criteria();

		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);
		List result = UserPeer.doSelect(crit);

		pageInfo.setResults(result);

		// return 0;
	}

	/**
	 * <p>
	 * Find object list using pagination
	 * </p>
	 * 
	 */
	public static void findUserPagination(PageInfo pageInfo, String searchWord,
			boolean isShieldSystemUser) throws Exception {

		Criteria crit = new Criteria();
		int shieldUserFlag = isShieldSystemUser ? 1 : 0;
		String whereSql = " Where 1 = 1 ";
		if (searchWord != null && !searchWord.equals("")) {
			crit.add(UserPeer.NAME, (Object) ("%" + searchWord + "%"),
					Criteria.LIKE);
			whereSql += "AND " + UserPeer.NAME + " LIKE "
					+ ("'%" + searchWord + "%'");

		}
		whereSql += "AND " + UserPeer.IS_SHIELD_SYSTEM_USER + " = "
				+ shieldUserFlag;
		crit.add(UserPeer.IS_SHIELD_SYSTEM_USER, shieldUserFlag);

		int totalRecord = ((Record) UserPeer.executeQuery(
				"SELECT count(*) FROM " + UserPeer.TABLE_NAME + whereSql, UserPeer.DATABASE_NAME).get(0)).getValue(1).asInt();
		int totalPageNumber = (int) Math.ceil((double) totalRecord
				/ pageInfo.getPageSize());
		pageInfo.setTotalPageNumber(totalPageNumber);
		pageInfo.initCrit(crit);

		List result = UserPeer.doSelect(crit);

		pageInfo.setResults(result);

		// return 0;
	}

	public static User attemptLogin(String userName, String password) throws Exception {

		Criteria crit = new Criteria();
		crit.add(UserPeer.NAME, userName);
		crit.add(UserPeer.PASSWORD, password);

		List<User> userList = UserPeer.doSelect(crit);

		if (userList == null || userList.isEmpty()) {
			return null;
		} else {
			return userList.get(0);
		}
	}

	/**
	 * construct the permission map key permission_code_name, value permission
	 * object
	 */
	public static void constuctPermissonMap(User user, Map session) throws TorqueException {
		List<AuthPermission> authPermissons = AuthPermissionPeer.doSelect(
				new Criteria());
		Map<String, AuthPermission> permissionMap = new HashMap<String, AuthPermission>();
		Map<Integer, AuthPermission> permissionIdMap = new HashMap<Integer, AuthPermission>();
		for (AuthPermission auth : authPermissons) {
			permissionMap.put(auth.getCodeName(), auth);
			permissionIdMap.put(auth.getId(), auth);
		}

		session.put(Constants.PERMISSION_MAP_KEY, permissionMap);

		List<UserPermission> permissionsList = UserPermissionService
				.findPermissionsByUserId(user.getUserId());
		Map<String, AuthPermission> userPermissionMap = new HashMap<String, AuthPermission>();
		if (user.getIsActive() != 1) {
			session.put(Constants.USER_PERMISSION_KEY, userPermissionMap);
			return;
		}
		if (user.getIsActive() == 1 && user.getIsSuperUser() == 1) {
			session.put(Constants.USER_PERMISSION_KEY, permissionMap);
			return;
		}
		if (user.getIsActive() == 1) {
			for (UserPermission userPermission : permissionsList) {
				AuthPermission aPermission = permissionIdMap.get(userPermission
						.getPermissionId());
				userPermissionMap.put(aPermission.getCodeName(), aPermission);
			}
			session.put(Constants.USER_PERMISSION_KEY, userPermissionMap);
			return;
		}

	}

	public static boolean hasShieldSystemPermission(Map session)
			throws Exception {

		User currentUser = (User) session.get(Constants.USER_KEY);

		List<AuthPermission> authPermissons = AuthPermissionPeer.doSelect(
				new Criteria());

		Map<String, AuthPermission> allPermissionMap = (Map<String, AuthPermission>) session
				.get(Constants.PERMISSION_MAP_KEY);

		List<UserPermission> permissionsList = UserPermissionService
				.findPermissionsByUserId(currentUser.getUserId());

		AuthPermission manageShieldPermission = allPermissionMap
				.get(Constants.MANAGE_SHIELDING_SYSTEM);
		for (UserPermission userPermission : permissionsList) {
			if (manageShieldPermission != null
					&& userPermission.getPermissionId() == manageShieldPermission
							.getId()) {
				return true;
			}
		}

		return false;
	}
}
