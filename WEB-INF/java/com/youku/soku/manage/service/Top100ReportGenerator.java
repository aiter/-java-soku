package com.youku.soku.manage.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.VideoInfoBo;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.top.directory.TopDate;
import com.youku.soku.top.directory.TopDateManager;

public class Top100ReportGenerator {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public File generateFile(int cate) {
		String fileName = "/tmp/teleplay.txt";
		if(cate == Constants.MOVIE_CATE_ID) {
			fileName = "/tmp/movie.txt";
		}
		
		try {
			File file = new File(fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String content = loadTop100Data(cate);
			writer.write(content);
			writer.flush();
			writer.close();
			
			return file;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public String loadTop100Data(int cate) {

		int versionNo=1;
		TopDateManager.initTopDate();
		TopDate top_date = TopDateManager.getDate(TopDateManager.ZHIDAQU);
		
		String date = top_date.getTopDate();
		versionNo=top_date.getVersion();		
		
		logger.info("the date:"+date+"  the versionNo:"+versionNo);

		StringBuffer sql = new StringBuffer(
				"select distinct  ri.fk_programme_id, query_count from rankinfo_"+date+" ri  ");
		
		StringBuffer countBf=new StringBuffer("select count(distinct fk_programme_id) from rankinfo_"+date+" ri ");
		
		sql.append(" where ri.fk_programme_id>0 and  ri.fk_cate_id=" + cate);
		countBf.append(" where ri.fk_programme_id>0 and ri.fk_cate_id=" + cate);
		
		
		sql.append(" and ri.version_no="+versionNo);
		countBf.append(" and ri.version_no="+versionNo);

		sql.append(" order by ri.query_count desc limit 100");
		
		logger.info("the sql:"+sql);
		

		Map<Integer, String> reportSite = getReportSite();
		StringBuilder builder = new StringBuilder();
		if(cate == Constants.TELEPLAY_CATE_ID) {
			builder.append("电视剧排行榜").append("\t");
		} else if(cate == Constants.MOVIE_CATE_ID) {
			builder.append("电影排行榜").append("\t");
		}
		builder.append("搜索量").append("\t");
		builder.append("是否有版权").append("\t");
		for(int key : reportSite.keySet()) {
			builder.append(reportSite.get(key)).append("\t");
		}
		builder.append("\n");
		try {
			List<Record> records = BasePeer.executeQuery(sql.toString(), "new_soku_top");
			for (Record recode : records) {
				int programmeId = recode.getValue("ri.fk_programme_id").asInt();
				Programme programme = ProgrammePeer.retrieveByPK(programmeId);
				boolean isHaveRight = getProgrammeCopyRightFlag(programme);
				
				Criteria crit = new Criteria();
				crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, programmeId);
				List<ProgrammeSite> psList = ProgrammeSitePeer.doSelect(crit);
				
				builder.append(programme.getName()).append("\t");
				builder.append(recode.getValue("query_count").asInt()).append("\t");
				builder.append(isHaveRight ? "有" : "无").append("\t");
				if(psList != null ) {
					for(int key : reportSite.keySet()) {
						int episodeCollected = 0;
						for(ProgrammeSite ps : psList) {
							if(ps.getSourceSite() == key) {
								episodeCollected = ps.getEpisodeCollected();
							}
						}
						builder.append(episodeCollected > 0 ? "有剧集" : "").append("\t");
					}
				}
				builder.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 查询排行榜数据出现异常 ：" + sql.toString());
		}

		return builder.toString();
	
	}
	
	private boolean getProgrammeCopyRightFlag(Programme p) {
		VideoInfoBo videoInfoBo = new VideoInfoBo();
		videoInfoBo.setContentId(p.getContentId());
		VideoInfoService.videoInfoGetterFromHttp(videoInfoBo);
		
		return videoInfoBo.getHaveRight() == 1;
	}
	
	private Map<Integer, String> getReportSite() {
		Map<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
		
		siteMap.put(14, "优酷网");
		siteMap.put(1, "土豆网");
		//siteMap.put(2, "56网");
		siteMap.put(3, "新浪网");
		siteMap.put(6, "搜狐");
		//siteMap.put(15, "CNTV");
		//siteMap.put(9, "激动网");
		siteMap.put(17, "乐视网");
		siteMap.put(19, "奇艺网");
		//siteMap.put(11, "天线视频");
		
		return siteMap;
	}

}
