package com.youku.soku.manage.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.torque.NoRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.manage.bo.UserBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.UserPermissionService;
import com.youku.soku.manage.service.UserService;
import com.youku.soku.manage.torque.AuthPermission;
import com.youku.soku.manage.torque.AuthPermissionPeer;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.torque.UserPeer;
import com.youku.soku.manage.torque.UserPermission;

/**
 * <p>
 * Insert or update a User object to the persistent store.
 * </p>
 * 
 * @author tanxiuguang
 * 
 */
public class UserAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware {

	private Logger log = Logger.getLogger(this.getClass());

	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	/**
	 * <p>
	 * Create user action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String input() throws Exception{

		log.debug("=========User action input==============");


		if (getUserId() == -1) {
			setTask(Constants.CREATE);
			setUserId(-1);
			return INPUT;
		} else {
			try {
				User oldUser = UserPeer.retrieveByPK(getUserId());
				
				if(oldUser == null) {
					setTask(Constants.CREATE);
					setUserId(-1);
					return INPUT;
				}
				
				Map session = getSession();
		        User currentUser = (User) session.get(Constants.USER_KEY);
		        		        
		        
				List<AuthPermission> authPermissons = AuthPermissionPeer
						.doSelect(new Criteria());
				
				Map<String, AuthPermission> allPermissionMap = (Map<String, AuthPermission>) session
				.get(Constants.PERMISSION_MAP_KEY);
				List<AuthPermission> permissionList = null;
				if(currentUser.getIsSuperUser() != 1) {
					if(hasShieldSystemPermission()) {
						setShieldUser(true);
						permissionList = fixShieldSystemPermission(authPermissons, true);
					} else {
						permissionList = fixShieldSystemPermission(authPermissons, false);
					}
				} else {
					permissionList = authPermissons;
				}
				setPermissionList(permissionList);
				
				UserBo user = getUserBo(oldUser);

				setUser(user);
				setUserId(oldUser.getUserId());
				setTask(Constants.EDIT);
			} catch (NoRowsException e) {
				throw new PageNotFoundException(getText("error.page.not.found"));
			}
			return INPUT;
		}
	

	}
	
	private boolean hasShieldSystemPermission() throws Exception{
			Map session = getSession();
		 	//return UserService.hasShieldSystemPermission(session);
			if(session.get(Constants.SHIELD_USER_KEY) != null) {
				return (Boolean) session.get(Constants.SHIELD_USER_KEY);
			}
			return false;
	}
	
	private List<AuthPermission> fixShieldSystemPermission(List<AuthPermission> authPermissons, boolean removeFalg) {
		List<AuthPermission> result = new ArrayList<AuthPermission>();
		for(Iterator<AuthPermission> itera = authPermissons.iterator(); itera.hasNext(); ) {
			AuthPermission ap = itera.next();
			for(String shieldAuth : Constants.SHIELDING_AUTHS) {
				if (removeFalg) {
					if (ap.getCodeName().equals(shieldAuth)) { 
						result.add(ap);
					}
				} else {
					if (!ap.getCodeName().equals(shieldAuth)) { // remove the auth of shield system
						result.add(ap);
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * <p>
	 * Change user's password action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String changePassword() throws Exception{

		log.debug("=========User action changePassword==============");
		if (getPassword() != null && getRepeatPassword() != null) {				
			User user = UserPeer.retrieveByPK(getUserId());
			
			if(! getPassword().equals(getRepeatPassword())) {
				addActionError(getText("error.password.unique"));
				return INPUT;
			}
			user.setPassword(encryptPassword(getPassword()));
			UserPeer.doUpdate(user);
			return SUCCESS;
		} else {		
			setUserId(this.getCurUserId());
			return INPUT;
		}
	}

	/**
	 * <p>
	 * Insert or update an item
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}

	/**
	 * <p>
	 * List the Users
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() {

		try {
			/*
			 * String pageSize = getText("item.list.pageSize"); LargeSelect
			 * largeSelect =
			 * UserPeer.findUserPagination(Integer.valueOf(pageSize));
			 * log.debug("page number is: " + getPageNumber());
			 * log.debug("total page number is: " +
			 * largeSelect.getTotalPages());
			 * 
			 * if(largeSelect.getTotalPages() < getPageNumber()) { return ERROR;
			 * } List itemList = largeSelect.getPage(getPageNumber());
			 */

			PageInfo pageInfo = new PageInfo();
			pageInfo
					.setPageSize(Integer.valueOf(getText("user.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log
					.debug("UserAction getPageNumber() value is: "
							+ getPageNumber());
			boolean isShieldSystemUser = hasShieldSystemPermission();
			setShieldUser(isShieldSystemUser);
			UserService.findUserPagination(pageInfo, getSearchWord(), isShieldSystemUser);

			List itemList = pageInfo.getResults();
			// itemList = UserPeer.populateObjects(itemList);
			pageInfo.setResults(itemList);
			log.debug("itemList size is: " + itemList.size());
			setPageInfo(pageInfo);

			return Constants.LIST;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}

	/**
	 * <p>
	 * Delete an item from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String delete() throws Exception {

		User item = UserPeer.retrieveByPK(getUserId());
		UserPeer.doDelete(item);

		return SUCCESS;
	}
	
	/**
	 * <p>Batch delete an user from the presistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			User item = UserPeer.retrieveByPK(deleteId);
			UserPeer.doDelete(item);			
		}
		
		return SUCCESS;
	}
	

	/**
	 * <p>
	 * Insert or update an item object to the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String execute() throws Exception{

		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());

			if (creating) {
				log.debug("User name is: " + getUser().getName());
				List<User> itemList = UserService.findUserByName(getUser()
						.getName());
				boolean haveUser = itemList.size() > 0;
				log.debug("itemList size is:" + itemList.size());
				if (haveUser) {
					addActionError(getText("error.user.unique"));
					return INPUT;
				}
				User user = getUserVo(getUser());
				user.setPassword(encryptPassword(user.getPassword()));
				user.setIsShieldSystemUser((byte) (hasShieldSystemPermission() ? 1 : 0));
				user.setDateJoined(new Date());
				user.save();
			} else {
				log.debug("Task update, user id is " + getUser().getUserId());
				User oldUser = UserPeer.retrieveByPK(getUserId());
				oldUser.setName(getUser().getName());
				oldUser.setActualName(getUser().getActualName());
				oldUser.setEmail(getUser().getEmail());
				oldUser.setIsActive(getUser().getIsActive());
				oldUser.setIsSuperUser(getUser().getIsSuperUser());
				log.debug("Task deletePermissionsByUserId user id is  " + getUserId());
				UserPermissionService.deletePermissionsByUserId(getUserId());
				List permissionList = getUser().getPermissionList();
				log.debug("permissionList is  " + permissionList);
				for (Object userPermissionID : permissionList) {
					UserPermission userPermission = new UserPermission();
					userPermission.setPermissionId(Integer
							.valueOf((String) userPermissionID));
					userPermission.setUserId(oldUser.getUserId());
					userPermission.save();
				}
				log.debug("Task doUpdate user: " + oldUser);
				UserPeer.doUpdate(oldUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}

	

	private User getUserVo(UserBo userBo) throws IllegalAccessException,
			InvocationTargetException {
		User user = new User();
		copyProperties(user, userBo);
		return user;
	}

	private UserBo getUserBo(User user) throws IllegalAccessException,
			InvocationTargetException, TorqueException {

		UserBo userBo = new UserBo();
		copyProperties(userBo, user);
		List<UserPermission> permissionsList = UserPermissionService
				.findPermissionsByUserId(user.getUserId());

		List thisUserPermission = new ArrayList();
		for (UserPermission userPermisson : permissionsList) {
			thisUserPermission.add(userPermisson.getPermissionId());
		}
		userBo.setPermissionList(thisUserPermission);
		return userBo;
	}
	
	public String leave() {
		log.debug("===========log out=========");
		System.out.println("=======log out");
		Map session = getSession();
		// session.clear();
		session.remove(Constants.USER_KEY);

		Cookie[] cookies = request.getCookies();

		System.out.println(cookies);
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Constants.COOKIE_REMEMBER_ME.equals(cookie.getName())) {
					cookie.setValue("");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
		return INPUT;
	}
	

	/**
	 * current page number, for the user list view
	 */
	private int pageNumber;
	
	/**
	 * the user object list
	 */
	private List userList;
	
	/**
	 * the pagination object
	 */
	private PageInfo pageInfo;
	

	/**
	 * the value for the word for searching
	 */
	private String searchWord;
	
	/**
	 * the list with all the permission
	 */
	private List permissionList;
	
	/** the object of the User **/
	private UserBo userBo;

	/**
	 * User id, used for update the user object
	 */
	private int userId;
	
	/**
	 * The value for password
	 */
	private String password;
	
	/**
	 * The value for the repeat password
	 */
	private String repeatPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	/**
	 * create the User object
	 */
	public UserBo getUser() {
		return userBo;
	}

	/**
	 * set the User object
	 * 
	 * @param user
	 *            User ojbect
	 */
	public void setUser(UserBo userBo) {
		this.userBo = userBo;
	}

	/**
	 * get user id
	 * 
	 * @return userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * set user id
	 * 
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * get the current page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * set the current page number
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}	

	/**
	 * get the user list
	 */
	public List getUserList() {
		return userList;
	}

	/**
	 * set the user list
	 */
	public void setUserList(List userList) {
		this.userList = userList;
	}	

	/**
	 * get the pagination object
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * set the pagination object
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public List getPermissionList() {
		return this.permissionList;
	}

	public void setPermissionList(List permissionList) {
		this.permissionList = permissionList;
	}


	private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private boolean shieldUser;

	public boolean isShieldUser() {
		return shieldUser;
	}

	public void setShieldUser(boolean shieldUser) {
		this.shieldUser = shieldUser;
	}
	
	

}
