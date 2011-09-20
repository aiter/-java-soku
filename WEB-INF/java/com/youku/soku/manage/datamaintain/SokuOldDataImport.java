package com.youku.soku.manage.datamaintain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.Utils;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.jsp.TorqueManage;
import com.youku.soku.manage.service.ProgrammeEpisodeService;
import com.youku.soku.manage.service.ProgrammeSiteService;

public class SokuOldDataImport {
	/**
	 * soku有的版权没有的数据，需要将原来维护的链接导入到新库
	 */
	
	private Logger log = Logger.getLogger(this.getClass());
	
	public List<ImportDataStruct> getDataFromExcel(String sheetName, int cateId) throws Exception {
		Workbook workbook = Workbook.getWorkbook(new File("/opt/soku_data_import_log/sokudata.xls"));
		Sheet[] sheets = workbook.getSheets();
		
		if(sheets != null) {
			for(Sheet sheet : sheets) {
				if(sheetName.equals(sheet.getName())) {
					int rowNumber = sheet.getRows();
					int columnNumber = sheet.getColumns();
					
					List<ImportDataStruct> result = new ArrayList<ImportDataStruct>();
					for(int i = 0; i < rowNumber; i++) {
						Cell[] rows = sheet.getRow(i);
						if("是".equals(rows[0].getContents())) {
							ImportDataStruct data = new ImportDataStruct();
							try {
								data.setContentId(Integer.valueOf(rows[1].getContents()));
								data.setYoukuName(rows[2].getContents());
								data.setSokuId(Integer.valueOf(rows[3].getContents()));
								data.setSokuName(rows[4].getContents());
								if(!"NULL".equals(rows[5].getContents())) {
									data.setSokuVersionName(rows[5].getContents());
								}
								data.setCateId(cateId);
								result.add(data);
							} catch (Exception e) {
								log.error("exception happend rows[1]:" + rows[1].getContents());
								log.error(e.getMessage(), e);
							}
						}
					}
					
					return result;
				}
				
			}
		}
		
		return null;
	}
	
	public List<ImportDataStruct> getDataFromPlainText() throws Exception {
		File dataFile = new File("/opt/soku_data_import_log/cartoon_import.log");
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		
		String line;
		List<ImportDataStruct> result = new ArrayList<ImportDataStruct>();
		while((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			if(st.hasMoreElements()) {
				
				ImportDataStruct data = new ImportDataStruct();
				try {
					data.setShowId(st.nextToken());
					data.setContentId(Integer.valueOf(st.nextToken()));
					data.setSokuId(Integer.valueOf(st.nextToken()));
					data.setYoukuName(st.nextToken());
					data.setCateId(5);
				} catch (Exception e) {
					log.error("exception happend data.getContentId():" + data.getContentId());
					log.error(e.getMessage(), e);
				}
				result.add(data);
			}
			
		}
		return result;
	}
	
	public List<ImportDataStruct> getDataFromPlainTextRemoveTv() throws Exception {
		// 导入去除TV版的数据
		File dataFile = new File("/opt/soku_data_import_log/anime_0504.txt");
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		
		String line;
		List<ImportDataStruct> result = new ArrayList<ImportDataStruct>();
		while((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			if(st.hasMoreElements()) {
				
				ImportDataStruct data = new ImportDataStruct();
				try {
					data.setShowId(st.nextToken());
					data.setContentId(Integer.valueOf(st.nextToken()));
					data.setSokuId(Integer.valueOf(st.nextToken()));
					data.setYoukuName(st.nextToken());
					data.setCateId(5);
				} catch (Exception e) {
					log.error("exception happend data.getContentId():" + data.getContentId());
					log.error(e.getMessage(), e);
				}
				result.add(data);
			}
			
		}
		return result;
	}
	
	public void logImporting(List<ImportDataStruct> dataList, int cateId, String logFile) throws Exception {
		File successImportData = new File("/opt/soku_data_import_log/success_import_data_cate" + cateId + logFile+  ".log");
		File failImportData = new File("/opt/soku_data_import_log/fail_import_data"+ cateId + logFile +  ".log");
		BufferedWriter successWriter = new BufferedWriter(new FileWriter(successImportData));
		BufferedWriter failDataWriter = new BufferedWriter(new FileWriter(failImportData));

		successWriter.write("=============================================" + getCateName(cateId) + "========================================\n");
		successWriter.write("		*******************\n");
		failDataWriter.write("=============================================" + getCateName(cateId) + "========================================\n");
		failDataWriter.write("		*******************\n");
		for(ImportDataStruct data : dataList) {
			if(data.importFlag) {
				successWriter.write(data.getContentId() + ",	" + data.getYoukuName() + ",   " + data.getSokuId() + ",	" + data.getSokuName()
						+",		" + (data.getSokuVersionName() == null ? "" : data.getSokuVersionName())+ ",		" + urlListToString(data.getImportUrls()) + "\n");
			} else {
				failDataWriter.write(data.getContentId() + ",	" + data.getYoukuName() + ",   " + data.getSokuId() + ",	" + data.getSokuName()
						+",		" + (data.getSokuVersionName() == null ? "" : data.getSokuVersionName()) + ",		" + urlListToString(data.getImportUrls()) + "\n");
			}
		}
		
		successWriter.flush();
		successWriter.close();
		failDataWriter.flush();
		failDataWriter.close();
		
	}
	
	private String urlListToString(List<String> urls) {
		StringBuilder sb = new StringBuilder();
		if(urls != null) {
			for(String url : urls) {
				if(sb.length() != 0) {
					sb.append("|");
				}
				sb.append(url);
			}
		}
		
		return sb.toString();
	}
	
	public String getCateName(int cateId) {
		return new String[]{"", "电视剧", "电影", "综艺", "", "动漫"}[cateId];
	}
	
	public void addSheetHead(WritableSheet sheet) throws Exception {
		Label a1Label = new Label(0, 0, "是否导入成功");
		Label b1Label = new Label(1, 0, "官方库id");
		Label c1Label = new Label(2, 0, "官方库节目名");
		Label d1Label = new Label(3, 0, "sokuid");
		Label e1Label = new Label(4, 0, "soku节目名");
		Label f1Label = new Label(5, 0, "soku版本名");
		Label g1Label = new Label(6, 0, "导入播放链接");
		sheet.addCell(a1Label);
		sheet.addCell(b1Label);
		sheet.addCell(c1Label);
		sheet.addCell(d1Label);
		sheet.addCell(e1Label);
		sheet.addCell(f1Label);
		sheet.addCell(g1Label);
	}
	
	public boolean importEpisodeFromOldDb(List<ImportDataStruct> dataList, boolean dropYouku, int dataCate, String logFile) {
		
		Connection conn = null; 
		boolean imported = false;
		
		String defaultSource = "";
		/*try {
			conn = Torque.getConnection("old_soku_library");
			for(ImportDataStruct data : dataList) {
				int versionId = data.getSokuId();
				int cate = data.getCateId();
				log.error("versionId: " + versionId + data.getYoukuName());
				try {			
					
					Criteria pCrit = new Criteria();
					pCrit.add(ProgrammePeer.CONTENT_ID, data.getContentId());
					List<Programme> pList = ProgrammePeer.doSelect(pCrit);
					Programme p = null;
					if(pList != null && !pList.isEmpty()) {
						p = pList.get(0);
					} else {
						continue;
					}
					int cateId = p.getCate();
					int programmeId = p.getId();
					String operator = "SokuOldDataImport";
					List<String> urlList = new ArrayList<String>();
					if(cate == Constants.TELEPLAY_CATE_ID) {
						Criteria crit = new Criteria();
						crit.add(TeleplaySiteVersionPeer.FK_TELEPLAY_VERSION_ID, versionId);
						List<TeleplaySiteVersion> tsvList = TeleplaySiteVersionPeer.doSelect(crit, conn);
						
					
						
						for(TeleplaySiteVersion tsv : tsvList) {	
							if(tsv.getEpisodeCollected() < 1 || tsv.getSourceSite() == Constants.KU6_SITE_ID
									|| tsv.getBlocked() == 1 || tsv.getSourceSite() == Constants.INTEGRATED_SITE_ID) {
								continue;
							}
							if(dropYouku && tsv.getSourceSite() == Constants.YOUKU_SITE_ID) {
								continue;
							}
							int programmeSiteId = ProgrammeSiteService.getProgrammeSiteId(programmeId, tsv.getSourceSite());
							Criteria teCrit = new Criteria();
							teCrit.add(TeleplayEpisodePeer.FK_TELEPLAY_SITE_VERSION_ID, tsv.getId());
							List<TeleplayEpisode> teList = TeleplayEpisodePeer.doSelect(teCrit, conn);
							if(teList != null && teList.size() > 0) {
								
								for(TeleplayEpisode te: teList) {
									if(!StringUtils.isBlank(te.getUrl())) {
										int siteId = getSiteId(te.getUrl());
										if(siteId != tsv.getSourceSite()) {
											log.error("error siteId: " + te.getUrl());
											continue;
										}
										ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, te.getOrderId());
										if(pe == null) {
											pe = new ProgrammeEpisode();
											pe.setCreateTime(new Date());
										}
										pe.setUrl(te.getUrl());
										pe.setLogo(te.getLogo());
										pe.setFkProgrammeSiteId(programmeSiteId);
										pe.setOrderId(te.getOrderId());
										pe.setHd(te.getHd());
										pe.setTitle(te.getTitle());
										pe.setUpdateTime(new Date());
										pe.setSeconds(te.getSeconds());
										pe.setUpdateTime(new Date());
										pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
										pe.setSource(defaultSource);
										pe.save();
										
										urlList.add(pe.getUrl());
										
										imported = true;
										//Log URL change
										//ProgrammeSiteService.logEpisodeUrl(pe, cateId, operator);
									}
								}
								
								ProgrammeSiteService.updateEpisodeCollected(programmeSiteId);
							}
						}
					}
					
					if(cate == Constants.ANIME_CATE_ID) {	
						AnimeVersion av = AnimeVersionPeer.retrieveByPK(versionId, conn);
						Names names = NamesPeer.retrieveByPK(av.getFkNamesId(), conn);
						data.setSokuName(names.getName());
						if(!StringUtils.isBlank(av.getName())) {
							data.setSokuVersionName(av.getName());
						}
						
						Criteria crit = new Criteria();
						crit.add(AnimeSiteVersionPeer.FK_ANIME_VERSION_ID, versionId);
						List<AnimeSiteVersion> tsvList = AnimeSiteVersionPeer.doSelect(crit, conn);
						
						
						for(AnimeSiteVersion tsv : tsvList) {
							if(tsv.getEpisodeCollected() < 1 || tsv.getSourceSite() == Constants.KU6_SITE_ID
									|| tsv.getBlocked() == 1 || tsv.getSourceSite() == Constants.INTEGRATED_SITE_ID) {
								continue;
							}
							if(dropYouku && tsv.getSourceSite() == Constants.YOUKU_SITE_ID) {
								continue;
							}
							int programmeSiteId = ProgrammeSiteService.getProgrammeSiteId(programmeId, tsv.getSourceSite());
							
							Criteria teCrit = new Criteria();
							teCrit.add(AnimeEpisodePeer.FK_ANIME_SITE_VERSION_ID, tsv.getId());
							List<AnimeEpisode> teList = AnimeEpisodePeer.doSelect(teCrit, conn);
							if(teList != null && teList.size() > 0) {
								for(AnimeEpisode te: teList) {
									if(!StringUtils.isBlank(te.getUrl())) {
										int siteId = getSiteId(te.getUrl());
										if(siteId != tsv.getSourceSite()) {
											log.error("error siteId: " + te.getUrl());
											continue;
										}
										ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, te.getOrderId());
										if(pe == null) {
											pe = new ProgrammeEpisode();
											pe.setCreateTime(new Date());
										}
										pe.setUrl(te.getUrl());
										pe.setLogo(te.getLogo());
										pe.setFkProgrammeSiteId(programmeSiteId);
										pe.setOrderId(te.getOrderId());
										pe.setHd(te.getHd());
										pe.setTitle(te.getTitle());
										pe.setUpdateTime(new Date());
										pe.setSeconds(te.getSeconds());
										pe.setUpdateTime(new Date());
										pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
										pe.setSource(defaultSource);
										pe.save();
										
										urlList.add(pe.getUrl());
										imported = true;
										//Log URL change
										//ProgrammeSiteService.logEpisodeUrl(pe, cateId, operator);
									}
								}						
								ProgrammeSiteService.updateEpisodeCollected(programmeSiteId);
							}
						}
					
						
					}
					
					if(cate == Constants.MOVIE_CATE_ID) {
						Movie m = MoviePeer.retrieveByPK(versionId, conn);
						
						String url = m.getViewUrl();
						if(!StringUtils.isBlank(url)) {
							int siteId = getSiteId(url);
							
							if(siteId > 0 && siteId != Constants.KU6_SITE_ID) {											
																		
									if(dropYouku && siteId == Constants.YOUKU_SITE_ID) {
										continue;
									}
									int programmeSiteId = ProgrammeSiteService.getProgrammeSiteId(programmeId, siteId);
									
									ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, 1);
									if(pe == null) {
										pe = new ProgrammeEpisode();
										pe.setCreateTime(new Date());
									}
									pe.setUrl(m.getViewUrl());
									pe.setLogo(m.getFirstLogo());
									pe.setFkProgrammeSiteId(programmeSiteId);
									pe.setOrderId(1);
									pe.setHd(m.getHd());
									pe.setUpdateTime(new Date());
									pe.setSeconds(m.getSeconds());
									pe.setUpdateTime(new Date());
									pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
									pe.setSource(defaultSource);
									pe.save();
									urlList.add(pe.getUrl());
									imported = true;
									//Log URL change
									//ProgrammeSiteService.logEpisodeUrl(pe, cateId, operator);
									ProgrammeSiteService.updateEpisodeCollected(programmeSiteId);
							}
						}
					}
					
					if(cate == Constants.VARIETY_CATE_ID) {
						Criteria crit = new Criteria();
						crit.add(VarietySubPeer.FK_VARIETY_ID, versionId);
						crit.addAscendingOrderByColumn(VarietySubPeer.ORDER_ID);
						
						List<VarietySub> vsList = VarietySubPeer.doSelect(crit, conn);
					
						int orderId = 0;
						for(VarietySub vs : vsList) {
							orderId++;
							Criteria veCrit = new Criteria();
							veCrit.add(VarietyEpisodePeer.FK_VARIETY_SUB_ID, vs.getId());
							List<VarietyEpisode> veList = VarietyEpisodePeer.doSelect(veCrit, conn);
							if(veList != null && !veList.isEmpty()) {
								int programmeSiteId = -1;
								for(VarietyEpisode ve : veList) {
									if(!StringUtils.isBlank(ve.getUrl())) {
										int orderStage = vs.getOrderId();  //对应programme_episode的order_stage
										if(dropYouku && ve.getSourceSite() == Constants.YOUKU_SITE_ID) {
											continue;
										}
										int siteId = getSiteId(ve.getUrl());
										if(siteId != ve.getSourceSite()) {
											log.error("error siteId: " + ve.getUrl());
											continue;
										}
										if(ve.getSourceSite() != Constants.KU6_SITE_ID && ve.getSourceSite() != Constants.INTEGRATED_SITE_ID) {
											programmeSiteId = ProgrammeSiteService.getProgrammeSiteId(programmeId, ve.getSourceSite());
											ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(programmeSiteId, orderId);
											if(pe == null) {
												pe = new ProgrammeEpisode();
												pe.setCreateTime(new Date());
											}
											pe.setUrl(ve.getUrl());
											pe.setLogo(ve.getLogo());
											pe.setFkProgrammeSiteId(programmeSiteId);
											pe.setOrderId(orderId);
											pe.setOrderStage(orderStage);
											pe.setHd(ve.getHd());
											pe.setTitle(ve.getTitle());
											pe.setUpdateTime(new Date());
											pe.setSeconds(ve.getSeconds());
											pe.setUpdateTime(new Date());
											pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
											pe.setSource(defaultSource);
											pe.save();
											urlList.add(pe.getUrl());
											imported = true;
											//Log URL change
											//ProgrammeSiteService.logEpisodeUrl(pe, cateId, operator);
										}
									}
									ProgrammeSiteService.updateEpisodeCollected(programmeSiteId);
								}
								
								
							}
						}
					}
					
					data.setImportUrls(urlList);
					data.setImportFlag(imported);
				} catch(Exception e) {
					log.error("error versionId: " + versionId);
					log.error(e.getMessage(), e);
				}
			}
			logImporting(dataList, dataCate, logFile);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}*/
		return imported;
	}
	
public boolean animeDuplicateNameProcess(List<ImportDataStruct> dataList) {
		
		boolean imported = false;
		try {
			
			File animeDuplicateName = new File("/opt/soku_data_import_log/anime_duplicate_name.log");
			BufferedWriter bw = new BufferedWriter(new FileWriter(animeDuplicateName));
			for(ImportDataStruct data : dataList) {
				int versionId = data.getSokuId();
				log.error("versionId: " + versionId + data.getYoukuName());
				try {			
					
					Criteria pCrit = new Criteria();
					pCrit.add(ProgrammePeer.CONTENT_ID, data.getContentId());
					List<Programme> pList = ProgrammePeer.doSelect(pCrit);
					Programme p = null;
					if(pList != null && !pList.isEmpty()) {
						p = pList.get(0);
					} else {
						continue;
					}
									
					Criteria nameCrit = new Criteria();
					nameCrit.add(ProgrammePeer.NAME, p.getName());
					nameCrit.add(ProgrammePeer.CATE, 5);
					nameCrit.add(ProgrammePeer.CONTENT_ID, data.getContentId(), Criteria.NOT_EQUAL);
					List<Programme> duplicateNameList = ProgrammePeer.doSelect(nameCrit);
					if(duplicateNameList != null && !duplicateNameList.isEmpty()) {
						Programme oldP = duplicateNameList.get(0);
						
						Criteria psCrit = new Criteria();
						psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
						List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
						if(psList != null) {
							for(ProgrammeSite ps: psList) {
								int newProgrammeSiteId = ProgrammeSiteService.getProgrammeSiteId(oldP.getId(), ps.getSourceSite()); //复制programmeSite
								
								Criteria peCrit = new Criteria();
								peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
								List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
								if(peList != null) {
									for(ProgrammeEpisode oldPe : peList) {
										ProgrammeEpisode pe = ProgrammeEpisodeService.getUniqueProgrammeEpisode(newProgrammeSiteId, oldPe.getOrderId());
										if(pe == null) {
											pe = new ProgrammeEpisode();
											pe.setCreateTime(new Date());
										}
										pe.setUrl(oldPe.getUrl());
										pe.setLogo(oldPe.getLogo());
										pe.setFkProgrammeSiteId(newProgrammeSiteId);
										pe.setOrderId(oldPe.getOrderId());
										pe.setOrderStage(oldPe.getOrderStage());
										pe.setHd(oldPe.getHd());
										pe.setTitle(oldPe.getTitle());
										pe.setUpdateTime(new Date());
										pe.setSeconds(oldPe.getSeconds());
										pe.setUpdateTime(new Date());
										pe.setViewOrder(oldPe.getViewOrder());
										pe.setSource(oldPe.getSource());
										pe.save();
									}
								}
								
								log.info("update programme site name:" + p.getName() + " old fkid: " + p.getId() + "new fkId: " + oldP.getId());
								ProgrammeSiteService.updateEpisodeCollected(newProgrammeSiteId);
							}
							
						}
						bw.write(data.getShowId() + "," + data.getContentId() + "," + data.getSokuId() + "," + data.getYoukuName() + "\n");
					}
					
				} catch(Exception e) {
					log.error("error versionId: " + versionId);
					log.error(e.getMessage(), e);
				}
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
		}
		return imported;
	}

private int getSiteId(String url) {
	int siteId = -1;
	String domain = Utils.parseDomain(url);
	if(!StringUtils.isBlank(domain)) {		
		//	siteId = NameAndCons.domainAndSiteIds.get(domain);
	}
	
	return siteId;
}
	
	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			TorqueManage.initTorque();
			WritableWorkbook workbook = Workbook.createWorkbook(new File("/opt/soku_data_import_log/output.xls")); 
			SokuOldDataImport importer = new SokuOldDataImport();
			List<ImportDataStruct> cartoonData = importer.getDataFromPlainText();
			importer.importEpisodeFromOldDb(cartoonData, false, 5, "");
			workbook.close();
			TorqueManage.closeTorque();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static class ImportDataStruct {
		
		private String showId;
		
		private int cateId;
		
		private int contentId;
		
		private String youkuName;
		
		private int sokuId;
		
		private String sokuName;
		
		private String sokuVersionName;
		
		private boolean importFlag;
		
		private List<String> importUrls;
		
		
		

		public String getShowId() {
			return showId;
		}

		public void setShowId(String showId) {
			this.showId = showId;
		}

		public int getCateId() {
			return cateId;
		}

		public void setCateId(int cateId) {
			this.cateId = cateId;
		}

		public int getContentId() {
			return contentId;
		}

		public void setContentId(int contentId) {
			this.contentId = contentId;
		}

		public String getYoukuName() {
			return youkuName;
		}

		public void setYoukuName(String youkuName) {
			this.youkuName = youkuName;
		}

		public int getSokuId() {
			return sokuId;
		}

		public void setSokuId(int sokuId) {
			this.sokuId = sokuId;
		}

		public String getSokuName() {
			return sokuName;
		}

		public void setSokuName(String sokuName) {
			this.sokuName = sokuName;
		}

		public boolean isImportFlag() {
			return importFlag;
		}

		public void setImportFlag(boolean importFlag) {
			this.importFlag = importFlag;
		}

		public List<String> getImportUrls() {
			return importUrls;
		}

		public void setImportUrls(List<String> importUrls) {
			this.importUrls = importUrls;
		}

		public String getSokuVersionName() {
			return sokuVersionName;
		}

		public void setSokuVersionName(String sokuVersionName) {
			this.sokuVersionName = sokuVersionName;
		}
		
		

	}
}
