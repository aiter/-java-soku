package com.youku.soku.manage.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.service.CorrectionService;
import com.youku.soku.manage.torque.Correction;
import com.youku.soku.manage.torque.CorrectionPeer;

public class CorrectionAction extends BaseActionSupport {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Correction correction;
	private String searchWord;
	private int status=-1;
	private int correctionId;
	private String[] batchids;
	private int pageNumber;
	private PageInfo pageInfo;
	private List<Correction> correctionList;
	private String keyword;
	private int updateid;
	private String all_correct_keyword;
	
	public String getAll_correct_keyword() {
		return all_correct_keyword;
	}

	public void setAll_correct_keyword(String allCorrectKeyword) {
		all_correct_keyword = allCorrectKeyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public int getUpdateid() {
		return updateid;
	}

	public void setUpdateid(int updateid) {
		this.updateid = updateid;
	}

	public List<Correction> getCorrectionList() {
		return correctionList;
	}

	public void setCorrectionList(List<Correction> correctionList) {
		this.correctionList = correctionList;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	

	public String[] getBatchids() {
		return batchids;
	}

	public void setBatchids(String[] batchids) {
		this.batchids = batchids;
	}

	public int getCorrectionId() {
		return correctionId;
	}

	public void setCorrectionId(int correctionId) {
		this.correctionId = correctionId;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Correction getCorrection() {
		return correction;
	}

	public void setCorrection(Correction correction) {
		this.correction = correction;
	}

	public String save(){
		Correction correct = getCorrection();
		if(null==correct){
			return INPUT;
		}
		Correction c = null;
		try {
		if(correct.getId()<1){
			
			c = CorrectionService.findCorrectionByCorrectKeyword(correct.getCorrectKeyword());
			
			if(null==c){
				correct.setCreateTime(new Date());
				correct.setUpdateTime(new Date());
				correct.save();
				logger.info("after insert correction:"+correct.toString());
			}else{
				addActionError("数据库中存在相同的词,"+correct.getCorrectKeyword());
				return INPUT;
			}
		}else{
			logger.info("before update correction:"+CorrectionPeer.retrieveByPK(correct.getId()).toString());
			correct.setUpdateTime(new Date());
			correct.setModified(true);
			correct.setNew(false);
			correct.save();
			logger.info("after update correction:"+correct.toString());
		}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		return list();
	}
	
	public String update(){
		int s = getStatus();
		int update_id = getUpdateid();
		String keyword = getKeyword();
		try {
			CorrectionService.update(update_id, s, keyword);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	public String volumeAdd(){
		return "volumeAdd";
	}
	
	public String volumeSave(){
		if(StringUtils.isBlank(all_correct_keyword))
			return Constants.LIST;
		List<String> list = CorrectionService.parseStr2List(all_correct_keyword,"\n");
		String parsemessage = null;
		String existmessage = null;
		List<String> parselist = new ArrayList<String>();
		List<String> existlist = new ArrayList<String>();
		if(null!=list){
			List<String> line = null;
			int i=0;
			int j=0;
			for(String res:list){
				line = CorrectionService.parseStr2List(res,"&");
				if(null==line||line.size()!=2){
					if(i==0){
						parsemessage="下面的词解析出错:";
						i=i+1;
					}
					parselist.add(res);
				}else{
						Correction m = null;
						try{
							m = CorrectionService.findCorrectionByCorrectKeyword(line.get(0));
							
						}catch(Exception e){
							if(j==0){
								existmessage="下面的词数据库保存到数据库出错:";
								j=j+1;
							}
							existlist.add(res);
							continue;
						}
						if(null==m){
							m = new Correction();
							m.setCorrectKeyword(line.get(0));
							m.setCreateTime(new Date());
						}else
							logger.info("before update correction:"+m.toString());
						m.setKeyword(line.get(1).trim());
						m.setUpdateTime(new Date());
						m.setStatus(1);
						try {
							m.save();
							logger.info("after updateOrInsert correction:"+m.toString());
						} catch (Exception e) {
							if(j==0){
								existmessage="下面的词数据库保存到数据库出错:";
								j=j+1;
							}
							existlist.add(res);
						}
						
					}
				}
		}else{
			parsemessage="解析出错.";
		}
		if(existlist.size()==0&&parselist.size()==0){
			return list();
		}else{
			if(parselist.size()!=0){
				addActionError(parsemessage);
				for(String ps:parselist){
					addActionError(ps);
				}
			}
			if(existlist.size()!=0){
				addActionError(existmessage);
				for(String es:existlist){
					addActionError(es);
				}
			}
			return "volumeAdd";
		}
	}
	
	public String input(){
		int correction_id = getCorrectionId();
		if(0>=correction_id){
			setTask(Constants.CREATE);
			correction = new Correction();
			return INPUT;
		}else{
			try {
				correction = CorrectionPeer.retrieveByPK(correction_id);
				setTask(Constants.EDIT);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return INPUT;
	}
	
	public String list(){
		String k = getSearchWord();
		int s = getStatus();
		try {
			if(null==pageInfo)
				pageInfo=new PageInfo();
			pageInfo.setPageSize(30);
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());
			CorrectionService.findCorrectionPagination(pageInfo, k, s);
			pageInfo.setCurrentPageNumber(getPageNumber());
			setPageInfo(pageInfo);
		} catch (Exception e) {
			logger.error(e);
		}
		return Constants.LIST;
	}
	
	public String batchdelete(){
		for(String deleteId : getBatchids()) {
			int did = DataFormat.parseInt(deleteId);
			if(0==did) continue;
			deleteById(did);
		}
		return list();
	}
	
	public String delete(){
		deleteById(getCorrectionId());
		return list();
	}
	
	private void deleteById(int id){
		try {
			Correction corr = CorrectionPeer.retrieveByPK(id);
			CorrectionPeer.doDelete(corr);
			logger.info("delete correction:"+corr.toString());
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
