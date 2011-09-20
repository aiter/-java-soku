package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.sort.json.util.JSONUtil;
import com.youku.soku.library.load.ProgrammeDouban;
import com.youku.soku.library.load.ProgrammeDoubanMore;
import com.youku.soku.library.load.ProgrammeDoubanMorePeer;
import com.youku.soku.library.load.ProgrammeDoubanPeer;
import com.youku.soku.library.load.dao.ProgrammeDoubanDao;
import com.youku.soku.library.load.douban.DoubanWget;
import com.youku.soku.library.load.util.SyncUtil;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;

public class ProgrammeDoubanAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

//	private static final String TABNAME = "anime_episode";
	
	public String listdouban() throws Exception {
		int page = getPage();
		int size = 20;
		
		int totalSize = 0;
		Criteria criteria0 = new Criteria();
		if(getStatus()!=0){
			criteria0.add(ProgrammeDoubanPeer.STATUS, getStatus());
		}
		if(getCate()>0){
			criteria0.add(ProgrammeDoubanPeer.CATE, getCate());
		}
		List<ProgrammeDouban> pdMore = ProgrammeDoubanPeer.doSelect(criteria0);
		if(pdMore!=null){
				totalSize = pdMore.size();
		}
		
		setTotalSize(totalSize);
		int totalPage = totalSize%size==0?totalSize/size:totalSize/size+1;
		setTotalPage(totalPage);
		if(page>totalPage || page<1){
			page = 1;
		}
		setPage(page);
		int start = (page-1)*size;
		
		Criteria criteria1 = new Criteria();
		if(getStatus()!=0){
			criteria1.add(ProgrammeDoubanPeer.STATUS, getStatus());
		}
		if(getCate()>0){
			criteria1.add(ProgrammeDoubanPeer.CATE, getCate());
		}
		criteria1.setOffset(start);
		criteria1.setLimit(size);
		
		List<ProgrammeDouban> pdList = ProgrammeDoubanPeer.doSelect(criteria1);
		
		setPdList(pdList);
		return "listdouban";
	}
	
	public String cancel() throws Exception {
		ProgrammeDoubanDao doubanDao = ProgrammeDoubanDao.getInstance();
		
		if(getContentId()>0){
			ProgrammeDouban pDouban = doubanDao.isExist(getContentId());
			if(pDouban!=null){
				if(getType()==1){
					pDouban.setStatus(1);
				}else if (getType()==-1) {
					pDouban.setStatus(-1);
				} 
				if(pDouban.isModified()){
					pDouban.setUpdateTime(new Date());
				}
				
				pDouban.save();
			}
		}
		
		return "rec_listdouban";
	}

	/**
	 * List the AnimeEpisode
	 * 
	 * @return the "list" result for this mapping
	 */
	public String list() throws Exception {

		int page = getPage();
		int size = 20;
		
		int totalSize = 0;
//		System.out.println("start:"+totalSize);
//		List<Integer> counts = ProgrammeDoubanMorePeer.executeQuery("select count(1) from (select id from programme_douban_more where status=1 group by fk_programme_id order by id desc) tmp");
		Criteria criteria0 = new Criteria();
		criteria0.add(ProgrammeDoubanMorePeer.STATUS, 1);
		if(getCate()>0){
			criteria0.add(ProgrammeDoubanMorePeer.CATE, getCate());
		}
		criteria0.addGroupByColumn(ProgrammeDoubanMorePeer.FK_CONTENT_ID);
		criteria0.addDescendingOrderByColumn(ProgrammeDoubanMorePeer.ID);
		List<ProgrammeDoubanMore> pdMore = ProgrammeDoubanMorePeer.doSelect(criteria0);
		if(pdMore!=null){
				totalSize = pdMore.size();
		}
		
//		System.out.println("end:"+totalSize);
		
		setTotalSize(totalSize);
		int totalPage = totalSize%size==0?totalSize/size:totalSize/size+1;
		setTotalPage(totalPage);
		if(page>totalPage || page<1){
			page = 1;
		}
		setPage(page);
		int start = (page-1)*size;
		
//		System.out.println(start+" size:"+size);
		
		Criteria criteria = new Criteria();
		criteria.add(ProgrammeDoubanMorePeer.STATUS, 1);
		if(getCate()>0){
			criteria.add(ProgrammeDoubanMorePeer.CATE, getCate());
		}
		criteria.addGroupByColumn(ProgrammeDoubanMorePeer.FK_CONTENT_ID);
		criteria.addDescendingOrderByColumn(ProgrammeDoubanMorePeer.ID);
		criteria.setOffset(start);
		criteria.setLimit(size);
		List<ProgrammeDoubanMore> ids = ProgrammeDoubanMorePeer.doSelect(criteria);
		Set<Integer> programmeIdSet = null;
		if(ids!=null){
			programmeIdSet = new HashSet<Integer>();
			for (ProgrammeDoubanMore pdMod : ids) {
				programmeIdSet.add(pdMod.getFkContentId());
			}
			
//			System.out.println(" id size:"+ids.size());
		}
		
		
		
		if(programmeIdSet!=null){
			Criteria criteria1 = new Criteria();
			for (Integer pid : programmeIdSet) {
				criteria1.or(ProgrammeDoubanMorePeer.FK_CONTENT_ID, pid);
			}
			criteria1.addDescendingOrderByColumn(ProgrammeDoubanMorePeer.FK_CONTENT_ID);
			criteria1.addAscendingOrderByColumn(ProgrammeDoubanMorePeer.ID);
			
			List<ProgrammeDoubanMore> pdMoreList = ProgrammeDoubanMorePeer.doSelect(criteria1);
			Map<Integer, List<ProgrammeDoubanMore>> map = new HashMap<Integer, List<ProgrammeDoubanMore>>();
			List<ProgrammeDoubanMore> tmp = null;
			for (ProgrammeDoubanMore pdMore0 : pdMoreList) {
				tmp = map.get(pdMore0.getFkContentId());
				if(tmp==null){
					tmp = new ArrayList<ProgrammeDoubanMore>();
					map.put(pdMore0.getFkContentId(), tmp);
				}
				
				tmp.add(pdMore0);
				tmp = null;
			}
			
			setPdMore(map);
		}

		return Constants.LIST;
	}
	
	public String confirm() throws Exception {
		ProgrammeDoubanDao doubanDao = ProgrammeDoubanDao.getInstance();
		boolean success = doubanDao.comfirm(getContentId(), getDoubanId());//将确定的一条加入到豆瓣对应表中
		if(success){
			//添加成功后，将审核表的数据都标记为删除状态
			doubanDao.deleteByPid(getContentId());
		}
		
		return SUCCESS;
	}
	
	public String detail() throws Exception {
		if(getDoubanId()>0 && getContentId()>0){
//			ProgrammeDoubanDao doubanDao = ProgrammeDoubanDao.getInstance();
//			doubanDao.isExist(getPId());
			
				JSONObject showJsonObject = SyncUtil.buildProgrammeDoubanInfoByID(getContentId());
				String youkuInfo = getYoukuInfo(showJsonObject);
				
				
				if(!JSONUtil.isEmpty(showJsonObject)){
					//暂时不能访问外网
					JSONObject doubanJsonObject = DoubanWget.getMovie(getDoubanId()+"");
//					System.out.println(doubanJsonObject);
					String doubanInfo = getDoubanInfo(doubanJsonObject);
					
					HttpServletResponse response = ServletActionContext.getResponse();
					response.setContentType("text/html;charset=utf-8");
					PrintWriter out = response.getWriter();
//					out.print(youkuInfo);
					out.print(youkuInfo+"<br/><span style='color:#007733;'>"+doubanInfo+"</span>");
				}
		}
		
//		JSONObject doubanJsonObject = DoubanWget.getMovie(1296177+"");
//		System.out.println(doubanJsonObject.toString(4));
		
		
		return null;
	}
	
	/**
	 * @param showJsonObject
	 * @return
	 */
	private String getYoukuInfo(JSONObject showJsonObject) {
		if(!JSONUtil.isEmpty(showJsonObject)){
		JSONArray youkuAttr = showJsonObject.optJSONArray("results");
		if(!JSONUtil.isEmpty(youkuAttr)){
			JSONObject youkuAttrObject = youkuAttr.optJSONObject(0);
			if(!JSONUtil.isEmpty(youkuAttrObject)){
				StringBuilder sBuilder = new StringBuilder();
				for (Iterator iterator = youkuAttrObject.keys(); iterator
						.hasNext();) {
					String key = (String) iterator.next();
					if("pk_odshow".equals(key)||"showid".equals(key)||"releaseyear".equals(key)){
						continue;
					}
					Object object = null;
					try {
						object = youkuAttrObject.get(key);
						if (object instanceof JSONArray) {
							JSONArray tmpArry = (JSONArray)object;
							for (int j = 0; j < tmpArry.length(); j++) {
								sBuilder.append(tmpArry.get(j)).append("/");
							}
						}else {
							sBuilder.append(object).append("/");
						}
					} catch (JSONException e) {
					}
					
				}
				
//				System.out.println(sBuilder.toString());
				return sBuilder.toString();
			}
		}
		}
		
		return "";
	}

	/**
	 * @param showJsonObject
	 * @return
	 */
	private String getDoubanInfo(JSONObject showJsonObject) {
		if(!JSONUtil.isEmpty(showJsonObject)){
			JSONArray doubanAttr = showJsonObject.optJSONArray("db:attribute");
			if(!JSONUtil.isEmpty(doubanAttr)){
				StringBuilder sbBuilder = new StringBuilder();
				for (int i = 0; i < doubanAttr.length(); i++) {
					String name = doubanAttr.optJSONObject(i).optString("@name");
					if(name!=null && name.length()>0){
						if("language".equals(name)||"imdb".equals(name)||"language".equals(name)||"episodes".equals(name)||"website".equals(name)){
							continue;
						}
						sbBuilder.append(doubanAttr.optJSONObject(i).optString("$t")).append("/");
					}
				}
//				System.out.println(sbBuilder);
				return sbBuilder.toString();
			}
		}
		
		return "";
	}
		

	public static void main(String[] args) throws Exception {
		ProgrammeDoubanAction action = new ProgrammeDoubanAction();
		action.detail();
	}

	
	private int page=1;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	private int status=0;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	private int cate=0;
	
	public int getCate() {
		return cate;
	}
	
	public void setCate(int cate) {
		this.cate = cate;
	}

	private int totalSize;
	
	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	
	private int totalPage;

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	private Map<Integer, List<ProgrammeDoubanMore>> pdMore;

	public Map<Integer, List<ProgrammeDoubanMore>> getPdMore() {
		return pdMore;
	}

	public void setPdMore(Map<Integer, List<ProgrammeDoubanMore>> pdMore) {
		this.pdMore = pdMore;
	}
	
	private int contentId;
	
	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}


	private int doubanId;

	public int getDoubanId() {
		return doubanId;
	}

	public void setDoubanId(int doubanId) {
		this.doubanId = doubanId;
	}

	private List<ProgrammeDouban> pdList;

	public List<ProgrammeDouban> getPdList() {
		return pdList;
	}

	public void setPdList(List<ProgrammeDouban> pdList) {
		this.pdList = pdList;
	}
	
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
