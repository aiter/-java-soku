/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.util.HashMap;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.DataSetException;
import com.workingdogs.village.Record;
import com.youku.soku.util.MyUtil;
import com.youku.soku.util.DataBase;

/**
 * @author 1verge
 *
 */
public class YoukuCateManager {
	private static YoukuCateManager self = null;
	private static HashMap<Integer,String> cateMap = new HashMap<Integer,String>();
	
	public static YoukuCateManager getInstance(){
		
		if(self == null){
				self = new YoukuCateManager();
				self.init();
		}
		return self;
	}

	public synchronized void init()
	{
		List<Record> list = getCateList();
		if (list != null)
		{
			cateMap.clear();
			for (Record record:list)
			{
				try {
					cateMap.put(record.getValue("cate_id").asIntegerObj(), MyUtil.getString(record.getValue("cate_name").asString()));
				} catch (DataSetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Record> getCateList()
	{
		try {
			return BasePeer.executeQuery("SELECT cate_id, cate_name from t_cate_info WHERE cate_type = 'VIDEO_CATEGORY' or cate_type = 'FOLDER_CATEGORY'",false,DataBase.getYoukuMappingConnection());
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getName(int cate_id)
	{
		return cateMap.get(cate_id);
	}

}
