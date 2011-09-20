package com.youku.soku.manage.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.util.JdbcUtil;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSearchNumber;
import com.youku.soku.library.load.ProgrammeSearchNumberPeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.ProgrammePlainBo;
import com.youku.soku.manage.bo.VideoInfoBo;
import com.youku.soku.manage.common.CommonVideo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.top.mapping.TopWords;
import com.youku.soku.util.DataBase;

public class ProgrammeEpisodeService {
	public static List<ProgrammeEpisode> findProgrammeEpisode(int siteId)
			throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, siteId);
		crit.addDescendingOrderByColumn(ProgrammeEpisodePeer.ORDER_ID);

		/*
		 * if(siteId == Constants.YOUKU_SITE_ID) {
		 * crit.add(ProgrammeEpisodePeer.VIEW_ORDER, 2); //youku站点只显示自动发现的剧集 }
		 */

		List<ProgrammeEpisode> telList = ProgrammeEpisodePeer.doSelect(crit);
		return telList;

	}

	public static void integrateEpisode(int integrateSiteId) throws Exception {
		ProgrammeSite integrateSite = ProgrammeSitePeer
				.retrieveByPK(integrateSiteId);
		Programme version = ProgrammePeer.retrieveByPK(integrateSite
				.getFkProgrammeId());

		List<ProgrammeEpisode> episodeList = findProgrammeEpisode(integrateSiteId);
		if (episodeList != null && !episodeList.isEmpty()
				&& episodeList.size() == version.getEpisodeTotal()) {
			return;
		}

		List<ProgrammeSite> siteList = ProgrammeSiteService
				.findProgrammeSite(version.getId());
		int maxEpisode = 0;
		if (version.getEpisodeTotal() != 0) {
			maxEpisode = version.getEpisodeTotal();
			/*
			 * ProgrammeSite youkuSite =
			 * ProgrammeSiteService.getSite(version.getId(),
			 * Constants.YOUKU_SITE_ID); List<ProgrammeEpisode> youkuEpisodeList
			 * = findProgrammeEpisode(youkuSite.getId()); if(youkuEpisodeList !=
			 * null && youkuEpisodeList.size() == maxEpisode) {
			 * 
			 * }
			 */
		} else {
			for (ProgrammeSite site : siteList) {
				List<ProgrammeEpisode> sitesEpisodeList = findProgrammeEpisode(site
						.getId());
				for (ProgrammeEpisode ep : sitesEpisodeList) {
					if (ep.getOrderId() > maxEpisode) {
						maxEpisode = ep.getOrderId();
					}
				}
			}
		}

		for (int i = 1; i <= maxEpisode; i++) {
			for (ProgrammeSite site : siteList) {
				List<ProgrammeEpisode> sitesEpisodeList = findProgrammeEpisode(
						site.getId(), i);

				if (sitesEpisodeList != null && sitesEpisodeList.size() > 0) {
					ProgrammeEpisode ep = sitesEpisodeList.get(0);
					saveIntegrateEpisode(ep, integrateSiteId);
				}

			}
		}

	}

	private static void saveIntegrateEpisode(ProgrammeEpisode episode,
			int integrateSiteId) throws Exception {
		ProgrammeEpisode newEpisode = new ProgrammeEpisode();

		newEpisode.setFkProgrammeSiteId(integrateSiteId);
		newEpisode.setTitle(episode.getTitle());
		newEpisode.setOrderId(episode.getOrderId());
		newEpisode.setLogo(episode.getLogo());
		newEpisode.setSeconds(episode.getSeconds());
		newEpisode.setHd(episode.getHd());
		newEpisode.setUrl(episode.getUrl());
		newEpisode.setCreateTime(new Date());
		newEpisode.setUpdateTime(new Date());
		newEpisode.save();
	}

	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static List<ProgrammeEpisode> findProgrammeEpisode()
			throws TorqueException {

		Criteria crit = new Criteria();

		List<ProgrammeEpisode> telList = ProgrammeEpisodePeer.doSelect(crit);
		return telList;

	}

	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static List<ProgrammeEpisode> findProgrammeEpisode(int siteId,
			int orderId) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, siteId);
		crit.add(ProgrammeEpisodePeer.ORDER_ID, orderId);
		List<ProgrammeEpisode> telList = ProgrammeEpisodePeer.doSelect(crit);
		return telList;

	}

	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static ProgrammeEpisode getUniqueProgrammeEpisode(
			int programmeSiteId, int orderId) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, programmeSiteId);
		crit.add(ProgrammeEpisodePeer.ORDER_ID, orderId);
		crit.addDescendingOrderByColumn(ProgrammeEpisodePeer.VIEW_ORDER);
		List<ProgrammeEpisode> telList = ProgrammeEpisodePeer.doSelect(crit);
		if (telList != null && telList.size() > 0) {
			return telList.get(0);
		} else {
			return null;
		}

	}

	/**
	 * <p>
	 * Find all the tel
	 * </p>
	 * 
	 * @return the list of the tel object
	 */
	public static ProgrammeEpisode getUniqueVarietyEpisode(int programmeSiteId,
			int orderStage) throws TorqueException {

		Criteria crit = new Criteria();
		crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID, programmeSiteId);
		crit.add(ProgrammeEpisodePeer.ORDER_STAGE, orderStage);
		crit.addDescendingOrderByColumn(ProgrammeEpisodePeer.VIEW_ORDER);
		List<ProgrammeEpisode> telList = ProgrammeEpisodePeer.doSelect(crit);
		if (telList != null && telList.size() > 0) {
			return telList.get(0);
		} else {
			return null;
		}

	}

	public static void updateProgrammeEpisodeContent(ProgrammeEpisode te,
			int siteId) throws SQLException, TorqueException {

		CommonVideo cv = CommonVideoService.getCommonVideoInfo(te.getUrl(),
				siteId);
		if (cv != null) {
			if (!cv.isYouku()) {
				te.setTitle(cv.getTitle());
			} else {
				if (te.getTitle() == null || te.getTitle().equals("")) {
					te.setTitle("第" + te.getOrderId() + "集");
				}
			}
			te.setHd(cv.getHd());
			te.setSeconds(cv.getSeconds());
			te.setLogo(cv.getLogo());
		} else {
			te.setTitle("第" + te.getOrderId() + "集");
		}

		System.out.println("te.getTitle" + te.getTitle());
		System.out.println(cv);
	}

	public static Programme getProgramme(int programmeSiteId) throws Exception {
		ProgrammeSite ps = ProgrammeSitePeer.retrieveByPK(programmeSiteId);
		return ProgrammePeer.retrieveByPK(ps.getFkProgrammeId());

	}

	// 获取非官方库的优酷非高清/超清视频
	public static PageInfo getPlainPage(PageInfo page,String searchWord,int cate, int ob, int resource,
			Logger log) {
		
		Map<Integer,Programme> programmeMap=new HashMap<Integer,Programme>();
		Map<Integer,ProgrammePlainBo> ppbMap = new HashMap<Integer,ProgrammePlainBo>();
		List<ProgrammePlainBo> result=new ArrayList<ProgrammePlainBo>();
		
		String psql = "from programme where id in "
					+"(select distinct fk_programme_id from programme_site where source_site="+Constants.YOUKU_SITE_ID
					+" and id in "
					+"(select distinct fk_programme_site_id from programme_episode where";
		if(resource>-1)
			psql += " resource="+resource+" and";
		psql += " view_order=2 and hd=0 and url is not null))";
		String catesql = " and cate="+cate;
		String likesql = " and name like '%"+searchWord+"%'";
		if(null!=searchWord && !searchWord.isEmpty())
			psql = psql +likesql;
		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		PreparedStatement pepst = null;
		ResultSet pers = null;

		try {
			conn = DataBase.getLibraryConn();
			if(cate>0)
				pstcnt = conn.prepareStatement("select count(*) "+psql+catesql);
			else
				pstcnt = conn.prepareStatement("select count(*) "+psql);
			
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			
			if (recordCount == 0){
				page.setResults(result);
				return page;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ page.getPageSize());
			page.setTotalPageNumber(totalPageNumber);
			page.setTotalRecords(recordCount);
			StringBuffer sb=new StringBuffer(); 
			if(cate>0){
				//if(ob>0)
				//	pst = conn.prepareStatement("select * "+psql+catesql);
				//else
					pst = conn.prepareStatement("select * "+psql+catesql+" LIMIT " + page.getOffset() + ", "
							+ page.getPageSize());
			}else{
				//if(ob>0)
				//	pst = conn.prepareStatement("select * "+psql);
				//else
					pst = conn.prepareStatement("select * "+psql+" LIMIT " + page.getOffset() + ", "
							+ page.getPageSize());
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				Programme p = new Programme();
				p.setId(rs.getInt("id"));
				p.setName(rs.getString("name"));
				p.setCate(rs.getInt("cate"));
				p.setContentId(rs.getInt("content_id"));
				if(ob>0)
					fixProgrammeInfo(p,ob);
				ProgrammePlainBo ppb =new ProgrammePlainBo();
				ppb.setProgramme(p);
				
				programmeMap.put(p.getId(), p);
				ppbMap.put(p.getId(), ppb);
				
				sb.append(p.getId()+",");
			}
			if(sb.length()>0)
				sb=sb.append(sb.substring(0, sb.length()-1));
			
			String pesql = "select ps.fk_programme_id,pe.* from programme_episode pe,programme_site ps where ps.source_site="+Constants.YOUKU_SITE_ID+" and";
			if(resource>-1)
				pesql += " pe.resource="+resource+" and";
			pesql+=" pe.view_order=2 and pe.hd=0 and pe.url is not null and pe.fk_programme_site_id=ps.id and ps.fk_programme_id in "
				         +"("+sb.toString()+")";
			
			pepst = conn.prepareStatement(pesql);
			pers = pepst.executeQuery();
			while (pers.next()) {
				int pid=pers.getInt("ps.fk_programme_id");
				ProgrammeEpisode pe = new ProgrammeEpisode();
				pe.setId(pers.getInt("pe.id"));
				pe.setOrderId(pers.getInt("pe.order_id"));
				pe.setOrderStage(pers.getInt("pe.order_stage"));
				pe.setTitle(pers.getString("pe.title"));
				pe.setUrl(pers.getString("pe.url"));
				pe.setResource(pers.getInt("pe.resource"));
				
				ProgrammePlainBo ppb=ppbMap.get(pid);
				if(null!=ppb)
				{
					List<ProgrammeEpisode> peList=ppb.getProgrammeEpisodes();
					if(peList==null)
						peList=new ArrayList<ProgrammeEpisode>();
					peList.add(pe);
					ppb.setProgrammeEpisodes(peList);
					ppbMap.put(pid, ppb);
				}
			}
			result.addAll(ppbMap.values());
			if(ob>0){
				sortPlainProgramme(result,ob);
				result=page.pagingList(result);
			}
			page.setResults(result);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (pstcnt != null) {
					pstcnt.close();
				}
				if (rscnt != null) {
					rscnt.close();
				}
				if (pepst != null) {
					pepst.close();
				}
				if (pers != null) {
					pers.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return page;
	}
	
	public static void sortPlainProgramme(List<ProgrammePlainBo> ppbs,int ob){
		switch(ob){
		//按发行时间排序
			case 1:
				Collections.sort(ppbs, new Comparator<ProgrammePlainBo>(){
					public int compare(ProgrammePlainBo ppb1,ProgrammePlainBo ppb2){
						if(ppb1.getProgramme().getReleaseDate()==null)
							return -1;
						if(ppb2.getProgramme().getReleaseDate()==null)
							return 1;
						try {
							Date d1=new SimpleDateFormat("yyyy-MM-dd").parse(ppb1.getProgramme().getReleaseDate());
							Date d2=new SimpleDateFormat("yyyy-MM-dd").parse(ppb2.getProgramme().getReleaseDate());
							return d2.compareTo(d1);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return 0;
						}
					}
				});
				break;
		//按搜索热度排序
			case 2:
				Collections.sort(ppbs, new Comparator<ProgrammePlainBo>(){
					public int compare(ProgrammePlainBo ppb1,ProgrammePlainBo ppb2){
						if(ppb1.getProgramme().getPaid()==ppb2.getProgramme().getPaid())
							return 0;
						return ppb1.getProgramme().getPaid() > ppb2.getProgramme().getPaid() ? -1 : 1;
					}
				});
				break;
			default:
				;
		}
	}
	
	public static void fixProgrammeInfo(Programme p,int ob){
		VideoInfoBo videoBo = new VideoInfoBo();
		videoBo.setId(p.getId());
		videoBo.setContentId(p.getContentId());
		videoBo.setName(p.getName());
		videoBo.setCategory(p.getCate());
		videoBo.setEpisodeTotal(p.getEpisodeTotal());
		videoBo.setSource(p.getSource());
		videoBo.setBlock(p.getBlocked());
		videoBo.setAlias(p.getAlias());
		if(ob==1){
			//获取上映时间
			VideoInfoService.videoInfoGetterFromHttp(videoBo);
			p.setReleaseDate(videoBo.getReleaseDate());
		}else if(ob==2){
			//获取搜索量
			Criteria crit = new Criteria();
			crit.add(ProgrammeSearchNumberPeer.FK_PROGRAMME_ID,p.getId());
			try {
				List<ProgrammeSearchNumber> twList = ProgrammeSearchNumberPeer.doSelect(crit);
				if(null!=twList && twList.size()>0)
				{
					ProgrammeSearchNumber psn=twList.get(0);
					p.setPaid(psn.getSearchNumber());
				}
			} catch (TorqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void synProgrammeIdForProgrammeEpisode(Logger log){
		try {
			//获取所有节目-站点对应的节目id
			Map<Integer,Integer> pspidMap=new HashMap<Integer,Integer>();
			int count = 0;
			Criteria crit = new Criteria();
			List<ProgrammeSite> psList=ProgrammeSitePeer.doSelect(crit);
			
			if(null==psList||psList.size()==0)
				return;
			for(ProgrammeSite ps:psList)
				pspidMap.put(ps.getId(), ps.getFkProgrammeId());
			
			Criteria pecrit = new Criteria();
			pecrit.add(ProgrammeEpisodePeer.FK_PROGRAMME_ID,0);
			List<ProgrammeEpisode> peList=ProgrammeEpisodePeer.doSelect(pecrit);
			
			if(null==peList||peList.size()==0)
				return;
			for(ProgrammeEpisode pe:peList){
				int pid= 0;
				if(null != pspidMap.get(pe.getFkProgrammeSiteId()))
					pid=pspidMap.get(pe.getFkProgrammeSiteId());
				pe.setFkProgrammeId(pid);
				ProgrammeEpisodePeer.doUpdate(pe);
				if(pid>0)
					count++;
			}
			log.debug("******* syn pid:"+count+"****");
		} catch (TorqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
