package com.youku.soku.top.directory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.util.BasePeer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.workingdogs.village.Record;
import com.youku.soku.config.ExtServerConfig;
import com.youku.soku.util.HttpClientUtil;
import com.youku.top.util.VideoType;

public class DirectoryDateGetter {
	static Log logger = LogFactory.getLog(DirectoryDateGetter.class);


	// 从ext服务器取回数据组装成json返回给前端。  目录
	public static MovieTopResult queryTopList(String date, String year_str, String channel,
			String type_str, String area_str, int start, int limit) {
		
		MovieTopResult returnResult = new MovieTopResult();
		int cate = DirectoryUtil.channel2Cate(channel);
		if (0 == cate)
			return null;
//		date = DirectoryUtils.getDate(date, cate);
		int versionNo=1;
		if (date == null){
			TopDate top_date = TopDateManager.getDate(TopDateManager.ZHIDAQU);
			if (top_date == null)
				return null;
			
			date = top_date.getTopDate();
			versionNo=top_date.getVersion();
		}
		
		
		logger.info("the date:"+date+"  the versionNo:"+versionNo);

		StringBuffer sql = new StringBuffer(
				"select distinct  ri.fk_programme_id from rankinfo_"+date+" ri  ");
		
		StringBuffer countBf=new StringBuffer("select count(distinct fk_programme_id) from rankinfo_"+date+" ri ");
		
		sql.append(" where ri.fk_programme_id>0 and  ri.fk_cate_id=" + cate);
		countBf.append(" where ri.fk_programme_id>0 and ri.fk_cate_id=" + cate);

		if (!StringUtils.isBlank(type_str)) {
			
			if(!type_str.equals("0")){
				sql.append(" and ri.fk_types_id=" + type_str);
				countBf.append(" and ri.fk_types_id=" + type_str);
			}
            	
		}

		if (!StringUtils.isBlank(area_str)) {
			if(!area_str.equals("0")){
				sql.append(" and ri.fk_areas_id=" + area_str);
				countBf.append(" and ri.fk_areas_id=" + area_str);
			}
			
		}
		
		if (!StringUtils.isBlank(year_str)) {
			if(!year_str.equals("0") && !year_str.equals("9999")){
				sql.append(" and ri.year=" + year_str);
				countBf.append(" and ri.year=" + year_str);
			}else if(year_str.equals("9999")){
				sql.append(" and ri.year=" + year_str);
				countBf.append(" and ri.year=" + year_str);
			}else if(year_str.equals("0")){
//				sql.append(" and ri.year=" + year_str);
//				countBf.append(" and ri.year=" + year_str);
			}
			
		}
		
		sql.append(" and ri.version_no="+versionNo);
		countBf.append(" and ri.version_no="+versionNo);

		sql.append(" order by ri.query_count desc limit " + start + " , " +limit);
		
		logger.info("the sql:"+sql);
		List<Integer> programmeIdList = new ArrayList<Integer>();
		try {

			List<Record> records = BasePeer.executeQuery(sql.toString(),
					"new_soku_top");
			for (Record recode : records) {
				programmeIdList.add(recode.getValue("ri.fk_programme_id")
						.asInt());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 查询排行榜数据出现异常 ：" + sql.toString());
			return null;
		}

		// 发送到Ext 服务器的参数：1） programmeIdStr 2) cate
		String programmeIdStr = "";
		programmeIdStr = StringUtils.join(programmeIdList, ",");
		logger.info("the programmeIdStr:"+programmeIdStr);
		if (StringUtils.trimToEmpty(programmeIdStr).length() <= 0)
			return null;

		// 调用Ext服务器来返回结果
		StringBuffer serverURI = new StringBuffer("http://");
		serverURI.append(
				StringUtils.trimToEmpty(ExtServerConfig.getRandomServer()))
				.append("/");
		serverURI.append(StringUtils.trimToEmpty(ExtServerConfig.getInstance()
				.getString("TOPPATH")));
		serverURI.append("?programmeId=").append(programmeIdStr);
		serverURI.append("&cate=").append(cate);
		logger.info("the request url:"+serverURI.toString());
		String returnExt = HttpClientUtil.getRemoteResult(serverURI.toString());
		if (StringUtils.trimToEmpty(returnExt).length() <= 0)
			return null;

		JSONObject extJson = null;
		try {
			extJson = new JSONObject(StringUtils.trimToEmpty(returnExt));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (extJson != null)
			returnResult.setContents(extJson);

		logger.info("the count sql is:"+countBf.toString());
		returnResult.setTotal(DirectoryUtil.getTotalCount(countBf.toString()));

		return returnResult;
	}
	
	
	
	// 返回筛选条件下，总视频个数
	public static int queryTopCount(String date, String year_str, String channel,
			String type_str, String area_str, int start, int limit) {
		
		MovieTopResult returnResult = new MovieTopResult();
		int cate = DirectoryUtil.channel2Cate(channel);
		if (0 == cate)
			return 0;
//		date = DirectoryUtils.getDate(date, cate);
		int versionNo=1;
		if (date == null){
			TopDate top_date = TopDateManager.getDate(TopDateManager.ZHIDAQU);
			if (top_date == null)
				return 0;
			
			date = top_date.getTopDate();
			versionNo=top_date.getVersion();
		}
		StringBuffer countBf=new StringBuffer("select count(distinct fk_programme_id) from rankinfo_"+date+" ri ");
		
		countBf.append(" where ri.fk_programme_id>0 and ri.fk_cate_id=" + cate);

		if (!StringUtils.isBlank(type_str)) {
			
			if(!type_str.equals("0")){
				countBf.append(" and ri.fk_types_id=" + type_str);
			}
            	
		}

		if (!StringUtils.isBlank(area_str)) {
			if(!area_str.equals("0")){
				countBf.append(" and ri.fk_areas_id=" + area_str);
			}
			
		}
		
		if (!StringUtils.isBlank(year_str)) {
			if(!year_str.equals("0") && !year_str.equals("9999")){
				countBf.append(" and ri.year=" + year_str);
			}else if(year_str.equals("9999")){
				countBf.append(" and ri.year=" + year_str);
			}else if(year_str.equals("0")){
//				sql.append(" and ri.year=" + year_str);
//				countBf.append(" and ri.year=" + year_str);
			}
			
		}
		
		countBf.append(" and ri.version_no="+versionNo);

		List<Integer> programmeIdList = new ArrayList<Integer>();
		logger.info("the count sql is:"+countBf.toString());

		return DirectoryUtil.getTotalCount(countBf.toString());
	}
	
	
	// 新榜单数据接口  装成json返回给前端。  榜单
	public static List<Record> queryNewTop(String channel, int limit) {
		
		int cate = DirectoryUtil.channel2Cate(channel);
		if (cate == 0)
			return null;
		
		TopDate date= null;
		if(channel.equals("fun")){
			date= TopDateManager.getDate(TopDateManager.FUN);
		}else{
			date= TopDateManager.getDate(TopDateManager.TOP);
		}
		
		if(date==null) return null;
		
		String theDate= date.getTopDate();
		logger.info("the date:"+date);

		StringBuffer sql = new StringBuffer(
				"select * " +
				" from top_words where cate="+cate+" and  visible=1  and top_date='"+theDate+
				"'  order by query_count desc     limit "+limit);
		
		logger.info("the sql:"+sql);
		try {

			return BasePeer.executeQuery(sql.toString(),
					"new_soku_top");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 查询排行榜数据出现异常 ：" + sql.toString());
		}

		return null;
	}
	
	
	// api 装成json返回给前端。  top.jsp
	public static JSONObject queryTopJson(String channel, int limit) {
		int cate = DirectoryUtil.channel2Cate(channel);

		TopDate date = null;
		if (channel.equals("fun")) {
			date = TopDateManager.getDate(TopDateManager.FUN);
		} else {
			date = TopDateManager.getDate(TopDateManager.TOP);
		}

		if (date == null)
			return null;

		String theDate = date.getTopDate();
		logger.info("the date:" + date);

		StringBuffer sql = new StringBuffer(
				"select * from top_words where  visible=1  ");
		if (cate > 0) {
			sql.append(" and  cate=" + cate);
		};
		sql.append(" and   top_date='" + theDate
				+ "'  order by query_count desc     limit " + limit);

		logger.info("the sql:" + sql);
		try {

			List<Record> recordList = BasePeer.executeQuery(sql.toString(),
					"new_soku_top");

			JSONArray contentArr = new JSONArray();
			if (recordList != null && recordList.size() > 0) {
				for (Record tmpRecord : recordList) {
					JSONObject tmpJson = new JSONObject();
					tmpJson.put("keyword", tmpRecord.getValue("keyword")
							.toString());
					tmpJson.put("querycount", tmpRecord.getValue("query_count"));
					contentArr.put(tmpJson);
				}
			}
			JSONObject returnJson=new JSONObject();
            return returnJson.put("content", contentArr);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 查询排行榜数据出现异常 ：" + sql.toString());
		}

		return null;
	}
}
