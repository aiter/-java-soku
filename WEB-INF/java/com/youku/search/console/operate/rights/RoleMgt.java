package com.youku.search.console.operate.rights;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.Menu;
import com.youku.search.console.pojo.Role;
import com.youku.search.console.pojo.RoleMenu;
import com.youku.search.console.pojo.RoleMenuPeer;
import com.youku.search.console.pojo.RolePeer;
import com.youku.search.console.pojo.User;
import com.youku.search.console.pojo.UserPeer;
import com.youku.search.console.pojo.UserRole;
import com.youku.search.console.pojo.UserRolePeer;
import com.youku.search.console.util.MD5;

public class RoleMgt {
	private static RoleMgt instance = null;

	public static synchronized RoleMgt getInstance() {
		if (null != instance)
			return instance;
		else
			return new RoleMgt();
	}

	/**
	 * 保存角色
	 * 
	 * @param r
	 * @throws Exception 
	 */
	public void save(Role r) throws Exception {
		r.save();
	}

	/**
	 * 删除角色
	 * 
	 * @param r
	 * @throws TorqueException 
	 */
	public void delete(Role r) throws TorqueException {
		deleteByKey(""+r.getId());
	}

	/**
	 * 根据主键删除角色
	 * 
	 * @param key
	 * @throws TorqueException 
	 */
	public void deleteByKey(String key) throws TorqueException {
			Criteria criteria = new Criteria();
			criteria.add(RoleMenuPeer.ROLE_ID, Integer.parseInt(key));
			RoleMenuPeer.doDelete(criteria);

			criteria = new Criteria();
			criteria.add(UserRolePeer.ROLE_ID, Integer.parseInt(key));
			UserRolePeer.doDelete(criteria);

			criteria = new Criteria();
			criteria.add(RolePeer.ID, Integer.parseInt(key));
			RolePeer.doDelete(criteria);
	}

	/**
	 * 修改角色
	 * 
	 * @param r
	 * @throws TorqueException 
	 */
	public void update(Role r) throws TorqueException {
		RolePeer.doUpdate(r);
	}

	/**
	 * 查找出所有角色
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Role> findAll() throws TorqueException {
		List<Role> ul = new ArrayList<Role>();
		Criteria criteria = new Criteria();
		criteria.add(RolePeer.ID, (Object) ("ID is not null"), Criteria.CUSTOM);
		ul = RolePeer.doSelect(criteria);
		return ul;
	}

	/**
	 * 根据主键查找角色
	 * 
	 * @param id
	 * @return
	 * @throws TorqueException 
	 * @throws NumberFormatException 
	 * @throws TooManyRowsException 
	 * @throws NoRowsException 
	 */
	public Role findById(String id) throws NoRowsException, TooManyRowsException, NumberFormatException, TorqueException {
		return RolePeer.retrieveByPK(Integer.parseInt(id));
	}

	/**
	 * 查找角色对应的用户
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<User> findUserByRoleId(String roleid) throws TorqueException {
		List<UserRole> url = new ArrayList<UserRole>();
		List<User> ul = new ArrayList<User>();
		User u = null;
		Criteria criteria = new Criteria();
		criteria.add(UserRolePeer.ROLE_ID, roleid);
		int i = 0;
		url = UserRolePeer.doSelect(criteria);
		for (; i < url.size(); i++) {
			u = UserPeer.retrieveByPK(url.get(i).getUserId());
			ul.add(u);
		}
		return ul;
	}

	/**
	 * 查找角色对应的菜单
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findModulesByRoleId(String roleid) throws TorqueException {
//		System.out.println("roleid:"+roleid);
		List<RoleMenu> url = new ArrayList<RoleMenu>();
		List<Menu> ml=new ArrayList<Menu>();
		Criteria criteria = new Criteria();
		criteria.add(RoleMenuPeer.ROLE_ID, roleid);
		url = RoleMenuPeer.doSelect(criteria);
		if(null==url||url.size()<1)
			return ml;
		for(RoleMenu rm:url){
			ml.add(rm.getMenu());
		}
		return ml;
	}

	/**
	 * 根据角色给用户授权
	 * 
	 * @param id
	 *            角色id
	 * @param uids
	 *            用户id列表
	 * @throws Exception 
	 */
	public void authorize(String id, String[] uids) throws Exception {
		UserRole ur;
		Criteria criteria;
		List<UserRole> url = null;
		int roleid = Integer.parseInt(id);
		for (int i = 0; i < uids.length; i++) {
			ur = new UserRole();
			criteria = new Criteria();
			criteria.add(UserRolePeer.USER_ID, Integer.parseInt(uids[i]));
			criteria.add(UserRolePeer.ROLE_ID, Integer.parseInt(id));
			url = UserRolePeer.doSelect(criteria);
			if (url != null && url.size() > 0)
				continue;
			ur.setRoleId(roleid);
			ur.setUserId(Integer.parseInt(uids[i]));
			ur.save();
		}

	}

	/**
	 * 取消授权
	 * 
	 * @param id
	 * @param uids
	 * @throws TorqueException 
	 */
	public void unAuthorize(String id, String[] uids) throws TorqueException {
		Criteria criteria;
		int roleid = Integer.parseInt(id);
		for (int i = 0; i < uids.length; i++) {
			criteria = new Criteria();
				// 取得要跟新的数据
			criteria.add(UserRolePeer.ROLE_ID, roleid);
			criteria.add(UserRolePeer.USER_ID, Integer.parseInt(uids[i]));
			UserRolePeer.doDelete(criteria);
		}

	}

	/**
	 * 根据角色匹配菜单
	 * 
	 * @param id
	 *            角色id
	 * @param menuids
	 *            资源菜单id列表
	 * @throws Exception 
	 */
	public void match(String id, String[] menuids) throws Exception {
		RoleMenu rm;
		Criteria criteria;
		List<RoleMenu> rml = null;
		int roleid = Integer.parseInt(id);
		for (int i = 0; i < menuids.length; i++) {
			rm = new RoleMenu();
			criteria = new Criteria();
			criteria.add(RoleMenuPeer.MENU_ID, Integer
					.parseInt(menuids[i]));
			criteria.add(RoleMenuPeer.ROLE_ID, roleid);
			rml = RoleMenuPeer.doSelect(criteria);
			if (rml != null && rml.size() > 0)
				continue;
			rm.setRoleId(roleid);
			rm.setMenuId(Integer.parseInt(menuids[i]));
			rm.save();
		}
	}

	/**
	 * 取消角色菜单匹配
	 * 
	 * @param id
	 * @param menuids
	 * @throws TorqueException 
	 */
	public void unMatch(String id, String[] menuids) throws TorqueException {
		Criteria criteria;
		int roleid = Integer.parseInt(id);
		for (int i = 0; i < menuids.length; i++) {
			criteria = new Criteria();
			criteria.add(RoleMenuPeer.ROLE_ID, roleid);
			criteria.add(RoleMenuPeer.MENU_ID, Integer
						.parseInt(menuids[i]));
			RoleMenuPeer.doDelete(criteria);
		}
	}

	public static void main(String[] args) {
		System.out.println(27530436.00/39309577);
		System.out.println(27767855.00/39705199);
	}
}
