package com.youku.soku.manage.datamaintain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;

public class HandleLackEpisodeData {
	
	/**
	 * 列出缺集的电视剧，并删除缺集数大于2的
	 */
	
	public void listData() throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.CATE, 1);
		
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		Map<Integer, Set<String>> lackEpisodeMap = new TreeMap<Integer, Set<String>>();
		for(Programme p : pList) {
			Criteria psCrit = new Criteria();
			psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
			for(ProgrammeSite ps : psList) {
				if (p.getCate() == Constants.TELEPLAY_CATE_ID && ps.getSourceSite() != 100) {

					if (p.getEpisodeTotal() != 0) {
						int lackEpisodeCount = p.getEpisodeTotal() - ps.getEpisodeCollected();
						Set<String> episodeList = lackEpisodeMap.get(lackEpisodeCount);
						if (episodeList == null) {
							episodeList = new HashSet<String>();
							lackEpisodeMap.put(lackEpisodeCount, episodeList);
						}
						episodeList.add("siteId:" + ps.getSourceSite() + " name: " + p.getName());
					}
				}
			}
		}
		
		File file = new File("/opt/search/lack_episode_HandleLackEpisodeData.log");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int totalCount = 0;
		for (int key : lackEpisodeMap.keySet()) {
			bw.write("缺少" + key + "集的 剧集数" + lackEpisodeMap.get(key).size()  + "   详细 " + lackEpisodeMap.get(key));
			bw.write("\n");
			totalCount += lackEpisodeMap.get(key).size();
		}

		bw.close();
	}
	
	
	public void deleteData() throws Exception {
		
		File file = new File("/opt/search/lack_episode_HandleLackEpisodeData_delete_0507_2_includeyouku.log");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.CATE, 1);
		
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		Map<Integer, Set<String>> lackEpisodeMap = new TreeMap<Integer, Set<String>>();
		
		int deleteProgrammeCount = 0;
		int delteProgrammeSiteCount = 0;
		int deleteUrlCount = 0;
		for(Programme p : pList) {
			Criteria psCrit = new Criteria();
			psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
			boolean deleteProgramme = false;
			for(ProgrammeSite ps : psList) {
				if (p.getCate() == Constants.TELEPLAY_CATE_ID && ps.getSourceSite() != 100) {

					if (p.getEpisodeTotal() != 0 && ps.getEpisodeCollected() > 0) {
						int lackEpisodeCount = p.getEpisodeTotal() - ps.getEpisodeCollected();
						
						if(lackEpisodeCount > 2) {
							deleteProgramme = true;
							Criteria peCrit = new Criteria();
							peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
							
							List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
							bw.write("删除剧集： " + p.getName() + " siteId:" + ps.getSourceSite()  + "   详细 :");
							for(ProgrammeEpisode pe : peList) {
								bw.write(pe.getUrl() + "|");
								deleteUrlCount++;
								ProgrammeEpisodePeer.doDelete(pe.getPrimaryKey());
							}
							bw.write("\n");
						}
						delteProgrammeSiteCount++;
					}
				}
			}
			
			if(deleteProgramme) {
				deleteProgrammeCount++;
			}
		}
		
		
		bw.write("deleteProgrammeCount: " + deleteProgrammeCount + "delteProgrammeSiteCount: " + delteProgrammeSiteCount + "deleteUrlCount: " + deleteUrlCount);
		bw.close();
	}
	
	
	
public void deleteDataEpisodeTotal0() throws Exception {
		
		File file = new File("/opt/search/lack_episode_HandleLackEpisodeData_delete_0507_total0.log");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.CATE, 1);
		crit.add(ProgrammePeer.EPISODE_TOTAL, 0);
		
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		Map<Integer, Set<String>> lackEpisodeMap = new TreeMap<Integer, Set<String>>();
		
		int deleteProgrammeCount = 0;
		int delteProgrammeSiteCount = 0;
		int deleteUrlCount = 0;
		for(Programme p : pList) {
			Criteria psCrit = new Criteria();
			psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
			
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
			boolean deleteProgramme = false;
			for(ProgrammeSite ps : psList) {
				if (p.getCate() == Constants.TELEPLAY_CATE_ID && ps.getSourceSite() != 100) {

					if (p.getEpisodeTotal() == 0) {
						
						Criteria peCrit = new Criteria();
						peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
						
						List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
						
						int maxOrder = 0;
						for(ProgrammeEpisode pe : peList) {
							if(maxOrder < pe.getOrderId()) {
								maxOrder = pe.getOrderId();
							}
						}
						
						if((maxOrder - ps.getEpisodeCollected()) > 2) {
							bw.write("删除剧集： " + p.getName() + " siteId:" + ps.getSourceSite()  + "   详细 :");
							for(ProgrammeEpisode pe : peList) {
								bw.write(pe.getUrl() + "|");
								delteProgrammeSiteCount++;
								ProgrammeEpisodePeer.doDelete(pe.getPrimaryKey());
							}
							bw.write("\n");
						}
						
					}
				}
			}
			
			if(deleteProgramme) {
				deleteProgrammeCount++;
			}
		}
		
		
		bw.write("deleteProgrammeCount: " + deleteProgrammeCount + "delteProgrammeSiteCount: " + delteProgrammeSiteCount + "deleteUrlCount: " + deleteUrlCount);
		bw.close();
	}

}
