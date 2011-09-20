/**
 * 
 */
package com.youku.soku.library.load.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.ForwardWordPeer;
import com.youku.soku.library.load.ProgrammeForwardWord;
import com.youku.soku.library.load.ProgrammeForwardWordPeer;

/**
 * 保存跳转词的对应关系
 * @author liuyunjian
 * 2011-4-23
 */
public class ForwardWord {
	private Log logger = LogFactory.getLog(getClass());
	private static ForwardWord instance = null;
	private static byte [] lock = new byte[1];
	private static Map<String, com.youku.soku.library.load.ForwardWord> map = new HashMap<String, com.youku.soku.library.load.ForwardWord>();
	
	private ForwardWord() {
		init();//
	}
	
	public static ForwardWord getInstance(){
		if(instance==null){
			synchronized (lock) {
				if(instance==null){
					instance = new ForwardWord();
				}
			}
		}
		
		return instance;
	}
	
	public void init(){
		Map<String, com.youku.soku.library.load.ForwardWord> tmp = null;;
		try {
			tmp = getAllWordMap();
			if(tmp!=null && tmp.size()>0){
				map = tmp;
				if(logger.isDebugEnabled()){
					logger.info("forwardWord.size:"+map.size());
				}
//				System.out.println("forwardWord.size:"+map.size());
			}
		} catch (TorqueException e) {
		}
	}
	
	public String getProgrammeId(String keyword){
		if(keyword==null || keyword.trim().length()==0){
			return "";
		}

		Map<String, com.youku.soku.library.load.ForwardWord> tmp = map;
		Object object = tmp.get(keyword.toLowerCase());
		tmp = null;
		
		if(object==null){
			return "";
		}else {
			com.youku.soku.library.load.ForwardWord pfw = (com.youku.soku.library.load.ForwardWord)object;
			Date startTime = pfw.getStartTime();
			Date expireTime = pfw.getExpireTime();//TODO  过期时间
			Date now = new Date();
			if(now.before(startTime)){
				return "";
			}
//			if(now.after(expireTime)){
//				return 0;
//			}
			
			return pfw.getForwardUrl();
		}
	}
	
	
	private Map<String, com.youku.soku.library.load.ForwardWord> getAllWordMap() throws TorqueException{
		Criteria criteria = new Criteria();
		List<com.youku.soku.library.load.ForwardWord> list = ForwardWordPeer.doSelect(criteria);
		Map<String, com.youku.soku.library.load.ForwardWord> map = new HashMap<String, com.youku.soku.library.load.ForwardWord>(list.size());
		if(null!=list){
			for(com.youku.soku.library.load.ForwardWord c:list){
				map.put(c.getForwardWord().trim().toLowerCase(), c);
			}
		}
		return map;
	}
	
}
