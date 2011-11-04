package com.youku.soku.manage.shield;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.youku.search.sort.entity.CategoryMap;
import com.youku.search.sort.entity.CategoryMap.Category;
import com.youku.search.util.DataFormat;
import com.youku.soku.manage.bo.ShieldWordsBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.entity.HitRolesConstants;
import com.youku.soku.manage.entity.ShieldSiteConstants;
import com.youku.soku.manage.entity.ShieldWordConstants;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ShieldWordService;
import com.youku.soku.manage.service.UserPermissionService;
import com.youku.soku.manage.torque.AuthPermission;
import com.youku.soku.manage.torque.DeterminerWordRelation;
import com.youku.soku.manage.torque.DeterminerWordRelationPeer;
import com.youku.soku.manage.torque.ShieldCategory;
import com.youku.soku.manage.torque.ShieldCategoryPeer;
import com.youku.soku.manage.torque.ShieldWordRelation;
import com.youku.soku.manage.torque.ShieldWordRelationPeer;
import com.youku.soku.manage.torque.ShieldWords;
import com.youku.soku.manage.torque.ShieldWordsPeer;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.torque.UserPermission;
import com.youku.soku.manage.util.JFConverter;

public class WordsAction extends BaseActionSupport {
	
	private Logger log = Logger.getLogger(this.getClass());
	

	
	/**
	 * <p>
	 * Create ShieldWord action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String input() throws Exception {
		setShieldChannelList(CategoryMap.getInstance().videoList);
		setShieldRangeList(getShieldRangeByAuthen());
		if(getWordId() == -1) {
			setTask(Constants.CREATE);
			return INPUT;
		} else {
			try {
				ShieldWords sw = ShieldWordsPeer.retrieveByPK(getWordId());
				ShieldWordsBo swBo = getShieldWordBo(sw);
				setShieldWordsBo(swBo);
				setTask(Constants.EDIT);
				
			} catch (NoRowsException e) {
				e.printStackTrace();
				throw new PageNotFoundException(getText("error.page.not.found"));
			}
			return INPUT;
		}
		
		
	}
	
	/**
	 * <p>
	 * Insert or update an item
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}
	
	/**
	 * <p>
	 * Delete an shield word from the persistence store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String delete() throws Exception {

		ShieldWords item = ShieldWordsPeer.retrieveByPK(getWordId());
		ShieldWordsPeer.doDelete(item);

		return SUCCESS;
	}
	
	/**
	 * <p>Batch delete a shield word from the persistent store</p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception on any error
	 */
	public String batchdelete() throws Exception {
		
		
		for(int deleteId : getBatchdeleteids()) {
			log.info("#########delete id is: " + deleteId);
			ShieldWords item = ShieldWordsPeer.retrieveByPK(deleteId);
			ShieldWordsPeer.doDelete(item);			
		}
		
		return SUCCESS;
	}
	
	/**
	 * <p>
	 * Insert or update an shield word object to the persistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String execute() throws Exception{

		try {
			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			
			JFConverter converter = new JFConverter();
			String word = converter.traditionalized(getShieldWordsBo().getWord());
			word = word.toLowerCase();
			String excluding = converter.traditionalized(getShieldWordsBo().getExcluding());
			int type = getShieldWordsBo().getType();
			boolean wordExist = checkWordExist(word, type);
			
			if(getShieldWordsBo().getType() == ShieldWordConstants.SHIELDWORDTYPE || getShieldWordsBo().getType() == ShieldWordConstants.BOTHWORDTYPE) {
				if(getShieldWordsBo().getShieldChannelIdList() == null) {
					addActionError(getText("error.shieldchannel.select"));
					return INPUT;
				}
			}
			if (creating) {			
				
				if (wordExist) {
					setShieldChannelList(CategoryMap.getInstance().videoList);
					addActionError(getText("error.words.unique"));
					return INPUT;
				}
				ShieldWordsBo swBo = getShieldWordsBo();
				ShieldWords sw = getShieldWordVo(getShieldWordsBo());
				sw.setWord(word);
				sw.setType(getShieldWordsBo().getType());
				sw.setExcluding(excluding);
				sw.setCreateTime(new Date());
				sw.setUpdateTime(new Date());
				sw.save();
				swBo.setId(sw.getId());
				saveShieldWordRelation(swBo);
			} else {
				ShieldWords sw = ShieldWordsPeer.retrieveByPK(getWordId());
				/*if (getShieldWordsBo().getType() == shieldWordType) {
					List<ShieldChannel> shieldChannelList = getShieldWordsBo()
							.getShieldChannelList();
					for (ShieldChannel shieldChannel : shieldChannelList) {
						ShieldWordRelation swr = new ShieldWordRelation();
						swr.setFkWordId(sw.getId());
						swr.setFkShieldChannelId(shieldChannel.getId());
						swr.save();
					}
				} else {
					DeterminerWordRelation dwr = new DeterminerWordRelation();
					dwr.setFkWordId(sw.getId());
					dwr.setSiteCategory(getShieldWordsBo().getWordSiteCategory());
					dwr.setSiteLevel(getShieldWordsBo().getWordSiteLevel());
					dwr.save();
				}*/
				if (!sw.getWord().equals(word) && wordExist) {
					setShieldChannelList(CategoryMap.getInstance().videoList);
					addActionError(getText("error.words.unique"));
					return INPUT;
				}
				sw.setWord(word);
				sw.setType(getShieldWordsBo().getType());
				sw.setExcluding(excluding);
				sw.setStartTime(getShieldWordsBo().getStartTime());
				sw.setExpireTime(getShieldWordsBo().getExpireTime());
				sw.setFkShieldCategoryId(getShieldWordsBo().getFkShieldCategoryId());
				sw.setHitRole(getShieldWordsBo().getHitRole());
				sw.setRemark(getShieldWordsBo().getRemark());
				sw.setYoukuEffect(getShieldWordsBo().getYoukuEffect());
				sw.setOthersEffect(getShieldWordsBo().getOthersEffect());
				sw.setUpdateTime(new Date());
				ShieldWordsPeer.doUpdate(sw);
				
				ShieldWordsBo swBo = getShieldWordsBo();
				swBo.setId(sw.getId());
				swBo.setType(sw.getType());
				saveShieldWordRelation(swBo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}
	
	private boolean checkWordExist(String word, int type) throws TorqueException{
		Criteria crit = new Criteria();
		crit.add(ShieldWordsPeer.WORD, word);
		crit.add(ShieldWordsPeer.TYPE, type);
		List<ShieldWords> wordsList = ShieldWordsPeer.doSelect(crit);
		
		return wordsList != null && wordsList.size() > 0;
	}
	
	/**
	 * <p>
	 * List the Users
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() {

		try {
			
			if(getExportcsvflag() == 1) {
				return exportCsv();
			}
			/*
			 * String pageSize = getText("item.list.pageSize"); LargeSelect
			 * largeSelect =
			 * UserPeer.findUserPagination(Integer.valueOf(pageSize));
			 * log.debug("page number is: " + getPageNumber());
			 * log.debug("total page number is: " +
			 * largeSelect.getTotalPages());
			 * 
			 * if(largeSelect.getTotalPages() < getPageNumber()) { return ERROR;
			 * } List itemList = largeSelect.getPage(getPageNumber());
			 */

			PageInfo pageInfo = new PageInfo();
			pageInfo
					.setPageSize(Integer.valueOf(getText("user.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log
					.debug("UserAction getPageNumber() value is: "
							+ getPageNumber());
			
			ShieldWordService.searchShieldWords(pageInfo, getSearchParameter(), false);
			List<ShieldWords> shieldWordList = pageInfo.getResults();
			List<ShieldWordsBo> result = new ArrayList<ShieldWordsBo>();
			if(shieldWordList == null) {
				shieldWordList = new ArrayList<ShieldWords>();
			}
			for(ShieldWords sw : shieldWordList) {
				ShieldWordsBo swbo = getShieldWordBo(sw);
				result.add(swbo);
			}
			pageInfo.setResults(result);
			setPageInfo(pageInfo);

			return Constants.LIST;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.LIST;
	}
	
	
	public String exportCsv() {

		try {
			/*
			 * String pageSize = getText("item.list.pageSize"); LargeSelect
			 * largeSelect =
			 * UserPeer.findUserPagination(Integer.valueOf(pageSize));
			 * log.debug("page number is: " + getPageNumber());
			 * log.debug("total page number is: " +
			 * largeSelect.getTotalPages());
			 * 
			 * if(largeSelect.getTotalPages() < getPageNumber()) { return ERROR;
			 * } List itemList = largeSelect.getPage(getPageNumber());
			 */

			PageInfo pageInfo = new PageInfo();
			pageInfo
					.setPageSize(Integer.valueOf(getText("user.list.pageSize")));
			if(getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log
					.debug("UserAction getPageNumber() value is: "
							+ getPageNumber());
			
			ShieldWordService.searchShieldWords(pageInfo, getSearchParameter(), true);
			List<ShieldWords> shieldWordList = pageInfo.getResults();
			List<ShieldWordsBo> result = new ArrayList<ShieldWordsBo>();
			if(shieldWordList == null) {
				shieldWordList = new ArrayList<ShieldWords>();
			}
			for(ShieldWords sw : shieldWordList) {
				ShieldWordsBo swbo = getShieldWordBo(sw);
				result.add(swbo);
			}
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "filename=words.csv");  
            response.setContentLength((int) result.size() * 100); //  设置下载内容大小   
			writeCsvFile(response.getOutputStream(), result);

			response.flushBuffer();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeCsvFile(OutputStream stream, List<ShieldWordsBo> swList) {
		try {
			CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("GBK"));
			csvWriter.write("关键词");
			csvWriter.write("关键词分类");
			csvWriter.write("命中规则");
			csvWriter.write("生效频道");
			csvWriter.write("站点级别");
			csvWriter.write("生效时间-开始");
			csvWriter.write("生效时间-结束");
			csvWriter.write("更新时间");
			csvWriter.write("修改人员");
			csvWriter.endRecord();
			
			for(ShieldWordsBo sw : swList) {
				csvWriter.write(sw.getWord());
				csvWriter.write(sw.getWordCategoryStr());
				csvWriter.write(sw.getHitRoleStr());
				csvWriter.write(sw.getShieldChannelStr());
				csvWriter.write(sw.getDeterminWordInfo());
				try {
					csvWriter.write(DataFormat.formatDate(sw.getStartTime(), DataFormat.FMT_DATE_YYYY_MM_DD));
					csvWriter.write(DataFormat.formatDate(sw.getExpireTime(), DataFormat.FMT_DATE_YYYY_MM_DD));
					csvWriter.write(DataFormat.formatDate(sw.getUpdateTime(), DataFormat.FMT_DATE_YYYY_MM_DD));
				} catch (Exception e) {
					e.printStackTrace();
				}
				csvWriter.write(sw.getModifier());
				csvWriter.endRecord();
			}
			csvWriter.close();
			
			
			/*CsvReader reader = new CsvReader("c:\\temp\\test.csv", ',', Charset.forName("GBK"));
			while(reader.readRecord()) {
				System.out.println(reader.get(0));
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	public String importCsv() {
		InputStream in = null;
		String errorMsg = null;
		
		
		try {
			JFConverter converter = new JFConverter();
			Map<String, Integer> channelNameMap = getCategoryNameMap();
			in = new FileInputStream(getFile());
			CsvReader reader = new CsvReader(in, Charset.forName("GBK"));
			reader.readHeaders();
			int columnCount = reader.getHeaderCount();
			
			if(columnCount < 5) {
				errorMsg = "文件格式不正确";
			}
			
			StringBuffer notImportWordsRecords = new StringBuffer();
			while(reader.readRecord()) {
				String notImportReason = "";
				ShieldWords sw = new ShieldWords();
				ShieldWordsBo swBo = new ShieldWordsBo();
				sw.setWord(converter.traditionalized(reader.get(0)));
				if(channelNameMap.get(reader.get(1)) == null) {
					notImportReason = "缺少类型";
				}else {
					sw.setFkShieldCategoryId(channelNameMap.get(reader.get(1)));
				}
				if(HitRolesConstants.HITROLENAMEMAP.get(reader.get(2)) == null) {
					notImportReason = "缺少命中规则";
				} else {
					sw.setHitRole(HitRolesConstants.HITROLENAMEMAP.get(reader.get(2)));
				}
				
				if(!"".equals(reader.get(3))) {
					String[] channels = reader.get(3).split("[|]");
					List channelIdList = new ArrayList();
					for(String channel : channels) {
						Integer channelId = ShieldWordService.getShieldChannelNameMap().get(channel);
						if(channelId != null) {
							channelIdList.add(channelId + "");
						}
					}
					swBo.setShieldChannelIdList(channelIdList);
				}
				
				if(!"".equals(reader.get(4))) {
					
					
					String[] siteInfos = reader.get(4).split("[|]");
					System.out.println("site Info" + Arrays.toString(siteInfos));
					if(siteInfos.length != 2) {
						notImportReason = "站点规则信息不全";
					} else {
						int siteCategory = ShieldSiteConstants.SITECATEGORYVALUEMAP.get(siteInfos[0]);
						int siteLevel = ShieldSiteConstants.SITELEVELVALUEMAP.get(siteInfos[1]);
						
						swBo.setWordSiteCategory(siteCategory);
						swBo.setWordSiteLevel(siteLevel);
					}
				}
				
				if("".equals(reader.get(5))) {  //缺少开始时间
					sw.setStartTime(new Date());
				} else {
					sw.setStartTime(DataFormat.formatDate(reader.get(5)));
				}
				if("".equals(reader.get(6))) {  //缺少结束时间,设置为null
					sw.setExpireTime(null);
				} else {
					sw.setExpireTime(DataFormat.formatDate(reader.get(6)));
				}
				
				if(!notImportReason.isEmpty()) {
					notImportWordsRecords.append("关键字： " + sw.getWord() + "未导入原因： " + notImportReason + "<br />");
				} else {
					Criteria crit = new Criteria();
					crit.add(ShieldWordsPeer.WORD, sw.getWord());
					crit.add(ShieldWordsPeer.TYPE, getImportWordType());
					List<ShieldWords> wordsList = ShieldWordsPeer.doSelect(crit);
					if(wordsList != null && wordsList.size() > 0) {
						ShieldWords oldSw = wordsList.get(0);
						oldSw.setFkShieldCategoryId(sw.getFkShieldCategoryId());
						oldSw.setHitRole(sw.getHitRole());
						oldSw.setCreateTime(sw.getCreateTime());
						oldSw.setExpireTime(sw.getExpireTime());
						oldSw.setModifier(getUserName());
						oldSw.setUpdateTime(new Date());
						oldSw.setType(getImportWordType());
						ShieldWordsPeer.doUpdate(oldSw);
						sw.setId(oldSw.getId());
					} else {
						sw.setCreateTime(new Date());
						sw.setUpdateTime(new Date());
						sw.setModifier(getUserName());
						sw.setType(getImportWordType());
						sw.save();
					}
				}
				swBo.setId(sw.getId());
				System.out.println(swBo);
				saveShieldWordRelation(swBo);
			}
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print("{\"notImportWords\":\"" + notImportWordsRecords.toString() + "\"}");
			return null;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return SUCCESS;
		
	}
	
	public Map<String, Integer> getCategoryNameMap() throws TorqueException {
		Map<String, Integer> categoryNameMap = new HashMap<String, Integer>();
		List<ShieldCategory> channelList = ShieldCategoryPeer.doSelect(new Criteria());
		
		for(ShieldCategory sc : channelList) {
			categoryNameMap.put(sc.getName(), sc.getId());
		}
		
		return categoryNameMap;
	}
	
	private ShieldWordsBo getShieldWordBo(ShieldWords sw) throws Exception{
		List<Category> channelList = CategoryMap.getInstance().videoList;
		List<Category> rangeList = CategoryMap.getShieldRangeList();
		List<DeterminerWordRelation> determinerWordRelationList = DeterminerWordRelationPeer.doSelect(new Criteria().add(DeterminerWordRelationPeer.FK_WORD_ID, sw.getId()));
		List<ShieldWordRelation> shieldWordRelationList = ShieldWordRelationPeer.doSelect(new Criteria().add(ShieldWordRelationPeer.FK_WORD_ID, sw.getId()));
		List<ShieldWords> shieldRangeList = ShieldWordsPeer.doSelect(new Criteria().add(ShieldWordsPeer.WORD,sw.getWord()));
		List wordChannelList = new ArrayList();
		List wordRangeList = new ArrayList();
		StringBuilder channelBuffer = new StringBuilder();
		for(ShieldWordRelation sr : shieldWordRelationList) {
			for(Category sc : channelList) {
				if(sc.getId() == sr.getFkShieldChannelId()) {
					wordChannelList.add(sc.getId());
					if(channelBuffer.length() > 0) {
						channelBuffer.append('|');
					}
					channelBuffer.append(sc.getName());
				}
			}
		}
		for(ShieldWords sws:shieldRangeList){
			for(Category sc : rangeList) {
				if(sc.getId() == sws.getRanges()) {
					wordRangeList.add(sc.getId());
				}
			}
		}
		
		
		ShieldWordsBo shieldWordBo = new ShieldWordsBo();
		copyProperties(shieldWordBo, sw);
		shieldWordBo.setShieldChannelIdList(wordChannelList);
		shieldWordBo.setShieldRangeIdList(wordRangeList);//
		shieldWordBo.setShieldChannelStr(channelBuffer.toString());
		if(determinerWordRelationList != null && !determinerWordRelationList.isEmpty()) {
			DeterminerWordRelation dwr = determinerWordRelationList.get(0);
			shieldWordBo.setWordSiteCategory(dwr.getSiteCategory());
			shieldWordBo.setWordSiteLevel(dwr.getSiteLevel());
			
			String determinerWordInfo = getSiteCategoryMap().get(dwr.getSiteCategory()) + "|" + getSiteLevelMap().get(dwr.getSiteLevel());
			shieldWordBo.setDeterminWordInfo(determinerWordInfo);
		}
		shieldWordBo.setWordCategoryStr(ShieldCategoryPeer.retrieveByPK(sw.getFkShieldCategoryId()).getName());
		return shieldWordBo;
	}
	
	private ShieldWords getShieldWordVo(ShieldWordsBo swBo) throws Exception {
		ShieldWords sw = new ShieldWords();
		copyProperties(sw, swBo);
		return sw;
	}
	
	private void saveShieldWordRelation(ShieldWordsBo swBo) throws Exception {

		List channelList = swBo.getShieldChannelIdList();
		List rangeList = swBo.getShieldRangeIdList();
		Criteria crit = new Criteria();
		crit.add(ShieldWordRelationPeer.FK_WORD_ID, swBo.getId());
		List<ShieldWordRelation> wordRelationList = ShieldWordRelationPeer.doSelect(crit);
		
		if (channelList != null) {
			for (Object sc : channelList) {
				int fkShieldChannelId = Integer.valueOf((String) sc);

				boolean existRelation = false;
				for (Iterator<ShieldWordRelation> it = wordRelationList
						.iterator(); it.hasNext();) {
					ShieldWordRelation swr = it.next();
					if (fkShieldChannelId == swr.getFkShieldChannelId()) {
						it.remove();
						existRelation = true;
						break;
					}

				}
				if (!existRelation) {
					ShieldWordRelation swr = new ShieldWordRelation();
					swr.setFkWordId(swBo.getId());
					swr.setFkShieldChannelId(fkShieldChannelId);
					swr.save();
				}
			}
		}
		for(ShieldWordRelation s : wordRelationList) {
			ShieldWordRelationPeer.doDelete(s.getPrimaryKey());
		}
		//shield range
		Criteria wordcrit = new Criteria();
		wordcrit.add(ShieldWordsPeer.WORD,swBo.getWord());
		List<ShieldWords> shieldWordsList = ShieldWordsPeer.doSelect(wordcrit);
		if (rangeList != null) {
			for (Object sc : rangeList) {
				int fkShieldChannelId = Integer.valueOf((String) sc);

				boolean existRelation = false;
				for (Iterator<ShieldWords> it = shieldWordsList
						.iterator(); it.hasNext();) {
					ShieldWords sw = it.next();
					if (fkShieldChannelId == sw.getRanges()) {
						it.remove();
						existRelation = true;
						break;
					}
				}
				if (!existRelation) {
					ShieldWords sw = new ShieldWords();
					sw.setWord(swBo.getWord());
					sw.setExcluding(swBo.getExcluding());
					sw.setType(swBo.getType());
					sw.setYoukuEffect(swBo.getYoukuEffect());
					sw.setOthersEffect(swBo.getOthersEffect());
					sw.setHitRole(swBo.getHitRole());
					sw.setFkShieldCategoryId(swBo.getFkShieldCategoryId());
					sw.setStartTime(swBo.getStartTime());
					sw.setExpireTime(swBo.getExpireTime());
					sw.setRemark(swBo.getRemark());
					sw.setUpdateTime(swBo.getUpdateTime());
					sw.setCreateTime(swBo.getCreateTime());
					sw.setRanges(fkShieldChannelId);
					sw.save();
				}
			}
		}
		for(ShieldWords s : shieldWordsList) {
			if(s.getRanges()>0)
				ShieldWordsPeer.doDelete(s.getPrimaryKey());
		}
	
		DeterminerWordRelation dwr = null;
		Criteria rcrit = new Criteria();
		rcrit.add(DeterminerWordRelationPeer.FK_WORD_ID, swBo.getId());
		List<DeterminerWordRelation> dwrList = DeterminerWordRelationPeer.doSelect(rcrit);
		if(dwrList != null && !dwrList.isEmpty()){
			dwr = dwrList.get(0);
			
		} else {
			dwr = new DeterminerWordRelation();
		}
		System.out.println("swBo.getType()" + swBo.getType() + dwr);
		if(swBo.getWordSiteCategory() > 0 && swBo.getWordSiteLevel() > 0 ){
			
			
			dwr.setFkWordId(swBo.getId());
			dwr.setSiteCategory(swBo.getWordSiteCategory());
			dwr.setSiteLevel(swBo.getWordSiteLevel());
			dwr.save();
		} else {
			DeterminerWordRelationPeer.doDelete(dwr.getPrimaryKey());
		}
	}
	
	//根据用户权限 设置用户管理屏蔽词的作用范围
	public List<Category> getShieldRangeByAuthen(){
		List<Category> result = new ArrayList<Category>();
		List<Category> allRange = CategoryMap.getShieldRangeList();
		Map session = getSession();
		User user = (User) session.get(Constants.USER_KEY);
		Map<String, AuthPermission> permissionMap = (Map<String, AuthPermission>)session.get(Constants.PERMISSION_MAP_KEY);
	    log.debug(" *** authen map :"+permissionMap.size()+" ***");
		boolean site = false;
	    boolean wifi = false;
	    boolean refer = false;
	    List<UserPermission> permissionsList = new ArrayList<UserPermission>();
		try {
			permissionsList = UserPermissionService.findPermissionsByUserId(user.getUserId());
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug(" ***user authen map :"+permissionsList.size()+" ***");
		AuthPermission siteAuth = permissionMap.get(Constants.SHIELD_WORD_RANGE_SITE);
		log.debug(" ***site authen value :"+siteAuth+" ***");
		AuthPermission wifiAuth = permissionMap.get(Constants.SHIELD_WORD_RANGE_WIFI);
		AuthPermission referAuth = permissionMap.get(Constants.SHIELD_WORD_RANGE_REFER);
		for (UserPermission userPermission : permissionsList) {
			if (site == false && null!=siteAuth && userPermission.getPermissionId() == siteAuth.getId()) {
				site = true;
			}
			if (wifi == false && wifiAuth != null && userPermission.getPermissionId() == wifiAuth.getId()) {
				wifi = true;
			}
			if (refer == false && referAuth != null && userPermission.getPermissionId() == referAuth.getId()) {
				refer = true;
			}
		}
		log.debug(" *** has site :"+site+" wifi:"+wifi+" refer:"+refer+" ***");
		for(Category c:allRange){
			if(site && c.getId()==1)
				result.add(c);
			if(wifi && c.getId()==2)
				result.add(c);
			if(refer && c.getId()==3)
				result.add(c);
		}
		log.debug(" *** result:"+result.size()+" ***");
		return result;
	}
	
	private int wordId;
	
	private PageInfo pageInfo;
	
	private ShieldWords shieldWord;
	
	private ShieldWordsBo shieldWordsBo;
	
	private List<ShieldWordRelation> shieldWordRelationList;
	
	private List<DeterminerWordRelation> determinerWordRelationList;
	
	private List<Category> shieldChannelList;
	
	//屏蔽词作用范围
	private List<Category> shieldRangeList;
	
	private int pageNumber;
	
	
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	// -----Task property ------
    /**
     * <p>Filed to store workflow task.</p>
     * <p/>
     * <p>The Task is used to track the state of the CURD workflows. It can be 
     * set to Constants.CREATE, Constants.EDIT, Constants.DELETE as needed. </p>
     */
	private String task = null;
	
	/**
	 * <p>Provide workflow task</p>
	 * 
	 * @return Return the task.
	 */
	public String getTask() {
		return task;
	}
	
	/**
	 * <p>Store new workflow task</p>
	 * 
	 * @param value The task to set
	 */
	public void setTask(String task) {
		this.task = task;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public ShieldWords getShieldWord() {
		return shieldWord;
	}

	public void setShieldWord(ShieldWords shieldWord) {
		this.shieldWord = shieldWord;
	}

	public ShieldWordsBo getShieldWordsBo() {
		return shieldWordsBo;
	}

	public void setShieldWordsBo(ShieldWordsBo shieldWordsBo) {
		this.shieldWordsBo = shieldWordsBo;
	}

	public List<ShieldWordRelation> getShieldWordRelationList() {
		return shieldWordRelationList;
	}

	public void setShieldWordRelationList(
			List<ShieldWordRelation> shieldWordRelationList) {
		this.shieldWordRelationList = shieldWordRelationList;
	}

	public List<DeterminerWordRelation> getDeterminerWordRelationList() {
		return determinerWordRelationList;
	}

	public void setDeterminerWordRelationList(
			List<DeterminerWordRelation> determinerWordRelationList) {
		this.determinerWordRelationList = determinerWordRelationList;
	}

	public List<Category> getShieldChannelList() {
		return shieldChannelList;
	}

	public void setShieldChannelList(List<Category> shieldChannelList) {
		this.shieldChannelList = shieldChannelList;
	}
	
    private int[] batchdeleteids;
	
	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}
	
	public Map<Integer, String> getShieldChannelMap() {
		return ShieldWordService.getShieldChannelMap(true);
	}
	
	public Map<Integer, String> getShieldCategoryMap() {
		return ShieldWordService.getShieldCategoryMap(true);
	}
	
	public Map<Integer, String> getRadioShieldChannelMap() {
		return ShieldWordService.getShieldChannelMap(false);
	}
	
	public Map<Integer, String> getRadioShieldCategoryMap() {
		return ShieldWordService.getShieldCategoryMap(false);
	}
	
	public Map<Integer, String> getSiteLevelMap() {
		return ShieldSiteConstants.SITELEVELMAP;
	}
	
	public Map<Integer, String> getHitRoleMap() {
		return HitRolesConstants.HITROLEMAP;
	}
	
	public Map<Integer, String> getRadioSiteLevelMap() {
		return ShieldSiteConstants.RADIOSITELEVELMAP;
	}
	
	public Map<Integer, String> getRadioHitRoleMap() {
		return HitRolesConstants.RADIOHITROLEMAP;
	}
	
	public Map<Integer, String> getSiteCategoryMap() {
		return ShieldSiteConstants.SITECATEGORYMAP;
	}
	
	public Map<Integer, String> getRadioSiteCategoryMap() {
		return ShieldSiteConstants.RADIOSITECATEGORYMAP;
	}
	
	public Map<Integer, String> getOrderByMap() {
		return ShieldWordConstants.ORDERBYMAP;
	}
	
	public Map<Integer, String> getTrendMap() {
		return ShieldWordConstants.TRENDMAP;
	}
	
	private SearchParameter searchParameter;

	public SearchParameter getSearchParameter() {
		return searchParameter;
	}

	public void setSearchParameter(SearchParameter searchParameter) {
		this.searchParameter = searchParameter;
	}

	private int exportcsvflag;



	public int getExportcsvflag() {
		return exportcsvflag;
	}

	public void setExportcsvflag(int exportcsvflag) {
		this.exportcsvflag = exportcsvflag;
	}
	
	private File file;

	private String contentType;

	private String fileName;



	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	private int importWordType;



	public int getImportWordType() {
		return importWordType;
	}

	public void setImportWordType(int importWordType) {
		this.importWordType = importWordType;
	}

	public List<Category> getShieldRangeList() {
		return shieldRangeList;
	}

	public void setShieldRangeList(List<Category> shieldRangeList) {
		this.shieldRangeList = shieldRangeList;
	}
	
	
	
}
