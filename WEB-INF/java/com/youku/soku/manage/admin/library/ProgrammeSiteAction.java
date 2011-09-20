package com.youku.soku.manage.admin.library;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.ProgrammeSiteBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.datamaintain.FixProgrammeOrderStage;
import com.youku.soku.manage.datamaintain.RemoveCntvErrorData;
import com.youku.soku.manage.datamaintain.SokuOldDataImport;
import com.youku.soku.manage.datamaintain.SokuOldDataImport.ImportDataStruct;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ProgrammeSiteService;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.service.UserOperationService;
import com.youku.soku.manage.timer.ProgrammeSiteHdSynTimer;
import com.youku.soku.manage.util.ImageUtil;

public class ProgrammeSiteAction extends BaseActionSupport {

	private Logger logger = Logger.getLogger(this.getClass());

	public String list() {
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageSize(Integer.valueOf(getText("programmeSite.list.pageSize")));

		if (getPageNumber() == 0) {
			setPageNumber(1);
		}
		pageInfo.setCurrentPageNumber(getPageNumber());

		try {
			ProgrammeSiteService.findProgrammeSitePagination(pageInfo, getProgrammeId());

			List<ProgrammeSite> resultList = pageInfo.getResults();
			List<ProgrammeSiteBo> psBoList = new ArrayList<ProgrammeSiteBo>();
			for (ProgrammeSite ps : resultList) {
				if(ps.getSourceSite() != Constants.INTEGRATED_SITE_ID) {
					psBoList.add(getProgrammeSiteBo(ps));
				}
			}

			pageInfo.setResults(psBoList);
			setPageInfo(pageInfo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Constants.LIST;
	}

	/**
	 * <p>
	 * Create category action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String input() throws Exception {

		try {
			if (getProgrammeSiteId() == -1) {
				setTask(Constants.CREATE);
				Programme p = ProgrammePeer.retrieveByPK(getProgrammeId());
				ProgrammeSiteBo psBo = new ProgrammeSiteBo();
				psBo.setName(p.getName());
				psBo.setFkProgrammeId(p.getId());
				setProgrammeSite(psBo);
				return INPUT;
			} else {
				ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(getProgrammeSiteId());
				setProgrammeSite(getProgrammeSiteBo(ps));
				setProgrammeId(ps.getFkProgrammeId());
				setTask(Constants.EDIT);
				return INPUT;
			}
		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return INPUT;

	}

	/**
	 * <p>
	 * Insert or update an Programme Site
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
	 * Insert or update an Programme Site
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String delete() throws Exception {
		ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(getProgrammeSiteId());
		logger.info("Operator: " + getUserName());
		logger.info("Delete Programme Site at " + formatLogDate(new Date()));
		logger.info(ps);
		ProgrammeSitePeer.doDelete(ps.getPrimaryKey());
		UserOperationService.logChangeProgrammeSite(Constants.USER_OPERATION_DELETE, getUserName(), ps, ps.toString());
		return Constants.LIST;
	}

	/**
	 * <p>
	 * Insert or update an teleplaySiteVersion object to the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String execute() throws Exception {

		try {

			boolean creating = Constants.CREATE.equals(getTask());

			if (creating) {
				int siteId = getProgrammeSite().getSourceSite();
				List<ProgrammeSite> teleSiteVersionList = ProgrammeSiteService.findProgrammeSite(getProgrammeId(), siteId);

				if (teleSiteVersionList != null && teleSiteVersionList.size() > 0) {
					ProgrammeSite tsv = teleSiteVersionList.get(0);
					setProgrammeSiteId(tsv.getId());
					return "episode";
				}

				getProgrammeSite().setCreateTime(new Date());
				getProgrammeSite().setUpdateTime(new Date());
				ProgrammeSite h = getProgrammeSiteVo(getProgrammeSite());

				logger.info("Operator: " + getUserName());
				logger.info("Save Programme Site at " + formatLogDate(new Date()));
				logger.info(h);
				h.save();
				UserOperationService.logChangeProgrammeSite(Constants.USER_OPERATION_ADD, getUserName(), h, "");
			} else {
				ProgrammeSite oldPs = ProgrammeSitePeer.retrieveByPK(getProgrammeSiteId());
				logger.info("Operator: " + getUserName());
				logger.info("Update Programme Site at " + formatLogDate(new Date()));
				String updateDetail = "Object before update" + oldPs;

				ProgrammeSiteBo psbo = getProgrammeSite();
				oldPs.setBlocked(psbo.getBlocked());
				oldPs.setCompleted(psbo.getCompleted());
				oldPs.setEpisodeCollected(psbo.getEpisodeCollected());
				oldPs.setFirstLogo(psbo.getFirstLogo());
				oldPs.setOrderId(psbo.getOrderId());
				oldPs.setSourceSite(psbo.getSourceSite());
				oldPs.setUpdateTime(new Date());
				updateDetail += "Object after update" + oldPs;
				logger.info(updateDetail);
				ProgrammeSitePeer.doUpdate(oldPs);
				UserOperationService.logChangeProgrammeSite(Constants.USER_OPERATION_UPDATE, getUserName(), oldPs, updateDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbOperationException(getText("数据库操作错误，该站点可能已经存在"));
		}
		return SUCCESS;
	}

	private ProgrammeSiteBo getProgrammeSiteBo(ProgrammeSite ps) throws Exception {
		
		
		ProgrammeSiteBo psBo = new ProgrammeSiteBo();
		Programme p = ProgrammePeer.retrieveByPK(ps.getFkProgrammeId());

		copyProperties(psBo, ps);
		psBo.setSiteName(SiteService.getSiteName(ps.getSourceSite()));
		psBo.setFirstLogo(ImageUtil.getDisplayUrl(ps.getFirstLogo()));
		psBo.setName(p.getName());
		return psBo;
	}

	private ProgrammeSite getProgrammeSiteVo(ProgrammeSiteBo psBo) throws Exception {
		ProgrammeSite ps = new ProgrammeSite();
		copyProperties(ps, psBo);

		return ps;
	}
	
	public String fixProgrammCompleteFlag() throws Exception {

		List<Programme> pList = ProgrammePeer.doSelect(new Criteria());
		for(Programme p : pList) {
			
			Criteria pCrit = new Criteria();
			pCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(pCrit);
			if(psList != null) {
				for(ProgrammeSite ps : psList) {
					ProgrammeSiteService.updateEpisodeCollected(ps.getId());
					logger.info("fixProgrammCompleteFlag  .......  ");
				}
			}
		}
	
		return Constants.LIST;
	}

	/*public String importEpisodeFromOldDb() throws Exception {
		String status = "success";

		try {
			String importingUrl = getImportingUrl();

			int versionId = Integer.valueOf(getVer());
			int cateId = Integer.valueOf(getCa());

			logger.info("versionId" + versionId);

			ProgrammeSiteService.importEpisodeFromOldDb(getProgrammeId(), versionId, cateId, getUserName());

			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print("{\"status\":\"" + status + "\"}");
		} catch (NumberFormatException e) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print("alert('输入的URL有误')");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}*/

	public String deleteOtherSiteContent() {
		try {
			Criteria crit = new Criteria();
			//crit.add(ProgrammePeer.CATE, Constants.VARIETY_CATE_ID);
			List<Programme> pList = ProgrammePeer.doSelect(crit);
			for(Programme p : pList) {
				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
				if (psList != null) {
					for(ProgrammeSite ps : psList) {
						if(ps.getSourceSite() != Constants.YOUKU_SITE_ID) {
							Criteria peCrit = new Criteria();
							peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
							List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
							if(peList != null) {
								for(ProgrammeEpisode pe : peList) {
									logger.info("delete url: " + pe.getUrl());
									ProgrammeEpisodePeer.doDelete(pe.getPrimaryKey());
								}
							}
							logger.info("delete programme site: " + ps.getId() + "source site: " + ps.getSourceSite());
							ProgrammeSitePeer.doDelete(ps.getPrimaryKey());
						}
						
					}
				}
			}
		} catch (TorqueException e) {
			logger.error(e.getMessage(), e);
		}
		return SUCCESS;
	}

/*	public String importAllEpisodeFromOldDb() throws Exception {
		String status = "success";
		Connection conn = null;
		logger.info("importAllEpisodeFromOldDb");
		try {
			List<Programme> pList = ProgrammePeer.doSelect(new Criteria());

			conn = Torque.getConnection("old_soku_library");

			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
			File file = new File("site_version_import" + format.format(new Date()));
			List<SokuOldDataImport.ImportDataStruct> dataList = new ArrayList<SokuOldDataImport.ImportDataStruct>();
			for (Programme p : pList) {
				SokuOldDataImport.ImportDataStruct data = new SokuOldDataImport.ImportDataStruct();
				int versionId = -1;
				logger.info(p);
				if (p.getCate() == Constants.TELEPLAY_CATE_ID) {
					Criteria tCrit = new Criteria();
					//tCrit.add(TeleplayVersionPeer.CONTENT_ID, p.getContentId());
					tCrit.add(TeleplayVersionPeer.CONTENT_ID, p.getContentId());
					List<TeleplayVersion> tList = TeleplayVersionPeer.doSelect(tCrit, conn);
					if (tList != null && !tList.isEmpty()) {
						TeleplayVersion tv = tList.get(0);
						versionId = tv.getId();
						Names names = NamesPeer.retrieveByPK(tv.getFkNamesId(), conn);
						data.setSokuName(names.getName());
						if(!StringUtils.isBlank(tv.getName())) {
							data.setSokuVersionName(tv.getName());
						}
					} else { // 如果contentId找不到，尝试按名字查找

					}
				}

				if (p.getCate() == Constants.ANIME_CATE_ID) {
					Criteria aCrit = new Criteria();
					aCrit.add(AnimeVersionPeer.CONTENT_ID, p.getContentId());
					List<AnimeVersion> aList = AnimeVersionPeer.doSelect(aCrit, conn);
					if (aList != null && !aList.isEmpty()) {
						AnimeVersion av = aList.get(0);
						versionId = av.getId();
						Names names = NamesPeer.retrieveByPK(av.getFkNamesId(), conn);
						data.setSokuName(names.getName());
						if(!StringUtils.isBlank(av.getName())) {
							data.setSokuVersionName(av.getName());
						}
					}
				}
				
				if(p.getCate() == Constants.MOVIE_CATE_ID) {
					Criteria mCrit = new Criteria();
					mCrit.add(MoviePeer.CONTENT_ID, p.getContentId());
					List<Movie> mList = MoviePeer.doSelect(mCrit, conn);
					
					if (mList != null && !mList.isEmpty()) {
						Movie m = mList.get(0);
						versionId = m.getId();
						Names names = NamesPeer.retrieveByPK(m.getFkNamesId(), conn);
						data.setSokuName(names.getName());
						if(!StringUtils.isBlank(m.getName())) {
							data.setSokuVersionName(m.getName());
						}
					}
				}

				if (p.getCate() == Constants.VARIETY_CATE_ID) {
					Criteria vCrit = new Criteria();
					vCrit.add(VarietyPeer.CONTENT_ID, p.getContentId());
					List<Variety> vList = VarietyPeer.doSelect(vCrit, conn);
					if (vList != null && !vList.isEmpty()) {
						Variety v = vList.get(0);
						versionId = v.getId();
						Names names = NamesPeer.retrieveByPK(v.getFkNamesId(), conn);
						data.setSokuName(names.getName());
						if(!StringUtils.isBlank(v.getName())) {
							data.setSokuVersionName(v.getName());
						}
					}
				}

				//boolean imported = ProgrammeSiteService.importEpisodeFromOldDb(p.getId(), versionId, p.getCate(), getUserName());
				
				data.setCateId(p.getCate());
				data.setContentId(p.getContentId());
				data.setSokuId(versionId);
				data.setYoukuName(p.getName());
				
				dataList.add(data);
			}
			SokuOldDataImport importer = new SokuOldDataImport();
			importer.importEpisodeFromOldDb(dataList, false, 6, "content_id");
			

			return SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}
	
	public String importJujiFromOldDb() throws Exception {
		String status = "success";
		Connection conn = null;
		logger.info("importAllEpisodeFromOldDb");
		try {
			

			conn = Torque.getConnection("old_soku_library");

			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
			List<SokuOldDataImport.ImportDataStruct> dataList = new ArrayList<SokuOldDataImport.ImportDataStruct>();
			

			Criteria tCrit = new Criteria();
			//tCrit.add(TeleplayVersionPeer.CONTENT_ID, p.getContentId());
			List<TeleplayVersion> tList = TeleplayVersionPeer.doSelect(tCrit, conn);
			if (tList != null && !tList.isEmpty()) {
				for(TeleplayVersion tv : tList) {
					Names names = NamesPeer.retrieveByPK(tv.getFkNamesId(), conn);
					
					String sokuName = names.getName();
					if(!StringUtils.isBlank(tv.getName())) {
						sokuName += " " + tv.getName();
					}
					
					addImportData(Constants.TELEPLAY_CATE_ID, sokuName, tv.getId(), dataList);
				}
			} 
			
			Criteria aCrit = new Criteria();
			List<AnimeVersion> aList = AnimeVersionPeer.doSelect(aCrit, conn);
			if (aList != null && !aList.isEmpty()) {
				for(AnimeVersion av : aList) {
					Names names = NamesPeer.retrieveByPK(av.getFkNamesId(), conn);
					
					String sokuName = names.getName();
					if(!StringUtils.isBlank(av.getName())) {
						sokuName += " " + av.getName();
					}
					
					addImportData(Constants.ANIME_CATE_ID, sokuName, av.getId(), dataList);
				}
			}
		

			Criteria mCrit = new Criteria();
			List<Movie> mList = MoviePeer.doSelect(mCrit, conn);
			
			if (mList != null && !mList.isEmpty()) {
				for(Movie m : mList) {
					Names names = NamesPeer.retrieveByPK(m.getFkNamesId(), conn);
					
					String sokuName = names.getName();
					if(!StringUtils.isBlank(m.getName())) {
						sokuName += " " + m.getName();
					}
					
					addImportData(Constants.MOVIE_CATE_ID, sokuName, m.getId(), dataList);
				}
				
			}
		

			Criteria vCrit = new Criteria();
			List<Variety> vList = VarietyPeer.doSelect(vCrit, conn);
			if (vList != null && !vList.isEmpty()) {
				for(Variety v : vList) {
					Names names = NamesPeer.retrieveByPK(v.getFkNamesId(), conn);
					String sokuName = names.getName();
					if(!StringUtils.isBlank(v.getName())) {
						sokuName += " " + v.getName();
					}
					
					addImportData(Constants.VARIETY_CATE_ID, sokuName, v.getId(), dataList);
				}
			}
			
			SokuOldDataImport importer = new SokuOldDataImport();
			importer.importEpisodeFromOldDb(dataList, false, 1, "same_name");
			

			return SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}
	*/
	private void addImportData(int cate, String sokuName, int sokuId, List<SokuOldDataImport.ImportDataStruct> dataList) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.NAME, sokuName);
		crit.add(ProgrammePeer.CATE, cate);
		crit.add(ProgrammePeer.STATE, "normal");
		SokuOldDataImport.ImportDataStruct data = new SokuOldDataImport.ImportDataStruct();
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		if(pList != null && !pList.isEmpty()) {
			Programme p = pList.get(0);
			
			data.setSokuName(sokuName);
			data.setCateId(p.getCate());
			data.setContentId(p.getContentId());
			data.setSokuId(sokuId);
			data.setYoukuName(p.getName());
			dataList.add(data);
		}
	}

/*	public String listSameContentIdDifferentName() throws Exception {
		String status = "success";
		Connection conn = null;

		try {
			File sameNameFile = new File("/opt/soku_data_import_log/same_name.log");
			File diffNameFile = new File("/opt/soku_data_import_log/diff_name.log");

			BufferedWriter sameNameWriter = new BufferedWriter(new FileWriter(sameNameFile));
			BufferedWriter diffNameWriter = new BufferedWriter(new FileWriter(diffNameFile));

			List<Programme> pList = ProgrammePeer.doSelect(new Criteria());
			conn = Torque.getConnection("old_soku_library");
			int versionId = -1;

			int sameNameSize = 0;
			int diffNameSize = 0;
			for (Programme p : pList) {

				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
				if (psList == null || psList.size() == 1) {
					continue;
				}

				if (p.getCate() == Constants.TELEPLAY_CATE_ID) {
					Criteria tCrit = new Criteria();
					tCrit.add(TeleplayVersionPeer.CONTENT_ID, p.getContentId());
					List<TeleplayVersion> tList = TeleplayVersionPeer.doSelect(tCrit, conn);
					if (tList != null && !tList.isEmpty()) {
						TeleplayVersion tv = tList.get(0);
						Names name = NamesPeer.retrieveByPK(tv.getFkNamesId(), conn);
						String teleplayName = name.getName();
						if (!StringUtils.isBlank(tv.getName())) {
							teleplayName = teleplayName + " " + tv.getName();
						}

						if (teleplayName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + tv.getId() + "    " + teleplayName + "\n");
							diffNameSize++;
						}
					}
				}

				if (p.getCate() == Constants.ANIME_CATE_ID) {
					Criteria aCrit = new Criteria();
					aCrit.add(AnimeVersionPeer.CONTENT_ID, p.getContentId());
					List<AnimeVersion> aList = AnimeVersionPeer.doSelect(aCrit, conn);
					if (aList != null && !aList.isEmpty()) {
						AnimeVersion av = aList.get(0);
						Names name = NamesPeer.retrieveByPK(av.getFkNamesId(), conn);
						String animeName = name.getName();
						if (!StringUtils.isBlank(av.getName())) {
							animeName = animeName + " " + av.getName();
						}

						if (animeName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + av.getId() + "    " + animeName + "\n");
							diffNameSize++;
						}
					}
				}

				if (p.getCate() == Constants.VARIETY_CATE_ID) {
					Criteria vCrit = new Criteria();
					vCrit.add(VarietyPeer.CONTENT_ID, p.getContentId());
					List<Variety> vList = VarietyPeer.doSelect(vCrit, conn);
					if (vList != null && !vList.isEmpty()) {
						Variety v = vList.get(0);
						Names name = NamesPeer.retrieveByPK(v.getFkNamesId(), conn);
						String varietyName = name.getName();
						if (!StringUtils.isBlank(v.getName())) {
							varietyName = varietyName + " " + v.getName();
						}

						if (varietyName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + v.getId() + "    " + varietyName + "\n");
							diffNameSize++;
						}

					}
				}

			}

			sameNameWriter.flush();
			sameNameWriter.close();
			diffNameWriter.flush();
			diffNameWriter.close();

			return SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}*/

	/*public String listSameContentIdDifferentNameByLogFile() throws Exception {
		String status = "success";
		Connection conn = null;

		try {
			File sameNameFile = new File("/opt/soku_data_import_log/same_name_by_log_file.log");
			File diffNameFile = new File("/opt/soku_data_import_log/diff_name_by_log_file.log");
			BufferedWriter sameNameWriter = new BufferedWriter(new FileWriter(sameNameFile));
			BufferedWriter diffNameWriter = new BufferedWriter(new FileWriter(diffNameFile));

			File logDirectory = new File("/opt/resin");
			File[] logFiles = logDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("site_version_import");
				}
			});

			Set<String> programmeNameSet = new HashSet<String>();
			for (File logFile : logFiles) {
				BufferedReader br = new BufferedReader(new FileReader(logFile));
				String name = null;
				while ((name = br.readLine()) != null) {
					if (name != null) {
						try {
							programmeNameSet.add(name.substring(0, name.length() - 2));
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				br.close();
			}

			int sameNameSize = 0;
			int diffNameSize = 0;

			conn = Torque.getConnection("old_soku_library");
			int versionId = -1;

			for (String pname : programmeNameSet) {
				Criteria pCrit = new Criteria();
				pCrit.add(ProgrammePeer.NAME, pname);
				List<Programme> pList = ProgrammePeer.doSelect(pCrit);

				if (pList == null || pList.isEmpty()) {
					logger.info(pname);
					continue;
				}

				Programme p = pList.get(0);

				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);

				if (p.getCate() == Constants.TELEPLAY_CATE_ID) {
					Criteria tCrit = new Criteria();
					tCrit.add(TeleplayVersionPeer.CONTENT_ID, p.getContentId());
					List<TeleplayVersion> tList = TeleplayVersionPeer.doSelect(tCrit, conn);
					if (tList != null && !tList.isEmpty()) {
						TeleplayVersion tv = tList.get(0);
						Names name = NamesPeer.retrieveByPK(tv.getFkNamesId(), conn);
						String teleplayName = name.getName();
						if (!StringUtils.isBlank(tv.getName())) {
							teleplayName = teleplayName + " " + tv.getName();
						}

						if (teleplayName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + tv.getId() + "    " + teleplayName + "\n");
							diffNameSize++;
						}
					}
				}

				if (p.getCate() == Constants.ANIME_CATE_ID) {
					Criteria aCrit = new Criteria();
					aCrit.add(AnimeVersionPeer.CONTENT_ID, p.getContentId());
					List<AnimeVersion> aList = AnimeVersionPeer.doSelect(aCrit, conn);
					if (aList != null && !aList.isEmpty()) {
						AnimeVersion av = aList.get(0);
						Names name = NamesPeer.retrieveByPK(av.getFkNamesId(), conn);
						String animeName = name.getName();
						if (!StringUtils.isBlank(av.getName())) {
							animeName = animeName + " " + av.getName();
						}

						if (animeName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + av.getId() + "    " + animeName + "\n");
							diffNameSize++;
						}
					}
				}

				if (p.getCate() == Constants.VARIETY_CATE_ID) {
					Criteria vCrit = new Criteria();
					vCrit.add(VarietyPeer.CONTENT_ID, p.getContentId());
					List<Variety> vList = VarietyPeer.doSelect(vCrit, conn);
					if (vList != null && !vList.isEmpty()) {
						Variety v = vList.get(0);
						Names name = NamesPeer.retrieveByPK(v.getFkNamesId(), conn);
						String varietyName = name.getName();
						if (!StringUtils.isBlank(v.getName())) {
							varietyName = varietyName + " " + v.getName();
						}

						if (varietyName.equals(p.getName())) {
							sameNameWriter.write(p.getName() + "\n");
							sameNameSize++;
						} else {
							diffNameWriter.write(p.getId() + "    " + p.getName() + "      " + v.getId() + "    " + varietyName + "\n");
							diffNameSize++;
						}

					}
				}

			}

			sameNameWriter.flush();
			sameNameWriter.close();
			diffNameWriter.flush();
			diffNameWriter.close();
			return SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}

	// 统计有站外播放链接的电视剧
	public String otherSiteEpisodeStatistics() {
		Connection conn = null;
		try {
			conn = Torque.getConnection("old_soku_library");
			Criteria tCrit = new Criteria();
			tCrit.add(TeleplayVersionPeer.CONTENT_ID, 0, Criteria.GREATER_THAN);
			List<TeleplayVersion> tList = TeleplayVersionPeer.doSelect(tCrit, conn);
			File file = new File("other_site_episode");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for (TeleplayVersion tv : tList) {
				Criteria tvCrit = new Criteria();
				tvCrit.add(TeleplaySiteVersionPeer.FK_TELEPLAY_VERSION_ID, tv.getId());
				tvCrit.add(TeleplaySiteVersionPeer.SOURCE_SITE, 14, Criteria.NOT_EQUAL);
				tvCrit.add(TeleplaySiteVersionPeer.EPISODE_COLLECTED, 0, Criteria.GREATER_THAN);
				List<TeleplaySiteVersion> tsvList = TeleplaySiteVersionPeer.doSelect(tvCrit, conn);

				boolean isIntegrate = false;
				int siteCount = 0;

				for (TeleplaySiteVersion tsv : tsvList) {
					siteCount++;
					if (tsv.getSourceSite() == Constants.INTEGRATED_SITE_ID) {
						isIntegrate = true;
					}
				}

				if (siteCount > 0) {
					Names names = NamesPeer.retrieveByPK(tv.getFkNamesId(), conn);
					bw.write(names.getName() + tv.getName() + "    content_id: " + tv.getContentId());
					if (siteCount == 1 && isIntegrate) {
						bw.write("    (只有综合)");
					}
					bw.write("\n");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}

		return null;
	}*/
	
	public String importingSokuData() {
		try {
			SokuOldDataImport importer = new SokuOldDataImport();
			importer.importEpisodeFromOldDb(importer.getDataFromPlainText(), false, 5, "");
			importer.importEpisodeFromOldDb(importer.getDataFromExcel("电视剧", 1), false, 1, "");
			importer.importEpisodeFromOldDb(importer.getDataFromExcel("电影", 2), false, 2, "");
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String animeDuplicateName() {  //复制重名动漫的播放链接，并提供contentId供删除
		try {
			SokuOldDataImport importer = new SokuOldDataImport();
			List<ImportDataStruct> animeDataList = importer.getDataFromPlainText();
			
			importer.animeDuplicateNameProcess(animeDataList);
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String importRemoveTvAnime() {  //导入去掉tv版动漫的播放链接
		try {
			SokuOldDataImport importer = new SokuOldDataImport();
			List<ImportDataStruct> animeDataList = importer.getDataFromPlainTextRemoveTv();
			
			importer.importEpisodeFromOldDb(animeDataList, false, 5, "");
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String deleteCntrUrl() {  //删除spider错误数据
		try {
			RemoveCntvErrorData r = new RemoveCntvErrorData();
			r.delteteCntvUrl();
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	public String fixTeleplayAnimeOrderStage() {  //将电视剧，动漫的order_stage
		try {
			FixProgrammeOrderStage r = new FixProgrammeOrderStage();
			r.fix();
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}
	
	/*public String contentIdImportFixVariety() {  //根据contentId导入的综艺错误修正，暂时没有用了。站外综艺不再导入
		try {
			

			String status = "success";
			Connection conn = null;
			logger.info("importAllEpisodeFromOldDb");
			
			List<SokuOldDataImport.ImportDataStruct> dataList = new ArrayList<SokuOldDataImport.ImportDataStruct>();
			try {
				Criteria crit = new Criteria();
				crit.add(ProgrammePeer.CATE, Constants.VARIETY_CATE_ID);
				List<Programme> pList = ProgrammePeer.doSelect(crit);

				conn = Torque.getConnection("old_soku_library");

				SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
				for (Programme p : pList) {
					int versionId = -1;
					logger.info(p);
					
					SokuOldDataImport.ImportDataStruct data = new SokuOldDataImport.ImportDataStruct();
					data.setCateId(Constants.VARIETY_CATE_ID);
					data.setContentId(p.getContentId());
					
					if (p.getCate() == Constants.VARIETY_CATE_ID) {
						Criteria vCrit = new Criteria();
						vCrit.add(VarietyPeer.CONTENT_ID, p.getContentId());
						List<Variety> vList = VarietyPeer.doSelect(vCrit, conn);
						if (vList != null && !vList.isEmpty()) {
							Variety v = vList.get(0);
							data.setSokuId(v.getId());
							Names names = NamesPeer.retrieveByPK(v.getFkNamesId(), conn);
							data.setSokuName(names.getName());
							data.setSokuVersionName(v.getName());
						}
					}
					
					dataList.add(data);
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				JdbcUtil.close(conn);
			}
		
			SokuOldDataImport importer = new SokuOldDataImport();
			//importer.importEpisodeFromOldDb(importer.getDataFromPlainText(), 5);
			//importer.importEpisodeFromOldDb(importer.getDataFromExcel("电视剧", 1), 1);
			//importer.importEpisodeFromOldDb(importer.getDataFromExcel("电影", 2), 2);
			importer.importEpisodeFromOldDb(dataList, true, 3, "content_id");
			
		}  catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
		return SUCCESS;
	}*/

	public String synHd(){
		new ProgrammeSiteHdSynTimer().start();
		return "syn";
	}
	
	
	private String importingUrl;

	private int programmeId;

	private int programmeSiteId;

	private int pageNumber;

	private PageInfo pageInfo;

	private ProgrammeSiteBo programmeSiteBo;

	private String ver;

	private String ca;

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}

	public int getProgrammeSiteId() {
		return programmeSiteId;
	}

	public void setProgrammeSiteId(int programmeSiteId) {
		this.programmeSiteId = programmeSiteId;
	}

	public ProgrammeSiteBo getProgrammeSite() {
		return programmeSiteBo;
	}

	public void setProgrammeSite(ProgrammeSiteBo programmSiteBo) {
		this.programmeSiteBo = programmSiteBo;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getImportingUrl() {
		return importingUrl;
	}

	public void setImportingUrl(String importingUrl) {
		this.importingUrl = importingUrl;
	}

	public Map<Integer, String> getSitesMap() {
		return SiteService.getSitesMap();
	}

}
