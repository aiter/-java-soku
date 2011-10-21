package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.swing.Action;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONObject;

import com.youku.search.util.StringUtil;
import com.youku.soku.library.load.Blacklist;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeEpisodePeer;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.ProgrammeEpisodeDetailBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.ProgrammeEpisodeService;
import com.youku.soku.manage.service.ProgrammeSiteService;
import com.youku.soku.manage.service.SiteService;
import com.youku.soku.manage.service.UserOperationService;
import com.youku.soku.manage.util.ImageUtil;
import com.youku.soku.manage.util.ManageUtil;

public class ProgrammeEpisodeAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	private static final String TABNAME = "programme_episode";

	private static final String PROGRAMME_STATE_DELETED = "deleted";

	private static final int EPISODE_PAGE_SIZE = 50;

	/**
	 * <p>
	 * List the ProgrammeEpisode
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {
			int programmeSiteId = getProgrammeSiteId();
			ProgrammeSite programmeSite = ProgrammeSitePeer
					.retrieveByPK(programmeSiteId);

			ProgrammeEpisodeDetailBo peDetail = getSiteEpisodeDetailBo(programmeSite);

			if (peDetail == null) {
				HttpServletResponse response = ServletActionContext
						.getResponse();
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();

				out.print("<script>alert('没有相关站点信息');history.back();</script>");
				return null;
			}

			programmeEpisodeList = peDetail.getSiteEpisode();
			setSourceSiteId(programmeSite.getSourceSite());
			// 分页
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(EPISODE_PAGE_SIZE);
			pageInfo.setTotalPageNumber((int) Math
					.ceil((double) programmeEpisodeList.size()
							/ pageInfo.getPageSize()));
			if (getPageNumber() == 0) {
				pageInfo.setCurrentPageNumber(1);
			} else {
				pageInfo.setCurrentPageNumber(getPageNumber());
			}
			List<ProgrammeEpisode> pageResult = pageInfo
					.pagingList(programmeEpisodeList);
			log.info("pageResult" + pageResult.size());
			pageInfo.setResults(pageResult);
			setEpisodeTotal(programmeEpisodeList.size());
			setPageInfo(pageInfo);
			return Constants.LIST;
		} catch (NoRowsException e) {
			e.printStackTrace();
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return Constants.LIST;
	}

	/**
	 * <p>
	 * List the ProgrammeEpisode
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String detail() throws Exception {

		log.info("detail method");

		try {
			int programmeId = getProgrammeId();
			Programme p = ProgrammePeer.retrieveByPK(programmeId);

			if (PROGRAMME_STATE_DELETED.equals(p.getState())) {
				throw new PageNotFoundException(getText("该节目已经被删除"));
			}

			Criteria psCrit = new Criteria();
			psCrit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programmeId);
			List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(psCrit);

			int maxOrder = 0;
			int maxOrderSite = 0;
			int maxEpisodeCount = 0;
			Map<String, ProgrammeEpisode> episodeSiteMap = new HashMap<String, ProgrammeEpisode>();
			Map<Integer, Integer> programmeSiteIdMap = new HashMap<Integer, Integer>(); // key:
																						// siteId,
																						// value:
																	                    // programmeSiteId
			//根据综艺节目名称获取年份 去掉出现其他年份的数据
			String year = "";
			boolean zy_stage_no_date = false;
			//判断综艺order_stage是否全是非日期  以youku的为准
			if(p.getCate() == Constants.VARIETY_CATE_ID){
				
				for(ProgrammeSite ps : psList){
					if(ps.getSourceSite()==Constants.YOUKU_SITE_ID){
						ProgrammeEpisodeDetailBo peDetailBo = getSiteEpisodeDetailBo(ps);
						List<ProgrammeEpisode> episodeList = peDetailBo
								.getSiteEpisode();
						if (episodeList != null && episodeList.size()>0) {
							int order_stage_no_date = 0;
							for (ProgrammeEpisode pe : episodeList) {
								if(null!=pe.getUrl() && !pe.getUrl().isEmpty() && pe.getOrderStage()<10000000)
									order_stage_no_date++;
							}
							if(order_stage_no_date == episodeList.size())
								zy_stage_no_date =true;
						}
					}
				}
				
				String name = p.getName();
				if(!zy_stage_no_date && name.endsWith("\\s+\\d{4}"))
					year = name.substring(name.length()-4,name.length());
				System.out.print(" zy year:"+year);
			}
			
			for (ProgrammeSite ps : psList) {
				programmeSiteIdMap.put(ps.getSourceSite(), ps.getId());
				ProgrammeEpisodeDetailBo peDetailBo = getSiteEpisodeDetailBo(ps);
				List<ProgrammeEpisode> episodeList = peDetailBo
						.getSiteEpisode();
				if (episodeList != null) {
					int episodeCount = 0;

					for (ProgrammeEpisode pe : episodeList) {
						episodeCount++;
						String key = getEpisodeKey(ps.getSourceSite(),
								pe.getOrderId());
						
						  //add on 2011.10.15 orderid 值为日期时，会造成内存不够用
						  if(p.getCate() == Constants.VARIETY_CATE_ID) { 
							  key = getEpisodeKey(ps.getSourceSite(), episodeCount); 
						  }

						String date = "";
						String o_year ="";
						if (!zy_stage_no_date && p.getCate() == Constants.VARIETY_CATE_ID) {
							if (pe.getOrderStage() > 10000000){
								date=(pe.getOrderStage()+"").substring(4,8);
								o_year=(pe.getOrderStage()+"").substring(0,4);
								date = year + date;
								key = "site"
										+ ps.getSourceSite()
										+ "date"
										+ date;
							}
							else
								System.out.println(" out of 8:" + pe.getId()
										+ "");
						}
						if(year.equals(o_year))
							episodeSiteMap.put(key, pe);
					}
				}

				if (maxOrder < peDetailBo.getOrderId()) {
					maxOrder = peDetailBo.getOrderId();
					maxOrderSite = ps.getSourceSite();
				}

				if (maxEpisodeCount < episodeList.size()) {
					maxEpisodeCount = episodeList.size();
				}
			}

			List<ProgrammeEpisodeDetailBo> detailBoList = new ArrayList<ProgrammeEpisodeDetailBo>();
			List<ProgrammeEpisodeDetailBo> detailBoListOrderStageZero = new ArrayList<ProgrammeEpisodeDetailBo>(); // 单独处理新添加的order_stage是0的剧集，保证这些剧集出现在最前面
			Map<Integer, String> siteMap = SiteService.getEpisodeDetailMap();
			int episodeCollected = 0;
			int episodeTotal = p.getEpisodeTotal();

			// add on 2011.10.15 orderid 值为日期时，会造成内存不够用

			if (p.getCate() == Constants.VARIETY_CATE_ID) {
				if(!zy_stage_no_date)
					maxOrder = 12 * 32;// maxEpisodeCount;
				else
					maxOrder = maxEpisodeCount;
			}

			for (int i = Math.max(maxOrder, episodeTotal); i > 0; i--) {
				ProgrammeEpisodeDetailBo peDetailBo = new ProgrammeEpisodeDetailBo();
				peDetailBo.setOrderId(i);
				peDetailBo.setCate(p.getCate());
				List<ProgrammeEpisode> peList = new ArrayList<ProgrammeEpisode>();
				boolean hasEpisode = false;
				for (int siteId : siteMap.keySet()) {
					String key = getEpisodeKey(siteId, i);
					if (!zy_stage_no_date && p.getCate() == Constants.VARIETY_CATE_ID) {
						String date = getDate(i,p.getCate());
						date = year + date;
						key = "site" + siteId + "date" + date;
					}
					ProgrammeEpisode pe = episodeSiteMap.get(key);
					if (pe == null) {
						pe = new ProgrammeEpisode();
						// ProgrammeSite ps =
						// ProgrammeSiteService.getSite(programmeId, siteId);
						Integer programmeSiteId = programmeSiteIdMap
								.get(siteId);
						if (programmeSiteId == null) {
							pe.setFkProgrammeSiteId(-1);
						} else {
							pe.setFkProgrammeSiteId(programmeSiteId);
						}
						pe.setOrderId(i);
						pe.setId(-1);
					} else {
						// if (peDetailBo.getOrderStage() <= 0 && siteId ==
						// Constants.YOUKU_SITE_ID) {
						peDetailBo.setOrderStage(pe.getOrderStage());
						// }
						pe.setOrderId(i);
					}
					if (!StringUtils.isBlank(pe.getUrl())) {
						hasEpisode = true;
					}

					pe.setViewOrder(siteId); // 页面中用不到vieworder, 当做siteId传递到页面

					peList.add(pe);
				}
				if (hasEpisode) {
					episodeCollected++;
				}

				peDetailBo.setSiteEpisode(peList);

				if (p.getCate() == Constants.VARIETY_CATE_ID) {
					// if (peDetailBo.getOrderStage() == 0) {
					// detailBoListOrderStageZero.add(peDetailBo);
					// } else {
					detailBoList.add(peDetailBo);
					// }
				} else {
					detailBoList.add(peDetailBo);
				}
			}

			if (p.getCate() == Constants.VARIETY_CATE_ID) {
				Collections.sort(detailBoList,
						new Comparator<ProgrammeEpisodeDetailBo>() {
							@Override
							public int compare(ProgrammeEpisodeDetailBo o1,
									ProgrammeEpisodeDetailBo o2) {
								return o1.getOrderId() - o2.getOrderId();
							}
						});

				// if (detailBoListOrderStageZero != null
				// && !detailBoListOrderStageZero.isEmpty()) {
				// detailBoList.addAll(0, detailBoListOrderStageZero);
				// }
			}

			String programmDetail = p.getName()
					+ "   总集数   "
					+ ((p.getEpisodeTotal() == 0) ? "未知" : p.getEpisodeTotal()
							+ "集") + "   已收录    " + episodeCollected + "集";
			setProgrammDetail(programmDetail);
			// 分页
			PageInfo pageInfo = new PageInfo();
			if(zy_stage_no_date || p.getCate()!=Constants.VARIETY_CATE_ID)
				pageInfo.setPageSize(EPISODE_PAGE_SIZE);
			else
				pageInfo.setPageSize(32);
			pageInfo.setTotalRecords(detailBoList.size());
			pageInfo.setTotalPageNumber((int) Math.ceil((double) detailBoList
					.size() / pageInfo.getPageSize()));
			if (getPageNumber() == 0) {
				if(!zy_stage_no_date && p.getCate()==Constants.VARIETY_CATE_ID){
					int month = Calendar.getInstance().get(Calendar.MONTH)+1;
					pageInfo.setCurrentPageNumber(month);
				}else
					pageInfo.setCurrentPageNumber(1);
			} else {
				pageInfo.setCurrentPageNumber(getPageNumber());
			}
			List<ProgrammeEpisodeDetailBo> pageResult = pageInfo
					.pagingList(detailBoList);
			log.info("pageResult" + pageResult.size());
			pageInfo.setResults(pageResult);
			setEpisodeTotal(detailBoList.size());
			setPageInfo(pageInfo);
			setMaxOrder(maxOrder);
			setMaxOrderSite(maxOrderSite);

			if (p.getEpisodeTotal() == 0) { // 只有未收录完的剧集才能更改集数
				setCanAddEpisodeSize(1);
			}
			setCate(p.getCate());
			return Constants.LIST;
		} catch (NoRowsException e) {
			e.printStackTrace();
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			e.printStackTrace();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		return Constants.DETAIL;
	}

	private ProgrammeEpisodeDetailBo getSiteEpisodeDetailBo(
			ProgrammeSite programmeSite) throws Exception {

		Programme tele = ProgrammePeer.retrieveByPK(programmeSite
				.getFkProgrammeId());

		setProgrammeId(tele.getId());

		if (getSiteFilter() > 0) {
			programmeSite = ProgrammeSiteService.getSite(tele.getId(),
					getSiteFilter());
			if (programmeSite == null) {
				return null;
			}
			setProgrammeSiteId(programmeSite.getId());
		}
		List<ProgrammeEpisode> rawProgrammeEpisodeList = ProgrammeEpisodeService
				.findProgrammeEpisode(programmeSite.getId());

		// 优酷站点的剧集优先显示有版权的
		Map<Integer, ProgrammeEpisode> epMap = new LinkedHashMap<Integer, ProgrammeEpisode>();

		int maxOrder = 0;
		for (ProgrammeEpisode te : rawProgrammeEpisodeList) {
			if (te.getOrderId() > maxOrder) {
				maxOrder = te.getOrderId();
			}
			if (!ImageUtil.isYoukuImage(te.getLogo())) {
				te.setLogo(ImageUtil.getDisplayUrl(te.getLogo()));
			}

			ProgrammeEpisode teInMap = epMap.get(te.getOrderId());

			if (teInMap == null) {
				epMap.put(te.getOrderId(), te);
			}

			if (te != null && ManageUtil.isYoukuRight(te.getSource())
					&& !StringUtils.isBlank(te.getUrl())) {
				epMap.put(te.getOrderId(), te);
			}
		}

		List<ProgrammeEpisode> programmeEpisodeList = new ArrayList<ProgrammeEpisode>(
				epMap.values());

		if (tele.getCate() != Constants.VARIETY_CATE_ID) {
			int episodeTotal = tele.getEpisodeTotal();
			int episodeCollected = programmeSite.getEpisodeCollected();
			if (programmeEpisodeList.size() < episodeTotal) {
				List<ProgrammeEpisode> allTelplayList = new ArrayList<ProgrammeEpisode>();
				for (int i = 0; i <= Math.max(episodeTotal, maxOrder); i++) {
					ProgrammeEpisode te = new ProgrammeEpisode();
					te.setOrderId(i);
					allTelplayList.add(te);
				}

				for (ProgrammeEpisode te : programmeEpisodeList) {
					allTelplayList.set(te.getOrderId(), te);
				}
				allTelplayList.remove(0);
				programmeEpisodeList = allTelplayList;
			}

			if (episodeTotal == 0) {

				maxOrder = 0;
				for (ProgrammeEpisode te : programmeEpisodeList) {
					if (te.getOrderId() > maxOrder) {
						maxOrder = te.getOrderId();
					}
				}

				List<ProgrammeEpisode> allTelplayList = new ArrayList<ProgrammeEpisode>();
				for (int i = 0; i <= Math.max(episodeCollected, maxOrder); i++) {
					ProgrammeEpisode te = new ProgrammeEpisode();
					te.setOrderId(i);
					allTelplayList.add(te);
				}

				for (ProgrammeEpisode te : programmeEpisodeList) {
					allTelplayList.set(te.getOrderId(), te);
				}
				allTelplayList.remove(0);
				programmeEpisodeList = allTelplayList;
			}
		} /*
		 * else { maxOrder = programmeEpisodeList.size(); }
		 */

		ProgrammeEpisodeDetailBo episodeDetailBo = new ProgrammeEpisodeDetailBo();
		episodeDetailBo.setOrderId(maxOrder);
		episodeDetailBo.setSiteEpisode(programmeEpisodeList);
		return episodeDetailBo;
	}

	// 增加一集
	public String addEpisodeSize() throws Exception {
		ProgrammeSite ps = ProgrammeSiteService.getSite(getProgrammeId(),
				getSourceSiteId());
		ProgrammeEpisode pe = new ProgrammeEpisode();
		pe.setFkProgrammeSiteId(ps.getId());
		pe.setOrderId(getMaxOrder() + 1);
		pe.setSource(Constants.ADD_URL_SOURCE);
		pe.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
		pe.save();

		UserOperationService.logChangeProgrammEpisode(
				Constants.USER_OPERATION_ADD, getUserName(), pe, pe.toString());

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"status\":\"success\"}");
		return null;
	}

	private String getEpisodeKey(int sourceSite, int orderId) {
		return "source_site" + sourceSite + "order_id" + orderId;
	}

	/**
	 * update or insert the programme episode
	 */
	public String save() throws Exception {

		String status = "success";
		String newId = "0";

		String regex = "^http(.*)";
		try {
			if (getUrl() != null
					&& ("".equals(getUrl()) || Pattern.matches(regex, getUrl()))) {
				newId = saveEpisode(getId(), getUrl());
			} else {
				status = "fail";
			}
		} catch (SQLException e) {
			status = "fail";
			throw e;
		}
		if (newId.equals("-1"))
			status = "unique";

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"status\":\"" + status + "\", \"newId\":\"" + newId
				+ "\"}");
		return null;
	}

	private String saveEpisode(int id, String url) throws Exception {

		ProgrammeSite ps = ProgrammeSiteService.getSite(getProgrammeId(),
				getSourceSiteId());

		if (ps == null) {
			ps = new ProgrammeSite();
			ps.setFkProgrammeId(getProgrammeId());
			ps.setSourceSite(getSourceSiteId());
			ps.setCreateTime(new Date());
			ps.setUpdateTime(new Date());
			ps.save();
			UserOperationService.logChangeProgrammeSite(
					Constants.USER_OPERATION_ADD, getUserName(), ps, "");
		}

		if (getProgrammeSiteId() == 0) {
			setProgrammeSiteId(ps.getId());
		}

		// 保存时 检查是否有相同order_stage的剧集，若存在则提示order_stage唯一错误
		/*
		 * Criteria crit = new Criteria();
		 * crit.add(ProgrammeEpisodePeer.FK_PROGRAMME_SITE_ID,ps.getId());
		 * crit.add(ProgrammeEpisodePeer.ORDER_STAGE,getOrderStage());
		 * crit.add(ProgrammeEpisodePeer.URL,(Object)null,Criteria.NOT_EQUAL);
		 * crit.add(ProgrammeEpisodePeer.URL,(Object)"",Criteria.NOT_EQUAL);
		 * List<ProgrammeEpisode> checkPeList =
		 * ProgrammeEpisodePeer.doSelect(crit);
		 * if(null!=url&&!url.isEmpty()&&null!=checkPeList &&
		 * checkPeList.size()>0) return "-1";
		 */

		String newId = "0";
		if (id > 0) {
			ProgrammeEpisode te = ProgrammeEpisodePeer.retrieveByPK(id);
			log.info("Operator: " + getUserName());
			log.info("Update Programme Episode at: "
					+ formatLogDate(new Date()));
			String updateDetail = "before update: " + te;
			te.setUrl(url);
			ProgrammeEpisodeService.updateProgrammeEpisodeContent(te,
					ps.getSourceSite());
			te.setUpdateTime(new Date());
			if (url == null || url.equals("")) {
				// te.setSource(8);
				te.setLogo("NA");
				te.setSeconds(0);
			} else {
				te.setSource(Constants.ADD_URL_SOURCE);

				te.setSeconds(getDoubleSeconds(getSeconds()));
				setEpisodeDetail(te);
			}

			if (getOrderStage() > 0) {
				te.setOrderStage(getOrderStage());
			}

			ProgrammeEpisodePeer.doUpdate(te);
			updateDetail += "after update: +" + te;
			log.info(updateDetail);
			UserOperationService.logChangeProgrammEpisode(
					Constants.USER_OPERATION_UPDATE, getUserName(), te,
					updateDetail);

			newId = te.getId() + "";
			ProgrammeSiteService.updateEpisodeCollected(getProgrammeSiteId());
		} else {
			log.debug("save a new programme episode");
			String title = ProgrammeEpisodeService.getProgramme(
					getProgrammeSiteId()).getName()
					+ " " + getOrderId();
			ProgrammeEpisode te = ProgrammeEpisodeService
					.getUniqueProgrammeEpisode(getProgrammeSiteId(),
							getOrderId());
			if (te == null) {
				te = new ProgrammeEpisode();
			}
			te.setUrl(url);
			ProgrammeEpisodeService.updateProgrammeEpisodeContent(te,
					ps.getSourceSite());
			te.setFkProgrammeSiteId(getProgrammeSiteId());
			te.setTitle(title);
			te.setOrderId(getOrderId());
			te.setOrderStage(getOrderStage());
			te.setViewOrder(Constants.VIEW_ORDER_NOT_YOUKU);
			te.setSeconds(getDoubleSeconds(getSeconds()));
			te.setCreateTime(new Date());
			te.setUpdateTime(new Date());
			if (url == null || url.equals("")) {
				// te.setSource(8);
			} else {
				te.setSource(Constants.ADD_URL_SOURCE);
			}
			setEpisodeDetail(te);
			log.info("Operator: " + getUserName());
			log.info("Save Programme Episode at: " + formatLogDate(new Date()));
			log.info(te);
			te.save();
			UserOperationService.logChangeProgrammEpisode(
					Constants.USER_OPERATION_ADD, getUserName(), te,
					te.toString());
			newId = te.getId() + "";
			ProgrammeSiteService.updateEpisodeCollected(getProgrammeSiteId());
		}

		return newId;
	}

	public String getDate(int i, int cate) {
		if (cate != Constants.VARIETY_CATE_ID)
			return i + "";
		int month = i / 32 + 1;
		int day = i % 32;
		String mon = month > 9 ? "" + month : "0" + month;
		String d = day > 9 ? "" + day : "0" + day;
		return mon + d;
	}

	private double getDoubleSeconds(String seconds) {
		log.info("seconds: " + seconds);
		try {
			return Double.valueOf(seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	/** get the episode detail with url */
	public String searchProgrammeDetail() throws Exception {

		ProgrammeEpisode te = new ProgrammeEpisode();
		te.setUrl(getUrl());

		ProgrammeEpisodeService.updateProgrammeEpisodeContent(te,
				getSourceSiteId());

		te.setLogo(ImageUtil.getDisplayUrl(te.getLogo()));
		JSONObject json = new JSONObject(te);

		log.debug(json.toString());

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(json.toString());
		return null;
	}

	/** get the episode detail with id */
	public String getProgrammeDetail() throws Exception {

		log.info(getEpisodeId());
		ProgrammeEpisode te = ProgrammeEpisodePeer.retrieveByPK(getEpisodeId());

		te.setLogo(ImageUtil.getDisplayUrl(te.getLogo()));
		JSONObject json = new JSONObject(te);
		log.info(json.toString());

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(json.toString());
		return null;
	}

	private void setEpisodeDetail(ProgrammeEpisode te) {
		String logoUrl = getLogoUrl();
		int seconds = formatSeconds(getSeconds());
		int hd = getHd();
		if (StringUtil.isNotNull(logoUrl)) {
			te.setLogo(logoUrl);
		}

		if (seconds > 0) {
			te.setSeconds(seconds);
		}

		te.setHd(hd);
	}

	/** add the programme episode to the blacklist */
	public String addToBlacklist() throws Exception {
		String status = "success";

		try {
			if (getId() > 0) {
				ProgrammeEpisode te = ProgrammeEpisodePeer
						.retrieveByPK(getId());
				Programme p = ProgrammeEpisodeService.getProgramme(te
						.getFkProgrammeSiteId());
				Blacklist bl = new Blacklist();
				bl.setCate(p.getCate());
				bl.setFkProgrammeId(p.getId());
				bl.setUrl(te.getUrl());
				bl.setCreateTime(new Date());
				bl.setUpdateTime(new Date());
				bl.save();

				log.info("Operator: " + getUserName());
				log.info("Add to blackList at: " + formatLogDate(new Date()));
				log.info("Save: " + bl);
				UserOperationService.logChangeBlackList(
						Constants.USER_OPERATION_ADD, getUserName(), bl);
			}
		} catch (Exception e) {
			status = "fail";
			e.printStackTrace();
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"status\":\"" + status + "\"}");
		return null;
	}

	/** lock and unlock the programmeEpisode */
	/*
	 * public String changeLockStatus() throws Exception { String status =
	 * "success"; String operation = ""; try { if(getId() > 0) {
	 * ProgrammeEpisode te = ProgrammeEpisodePeer.retrieveByPK(getId());
	 * if(te.getLocked() == 0) { te.setLocked(1); operation = "lock"; } else {
	 * te.setLocked(0); operation = "unlock"; }
	 * ProgrammeEpisodePeer.doUpdate(te); } } catch (Exception e) { status =
	 * "fail"; e.printStackTrace(); }
	 * 
	 * HttpServletResponse response = ServletActionContext.getResponse();
	 * response.setContentType("text/html;charset=utf-8"); PrintWriter out =
	 * response.getWriter(); out.print("{\"status\":\"" + status +
	 * "\", \"operation\":\"" + operation + "\"}"); return null; }
	 */

	public String lockSite() throws Exception {
		String status = "success";

		ProgrammeSite oldTsv = ProgrammeSitePeer
				.retrieveByPK(getProgrammeSiteId());
		log.info("Operator: " + getUserName());
		log.info("Update Programme Site  at " + formatLogDate(new Date()));
		log.info("Object before update" + oldTsv);
		oldTsv.setEpisodeCollected(getEpisodeCollected());
		log.info("Object after update" + oldTsv);
		ProgrammeSitePeer.doUpdate(oldTsv);

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print("{\"status\":\"" + status + "\"}");
		return null;
	}

	/** change programme episode collected */
	public String updateEpisodeCollected() throws Exception {

		ProgrammeSite oldTsv = ProgrammeSitePeer
				.retrieveByPK(getProgrammeSiteId());
		log.info("Operator: " + getUserName());
		log.info("Update Programme Site  at " + formatLogDate(new Date()));
		log.info("Object before update" + oldTsv);
		oldTsv.setEpisodeCollected(getEpisodeCollected());
		log.info("Object after update" + oldTsv);
		ProgrammeSitePeer.doUpdate(oldTsv);

		Programme tele = ProgrammePeer.retrieveByPK(oldTsv.getFkProgrammeId());
		if (tele.getEpisodeTotal() != 0) {
			log.info("Operator: " + getUserName());
			log.info("Update Programme  EpisodeTotal at "
					+ formatLogDate(new Date()));
			log.info("Object before update" + tele);
			tele.setEpisodeTotal(getEpisodeCollected());
			log.info("Object after update" + tele);
			ProgrammePeer.doUpdate(tele);
		}

		return SUCCESS;
	}

	/**
	 * Search the programme episode
	 * 
	 * @deprecated
	 * @return
	 * @throws Exception
	 * 
	 */
	/*
	 * public String searchProgramme() throws Exception { if (getOnlySave() !=
	 * null && getOnlySave().equals("onlySave")) { // Save all episode
	 * List<ProgrammeEpisode> el = getProgrammeEpisodeList(); for
	 * (ProgrammeEpisode e : el) { if (e == null) { continue; }
	 * 
	 * if (e.getId() > 0) { ProgrammeEpisode te =
	 * ProgrammeEpisodePeer.retrieveByPK(e .getId()); log.info("Operator: " +
	 * getUserName()); log.info("Update Programme Episode at: " +
	 * formatLogDate(new Date())); log.info("before update: " + te);
	 * 
	 * te.setUrl(e.getUrl()); te.setHd(e.getHd());
	 * te.setSeconds(e.getSeconds()); te.setLogo(e.getLogo());
	 * 
	 * if (e.getUrl() == null || e.getUrl().equals("")) { // te.setSource(8); }
	 * else { te.setSource(Constants.ADD_URL_SOURCE); }
	 * ProgrammeEpisodePeer.doUpdate(te); log.info("after update: +" + te); }
	 * else { log.debug("save a new programme episode"); String title =
	 * ProgrammeEpisodeService.getProgramme( getProgrammeSiteId()).getName() +
	 * " " + getOrderId(); ProgrammeEpisode te = new ProgrammeEpisode();
	 * te.setUrl(e.getUrl()); te.setFkProgrammeSiteId(getProgrammeSiteId());
	 * te.setTitle(title); te.setOrderId(e.getOrderId()); te.setHd(e.getHd());
	 * te.setSeconds(e.getSeconds()); te.setLogo(e.getLogo());
	 * te.setCreateTime(new Date()); te.setUpdateTime(new Date()); if (url ==
	 * null || url.equals("")) { // te.setSource(8); } else {
	 * te.setSource(Constants.ADD_URL_SOURCE); } log
	 * .debug("the id of the episode before persistent to db is: " +
	 * te.getId()); log.info("Operator: " + getUserName());
	 * log.info("Save Programme Episode at: " + formatLogDate(new Date()));
	 * log.info(te); log
	 * .debug("the id of the episode before persistent to db is: " +
	 * te.getId()); te.save(); log
	 * .debug("the id of the episode after persistent to db is: " + te.getId());
	 * } }
	 * 
	 * OperationLogService.log(getUserName(),
	 * OperationLogService.OPERATION_CREATE, TABNAME,
	 * "Save All Episode Site Versio Id: " + getProgrammeSiteId()); return
	 * SUCCESS;
	 * 
	 * }
	 * 
	 * OperationLogService.log(getUserName(),
	 * OperationLogService.OPERATION_CREATE, TABNAME,
	 * "Search All Episode Site Versio Id: " + getProgrammeSiteId());
	 * List<Episode> es = serarchProgramme();
	 * 
	 * 
	 * JSONArray jsonArray = new JSONArray(); for(int i = 0; i < es.size(); i++)
	 * { JSONObject json = new JSONObject(es.get(i)); jsonArray.put(i, json); }
	 * 
	 * 
	 * log.debug(jsonArray.toString());
	 * 
	 * HttpServletResponse response = ServletActionContext.getResponse();
	 * response.setContentType("text/html;charset=utf-8"); PrintWriter out =
	 * response.getWriter(); out.print(jsonArray.toString());
	 * 
	 * return SUCCESS; }
	 *//** Search the programme episode */
	/*
	 * public String searchProgrammeAjax() throws Exception { List<Episode> es =
	 * serarchProgramme();
	 * 
	 * JSONArray jsonArray = new JSONArray(); for (int i = 0; i < es.size();
	 * i++) { JSONObject json = new JSONObject(es.get(i)); jsonArray.put(i,
	 * json); }
	 * 
	 * log.debug(jsonArray.toString());
	 * 
	 * HttpServletResponse response = ServletActionContext.getResponse();
	 * response.setContentType("text/html;charset=utf-8"); PrintWriter out =
	 * response.getWriter(); out.print(jsonArray.toString()); return null; }
	 * 
	 * private List<Episode> serarchProgramme() throws Exception { //
	 * ProgrammeSite site = //
	 * ProgrammeSitePeer.retrieveByPK(getProgrammeSiteId()); // Programme
	 * version = // ProgrammePeer.retrieveByPK(site.getFkProgrammeId()); //
	 * Names names = NamesPeer.retrieveByPK(version.getFkNamesId()); // //
	 * List<String> ns = new ArrayList<String>(); // List<String> vs = new
	 * ArrayList<String>(); // // ns.add(names.getName()); //
	 * if(names.getAlias() != null && !names.getAlias().equals("")) { //
	 * ns.addAll(Utils.parseStr2List(names.getAlias(), "\\|")); // } // // //
	 * if(version.getName() != null) { // vs.add(version.getName()); // } //
	 * if(version.getAlias() != null && !version.getAlias().equals("")) { //
	 * vs.addAll(Utils.parseStr2List(version.getAlias(), "\\|")); // } // // //
	 * Parameter param = new Parameter(); //
	 * param.setCate(Constants.CATEGORY_TELEPLAY); //
	 * param.setSite(site.getSourceSite()); // param.setYxSite(getYxSite()); //
	 * param.setNs(ns); // param.setVs(vs); // if(site.getSourceSite() ==
	 * Constants.INTEGRATED_SITE_ID) { // param.setFid(version.getId()); // } //
	 * // // Result rs = null; // Search s = new Search(); // if(getOrderId() >
	 * 0) { // List<Integer> orders = new ArrayList<Integer>(); //
	 * orders.add(getOrderId()); // param.setOrders(orders); //
	 * param.setTotal(version.getEpisodeTotal()); // rs = s.search(param); // }
	 * else { // param.setTotal(version.getEpisodeTotal()); // rs =
	 * s.search(param); // } // // log.debug(param); // log.debug(rs); //
	 * System.out.println(param); // System.out.println(rs); // List<Episode> es
	 * = rs.getEs(); // // for(Episode e : es) { // String newId = "0"; //
	 * System.out.println("site.getId()" + site.getId()); //
	 * System.out.println("e.getOrder_id()" + e.getOrder_id()); //
	 * List<ProgrammeEpisode> teList = //
	 * ProgrammeEpisodeService.findProgrammeEpisode(site.getId(), //
	 * e.getOrder_id()); // if(teList != null && teList.size() > 0) { //
	 * ProgrammeEpisode teleEp = teList.get(0); // if(teleEp.getLocked() == 0 ||
	 * teleEp.getUrl() == null || // "".equals(teleEp.getUrl())) { //
	 * System.out.println("URL" + e.getUrl()); // log.info("Operator: " +
	 * getUserName()); // log.info("Update Programme Episode at: " +
	 * formatLogDate(new // Date())); // log.info("before update: " + teleEp);
	 * // teleEp.setUrl(e.getUrl()); // teleEp.setLogo(e.getLogo()); //
	 * teleEp.setSeconds(e.getSeconds()); // teleEp.setOrderId(e.getOrder_id());
	 * // teleEp.setTitle(e.getTitle()); // teleEp.setHd(e.isHd() ? 1 : 0); //
	 * teleEp.setUpdateTime(new Date()); // teleEp.setSource(9); //
	 * ProgrammeEpisodePeer.doUpdate(teleEp); // log.info("after update: " +
	 * teleEp); // newId = teleEp.getId() + ""; //
	 * ProgrammeSiteService.updateEpisodeCollected(getProgrammeSiteId()); // }
	 * // // // // } else { // ProgrammeEpisode te = new ProgrammeEpisode(); //
	 * // System.out.println("URL" + e.getUrl()); //
	 * System.out.println("getSourceSiteId()" + getSourceSiteId()); //
	 * te.setUrl(e.getUrl()); // //
	 * ProgrammeEpisodeService.updateProgrammeEpisodeContent(te, //
	 * getSourceSiteId()); // te.setFkProgrammeSiteId(getProgrammeSiteId()); //
	 * te.setTitle(e.getTitle()); // te.setOrderId(e.getOrder_id()); //
	 * te.setLogo(e.getLogo()); // te.setHd(e.isHd() ? 1 : 0); //
	 * te.setCreateTime(new Date()); // te.setUpdateTime(new Date()); //
	 * te.setSeconds(e.getSeconds()); // te.setLocked(e.isLocked() ? 1 : 0); //
	 * te.setSource(9); //
	 * log.debug("the id of the episode before persistent to db is: " + //
	 * te.getId()); // log.info("Operator: " + getUserName()); //
	 * log.info("Save Programme Episode at: " + formatLogDate(new Date())); //
	 * log.info(te); // te.save(); //
	 * log.debug("the id of the episode after persistent to db is: " + //
	 * te.getId()); // newId = te.getId() + ""; //
	 * ProgrammeSiteService.updateEpisodeCollected(getProgrammeSiteId()); // }
	 * // } // // return es; return null; }
	 */

	private int formatSeconds(String seconds) {
		if (seconds != null && !seconds.equals("") && seconds.indexOf(":") > 0) {
			String[] strs = seconds.split(":");
			return Integer.valueOf(strs[0]) * 60 + Integer.valueOf(strs[1]);
		} else {
			return 0;
		}
	}

	// syn programe_id for programme_episode
	public String synProgrammeId() {
		ProgrammeEpisodeService.synProgrammeIdForProgrammeEpisode(log);
		return "syn";
	}

	private int programmeSiteId;

	private int programmeId;

	public int getProgrammeId() {
		return programmeId;
	}

	public void setProgrammeId(int programmeId) {
		this.programmeId = programmeId;
	}

	public int getProgrammeSiteId() {
		return programmeSiteId;
	}

	public void setProgrammeSiteId(int programmeSiteId) {
		this.programmeSiteId = programmeSiteId;
	}

	private List<ProgrammeEpisode> programmeEpisodeList;

	public List<ProgrammeEpisode> getProgrammeEpisodeList() {
		return programmeEpisodeList;
	}

	public void setProgrammeEpisodeList(
			List<ProgrammeEpisode> programmeEpisodeList) {
		this.programmeEpisodeList = programmeEpisodeList;
	}

	private String programmeNames;

	public String getProgrammeNames() {
		return programmeNames;
	}

	public void setProgrammeNames(String programmeNames) {
		this.programmeNames = programmeNames;
	}

	private int id;

	private String url;

	private int sourceSiteId;

	private int orderId;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getSourceSiteId() {
		return sourceSiteId;
	}

	public void setSourceSiteId(int sourceSiteId) {
		this.sourceSiteId = sourceSiteId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	int episodeCollected;

	public int getEpisodeCollected() {
		return episodeCollected;
	}

	public void setEpisodeCollected(int episodeCollected) {
		this.episodeCollected = episodeCollected;
	}

	public Map<Integer, String> getSitesFilterMap() {
		return SiteService.getSitesMap();
	}

	private int siteFilter;

	public int getSiteFilter() {
		return siteFilter;
	}

	public void setSiteFilter(int siteFilter) {
		this.siteFilter = siteFilter;
	}

	private List<Integer> batchIds;

	private List<String> batchUrls;

	private String onlySave;

	private String seconds;

	private String logoUrl;

	private int hd;

	private int yxSite;

	private int pageNumber;

	private PageInfo pageInfo;

	private int episodeId;

	private String programmDetail;

	private int maxOrder;

	private int maxOrderSite;

	private int cate;

	private int canAddEpisodeSize;

	private int orderStage;

	public int getOrderStage() {
		return orderStage;
	}

	public void setOrderStage(int orderStage) {
		this.orderStage = orderStage;
	}

	public int getCate() {
		return cate;
	}

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getCanAddEpisodeSize() {
		return canAddEpisodeSize;
	}

	public void setCanAddEpisodeSize(int canAddEpisodeSize) {
		this.canAddEpisodeSize = canAddEpisodeSize;
	}

	public int getMaxOrderSite() {
		return maxOrderSite;
	}

	public void setMaxOrderSite(int maxOrderSite) {
		this.maxOrderSite = maxOrderSite;
	}

	public int getMaxOrder() {
		return maxOrder;
	}

	public void setMaxOrder(int maxOrder) {
		this.maxOrder = maxOrder;
	}

	public String getProgrammDetail() {
		return programmDetail;
	}

	public void setProgrammDetail(String programmDetail) {
		this.programmDetail = programmDetail;
	}

	public int getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(int episodeId) {
		this.episodeId = episodeId;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getYxSite() {
		return yxSite;
	}

	public void setYxSite(int yxSite) {
		this.yxSite = yxSite;
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public int getHd() {
		return hd;
	}

	public void setHd(int hd) {
		this.hd = hd;
	}

	public List<Integer> getBatchIds() {
		return batchIds;
	}

	public void setBatchIds(List<Integer> batchIds) {
		this.batchIds = batchIds;
	}

	public List<String> getBatchUrls() {
		return batchUrls;
	}

	public void setBatchUrls(List<String> batchUrls) {
		this.batchUrls = batchUrls;
	}

	public String getOnlySave() {
		return onlySave;
	}

	public void setOnlySave(String onlySave) {
		this.onlySave = onlySave;
	}

	private int episodeTotal;

	public int getEpisodeTotal() {
		return episodeTotal;
	}

	public void setEpisodeTotal(int episodeTotal) {
		this.episodeTotal = episodeTotal;
	}

}
