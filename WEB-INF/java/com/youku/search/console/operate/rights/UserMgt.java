package com.youku.search.console.operate.rights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.Menu;
import com.youku.search.console.pojo.MenuPeer;
import com.youku.search.console.pojo.RoleMenu;
import com.youku.search.console.pojo.RoleMenuPeer;
import com.youku.search.console.pojo.User;
import com.youku.search.console.pojo.UserPeer;
import com.youku.search.console.pojo.UserRole;
import com.youku.search.console.pojo.UserRolePeer;
import com.youku.search.console.util.MD5;
import com.youku.search.console.vo.LeftMenuVO;

public class UserMgt{

	private static UserMgt instance = null;

	public static synchronized UserMgt getInstance() {
		if (instance != null)
			return instance;
		else
			return new UserMgt();
	}

	/**
	 * 保存用户
	 * 
	 * @param u
	 * @throws Exception 
	 */
	public void save(User u) throws Exception {
		u.save();
	}

	/**
	 * 删除用户
	 * 
	 * @param u
	 * @throws TorqueException 
	 */
	public void delete(User u) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(UserRolePeer.USER_ID, u.getId());
		UserRolePeer.doDelete(criteria);
		UserPeer.doDelete(u);
	}

	/**
	 * 根据主键删除用户
	 * 
	 * @param key
	 * @throws TorqueException 
	 */
	public void deleteByKey(String key) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(UserRolePeer.USER_ID, Integer.parseInt(key));
		UserRolePeer.doDelete(criteria);

		criteria = new Criteria();
		criteria.add(UserPeer.ID, Integer.parseInt(key));
		UserPeer.doDelete(criteria);
	}

	/**
	 * 修改用户
	 * 
	 * @param u
	 * @throws TorqueException 
	 */
	public void update(User u) throws TorqueException {
		UserPeer.doUpdate(u);
	}

	/**
	 * 查找出所有用户
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<User> findAll() throws TorqueException {
		List<User> ul = new ArrayList<User>();
		Criteria criteria = new Criteria();
		criteria.add(UserPeer.ID, (Object) ("ID is not null"), Criteria.CUSTOM);
		ul = UserPeer.doSelect(criteria);
		return ul;
	}

	/**
	 * 根据主键查找用户
	 * 
	 * @param id
	 * @return
	 * @throws TorqueException 
	 * @throws NumberFormatException 
	 * @throws TooManyRowsException 
	 * @throws NoRowsException 
	 */
	public User findById(String id) throws NoRowsException, TooManyRowsException, NumberFormatException, TorqueException {
		return UserPeer.retrieveByPK(Integer.parseInt(id));
	}

	public Set<String> getModulesbyuid(int userid) throws TorqueException{
		Set<String> modules=new HashSet<String>();
		List<UserRole> urs = null;
		List<RoleMenu> rms = null;
		Criteria criteria = new Criteria();
		criteria.add(UserRolePeer.USER_ID, userid);
		urs = UserRolePeer.doSelect(criteria);
		if(null==urs||urs.size()<1)
			return modules;
		for(UserRole ur:urs){
			criteria = new Criteria();
			criteria.add(RoleMenuPeer.ROLE_ID, ur.getRoleId());
			rms=RoleMenuPeer.doSelect(criteria);
			if(null==rms||rms.size()<1)
				continue;
			for(RoleMenu rm:rms){
				modules.add(rm.getMenu().getName());
			}
		}
		return modules;
	}
	
	public List<LeftMenuVO> mapToList(Map<Integer,LeftMenuVO> lmvomap) throws TorqueException{
		List<LeftMenuVO> lmvol = new ArrayList<LeftMenuVO>();
		if(null!=lmvomap&&lmvomap.size()>0){
			Iterator<Integer> iterator=lmvomap.keySet().iterator();
			while(iterator.hasNext()){
				lmvol.add(lmvomap.get(iterator.next()));
			}
		}
		return lmvol;
	}
	
	public Map<Integer,LeftMenuVO> getPrivilege(int userid) throws TorqueException{
		LeftMenuVO lmvo=null;
		LeftMenuVO slmvo=null;
		Menu m=null;
		Menu tempm=null;
		List<UserRole> urs = null;
		List<RoleMenu> rms = null;
		List<Menu> ms = null;
		Map<Integer,LeftMenuVO> lmvmap=new LinkedHashMap<Integer, LeftMenuVO>();
		Criteria criteria = new Criteria();
		criteria.add(UserRolePeer.USER_ID, userid);
		criteria.addAscendingOrderByColumn(UserRolePeer.ROLE_ID);
		urs = UserRolePeer.doSelect(criteria);
		if(null==urs||urs.size()<1)
			return lmvmap;
		for(UserRole ur:urs){
			criteria = new Criteria();
			criteria.add(RoleMenuPeer.ROLE_ID, ur.getRoleId());
			criteria.addAscendingOrderByColumn(RoleMenuPeer.MENU_ID);
			rms=RoleMenuPeer.doSelect(criteria);
			if(null==rms||rms.size()<1)
				continue;
			int tfid=-2;
			for(RoleMenu rm:rms){
				m=rm.getMenu();
				tfid=m.getFatherId();
				if(tfid==-1){
					lmvo=lmvmap.get(rm.getMenuId());
					if(null==lmvo){
						lmvo=new LeftMenuVO();
						lmvo.setId(m.getId());
						lmvo.setName(m.getName());
						lmvo.setUrl(m.getMenuurl());
						lmvo.setModule(m.getModule());
//						System.out.println(lmvo.toString());
						lmvmap.put(m.getId(), lmvo);
					}
//					lmvo.getModules().add(m.getModule());
					criteria = new Criteria();
					criteria.add(MenuPeer.FATHER_ID,m.getId());
					criteria.addAscendingOrderByColumn(MenuPeer.ID);
					ms=MenuPeer.doSelect(criteria);
					for(Menu menu:ms){
						slmvo=new LeftMenuVO();
						slmvo.setId(menu.getId());
						slmvo.setModule(menu.getModule());
						slmvo.setName(menu.getName());
						slmvo.setUrl(menu.getMenuurl());
						lmvo.getTwoMenu().add(slmvo);
						lmvo.getModules().add(menu.getModule());
					}
					}else{
						lmvo=lmvmap.get(tfid);
						if(null==lmvo){
							lmvo=new LeftMenuVO();
							tempm=MenuMgt.getInstance().findById(tfid);
							if(null!=tempm){
								lmvo.setId(tempm.getId());
								lmvo.setName(tempm.getName());
								lmvo.setUrl(tempm.getMenuurl());
								lmvo.setModule(tempm.getModule());
//								System.out.println(lmvo.toString());
								lmvmap.put(tempm.getId(), lmvo);
							}
						}
						
						slmvo=new LeftMenuVO();
						slmvo.setId(m.getId());
						slmvo.setName(m.getName());
						slmvo.setUrl(m.getMenuurl());
						slmvo.setModule(m.getModule());
						lmvo.getTwoMenu().add(slmvo);
						
						lmvo.getModules().add(m.getModule());
					}
				}
			}
//		printLeftMenuVO(null,lmvmap);
		return lmvmap;
	}
	
	public void printLeftMenuVO(String title,Map<Integer,LeftMenuVO> lmvomap){
		System.out.println(title+"start");
		if(lmvomap!=null&&lmvomap.size()>0){
			Iterator<Integer> iterator=lmvomap.keySet().iterator();
			while(iterator.hasNext()){
				System.out.println(lmvomap.get(iterator.next()));
			}
		}
		System.out.println(title+"end");
	}

	/**
	 * 检测登陆，返回数组第一个为1表示成功登陆，第二个为用户id
	 * 
	 * @param name
	 * @param password
	 * @return
	 * @throws TorqueException 
	 */
	public int[] login(String name, String password) throws TorqueException {
		if (null == name || null == password) {
			int[] b = { 0, 0 };
			return b;
		}
		String pwd = MD5.hash(password);
		Criteria criteria = new Criteria();
		criteria.add(UserPeer.NAME, name);
		criteria.add(UserPeer.PASSWORD, pwd);
		List<User> ul = new ArrayList<User>();
		ul = UserPeer.doSelect(criteria);
		if (ul != null && ul.size() > 0) {
			UserPeer
						.executeStatement("update user set lastLoginDate=now() where name='"
								+ name + "' and password='" + pwd + "'");
			int[] a = { 1, ul.get(0).getId() };
			return a;
		} else {
			int[] b = { 0, 0 };
			return b;
		}
	}

	/**
	 * 检测用户，如果返回true，表示添加用户名称可用
	 * 
	 * @param name
	 * @return
	 * @throws TorqueException 
	 */
	public boolean checkName(String name) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(UserPeer.NAME, name);
		List<User> ul = new ArrayList<User>();
		ul = UserPeer.doSelect(criteria);
		if (ul != null && ul.size() > 0)
			return false;
		else
			return true;
	}

	
	public static void main(String[] args) {
		Set<String> modules=new HashSet<String>();
		System.out.println(Arrays.toString(modules.toArray(new String[]{})));
	}
}
