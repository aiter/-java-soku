package com.youku.search.console.operate.rights;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.search.console.pojo.Menu;
import com.youku.search.console.pojo.MenuPeer;
import com.youku.search.console.pojo.MenuResource;
import com.youku.search.console.pojo.MenuResourcePeer;
import com.youku.search.console.pojo.RoleMenu;
import com.youku.search.console.pojo.RoleMenuPeer;
import com.youku.search.console.vo.MenuVO;

public class MenuMgt {
	private static MenuMgt instance = null;

	public static synchronized MenuMgt getInstance() {
		if (null != instance)
			return instance;
		else
			return new MenuMgt();
	}
	
	/**
	 * 保存菜单
	 * 
	 * @param r
	 * @throws Exception 
	 */
	public void save(Menu m) throws Exception {
		m.save();
	}

	/**
	 * 删除菜单
	 * 
	 * @param r
	 * @throws TorqueException 
	 */
	public void delete(Menu m) throws TorqueException {
		deleteByKey(""+m.getId());
	}

	/**
	 * 根据主键删除菜单及子菜单
	 * 
	 * @param key
	 * @throws TorqueException 
	 */
	public void deleteByKey(String key) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(MenuResourcePeer.MENU_ID,key);
		MenuResourcePeer.doDelete(criteria);
		
		delete(key);
		
		criteria = new Criteria();
		criteria.add(MenuPeer.FATHER_ID,key);
		List<Menu> menus=MenuPeer.doSelect(criteria);
		
		for(Menu menu:menus){
			delete(""+menu.getId());
		}
		
		MenuPeer.doDelete(criteria);
	}
	
	public void delete(String key) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(RoleMenuPeer.MENU_ID,key);
		RoleMenuPeer.doDelete(criteria);
		
		criteria = new Criteria();
		criteria.add(MenuPeer.ID,key);
		MenuPeer.doDelete(criteria);
	}

	public List<MenuVO> searchMenu() throws TorqueException{
		MenuVO mvo=null;
		List<MenuVO> mvolist=new ArrayList<MenuVO>();
		List<Menu> ml=null;
		List<Menu> subml=null;
		ml=findAllFatherMenu(-1);
		if(null!=ml&&ml.size()>0){
			for(Menu m:ml){
				mvo=new MenuVO();
				mvo.setLevel(1);
				mvo.setM(m);
				mvolist.add(mvo);
				subml=findAllFatherMenu(m.getId());
				if(null!=subml&&subml.size()>0){
					for(Menu menu:subml){
						mvo=new MenuVO();
						mvo.setLevel(2);
						mvo.setM(menu);
						mvolist.add(mvo);
					}
				}
			}
		}
		return mvolist;
	}
	
	/**
	 * 查找出所有菜单
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findAll() throws TorqueException {
		List<Menu> ul = new ArrayList<Menu>();
		Criteria criteria = new Criteria();
		criteria.add(MenuPeer.ID, (Object) ("ID is not null"),
				Criteria.CUSTOM);
		ul = MenuPeer.doSelect(criteria);
		return ul;
	}

	/**
	 * 查找出同级别菜单
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findAllByLevel(int level) throws TorqueException {
		List<Menu> ul = new ArrayList<Menu>();
		Criteria criteria = new Criteria();
		if(1==level)
			criteria.add(MenuPeer.FATHER_ID, -1);
		else criteria.add(MenuPeer.FATHER_ID, -1,SqlEnum.NOT_EQUAL);
		ul = MenuPeer.doSelect(criteria);
		return ul;
	}
	
	/**
	 * 根据父菜单查找出所有子菜单
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findAllFatherMenu(int father_id) throws TorqueException {
		List<Menu> ul = null;
		Criteria criteria = new Criteria();
		criteria.add(MenuPeer.FATHER_ID, father_id);
		criteria.addAscendingOrderByColumn(MenuPeer.ID);
		ul = MenuPeer.doSelect(criteria);
		return ul;
	}
	
	/**
	 * 查找出所有特定的菜单
	 * @param f true:已经匹配资源的菜单 false:未匹配的菜单
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findMenusResource(boolean f) throws TorqueException {
		List<MenuResource> ul = new ArrayList<MenuResource>();
		Criteria criteria = new Criteria();
		criteria.add(MenuResourcePeer.ID, (Object) ("ID is not null"),
				Criteria.CUSTOM);
		ul = MenuResourcePeer.doSelect(criteria);
		List<Menu> rml=new ArrayList<Menu>();
		if(null!=ul&&ul.size()>0){
			for(MenuResource mr:ul){
				rml.add(mr.getMenu());
			}
		}
		if(f) return rml;
		else{
		List<Menu> ral=findAllByLevel(2);
		ral.removeAll(rml);
		return ral;
		}
	}
	
	/**
	 * 查找出所有特定的菜单
	 * @param f true:已经匹配角色的菜单 false:未匹配的菜单
	 * @return
	 * @throws TorqueException 
	 */
	public List<Menu> findMenusRole(boolean f) throws TorqueException {
		List<RoleMenu> ul = new ArrayList<RoleMenu>();
		Criteria criteria = new Criteria();
		criteria.add(RoleMenuPeer.ID, (Object) ("ID is not null"),
				Criteria.CUSTOM);
		ul = RoleMenuPeer.doSelect(criteria);
		List<Menu> rml=new ArrayList<Menu>();
		if(null!=ul&&ul.size()>0){
			for(RoleMenu mr:ul){
				rml.add(mr.getMenu());
			}
		}
		if(f) return rml;
		else{
		List<Menu> ral=findAllByLevel(2);
		ral.removeAll(rml);
		return ral;
		}
	}
	
	/**
	 * 根据主键查找菜单
	 * 
	 * @param id
	 * @return
	 * @throws TorqueException 
	 * @throws NumberFormatException 
	 * @throws TooManyRowsException 
	 * @throws NoRowsException 
	 */
	public Menu findById(int id) throws NoRowsException, TooManyRowsException, NumberFormatException, TorqueException {
		return MenuPeer.retrieveByPK(id);
	}
}
