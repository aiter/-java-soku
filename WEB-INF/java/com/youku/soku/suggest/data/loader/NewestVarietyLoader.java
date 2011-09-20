package com.youku.soku.suggest.data.loader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.common.Constants;

public class NewestVarietyLoader {
	
	private static final Logger logger = Logger.getLogger(NewestVarietyLoader.class);
	
	public static Map<String, List<String[]>> getNewestVariety() throws TorqueException {
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		Map<String, List<String[]>> newestVarietyMap = new HashMap<String, List<String[]>>();
		
		Criteria crit = new Criteria();
		crit.add(ProgrammePeer.CATE, Constants.VARIETY_CATE_ID);
		crit.add(ProgrammePeer.NAME, (Object)("%" + curYear), Criteria.LIKE);
		
		List<Programme> pList = ProgrammePeer.doSelect(crit);
		if(pList != null) {
			for(Programme p : pList) {
				logger.info("build newest variety: " + p.getName());
				String programmeName = p.getName().replace(curYear + "", "").trim();
				Criteria psCrit = new Criteria();
				psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, p.getId());
				psCrit.add(ProgrammeSitePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);
				
				if(psList != null && !psList.isEmpty()) {
					ProgrammeSite ps = psList.get(0);
					Criteria peCrit = new Criteria();
					peCrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, ps.getId());
					peCrit.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_STAGE);
					peCrit.setLimit(3);
					
					List<ProgrammeEpisode> peList = ProgrammeEpisodePeer.doSelect(peCrit);
					if(peList != null) {
						for(ProgrammeEpisode pe : peList) {
							List<String[]> urlList = newestVarietyMap.get(programmeName);
							if(urlList == null) {
								urlList = new ArrayList<String[]>();
								newestVarietyMap.put(programmeName, urlList);
							}
							if(!StringUtils.isBlank(pe.getUrl())) {
								urlList.add(new String[]{pe.getUrl(), programmeName + pe.getOrderStage()});
								logger.info("variety: " + programmeName + pe.getOrderStage() + "url: " + pe.getUrl());
							}
						}
					}
				}
			}
		}
		return newestVarietyMap;
	}
	

	
	/*public static Map<String, List<String[]>> getNewestVariety_olg() throws TorqueException {
		Criteria namesCrit = new Criteria();
		namesCrit.add(NamesPeer.FK_CATE_ID, Constants.VARIETY_CATE_ID);
		//namesCrit.add(NamesPeer.NAME, "康熙来了");
		List<Names> namesList= NamesPeer.doSelect(namesCrit);
		Map<String, List<String[]>> newestVarietyMap = new HashMap<String, List<String[]>>();
		for(Names names : namesList) {
			Criteria varietyCrit = new Criteria();
			varietyCrit.add(VarietyPeer.FK_NAMES_ID, names.getId());
			varietyCrit.addDescendingOrderByColumn(VarietyPeer.NAME);
			
			List<Variety> varietyList = VarietyPeer.doSelect(varietyCrit);
			if(varietyList != null && !varietyList.isEmpty()) {
				Variety newestVariety = varietyList.get(0);
				Criteria subCrit = new Criteria();
				subCrit.add(VarietySubPeer.FK_VARIETY_ID, newestVariety.getId());
				subCrit.addDescendingOrderByColumn(VarietySubPeer.MONTH);
				subCrit.addDescendingOrderByColumn(VarietySubPeer.DAY);
				subCrit.setLimit(2);
				
				List<VarietySub> subList = VarietySubPeer.doSelect(subCrit);
				if(subList != null && !subList.isEmpty()) {
					List<String[]> urlList = newestVarietyMap.get(names.getName());
					if(urlList == null) {
						urlList = new ArrayList<String[]>();
						newestVarietyMap.put(names.getName(), urlList);
					}
					for(VarietySub vs : subList) { 
						Criteria episodeCrit = new Criteria();
						episodeCrit.add(VarietyEpisodePeer.SOURCE_SITE, Constants.YOUKU_SITE_ID);
						episodeCrit.add(VarietyEpisodePeer.FK_VARIETY_SUB_ID, vs.getId());
						List<VarietyEpisode> episodeList = VarietyEpisodePeer.doSelect(episodeCrit);
						if(episodeList != null && !episodeList.isEmpty()) {
							VarietyEpisode e = episodeList.get(0);
							if(!StringUtils.isBlank(e.getUrl())) {
								urlList.add(new String[]{e.getUrl(), names.getName() + vs.getYear()+ "" + ((vs.getMonth() < 10) ? "0" : "")+ vs.getMonth() + "" + ((vs.getDay() < 10) ? "0" : "") + vs.getDay()});
							}
						}
					}
				}
				
			}
		}
		return newestVarietyMap;
	}
*/
	
}
