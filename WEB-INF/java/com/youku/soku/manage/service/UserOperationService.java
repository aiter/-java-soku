package com.youku.soku.manage.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Blacklist;
import com.youku.soku.library.load.EpisodeLog;
import com.youku.soku.library.load.EpisodeLogPeer;
import com.youku.soku.library.load.KnowledgeColumn;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammeEpisode;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.SeriesSubject;
import com.youku.soku.library.load.UserOperationLog;
import com.youku.soku.manage.common.Constants;

public class UserOperationService {
	
	private static Logger logger = Logger.getLogger(UserOperationService.class);
	
	public static void logOperateSeries(String type, String operator, Series s, String detail) throws Exception {
		String brief = "series_id:" + s.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("series");
		ul.setCreateTime(new Date());
		
		ul.save();
	}
	
	
	public static void logChangeSeries(String type, String operator, SeriesSubject ss, String detail) throws Exception {
		String brief = "programme_id:" + ss.getProgrammeId() + "series_id:" + ss.getFkSeriesId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("series_subject");
		ul.setCreateTime(new Date());
		
		ul.save();
	}
	
	
	public static void logChangeProgrammeSite(String type, String operator, ProgrammeSite ps, String detail) throws Exception {
		String brief = "programme_site_id:" + ps.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("programme_site");
		ul.setCreateTime(new Date());
		
		ul.save();
	}
	
	public static void logChangeProgrammEpisode(String type, String operator, ProgrammeEpisode pe, String detail) throws Exception {
		String brief = "programme_episode_id:" + pe.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("programme_episode");
		ul.setCreateTime(new Date());
		ul.save();
		
		logger.info("================================");
		logger.info("Log change Programme Episode");
		
		EpisodeLog el = new EpisodeLog();
		el.setFkProgrammeSiteId(pe.getFkProgrammeSiteId());
		el.setOperator(operator);
		el.setUrl(pe.getUrl());
		el.setOrderId(pe.getOrderId());
		el.setSeconds(pe.getSeconds());
		el.setSource(Constants.ADD_URL_SOURCE);
		el.setTitle(pe.getTitle());
		el.setStatus(Constants.EPISODE_LOG_EDITOR_ADDED);
		logger.info("====" + el);
		el.save();
		
	}
	
	public static void logChangeBlackList(String type, String operator, Blacklist bl) throws Exception {
		String brief = "blacklist_id:" + bl.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setTabName(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(bl.toString());
		ul.setTabName("blacklist");
		ul.setCreateTime(new Date());
		
		ul.save();
	}
	
	public static void logBlockProgramme(String type, String operator, Programme p) throws Exception {
		String brief = "programme_id:" + p.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(p.toString());
		ul.setTabName("programme");
		ul.setCreateTime(new Date());
		
		ul.save();
	}
	
	public static void logOperateKnowledgeColumn(String type, String operator, KnowledgeColumn n, String detail) throws Exception {
		String brief = "knowledge_id:" + n.getId();
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("knowledge_column");
		ul.setCreateTime(new Date());
		ul.save();
	}
	
	public static void logOperateForwardWord(String type, String operator, String detail) throws Exception {
		String brief = "forward_word: ";
		UserOperationLog ul = new UserOperationLog();
		ul.setType(type);
		ul.setOperator(operator);
		ul.setBrief(brief);
		ul.setDetail(detail);
		ul.setTabName("forward_word");
		ul.setCreateTime(new Date());
		ul.save();
	}
}
