package com.youku.search.console.operate;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.search.console.pojo.Synonym;
import com.youku.search.console.pojo.SynonymPeer;
import com.youku.search.util.StringUtil;

public class SynonymMgt {
	
	private static SynonymMgt self = null;
	private SynonymMgt(){}
	public  static synchronized SynonymMgt getInstance(){
		
		if(self == null){
			self = new SynonymMgt();
		}
		return self;
	}
	
	/**
	 * 根据关键字查找同义词
	 * @throws TorqueException 
	 */
	public List<Synonym> findSynonyms(String keyword,int page,int pagesize,int state) throws TorqueException{
		List<Synonym> synonyms=new ArrayList<Synonym>();
		Criteria criteria = new Criteria();
		criteria.add(SynonymPeer.STATE, state);
		if(StringUtil.isNotNull(keyword)) criteria.add(SynonymPeer.KEYWORDS,(Object)("%"+keyword+"%"),SqlEnum.LIKE);
		criteria.setLimit(pagesize);
		criteria.setOffset((page-1)*pagesize);
		synonyms=SynonymPeer.doSelect(criteria);
		return synonyms;
	}
	
	public int findSynonymsCounts(String keyword,int state) throws TorqueException{
		List<Synonym> synonyms=new ArrayList<Synonym>();
		Criteria criteria = new Criteria();
		criteria.add(SynonymPeer.STATE, state);
		if(StringUtil.isNotNull(keyword)) criteria.add(SynonymPeer.KEYWORDS,(Object)("%"+keyword+"%"),SqlEnum.LIKE);
		synonyms = SynonymPeer.doSelect(criteria);
		if(synonyms.isEmpty())return 0;
		return synonyms.size();
	}
	
	public Synonym findSynonym(int id) throws TorqueException{
		return SynonymPeer.retrieveByPK(id);
	}
	
	public void insertSynonym(Synonym synonym) throws Exception{
		synonym.save();
	}
	
	public void updateSynonym(Synonym synonym) throws Exception{
		SynonymPeer.executeStatement("update synonym set keywords='"+synonym.getKeywords()+"' where id= "+synonym.getId());
	}
	
	public void deleteSynonyms(String[] ids) throws Exception{
		for(String id:ids){
			deleteSynonym(id);
		}
	}
	
	public void deleteSynonym(String id) throws Exception{
		Criteria criteria = new Criteria();
		criteria.add(SynonymPeer.ID,id);
		SynonymPeer.doDelete(criteria);
	}
	
	public void deleteSynonym(Synonym synonym) throws Exception{
		deleteSynonym(""+synonym.getId());
	}
	
	public void updateSynonyms(String[] ids,int state) throws Exception{
		for(String id:ids){
			updateSynonym(id,state);
		}
	}
	
	public void updateSynonym(String id,int state) throws Exception{
		SynonymPeer.executeStatement("update synonym set state="+state+" where id= "+id);
	}
}
