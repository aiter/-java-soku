package com.youku.soku.manage.service;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.util.JdbcUtil;
import com.youku.search.util.Request;
import com.youku.soku.config.Config;
import com.youku.soku.library.load.Programme;
import com.youku.soku.library.load.ProgrammePeer;
import com.youku.soku.library.load.ProgrammeSite;
import com.youku.soku.library.load.ProgrammeSitePeer;
import com.youku.soku.manage.bo.ProgrammeExportBo;
import com.youku.soku.manage.bo.VideoInfoBo;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.util.LanguageAndArea;
import com.youku.soku.manage.util.SearchParameter;
import com.youku.soku.top.mapping.TopWords;
import com.youku.soku.util.DataBase;
import com.youku.top.util.TopWordType;

public class VideoInfoService {

	private static final Logger log = Logger.getLogger(VideoInfoService.class);

	private static final int CATEGORY_PAGE_SIZE = 20;

	private static final int NO_CATEGORY_PAGE_SIZE = 20;

	private static final String TABNAME_SPILTER = "|";
	
	private static final String COPY_RIGHT_AUTHORIZED = "authorized";

	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	private static String HOST = Config.getMiddleTierOfflineHost();
	private static String REQUERIED_FIELDS = "showname showalias showdesc deschead " +
						"showcategory releaseyear area performer director host show_thumburl show_vthumburl " +
						"movie_genre tv_genre anime_genre variety_genre copyright_status paid showid firstepisode_videourl";

	public static void searchVideoInfo(SearchParameter param, PageInfo pageInfo)
			throws Exception {

		List<VideoInfoBo> resultList = new ArrayList<VideoInfoBo>();


		String mainSql = buildSql(param) + " AND p.state = 'normal'";
		String likeSql = "";
		if (param.getSearchWord() != null && !param.getSearchWord().equals("")) {
			likeSql = " AND (p.name LIKE ? OR p.alias LIKE ?)";
			if (param.getStatus() == SearchParameter.WITH_SERIES) {
				likeSql = " AND (p.name LIKE ? or s.name like ?) ";
			}
		}
		
		if(param.getCategoryId() > 0) {
			mainSql += " AND p.cate = " + param.getCategoryId();
		}

		if (param.isAccuratelyMatched()) {
			param.setSearchWord(param.getSearchWord());
		} else {
			param.setSearchWord("%" + param.getSearchWord() + "%");
		}
		
		if(param.getConcernLevel() >= 0) {
			mainSql += " AND p.concern_level = " + param.getConcernLevel();
		}

		String countsql = "SELECT COUNT(*) FROM " + mainSql;
		countsql += likeSql;

		PreparedStatement pstcnt = null;
		ResultSet rscnt = null;

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			conn = DataBase.getLibraryConn();
			pstcnt = conn.prepareStatement(countsql);
			if (!likeSql.equals("")) {
				pstcnt.setString(1, param.getSearchWord());
				pstcnt.setString(2, param.getSearchWord());
				if (param.getStatus() == SearchParameter.WITH_SERIES) {
					pstcnt.setString(2, param.getSearchWord());
				}
			}
			log.info("pstcnt: " + pstcnt.toString());
			rscnt = pstcnt.executeQuery();
			int recordCount = 0;
			while (rscnt.next()) {
				recordCount = rscnt.getInt(1);
			}
			if (recordCount == 0) {
				return;
			}
			int totalPageNumber = (int) Math.ceil((double) recordCount
					/ pageInfo.getPageSize());
			pageInfo.setTotalPageNumber(totalPageNumber);
			pageInfo.setTotalRecords(recordCount);
			
			String orderByField = "p.update_time";
			if(mainSql.indexOf("programme_search_number") > -1) {
				orderByField = "s.search_number";
			}
			String sql = "SELECT * FROM " + mainSql + likeSql 
					+ " ORDER BY " + orderByField + " desc LIMIT " + pageInfo.getOffset() + ", "
					+ pageInfo.getPageSize();


			pst = conn.prepareStatement(sql);
			log.info("SQL " + sql);
			if (!likeSql.equals("")) {
				pst.setString(1, param.getSearchWord());
				pst.setString(2, param.getSearchWord());
				if (param.getStatus() == SearchParameter.WITH_SERIES) {
					pst.setString(2, param.getSearchWord());
				}
			}
			log.info("pst toString " + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				VideoInfoBo videoBo = new VideoInfoBo();
				videoBo.setId(rs.getInt("p.id"));
				videoBo.setContentId(rs.getInt("p.content_id"));
				videoBo.setName(rs.getString("p.name"));
				videoBo.setCategory(rs.getInt("p.cate"));
				videoBo.setEpisodeTotal(rs.getInt("p.episode_total"));
				videoBo.setSource(rs.getString("p.source"));
				videoBo.setBlock(rs.getInt("p.blocked"));
				videoBo.setConcernLevel(rs.getInt("p.concern_level"));
				videoBo.setCreateTime(DATEFORMAT.format(rs.getTimestamp("p.create_time")));
				videoBo.setUpdateTime(DATEFORMAT.format(rs.getTimestamp("p.update_time")));
				videoBo.setAlias(rs.getString("p.alias"));
				resultList.add(videoBo);
			}
			
			for(VideoInfoBo vb : resultList) {
				videoInfoGetter(vb);
				videoSeriesCompact(vb);
			}
			pageInfo.setResults(resultList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
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
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	private static String buildSql(SearchParameter param) {
		if (param.getStatus() == SearchParameter.WITH_SERIES) {
			return "programme p, series s, series_subject ss WHERE ss.fk_series_id = s.id AND ss.programme_id = p.id ";
		} else if (param.getStatus() == SearchParameter.SOURCE_YOUKU) {
			return "programme p WHERE p.source like '1__'";
		} else if (param.getStatus() == SearchParameter.SOURCE_OTHER_SITE) {
			return "programme p WHERE p.source like '_1_'";
		} else if (param.getStatus() == SearchParameter.SOURCE_AUTO_SEARCH) {
			return "programme p WHERE p.source like '__1'";
		} else if (param.getStatus() == SearchParameter.STATUS_COMPLETE) {
			return "programme p, programme_site ps WHERE ps.fk_programme_id = p.id "
					+ "AND ps.source_site = 100 AND ps.completed= 1";
		} else if (param.getStatus() == SearchParameter.STATUS_NOT_COMPLETE) {
			return "programme p, programme_site ps WHERE ps.fk_programme_id = p.id "
					+ "AND ps.source_site = 100 AND ps.completed= 0";
		} /*else if (param.getStatus() == SearchParameter.STATUS_NO_LOGO) {
			return "programme p, programme_site ps, programme_episode pe where ps.fk_programme_id ="
					+ " ps.id and ps.source_site = 14 and pe.fk_programme_site_id = ps.id and pe.url is not null and pe.logo = 'NA'";
		}*/ else if (param.getStatus() == SearchParameter.STATUS_BLOCKED){
			return "programme p where p.blocked = 1";
		} else {
			return "programme p, programme_search_number s WHERE s.fk_programme_id = p.id ";
		}

	}
	
	public static void videoInfoGetter(VideoInfoBo videoInfoBo) {
		try {
			videoInfoBo.setCategoryName(CategoryService.getCategoryMap().get(videoInfoBo.getCategory()));
			defaultDisplaySiteGetter(videoInfoBo);
			videoInfoGetterFromHttp(videoInfoBo);
			videoInfoBo.setBrief(buildBriefStr(videoInfoBo.getPerformers(), videoInfoBo.getArea(), videoInfoBo.getEpisodeTotal(), videoInfoBo.getShowDescription()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void videoInfoGetterFromHttp(VideoInfoBo videoInfoBo) {
		
		StringBuilder requestUrl = new StringBuilder();
		int requestTime = 0;
		try {
			requestUrl.append("http://").append(HOST).append("/show.show?q=showid:").append(videoInfoBo.getContentId()).append("&fd=")
					.append(URLEncoder.encode(REQUERIED_FIELDS, "utf-8")).append("&ft=json");
			
			String result = Request.requestGet(requestUrl.toString(), 1000);
			
			requestTime = 0;
			while(result == null && requestTime < 5) {
				result = Request.requestGet(requestUrl.toString(), 1000);
				requestTime++;
			}
			
			if(requestTime == 5 && result == null) {
				log.error("Request 5 timt, still no result!!!!");
				return;
			}
			JSONObject json = new JSONObject(result);
			JSONArray jsArr = json.optJSONArray("results");
			
			if(jsArr.length() > 0) {
				JSONObject video = jsArr.getJSONObject(0);
				videoInfoBo.setShowDescription(video.optString("showdesc"));
				videoInfoBo.setReleaseDate(video.optString("releasedate"));
				videoInfoBo.setThumb(video.optString("show_thumburl"));
				videoInfoBo.setPoster(video.optString("show_vthumburl"));
				videoInfoBo.setNameAlias(video.optString("showalias"));
				
				videoInfoBo.setPaid(video.optInt("paid"));
				
				videoInfoBo.setShowIdStr(video.optString("showid"));
				
				String firstepisodeVideourl = video.optString("firstepisode_videourl");
				videoInfoBo.setHasYoukuDetail(!StringUtils.isBlank(firstepisodeVideourl));
				
				String copyRightFlag = video.optString("copyright_status");
				if(COPY_RIGHT_AUTHORIZED.equals(copyRightFlag)) {
					videoInfoBo.setHaveRight(1);
				} else {
					videoInfoBo.setHaveRight(0);
				}
				
				JSONArray performerArr = video.optJSONArray("performer");
				JSONArray areaArr = video.optJSONArray("area");
				JSONArray directorsArr = video.optJSONArray("director");
				JSONArray hostsArr = video.optJSONArray("host");
				JSONArray genreArr = null;
				switch(videoInfoBo.getCategory()) {
				case 1:
					genreArr = video.optJSONArray("tv_genre");
					break;
				case 2:
					genreArr = video.optJSONArray("movie_genre");
					break;
				case 3:
					genreArr = video.optJSONArray("variety_genre");
					break;
				case 5:
					genreArr = video.optJSONArray("anime_genre");
					break;
				}
				
				videoInfoBo.setGenre(jsonArrayToString(genreArr));
				videoInfoBo.setDirectors(personJsonArrayToString(directorsArr, 3));
				videoInfoBo.setArea(jsonArrayToString(areaArr, 3));				
				videoInfoBo.setPerformers(personJsonArrayToString(performerArr, 3));
				videoInfoBo.setHosts(personJsonArrayToString(hostsArr, 3));
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Request Time:  " + requestTime);
		}
	}
		
	public static void videoSeriesCompact(VideoInfoBo videoInfoBo) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			String sql = "SELECT * FROM programme p, series s, series_subject ss WHERE ss.programme_id = p.id AND ss.fk_series_id = s.id AND p.id = ?";
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, videoInfoBo.getId());
			
			rs = pst.executeQuery();
			while(rs.next()) {
				videoInfoBo.setSeriesId(rs.getInt("s.id"));
				videoInfoBo.setSeriesName(rs.getString("s.name"));
				videoInfoBo.setSerialAlias(rs.getString("s.alias"));
				videoInfoBo.setSeriesOrder(rs.getInt("ss.order_id"));
			}
			/*if(StringUtils.isBlank(videoInfoBo.getSeriesName())) {
				videoInfoBo.setSeriesName(videoInfoBo.getName());
			}*/
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtil.close(rs, pst, conn);
		}
	}
	
	private static void defaultDisplaySiteGetter(VideoInfoBo videoInfoBo) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ProgrammeSitePeer.FK_PROGRAMME_ID, videoInfoBo.getId());
		crit.addAscendingOrderByColumn(ProgrammeSitePeer.ORDER_ID);
		
		List<ProgrammeSite> resultList = ProgrammeSitePeer.doSelect(crit);
		if(resultList.size() > 0) {
			ProgrammeSite defaultDisplaySite = resultList.get(0);
			int siteId = defaultDisplaySite.getSourceSite();
			videoInfoBo.setDefaultDisplaySite(siteId);
			videoInfoBo.setDefaultDisplaySiteName(SiteService.getSiteName(siteId));
		}
	}
	
	private static String jsonArrayToString(JSONArray jsArr, int maxLength) throws JSONException {
		StringBuilder result = new StringBuilder();
		if(jsArr != null && jsArr.length() > 0) {
			for(int i = 0; i < Math.min(maxLength, jsArr.length()); i++) {
				if(result.length() > 0) {
					result.append(",");
				}
				result.append(jsArr.getString(i));
			}
		}
		
		return result.toString();
	}
	
	private static String personJsonArrayToString(JSONArray jsArr, int maxLength) throws JSONException {
		StringBuilder result = new StringBuilder();
		if(jsArr != null && jsArr.length() > 0) {
			for(int i = 0; i < Math.min(maxLength, jsArr.length()); i++) {
				if(result.length() > 0) {
					result.append(",");
				}
				JSONObject jsObj = jsArr.optJSONObject(i);
				if(jsObj != null) {
					result.append(jsObj.optString("name"));
				}
				
			}
		}
		
		return result.toString();
	}
	
	private static String jsonArrayToString(JSONArray jsArr) throws JSONException {
		StringBuilder result = new StringBuilder();
		if(jsArr != null && jsArr.length() > 0) {
			for(int i = 0; i < jsArr.length(); i++) {
				if(result.length() > 0) {
					result.append(",");
				}
				result.append(jsArr.getString(i));
			}
		}
		
		return result.toString();
	}

	public static void main(String[] args) {
		VideoInfoBo v = new VideoInfoBo();
		v.setContentId(88605);
		
		videoInfoGetter(v);
		System.out.println(v);
	}


	

	private static String buildBriefStr(
			String performersName, String area,
			int episodetotal, String brief) throws TorqueException {

		if (brief != null && brief.length() > 45) {
			brief = brief.substring(0, 45) + "...";
		}

		if (area == null) {
			area = "";
		}

		if (brief == null) {
			brief = "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("主演： ").append(performersName).append("<br />");
		sb.append("地区： ").append(area).append("<br />");
		if (episodetotal == 0) {
			sb.append("持续更新中... <br />");
		} else if (episodetotal > 0) {
			sb.append("集数： ").append(episodetotal).append("集<br />");
		}
		sb.append("简介： ").append(brief);
		return sb.toString();
	}

	public static String buildOtherAreaSql() {
		List<String> areaList = LanguageAndArea.getAreaList();
		StringBuilder buffer = new StringBuilder();
		for (String s : areaList) {
			if (LanguageAndArea.AREA_OTHRES.equals(s)) {
				continue;
			}

			if (buffer.length() > 0) {
				buffer.append('|');
			}
			buffer.append(s);
		}

		return buffer.toString();

	}
	
	//查询指定数量的热门搜索剧集 并生存xls
	public static WritableWorkbook exportXls(int accuratelyMatched,int num,int cate,String sheetName,WritableWorkbook wwb){
		String pids = "";
		Map<Integer,TopWords> pcMap = new HashMap<Integer,TopWords>();
		List<TopWords> twList = getProgrammeBySearchNumber(null,cate,num);
		
		for(int i=0;i<twList.size();i++){
			TopWords tw = twList.get(i);
			//若programme_id=0 则根据keyword搜索相应的节目
			if(tw.getProgrammeId()==0){
				Criteria crit = new Criteria();
				crit.add(ProgrammePeer.NAME,(Object)("'%"+tw.getKeyword()+"%'"),Criteria.LIKE);
				//crit.add(ProgrammePeer.NAME,tw.getKeyword());
				crit.add(ProgrammePeer.STATE,"normal");
				try {
					List<Programme> pList = ProgrammePeer.doSelect(crit);
					if(null!=pList&&pList.size()>0)
						tw.setProgrammeId(pList.get(0).getId());
				} catch (TorqueException e) {
					e.printStackTrace();
				}
			}
			if(i==twList.size()-1)
				pids += tw.getProgrammeId();
			else
				pids += tw.getProgrammeId()+",";
			pcMap.put(tw.getProgrammeId(), tw);
		}
		
		WritableSheet ws = wwb.createSheet(sheetName, 0);
		ws.setColumnView(0, 25);
		ws.setColumnView(1, 10);
		ws.setColumnView(3, 10);
		Label keyWordCell = new Label(0, 0, "节目名");  
		Label countCell = new Label(1, 0, "搜索量");  
		Label cateCell = new Label(2, 0, "类型");  
		Label sourceCell = new Label(3, 0, "是否有版权");  
		Label ykCell = new Label(4, 0, "优酷网");  
		Label tdCell = new Label(5, 0, "土豆网");  
		Label xlCell = new Label(6, 0, "新浪网");  
		Label shCell = new Label(7, 0, "搜狐网");  
		Label lsCell = new Label(8, 0, "乐视网");  
		Label qyCell = new Label(9, 0, "奇艺网");  
		try {
			ws.addCell(keyWordCell);
			ws.addCell(countCell);
			ws.addCell(cateCell);
			ws.addCell(sourceCell);
			ws.addCell(ykCell);
			ws.addCell(tdCell);
			ws.addCell(xlCell);
			ws.addCell(shCell);
			ws.addCell(lsCell);
			ws.addCell(qyCell);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ProgrammeExportBo> pebList = new ArrayList<ProgrammeExportBo>(); 
		String mainsql = "select p.id,p.name,p.cate,p.source,group_concat(ps.episode_collected)psecs,group_concat(ps.source_site)sourcesites" +
				        " from programme p,programme_site ps where p.id=ps.fk_programme_id and p.id in ("+pids+") group by p.id";
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = DataBase.getLibraryConn();
			pst = conn.prepareStatement(mainsql);
			rs = pst.executeQuery();
			while(rs.next()){
				int pid = rs.getInt("p.id");
				String name = rs.getString("p.name");
				int _cate = rs.getInt("p.cate");
				String source = rs.getString("p.source");
				String psecs = rs.getString("psecs");
				String psscs = rs.getString("sourcesites");
				String source_sites = ",";
				if(null!=psecs.split(",")&&psecs.split(",").length>0){
					String[] psecss=psecs.split(",");
					for(int i=0;i<psecs.split(",").length;i++){
						if(psecss[i].equals("0"))
							source_sites += 0+",";
						else
							source_sites += psscs.split(",")[i]+",";
					}
				}
				
				//String source_sites = ","+rs.getString("sourcesites")+",";
				ProgrammeExportBo peb = new ProgrammeExportBo();
				peb.setId(pid);
				peb.setName(name);
				peb.setCate(_cate);
				peb.setNum(pcMap.get(pid).getQueryCount());
				peb.setSource(source);
				peb.setSourceSites(source_sites);
				pebList.add(peb);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		for(int i=0;i<twList.size();i++){
			if(pebList.size()>i){
				ProgrammeExportBo peb = pebList.get(i);
				TopWords tw = twList.get(i);
				if(accuratelyMatched==0)
					peb.setName(pcMap.get(peb.getId()).getKeyword());
				if(tw.getCate()>0)
					peb.setCate(tw.getCate());
			}
		}
		
		Collections.sort(pebList, new Comparator<ProgrammeExportBo>() {
			public int compare(ProgrammeExportBo w1, ProgrammeExportBo w2) {
				if(w1.getNum()==w2.getNum())
					return 0;
				return w1.getNum() > w2.getNum()? -1:1;
			}
		})	;	
		
		int row = 0;
		for(ProgrammeExportBo peb:pebList){
			row ++;
			Label _keyWordCell = new Label(0, row, peb.getName());  
			Label _countCell = new Label(1, row,peb.getNum()+"");  
			Label _cateCell = new Label(2, row, peb.getCate() > 0 ? TopWordType.wordTypeMap.get(peb.getCate()):"无类型");  
			Label _sourceCell = new Label(3, row, peb.getSource().startsWith("1") ? "有":"无");  
			Label _ykCell = new Label(4, row, peb.getSourceSites().contains(",14,") ? "有剧集":"");  
			Label _tdCell = new Label(5, row, peb.getSourceSites().contains(",1,") ? "有剧集":"");  
			Label _xlCell = new Label(6, row, peb.getSourceSites().contains(",3,") ? "有剧集":"");  
			Label _shCell = new Label(7, row, peb.getSourceSites().contains(",6,") ? "有剧集":"");  
			Label _lsCell = new Label(8, row, peb.getSourceSites().contains(",17,") ? "有剧集":"");  
			Label _qyCell = new Label(9, row, peb.getSourceSites().contains(",19,") ? "有剧集":"");  
			
			try {
				ws.addCell(_keyWordCell);
				ws.addCell(_countCell);
				ws.addCell(_cateCell);
				ws.addCell(_sourceCell);
				ws.addCell(_ykCell);
				ws.addCell(_tdCell);
				ws.addCell(_xlCell);
				ws.addCell(_shCell);
				ws.addCell(_lsCell);
				ws.addCell(_qyCell);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return wwb;
	}
	
	//获取指定类型的节目
	//指定日期 指定类型 精确搜索
	public static List<TopWords> getProgrammeBySearchNumber(String date,int cate,int num){
		List<TopWords> result =new ArrayList<TopWords>();
		if(null==date || date.isEmpty()){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -1);
			date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		}
		String mainsql = "select y.keyword,y.programme_id,o.query_count" +
        " from top_words o,type_words y where o.keyword=y.keyword and o.cate=y.cate";
		if(cate>0)
			mainsql+=" and o.cate="+cate;
		mainsql+=" and o.visible=1 and o.top_date='"+date+"' order by o.query_count desc limit "+num;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		conn = DataBase.getNewSokuTopConn();
		pst = conn.prepareStatement(mainsql);
		rs = pst.executeQuery();
		
		while(rs.next()){
			int pid=rs.getInt("y.programme_id");
			int query_count = rs.getInt("o.query_count");
			String keyword = rs.getString("y.keyword");
			
			TopWords tw = new TopWords();
			tw.setProgrammeId(pid);
			tw.setQueryCount(query_count);
			if(cate>0)
				tw.setCate(cate);
			tw.setKeyword(keyword);
			result.add(tw);
		}
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				JdbcUtil.close(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		
		return result;
	}
	
}
