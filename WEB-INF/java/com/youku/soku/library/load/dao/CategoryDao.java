/**
 * 
 */
package com.youku.soku.library.load.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Category;
import com.youku.soku.library.load.CategoryPeer;


/**
 * 分类category 的数据库操作类
 * @author liuyunjian
 * 2011-2-22
 */
public class CategoryDao {
	private CategoryDao(){}
	private static CategoryDao instance = null;
	public static synchronized CategoryDao getInstance(){
		if(null==instance){
			instance = new CategoryDao();
		}
		return instance;
	}
	
	/**
	 * 查询数据库所有的category 
	 */
	public List<Category> getAllCateList() throws TorqueException{
		Criteria criteria = new Criteria();
		return CategoryPeer.doSelect(criteria);
	}
	
	
	/**
	 * 查询数据库所有的category。返回Map
	 */
	public Map<String,Integer> getCateMap() throws TorqueException{
		Map<String,Integer> cateMap = new HashMap<String, Integer>();
		List<Category> cateList = CategoryDao.getInstance().getAllCateList();
		if(null!=cateList){
			for(Category c:cateList){
				cateMap.put(c.getName().trim(), c.getId());
			}
		}
		return cateMap;
	}
	
	
}
