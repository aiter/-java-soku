package com.youku.search.console.operate.rights;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.MenuResource;
import com.youku.search.console.pojo.MenuResourcePeer;
import com.youku.search.console.pojo.Resource;
import com.youku.search.console.pojo.ResourcePeer;
import com.youku.search.console.vo.ResourceVO;

public class ResourceMgt {
	private static ResourceMgt instance = null;

	public static synchronized ResourceMgt getInstance() {
		if (null != instance)
			return instance;
		else
			return new ResourceMgt();
	}

	/**
	 * 保存资源
	 * 
	 * @param r
	 * @throws Exception 
	 */
	public void save(Resource r) throws Exception {
		r.save();
	}

	/**
	 * 删除资源
	 * 
	 * @param r
	 * @throws TorqueException 
	 */
	public void delete(Resource r) throws TorqueException {
			deleteByKey(""+r.getId());
	}

	/**
	 * 根据主键删除资源
	 * 
	 * @param key
	 * @throws TorqueException 
	 */
	public void deleteByKey(String key) throws TorqueException {
		Criteria criteria = new Criteria();
		criteria.add(MenuResourcePeer.RESOURCE_ID,key);
		MenuResourcePeer.doDelete(criteria);
		
		criteria = new Criteria();
		criteria.add(ResourcePeer.ID,key);
		ResourcePeer.doDelete(criteria);
	}
	
	/**
	 * 查找出所有有效资源
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<ResourceVO> findAllResVO() throws TorqueException {
		List<ResourceVO> ul = new ArrayList<ResourceVO>();
		List<MenuResource> mrl=null;
		List<Resource> rl=findAll();
		if(null!=rl&&rl.size()>0){
			ResourceVO rvo=null;
			Criteria criteria =null;
			for(Resource r:rl){
				rvo=new ResourceVO();
				rvo.setR(r);
				criteria = new Criteria();
				criteria.add(MenuResourcePeer.RESOURCE_ID,r.getId());
				mrl=MenuResourcePeer.doSelect(criteria);
				if(null!=mrl&&mrl.size()>0)
					rvo.setMenuid(mrl.get(0).getMenuId());
				ul.add(rvo);
			}
		}
		return ul;
	}

	/**
	 * 查找出所有有效资源
	 * 
	 * @return
	 * @throws TorqueException 
	 */
	public List<Resource> findAll() throws TorqueException {
		List<Resource> ul = new ArrayList<Resource>();
		Criteria criteria = new Criteria();
		criteria.add(ResourcePeer.ID, (Object) ("ID is not null"),
				Criteria.CUSTOM);
		ul = ResourcePeer.doSelect(criteria);
		return ul;
	}
	
	/**
	 * 查找出所有特定的资源
	 * @param f true:已经匹配菜单的资源 false:未匹配菜单的资源
	 * @return
	 * @throws TorqueException 
	 */
	public List<Resource> findResources(boolean f) throws TorqueException {
		List<MenuResource> ul = new ArrayList<MenuResource>();
		Criteria criteria = new Criteria();
		criteria.add(MenuResourcePeer.ID, (Object) ("ID is not null"),
				Criteria.CUSTOM);
		ul = MenuResourcePeer.doSelect(criteria);
		List<Resource> rml=new ArrayList<Resource>();
		if(null!=ul&&ul.size()>0){
			for(MenuResource mr:ul){
				rml.add(mr.getResource());
			}
		}
		if(f) return rml;
		else{
		List<Resource> ral=findAll();
		ral.removeAll(rml);
		return ral;
		}
	}
	
	/**
	 * 根据主键查找资源
	 * 
	 * @param id
	 * @return
	 * @throws TorqueException 
	 * @throws NumberFormatException 
	 * @throws TooManyRowsException 
	 * @throws NoRowsException 
	 */
	public Resource findById(String id) throws NoRowsException, TooManyRowsException, NumberFormatException, TorqueException {
		return ResourcePeer.retrieveByPK(Integer.parseInt(id));
	}
	
	/**
	 * 根据资源匹配菜单
	 * @param resid
	 * @param menuid
	 * @throws Exception 
	 */
	public void match(String resid,String menuid) throws Exception{
		MenuResource mr=new MenuResource();
		mr.setResourceId(Integer.parseInt(resid));
		mr.setMenuId(Integer.parseInt(menuid));
		mr.save();
	}
	
	/**
	 * 根据资源取消匹配的菜单
	 * @param resid
	 * @param menuid
	 * @throws Exception 
	 */
	public void unmatch(String resid) throws Exception{
		Criteria criteria = new Criteria();
		criteria.add(MenuResourcePeer.RESOURCE_ID,resid);
		MenuResourcePeer.doDelete(criteria);
	}
}
