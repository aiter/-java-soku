package com.youku.search.console.operate.juji;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.PlayName;
import com.youku.search.console.pojo.PlayNamePeer;
import com.youku.search.console.vo.TeleName;

public class PlayNameMgt {
	static Log logger = LogFactory.getLog(PlayNameMgt.class);

	private PlayNameMgt() {
	}

	private static PlayNameMgt instance = null;

	public static synchronized PlayNameMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new PlayNameMgt();
			return instance;
		}
	}
	
	public void executeSql(String sql){
		try {
			PlayNamePeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			logger.error(sql,e);
		}
	}
	
	public void deleteByTeleplayId(int tid){
		Criteria criteria = new Criteria();
		criteria.add(PlayNamePeer.FK_TELEPLAY_ID,tid);
		try {
			PlayNamePeer.doDelete(criteria);
		} catch (Exception e) {
			logger.error(tid,e);
		}
	}
	
	public List<PlayName> getPlayNamesByTeleplayID(int tid){
		Criteria criteria = new Criteria();
		criteria.add(PlayNamePeer.FK_TELEPLAY_ID,tid);
		try {
			List<PlayName> pns = PlayNamePeer.doSelect(criteria);
			if(null!=pns&&pns.size()>0){
				return pns;
			}
		} catch (Exception e) {
			logger.error("tid:"+tid,e);
		}
		return null;
	}
	
	public List<PlayName> getAllPlayNames(){
		Criteria criteria = new Criteria();
		try {
			List<PlayName> pns = PlayNamePeer.doSelect(criteria);
			if(null!=pns&&pns.size()>0){
				return pns;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public String getTeleplayMainName(int tid) {
		String sql = "select name from play_name where fk_teleplay_id="+tid+" and is_main=1";
		try {
			List<Record> res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			if(res!=null&&res.size()>0){
				String name =  res.get(0).getValue("name").asString();
				if(!StringUtils.isBlank(name)&&!name.trim().toLowerCase().equalsIgnoreCase("null"))
					return name.trim();
			}
		} catch (Exception e) {
			logger.error(tid,e);
		}
		return null;
	}
	
	public TeleName getTeleplayNameByIdReturnTeleName(int tid) {
		String sql = "select name from play_name where fk_teleplay_id=" + tid
				+ " order by is_main desc";
		List<String> names=new ArrayList<String>();
		TeleName telename=new TeleName();
		try {
			List<Record> res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			String name = null;
			int i = 0;
			for(Record r:res) {
				name = r.getValue("name").asString();
				if (!StringUtils.isBlank(name)&&!name.trim().toLowerCase().equalsIgnoreCase("null")) {
					if(i==0)
						telename.setName(name);
					else
						names.add(name);
				}
				i++;
			}
			if(null!=names&&names.size()>0)
				telename.setAlias(names.toArray(new String[]{}));
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return telename;
	}
	
	/**
	 * 竖线隔开的系列名
	 * @param id
	 * @return
	 */
	public String getTeleplayNameByIdReturnString(int tid) {
		TeleName tn = getTeleplayNameByIdReturnTeleName(tid);
		StringBuilder bf = new StringBuilder();
		if(StringUtils.isBlank(tn.getName()))
			return null;
		bf.append(tn.getName());
		if(null!=tn.getAlias()&&tn.getAlias().length>0){
			for(String a:tn.getAlias()){
				bf.append("|");
				bf.append(a);
			}
		}
		return bf.toString();
	}
	
	public Set<String> getTeleplayNameByIdReturnSet(int tid){
		TeleName tn = getTeleplayNameByIdReturnTeleName(tid);
		if(StringUtils.isBlank(tn.getName()))
			return null;
		Set<String> set = new HashSet<String>();
		set.add(tn.getName());
		if(null!=tn.getAlias()&&tn.getAlias().length>0){
			for(String a:tn.getAlias()){
				if(!StringUtils.isBlank(a))
					set.add(a);
			}
		}
		return set;
	}
	
	public void playNameSave(PlayName p,int tid,String name, Collection<String> aliases,int subcate){
		try{
		p.setFkTeleplayId(tid);
		p.setName(name);
		p.setIsMain(1);
		p.save();
		if(null!=aliases){
		for (String alias : aliases) {
			if (null != alias && alias.trim().length() > 0) {
				if(!nameIsExist(alias, null, subcate)){
				p = new PlayName();
				p.setFkTeleplayId(tid);
				p.setName(alias);
				p.setIsMain(0);
				p.save();
				}
			}
		}
		}
		}catch(Exception e){
			logger.error(p,e);
		}
	}
	
	public void aliasSave(int tid,Collection<String> aliases,int subcate){
		
		PlayName p = null;
		if(null!=aliases){
		for (String alias : aliases) {
			if (null != alias && alias.trim().length() > 0) {
				try{
					if(!nameIsExist(alias, null, subcate)){
						p = new PlayName();
						p.setFkTeleplayId(tid);
						p.setName(alias);
						p.setIsMain(0);
						p.save();
					}
				}catch(Exception e){
					logger.error(e);
				}
			}
		}
		}
		
	}
	
	public static boolean nameIsExist(String name, Collection<String> aliases,int subcate){
		StringBuffer nameWhere=new StringBuffer(" ( name='"+name+"'");
		if(null!=aliases){
			for(String alias:aliases){
				if(null!=alias&&alias.trim().length()>0){
					nameWhere.append(" or name='");
					nameWhere.append(alias);
					nameWhere.append("'");
				}
			}
		}
		nameWhere.append(" )");
		String sql="select a.id from teleplay a,play_name b where a.id=b.fk_teleplay_id and a.subcate="+subcate+" and "+nameWhere.toString();
		List<Record> res;
		try {
			res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return true;
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return false;
	}
	
	public static boolean nameIsOtherExist(String name,int subcate,int playnameId){
		String sql="select a.id from teleplay a,play_name b where a.id=b.fk_teleplay_id and a.subcate="+subcate+" and b.name='"+name+"' and b.id!="+playnameId;
		//System.out.println("sql:"+sql);
		List<Record> res;
		try {
			res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return true;
		} catch (TorqueException e) {
			logger.error(sql,e);
		}
		return false;
	}
	
	public static boolean aliasIsExist(String[] aliases,int subcate,int tid){
		StringBuffer nameWhere=new StringBuffer();
		if(null!=aliases&&aliases.length>0){
			int f=1;
			for(int i=0;i<aliases.length;i++){
				if(null!=aliases[i]&&aliases[i].trim().length()>0){
					if(f==1)
						nameWhere.append(" and (");
					else if(f==2)
						nameWhere.append(" or ");
					f=2;
					nameWhere.append(" name='");
					nameWhere.append(aliases[i]);
					nameWhere.append("'");
				}
			}
			if(nameWhere!=null&&nameWhere.length()>0)
				nameWhere.append(")");
		}
		String sql="select a.id from teleplay a,play_name b where a.id=b.fk_teleplay_id and a.subcate="+subcate+" and a.id!= "+tid+nameWhere.toString();
		List<Record> res;
		try {
			res = PlayNamePeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return true;
		} catch (TorqueException e) {
			logger.error(sql,e);
		}
		return false;
	}
		
}
